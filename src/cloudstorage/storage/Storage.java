package cloudstorage.storage;

import cloudstorage.cloud.CloudStorage;
import cloudstorage.shared.*;
import cloudstorage.storage.repository.ISRepositoryContext;
import cloudstorage.storage.repository.SRepository;
import cloudstorage.storage.repository.SRepositoryLocalContext;
import cloudstorage.storage.repository.SRepositorySQLContext;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class Storage extends UnicastRemoteObject implements IStorage, IFileProvider {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());

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
    public boolean upload(File file, Folder location) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File download(int fileId) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean lockFile(File file, Account account) {
        File realFile = root.getFile(file.getId());

        boolean succes =  realFile.lock(account.getId());
        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot lock file");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }
        return succes;
    }

    @Override
    public boolean saveFile(File file, String fileText, Account account) {
        File realFile = root.getFile(file.getId());
        if (realFile.editText(account, fileText)) {
            try {
                publisher.inform("root", null, root);
            } catch (RemoteException e) {
                LOGGER.severe("Storage: Cannot save file");
                LOGGER.severe("Storage: RemoteException: " + e.getMessage());
            }
            return repository.saveFile(realFile);
        }

        return false;
    }

    public boolean cancelEditFile(File file, Account owner) {
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
    public boolean shareFile(File file, String username) throws RemoteException {
        File realFile = root.getFile(file.getId());

        //First save the share in the repository
        repository.shareFile(file, username);

        //Try to get the other storage (for push notification), only works if they are logged in.
        /*
        IFileProvider other = cloudStorage.getStorageReference(username);
        if (other != null) {
            other.receiveSharedFile(file);
        } */

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
}
