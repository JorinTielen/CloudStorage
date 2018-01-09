package cloudstorage.shared;

import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IStorage extends Remote {
    /**
     * Gets the root folder, which includes all the sub-folders and files.
     * @return the root folder.
     * @throws RemoteException when RMI fails.
     */
    Folder getRoot() throws RemoteException;

    int getId() throws RemoteException;

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
     * Uploads a file to the storage.
     * @param file the file.
     * @param location the location of the file.
     * @return success status.
     * @throws RemoteException when RMI fails.
     */
    boolean upload(File file, Folder location) throws RemoteException;

    /**
     * Downloads a file from the storage.
     * @param fileId the File's id.
     * @return the File.
     * @throws RemoteException when RMI fails.
     */
    File download(int fileId) throws RemoteException;

    boolean lockFile(File file, Account account) throws RemoteException;

    boolean saveFile(File file, String fileText, Account account) throws RemoteException;

    boolean cancelEditFile(File file, Account owner) throws RemoteException;
}
