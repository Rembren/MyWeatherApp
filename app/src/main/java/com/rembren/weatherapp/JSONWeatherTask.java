package com.rembren.weatherapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

public class JSONWeatherTask extends AsyncTask<Object, Integer, Weather> {


    private static final String TAG = "myLog";

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    private static String WEATHER_MAP_API_KEY = "777097772b15e1966cc3bd58ee851a1c";

    public static final String METRIC_ENDING = "&units=metric";

    private Observable wn = new Observable();//simple Observable Object created to nofity event

    private StringBuilder jsonurl = new StringBuilder();
    private StringBuilder data = new StringBuilder();



    public JSONWeatherTask(double lat, double lon) {
        super();
        jsonurl.append(BASE_URL).append("?lat=").append(lat).append("&lon=").append(lon)
                .append("&APPID=").append(WEATHER_MAP_API_KEY).append(METRIC_ENDING);
    }

    @Override
    protected Weather doInBackground(Object... params) {
        Log.d(TAG, "BACKGROUND TASK STARTED" + "\n url: " + jsonurl.toString());
        Weather weather = new Weather();
        try {
            URL url = new URL(jsonurl.toString());
            HttpURLConnection httpURLConnection = url.openConnection();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader,
                        8192);
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
            }



            try {
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage() + "- " + e.getCause(), e);
            }
        } catch (Throwable t) {
            Log.e(TAG, t.getMessage(), t);
            return null;
        }
        return weather;
    }


    @Override
    protected void onPostExecute(Weather result) {
        this.wn.notifyObservers(result);
    }

    void register(Observer obs) {
        this.wn.addObserver(obs);
    }

}

