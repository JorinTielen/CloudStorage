package cloudstorage.shared;

import cloudstorage.storage.IStorageServer;
import cloudstorage.storage.Storage;
import cloudstorage.storage.StorageServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICloudStorage extends Remote {
    IStorage login(String username, String password) throws RemoteException;
    IStorage register(String username, String email, String password) throws RemoteException;
    boolean registerStorageServer(IStorageServer server) throws RemoteException;
}
