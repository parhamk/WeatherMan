package com.parham.android.weatherman;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WeatherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return WeatherManFragment.newInstance();
    }
}
