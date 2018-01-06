package cloudstorage.storage.repository;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;

public interface ISRepositoryContext {
    Folder getRoot(Account owner);
    void getFolderChildren(Account owner, Folder folder);
    boolean addFolder(Account owner, String name, Folder parent);
    boolean addFile(String name, Folder parent, Account owner);
}
