package com.example.piceditor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateAlbumActivity extends AppCompatActivity {
    ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        // Lấy dữ liệu từ Intent
        String albumName = getIntent().getStringExtra("album_name");


        if (albumName != null) {
            TextView albumTitle = findViewById(R.id.album_title);
            albumTitle.setText(albumName);
        }

        closeButton = findViewById(R.id.cancel_icon);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}