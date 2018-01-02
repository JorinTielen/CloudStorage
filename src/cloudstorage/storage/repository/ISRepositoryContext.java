package cloudstorage.storage.repository;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;

import java.util.List;

public interface ISRepositoryContext {
    Folder getRoot(Account owner);
    List<Folder> getFolderChildren(Account owner, int folder_id);
}
