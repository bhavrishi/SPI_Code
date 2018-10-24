package com.myonic.rishibhv.tracker;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserAsyncTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    GoogleMap mMap = null;
    /* variable to hold context*/
    private Context applciationContext;

    /*save the context received via constructor in a local variable*/

    public ParserAsyncTask(Context context) {
        this.applciationContext = context;
    }

    /* Parsing the data in non-ui thread*/
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            PathParser parser = new PathParser();
            /* Starts parsing data*/
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    /* Executes in UI thread, after the parsing process*/
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        double lat, lng = 0.0;
        LatLng position = null;
        HashMap<String, String> point = null;
        List<HashMap<String, String>> path = null;
        /* Traversing through all the routes*/
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                /* Fetching i-th route*/
                path = result.get(i);

                /* Fetching all the points in i-th route*/
                for (int j = 0; j < path.size(); j++) {
                    point = path.get(j);
                    lat = Double.parseDouble(point.get("lat"));
                    lng = Double.parseDouble(point.get("lng"));
                    position = new LatLng(lat, lng);
                    points.add(position);
                }

                /*  Adding all the points in the route to LineOptions*/
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }

            /* Drawing polyline in the Google Map for the i-th route*/
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        } else {
            Toast.makeText(applciationContext, "Api returned 0 results", Toast.LENGTH_SHORT).show();
        }
    }
}