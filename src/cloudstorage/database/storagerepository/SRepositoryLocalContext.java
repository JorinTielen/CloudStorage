package cloudstorage.database.storagerepository;

import cloudstorage.shared.Account;
import cloudstorage.shared.File;
import cloudstorage.shared.Folder;

public class SRepositoryLocalContext implements ISRepositoryContext {

    Account owner = new Account(1, "jorin", "jorin@mail.nl");
    private Folder root = new Folder(getNextId(), "root", owner);

    public SRepositoryLocalContext() {
        Folder files = new Folder(getNextId(), "Your Storage", owner);
        Folder shared = new Folder(getNextId(), "Shared with You", owner);
        root.getChildren().add(files);
        root.getChildren().add(shared);
    }

    @Override
    public Folder getRoot(Account owner) {
        return root;
    }

    @Override
    public void getFolderChildren(Account owner, Folder folder) {
        //TODO?
    }

    @Override
    public boolean addFolder(Account owner, String name, Folder parent) {
        if (parent.getId() == root.getId()) {
            root.getChildren().add(new Folder(getNextId(), name, owner, parent));
            return true;
        }

        return addFolder(root, parent, name);
    }

    @Override
    public boolean addFile(String name, Folder parent, Account owner) {
        parent.getFiles().add(new File(nextId, name, 0, parent, owner, ""));
        return true;
    }

    @Override
    public boolean saveFile(File file) {
        return false;
    }

    @Override
    public boolean shareFile(File file, String username) {
        return false;
    }

    private boolean addFolder(Folder root, Folder parent, String name) {
        for (Folder f : root.getChildren()) {
            if (f.getId() == parent.getId()) {
                f.getChildren().add(new Folder(getNextId(), name, owner, parent));
                return true;
            }
        }

        for (Folder f : root.getChildren()) {
            boolean succes = addFolder(f, parent, name);
            if (succes) return true;
        }

        return false;
    }

    private static int nextId = 0;
    private int getNextId() {
        nextId++;
        return nextId;
    }
}
