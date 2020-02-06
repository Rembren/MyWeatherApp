package com.rembren.weatherapp;

import java.util.Observable;

public class SimpleObservable extends Observable {

    public void changeState(){
        setChanged();
    }

}
