package com.hw.gallery4;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

public class AlbumImgAdapter extends RecyclerView.Adapter<AlbumImgAdapter.AlbumImgViewHolder> {

    private Context context;
    private Cursor cursor;
    private AlbumDAO albumDAO;
    private ImageDAO imageDAO;

    public AlbumImgAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.albumDAO = new AlbumDAO(context);
        this.imageDAO = new ImageDAO(context);
    }

    @NonNull
    @Override
    public AlbumImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album_img, parent, false);
        return new AlbumImgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumImgViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        // Get the album ID and image address from the cursor
        int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_IMG_ALBUM_ID));

        // Get the image address from the cursor
        String imgAddress = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_IMG_IMG_ADDRESS));

        // Fetch the album name using AlbumDAO
        String albumName = albumDAO.getAlbumNameById(albumId);

        // Set the album name in the TextView
        holder.tvAlbumName.setText("Album: " + albumName);

        // Load the image into the ImageView using Glide
        Glide.with(context)
                .load(Uri.parse(imgAddress)) // Load the image from the URI
                .into(holder.ivAlbumImg);

        // Fetch the image details from the Image table
        Cursor imageCursor = imageDAO.getImageByAddress(imgAddress);
        if (imageCursor != null && imageCursor.moveToFirst()) {
            String description = imageCursor.getString(imageCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMG_DESCRIPTION));
            int rating = imageCursor.getInt(imageCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMG_RATING));
            boolean favorite = imageCursor.getInt(imageCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMG_FAVORITE)) == 1;

            // Set click listeners for the buttons
            holder.btnDescription.setOnClickListener(v -> showDescriptionDialog(imgAddress, description));
            holder.btnRating.setOnClickListener(v -> showRatingDialog(imgAddress, rating));
            holder.btnFavorite.setOnClickListener(v -> toggleFavorite(imgAddress, favorite));
        }
        if (imageCursor != null) {
            imageCursor.close();
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // Show a dialog to update the description
    private void showDescriptionDialog(String imgAddress, String currentDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Description");

        // Add an input field
        final EditText input = new EditText(context);
        input.setText(currentDescription);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newDescription = input.getText().toString();
            imageDAO.updateDescription(imgAddress, newDescription);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Show a dialog to update the rating
    private void showRatingDialog(String imgAddress, int currentRating) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Rating");

        // Add an input field
        final EditText input = new EditText(context);
        input.setText(String.valueOf(currentRating));
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            try {
                int newRating = Integer.parseInt(input.getText().toString());
                imageDAO.updateRating(imgAddress, newRating);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid rating", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Toggle the favorite status
    private void toggleFavorite(String imgAddress, boolean currentFavorite) {
        boolean newFavorite = !currentFavorite;
        imageDAO.updateFavorite(imgAddress, newFavorite);
        Toast.makeText(context, "Favorite: " + newFavorite, Toast.LENGTH_SHORT).show();
    }

    public void closeCursor() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static class AlbumImgViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlbumName;
        ImageView ivAlbumImg;
        Button btnDescription, btnRating, btnFavorite;

        public AlbumImgViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
            ivAlbumImg = itemView.findViewById(R.id.ivAlbumImg);
            btnDescription = itemView.findViewById(R.id.btnDescription);
            btnRating = itemView.findViewById(R.id.btnRating);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
