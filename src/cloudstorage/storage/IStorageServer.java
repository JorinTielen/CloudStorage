package cloudstorage.storage;

import cloudstorage.shared.Account;
import cloudstorage.shared.ICloudStorage;
import cloudstorage.shared.IStorage;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IStorageServer extends Remote {
    boolean isWaiting() throws RemoteException;
    IStorage assignStorage(Account owner, ICloudStorage cloudStorage, int id) throws RemoteException;
}
