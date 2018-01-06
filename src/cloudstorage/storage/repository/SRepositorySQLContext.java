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

        return root;
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
                            //Fucking beautiful
                            LocalDateTime.ofInstant(((Timestamp) results.getObject("created")).toInstant(), ZoneOffset.ofHours(0)).toLocalDate(),
                            LocalDateTime.ofInstant(((Timestamp) results.getObject("edited")).toInstant(), ZoneOffset.ofHours(0)).toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to get folder children");
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
}
