package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.utils.BottomNavHelper;

public class SettingActivity extends AppCompatActivity {

    private String email, username, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");

        // Gán bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_setting);
        BottomNavHelper.setupNavigation(bottomNav, this, username, email);

        // Tìm userId từ email
        getUserIdByEmail();

        // Xử lý đổi mật khẩu
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> showChangePasswordDialog());

        // Xử lý đăng xuất
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    private void getUserIdByEmail() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String userEmail = userSnap.child("email").getValue(String.class);
                    if (email != null && email.equalsIgnoreCase(userEmail)) {
                        userId = userSnap.getKey();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SettingActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        EditText etCurrent = view.findViewById(R.id.etCurrentPassword);
        EditText etNew = view.findViewById(R.id.etNewPassword);
        EditText etConfirm = view.findViewById(R.id.etConfirmPassword);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Đổi mật khẩu")
                .setView(view)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String current = etCurrent.getText().toString().trim();
            String newPass = etNew.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String currentDbPass = snapshot.getValue(String.class);
                    if (!current.equals(currentDbPass)) {
                        Toast.makeText(SettingActivity.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                    } else {
                        userRef.child("password").setValue(newPass);
                        Toast.makeText(SettingActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(SettingActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }));

        dialog.show();
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
}
