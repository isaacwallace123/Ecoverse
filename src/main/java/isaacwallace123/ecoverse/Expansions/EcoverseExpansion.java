package isaacwallace123.ecoverse.Expansions;

import isaacwallace123.ecoverse.DataAccess.User;
import isaacwallace123.ecoverse.Ecoverse;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;

public class EcoverseExpansion extends PlaceholderExpansion {
    private static Ecoverse plugin;

    @Override
    public String getIdentifier() {
        return "ecoverse";
    }

    @Override
    public String getAuthor() {
        return "isaacwallace123";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        try {
            this.plugin = (Ecoverse) Bukkit.getPluginManager().getPlugin("Ecoverse");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Bukkit.getPluginManager().getPlugin("Ecoverse") != null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return null;
        if (plugin.userCache == null) return null;

        User user = plugin.userCache.get(player.getUniqueId());

        if (user == null) return null;

        Double balance = user.getBalance();

        if (params.equalsIgnoreCase("balance")) return Double.toString(balance);

        if (params.equalsIgnoreCase("balance_formatted"))
            return (balance < 1000) ? String.format("%.2f", balance)
                    : (balance < 1000000) ? String.format("%,.2f", balance)
                    : String.format("%.1f%s", balance / Math.pow(1000, (int) (Math.log10(balance) / 3)), new String[]{"", "K", "M", "B", "T", "Qa", "Qi", "Sx"}[(int) (Math.log10(balance) / 3)]);

        long playtime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;

        if (params.equalsIgnoreCase("playtime_formatted")) return formatPlaytime(playtime, "formatted");

        if (params.equalsIgnoreCase("playtime_formatted_characters")) return formatPlaytime(playtime, "formatted_characters");

        return null;
    }

    private String formatPlaytime(long seconds, String formatType) {
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String secondUnit = (formatType.equals("formatted_characters")) ? "s" : "second%s";
        String minuteUnit = (formatType.equals("formatted_characters")) ? "m" : "minute%s";
        String hourUnit = (formatType.equals("formatted_characters")) ? "h" : "hour%s";
        String dayUnit = (formatType.equals("formatted_characters")) ? "d" : "day%s";

        if (seconds < 60) {
            return String.format("%d%s", seconds, (seconds != 1 || formatType.equals("formatted_characters")) ? secondUnit : "");
        }

        if (minutes < 60) {
            return String.format("%d%s %d%s", minutes, (minutes != 1 || formatType.equals("formatted_characters")) ? minuteUnit : "", seconds % 60, (seconds % 60 != 1 || formatType.equals("formatted_characters")) ? secondUnit : "");
        }

        if (hours < 24) {
            return String.format("%d%s %d%s", hours, (hours != 1 || formatType.equals("formatted_characters")) ? hourUnit : "", minutes % 60, (minutes % 60 != 1 || formatType.equals("formatted_characters")) ? minuteUnit : "");
        }

        return String.format("%d%s %d%s", days % 365, (days % 365 != 1 || formatType.equals("formatted_characters")) ? dayUnit : "", hours % 24, (hours % 24 != 1 || formatType.equals("formatted_characters")) ? hourUnit : "");
    }
}
