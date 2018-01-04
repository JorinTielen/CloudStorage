package cloudstorage.storage;

import cloudstorage.shared.ICloudStorage;
import cloudstorage.shared.IStorage;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IStorageServer extends Remote {
    boolean isWaiting() throws RemoteException;
    IStorage assignStorage(ICloudStorage cloudStorage, int id) throws RemoteException;
}
