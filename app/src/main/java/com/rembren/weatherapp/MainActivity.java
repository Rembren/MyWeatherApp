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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rembren.weatherapp.recyclerViewAdapters.SimpleDataRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DatabaseHelper placesDB;
    SimpleDataRecyclerViewAdapter mAdapter;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesDB = new DatabaseHelper(this);
        mDatabase = placesDB.getWritableDatabase();

        getForecast();

        initRecyclerView();
    }

    private void getForecast() {


    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SimpleDataRecyclerViewAdapter(this, getAllPlaces());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        mAdapter.swapCursor(getAllPlaces());
        super.onResume();
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


}
