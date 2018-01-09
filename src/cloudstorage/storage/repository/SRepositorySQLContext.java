package cloudstorage.storage.repository;

import cloudstorage.cloud.repository.CSRepositorySQLContext;
import cloudstorage.database.SQLConnector;
import cloudstorage.shared.Account;
import cloudstorage.shared.File;
import cloudstorage.shared.Folder;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class SRepositorySQLContext implements ISRepositoryContext {
    private static final Logger LOGGER = Logger.getLogger(CSRepositorySQLContext.class.getName());

    private SQLConnector connector = new SQLConnector();

    public Folder getRoot(Account owner) {
        String SQL = "SELECT id, \"name\" FROM folders WHERE owner_id = ?";

        Folder root = null;

        connector.openConnection();
        //First, get the root folder
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, owner.getId());
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    if (results.getString("name").equals("root")) {
                        root = new Folder(
                                results.getInt("id"),
                                results.getString("name"),
                                owner);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        //Now, get all it's children
        if (root != null) {
            getFolderChildren(owner, root);
        }

        //Now get the shared files
        if (root != null) {
            getSharedFiles(root.getFolder("Shared with You"), owner, root);
        }

        return root;
    }

    private void getSharedFiles(Folder sharedFolder, Account owner, Folder root) {
        String SQL = "SELECT f.id, f.\"name\", f.folder_id, f.owner_id, f.filetext, f.created, f.edited, f.size FROM files f JOIN shared_files sf ON f.id = sf.orig_file_id WHERE sf.shared_with_id = ?";

        List<File> files = new ArrayList<>();

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, owner.getId());
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    files.add(new File(
                            results.getInt("id"),
                            results.getString("name"),
                            results.getInt("size"),
                            sharedFolder,
                            owner,
                            results.getString("filetext"),
                            LocalDateTime.ofInstant(((Timestamp) results.getObject("created")).toInstant(), ZoneOffset.ofHours(0)).toLocalDate(),
                            LocalDateTime.ofInstant(((Timestamp) results.getObject("edited")).toInstant(), ZoneOffset.ofHours(0)).toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to get shared files");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        sharedFolder.getFiles().addAll(files);
    }

    public void getFolderChildren(Account owner, Folder folder) {
        String SQL = "SELECT id, \"name\" FROM folders WHERE parent_id = ?";

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, folder.getId());
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    Folder a = new Folder(
                            results.getInt("id"),
                            results.getString("name"),
                            owner);

                    a.getFiles().addAll(getFolderFiles(a, owner));

                    getFolderChildren(owner, a);

                    folder.getChildren().add(a);
                    a.setParent(folder);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to get folder children");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }
    }

    @Override
    public boolean addFolder(Account owner, String name, Folder parent) {
        String SQL = "INSERT INTO folders (\"name\", parent_id, owner_id) VALUES (?, ?, ?)";

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setString(1, name);
            pStmt.setInt(2, parent.getId());
            pStmt.setInt(3, owner.getId());

            return pStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to add folder");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return false;
    }

    private List<File> getFolderFiles(Folder folder, Account owner) {
        String SQL = "SELECT id, \"name\", folder_id, owner_id, filetext, created, edited, size FROM files WHERE folder_id = ?";

        List<File> files = new ArrayList<>();

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, folder.getId());
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    files.add(new File(
                            results.getInt("id"),
                            results.getString("name"),
                            results.getInt("size"),
                            folder,
                            owner,
                            results.getString("filetext"),
                            LocalDateTime.ofInstant(((Timestamp) results.getObject("created")).toInstant(), ZoneOffset.ofHours(0)).toLocalDate(),
                            LocalDateTime.ofInstant(((Timestamp) results.getObject("edited")).toInstant(), ZoneOffset.ofHours(0)).toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to get folder files");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return files;
    }

    @Override
    public boolean addFile(String name, Folder parent, Account owner) {
        String SQL = "INSERT INTO files (\"name\", folder_id, owner_id, " +
                                           "filetext, created, edited, size) VALUES (?, ?, ?, ?, ?, ?, ?)";

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setString(1, name);
            pStmt.setInt(2, parent.getId());
            pStmt.setInt(3, owner.getId());

            pStmt.setString(4, "");

            LocalDate localDate = LocalDate.now();
            pStmt.setObject(5, localDate);
            pStmt.setObject(6, localDate);

            int size = 0;
            try {
                size = "".getBytes("UTF-16BE").length;
            } catch (UnsupportedEncodingException e) {
                LOGGER.severe("SQLContext: UnsupportedEncodingException when trying to get files size");
                LOGGER.severe("SQLContext: UnsupportedEncodingException: " + e.getMessage());
            }
            pStmt.setInt(7, size);

            return pStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to add file");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return false;
    }

    @Override
    public boolean saveFile(File file) {
        String SQL = "UPDATE files SET \"name\" = ?, filetext = ?, edited = ?, size = ? WHERE id = ?";

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setString(1, file.getName());
            pStmt.setString(2, file.getText());
            pStmt.setObject(3, file.getEditedAt());

            int size = 0;
            try {
                size = file.getText().getBytes("UTF-16BE").length;
            } catch (UnsupportedEncodingException e) {
                LOGGER.severe("SQLContext: UnsupportedEncodingException when trying to get files size");
                LOGGER.severe("SQLContext: UnsupportedEncodingException: " + e.getMessage());
            }
            pStmt.setInt(4, size);
            pStmt.setInt(5, file.getId());

            return pStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to add file");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return false;
    }

    @Override
    public boolean shareFile(File file, String username) {
        String SQL = "INSERT INTO shared_files (orig_file_id, shared_with_id) VALUES (?, ?)";

        //First, get the id of to share person
        int accountid = -1;
        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement("SELECT id FROM accounts WHERE username = ?")) {
            pStmt.setString(1, username);
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    accountid = results.getInt("id");
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to add file");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        if (accountid != -1) {
            connector.openConnection();
            try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
                pStmt.setInt(1, file.getId());
                pStmt.setInt(2, accountid);

                return pStmt.executeUpdate() > 0;
            } catch (SQLException e) {
                LOGGER.severe("SQLContext: SQLException when trying to add file");
                LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
            } finally {
                connector.closeConnection();
            }
        }

        return false;
    }
}
