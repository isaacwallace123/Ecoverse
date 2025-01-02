package isaacwallace123.ecoverse.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class CommandBase extends BukkitCommand implements CommandExecutor {
    private List<String> delayedPlayers = null;
    private int delay = 0;

    private final int minArguments;
    private final int maxArguments;

    private final String description;
    private final String permission;

    private final boolean playerOnly;

    public CommandBase(String command, String description) {
        this(command, 0, description);
    }

    public CommandBase(String command, boolean playerOnly, String description) {
        this(command, 0, playerOnly, description);
    }

    public CommandBase(String command, int requiredArguments, String description) {
        this(command, requiredArguments, requiredArguments, description);
    }

    public CommandBase(String command, int minArguments, int maxArguments, String description) {
        this(command, minArguments, maxArguments, false, description);
    }

    public CommandBase(String command, int requiredArguments, boolean playerOnly, String description) {
        this(command, requiredArguments, requiredArguments, playerOnly, description);
    }

    public CommandBase(String command, int minArguments, int maxArguments, boolean playerOnly, String description) {
        super(command);

        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.playerOnly = playerOnly;
        this.description = description;

        this.setDescription(this.description);
        this.permission = this.generatePermissionString(command);

        CommandMap commandMap = getCommandMap();

        if (commandMap != null) {
            this.setLabel("Ecoverse:" + command);
            this.setAliases(List.of());

            commandMap.register("Ecoverse", this);
        }
    }

    public CommandMap getCommandMap() {
        PluginManager plugin = Bukkit.getPluginManager();

        try {
            Field field = plugin.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);

            return (CommandMap) field.get(Bukkit.getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public CommandBase enableDelay(int delay) {
        this.delay = delay;
        this.delayedPlayers = new ArrayList<>();

        return this;
    }

    public void removeDelay(Player player) {
        this.delayedPlayers.remove(player.getName());
    }

    public void sendUsage(CommandSender sender) {
        Message.info(sender, this.getUsage());
    }

    private String generatePermissionString(String command) {
        return "ecoverse." + command.toLowerCase();
    }

    public boolean execute(CommandSender sender, String alias, String[] arguments) {
        if (arguments.length < minArguments || (arguments.length > maxArguments && maxArguments != -1)) {
            sendUsage(sender);
            return true;
        }

        if (playerOnly && !(sender instanceof Player)) {
            Message.error(sender, "Only players can use this command.");
            return true;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            Message.error(sender, "You do not have permission to use this command.");
            return true;
        }

        boolean commandExecuted = false;

        if (sender instanceof Player player) {
            String cooldownPermission = "ecoverse." + alias + ".cooldown";

            if (player.isOp() || player.hasPermission("ecoverse.admin") || player.hasPermission(cooldownPermission)) {
                commandExecuted = onCommand(sender, arguments);
            } else if (delayedPlayers != null) {
                if (delayedPlayers.contains(player.getName())) {
                    Message.error(sender, "You cannot use this command for " + delay + " seconds.");
                    return true;
                }

                delayedPlayers.add(player.getName());

                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Ecoverse"), () -> delayedPlayers.remove(player.getName()), delay * 20L);
            }
        }

        if (!commandExecuted) {
            commandExecuted = onCommand(sender, arguments);
        }

        if (!commandExecuted) {
            sendUsage(sender);
        }

        return true;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] arguments) {
        return this.onCommand(sender, arguments);
    }

    public abstract boolean onCommand(CommandSender sender, String[] arguments);
    public abstract String getUsage();
}
