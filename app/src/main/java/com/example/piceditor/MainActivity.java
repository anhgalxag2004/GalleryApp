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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int SORT_BY_DATE = 0;
    private static final int SORT_BY_NAME = 1;
    private int currentSortMode = SORT_BY_DATE;
    RecyclerView recycler;
    ArrayList<String> images;
    TextView allAlbumsTextView, allPhotosTextView;

    GalleryAdapter adapter;
    GridLayoutManager manager;
    private ImageView moreButton;
    TextView totalimages;

    EditText searchText;

    private boolean isSortedByName = false;

    // ActivityResultLauncher để yêu cầu từng quyền riêng biệt
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Quyền đã được cấp, kiểm tra và yêu cầu quyền tiếp theo (nếu cần)
                    checkPermissions();
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
        loadImages();
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
            MenuItem sortByNameItem = popupMenu.getMenu().findItem(R.id.sort_by_name);
            MenuItem sortByDateItem = popupMenu.getMenu().findItem(R.id.sort_by_date);
            if (currentSortMode == SORT_BY_NAME) {
                sortByNameItem.setTitle("Đang xếp theo tên ✓");
                sortByDateItem.setTitle("Xếp theo ngày chụp");
            } else {
                sortByNameItem.setTitle("Xếp theo tên");
                sortByDateItem.setTitle("Đang xếp theo ngày ✓");
            }

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
                    currentSortMode = SORT_BY_NAME;
                    loadImages();
                    Toast.makeText(MainActivity.this, "Đã xếp theo tên", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.sort_by_date) {
                    currentSortMode = SORT_BY_DATE;
                    loadImages();
                    Toast.makeText(MainActivity.this, "Đã xếp theo ngày chụp", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.theme_dark) {
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_YES);
                    return true;
                } else if (id == R.id.theme_light) {
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_NO);
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
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN};
            String order;
            switch (currentSortMode) {
                case SORT_BY_NAME:
                    order = MediaStore.Images.Media.DISPLAY_NAME + " ASC";
                    break;
                case SORT_BY_DATE:
                default:
                    order = MediaStore.Images.Media.DATE_TAKEN + " DESC";
                    break;
            }

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, order);
            images.clear();
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

    private void setAppTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        // Recreate the activity to apply the theme
        recreate();
    }

    private void showThemeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Theme");

        String[] themes = {"System Default", "Light Mode", "Dark Mode"};

        builder.setSingleChoiceItems(themes, getCurrentThemePreference(), (dialog, which) -> {
            switch (which) {
                case 0:
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case 1:
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case 2:
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
            }
            dialog.dismiss();
        });

        builder.show();
    }

    private int getCurrentThemePreference() {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                return 1;
            case AppCompatDelegate.MODE_NIGHT_YES:
                return 2;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                return 0;
        }
    }
}
