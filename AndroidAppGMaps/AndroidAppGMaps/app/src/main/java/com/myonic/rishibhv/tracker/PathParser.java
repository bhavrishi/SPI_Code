package com.myonic.rishibhv.tracker;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PathParser {
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        String derivedPolyLine = "";
        HashMap<String, String> hMapOfLatLng = null;
        List pathBtwPoints = null;
        List<LatLng> listOfLatLng = null;

        try {
            jRoutes = jObject.getJSONArray(MapConstants.ROUTES);

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray(MapConstants.ROUTE_LEGS);
                pathBtwPoints = new ArrayList<>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray(MapConstants.ROUTE_STEPS);

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        derivedPolyLine = "";
                        derivedPolyLine = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get(MapConstants.POLYLINE)).get(MapConstants.POINTS);
                        listOfLatLng = decodePoly(derivedPolyLine);

                        /** Traversing all points */
                        for (int l = 0; l < listOfLatLng.size(); l++) {
                            hMapOfLatLng = new HashMap<>();
                            hMapOfLatLng.put("lat", Double.toString((listOfLatLng.get(l)).latitude));
                            hMapOfLatLng.put("lng", Double.toString((listOfLatLng.get(l)).longitude));
                            pathBtwPoints.add(hMapOfLatLng);
                        }
                    }
                    routes.add(pathBtwPoints);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


        return routes;
    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
