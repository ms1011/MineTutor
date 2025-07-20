package com.ms1011.minetutor;

import org.bukkit.entity.Player;

public interface AIProvider {
    void askQuestion(Player player, String question);
}
