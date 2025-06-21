package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.*;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

import vn.edu.tlu.dinhcaothang.ezilish.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgotPassword;
    private CheckBox cbRememberMe;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private String currentUserId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        loadSavedCredentials();

        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot Password feature not implemented yet", Toast.LENGTH_SHORT).show());

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                checkLogin(email, password);
            }
        });
    }

    private void loadSavedCredentials() {
        boolean isRemembered = sharedPreferences.getBoolean("remember_me", false);
        if (isRemembered) {
            etEmail.setText(sharedPreferences.getString("email", ""));
            etPassword.setText(sharedPreferences.getString("password", ""));
            cbRememberMe.setChecked(true);
        }
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (cbRememberMe.isChecked()) {
            editor.putBoolean("remember_me", true);
            editor.putString("email", email);
            editor.putString("password", password);
        } else {
            editor.clear();
        }
        editor.apply();
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return false;
        }
        return true;
    }

    private void checkLogin(String email, String password) {
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String storedPassword = userSnapshot.child("password").getValue(String.class);
                                String username = userSnapshot.child("username").getValue(String.class);
                                String storedEmail = userSnapshot.child("email").getValue(String.class);

                                if (storedPassword != null && storedPassword.equals(password)) {
                                    saveCredentials(email, password);
                                    currentUserId = userSnapshot.getKey(); // Lưu userId để update vị trí

                                    // Yêu cầu quyền vị trí
                                    if (ContextCompat.checkSelfPermission(LoginActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(LoginActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                LOCATION_PERMISSION_REQUEST_CODE);
                                    } else {
                                        updateUserLocation();
                                    }

                                    // Điều hướng
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("email", storedEmail);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                updateUserLocation();
            } else {
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result != null && result.getLastLocation() != null && currentUserId != null) {
                    Location location = result.getLastLocation();
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    Map<String, Object> locationMap = new HashMap<>();
                    locationMap.put("latitude", lat);
                    locationMap.put("longitude", lng);
                    locationMap.put("updated_at", System.currentTimeMillis());

                    databaseReference.child(currentUserId).child("location").setValue(locationMap);
                }
            }
        }, Looper.getMainLooper());
    }
}
