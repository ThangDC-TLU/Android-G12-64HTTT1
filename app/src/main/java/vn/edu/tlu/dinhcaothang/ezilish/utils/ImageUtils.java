package vn.edu.tlu.dinhcaothang.ezilish.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {

    public interface AvatarLoadCallback {
        void onAvatarLoaded(Bitmap bitmap);
    }

    public static void loadAvatarBase64(String userId, Context context, ImageView imageView) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("avatarBase64");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String base64 = snapshot.getValue(String.class);
                if (base64 != null && !base64.isEmpty()) {
                    byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void loadAvatarBase64(String userId, Context context, AvatarLoadCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("avatarBase64");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String base64 = snapshot.getValue(String.class);
                if (base64 != null && !base64.isEmpty()) {
                    byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    callback.onAvatarLoaded(bitmap);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void uploadAvatarBase64(Uri uri, String userId, Context context, ImageView imgAvatar) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            String base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("avatarBase64");
            ref.setValue(base64).addOnSuccessListener(unused -> {
                imgAvatar.setImageBitmap(bitmap);
                Toast.makeText(context, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getBase64FromDrawable(Context context, int drawableResId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
