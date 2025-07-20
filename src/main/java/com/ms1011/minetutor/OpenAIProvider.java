package com.ms1011.minetutor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class OpenAIProvider implements AIProvider {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final MineTutor plugin;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public OpenAIProvider(MineTutor plugin) {
        this.plugin = plugin;
    }

    @Override
    public void askQuestion(Player player, String question) {
        String thinkingMessage = plugin.getConfig().getString("messages.thinking", "MineTutor is thinking...");
        player.sendMessage(thinkingMessage);

        String apiKey = plugin.getConfig().getString("api-key");
        if (apiKey == null || apiKey.equals("YOUR_OPENAI_API_KEY") || apiKey.isEmpty()) {
            player.sendMessage("§cError: OpenAI API key is not set in the config.yml file.");
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();

        // System Prompt
        String systemPrompt = plugin.getConfig().getString("system-prompt");
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", systemPrompt);
            messages.add(systemMessage);
        }

        // User Message
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
                .header("Authorization", "Bearer " + apiKey)
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
                    if (responseBody == null) {
                        return;
                    }
                    String responseString = responseBody.string();
                    JsonObject jsonResponse = gson.fromJson(responseString, JsonObject.class);
                    String answer = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§a[MineTutor]§f " + answer);
                    });
                }
            }
        });
    }
}
