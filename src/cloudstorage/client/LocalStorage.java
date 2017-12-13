package cloudstorage.client;

import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;
import cloudstorage.storage.Storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class LocalStorage {
    private static final Logger LOGGER = Logger.getLogger(LocalStorage.class.getName());

    private Storage remoteStorage;

    private Folder root = new Folder(1, "Your Storage");
    private Folder shared = new Folder(2, "Shared with You");
    private Account owner = new Account(1, "you", "you@you.com");

    public LocalStorage(String ip) {
        connectToStorage(ip);
    }
    
    private void connectToStorage(String ip) {
        int port = 1099;
        Registry registry = null;

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


    }

}
