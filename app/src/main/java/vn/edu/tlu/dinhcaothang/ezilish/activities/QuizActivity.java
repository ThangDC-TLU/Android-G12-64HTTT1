package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;

public class QuizActivity extends AppCompatActivity {
    private RecyclerView rvQuiz;
    private QuizAdapter adapter;
    private TextView tvQuizTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        rvQuiz = findViewById(R.id.rvQuiz);
        rvQuiz.setLayoutManager(new LinearLayoutManager(this));
        tvQuizTitle = findViewById(R.id.tvQuizTitle);

        // Lấy chủ đề từ Intent, nếu không có thì mặc định là "Travel"
        String topic = getIntent().getStringExtra("topic");
        if (topic == null) topic = "Travel";
        tvQuizTitle.setText(topic);

        // Gọi generator để lấy 20 câu hỏi
        QuizGenerator.generateQuiz(topic, 20, new QuizGenerator.Callback() {
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

        // Xử lý nút back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}