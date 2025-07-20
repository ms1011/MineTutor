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
class GeminiProviderTest {

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

    private GeminiProvider geminiProvider;

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
        when(config.getString("gemini-model", "gemini-1.5-flash")).thenReturn("gemini-1.5-flash");

        geminiProvider = new GeminiProvider(plugin);

        try {
            java.lang.reflect.Field clientField = GeminiProvider.class.getDeclaredField("client");
            clientField.setAccessible(true);
            clientField.set(geminiProvider, client);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAskQuestionRequestFormat() throws Exception {
        when(client.newCall(any(Request.class))).thenReturn(call);

        geminiProvider.askQuestion(player, "What is the crafting recipe for a diamond pickaxe?");

        verify(client).newCall(requestCaptor.capture());
        Request request = requestCaptor.getValue();

        assertEquals("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=test-api-key", request.url().toString());

        okio.Buffer buffer = new okio.Buffer();
        request.body().writeTo(buffer);
        String jsonBody = buffer.readUtf8();
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();

        assertEquals("You are a helpful assistant.", jsonObject.getAsJsonObject("system_instruction").getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString());
        assertEquals("What is the crafting recipe for a diamond pickaxe?", jsonObject.getAsJsonArray("contents").get(0).getAsJsonObject().getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString());
    }
}
