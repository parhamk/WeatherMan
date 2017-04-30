package com.parham.android.weatherman;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by parham on 4/30/2017 AD.
 */

public class WeatherLab {
    private static WeatherLab sWeatherLab;

    private List<Weather> mWeathers;

    public static WeatherLab get(Context context) {
        if (sWeatherLab == null) {
            sWeatherLab = new WeatherLab(context);
        }

        return sWeatherLab;
    }

    private WeatherLab(Context context) {
        mWeathers = new ArrayList<>();
    }

    public List<Weather> getWeathers() {
        return mWeathers;
    }

    public Weather getWeather(UUID id) {
        for (Weather weather : mWeathers) {
            if (weather.getId().equals(id)) {
                return weather;
            }
        }
        return null;
    }
}
