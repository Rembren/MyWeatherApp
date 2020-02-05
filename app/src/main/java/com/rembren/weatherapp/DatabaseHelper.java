package com.rembren.weatherapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "selected_places.db";
    public static final String TABLE_NAME = "selected_places_table";
    public static final String ITEM_ID = "ID";
    public static final String CITY_NAME = "NAME";
    public static final String CITY_ADDRESS = "ADDRESS";
    static final String LATITUDE = "LAT";
    static final String LONGITUDE = "LONG";
    static final String UNIX_SUNRISE = "SUNRISE";
    static final String UNIX_SUNSET = "SUNSET";
    static final String UNIX_TIMEZONE = "TIMEZONE";
    static final String WEATHER_MAIN = "WEATHER_MAIN";
    static final String WEATHER_DESCRIPTION = "WEATHER_DESC";
    static final String TEMPERATURE = "TEMPERATURE";
    static final String HUMIDITY = "HUMIDITY";
    static final String PRESSURE = "PRESSURE";
    static final String WIND_SPEED = "WIND_SPEED";
    static final String WIND_DEG = "WIND_DEG";
    static final String CLOUDS = "CLOUDS";
    static final String RAIN = "RAIN";
    static final String SNOW = "SNOW";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" +
                ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CITY_NAME + " TEXT, " +
                CITY_ADDRESS + " TEXT," +
                LATITUDE + " DOUBLE," +
                LONGITUDE + " DOUBLE," +
                UNIX_SUNRISE + " INTEGER," +
                UNIX_SUNSET + " INTEGER," +
                UNIX_TIMEZONE + " INTEGER," +
                WEATHER_MAIN + " TEXT," +
                WEATHER_DESCRIPTION + " TEXT," +
                TEMPERATURE + " DOUBLE," +
                HUMIDITY + " INTEGER," +
                PRESSURE + " DOUBLE," +
                WIND_SPEED + " DOUBLE," +
                WIND_DEG + " DOUBLE," +
                CLOUDS + " INTEGER," +
                RAIN + " BOOLEAN," +
                SNOW + " BOOLEAN)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
