package com.multixpro.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.activity.ParentActivity;
import com.fragments.CabSelectionFragment;
import com.general.files.ActUtils;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.general.files.InternetConnection;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HailActivity extends ParentActivity implements GetLocationUpdates.LocationUpdatesListener, OnMapReadyCallback, GetAddressFromLocation.AddressFound {

    public Location userLocation, destLocation;
    public String pickupaddress = "", Destinationaddress = "", destlat = "", destlong = "";
    public SlidingUpPanelLayout sliding_layout;
    public ArrayList<String> cabTypesArrList = new ArrayList<>();
    public CabSelectionFragment cabSelectionFrag;
    public View toolbararea;
    public LinearLayout destarea;
    public boolean isVerticalCabscroll = false;
    private MTextView titleTxt, destLocHTxt, destLocTxt;
    private GoogleMap gMap;
    private ImageView pinImgView, addDestLocImgView;
    private GetAddressFromLocation getAddressFromLocation;
    private ProgressBar progressBar;
    private boolean isAddressEnable, isdstination = false;
    private final static int RENTAL_REQ_CODE = 1234;
    private Location tempLoc = null;
    private FrameLayout mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hail);


        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);

        destLocation = new Location("dest");

        if (generalFunc.getJsonValueStr("VEHICLE_TYPE_SHOW_METHOD", obj_userProfile) != null &&
                generalFunc.getJsonValueStr("VEHICLE_TYPE_SHOW_METHOD", obj_userProfile).equalsIgnoreCase("Vertical")) {
            isVerticalCabscroll = true;
        }


        toolbararea = findViewById(R.id.toolbararea);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        ImageView backImgView = (ImageView) findViewById(R.id.backImgView);
        addToClickHandler(backImgView);


        getAddressFromLocation = new GetAddressFromLocation(getActContext(), generalFunc);
        getAddressFromLocation.setAddressList(this);


        destarea = (LinearLayout) findViewById(R.id.destarea);
        destLocHTxt = (MTextView) findViewById(R.id.destLocHTxt);

        addToClickHandler(destarea);
        destLocTxt = (MTextView) findViewById(R.id.destLocTxt);
        pinImgView = (ImageView) findViewById(R.id.pinImgView);
        addDestLocImgView = (ImageView) findViewById(R.id.addDestLocImgView1);
        mainContent = (FrameLayout) findViewById(R.id.mainContent);

        intCheck = new InternetConnection(this);

        View imagemarkerdest2 = (View) findViewById(R.id.imagemarkerdest2);

        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        if (map != null) {
            map.getMapAsync(HailActivity.this);
        }

        setLabel();
        destarea.setEnabled(false);
        new CreateRoundedView(getActContext().getResources().getColor(R.color.pickup_req_later_btn), Utils.dipToPixels(getActContext(), 6), 2,
                getActContext().getResources().getColor(R.color.pickup_req_later_btn), imagemarkerdest2);
        progressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        showprogress();
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
    }

    private void showprogress() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(getActContext().getResources().getColor(R.color.appThemeColor_2), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void hideprogress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setPanelHeight(int value) {
        Logger.d("setPanelHeight", "::" + value);

        int FragHeight = Utils.dipToPixels(getActContext(), value);

        if (cabSelectionFrag != null) {
            if (isVerticalCabscroll) {

                /*
                when rentalTypeList is 1 then bottom button is not show proper
                orignal condition
                cabSelectionFrag.rentalTypeList != null && cabSelectionFrag.rentalTypeList.size() > 1
                 */
                if (cabSelectionFrag.rentalTypeList != null && cabSelectionFrag.rentalTypeList.size() >= 1) {
                    if (cabSelectionFrag.rentalarea.getVisibility() == View.GONE) {
                        FragHeight = getActContext().getResources().getDimensionPixelSize(R.dimen._337sdp);
                    } else {
                        FragHeight = getActContext().getResources().getDimensionPixelSize(R.dimen._370sdp);
                    }
                } else {
                    FragHeight = getActContext().getResources().getDimensionPixelSize(R.dimen._340sdp);
                }

                RelativeLayout.LayoutParams flParams = (RelativeLayout.LayoutParams) mainContent.getLayoutParams();
                flParams.height = getActContext().getResources().getDimensionPixelSize(R.dimen._275sdp);
                mainContent.setLayoutParams(flParams);
            } else {
                //sliding_layout.setPanelHeight(Utils.dipToPixels(getActContext(), value));
                // sliding_layout.setPanelHeight(getResources().getDimensionPixelOffset(value));

                gMap.setPadding(0, 0, 0, Utils.dipToPixels(getActContext(), value + 5));
            }
        }

        sliding_layout.setPanelHeight(FragHeight);
    }

    public void OpenCardPaymentAct(boolean fromcabselection) {
        Bundle bn = new Bundle();
        //  bn.putString("UserProfileJson", userProfileJson);
        bn.putBoolean("fromcabselection", fromcabselection);
        new ActUtils(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
    }

    public Context getActContext() {
        return HailActivity.this;
    }

    private void setLabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Taxi Hail", "LBL_TAXI_HAIL"));
        destLocHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DROP_AT"));
        destLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
        addDestLocImgView.setImageResource(R.mipmap.plus);
    }

    @Override
    public void onAddressFound(String address, double latitude, double longitude, String geocodeobject) {
        if (pickupaddress.equals("") || pickupaddress.length() == 0) {
            destarea.setEnabled(true);
            pickupaddress = address;
            hideprogress();
        }
        if (isdstination) {
            isdstination = false;

            destLocTxt.setText(address);
            addDestLocImgView.setImageResource(R.drawable.ic_pencil_edit_button);
            Destinationaddress = address;
            destlat = latitude + "";
            destlong = longitude + "";

            destLocation.setLatitude(latitude);
            destLocation.setLongitude(longitude);

        }
    }

    @Override
    public void onLocationUpdate(Location location) {

        this.userLocation = location;
        if (pickupaddress.equals("") || pickupaddress.length() == 0) {
            if (tempLoc != null && tempLoc.getLatitude() == location.getLatitude()) {
                return;
            }

            getAddressFromLocation.setLocation(location.getLatitude(), location.getLongitude());
            getAddressFromLocation.execute();
        }
        tempLoc = location;

        if (!Destinationaddress.equals("")) {
            return;
        }

        /// CameraPosition cameraPosition = cameraForUserPosition();
        CameraUpdate cameraPosition = generalFunc.getCameraPosition(userLocation, gMap);


        if (cameraPosition != null)
            getMap().moveCamera(cameraPosition);


        String isGoOnline = generalFunc.retrieveValue(Utils.GO_ONLINE_KEY);

        if ((isGoOnline != null && !isGoOnline.equals("") && isGoOnline.equals("Yes"))) {
            //long lastTripTime = generalFunc.parseLongValue(0, generalFunc.retrieveValue(Utils.LAST_FINISH_TRIP_TIME_KEY));
            //long currentTime = Calendar.getInstance().getTimeInMillis();

            HashMap<String, String> storeData = new HashMap<>();
            storeData.put(Utils.GO_ONLINE_KEY, "No");
            storeData.put(Utils.LAST_FINISH_TRIP_TIME_KEY, "0");
            generalFunc.storeData(storeData);

        }
    }

    private GoogleMap getMap() {
        return this.gMap;
    }

    /*private CameraPosition cameraForUserPosition() {
        double currentZoomLevel = getMap().getCameraPosition().zoom;

        if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
        }
        if (userLocation != null) {
            return new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude())).bearing(getMap().getCameraPosition().bearing)
                    .zoom((float) currentZoomLevel).build();
        } else {
            return null;
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        GetLocationUpdates.getInstance().startLocationUpdates(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }

    private void removeLocationUpdates() {

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        if (getAddressFromLocation != null) {
            getAddressFromLocation.setAddressList(null);
            getAddressFromLocation = null;
        }

        if (gMap != null) {
            this.gMap.setOnCameraChangeListener(null);
            this.gMap = null;
        }

        this.userLocation = null;
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        if (generalFunc.checkLocationPermission(true)) {
            getMap().setMyLocationEnabled(true);
            if (isVerticalCabscroll) {
                getMap().setPadding(0, getActContext().getResources().getDimensionPixelSize(R.dimen._35sdp), 0, getActContext().getResources().getDimensionPixelSize(R.dimen._55sdp));
            } else {
                getMap().setPadding(0, 0, 0, Utils.dipToPixels(getActContext(), 15));
            }
            getMap().getUiSettings().setTiltGesturesEnabled(false);
            getMap().getUiSettings().setZoomControlsEnabled(false);
            getMap().getUiSettings().setCompassEnabled(false);
            getMap().getUiSettings().setMyLocationButtonEnabled(false);
        }

        getMap().setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            return true;
        });

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        GetLocationUpdates.getInstance().startLocationUpdates(this, this);

    }

    public void removeImage(View v) {
        if (cabSelectionFrag != null) {
            cabSelectionFrag.removeImage(v);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == HailActivity.RESULT_OK) {

                isdstination = true;
                Place place = PlaceAutocomplete.getPlace(getActContext(), data);

                LatLng placeLocation = place.getLatLng();
                Destinationaddress = Objects.requireNonNull(place.getAddress()).toString();
                destlat = placeLocation.latitude + "";
                destlong = placeLocation.longitude + "";
                destLocation.setLatitude(placeLocation.latitude);
                destLocation.setLongitude(placeLocation.longitude);


                gMap.setOnCameraChangeListener(new onGoogleMapCameraChangeList());
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);
                addcabselectionFragment();

                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }
                pinImgView.setVisibility(View.VISIBLE);
                destLocTxt.setText(place.getAddress().toString());
                addDestLocImgView.setImageResource(R.drawable.ic_pencil_edit_button);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActContext(), data);


                generalFunc.showMessage(generalFunc.getCurrentView(HailActivity.this),
                        status.getStatusMessage());
            }
        } else if (requestCode == Utils.SEARCH_DEST_LOC_REQ_CODE) {

            if (resultCode == RESULT_OK && data != null && gMap != null) {

                isdstination = true;
                isAddressEnable = true;

                String Latitude = data.getStringExtra("Latitude");
                String Longitude = data.getStringExtra("Longitude");
                String Address = data.getStringExtra("Address");

                LatLng placeLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, Latitude), GeneralFunctions.parseDoubleValue(0.0, Longitude));
                destlat = Latitude;
                destlong = Longitude;


                Destinationaddress = Address;
                destLocTxt.setText(Address);
                addDestLocImgView.setImageResource(R.drawable.ic_pencil_edit_button);


                gMap.setOnCameraChangeListener(new onGoogleMapCameraChangeList());
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, 14.0f);

                addcabselectionFragment();
                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }

                destlat = Latitude;
                destlong = Longitude;
                pinImgView.setVisibility(View.VISIBLE);


            }
        } else if (requestCode == RENTAL_REQ_CODE) {

            if (resultCode == RESULT_OK) {

                if (cabSelectionFrag != null) {
                    if (data != null && data.getStringExtra("iRentalPackageId") != null)
                        cabSelectionFrag.iRentalPackageId = data.getStringExtra("iRentalPackageId");
                    cabSelectionFrag.RentalTripHandle();
                }
            }
        }
    }

    private void addcabselectionFragment() {
        if (cabSelectionFrag == null) {
            cabSelectionFrag = new CabSelectionFragment();
            Bundle bundle = new Bundle();
            cabSelectionFrag.setArguments(bundle);
            pinImgView.setVisibility(View.VISIBLE);


        }

        /*if (cabSelectionFrag != null) {
            if (cabSelectionFrag.rentalPkgDesc != null) {
                runOnUiThread(() -> {
                    cabSelectionFrag.rentalPkgDesc.setVisibility(View.GONE);

                    if (cabSelectionFrag.rentalBackImage != null) {
                        cabSelectionFrag.rentalBackImage.setVisibility(View.GONE);
                        cabSelectionFrag.rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_CHOOSE_TRIP_OR_SWIPE"));
                    }
                });

            }
        }*/
        super.onPostResume();
        getSupportFragmentManager().beginTransaction().replace(R.id.dragView, cabSelectionFrag).commit();
    }

    public void handleImgUploadResponse(String responseString, String imageUploadedType) {

        if (cabSelectionFrag != null) {
            cabSelectionFrag.handleImgUploadResponse(responseString);
        }
    }

    private class onGoogleMapCameraChangeList implements GoogleMap.OnCameraChangeListener {

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            LatLng center = gMap.getCameraPosition().target;

            if (!isAddressEnable) {
                getAddressFromLocation.setLocation(center.latitude, center.longitude);
                getAddressFromLocation.execute();
                destLocTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT"));
                addDestLocImgView.setImageResource(R.drawable.ic_pencil_edit_button);
                destlat = center.latitude + "";
                destlong = center.longitude + "";
            } else {
                isAddressEnable = false;
            }

            if (cabSelectionFrag != null) {
                isdstination = true;
                showprogress();
                cabSelectionFrag.findRoute();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isVerticalCabscroll && sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    public void onClick(View view) {
        int id = view.getId();
        Utils.hideKeyboard(HailActivity.this);
        if (view.getId() == R.id.backImgView) {
            onBackPressed();
        } else if (id == destarea.getId()) {

            if (userLocation == null) {
                return;
            }
            Bundle bn = new Bundle();
            bn.putString("locationArea", "dest");
            bn.putDouble("lat", userLocation.getLatitude());
            bn.putDouble("long", userLocation.getLongitude());
            new ActUtils(getActContext()).startActForResult(SearchLocationActivity.class, bn, Utils.SEARCH_DEST_LOC_REQ_CODE);
        }
    }

    @Override
    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {
        if (cabSelectionFrag != null) {
            cabSelectionFrag.onFileSelected(mFileUri, mFilePath, mFileType);
        }
    }
}