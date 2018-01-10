package cloudstorage.shared;

import cloudstorage.storage.IFileProvider;
import cloudstorage.storage.IStorageServer;
import cloudstorage.storage.Storage;
import cloudstorage.storage.StorageServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICloudStorage extends Remote {
    /**
     * Used when you want to log in to the CloudStorage, as a client.
     * @param username Your username.
     * @param password Your password.
     * @return A reference to your own storage component.
     * @throws RemoteException when RMI fails.
     */
    IStorage login(String username, String password) throws RemoteException;

    /**
     * Used when you want to register a new account, as a client.
     * @param username Your username.
     * @param email Your email.
     * @param password Your password.
     * @return A reference to your newly created storage component.
     * @throws RemoteException when RMI fails.
     */
    IStorage register(String username, String email, String password) throws RemoteException;

    /**
     * Used when you want to register to the CloudStorage as a storage server.
     * Upon the next login request, the CloudStorage will assign you the client.
     * @param server the server you will register to.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean registerStorageServer(IStorageServer server) throws RemoteException;

    /**
     * Used to get the account from a particular storage id.
     * @param id the id of the storage, that you want the account from.
     * @return the account.
     * @throws RemoteException when RMI fails.
     */
    Account getAccountFromStorage(int id) throws RemoteException;

    /**
     * Used to log your storage out from the CloudStorage.
     * @param id your storage id.
     * @throws RemoteException when RMI fails.
     */
    void logoutStorage(int id) throws RemoteException;

    /**
     * Used to get a reference to another storage, so you can share files with it, etc.
     * In the case this storage is not online, it will return null.
     * You can then proceed to use any files on this storage from the database.
     * @param username the username that this storage belongs to.
     * @return a interface to talk to this storage.
     * @throws RemoteException when RMI fails.
     */
    IFileProvider getStorageReference(String username) throws RemoteException;

    void logout(Integer session) throws RemoteException;
}
