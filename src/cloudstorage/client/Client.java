package cloudstorage.client;

import cloudstorage.master.CloudStorage;
import cloudstorage.shared.IStorage;
import cloudstorage.storage.Storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(LocalStorage.class.getName());

    private CloudStorage cloudStorage;

    private Integer session;
    private LocalStorage localStorage;

    public Client() {
        connectToCloudStorage("localhost");
    }

    private void connectToCloudStorage(String ip) {
        int port = 1099;
        Registry registry = null;

        Storage remoteStorage = null;

        LOGGER.info("Client: Connecting to: " + ip + " : " + port);

        //Locate the registry
        try {
            registry = LocateRegistry.getRegistry(ip, port);
        } catch (RemoteException e) {
            LOGGER.severe("Client: Cannot locate registry");
            LOGGER.severe("Client: RemoteException: " + e.getMessage());
        }

        //Bind with Registry
        if (registry != null) {
            try {
                cloudStorage = (CloudStorage) registry.lookup("CloudStorage");
            } catch (RemoteException e) {
                LOGGER.severe("Client: RemoteException when binding remoteStorage");
                LOGGER.severe("Client: RemoteException: " + e.getMessage());
            } catch (NotBoundException e) {
                LOGGER.severe("Client: NotBoundException when binding remoteStorage");
                LOGGER.severe("Client: NotBoundException: " + e.getMessage());
            }
        }
    }

    public boolean login(String username, String password) {
        if (cloudStorage != null) {
            try {
                IStorage remoteStorage = cloudStorage.login(username, password);
                if (remoteStorage != null) {
                    LOGGER.info("Client: Login successful");
                    localStorage = new LocalStorage(remoteStorage);
                    return true;
                } else {
                    LOGGER.info("Client: Login failed");
                    return false;
                }
            } catch (RemoteException e) {
                LOGGER.severe("Client: RemoteException when trying to log in");
                LOGGER.severe("Client: RemoteException: " + e.getMessage());
            }
        }
        return false;
    }
}
