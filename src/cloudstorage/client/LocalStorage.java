package cloudstorage.client;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;
import cloudstorage.shared.IStorage;
import fontyspublisher.IRemotePropertyListener;
import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class LocalStorage extends UnicastRemoteObject implements IRemotePropertyListener {
    private static final Logger LOGGER = Logger.getLogger(LocalStorage.class.getName());

    private IStorage remoteStorage;


    private Folder root;
    private Folder files;
    private Folder shared;
    private Account owner;

    LocalStorage(IStorage remoteStorage) throws RemoteException {
        this.remoteStorage = remoteStorage;
        subscribeToRemote();
        this.owner = remoteStorage.getOwner();

        root = remoteStorage.getRoot();
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        root = (Folder) propertyChangeEvent.getNewValue();
    }

    public Folder getRoot() {
        return root;
    }

    public boolean createFolder(String name, Folder parent) {
        try {
            return remoteStorage.createFolder(name, parent);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to create folder");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
            return false;
        }
    }

    public Folder getFolder(int id, String name) {
        if (name.equals("root")) return root;
        for (Folder f : root.getChildren()) {
            if (f.getName().equals(name) && f.getId() == id) {
                return f;
            }
        }
        return null;
    }

    public boolean createFile(String name, Folder parent) {
        try {
            return remoteStorage.createFile(name, parent);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to create file");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
            return false;
        }
    }

    private void subscribeToRemote() {
        try {
            remoteStorage.subscribe(this, "Files");
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to subscribe to remote");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
        }
    }

}
