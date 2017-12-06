package cloudstorage.shared;

import java.util.List;

public class Folder {
    private int id;
    private String name;

    private Folder parent;
    private List<Folder> children;

    public Folder(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Folder(int id, String name, Folder parent) {
        this(id, name);

        this.parent = parent;
    }
}
