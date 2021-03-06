package cloudstorage.database.storagerepository;

import cloudstorage.shared.Account;
import cloudstorage.shared.File;
import cloudstorage.shared.Folder;

public interface ISRepositoryContext {
    Folder getRoot(Account owner);
    void getFolderChildren(Account owner, Folder folder);
    boolean addFolder(Account owner, String name, Folder parent);
    boolean addFile(String name, Folder parent, Account owner);
    boolean saveFile(File file);
    boolean shareFile(File file, String username);
}
