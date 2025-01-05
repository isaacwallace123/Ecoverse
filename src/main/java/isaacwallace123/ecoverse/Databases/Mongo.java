package isaacwallace123.ecoverse.Databases;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.MongoException;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import isaacwallace123.ecoverse.Ecoverse;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mongo implements Database {
    private Ecoverse plugin;
    
    private final String host;
    private final int port;

    private final String username;
    private final String password;

    private final String name;
    
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection collection;
    
    public Mongo(Ecoverse plugin) {
        this.plugin = plugin;
    
        this.host = plugin.getConfig().getString("database.host");
        this.port = plugin.getConfig().getInt("database.port");

        this.username = plugin.getConfig().getString("database.username");
        this.password = plugin.getConfig().getString("database.password");

        this.name = plugin.getConfig().getString("database.collection");

        System.setProperty("DEBUG.GO", "true");
        System.setProperty("DB.TRACE", "true");

        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
    }
    
    @Override
    public void connect() {
        try {
            MongoCredential mongoCredential = MongoCredential.createCredential(username, name, password.toCharArray());
            ServerAddress serverAddress = new ServerAddress(host, port);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(builder ->
                            builder.hosts(Collections.singletonList(serverAddress)))
                    .credential(mongoCredential)
                    .build();

            client = MongoClients.create(settings);
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to connect to MongoDB. " + e.getMessage());
        }

        try {
            database = client.getDatabase(name);
            collection = database.getCollection("users");
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to initialize MongoDB: " + e.getMessage());
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
