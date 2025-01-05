package isaacwallace123.ecoverse.DataAccess;

import com.mongodb.*;
import isaacwallace123.ecoverse.Ecoverse;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class MongoController implements Database, Listener {
    private Ecoverse plugin;

    private FileConfiguration config;

    private final String address;

    private final String username;
    private final String password;

    private final String name;

    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> userCollection;

    public MongoController(Ecoverse plugin) {
        this.plugin = plugin;

        this.config = plugin.getConfig();

        this.address = config.getString("database.address");

        this.username = config.getString("database.username");
        this.password = config.getString("database.password");

        this.name = config.getString("database.collection");
    }

    public void connect() {
        try {
            String uri = config.getString("database.URI");

            MongoClientSettings settings;

            if (uri != null && !uri.isEmpty()) {
                settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(uri))
                        .applyToConnectionPoolSettings(builder -> builder.maxSize(config.getInt("pool.size", 10)))
                        .applyToSocketSettings(builder -> builder.connectTimeout(10000, TimeUnit.MILLISECONDS))
                        .applyToSslSettings(builder -> builder.enabled(config.getBoolean("ssl", false)))
                        .build();
            } else {
                String[] connectionParts = address.split(":");

                MongoCredential mongoCredential = MongoCredential.createCredential(username, config.getString("credential", "admin"), password.toCharArray());
                ServerAddress serverAddress = new ServerAddress(connectionParts[0], Integer.parseInt(connectionParts[1]));

                settings = MongoClientSettings.builder()
                        .credential(mongoCredential)
                        .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(serverAddress)))
                        .applyToConnectionPoolSettings(builder -> builder.maxSize(config.getInt("pool.size", 10)))
                        .applyToSocketSettings(builder -> builder.connectTimeout(10000, TimeUnit.MILLISECONDS))
                        .applyToSslSettings(builder -> builder.enabled(config.getBoolean("ssl", false)))
                        .build();
            }

            client = MongoClients.create(settings);

            database = client.getDatabase(name);

            userCollection = database.getCollection("users");
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to connect to MongoDB. Be sure to check your configuration file for inconsistencies.");
        }
    }

    public void disconnect() {
        if (client != null) {
            client.close();
            client = null;

            plugin.getLogger().info("Disconnected from MongoDB successfully.");
        }
    }

    public boolean isConnected() {
        return client != null;
    }

    public void save(UUID playerUUID) {
        try {
            if (plugin.userCache.containsKey(playerUUID)) {
                User user = plugin.userCache.get(playerUUID);

                Document updateDoc = user.toDocument();

                userCollection.updateOne(
                        Filters.eq("UUID", playerUUID.toString()),
                        new Document("$set", updateDoc)
                );

                plugin.userCache.remove(playerUUID);
            }
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to save user data: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();

            if (!plugin.userCache.containsKey(playerUUID)) {
                Document document = userCollection.find(Filters.eq("UUID", playerUUID.toString())).first();

                if (document == null) {
                    User user = new User(playerUUID.toString(), player.getName(), config.getDouble("economy.default.balance", 100.0));

                    userCollection.insertOne(user.toDocument());

                    plugin.userCache.put(playerUUID, user);
                } else {
                    User user = User.fromDocument(document);
                    plugin.userCache.put(playerUUID, user);
                }
            }
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to load user data: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();

            this.save(playerUUID);
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to save user data: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        try {
            if (event.getPlugin() == plugin) {
                plugin.getLogger().info("Saving all cached user data.");

                for (Map.Entry<UUID, User> entry : plugin.userCache.entrySet()) {
                    this.save(entry.getKey());
                }

                plugin.userCache.clear();
            }
        } catch (MongoException e) {
            plugin.getLogger().severe("Failed to save user data: " + e.getMessage());
        }
    }
}
