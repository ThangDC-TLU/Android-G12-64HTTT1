package vn.edu.tlu.dinhcaothang.ezilish.activities;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_topic);

        recyclerView = findViewById(R.id.rv_topics);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        topicList = new ArrayList<>();
        adapter = new VocabularyTopicAdapter(topicList);
        recyclerView.setAdapter(adapter);

        fetchTopicsAndWordCounts();
    }

    /**
     * Đúng luồng:
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
                    VocabularyTopic topic = new VocabularyTopic(id, name, 0);
                    topicList.add(topic);
                    topicMap.put(id, topic);
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