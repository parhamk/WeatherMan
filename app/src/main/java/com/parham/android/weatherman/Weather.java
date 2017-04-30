package com.parham.android.weatherman;

import android.location.Location;

import java.util.UUID;

/**
 * Created by parham on 3/14/2017 AD.
 */

public class Weather {
    private UUID mId;
    private Location mLocation;
    private String mCity;
    private String mTemperature_c;

    public Weather() {
    mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getTemperature() {
        return mTemperature_c;
    }

    public void setTemperature(String temperature) {
        mTemperature_c = temperature;
    }
}
