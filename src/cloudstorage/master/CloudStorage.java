package cloudstorage.master;

import cloudstorage.shared.IStorage;
import cloudstorage.storage.Storage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CloudStorage extends UnicastRemoteObject {
    private List<Storage> storages = new ArrayList<>();

    public CloudStorage() throws RemoteException {
    }

    public IStorage login(String username, String password) {
        throw new UnsupportedOperationException();
    }

    public IStorage register(String username, String email, String password) {
        throw new UnsupportedOperationException();
    }

    public void logout(int session) {
        throw new UnsupportedOperationException();
    }
}
