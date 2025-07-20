package com.ms1011.minetutor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClaudeProvider implements AIProvider {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private final MineTutor plugin;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public ClaudeProvider(MineTutor plugin) {
        this.plugin = plugin;
    }

    @Override
    public void askQuestion(Player player, String question) {
        String thinkingMessage = plugin.getConfig().getString("messages.thinking", "MineTutor is thinking...");
        player.sendMessage(thinkingMessage);

        String apiKey = plugin.getConfig().getString("api-key");
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY")) {
            player.sendMessage("§cError: Claude API key is not set in the config.yml file.");
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("model", plugin.getConfig().getString("claude-model", "claude-3-haiku-20240307"));
        payload.addProperty("max_tokens", 1024);

        // System Prompt
        String systemPrompt = plugin.getConfig().getString("system-prompt");
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            payload.addProperty("system", systemPrompt);
        }

        JsonArray messages = new JsonArray();
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", question);
        messages.add(userMessage);

        payload.add("messages", messages);

        RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    String errorMessage = plugin.getConfig().getString("messages.error", "§cError: Failed to get response from MineTutor.");
                    player.sendMessage(errorMessage);
                    e.printStackTrace();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        try {
                            player.sendMessage("§cError: " + response.code() + " " + response.body().string());
                        } catch (IOException e) {
                            plugin.getLogger().severe("Failed to read error response body.");
                        }
                    });
                    return;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) return;
                    String responseString = responseBody.string();
                    JsonObject jsonResponse = gson.fromJson(responseString, JsonObject.class);
                    String answer = jsonResponse.getAsJsonArray("content").get(0).getAsJsonObject().get("text").getAsString();

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§a[MineTutor]§f " + answer);
                    });
                }
            }
        });
    }
}
