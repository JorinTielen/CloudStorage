package cloudstorage.client;

import cloudstorage.cloud.CloudStorage;
import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;
import cloudstorage.storage.Storage;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class LocalStorageTest {
    private Client client;
    private LocalStorage localStorage;
    private Storage remoteStorage;

    public LocalStorageTest() {
        this.client = new Client();

        Account a = new Account(1, "jorin", "jorin@mail.com");
        CloudStorage cloudStorage = null;
        try {
            cloudStorage = new CloudStorage();
        } catch (RemoteException e) {
            fail("cloudStorage could not be created: " + e.getMessage());
        }

        try {
            this.remoteStorage = new Storage(a, cloudStorage, 1, true);
        } catch (RemoteException e) {
            fail("remoteStorage could not be created: " + e.getMessage());
        }

        try {
            this.localStorage = new LocalStorage(remoteStorage, client);
        } catch (RemoteException e) {
            fail("localStorage could not be created: " + e.getMessage());
        }
    }

    @Test
    void getRoot() {
        Folder root = localStorage.getRoot();

        assertEquals("root", root.getName());
    }

    @Test
    void getSelectedFolder() {
        assertEquals("root", localStorage.getSelectedFolder().getName());

        selectFolder();
    }

    @Test
    void selectFolder() {
        localStorage.selectFolder(localStorage.getRoot().getChildren().get(0));

        assertEquals("Your Storage", localStorage.getSelectedFolder().getName());
    }

    @Test
    void createFolder() {
        localStorage.selectFolder(localStorage.getRoot().getChildren().get(0));
        localStorage.createFolder("ayy");

        assertEquals("ayy", localStorage.getSelectedFolder().getChildren().get(0).getName());
    }
}