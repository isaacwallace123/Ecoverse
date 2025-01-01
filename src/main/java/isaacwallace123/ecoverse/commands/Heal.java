package isaacwallace123.ecoverse.commands;

import isaacwallace123.ecoverse.Utils.CommandBase;
import isaacwallace123.ecoverse.Utils.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Heal {
    public Heal() {
        new CommandBase("heal", true) {
            @Override
            public boolean onCommand(CommandSender sender, String[] arguments) {
                Player player = (Player) sender;

                player.setHealth(20);
                player.setFoodLevel(20);

                Message.success(sender, "You have been healed.");

                return true;
            }

            @Override
            public String getUsage() {
                return "/heal";
            }
        }.enableDelay(2);
    }
}
