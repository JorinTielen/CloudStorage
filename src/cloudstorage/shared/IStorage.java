package cloudstorage.shared;

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
     * Gets the shared folder, which includes files shared with you.
     * @return the shared folder.
     * @throws RemoteException when RMI fails.
     */
    Folder getShared() throws RemoteException;

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
     * Downloads a file of the storage.
     * @param fileId the File's id.
     * @return the File.
     * @throws RemoteException when RMI fails.
     */
    File download(int fileId) throws RemoteException;
}
