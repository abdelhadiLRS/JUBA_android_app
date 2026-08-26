package com.general.files;

import android.content.Context;
import android.location.Location;

import com.multixpro.store.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.service.handler.AppService;
import com.service.model.DataProvider;
import com.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 02-08-2017.
 */

//public class UpdateDirections implements GetLocationUpdates.LocationUpdates, RecurringTask.OnTaskRunCalled {
public class UpdateDirections implements RecurringTask.OnTaskRunCalled {

    public GoogleMap googleMap;
    public Location destinationLocation;
    public Context mcontext;
    public Location userLocation;

    GeneralFunctions generalFunctions;

    String serverKey;
    Polyline route_polyLine;

    GetLocationUpdates getLocUpdates;
    RecurringTask updateFreqTask;

    String gMapLngCode = "en";
    JSONObject userProfileJsonObj;
    String eUnit = "KMs";

    public UpdateDirections(Context mcontext, GoogleMap googleMap, Location userLocation, Location destinationLocation) {
        this.googleMap = googleMap;
        this.destinationLocation = destinationLocation;
        this.mcontext = mcontext;
        this.userLocation = userLocation;

        generalFunctions = MyApp.getInstance().getGeneralFun(mcontext);

        serverKey = generalFunctions.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_COMPANY_APP_KEY);

        gMapLngCode = generalFunctions.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY);

        userProfileJsonObj = generalFunctions.getJsonObject(generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON));
        eUnit = generalFunctions.getJsonValueStr("eUnit", userProfileJsonObj);
    }

    public void scheduleDirectionUpdate() {

        String DESTINATION_UPDATE_TIME_INTERVAL = generalFunctions.retrieveValue("DESTINATION_UPDATE_TIME_INTERVAL");
        updateFreqTask = new RecurringTask((int) (generalFunctions.parseDoubleValue(2, DESTINATION_UPDATE_TIME_INTERVAL) * 60 * 1000));
        updateFreqTask.setTaskRunListener(this);
        updateFreqTask.startRepeatingTask();
    }

    public void releaseTask() {
        if (updateFreqTask != null) {
            updateFreqTask.stopRepeatingTask();
            updateFreqTask = null;
        }

        if (getLocUpdates != null) {
            getLocUpdates.stopLocationUpdates();
            getLocUpdates = null;
        }

        Utils.runGC();
    }

    public void changeDestLoc(Location destinationLocation) {
        this.destinationLocation = destinationLocation;

    }

    public void updateDirections() {

        if (userLocation == null || destinationLocation == null) {
            return;
        }

        String originLoc = userLocation.getLatitude() + "," + userLocation.getLongitude();
        String destLoc = destinationLocation.getLatitude() + "," + destinationLocation.getLongitude();


        String trip_data = generalFunctions.getJsonValueStr("TripDetails", userProfileJsonObj);


        String eTollSkipped = generalFunctions.getJsonValue("eTollSkipped", trip_data);
        boolean istollSkip = false;
        if (eTollSkipped == "Yes") {
            istollSkip = true;
        }

        AppService.getInstance().executeService(mcontext, new DataProvider.DataProviderBuilder(userLocation.getLatitude() + "", userLocation.getLongitude() + "").setDestLatitude(destinationLocation.getLatitude() + "").setDestLongitude(destinationLocation.getLongitude() + "").setTollAccess(istollSkip).setWayPoints(new JSONArray()).build(), AppService.Service.DIRECTION, new AppService.ServiceDelegate() {
            @Override
            public void onResult(HashMap<String, Object> data) {
                if (data.get("RESPONSE_TYPE") != null && data.get("RESPONSE_TYPE").toString().equalsIgnoreCase("FAIL")) {
                    return;
                }
                String responseString = data.get("RESPONSE_DATA").toString();
                if (responseString != null && !responseString.equalsIgnoreCase("") && data.get("DISTANCE") == null) {
                    isGoogle = true;

//            JSONArray obj_routes = generalFunctions.getJsonArray(responseString);
                    JSONArray obj_routes = generalFunctions.getJsonArray("routes", responseString);
                    if (obj_routes != null && obj_routes.length() > 0) {
                        JSONObject obj_legs = generalFunctions.getJsonObject(generalFunctions.getJsonArray("legs", generalFunctions.getJsonObject(obj_routes, 0).toString()), 0);


                        String distance = "" + generalFunctions.getJsonValue("value",
                                generalFunctions.getJsonValue("distance", obj_legs.toString()).toString());
                        String time = "" + generalFunctions.getJsonValue("value",
                                generalFunctions.getJsonValue("duration", obj_legs.toString()).toString());

                        double distance_final = generalFunctions.parseDoubleValue(0.0, distance);


                        if (userProfileJsonObj != null && !generalFunctions.getJsonValueStr("eUnit", userProfileJsonObj).equalsIgnoreCase("KMs")) {

                            distance_final = distance_final * 0.000621371;
                        } else {
                            distance_final = distance_final * 0.00099999969062399994;
                        }

                        distance_final = generalFunctions.round(distance_final, 2);


                    }


                    if (googleMap != null) {

                        PolylineOptions lineOptions = generalFunctions.getGoogleRouteOptions(responseString, Utils.dipToPixels(mcontext, 5), mcontext.getResources().getColor(R.color.black));

                        if (lineOptions != null) {
                            if (route_polyLine != null) {
                                route_polyLine.remove();
                            }
                            route_polyLine = googleMap.addPolyline(lineOptions);

                        }
                    }


                } else {
                    isGoogle = false;

                    double distance_final = generalFunctions.parseDoubleValue(0.0, data.get("DISTANCE").toString());

                    if (userProfileJsonObj != null && !generalFunctions.getJsonValueStr("eUnit", userProfileJsonObj).equalsIgnoreCase("KMs")) {
                        distance_final = distance_final * 0.000621371;
                    } else {
                        distance_final = distance_final * 0.00099999969062399994;
                    }
                    distance_final = generalFunctions.round(distance_final, 2);


                    if (googleMap != null) {


                        PolylineOptions lineOptions;
                        if (isGoogle) {
                            lineOptions = generalFunctions.getGoogleRouteOptions(responseString, Utils.dipToPixels(mcontext, 5), mcontext.getResources().getColor(R.color.black));
                        } else {
                            HashMap<String, Object> data_dict = new HashMap<>();
                            data_dict.put("routes", data.get("ROUTES"));
                            responseString = data_dict.toString();
                            lineOptions = getGoogleRouteOptionsHandle(responseString, Utils.dipToPixels(mcontext, 5), mcontext.getResources().getColor(R.color.black));
                        }

                        if (lineOptions != null) {
                            if (route_polyLine != null) {
                                route_polyLine.remove();
                            }
                            route_polyLine = googleMap.addPolyline(lineOptions);

                        }
                    }

                }

            }
        });

    }


    @Override
    public void onTaskRun() {
        Utils.runGC();
        updateDirections();
    }

    public void changeUserLocation(Location location) {
        if (location != null) {
            this.userLocation = location;
        }
    }


    boolean isGoogle = false;


    public PolylineOptions getGoogleRouteOptionsHandle(String directionJson, int width, int color) {
        PolylineOptions lineOptions = new PolylineOptions();


        try {
            JSONArray obj_routes1 = generalFunctions.getJsonArray("routes", directionJson);

            ArrayList<LatLng> points = new ArrayList<LatLng>();

            if (obj_routes1.length() > 0) {
                // Fetching i-th route
                // Fetching all the points in i-th route
                for (int j = 0; j < obj_routes1.length(); j++) {

                    JSONObject point = generalFunctions.getJsonObject(obj_routes1, j);

                    LatLng position = new LatLng(GeneralFunctions.parseDoubleValue(0, generalFunctions.getJsonValue("latitude", point).toString()), GeneralFunctions.parseDoubleValue(0, generalFunctions.getJsonValue("longitude", point).toString()));


                    points.add(position);

                }


                lineOptions.addAll(points);
                lineOptions.width(width);
                lineOptions.color(color);

                return lineOptions;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }


}
