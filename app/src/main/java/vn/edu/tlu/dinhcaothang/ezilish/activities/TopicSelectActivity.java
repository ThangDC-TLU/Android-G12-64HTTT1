package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.content.Intent;
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
import vn.edu.tlu.dinhcaothang.ezilish.activities.TopicAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.activities.Topic;

public class TopicSelectActivity extends AppCompatActivity {

    private RecyclerView rvTopics;
    private TopicAdapter adapter;
    private List<Topic> topicList = new ArrayList<>();
    private DatabaseReference topicRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_select);

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
    }

    private void loadTopics() {
        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                for (DataSnapshot topicSnap : snapshot.getChildren()) {
                    String id = topicSnap.getKey();
                    String name = topicSnap.child("name").getValue(String.class);
                    if (name != null) {
                        topicList.add(new Topic(id, name));
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