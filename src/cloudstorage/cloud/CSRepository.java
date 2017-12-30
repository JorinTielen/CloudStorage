package cloudstorage.cloud;

public class CSRepository {
    private ICSRepositoryContext context;

    CSRepository(ICSRepositoryContext context) {
        this.context = context;
    }

    public boolean login(String username, String password) {
        return this.context.login(username, password);
    }

    public boolean register(String username, String password, String email) {
        return this.context.register(username, password, email);
    }
}
