package cloudstorage.shared;

import java.io.Serializable;

public class Account implements Serializable {
    private int id;
    private String name;
    private String email;

    public Account(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
