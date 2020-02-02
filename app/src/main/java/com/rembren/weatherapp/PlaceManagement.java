package com.rembren.weatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rembren.weatherapp.recyclerViewAdapters.SimpleDataRecyclerViewAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PlaceManagement extends AppCompatActivity implements Observer {

    int AUTOCOMPLETE_REQUEST_CODE = 1;

    List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ID);

    Context context = this;

    SimpleDataRecyclerViewAdapter mAdapter;

    DatabaseHelper placesDB;

    Place place;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_managment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        placesDB = new DatabaseHelper(this);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);


        initRecyclerView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(context);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_selected_places);
        mAdapter = new SimpleDataRecyclerViewAdapter(this, getAllPlaces());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = Autocomplete.getPlaceFromIntent(data);
                getForecast();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                //TODO: delete this
            }
        }
    }

    private void getForecast() {
        JSONWeatherTask wt = new JSONWeatherTask(place.getLatLng().latitude, place.getLatLng().longitude);
        wt.register(this);
        wt.execute();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Weather){
            Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show();
            return;
            //addDataToDatabase(data);
        }
    }

    private void addDataToDatabase(Object data) {
        Weather forecast = (Weather) data;
        SQLiteDatabase db = placesDB.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.CITY_ID, place.getId());
        cv.put(DatabaseHelper.CITY_NAME, place.getName());
        cv.put(DatabaseHelper.CITY_ADDRESS, place.getAddress());
        cv.put(DatabaseHelper.LATITUDE, place.getLatLng().latitude);
        cv.put(DatabaseHelper.LONGITUDE, place.getLatLng().longitude);
        cv.put(DatabaseHelper.WEATHER_MAIN, forecast.getWeather_main());
        cv.put(DatabaseHelper.WEATHER_DESCRIPTION, forecast.getWeather_description());
        cv.put(DatabaseHelper.TEMPERATURE, forecast.getTemperature());
        cv.put(DatabaseHelper.HUMIDITY, forecast.getHumidity());
        cv.put(DatabaseHelper.PRESSURE, forecast.getPressure());
        cv.put(DatabaseHelper.WIND_SPEED, forecast.getWind_speed());
        cv.put(DatabaseHelper.WIND_DEG, forecast.getWind_deg());
        db.insert(DatabaseHelper.TABLE_NAME, null, cv);
        db.close();
        mAdapter.swapCursor(getAllPlaces());
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                mAdapter.removeItem(item.getGroupId());
                mAdapter.swapCursor(getAllPlaces());
                return true;
            case 2:
                displayMessage("sort items");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Cursor getAllPlaces(){
        SQLiteDatabase db = placesDB.getWritableDatabase();
        return db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
    }


}
