package cloudstorage.shared;

import java.util.Date;

public class File implements IFile {
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
    public void rename(String name) {

    }

    @Override
    public void editContent() {

    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public Account getOwner() {
        return null;
    }

    @Override
    public Date getCreatedAt() {
        return null;
    }

    @Override
    public Date getEditedAt() {
        return null;
    }

    @Override
    public Folder getLocation() {
        return null;
    }
}
