package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.utils.BottomNavHelper;

public class HomeActivity extends AppCompatActivity {
    private TextView tvExploreMore;
    private Button btnFlashCard, btnQuiz, btnFindCompanion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        TextView tvUserName = findViewById(R.id.tvUserName);
        String username = getIntent().getStringExtra("username");
        tvUserName.setText(username != null ? "Hello, " + username : "Hello, Guest");

        tvExploreMore = findViewById(R.id.tvExploreMore);
        tvExploreMore.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FindCompanionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("email", getIntent().getStringExtra("email"));
            startActivity(intent);
        });

        // Gán các nút bấm
        btnFlashCard = findViewById(R.id.btnFlashCard);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnFindCompanion = findViewById(R.id.btnFindCompanion);
        btnFlashCard.setOnClickListener(v ->
                startActivity(new Intent(this, VocabularyTopicActivity.class)));

        btnQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, TopicSelectActivity.class)));

        btnFindCompanion.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FindCompanionActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("email", getIntent().getStringExtra("email"));
            startActivity(intent);
        });

        // Gán bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home); // Đánh dấu Home đang được chọn

        // Cài đặt menu
        BottomNavHelper.setupNavigation(bottomNav, this, username, getIntent().getStringExtra("email"));
    }
}
