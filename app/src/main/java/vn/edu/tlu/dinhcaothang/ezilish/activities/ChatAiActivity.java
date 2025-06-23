package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.tlu.dinhcaothang.ezilish.BuildConfig;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.adapters.ChatAiAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.models.MessageAi;

public class ChatAiActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private EditText etMessage;
    private Button btnSend;
    private RecyclerView rvChatMessages;
    private ChatAiAdapter chatAiAdapter;
    private List<MessageAi> messageAiList = new ArrayList<>();

    private String userEmail;
    private static final String BASE_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_ai);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Lấy email người dùng từ Intent
        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null || userEmail.isEmpty()) userEmail = "unknown_user";

        btnBack = findViewById(R.id.btnBack);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        rvChatMessages = findViewById(R.id.rvChatMessages);

        chatAiAdapter = new ChatAiAdapter(messageAiList);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAiAdapter);

        loadChatHistory();

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSend.setOnClickListener(v -> {
            String userText = etMessage.getText().toString().trim();
            if (!userText.isEmpty()) {
                addMessage(userText, true);
                etMessage.setText("");
                sendToGemini(userText);
            }
        });
    }

    private String getPrefsName() {
        return "chat_prefs_" + userEmail;
    }

    private String getChatKey() {
        return "chat_history_" + userEmail;
    }

    private void addMessage(String content, boolean isUser) {
        messageAiList.add(new MessageAi(content, isUser));
        chatAiAdapter.notifyItemInserted(messageAiList.size() - 1);
        rvChatMessages.scrollToPosition(messageAiList.size() - 1);
        saveChatHistory();
    }

    private void sendToGemini(String userPrompt) {
        try {
            JSONObject part = new JSONObject();
            part.put("text", userPrompt);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(userMessage);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);

            String API_URL = BASE_API_URL + BuildConfig.GEMINI_API_KEY;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL, requestBody,
                    response -> {
                        try {
                            String aiReply = response
                                    .getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");
                            addMessage(aiReply, false);
                        } catch (JSONException e) {
                            Log.e("Gemini", "Lỗi xử lý JSON phản hồi", e);
                            addMessage("Lỗi khi đọc phản hồi từ AI", false);
                        }
                    },
                    error -> {
                        Log.e("GeminiAPI", "Lỗi kết nối: " + error.toString());
                        addMessage("Không kết nối được với AI: " + error.getMessage(), false);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            Log.e("Gemini", "Lỗi tạo JSON request", e);
            addMessage("Lỗi tạo yêu cầu JSON", false);
        }
    }

    private void saveChatHistory() {
        SharedPreferences prefs = getSharedPreferences(getPrefsName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = new Gson().toJson(messageAiList);
        editor.putString(getChatKey(), json);
        editor.apply();
    }

    private void loadChatHistory() {
        SharedPreferences prefs = getSharedPreferences(getPrefsName(), MODE_PRIVATE);
        String json = prefs.getString(getChatKey(), null);
        if (json != null) {
            Type type = new TypeToken<List<MessageAi>>() {}.getType();
            List<MessageAi> savedMessageAis = new Gson().fromJson(json, type);
            messageAiList.clear();
            messageAiList.addAll(savedMessageAis);
            chatAiAdapter.notifyDataSetChanged();
        }
    }

    public void clearChatHistory() {
        getSharedPreferences(getPrefsName(), MODE_PRIVATE).edit().remove(getChatKey()).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
