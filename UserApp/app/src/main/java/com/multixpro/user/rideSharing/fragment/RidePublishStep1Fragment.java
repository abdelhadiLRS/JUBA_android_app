package com.multixpro.user.rideSharing.fragment;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.fragments.BaseFragment;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.R;
import com.multixpro.user.SearchPickupLocationActivity;
import com.multixpro.user.databinding.FragmentRidePublishStep1Binding;
import com.multixpro.user.rideSharing.RidePublish;
import com.multixpro.user.rideSharing.model.RidePublishData;
import com.map.BitmapDescriptorFactory;
import com.map.GeoMapLoader;
import com.map.Marker;
import com.map.Polyline;
import com.map.models.LatLng;
import com.map.models.MarkerOptions;
import com.service.handler.ApiHandler;
import com.service.handler.AppService;
import com.service.model.DataProvider;
import com.service.server.ServerTask;
import com.utils.MapUtils;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.HashMap;

public class RidePublishStep1Fragment extends BaseFragment implements GeoMapLoader.OnMapReadyCallback {

    private FragmentRidePublishStep1Binding binding;
    @Nullable
    private RidePublish mActivity;
    private GeneralFunctions generalFunc;
    private RidePublishData.LocationDetails mLocationDetails;
    private GeoMapLoader.GeoMap geoMap;
    private Marker startMarker, endMarker;

    private boolean isStartLocationClick = false;
    private Polyline route_polyLine;
    private String Distance, Duration;
    private ServerTask currentCallExeWebServer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_publish_step_1, container, false);

        setLabels();
        setData();

        assert mActivity != null;
        LinearLayout bottomArea = mActivity.binding.bottomAreaView;

        (new GeoMapLoader(mActivity, R.id.mapRidePublishContainer)).bindMap(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            int minusValue = mActivity.mToolbar.getLayoutParams().height
                    + binding.rideStartEndLocationArea.getMeasuredHeight()
                    + bottomArea.getMeasuredHeight()
                    + mActivity.getResources().getDimensionPixelSize(R.dimen._30sdp);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.mapRidePublishContainer.getLayoutParams();
            params.height = (int) (Utils.getScreenPixelHeight(mActivity) - minusValue);
            binding.mapRidePublishContainer.setLayoutParams(params);
            mActivity.setPagerHeight();
        }, 50);
        return binding.getRoot();
    }

    private void setLabels() {

        binding.startLocationBox.setHint(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_START_LOC_TXT"));
        binding.endLocationBox.setHint(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_END_LOC_TXT"));

        addToClickHandler(binding.startLocationBox);
        addToClickHandler(binding.endLocationBox);
        addToClickHandler(binding.locationFlipArea);
    }

    private void setData() {
        if (mActivity != null) {
            RidePublishData.LocationDetails locationDetails = mActivity.mPublishData.getLocationDetails();
            if (locationDetails != null) {
                binding.startLocationBox.setText(locationDetails.getFromAddress());
                binding.endLocationBox.setText(locationDetails.getToAddress());

                if (mActivity.myRideDataHashMap != null) {
                    binding.startLocationBox.setOnClickListener(null);
                    binding.endLocationBox.setOnClickListener(null);
                    binding.locationFlipArea.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApp.getInstance().getCurrentAct() instanceof RidePublish) {
            mActivity = (RidePublish) requireActivity();
            generalFunc = mActivity.generalFunc;

            if (mActivity.mPublishData.getLocationDetails() == null) {
                mLocationDetails = new RidePublishData.LocationDetails();
            } else {
                mLocationDetails = mActivity.mPublishData.getLocationDetails();
            }
        }
    }

    private void setLocationMarkers(boolean isStartLocation, String latitude, String longitude) {
        Location resultLocation = new Location("");
        resultLocation.setLatitude(GeneralFunctions.parseDoubleValue(0.0, latitude));
        resultLocation.setLongitude(GeneralFunctions.parseDoubleValue(0.0, longitude));
        LatLng latLng = new LatLng(resultLocation.getLatitude(), resultLocation.getLongitude());
        geoMap.moveCamera(latLng);

        if (isStartLocation) {
            if (startMarker == null) {
                startMarker = geoMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.dot)));
            } else {
                startMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.dot));
                startMarker.setPosition(latLng);
            }
        } else {
            if (endMarker == null) {
                endMarker = geoMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.dot)));
            } else {
                endMarker.setPosition(latLng);
            }
        }
        findRoute();
    }

    private void findRoute() {

        if (startMarker == null || endMarker == null) {
            return;
        }
        assert mActivity != null;
        AppService.getInstance().executeService(mActivity, new DataProvider.DataProviderBuilder(mLocationDetails.getFromLatitude() + "", mLocationDetails.getFromLongitude() + "").setDestLatitude(mLocationDetails.getToLatitude()).setDestLongitude(mLocationDetails.getToLongitude()).setWayPoints(new JSONArray()).build(), AppService.Service.DIRECTION, data -> {
            Distance = String.valueOf(data.get("DISTANCE"));
            Duration = String.valueOf(data.get("DURATION"));
            if (data.get("RESPONSE_TYPE") != null && data.get("RESPONSE_TYPE").toString().equalsIgnoreCase("FAIL")) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DEST_ROUTE_NOT_FOUND"));
                return;
            }

            if (data.get("RESPONSE_TYPE") != null && data.get("RESPONSE_TYPE").toString().equalsIgnoreCase("FAIL")) {
                return;
            }

            String responseString = data.get("RESPONSE_DATA").toString();

            if (responseString.equalsIgnoreCase("")) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                return;
            }

            if (!responseString.equalsIgnoreCase("") && data.get("DISTANCE") == null) {

                JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                if (obj_routes != null && obj_routes.length() > 0) {

                    LatLng sourceLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getFromLatitude()), GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getFromLongitude()));

                    LatLng destLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getToLatitude()), GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getToLongitude()));

                    responseString = data.get("ROUTES").toString();
                    route_polyLine = MapUtils.handleMapAnimation(mActivity, generalFunc, responseString, sourceLocation, destLocation, geoMap, route_polyLine, true, false);

                } else {
                    generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_ERROR_TXT"), generalFunc.retrieveLangLBl("", "LBL_GOOGLE_DIR_NO_ROUTE"));
                }
            } else {
                LatLng sourceLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getFromLatitude()), GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getFromLongitude()));

                LatLng destLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getToLatitude()), GeneralFunctions.parseDoubleValue(0.0, mLocationDetails.getToLongitude()));

                HashMap<String, Object> data_dict = new HashMap<>();
                data_dict.put("routes", data.get("ROUTES"));
                responseString = data_dict.toString();
                route_polyLine = MapUtils.handleMapAnimation(mActivity, generalFunc, responseString, sourceLocation, destLocation, geoMap, route_polyLine, false, false);
            }
        });
    }

    @Override
    public void onMapReady(GeoMapLoader.GeoMap googleMap) {
        assert mActivity != null;
        this.geoMap = googleMap;
        googleMap.setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            return true;
        });

        Location myLocation = MyApp.getInstance().currentLocation;
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        geoMap.moveCamera(latLng);
        startMarker = geoMap.addMarker(new MarkerOptions().position(latLng));

        RidePublishData.LocationDetails locationDetails = mActivity.mPublishData.getLocationDetails();
        if (locationDetails != null) {
            if (mActivity.myRideDataHashMap != null) {
                setLocationMarkers(true, locationDetails.getFromLatitude(), locationDetails.getFromLongitude());
                setLocationMarkers(false, locationDetails.getToLatitude(), locationDetails.getToLongitude());
            }
        }
    }

    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                    assert mActivity != null;

                    if (isStartLocationClick) {
                        binding.startLocationBox.setText(data.getStringExtra("Address"));
                        mLocationDetails.setFromAddress(data.getStringExtra("Address"));
                        mLocationDetails.setFromLatitude(data.getStringExtra("Latitude"));
                        mLocationDetails.setFromLongitude(data.getStringExtra("Longitude"));

                    } else {
                        binding.endLocationBox.setText(data.getStringExtra("Address"));
                        mLocationDetails.setToAddress(data.getStringExtra("Address"));
                        mLocationDetails.setToLatitude(data.getStringExtra("Latitude"));
                        mLocationDetails.setToLongitude(data.getStringExtra("Longitude"));
                    }
                    mActivity.mPublishData.setLocationDetails(mLocationDetails);

                    setLocationMarkers(isStartLocationClick, data.getStringExtra("Latitude"), data.getStringExtra("Longitude"));
                }
            });

    public void onClickView(View view) {
        Utils.hideKeyboard(getActivity());
        int i = view.getId();
        if (i == binding.startLocationBox.getId() || i == binding.endLocationBox.getId()) {

            isStartLocationClick = i == binding.startLocationBox.getId();
            Intent intent = new Intent(mActivity, SearchPickupLocationActivity.class);
            Bundle bndl = new Bundle();
            bndl.putString("IS_FROM_SELECT_LOC", "Yes");
            intent.putExtras(bndl);
            launchActivity.launch(intent);

        } else if (i == binding.locationFlipArea.getId()) {
            assert mActivity != null;

            String fromAddress = mLocationDetails.getFromAddress();
            String fromLatitude = mLocationDetails.getFromLatitude();
            String fromLongitude = mLocationDetails.getFromLongitude();

            String toAddress = mLocationDetails.getToAddress();
            String toLatitude = mLocationDetails.getToLatitude();
            String toLongitude = mLocationDetails.getToLongitude();

            binding.startLocationBox.setText(toAddress == null ? "" : toAddress);
            binding.endLocationBox.setText(fromAddress == null ? "" : fromAddress);

            mLocationDetails.setToAddress(fromAddress);
            mLocationDetails.setToLatitude(fromLatitude);
            mLocationDetails.setToLongitude(fromLongitude);

            mLocationDetails.setFromAddress(toAddress);
            mLocationDetails.setFromLatitude(toLatitude);
            mLocationDetails.setFromLongitude(toLongitude);

            mActivity.mPublishData.setLocationDetails(mLocationDetails);
            findRoute();
        }
    }

    public void checkPageNext() {
        if (mActivity != null) {
            if (Utils.checkText(binding.startLocationBox.getText().toString()) && Utils.checkText(binding.endLocationBox.getText().toString())) {
                RecommendedPrice();
            } else {
                generalFunc.showMessage(binding.rideStartEndLocationArea, generalFunc.retrieveLangLBl("", "LBL_ENTER_REQUIRED_FIELDS"));
            }
        }
    }

    private void RecommendedPrice() {
        assert mActivity != null;
        if (!Utils.checkText(Distance) || !Utils.checkText(Duration)) {
            return;
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetRideShareRecommendedPrice");

        parameters.put("tStartLocation", mLocationDetails.getFromAddress());
        parameters.put("tEndLocation", mLocationDetails.getToAddress());
        parameters.put("tStartLat", mLocationDetails.getFromLatitude());
        parameters.put("tStartLong", mLocationDetails.getFromLongitude());
        parameters.put("tEndLat", mLocationDetails.getToLatitude());
        parameters.put("tEndLong", mLocationDetails.getToLongitude());
        parameters.put("distance", Distance);
        parameters.put("duration", Duration);

        if (currentCallExeWebServer != null) {
            currentCallExeWebServer.cancel(true);
            currentCallExeWebServer = null;
        }

        currentCallExeWebServer = ApiHandler.execute(mActivity, parameters, true, false, generalFunc, responseString -> {
            currentCallExeWebServer = null;
            if (responseString != null && !responseString.equalsIgnoreCase("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    mActivity.mPublishData.setRecommendedPrice("" + generalFunc.getJsonValue("RecommdedPrice", responseString));
                    mActivity.mPublishData.setPassengerNo("" + generalFunc.getJsonValue("PassengerNo", responseString));
                    mActivity.mPublishData.setRecommdedPriceText("" + generalFunc.getJsonValue("RecommdedPriceText", responseString));
                    mActivity.mPublishData.setRecommdedPriceRange("" + generalFunc.getJsonValue("RecommdedPriceRange", responseString));
                    mActivity.setPageNext();
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
    }
}