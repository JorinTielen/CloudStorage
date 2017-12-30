package cloudstorage.cloud;

import cloudstorage.shared.ICloudStorage;
import cloudstorage.shared.IStorage;
import cloudstorage.storage.Storage;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CloudStorage extends UnicastRemoteObject implements ICloudStorage {
    private static final Logger LOGGER = Logger.getLogger(CloudStorage.class.getName());

    private static final String BINDINGNAME = "CloudStorage";

    private CSRepository repository;

    private List<Storage> storages = new ArrayList<>();

    public CloudStorage() throws RemoteException {
        startCloudStorage();

        repository = new CSRepository(new CSRepositorySQLContext());

        storages.add(new Storage(this));
    }

    public IStorage login(String username, String password) {
        if (repository.login(username, password)) {
            try {
                return new Storage(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean registerStorage(Storage storage) throws RemoteException {
        storages.add(storage);
        return true;
    }

    public IStorage register(String username, String email, String password) {
        if (repository.register(username, password, email)) {
            try {
                return new Storage(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void logout(int session) {
        throw new UnsupportedOperationException();
    }

    private void startCloudStorage() {
        Registry registry = null;

        //Print IP Address
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
            LOGGER.info("CloudStorage: IP Address: " + localhost.getHostAddress());
        } catch (UnknownHostException e) {
            LOGGER.severe("CloudStorage: Cannot get localhost");
            LOGGER.severe("CloudStorage: UnknownHostException: " + e.getMessage());
        }

        //Create Registry
        try {
            registry = LocateRegistry.createRegistry(1099);
            LOGGER.info("CloudStorage: Registry created");
        } catch (RemoteException e) {
            LOGGER.severe("CloudStorage: Cannot create registry");
            LOGGER.severe("CloudStorage: RemoteException: " + e.getMessage());
        }

        try {
            if (registry != null) {
                registry.rebind(BINDINGNAME, this);
                LOGGER.info("CloudStorage: CloudStorage bound to registry");
            }
        } catch (RemoteException e) {
            LOGGER.severe("CloudStorage: Cannot bind CloudStorage to Registry");
            LOGGER.severe("CloudStorage: RemoteException: " + e.getMessage());
        } catch (NullPointerException e) {
            LOGGER.severe("Server: Port already in use. Please check if the server isn't already running");
            LOGGER.severe("Server: NullPointerException: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            new CloudStorage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
