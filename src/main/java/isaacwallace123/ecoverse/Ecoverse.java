package isaacwallace123.ecoverse;

import isaacwallace123.ecoverse.commands.Heal;
import isaacwallace123.ecoverse.database.Database;
import isaacwallace123.ecoverse.database.Maria;
import isaacwallace123.ecoverse.database.Mongo;
import isaacwallace123.ecoverse.database.MySQL;

import org.bukkit.plugin.java.JavaPlugin;

public final class Ecoverse extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
    
        String databaseType = getConfig().getString("database.type", "mysql").toLowerCase();

        switch (databaseType) {
            case "mysql" -> database = new MySQL(this);
            case "mariadb" -> database = new Maria(this);
            case "mongodb" -> database = new Mongo(this);
            default -> getLogger().warning("Unsupported database type: " + databaseType);
        }

        database.connect();

        new Heal();
    }

    @Override
    public void onDisable() {
        database.disconnect();
    }
}
