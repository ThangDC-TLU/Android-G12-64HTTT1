package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.edu.tlu.dinhcaothang.ezilish.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText etUserName, etEmail, etPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide(); // Ẩn ActionBar

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Khởi tạo các thành phần giao diện
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvSignOptions);

        // Thiết lập sự kiện click để chuyển về LoginActivity
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });

        // Thiết lập sự kiện click cho nút Đăng ký
        btnSignUp.setOnClickListener(v -> {
            String username = etUserName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInputs(username, email, password)) {
                checkEmailAvailability(email, username, password);
            }
        });
    }

    // Kiểm tra đầu vào
    private boolean validateInputs(String username, String email, String password) {
        if (username.isEmpty()) {
            etUserName.setError("Username is required");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    // Kiểm tra email đã tồn tại trong Firebase
    private void checkEmailAvailability(String email, String username, String password) {
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) { // Nếu email đã tồn tại
                    Toast.makeText(SignUpActivity.this, "Email already exists", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo
                } else {
                    registerUser(email, username, password); // Đăng ký người dùng nếu email chưa tồn tại
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignUpActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show(); // Hiển thị lỗi từ database
            }
        });
    }

    // Đăng ký người dùng vào Firebase
    private void registerUser(String email, String username, String password) {
        String userId = databaseReference.push().getKey(); // Tạo một key duy nhất cho người dùng
        if (userId != null) {
            databaseReference.child(userId).child("username").setValue(username); // Lưu username
            databaseReference.child(userId).child("email").setValue(email); // Lưu email
            databaseReference.child(userId).child("password").setValue(password); // Lưu password

            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo thành công
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class)); // Chuyển về màn hình đăng nhập
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}