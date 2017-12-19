package cloudstorage.shared;

import java.io.Serializable;
import java.util.Date;

public class File implements IViewable, Serializable {
    private int id;
    private String name;
    private int size;
    private Folder location;
    private Account owner;

    private String locationOnDisk;

    private Date createdAt;
    private Date editedAt;

    public File(String name, int size, Account owner) {
        this.name = name;
        this.size = size;
        this.owner = owner;
    }

    public File(int id, String name, int size, Folder location, Account owner, String locationOnDisk) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.location = location;
        this.owner = owner;
        this.locationOnDisk = locationOnDisk;
    }

    public File(int id, String name, int size, Folder location, Account owner, String locationOnDisk, Date createdAt, Date editedAt) {
        this(id, name, size, location, owner, locationOnDisk);

        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }

    @Override
    public String getName() {
        return name;
    }

    public void rename(String name) {

    }

    public void editContent() {

    }

    public Integer getId() {
        return null;
    }

    public Account getOwner() {
        return null;
    }

    public Date getCreatedAt() {
        return null;
    }

    public Date getEditedAt() {
        return null;
    }

    public Folder getLocation() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
