package cloudstorage.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICloudStorage extends Remote {
    IStorage login(String username, String password) throws RemoteException;
}
