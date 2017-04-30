package com.parham.android.weatherman;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetchr {

    private Context context;
    private String[] mWeather;

    private static final String TAG = "WeatherFetchr";
    private static final String API_KEY = "f1beba3653826634";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String QUERY_CONDITIONS = "conditions";
    private static final String QUERY_GEO_LOOKUP = "geolookup";
    private static final String AUTHORITHY = "api.wunderground.com";

    /*
         */
/*       .parse("http://api.wunderground.com/api/f1beba3653826634/conditions/lang:FA/q/zmw:00000.1.40754.json")
            .buildUpon()
            .build();
*/
/*    Uri.Builder uriBuilder = ENDPOINT.buildUpon();
    String url = uriBuilder.build().toString();*/

    public WeatherFetchr(Context context) {
        this.context = context;
    }



    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * find name of the city by coordinates
     * @param location location object containing lat and long
     * @param callback callback for successful find
     */
    public void findCity(Location location,final WeatherManFragment.ServerCallback callback ) {
        String coordinates = location.getLatitude() + "," + location.getLongitude();
        String url = buildUrl(QUERY_GEO_LOOKUP, "FA", coordinates);
        Log.i(TAG, "built url is: " + url);
        final String[] query = new String[1];
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "findCity() Response is " + response.toString());
                        try {
                            JSONObject cityQuery;
                            cityQuery = response.getJSONObject("location");
                            Log.i(TAG, "location object is " + cityQuery.toString());
                            try {
                                query[0] = cityQuery.getString("l");
                                Log.i(TAG, "current city query is: " + query[0]);
                                callback.onSuccess(query[0]);
                            } catch (JSONException e) {
                                Log.i(TAG, "Unable to parse location object");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.i(TAG, "Unable to fetch url");
                    }
                });

        Volley.newRequestQueue(context).add(jsObjRequest);
//        Log.i(TAG, "returning city query is " + query[0]);
//        return query[0];

    }

    public String[] getCurrentTemp(String query, final WeatherManFragment.ServerCallback callback) {
//        Log.i(TAG, "incoming location value is " + location);
//        String query = findCity(location);
//        Log.i(TAG, "Found query is: " + query);
        String url = buildUrl(QUERY_CONDITIONS, "FA", query.replace("/q/",""));
        Log.i(TAG, "Built url is: " + url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "getCurrentTemp() Response is " + response.toString());
                        try {
                            JSONObject location, weather;
                            location = response.getJSONObject("current_observation").getJSONObject("display_location");
                            weather = response.getJSONObject("current_observation");
                            Log.i(TAG, "display_location is " + location.toString());
                            try {
                                mWeather = new String[3];
                                mWeather[0] = location.getString("city");
                                mWeather[1] = weather.getString("weather");
                                mWeather[2] = weather.getString("temp_c");
                                callback.onSuccess(mWeather);
                                Log.i(TAG, "current temp is " + mWeather[2]);
                            } catch (JSONException e) {
                                Log.i(TAG, "Unable to parse temp_c object");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(context).add(jsObjRequest);
//        Log.i(TAG, "returning mTemp is " + mTemp);
        return mWeather;

    }

    public String getCurrentTemperature() {

        String url = buildUrl(QUERY_CONDITIONS, "FA", "zmw:00000.1.40754");

        String temp = "0";
        try {
            String jsonString = getUrlString(url);
//            Log.i(TAG, "Received JSON: " + jsonString);

            JSONObject jsonBody = new JSONObject(jsonString);
            JSONObject weatherJsonObject = jsonBody.getJSONObject("current_observation");
            Log.i(TAG, "Json Object temp_c " + weatherJsonObject.getString("temp_c"));
            temp = weatherJsonObject.getString("temp_c");

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException jse) {
            Log.e(TAG, "Failed to parse JSON", jse);
        }
        return temp;
    }

    private String buildUrl(String method, String lang, String query) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .authority(AUTHORITHY)
                .appendPath("api")
                .appendPath(API_KEY)
                .appendPath(method)
                .appendPath("lang:" + lang)
                .appendPath("q")
                .appendPath(query + ".json");

        return uriBuilder.build().toString();
    }

//    public List<GalleryItem> fetchRecentPhotos() {
//        String url = buildUrl(FETCH_RECENTS_METHOD, null);
//        return downloadGalleryItems(url);
//    }
//
//    public List<GalleryItem> searchPhotos(String query) {
//        String url = buildUrl(SEARCH_METHOD, query);
//        return downloadGalleryItems(url);
//    }

//    private List<GalleryItem> downloadGalleryItems(String url) {
//
//        List<GalleryItem> items = new ArrayList<>();
//
//        try {
//            String jsonString = getUrlString(url);
//            Log.i(TAG, "Received JSON: " + jsonString);
//            JSONObject jsonBody = new JSONObject(jsonString);
//            parseItems(items, jsonBody);
//        } catch (IOException ioe) {
//            Log.e(TAG, "Failed to fetch items", ioe);
//        } catch (JSONException je) {
//            Log.e(TAG, "Failed to parse JSON", je);
//        }
//
//        return items;
//    }

//    private String buildUrl(String method, String query) {
//        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
//                .appendQueryParameter("method", method);
//
//        if (method.equals(SEARCH_METHOD)) {
//            uriBuilder.appendQueryParameter("text", query);
//        }
//
//        return uriBuilder.build().toString();
//    }

//    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
//            throws IOException, JSONException {
//
//        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
//        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
//
//        for (int i = 0; i < photoJsonArray.length(); i++) {
//            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
//
//            GalleryItem item = new GalleryItem();
//            item.setId(photoJsonObject.getString("id"));
//            item.setCaption(photoJsonObject.getString("title"));
//
//            if (!photoJsonObject.has("url_s")) {
//                continue;
//            }
//
//            item.setUrl(photoJsonObject.getString("url_s"));
//            items.add(item);
//        }
//    }

}
