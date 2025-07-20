package com.ms1011.minetutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuideCommand implements CommandExecutor {

    private final OpenAIHandler openAIHandler;

    public GuideCommand(OpenAIHandler openAIHandler) {
        this.openAIHandler = openAIHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /guide <question>");
            return false;
        }

        Player player = (Player) sender;
        String question = String.join(" ", args);

        openAIHandler.askQuestion(player, question);

        return true;
    }
}
