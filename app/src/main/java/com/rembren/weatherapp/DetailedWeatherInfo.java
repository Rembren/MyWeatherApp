package com.rembren.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DetailedWeatherInfo extends Fragment {

    private static final String TAG = "myLog";

    private TextView cityName;
    private TextView localDate;
    private TextView temperature;
    private TextView windSpeed;
    private ImageView windDirection;
    private TextView humidity;
    private TextView pressure;
    private DatabaseHelper placesDB;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private int index;

    public DetailedWeatherInfo(int index, Context context) {
        this.index = index;
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_weather_info, container, false);
        cityName = view.findViewById(R.id.fragment_city_name);
        localDate = view.findViewById(R.id.fragment_date);
        temperature = view.findViewById(R.id.fragment_temperature);
        windSpeed = view.findViewById(R.id.fragment_wind_speed);
        windDirection = view.findViewById(R.id.fragment_wind_deg);
        humidity = view.findViewById(R.id.fragment_humidity);
        pressure = view.findViewById(R.id.fragment_pressure);

        placesDB = new DatabaseHelper(mContext);
        mDatabase = placesDB.getWritableDatabase();

        updateData();
        return view;
    }

    private void updateData() {
        Cursor cursor = getAllPlaces();
        cursor.move(index);

        cityName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY_NAME)));
        localDate.setText(getDate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIX_TIMEZONE))));
        temperature.setText( " t,°C: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEMPERATURE)));
        windSpeed.setText("Wind speed, km/h: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.WIND_SPEED)));
        windDirection.setRotation(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WIND_DEG)));
        humidity.setText("Humidity, %: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.HUMIDITY)));
        pressure.setText(" Pressure, hPa: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRESSURE)));


    }

    private String getDate(int timeDifference) {
        long unixSeconds = getUnixTime() + timeDifference;
        Log.d(TAG, "getDate: unix seconds" + unixSeconds);
        Date date = new Date(unixSeconds * 1000L); // seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM"));
        return sdf.format(date);
    }

    private Cursor getAllPlaces() {
        return mDatabase.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
    }

    public long getUnixTime() {
        Clock clock = Clock.systemUTC();
        return (int) Instant.now(clock).getEpochSecond();
    }

}
