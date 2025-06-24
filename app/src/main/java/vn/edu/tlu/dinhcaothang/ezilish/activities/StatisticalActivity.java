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
import vn.edu.tlu.dinhcaothang.ezilish.adapters.StatisticalAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.models.Statistical;

public class StatisticalActivity extends AppCompatActivity {
    private RecyclerView rvTopicStats;
    private StatisticalAdapter adapter;
    private final List<Statistical> statsList = new ArrayList<>();
    private String userEmail;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);

        rvTopicStats = findViewById(R.id.rvTopicStats);
        rvTopicStats.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatisticalAdapter(statsList);
        rvTopicStats.setAdapter(adapter);

        userEmail = getUserEmailFromPrefs();
        if (userEmail == null) {
            Toast.makeText(this, "Không tìm thấy email người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        dbRef = FirebaseDatabase.getInstance().getReference();
        fetchTopicsOfCurrentUser();

        findViewById(R.id.back_arrow).setOnClickListener(v ->
                startActivity(new Intent(this, SettingActivity.class)));
    }

    private String getUserEmailFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("email", null);
    }

    private void fetchTopicsOfCurrentUser() {
        dbRef.child("vocabularyTopics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot topicsSnap) {
                statsList.clear();
                for (DataSnapshot topicSnap : topicsSnap.getChildren()) {
                    String topicEmail = topicSnap.child("email").getValue(String.class);
                    if (topicEmail != null && topicEmail.equals(userEmail)) {
                        String topicId = topicSnap.getKey();
                        String topicName = topicSnap.child("name").getValue(String.class);
                        int score = fetchScoreOfTopic(topicSnap);

                        // Gọi hàm mới để lấy số từ từ bảng words riêng biệt
                        fetchWordsStatsByTopicId(topicId, topicName, score);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatisticalActivity.this, "Lỗi lấy chủ đề", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm lấy điểm của topic
    private int fetchScoreOfTopic(DataSnapshot topicSnap) {
        Integer score = topicSnap.child("score").getValue(Integer.class);
        return score != null ? score : 0;
    }

    // Hàm lấy số từ đã học và tổng số từ (trả về mảng 2 phần tử: [learned, total])
    private void fetchWordsStatsByTopicId(String topicId, String topicName, int score) {
        dbRef.child("words").orderByChild("topic_id").equalTo(topicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot wordsSnap) {
                        int totalWords = 0;
                        int learnedCount = 0;

                        for (DataSnapshot wordSnap : wordsSnap.getChildren()) {
                            totalWords++;
                            Boolean learned = wordSnap.child("learned").getValue(Boolean.class);
                            if (Boolean.TRUE.equals(learned)) {
                                learnedCount++;
                            }
                        }

                        // Thêm vào danh sách và cập nhật adapter
                        statsList.add(new Statistical(topicId, topicName, learnedCount, totalWords, score));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StatisticalActivity.this, "Lỗi lấy từ vựng", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}