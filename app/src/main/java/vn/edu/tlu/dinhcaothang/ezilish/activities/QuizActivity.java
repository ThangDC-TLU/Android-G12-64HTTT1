package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.adapters.QuizAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.utils.QuizGenerator;
import vn.edu.tlu.dinhcaothang.ezilish.utils.QuizQuestion;

public class QuizActivity extends AppCompatActivity {
    private RecyclerView rvQuiz;
    private QuizAdapter adapter;
    private TextView tvQuizTitle;
    private Button btnSubmitQuiz;
    private String topicId;
    private String topicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        rvQuiz = findViewById(R.id.rvQuiz);
        rvQuiz.setLayoutManager(new LinearLayoutManager(this));
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);

        topicName = getIntent().getStringExtra("topic");
        topicId = getIntent().getStringExtra("topicId");
        if (topicName == null) topicName = "Travel";
        tvQuizTitle.setText(topicName);

        QuizGenerator.generateQuiz(topicName, 20, new QuizGenerator.Callback() {
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

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnSubmitQuiz.setOnClickListener(v -> submitQuiz());
    }

    private void submitQuiz() {
        if (adapter == null) return;
        int score = adapter.calculateScore();

        showResultDialog(score, adapter.getItemCount());
        saveScoreToTopic(score);
    }

    private void saveScoreToTopic(int score) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (topicId == null || email == null) return;

        DatabaseReference topicRef = FirebaseDatabase.getInstance()
                .getReference("vocabularyTopics")
                .child(topicId);

        topicRef.child("score").setValue(score);

        // Nếu muốn lưu nhiều lần hoặc nhiều user, hãy dùng:
        // topicRef.child("scores").child(email.replace(".", "_")).setValue(score);
    }

    private void showResultDialog(int score, int total) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quiz_result, null);

        TextView tvScore = dialogView.findViewById(R.id.tvResultScore);
        TextView tvMessage = dialogView.findViewById(R.id.tvResultMessage);
        ImageView imgResult = dialogView.findViewById(R.id.imgResult);
        Button btnClose = dialogView.findViewById(R.id.btnCloseResult);

        tvScore.setText("Điểm số của bạn");
        tvMessage.setText("Bạn đạt " + score + " điểm!");

        // Optionally: đổi icon hoặc màu sắc dựa vào điểm số nếu muốn
        if (score == total) {
            imgResult.setImageResource(R.drawable.ic_star);
            imgResult.setColorFilter(0xFFFFD600);
        } else if (score >= total * 0.7) {
            imgResult.setImageResource(R.drawable.ic_star);
            imgResult.setColorFilter(0xFF2196F3);
        } else {
            imgResult.setImageResource(R.drawable.ic_star);
            imgResult.setColorFilter(0xFFFF0000);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}