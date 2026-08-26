package com.multixpro.user.trackService;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.activity.ParentActivity;
import com.general.call.CommunicationManager;
import com.general.call.MediaDataProvider;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.R;
import com.map.BitmapDescriptorFactory;
import com.map.GeoMapLoader;
import com.map.Marker;
import com.map.helper.MarkerAnim;
import com.map.models.LatLng;
import com.map.models.LatLngBounds;
import com.map.models.MarkerOptions;
import com.service.handler.AppService;
import com.service.model.EventInformation;
import com.utils.LoadImage;
import com.utils.Utils;
import com.utils.VectorUtils;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TrackAnyLiveTracking extends ParentActivity implements GeoMapLoader.OnMapReadyCallback {

    private HashMap<String, String> trackAnyHashMap;
    private GeoMapLoader.GeoMap geoMap;
    private LatLngBounds.Builder builderTracking;
    private String iDriverId = "";
    private Marker driverMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_any_live_tracking);

        trackAnyHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("trackAnyHashMap");

        (new GeoMapLoader(this, R.id.mapLiveTrackingContainer)).bindMap(this);
        if (trackAnyHashMap == null) {
            return;
        }

        iDriverId = trackAnyHashMap.get("iTrackServiceUserId");

        initialization();

        ImageView userLocBtnImgView = findViewById(R.id.userLocBtnImgView);
        addToClickHandler(userLocBtnImgView);

        (new GeoMapLoader(this, R.id.mapLiveTrackingContainer)).bindMap(this);
    }

    private void initialization() {
        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_LIVE_TRACKING_TXT"));
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);

        MTextView txtLiveTrackTitle = findViewById(R.id.txtLiveTrackTitle);
        txtLiveTrackTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_LIVE_TRACKING_TXT"));
        ImageView imvUser = findViewById(R.id.imvUser);

        String vUserImage = trackAnyHashMap.get("vImage");
        if (!Utils.checkText(vUserImage)) {
            vUserImage = "Temp";
        }
        new LoadImage.builder(LoadImage.bind(vUserImage), imvUser).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();

        MTextView txtUserName = findViewById(R.id.txtUserName);
        MTextView txtStatus = findViewById(R.id.txtStatus);
        MTextView txtLblStatus = findViewById(R.id.txtLblStatus);
        txtUserName.setText(trackAnyHashMap.get("userName"));
        txtStatus.setText(trackAnyHashMap.get("TripStatusDisplay"));

        txtLblStatus.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_TRACKING_STATUS_TXT"));
        if (trackAnyHashMap.get("LocationTrackingStatus").equalsIgnoreCase("Yes")) {
            txtStatus.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_ACTIVE_STATUS"));
            txtStatus.setTextColor(Color.parseColor("#34C759"));

        } else {
            txtStatus.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_INACTIVE_STATUS"));
            txtStatus.setTextColor(Color.parseColor("#800000"));
        }

        MTextView txtCall = findViewById(R.id.txtCall);
        LinearLayout txtCallArea = findViewById(R.id.txtCallArea);
        MTextView txtCallOrg = findViewById(R.id.txtCallOrg);
        txtCall.setText(generalFunc.retrieveLangLBl("", "LBL_CALL_TXT"));
        txtCallOrg.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_CALL_ORG_TXT"));
        addToClickHandler(txtCallArea);
        addToClickHandler(txtCallOrg);
    }

    private Context getActContext() {
        return TrackAnyLiveTracking.this;
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        } else if (i == R.id.userLocBtnImgView) {
            if (builderTracking == null) {
                return;
            }
            geoMap.animateCamera(builderTracking.build(), Utils.dipToPixels(getActContext(), 40));

        } else if (i == R.id.txtCallArea) {
            MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                    .setPhoneNumber(trackAnyHashMap.get("userPhone"))
                    .setToMemberName(trackAnyHashMap.get("userName"))
                    .setMedia(CommunicationManager.MEDIA.DEFAULT)
                    .build();
            CommunicationManager.getInstance().communicate(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.OTHER);
        } else if (i == R.id.txtCallOrg) {
            MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                    .setPhoneNumber(trackAnyHashMap.get("orgPhone"))
                    .setToMemberName(trackAnyHashMap.get("orgName"))
                    .setMedia(CommunicationManager.MEDIA.DEFAULT)
                    .build();
            CommunicationManager.getInstance().communicate(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.OTHER);
        }
    }

    @Override
    public void onMapReady(@NonNull GeoMapLoader.GeoMap googleMap) {
        this.geoMap = googleMap;
        if (generalFunc.checkLocationPermission(true)) {
            googleMap.setMyLocationEnabled(false);
        }
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(new LatLng(GeneralFunctions.parseDoubleValue(0, trackAnyHashMap.get("userLatitude")), GeneralFunctions.parseDoubleValue(0, trackAnyHashMap.get("userLongitude"))));


        if (!Objects.equals(trackAnyHashMap.get("userLatitude"), "") || !Objects.equals(trackAnyHashMap.get("userLongitude"), "") || !Objects.equals(trackAnyHashMap.get("userAddress"), "")) {
            double orgLatitude = GeneralFunctions.parseDoubleValue(0.0, trackAnyHashMap.get("userLatitude"));
            double orgLongitude = GeneralFunctions.parseDoubleValue(0.0, trackAnyHashMap.get("userLongitude"));
            BitmapDescriptorFactory.BitmapDescriptor markerIcon = VectorUtils.vectorToBitmap(getActContext(), R.drawable.track_icon, 0);
            MarkerOptions orgOpt = new MarkerOptions().position(new LatLng(orgLatitude, orgLongitude));
            orgOpt.icon(markerIcon).anchor(0.5f, 1);
            Objects.requireNonNull(googleMap.addMarker(orgOpt)).setFlat(false);

            builderTracking = new LatLngBounds.Builder();
            builderTracking.include(new LatLng(orgLatitude, orgLongitude));
        }


        JSONArray userList = generalFunc.getJsonArray(trackAnyHashMap.get("userList"));
        if (userList != null) {
            for (int jk = 0; jk < userList.length(); jk++) {
                JSONObject object = generalFunc.getJsonObject(userList, jk);
                double lat = GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValueStr("vLatitude", object));
                double lon = GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValueStr("vLongitude", object));
                if (lat != 0.0 && lon != 0.0) {
                    LatLng newLocation = new LatLng(lat, lon);
                    builderTracking.include(newLocation);
                    MarkerOptions userHomeOpt = new MarkerOptions();
                    userHomeOpt.position(newLocation);
                    userHomeOpt.title(generalFunc.getJsonValueStr("userName", object));
                    userHomeOpt.snippet(generalFunc.getJsonValueStr("vAddress", object));

                    if (generalFunc.getJsonValueStr("iTrackServiceUserId", object).equalsIgnoreCase(trackAnyHashMap.get("iTrackServiceUserId"))) {
                        userHomeOpt.icon(VectorUtils.vectorToBitmap(getActContext(), R.drawable.ic_track_trip_home, 0)).anchor(0.5f, 0.5f).flat(true);
                    } else {
                        userHomeOpt.icon(VectorUtils.vectorToBitmap(getActContext(), R.drawable.ic_place_marker, 0)).anchor(0.5f, 0.5f).flat(true);
                    }
                    Objects.requireNonNull(googleMap.addMarker(userHomeOpt)).setFlat(false);
                }
            }
        }
        googleMap.setOnMapLoadedCallback(() -> {
            if (!Objects.equals(trackAnyHashMap.get("userLatitude"), "") || !Objects.equals(trackAnyHashMap.get("userLongitude"), "") || !Objects.equals(trackAnyHashMap.get("userAddress"), "")) {
                geoMap.animateCamera(builderTracking.build(), Utils.dipToPixels(getActContext(), 40));
            }
           /* LatLng driverLocation_update = new LatLng(
                    GeneralFunctions.parseDoubleValue(0.0, trackAnyHashMap.get("driverLatitude")),
                    GeneralFunctions.parseDoubleValue(0.0, trackAnyHashMap.get("driverLongitude")));
            updateDriverLocation(driverLocation_update);*/
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeToDriverLocChannel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unSubscribeToDriverLocChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribeToDriverLocChannel();
    }

    private void subscribeToDriverLocChannel() {
        ArrayList<String> channelName = new ArrayList<>();
        channelName.add("Tracking_" + iDriverId);
        AppService.getInstance().executeService(new EventInformation.EventInformationBuilder().setChanelList(channelName).build(), AppService.Event.SUBSCRIBE);
    }

    private void unSubscribeToDriverLocChannel() {
        ArrayList<String> channelName = new ArrayList<>();
        channelName.add("Tracking_" + iDriverId);
        AppService.getInstance().executeService(new EventInformation.EventInformationBuilder().setChanelList(channelName).build(), AppService.Event.UNSUBSCRIBE);
    }

    public void pubNubMsgArrived(final String message) {

        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);
            String DriverId = generalFunc.getJsonValue("iDriverId", message);

            if (msgType.equals("TrackMember") && DriverId.equalsIgnoreCase(iDriverId)) {
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue("vTitle", message), buttonId -> {
                    if (generalFunc.getJsonValue("CLOSED", message).equalsIgnoreCase("1")) {
                        finish();
                    }
                });
            } else if (msgType.equals("LocationUpdate") && DriverId.equalsIgnoreCase(iDriverId)) {
                LatLng driverLocation_update = new LatLng(
                        GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("vLatitude", message)),
                        GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("vLongitude", message)));
                updateDriverLocation(driverLocation_update);
            }
        });
    }

    private void updateDriverLocation(LatLng driverLocation) {
        if (driverLocation == null || geoMap == null || driverLocation.latitude == 0.0) {
            return;
        }

        if (driverMarker == null) {
            geoMap.clear();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(driverLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.track_icon)).anchor(0.5f, 0.5f).flat(true);
            driverMarker = geoMap.addMarker(markerOptions);
        } else {
            //builderTracking.include(driverLocation);


            if (geoMap != null && geoMap.getView() != null) {
                Location driver_loc = new Location("gps");
                driver_loc.setLatitude(driverLocation.latitude);
                driver_loc.setLongitude(driverLocation.longitude);
                MarkerAnim.animateMarker(driverMarker, this.geoMap, driver_loc, 0, 850);

//                int width = geoMap.getView().getMeasuredWidth();
//                int height = geoMap.getView().getMeasuredHeight();
//                height = (height - (int) (height * 0.40));
//                int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
//
//                geoMap.animateCamera(builderTracking.build(), width, height, padding);
//
//                float rotation = (float) SphericalUtil.computeHeading(driverMarker.getPosition(), driverLocation);
//
//
//
//                MarkerAnim.animateMarker(driverMarker, geoMap, driver_loc, rotation, 1200);
            }

            double currentZoomLevel = Utils.defaultZomLevel;

            if (geoMap != null) {


                geoMap.moveCamera(new LatLng(driverLocation.latitude, driverLocation.longitude, (float) currentZoomLevel));
            }
        }
    }


}