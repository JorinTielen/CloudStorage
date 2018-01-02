package cloudstorage.database;

import cloudstorage.cloud.repository.CSRepositorySQLContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class SQLConnector {
    private static final Logger LOGGER = Logger.getLogger(CSRepositorySQLContext.class.getName());

    public Connection con;

    public void openConnection() {
        Properties prop = new Properties();
        InputStream input;

        try {
            input = new FileInputStream("database.properties");
            prop.load(input);

            String url = prop.getProperty("url");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");

            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(url, username, password);
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

    public void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLContext: SQLException when trying to close connection");
            LOGGER.severe("SQLContext: SQLException: " + e.getMessage());
        } finally {
            con = null;
        }
    }
}
