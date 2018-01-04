package cloudstorage.storage.repository;

import cloudstorage.cloud.repository.CSRepositorySQLContext;
import cloudstorage.database.SQLConnector;
import cloudstorage.shared.Account;
import cloudstorage.shared.Folder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

        //TODO: Get the folder's files.

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

                    getFolderChildren(owner, a);

                    folder.getChildren().add(a);
                    a.setParent(folder);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
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

            pStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return false;
    }
}
