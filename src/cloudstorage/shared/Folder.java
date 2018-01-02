package cloudstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Folder implements IViewable, Serializable {
    private int id;
    private String name;
    private Account owner;

    private Folder parent;
    private List<Folder> children;

    private List<File> files;

    public Folder(int id, String name, Account owner) {
        this.id = id;
        this.name = name;

        this.children = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public Folder(int id, String name, Account owner, Folder parent) {
        this(id, name, owner);

        this.parent = parent;
    }

    public Folder getFolder(String name) {
        for (Folder f : children) {
            if (f.name.equals(name)) {
                return f;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<File> getFiles() {
        return files;
    }

    public Folder getParent() {
        return parent;
    }

    public List<Folder> getChildren() {
        return children;
    }

    public boolean addFolder(String name) {
        for (Folder f : children) {
            if (f.getName().equals(name)) {
                return false;
            }
        }

        Folder f = new Folder(8, name, owner, this);
        children.add(f);
        return true;
    }

    public boolean addFile(String name) {
        for (File f : files) {
            if (f.getName().equals(name)) {
                return false;
            }
        }

        File f = new File(name, 1, owner);
        files.add(f);
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
