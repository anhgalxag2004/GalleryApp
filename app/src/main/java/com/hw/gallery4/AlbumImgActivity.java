package com.hw.gallery4;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

public class AlbumImgActivity extends AppCompatActivity {

    private AlbumDAO albumDAO;
    private RecyclerView rvAlbumImg;
    private AlbumImgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_img);

        // Initialize AlbumDAO
        albumDAO = new AlbumDAO(this);

        // Find the RecyclerView
        rvAlbumImg = findViewById(R.id.rvAlbumImg);

        // Set up the RecyclerView
        rvAlbumImg.setLayoutManager(new LinearLayoutManager(this));

        // Get all rows from the AlbumImg table
        Cursor cursor = albumDAO.getAllAlbumImg();

        // Create and set the adapter
        adapter = new AlbumImgAdapter(this, cursor);
        rvAlbumImg.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the cursor to avoid memory leaks
        if (adapter != null) {
            adapter.closeCursor();
        }
    }

    /*private void displayAlbumImg() {
        // Get all rows from the AlbumImg table
        Cursor cursor = albumDAO.getAllAlbumImg();

        // Create a StringBuilder to hold the table contents
        StringBuilder albumImgBuilder = new StringBuilder();

        // Loop through the cursor and append data to the StringBuilder
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_IMG_ALBUM_ID));
                String imgAddress = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_IMG_IMG_ADDRESS));

                albumImgBuilder.append("Album ID: ").append(albumId).append("\n");
                albumImgBuilder.append("Image Address: ").append(imgAddress).append("\n\n");
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            albumImgBuilder.append("No data found in AlbumImg table.");
        }

        // Display the data in a TextView
        TextView tvAlbumImg = findViewById(R.id.tvAlbumImg);
        tvAlbumImg.setText(albumImgBuilder.toString());
    }*/
}
