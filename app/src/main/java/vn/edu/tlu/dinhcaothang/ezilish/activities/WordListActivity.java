package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.adapters.WordAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.models.Word;

public class WordListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WordAdapter adapter;
    private List<Word> wordList;
    private String topicId, topicName;
    private TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        recyclerView = findViewById(R.id.rv_words);
        tvHeader = findViewById(R.id.tvTopicHeader);
        Button btnAddWord = findViewById(R.id.btnAddWord);
        ImageView btnBack = findViewById(R.id.btnBack);

        wordList = new ArrayList<>();
        adapter = new WordAdapter(wordList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        topicId = getIntent().getStringExtra("topicId");
        topicName = getIntent().getStringExtra("topicName");
        tvHeader.setText(topicName);

        btnBack.setOnClickListener(v -> onBackPressed()); // Quay lại trang topic

        findViewById(R.id.btnAddWord).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddWordActivity.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        });

        fetchWords();
    }

    private void fetchWords() {
        DatabaseReference wordsRef = FirebaseDatabase.getInstance().getReference("words");
        wordsRef.orderByChild("topic_id").equalTo(topicId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        wordList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Word word = snap.getValue(Word.class);
                            wordList.add(word);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}