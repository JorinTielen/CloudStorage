package cloudstorage.storage;

import cloudstorage.cloud.CloudStorage;
import cloudstorage.shared.Account;
import cloudstorage.shared.File;
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
        Folder f = storage.getFiles();

        storage.createFile("test.txt", f);

        File file = f.getFiles().get(0);

        assertEquals("test.txt", file.getName());
    }

    @org.junit.jupiter.api.Test
    void getOwner() {
        Account owner = storage.getOwner();

        assertEquals("jorin", owner.getName());
        assertEquals(1, owner.getId());
    }

    @org.junit.jupiter.api.Test
    void clientPull() {
        Folder root = storage.clientPull();

        assertEquals("root", root.getName());
    }

    @org.junit.jupiter.api.Test
    void logout() {
        storage.logout();
    }

    @org.junit.jupiter.api.Test
    void setSessionid() {
        storage.setSessionid(1);

        assertEquals(1, storage.getSessionid());
    }

    @org.junit.jupiter.api.Test
    void getSessionid() {
        assertEquals(0, storage.getSessionid());
    }

    @org.junit.jupiter.api.Test
    void saveFile() {
        File f = new File(1, "f", 6, storage.getFiles(), storage.getOwner(), "ayy");

        storage.getFiles().getFiles().add(f);
        assertEquals("ayy", storage.getFiles().getFile("f").getText());

        assertEquals(true, storage.lockFile(f, storage.getOwner()));

        assertEquals(true, storage.saveFile(f, "ayy2", storage.getOwner()));

        assertEquals("ayy2", storage.getFiles().getFile("f").getText());
    }

    @org.junit.jupiter.api.Test
    void lockFile() {
        File f = new File(1, "f", 6, storage.getFiles(), storage.getOwner(), "ayy");

        storage.getFiles().getFiles().add(f);
        assertEquals("ayy", storage.getFiles().getFile("f").getText());

        assertEquals(true, storage.lockFile(f, storage.getOwner()));
    }

    @org.junit.jupiter.api.Test
    void cancelEditFile() {
        File f = new File(1, "f", 6, storage.getFiles(), storage.getOwner(), "ayy");

        storage.getFiles().getFiles().add(f);
        assertEquals("ayy", storage.getFiles().getFile("f").getText());

        assertEquals(true, storage.lockFile(f, storage.getOwner()));

        assertEquals(true, storage.cancelEditFile(f, storage.getOwner()));

        assertEquals("ayy", storage.getFiles().getFile("f").getText());
    }
}