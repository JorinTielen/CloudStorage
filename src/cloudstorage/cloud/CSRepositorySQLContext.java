package cloudstorage.cloud;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class CSRepositorySQLContext implements ICSRepositoryContext {
    private static final Logger LOGGER = Logger.getLogger(CSRepositorySQLContext.class.getName());

    private Connection conn;

    private void openConnection() {
        Properties prop = new Properties();
        InputStream input;

        try {
            input = new FileInputStream("database.properties");
            prop.load(input);

            String url = prop.getProperty("url");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");

            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, username, password);
            LOGGER.info("Connected to database");
        } catch (ClassNotFoundException e) {
            LOGGER.severe("SQLContext: ClassNotFoundException when trying to connect");
            LOGGER.severe("SQLContext: ClassNotFoundException: " + e.getMessage());
        } catch (FileNotFoundException e) {
            LOGGER.severe("SQLContext: FileNotFoundException when trying to connect");
            LOGGER.severe("SQLContext: FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.severe("SQLContext: IOException when trying to connect");
            LOGGER.severe("SQLContext: IOException: " + e.getMessage());
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to connect");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            LOGGER.info(ex.getMessage());
        } finally {
            conn = null;
        }
    }

    public boolean login(String username, String password) {
        String SQL = "SELECT password_hash FROM accounts WHERE username = ?";

        String hashedPassword = hashPassword(password);
        String correctPassword = "";

        openConnection();
        try (PreparedStatement pStmt = conn.prepareStatement(SQL)) {
            pStmt.setString(1, username);
            try (ResultSet results = pStmt.executeQuery()) {
                while (results.next()) {
                    correctPassword = results.getString("password_hash");
                }
            }
        } catch (SQLException ex) {
            LOGGER.info(ex.getMessage());
        } finally {
            closeConnection();
        }

        return hashedPassword != null && hashedPassword.equals(correctPassword);
    }

    public boolean register(String username, String password, String email) {
        String SQL = "INSERT INTO accounts (username, password_hash, email) VALUES (?, ?, ?)";

        String hashedPassword = hashPassword(password);
        int affectedRows = 0;

        openConnection();
        try (PreparedStatement pStmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            pStmt.setString(1, username);
            pStmt.setString(2, hashedPassword);
            pStmt.setString(3, email);

            affectedRows = pStmt.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.info(ex.getMessage());
        } finally {
            closeConnection();
        }

        return affectedRows > 0;
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