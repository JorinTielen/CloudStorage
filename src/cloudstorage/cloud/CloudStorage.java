package cloudstorage.cloud;

import cloudstorage.cloud.repository.CSRepository;
import cloudstorage.cloud.repository.CSRepositorySQLContext;
import cloudstorage.shared.Account;
import cloudstorage.shared.ICloudStorage;
import cloudstorage.shared.IStorage;
import cloudstorage.storage.IStorageServer;
import cloudstorage.storage.Storage;
import cloudstorage.storage.StorageServer;
import cloudstorage.storage.repository.SRepositorySQLContext;

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

    private static final String BINDING_NAME = "CloudStorage";

    private CSRepository repository;

    private List<IStorage> storages = new ArrayList<>();
    private List<IStorageServer> waitingServers = new ArrayList<>();

    public CloudStorage() throws RemoteException {
        startCloudStorage();

        repository = new CSRepository(new CSRepositorySQLContext());
    }

    public IStorage login(String username, String password) {
        if (repository.login(username, password)) {
            try {
                Account a = repository.getAccount(username);
                IStorage s;

                if (waitingServers.size() > 0) {
                    IStorageServer server = waitingServers.get(0);
                    s = server.assignStorage(a, this, repository.getStorageId(a.getId()));
                    waitingServers.remove(server);
                    System.out.println(waitingServers.size());
                } else {
                    s = new Storage(a, this, repository.getStorageId(a.getId()), false);
                }

                storages.add(s);
                return s;
            } catch (RemoteException e) {
                LOGGER.severe("CloudStorage: Cannot login");
                LOGGER.severe("CloudStorage: RemoteException: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean registerStorageServer(IStorageServer server) {
        waitingServers.add(server);
        System.out.println(waitingServers.size());
        return true;
    }

    @Override
    public Account getAccountFromStorage(int storage_id) {
        return repository.getAccountFromStorage(storage_id);
    }

    public IStorage register(String username, String email, String password) {
        if (repository.register(username, password, email)) {
            try {
                Account a = repository.getAccount(username);
                Storage s = new Storage(a, this, repository.getStorageId(a.getId()), false);
                storages.add(s);
                return s;
            } catch (RemoteException e) {
                LOGGER.severe("CloudStorage: Cannot register");
                LOGGER.severe("CloudStorage: RemoteException: " + e.getMessage());
            }
        }
        return null;
    }

    public void logout(int session) {
        for (IStorage s : storages) {
            try {
                if (session == s.getId()) {
                    storages.remove(s);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
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
                registry.rebind(BINDING_NAME, this);
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
