package isaacwallace123.ecoverse.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoException;

import isaacwallace123.ecoverse.Ecoverse;

public class Mongo implements Database {
    private Ecoverse plugin;
    
    private final String host;
    private final int port;
    private final String uri;

    private final String username;
    private final String password;

    private final String collection;
    
    private MongoClient client;
    
    public Mongo(Ecoverse plugin) {
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
            if (uri != null && !uri.isBlank()) {
                plugin.getLogger().info("Attempting to connect to MongoDB using URI.");

                client = MongoClients.create(uri);

                plugin.getLogger().info("Connected to MongoDB successfully.");
            }
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to connect to MongoDB using URI: " + e.getMessage());
        }

        if (client == null) {
            try {
                plugin.getLogger().info("Attempting to connect to MongoDB using connection details.");

                String connectionString = String.format("mongodb://%s:%s@%s:%d/%s", username, password, host, port, collection);

                client = MongoClients.create(connectionString);

                plugin.getLogger().info("Connected to MongoDB successfully.");
            } catch (MongoException e) {
                plugin.getLogger().severe("Failed to connect to MongoDB using connection details: " + e.getMessage());
            }
        }
    }

    @Override
    public void disconnect() {
        if (client != null) {
            client.close();
            client = null;

            plugin.getLogger().info("Disconnected from MongoDB successfully.");
        }
    }
    
    @Override
    public boolean isConnected() {
        return client != null;
    }
    
    @Override
    public double getBalance(String player) {
        return 0;
    }
    
    @Override
    public void setBalance(String player, double balance) {
        
    }
}
