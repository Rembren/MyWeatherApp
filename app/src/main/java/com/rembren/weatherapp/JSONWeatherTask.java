package com.rembren.weatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.libraries.places.api.model.Place;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Observer;
import java.util.Scanner;

public class JSONWeatherTask extends AsyncTask<Object, Integer, Weather> {


    private static final String TAG = "myLog";

    private static String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private static String WEATHER_MAP_API_KEY = "777097772b15e1966cc3bd58ee851a1c";

    private static final String METRIC_ENDING = "&units=metric";

    private SimpleObservable obs = new SimpleObservable(); //simple Observable Object created to nofity event

    private StringBuilder jsonUrl = new StringBuilder();

    private StringBuilder data = new StringBuilder();

    private SQLiteDatabase mDatabase;

    private Place place;

    private Cursor mCursor;

    JSONWeatherTask(Place place, Context context) {
        super();
        this.place = place;
        DatabaseHelper placesDB = new DatabaseHelper(context);
        mDatabase = placesDB.getWritableDatabase();
        jsonUrl.append(BASE_URL)
                .append("?lat=")
                .append(Objects.requireNonNull(place.getLatLng()).latitude)
                .append("&lon=")
                .append(place.getLatLng().longitude)
                .append("&APPID=")
                .append(WEATHER_MAP_API_KEY)
                .append(METRIC_ENDING);

    }

    JSONWeatherTask(int position, Context context) {
        super();
        DatabaseHelper placesDB = new DatabaseHelper(context);
        mDatabase = placesDB.getWritableDatabase();
        mCursor = getAllPlaces();
        mCursor.moveToPosition(position);
        jsonUrl.append(BASE_URL)
                .append("?lat=")
                .append(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.LATITUDE)))
                .append("&lon=")
                .append(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.LONGITUDE)))
                .append("&APPID=")
                .append(WEATHER_MAP_API_KEY)
                .append(METRIC_ENDING);

    }


    @Override
    protected Weather doInBackground(Object... params) {
        Weather weather;
        try {
            URL url = new URL(jsonUrl.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Scanner sc = new Scanner(url.openStream());
                while (sc.hasNext()) {
                    data.append(sc.nextLine());
                }
            }
            weather = Weather.parseJSON(data.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
        return weather;
    }


    @Override
    protected void onPostExecute(Weather result) {
        if (place != null) {
            addDataToDatabase(result);
        } else {
            updateDataInDatabase(result);
        }
        obs.changeState();
        obs.notifyObservers(result);
    }

    private void addDataToDatabase(Weather forecast) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.CITY_NAME, place.getName());
        cv.put(DatabaseHelper.CITY_ADDRESS, place.getAddress());
        cv.put(DatabaseHelper.LATITUDE, Objects.requireNonNull(place.getLatLng()).latitude);
        cv.put(DatabaseHelper.LONGITUDE, place.getLatLng().longitude);
        cv.put(DatabaseHelper.WEATHER_MAIN, forecast.getWeather_main());
        cv.put(DatabaseHelper.WEATHER_DESCRIPTION, forecast.getWeather_description());
        cv.put(DatabaseHelper.TEMPERATURE, forecast.getTemperature());
        cv.put(DatabaseHelper.HUMIDITY, forecast.getHumidity());
        cv.put(DatabaseHelper.PRESSURE, forecast.getPressure());
        cv.put(DatabaseHelper.WIND_SPEED, forecast.getWind_speed());
        cv.put(DatabaseHelper.WIND_DEG, forecast.getWind_deg());
        cv.put(DatabaseHelper.CLOUDS, forecast.getClouds());
        cv.put(DatabaseHelper.UNIX_SUNRISE, forecast.getUnixSunrise());
        cv.put(DatabaseHelper.UNIX_SUNSET, forecast.getUnixSunset());
        cv.put(DatabaseHelper.UNIX_TIMEZONE, forecast.getUnixTimezone());

        mDatabase.insert(DatabaseHelper.TABLE_NAME, null, cv);
    }

    private void updateDataInDatabase(Weather forecast) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.WEATHER_MAIN, forecast.getWeather_main());
        cv.put(DatabaseHelper.WEATHER_DESCRIPTION, forecast.getWeather_description());
        cv.put(DatabaseHelper.TEMPERATURE, forecast.getTemperature());
        cv.put(DatabaseHelper.HUMIDITY, forecast.getHumidity());
        cv.put(DatabaseHelper.PRESSURE, forecast.getPressure());
        cv.put(DatabaseHelper.WIND_SPEED, forecast.getWind_speed());
        cv.put(DatabaseHelper.WIND_DEG, forecast.getWind_deg());
        cv.put(DatabaseHelper.CLOUDS, forecast.getClouds());
        cv.put(DatabaseHelper.UNIX_SUNRISE, forecast.getUnixSunrise());
        cv.put(DatabaseHelper.UNIX_SUNSET, forecast.getUnixSunset());
        cv.put(DatabaseHelper.UNIX_TIMEZONE, forecast.getUnixTimezone());
        mDatabase.update(DatabaseHelper.TABLE_NAME,
                cv,
                "ID = ?",
                new String[]{Integer.toString(mCursor.getPosition() + 1)});
        Log.d(TAG, "updateDataInDatabase: updated :)" + mCursor.getPosition() + " city name: "
                +  mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.CITY_NAME)));
    }

    void register(Observer obs) {
        this.obs.addObserver(obs);
    }

    private Cursor getAllPlaces() {
        return mDatabase.query(DatabaseHelper.TABLE_NAME, null, null, null,
                null, null, null);
    }

}

