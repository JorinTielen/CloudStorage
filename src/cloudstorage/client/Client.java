package cloudstorage.client;

import cloudstorage.storage.Storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(LocalStorage.class.getName());

    private Integer session;
    private LocalStorage localStorage;

    public Client() {
        connectToCloudStorage("localhost");
    }

    private void connectToCloudStorage(String ip) {
        int port = 1099;
        Registry registry = null;

        Storage remoteStorage = null;

        LOGGER.info("Connecting to: " + ip + " : " + port);

        //Locate the registry
        try {
            registry = LocateRegistry.getRegistry(ip, port);
        } catch (RemoteException e) {
            LOGGER.severe("Cannot locate registry");
            LOGGER.severe("RemoteException: " + e.getMessage());
        }

        //Bind with Registry
        if (registry != null) {
            try {
                remoteStorage = (Storage) registry.lookup("remoteStorage");
            } catch (RemoteException e) {
                LOGGER.severe("RemoteException when binding remoteStorage");
                LOGGER.severe("RemoteException: " + e.getMessage());
            } catch (NotBoundException e) {
                LOGGER.severe("NotBoundException when binding remoteStorage");
                LOGGER.severe("NotBoundException: " + e.getMessage());
            }
        }

        //Test connection
        if (remoteStorage != null) {
            try {
                localStorage = new LocalStorage(remoteStorage);
            } catch (RemoteException e) {
                LOGGER.severe("RemoteException when testing connection");
                LOGGER.severe("RemoteException: " + e.getMessage());
            }
        }
    }
}
