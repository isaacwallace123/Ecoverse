package isaacwallace123.ecoverse;

import isaacwallace123.ecoverse.Expansions.EcoverseExpansion;
import isaacwallace123.ecoverse.Utils.Scoreboard;
import isaacwallace123.ecoverse.Command.Heal;
import isaacwallace123.ecoverse.Databases.Database;
import isaacwallace123.ecoverse.Databases.Maria;
import isaacwallace123.ecoverse.Databases.Mongo;
import isaacwallace123.ecoverse.Databases.MySQL;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ecoverse extends JavaPlugin {
    private Database database;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        config = getConfig();
    
        String databaseType = config.getString("database.type", "mysql").toLowerCase();

        switch (databaseType) {
            case "mysql" -> database = new MySQL(this);
            case "mariadb" -> database = new Maria(this);
            case "mongodb" -> database = new Mongo(this);
            default -> getLogger().warning("Unsupported database type: " + databaseType);
        }

        database.connect();

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
