package vn.edu.tlu.dinhcaothang.ezilish.activities;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.adapters.VocabularyTopicAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.models.VocabularyTopic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VocabularyTopicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VocabularyTopicAdapter adapter;
    private List<VocabularyTopic> topicList;

    // Các View cho dialog thêm topic
    private LinearLayout addTopicDialog;
    private View blurOverlay;
    private EditText etTopicName;
    private Button btnSave, btnCancel;

    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_topic);

        recyclerView = findViewById(R.id.rv_topics);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        topicList = new ArrayList<>();
        adapter = new VocabularyTopicAdapter(topicList, this);
        recyclerView.setAdapter(adapter);

        // Lấy email user từ SharedPreferences (KHÔNG dùng Firebase Auth)
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", null);

        // KHÔNG kiểm tra email ở đây, chỉ cảnh báo khi tạo topic

        // Ánh xạ các view dialog
        addTopicDialog = findViewById(R.id.addTopicDialog);
        blurOverlay = findViewById(R.id.blurOverlay);
        etTopicName = findViewById(R.id.etTopicName);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        findViewById(R.id.btn_add).setOnClickListener(v -> showAddTopicDialog());
        findViewById(R.id.tv_add_topic).setOnClickListener(v -> showAddTopicDialog());
        btnCancel.setOnClickListener(v -> hideAddTopicDialog());
        blurOverlay.setOnClickListener(v -> hideAddTopicDialog());
        btnSave.setOnClickListener(v -> saveTopic());

        findViewById(R.id.back_arrow).setOnClickListener(v -> onBackPressed());

        fetchTopicsAndWordCounts();
    }

    // Hiện dialog thêm topic và overlay mờ
    private void showAddTopicDialog() {
        etTopicName.setText("");
        blurOverlay.setVisibility(View.VISIBLE);
        addTopicDialog.setVisibility(View.VISIBLE);
        etTopicName.requestFocus();
    }

    // Ẩn dialog và overlay
    private void hideAddTopicDialog() {
        blurOverlay.setVisibility(View.GONE);
        addTopicDialog.setVisibility(View.GONE);
    }

    // Lưu dữ liệu lên Firebase và cập nhật giao diện
    private void saveTopic() {

        String topicName = etTopicName.getText().toString().trim();
        if (TextUtils.isEmpty(topicName)) {
            etTopicName.setError("Vui lòng nhập tên chủ đề");
            return;
        }
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email người dùng, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference("vocabularyTopics");
        String topicId = topicRef.push().getKey();
        if (topicId == null) {
            Toast.makeText(this, "Không thể tạo chủ đề mới. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo dữ liệu topic mới
        Map<String, Object> topicData = new HashMap<>();
        topicData.put("name", topicName);
        topicData.put("email", currentUserEmail); // Thêm email vào topic

        // Lưu lên Firebase
        topicRef.child(topicId).setValue(topicData, (error, ref) -> {
            if (error == null) {
                Toast.makeText(this, "Đã thêm chủ đề!", Toast.LENGTH_SHORT).show();
                hideAddTopicDialog();
                fetchTopicsAndWordCounts(); // refresh list
            } else {
                Toast.makeText(this, "Lỗi khi thêm chủ đề", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 1. Lấy danh sách topic.
     * 2. Lấy toàn bộ words, đếm số lượng theo topic_id.
     * 3. Gán wordCount vào từng topic.
     * 4. Cập nhật adapter.
     */
    private void fetchTopicsAndWordCounts() {
        DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference("vocabularyTopics");
        DatabaseReference wordsRef = FirebaseDatabase.getInstance().getReference("words");

        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot topicSnapshot) {
                topicList.clear();
                Map<String, VocabularyTopic> topicMap = new HashMap<>();
                for (DataSnapshot topicSnap : topicSnapshot.getChildren()) {
                    String id = topicSnap.getKey();
                    String name = topicSnap.child("name").getValue(String.class);
                    String email = topicSnap.child("email").getValue(String.class);

                    // Lọc chỉ những topic của user hiện tại
                    if (name != null && email != null && email.equalsIgnoreCase(currentUserEmail)) {
                        VocabularyTopic topic = new VocabularyTopic(id, name, 0, email);
                        topicList.add(topic);
                        topicMap.put(id, topic);
                    }
                }

                // Sau khi có danh sách topic, lấy toàn bộ words để đếm số lượng theo topic_id
                wordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot wordSnapshot) {
                        // Đếm số từ cho mỗi topic
                        for (DataSnapshot wordSnap : wordSnapshot.getChildren()) {
                            String topicId = wordSnap.child("topic_id").getValue(String.class);
                            if (topicId != null && topicMap.containsKey(topicId)) {
                                VocabularyTopic topic = topicMap.get(topicId);
                                topic.setWordCount(topic.getWordCount() + 1);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu cần
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}