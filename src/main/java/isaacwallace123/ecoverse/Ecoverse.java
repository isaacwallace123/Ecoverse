package isaacwallace123.ecoverse;

import isaacwallace123.ecoverse.DataAccess.User;
import isaacwallace123.ecoverse.Expansions.EcoverseExpansion;
import isaacwallace123.ecoverse.Utils.Scoreboard;
import isaacwallace123.ecoverse.Command.Heal;
import isaacwallace123.ecoverse.DataAccess.Database;
import isaacwallace123.ecoverse.DataAccess.MongoController;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Ecoverse extends JavaPlugin {
    public Database database;
    private FileConfiguration config;

    public Map<UUID, User> userCache = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        config = getConfig();
    
        String databaseType = config.getString("database.type", "mysql").toLowerCase();

        switch (databaseType) {
            case "mysql" -> database = null; //new MySQLController(this);
            case "mariadb" -> database = null; //new MariaController(this);
            case "mongodb" -> database = new MongoController(this);
            default -> getLogger().warning("Unsupported database type: " + databaseType);
        }

        database.connect();

        getServer().getPluginManager().registerEvents((Listener) database, this);

        new EcoverseExpansion().register();

        new Heal();

        if (config.getString("scoreboard.enabled", "false").equalsIgnoreCase("true")) {
            new Scoreboard(this);
        }
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }
}
