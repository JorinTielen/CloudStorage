package cloudstorage.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FolderTest {

    private Account owner;
    private Folder folder;

    public FolderTest() {
        owner = new Account(1, "testaccount", "mail");
        folder = new Folder(1, "testfolder", owner);
    }

    @Test
    void getFolder() {
        folder.getChildren().add(new Folder(2, "testfolderchild", owner, folder));

        assertEquals("testfolder", folder.getFolder(1).getName());

        Folder folderWithId2 = folder.getFolder(2);

        assertEquals(2, (int) folderWithId2.getId());

        folderWithId2.getChildren().add(new Folder(4, "testfolderchild4", owner, folderWithId2));

        assertEquals("testfolderchild4", folder.getFolder(4).getName());

        assertEquals(null, folder.getFolder(999));
    }

    @Test
    void getFolder1() {
        folder.getChildren().add(new Folder(3, "testfolderchild3", owner, folder));

        assertEquals("testfolder", folder.getFolder("testfolder").getName());

        Folder folderWithId3 = folder.getFolder("testfolderchild3");

        assertEquals("testfolderchild3", folderWithId3.getName());

        folderWithId3.getChildren().add(new Folder(4, "testfolderchild4", owner, folderWithId3));

        assertEquals("testfolderchild4", folder.getFolder("testfolderchild4").getName());

        assertEquals(null, folder.getFolder("asdf"));
    }

    @Test
    void getId() {
        assertEquals(1, (int) folder.getId());
    }

    @Test
    void getName() {
        assertEquals("testfolder", folder.getName());
    }

    @Test
    void getFiles() {
        folder.getFiles().add(new File(1, "file", 8, folder, owner, "text"));

        File f = folder.getFiles().get(0);

        assertEquals(1, (int) f.getId());
    }

    @Test
    void getFile() {
        folder.getFiles().add(new File(1, "file", 8, folder, owner, "text"));

        File f = folder.getFile(1);

        assertEquals(1, (int) f.getId());

        Folder folderChild = new Folder(2, "testfolderchild", owner, folder);
        folderChild.getFiles().add(new File(2, "file2", 10, folder, owner, "text2"));
        folder.getChildren().add(folderChild);

        assertEquals(2, (int) folder.getFile(2).getId());

        assertEquals(null, folder.getFile(999));
    }

    @Test
    void getFile1() {
        folder.getFiles().add(new File(1, "file", 8, folder, owner, "text"));

        File f = folder.getFile("file");

        assertEquals("file", f.getName());

        Folder folderChild = new Folder(2, "testfolderchild", owner, folder);
        folderChild.getFiles().add(new File(2, "file2", 10, folder, owner, "text2"));
        folder.getChildren().add(folderChild);

        assertEquals("file2", folder.getFile("file2").getName());

        assertEquals(null, folder.getFile("aksjdhf"));
    }

    @Test
    void getParent() {
        Folder f = new Folder(2, "test2", owner);
        f.setParent(folder);
        String name = f.getParent().getName();

        assertEquals("testfolder", name);
    }

    @Test
    void setParent() {
        Folder f = new Folder(2, "test2", owner);
        f.setParent(folder);

        assertEquals("testfolder", f.getParent().getName());
    }

    @Test
    void getChildren() {
        folder.getChildren().add(new Folder(2, "testfolderchild", owner, folder));

        Folder folderWithId2 = folder.getChildren().get(0);

        assertEquals(2, (int) folderWithId2.getId());
    }

    @Test
    void testToString() {
        assertEquals("testfolder", folder.toString());
    }
}