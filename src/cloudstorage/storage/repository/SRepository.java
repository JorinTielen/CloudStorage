package cloudstorage.storage.repository;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;

public class SRepository {
    private ISRepositoryContext context;

    public SRepository(ISRepositoryContext context) {
        this.context = context;
    }

    public Folder getRoot(Account owner) {
        return this.context.getRoot(owner);
    }

    public void getFolderChildren(Account owner, Folder folder) {
        this.context.getFolderChildren(owner, folder);
    }

    public boolean addFolder(Account owner, String name, Folder parent) {
        return this.context.addFolder(owner, name, parent);
    }
}
