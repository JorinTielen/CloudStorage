package cloudstorage.storage;

import cloudstorage.cloud.CloudStorage;
import cloudstorage.shared.*;
import cloudstorage.storage.repository.SRepository;
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

    private Account owner = new Account(1, "test", "you@you.com");

    private Folder root;
    private Folder files;
    private Folder shared;

    public Storage(CloudStorage cloudStorage, int id) throws RemoteException {
        this.cloudStorage = cloudStorage;

        this.repository = new SRepository(new SRepositorySQLContext());
        loadFromDB();

        try {
            publisher = new RemotePublisher();
            publisher.registerProperty("root");
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create publisher");
            LOGGER.severe("Storage: RemoteException " + e.getMessage());
            throw e;
        }

        cloudStorage.registerStorage(this);
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
                return createFolder(name + " (1)", parent);
            }
        }

        boolean success = parent.addFile(name);
        try {
            publisher.inform("root", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create folder");
            LOGGER.severe("Storage: RemoteException: " + e.getMessage());
        }
        return success;
    }

    @Override
    public Account getOwner() throws RemoteException {
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
    public boolean LockFile(File file, int accountId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean SaveFile(File file, int accountId) {
        throw new UnsupportedOperationException();
    }
}
