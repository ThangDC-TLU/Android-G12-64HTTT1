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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.*;

import java.util.*;

import vn.edu.tlu.dinhcaothang.ezilish.R;

public class FindCompanionActivity extends AppCompatActivity {
    private FrameLayout cardContainer;
    private ImageButton btnBack, btnDislike, btnLike, btnRefresh;
    private FusedLocationProviderClient fusedLocationClient;
    private double myLat, myLng;
    private String currentUserEmail = "";
    private String currentUserId = "";

    private final List<Map<String, Object>> nearbyUsers = new ArrayList<>();
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
        btnLike.setOnClickListener(v -> handleLikeAction());
        btnRefresh.setOnClickListener(v -> requestLocationAndLoadUsers());

        findCurrentUserIdByEmail(currentUserEmail, this::requestLocationAndLoadUsers);
    }

    private void findCurrentUserIdByEmail(String email, Runnable onFound) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailInDb = String.valueOf(userSnapshot.child("email").getValue());
                    if (emailInDb.equalsIgnoreCase(email)) {
                        currentUserId = userSnapshot.getKey();
                        onFound.run();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void requestLocationAndLoadUsers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                        userData.put("id", userSnapshot.getKey());
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

    private void handleLikeAction() {
        if (currentIndex >= nearbyUsers.size() || currentUserId.isEmpty()) return;

        Map<String, Object> likedUser = nearbyUsers.get(currentIndex);
        String likedUserId = likedUser.get("id").toString();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.child(currentUserId).child("likedUsers").child(likedUserId).setValue(true)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) return;

                    usersRef.child(likedUserId).child("likedUsers").child(currentUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        usersRef.child(currentUserId).child("matchedUsers").child(likedUserId).setValue(true);
                                        usersRef.child(likedUserId).child("matchedUsers").child(currentUserId).setValue(true);
                                        showMatchDialog("Bạn", likedUser.get("username").toString());
                                    } else {
                                        showNextUser();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                });
    }

    private void showMatchDialog(String currentUser, String matchedUser) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.match_dialog, null);

        TextView title = dialogView.findViewById(R.id.match_title);
        ImageView currentImg = dialogView.findViewById(R.id.current_user_image);
        ImageView otherImg = dialogView.findViewById(R.id.other_user_image);

        title.setText("Bạn và " + matchedUser + " đã match thành công!");
        currentImg.setImageResource(R.drawable.img_profile);
        otherImg.setImageResource(R.drawable.img_profile);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.send_message_button).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.keep_swiping_button).setOnClickListener(v -> {
            dialog.dismiss();
            showNextUser();
        });

        dialog.show();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }
}