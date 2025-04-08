package com.example.piceditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.IOException;

public class ViewPicture extends AppCompatActivity {

    ZoomageView image;
    String image_file;
    private ImageView moreButton, backButton, shareIcon;
    private static final int REQUEST_SET_WALLPAPER = 100;
    private static final int REQUEST_SET_LOCKSCREEN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        image_file = getIntent().getStringExtra("image_file");
        File file = new File(image_file);
        image = findViewById(R.id.image);
        moreButton = findViewById(R.id.more_icon);
        backButton = findViewById(R.id.back_icon);
        shareIcon = findViewById(R.id.share_icon);

        if (file.exists()) {
            Glide.with(this).load(image_file).into(image);
        }

        goBack();
        setMoreButton();
        setupShareButton();
    }

    private void setMoreButton()
    {
        // Xử lý khi nhấn vào nút menu (More)
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo PopupMenu
                PopupMenu popupMenu = new PopupMenu(ViewPicture.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.detail_image_menu, popupMenu.getMenu());

                // Xử lý sự kiện khi chọn một mục trong PopupMenu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.add_description) {
                            Toast.makeText(ViewPicture.this, "Thêm mô tả", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.add_to_album) {
                            Toast.makeText(ViewPicture.this, "Thêm vào album", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.secure_image) {
                            Toast.makeText(ViewPicture.this, "Ẩn và bảo mật ảnh", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.main_screen) {
                            setAsWallpaper(WallpaperManager.FLAG_SYSTEM);
                            return true;
                        } else if (id == R.id.lock_screen) {
                            setAsWallpaper(WallpaperManager.FLAG_LOCK);
                            return true;
                        } else if (id == R.id.details) {
                            Toast.makeText(ViewPicture.this, "Chi tiết ảnh", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });

                // Hiển thị PopupMenu
                popupMenu.show();
            }
        });
    }

    private void setAsWallpaper(int which) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            Bitmap bitmap = BitmapFactory.decodeFile(image_file);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // For Android 7.0+ (API 24+) which supports separate lockscreen wallpapers
                wallpaperManager.setBitmap(bitmap, null, true, which);
                String message = (which == WallpaperManager.FLAG_SYSTEM) ?
                        "Đã đặt làm màn hình chính" : "Đã đặt làm màn hình khóa";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                // For older versions, can only set main wallpaper
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(this, "Đã đặt làm hình nền", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi đặt hình nền: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void goBack()
    {
        backButton.setOnClickListener(v -> {
            // Intent to navigate to AllAlbumsActivity
            Intent intent = new Intent(ViewPicture.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void setupShareButton() {
        shareIcon.setOnClickListener(v -> {
            // Get the image file
            File imageFile = new File(image_file);

            // Check if file exists
            if (!imageFile.exists()) {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");

            // For Android 7.0 and above, we need to use FileProvider
            Uri imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider", // Make sure this matches your provider authority
                    imageFile
            );

            // Grant temporary read permission to the content URI
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            // Add optional text
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this image!");

            // Start the share activity
            startActivity(Intent.createChooser(shareIntent, "Share image via"));
        });
    }
}