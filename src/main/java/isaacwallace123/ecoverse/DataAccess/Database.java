package isaacwallace123.ecoverse.DataAccess;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.UUID;

public interface Database {
    void connect();
    void disconnect();
    boolean isConnected();
    void save(UUID playerUUID);
    void onPlayerJoin(PlayerJoinEvent event);
    void onPlayerQuit(PlayerQuitEvent event);
    void onPluginDisable(PluginDisableEvent event);
}
