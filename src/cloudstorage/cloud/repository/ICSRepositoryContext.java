package cloudstorage.cloud.repository;

import cloudstorage.shared.Account;

public interface ICSRepositoryContext {

    boolean login(String username, String password);

    boolean register(String username, String password, String email);

    int getStorageId(int owner_id);

    Account getAccount(String username);
}
