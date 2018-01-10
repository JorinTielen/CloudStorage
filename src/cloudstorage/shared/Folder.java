package cloudstorage.shared;

import java.io.Serializable;
import java.util.ArrayList;
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
        if (name.equals(this.name)) {
            return this;
        }

        for (Folder f : children) {
            if (f.name.equals(name)) {
                return f;
            }
        }

        for (Folder f : children) {
            Folder correctFolder = f.getFolder(name);
            if (correctFolder != null) {
                return correctFolder;
            }
        }

        return null;
    }

    public Folder getFolder(int id) {
        if (id == this.id) {
            return this;
        }

        for (Folder f : children) {
            if (f.id == id) {
                return f;
            }
        }

        for (Folder f : children) {
            Folder correctFolder = f.getFolder(id);
            if (correctFolder != null) {
                return correctFolder;
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

    public File getFile(String name) {
        for (File f : files) {
            if (f.getName().equals(name)) {
                return f;
            }
        }

        for (Folder f : children) {
            File file = f.getFile(name);
            if (file != null) {
                return file;
            }
        }

        return null;
    }

    public File getFile(int id) {
        for (File f : files) {
            if (f.getId().equals(id)) {
                return f;
            }
        }

        for (Folder f : children) {
            File file = f.getFile(id);
            if (file != null) {
                return file;
            }
        }

        return null;
    }


    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public List<Folder> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name;
    }
}
