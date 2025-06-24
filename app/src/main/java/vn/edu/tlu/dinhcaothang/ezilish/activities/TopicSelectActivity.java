package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.adapters.TopicAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.models.Topic;

public class TopicSelectActivity extends AppCompatActivity {

    private RecyclerView rvTopics;
    private TopicAdapter adapter;
    private List<Topic> topicList = new ArrayList<>();
    private DatabaseReference topicRef;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_select);

        // Lấy email user hiện tại từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("email", null);

        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email người dùng, vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rvTopics = findViewById(R.id.rvTopics);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TopicAdapter(topicList, topic -> {
            Intent intent = new Intent(TopicSelectActivity.this, QuizActivity.class);
            intent.putExtra("topic", topic.name);
            intent.putExtra("topicId", topic.id);
            startActivity(intent);
        });
        rvTopics.setAdapter(adapter);

        // Lấy dữ liệu từ Firebase
        topicRef = FirebaseDatabase.getInstance().getReference("vocabularyTopics");
        loadTopics();

        findViewById(R.id.back_arrow).setOnClickListener(v -> finish());
    }

    private void loadTopics() {
        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                for (DataSnapshot topicSnap : snapshot.getChildren()) {
                    String id = topicSnap.getKey();
                    String name = topicSnap.child("name").getValue(String.class);
                    String email = topicSnap.child("email").getValue(String.class);

                    // Lọc topic theo email user hiện tại
                    if (name != null && email != null && email.equalsIgnoreCase(currentUserEmail)) {
                        topicList.add(new Topic(id, name, email));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopicSelectActivity.this, "Lỗi tải chủ đề!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}