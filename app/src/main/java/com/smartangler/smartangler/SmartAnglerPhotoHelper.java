package com.smartangler.smartangler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;


public class SmartAnglerPhotoHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "photoGallery";
    public static final String PHOTO_TABLE_NAME = "photos";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image"; // BLOB per salvare l'immagine
    public static final String KEY_DATE = "date";
    public static final String KEY_LOCATION = "location";

    public static final String CREATE_PHOTO_TABLE_SQL = "CREATE TABLE " + PHOTO_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_IMAGE + " BLOB, " +
            KEY_DATE + " TEXT, " +
            KEY_LOCATION + " TEXT);";

    public SmartAnglerPhotoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PHOTO_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PHOTO_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public static void addPhoto(Context context, String title, String description, byte[] image, String date, String location) {
        SmartAnglerPhotoHelper databaseHelper = new SmartAnglerPhotoHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_IMAGE, image);
        values.put(KEY_DATE, date);
        values.put(KEY_LOCATION, location);

        database.insert(PHOTO_TABLE_NAME, null, values);
        database.close();
    }

    public static List<Object[]> loadAllPhotos(Context context) {
        List<Object[]> photos = new LinkedList<>();
        SmartAnglerPhotoHelper databaseHelper = new SmartAnglerPhotoHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(PHOTO_TABLE_NAME, null, null, null, null, null, KEY_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int titleIndex = cursor.getColumnIndex(KEY_TITLE);
                int descIndex = cursor.getColumnIndex(KEY_DESCRIPTION);
                int imageIndex = cursor.getColumnIndex(KEY_IMAGE);
                int dateIndex = cursor.getColumnIndex(KEY_DATE);
                int locationIndex = cursor.getColumnIndex(KEY_LOCATION);

                if (idIndex == -1 || titleIndex == -1 || descIndex == -1 ||
                        imageIndex == -1 || dateIndex == -1 || locationIndex == -1) {
                    Log.e("Database", "Column not found in cursor");
                    continue;
                }

                int id = cursor.getInt(idIndex);
                String title = cursor.getString(titleIndex);
                String description = cursor.getString(descIndex);
                byte[] image = cursor.getBlob(imageIndex);
                String date = cursor.getString(dateIndex);
                String location = cursor.getString(locationIndex);

            } while (cursor.moveToNext());
            cursor.close();
        }

        database.close();
        return photos;
    }

    public static void deleteAllPhotos(Context context) {
        SmartAnglerPhotoHelper databaseHelper = new SmartAnglerPhotoHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int numberDeletedRecords = database.delete(PHOTO_TABLE_NAME, null, null);
        database.close();

        Log.d("PhotoGallery", "Deleted " + numberDeletedRecords + " records.");
    }
}
