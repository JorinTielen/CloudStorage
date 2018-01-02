package cloudstorage.storage.repository;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;

import java.util.List;

public class SRepository {
    private ISRepositoryContext context;

    public SRepository(ISRepositoryContext context) {
        this.context = context;
    }

    public Folder getRoot(Account owner) {
        return this.context.getRoot(owner);
    }

    public List<Folder> getFolderChildren(Account owner, int folder_id) {
        return this.context.getFolderChildren(owner, folder_id);
    }
}
