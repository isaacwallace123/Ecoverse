package isaacwallace123.ecoverse.Utils;

import fr.mrmicky.fastboard.FastBoard;
import isaacwallace123.ecoverse.Ecoverse;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scoreboard implements Listener {
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final FileConfiguration config;
    private final boolean hasPlaceholders = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

    public Scoreboard(Ecoverse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        config = plugin.getConfig();

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (FastBoard board : boards.values()) {
                updateBoard(board);
            }
        }, 0L, 10L);
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FastBoard board = new FastBoard(player);

        String title = config.getString("scoreboard.title", "&c&lEcoverse");;
        board.updateTitle(ChatColor.translateAlternateColorCodes('&', title));

        boards.put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FastBoard board = boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    private void updateBoard(FastBoard board) {
        String[] lines = config.getStringList("scoreboard.lines").toArray(new String[0]);

        Player player = board.getPlayer();

        for (int i = 0; i < lines.length; i++) {
            if (hasPlaceholders) {
                lines[i] = PlaceholderAPI.setPlaceholders(player, lines[i]);
            }

            lines[i] = ChatColor.translateAlternateColorCodes('&', lines[i]);
        }

        board.updateLines(lines);
    }
}
