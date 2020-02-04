package com.rembren.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rembren.weatherapp.recyclerViewAdapters.SimpleDataRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "myLog";

    DatabaseHelper placesDB;
    SimpleDataRecyclerViewAdapter mAdapter;
    SQLiteDatabase mDatabase;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

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
    }

    private void refreshMap() {
        Cursor cursor = getAllPlaces();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            LatLng currentPos = new LatLng(
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
            mMap.addMarker(new MarkerOptions()
                    .position(currentPos)
                    .title(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CITY_NAME))));
            builder.include(currentPos);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu;
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);
        if (cursor.getCount() > 1) {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        } else {
            cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 12);
        }
        mMap.animateCamera(cu);
    }


}
