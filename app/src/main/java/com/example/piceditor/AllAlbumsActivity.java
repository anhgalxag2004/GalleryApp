package com.example.piceditor;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllAlbumsActivity extends AppCompatActivity {

    static int PERMISSION_REQUEST_CODE = 100;
    RecyclerView recycler;
    ArrayList<String> images;
    TextView allAlbumsTextView, allPhotosTextView;

    GalleryAdapter adapter;
    GridLayoutManager manager;
    private ImageView moreButton, addAlbumButton ;
    TextView totalimages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_albums);

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
        allPhotosTextView.setAlpha(0.5f);
        addAlbumButton = findViewById(R.id.add_album);

        addAlbumButton.setOnClickListener(v -> showAddAlbumDialog());
        changeToPhotos();
        setMoreButton();
    }

    private void changeToPhotos()
    {
        allPhotosTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to AllAlbumsActivity
                Intent intent = new Intent(AllAlbumsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setMoreButton()
    {
        // Xử lý khi nhấn vào nút menu (More)
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo PopupMenu
                PopupMenu popupMenu = new PopupMenu(AllAlbumsActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.more_menu, popupMenu.getMenu());

                // Xử lý sự kiện khi chọn một mục trong PopupMenu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.sync_cloud) {
                            Toast.makeText(AllAlbumsActivity.this, "Đồng bộ ảnh với Cloud", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.create_slideshow) {
                            Intent intent = new Intent(AllAlbumsActivity.this, CreateSlideShowActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (id == R.id.sort_by_name) {
                            Toast.makeText(AllAlbumsActivity.this, "Xếp theo tên", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.sort_by_date) {
                            Toast.makeText(AllAlbumsActivity.this, "Xếp theo ngày chụp", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.theme_dark) {
                            Toast.makeText(AllAlbumsActivity.this, "Tùy chỉnh giao diện tối", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.theme_light) {
                            Toast.makeText(AllAlbumsActivity.this, "Tùy chỉnh giao diện sáng", Toast.LENGTH_SHORT).show();
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

    private void showAddAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Album Name");

        // Tạo EditText để nhập tên album
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Nút "Thêm"
        builder.setPositiveButton("Add", (dialog, which) -> {
            String albumName = input.getText().toString().trim();
            if (!albumName.isEmpty()) {
                Intent intent = new Intent(this, CreateAlbumActivity.class);
                intent.putExtra("album_name", albumName);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Assign a name!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút "Hủy"
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Hiển thị dialog
        builder.show();
    }
}