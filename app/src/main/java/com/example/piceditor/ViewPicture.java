package com.example.piceditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;

public class ViewPicture extends AppCompatActivity {

    ZoomageView image;
    String image_file;
    private ImageView moreButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        image_file = getIntent().getStringExtra("image_file");
        File file = new File(image_file);
        image = findViewById(R.id.image);
        moreButton = findViewById(R.id.more_icon);
        backButton = findViewById(R.id.back_icon);

        if (file.exists()) {
            Glide.with(this).load(image_file).into(image);
        }

        goBack();
        setMoreButton();
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
                        } else if (id == R.id.wallpaper) {
                            Toast.makeText(ViewPicture.this, "Đặt làm hình nền", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (id == R.id.main_screen) {
                            Toast.makeText(ViewPicture.this, "Đặt làm màn hình chính", Toast.LENGTH_SHORT).show();
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

    private void goBack()
    {
        backButton.setOnClickListener(v -> {
            // Intent to navigate to AllAlbumsActivity
            Intent intent = new Intent(ViewPicture.this, MainActivity.class);
            startActivity(intent);
        });
    }
}