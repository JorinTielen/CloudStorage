package cloudstorage.storage;

import cloudstorage.shared.File;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFileProvider extends Remote {
    /**
     * Downloads a file from the storage.
     * @param id the file's id.
     * @return the file.
     * @throws RemoteException when RMI fails.
     */
    File download(int id) throws RemoteException;

    /**
     * Locks a file so you can edit it.
     * @param file the file you want to lock.
     * @param accountId your account id.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean LockFile(File file, int accountId) throws RemoteException;

    /**
     * Saves a file, if you have the lock on it.
     * @param file the file you want to save.
     * @param accountId your account id.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean SaveFile(File file, int accountId) throws RemoteException;
}
