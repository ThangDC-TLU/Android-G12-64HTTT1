package vn.edu.tlu.dinhcaothang.ezilish.utils;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.activities.ChatAiActivity;
import vn.edu.tlu.dinhcaothang.ezilish.activities.ConversationActivity;
import vn.edu.tlu.dinhcaothang.ezilish.activities.HomeActivity;
import vn.edu.tlu.dinhcaothang.ezilish.activities.SettingActivity;

public class BottomNavHelper {

    public static void setupNavigation(BottomNavigationView bottomNav, Activity activity, String username, String email) {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_home && !(activity instanceof HomeActivity)) {
                intent = new Intent(activity, HomeActivity.class);
            } else if (itemId == R.id.nav_chat_ai && !(activity instanceof ChatAiActivity)) {
                intent = new Intent(activity, ChatAiActivity.class);
            } else if (itemId == R.id.nav_conversation && !(activity instanceof ConversationActivity)) {
                intent = new Intent(activity, ConversationActivity.class);
            } else if (itemId == R.id.nav_setting && !(activity instanceof SettingActivity)) {
                intent = new Intent(activity, SettingActivity.class);
            }

            if (intent != null) {
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0); // No animation
                return true;
            }

            return true;
        });
    }
}
