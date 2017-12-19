package cloudstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Folder implements IViewable, Serializable {
    private int id;
    private String name;

    private Folder parent;
    private List<Folder> children;

    private List<File> files;

    public Folder(int id, String name) {
        this.id = id;
        this.name = name;

        this.children = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public Folder(int id, String name, Folder parent) {
        this(id, name);

        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public Folder getParent() {
        return parent;
    }

    public List<Folder> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean addFolder(String name) {
        for (Folder f : children) {
            if (f.getName().equals(name)) {
                return false;
            }
        }

        Folder f = new Folder(1, name , this);
        children.add(f);
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
