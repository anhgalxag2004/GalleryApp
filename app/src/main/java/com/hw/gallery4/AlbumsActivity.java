package com.hw.gallery4;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlbumsActivity extends AppCompatActivity {

    private AlbumDAO albumDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        // Initialize AlbumDAO
        albumDAO = new AlbumDAO(this);

        // Display all albums
        displayAlbums();
    }

    private void displayAlbums() {
        // Get all albums from the database
        Cursor cursor = albumDAO.getAllAlbums();

        // Create a StringBuilder to hold the album details
        StringBuilder albumsBuilder = new StringBuilder();

        // Loop through the cursor and append album details to the StringBuilder
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_DESCRIPTION));

                albumsBuilder.append("Name: ").append(name).append("\n");
                albumsBuilder.append("Description: ").append(description).append("\n\n");
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            albumsBuilder.append("No albums found.");
        }

        // Display the album details in a TextView
        TextView tvAlbums = findViewById(R.id.tvAlbums);
        tvAlbums.setText(albumsBuilder.toString());
    }
}