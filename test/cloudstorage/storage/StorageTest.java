package cloudstorage.storage;

import cloudstorage.cloud.CloudStorage;
import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;
import org.junit.jupiter.api.Assertions;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {
    private CloudStorage cloudStorage;
    private Storage storage;

    public StorageTest() {
        try {
            cloudStorage = new CloudStorage(true);
        } catch (RemoteException e) {
            fail("CloudStorage could not be created: " + e.getMessage());
        }

        Account a = new Account(1, "jorin", "jorin@mail.nl");

        try {
            this.storage = new Storage(a, cloudStorage, 1, true);
        } catch (RemoteException e) {
            fail("Storage could not be created: " + e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void getRoot() {
        Folder root = storage.getRoot();

        assertEquals("root", root.getName());
    }

    @org.junit.jupiter.api.Test
    void getFiles() {
        Folder files = storage.getFiles();

        assertEquals("Your Storage", files.getName());
    }

    @org.junit.jupiter.api.Test
    void getShared() {
        Folder shared = storage.getShared();

        assertEquals("Shared with You", shared.getName());
    }

    @org.junit.jupiter.api.Test
    void getId() {
        int id = storage.getId();

        assertEquals(1, id);
    }

    @org.junit.jupiter.api.Test
    void createFolder() {
        storage.createFolder("test", storage.getFiles());

        Folder createdFolder = storage.getRoot().getFolder("test");

        assertEquals("test", createdFolder.getName());
        assertEquals("Your Storage", createdFolder.getParent().getName());
    }

    @org.junit.jupiter.api.Test
    void createFile() {
        fail("not implemented");
    }

    @org.junit.jupiter.api.Test
    void getOwner() {
        Account owner = storage.getOwner();

        assertEquals("jorin", owner.getName());
        assertEquals(1, owner.getId());
    }
}