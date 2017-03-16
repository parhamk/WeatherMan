package com.parham.android.weatherman;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

    private static final String TAG = "WeatherFetchr";
    private static final String API_KEY = "f1beba3653826634";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("http://api.wunderground.com/api/f1beba3653826634/conditions/q/CA/San_Francisco.json")
            .buildUpon()
            .build();

    public WeatherFetchr(Context context){
        this.context=context;
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

    public String getCurrentTemperature() {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon();
        String url = uriBuilder.build().toString();
        String temp = "0";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
//                        System.out.println(response.substring(0,100));
                        Log.i(TAG, "Response " + response.substring(0, 100));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
//                System.out.println("Something went wrong!");
                Log.i(TAG, "Something went wrong!");
                error.printStackTrace();

            }
        });

        // Add the request to the queue
        Volley.newRequestQueue(context).add(stringRequest);

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);

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
