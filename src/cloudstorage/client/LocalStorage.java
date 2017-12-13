package cloudstorage.client;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;
import cloudstorage.shared.IStorage;
import cloudstorage.storage.Storage;

import java.rmi.RemoteException;

public class LocalStorage {

    private IStorage remoteStorage;

    private Folder root;
    private Folder shared;
    private Account owner;

    public LocalStorage(IStorage remoteStorage) throws RemoteException {
        root = remoteStorage.getRoot();
    }
}
