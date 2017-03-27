package com.parham.android.weatherman;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * Created by parham on 3/14/2017 AD.
 */

public class WeatherManFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WeatherManFragment";

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,};
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private GoogleApiClient mClient;

    private TextView mCityTextView;
    private TextView mConditionTextView;
    private TextView mTempTextView;
    private String mTemp;
    private Location mLocation;

    public static WeatherManFragment newInstance() {
        return new WeatherManFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        updateItems();
        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    setLocation();
                }
            default:

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    private void setLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(10);
        request.setInterval(0);
        Log.i(TAG, "Begin retrieving location ");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "Has no permission");
            return;
        }
        Location lastLocation01 = LocationServices.FusedLocationApi.getLastLocation(
                mClient);
        if (lastLocation01 != null) {
            mLocation = lastLocation01;
            Log.i(TAG, "Got a fix from getLastLocation: " + lastLocation01);
            updateUI();
        }

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mLocation = location;
                        Log.i(TAG, "Got a fix: " + location);
                        updateUI();

                    }
                });
    }

    private void updateUI() {
        Log.i(TAG, "updateUI is executed");
        new WeatherFetchr(getActivity()).findCity(mLocation, new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                new WeatherFetchr(getActivity()).getCurrentTemp(result, new ServerCallback() {
                    @Override
                    public void onSuccess(String[] result) {
                        Log.i(TAG, "onSuccess is executed");
                        mCityTextView.setText(result[0]);
                        mConditionTextView.setText(result[1]);
                        mTempTextView.setText(result[2]);
                    }

                    @Override
                    public void onSuccess(String result) {

                    }
                });

                }

            @Override
            public void onSuccess(String[] result) {

            }
        });

        }

        @Override
        public void onConnected (Bundle bundle){
            if (mClient.isConnected()) {
                Log.i(TAG, "Google_Api_Client: It was connected on (onConnected) function, working as it should.");
                setLocation();
            } else {
                Log.i(TAG, "Google_Api_Client: It was NOT connected on (onConnected) function, It is defiantly bugged.");
            }

        }

        @Override
        public void onConnectionSuspended ( int i){
            Log.i(TAG, "Suspended");
        }

        @Override
        public void onConnectionFailed (@NonNull ConnectionResult connectionResult){
            Log.i(TAG, "Failed");
        }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public interface ServerCallback {
        void onSuccess(String[] result);

        void onSuccess(String result);
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
        String fontPath = "fonts/BZar.ttf";
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), fontPath);
        mCityTextView.setTypeface(tf);
        mConditionTextView.setTypeface(tf);
        mTempTextView.setTypeface(tf);

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
