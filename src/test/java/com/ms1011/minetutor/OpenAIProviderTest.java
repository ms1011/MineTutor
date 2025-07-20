package com.ms1011.minetutor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAIProviderTest {

    @Mock
    private MineTutor plugin;
    @Mock
    private Player player;
    @Mock
    private FileConfiguration config;
    @Mock
    private OkHttpClient client;
    @Mock
    private Call call;
    @Mock
    private Server server;
    @Mock
    private BukkitScheduler scheduler;
    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    private OpenAIProvider openAIProvider;

    @BeforeEach
    void setUp() {
        // Mock Bukkit environment
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        when(scheduler.runTask(any(MineTutor.class), any(Runnable.class))).thenAnswer(invocation -> {
            invocation.getArgument(1, Runnable.class).run();
            return null;
        });

        when(plugin.getConfig()).thenReturn(config);
        when(config.getString(eq("messages.thinking"), anyString())).thenReturn("Thinking...");
        when(config.getString("api-key")).thenReturn("test-api-key");
        when(config.getString("system-prompt")).thenReturn("You are a helpful assistant.");

        openAIProvider = new OpenAIProvider(plugin);

        // Inject the mocked client
        try {
            java.lang.reflect.Field clientField = OpenAIProvider.class.getDeclaredField("client");
            clientField.setAccessible(true);
            clientField.set(openAIProvider, client);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAskQuestionRequestFormat() throws Exception {
        when(client.newCall(any(Request.class))).thenReturn(call);

        openAIProvider.askQuestion(player, "What is the crafting recipe for a diamond pickaxe?");

        verify(client).newCall(requestCaptor.capture());
        Request request = requestCaptor.getValue();

        assertEquals("https://api.openai.com/v1/chat/completions", request.url().toString());
        assertEquals("Bearer test-api-key", request.header("Authorization"));

        // Extract and parse the JSON body
        okhttp3.RequestBody body = request.body();
        okio.Buffer buffer = new okio.Buffer();
        body.writeTo(buffer);
        String jsonBody = buffer.readUtf8();
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();

        assertEquals("gpt-3.5-turbo", jsonObject.get("model").getAsString());
        assertEquals("You are a helpful assistant.", jsonObject.getAsJsonArray("messages").get(0).getAsJsonObject().get("content").getAsString());
        assertEquals("What is the crafting recipe for a diamond pickaxe?", jsonObject.getAsJsonArray("messages").get(1).getAsJsonObject().get("content").getAsString());
    }
}
