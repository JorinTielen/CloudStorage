package cloudstorage.cloud.repository;

import cloudstorage.shared.Account;
import cloudstorage.storage.Storage;

import java.util.ArrayList;
import java.util.List;

public class CSRepositoryLocalContext implements ICSRepositoryContext {
    private List<Account> accounts = new ArrayList<>();

    private List<Storage> storages = new ArrayList<>();

    public CSRepositoryLocalContext() {
        accounts.add(new Account(1, "jorin", "jorin@mail.com"));
    }


    @Override
    public boolean login(String username, String password) {
        for (Account a : accounts) {
            if (username.equals(a.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean register(String username, String password, String email) {
        accounts.add(new Account(2, username, email));
        return true;
    }

    @Override
    public int getStorageId(int owner_id) {
        return 99;
    }

    @Override
    public Account getAccount(String username) {
        for (Account a : accounts) {
            if (username.equals(a.getName())) {
                return a;
            }
        }

        return null;
    }

    @Override
    public Account getAccountFromStorage(int id) {
        throw new UnsupportedOperationException();
    }
}
