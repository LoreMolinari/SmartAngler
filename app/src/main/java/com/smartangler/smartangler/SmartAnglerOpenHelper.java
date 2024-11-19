package com.smartangler.smartangler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SmartAnglerOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartAngler";
    public static final String TABLE_NAME = "num_steps";
    public static final String KEY_ID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_DAY = "day";
    public static final String KEY_HOUR = "hour";
    public static final String CREATE_TABLE_SQL = "CREATE TABLE  " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_DAY + " TEXT, " + KEY_HOUR + "  TEXT, " + KEY_TIMESTAMP + "  TEXT);";

    public SmartAnglerOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Load all records in the database
    public static void loadRecords(Context context) {
        List<String> dates = new LinkedList<>();
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] columns = new String[]{SmartAnglerOpenHelper.KEY_TIMESTAMP};
        @SuppressLint("Recycle") Cursor cursor = database.query(SmartAnglerOpenHelper.TABLE_NAME, columns, null, null, SmartAnglerOpenHelper.KEY_TIMESTAMP,
                null, null);

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index = 0; index < cursor.getCount(); index++) {
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Log.d("STORED TIMESTAMPS: ", String.valueOf(dates));
    }

    // load records from a single day
    public static Integer loadSingleRecord(Context context, String date) {
        List<String> steps = new LinkedList<>();
        // Get the readable database
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String where = SmartAnglerOpenHelper.KEY_DAY + " = ?";
        String[] whereArgs = {date};

        @SuppressLint("Recycle") Cursor cursor = database.query(SmartAnglerOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null);

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index = 0; index < cursor.getCount(); index++) {
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS TODAY: ", String.valueOf(numSteps));
        return numSteps;
    }

    public static void deleteRecords(Context context) {
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int numberDeletedRecords;

        numberDeletedRecords = database.delete(SmartAnglerOpenHelper.TABLE_NAME, null, null);
        database.close();

        Toast.makeText(context, "Deleted + " + numberDeletedRecords + " steps", Toast.LENGTH_LONG).show();

    }


    public static Map<Integer, Integer> loadStepsByHour(Context context, String date) {
        Map<Integer, Integer> map = new HashMap<>();

        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT hour, COUNT(*)  FROM num_steps " +
                "WHERE day = ? GROUP BY hour ORDER BY  hour ASC ", new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                Integer tmpKey = Integer.parseInt(cursor.getString(0));
                Integer tmpValue = Integer.parseInt(cursor.getString(1));
                map.put(tmpKey, tmpValue);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return map;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
