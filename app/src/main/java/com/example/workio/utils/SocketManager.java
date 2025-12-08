package com.example.workio.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static final String TAG = "SocketManager";
    private static final String SOCKET_URL =
            "https://emsbackend-enh5aahkg4dcfkfs.southeastasia-01.azurewebsites.net";

    private static SocketManager instance;
    private Socket socket;
    private final Context context;

    private MessageListener messageListener;

    public interface MessageListener {
        void onMessageReceived(String senderId, String content);
    }

    private SocketManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized SocketManager getInstance(Context context) {
        if (instance == null) {
            instance = new SocketManager(context);
        }
        return instance;
    }

    public void connect(String token) {
        try {
            IO.Options options = new IO.Options();
            options.auth = new HashMap<String, String>() {{
                put("token", token);
            }};

            socket = IO.socket(SOCKET_URL, options);

            socket.on(Socket.EVENT_CONNECT, args ->
                    Log.d(TAG, "✅ Socket connected")
            );

            socket.on(Socket.EVENT_DISCONNECT, args ->
                    Log.d(TAG, "⚠️ Socket disconnected")
            );

            socket.on(Socket.EVENT_CONNECT_ERROR, args ->
                    Log.e(TAG, "❌ Socket error: " + args[0])
            );

            socket.on("message", args ->
                    Log.d(TAG, "Connected!")
            );

            socket.connect();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI error", e);
        }
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.off();
            socket = null;
        }
    }

    public void sendMessage(String receiverId, String content) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("receiverId", receiverId);
                data.put("content", content);

                socket.emit("send-message", data);

                Log.d(TAG, "📤 Sent message: " + content);

            } catch (JSONException e) {
                Log.e(TAG, "Error sending message", e);
            }
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
}
