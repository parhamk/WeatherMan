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
    private TextView mCityTextView;
    private TextView mConditionTextView;
    private TextView mTempTextView;
    private String mTemp;

    public static WeatherManFragment newInstance() {
        return new WeatherManFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        updateItems();
        new WeatherFetchr(getActivity()).getCurrentTempByVolley(new ServerCallback() {
            @Override
            public void onSuccess(String[] result) {
                mCityTextView.setText(result[0]);
                mConditionTextView.setText(result[1]);
                mTempTextView.setText(result[2]);
            }
        });
        Log.i(TAG, "Background thread started");
    }

    public interface ServerCallback{
        void onSuccess(String[] result);
    }

    public void updateItems(String temp) {
//        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather_man, container, false);
        mCityTextView = (TextView) v.findViewById(R.id.city_name_text_view);
        mConditionTextView = (TextView) v.findViewById(R.id.condition_text_view);
        mTempTextView = (TextView) v.findViewById(R.id.temp_text_view);
        return v;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String temp = new WeatherFetchr(getActivity()).getCurrentTemperature();
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
