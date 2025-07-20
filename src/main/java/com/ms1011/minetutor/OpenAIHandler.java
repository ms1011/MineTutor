package com.ms1011.minetutor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class OpenAIHandler {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    // TODO: Move API Key to a config file
    private static final String API_KEY = "YOUR_OPENAI_API_KEY";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public void askQuestion(Player player, String question) {
        player.sendMessage("MineTutor is thinking...");

        JsonObject payload = new JsonObject();
        payload.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", question);
        messages.add(message);

        payload.add("messages", messages);

        RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                player.getServer().getScheduler().runTask(MineTutor.getPlugin(MineTutor.class), () -> {
                    player.sendMessage("§cError: Failed to get response from MineTutor.");
                    e.printStackTrace();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    player.getServer().getScheduler().runTask(MineTutor.getPlugin(MineTutor.class), () -> {
                        try {
                            player.sendMessage("§cError: " + response.code() + " " + response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
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

                    player.getServer().getScheduler().runTask(MineTutor.getPlugin(MineTutor.class), () -> {
                        player.sendMessage("§a[MineTutor]§f " + answer);
                    });
                }
            }
        });
    }
}
