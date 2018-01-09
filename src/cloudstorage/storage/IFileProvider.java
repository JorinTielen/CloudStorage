package cloudstorage.storage;

import cloudstorage.shared.Account;
import cloudstorage.shared.File;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFileProvider extends Remote {
    /**
     * Locks a file so you can edit it.
     * @param file the file you want to lock.
     * @param account your account.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean lockFile(File file, Account account) throws RemoteException;

    /**
     * Saves a file, if you have the lock on it.
     * @param file the file you want to save.
     * @param account your account.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean saveFile(File file, String fileText, Account account) throws RemoteException;

    /**
     * Used for when you want to share a file with this IFileProvider
     * IMPORTANT: You need to add the file to the shared_files in the database first, otherwise
     * this will not work correctly.
     * @param file the file you're going to share.
     * @throws RemoteException when RMI fails.
     */
    void receiveSharedFile(File file) throws RemoteException;
}
