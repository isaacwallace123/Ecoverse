package isaacwallace123.ecoverse.Expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class EcoverseExpansion extends PlaceholderExpansion {
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
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("economy_balance")) {
            return "100";
        }

        return null;
    }
}
