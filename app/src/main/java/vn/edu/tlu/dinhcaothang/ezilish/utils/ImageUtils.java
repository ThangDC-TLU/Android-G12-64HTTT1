package vn.edu.tlu.dinhcaothang.ezilish.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {

    public interface AvatarLoadCallback {
        void onAvatarLoaded(Bitmap bitmap);
    }

    // 🔹 LOAD AVATAR
    public static int loadAvatarIntoImageView(String userId, Context context, ImageView imageView) {
        loadAvatarBitmap(userId, context, new AvatarLoadCallback() {
            @Override
            public void onAvatarLoaded(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
        return 0;
    }

    public static void loadAvatarBitmap(String userId, Context context, AvatarLoadCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("avatarBase64");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String base64 = snapshot.getValue(String.class);
                if (base64 != null && !base64.isEmpty()) {
                    try {
                        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        callback.onAvatarLoaded(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(context, "Lỗi giải mã ảnh", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Lỗi tải ảnh đại diện", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 UPLOAD AVATAR
    public static void uploadAvatarBase64(Uri imageUri, String userId, Context context, ImageView imgAvatarView) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            String base64 = bitmapToBase64(bitmap);
            if (base64 == null) {
                Toast.makeText(context, "Không thể chuyển ảnh sang Base64", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("avatarBase64");
            ref.setValue(base64).addOnSuccessListener(unused -> {
                imgAvatarView.setImageBitmap(bitmap);
                Toast.makeText(context, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            });

        } catch (Exception e) {
            Toast.makeText(context, "Lỗi chọn ảnh", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // 🔹 CONVERT TO BASE64
    public static String bitmapToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBase64FromDrawable(Context context, int drawableResId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);
            return bitmapToBase64(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
