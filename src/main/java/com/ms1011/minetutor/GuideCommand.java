package com.ms1011.minetutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GuideCommand implements CommandExecutor {

    private final OpenAIHandler openAIHandler;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownSeconds = 30; // TODO: Make this configurable

    public GuideCommand(OpenAIHandler openAIHandler) {
        this.openAIHandler = openAIHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (cooldowns.containsKey(playerId)) {
            long secondsLeft = ((cooldowns.get(playerId) / 1000) + cooldownSeconds) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                player.sendMessage("§cYou must wait " + secondsLeft + " more seconds before using this command again.");
                return true;
            }
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /guide <question>");
            return false;
        }

        String question = String.join(" ", args);

        cooldowns.put(playerId, System.currentTimeMillis());
        openAIHandler.askQuestion(player, question);

        return true;
    }
}
