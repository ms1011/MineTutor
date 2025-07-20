package com.ms1011.minetutor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GeminiProvider implements AIProvider {

    private final MineTutor plugin;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public GeminiProvider(MineTutor plugin) {
        this.plugin = plugin;
    }

    @Override
    public void askQuestion(Player player, String question) {
        String thinkingMessage = plugin.getConfig().getString("messages.thinking", "MineTutor is thinking...");
        player.sendMessage(thinkingMessage);

        String apiKey = plugin.getConfig().getString("api-key");
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY")) {
            player.sendMessage("§cError: Gemini API key is not set in the config.yml file.");
            return;
        }

        String model = plugin.getConfig().getString("gemini-model", "gemini-1.5-flash");
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        JsonObject payload = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", question);
        parts.add(textPart);
        content.add("parts", parts);
        contents.add(content);
        payload.add("contents", contents);

        // System Prompt
        String systemPrompt = plugin.getConfig().getString("system-prompt");
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JsonObject systemInstruction = new JsonObject();
            JsonObject systemPart = new JsonObject();
            systemPart.addProperty("text", systemPrompt);
            systemInstruction.add("parts", new JsonArray());
            systemInstruction.getAsJsonArray("parts").add(systemPart);
            payload.add("system_instruction", systemInstruction);
        }

        RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
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
                    String answer = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                            .getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject()
                            .get("text").getAsString();

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.sendMessage("§a[MineTutor]§f " + answer);
                    });
                }
            }
        });
    }
}
