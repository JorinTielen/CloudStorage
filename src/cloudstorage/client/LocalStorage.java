package cloudstorage.client;

import cloudstorage.shared.Account;
import cloudstorage.shared.File;
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
    private Client client;

    private Account owner;
    private Folder root;

    private Folder selectedFolder;

    LocalStorage(IStorage remoteStorage, Client client) throws RemoteException {
        this.remoteStorage = remoteStorage;
        this.client = client;
        subscribeToRemote();

        this.owner = remoteStorage.getOwner();
        this.root = remoteStorage.getRoot();
        this.selectedFolder = root;
    }

    LocalStorage(IStorage remoteStorage, Client client, boolean test) throws RemoteException {
        this.remoteStorage = remoteStorage;
        this.client = client;
        subscribeToRemote();

        this.owner = remoteStorage.getOwner();
        this.root = remoteStorage.getRoot();
        this.selectedFolder = root;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        int selected_id = selectedFolder.getId();

        root = (Folder) propertyChangeEvent.getNewValue();
        selectedFolder = root.getFolder(selected_id);

        client.updateUI();
    }

    public void clientPull() {
        int selected_id = selectedFolder.getId();

        try {
            root = (Folder) remoteStorage.clientPull();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        selectedFolder = root.getFolder(selected_id);

        client.updateUI();
    }

    public Folder getRoot() {
        return root;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public void selectFolder(Folder folder) {
        selectedFolder = root.getFolder(folder.getId());
    }

    public boolean createFolder(String name) {
        try {
            return remoteStorage.createFolder(name, selectedFolder);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to create folder");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
            return false;
        }
    }

    public boolean createFile(String name) {
        try {
            return remoteStorage.createFile(name, selectedFolder);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to create file");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
            return false;
        }
    }

    private void subscribeToRemote() {
        try {
            remoteStorage.subscribe(this, "root");
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to subscribe to remote");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
        }
    }

    public boolean requestEditFile(File file) {
        try {
            return remoteStorage.lockFile(file, owner);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to request edit file");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
        }

        return false;
    }

    public boolean saveFile(File file, String fileText) {
        try {
            return remoteStorage.saveFile(file, fileText, owner);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying save file");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
        }

        return false;
    }

    public boolean cancelEditFile(File file) {
        try {
            return remoteStorage.cancelEditFile(file, owner);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to request edit file");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
        }

        return false;
    }

    public boolean shareFile(File file, String username) {
        try {
            return remoteStorage.shareFile(file, username);
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to share file");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
        }

        return false;
    }

    public void logout() {
        try {
            remoteStorage.logout();
        } catch (RemoteException e) {
            LOGGER.severe("LocalStorage: RemoteException when trying to share file");
            LOGGER.severe("LocalStorage: RemoteException: " + e.getMessage());
        }
    }

    public Folder pullSelectedFolder() {
        int selected_id = selectedFolder.getId();

        try {
            return (Folder) remoteStorage.clientPull().getFolder(selected_id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }
}
