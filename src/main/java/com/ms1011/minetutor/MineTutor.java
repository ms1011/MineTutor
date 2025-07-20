package com.ms1011.minetutor;

import org.bukkit.plugin.java.JavaPlugin;

public final class MineTutor extends JavaPlugin {

    private OpenAIHandler openAIHandler;

    @Override
    public void onEnable() {
        this.openAIHandler = new OpenAIHandler();
        // Register command executor
        this.getCommand("guide").setExecutor(new GuideCommand(this.openAIHandler));
        getLogger().info("MineTutor has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MineTutor has been disabled!");
    }
}
