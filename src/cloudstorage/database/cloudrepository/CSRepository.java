package cloudstorage.database.cloudrepository;

import cloudstorage.shared.Account;

public class CSRepository {
    private ICSRepositoryContext context;

    public CSRepository(ICSRepositoryContext context) {
        this.context = context;
    }

    public boolean login(String username, String password) {
        return this.context.login(username, password);
    }

    public boolean register(String username, String password, String email) {
        return this.context.register(username, password, email);
    }

    public int getStorageId(int owner_id) {
        return this.context.getStorageId(owner_id);
    }

    public Account getAccount(String username) {
        return this.context.getAccount(username);
    }

    public Account getAccountFromStorage(int storage_id) {
        return this.context.getAccountFromStorage(storage_id);
    }
}
