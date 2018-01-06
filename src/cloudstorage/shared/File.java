package cloudstorage.shared;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class File implements IViewable, Serializable {
    private int id;
    private String name;
    private int size;
    private Folder location;
    private Account owner;

    private String text;

    private LocalDate createdAt;
    private LocalDate editedAt;

    public File(int id, String name, Account owner, Folder location) {
        this.id = id;
        this.name = name;
        this.owner = owner;

        this.location = location;

        this.size = 0;
        this.text = "";
    }

    public File(int id, String name, int size, Folder location, Account owner, String text) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.location = location;
        this.owner = owner;
        this.text = text;
    }

    public File(int id, String name, int size, Folder location, Account owner, String text, LocalDate createdAt, LocalDate editedAt) {
        this(id, name, size, location, owner, text);

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

    public String getText() {
        return text;
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
