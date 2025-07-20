package com.ms1011.minetutor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaudeProviderTest {

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

    private ClaudeProvider claudeProvider;

    @BeforeEach
    void setUp() {
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
        when(config.getString("claude-model", "claude-3-haiku-20240307")).thenReturn("claude-3-haiku-20240307");

        claudeProvider = new ClaudeProvider(plugin);

        try {
            java.lang.reflect.Field clientField = ClaudeProvider.class.getDeclaredField("client");
            clientField.setAccessible(true);
            clientField.set(claudeProvider, client);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAskQuestionRequestFormat() throws Exception {
        when(client.newCall(any(Request.class))).thenReturn(call);

        claudeProvider.askQuestion(player, "What is the crafting recipe for a diamond pickaxe?");

        verify(client).newCall(requestCaptor.capture());
        Request request = requestCaptor.getValue();

        assertEquals("https://api.anthropic.com/v1/messages", request.url().toString());
        assertEquals("test-api-key", request.header("x-api-key"));
        assertEquals("2023-06-01", request.header("anthropic-version"));

        okio.Buffer buffer = new okio.Buffer();
        request.body().writeTo(buffer);
        String jsonBody = buffer.readUtf8();
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();

        assertEquals("claude-3-haiku-20240307", jsonObject.get("model").getAsString());
        assertEquals("You are a helpful assistant.", jsonObject.get("system").getAsString());
        assertEquals("What is the crafting recipe for a diamond pickaxe?", jsonObject.getAsJsonArray("messages").get(0).getAsJsonObject().get("content").getAsString());
    }
}
