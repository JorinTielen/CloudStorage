package cloudstorage.client;

import cloudstorage.shared.File;
import cloudstorage.shared.Folder;
import cloudstorage.shared.ICloudStorage;
import cloudstorage.shared.IStorage;
import cloudstorage.storage.Storage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    private ICloudStorage cloudStorage;
    private CloudStorageFX ui;

    private Integer session;
    private LocalStorage localStorage;

    Client(CloudStorageFX ui) {
        this.ui = ui;
        connectToCloudStorage("145.93.164.5");
    }

    Client() {
        //ONLY FOR TESTING PURPOSES!!!
    }

    public boolean login(String username, String password) {
        if (cloudStorage != null) {
            try {
                IStorage remoteStorage = cloudStorage.login(username, password);
                if (remoteStorage != null) {
                    localStorage = new LocalStorage(remoteStorage, this);
                    LOGGER.info("Client: Login successful");
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

    public boolean register(String username, String password, String email) {
        if (cloudStorage != null) {
            try {
                IStorage remoteStorage = cloudStorage.register(username, email, password);
                if (remoteStorage != null) {
                    localStorage = new LocalStorage(remoteStorage, this);
                    LOGGER.info("Client: Register successful");
                    return true;
                } else {
                    LOGGER.info("Client: Register failed");
                    return false;
                }
            } catch (RemoteException e) {
                LOGGER.severe("Client: RemoteException when trying to register");
                LOGGER.severe("Client: RemoteException: " + e.getMessage());
            }
        }

        return false;
    }

    public Folder getRoot() {
        return localStorage.getRoot();
    }

    public Folder getSelectedFolder() {
        return localStorage.getSelectedFolder();
    }

    public void selectFolder(Folder folder) {
        localStorage.selectFolder(folder);
    }

    public boolean createFolder(String name) {
        return localStorage.createFolder(name);
    }

    public boolean createFile(String name) {
        return localStorage.createFile(name);
    }

    public void updateUI() {
        ui.updateFileList();
    }

    private void connectToCloudStorage(String ip) {
        int port = 1099;

        LOGGER.info("Client: IP Address: " + ip);
        LOGGER.info("Client: Port number " + port);

        //Locate the registry
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(ip, port);
        } catch (RemoteException e) {
            LOGGER.severe("Client: Cannot locate registry");
            LOGGER.severe("Client: RemoteException: " + e.getMessage());
            registry = null;
        }

        // Print result locating registry
        if (registry != null) {
            LOGGER.info("Client: Registry located");
        } else {
            LOGGER.info("Client: Cannot locate registry");
            LOGGER.info("Client: Registry is null pointer");
            return;
        }

        //Bind with Registry
        try {
            cloudStorage = (ICloudStorage) registry.lookup("CloudStorage");
        } catch (RemoteException e) {
            LOGGER.severe("Client: RemoteException when binding cloudStorage");
            LOGGER.severe("Client: RemoteException: " + e.getMessage());
        } catch (NotBoundException e) {
            LOGGER.severe("Client: NotBoundException when binding cloudStorage");
            LOGGER.severe("Client: NotBoundException: " + e.getMessage());
        }
    }

    public boolean requestEditFile(File file) {
        return localStorage.requestEditFile(file);
    }

    public boolean saveFile(File file, String fileText) {
        return localStorage.saveFile(file, fileText);
    }

    public void cancelEditFile(File file) {
        localStorage.cancelEditFile(file);
    }
}
