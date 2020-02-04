package com.rembren.weatherapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "selected_places.db";
    public static final String TABLE_NAME = "selected_places_table";
    public static final String ITEM_ID = "ID";
    public static final String CITY_NAME = "NAME";
    public static final String CITY_ADDRESS = "ADDRESS";
    public static final String LATITUDE = "LAT";
    public static final String LONGITUDE = "LONG";
    public static final String WEATHER_MAIN = "WEATHER_MAIN";
    public static final String WEATHER_DESCRIPTION = "WEATHER_DESC";
    public static final String TEMPERATURE = "TEMPERATURE";
    public static final String HUMIDITY = "HUMIDITY";
    public static final String PRESSURE = "PRESSURE";
    public static final String WIND_SPEED = "WIND_SPEED";
    public static final String WIND_DEG = "WIND_DEG";
    public static final String CLOUDS = "CLOUDS";
    public static final String RAIN = "RAIN";
    public static final String SNOW = "SNOW";



    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ("+
                ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CITY_NAME + " TEXT, " +
                CITY_ADDRESS + " TEXT," +
                LATITUDE + " DOUBLE," +
                LONGITUDE + " DOUBLE," +
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
