package com.rembren.weatherapp;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ForecastUpdater extends Service {

    private static final String TAG = "myLog";


    ExecutorService executor;
    SQLiteDatabase mDatabase;

    public ForecastUpdater() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newFixedThreadPool(1);
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        mDatabase = databaseHelper.getWritableDatabase();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: service started");
        executor.execute(new Updater());
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private Cursor getAllPlaces() {
        return mDatabase.query(DatabaseHelper.TABLE_NAME, null, null, null,
                null, null, null);
    }

    class Updater implements Runnable {

        public void run() {

            try {
                while (true) {
                    Cursor cursor = getAllPlaces();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        JSONWeatherTask wt = new JSONWeatherTask(i, getApplicationContext());
                        wt.execute();
                    }
                    TimeUnit.SECONDS.sleep(15);
                    Intent i = new Intent(MainActivity.UPDATE_RECEIVED);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                    Log.d(TAG, "run: Data updated");
                    TimeUnit.SECONDS.sleep(15);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
