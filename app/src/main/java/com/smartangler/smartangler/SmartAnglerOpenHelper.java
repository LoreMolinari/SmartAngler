package com.smartangler.smartangler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.smartangler.smartangler.FishingLocation.Vertex;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class SmartAnglerOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "smartAngler";

    // Fish DB
    public static final String FISH_TABLE_NAME = "fish";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TECHNIQUES = "techniques";
    public static final String KEY_BAITS_AND_LURES = "baits_and_lures";
    public static final String KEY_SEASONS = "seasons";
    public static final String KEY_TIMES_OF_DAY = "times_of_day";
    public static final String KEY_LATITUTE = "latitute";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LOCATIONS = "locations";

    // FishingLocation DB
    public static final String LOCATIONS_TABLE_NAME = "fishing_location";
    public static final String VERTICES_TABLE_NAME = "vertices";
    public static final String KEY_LOCATION_ID = "location_id";
    public static final String KEY_ID = "id";
    public static final String KEY_LATITUDE = "latitude";

    public static final String CREATE_FISH_TABLE_SQL = "CREATE TABLE " + FISH_TABLE_NAME + " (" +
            KEY_NAME + " TEXT PRIMARY KEY, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_TECHNIQUES + " TEXT, " +
            KEY_BAITS_AND_LURES + " TEXT, " +
            KEY_SEASONS + " TEXT, " +
            KEY_TIMES_OF_DAY + " TEXT, " +
            KEY_LOCATION_ID + " TEXT, " +
            "FOREIGN KEY (" + KEY_LOCATION_ID + ")  REFERENCES " + LOCATIONS_TABLE_NAME + "(" + KEY_LOCATION_ID + ") );";

    public static final String[] DEFAULT_FISH_DATA = {
            "INSERT INTO " + FISH_TABLE_NAME + " (" +
                    KEY_NAME + ", " +
                    KEY_DESCRIPTION + ", " +
                    KEY_TECHNIQUES + ", " +
                    KEY_BAITS_AND_LURES + ", " +
                    KEY_SEASONS + ", " +
                    KEY_TIMES_OF_DAY + ", " +
                    KEY_LOCATION_ID + ") " +
                    "VALUES (" +
                    "'" + "Perch" + "', " +
                    "'" + "You can make risotto with this" + "', " +
                    "'" + "Dropshot,Spinning" + "', " +
                    "'" + "Creature bait,Shad,Spoon" + "', " +
                    "'" + "SUMMER,AUTUMN" + "', " +
                    "'" + "MORNING,AFTERNOON" + "', " +
                    "'" + "0" + "');",
            "INSERT INTO " + FISH_TABLE_NAME + " (" +
                    KEY_NAME + ", " +
                    KEY_DESCRIPTION + ", " +
                    KEY_TECHNIQUES + ", " +
                    KEY_BAITS_AND_LURES + ", " +
                    KEY_SEASONS + ", " +
                    KEY_TIMES_OF_DAY + ", " +
                    KEY_LOCATION_ID + ") " +
                    "VALUES (" +
                    "'" + "Zander" + "', " +
                    "'" + "Big" + "', " +
                    "'" + "Dropshot,Spinning" + "', " +
                    "'" + "Creature bait,Shad" + "', " +
                    "'" + "AUTUMN,WINTER" + "', " +
                    "'" + "EVENING,NIGHT" + "', " +
                    "'" + "0" + "');"
    };

    public static final String CREATE_LOCATIONS_TABLES_SQL = "CREATE TABLE " + LOCATIONS_TABLE_NAME + " (" +
            KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_NAME + " TEXT);";

    public static final String CREATE_VERTICES_TABLE_SQL = "CREATE TABLE " + VERTICES_TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_LOCATION_ID + " INTEGER, " +
            KEY_LATITUDE + " DOUBLE, " +
            KEY_LONGITUDE + " DOUBLE, " +
            "FOREIGN KEY (" + KEY_LOCATION_ID + ")  REFERENCES " + LOCATIONS_TABLE_NAME + "(" + KEY_LOCATION_ID + ") ON DELETE CASCADE);";

    private static final String[] DEFAULT_LOCATIONS_DATA = {
            "INSERT INTO " + LOCATIONS_TABLE_NAME + " (" +
                    KEY_LOCATION_ID + ", " +
                    KEY_NAME + ") " +
                    "VALUES (" +
                    "'" + "0" + "', " +
                    "'" + "Lake Lugano" + "');"
    };
    private static final String[] DEFAULT_VERTICES_DATA = {
            "INSERT INTO " + VERTICES_TABLE_NAME + " (" +
                    KEY_LOCATION_ID + ", " +
                    KEY_LATITUDE + ", " +
                    KEY_LONGITUDE + ") " +
                    "VALUES (" +
                    "'" + "0" + "', " +
                    "'" + "46.04482" + "', " +
                    "'" + "9.11917" + "');",
            "INSERT INTO " + VERTICES_TABLE_NAME + " (" +
                    KEY_LOCATION_ID + ", " +
                    KEY_LATITUDE + ", " +
                    KEY_LONGITUDE + ") " +
                    "VALUES (" +
                    "'" + "0" + "', " +
                    "'" + "45.9766" + "', " +
                    "'" + "8.8361" + "');",
            "INSERT INTO " + VERTICES_TABLE_NAME + " (" +
                    KEY_LOCATION_ID + ", " +
                    KEY_LATITUDE + ", " +
                    KEY_LONGITUDE + ") " +
                    "VALUES (" +
                    "'" + "0" + "', " +
                    "'" + "45.8906" + "', " +
                    "'" + "8.8821" + "');",
            "INSERT INTO " + VERTICES_TABLE_NAME + " (" +
                    KEY_LOCATION_ID + ", " +
                    KEY_LATITUDE + ", " +
                    KEY_LONGITUDE + ") " +
                    "VALUES (" +
                    "'" + "0" + "', " +
                    "'" + "45.8876" + "', " +
                    "'" + "8.9923" + "');",
            "INSERT INTO " + VERTICES_TABLE_NAME + " (" +
                    KEY_LOCATION_ID + ", " +
                    KEY_LATITUDE + ", " +
                    KEY_LONGITUDE + ") " +
                    "VALUES (" +
                    "'" + "0" + "', " +
                    "'" + "45.9767" + "', " +
                    "'" + "9.0050" + "');",
            "INSERT INTO " + VERTICES_TABLE_NAME + " (" +
                    KEY_LOCATION_ID + ", " +
                    KEY_LATITUDE + ", " +
                    KEY_LONGITUDE + ") " +
                    "VALUES (" +
                    "'" + "0" + "', " +
                    "'" + "46.0215" + "', " +
                    "'" + "9.1468" + "');",
    };

    public SmartAnglerOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static List<Fish> loadAllFish(Context context) {
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(SmartAnglerOpenHelper.FISH_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        List<Fish> fish = getFishList(cursor);
        database.close();

        Log.d("Fetched fish: ", String.valueOf(fish.size()));
        return fish;
    }

    public static List<Fish> getFishByConditions(Context context, Fish.Season season, Fish.TimeOfDay timeOfDay, Vertex currentLocation) {
        if (currentLocation == null) {
            return null;
        }

        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = String.format("%s like ? AND %s like ?",
                KEY_SEASONS,
                KEY_TIMES_OF_DAY
                ); // TODO: Add lat and long
        String[] selectionArgs = new String[] {
                "%" + season.toString() + "%",
                "%" + timeOfDay.toString() + "%"
        };

        Log.d("Query: ", selection);
        Log.d("Query: ", selectionArgs[0]);
        Log.d("Query: ", selectionArgs[1]);

        Cursor cursor = database.query(SmartAnglerOpenHelper.FISH_TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        List<Fish> fish = getFishList(cursor);
        database.close();

        Log.d("Fetched fish: ", String.valueOf(fish.size()));
        return  fish;
    }

    @SuppressLint("Range")
    private static List<Fish> getFishList(Cursor cursor) {
        Fish newFish;
        List<Fish> fish = new LinkedList<>();

        cursor.moveToFirst();
        for (int index = 0; index < cursor.getCount(); index++) {
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
                for (String fishSeason : seasonsList) {
                    newFish.addSeason(Fish.Season.valueOf(fishSeason));
                }
            }

            String timesOfDayString = cursor.getString(cursor.getColumnIndex(KEY_TIMES_OF_DAY));
            if (timesOfDayString != null) {
                List<String> timesOfDayList = Arrays.asList(timesOfDayString.split(","));
                for (String fishTimeOfDay : timesOfDayList) {
                    newFish.addTimeOfDay(Fish.TimeOfDay.valueOf(fishTimeOfDay));
                }
            }

            fish.add(newFish);
            cursor.moveToNext();
        }
        return fish;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_FISH_TABLE_SQL);
        for (String sql : DEFAULT_FISH_DATA) {
            sqLiteDatabase.execSQL(sql);
        }

        sqLiteDatabase.execSQL(CREATE_LOCATIONS_TABLES_SQL);
        for (String sql : DEFAULT_LOCATIONS_DATA) {
            sqLiteDatabase.execSQL(sql);
        }

        sqLiteDatabase.execSQL(CREATE_VERTICES_TABLE_SQL);
        for (String sql : DEFAULT_VERTICES_DATA) {
            sqLiteDatabase.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i < 2) {
            sqLiteDatabase.execSQL("DROP TABLE num_steps");
            sqLiteDatabase.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s DOUBLE", FISH_TABLE_NAME, KEY_LATITUTE));
            sqLiteDatabase.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s DOUBLE", FISH_TABLE_NAME, KEY_LONGITUDE));
        }
        if (i < 3) {
            sqLiteDatabase.execSQL(String.format("DROP TABLE %s", FISH_TABLE_NAME));
            sqLiteDatabase.execSQL(CREATE_FISH_TABLE_SQL);
            for (String sql : DEFAULT_FISH_DATA) {
                sqLiteDatabase.execSQL(sql);
            }
        }
        if (i < 3) {
            sqLiteDatabase.execSQL(CREATE_LOCATIONS_TABLES_SQL);
            sqLiteDatabase.execSQL(CREATE_VERTICES_TABLE_SQL);
        }
        if (i < 4) {
            sqLiteDatabase.execSQL(String.format("DROP TABLE %s", LOCATIONS_TABLE_NAME));
            sqLiteDatabase.execSQL(String.format("DROP TABLE %s", VERTICES_TABLE_NAME));

            sqLiteDatabase.execSQL(CREATE_LOCATIONS_TABLES_SQL);
            for (String sql : DEFAULT_LOCATIONS_DATA) {
                sqLiteDatabase.execSQL(sql);
            }

            sqLiteDatabase.execSQL(CREATE_VERTICES_TABLE_SQL);
            for (String sql : DEFAULT_VERTICES_DATA) {
                sqLiteDatabase.execSQL(sql);
            }
        }
        if (i < 5) {
            sqLiteDatabase.execSQL(String.format("DROP TABLE %s", FISH_TABLE_NAME));
            sqLiteDatabase.execSQL(CREATE_FISH_TABLE_SQL);
            for (String sql : DEFAULT_FISH_DATA) {
                sqLiteDatabase.execSQL(sql);
            }
        }
    }
}
