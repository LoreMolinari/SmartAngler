package com.smartangler.smartangler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmartAnglerSessionHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "smartAnglerDatabase";

    public static final String PHOTO_TABLE_NAME = "photos";
    public static final String KEY_PHOTO_ID = "id";
    public static final String KEY_PHOTO_TITLE = "title";
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
    public static final String KEY_SESSION_STEPS = "steps";

    public static final String CREATE_PHOTO_TABLE_SQL = "CREATE TABLE " + PHOTO_TABLE_NAME + " (" +
            KEY_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_PHOTO_TITLE + " TEXT, " +
            KEY_PHOTO_IMAGE + " BLOB, " +
            KEY_PHOTO_DATE + " TEXT, " +
            KEY_PHOTO_LOCATION + " TEXT, " +
            KEY_PHOTO_SESSION_ID + " TEXT);";

    public static final String CREATE_SESSION_TABLE_SQL = "CREATE TABLE " + SESSION_TABLE_NAME + " (" +
            KEY_SESSION_ID + " TEXT PRIMARY KEY, " +
            KEY_SESSION_DATE + " TEXT, " +
            KEY_SESSION_LOCATION + " TEXT, " +
            KEY_SESSION_DURATION + " INTEGER, " +
            KEY_SESSION_STEPS + " INTEGER, " +
            KEY_SESSION_FISH_CAUGHT + " INTEGER); ";

    public SmartAnglerSessionHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_PHOTO_TABLE_SQL);
            sqLiteDatabase.execSQL(CREATE_SESSION_TABLE_SQL);
        } catch (Exception e) {
            Log.e("DBSession", "Error on creation: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            sqLiteDatabase.execSQL("ALTER TABLE " + SESSION_TABLE_NAME + " ADD COLUMN " + KEY_SESSION_STEPS + " INTEGER");
        }
    }
    public static void addPhoto(Context context, String title, byte[] image, String date, String location, String sessionId) {
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHOTO_TITLE, title);
        values.put(KEY_PHOTO_IMAGE, image);
        values.put(KEY_PHOTO_DATE, date);
        values.put(KEY_PHOTO_LOCATION, location);
        values.put(KEY_PHOTO_SESSION_ID, sessionId);
        long result = database.insert(PHOTO_TABLE_NAME, null, values);
        database.close();

        if (result != -1) {
            Log.d("DBSession", "Photo added: " + title);
        } else {
            Log.e("DBSession", "Error on photo: " + title);
        }
    }

    public static List<Object[]> loadPhotosForSession(Context context, String sessionId) {
        List<Object[]> photos = new ArrayList<>();
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String selection = KEY_PHOTO_SESSION_ID + " = ?";
        String[] selectionArgs = {sessionId};
        String[] columns = {KEY_PHOTO_ID, KEY_PHOTO_TITLE, KEY_PHOTO_IMAGE, KEY_PHOTO_DATE, KEY_PHOTO_LOCATION};

        try {
            Cursor cursor = database.query(PHOTO_TABLE_NAME, columns, selection, selectionArgs, null, null, KEY_PHOTO_DATE + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PHOTO_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_TITLE));
                    byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(KEY_PHOTO_IMAGE));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_DATE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHOTO_LOCATION));
                    photos.add(new Object[]{id, title, image, date, location});
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DBSession", "Error on photo: " + e.getMessage());
        } finally {
            database.close();
        }

        Log.d("DBSession", "Photo Loaded: " + photos.size());
        return photos;
    }

    public static boolean addSession(Context context, String id, String date, String location, int duration, int fishCaught, int steps) {
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, id);
        values.put(KEY_SESSION_DATE, date);
        values.put(KEY_SESSION_LOCATION, location);
        values.put(KEY_SESSION_DURATION, duration);
        values.put(KEY_SESSION_FISH_CAUGHT, fishCaught);
        values.put(KEY_SESSION_STEPS, steps);

        try {
            long result = database.insertOrThrow(SESSION_TABLE_NAME, null, values);
            if (result != -1) {
                return true;
            } else {
                Log.e("DBSession", "Error on session: " + id);
                return false;
            }
        } catch (SQLiteException e) {
            Log.e("DBSession", "Error SQLite: " + id + e.getMessage());
            return false;
        } finally {
            database.close();
        }
    }

    public static List<Object[]> loadAllSessions(Context context) {
        List<Object[]> sessions = new ArrayList<>();
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        try {
            String[] columns = {KEY_SESSION_ID, KEY_SESSION_DATE, KEY_SESSION_LOCATION, KEY_SESSION_DURATION, KEY_SESSION_FISH_CAUGHT, KEY_SESSION_STEPS};
            Cursor cursor = database.query(SESSION_TABLE_NAME, columns, null, null, null, null, KEY_SESSION_DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SESSION_ID));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SESSION_DATE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SESSION_LOCATION));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SESSION_DURATION));
                    int fishCaught = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SESSION_FISH_CAUGHT));
                    int steps = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SESSION_STEPS));
                    sessions.add(new Object[]{id, date, location, duration, fishCaught, steps});
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("DBSession", "Error session load: " + e.getMessage());
        } finally {
            database.close();
        }

        Log.d("DBSession", "Session Loaded: " + sessions.size());

        return sessions;
    }

    public static void deleteAllData(Context context) {
        SmartAnglerSessionHelper databaseHelper = new SmartAnglerSessionHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int deletedPhotos = database.delete(PHOTO_TABLE_NAME, null, null);
        int deletedSessions = database.delete(SESSION_TABLE_NAME, null, null);
        database.close();
        Log.d("SmartAngler", "Eliminati " + deletedPhotos + " foto e " + deletedSessions + " sessioni.");
    }
}