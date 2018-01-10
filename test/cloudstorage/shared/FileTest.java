package cloudstorage.shared;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FileTest {

    private Account owner = new Account(1, "dude", "email");
    private Folder folder = new Folder(1, "folder", owner);
    private File file = new File(1, "file", 8, folder, owner, "test");

    @Test
    void lock() {
        assertEquals(true, file.lock(1));

        assertEquals(false, file.lock(2));
    }

    @Test
    void unlock() {
        assertEquals(true, file.lock(1));

        assertEquals(false, file.lock(2));

        assertEquals(false, file.unlock(2));

        assertEquals(true, file.unlock(1));

        assertEquals(true, file.lock(2));
    }

    @Test
    void getName() {
        assertEquals("file", file.getName());
    }

    @Test
    void rename() {
        file.rename("test");

        assertEquals("test", file.getName());
    }

    @Test
    void editText() {
        file.lock(owner.getId());
        file.editText(owner, "test text");
        file.unlock(owner.getId());

        assertEquals("test text", file.getText());

        assertEquals(false, file.editText(owner, "test text"));
    }

    @Test
    void getText() {
        assertEquals("test", file.getText());

        file.lock(owner.getId());
        file.editText(owner, "test text");
        file.unlock(owner.getId());

        assertEquals("test text", file.getText());
    }

    @Test
    void getId() {
        assertEquals(1, (int) file.getId());
    }

    @Test
    void getOwner() {
        assertEquals(owner, file.getOwner());
    }

    @Test
    void getCreatedAt() {
        LocalDate date1 = LocalDate.of(1999, 4, 27);
        LocalDate date2 = LocalDate.of(1999, 4, 27);
        File fileWithDate = new File(1, "file", 8, folder, owner, "test", date1, date2);

        assertEquals(date1, fileWithDate.getCreatedAt());
    }

    @Test
    void getEditedAt() {
        LocalDate date1 = LocalDate.of(1999, 4, 27);
        LocalDate date2 = LocalDate.of(1999, 4, 27);
        File fileWithDate = new File(1, "file", 8, folder, owner, "test", date1, date2);

        assertEquals(date1, fileWithDate.getEditedAt());
    }

    @Test
    void getLocation() {
        assertEquals(folder, file.getLocation());
    }

    @Test
    void TestToString() {
        assertEquals("file", file.toString());
    }

    @Test
    void setOwner() {
        Account newOwner = new Account(2, "new owner", "mail2");
        file.setOwner(newOwner);
        assertEquals(newOwner, file.getOwner());
    }
}