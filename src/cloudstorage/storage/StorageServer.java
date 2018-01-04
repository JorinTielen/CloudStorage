package cloudstorage.storage;

import cloudstorage.cloud.CloudStorage;
import cloudstorage.shared.ICloudStorage;
import cloudstorage.shared.IStorage;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.logging.Logger;

public class StorageServer extends UnicastRemoteObject implements IStorageServer {
    private static final Logger LOGGER = Logger.getLogger(StorageServer.class.getName());

    private ICloudStorage cloudStorage;
    private boolean waiting;

    private Storage storage;

    private StorageServer() throws RemoteException {
        connectToCloudStorage();
        waiting = true;
    }

    public IStorage assignStorage(ICloudStorage cloudStorage, int id) {
        try {
            this.storage = new Storage(cloudStorage, id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        waiting = false;

        return this.storage;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public static void main(String[] args) {
        try {
            new StorageServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void connectToCloudStorage() {
        // Get ip address of server
        Scanner input = new Scanner(System.in);
        System.out.print("StorageServer: Enter IP address of CloudStorage: ");
        String ip = input.nextLine();

        int port = 1099;

        LOGGER.info("StorageServer: IP Address: " + ip);
        LOGGER.info("StorageServer: Port number " + port);

        //Locate the registry
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(ip, port);
        } catch (RemoteException e) {
            LOGGER.severe("StorageServer: Cannot locate registry");
            LOGGER.severe("StorageServer: RemoteException: " + e.getMessage());
            registry = null;
        }

        // Print result locating registry
        if (registry != null) {
            LOGGER.info("StorageServer: Registry located");
        } else {
            LOGGER.info("StorageServer: Cannot locate registry");
            LOGGER.info("StorageServer: Registry is null pointer");
            return;
        }

        //Bind with Registry
        try {
            cloudStorage = (ICloudStorage) registry.lookup("CloudStorage");
        } catch (RemoteException e) {
            LOGGER.severe("StorageServer: RemoteException when binding cloudStorage");
            LOGGER.severe("StorageServer: RemoteException: " + e.getMessage());
        } catch (NotBoundException e) {
            LOGGER.severe("StorageServer: NotBoundException when binding cloudStorage");
            LOGGER.severe("StorageServer: NotBoundException: " + e.getMessage());
        }

        //Register myself
        try {
            cloudStorage.registerStorageServer(this);
        } catch (RemoteException e) {
            LOGGER.severe("StorageServer: RemoteException when registering to cloudStorage");
            LOGGER.severe("StorageServer: RemoteException: " + e.getMessage());
        }
    }
}
