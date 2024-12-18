package com.smartangler.smartangler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.smartangler.smartangler.FishingLocation.FishingLocation;
import com.smartangler.smartangler.FishingLocation.Vertex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SmartAnglerOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "smartAngler";

    // Fish DB
    public static final String FISH_TABLE_NAME = "fish";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TECHNIQUES = "techniques";
    public static final String KEY_BAITS_AND_LURES = "baits_and_lures";
    public static final String KEY_SEASONS = "seasons";
    public static final String KEY_TIMES_OF_DAY = "times_of_day";
    public static final String KEY_LONGITUDE = "longitude";

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
                    "'" + "Magical Fish" + "', " +
                    "'" + "Magical fish of the days of yonder" + "', " +
                    "'" + "Dropshot,Spinning" + "', " +
                    "'" + "Creature bait,Shad" + "', " +
                    "'" + "AUTUMN,WINTER,SPRING,SUMMER" + "', " +
                    "'" + "MORNING,AFTERNOON,EVENING,NIGHT" + "', " +
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
                    "'" + "46.0215" + "', " +
                    "'" + "8.8224" + "');",
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
        Log.d("Fish DB", "Getting all fish");
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(SmartAnglerOpenHelper.FISH_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        List<Fish> fish = getFishList(context, cursor);
        database.close();

        Log.d("Fish DB", "Fetched fish: " + fish.size());
        return fish;
    }

    public static List<Fish> getFishByConditions(Context context, Fish.Season season, Fish.TimeOfDay timeOfDay, Vertex currentLocation) {
        Log.d("Fish DB", "Getting fish based on condition");
        if (currentLocation == null) {
            Log.d("Fish DB", "Current location was null");
            return null;
        }
        Log.d("Fish DB", "Current location: " + currentLocation);

        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = String.format("%s like ? AND %s like ?",
                KEY_SEASONS,
                KEY_TIMES_OF_DAY
        );
        String[] selectionArgs = new String[]{
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

        List<Fish> fish_tmp = getFishList(context, cursor);
        Log.d("Fish DB", "Fetched tmp_fish: " + fish_tmp.size());
        database.close();

        List<Fish> fish = new ArrayList<>();
        for (Fish currentFish : fish_tmp) {
            for (FishingLocation fishingLocation : currentFish.getFishingLocations()) {
                if (fishingLocation.isPointInsideLocation(currentLocation)) {
                    fish.add(currentFish);
                }
            }
        }


        Log.d("Fish DB", "Fetched fish: " + fish.size());
        return fish;
    }

    @SuppressLint("Range")
    private static List<Fish> getFishList(Context context, Cursor cursor) {
        Fish newFish;
        List<Fish> fish = new LinkedList<>();

        cursor.moveToFirst();
        for (int index = 0; index < cursor.getCount(); index++) {
            newFish = new Fish(cursor.getString(cursor.getColumnIndex(KEY_NAME)));

            newFish.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));

            String techniquesString = cursor.getString(cursor.getColumnIndex(KEY_TECHNIQUES));
            if (techniquesString != null) {
                String[] techniquesList = techniquesString.split(",");
                for (String technique : techniquesList) {
                    newFish.addTechnique(technique);
                }
            }

            String baitsAndLuresString = cursor.getString(cursor.getColumnIndex(KEY_BAITS_AND_LURES));
            if (baitsAndLuresString != null) {
                String[] baitsAndLuresList = baitsAndLuresString.split(",");
                for (String baitOrLure : baitsAndLuresList) {
                    newFish.addBaitOrLure(baitOrLure);
                }
            }

            String seasonsString = cursor.getString(cursor.getColumnIndex(KEY_SEASONS));
            if (seasonsString != null) {
                String[] seasonsList = seasonsString.split(",");
                for (String fishSeason : seasonsList) {
                    newFish.addSeason(Fish.Season.valueOf(fishSeason));
                }
            }

            String timesOfDayString = cursor.getString(cursor.getColumnIndex(KEY_TIMES_OF_DAY));
            if (timesOfDayString != null) {
                String[] timesOfDayList = timesOfDayString.split(",");
                for (String fishTimeOfDay : timesOfDayList) {
                    newFish.addTimeOfDay(Fish.TimeOfDay.valueOf(fishTimeOfDay));
                }
            }

            String fishingLocationsString = cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ID));
            if (fishingLocationsString != null) {
                Log.d("Location queries", "Location found");
                String[] fishingLocationsIDStringList = fishingLocationsString.split(",");
                for (String fishLocationIdString : fishingLocationsIDStringList) {
                    newFish.addFishingLocation(getFishingLocation(context, Integer.valueOf(fishLocationIdString)));
                }
            }

            fish.add(newFish);
            cursor.moveToNext();
        }
        return fish;
    }

    @SuppressLint("Range")
    private static FishingLocation getFishingLocation(Context context, Integer fishingLocationID) {
        Log.d("Fish DB", "Getting location with ID " + fishingLocationID);

        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String selection = String.format(
                "SELECT v.%s, v.%s, v.%s, lc.%s " +
                        "FROM %s v " +
                        "JOIN %s lc ON v.%s = lc.%s " +
                        "WHERE lc.%s = ?;",
                KEY_ID,
                KEY_LATITUDE,
                KEY_LONGITUDE,
                KEY_NAME,
                VERTICES_TABLE_NAME,
                LOCATIONS_TABLE_NAME,
                KEY_LOCATION_ID,
                KEY_LOCATION_ID,
                KEY_LOCATION_ID
        );
        String[] selectionArgs = new String[]{
                fishingLocationID.toString(),
        };

        Cursor cursor = database.rawQuery(selection, selectionArgs);

        FishingLocation newFishingLocation = new FishingLocation(null);
        String locationName = "";

        cursor.moveToFirst();
        for (int index = 0; index < cursor.getCount(); index++) {
            double latitude = 0;
            double longitude = 0;

            locationName = cursor.getString(cursor.getColumnIndex(KEY_NAME));

            String latitudeString = cursor.getString(cursor.getColumnIndex(KEY_LATITUDE));
            if (latitudeString != null) {
                latitude = Double.valueOf(latitudeString);
            }

            String longitudeString = cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE));
            if (longitudeString != null) {
                longitude = Double.valueOf(longitudeString);
            }

            if (latitudeString != null && longitudeString != null) {
                Vertex newVertex = new Vertex(latitude, longitude);
                newFishingLocation.addVertex(newVertex);
                Log.d("Fish DB", String.format("Added vertex %s to location %d", newVertex, fishingLocationID));
            }

            cursor.moveToNext();
        }

        if (locationName != null) {
            newFishingLocation.setName(locationName);
            Log.d("Fish DB", String.format("Got location with name %s", locationName));
        }
        cursor.close();
        return newFishingLocation;
    }

    @SuppressLint("Range")
    public static FishingLocation getCurrentFishingLocation(Context context, Vertex currentPosition) {
        Log.d("Fish DB", "Getting current location");
        SmartAnglerOpenHelper databaseHelper = new SmartAnglerOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(LOCATIONS_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        FishingLocation newFishingLocation;
        List<FishingLocation> fishingLocations = new ArrayList<>();

        cursor.moveToFirst();
        for (int index = 0; index < cursor.getCount(); index++) {
            String fishingLocationID = cursor.getString(cursor.getColumnIndex(KEY_LOCATION_ID));
            if (fishingLocationID != null) {
                newFishingLocation = getFishingLocation(context, Integer.valueOf(fishingLocationID));
                fishingLocations.add(newFishingLocation);
            }
            cursor.moveToNext();
        }
        cursor.close();
        database.close();

        Log.d("Fish DB", "Fetched fishing locations: " + fishingLocations.size());

        for (FishingLocation fishingLocation : fishingLocations) {
            if (fishingLocation.isPointInsideLocation(currentPosition)) {
                return fishingLocation;
            }
        }
        return null;
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
        if (i < 9) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS num_steps");

            sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s", FISH_TABLE_NAME));
            sqLiteDatabase.execSQL(CREATE_FISH_TABLE_SQL);
            for (String sql : DEFAULT_FISH_DATA) {
                sqLiteDatabase.execSQL(sql);
            }

            sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s", LOCATIONS_TABLE_NAME));
            sqLiteDatabase.execSQL(CREATE_LOCATIONS_TABLES_SQL);
            for (String sql : DEFAULT_LOCATIONS_DATA) {
                sqLiteDatabase.execSQL(sql);
            }

            sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s", VERTICES_TABLE_NAME));
            sqLiteDatabase.execSQL(CREATE_VERTICES_TABLE_SQL);
            for (String sql : DEFAULT_VERTICES_DATA) {
                sqLiteDatabase.execSQL(sql);
            }
        }
    }
}
