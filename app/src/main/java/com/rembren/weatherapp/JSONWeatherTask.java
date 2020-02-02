package com.rembren.weatherapp;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class JSONWeatherTask extends AsyncTask<Object, Integer, Weather> {


    private static final String TAG = "myLog";

    private static String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

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
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                Scanner sc  = new Scanner(url.openStream());
                while(sc.hasNext()){
                    data.append(sc.nextLine());
                }
            }
            Log.d(TAG, "JSON read" + "\n url: " + data.toString());
            weather = Weather.parseJSON(data.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
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

