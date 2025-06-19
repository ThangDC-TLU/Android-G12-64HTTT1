package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.tlu.dinhcaothang.ezilish.R;

public class HomeActivity extends AppCompatActivity {
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

        getSupportActionBar().hide();

        // Get username from Intent
        TextView tvUserName = findViewById(R.id.tvUserName); // Giả sử ID này có trong layout
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            tvUserName.setText("Hello, " + username);
        } else {
            tvUserName.setText("Hello, Guest"); // Mặc định nếu không có username
        }
    }
}