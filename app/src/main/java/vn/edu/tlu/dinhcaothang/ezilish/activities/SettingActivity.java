package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.utils.BottomNavHelper;
import vn.edu.tlu.dinhcaothang.ezilish.utils.ImageUtils;

public class SettingActivity extends AppCompatActivity {

    private String email, username, userId;
    private ImageView imgAvatar, imgEditAvatar;
    private TextView tvUsername;
    private ActivityResultLauncher<String> imagePickerLauncher;

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

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");

        imgAvatar = findViewById(R.id.imgAvatar);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);

        // Launcher chọn ảnh từ thư viện
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && userId != null) {
                        ImageUtils.uploadAvatarBase64(uri, userId, this, imgAvatar);
                    }
                }
        );

        // Click biểu tượng máy ảnh -> chọn ảnh
        imgEditAvatar.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1001);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        });

        // Xem ảnh full screen
        imgAvatar.setOnClickListener(v -> {
            if (userId != null) {
                ImageUtils.loadAvatarBitmap(userId, this, bitmap -> {
                    View dialogView = LayoutInflater.from(this)
                            .inflate(R.layout.dialog_view_avatar, null);
                    ImageView fullImg = dialogView.findViewById(R.id.imgFullAvatar);
                    fullImg.setImageBitmap(bitmap);

                    AlertDialog dialog = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                            .setView(dialogView)
                            .create();

                    dialogView.setOnClickListener(view -> dialog.dismiss());
                    dialog.show();
                });
            }
        });

        // Bottom Nav
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_setting);
        BottomNavHelper.setupNavigation(bottomNav, this, username, email);

        // Lấy userId
        getUserIdByEmail();

        tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText(username);
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> showChangePasswordDialog());

        findViewById(R.id.btnStatistical).setOnClickListener(v ->
                startActivity(new Intent(this, StatisticalActivity.class)));

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
                        ImageUtils.loadAvatarIntoImageView(userId, SettingActivity.this, imgAvatar);
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

    private void pickImageFromGallery() {
        imagePickerLauncher.launch("image/*");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        } else {
            Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);

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
                Toast.makeText(this, "Vui lòng điền đủ", Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}
