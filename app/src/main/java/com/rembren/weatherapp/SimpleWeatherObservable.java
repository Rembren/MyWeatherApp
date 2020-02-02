package com.rembren.weatherapp;

import java.util.Observable;

public class SimpleWeatherObservable extends Observable {

    public void changeState(){
        setChanged();
    }

}
