package cloudstorage.cloud;

import cloudstorage.shared.IStorage;
import cloudstorage.storage.StorageServer;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

    /*@Test
    void registerStorageServer() {
        StorageServer server;
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            server = new StorageServer(localhost.getHostAddress());
            assertEquals(true, cloudstorage.registerStorageServer(server));
        } catch (RemoteException | UnknownHostException e) {
            e.printStackTrace();
        }
    }*/

    /*@Test
    void getAccountFromStorage() {
        IStorage storage = cloudstorage.register("henk", "asdf@mail", "asdf");

        assertNotEquals(null, storage);
        try {
            assertEquals("henk", cloudstorage.getAccountFromStorage(storage.getId()).getName());
        } catch (RemoteException e) {
            fail("could not get owner: " + e.getMessage());
        }
    }*/

    @Test
    void logoutStorage() {
    }

    @Test
    void getStorageReference() {
    }

    @Test
    void logout() {
    }
}