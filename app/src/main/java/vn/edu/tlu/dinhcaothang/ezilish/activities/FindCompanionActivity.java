package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.tlu.dinhcaothang.ezilish.R;

public class FindCompanionActivity extends AppCompatActivity {
    private FrameLayout cardContainer;
    private ImageButton btnBack, btnDislike, btnLike, btnRefresh;
    private FusedLocationProviderClient fusedLocationClient;
    private double myLat, myLng;
    private String currentUserEmail = "";

    private List<Map<String, Object>> nearbyUsers = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_companion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        // Lấy email người dùng từ Intent
        currentUserEmail = getIntent().getStringExtra("email");

        // Ánh xạ view
        btnBack = findViewById(R.id.btnBack);
        cardContainer = findViewById(R.id.cardContainer);
        btnDislike = findViewById(R.id.btnDislike);
        btnLike = findViewById(R.id.btnLike);
        btnRefresh = findViewById(R.id.btnRefresh);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnDislike.setOnClickListener(v -> showNextUser());
        btnLike.setOnClickListener(v -> {
            // TODO: xử lý hành động Like (ví dụ lưu vào Firebase)
            showNextUser();
        });

        btnRefresh.setOnClickListener(v -> {
            requestLocationAndLoadUsers();
        });

        requestLocationAndLoadUsers();
    }

    private void requestLocationAndLoadUsers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                myLat = location.getLatitude();
                myLng = location.getLongitude();
                loadNearbyUsers();
            }
        });
    }

    private void loadNearbyUsers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardContainer.removeAllViews();
                nearbyUsers.clear();
                currentIndex = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Map<String, Object> userData = (Map<String, Object>) userSnapshot.getValue();

                    if (userData == null || userData.get("email") == null || userData.get("location") == null)
                        continue;

                    String email = userData.get("email").toString();
                    if (email.equalsIgnoreCase(currentUserEmail)) continue;

                    Map<String, Object> locationMap = (Map<String, Object>) userData.get("location");
                    double lat = Double.parseDouble(locationMap.get("latitude").toString());
                    double lng = Double.parseDouble(locationMap.get("longitude").toString());
                    double distance = calculateDistance(myLat, myLng, lat, lng);

                    if (distance <= 50.0) {
                        userData.put("distance", distance);
                        nearbyUsers.add(userData);
                    }
                }

                if (!nearbyUsers.isEmpty()) {
                    showUserCard(currentIndex);
                } else {
                    showNoUsersMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showUserCard(int index) {
        if (index >= nearbyUsers.size()) {
            showNoUsersMessage();
            return;
        }

        cardContainer.removeAllViews();

        Map<String, Object> userData = nearbyUsers.get(index);
        double distance = Double.parseDouble(userData.get("distance").toString());

        View card = LayoutInflater.from(this).inflate(R.layout.card_item, cardContainer, false);

        TextView tvNameAge = card.findViewById(R.id.user_name_age);
        TextView tvResidence = card.findViewById(R.id.user_residence);
        TextView tvDistance = card.findViewById(R.id.user_distance);
        ImageView imageView = card.findViewById(R.id.user_image);

        tvNameAge.setText(userData.get("username") + ", 20");
        tvResidence.setText("Hà Nội");
        tvDistance.setText(distance + " KM");

        // TODO: Load ảnh đại diện nếu có URL trong userData

        cardContainer.addView(card);
    }

    private void showNextUser() {
        currentIndex++;
        showUserCard(currentIndex);
    }

    private void showNoUsersMessage() {
        cardContainer.removeAllViews();
        TextView message = new TextView(this);
        message.setText("Không còn người dùng nào gần bạn.");
        message.setPadding(40, 40, 40, 40);
        cardContainer.addView(message);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }
}
