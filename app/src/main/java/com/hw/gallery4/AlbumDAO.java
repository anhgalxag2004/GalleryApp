package com.hw.gallery4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlbumDAO extends DatabaseHelper {
    public AlbumDAO(Context context) {
        super(context); // Call the parent constructor
    }

    public long insertAlbum(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ALBUM_NAME, name);
        values.put(DatabaseHelper.COLUMN_ALBUM_DESCRIPTION, description);

        // Insert the new row
        long id = db.insert(DatabaseHelper.TABLE_ALBUM, null, values);
        db.close();
        return id;
    }

    public Cursor getAllAlbums() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_ALBUM, null, null, null, null, null, null);
    }

    public long insertAlbumImage(int albumId, String imgAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert into Image table if the image address doesn't exist
        ContentValues imageValues = new ContentValues();
        imageValues.put(DatabaseHelper.COLUMN_IMG_ADDRESS, imgAddress);
        imageValues.put(DatabaseHelper.COLUMN_IMG_DESCRIPTION, ""); // Default description
        imageValues.put(DatabaseHelper.COLUMN_IMG_RATING, 0); // Default rating
        imageValues.put(DatabaseHelper.COLUMN_IMG_FAVORITE, 0); // Default favorite (false)
        db.insertWithOnConflict(
                DatabaseHelper.TABLE_IMAGE,
                null,
                imageValues,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        // Insert into AlbumImg table
        ContentValues albumImgValues = new ContentValues();
        albumImgValues.put(DatabaseHelper.COLUMN_ALBUM_IMG_ALBUM_ID, albumId);
        albumImgValues.put(DatabaseHelper.COLUMN_ALBUM_IMG_IMG_ADDRESS, imgAddress);
        long id = db.insert(DatabaseHelper.TABLE_ALBUM_IMG, null, albumImgValues);

        db.close();
        return id;
    }

    public Cursor getAllAlbumImg() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(DatabaseHelper.TABLE_ALBUM_IMG, null, null, null, null, null, null);
    }

    public String getAlbumNameById(int albumId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String albumName = "";

        // Query the Album table to get the album name
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ALBUM,
                new String[]{DatabaseHelper.COLUMN_ALBUM_NAME},
                DatabaseHelper.COLUMN_ALBUM_ID + " = ?",
                new String[]{String.valueOf(albumId)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            albumName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_NAME));
            cursor.close();
        }

        return albumName;
    }

    public long insertAutoScanFolder(String folderAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FLD_ADDRESS, folderAddress);

        long id = db.insert(DatabaseHelper.TABLE_AUTO_SCAN_FLD, null, values);
        db.close();
        return id;
    }
}