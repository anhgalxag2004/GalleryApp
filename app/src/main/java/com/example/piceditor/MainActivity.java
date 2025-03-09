package com.example.piceditor;

import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.os.Environment.MEDIA_MOUNTED;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    RecyclerView recycler;
    ArrayList<String> images;
    TextView allAlbumsTextView, allPhotosTextView;

    GalleryAdapter adapter;
    GridLayoutManager manager;
    private ImageView moreButton;
    TextView totalimages;

    EditText searchText;

    // ActivityResultLauncher để yêu cầu từng quyền riêng biệt
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Quyền đã được cấp, kiểm tra và yêu cầu quyền tiếp theo (nếu cần)
                    checkPermissions();
                } else {
                    // Quyền bị từ chối, hiển thị thông báo
                    Toast.makeText(this, "Permission denied. Some features may not work.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.gallery_recycler);
        images = new ArrayList<>();
        adapter = new GalleryAdapter(this, images);
        manager = new GridLayoutManager(this, 3);
        totalimages = findViewById(R.id.gallery_total_images);
        moreButton = findViewById(R.id.more);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(manager);

        allPhotosTextView = findViewById(R.id.textView3); // All Photos
        allAlbumsTextView = findViewById(R.id.more_txt); // All Albums
        allAlbumsTextView.setAlpha(0.5f);

        changeToAlbums();
        setMoreButton();
        checkPermissions();
    }

    private void changeToAlbums() {
        allAlbumsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllAlbumsActivity.class);
            startActivity(intent);
        });
    }

    private void setMoreButton() {
        moreButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.more_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.sync_cloud) {
                    Toast.makeText(MainActivity.this, "Đồng bộ ảnh với Cloud", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.create_slideshow) {
                    Intent intent = new Intent(MainActivity.this, CreateSlideShowActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.sort_by_name) {
                    Toast.makeText(MainActivity.this, "Xếp theo tên", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.sort_by_date) {
                    Toast.makeText(MainActivity.this, "Xếp theo ngày chụp", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.theme_dark) {
                    Toast.makeText(MainActivity.this, "Tùy chỉnh giao diện tối", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.theme_light) {
                    Toast.makeText(MainActivity.this, "Tùy chỉnh giao diện sáng", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    private void checkPermissions() {
        // Kiểm tra từng quyền một
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền CAMERA
            requestPermissionLauncher.launch(CAMERA);
        } else if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền READ_EXTERNAL_STORAGE
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);
        } else {
            // Tất cả quyền đã được cấp, tải ảnh
            loadImages();
        }
    }

    private void loadImages() {
        boolean SDCard = Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if (SDCard) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, order);
            if (cursor != null) {
                int count = cursor.getCount();
                totalimages.setText("Total items: " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToPosition(i);
                    int columnindex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    images.add(cursor.getString(columnindex));
                }

                recycler.getAdapter().notifyDataSetChanged();
                cursor.close();
            }
        }
    }
}
