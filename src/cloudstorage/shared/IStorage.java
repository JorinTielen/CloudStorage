package cloudstorage.shared;

import fontyspublisher.IRemotePropertyListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IStorage extends Remote {
    /**
     * Gets the root folder, which includes all the sub-folders and files.
     * @return the root folder.
     * @throws RemoteException when RMI fails.
     */
    Folder getRoot() throws RemoteException;

    /**
     * Gets the id.
     * @return the id.
     * @throws RemoteException when RMI fails.
     */
    int getId() throws RemoteException;

    /**
     * Gets the files folder, This is the "Your Storage" folder, in which
     * you can create files.
     * @return the files folder.
     * @throws RemoteException when RMI fails.
     */
    Folder getFiles() throws RemoteException;

    /**
     * Gets the shared folder, which includes files shared with you.
     * @return the shared folder.
     * @throws RemoteException when RMI fails.
     */
    Folder getShared() throws RemoteException;

    /**
     * Subscribe to the remote storage, using RMI push.
     * @param listener the listener.
     * @param property the property you want to subscribe to.
     * @throws RemoteException when RMI fails.
     */
    void subscribe(IRemotePropertyListener listener, String property) throws RemoteException;

    /**
     * Creates a new Folder inside of the Folder 'parent'.
     * @param name The name of the new Folder.
     * @param parent The parent Folder.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean createFolder(String name, Folder parent) throws RemoteException;

    /**
     * Creates a new File inside of the folder 'parent'.
     * @param name The name of the new File.
     * @param parent The parent Folder.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean createFile(String name, Folder parent) throws RemoteException;

    /**
     * Gets the Storage's owner.
     * @return the owner.
     * @throws RemoteException when RMI fails.
     */
    Account getOwner() throws RemoteException;

    /**
     * Asks to lock a file, when you have a lock, you can edit the file.
     * @param file the file you want to lock
     * @param account your account.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean lockFile(File file, Account account) throws RemoteException;

    /**
     * Saves a file, but only if you have the lock for it.
     * @param file the file you want to save.
     * @param fileText the new content of the file.
     * @param account your account.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean saveFile(File file, String fileText, Account account) throws RemoteException;

    /**
     * Cancels the editing of a file, which effectively
     * releases the lock of the file.
     * @param file the file that you cancel the lock on.
     * @param owner your account.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean cancelEditFile(File file, Account owner) throws RemoteException;

    /**
     * Used to share a file with another storage.
     * @param file the file to be shared.
     * @param username the username of the account you want to share with.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean shareFile(File file, String username) throws RemoteException;

    /**
     * Used to log out.
     * @throws RemoteException when RMI fails.
     */
    void logout() throws RemoteException;

    void setSessionid(int sessionid) throws RemoteException;

    int getSessionid() throws RemoteException;

    Folder clientPull() throws RemoteException;
}
