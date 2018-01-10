package cloudstorage.database.cloudrepository;

import cloudstorage.database.SQLConnector;
import cloudstorage.shared.Account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.logging.Logger;

public class CSRepositorySQLContext implements ICSRepositoryContext {
    private static final Logger LOGGER = Logger.getLogger(CSRepositorySQLContext.class.getName());

    private SQLConnector connector = new SQLConnector();

    public Account getAccount(String username) {
        String SQL = "SELECT id, username, email FROM accounts WHERE username = ?";

        Account a = null;

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setString(1, username);
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    a = new Account(
                            results.getInt("id"),
                            username,
                            results.getString("email"));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return a;
    }

    @Override
    public Account getAccountFromStorage(int id) {
        String SQL = "SELECT a.id, a.username, a.email FROM accounts a JOIN storages s ON a.id = s.account_id WHERE s.id = ?";

        Account a = null;

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, id);
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    a = new Account(
                            results.getInt("id"),
                            results.getString("username"),
                            results.getString("email"));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return a;
    }

    public int getStorageId(int owner_id) {
        String SQL = "SELECT id FROM storages WHERE account_id = ?";

        int id = -1;

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, owner_id);
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    id = results.getInt("id");
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return id;
    }

    public boolean login(String username, String password) {
        String SQL = "SELECT password_hash FROM accounts WHERE username = ?";

        String hashedPassword = hashPassword(password);
        String correctPassword = "";

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setString(1, username);
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    correctPassword = results.getString("password_hash");
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return hashedPassword != null && hashedPassword.equals(correctPassword);
    }

    public boolean register(String username, String password, String email) {
        String SQL = "INSERT INTO accounts (username, password_hash, email) VALUES (?, ?, ?)";

        String hashedPassword = hashPassword(password);
        int affectedRows = 0;

        connector.openConnection();

        //First add the account
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            pStmt.setString(1, username);
            pStmt.setString(2, hashedPassword);
            pStmt.setString(3, email);

            affectedRows = pStmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to register");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
            return false;
        } finally {
            connector.closeConnection();
        }

        //Then, add the storage
        int affectedRowsStorage = 0;
        int account_id = -1;
        if (affectedRows > 0) {
            String SQL2 = "INSERT INTO storages (account_id) VALUES (?)";

            account_id = getAccount(username).getId();

            connector.openConnection();
            try (PreparedStatement pStmt = connector.con.prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS)) {
                pStmt.setInt(1, account_id);

                affectedRowsStorage = pStmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.severe("SQLContext: SQLException when trying to register storage");
                LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
            } finally {
                connector.closeConnection();
            }
        }

        //Then add the root folder
        if (affectedRows > 0 && affectedRowsStorage > 0) {
            String SQL2 = "INSERT INTO folders (\"name\", parent_id, owner_id) VALUES (?, ?, ?)";

            connector.openConnection();
            try (PreparedStatement pStmt = connector.con.prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS)) {
                pStmt.setString(1, "root");
                pStmt.setNull(2, Types.INTEGER);
                pStmt.setInt(3, account_id);

                pStmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.severe("SQLContext: SQLException when trying to register storage");
                LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
            } finally {
                connector.closeConnection();
            }
        }

        int parent_id = getFolderId("root", account_id);

        //Then add the files folder
        if (affectedRows > 0 && affectedRowsStorage > 0) {
            String SQL2 = "INSERT INTO folders (\"name\", parent_id, owner_id) VALUES (?, ?, ?)";


            connector.openConnection();
            try (PreparedStatement pStmt = connector.con.prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS)) {
                pStmt.setString(1, "Your Storage");
                pStmt.setInt(2, parent_id);
                pStmt.setInt(3, account_id);

                pStmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.severe("SQLContext: SQLException when trying to register storage");
                LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
            } finally {
                connector.closeConnection();
            }
        }

        //Then add the shared folder
        if (affectedRows > 0 && affectedRowsStorage > 0) {
            String SQL2 = "INSERT INTO folders (\"name\", parent_id, owner_id) VALUES (?, ?, ?)";

            connector.openConnection();
            try (PreparedStatement pStmt = connector.con.prepareStatement(SQL2, Statement.RETURN_GENERATED_KEYS)) {
                pStmt.setString(1, "Shared with You");
                pStmt.setInt(2, parent_id);
                pStmt.setInt(3, account_id);

                pStmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.severe("SQLContext: SQLException when trying to register storage");
                LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
            } finally {
                connector.closeConnection();
            }
        }

        return affectedRows > 0;
    }

    public int getFolderId(String name, int owner_id) {
        String SQL = "SELECT id FROM folders WHERE owner_id = ? AND \"name\" = ?";

        int id = -1;

        connector.openConnection();
        try (PreparedStatement pStmt = connector.con.prepareStatement(SQL)) {
            pStmt.setInt(1, owner_id);
            pStmt.setString(2, name);

            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    id = results.getInt("id");
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            connector.closeConnection();
        }

        return id;
    }

    private String hashPassword(String password) {
        String hash;

        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("SQLContext: NoSuchAlgorithmException when trying to hash password");
            LOGGER.severe("SQLContext: NoSuchAlgorithmException: " + e.getMessage());
        }

        if (md != null) {
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            //Get complete hashed password in hex format
            hash = sb.toString();

            return hash;
        }

        return null;
    }
}