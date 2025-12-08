package com.example.workio.ui.main.chat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.api.ApiService;
import com.example.workio.data.api.RetrofitClient;
import com.example.workio.data.model.Conversation;
import com.example.workio.data.model.SimpleMessage;
import com.example.workio.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private List<Conversation> conversations = new ArrayList<>();
    private ApiService api;
    private String currentUserId;

    private Button btnPersonal, btnGroup; // hai nút trên cùng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnPersonal = view.findViewById(R.id.btnDirect);
        btnGroup = view.findViewById(R.id.btnGroup);

        adapter = new ChatListAdapter(conversations, getContext());
        recyclerView.setAdapter(adapter);

        api = RetrofitClient.getInstance(getContext()).getApiService();
        currentUserId = new SessionManager(getContext()).getEmployeeId();

        // Mặc định hiển thị tab "Cá nhân"
        loadPersonalChats();

        // Xử lý chuyển tab
        btnPersonal.setOnClickListener(v -> {
            btnPersonal.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
            btnPersonal.setTextColor(Color.WHITE);

            btnGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            btnGroup.setTextColor(Color.BLACK);
            loadPersonalChats();
        });

        btnGroup.setOnClickListener(v -> {
            btnGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
            btnGroup.setTextColor(Color.WHITE);

            btnPersonal.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            btnPersonal.setTextColor(Color.BLACK);
            loadGroupChatItem();
        });

        return view;
    }

    // 🟩 Tab "Cá nhân" → load từ API
    private void loadPersonalChats() {
        api.getConversations().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JSONObject root = new JSONObject(json);
                        JSONArray dataArray = root.getJSONArray("data");

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Conversation>>() {}.getType();
                        List<Conversation> list = gson.fromJson(dataArray.toString(), listType);

                        conversations.clear();
                        for (Conversation c : list) {
                            if (!c.isGroup()) {
                                conversations.add(c);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        Log.d("ChatList", "✅ Loaded " + conversations.size() + " personal chats");

                    } catch (Exception e) {
                        Log.e("ChatList", "❌ Parse error: " + e.getMessage());
                    }
                } else {
                    Log.e("ChatList", "⚠️ Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ChatList", "❌ API error: " + t.getMessage());
            }
        });
    }

    // 🟦 Tab "Nhóm" → chỉ hiển thị 1 item "Nhóm chi nhánh"
    private void loadGroupChatItem() {
        conversations.clear();

        Conversation groupConv = new Conversation();
        groupConv.setGroup(true);
        groupConv.setGroupName("💬 Nhóm chi nhánh");
        groupConv.setLastMessage(new SimpleMessage("Bấm để xem tin nhắn nhóm", null, "Group Chat"));

        conversations.add(groupConv);
        adapter.notifyDataSetChanged();
        Log.d("ChatList", "✅ Loaded group chat tab");
    }
}
