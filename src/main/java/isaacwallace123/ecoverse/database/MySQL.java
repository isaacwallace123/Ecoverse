package isaacwallace123.ecoverse.database;

import isaacwallace123.ecoverse.Ecoverse;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Connection;

public class MySQL implements Database {
    private Connection connection;
    
    private Ecoverse plugin;

    private String host;
    private String port;
    private String uri;

    private String username;
    private String password;

    private String collection;

    public MySQL(Ecoverse plugin) {
        this.plugin = plugin;
        
        this.host = plugin.getConfig().getString("database.host");
        this.port = plugin.getConfig().getString("database.port");
        this.uri = plugin.getConfig().getString("database.uri");

        this.username = plugin.getConfig().getString("database.username");
        this.password = plugin.getConfig().getString("database.password");

        this.collection = plugin.getConfig().getString("database.collection");
    }

    @Override
    public void connect() {
        try {
            connection = DriverManager.getConnection(uri, username, password);
            plugin.getLogger().info("Connected to the MySQL database successfully using the URI.");
        } catch (SQLException uriException) {
            plugin.getLogger().warning("Failed to connect using the URI. Attempting with host and port.");
            try {
                connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port, username, password
                );

                plugin.getLogger().info("Connected to the MySQL server successfully using host and port.");
            } catch (SQLException fallbackException) {
                plugin.getLogger().severe("Failed to connect to the MySQL server: " + fallbackException.getMessage());
                return;
            }
        }

        try (Statement statement = connection.createStatement()) {
            plugin.getLogger().info("Checking and creating the database if it doesn't exist...");

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + this.collection);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to verify or create the database: " + e.getMessage());
            return;
        }
        
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + this.collection, username, password
            );
            plugin.getLogger().info("Connected to the MySQL database successfully with the database.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to reconnect to the MySQL database: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
                connection = null;
                
                plugin.getLogger().info("Disconnected from the MySQL database successfully.");
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to disconnect from the MySQL database: " + e.getMessage());
            }
        } else {
            plugin.getLogger().warning("Attempted to disconnect, but the connection was already closed or not present.");
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while checking connection status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public double getBalance(String player) {
        return 0;
    }

    @Override
    public void setBalance(String player, double balance) {

    }
}
