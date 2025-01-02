package isaacwallace123.ecoverse.Commands;

import isaacwallace123.ecoverse.Utils.CommandBase;
import isaacwallace123.ecoverse.Utils.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly {
    public Fly() {
        new CommandBase("fly", true, "Allows the player to fly.") {
            @Override
            public boolean onCommand(CommandSender sender, String[] arguments) {
                Player player = (Player) sender;

                Message.success(player, "You have been healed.");

                return true;
            }

            @Override
            public String getUsage() {
                return "/heal";
            }
        }.enableDelay(2);
    }
}
