package com.hw.gallery4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private AlbumDAO albumDAO;
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for file picker
    private static final int PICK_FOLDER_REQUEST = 2; // Request code for folder picker
    private List<Integer> selectedAlbumIds = new ArrayList<>(); // List to store selected album IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AlbumDAO
        albumDAO = new AlbumDAO(this); // Initialize here

        // Find the "New Album" button
        Button btnNewAlbum = findViewById(R.id.btnNewAlbum);

        // Set click listener for the "New Album" button
        btnNewAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewAlbumDialog();
            }
        });

        // Find the "Albums" button
        Button btnAlbums = findViewById(R.id.btnAlbums);

        // Set click listener for the "Albums" button
        btnAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch AlbumsActivity
                Intent intent = new Intent(MainActivity.this, AlbumsActivity.class);
                startActivity(intent);
            }
        });

        // Find the "Add Image to Album" button
        Button btnAddImageToAlbum = findViewById(R.id.btnAddImageToAlbum);

        // Set click listener for the "Add Image to Album" button
        btnAddImageToAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlbumSelectionDialog();
            }
        });

        // Find the "Display AlbumImg" button
        Button btnDisplayAlbumImg = findViewById(R.id.btnDisplayAlbumImg);
        btnDisplayAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch AlbumImgActivity
                Intent intent = new Intent(MainActivity.this, AlbumImgActivity.class);
                startActivity(intent);
            }
        });

        // Find the "Add Folder to AutoScan" button
        Button btnAddFolder = findViewById(R.id.btnAddFolder);
        btnAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolderPicker();
            }
        });
    }

    private void showNewAlbumDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_new_album, null);

        // Find the input fields in the dialog
        final EditText etAlbumName = dialogView.findViewById(R.id.etAlbumName);
        final EditText etAlbumDescription = dialogView.findViewById(R.id.etAlbumDescription);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("New Album")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the input values
                        String name = etAlbumName.getText().toString().trim();
                        String description = etAlbumDescription.getText().toString().trim();

                        // Validate input
                        if (name.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Save the album to the database
                        long id = albumDAO.insertAlbum(name, description); // Use albumDAO here

                        // Show a message based on the result
                        if (id != -1) {
                            Toast.makeText(MainActivity.this, "Album created successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to create album", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Show the dialog
        builder.create().show();
    }

    private void showAlbumSelectionDialog() {
        // Get all albums from the database
        Cursor cursor = albumDAO.getAllAlbums();

        // Create a list of album names
        List<String> albumNames = new ArrayList<>();
        final List<Integer> albumIds = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_NAME));
                albumNames.add(name);
                albumIds.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Convert the list to an array for the dialog
        final CharSequence[] albumNamesArray = albumNames.toArray(new CharSequence[0]);

        // Create a boolean array to track selected albums
        final boolean[] checkedAlbums = new boolean[albumNames.size()];

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Albums")
                .setMultiChoiceItems(albumNamesArray, checkedAlbums, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // Track selected albums
                        if (isChecked) {
                            selectedAlbumIds.add(albumIds.get(which));
                        } else {
                            selectedAlbumIds.remove(Integer.valueOf(albumIds.get(which)));
                        }
                    }
                })
                .setPositiveButton("Select Image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open the file picker to select an image
                        openFilePicker();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Show the dialog
        builder.create().show();
    }

    private void openFilePicker() {
        // Create an intent to open the file picker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // Only allow image files
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, PICK_FOLDER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();

            // Get the image file path (or URI)
            String imageAddress = imageUri.toString();

            // Save the image address and selected album IDs to the database
            for (int albumId : selectedAlbumIds) {
                albumDAO.insertAlbumImage(albumId, imageAddress);
            }
            selectedAlbumIds.clear();

            // Show a success message
            Toast.makeText(this, "Image added to selected albums", Toast.LENGTH_SHORT).show();
        } else if (requestCode == PICK_FOLDER_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle folder selection
            Uri folderUri = data.getData();
            String folderAddress = folderUri.toString();

            // Save the folder address to the AutoScanFld table
            long id = albumDAO.insertAutoScanFolder(folderAddress);

            if (id != -1) {
                Toast.makeText(this, "Folder added to AutoScan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add folder", Toast.LENGTH_SHORT).show();
            }
        }
    }
}