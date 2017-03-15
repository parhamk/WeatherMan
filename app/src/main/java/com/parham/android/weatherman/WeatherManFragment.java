package com.parham.android.weatherman;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by parham on 3/14/2017 AD.
 */

public class WeatherManFragment extends Fragment {

    private static final String TAG = "WeatherManFragment";
    private TextView mTempTextView;
    private String mTemp;

    public static WeatherManFragment newInstance() {
        return new WeatherManFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateItems();
        Log.i(TAG, "Background thread started");
    }

    private void updateItems() {
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather_man, container, false);
        mTempTextView = (TextView) v.findViewById(R.id.weather_text_view);
        return v;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String temp = new WeatherFetchr().getCurrentTemperature();
            return temp;
        }

        @Override
        protected void onPostExecute(String temp) {
            super.onPostExecute(temp);
            mTemp = temp;
            mTempTextView.setText(mTemp);
            Log.i(TAG, "current mTemp" + mTemp);
        }

    }
}