package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.adapters.ConversationAdapter;
import vn.edu.tlu.dinhcaothang.ezilish.models.Conversation;
import vn.edu.tlu.dinhcaothang.ezilish.utils.BottomNavHelper;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView rvConversations;
    private List<Conversation> conversationList;
    private ConversationAdapter adapter;
    private String currentUserEmail, currentUserId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Lấy email đã truyền từ Intent
        currentUserEmail = getIntent().getStringExtra("email");

        // Khởi tạo view
        btnBack = findViewById(R.id.btnBack);
        rvConversations = findViewById(R.id.rvConversations);
        rvConversations.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách và adapter
        conversationList = new ArrayList<>();
        adapter = new ConversationAdapter(this, conversationList, this::openChatWithUser);
        rvConversations.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        findCurrentUserId();

        // Gán bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_conversation); // Đánh dấu Home đang được chọn

        // Cài đặt menu
        BottomNavHelper.setupNavigation(bottomNav, this, getIntent().getStringExtra("username"), getIntent().getStringExtra("email"));
    }

    private void findCurrentUserId() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("email").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            currentUserId = userSnap.getKey();
                            loadMatchedUsers();
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadMatchedUsers() {
        DatabaseReference matchedRef = FirebaseDatabase.getInstance()
                .getReference("users").child(currentUserId).child("matchedUsers");

        matchedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationList.clear();
                for (DataSnapshot matchSnap : snapshot.getChildren()) {
                    String matchedUserId = matchSnap.getKey();
                    fetchMatchedUserInfo(matchedUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchMatchedUserInfo(String matchedUserId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(matchedUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);

                String chatPath = generateChatId(currentUserId, matchedUserId);

                DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("messages").child(chatPath);
                chatRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot chatSnap) {
                        String lastMessage = "Chưa có tin nhắn";
                        for (DataSnapshot msg : chatSnap.getChildren()) {
                            String text = msg.child("text").getValue(String.class);
                            if (text != null) lastMessage = text;
                        }

                        conversationList.add(new Conversation(matchedUserId, username, lastMessage));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void openChatWithUser(Conversation conversation) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("currentUserId", currentUserId);
        intent.putExtra("receiverId", conversation.getUserId()); // Phải là receiverId như ChatActivity
        startActivity(intent);
    }

    private String generateChatId(String id1, String id2) {
        return id1.compareTo(id2) < 0 ? id1 + "_" + id2 : id2 + "_" + id1;
    }
}
