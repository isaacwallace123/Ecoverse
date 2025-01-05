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
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scoreboard implements Listener {
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final FileConfiguration config;
    private final boolean hasPlaceholders = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

    private final String[] lines;

    public Scoreboard(Ecoverse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        config = plugin.getConfig();

        lines = config.getStringList("scoreboard.lines").toArray(new String[0]);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (FastBoard board : boards.values()) {
                updateBoard(board);
            }
        }, 0L, 10L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FastBoard board = new FastBoard(player);

        String title = config.getString("scoreboard.title", "&c&lEcoverse");;
        board.updateTitle(formatLine(title, player));

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
        Player player = board.getPlayer();

        String[] linesCopy = lines.clone();

        for (int i = 0; i < linesCopy.length; i++) {
            linesCopy[i] = formatLine(linesCopy[i], player);
        }

        board.updateLines(linesCopy);
    }

    public static String convertHexToMinecraftColor(String hexColor) {
        if (!hexColor.startsWith("#") || hexColor.length() != 7) {
            return hexColor;
        }

        StringBuilder minecraftColor = new StringBuilder("§x");

        for (int i = 1; i < hexColor.length(); i++) {
            minecraftColor.append("§").append(hexColor.charAt(i));
        }
        return minecraftColor.toString();
    }

    private String formatLine(String line, Player player) {
        if (hasPlaceholders) line = PlaceholderAPI.setPlaceholders(player, line);

        line = ChatColor.translateAlternateColorCodes('&', line);

        if (GradientUtils.isGradientString(line)) {
            line = GradientUtils.applyGradientFromConfig(line);
        } else {
            line = convertHexToMinecraftColor(line);
        }

        return line;
    }
}
