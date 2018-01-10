package cloudstorage.storage;

import cloudstorage.shared.*;
import cloudstorage.database.storagerepository.SRepository;
import cloudstorage.database.storagerepository.SRepositoryLocalContext;
import cloudstorage.database.storagerepository.SRepositorySQLContext;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class Storage extends UnicastRemoteObject implements IStorage, IFileProvider {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());

    private int sessionid;

    private int id;

    private ICloudStorage cloudStorage;
    private RemotePublisher publisher;

    private SRepository repository;

    private Account owner;

    private Folder root;
    private Folder files;
    private Folder shared;

    public Storage(Account owner, ICloudStorage cloudStorage, int id, boolean localContext) throws RemoteException {
        this.cloudStorage = cloudStorage;

        this.id = id;
        this.owner = owner;

        if (localContext) this.repository = new SRepository(new SRepositoryLocalContext());
        else this.repository = new SRepository(new SRepositorySQLContext());

        loadFromDB();

        try {
            publisher = new RemotePublisher();
            publisher.registerProperty("root");
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create publisher");
            LOGGER.severe("Storage: RemoteException " + e.getMessage());
            throw e;
        }
    }

    private void loadFromDB() {
        this.root = repository.getRoot(owner);
        this.files = root.getFolder("Your Storage");
        this.shared = root.getFolder("Shared with You");
    }

    @Override
    public Folder getRoot() {
        return root;
    }

    public Folder clientPull() {
        return repository.getRoot(owner);
    }

    @Override
    public Folder getFiles() {
        return files;
    }

    @Override
    public Folder getShared() {
        return shared;
    }

    public int getId() {
        return id;
    }

    @Override
    public void subscribe(IRemotePropertyListener listener, String property) throws RemoteException {
        publisher.subscribeRemoteListener(listener, property);
    }

    @Override
    public boolean createFolder(String name, Folder parent) {
        //You are not allowed to name the folder these names
        if ("Your Storage".equals(name) || "Shared with You".equals(name) || "root".equals(name)) {
            return false;
        }

        //You are not allowed to make folders in the root or shared folder.
        if (parent.getName().equals("root") || parent.getName().equals("Shared with You")) {
            return false;
        }

        //no duplicate names
        for (Folder f : parent.getChildren()) {
            if (f.getName().equals(name)) {
                return createFolder(name + " (1)", parent);
            }
        }

        boolean success = repository.addFolder(owner, name, parent);
        loadFromDB();
        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create folder");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }
        return success;
    }

    @Override
    public boolean createFile(String name, Folder parent) {
        //You are not allowed to name the file these names
        if ("Your Storage".equals(name) || "Shared with You".equals(name) || "root".equals(name)) {
            return false;
        }

        //You are not allowed to make files in the root or shared folder.
        if (parent.getName().equals("root") || parent.getName().equals("Shared with You")) {
            return false;
        }

        //no duplicate names
        for (Folder f : parent.getChildren()) {
            if (f.getName().equals(name)) {
                return createFile(name + " (1)", parent);
            }
        }

        boolean success = repository.addFile(name, parent, owner);

        // Also update locally:
        root = repository.getRoot(owner);
        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create file");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }
        return success;
    }

    @Override
    public Account getOwner() {
        return owner;
    }

    @Override
    public boolean lockFile(File file, Account account) {
        File realFile = root.getFile(file.getId());
        IFileProvider fileProvider;

        //If the file is not yours go to the owner's storage.
        if(file.getOwner().getId() != this.owner.getId()) {
            try {
                fileProvider = cloudStorage.getStorageReference(file.getOwner().getName());
            } catch (RemoteException e) {
                LOGGER.severe("Storage: Cannot get storage reference");
                LOGGER.severe("Storage: RemoteException: " + e.getMessage());
                fileProvider = null;
            }

            //The remote storage is online, so let's go there.
            if (fileProvider != null) {
                try {
                    return fileProvider.lockFile(file, account);
                } catch (RemoteException e) {
                    LOGGER.severe("Storage: Cannot get lock file remotely");
                    LOGGER.severe("Storage: RemoteException: " + e.getMessage());
                }
            }
        }

        //The remote storage wasn't online, so let's lock it here.
        boolean success = realFile.lock(account.getId());

        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot lock file");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }

        return success;
    }

    @Override
    public boolean saveFile(File file, String fileText, Account account) {
        File realFile = root.getFile(file.getId());
        IFileProvider fileProvider;

        //If the file is not yours go to the owner's storage.
        if(file.getOwner().getId() != this.owner.getId()) {
            try {
                fileProvider = cloudStorage.getStorageReference(file.getOwner().getName());
            } catch (RemoteException e) {
                LOGGER.severe("Storage: Cannot get storage reference when saving file");
                LOGGER.severe("Storage: RemoteException: " + e.getMessage());
                fileProvider = null;
            }

            //The remote storage is online, so let's go there.
            if (fileProvider != null) {
                try {
                    boolean success = fileProvider.saveFile(file, fileText, account);

                    try {
                        publisher.inform("root", null, root);
                    } catch (RemoteException e) {
                        LOGGER.severe("Storage: Cannot save file");
                        LOGGER.severe("Storage: RemoteException: " + e.getMessage());
                    }

                    return success;
                } catch (RemoteException e) {
                    LOGGER.severe("Storage: Cannot save file remotely");
                    LOGGER.severe("Storage: RemoteException: " + e.getMessage());
                }
            }
        }

        boolean success;

        //The remoteStorage wasn't online, so we will edit it here. (or we ARE the remote storage :) )
        if (realFile.editText(account, fileText)) {
             success = repository.saveFile(realFile);
        } else {
            success = false;
        }

        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot save file");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }

        return success;
    }

    @Override
    public void receiveSharedFile(File file) {
        shared.getFiles().add(file);

        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: cannot receive shared file");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }
    }

    public boolean cancelEditFile(File file, Account owner) {

        //TODO fix
        File realFile = root.getFile(file.getId());

        boolean succes =  realFile.unlock(owner.getId());
        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot lock file");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }
        return succes;
    }

    @Override
    public boolean shareFile(File file, String username) {
        File realFile = root.getFile(file.getId());

        //First save the share in the cloudrepository
        repository.shareFile(file, username);

        //Try to get the other storage (for push notification), only works if they are logged in.
        IFileProvider other = null;
        try {
            other = cloudStorage.getStorageReference(username);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot get StorageReference");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }

        if (other != null) {
            try {
                other.receiveSharedFile(file);
            } catch (RemoteException e) {
                LOGGER.severe("Storage: Cannot contact StorageReference");
                LOGGER.severe("Storage: RemoteException: " + e.getMessage());
            }
        }

        //inform client
        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot lock file");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }

        return false;
    }

    @Override
    public void logout() {
        try {
            cloudStorage.logoutStorage(id);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot log out");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }
    }

    public void setSessionid(int sessionid) {
        this.sessionid = sessionid;
    }

    public int getSessionid() {
        return sessionid;
    }
}
