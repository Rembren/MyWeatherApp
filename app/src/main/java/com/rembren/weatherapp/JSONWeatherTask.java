package com.rembren.weatherapp;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observer;
import java.util.Scanner;

public class JSONWeatherTask extends AsyncTask<Object, Integer, Weather>{


    private static final String TAG = "myLog";

    private static String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private static String WEATHER_MAP_API_KEY = "777097772b15e1966cc3bd58ee851a1c";

    public static final String METRIC_ENDING = "&units=metric";

    private SimpleWeatherObservable obs = new SimpleWeatherObservable(); //simple Observable Object created to nofity event

    private StringBuilder jsonUrl = new StringBuilder();
    private StringBuilder data = new StringBuilder();



    public JSONWeatherTask(double lat, double lon) {
        super();
        jsonUrl.append(BASE_URL).append("?lat=").append(lat).append("&lon=").append(lon)
                .append("&APPID=").append(WEATHER_MAP_API_KEY).append(METRIC_ENDING);
    }

    @Override
    protected Weather doInBackground(Object... params) {
        Weather weather;
        try {
            URL url = new URL(jsonUrl.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                Scanner sc  = new Scanner(url.openStream());
                while(sc.hasNext()){
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
        obs.changeState();
        obs.notifyObservers(result);
    }

    void register(Observer obs) {
        this.obs.addObserver(obs);
    }

}

