package cloudstorage.cloud;

public interface ICSRepositoryContext {

    boolean login(String username, String password);

    boolean register(String username, String password, String email);
}
