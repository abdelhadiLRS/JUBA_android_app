package com.multixpro.store;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.adapter.files.OrderItemsRecyclerAdapter;
import com.dialogs.OpenListView;
import com.fontanalyzer.SystemFont;
import com.general.call.CommunicationManager;
import com.general.call.MediaDataProvider;
import com.general.files.ActUtils;
import com.general.files.Closure;
import com.general.files.CustomDialog;
import com.general.files.DownloadImage;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.PreferenceDailogJava;
import com.general.files.thermalPrint.AsyncBluetoothThermalPosPrint;
import com.kyleduo.switchbutton.SwitchButton;
import com.service.handler.ApiHandler;
import com.service.server.ServerTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.utils.CommonUtilities;
import com.utils.LoadImage;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.FloatingActionButton.FloatingActionButton;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Esite on 30-03-2018.
 */

public class OrderDetailActivity extends ParentActivity implements OrderItemsRecyclerAdapter.OnItemClickList {

    SwitchButton availItemSwitch;
    MTextView titleTxt;
    MTextView subTitleTxt;
    MTextView totalItemsTxt;
    MTextView deliveryStatusTxtView;
    View containerView;
    View bgView;
    String userPhoneNumber = "";
    String userName = "";
    String vUserImage = "";
    String vCompany = "";
    String vRestuarantImage = "";
    String iUserid = "";
    String vInstruction = "";
    View iconRefreshImgView;
    View iconInstructionView;
    View reAssignArea;
    ProgressBar mAssignDriverProgressBar;
    View confirmDeclineAreaView;
    View assignDriverBtnAra;
    RecyclerView itemsRecyclerView;
    ImageView iconImgView;
    ProgressBar loadingBar;
    HashMap<String, String> orderData;
    OrderItemsRecyclerAdapter adapter;
    AlertDialog dialog_declineOrder;
    ServerTask currExeTask;
    private LinearLayout chargeDetailArea;
    private LinearLayout chargeDetailTitleArea;
    private ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> fareList = new ArrayList<>();
    private ImageView backImgView;
    private MButton confirmBtn;
    private MButton declineBtn;
    private MButton assignDriverBtn;
    private MButton reAssignBtn;
    private MButton declineAssignBtn;
    private MButton trackOrderBtn;
    private boolean isExpanded = false;
    GenerateAlertBox reqInProgressAlertBox;

    FloatingActionButton viewPrescTxtView;

    ArrayList<String> imageList = new ArrayList<>();
    Menu menu;

    /*Thermal*/
    HashMap<String, String> reqiuredDetails = new HashMap<>();
    CustomDialog customDialog;
    public static final int REQUEST_COARSE_LOCATION = 200;
    JSONArray PrescriptionImages;
    FloatingActionButton connectPrinterArea;
    FrameLayout connectPrinterlayout;
    HashMap<String, String> data = new HashMap<>();
    boolean isOpenDriverSelection = false;
    String eDriverType = "";
    private boolean iseTakeAway;
    private boolean isDineIn;
    boolean isPrefrence;
    ArrayList<HashMap<String, String>> instructionslit;
    RelativeLayout moreinstructionLyout;
    MTextView textmoreinsview;
    MTextView textuserprefrence;

    JSONObject DeliveryPreferences;

    View orderPickedUpBtnArea;
    private MButton orderPickedUpBtn;
    private MTextView takeAwayOrderTitleTxt;
    private String tReceiptDataImageUrl = "http://";
    Target mTarget = null;

    //manage proof
    String vIdProofImage = "";
    String vIdProofImageNote = "";
    String vIdProofImageUploaded = "";
    androidx.appcompat.app.AlertDialog ConfirmproofAlert;
    //kiosk handling
    String eOrderplacedBy = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);


        orderData = (HashMap<String, String>) getIntent().getSerializableExtra("OrderData");

       /* Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);*/

        initView();
//        setChargeDetails();
        setLabels();
    }

    private void initView() {

        instructionslit = new ArrayList<>();
        textuserprefrence = findViewById(R.id.textuserprefrence);
        textmoreinsview = findViewById(R.id.textmoreinsview);
        moreinstructionLyout = findViewById(R.id.moreinstructionLyout);
        chargeDetailArea = findViewById(R.id.chargeDetailArea);
        chargeDetailTitleArea = findViewById(R.id.chargeDetailTitleArea);
        availItemSwitch = (SwitchButton) findViewById(R.id.availItemSwitch);

        textmoreinsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("DeliveryPreferences", DeliveryPreferences.toString());
                new ActUtils(getActContext()).startActWithData(UserPrefrenceActivity.class, bundle);
            }
        });

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        viewPrescTxtView = (FloatingActionButton) findViewById(R.id.viewPrescTxtView);
        addToClickHandler(viewPrescTxtView);
        containerView = findViewById(R.id.containerView);
        subTitleTxt = (MTextView) findViewById(R.id.subTitleTxt);
        subTitleTxt.setSelected(true);
        totalItemsTxt = (MTextView) findViewById(R.id.totalItemsTxt);
        deliveryStatusTxtView = (MTextView) findViewById(R.id.deliveryStatusTxtView);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        iconImgView = (ImageView) findViewById(R.id.iconImgView);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        mAssignDriverProgressBar = (ProgressBar) findViewById(R.id.mAssignDriverProgressBar);
        iconRefreshImgView = findViewById(R.id.iconRefreshImgView);
        iconInstructionView = findViewById(R.id.iconInstructionView);
        assignDriverBtnAra = findViewById(R.id.assignDriverBtnAra);
        orderPickedUpBtnArea = findViewById(R.id.orderPickedUpBtnArea);
        confirmDeclineAreaView = findViewById(R.id.confirmDeclineAreaView);
        reAssignArea = findViewById(R.id.reAssignArea);
        bgView = findViewById(R.id.bgView);

        itemsRecyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);

        adapter = new OrderItemsRecyclerAdapter(getActContext(), dataList, generalFunc);
        itemsRecyclerView.setAdapter(adapter);
        itemsRecyclerView.setClipToPadding(false);


        declineBtn = ((MaterialRippleLayout) findViewById(R.id.declineBtn)).getChildView();
        declineBtn.setBackgroundColor(getResources().getColor(R.color.red));
        confirmBtn = ((MaterialRippleLayout) findViewById(R.id.confirmBtn)).getChildView();
        confirmBtn.setBackgroundColor(getResources().getColor(R.color.green));
        trackOrderBtn = ((MaterialRippleLayout) findViewById(R.id.trackOrderBtn)).getChildView();

        assignDriverBtn = ((MaterialRippleLayout) findViewById(R.id.assignDriverBtn)).getChildView();
        takeAwayOrderTitleTxt = findViewById(R.id.takeAwayOrderTitleTxt);
        orderPickedUpBtn = ((MaterialRippleLayout) findViewById(R.id.orderPickedUpBtn)).getChildView();
        orderPickedUpBtn.setId(Utils.generateViewId());

        reAssignBtn = ((MaterialRippleLayout) findViewById(R.id.reAssignBtn)).getChildView();
        declineAssignBtn = ((MaterialRippleLayout) findViewById(R.id.declineAssignBtn)).getChildView();

        declineBtn.setId(Utils.generateViewId());
        confirmBtn.setId(Utils.generateViewId());
        trackOrderBtn.setId(Utils.generateViewId());
        assignDriverBtn.setId(Utils.generateViewId());
        reAssignBtn.setId(Utils.generateViewId());
        declineAssignBtn.setId(Utils.generateViewId());


        addToClickHandler(declineBtn);
        addToClickHandler(confirmBtn);
        addToClickHandler(trackOrderBtn);
        addToClickHandler(assignDriverBtn);
        addToClickHandler(orderPickedUpBtn);
        addToClickHandler(reAssignBtn);
        addToClickHandler(declineAssignBtn);
        addToClickHandler(iconImgView);
        addToClickHandler(iconRefreshImgView);

        data.put(Utils.THERMAL_PRINT_ALLOWED_KEY, "");
        data = new GeneralFunctions(getActContext()).retrieveValue(data);

        connectPrinterlayout = (FrameLayout) findViewById(R.id.connectPrinterlayout);
        connectPrinterArea = (FloatingActionButton) findViewById(R.id.connectPrinterArea);
        connectPrinterlayout.setVisibility(MyApp.getInstance().isThermalPrintAllowed(false) && data.get(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes") ? View.VISIBLE : View.GONE);
        addToClickHandler(connectPrinterArea);

        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        addToClickHandler(bgView);

        iconImgView.setVisibility(View.GONE);
        iconRefreshImgView.setVisibility(View.GONE);
        iconInstructionView.setVisibility(View.GONE);


        int dp4 = Utils.dipToPixels(getActContext(), 4);
        iconImgView.setPadding(dp4, dp4, dp4, dp4);
        iconRefreshImgView.setPadding(dp4, dp4, dp4, dp4);

        //   iconInstructionView.setPadding(Utils.dipToPixels(getActContext(), 4), Utils.dipToPixels(getActContext(), 4), Utils.dipToPixels(getActContext(), 4), Utils.dipToPixels(getActContext(), 4));

        containerView.setVisibility(View.GONE);


        adapter.setOnItemClickList(this);
        getOrderDetails();
    }

    private void setLabels() {
        trackOrderBtn.setText(generalFunc.retrieveLangLBl("Track Order", "LBL_TRACK_ORDER"));
        takeAwayOrderTitleTxt.setText(generalFunc.retrieveLangLBl("Take Away Order", "LBL_TAKE_WAY_ORDER"));

        deliveryStatusTxtView.setText(generalFunc.retrieveLangLBl("Delivery Executive not found", "LBL_NO_PROVER_FOUND"));


        confirmBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_CONFIRM_TXT"));
        declineBtn.setText(generalFunc.retrieveLangLBl("", "LBL_DECLINE_TXT"));

        assignDriverBtn.setText(generalFunc.retrieveLangLBl("Assign Driver", "LBL_ASSIGN_DRIVER"));
        orderPickedUpBtn.setText(generalFunc.retrieveLangLBl("Mark As Picked-up", "LBL_PICKEDUP_ORDER"));
        reAssignBtn.setText(generalFunc.retrieveLangLBl("Assign Driver", "LBL_ASSIGN_DRIVER"));
        declineAssignBtn.setText(generalFunc.retrieveLangLBl("", "LBL_DECLINE_TXT"));
        // viewPrescTxtView.setText(generalFunc.retrieveLangLBl("View Prescription", "LBL_VIEW_PRESCRIPTION"));

        assignDriverBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        assignDriverBtn.setTextColor(getResources().getColor(android.R.color.white));

        reAssignBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        reAssignBtn.setTextColor(getResources().getColor(android.R.color.white));

        declineAssignBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        declineAssignBtn.setTextColor(getResources().getColor(android.R.color.white));

        titleTxt.setText("#" + generalFunc.convertNumberWithRTL(orderData.get("vOrderNo")));
        subTitleTxt.setText(generalFunc.convertNumberWithRTL(orderData.get("tOrderRequestDateFormatted")));
    }

    private void setChargeDetails(JSONArray fareArr) {

        if (chargeDetailArea.getChildCount() > 0) {
            chargeDetailArea.removeAllViewsInLayout();
        }


        if (chargeDetailTitleArea.getChildCount() > 0) {
            chargeDetailTitleArea.removeAllViewsInLayout();
            isExpanded = false;
        }

        fareList.clear();
        chargeDetailArea.removeAllViews();
        chargeDetailTitleArea.removeAllViews();

        for (int i = 0; i < fareArr.length(); i++) {
            HashMap<String, String> mapData = new HashMap<>();
            JSONObject obj_tmp = generalFunc.getJsonObject(fareArr, i);

            try {
                mapData.put("Name", obj_tmp.names().getString(0));
                mapData.put("Amount", obj_tmp.get(obj_tmp.names().getString(0)).toString());
            } catch (JSONException e) {
                e.printStackTrace();
                mapData.put("Name", "");
                mapData.put("Amount", "");
            }

            fareList.add(mapData);
        }


        List<HashMap<String, String>> answer = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < fareList.size(); i++) {
            if (i == fareList.size() - 1) {
                setChildData(i, true);
            } else {
                answer.add(fareList.get(i));
                setChildData(i, false);
            }
        }

//        btn_type1.setTextColor(this.getResources().getColor(R.color.appThemeColor_TXT_1));
//        btn_type2.setTextColor(this.getResources().getColor(R.color.appThemeColor_TXT_1));

//        chargeDetailArea.setVisibility(View.VISIBLE);
    }

    private void setChildData(int i, boolean isLast) {
        Log.d("setChildData", "setChildData: " + i);
        LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View convertView = infalInflater.inflate(R.layout.charge_header_cell, null);

        MTextView itemNameTxt = (MTextView) convertView.findViewById(R.id.itemNameTxt);
        RelativeLayout orderDetailArea = (RelativeLayout) convertView.findViewById(R.id.orderDetailArea);
        MTextView fareTxt = (MTextView) convertView.findViewById(R.id.fareTxt);
        View shadowView = (View) convertView.findViewById(R.id.shadowView);
        itemNameTxt.setText(fareList.get(i).get("Name"));
        fareTxt.setText(generalFunc.convertNumberWithRTL(fareList.get(i).get("Amount")));

        final ImageView indicatorImg = (ImageView) convertView.findViewById(R.id.indicatorImg);

        if (i == 0) {
            shadowView.setVisibility(View.GONE);
            indicatorImg.setVisibility(View.GONE);

         /*   convertView.setOnClickListener(view -> {
                if (chargeDetailArea.getVisibility() == View.VISIBLE) {
                    chargeDetailArea.setVisibility(View.GONE);
                    bgView.setVisibility(View.GONE);

                    View totalChargeView = chargeDetailTitleArea.getChildAt(0);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.VISIBLE);
                    (totalChargeView.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
                }
            });*/
            chargeDetailArea.addView(convertView);

            itemNameTxt.setTypeface(SystemFont.FontStyle.LIGHT.font);
            itemNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            itemNameTxt.setTextColor(Color.parseColor("#272727"));

            fareTxt.setTypeface(SystemFont.FontStyle.MEDIUM.font);

            fareTxt.setTextColor(Color.parseColor("#272727"));
            fareTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        } else if (i == (fareList.size() - 1)) {
            shadowView.setVisibility(View.VISIBLE);
            itemNameTxt.setTypeface(SystemFont.FontStyle.SEMI_BOLD.font);
            fareTxt.setTypeface(SystemFont.FontStyle.BOLD.font);

            itemNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

            itemNameTxt.setTextColor(Color.parseColor("#4a4a4a"));

            fareTxt.setTextColor(Color.parseColor("#000000"));
            fareTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);


            if (isExpanded) {
                indicatorImg.setVisibility(View.INVISIBLE);
            } else {
                indicatorImg.setVisibility(View.VISIBLE);
            }
            indicatorImg.setImageResource(R.mipmap.ic_arrow_up);

            orderDetailArea.getLayoutParams().height = Utils.dpToPx(40, getActContext());

            chargeDetailTitleArea.addView(convertView);

            addToClickHandler(chargeDetailTitleArea);
        } else {
            itemNameTxt.setTypeface(SystemFont.FontStyle.LIGHT.font);
            itemNameTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            itemNameTxt.setTextColor(Color.parseColor("#272727"));

            fareTxt.setTypeface(SystemFont.FontStyle.MEDIUM.font);

            fareTxt.setTextColor(Color.parseColor("#272727"));
            fareTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

            shadowView.setVisibility(View.GONE);
            indicatorImg.setVisibility(View.INVISIBLE);

            chargeDetailArea.addView(convertView);
        }

    }

    // slide the view from below itself to the current position
    public void slideUp(View view, Animation.AnimationListener listener) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setAnimationListener(listener);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private Context getActContext() {
        return OrderDetailActivity.this;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemAvailabilityChanged(int position, boolean isAvailable) {
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "UpdateOrderDetailsRestaurant");
        parameters.put("iOrderId", orderData.get("iOrderId"));
        parameters.put("iOrderDetailId", dataList.get(position).get("iOrderDetailId"));
        parameters.put("eAvailable", isAvailable == true ? "Yes" : "No");
        parameters.put("UserType", Utils.app_type);


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = generalFunc.checkDataAvail(Utils.action_str, responseString);

                        if (isDataAvail) {
                            getOrderDetails();
                        }

                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                    } else {
                        generalFunc.showError();
                    }

                });

    }

    public void getDeclineReasonsList() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetCancelReasons");
        parameters.put("iOrderId", orderData.get("iOrderId"));
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eUserType", Utils.app_type);
        parameters.put("UserType", Utils.app_type);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (Utils.checkText(responseString)) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    showDeclineReasonsAlert(responseString);
                } else {
                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    if (message.equals("DO_RESTART") || message.equals(Utils.GCM_FAILED_KEY) || message.equals(Utils.APNS_FAILED_KEY)
                            || message.equals("LBL_SERVER_COMM_ERROR")) {

                        MyApp.getInstance().restartWithGetDataApp();
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message));
                    }
                }

            } else {
                generalFunc.showError();
            }

        });

    }


    int selCurrentPosition = -1;

    public void showDeclineReasonsAlert(String responseString) {
        selCurrentPosition = -1;
        String titleDailog = generalFunc.retrieveLangLBl("Decline Order", "LBL_DECLINE_ORDER");

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());
        // builder.setTitle(generalFunc.retrieveLangLBl("Decline Order", "LBL_DECLINE_ORDER"));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.decline_order_dialog_design, null);
        builder.setView(dialogView);

        MaterialEditText reasonBox = (MaterialEditText) dialogView.findViewById(R.id.inputBox);
        RelativeLayout commentArea = (RelativeLayout) dialogView.findViewById(R.id.commentArea);
        reasonBox.setHideUnderline(true);
        if (generalFunc.isRTLmode()) {
            reasonBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._10sdp), 0);
        } else {
            reasonBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, 0, 0);
        }

        reasonBox.setSingleLine(false);
        reasonBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        reasonBox.setGravity(Gravity.TOP);
        if (generalFunc.isRTLmode()) {
            reasonBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._10sdp), 0);
        } else {
            reasonBox.setPaddings((int) getResources().getDimension(R.dimen._10sdp), 0, 0, 0);
        }

        reasonBox.setVisibility(View.GONE);
        commentArea.setVisibility(View.GONE);
        new CreateRoundedView(Color.parseColor("#ffffff"), 5, 1, Color.parseColor("#C5C3C3"), commentArea);
        reasonBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_ENTER_REASON"));


        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        JSONArray arr_msg = generalFunc.getJsonArray(Utils.message_str, responseString);
        if (arr_msg != null) {

            for (int i = 0; i < arr_msg.length(); i++) {

                JSONObject obj_tmp = generalFunc.getJsonObject(arr_msg, i);

                //  arrListTitle.add(generalFunc.getJsonValueStr("vTitle", obj_tmp));
                //   arrListIDs.add(generalFunc.getJsonValueStr("iCancelReasonId", obj_tmp));

                HashMap<String, String> datamap = new HashMap<>();
                datamap.put("title", generalFunc.getJsonValueStr("vTitle", obj_tmp));
                datamap.put("id", generalFunc.getJsonValueStr("iCancelReasonId", obj_tmp));
                list.add(datamap);


            }

            // arrListTitle.add(generalFunc.retrieveLangLBl("", "LBL_OTHER_DL"));
            // arrListIDs.add("");


            HashMap<String, String> othermap = new HashMap<>();
            othermap.put("title", generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
            othermap.put("id", "");
            list.add(othermap);


            //   AppCompatSpinner spinner = (AppCompatSpinner) dialogView.findViewById(R.id.declineReasonsSpinner);

            MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);
            MTextView submitTxt = (MTextView) dialogView.findViewById(R.id.submitTxt);
            MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);
            ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
            subTitleTxt.setText(titleDailog);

            MTextView declinereasonBox = (MTextView) dialogView.findViewById(R.id.declinereasonBox);
            declinereasonBox.setText("-- " + generalFunc.retrieveLangLBl("", "LBL_SELECT_CANCEL_REASON") + " --");
            submitTxt.setClickable(false);
            submitTxt.setTextColor(getResources().getColor(R.color.gray_holo_light));

            // style="@style/decline_order_spinner_style"


            submitTxt.setText(generalFunc.retrieveLangLBl("", "LBL_YES"));
            cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO"));
            submitTxt.setOnClickListener(v -> {
                // String selectedItemId = arrListIDs.get(spinner.getSelectedItemPosition());

                if (selCurrentPosition == -1) {
                    return;
                }

                if (Utils.checkText(reasonBox) == false && selCurrentPosition == (list.size() - 1)) {
                    reasonBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD"));
                    return;
                }

                declineOrder(list.get(selCurrentPosition).get("id"), Utils.getText(reasonBox));

                dialog_declineOrder.dismiss();

            });
            cancelTxt.setOnClickListener(v -> {
                Utils.hideKeyboard(getActContext());
                dialog_declineOrder.dismiss();
            });

            cancelImg.setOnClickListener(v -> {
                Utils.hideKeyboard(getActContext());
                dialog_declineOrder.dismiss();
            });


           /* ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrListTitle);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (spinner.getSelectedItemPosition() == (arrListIDs.size() - 1)) {
                        reasonBox.setVisibility(View.VISIBLE);
                        commentArea.setVisibility(View.VISIBLE);
                    } else if (spinner.getSelectedItemPosition() == 0) {
                        commentArea.setVisibility(View.GONE);
                        reasonBox.setVisibility(View.GONE);
                    } else {
                        commentArea.setVisibility(View.GONE);
                        reasonBox.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/

            declinereasonBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_REASON"), list, OpenListView.OpenDirection.CENTER, true, position -> {


                        selCurrentPosition = position;
                        HashMap<String, String> mapData = list.get(position);
                        declinereasonBox.setText(mapData.get("title"));
                        if (selCurrentPosition == (list.size() - 1)) {
                            reasonBox.setVisibility(View.VISIBLE);
                            commentArea.setVisibility(View.VISIBLE);
                        } else {
                            reasonBox.setVisibility(View.GONE);
                            commentArea.setVisibility(View.GONE);
                        }
                        submitTxt.setClickable(true);
                        submitTxt.setTextColor(getResources().getColor(R.color.white));


                    }).show(selCurrentPosition, "title");
                }
            });


            dialog_declineOrder = builder.create();
            dialog_declineOrder.setCancelable(false);
            dialog_declineOrder.getWindow().setBackgroundDrawable(getActContext().getResources().getDrawable(R.drawable.all_roundcurve_card));
            dialog_declineOrder.show();

            //  dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            /*dialog_declineOrder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String selectedItemId = arrListIDs.get(spinner.getSelectedItemPosition());

                    if (spinner.getSelectedItemPosition() == 0) {
                        return;
                    }

                    if (Utils.checkText(reasonBox) == false && spinner.getSelectedItemPosition() == (arrListIDs.size() - 1)) {
                        reasonBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD"));
                        return;
                    }

                    declineOrder(arrListIDs.get(spinner.getSelectedItemPosition()), Utils.getText(reasonBox));

                    dialog_declineOrder.dismiss();
                }
            });*/
/*
            dialog_declineOrder.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_declineOrder.dismiss();
                }
            });*/
        } else {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NO_DATA_AVAIL"));
        }
    }

    public void declineOrder(String iCancelReasonId, String reason) {

        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "DeclineOrder");
        parameters.put("iCompanyId", generalFunc.getMemberId());
        parameters.put("iOrderId", orderData.get("iOrderId"));
        parameters.put("eUserType", Utils.app_type);
        parameters.put("eCancelledBy", Utils.app_type);
        parameters.put("iCancelReasonId", iCancelReasonId);
        parameters.put("vCancelReason", iCancelReasonId.equals("") ? reason : "");
        parameters.put("UserType", Utils.app_type);


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,

                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = generalFunc.checkDataAvail(Utils.action_str, responseString);

                        if (isDataAvail) {
                            (new ActUtils(getActContext())).setOkResult();
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), true);
                        } else {

                            if (generalFunc.getJsonValue("DO_RESTART", responseString).equalsIgnoreCase("Yes")) {
                                finish();
                            } else {

                                GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                                generateAlert.setContentMessage("",
                                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Allow", "LBL_BTN_OK_TXT"));
                                generateAlert.showAlertBox();

                                generateAlert.setCancelable(false);
                                generateAlert.setBtnClickList(btn_id -> {

                                    generateAlert.closeAlertBox();


                                });
                            }

                            // generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        }

                    } else {


                        generalFunc.showError();
                    }

                });

    }

    public void confirmOrder(String eTakeAwayPickedUp) {

        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "ConfirmOrderByRestaurant");
        parameters.put("iCompanyId", generalFunc.getMemberId());
        parameters.put("iOrderId", orderData.get("iOrderId"));
        parameters.put("UserType", Utils.app_type);

        if (Utils.checkText(eTakeAwayPickedUp)) {
            parameters.put("ePickedUp", eTakeAwayPickedUp);
        }

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,

                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = generalFunc.checkDataAvail(Utils.action_str, responseString);

                        if (isDataAvail) {
                            (new ActUtils(getActContext())).setOkResult();
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), false);

                            if (!Utils.checkText(eTakeAwayPickedUp)) {
                                if (MyApp.getInstance().isThermalPrintAllowed(true)) {
                                    if (MyApp.btsocket != null) {
                                        startprint();
                                    }
                                }

                                if (MyApp.getInstance().isThermalPrintAllowed(false) && data.get(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes")) {
                                    connectPrinterlayout.setVisibility(View.VISIBLE);

                                } else {
                                    connectPrinterlayout.setVisibility(View.GONE);
                                }
                                getOrderDetails();
                            } else {
                                finish();
                            }
                        } else {

                            if (generalFunc.getJsonValue("DO_RESTART", responseString).equalsIgnoreCase("Yes")) {
                                finish();
                            } else {
                                GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                                generateAlert.setContentMessage("",
                                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Allow", "LBL_BTN_OK_TXT"));
                                generateAlert.showAlertBox();

                                generateAlert.setCancelable(false);
                                generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                    @Override
                                    public void handleBtnClick(int btn_id) {

                                        generateAlert.closeAlertBox();

                                    }
                                });
                            }
                            // generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        }

                    } else {
                        generalFunc.showError();
                    }

                });

    }

    public void startprint() {
        if (mTarget != null) {
            Picasso.get().cancelRequest(mTarget);
        }
        try {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Picasso.get().cancelTag("loadImage");

        /* dialog = new MyProgressDialog(OrderDetailActivity.this, false, generalFunc.retrieveLangLBl("Please Wait", "LBL_PLEASE_WAIT"));*/
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.design_print_process);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        ProgressBar progressBar = dialog.findViewById(R.id.progressbar);
        MTextView tvProgressCount = dialog.findViewById(R.id.tvProgressCount);
        MTextView printerHTxt = dialog.findViewById(R.id.printerHTxt);
        printerHTxt.setText(generalFunc.retrieveLangLBl("Printing in progress...", "LBL_PRINTING_PROGRESS"));
        tvProgressCount.setText("" + 0);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        Logger.d(TAG, "tReceiptDataImageUrl" + tReceiptDataImageUrl);
        if (dialog != null) {
            dialog.show();
        }


        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap == null) {
                    Logger.d(TAG, "Bitmap is Null");
                    getBitmap();
                } else {
                    Logger.d(TAG, "Worked");
                    Logger.d(TAG, "in onBitmapLoaded : ");
                    printImage(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Logger.d(TAG, "failed");
                getBitmap();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Logger.d(TAG, "Prepare");
            }
        };

        RequestCreator imageLoadRequestCreator = Picasso.get().load(tReceiptDataImageUrl);

        imageLoadRequestCreator.fetch(new Callback() {
            @Override
            public void onSuccess() {

                Logger.d(TAG, "onSuccess");

            }

            @Override
            public void onError(Exception e) {
                Logger.d(TAG, "onError of url load " + e.getMessage());
                getBitmap();

            }
        });
        imageLoadRequestCreator.networkPolicy(NetworkPolicy.NO_CACHE).tag("loadImage").into(mTarget);

    }

    private void getBitmap() {
        InternetConnection intCheck = new InternetConnection(this);
        if (intCheck.isNetworkConnected() && intCheck.check_int()) {
            String tag = "imageRequestTag";

            DownloadImage dwnImg = new DownloadImage(tReceiptDataImageUrl, null, new DownloadImage.ImageDownloadListener() {
                @Override
                public void onSuccess(Bitmap result) {
                    if (result != null) {
                        printImage(result);
                    } else {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }

                        generalFunc.showMessage(findViewById(R.id.mainArea), "Oops..! We are facing some problem with this printer.Kindly do the same process in a while.");
                    }
                }

                @Override
                public void onError() {
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }

                    generalFunc.showMessage(findViewById(R.id.mainArea), /*Utils.checkText(error.getLocalizedMessage()) ? error.getLocalizedMessage() :*/ "Oops..! We are facing some problem with this printer.Kindly do the same process in a while.");
                }
            });

            dwnImg.execute();

        } else {
            generalFunc.showMessage(findViewById(R.id.mainArea), "Oops..! We are facing some problem with this printer.Kindly do the same process in a while.");
        }
    }

    private void printImage(Bitmap bitmap) {
//        Drawable d = new BitmapDrawable(getResources(), bitmap);

        // do anything with bitmap
        Logger.d(TAG, "bitmap>>" + bitmap.getGenerationId());
        Logger.d(TAG, "bitmap Width>>" + bitmap.getWidth());
        Logger.d(TAG, "bitmap Height>>" + bitmap.getHeight());

//     Maximum Width size is 355 pixels Maximum Height size is 500 pixels
        try {
            new AsyncBluetoothThermalPosPrint(getActContext(), dialog, generalFunc, bitmap).execute();
        } catch (Exception e) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            e.printStackTrace();
        }
//        bitmap.recycle();
    }


    boolean isCanceled = false;

    public synchronized boolean isCanceled() {
        return this.isCanceled;
    }

    ArrayList<Bitmap> partsList = new ArrayList<>();
    Dialog dialog;
    String TAG = "PRINT_UTILS";

    public void confirmIncomingDriver(String iOrderId) {

        if (orderData.get("iOrderId").equalsIgnoreCase(iOrderId)) {
            if (reqInProgressAlertBox != null) {
                reqInProgressAlertBox.closeAlertBox();
                reqInProgressAlertBox = null;
            }

            getOrderDetails();
        }
    }

    public boolean confirmIncomingDriver(String iOrderId, boolean isForCheck) {

        if (orderData.get("iOrderId").equalsIgnoreCase(iOrderId)) {
            return true;
        }

        return false;
    }


    public void assignDriver() {

        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "sendRequestToDrivers");
        parameters.put("iOrderId", orderData.get("iOrderId"));
        parameters.put("eDriverType", eDriverType);
        parameters.put("UserType", Utils.app_type);


        ApiHandler.execute(getActContext(), parameters, true, true, generalFunc,
                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {
                        generalFunc.sendHeartBeat();
                        boolean isDataAvail = generalFunc.checkDataAvail(Utils.action_str, responseObj);

                        String message = generalFunc.getJsonValueStr(Utils.message_str, responseObj);

                        if (isDataAvail == false) {
                            if (message.equals("SESSION_OUT")) {
                                MyApp.getInstance().notifySessionTimeOut();
                                Utils.runGC();
                                return;
                            }

                            if (message.equals("NO_CARS")) {


                                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                                generateAlert.setCancelable(false);
                                generateAlert.setBtnClickList(btn_id -> {

                                    if (btn_id == 1) {
                                        generateAlert.closeAlertBox();
                                    } else if (btn_id == 0) {
                                        declineBtn.performClick();
                                    }

                                });
                                generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_NO_DRIVER_FOUND"));
                                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_DECLINE_TXT"));

                                generateAlert.showAlertBox();

                                return;
                            }

                            if (message.equals(Utils.GCM_FAILED_KEY) || message.equals(Utils.APNS_FAILED_KEY)) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_TXT"));
                                return;
                            }
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message));

                        } else {
                            reqInProgressAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_REQ_IN_PROCESS"));

                            getOrderDetails();
                        }
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_TXT"));
                    }

                });

    }

    public void getOrderDetails() {
        if (loadingBar.getVisibility() != View.VISIBLE) {
            loadingBar.setVisibility(View.VISIBLE);
        }

        bgView.performClick();

        dataList.clear();
        adapter.notifyDataSetChanged();

        containerView.setVisibility(View.GONE);
        chargeDetailArea.setVisibility(View.GONE);

        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("type", "GetOrderDetailsRestaurant");
        parameters.put("iOrderId", orderData.get("iOrderId"));
        parameters.put("UserType", Utils.app_type);


        if (currExeTask != null) {
            currExeTask.cancel(true);
            currExeTask = null;
        }


        currExeTask = ApiHandler.execute(getActContext(), parameters,
                responseString -> {
                    iconRefreshImgView.setVisibility(View.VISIBLE);
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = generalFunc.checkDataAvail(Utils.action_str, responseObj);

                        if (isDataAvail) {
                            imageList.clear();
                            JSONObject obj_msg = generalFunc.getJsonObject(Utils.message_str, responseObj);

                            vIdProofImage = generalFunc.getJsonValueStr("vIdProofImage", obj_msg);
                            vIdProofImageNote = generalFunc.getJsonValueStr("vIdProofImageNote", obj_msg);
                            vIdProofImageUploaded = generalFunc.getJsonValueStr("vIdProofImageUploaded", obj_msg);

                            String driverAssign = generalFunc.getJsonValueStr("DriverAssign", obj_msg);
                            String assignStatus = generalFunc.getJsonValueStr("AssignStatus", obj_msg);
                            String eConfirm = generalFunc.getJsonValueStr("eConfirm", obj_msg);
                            String eDecline = generalFunc.getJsonValueStr("eDecline", obj_msg);
                            String eOrderPicked = generalFunc.getJsonValueStr("eOrderPickedByDriver", obj_msg);
                            String eTakeaway = generalFunc.getJsonValueStr("eTakeAway", obj_msg);

                            iseTakeAway = Utils.checkText(eTakeaway) && eTakeaway.equalsIgnoreCase("Yes");
                            takeAwayOrderTitleTxt.setVisibility(iseTakeAway ? View.VISIBLE : View.GONE);

                            String eOrderType = generalFunc.getJsonValueStr("eOrderType", obj_msg);

                            isDineIn = Utils.checkText(eOrderType) && eOrderType.equalsIgnoreCase("Dine In");
                            eOrderplacedBy = generalFunc.getJsonValueStr("eOrderplacedBy", obj_msg);
                            if (isDineIn) {
                                takeAwayOrderTitleTxt.setText(generalFunc.retrieveLangLBl("Dine In", "LBL_DINE_IN_TXT"));
                                takeAwayOrderTitleTxt.setVisibility(isDineIn ? View.VISIBLE : View.GONE);
                            }

                            String UserPhone = generalFunc.getJsonValueStr("UserPhone", obj_msg);
                            userName = generalFunc.getJsonValueStr("UserName", obj_msg);
                            vUserImage = generalFunc.getJsonValueStr("vUserImage", obj_msg);
                            vCompany = generalFunc.getJsonValueStr("vCompany", obj_msg);
                            vRestuarantImage = generalFunc.getJsonValueStr("vRestuarantImage", obj_msg);
                            iUserid = generalFunc.getJsonValueStr("iUserId", obj_msg);
                            vInstruction = generalFunc.getJsonValueStr("vInstruction", obj_msg);
                            tReceiptDataImageUrl = generalFunc.getJsonValueStr("tReceiptDataImageUrl", obj_msg);

                            isOpenDriverSelection = generalFunc.getJsonValueStr("isOpenDriverSelection", obj_msg).equals("Yes") ? true : false;

                            reqiuredDetails = new HashMap<>();
                            reqiuredDetails.put("CUSTOMER_NAME", userName);
                            reqiuredDetails.put("COMPANY_NAME", vCompany);
                            reqiuredDetails.put("ORDER_DATETIME", orderData.get("tOrderRequestDateFormatted"));
                            reqiuredDetails.put("ORDER_NO", generalFunc.convertNumberWithRTL(orderData.get("vOrderNo")));
                            reqiuredDetails.put("ORDER_VIA", generalFunc.retrieveValue(Utils.SITE_NAME_KEY));


                            DeliveryPreferences = generalFunc.getJsonObject("DeliveryPreferences", responseObj);

                            isPrefrence = generalFunc.getJsonValueStr("Enable", DeliveryPreferences).equalsIgnoreCase("Yes") ? true : false;
                            if (isPrefrence) {
                                moreinstructionLyout.setVisibility(View.GONE);
                            } else {
                                moreinstructionLyout.setVisibility(View.GONE);
                            }


                            if ((vInstruction != null && !vInstruction.equals("")) || isPrefrence) {
                                iconInstructionView.setVisibility(View.VISIBLE);
                                addToClickHandler(iconInstructionView);
                            } else {
                                iconInstructionView.setVisibility(View.GONE);
                            }


                            PrescriptionImages = generalFunc.getJsonArray("PrescriptionImages", obj_msg);

                            if (PrescriptionImages != null && !PrescriptionImages.equals("")) {
                                viewPrescTxtView.setVisibility(View.VISIBLE);

                                for (int i = 0; i < PrescriptionImages.length(); i++) {

                                    imageList.add(generalFunc.getJsonValue(PrescriptionImages, i).toString());

                                }

                            }

                            int REQUEST_REMAINS_SEC = GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValueStr("REQUEST_REMAINS_SEC", obj_msg));

                            userPhoneNumber = UserPhone;


                            if (eOrderplacedBy != null && eOrderplacedBy.equalsIgnoreCase("Kiosk")) {
                                iconImgView.setVisibility(View.GONE);
                            } else {
                                iconImgView.setVisibility(View.VISIBLE);
                            }

                            if (assignStatus.equalsIgnoreCase("REQ_NOT_FOUND")) {
                                deliveryStatusTxtView.setVisibility(View.GONE);

                                reAssignArea.setVisibility(View.GONE);
                                assignDriverBtnAra.setVisibility(View.GONE);

                                if (eConfirm.equalsIgnoreCase("Yes") && !iseTakeAway) {
                                    assignDriverBtnAra.setVisibility(View.VISIBLE);
                                    mAssignDriverProgressBar.setVisibility(View.GONE);
                                    assignDriverBtn.setText(generalFunc.retrieveLangLBl("Assign Driver", "LBL_ASSIGN_DRIVER"));
                                } else if (iseTakeAway & eConfirm.equalsIgnoreCase("Yes")) {
                                    orderPickedUpBtnArea.setVisibility(View.VISIBLE);
                                    orderPickedUpBtn.setText(generalFunc.retrieveLangLBl("Mark As Picked-up", "LBL_PICKEDUP_ORDER"));
                                }
                                if (eConfirm.equalsIgnoreCase("Yes") && isDineIn) {
                                    assignDriverBtnAra.setVisibility(View.GONE);
                                    orderPickedUpBtnArea.setVisibility(View.VISIBLE);
                                    orderPickedUpBtn.setText(generalFunc.retrieveLangLBl("Mark As completed", "LBL_COMPLETED_ORDER"));
                                }

                                assignDriverBtn.setEnabled(true);

                                ((MaterialRippleLayout) trackOrderBtn.getParent()).setVisibility(View.GONE);
                            } else if (assignStatus.equalsIgnoreCase("DRIVER_ASSIGN")) {
                                deliveryStatusTxtView.setVisibility(View.VISIBLE);
                                deliveryStatusTxtView.setText(generalFunc.retrieveLangLBl("Delivery executive is on way to pick order.", "LBL_DELIVERY_DRIVER_ASSIGNED"));
                                if (eOrderPicked.equalsIgnoreCase("Yes")) {
                                    deliveryStatusTxtView.setText(generalFunc.retrieveLangLBl("Delivery executive is on way to deliver the order.", "LBL_DELIVERY_DRIVER_IN_ROUTE_DELIVER"));
                                }
                                assignDriverBtn.setText(generalFunc.retrieveLangLBl("Assign Driver", "LBL_ASSIGN_DRIVER"));
                                assignDriverBtn.setEnabled(false);
                                assignDriverBtnAra.setVisibility(View.GONE);
                                reAssignArea.setVisibility(View.GONE);
                                ((MaterialRippleLayout) trackOrderBtn.getParent()).setVisibility(View.VISIBLE);
                            } else if (assignStatus.equalsIgnoreCase("REQ_PROCESS")) {
                                deliveryStatusTxtView.setVisibility(View.GONE);

                                mAssignDriverProgressBar.setVisibility(View.VISIBLE);
                                mAssignDriverProgressBar.setIndeterminate(true);
                                mAssignDriverProgressBar.getIndeterminateDrawable().setColorFilter(
                                        Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.SRC_IN);

                                assignDriverBtnAra.setVisibility(View.VISIBLE);
                                assignDriverBtn.setEnabled(false);

                                if (REQUEST_REMAINS_SEC > 0) {

                                    new Handler().postDelayed(() -> {
                                        getOrderDetails();
                                    }, REQUEST_REMAINS_SEC * 1000);

                                }


                                reAssignArea.setVisibility(View.GONE);
                                assignDriverBtn.setText(generalFunc.retrieveLangLBl("Assigning Driver", "LBL_ASSIGNING_DELIVERY_DRIVER"));
                                ((MaterialRippleLayout) trackOrderBtn.getParent()).setVisibility(View.GONE);
                            } else if (assignStatus.equalsIgnoreCase("REQ_FAILED")) {
                                deliveryStatusTxtView.setText(generalFunc.retrieveLangLBl("Delivery executive not assigned.", "LBL_DELIVERY_DRIVER_NOT_ASSIGNED"));
                                deliveryStatusTxtView.setVisibility(View.VISIBLE);
                                reAssignArea.setVisibility(View.VISIBLE);
                                assignDriverBtnAra.setVisibility(View.GONE);
                                assignDriverBtn.setText(generalFunc.retrieveLangLBl("Assign Driver", "LBL_ASSIGN_DRIVER"));
                                ((MaterialRippleLayout) trackOrderBtn.getParent()).setVisibility(View.GONE);

                            } else {
                                ((MaterialRippleLayout) trackOrderBtn.getParent()).setVisibility(View.GONE);
                                deliveryStatusTxtView.setVisibility(View.GONE);
                                assignDriverBtn.setText(generalFunc.retrieveLangLBl("Assign Driver", "LBL_ASSIGN_DRIVER"));
                            }


                            if (eConfirm.equalsIgnoreCase("Yes") || eDecline.equalsIgnoreCase("Yes")) {
                                confirmDeclineAreaView.setVisibility(View.GONE);
                            }


                            int totalItems = GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValueStr("TotalItems", obj_msg));

                            totalItemsTxt.setText(totalItems > 1 ? (generalFunc.convertNumberWithRTL("" + totalItems) + " " + generalFunc.retrieveLangLBl("Items", "LBL_ITEMS")) : (generalFunc.convertNumberWithRTL("" + totalItems) + " " + generalFunc.retrieveLangLBl("Item", "LBL_ITEM")));


                            JSONArray fareArr = generalFunc.getJsonArray("FareDetailsArr", obj_msg);

                            if (fareArr != null) {
                                setChargeDetails(fareArr);
                            }

                            dataList.clear();
                            JSONArray msgArr = generalFunc.getJsonArray("itemlist", obj_msg);
                            if (msgArr != null) {

                                for (int i = 0; i < msgArr.length(); i++) {
                                    JSONObject obj_tmp = generalFunc.getJsonObject(msgArr, i);
                                    HashMap<String, String> dataMap = new HashMap<>();
                                    dataMap.put("DriverAssign", driverAssign);
                                    dataMap.put("eConfirm", eConfirm);
                                    dataMap.put("eDecline", eDecline);

                                    String iQty = generalFunc.getJsonValueStr("iQty", obj_tmp);
                                    dataMap.put("iQty", iQty);
                                    dataMap.put("iQtyConveretd", generalFunc.convertNumberWithRTL(iQty));

                                    dataMap.put("MenuItem", generalFunc.getJsonValueStr("MenuItem", obj_tmp));
                                    dataMap.put("MenuItemToppings", generalFunc.getJsonValueStr("MenuItemToppings", obj_tmp));
                                    dataMap.put("MenuItemOptions", generalFunc.getJsonValueStr("MenuItemOptions", obj_tmp));
                                    dataMap.put("eAvailable", generalFunc.getJsonValueStr("eAvailable", obj_tmp));
                                    dataMap.put("SubTitle", generalFunc.getJsonValueStr("SubTitle", obj_tmp));
                                    dataMap.put("SubTitleConverted", generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("SubTitle", obj_tmp)));
                                    dataMap.put("iOrderDetailId", generalFunc.getJsonValueStr("iOrderDetailId", obj_tmp));
                                    String fTotPrice = generalFunc.getJsonValueStr("fTotPrice", obj_tmp);
                                    dataMap.put("fTotPrice", fTotPrice);
                                    dataMap.put("fTotPriceConverted", generalFunc.convertNumberWithRTL(fTotPrice));

                                    String TotalDiscountPrice = generalFunc.getJsonValueStr("TotalDiscountPrice", obj_tmp);
                                    dataMap.put("TotalDiscountPrice", TotalDiscountPrice);
                                    dataMap.put("TotalDiscountPriceConverted", generalFunc.convertNumberWithRTL(TotalDiscountPrice));

                                    dataMap.put("LBL_AVAILABLE", generalFunc.retrieveLangLBl("", "LBL_AVAILABLE"));
                                    dataMap.put("LBL_NOT_AVAILABLE", generalFunc.retrieveLangLBl("", "LBL_NOT_AVAILABLE"));
                                    dataMap.put("vSKU", generalFunc.getJsonValueStr("vSKU", obj_tmp));

                                    dataList.add(dataMap);
                                }
                                adapter.notifyDataSetChanged();
                                containerView.setVisibility(View.VISIBLE);
                            } else {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TRY_AGAIN_LATER_TXT"), true);
                            }
                        } else {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)), true);
                        }
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TRY_AGAIN_LATER_TXT"), true);
                    }

                    loadingBar.setVisibility(View.GONE);
                });


    }

    /*Thermal Print Start*/
    private void generateBillData(boolean skipRedirect) {


        if (skipRedirect) {
            if (MyApp.btsocket != null) {
                startprint();
            }
        } else {
            if (MyApp.btsocket == null) {
                Bundle bn = new Bundle();
                new ActUtils(getActContext()).startActForResult(ThermalPrintSettingActivity.class, bn, CommonUtilities.MY_THERMAL_REQ_CODE);

            } else {
                startprint();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                connectThermalPrinter();
                break;

            }
            case Utils.REQUEST_ENABLE_BT: {
                if (customDialog != null) {
                    customDialog.closeDialog(false);
                }
                break;

            }
        }
    }


    private void connectThermalPrinter() {

        if (customDialog != null && customDialog.isShowing()) {
            return;
        }

        customDialog = new CustomDialog(getActContext());
        customDialog.setDetails(generalFunc.retrieveLangLBl("", "LBL_T_PRINTER_ALERT_TITLE_TXT"), null, null, generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), false, R.drawable.ic_printer, true, true, 2);
        customDialog.setPrintDetails(true, dataList, reqiuredDetails);
        customDialog.setDirection(CustomDialog.OpenDirection.BOTTOM);
        customDialog.setRoundedViewBackgroundColor(R.color.appThemeColor_1);
        customDialog.setIconTintColor(R.color.white);
        customDialog.setBtnRadius(10);
        customDialog.setImgStrokWidth(10);
        customDialog.setTitleTxtColor(R.color.appThemeColor_1);
        customDialog.createDialog();
       /* customDialog.setNegativeButtonClick(new Closure() {
            @Override
            public void exec() {
            }
        });*/
        customDialog.setCloseDialogListener(new Closure() {
            @Override
            public void exec() {
                customDialog = null;
            }
        });
        customDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.TRACK_ORDER_REQ_CODE && resultCode == RESULT_OK) {
            backImgView.performClick();
        } else if (requestCode == Utils.REQUEST_ENABLE_BT /*&& resultCode == RESULT_OK */) {
            customDialog.startDiscovery();
        } else if (requestCode == CommonUtilities.MY_THERMAL_REQ_CODE /*&& resultCode == RESULT_OK */) {
        }
    }


    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());

        if (view.getId() == declineBtn.getId() || view.getId() == declineAssignBtn.getId()) {
            getDeclineReasonsList();
            return;
        } else if (view.getId() == confirmBtn.getId()) {

            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Are you sure, you want to confirm order? After confirming order you will not be able to change items' availability.", "LBL_CONFIRM_ORDER_ALERT"), generalFunc.retrieveLangLBl("", "LBL_BTN_NO_TXT"), generalFunc.retrieveLangLBl("", "LBL_BTN_YES_TXT"), buttonId -> {
                if (buttonId == 1) {
                    confirmOrder("");
                }
            });


            return;
        } else if (view.getId() == trackOrderBtn.getId()) {
            Bundle bn = new Bundle();
            bn.putString("iOrderId", orderData.get("iOrderId"));
            (new ActUtils(getActContext())).startActForResult(TrackOrderActivity.class, bn, Utils.TRACK_ORDER_REQ_CODE);
            return;
        } else if (view.getId() == assignDriverBtn.getId() || view.getId() == reAssignBtn.getId()) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_CONFIRM_ASSIGN_DRIVER"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_ASSIGN"), buttonId -> {

                if (buttonId == 1) {

                    if (isOpenDriverSelection) {
                        openDriverType();
                    } else {
                        assignDriver();
                    }
                }
            });

            return;
        } else if (view.getId() == orderPickedUpBtn.getId()) {

            if (vIdProofImageUploaded != null && vIdProofImageUploaded.equalsIgnoreCase("Yes")) {
                openproofDailog();
            } else {


                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Are you sure, you want to confirm order? After confirming order you will not be able to change items' availability.", "LBL_CONFIRM_NOTE_PICKUP_ORDER"), generalFunc.retrieveLangLBl("", "LBL_BTN_NO_TXT"), generalFunc.retrieveLangLBl("", "LBL_BTN_YES_TXT"), buttonId -> {
                    if (buttonId == 1) {

                        confirmOrder("Yes");
                    }
                });
            }
            return;
        } else if (view.getId() == viewPrescTxtView.getId()) {

            Bundle bn = new Bundle();
            bn.putSerializable("imageList", imageList);
            (new ActUtils(getActContext())).startActWithData(PrescriptionActivity.class, bn);

        }

        switch (view.getId()) {
            case R.id.backImgView:
                OrderDetailActivity.super.onBackPressed();
                break;

            case R.id.iconImgView:
                MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                        .setCallId(iUserid)
                        .setPhoneNumber(userPhoneNumber)
                        .setToMemberType(Utils.CALLTOPASSENGER)
                        .setToMemberName(userName)
                        .setToMemberImage(vUserImage)
                        .setMedia(CommunicationManager.MEDIA_TYPE)
                        .build();
                CommunicationManager.getInstance().toCall(MyApp.getInstance().getCurrentAct(), mDataProvider);
                break;

            case R.id.connectPrinterArea:
                generateBillData(false);
                break;

            case R.id.iconInstructionView:
                Bundle bundle = new Bundle();

                boolean hasInstructions = vInstruction != null && !vInstruction.equals("");
                if (hasInstructions) {
                    bundle.putString("vInstruction", vInstruction);
                    // generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Special Instruction", "LBL_FOOD_INSTRUCTION_TXT"), vInstruction);
                } //else {
                // generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Special Instruction", "LBL_FOOD_INSTRUCTION_TXT"), generalFunc.retrieveLangLBl("", "LBL_NO_FOOD_INSTRUCTION"));
                // }

                if (isPrefrence || hasInstructions) {
                    iconInstructionView.setVisibility(View.VISIBLE);

                    bundle.putString("DeliveryPreferences", DeliveryPreferences.toString());
                    new ActUtils(getActContext()).startActWithData(UserPrefrenceActivity.class, bundle);
                }

                break;

            case R.id.iconRefreshImgView:
                getOrderDetails();
                break;

            case R.id.bgView:
                if (chargeDetailArea.getVisibility() == View.VISIBLE) {
                    chargeDetailArea.setVisibility(View.GONE);
                    bgView.setVisibility(View.GONE);
                    if (isPrefrence) {
                        moreinstructionLyout.setVisibility(View.GONE);
                    }
                    View totalChargeView = chargeDetailTitleArea.getChildAt(0);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.VISIBLE);
                    (totalChargeView.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setImageResource(R.mipmap.ic_arrow_up);
                    if (PrescriptionImages != null && !PrescriptionImages.equals("")) {
                        viewPrescTxtView.setVisibility(View.VISIBLE);
                    }

                }
                break;
            case R.id.chargeDetailTitleArea:
                if (chargeDetailArea.getVisibility() == View.VISIBLE) {
                    chargeDetailArea.setVisibility(View.GONE);
                    bgView.setVisibility(View.GONE);

                    if (isPrefrence) {
                        moreinstructionLyout.setVisibility(View.GONE);
                    }

                    View totalChargeView = chargeDetailTitleArea.getChildAt(0);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.VISIBLE);
                    (totalChargeView.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setImageResource(R.mipmap.ic_arrow_up);
                    if (PrescriptionImages != null && !PrescriptionImages.equals("")) {
                        viewPrescTxtView.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (isPrefrence) {
                        moreinstructionLyout.setVisibility(View.GONE);
                    }


                    if (PrescriptionImages != null && !PrescriptionImages.equals("")) {
                        viewPrescTxtView.setVisibility(View.GONE);
                    }

                    chargeDetailArea.setVisibility(View.VISIBLE);
                    bgView.setVisibility(View.VISIBLE);

                    View totalChargeView = chargeDetailTitleArea.getChildAt(0);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.VISIBLE);
                    ((ImageView) totalChargeView.findViewById(R.id.indicatorImg)).setImageResource(R.mipmap.ic_arrow_down);
                    (totalChargeView.findViewById(R.id.shadowView)).setVisibility(View.GONE);

                    View firstChargeView = chargeDetailArea.getChildAt(0);
                    ((ImageView) firstChargeView.findViewById(R.id.indicatorImg)).setVisibility(View.GONE);
                    (firstChargeView.findViewById(R.id.shadowView)).setVisibility(View.VISIBLE);

                }
                break;
        }
    }


    public void openproofDailog() {


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.proof_dialog_design, null);


        final MTextView noteTxt = (MTextView) dialogView.findViewById(R.id.noteTxt);
        final MTextView clickToLargeTxt = (MTextView) dialogView.findViewById(R.id.clickToLargeTxt);
        final ImageView itemImg = (ImageView) dialogView.findViewById(R.id.itemImg);
        final ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);


        final MButton btn_confirm = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_confirm)).getChildView();
        final MButton btn_discard = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_discard)).getChildView();

        clickToLargeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CLICK_TO_LARGE"));


        btn_confirm.setOnClickListener(v ->
        {
            ConfirmproofAlert.dismiss();
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Are you sure, you want to confirm order? After confirming order you will not be able to change items' availability.", "LBL_CONFIRM_NOTE_PICKUP_ORDER"), generalFunc.retrieveLangLBl("", "LBL_BTN_NO_TXT"), generalFunc.retrieveLangLBl("", "LBL_BTN_YES_TXT"), buttonId -> {
                if (buttonId == 1) {

                    confirmOrder("Yes");
                }
            });

        });

        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showproofHelp();
                ConfirmproofAlert.dismiss();
            }
        });

        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmproofAlert.dismiss();
            }
        });
        builder.setView(dialogView);
        btn_confirm.setText(generalFunc.retrieveLangLBl("Confirm", "LBL_CONFIRM_TXT"));
        btn_discard.setText(generalFunc.retrieveLangLBl("Discard", "LBL_DECLINE_TXT"));
        noteTxt.setText(vIdProofImageNote);
        new LoadImage.builder(LoadImage.bind(vIdProofImage), itemImg).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();


        ConfirmproofAlert = builder.create();
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(ConfirmproofAlert);
        }
        ConfirmproofAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.all_roundcurve_card));
        ConfirmproofAlert.show();
        ConfirmproofAlert.setCancelable(false);
        ConfirmproofAlert.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(getActContext()));

        itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActUtils(getActContext()).openURL(vIdProofImage);


            }

        });


    }

    private void showproofHelp() {

        PreferenceDailogJava preferenceDailogJava = new PreferenceDailogJava(getActContext());
        preferenceDailogJava.showPreferenceDialog("", generalFunc.retrieveLangLBl("", "LBL_PROOF_DECLINE_NOTE"), R.drawable.ic_caution, false, generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT")
                , generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), true);

    }


    public void openDriverType() {

        new DriverTypeDialog().run();
    }

    class DriverTypeDialog implements Runnable {

        @Override
        public void run() {

            final Dialog dialog_img_update = new Dialog(getActContext(), R.style.ImageSourceDialogStyle);
            dialog_img_update.setContentView(R.layout.design_image_source_select_new);
            MTextView personalTxt = (MTextView) dialog_img_update.findViewById(R.id.cameraTxt);
            MTextView otherTxt = (MTextView) dialog_img_update.findViewById(R.id.galleryTxt);
            MTextView titleTxt = (MTextView) dialog_img_update.findViewById(R.id.titleTxt);
            LinearLayout cameraView = (LinearLayout) dialog_img_update.findViewById(R.id.cameraView);
            LinearLayout galleryView = (LinearLayout) dialog_img_update.findViewById(R.id.galleryView);

            MButton btn_type2 = ((MaterialRippleLayout) dialog_img_update.findViewById(R.id.btn_type2)).getChildView();
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

            personalTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PERSONAL"));
            otherTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PROVIDER_SELECTION_TXT"));

            ImageView closeDialogImgView = (ImageView) dialog_img_update.findViewById(R.id.closeDialogImgView);

            closeDialogImgView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }

            });

            btn_type2.setOnClickListener(view -> dialog_img_update.dismiss());


            cameraView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                eDriverType = "Personal";
                assignDriver();


            });

            galleryView.setOnClickListener(v -> {

                if (dialog_img_update != null) {
                    dialog_img_update.cancel();
                }
                eDriverType = "Site";
                assignDriver();


            });

            dialog_img_update.setCanceledOnTouchOutside(true);

            Window window = dialog_img_update.getWindow();
            window.setGravity(Gravity.BOTTOM);

            window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialog_img_update.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            if (generalFunc.isRTLmode()) {
                dialog_img_update.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            dialog_img_update.show();

        }

    }
}
