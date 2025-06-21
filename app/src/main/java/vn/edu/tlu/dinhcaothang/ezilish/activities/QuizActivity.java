package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.activities.QuizAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.activities.QuizGenerator;
import vn.edu.tlu.dinhcaothang.ezilish.activities.QuizQuestion;

public class QuizActivity extends AppCompatActivity {
    private RecyclerView rvQuiz;
    private QuizAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        rvQuiz = findViewById(R.id.rvQuiz);
        rvQuiz.setLayoutManager(new LinearLayoutManager(this));

        // Ví dụ lấy chủ đề từ Intent hoặc chọn mặc định
        String topic = getIntent().getStringExtra("topic");
        if (topic == null) topic = "Travel";

        QuizGenerator.generateQuiz(topic, new QuizGenerator.Callback() {
            @Override
            public void onSuccess(List<QuizQuestion> quiz) {
                runOnUiThread(() -> {
                    adapter = new QuizAdapter(quiz);
                    rvQuiz.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(QuizActivity.this, "Lỗi tạo quiz: " + error, Toast.LENGTH_LONG).show());
            }
        });
    }
}