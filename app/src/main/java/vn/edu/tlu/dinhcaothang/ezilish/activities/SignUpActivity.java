package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.utils.ImageUtils;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUserName, etEmail, etPassword, etDob;
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

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Ánh xạ view
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etDob = findViewById(R.id.etDob);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvSignOptions);

        // Mở DatePicker khi click vào DOB
        etDob.setOnClickListener(v -> showDatePicker());

        // Chuyển sang Login
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });

        // Đăng ký
        btnSignUp.setOnClickListener(v -> {
            String username = etUserName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String dob = etDob.getText().toString().trim();

            if (validateInputs(username, email, password, dob)) {
                checkEmailAvailability(email, username, password, dob);
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            etDob.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validateInputs(String username, String email, String password, String dob) {
        if (username.isEmpty()) {
            etUserName.setError("Vui lòng nhập tên người dùng");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        if (dob.isEmpty()) {
            etDob.setError("Vui lòng chọn ngày sinh");
            return false;
        }
        return true;
    }

    private void checkEmailAvailability(String email, String username, String password, String dob) {
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(SignUpActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            registerUser(email, username, password, dob);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(SignUpActivity.this, "Lỗi cơ sở dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser(String email, String username, String password, String dob) {
        String userId = databaseReference.push().getKey();
        if (userId != null) {
            // Lưu thông tin người dùng
            databaseReference.child(userId).child("username").setValue(username);
            databaseReference.child(userId).child("email").setValue(email);
            databaseReference.child(userId).child("password").setValue(password);
            databaseReference.child(userId).child("dob").setValue(dob);

            // Lưu ảnh đại diện mặc định
            String avatarBase64 = ImageUtils.getBase64FromDrawable(this, R.drawable.img_profile);
            databaseReference.child(userId).child("avatarBase64").setValue(avatarBase64);

            Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);

            finish();
        } else {
            Toast.makeText(SignUpActivity.this, "Lỗi khi tạo tài khoản", Toast.LENGTH_SHORT).show();
        }
    }
}
