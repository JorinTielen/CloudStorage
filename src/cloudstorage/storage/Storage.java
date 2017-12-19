package cloudstorage.storage;

import cloudstorage.shared.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Storage extends UnicastRemoteObject implements IStorage, IFileProvider {
    private Folder root = new Folder(1, "Your Storage");
    private Folder shared = new Folder(2, "Shared with You");

    private Account owner = new Account(1, "you", "you@you.com");

    public Storage() throws RemoteException {

    }

    @Override
    public Folder getRoot() throws RemoteException {
        return root;
    }

    @Override
    public Folder getShared() throws RemoteException {
        return shared;
    }

    @Override
    public Account getOwner() throws RemoteException {
        return owner;
    }

    @Override
    public boolean upload(IFile file, Folder location) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File download(int fileId) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean LockFile(IFile file, int accountId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean SaveFile(IFile file, int accountId) {
        throw new UnsupportedOperationException();
    }
}
