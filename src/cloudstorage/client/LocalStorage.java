package cloudstorage.client;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;
import cloudstorage.shared.IStorage;
import cloudstorage.storage.Storage;

import java.rmi.RemoteException;
import java.util.List;

public class LocalStorage {

    private IStorage remoteStorage;

    private Folder root;
    private Folder shared;
    private Account owner;

    public LocalStorage(IStorage remoteStorage) throws RemoteException {
        this.remoteStorage = remoteStorage;
        this.owner = remoteStorage.getOwner();

        root = new Folder(1, "root");
        shared = new Folder(1, "shared");
    }

    public Folder getRoot() {
        return root;
    }

    public boolean createFolder(String name) {
        return root.addFolder(name);
    }

    public boolean createFolder(String name, Folder parent) {
        return parent.addFolder(name);
    }
}
