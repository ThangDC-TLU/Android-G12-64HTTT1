package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword; // Ô nhập email và mật khẩu
    private Button btnLogin; // Nút đăng nhập
    private TextView tvSignUp, tvForgotPassword; // TextView cho đăng ký và quên mật khẩu
    private CheckBox cbRememberMe; // Checkbox để nhớ thông tin đăng nhập
    private SharedPreferences sharedPreferences; // Lưu trữ thông tin đăng nhập
    private DatabaseReference databaseReference; // Tham chiếu đến Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Bật chế độ Edge-to-Edge cho giao diện
        setContentView(R.layout.activity_login); // Thiết lập layout cho màn hình đăng nhập
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // Lấy kích thước thanh hệ thống
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Điều chỉnh padding
            return insets;
        });
        getSupportActionBar().hide(); // Ẩn ActionBar

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Khởi tạo SharedPreferences để lưu thông tin
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Khởi tạo các thành phần giao diện
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        // Tải thông tin đã lưu nếu "Remember me" được chọn trước đó
        loadSavedCredentials();

        // Thiết lập sự kiện click cho nút Đăng ký
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class)); // Chuyển sang màn hình đăng ký
        });

        // Thiết lập sự kiện click cho quên mật khẩu (chưa triển khai)
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password feature not implemented yet", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo
        });

        // Thiết lập sự kiện click cho nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim(); // Lấy email từ ô nhập
            String password = etPassword.getText().toString().trim(); // Lấy mật khẩu từ ô nhập

            if (validateInputs(email, password)) { // Kiểm tra đầu vào
                checkLogin(email, password); // Kiểm tra đăng nhập
            }
        });
    }

    // Tải thông tin đã lưu từ SharedPreferences
    private void loadSavedCredentials() {
        boolean isRemembered = sharedPreferences.getBoolean("remember_me", false); // Kiểm tra trạng thái "Remember me"
        if (isRemembered) {
            String savedEmail = sharedPreferences.getString("email", ""); // Lấy email đã lưu
            String savedPassword = sharedPreferences.getString("password", ""); // Lấy mật khẩu đã lưu
            etEmail.setText(savedEmail); // Điền email vào ô nhập
            etPassword.setText(savedPassword); // Điền mật khẩu vào ô nhập
            cbRememberMe.setChecked(true); // Đặt checkbox thành trạng thái đã chọn
        }
    }

    // Lưu thông tin vào SharedPreferences
    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit(); // Tạo đối tượng chỉnh sửa
        if (cbRememberMe.isChecked()) { // Nếu checkbox được chọn
            editor.putBoolean("remember_me", true); // Lưu trạng thái "Remember me"
            editor.putString("email", email); // Lưu email
            editor.putString("password", password); // Lưu mật khẩu
        } else { // Nếu không chọn
            editor.putBoolean("remember_me", false); // Đặt trạng thái "Remember me" thành false
            editor.remove("email"); // Xóa email
            editor.remove("password"); // Xóa mật khẩu
        }
        editor.apply(); // Áp dụng thay đổi
    }

    // Kiểm tra đầu vào
    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) { // Kiểm tra email có trống không
            etEmail.setError("Email is required"); // Hiển thị lỗi
            return false;
        }
        if (password.isEmpty()) { // Kiểm tra mật khẩu có trống không
            etPassword.setError("Password is required"); // Hiển thị lỗi
            return false;
        }
        return true; // Trả về true nếu hợp lệ
    }

    // Kiểm tra thông tin đăng nhập trong Firebase Realtime Database
    private void checkLogin(String email, String password) {
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) { // Kiểm tra xem email có tồn tại không
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userName = userSnapshot.child("username").getValue(String.class); // Lấy username
                        String storedPassword = userSnapshot.child("password").getValue(String.class); // Lấy mật khẩu lưu trữ
                        if (storedPassword != null && storedPassword.equals(password)) { // So sánh mật khẩu
                            // Đăng nhập thành công, lưu thông tin và chuyển màn hình
                            saveCredentials(email, password);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("username", userName); // Truyền username sang HomeActivity
                            startActivity(intent); // Chuyển sang HomeActivity
                            finish(); // Đóng LoginActivity
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show(); // Hiển thị lỗi mật khẩu sai
                } else {
                    Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show(); // Hiển thị lỗi email không tồn tại
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show(); // Hiển thị lỗi từ database
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy(); // Gọi phương thức onDestroy của lớp cha
    }
}