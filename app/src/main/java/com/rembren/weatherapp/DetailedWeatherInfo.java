package com.rembren.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
    private ImageView windDirectionImage;
    private TextView humidity;
    private TextView pressure;
    private TextView sunrise;
    private TextView sunset;
    private TextView windDirectionText;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private int index;

    DetailedWeatherInfo(int index, Context context) {
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
        windDirectionImage = view.findViewById(R.id.fragment_wind_deg);
        humidity = view.findViewById(R.id.fragment_humidity);
        pressure = view.findViewById(R.id.fragment_pressure);
        windDirectionText = view.findViewById(R.id.fragment_wind_direction);
        sunrise = view.findViewById(R.id.fragment_sunrise);
        sunset = view.findViewById(R.id.fragment_sunset);

        DatabaseHelper placesDB = new DatabaseHelper(mContext);
        mDatabase = placesDB.getWritableDatabase();

        updateData();
        return view;
    }

    /**
     * Updates text in fragment views based on data stored in database
     */
    private void updateData() {
        Cursor cursor = getAllPlaces();
        cursor.move(index);
        int timeDifference = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIX_TIMEZONE));
        cityName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY_NAME)));
        localDate.setText(getCurrentDate(timeDifference));
        sunrise.setText("Sunrise: "
                + getDate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIX_SUNRISE)),
                timeDifference));
        sunset.setText("Sunset: "
                + getDate(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIX_SUNSET)),
                timeDifference));
        temperature.setText(parseTemp(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.TEMPERATURE))) + " °C");
        windSpeed.setText("Wind speed, km/h: "
                + cursor.getString(cursor.getColumnIndex(DatabaseHelper.WIND_SPEED)));
        windDirectionImage.setRotation(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WIND_DEG)));
        windDirectionText.setText(getDirection(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WIND_DEG))));
        humidity.setText("Humidity, %: "
                + cursor.getString(cursor.getColumnIndex(DatabaseHelper.HUMIDITY)));
        pressure.setText("Pressure, hPa: "
                + cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRESSURE)));


    }

    /**
     * Returns cardinal direction based on degree between given direction and north in deg.
     * <p>
     * Divides given deg by 45, which is a step between every cardinal direction and rounds up
     * the resulting number to the closest integer
     *
     * @param deg azimuth
     * @return cardinal direction
     */
    private String getDirection(int deg) {
        int direction = Math.round(deg / 45f);
        switch (direction) {
            case 1:
                return "NE";
            case 2:
                return "E";
            case 3:
                return "SE";
            case 4:
                return "S";
            case 5:
                return "SW";
            case 6:
                return "W";
            case 7:
                return "NW";
            default:
                return "N";
        }
    }

    /**
     * Parses the string with temperature value, rounds it up to the closest integer and
     * adds a "+" in front if the value is greater than zero
     *
     * @param temperature temperature value
     * @return string with temperature value
     */
    private String parseTemp(double temperature) {

        int temp = Math.round((float) temperature);
        if (temp > 0) {
            return "+" + temp;
        }
        return Integer.toString(temp);
    }

    /**
     * Counts current time considering time zone and transforms it into a string with a suitable
     * format
     *
     * @param timeDifference time difference in seconds between UTC and local country time
     * @return string with date
     */
    private String getCurrentDate(int timeDifference) {
        long unixSeconds = getUnixTime() + timeDifference;
        Date date = new Date(unixSeconds * 1000L); // seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM"));
        return sdf.format(date);
    }

    /**
     * Calculates date value based on given time in unix format and time difference in seconds
     *
     * @param timeDifference time difference in seconds between UTC and local country time
     * @param UTCSeconds     time in unix format to be counted
     * @return string with date
     */
    private String getDate(int UTCSeconds, int timeDifference) {
        long localSeconds = UTCSeconds + timeDifference;
        Date date = new Date(localSeconds * 1000L); // seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GTM"));
        return sdf.format(date);
    }

    /**
     * Runs a query to the database
     *
     * @return Cursor with all elements of thedata base
     */
    private Cursor getAllPlaces() {
        return mDatabase.query(DatabaseHelper.TABLE_NAME, null, null,
                null, null, null, null);
    }

    /**
     * @return current UTC time
     */
    private long getUnixTime() {
        Clock clock = Clock.systemUTC();
        return (int) Instant.now(clock).getEpochSecond();
    }

}
