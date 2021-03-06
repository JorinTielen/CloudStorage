package cloudstorage.database.storagerepository;

import cloudstorage.shared.Account;
import cloudstorage.shared.File;
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

    public boolean addFile(String name, Folder parent, Account owner) {
        return this.context.addFile(name, parent, owner);
    }

    public boolean saveFile(File file) {
        return this.context.saveFile(file);
    }

    public boolean shareFile(File file, String username) {
        return this.context.shareFile(file, username);
    }
}
