package isaacwallace123.ecoverse.Commands;

import isaacwallace123.ecoverse.Utils.CommandBase;
import isaacwallace123.ecoverse.Utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Heal {
    public Heal() {
        new CommandBase("heal", 0, 1, true, "Heals the player.") {
            @Override
            public boolean onCommand(CommandSender sender, String[] arguments) {
                Player player = arguments.length == 1 ? Bukkit.getPlayer(arguments[0]) : (Player) sender;

                if (player == null) {
                    Message.error(sender, "Player not found.");
                    return true;
                }

                player.setHealth(20);
                player.setFoodLevel(20);
                player.setFireTicks(0);
                player.setSaturation(20);
                player.setExhaustion(0);

                Message.success(player, "You have been healed.");

                if (sender != player) {
                    Message.success(sender, "Successfully healed " + player.getName() + ".");
                }

                return true;
            }

            @Override
            public String getUsage() {
                return "/heal";
            }
        }.enableDelay(2);
    }
}
