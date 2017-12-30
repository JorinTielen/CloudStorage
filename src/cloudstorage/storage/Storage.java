package cloudstorage.storage;

import cloudstorage.client.LocalStorage;
import cloudstorage.cloud.CloudStorage;
import cloudstorage.shared.*;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.Publisher;
import fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class Storage extends UnicastRemoteObject implements IStorage, IFileProvider {
    private static final Logger LOGGER = Logger.getLogger(Storage.class.getName());

    private ICloudStorage cloudStorage;
    private RemotePublisher publisher;

    private Account owner = new Account(1, "test", "you@you.com");

    private Folder root = new Folder (2, "root", owner);
    private Folder files = new Folder(3, "Your Storage", owner);
    private Folder shared = new Folder(4, "Shared with You", owner);

    public Storage(CloudStorage cloudStorage) throws RemoteException {
        this.cloudStorage = cloudStorage;

        try {
            publisher = new RemotePublisher();
            publisher.registerProperty("Files");
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create publisher");
            LOGGER.severe("Storage: RemoteException " + e.getMessage());
            throw e;
        }

        cloudStorage.registerStorage(this);
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
        boolean success = parent.addFolder(name);
        try {
            publisher.inform("Files", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create folder");
            LOGGER.severe("Storage: RemoteException " + e.getMessage());
        }
        return success;
    }

    @Override
    public boolean createFile(String name, Folder parent) {
        boolean success = parent.addFile(name);
        try {
            publisher.inform("Files", null, root);
        } catch (RemoteException e) {
            LOGGER.severe("Storage: Cannot create folder");
            LOGGER.severe("Storage: RemoteException " + e.getMessage());
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
