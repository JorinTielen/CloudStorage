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
            for (Folder f : getFolderChildren(owner, root.getId())) {
                root.getChildren().add(f);
            }
        }

        //TODO: Get the folder's files.

        return root;
    }

    public List<Folder> getFolderChildren(Account owner, int folder_id) {
        String SQL = "SELECT id, \"name\" FROM folders WHERE parent_id = ?";

        List<Folder> folders = new ArrayList<>();

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, folder_id);
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    folders.add(new Folder(
                            results.getInt("id"),
                            results.getString("name"),
                            owner));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return folders;
    }
}
