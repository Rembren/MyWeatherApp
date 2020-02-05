package com.rembren.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rembren.weatherapp.recyclerViewAdapters.SimpleDataRecyclerViewAdapter;

import java.time.Clock;
import java.time.Instant;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "myLog";

    DatabaseHelper placesDB;
    SimpleDataRecyclerViewAdapter mAdapter;
    SQLiteDatabase mDatabase;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    public static final int FEW_CLOUDS_THRESHOLD = 25;
    public static final int SCATTERED_CLOUDS_THRESHOLD = 50;
    public static final int BROKEN_CLOUDS_THRESHOLD = 84;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesDB = new DatabaseHelper(this);
        mDatabase = placesDB.getWritableDatabase();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getForecast();

    }

    private void getForecast() {
    }

/*    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SimpleDataRecyclerViewAdapter(this, getAllPlaces());
        recyclerView.setAdapter(mAdapter);
    }*/


    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            refreshMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_places) {
            Intent intent = new Intent(this, PlaceManagement.class);
            startActivity(intent);
        } else if (id == R.id.menu_item_notifications) {
            Toast.makeText(this, "notifications", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private Cursor getAllPlaces() {
        return mDatabase.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        refreshMap();
        mMap.setOnInfoWindowClickListener(this);
    }

    private void refreshMap() {
        Cursor cursor = getAllPlaces();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            LatLng currentPos = new LatLng(
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
            MarkerOptions marker = new MarkerOptions().position(currentPos)
                    .title(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY_NAME)))
                    .snippet(cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_DESCRIPTION)))
                    .icon(getMarkerIcon(cursor))
                    .zIndex(i);
            mMap.addMarker(marker);
            builder.include(currentPos);
        }
        if (cursor.getCount() > 0) {
            LatLngBounds bounds = builder.build();
            CameraUpdate cu;
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (height * 0.15);
            if (cursor.getCount() > 1) {
                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            } else {
                cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 12);
            }
            mMap.animateCamera(cu);
        }
    }

    private BitmapDescriptor getMarkerIcon(Cursor cursor) {
        String mainState = cursor.getString(
                cursor.getColumnIndex(DatabaseHelper.WEATHER_MAIN));
        switch (mainState) {
            case "Thunderstorm":
                return BitmapDescriptorFactory.fromResource(R.drawable.thunderstorm);
            case "Drizzle":
                return BitmapDescriptorFactory.fromResource(R.drawable.drizzle);
            case "Rain":
                return BitmapDescriptorFactory.fromResource(R.drawable.rain);
            case "Snow":
                return BitmapDescriptorFactory.fromResource(R.drawable.snow);
            case "Mist":
                return BitmapDescriptorFactory.fromResource(R.drawable.mist);
            case "Smoke":
                return BitmapDescriptorFactory.fromResource(R.drawable.smoke);
            case "Haze":
                return BitmapDescriptorFactory.fromResource(R.drawable.haze);
            case "Dust":
                return BitmapDescriptorFactory.fromResource(R.drawable.dust);
            case "Fog":
                return BitmapDescriptorFactory.fromResource(R.drawable.fog);
            case "Sand":
                return BitmapDescriptorFactory.fromResource(R.drawable.sand);
            case "Ash":
                return BitmapDescriptorFactory.fromResource(R.drawable.ash);
            case "Squall":
                return BitmapDescriptorFactory.fromResource(R.drawable.squall);
            case "Tornado":
                return BitmapDescriptorFactory.fromResource(R.drawable.tornado);
            case "Clouds":
                return getCloudCondition(cursor);
            default:
                if (isDay(cursor)) {
                    return BitmapDescriptorFactory.fromResource(R.drawable.clear_sun);
                }
                return BitmapDescriptorFactory.fromResource(R.drawable.clear_moon);
        }
    }


    private BitmapDescriptor getCloudCondition(Cursor cursor) {
        int cloudsPercentage = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CLOUDS));
        if (cloudsPercentage <= FEW_CLOUDS_THRESHOLD) {
            if (isDay(cursor)) {
                return BitmapDescriptorFactory.fromResource(R.drawable.few_clouds);
            }
            return BitmapDescriptorFactory.fromResource(R.drawable.few_clouds_night);
        } else if (cloudsPercentage <= SCATTERED_CLOUDS_THRESHOLD) {
            return BitmapDescriptorFactory.fromResource(R.drawable.scattered_clouds);
        } else if (cloudsPercentage <= BROKEN_CLOUDS_THRESHOLD) {
            return BitmapDescriptorFactory.fromResource(R.drawable.broken_clouds);
        }
        return BitmapDescriptorFactory.fromResource(R.drawable.overcast_clouds);
    }

    private boolean isDay(Cursor cursor) {
        int sunriseTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIX_SUNRISE));
        int sunsetTime = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.UNIX_SUNSET));
        int currentTime = getUnixTime();
        Log.d(TAG, "isDay: UTC: " + currentTime);
        return currentTime > sunriseTime && currentTime < sunsetTime;
    }

    public int getUnixTime() {
        Clock clock = Clock.systemUTC();
        return (int) Instant.now(clock).getEpochSecond();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_top_to_bottom, R.anim.enter_top_to_bottom, R.anim.exit_bottom_to_top);
        DetailedWeatherInfo fragment = new DetailedWeatherInfo((int) marker.getZIndex() + 1, this);
        ft.replace(R.id.map, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
