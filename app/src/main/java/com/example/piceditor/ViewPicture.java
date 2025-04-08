package com.example.piceditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewPicture extends AppCompatActivity {

    ZoomageView image;
    String image_file;
    private ImageView moreButton, backButton, shareIcon;
    private static final int REQUEST_SET_WALLPAPER = 100;
    private static final int REQUEST_SET_LOCKSCREEN = 101;
    private static final String PREFS_NAME = "ImageDescriptions";
    private static final String DESCRIPTION_PREFIX = "desc_";
    private String imageDescription = "";

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

        // Load saved description
        loadDescription();

        if (file.exists()) {
            Glide.with(this).load(image_file).into(image);
        }

        goBack();
        setMoreButton();
        setupShareButton();
    }

    private void loadDescription() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Use file path as key to ensure uniqueness
        String key = DESCRIPTION_PREFIX + image_file.hashCode();
        imageDescription = prefs.getString(key, "");
    }

    private void saveDescription() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String key = DESCRIPTION_PREFIX + image_file.hashCode();
        editor.putString(key, imageDescription);
        editor.apply();
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
                            showAddDescriptionDialog();
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
                            showImageDetails();
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

    private void showImageDetails() {
        File file = new File(image_file);
        if (!file.exists()) {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Image Details");

        View view = getLayoutInflater().inflate(R.layout.dialog_image_details, null);
        builder.setView(view);

        TextView tvFileName = view.findViewById(R.id.tvFileName);
        TextView tvFilePath = view.findViewById(R.id.tvFilePath);
        TextView tvFileSize = view.findViewById(R.id.tvFileSize);
        TextView tvDateTaken = view.findViewById(R.id.tvDateTaken);
        TextView tvDescription = view.findViewById(R.id.tvDescription);

        // Set file name
        tvFileName.setText("Name: " + file.getName());

        // Set file path
        tvFilePath.setText("Path: " + file.getAbsolutePath());

        // Set file size
        long fileSize = file.length();
        String sizeString;
        if (fileSize < 1024) {
            sizeString = fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            sizeString = String.format(Locale.getDefault(), "%.1f KB", fileSize / 1024.0);
        } else {
            sizeString = String.format(Locale.getDefault(), "%.1f MB", fileSize / (1024.0 * 1024.0));
        }
        tvFileSize.setText("Size: " + sizeString);

        // Set last modified date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tvDateTaken.setText("Date Taken: " + sdf.format(new Date(file.lastModified())));

        // Set description
        tvDescription.setText("Description: " + (imageDescription.isEmpty() ? "No description" : imageDescription));

        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showAddDescriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Description");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_description, null);
        builder.setView(view);

        EditText etDescription = view.findViewById(R.id.etDescription);
        etDescription.setText(imageDescription);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageDescription = etDescription.getText().toString();
                saveDescription(); // Lưu mô tả vào SharedPreferences
                Toast.makeText(ViewPicture.this, "Description saved", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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
