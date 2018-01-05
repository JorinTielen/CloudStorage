package cloudstorage.cloud;

import cloudstorage.shared.IStorage;
import cloudstorage.storage.StorageServer;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class CloudStorageTest {

    private CloudStorage cloudstorage;

    public CloudStorageTest() {
        try {
            cloudstorage = new CloudStorage(true);
        } catch (RemoteException e) {
            fail("could not create cloudStorage");
        }
    }

    @Test
    void login() {
        IStorage storage = cloudstorage.login("jorin", "asdf");

        assertNotEquals(null, storage);
        try {
            assertEquals("jorin", storage.getOwner().getName());
        } catch (RemoteException e) {
            fail("could not get owner: " + e.getMessage());
        }
    }

    @Test
    void register() {
        IStorage storage = cloudstorage.register("henk", "asdf@mail", "asdf");

        assertNotEquals(null, storage);
        try {
            assertEquals("henk", storage.getOwner().getName());
        } catch (RemoteException e) {
            fail("could not get owner: " + e.getMessage());
        }
    }
}