package com.multixpro.user.deliverAll;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.adapter.files.deliverAll.TrackOrderAdapter;
import com.general.call.CommunicationManager;
import com.general.call.MediaDataProvider;
import com.general.files.ActUtils;
import com.general.files.DecimalDigitsInputFilter;
import com.general.files.MyApp;
import com.general.files.RecurringTask;
import com.general.files.UpdateDirections;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.multixpro.user.CardPaymentActivity;
import com.multixpro.user.HelpMainCategory23Pro;
import com.multixpro.user.MyWalletActivity;
import com.multixpro.user.PaymentWebviewActivity;
import com.multixpro.user.QuickPaymentActivity;
import com.multixpro.user.R;
import com.multixpro.user.UberXHomeActivity;
import com.multixpro.user.databinding.StatusLineBinding;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.map.BitmapDescriptorFactory;
import com.map.GeoMapLoader;
import com.map.Marker;
import com.map.Polyline;
import com.map.helper.SphericalUtil;
import com.map.models.JointType;
import com.map.models.LatLng;
import com.map.models.LatLngBounds;
import com.map.models.MarkerOptions;
import com.map.models.PatternItem;
import com.map.models.PolylineOptions;
import com.service.handler.ApiHandler;
import com.service.handler.AppService;
import com.service.model.EventInformation;
import com.utils.CommonUtilities;
import com.utils.LoadImage;
import com.utils.Logger;
import com.utils.MarkerAnim;
import com.utils.MyUtils;
import com.utils.Utils;
import com.utils.VectorUtils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TrackOrderActivity extends ParentActivity implements GeoMapLoader.OnMapReadyCallback, TrackOrderAdapter.ReviewItemClickListener {

    RecyclerView dataRecyclerView;
    TrackOrderAdapter adapter;
    GeoMapLoader.GeoMap geoMap;

    ArrayList<HashMap<String, String>> listData = new ArrayList<>();

    MTextView titleTxt;
    RecurringTask updateDriverLocTask;
    boolean isTaskKilled = false;
    public String iDriverId = "";
    Marker destMarker, driverMarker;
    LatLng driverLocation;
    MarkerAnim animDriverMarker;
    UpdateDirections updateDirections;
    Polyline polyline = null;

    // Tip feature - View Declarion Start
    AlertDialog giveTipAlertDialog;
    boolean showTipAddArea = false;
    AlertDialog alertDialog;
    private String tipAmount = "";
    private Dialog dialog_sucess;
    private boolean isPercentage;
    // Tip feature - View Declarion End

    MTextView pickedUpTimeTxtView;
    MTextView pickedUpTimeAbbrTxtView;
    MTextView pickedUpTxtView;
    MTextView distanceVTxtView;
    MTextView distanceVAbbrTxtView;
    MTextView distanceTxtView;

    public LatLng userLatLng = null, restLatLng = null;
    String eDisplayDottedLine = "", eDisplayRouteLine = "", CompanyAddress = "", DeliveryAddress = "";
    String etaVal = "", distanceValue = "", distanceUnit = "";

    LinearLayout finaldelArea, btn_cancelArea, vieDetailsArea;
    MTextView delTitleTxtView, delMsgTxtViewm, btn_help, btn_confirm, btn_help_txt, btn_confirm_txt, btn_cancel;
    LinearLayout btnConfirmarea;
    public static String serviceId = "", ETA = "";
    public String iOrderId = "";

    CardView timeArea;

    private String vImageDeliveryPref;
    private int imageWidth;
    String isContactLessDeliverySelected = "";
    private AlertDialog preferenceAlertDialog;
    LinearLayout contactLessDeliveryArea;
    MTextView contactLessDeliveryTxt;
    MTextView contactLessDeliveryHelpTxt;
    ImageView iv_preferenceImg;

    LinearLayout takeAwayArea;
    MTextView takeAwayDetailTxt;
    MTextView takeAwayPickedUpLocTxt;
    MTextView navigateBtn;
    String eTakeAway = "";
    String fTotalItemPrice = "";
    String LBL_TIP_AMOUNT = "";
    String userCurrencySymbol = "";
    String TIP_AMOUNT_1_VALUE = "";
    String TIP_AMOUNT_2_VALUE = "";
    String TIP_AMOUNT_3_VALUE = "";
    String eTakeAwayPickedUpNote = "";
    String prepareTime = "";
    private Double CompanyLat = 0.00, CompanyLong = 0.00;
    AlertDialog list_navigation;
    AlertDialog ConfirmGenieAlert;
    AlertDialog billDeatilsGenieAlert;
    String eBuyAnyService = "";
    boolean fromNoti = false;
    private static final int WEBVIEWPAYMENT = 001;
    String eWalletIgnore = "No";
    private RelativeLayout mainArea;
    private ProgressBar mProgressBar;
    String messageStr = "";
    Marker sourceMarker;
    Polyline linePoly;
    LinearLayout fareDetailDisplayArea;
    // TIP Feature Functionality Start
    MTextView tipAmountText1, tipAmountText2, tipAmountText3, tipAmountTextOther;
    MTextView errorTxt;
    MTextView tv_percentageAmount;
    LinearLayout tipAmountAreaOther, tipAmountArea1, tipAmountArea2, tipAmountArea3, addTipArea;
    LinearLayout amountArea;
    ImageView closeImgOther, closeImg1, closeImg2, closeImg3, iv_tip_help;
    EditText tipAmountBox;
    Menu menu;
    MenuItem menuTipItem;

    RelativeLayout bottomSheet, driverDetailArea, contactArea;
    LinearLayout contentArea, etaArea, statusLine;
    int statusLineSize = 0;
    View orderStatusMainArea;
    RecyclerView bottomStatusRecyclerView;
    MTextView currentStatusTxt, currentStatusTimeTxt, driverNametxt, driverRating;
    private MTextView itemCountTxt, itemPriceTxt, currentStatusDateTxt;
    ImageView imgChat, imgCall;
    SelectableRoundedImageView imgDriver;
    BottomSheetBehavior bottomSheetBehavior;
    NestedScrollView orderScrollView;

    //rating view
    View btnRatingArea;
    MTextView btn_rating_txt;
    boolean isDeliverNotify = false;
    public String vOrderNo = "";
    public String driverName = "";
    public String vCompany = "";
    public String eTakeaway = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        generalFunc.setOverflowButtonColor(toolbar, getResources().getColor(R.color.white));

        fromNoti = getIntent().getBooleanExtra("fromNoti", false);


        if (getIntent().getStringExtra("iOrderId") != null) {
            iOrderId = getIntent().getStringExtra("iOrderId");
        }

        isDeliverNotify = getIntent().getBooleanExtra("isDeliverNotify", false);
        LBL_TIP_AMOUNT = generalFunc.retrieveLangLBl("", "LBL_TIP_AMOUNT");
        animDriverMarker = new MarkerAnim();
        btnConfirmarea = (LinearLayout) findViewById(R.id.btnConfirmarea);
        addToClickHandler(btnConfirmarea);

        // new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), 5, 0, 0, btnConfirmarea);
        imageWidth = (int) this.getResources().getDimension(R.dimen._90sdp);


        setSupportActionBar(toolbar);
        ImageView backImgView = (ImageView) findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }

        addToClickHandler(backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);
        delTitleTxtView = (MTextView) findViewById(R.id.delTitleTxtView);
        finaldelArea = (LinearLayout) findViewById(R.id.finaldelArea);
        iv_preferenceImg = (ImageView) findViewById(R.id.iv_preferenceImg);
        orderScrollView = (NestedScrollView) findViewById(R.id.orderScrollView);

        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.GONE);
        mainArea = (RelativeLayout) findViewById(R.id.mainArea);
        mainArea.setVisibility(View.INVISIBLE);

        takeAwayArea = (LinearLayout) findViewById(R.id.takeAwayArea);
        takeAwayDetailTxt = (MTextView) findViewById(R.id.takeAwayDetailTxt);
        takeAwayPickedUpLocTxt = (MTextView) findViewById(R.id.takeAwayPickedUpLocTxt);
        navigateBtn = (MTextView) findViewById(R.id.navigateBtn);
        addToClickHandler(navigateBtn);
        navigateBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NAVIGATE"));

        btn_cancelArea = (LinearLayout) findViewById(R.id.btn_cancelArea);
        contactLessDeliveryArea = (LinearLayout) findViewById(R.id.contactLessDeliveryArea);
        contactLessDeliveryTxt = (MTextView) findViewById(R.id.contactLessDeliveryTxt);
        contactLessDeliveryHelpTxt = (MTextView) findViewById(R.id.contactLessDeliveryHelpTxt);
        contactLessDeliveryHelpTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DIS_HOW_IT_WORKS"));
        contactLessDeliveryTxt.setText(generalFunc.retrieveLangLBl("ContactLess Delivery", "LBL_CONTACT_LESS_DELIVERY_TXT"));
        addToClickHandler(contactLessDeliveryArea);
        vieDetailsArea = (LinearLayout) findViewById(R.id.vieDetailsArea);
        addToClickHandler(vieDetailsArea);


        delMsgTxtViewm = (MTextView) findViewById(R.id.delMsgTxtView);
        btn_help = (MTextView) findViewById(R.id.btn_help);
        btn_cancel = (MTextView) findViewById(R.id.btn_cancel);
        btn_help_txt = (MTextView) findViewById(R.id.btn_help_txt);
        btn_confirm = (MTextView) findViewById(R.id.btn_confirm);
        btn_confirm_txt = (MTextView) findViewById(R.id.btn_confirm_txt);
        btnRatingArea = findViewById(R.id.btnRatingArea);
        btn_rating_txt = findViewById(R.id.btn_rating_txt);
        addToClickHandler(btnRatingArea);
        //btn_help.setOnClickListener(new setOnClickList());
        addToClickHandler(btn_cancel);

        timeArea = (CardView) findViewById(R.id.timeArea);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), 5, 0, 0, btn_cancel);

        pickedUpTimeTxtView = (MTextView) findViewById(R.id.pickedUpTimeTxtView);
        pickedUpTimeAbbrTxtView = (MTextView) findViewById(R.id.pickedUpTimeAbbrTxtView);
        pickedUpTxtView = (MTextView) findViewById(R.id.pickedUpTxtView);
        distanceVTxtView = (MTextView) findViewById(R.id.distanceVTxtView);
        distanceVAbbrTxtView = (MTextView) findViewById(R.id.distanceVAbbrTxtView);
        distanceTxtView = (MTextView) findViewById(R.id.distanceTxtView);

        bottomSheet = findViewById(R.id.bottom_sheet_behavior_id);
        driverDetailArea = (RelativeLayout) findViewById(R.id.driverDetailArea);

        contactArea = (RelativeLayout) findViewById(R.id.contactArea);
        contentArea = (LinearLayout) findViewById(R.id.contentArea);
        statusLine = (LinearLayout) findViewById(R.id.statusLine);

        etaArea = (LinearLayout) findViewById(R.id.etaArea);
        orderStatusMainArea = (View) findViewById(R.id.orderStatusMainArea);
        bottomStatusRecyclerView = (RecyclerView) findViewById(R.id.bottomStatusRecyclerView);
        currentStatusTxt = (MTextView) findViewById(R.id.currentStatusTxt);
        currentStatusTimeTxt = (MTextView) findViewById(R.id.currentStatusTimeTxt);

        itemCountTxt = (MTextView) findViewById(R.id.itemCountTxt);
        itemPriceTxt = (MTextView) findViewById(R.id.itemPriceTxt);
        currentStatusDateTxt = (MTextView) findViewById(R.id.currentStatusDateTxt);

        driverNametxt = (MTextView) findViewById(R.id.driverNametxt);
        driverRating = (MTextView) findViewById(R.id.driverRating);
        imgDriver = findViewById(R.id.imgDriver);
        imgDriver.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_no_pic_user, null));
        imgChat = (ImageView) findViewById(R.id.imgChat);
        imgCall = (ImageView) findViewById(R.id.imgCall);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        manageBottomSheeetDefaultHeight((int) getResources().getDimension(R.dimen._120sdp));


        (new GeoMapLoader(this, R.id.mapFragmentContainer)).bindMap(this);


        // setting the bottom sheet callback for interacting with state changes and sliding
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        driverDetailArea.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        orderScrollView.fullScroll(View.FOCUS_UP);
                        orderScrollView.smoothScrollTo(0, 0);
                        if (etaArea.getVisibility() == View.VISIBLE) {
                            driverDetailArea.setVisibility(View.VISIBLE);
                        }
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });


        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //map.setListener(() -> (bottomSheet.requestDisallowInterceptTouchEvent(false));

        delTitleTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_DELIVERED"));
        delMsgTxtViewm.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_DELIVER_MSG"));
        btn_confirm.setText(generalFunc.retrieveLangLBl("", "LBL_OK_GOT_IT"));
        btn_cancel.setText(generalFunc.retrieveLangLBl("", "LBL_OK_GOT_IT"));
        btn_help.setText(generalFunc.retrieveLangLBl("", "LBL_NOT_DELIVERD"));
        btn_confirm_txt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERD_NOTE"));
        btn_help_txt.setText(generalFunc.retrieveLangLBl("", "LBL_NOTDELIVERD_NOTE"));
        pickedUpTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_ETA_TXT"));
        distanceTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_ETA_TXT"));
        btn_rating_txt.setText(generalFunc.retrieveLangLBl("", "LBL_RATE_ORDER_COMPLETED"));

        distanceVTxtView.setText("--");
        distanceVAbbrTxtView.setText("");

        pickedUpTimeTxtView.setText("--");
        pickedUpTimeAbbrTxtView.setText("");
        if (getIntent().getStringExtra("iOrderId") != null) {
            iOrderId = getIntent().getStringExtra("iOrderId");
        }

        adapter = new TrackOrderAdapter(getActContext(), generalFunc, listData, this);
        dataRecyclerView.setAdapter(adapter);
        bottomStatusRecyclerView.setAdapter(adapter);
        if (isDeliverNotify) {
            finaldelArea.setVisibility(View.VISIBLE);
            btnRatingArea.setVisibility(View.VISIBLE);

        }
    }

    public void setEta(String val, String distanceValue, String distanceUnit) {
        if (ETA.equalsIgnoreCase("")) {
            if (eDisplayRouteLine.equalsIgnoreCase("yes")) {
                runOnUiThread(() -> {
                    if (restLatLng != null && userLatLng != null) {
                        etaVal = val;
                        this.distanceValue = distanceValue;
                        this.distanceUnit = distanceUnit;

                        distanceVTxtView.setText(generalFunc.convertNumberWithRTL(etaVal));
                        pickedUpTimeTxtView.setText(generalFunc.convertNumberWithRTL(etaVal));

                        generateMapLocations(restLatLng.latitude, restLatLng.longitude, userLatLng.latitude, userLatLng.longitude);
                        if (driverLocation != null) {
                            updateDriverLocation(driverLocation);
                        }
                    }
                });
            }
        }

    }

    public void setTaskKilledValue(boolean isTaskKilled) {
        this.isTaskKilled = isTaskKilled;
        if (isTaskKilled) {
            onPauseCalled();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setTaskKilledValue(true);

        unSubscribeToDriverLocChannel();

    }

    public void onPauseCalled() {

        if (updateDriverLocTask != null) {
            updateDriverLocTask.stopRepeatingTask();
        }
        updateDriverLocTask = null;

        unSubscribeToDriverLocChannel();
    }

    public void subscribeToDriverLocChannel() {

        if (!iDriverId.equalsIgnoreCase("")) {

            ArrayList<String> channelName = new ArrayList<>();
            channelName.add(Utils.pubNub_Update_Loc_Channel_Prefix + iDriverId);
            AppService.getInstance().executeService(new EventInformation.EventInformationBuilder().setChanelList(channelName).build(), AppService.Event.SUBSCRIBE);
        }


    }

    public void pubnubMessage(JSONObject obj_msg) {
        //Logger.d("TrackOrderPubNub", "::" + obj_msg);


        if (generalFunc.getJsonValueStr("CustomNotification", obj_msg) != null && generalFunc.getJsonValueStr("CustomNotification", obj_msg).equalsIgnoreCase("yes") && MyApp.getInstance().isMyAppInBackGround()) {
            MyApp.getInstance().getLoclaNotificationObj().customNotification(getApplicationContext(), obj_msg.toString());

            String messageStr = generalFunc.getJsonValueStr("Message", obj_msg);
            if (messageStr.equalsIgnoreCase("OrderDelivered") || messageStr.equalsIgnoreCase("OrderDeclineByRestaurant") || messageStr.equalsIgnoreCase("OrderCancelByAdmin")) {
                //Logger.d("TrackOrderPubNub", " :: MyAppInBackGround");
            } else {
                return;
            }
        }

        String messageStr = generalFunc.getJsonValueStr("Message", obj_msg);
        String iOrderId_ = generalFunc.getJsonValueStr("iOrderId", obj_msg);
        String eTakeaway = generalFunc.getJsonValueStr("eTakeaway", obj_msg);

        String vImageDeliveryPref_ = generalFunc.getJsonValueStr("vImageDeliveryPref", obj_msg);

        if (!messageStr.equals("")) {
            String vTitle = generalFunc.getJsonValueStr("vTitle", obj_msg);

            switch (messageStr) {
                case "CabRequestAccepted":
                case "GenieOrderCancelByDriver":
                case "OrderPickedup":
                case "OrderConfirmByRestaurant":
                    handleDialog(false, vTitle);
                    break;
                case "OrderReviewItems":
                    if (this.messageStr.equalsIgnoreCase(messageStr)) {
                        return;
                    }
                    this.messageStr = messageStr;
                    handleDialog(false, vTitle);
                    break;
                case "OrderDeclineByRestaurant":
                case "OrderCancelByAdmin":
                    if (iOrderId_.equalsIgnoreCase(iOrderId)) {
                        runOnUiThread(() -> {
                            etaArea.setVisibility(View.GONE);
                            finaldelArea.setVisibility(View.VISIBLE);
                            btn_help_txt.setVisibility(View.GONE);
                            btn_confirm_txt.setVisibility(View.GONE);
                            btnConfirmarea.setVisibility(View.GONE);
                            btn_cancelArea.setVisibility(View.VISIBLE);
                            vieDetailsArea.setVisibility(View.GONE);
                            findViewById(R.id.btnMainConfirmarea).setVisibility(View.GONE);
                            delTitleTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_CANCELLED"));
                            delTitleTxtView.setTextColor(getActContext().getResources().getColor(R.color.red));
                            delMsgTxtViewm.setText(vTitle);
                        });
                    }
                    break;
                case "OrderDelivered":
                    if (iOrderId_.equalsIgnoreCase(iOrderId)) {
                        try {
                            runOnUiThread(() -> {
                                etaArea.setVisibility(View.GONE);
                                finaldelArea.setVisibility(View.VISIBLE);
                                btnRatingArea.setVisibility(View.VISIBLE);
                                getTrackDetails();
                                if (Utils.checkText(vImageDeliveryPref_)) {
                                    new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(getActContext(), vImageDeliveryPref_, imageWidth, imageWidth)), iv_preferenceImg).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();
                                    iv_preferenceImg.setVisibility(View.VISIBLE);
                                }

                                if (eTakeaway.equalsIgnoreCase("Yes")) {
                                    delMsgTxtViewm.setText(vTitle);
                                    takeAwayArea.setVisibility(View.GONE);
                                    btn_confirm_txt.setVisibility(View.GONE);
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                    break;
            }
        } else if (generalFunc.getJsonValueStr("MsgType", obj_msg) != null && !generalFunc.getJsonValueStr("MsgType", obj_msg).equalsIgnoreCase("")) {
            pubNubMsgArrived(obj_msg.toString(), true);
        }

    }

    public void handleDialog(boolean isFinish, String vTitle) {
        runOnUiThread(() -> {
            try {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    if (isFinish) {
                        finish();
                    } else {
                        getTrackDetails();
                    }
                });
                generateAlert.setContentMessage("", vTitle);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();
            } catch (Exception e) {
                getTrackDetails();
                e.printStackTrace();
            }
        });

    }

    public void unSubscribeToDriverLocChannel() {
        if (!iDriverId.equalsIgnoreCase("")) {
            ArrayList<String> channelName = new ArrayList<>();
            channelName.add(Utils.pubNub_Update_Loc_Channel_Prefix + iDriverId);
            AppService.getInstance().executeService(new EventInformation.EventInformationBuilder().setChanelList(channelName).build(), AppService.Event.UNSUBSCRIBE);
        }

    }

    public void pubNubMsgArrived(final String message, final Boolean ishow) {

        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);
            String DriverId = generalFunc.getJsonValue("iDriverId", message);

            if (msgType.equals("LocationUpdateOnTrip") && DriverId.equalsIgnoreCase(iDriverId)) {
                String vLatitude = generalFunc.getJsonValue("vLatitude", message);
                String vLongitude = generalFunc.getJsonValue("vLongitude", message);

                Location driverLoc = new Location("Driverloc");
                driverLoc.setLatitude(generalFunc.parseDoubleValue(0.0, vLatitude));
                driverLoc.setLongitude(generalFunc.parseDoubleValue(0.0, vLongitude));


                if (updateDirections != null) {
                    updateDirections.changeUserLocation(driverLoc);
                }

                if (eDisplayRouteLine.equalsIgnoreCase("Yes")) {
                    callUpdateDeirection(driverLoc);
                }

                LatLng driverLocation_update = new LatLng(generalFunc.parseDoubleValue(0.0, vLatitude),
                        generalFunc.parseDoubleValue(0.0, vLongitude));

                driverLocation = driverLocation_update;
                updateDriverLocation(driverLocation_update);

            }
        });
    }

    public void callUpdateDeirection(Location driverlocation) {
        if (userLatLng == null) {
            return;

        }
        if (updateDirections == null) {

            Location location = new Location("userLocation");
            location.setLatitude(userLatLng.latitude);
            location.setLongitude(userLatLng.longitude);
            updateDirections = new UpdateDirections(getActContext(), geoMap, driverlocation, location);
            updateDirections.scheduleDirectionUpdate();
        }

    }

    public void updateDriverLocation(LatLng latlog) {
        if (latlog == null) {
            return;
        }

        if (driverMarker == null) {
            try {
                if (geoMap != null) {
                    MarkerOptions markerOptions = new MarkerOptions();

                    int iconId = R.mipmap.car_driver_6;

                    markerOptions.position(latlog).title("DriverId" + iDriverId).icon(BitmapDescriptorFactory.fromResource(iconId))
                            .anchor(0.5f, 0.5f).flat(true);

                    driverMarker = geoMap.addMarker(markerOptions);

                    driverLocation = latlog;
                    geoMap.moveCamera(cameraForDriverPosition());


                }
            } catch (Exception e) {
            }
        } else {

            if (driverMarker != null) {
                driverMarker.remove();
            }
            if (geoMap != null) {
                MarkerOptions markerOptions = new MarkerOptions();

                int iconId = R.mipmap.car_driver_6;

                markerOptions.position(latlog).title("DriverId" + iDriverId).icon(BitmapDescriptorFactory.fromResource(iconId))
                        .anchor(0.5f, 0.5f).flat(true);

                driverMarker = geoMap.addMarker(markerOptions);

                driverLocation = latlog;

            }


            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(driverLocation);
            if (eDisplayDottedLine.equalsIgnoreCase("No") && eDisplayRouteLine.equalsIgnoreCase("No")) {
                //driver  to resturant bounding
                builder.include(restLatLng);

            } else if (eDisplayRouteLine.equalsIgnoreCase("Yes")) {
                //driver to user bounding
                builder.include(userLatLng);

            }

            LatLngBounds bounds = builder.build();

            if (geoMap != null && geoMap.getView() != null) {
                try {
                    int width = geoMap.getView().getMeasuredWidth();
                    int height = geoMap.getView().getMeasuredHeight();
                    height = (height - (int) (height * 0.40));
                    int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

                    geoMap.animateCamera(bounds, width, height, padding);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


    }

    public LatLng cameraForDriverPosition() {

        double currentZoomLevel = geoMap == null ? Utils.defaultZomLevel : geoMap.getCameraPosition().zoom;

        if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
        }
        if (driverLocation != null) {
            return new LatLng(this.driverLocation.latitude, this.driverLocation.longitude, (float) currentZoomLevel);
        } else {
            return null;
        }
    }

    public Context getActContext() {
        return TrackOrderActivity.this;
    }

    @Override
    public void onMapReady(GeoMapLoader.GeoMap geoMap) {
        this.geoMap = geoMap;
        geoMap.getUiSettings().setMapToolbarEnabled(false);

        geoMap.setPadding(0, 0, 0, 70);
    }

    public void generateMapLocations(double resLat, double resLong, double userLat, double userLong) {
        LatLng sourceLnt = new LatLng(resLat, resLong);
        restLatLng = sourceLnt;

        if (sourceMarker != null) {
            sourceMarker.remove();
        }
        if (destMarker != null) {
            destMarker.remove();
        }
        if (!iDriverId.equalsIgnoreCase("")) {
            if (linePoly != null) {
                linePoly.remove();
            }

        }

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View restaurantMarkerView = inflater.inflate(R.layout.marker_view, null);


        ImageView restaurantPinImgView = (ImageView) restaurantMarkerView.findViewById(R.id.pinImgView);
        restaurantPinImgView.setImageResource(R.mipmap.ic_track_restaurant);

        MTextView restaurantMarkerTxtView = (MTextView) restaurantMarkerView.findViewById(R.id.addressTxtView);
        restaurantMarkerTxtView.setText(CompanyAddress);

        View userMarkerView = inflater.inflate(R.layout.marker_view, null);
        ImageView userPinImgView = (ImageView) userMarkerView.findViewById(R.id.pinImgView);
        userPinImgView.setImageResource(R.mipmap.ic_track_user);


        MTextView userMarkerTxtView = (MTextView) userMarkerView.findViewById(R.id.addressTxtView);
        userMarkerTxtView.setText(DeliveryAddress);
        userMarkerTxtView.setTextColor(getResources().getColor(R.color.white));
        userMarkerTxtView.setBackgroundColor(getResources().getColor(R.color.black));

        sourceMarker = geoMap.addMarker(new MarkerOptions().position(sourceLnt).icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromView(restaurantMarkerView))));


        LatLng destLnt = new LatLng(userLat, userLong);
        userLatLng = destLnt;
        if (!eTakeAway.equalsIgnoreCase("Yes")) {
            destMarker = geoMap.addMarker(new MarkerOptions().position(destLnt).icon(BitmapDescriptorFactory.fromBitmap(Utils.getBitmapFromView(userMarkerView))));
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(sourceMarker.getPosition());
        if (!eTakeAway.equalsIgnoreCase("Yes")) {
            builder.include(destMarker.getPosition());
        }

        LatLngBounds bounds = builder.build();


        if (geoMap != null && geoMap.getView() != null) {
            try {
                int width = geoMap.getView().getMeasuredWidth();
                int height = geoMap.getView().getMeasuredHeight();
                height = (height - (int) (height * 0.40));
                int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

                geoMap.animateCamera(bounds, width, height, padding);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (!eTakeAway.equalsIgnoreCase("Yes")) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions
                    .jointType(JointType.ROUND)
                    .pattern(Arrays.asList(PatternItem.Gap(20), PatternItem.Dash(20)))
                    .add(sourceMarker.getPosition())
                    .add(destMarker.getPosition());
            polylineOptions.width(Utils.dipToPixels(getActContext(), 4));


            if (iDriverId.equalsIgnoreCase("")) {
                linePoly = geoMap.addPolyline(polylineOptions);
                linePoly.setColor(Color.parseColor("#cecece"));
                linePoly.setStartCap(geoMap.getRoundCap());
                linePoly.setEndCap(geoMap.getRoundCap());
            }

            if (!eTakeAway.equalsIgnoreCase("Yes")) {
                buildArcLine(sourceLnt, destLnt, 0.050);
            }
        }
    }

    private void buildArcLine(LatLng p1, LatLng p2, double arcCurvature) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        if (h < 0) {
            LatLng tmpP1 = p1;
            p1 = p2;
            p2 = tmpP1;

            d = SphericalUtil.computeDistanceBetween(p1, p2);
            h = SphericalUtil.computeHeading(p1, p2);
        }

        //Midpoint position
        LatLng midPointLnt = SphericalUtil.computeOffset(p1, d * 0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1 - arcCurvature * arcCurvature) * d * 0.5 / (2 * arcCurvature);
        double r = (1 + arcCurvature * arcCurvature) * d * 0.5 / (2 * arcCurvature);

        LatLng centerLnt = SphericalUtil.computeOffset(midPointLnt, x, h + 90.0);

        //Polyline options
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(PatternItem.Dash(30), PatternItem.Gap(20));

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(centerLnt, p1);
        double h2 = SphericalUtil.computeHeading(centerLnt, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numPoints = 100;
        double step = (h2 - h1) / numPoints;

        for (int i = 0; i < numPoints; i++) {
            LatLng middlePointTemp = SphericalUtil.computeOffset(centerLnt, r, h1 + i * step);
            options.add(middlePointTemp);
        }


        if (!eDisplayDottedLine.equalsIgnoreCase("") && eDisplayDottedLine.equalsIgnoreCase("Yes")) {
            //Draw polyline
            if (polyline != null) {
                polyline.remove();
                polyline = null;


            }
            polyline = geoMap.addPolyline(options.width(10).color(Color.BLACK).geodesic(false).pattern(pattern));
        } else {
            if (polyline != null) {
                polyline.remove();
                polyline = null;


            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTrackDetails();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void getTrackDetails() {
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("type", "getOrderDeliveryLog");
            parameters.put("iOrderId", iOrderId);
            parameters.put("iUserId", generalFunc.getMemberId());
            parameters.put("UserType", Utils.userType);
            parameters.put("eSystem", Utils.eSystem_Type);

            mProgressBar.setVisibility(View.VISIBLE);
            ApiHandler.execute(getActContext(), parameters, responseString -> {
                mProgressBar.setVisibility(View.GONE);
                if (responseString != null && !responseString.equals("")) {
                    mainArea.setVisibility(View.VISIBLE);
                    String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                    if (action.equals("1")) {
                        listData.clear();

                        vOrderNo = generalFunc.getJsonValue("vOrderNo", responseString);
                        driverName = generalFunc.getJsonValue("driverName", responseString);
                        vCompany = generalFunc.getJsonValue("vCompany", responseString);
                        eTakeaway = generalFunc.getJsonValue("eTakeAway", responseString);

                        if (generalFunc.getJsonValue("iServiceId", responseString) != null && !generalFunc.getJsonValue("iServiceId", responseString).equalsIgnoreCase("")) {
                            serviceId = generalFunc.getJsonValue("iServiceId", responseString);
                        }
                        ETA = generalFunc.getJsonValue("ETA", responseString);

                        eBuyAnyService = generalFunc.getJsonValue("eBuyAnyService", responseString);

                        eDisplayDottedLine = generalFunc.getJsonValue("eDisplayDottedLine", responseString);
                        eDisplayRouteLine = generalFunc.getJsonValue("eDisplayRouteLine", responseString);


                        CompanyAddress = generalFunc.getJsonValue("CompanyAddress", responseString);
                        DeliveryAddress = generalFunc.getJsonValue("DeliveryAddress", responseString);
                        isContactLessDeliverySelected = generalFunc.getJsonValue("isContactLessDeliverySelected", responseString);
                        contactLessDeliveryArea.setVisibility(isContactLessDeliverySelected.equalsIgnoreCase("Yes") ? View.VISIBLE : View.GONE);
                        prepareTime = generalFunc.getJsonValue("prepareTime", responseString);
                        eTakeAway = generalFunc.getJsonValue("eTakeAway", responseString);
                        TIP_AMOUNT_1_VALUE = generalFunc.getJsonValue("TIP_AMOUNT_1_VALUE", responseString);
                        fTotalItemPrice = generalFunc.getJsonValue("fTotalItemPrice", responseString);
                        userCurrencySymbol = generalFunc.getJsonValue("userCurrencySymbol", responseString);
                        TIP_AMOUNT_2_VALUE = generalFunc.getJsonValue("TIP_AMOUNT_2_VALUE", responseString);
                        TIP_AMOUNT_3_VALUE = generalFunc.getJsonValue("TIP_AMOUNT_3_VALUE", responseString);
                        showTipAddArea = generalFunc.getJsonValue("showTipAddArea", responseString).equalsIgnoreCase("Yes");
                        menuInvisible();
                        if (eTakeAway.equalsIgnoreCase("Yes")) {
                            vieDetailsArea.setVisibility(View.GONE);
                        }
                        eTakeAwayPickedUpNote = generalFunc.getJsonValue("eTakeAwayPickedUpNote", responseString);

                        takeAwayDetailTxt.setText(prepareTime);

                        boolean isETakeAway = Utils.checkText(eTakeAway) && eTakeAway.equalsIgnoreCase("Yes");

                        if (finaldelArea.getVisibility() != View.VISIBLE) {
                            delTitleTxtView.setText(generalFunc.retrieveLangLBl("", isETakeAway ? "LBL_TAKE_AWAY_ORDER_PICKEDUP_TXT" : "LBL_ORDER_DELIVERED"));
                            delMsgTxtViewm.setText(isETakeAway ? eTakeAwayPickedUpNote : generalFunc.retrieveLangLBl("", "LBL_ORDER_DELIVER_MSG"));
                            btn_confirm_txt.setVisibility(isETakeAway ? View.GONE : View.VISIBLE);

                            if (eBuyAnyService.equalsIgnoreCase("Yes")) {
                                btn_confirm_txt.setVisibility(View.GONE);
                            }
                            timeArea.setVisibility(View.GONE);
                            etaArea.setVisibility(View.GONE);
                            driverDetailArea.setVisibility(View.GONE);
                            btn_help.setText(generalFunc.retrieveLangLBl("", isETakeAway ? "LBL_TAKE_AWAY_HELP_TXT" : "LBL_NOT_DELIVERD"));

                            takeAwayPickedUpLocTxt.setText(CompanyAddress);
                            takeAwayArea.setVisibility(Utils.checkText(prepareTime) && isETakeAway ? View.VISIBLE : View.GONE);
                        } else {
                            takeAwayArea.setVisibility(View.GONE);
                        }

                        if (finaldelArea.getVisibility() == View.VISIBLE) {
                            btn_confirm_txt.setVisibility(eBuyAnyService.equalsIgnoreCase("Yes") ? View.GONE : View.VISIBLE);
                        }

                        String DriverPhone = generalFunc.getJsonValue("DriverPhone", responseString);
                        String OrderPickedUpDate = generalFunc.getJsonValue("OrderPickedUpDate", responseString);

                        if (OrderPickedUpDate.trim().equalsIgnoreCase("")) {
                            pickedUpTimeTxtView.setText("--");
                            pickedUpTimeAbbrTxtView.setText("");
                        } else {
                            String pickUpDate = generalFunc.getDateFormatedType(generalFunc.getJsonValue("OrderPickedUpDate", responseString), Utils.OriginalDateFormate, "hh:mm aa");
                            String[] pickUpDateArr = pickUpDate.split(" ");

//                                pickedUpTimeTxtView.setText(generalFunc.convertNumberWithRTL(pickUpDateArr[0]));
//                                pickedUpTimeAbbrTxtView.setText(pickUpDateArr[1]);
                        }


                        if (generalFunc.getJsonValue("iDriverId", responseString) != null && !generalFunc.getJsonValue("iDriverId", responseString).equalsIgnoreCase("")) {
                            iDriverId = generalFunc.getJsonValue("iDriverId", responseString);


                        }
                        CompanyLat = generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("CompanyLat", responseString));
                        CompanyLong = generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("CompanyLong", responseString));

                        generateMapLocations(CompanyLat, CompanyLong, generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("PassengerLat", responseString)), generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("PassengerLong", responseString)));

                        titleTxt.setText("#" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vOrderNo", responseString)));

                        String tOrderRequestDate = generalFunc.getJsonValue("tOrderRequestDate", responseString);

                        String formattedDate = generalFunc.getDateFormatedType(tOrderRequestDate, Utils.OriginalDateFormate, CommonUtilities.OriginalDateFormate);
                        currentStatusDateTxt.setText(generalFunc.convertNumberWithRTL(formattedDate));
                        itemCountTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("TotalOrderItems", responseString)));
                        itemPriceTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("fNetTotal", responseString)));


                        JSONArray messageArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        if (generalFunc.getJsonValue("iDriverId", responseString) != null && !generalFunc.getJsonValue("iDriverId", responseString).equalsIgnoreCase("")
                                && !generalFunc.getJsonValue("iDriverId", responseString).equalsIgnoreCase("0")) {
                            iDriverId = generalFunc.getJsonValue("iDriverId", responseString);

                            //  unSubscribeToDriverLocChannel();

                            LatLng driverLocation_update = new LatLng(generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("DriverLat", responseString)),
                                    generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("DriverLong", responseString)));

                            driverLocation = driverLocation_update;
                            //timeArea.setVisibility(View.GONE);
                            if (eDisplayRouteLine.equalsIgnoreCase("Yes") && !eTakeAway.equalsIgnoreCase("Yes")) {
                                Location driverLoc = new Location("Driverloc");
                                driverLoc.setLatitude(driverLocation_update.latitude);
                                driverLoc.setLongitude(driverLocation_update.longitude);
                                callUpdateDeirection(driverLoc);
                                timeArea.setVisibility(View.GONE);

                                if (finaldelArea.getVisibility() == View.VISIBLE) {
                                    manageBottomSheeetDefaultHeight((int) getResources().getDimension(R.dimen._120sdp));
                                } else {
                                    manageBottomSheeetDefaultHeight((int) getResources().getDimension(R.dimen._230sdp));
                                }

                                if (finaldelArea.getVisibility() != View.VISIBLE) {
                                    if (!generalFunc.getJsonValue("OrderCurrentStatusCode", responseString).equalsIgnoreCase("6")) {
                                        etaArea.setVisibility(View.VISIBLE);
                                        driverDetailArea.setVisibility(View.VISIBLE);
                                    }
                                }


                                // TODO: 25-12-2021  set driver image
                                new LoadImage.builder(LoadImage.bind(generalFunc.getJsonValue("driverImage", responseString)), imgDriver).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();

                                driverNametxt.setText(generalFunc.getJsonValue("driverName", responseString));
                                driverRating.setText(generalFunc.getJsonValue("driverAvgRating", responseString));
                                //  driverDetailArea.setVisibility(View.GONE);
                            }

                            updateDriverLocation(driverLocation);
                            subscribeToDriverLocChannel();

                        }

                        String RIDE_DRIVER_CALLING_METHOD = generalFunc.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", obj_userProfile);
                        String CALLMASKING_ENABLED = generalFunc.getJsonValueStr("CALLMASKING_ENABLED", obj_userProfile);
                        String vName = generalFunc.getJsonValueStr("vName", obj_userProfile);
                        String vImgName = generalFunc.getJsonValueStr("vImgName", obj_userProfile);

                        String LBL_INCOMING_CALL = generalFunc.retrieveLangLBl("", "LBL_INCOMING_CALL");

                        statusLineSize = statusLine.getMeasuredWidth() / messageArr.length();
                        if (statusLine.getChildCount() > 0) {
                            statusLine.removeAllViewsInLayout();
                        }

                        for (int i = 0; i < messageArr.length(); i++) {
                            JSONObject rowObject = generalFunc.getJsonObject(messageArr, i);
                            HashMap<String, String> mapData = new HashMap<>();
                            mapData.put("vStatus", generalFunc.getJsonValueStr("vStatus_Track", rowObject));
                            mapData.put("vStatusTitle", generalFunc.getJsonValueStr("vStatus", rowObject));
                            mapData.put("eShowCallImg", generalFunc.getJsonValueStr("eShowCallImg", rowObject));
                            mapData.put("iStatusCode", generalFunc.getJsonValueStr("iStatusCode", rowObject));
                            mapData.put("OrderCurrentStatusCode", generalFunc.getJsonValueStr("OrderCurrentStatusCode", rowObject));
                            mapData.put("eCompleted", generalFunc.getJsonValueStr("eCompleted", rowObject));
                            mapData.put("driverImage", generalFunc.getJsonValueStr("driverImage", rowObject));
                            mapData.put("driverName", generalFunc.getJsonValueStr("driverName", rowObject));
                            mapData.put("iDriverId", generalFunc.getJsonValueStr("iDriverId", rowObject));
                            mapData.put("driverImageUrl", mapData.get("driverImage"));
                            mapData.put("DriverPhone", DriverPhone);
                            mapData.put("RIDE_DRIVER_CALLING_METHOD", RIDE_DRIVER_CALLING_METHOD);
                            mapData.put("CALLMASKING_ENABLED", CALLMASKING_ENABLED);
                            mapData.put("vName", vName);
                            mapData.put("vImgName", vImgName);
                            mapData.put("LBL_INCOMING_CALL", LBL_INCOMING_CALL);
                            mapData.put("showPaymentButton", generalFunc.getJsonValueStr("showPaymentButton", rowObject));
                            mapData.put("showViewBillButton", generalFunc.getJsonValueStr("showViewBillButton", rowObject));
                            mapData.put("fStoreBillAmount", generalFunc.getJsonValueStr("fStoreBillAmount", rowObject));
                            if (generalFunc.getJsonValueStr("genieWaitingForUserApproval", rowObject) != null) {
                                JSONObject itemForReview = generalFunc.getJsonObject("itemForReview", rowObject);
                                mapData.put("genieWaitingForUserApproval", generalFunc.getJsonValueStr("genieWaitingForUserApproval", rowObject));
                                mapData.put("iOrderId", generalFunc.getJsonValueStr("iOrderId", itemForReview));
                                mapData.put("iOrderDetailId", generalFunc.getJsonValueStr("iOrderDetailId", itemForReview));
                                mapData.put("iItemDetailsId", generalFunc.getJsonValueStr("iItemDetailsId", itemForReview));
                                mapData.put("iQty", generalFunc.getJsonValueStr("iQty", itemForReview));
                                mapData.put("MenuItem", generalFunc.getJsonValueStr("MenuItem", itemForReview));
                                mapData.put("fTotPrice", generalFunc.getJsonValueStr("fTotPrice", itemForReview));
                                mapData.put("vImage", generalFunc.getJsonValueStr("vImage", itemForReview));
                                mapData.put("eItemAvailable", generalFunc.getJsonValueStr("eItemAvailable", itemForReview));
                                mapData.put("eExtraPayment", generalFunc.getJsonValueStr("eExtraPayment", itemForReview));
                            } else {
                                mapData.put("genieWaitingForUserApproval", "Yes");

                            }
                            if (generalFunc.getJsonValueStr("genieUserApproved", rowObject) != null) {
                                mapData.put("genieUserApproved", generalFunc.getJsonValueStr("genieUserApproved", rowObject));


                            } else {
                                mapData.put("genieUserApproved", "Yes");

                            }

                            Object iorderId = generalFunc.getJsonValue("iOrderId", rowObject);
                            if (iorderId != null && Utils.checkText(iorderId.toString())) {
                                mapData.put("iOrderId", iorderId.toString());
                                iOrderId = iorderId.toString();
                            }

                            if (mapData.get("vStatus").equalsIgnoreCase("Delivered")) {
                                vImageDeliveryPref = generalFunc.getJsonValueStr("vImageDeliveryPref", rowObject);
                            }

                            String dDate = generalFunc.getJsonValue("dDate", rowObject).toString();

                            if (!dDate.trim().equalsIgnoreCase("")) {
                                String dLogDate = generalFunc.getDateFormatedType(dDate, Utils.OriginalDateFormate, "hh:mm aa");
                                String[] dLogDateArr = dLogDate.split(" ", 2);
                                String date = dLogDateArr[0];
                                mapData.put("dDate", date);
                                mapData.put("dDateConverted", generalFunc.convertNumberWithRTL(date));
                                mapData.put("dDateAMPM", dLogDateArr[1]);
                            } else {
                                mapData.put("dDate", "");
                                mapData.put("dDateConverted", "");
                                mapData.put("dDateAMPM", "");
                            }

                            String eShowCallImg = mapData.get("eShowCallImg");
                            if (eShowCallImg != null && eShowCallImg.equalsIgnoreCase("Yes")) {
                                imgCall.setOnClickListener(v -> {

                                    MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                                            .setCallId(generalFunc.getJsonValue("iDriverId", responseString))
                                            .setPhoneNumber(generalFunc.getJsonValue("driverPhone", responseString))
                                            .setToMemberType(Utils.CALLTODRIVER)
                                            .setToMemberName(generalFunc.getJsonValue("driverName", responseString))
                                            .setToMemberImage(generalFunc.getJsonValue("driverImageUrl", responseString))
                                            .setMedia(CommunicationManager.MEDIA_TYPE)
                                            .build();
                                    CommunicationManager.getInstance().communicate(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.OTHER);
                                });
                                contactArea.setVisibility(View.VISIBLE);
                            } else {
                                if (contactArea.getVisibility() != View.VISIBLE) {
                                    contactArea.setVisibility(View.GONE);
                                }
                            }
                            LayoutInflater itemInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            @NonNull StatusLineBinding binding = StatusLineBinding.inflate(itemInflater, null, false);
                            if (Objects.requireNonNull(mapData.get("OrderCurrentStatusCode")).equalsIgnoreCase(mapData.get("iStatusCode"))) {
                                binding.dynamicLine.setBackgroundColor(ContextCompat.getColor(getActContext(), R.color.appThemeColor_1));

                                currentStatusTxt.setText(mapData.get("vStatusTitle"));
                                currentStatusTimeTxt.setText(mapData.get("dDateConverted") + " " + mapData.get("dDateAMPM"));

                            } else {
                                binding.dynamicLine.setBackgroundColor(Color.parseColor("#D9D9D9"));
                            }
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.dynamicLine.getLayoutParams();
                            params.width = statusLineSize;
                            binding.dynamicLine.setLayoutParams(params);
                            statusLine.addView(binding.getRoot());

                            listData.add(mapData);
                        }


                        if (ETA != null && !ETA.equalsIgnoreCase("")) {
                            long time = ((Long.parseLong(ETA) / 60));
                            distanceVTxtView.setText(getTimeTxt((int) time));
                            pickedUpTimeTxtView.setText(getTimeTxt((int) time));
                        }
                        adapter.notifyDataSetChanged();
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                } else {
                    generalFunc.showError();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageBottomSheeetDefaultHeight(int height) {
        bottomSheetBehavior.setPeekHeight(height);
    }

    public String getTimeTxt(int duration) {

        if (duration < 1) {
            duration = 1;
        }
        String durationTxt = "";
        String timeToreach = duration == 0 ? "--" : "" + duration;

        timeToreach = duration >= 60 ? formatHoursAndMinutes(duration) : timeToreach;


        durationTxt = (duration < 60 ? generalFunc.retrieveLangLBl("", "LBL_MINS_SMALL") : generalFunc.retrieveLangLBl("", "LBL_HOUR_TXT"));

        durationTxt = duration == 1 ? generalFunc.retrieveLangLBl("", "LBL_MIN_SMALL") : durationTxt;
        durationTxt = duration > 120 ? generalFunc.retrieveLangLBl("", "LBL_HOURS_TXT") : durationTxt;

        return timeToreach + " " + durationTxt;
    }

    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.track_order_activity, menu);
        this.menu = menu;
        menu.findItem(R.id.menu_help).setTitle(generalFunc.retrieveLangLBl("", "LBL_HELP"));
        menu.findItem(R.id.menu_order_details).setTitle(generalFunc.retrieveLangLBl("", "LBL_VIEW_ORDER_DETAILS"));
        menu.findItem(R.id.menu_tip).setTitle(generalFunc.retrieveLangLBl("", "LBL_ORDER_TIP_TITLE_TXT"));
        menu.findItem(R.id.menu_live_chat).setTitle(generalFunc.retrieveLangLBl("", "LBL_LIVE_CHAT"));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_order_details), getResources().getColor(R.color.black));
        Utils.setMenuTextColor(menu.findItem(R.id.menu_help), getResources().getColor(R.color.black));
        menu.findItem(R.id.menu_tip).setVisible(showTipAddArea);
        Utils.setMenuTextColor(menu.findItem(R.id.menu_tip), getResources().getColor(R.color.black));

        if (generalFunc.getJsonValueStr("ENABLE_LIVE_CHAT_TRACK_ORDER", obj_userProfile).equalsIgnoreCase("Yes")) {
            menu.findItem(R.id.menu_live_chat).setVisible(true);
        } else {
            menu.findItem(R.id.menu_live_chat).setVisible(false);
        }


        return true;
    }

    public void menuInvisible() {
        if (menuTipItem != null) {
            invalidateOptionsMenu();
            menuTipItem.setVisible(showTipAddArea); // make true to make the menu item visible.
        } else if (menu != null) {
            invalidateOptionsMenu();
            // TIP MENu
            menu.findItem(R.id.menu_tip).setVisible(showTipAddArea);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bn = new Bundle();
        switch (item.getItemId()) {
            case R.id.menu_order_details:
                bn.putString("iOrderId", iOrderId);
                new ActUtils(getActContext()).startActWithData(OrderDetailsActivity.class, bn);
                return true;

            // TIP MENU
            case R.id.menu_tip:
                this.menuTipItem = item;
                showTipDialog();
                return true;

            case R.id.menu_help:

                bn.putString("iOrderId", iOrderId);
                new ActUtils(getActContext()).startActWithData(HelpMainCategory23Pro.class, bn);
                return true;

            case R.id.menu_live_chat:
                startChatActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startChatActivity() {

        String vName = generalFunc.getJsonValueStr("vName", obj_userProfile);
        String vLastName = generalFunc.getJsonValueStr("vLastName", obj_userProfile);
        String driverName = vName + " " + vLastName;
        String driverEmail = generalFunc.getJsonValueStr("vEmail", obj_userProfile);
        Utils.LIVE_CHAT_LICENCE_NUMBER = generalFunc.getJsonValueStr("LIVE_CHAT_LICENCE_NUMBER", obj_userProfile);
        HashMap<String, String> map = new HashMap<>();
        map.put("FNAME", vName);
        map.put("LNAME", vLastName);
        map.put("EMAIL", driverEmail);
        map.put("USERTYPE", Utils.userType);
        Intent intent = new Intent(getActContext(), ChatWindowActivity.class);
        intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, Utils.LIVE_CHAT_LICENCE_NUMBER);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_NAME, driverName);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_EMAIL, driverEmail);
        intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, Utils.userType + "_" + generalFunc.getMemberId());
        intent.putExtra("myParam", map);
        startActivity(intent);
    }

    public void showTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_order_tip_layout, null);
        builder.setView(dialogView);

        ImageView iv_giveTip = dialogView.findViewById(R.id.iv_giveTip);
        MTextView noThanksArea = dialogView.findViewById(R.id.noThanksArea);
        FrameLayout btnArea = dialogView.findViewById(R.id.tipBtnArea);
        amountArea = dialogView.findViewById(R.id.amountArea);
        errorTxt = dialogView.findViewById(R.id.errorTxt);
        tv_percentageAmount = dialogView.findViewById(R.id.tv_percentageAmount);
//        MTextView giveTipArea = dialogView.findViewById(R.id.giveTipArea);
        MTextView tipTitleText = (MTextView) dialogView.findViewById(R.id.tipTitleText);
        MTextView tipDescTitleText = (MTextView) dialogView.findViewById(R.id.tipDescTitleText);
        MTextView tipDescHintTitleText = (MTextView) dialogView.findViewById(R.id.tipDescHintTitleText);
        tipAmountBox = (EditText) dialogView.findViewById(R.id.tipAmountBox);
        addTipArea = (LinearLayout) dialogView.findViewById(R.id.addTipArea);
        noThanksArea.setText(generalFunc.retrieveLangLBl("", "LBL_NO_THANKS"));
//        MButton tipGivenBtn = ((MaterialRippleLayout) dialogView.findViewById(R.id.tipGivenBtn)).getChildView();
        SelectableRoundedImageView tipGivenBtn = (SelectableRoundedImageView) dialogView.findViewById(R.id.tipGivenBtn);
//        giveTipId = Utils.generateViewId();
//        tipGivenBtn.setId(giveTipId);

        MButton giveTipArea = ((MaterialRippleLayout) dialogView.findViewById(R.id.giveTipArea)).getChildView();
        btnArea.setVisibility(View.GONE);
        iv_giveTip.setVisibility(View.GONE);
        giveTipArea.setText(generalFunc.retrieveLangLBl("", "LBL_GIVE_TIP"));
        tipAmountTextOther = (MTextView) dialogView.findViewById(R.id.tipAmountTextOther);
        tipAmountAreaOther = (LinearLayout) dialogView.findViewById(R.id.tipAmountAreaOther);
        closeImgOther = (ImageView) dialogView.findViewById(R.id.closeImgOther);

        tipAmountText1 = (MTextView) dialogView.findViewById(R.id.tipAmountText1);
        tipAmountArea1 = (LinearLayout) dialogView.findViewById(R.id.tipAmountArea1);
        closeImg1 = (ImageView) dialogView.findViewById(R.id.closeImg1);

        tipAmountText2 = (MTextView) dialogView.findViewById(R.id.tipAmountText2);
        tipAmountArea2 = (LinearLayout) dialogView.findViewById(R.id.tipAmountArea2);
        closeImg2 = (ImageView) dialogView.findViewById(R.id.closeImg2);

        tipAmountText3 = (MTextView) dialogView.findViewById(R.id.tipAmountText3);
        tipAmountArea3 = (LinearLayout) dialogView.findViewById(R.id.tipAmountArea3);
        closeImg3 = (ImageView) dialogView.findViewById(R.id.closeImg3);
        iv_tip_help = (ImageView) dialogView.findViewById(R.id.iv_tip_help);
        iv_tip_help.setVisibility(View.VISIBLE);
        tipDescHintTitleText.setVisibility(View.GONE);

        tipAmountBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tipAmountBox.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), getResources().getDimension(R.dimen._30sdp)), 10,
                getResources().getColor(R.color.gray_holo_dark), tipGivenBtn);
//        tipGivenBtn.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));


        tipTitleText.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_TIP_TITLE_TXT"));
        tipDescTitleText.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_TIP_DESCRIPTION_TXT"));
        tipDescHintTitleText.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_TIP_HOW_WORKS_TXT"));
        tipAmountBox.setHint(generalFunc.retrieveLangLBl("", "LBL_TIP_AMOUNT_ENTER_TITLE"));
//        tipGivenBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));

        String CurrencySymbol = generalFunc.getJsonValueStr("CurrencySymbol", obj_userProfile);
        String DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL = generalFunc.getJsonValueStr("DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL", obj_userProfile);
        String tipAMount1 = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("TIP_AMOUNT_1", obj_userProfile));
        String tipAMount2 = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("TIP_AMOUNT_2", obj_userProfile));
        String tipAMount3 = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("TIP_AMOUNT_3", obj_userProfile));
        isPercentage = DELIVERY_TIP_AMOUNT_TYPE_DELIVERALL.equalsIgnoreCase("Percentage");
        boolean eReverseSymbolEnable = generalFunc.retrieveValue("eReverseSymbolEnable").equalsIgnoreCase("Yes");
        tipAmountText1.setTag(isPercentage ? 1 : tipAMount1);
        tipAmountText1.setText(isPercentage ? tipAMount1 : eReverseSymbolEnable ? tipAMount1 + " " + CurrencySymbol : CurrencySymbol + tipAMount1);
        tipAmountText2.setTag(isPercentage ? 2 : tipAMount2);
        tipAmountText2.setText(isPercentage ? tipAMount2 : eReverseSymbolEnable ? tipAMount2 + " " + CurrencySymbol : CurrencySymbol + tipAMount2);
        tipAmountText3.setTag(isPercentage ? 3 : tipAMount3);
        tipAmountText3.setText(isPercentage ? tipAMount3 : eReverseSymbolEnable ? tipAMount3 + " " + CurrencySymbol : CurrencySymbol + tipAMount3);
        tipAmountTextOther.setTag(isPercentage ? 4 : "");
        tipAmountTextOther.setText(isPercentage ? generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT") : "");

        tipAmountArea1.setOnClickListener(view -> {
            setSelected(tipAmountText1, tipAmountArea1);
        });

        closeImg1.setOnClickListener(view -> {
            removeSelectedTip();
        });

        iv_tip_help.setOnClickListener(view -> {
//            showTipInfoDialog(getActContext().getResources().getDrawable(R.drawable.ic_save_money),"LBL_TIP_DIALOG_DESCRIPTION");
            showTipInfoDialog(getActContext().getResources().getDrawable(R.drawable.ic_save_money), "LBL_DELIVERY_TIP_DESC");

        });
        tipAmountArea2.setOnClickListener(view -> {
            setSelected(tipAmountText2, tipAmountArea2);
        });

        closeImg2.setOnClickListener(view -> {
            removeSelectedTip();
        });
        tipAmountArea3.setOnClickListener(view -> {
            setSelected(tipAmountText3, tipAmountArea3);
        });

        closeImg3.setOnClickListener(view -> {
            removeSelectedTip();
        });
        tipAmountAreaOther.setOnClickListener(view -> {
            setSelected(tipAmountTextOther, tipAmountAreaOther);
        });

        closeImgOther.setOnClickListener(view -> {
            removeSelectedTip();
        });

        tipAmountBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tipAmount.equalsIgnoreCase("4")) {
                    resetErrorTxt();
                    if (Utils.checkText(s.toString())) {
                        tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + generalFunc.formatNumAsPerCurrency(generalFunc, Utils.getText(tipAmountBox), userCurrencySymbol, true));
                        tv_percentageAmount.setVisibility(View.VISIBLE);
                    } else {
                        tv_percentageAmount.setText("");
                        tv_percentageAmount.setVisibility(View.GONE);
                    }
                }
            }
        });
        noThanksArea.setOnClickListener(view -> {

            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        });

        giveTipArea.setOnClickListener(view -> {


            if (addTipArea.getVisibility() == View.VISIBLE) {
                final boolean tipAmountEntered = Utils.checkText(tipAmountBox) ? true : false;
                if (tipAmountEntered == false) {
                    amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrecterrorwithdesign));
                    errorTxt.setText(generalFunc.retrieveLangLBl("", "LBL_REQUIRED"));
                    tv_percentageAmount.setText("");
                    tv_percentageAmount.setVisibility(View.GONE);
                    errorTxt.setVisibility(View.VISIBLE);
                    return;
                } else {
                    resetErrorTxt();
                }


                if (generalFunc.parseDoubleValue(0, tipAmountBox.getText().toString()) > 0) {
                    resetErrorTxt();
                    giveTip();

                } else {
                    tv_percentageAmount.setText("");
                    tv_percentageAmount.setVisibility(View.GONE);
                    amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrecterrorwithdesign));
                    errorTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ENTER_GRATER_ZERO_VALUE_TXT"));
                    errorTxt.setVisibility(View.VISIBLE);

                }

            } else {
                giveTip();
            }


        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);

        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(alertDialog);
        }
        alertDialog.show();
    }

    private void resetErrorTxt() {
        errorTxt.setText("");
        errorTxt.setVisibility(View.GONE);
        amountArea.setBackground(getActContext().getResources().getDrawable(R.drawable.roundrectwithdesign));
    }

    private void showTipInfoDialog(Drawable drawable, String LBL_DATA) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_tip_help, null);
        builder.setView(dialogView);


        final ImageView iamage_source = (ImageView) dialogView.findViewById(R.id.iamage_source);
        if (drawable != null) {
            iamage_source.setImageDrawable(drawable);
        }
        final MTextView okTxt = (MTextView) dialogView.findViewById(R.id.okTxt);
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        final MTextView msgTxt = (MTextView) dialogView.findViewById(R.id.msgTxt);
        titileTxt.setText(generalFunc.retrieveLangLBl("Tip your delivery partner", "LBL_TIP_DIALOG_TITLE"));
        okTxt.setText(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT "));
        msgTxt.setText("" + generalFunc.retrieveLangLBl("You can set a delivery preference before the delivery agent attempts to deliver your package at your door. Your preferences will be taken care by delivery driver.", LBL_DATA));
        msgTxt.setMovementMethod(new ScrollingMovementMethod());
        okTxt.setOnClickListener(v -> giveTipAlertDialog.dismiss());
        giveTipAlertDialog = builder.create();
        giveTipAlertDialog.setCancelable(true);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(giveTipAlertDialog);
        }
        giveTipAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        giveTipAlertDialog.show();


    }

    private void giveTip() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "OrderCollectTip");
        parameters.put("iOrderId", iOrderId);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);
        if (isPercentage && Utils.checkText(tipAmount)) {

            parameters.put("selectedTipPos", tipAmount);
            if (tipAmount.equalsIgnoreCase("4")) {
                parameters.put("fTipAmount", Utils.getText(tipAmountBox));
            }

        } else {
            parameters.put("fTipAmount", Utils.checkText(tipAmountBox) ? Utils.getText(tipAmountBox) : tipAmount);
        }

        parameters.put("iMemberId", generalFunc.getMemberId());
        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {
                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {
                    String paymentUrl = generalFunc.getJsonValue("paymentUrl", responseString);
                    if (Utils.checkText(paymentUrl)) {
                        Bundle bn = new Bundle();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("URL", paymentUrl);
                        map.put("vPageTitle", generalFunc.retrieveLangLBl("", "LBL_GIVE_TIP"));
                        bn.putSerializable("data", map);
                        new ActUtils(getActContext()).startActForResult(QuickPaymentActivity.class, bn, Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE);
                        return;
                    }
                    showTipSuccessMsg();
                } else {

                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    if (message.equalsIgnoreCase("LBL_NO_CARD_AVAIL_NOTE")) {

                        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
                        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                        generateAlert.setBtnClickList(btn_id -> {
                            if (btn_id == 1) {
                                generateAlert.closeAlertBox();
                                Bundle bn = new Bundle();
                                new ActUtils(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
                            } else {
                                generateAlert.closeAlertBox();
                            }
                        });
                        generateAlert.showAlertBox();
                    } else if (message.equalsIgnoreCase("LBL_REQUIRED_MINIMUM_AMOUT")) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)));
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    }

                }
            } else {
                generalFunc.showError();

            }
        });


    }

    private void showTipSuccessMsg() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        openSucessDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == WEBVIEWPAYMENT) {
            //  handlePayment();
            getTrackDetails();

        } else if (requestCode == Utils.REQ_VERIFY_INSTANT_PAYMENT_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                showTipSuccessMsg();
            } else {
                generalFunc.showError();
            }
        } else if (requestCode == Utils.SELECT_ORGANIZATION_PAYMENT_CODE && resultCode == RESULT_OK && data != null) {
            //  handlePayment();

        }


    }

    public void handlePayment() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "CollectPaymentBuyAnything");
        parameters.put("iOrderId", iOrderId);
        // parameters.put("CheckUserWallet", eWalletDebitAllow ? "Yes" : "No");
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("eWalletIgnore", eWalletIgnore);


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {
                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {

                    if (generalFunc.getJsonValue("WebviewPayment", responseString).equalsIgnoreCase("Yes")) {

                        Bundle bn = new Bundle();
                        String url = generalFunc.getJsonValue(Utils.message_str, responseString);


                        bn.putString("url", url);
                        bn.putBoolean("handleResponse", true);
                        bn.putBoolean("isBack", false);
                        new ActUtils(getActContext()).startActForResult(PaymentWebviewActivity.class, bn, WEBVIEWPAYMENT);

                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)), "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), button_Id -> {

                            getTrackDetails();

                        });


                    }


                } else {
                    if (generalFunc.getJsonValue(Utils.message_str, responseString).equalsIgnoreCase("LOW_WALLET_AMOUNT")) {


                        String walletMsg = "";
                        String low_balance_content_msg = generalFunc.getJsonValue("low_balance_content_msg", responseString);

                        if (low_balance_content_msg != null && !low_balance_content_msg.equalsIgnoreCase("")) {
                            walletMsg = low_balance_content_msg;
                        } else {
                            walletMsg = generalFunc.retrieveLangLBl("", "LBL_WALLET_LOW_AMOUNT_MSG_TXT");
                        }


                        generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"), walletMsg, generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("No") ? generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN") :
                                generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_ADD_MONEY"), generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("NO") ? generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT") :
                                "", button_Id -> {
                            if (button_Id == 1) {

                                new ActUtils(getActContext()).startAct(MyWalletActivity.class);
                            } else if (button_Id == 0) {
                                if (generalFunc.getJsonValue("IS_RESTRICT_TO_WALLET_AMOUNT", responseString).equalsIgnoreCase("No")) {
                                    eWalletIgnore = "Yes";
                                    handlePayment();
                                }
                            } else if (button_Id == 2) {

                            }
                        });
                    }


                }

            } else {
                generalFunc.showError();
            }
        });


    }

    private void setSelected(MTextView selectedTextAreaTxtView, LinearLayout selectedArea) {
        tv_percentageAmount.setVisibility(isPercentage ? View.VISIBLE : View.GONE);
        tipAmount = selectedTextAreaTxtView.getTag().toString();
        tipAmountBox.setText("");
        addTipArea.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        resetErrorTxt();
        errorTxt.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        tipAmountArea1.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea1 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg1.setVisibility(selectedArea == tipAmountArea1 ? View.VISIBLE : View.GONE);
        if (selectedArea == tipAmountArea1) {
            tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + generalFunc.formatNumAsPerCurrency(generalFunc, TIP_AMOUNT_1_VALUE, userCurrencySymbol, true));
        }
        tipAmountArea2.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea2 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg2.setVisibility(selectedArea == tipAmountArea2 ? View.VISIBLE : View.GONE);
        if (selectedArea == tipAmountArea2) {
            tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + generalFunc.formatNumAsPerCurrency(generalFunc, TIP_AMOUNT_2_VALUE, userCurrencySymbol, true));
        }
        tipAmountArea3.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountArea3 ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImg3.setVisibility(selectedArea == tipAmountArea3 ? View.VISIBLE : View.GONE);
        if (selectedArea == tipAmountArea3) {
            tv_percentageAmount.setText(LBL_TIP_AMOUNT + ": " + generalFunc.formatNumAsPerCurrency(generalFunc, TIP_AMOUNT_3_VALUE, userCurrencySymbol, true));
        }
        tipAmountAreaOther.setBackground(getActContext().getResources().getDrawable(selectedArea == tipAmountAreaOther ? R.drawable.button_background : R.drawable.button_normal_background));
        closeImgOther.setVisibility(selectedArea == tipAmountAreaOther ? View.VISIBLE : View.GONE);
        if (!isPercentage) {
            tipAmountTextOther.setTag("");
        }
        if (selectedArea == tipAmountAreaOther) {
            tv_percentageAmount.setVisibility(Utils.checkText(tipAmountBox) ? View.VISIBLE : View.GONE);
            tv_percentageAmount.setText("");
        }
        tipAmountTextOther.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
    }

    private void removeSelectedTip() {
        tipAmount = "";
        tipAmountBox.setText("");
        tv_percentageAmount.setText("");
        tipAmountArea1.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountArea2.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountArea3.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        tipAmountAreaOther.setBackground(getActContext().getResources().getDrawable(R.drawable.button_normal_background));
        closeImg1.setVisibility(View.GONE);
        closeImg2.setVisibility(View.GONE);
        closeImg3.setVisibility(View.GONE);
        closeImgOther.setVisibility(View.GONE);
        if (!isPercentage) {
            tipAmountTextOther.setTag("");
        }
        tipAmountTextOther.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
        addTipArea.setVisibility(View.GONE);
        resetErrorTxt();
    }

    public void openSucessDialog() {
        dialog_sucess = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);
        dialog_sucess.setContentView(R.layout.sucess_layout_nw);
        MTextView titleTxt = (MTextView) dialog_sucess.findViewById(R.id.titleTxt);
        MTextView msgTxt = (MTextView) dialog_sucess.findViewById(R.id.msgTxt);
        ImageView imgSuccess = (ImageView) dialog_sucess.findViewById(R.id.imgSuccess);
        VectorUtils.manageVectorImage(getActContext(), imgSuccess, R.drawable.ic_success_new, R.drawable.ic_success_new_compat);

        msgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_TIP_SUCCESS_TXT"));
//        titleTxt.setText(generalFunc.retrieveLangLBl("", ""));


        MButton btn_type2 = ((MaterialRippleLayout) dialog_sucess.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));


        btn_type2.setOnClickListener(view -> {
            dialog_sucess.dismiss();
            getTrackDetails();
        });

        dialog_sucess.setCancelable(false);
        dialog_sucess.show();

    }

    // TIP Feature Functionality End
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (fromNoti) {
            Bundle bn = new Bundle();
            new ActUtils(getActContext()).startActWithData(UberXHomeActivity.class, bn);
            finishAffinity();
            return;
        }

        if (getIntent().getBooleanExtra("isRestart", false)) {
            MyApp.getInstance().restartWithGetDataApp();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setOnreviewClick(int position) {
        openconfirmGenieItem(position);
    }

    @Override
    public void setOnMakePaymentClick(int position) {
//        Bundle bn = new Bundle();
//        //  new ActUtils(getActContext()).startActForResult(BusinessSelectPaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
//
//        bn.putBoolean("isGenie", true);
//        bn.putBoolean("isCODAllow", false);
//       // new ActUtils(getActContext()).startActForResult(ProfilePaymentActivity.class, bn, Utils.SELECT_ORGANIZATION_PAYMENT_CODE);
//        String url = generalFunc.getJsonValue("PAYMENT_MODE_URL", userprofileJson) + "&eType=" +  Utils.eSystem_Type;
//
//
//        url = url + "&tSessionId=" + (generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY));
//        url = url + "&GeneralUserType=" + Utils.app_type;
//        url = url + "&GeneralMemberId=" + generalFunc.getMemberId();
//        url = url + "&ePaymentOption=" + "Card";
//        url = url + "&vPayMethod=" + "Instant";
//        url = url + "&SYSTEM_TYPE=" + "APP";
//        url = url + "&vCurrentTime=" + generalFunc.getCurrentDateHourMin();
//
//
//        bn.putString("url", url);
//        bn.putBoolean("handleResponse", true);
//        bn.putBoolean("isBack", false);
//        new ActUtils(getActContext()).startActForResult(PaymentWebviewActivity.class, bn, WEBVIEWPAYMENT);
        handlePayment();

    }

    @Override
    public void setOnviewBill(int position) {
        viewBillDetails();
    }

    public void viewBillDetails() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetBuyAnyServiceBillDetails");
        parameters.put("iOrderId", iOrderId);
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {
                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {

                    openViewBillDialog(responseString);

                }
            } else {
                generalFunc.showError();
            }
        });

    }

    public void openViewBillDialog(String responseString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.view_bill_genie, null);

        final MTextView titleTxt = (MTextView) dialogView.findViewById(R.id.titleTxt);
        final ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
        fareDetailDisplayArea = (LinearLayout) dialogView.findViewById(R.id.fareDetailDisplayArea);

        titleTxt.setText(generalFunc.retrieveLangLBl("Bill Details", "LBL_INVOICE_DETAILS"));

        JSONArray FareDetailsArrNewObj = null;
        FareDetailsArrNewObj = generalFunc.getJsonArray("FareDetailsArr", responseString);


        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        if (FareDetailsArrNewObj != null) {
            for (int i = 0; i < FareDetailsArrNewObj.length(); i++) {
                JSONObject jobject = generalFunc.getJsonObject(FareDetailsArrNewObj, i);
                try {
                    String rName = jobject.names().getString(0);
                    String rValue = jobject.get(rName).toString();
                    fareDetailDisplayArea.addView(MyUtils.addFareDetailRow(getActContext(), generalFunc, rName, rValue, (FareDetailsArrNewObj.length() - 1) == i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        builder.setView(dialogView);
        cancelImg.setOnClickListener(view -> billDeatilsGenieAlert.dismiss());


        billDeatilsGenieAlert = builder.create();
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(billDeatilsGenieAlert);
        }
        billDeatilsGenieAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        billDeatilsGenieAlert.show();
        billDeatilsGenieAlert.setCancelable(false);
        billDeatilsGenieAlert.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));
    }

    public void openconfirmGenieItem(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.confirm_item_genie, null);

        final MTextView itemName = (MTextView) dialogView.findViewById(R.id.itemName);
        final MTextView priceTxt = (MTextView) dialogView.findViewById(R.id.priceTxt);
        final MTextView priceTitleTxt = (MTextView) dialogView.findViewById(R.id.priceTitleTxt);
        final ImageView itemImg = (ImageView) dialogView.findViewById(R.id.itemImg);
        final ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
        //final MButton btn_confirm = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_confirm)).getChildView();
        //final MButton btn_discard = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_discard)).getChildView();
        final MTextView btn_confirm = ((MTextView) dialogView.findViewById(R.id.btn_confirm));
        final MTextView btn_discard = ((MTextView) dialogView.findViewById(R.id.btn_discard));

        btn_confirm.setOnClickListener(view -> {
            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_GENIE_REVIEW_ALERT"));
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
            generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
            generateAlert.setBtnClickList(btn_id -> {
                if (btn_id == 1) {
                    handleConfirmReviewItems(pos, "Yes", "NO");
                } else {
                    generateAlert.closeAlertBox();
                }
            });
            generateAlert.showAlertBox();
        });
        btn_discard.setOnClickListener(view -> {
            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_GENIE_REVIEW_ALERT"));
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
            generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
            generateAlert.setBtnClickList(btn_id -> {
                if (btn_id == 1) {
                    handleConfirmReviewItems(pos, "No", "Yes");
                } else {
                    generateAlert.closeAlertBox();
                }
            });
            generateAlert.showAlertBox();
        });
        cancelImg.setOnClickListener(view -> ConfirmGenieAlert.dismiss());

        builder.setView(dialogView);
        btn_confirm.setText(generalFunc.retrieveLangLBl("Confirm", "LBL_CONFIRM_TXT"));
        btn_discard.setText(generalFunc.retrieveLangLBl("Discard", "LBL_DECLINE_TXT"));
        itemName.setText(listData.get(pos).get("iQty") + " x " + listData.get(pos).get("MenuItem"));
        priceTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENIE_PRICE") + ":");
        priceTxt.setText(/*generalFunc.retrieveLangLBl("", "LBL_GENIE_PRICE") + ":" + */listData.get(pos).get("fTotPrice"));

        Logger.d("ItemImageURL", "::" + listData.get(pos).get("vImage"));
        new LoadImage.builder(LoadImage.bind(listData.get(pos).get("vImage")), itemImg).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();

        if (listData.get(pos).get("eExtraPayment") != null && listData.get(pos).get("eExtraPayment").equalsIgnoreCase("No")) {
            priceTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAYMENT_NOT_REQUIRED"));
        }


        ConfirmGenieAlert = builder.create();
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(ConfirmGenieAlert);
        }
        //ConfirmGenieAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        ConfirmGenieAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ConfirmGenieAlert.show();
        ConfirmGenieAlert.setCancelable(false);
        ConfirmGenieAlert.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));

    }

    public void handleConfirmReviewItems(int pos, String econfirm, String eDecline) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "ConfirmReviewItems");
        parameters.put("iOrderId", iOrderId);
        parameters.put("iItemDetailsId", listData.get(pos).get("iItemDetailsId"));
        parameters.put("eConfirm", econfirm);
        parameters.put("eDecline", eDecline);
        parameters.put("UserType", Utils.userType);
        parameters.put("eSystem", Utils.eSystem_Type);


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {
                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {
                    if (ConfirmGenieAlert != null) {
                        ConfirmGenieAlert.dismiss();
                    }
                    getTrackDetails();

                }
            } else {
                generalFunc.showError();
            }
        });

    }


    public void onClick(View view) {
        Bundle bn = new Bundle();
        switch (view.getId()) {
            case R.id.backImgView:
                onBackPressed();
                break;

            case R.id.navigateBtn:
                openNavigationDialog("" + CompanyLat, "" + CompanyLong);
                break;
            case R.id.contactLessDeliveryArea:
                showPreferenceHelp();
                break;
            case R.id.btn_cancel:
            case R.id.btnConfirmarea:
                //finaldelArea.setVisibility(View.GONE);
                MyApp.getInstance().restartWithGetDataApp();
                break;
            case R.id.vieDetailsArea:
                finaldelArea.setVisibility(View.GONE);
                bn.putString("iOrderId", iOrderId);
                new ActUtils(getActContext()).startActWithData(HelpMainCategory23Pro.class, bn);
                break;
            case R.id.btnRatingArea:


                if (generalFunc.getJsonValueStr("ENABLE_FOOD_RATING_DETAIL_FLOW", obj_userProfile).equalsIgnoreCase("Yes")) {
                    bn.putBoolean("IS_NEW", true);
                    bn.putString("listDriverFeedbackQuestions", generalFunc.getJsonValueStr("DRIVER_FEEDBACK_QUESTIONS", obj_userProfile));
                } else {
                    bn.putBoolean("IS_NEW", false);
                }
                bn.putString("iOrderId", iOrderId);
                bn.putString("vOrderNo", vOrderNo);
                bn.putString("driverName", driverName);
                bn.putString("vCompany", vCompany);
                bn.putString("eTakeaway", eTakeaway);

                new ActUtils(getActContext()).startActWithData(FoodRatingActivity.class, bn);
                finish();
                break;
        }
    }


    private void showPreferenceHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.desgin_preference_help, null);
        builder.setView(dialogView);


        final MButton okTxt = ((MaterialRippleLayout) dialogView.findViewById(R.id.okTxt)).getChildView();
        final MTextView titileTxt = (MTextView) dialogView.findViewById(R.id.titileTxt);
        final WebView msgTxt = (WebView) dialogView.findViewById(R.id.msgTxt);
        titileTxt.setText(generalFunc.retrieveLangLBl("Contactless delivery", "LBL_CONTACT_LESS_DELIVERY_TXT"));
        okTxt.setText(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT "));
      /*  msgTxt.setText(Html.fromHtml("" + generalFunc.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfil this requirement you will have to follow below steps:\n" +
                "<br>" +
                "- Stay away from user<br>" +
                "- Put parcel at user's door.<br>" +
                "- Capture a photo of parcel at user's door as a proof of delivery.<br>" +
                "- Mark order as delivered", "LBL_CONTACTLESSDELIVERYUSER_NOTE_TXT ")));
        */
        //  msgTxt.setText(generalFunc.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfil this requirement you will have to follow below steps:\n - Stay away from user\n - Put parcel at user's door\n - Capture a photo of parcel at user's door as a proof of delivery.\n - Mark order as delivered", "LBL_CONTACTLESS_DELIVERYUSER_NOTE_TXT"));

        //  msgTxt.setMovementMethod(new ScrollingMovementMethod());

        MyApp.executeWV(msgTxt, generalFunc, generalFunc.retrieveLangLBl("Customer has selected contactless delivery option. We have introduced this feature to break infectious. To fulfil this requirement you will have to follow below steps:\n - Stay away from user\n - Put parcel at user's door\n - Capture a photo of parcel at user's door as a proof of delivery.\n - Mark order as delivered", "LBL_CONTACTLESS_DELIVERYUSER_NOTE_TXT"));


        okTxt.setOnClickListener(v -> preferenceAlertDialog.dismiss());
        preferenceAlertDialog = builder.create();
        preferenceAlertDialog.setCancelable(true);
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(preferenceAlertDialog);
        }
        preferenceAlertDialog.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
        preferenceAlertDialog.show();


    }

    public void openNavigationDialog(final String dest_lat, final String dest_lon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_selectnavigation_view, null);

        final MTextView NavigationTitleTxt = (MTextView) dialogView.findViewById(R.id.NavigationTitleTxt);
        final MTextView wazemapTxtView = (MTextView) dialogView.findViewById(R.id.wazemapTxtView);
        final MTextView googlemmapTxtView = (MTextView) dialogView.findViewById(R.id.googlemmapTxtView);
        LinearLayout ll_wazemapTxtView = dialogView.findViewById(R.id.ll_wazemapTxtView);
        LinearLayout ll_googlemmapTxtView = dialogView.findViewById(R.id.ll_googlemmapTxtView);
        ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);

        builder.setView(dialogView);
        NavigationTitleTxt.setText(generalFunc.retrieveLangLBl("Choose Option", "LBL_CHOOSE_OPTION"));
        googlemmapTxtView.setText(generalFunc.retrieveLangLBl("Google map navigation", "LBL_NAVIGATION_GOOGLE_MAP"));
        wazemapTxtView.setText(generalFunc.retrieveLangLBl("Waze navigation", "LBL_NAVIGATION_WAZE"));

        cancelImg.setOnClickListener(v -> {

            list_navigation.dismiss();
        });

        ll_googlemmapTxtView.setOnClickListener(v -> {
            try {
                list_navigation.dismiss();
                String url_view = "http://maps.google.com/maps?daddr=" + dest_lat + "," + dest_lon;
                (new ActUtils(getActContext())).openURL(url_view, "com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            } catch (Exception e) {
                generalFunc.showMessage(wazemapTxtView, generalFunc.retrieveLangLBl("Please install Google Maps in your device.", "LBL_INSTALL_GOOGLE_MAPS"));
            }
        });

        ll_wazemapTxtView.setOnClickListener(v -> {
            try {

                String uri = "waze://?ll=" + dest_lat + "," + dest_lon + "&navigate=yes";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                list_navigation.dismiss();

            } catch (Exception e) {
                generalFunc.showMessage(wazemapTxtView, generalFunc.retrieveLangLBl("Please install Waze navigation app in your device.", "LBL_INSTALL_WAZE"));
            }
        });


        list_navigation = builder.create();
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(list_navigation);
        }
        list_navigation.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        list_navigation.show();
        list_navigation.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));
    }
}