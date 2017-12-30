package cloudstorage.shared;

import cloudstorage.storage.Storage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICloudStorage extends Remote {
    IStorage login(String username, String password) throws RemoteException;
    IStorage register(String username, String email, String password) throws RemoteException;
    boolean registerStorage(Storage storage) throws RemoteException;
}
