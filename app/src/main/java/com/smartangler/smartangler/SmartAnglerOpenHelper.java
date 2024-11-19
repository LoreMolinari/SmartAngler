package com.smartangler.smartangler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
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

    // Fish DB
    public static final String FISH_TABLE_NAME = "fish";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TECHNIQUES = "techniques";
    public static final String KEY_BAITS_AND_LURES = "baits_and_lures";
    public static final String KEY_SEASONS = "seasons";
    public static final String KEY_TIMES_OF_DAY = "times_of_day";
    public static final String CREATE_FISH_TABLE_SQL = "CREATE TABLE " + FISH_TABLE_NAME + " (" +
            KEY_NAME + " TEXT PRIMARY KEY, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_TECHNIQUES + " TEXT, " +
            KEY_BAITS_AND_LURES + " TEXT, " +
            KEY_SEASONS + " TEXT, " +
            KEY_TIMES_OF_DAY + " TEXT);";
    public static final String[] DEFAULT_FISH_DATA = {
            "INSERT INTO " + FISH_TABLE_NAME + " (" +
                    KEY_NAME + ", " +
                    KEY_DESCRIPTION + ", " +
                    KEY_TECHNIQUES + ", " +
                    KEY_BAITS_AND_LURES + ", " +
                    KEY_SEASONS + ", " +
                    KEY_TIMES_OF_DAY + ") " +
                    "VALUES (" +
                    "'" + "Perch" + "', " +
                    "'" + "You can make risotto with this" + "', " +
                    "'" + "Dropshot,Spinning" + "', " +
                    "'" + "Creature bait,Shad,Spoon" + "', " +
                    "'" + "SUMMER,AUTUMN" + "', " +
                    "'" + "MORNING,AFTERNOON" + "');",
            "INSERT INTO " + FISH_TABLE_NAME + " (" +
                    KEY_NAME + ", " +
                    KEY_DESCRIPTION + ", " +
                    KEY_TECHNIQUES + ", " +
                    KEY_BAITS_AND_LURES + ", " +
                    KEY_SEASONS + ", " +
                    KEY_TIMES_OF_DAY + ") " +
                    "VALUES (" +
                    "'" + "Zander" + "', " +
                    "'" + "Big" + "', " +
                    "'" + "Dropshot,Spinning" + "', " +
                    "'" + "Creature bait,Shad" + "', " +
                    "'" + "AUTUMN,WINTER" + "', " +
                    "'" + "EVENING,NIGHT" + "');"
    };
    // Maybe these should all be ints referencing android strings?

    public SmartAnglerOpenHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    // Load all records in the database
    public static void loadRecords(Context context){
        List<String> dates = new LinkedList<String>();
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String [] columns = new String [] {SmartAnglerOpenHelper.KEY_TIMESTAMP};
        Cursor cursor = database.query(SmartAnglerOpenHelper.TABLE_NAME, columns, null, null, SmartAnglerOpenHelper.KEY_TIMESTAMP,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Log.d("STORED TIMESTAMPS: ", String.valueOf(dates));
    }

    // load records from a single day
    public static Integer loadSingleRecord(Context context, String date){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String where = SmartAnglerOpenHelper.KEY_DAY + " = ?";
        String [] whereArgs = { date };

        Cursor cursor = database.query(SmartAnglerOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS TODAY: ", String.valueOf(numSteps));
        return numSteps;
    }

    public static void deleteRecords (Context context) {
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int numberDeletedRecords = 0;

        numberDeletedRecords = database.delete(SmartAnglerOpenHelper.TABLE_NAME, null, null);
        database.close();

        Toast.makeText(context, "Deleted + "+ String.valueOf(numberDeletedRecords) + " steps", Toast.LENGTH_LONG).show();

    }



    public static Map<Integer, Integer> loadStepsByHour(Context context, String date){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<Integer, Integer>  map = new HashMap<>();

        // 2. Get the readable database
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT hour, COUNT(*)  FROM num_steps " +
                "WHERE day = ? GROUP BY hour ORDER BY  hour ASC ", new String [] {date});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            Integer tmpKey = Integer.parseInt(cursor.getString(0));
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }


    @SuppressLint("Range")
    public static List<Fish> loadAllFish(Context context){
        Fish newFish;
        List<Fish> fish = new LinkedList<>();
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(SmartAnglerOpenHelper.FISH_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            newFish = new Fish(cursor.getString(cursor.getColumnIndex(KEY_NAME)));

            newFish.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

            String techniquesString = cursor.getString(cursor.getColumnIndex(KEY_TECHNIQUES));
            if (techniquesString != null) {
                List<String> techniquesList = Arrays.asList(techniquesString.split(","));
                for (String technique : techniquesList) {
                    newFish.addTechnique(technique);
                }
            }

            String baitsAndLuresString = cursor.getString(cursor.getColumnIndex(KEY_BAITS_AND_LURES));
            if (baitsAndLuresString != null) {
                List<String> baitsAndLuresList = Arrays.asList(baitsAndLuresString.split(","));
                for (String baitOrLure : baitsAndLuresList) {
                    newFish.addBaitOrLure(baitOrLure);
                }
            }

            String seasonsString = cursor.getString(cursor.getColumnIndex(KEY_SEASONS));
            if (seasonsString != null) {
                List<String> seasonsList = Arrays.asList(seasonsString.split(","));
                for (String season : seasonsList) {
                    newFish.addSeason(Fish.Season.valueOf(season));
                }
            }

            String timesOfDayString = cursor.getString(cursor.getColumnIndex(KEY_TIMES_OF_DAY));
            if (timesOfDayString != null) {
                List<String> timesOfDayList = Arrays.asList(timesOfDayString.split(","));
                for (String timeOfDay : timesOfDayList) {
                    newFish.addTimeOfDay(Fish.TimeOfDay.valueOf(timeOfDay));
                }
            }

            fish.add(newFish);
            cursor.moveToNext();
        }
        database.close();

        Log.d("Fetched fish: ", String.valueOf(fish.size()));
        return fish;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_FISH_TABLE_SQL);

        for (String sql : DEFAULT_FISH_DATA) {
            sqLiteDatabase.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
