package isaacwallace123.ecoverse.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import isaacwallace123.ecoverse.Ecoverse;

public class Maria implements Database {
    private Ecoverse plugin;
    private Connection connection;

    private String host;
    private int port;
    private String uri;

    private String username;
    private String password;

    private String collection;
    
    public Maria(Ecoverse plugin) {
        this.plugin = plugin;

        this.host = plugin.getConfig().getString("database.host");
        this.port = plugin.getConfig().getInt("database.port");
        this.uri = plugin.getConfig().getString("database.uri");

        this.username = plugin.getConfig().getString("database.username");
        this.password = plugin.getConfig().getString("database.password");

        this.collection = plugin.getConfig().getString("database.collection");
    }
    
    @Override
    public void connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            plugin.getLogger().info("MariaDB driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("MariaDB driver not found! Please ensure it is included in the classpath.");
            return;
        }

        try {
            connection = DriverManager.getConnection(uri, username, password);
            plugin.getLogger().info("Connected to the MariaDB database successfully using the URI.");
        } catch (SQLException uriException) {
            plugin.getLogger().warning("Failed to connect using the URI. Attempting with host and port.");
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mariadb://" + host + ":" + port, username, password
                );

                plugin.getLogger().info("Connected to the MariaDB server successfully using host and port.");
            } catch (SQLException fallbackException) {
                plugin.getLogger().severe("Failed to connect to the MariaDB server: " + fallbackException.getMessage());
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
                    "jdbc:mariadb://" + host + ":" + port + "/" + this.collection, username, password
            );
            plugin.getLogger().info("Connected to the MariaDB database successfully with the database.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to reconnect to the MariaDB database: " + e.getMessage());
        }
    }
    
    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
                connection = null;

                System.out.println("Disconnected from MariaDB successfully.");
            } catch (SQLException e) {
                System.out.println("Failed to disconnect from MariaDB.");
            }
        } else {
            System.out.println("No active database connection to disconnect.");
        }
    }
    
    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
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