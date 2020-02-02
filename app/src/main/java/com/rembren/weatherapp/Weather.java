package com.rembren.weatherapp;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Weather {

    private static final String TAG = "myLog";

    private static final int KELVIN_THRESHOLD = 100;
    private static final double KELVIN_SUBTRACTION = 273.15;

    private String city_name;
    private String weather_main;
    private String weather_description;
    private double temperature;
    private double pressure;
    private int humidity;
    private double wind_speed;
    private double wind_deg;
    private int clouds;


    @NonNull
    @Override
    public String toString() {
        return "City: " + city_name + " Weather: " + weather_main + " Description: " +
                weather_description + " Temperature: " + temperature + " Pressure: " +
                pressure + " Humidity: " + humidity + " Wind Speed " + wind_speed +
                " Wind deg: " + wind_deg + " Clouds: " + clouds;
    }

    /**
     * This method parse the string in input and create the relative Weather object.
     *
     * @param json JSON object read from weather API
     * @return Weather object that contains parsed data
     * @throws JSONException if given field wasn't found
     */
    static Weather parseJSON(JSONObject json) {
        Weather weather = new Weather();

        // Parsing the response
        Log.d(TAG, "jsonobject=" + json);

        try {
            JSONArray json_weather_main = json.getJSONArray("weather");
            // We use only the first value
            JSONObject weatherObj = json_weather_main.getJSONObject(0);
            weather.weather_main = weatherObj.getString("main");
            weather.weather_description = weatherObj.getString("description");
            //Main params
            JSONObject mainObj = new JSONObject(json.getString("main"));
            weather.temperature = checkIfKelvin(mainObj.getDouble("temp"));
            weather.pressure = mainObj.getDouble("pressure");
            weather.humidity = mainObj.getInt("humidity");
            // Wind
            JSONObject wObj = new JSONObject(json.getString("wind"));
            weather.wind_speed = wObj.getDouble("speed");
            weather.wind_deg = wObj.getDouble("deg");
            // Clouds
            JSONObject cObj = new JSONObject(json.getString("clouds"));
            weather.clouds = cObj.getInt("all");
            // City name
            weather.city_name = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, weather.toString());
        return weather;
    }

    /**
     * Checks if the temperature is given in Kelvin and transforms it into Celsius if so
     *
     * @param temp temperature to check
     * @return temperature in Celsius
     */
    private static double checkIfKelvin(double temp) {
        if (temp > KELVIN_THRESHOLD) {
            return temp - KELVIN_SUBTRACTION;
        }
        return temp;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getWeather_main() {
        return weather_main;
    }

    public String getWeather_description() {
        return weather_description;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public double getWind_deg() {
        return wind_deg;
    }

    public int getClouds() {
        return clouds;
    }
}
