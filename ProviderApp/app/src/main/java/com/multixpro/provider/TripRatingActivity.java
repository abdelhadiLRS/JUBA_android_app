package com.multixpro.provider;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.activity.ParentActivity;
import com.general.files.GeneralFunctions;
import com.general.files.GetLocationUpdates;
import com.general.files.MyApp;
import com.general.files.SuccessDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.service.handler.ApiHandler;
import com.utils.CommonUtilities;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import java.util.HashMap;

public class TripRatingActivity extends ParentActivity implements OnMapReadyCallback, GetLocationUpdates.LocationUpdatesListener {

    MTextView titleTxt;
    ImageView backImgView;

    GoogleMap gMap;

    Location userLocation;

    MButton btn_type2;
    MaterialEditText commentBox;

    SimpleRatingBar ratingBar;
    String iTripId_str;

    HashMap<String, String> data_trip;
    boolean isSubmitClicked = false;
    MTextView pageTitle;

    SelectableRoundedImageView userImgView;
    String vImage = "", iOrderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_rating);


        data_trip = (HashMap<String, String>) getIntent().getSerializableExtra("TRIP_DATA");
        iTripId_str = data_trip.get("TripId");
        iOrderId = data_trip.get("LastOrderId");

        userImgView = findViewById(R.id.userImgView);
        pageTitle = findViewById(R.id.pageTitle);
        titleTxt = findViewById(R.id.titleTxt);
        backImgView = findViewById(R.id.backImgView);
        commentBox = findViewById(R.id.commentBox);
        ratingBar = findViewById(R.id.ratingBar);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();


        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);
        map.getMapAsync(this);


        if (!data_trip.get("PPicName").equals("")) {
            vImage = CommonUtilities.USER_PHOTO_PATH + data_trip.get("PassengerId") + "/"
                    + data_trip.get("PPicName");
            new LoadImage.builder(LoadImage.bind(vImage), userImgView).setErrorImagePath(R.mipmap.ic_no_pic_user).build();

        } else {
            vImage = "errorImage";
            new LoadImage.builder(LoadImage.bind(vImage), userImgView).setErrorImagePath(R.mipmap.ic_no_pic_user).build();


        }


        (findViewById(R.id.backImgView)).setVisibility(View.GONE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) titleTxt.getLayoutParams();
        params.setMargins(Utils.dipToPixels(getActContext(), 20), 0, 0, 0);
        titleTxt.setLayoutParams(params);

        btn_type2.setId(Utils.generateViewId());
        addToClickHandler(btn_type2);


        commentBox.setHideUnderline(true);
//        if (generalFunc.isRTLmode()) {
//            commentBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._10sdp), 0);
//        } else {
//            commentBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, 0, 0);
//        }

        commentBox.setSingleLine(false);
        commentBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        commentBox.setGravity(Gravity.TOP);
//        commentBox.setBackground(getResources().getDrawable(R.drawable.square_border_common));

        setLabels();

        ((MTextView) findViewById(R.id.nameTxt)).setText(data_trip.get("PName"));

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                generalFunc.restartApp();
            }
        }

        GetLocationUpdates.getInstance().setTripStartValue(false, false, "");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }

        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));
        pageTitle.setText(generalFunc.retrieveLangLBl("", "LBL_DRIVER_RATING_TITLE"));
        ((MTextView) findViewById(R.id.rateTxt)).setText(generalFunc.retrieveLangLBl("Rate", "LBL_RATE"));
        commentBox.setHint(generalFunc.retrieveLangLBl("", "LBL_ENTER_FEEDBACK"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));
    }

    public void submitRating() {
        isSubmitClicked = true;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "submitRating");
        parameters.put("iGeneralUserId", generalFunc.getMemberId());
        parameters.put("tripID", iTripId_str);
        parameters.put("rating", "" + ratingBar.getRating() + "");
        parameters.put("message", Utils.getText(commentBox));
        parameters.put("UserType", Utils.app_type);
        parameters.put("iMemberId", generalFunc.getMemberId());

        if (!iOrderId.equalsIgnoreCase("")) {
            parameters.put("iOrderId", iOrderId);
            parameters.put("eFromUserType", Utils.app_type);
            parameters.put("eToUserType", Utils.passenger_app_type);
            parameters.put("eSystem", Utils.eSystem_Type);
        }

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {

                    if (responseString != null && !responseString.equals("")) {

                        isSubmitClicked = true;
                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                            isSubmitClicked = false;
                            showBookingAlert(responseString);
                        } else {
                            isSubmitClicked = false;
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        }
                    } else {
                        isSubmitClicked = false;
                        generalFunc.showError();
                    }
                });

    }

    public void showBookingAlert(String responseString) {


        String eType = generalFunc.getJsonValue("eType", responseString);
        String titleTxt;
        String mesasgeTxt;
        if (generalFunc.getJsonValue("eType", responseString).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            titleTxt = generalFunc.retrieveLangLBl("Booking Successful", "LBL_JOB_FINISHED");
            mesasgeTxt = generalFunc.retrieveLangLBl("", "LBL_JOB_FINISHED_TXT");
        } else if (eType.equalsIgnoreCase("Deliver") || eType.equals(Utils.eType_Multi_Delivery)) {
            titleTxt = generalFunc.retrieveLangLBl("Booking Successful", "LBL_DELIVERY_SUCCESS_FINISHED");
            mesasgeTxt = generalFunc.retrieveLangLBl("", "LBL_DELIVERY_FINISHED_TXT");
        } else {
            titleTxt = generalFunc.retrieveLangLBl("Booking Successful", "LBL_SUCCESS_FINISHED");
            mesasgeTxt = generalFunc.retrieveLangLBl("", "LBL_TRIP_FINISHED_TXT");
        }
        if (!iOrderId.equalsIgnoreCase("")) {
            titleTxt = generalFunc.retrieveLangLBl("Booking Successful", "LBL_SUCCESS_FINISHED_DRDL");
            mesasgeTxt = generalFunc.retrieveLangLBl("", "LBL_FINISHED_DELIVERY_TXT");

        }
        if (data_trip.get("REQUEST_TYPE").equalsIgnoreCase("Bidding")) {
            titleTxt = generalFunc.retrieveLangLBl("", "LBL_TASK_COMPLETED_TXT");
            mesasgeTxt = generalFunc.retrieveLangLBl("", "LBL_TASK_FINISHED_TXT");
        }

        if (generalFunc.getJsonValue("isMedicalServiceTrip", responseString).equalsIgnoreCase("Yes")) {
            titleTxt = generalFunc.retrieveLangLBl("", "LBL_SUCCESS_FINISHED");
            mesasgeTxt = generalFunc.retrieveLangLBl("", "LBL_MEDICAL_TRIP_FINISHED_TXT");
        }


        SuccessDialog.showSuccessDialog(getActContext(), titleTxt, mesasgeTxt, generalFunc.retrieveLangLBl("Ok", "LBL_OK_THANKS"), false, () -> {
            generalFunc.saveGoOnlineInfo();
            MyApp.getInstance().refreshView(this, responseString);
        });


    }


    public Context getActContext() {
        return TripRatingActivity.this;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onLocationUpdate(Location location) {
        this.userLocation = location;
        CameraUpdate cameraPosition = generalFunc.getCameraPosition(userLocation, gMap);

        if (cameraPosition != null) {
            getMap().moveCamera(cameraPosition);

        }
    }

    public CameraPosition cameraForUserPosition() {


        if (userLocation == null) {
            return null;
        }


        double currentZoomLevel = getMap().getCameraPosition().zoom;

        if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
        }
        return new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude())).bearing(getMap().getCameraPosition().bearing)
                .zoom((float) currentZoomLevel).build();


    }

    public GoogleMap getMap() {
        return this.gMap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        if (generalFunc.checkLocationPermission(true)) {
            getMap().setMyLocationEnabled(true);
        }

        getMap().getUiSettings().setTiltGesturesEnabled(false);
        getMap().getUiSettings().setCompassEnabled(false);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);

        if (GetLocationUpdates.retrieveInstance() != null) {
            GetLocationUpdates.getInstance().stopLocationUpdates(this);
        }
        gMap.getUiSettings().setScrollGesturesEnabled(false);
        gMap.getUiSettings().setZoomGesturesEnabled(false);

        GetLocationUpdates.getInstance().startLocationUpdates(this, this);

    }


    public void onClick(View view) {
        int i = view.getId();
        Utils.hideKeyboard(TripRatingActivity.this);

        if (i == btn_type2.getId()) {
            if (!isSubmitClicked) {

                if (ratingBar.getRating() < 0.5) {
                    generalFunc.showMessage(generalFunc.getCurrentView(TripRatingActivity.this),
                            generalFunc.retrieveLangLBl("", "LBL_ERROR_RATING_DIALOG_TXT"));
                    return;
                }
                if (data_trip.get("REQUEST_TYPE").equalsIgnoreCase("Bidding")) {
                    submitBiddingRating();
                } else {
                    submitRating();
                }
            }
        }
    }


    private void submitBiddingRating() {
        isSubmitClicked = true;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "submitRatingBiddingService");
        parameters.put("iBiddingPostId", data_trip.get("TripId"));
        parameters.put("rating", "" + ratingBar.getRating() + "");
        parameters.put("message", Utils.getText(commentBox));

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {

                    if (responseString != null && !responseString.equals("")) {
                        isSubmitClicked = true;
                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                            isSubmitClicked = false;
                            showBookingAlert(responseString);
                        } else {
                            isSubmitClicked = false;
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        }
                    } else {
                        isSubmitClicked = false;
                        generalFunc.showError();
                    }
                });

    }
}
