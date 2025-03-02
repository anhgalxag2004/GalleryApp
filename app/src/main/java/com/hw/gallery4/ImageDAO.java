package com.hw.gallery4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ImageDAO {

    private DatabaseHelper dbHelper;

    public ImageDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Fetch image details by address
    public Cursor getImageByAddress(String imgAddress) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(
                DatabaseHelper.TABLE_IMAGE,
                null,
                DatabaseHelper.COLUMN_IMG_ADDRESS + " = ?",
                new String[]{imgAddress},
                null, null, null
        );
    }

    // Update description
    public void updateDescription(String imgAddress, String description) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMG_DESCRIPTION, description);
        db.update(
                DatabaseHelper.TABLE_IMAGE,
                values,
                DatabaseHelper.COLUMN_IMG_ADDRESS + " = ?",
                new String[]{imgAddress}
        );
        db.close();
    }

    // Update rating
    public void updateRating(String imgAddress, int rating) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMG_RATING, rating);
        db.update(
                DatabaseHelper.TABLE_IMAGE,
                values,
                DatabaseHelper.COLUMN_IMG_ADDRESS + " = ?",
                new String[]{imgAddress}
        );
        db.close();
    }

    // Update favorite
    public void updateFavorite(String imgAddress, boolean favorite) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMG_FAVORITE, favorite ? 1 : 0);
        db.update(
                DatabaseHelper.TABLE_IMAGE,
                values,
                DatabaseHelper.COLUMN_IMG_ADDRESS + " = ?",
                new String[]{imgAddress}
        );
        db.close();
    }
}