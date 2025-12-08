package com.example.workio.ui.main.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.ApiResponse;
import com.example.workio.data.model.LoginResponse;
import com.example.workio.data.model.Message;
import com.example.workio.data.model.SendMessageRequest;
import com.example.workio.ui.main.MainActivity;
import com.example.workio.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerChat;
    private EditText etMessage;
    private ImageButton btnSend, btnBack;
    private TextView tvReceiverName;

    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    private String receiverId;
    private String receiverName;
    private String currentUserId;
    private String socketUrl = "https://emsbackend-enh5aahkg4dcfkfs.southeastasia-01.azurewebsites.net";

    private boolean isGroup = false;
    private ApiService apiService;

    // Socket.IO
    private Socket mSocket;
    private boolean socketListenersRegistered = false;
    // ===================== ON CREATE VIEW =====================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerChat = view.findViewById(R.id.recyclerChat);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.btnBack);
        tvReceiverName = view.findViewById(R.id.tvReceiverName);

        apiService = RetrofitClient.getInstance(getContext()).getApiService();
        SessionManager session = new SessionManager(getContext());
        currentUserId = session.getEmployeeId();

        // ========== GET BUNDLE DATA ==========
        if (getArguments() != null) {
            receiverId = getArguments().getString("receiverId");
            receiverName = getArguments().getString("receiverName");
            isGroup = getArguments().getBoolean("isGroup", false);
            tvReceiverName.setText(isGroup ? "Nhóm Chat" : receiverName);
        }

        // Setup RecyclerView
        adapter = new MessageAdapter(messageList, currentUserId);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(adapter);

        // ========== BUTTON EVENTS ==========
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnSend.setOnClickListener(v -> sendMessage());

        // Load history & connect socket
        loadChatHistory();
        connectSocket();
        Log.d("Socket", "URL: " + socketUrl);
        Log.d("Socket", "Token: " + session.getAccessToken());
        Log.d("Socket", "ReceiverID: " + receiverId);
        Log.d("Socket", "isGroup: " + isGroup);

        return view;
    }

    // ===================== CONNECT SOCKET =====================
    private void connectSocket() {
        if (getContext() == null) return;
        if (mSocket != null && mSocket.connected()) {
            Log.d("Socket", "⚠️ Already connected, skip connectSocket()");
            return;
        }
        try {
            IO.Options options = new IO.Options();
            options.forceNew = false;
            options.transports = new String[]{"websocket"};
            options.timeout = 20000;
            options.reconnection = true;

            SessionManager session = new SessionManager(getContext());
            String token = session.getAccessToken();

            Map<String, Object> authMap = new HashMap<>();
            authMap.put("token", token);
            Map<String, String> authStringMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : authMap.entrySet()) {
                authStringMap.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
            }
            options.auth = authStringMap;

            Log.d("Socket", "Auth token length: " + (token != null ? token.length() : 0));

            if (mSocket == null) {
                mSocket = IO.socket(socketUrl, options);
            }

            if (!socketListenersRegistered) {
                registerSocketListeners();
                socketListenersRegistered = true;
            }

            // ✅ CONNECT SAU CÙNG
            mSocket.connect();
        } catch (URISyntaxException e) {
            Log.e("Socket", "URI Error: " + e.getMessage());
        }
    }
    private void registerSocketListeners() {
        // CONNECT
        mSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("Socket", "✅ CONNECTED SUCCESS!");
            if (getActivity() == null) return;

            requireActivity().runOnUiThread(() -> {
                try {
                    if (isGroup) {
                        mSocket.emit("group:join");
                        Log.d("Socket", "✅ Joined GROUP room");
                    } else if (receiverId != null && !receiverId.isEmpty()) {
                        JSONObject joinData = new JSONObject();
                        joinData.put("userId", receiverId);
                        mSocket.emit("direct:join", joinData);
                        Log.d("Socket", "✅ Joined DIRECT room: " + receiverId);
                    }
                } catch (JSONException e) {
                    Log.e("Socket", "JSON Error: " + e.getMessage());
                }
            });
        });

        // CONNECT ERROR – in chi tiết
        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.e("Socket", "❌ CONNECT ERROR detail: " + java.util.Arrays.toString(args));
        });

        // DIRECT MESSAGE
        mSocket.on("direct:message:received", args -> {
            Log.d("Socket", "🔥 DIRECT MESSAGE EVENT FIRED");
            if (getActivity() == null) return;

            requireActivity().runOnUiThread(() -> {
                try {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        Message newMsg = Message.fromJson(data.toString());
                        if (newMsg != null &&
                                newMsg.getSenderId() != null &&
                                newMsg.getSenderId().getId() != null &&
                                !newMsg.getSenderId().getId().equals(currentUserId)) {

                            messageList.add(newMsg);
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerChat.scrollToPosition(messageList.size() - 1);
                            Log.d("Socket", "📨 NEW DIRECT MSG ADDED: " + newMsg.getContent());
                        }
                    }
                } catch (Exception e) {
                    Log.e("Socket", "Parse direct msg error: " + e.getMessage());
                }
            });
        });

        // GROUP MESSAGE
        mSocket.on("group:message:received", args -> {
            Log.d("Socket", "🔥 GROUP MESSAGE EVENT FIRED");
            if (getActivity() == null) return;

            requireActivity().runOnUiThread(() -> {
                try {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        Message newMsg = Message.fromJson(data.toString());
                        if (newMsg != null &&
                                newMsg.getSenderId() != null &&
                                newMsg.getSenderId().getId() != null &&
                                !newMsg.getSenderId().getId().equals(currentUserId)) {

                            messageList.add(newMsg);
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerChat.scrollToPosition(messageList.size() - 1);
                            Log.d("Socket", "📨 NEW GROUP MSG ADDED: " + newMsg.getContent());
                        }
                    }
                } catch (Exception e) {
                    Log.e("Socket", "Parse group msg error: " + e.getMessage());
                }
            });
        });

        // DISCONNECT (optional log)
        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.d("Socket", "⚠️ DISCONNECTED: " + java.util.Arrays.toString(args));
        });
    }

    // ===================== LOAD CHAT HISTORY (Kết hợp cả 2) =====================
    private void loadChatHistory() {
        if (isGroup) {
            loadGroupChatHistory();
        } else {
            loadDirectChatHistory();
        }
    }

    private void loadDirectChatHistory() {
        apiService.getDirectMessages(receiverId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String json = response.body().string();
                                JSONObject root = new JSONObject(json);
                                JSONObject data = root.getJSONObject("data");
                                JSONArray messagesArray = data.getJSONArray("messages");

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Message>>() {}.getType();

                                List<Message> list =
                                        gson.fromJson(messagesArray.toString(), listType);

                                messageList.clear();
                                messageList.addAll(list);
                                adapter.notifyDataSetChanged();

                                recyclerChat.scrollToPosition(messageList.size() - 1);
                                Log.d("ChatFragment", "Loaded direct: " + list.size());

                            } catch (Exception e) {
                                Log.e("ChatFragment", "Parse direct error: " + e.getMessage());
                            }
                        } else {
                            Log.e("ChatFragment", "Direct API error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("ChatFragment", "Direct API failure: " + t.getMessage());
                    }
                });
    }

    private void loadGroupChatHistory() {
        apiService.getGroupChatHistory()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String json = response.body().string();
                                JSONObject root = new JSONObject(json);
                                JSONObject data = root.getJSONObject("data");
                                JSONArray messagesArray = data.getJSONArray("messages");

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Message>>() {}.getType();

                                List<Message> list =
                                        gson.fromJson(messagesArray.toString(), listType);

                                messageList.clear();
                                messageList.addAll(list);
                                adapter.notifyDataSetChanged();

                                recyclerChat.scrollToPosition(messageList.size() - 1);
                                Log.d("ChatFragment", "Loaded group: " + list.size());

                            } catch (Exception e) {
                                Log.e("ChatFragment", "Parse group error: " + e.getMessage());
                            }
                        } else {
                            Log.e("ChatFragment", "Group API error code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("ChatFragment", "Group API fail: " + t.getMessage());
                    }
                });
    }


    // ===================== SEND MESSAGE (Giữ nguyên nhưng optional socket) =====================
// Hàm dispatch chung khi bấm nút gửi
    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        if (isGroup) {
            // Tạm thời vẫn dùng API cho group
            sendGroupMessage(content);
        } else {
            // Direct chat: ưu tiên socket, fallback API
            sendDirectMessageSocket(content);
        }
    }
    // Gửi tin nhắn direct qua Socket.IO
    private void sendDirectMessageSocket(String content) {
        if (content == null || content.trim().isEmpty()) return;

        // Nếu socket chưa sẵn sàng thì fallback về API
        if (mSocket == null || !mSocket.connected()) {
            Log.e("Socket", "Socket not connected -> fallback to REST API");
            sendDirectMessage(content); // dùng hàm API sẵn có
            return;
        }

        try {
            JSONObject data = new JSONObject();
            data.put("receiverId", receiverId);
            data.put("content", content);
            mSocket.emit("direct:message", data);
            Log.d("Socket", "🚀 EMIT direct:message: " + data.toString());

            // 2. Tự tạo Message local để HIỆN NGAY trên UI cho chính mình
            Message selfMsg = new Message();
            selfMsg.setContent(content);

            LoginResponse.User sender = new LoginResponse.User();
            sender.setId(currentUserId);
            selfMsg.setSenderId(sender);

            // Timestamp giống backend
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String ts = utcFormat.format(new Date());
            selfMsg.setTimestamp(ts);


            messageList.add(selfMsg);
            adapter.notifyItemInserted(messageList.size() - 1);
            recyclerChat.scrollToPosition(messageList.size() - 1);
            etMessage.setText("");

        } catch (JSONException e) {
            Log.e("Socket", "Send socket error: " + e.getMessage());
            // Nếu JSON lỗi, vẫn có thể fallback API:
            sendDirectMessage(content);
        }
    }

    private void sendDirectMessage(String content) {
        SendMessageRequest request =
                new SendMessageRequest(currentUserId, receiverId, content);

        apiService.sendDirectMessage(request)
                .enqueue(new Callback<ApiResponse<Message>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Message>> call,
                                           Response<ApiResponse<Message>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            messageList.add(response.body().getData());
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerChat.scrollToPosition(messageList.size() - 1);
                            etMessage.setText("");
                        } else {
                            Toast.makeText(getContext(),
                                    "Gửi thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Message>> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Lỗi mạng khi gửi tin nhắn",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendGroupMessage(String content) {
        SendMessageRequest request =
                new SendMessageRequest(currentUserId, null, content);

        apiService.sendGroupMessage(request)
                .enqueue(new Callback<ApiResponse<Message>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Message>> call,
                                           Response<ApiResponse<Message>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            messageList.add(response.body().getData());
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerChat.scrollToPosition(messageList.size() - 1);
                            etMessage.setText("");
                        } else {
                            Toast.makeText(getContext(),
                                    "Gửi nhóm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Message>> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Lỗi mạng khi gửi nhóm",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // ===================== LIFECYCLE =====================
    @Override
    public void onResume() {
        super.onResume();
        if (mSocket != null && !mSocket.connected()) {
            connectSocket();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setBottomNavVisibility(true);
        }
    }
}
