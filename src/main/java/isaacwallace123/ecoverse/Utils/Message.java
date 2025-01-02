package isaacwallace123.ecoverse.Utils;

import org.bukkit.command.CommandSender;

public class Message {
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public static void info(CommandSender sender, String message) {
        sender.sendMessage("§9[Info] " + message);
    }

    public static void warning(CommandSender sender, String message) {
        sender.sendMessage("§6[Warning] " + message);
    }

    public static void error(CommandSender sender, String message) {
        sender.sendMessage("§c[Error] " + message);
    }

    public static void success(CommandSender sender, String message) {
        sender.sendMessage("§a[Success] " + message);
    }
}
