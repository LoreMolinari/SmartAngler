package com.smartangler.smartangler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SmartAnglerSessionHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartAnglerDatabase";

    public static final String PHOTO_TABLE_NAME = "photos";
    public static final String KEY_PHOTO_ID = "id";
    public static final String KEY_PHOTO_TITLE = "title";
    public static final String KEY_PHOTO_DESCRIPTION = "description";
    public static final String KEY_PHOTO_IMAGE = "image";
    public static final String KEY_PHOTO_DATE = "date";
    public static final String KEY_PHOTO_LOCATION = "location";
    public static final String KEY_PHOTO_SESSION_ID = "session_id";

    public static final String SESSION_TABLE_NAME = "sessions";
    public static final String KEY_SESSION_ID = "id";
    public static final String KEY_SESSION_DATE = "date";
    public static final String KEY_SESSION_LOCATION = "location";
    public static final String KEY_SESSION_DURATION = "duration";
    public static final String KEY_SESSION_FISH_CAUGHT = "fish_caught";

    public static final String CREATE_PHOTO_TABLE_SQL = "CREATE TABLE " + PHOTO_TABLE_NAME + " (" +
            KEY_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_PHOTO_TITLE + " TEXT, " +
            KEY_PHOTO_DESCRIPTION + " TEXT, " +
            KEY_PHOTO_IMAGE + " BLOB, " +
            KEY_PHOTO_DATE + " TEXT, " +
            KEY_PHOTO_LOCATION + " TEXT, " +
            KEY_PHOTO_SESSION_ID + " TEXT);";

    public static final String CREATE_SESSION_TABLE_SQL = "CREATE TABLE " + SESSION_TABLE_NAME + " (" +
            KEY_SESSION_ID + " TEXT PRIMARY KEY, " +
            KEY_SESSION_DATE + " TEXT, " +
            KEY_SESSION_LOCATION + " TEXT, " +
            KEY_SESSION_DURATION + " INTEGER, " +
            KEY_SESSION_FISH_CAUGHT + " INTEGER); ";

    public SmartAnglerSessionHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PHOTO_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_SESSION_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public static void addPhoto(Context context, String title, String description, byte[] image, String date, String location, String sessionId) {
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHOTO_TITLE, title);
        values.put(KEY_PHOTO_DESCRIPTION, description);
        values.put(KEY_PHOTO_IMAGE, image);
        values.put(KEY_PHOTO_DATE, date);
        values.put(KEY_PHOTO_LOCATION, location);
        values.put(KEY_PHOTO_SESSION_ID, sessionId);

        database.insert(PHOTO_TABLE_NAME, null, values);
        database.close();
    }

    public static List<Object[]> loadPhotosForSession(Context context, String sessionId) {
        List<Object[]> photos = new ArrayList<>();
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = KEY_PHOTO_SESSION_ID + " = ?";
        String[] selectionArgs = {sessionId};
        Cursor cursor = database.query(PHOTO_TABLE_NAME, null, selection, selectionArgs, null, null, KEY_PHOTO_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_PHOTO_ID);
                int titleIndex = cursor.getColumnIndex(KEY_PHOTO_TITLE);
                int descIndex = cursor.getColumnIndex(KEY_PHOTO_DESCRIPTION);
                int imageIndex = cursor.getColumnIndex(KEY_PHOTO_IMAGE);
                int dateIndex = cursor.getColumnIndex(KEY_PHOTO_DATE);
                int locationIndex = cursor.getColumnIndex(KEY_PHOTO_LOCATION);

                if (idIndex != -1 && titleIndex != -1 && descIndex != -1 &&
                        imageIndex != -1 && dateIndex != -1 && locationIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String description = cursor.getString(descIndex);
                    byte[] image = cursor.getBlob(imageIndex);
                    String date = cursor.getString(dateIndex);
                    String location = cursor.getString(locationIndex);

                    photos.add(new Object[]{id, title, description, image, date, location});
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        database.close();
        return photos;
    }

    public static void addSession(Context context, String id, String date, String location, int duration, int fishCaught, String notes) {
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, id);
        values.put(KEY_SESSION_DATE, date);
        values.put(KEY_SESSION_LOCATION, location);
        values.put(KEY_SESSION_DURATION, duration);
        values.put(KEY_SESSION_FISH_CAUGHT, fishCaught);

        database.insert(SESSION_TABLE_NAME, null, values);
        database.close();
    }

    public static List<Object[]> loadAllSessions(Context context) {
        List<Object[]> sessions = new ArrayList<>();
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(SESSION_TABLE_NAME, null, null, null, null, null, KEY_SESSION_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_SESSION_ID);
                int dateIndex = cursor.getColumnIndex(KEY_SESSION_DATE);
                int locationIndex = cursor.getColumnIndex(KEY_SESSION_LOCATION);
                int durationIndex = cursor.getColumnIndex(KEY_SESSION_DURATION);
                int fishCaughtIndex = cursor.getColumnIndex(KEY_SESSION_FISH_CAUGHT);

                if (idIndex != -1 && dateIndex != -1 && locationIndex != -1 &&
                        durationIndex != -1 && fishCaughtIndex != -1) {
                    String id = cursor.getString(idIndex);
                    String date = cursor.getString(dateIndex);
                    String location = cursor.getString(locationIndex);
                    int duration = cursor.getInt(durationIndex);
                    int fishCaught = cursor.getInt(fishCaughtIndex);

                    sessions.add(new Object[]{id, date, location, duration, fishCaught});
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        database.close();
        return sessions;
    }

    public static void deleteAllData(Context context) {
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int deletedPhotos = database.delete(PHOTO_TABLE_NAME, null, null);
        int deletedSessions = database.delete(SESSION_TABLE_NAME, null, null);
        database.close();

        Log.d("SmartAngler", "Deleted " + deletedPhotos + " photos and " + deletedSessions + " sessions.");
    }
}