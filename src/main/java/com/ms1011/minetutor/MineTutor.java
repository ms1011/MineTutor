package com.ms1011.minetutor;

import org.bukkit.plugin.java.JavaPlugin;

public final class MineTutor extends JavaPlugin {

    private OpenAIHandler openAIHandler;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        this.openAIHandler = new OpenAIHandler(this);
        // Register command executor
        this.getCommand("guide").setExecutor(new GuideCommand(this, this.openAIHandler));
        getLogger().info("MineTutor has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MineTutor has been disabled!");
    }

    public void reload() {
        reloadConfig();
        getLogger().info("Configuration reloaded.");
    }
}
