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

import java.util.Map;

import vn.edu.tlu.dinhcaothang.ezilish.R;

public class FindCompanionActivity extends AppCompatActivity {
    private FrameLayout cardContainer;
    private ImageButton btnBack;
    private FusedLocationProviderClient fusedLocationClient;
    private double myLat, myLng;
    private String currentUserEmail = "";

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

        currentUserEmail = getIntent().getStringExtra("email"); // 🟢 Lấy email người dùng đang đăng nhập

        btnBack = findViewById(R.id.btnBack);
        cardContainer = findViewById(R.id.cardContainer);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnBack.setOnClickListener(v -> onBackPressed());

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

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Map<String, Object> userData = (Map<String, Object>) userSnapshot.getValue();

                    if (userData == null || userData.get("email") == null || userData.get("location") == null)
                        continue;

                    String email = userData.get("email").toString();
                    if (email.equalsIgnoreCase(currentUserEmail)) {
                        continue; // ❌ BỎ QUA NGƯỜI DÙNG HIỆN TẠI
                    }

                    Map<String, Object> locationMap = (Map<String, Object>) userData.get("location");

                    double lat = Double.parseDouble(locationMap.get("latitude").toString());
                    double lng = Double.parseDouble(locationMap.get("longitude").toString());

                    double distance = calculateDistance(myLat, myLng, lat, lng);

                    if (distance <= 50.0) {
                        addUserCard(userData, distance);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addUserCard(Map<String, Object> userData, double distance) {
        View card = LayoutInflater.from(this).inflate(R.layout.card_item, cardContainer, false);

        TextView tvNameAge = card.findViewById(R.id.user_name_age);
        TextView tvResidence = card.findViewById(R.id.user_residence);
        TextView tvDistance = card.findViewById(R.id.user_distance);
        ImageView imageView = card.findViewById(R.id.user_image);

        tvNameAge.setText(userData.get("username") + ", 20"); // giả định tuổi
        tvResidence.setText("Hà Nội"); // nếu có trường "residence" thì dùng nó
        tvDistance.setText(distance + " KM");

        cardContainer.addView(card);
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
