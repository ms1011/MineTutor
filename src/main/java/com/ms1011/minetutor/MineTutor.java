package com.ms1011.minetutor;

import org.bukkit.plugin.java.JavaPlugin;

public final class MineTutor extends JavaPlugin {

    private AIProvider aiProvider;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        String providerType = getConfig().getString("ai-provider", "openai");
        if (providerType.equalsIgnoreCase("openai")) {
            this.aiProvider = new OpenAIProvider(this);
        }

        // Register command executor
        this.getCommand("guide").setExecutor(new GuideCommand(this, this.aiProvider));
        getLogger().info("MineTutor has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MineTutor has been disabled!");
    }

    public void reload() {
        reloadConfig();
        String providerType = getConfig().getString("ai-provider", "openai");
        if (providerType.equalsIgnoreCase("openai")) {
            this.aiProvider = new OpenAIProvider(this);
        }
        this.getCommand("guide").setExecutor(new GuideCommand(this, this.aiProvider));
        getLogger().info("Configuration reloaded.");
    }
}
