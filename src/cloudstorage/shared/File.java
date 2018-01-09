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

    private boolean locked = false;
    private int lockedById = -1;

    //With content
    public File(int id, String name, int size, Folder location, Account owner, String text) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.location = location;
        this.owner = owner;
        this.text = text;
    }

    //without content
    public File(int id, String name, int size, Folder location, Account owner, String text, LocalDate createdAt, LocalDate editedAt) {
        this.id = id;
        this.name = name;
        this.owner = owner;

        this.location = location;

        this.size = size;
        this.text = text;

        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }

    public boolean lock(int accountId) {
        if (lockedById == -1) {
            lockedById = accountId;
            locked = true;
            return true;
        }

        return false;
    }

    public boolean unlock(int accountId) {
        if (locked) {
            if (lockedById == accountId) {
                lockedById = -1;
                locked = false;
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public boolean editText(Account a, String text) {
        if (locked) {
            if (lockedById == a.getId()) {
                this.text = text;
                editedAt = LocalDate.now();
                unlock(a.getId());
                return true;
            }
        }

        return false;
    }

    public String getText() {
        return text;
    }

    public Integer getId() {
        return id;
    }

    public Account getOwner() {
        return owner;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getEditedAt() {
        return editedAt;
    }

    public Folder getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return name;
    }
}
