package com.ms1011.minetutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuideCommand implements CommandExecutor {

    private final MineTutor plugin;
    private final OpenAIHandler openAIHandler;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public GuideCommand(MineTutor plugin, OpenAIHandler openAIHandler) {
        this.plugin = plugin;
        this.openAIHandler = openAIHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("minetutor.admin")) {
                String noPermMessage = plugin.getConfig().getString("messages.no-permission", "§cYou do not have permission to use this command.");
                sender.sendMessage(noPermMessage);
                return true;
            }
            plugin.reload();
            sender.sendMessage("§aMineTutor configuration reloaded.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        long cooldownSeconds = plugin.getConfig().getLong("command-cooldown", 30);

        if (cooldowns.containsKey(playerId)) {
            long secondsLeft = ((cooldowns.get(playerId) / 1000) + cooldownSeconds) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                String cooldownMessage = plugin.getConfig().getString("messages.cooldown", "§cYou must wait {time} more seconds before using this command again.");
                player.sendMessage(cooldownMessage.replace("{time}", String.valueOf(secondsLeft)));
                return true;
            }
        }

        if (args.length == 0) {
            String usageMessage = plugin.getConfig().getString("messages.usage", "§cUsage: /guide <question>");
            sender.sendMessage(usageMessage);
            return false;
        }

        String question = String.join(" ", args);

        cooldowns.put(playerId, System.currentTimeMillis());
        openAIHandler.askQuestion(player, question);

        return true;
    }
}
