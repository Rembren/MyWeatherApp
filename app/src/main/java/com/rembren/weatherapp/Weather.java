package com.rembren.weatherapp;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Weather {

    private static final String TAG = "myLog";

    private String city_name;
    private String weather_main;
    private String weather_description;
    private double temperature;
    private double pressure;
    private int humidity;
    private double wind_speed;
    private double wind_deg;
    private int clouds;
    private boolean rain;
    private boolean snow;
    private int unixSunrise;
    private int unixSunset;
    private int unixTimezone;


    @NonNull
    @Override
    public String toString() {
        return "City: " + city_name + " Weather: " + weather_main + " Description: " +
                weather_description + " Temperature: " + temperature + " Pressure: " +
                pressure + " Humidity: " + humidity + " Wind Speed " + wind_speed +
                " Wind deg: " + wind_deg + " Clouds: " + clouds + " Raining: " + rain + " Snowing: "
                + snow + " Sunrise: " + unixSunrise + " Sunset: " + unixSunset + " TIMEZONE: "
                + unixTimezone;
    }


    /**
     * This method parses the string in input and create the relative Weather object.
     *
     * @param jsonStr string given in JSON to parse
     * @return Weather object that contains parsed data
     * @throws JSONException if given field wasn't found
     */
    static Weather parseJSON(String jsonStr) throws JSONException {
        Weather weather = new Weather();

        // Parsing the response
        JSONObject json = new JSONObject(jsonStr);
        //We get weather info (This is an array)
        JSONArray json_weather_main = json.getJSONArray("weather");
        // We use only the first value
        JSONObject weatherObj = json_weather_main.getJSONObject(0);
        weather.weather_main = weatherObj.getString("main");
        weather.weather_description = weatherObj.getString("description");
        //Main params
        JSONObject mainObj = new JSONObject(json.getString("main"));
        weather.temperature = mainObj.getDouble("temp");
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
        // Precipitation
        weather.rain = json.has("rain")
                && new JSONObject(json.getString("rain")).has("1h");
        weather.snow = json.has("snow")
                && new JSONObject(json.getString("snow")).has("1h");
        // Unix Time
        JSONObject tObj = new JSONObject(json.getString("sys"));
        weather.unixSunrise = tObj.getInt("sunrise");
        weather.unixSunset = tObj.getInt("sunset");
        weather.unixTimezone = json.getInt("timezone");

        Log.d(TAG, weather.toString());
        return weather;
    }

    public String getCity_name() {
        return city_name;
    }

    String getWeather_main() {
        return weather_main;
    }

    String getWeather_description() {
        return weather_description;
    }

    double getTemperature() {
        return temperature;
    }

    double getPressure() {
        return pressure;
    }

    int getHumidity() {
        return humidity;
    }

    double getWind_speed() {
        return wind_speed;
    }

    double getWind_deg() {
        return wind_deg;
    }

    int getClouds() {
        return clouds;
    }

    boolean isRaining() {
        return rain;
    }

    boolean isSnowing() {
        return snow;
    }

    public int getUnixSunrise() {
        return unixSunrise;
    }

    public int getUnixSunset() {
        return unixSunset;
    }

    public int getUnixTimezone() {
        return unixTimezone;
    }

}
