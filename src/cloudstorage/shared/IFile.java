package cloudstorage.shared;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;

public interface IFile extends Serializable {
    /**
     * Renames this file;
     * @param name the new name.
     * @throws RemoteException when RMI fails.
     */
    void rename(String name) throws RemoteException;

    /**
     * Edits the file's content.
     * @throws RemoteException when RMI fails.
     */
    void editContent() throws RemoteException;

    /**
     * Gets the file's Id used in the database.
     * @return the id.
     * @throws RemoteException when RMI fails.
     */
    Integer getId() throws RemoteException;

    /**
     * Gets this file's owner.
     * @return the file's owner.
     * @throws RemoteException when RMI fails.
     */
    Account getOwner() throws RemoteException;

    /**
     * Gets the date on which the file was created.
     * @return the created date.
     * @throws RemoteException when RMI fails.
     */
    Date getCreatedAt() throws RemoteException;

    /**
     * Gets the date on which the file was last edited.
     * @return the last edited date.
     * @throws RemoteException when RMI fails.
     */
    Date getEditedAt() throws RemoteException;

    /**
     * Gets the location of the file, which is the folder it is in.
     * @return the folder where the file is in.
     * @throws RemoteException when RMI fails.
     */
    Folder getLocation() throws RemoteException;
}
