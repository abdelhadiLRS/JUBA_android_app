package com.multixpro.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.activity.ParentActivity;
import com.dialogs.BottomScheduleDialog;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SuccessDialog;
import com.service.handler.ApiHandler;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RequestBidInfoActivity extends ParentActivity implements BottomScheduleDialog.OndateSelectionListener {


    RelativeLayout selectyear_layout;
    MTextView reqBox;
    private static final int ADD_ADDRESS = 129;
    MTextView bidDateBoxLBL;
    MTextView bidBoxTxt, serviceNameTxt, serviceDescTxt, titleTxt;
    FrameLayout expDateSelectArea;
    MButton btn_type2;
    MTextView confirmTxt, cancelTxt;
    ImageView backImgView;
    MaterialEditText locBox, amountBox, commentBox, AddCateoryBox, CateoryBox;

    boolean isbtnclick = false;
    String required_str = "";
    String iUserAddressId, vServiceAddress;
    Date selctedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_bid_info);

        expDateSelectArea = (FrameLayout) findViewById(R.id.expDateSelectArea);
        reqBox = (MTextView) findViewById(R.id.reqBox);
        bidDateBoxLBL = (MTextView) findViewById(R.id.bidDateBoxLBL);
        bidBoxTxt = (MTextView) findViewById(R.id.bidBoxTxt);
        serviceNameTxt = (MTextView) findViewById(R.id.serviceNameTxt);
        serviceDescTxt = (MTextView) findViewById(R.id.serviceDescTxt);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        selectyear_layout = (RelativeLayout) findViewById(R.id.selectyear_layout);
        CateoryBox = (MaterialEditText) findViewById(R.id.CateoryBox);
        locBox = (MaterialEditText) findViewById(R.id.locBox);
        amountBox = (MaterialEditText) findViewById(R.id.amountBox);
        AddCateoryBox = (MaterialEditText) findViewById(R.id.AddCateoryBox);
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        commentBox.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        commentBox.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        commentBox.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);

        commentBox.setHideUnderline(true);

        if (generalFunc.isRTLmode()) {
            locBox.setPaddings((int) getResources().getDimension(R.dimen._24sdp), 0, 0, 0);
            commentBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
        } else {
            locBox.setPaddings(0, 0, (int) getResources().getDimension(R.dimen._24sdp), 0);
            commentBox.setPaddings((int) getResources().getDimension(R.dimen._5sdp), 0, 0, 0);
        }


        commentBox.setSingleLine(false);
        commentBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        commentBox.setGravity(Gravity.TOP);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        confirmTxt = (MTextView) findViewById(R.id.confirmTxt);
        cancelTxt = (MTextView) findViewById(R.id.cancelTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        addToClickHandler(backImgView);
        generalFunc = MyApp.getInstance().getGeneralFun(this);
        btn_type2.setId(Utils.generateViewId());
        addToClickHandler(confirmTxt);
        addToClickHandler(cancelTxt);

        selectyear_layout.setOnTouchListener(new setOnTouchList());
        addToClickHandler(selectyear_layout);

        if (getIntent().getStringExtra("SelectvVehicleType") != null && getIntent().getBooleanExtra("isOther", false)) {
            AddCateoryBox.setVisibility(View.VISIBLE);
        }
        setLabels();
    }


    public void setLabels() {
        AddCateoryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_ADD_BIDDING_SERVICE"));
        locBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_BIDDING_SERVICE_ADDRESS_TXT"));
        amountBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_BIDDING_BUDGET_TXT") + " ( " + generalFunc.getJsonValueStr("CurrencySymbol", obj_userProfile) + " )");
        bidDateBoxLBL.setText(generalFunc.retrieveLangLBl("", "LBL_BIDDING_DATE_TIME_TXT"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_BIDDING_POST_TASK_TITLE"));
        confirmTxt.setText(generalFunc.retrieveLangLBl("", "LBL_POST_BID_TXT"));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        commentBox.setHint(generalFunc.retrieveLangLBl("", "LBL_BIDDING_DETAILS_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        reqBox.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_TXT"));
        CateoryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_TASK_CATEGORY_TXT"));
        Utils.removeInput(locBox);
        locBox.setMaxLines(2);
        amountBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        addToClickHandler(locBox);
        locBox.setOnTouchListener(new setOnTouchList());
        CateoryBox.setText(getIntent().getStringExtra("SelectvVehicleType"));
        CateoryBox.setClickable(false);
        CateoryBox.setFocusable(false);


    }

    @Override
    public void onScheduleSelection(String selDateTime, Date date, String iCabBookingId) {

        bidBoxTxt.setText(Utils.convertDateToFormat(CommonUtilities.DayTimeFormat, date));
        selctedDate = date;

    }


    public static class setOnTouchList implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                view.performClick();
            }
            return true;
        }
    }

    public void openDateSelection() {

        BottomScheduleDialog bottomScheduleDialog = new BottomScheduleDialog(getActContext(), this);

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        if (selctedDate != null) {
            cal.setTime(selctedDate);
        } else {
            cal.add(Calendar.MINUTE, (Integer.parseInt(generalFunc.getJsonValueStr("MINIMUM_HOURS_LATER_BIDDING", obj_userProfile)) * 60) + 1);
        }
        bottomScheduleDialog.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_SCHEDULE_BOOKING_TXT")
                , generalFunc.retrieveLangLBl("", "LBL_SET"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"),
                "", true, cal);
    }

    public void checkData() {


        boolean locEntered = Utils.checkText(locBox) || Utils.setErrorFields(locBox, required_str);
        boolean amountEntered = Utils.checkText(amountBox) || Utils.setErrorFields(amountBox, required_str);

        boolean serviceNameEnterd = Utils.checkText(AddCateoryBox) || Utils.setErrorFields(AddCateoryBox, required_str);
        if (!getIntent().getBooleanExtra("isOther", false)) {
            serviceNameEnterd = true;
        }

        if (!locEntered || !amountEntered || !serviceNameEnterd) {
            return;
        }
        if (expDateSelectArea.getVisibility() == View.VISIBLE && !Utils.checkText(bidBoxTxt.getText().toString())) {
            generalFunc.showMessage(generalFunc.getCurrentView((Activity) this), generalFunc.retrieveLangLBl("", "LBL_SELECT_DATE_TIME"));
            return;
        }

        if (isbtnclick) {
            return;
        }
        isbtnclick = true;
        postBidTask();
        new Handler(Looper.myLooper()).postDelayed(() -> isbtnclick = false, 1000);

    }

    private void managedailog(String message) {
        SuccessDialog.showSuccessDialog(getActContext(), generalFunc.retrieveLangLBl("", "LBL_BIDDING_POSTED"), message, generalFunc.retrieveLangLBl("Done", "LBL_VIEW_BIDDING_TASK_TEXT"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), false, () -> {
            Bundle bn = new Bundle();
            bn.putBoolean("isrestart", true);
            bn.putBoolean("isBid", true);
            bn.putString("selType", Utils.CabGeneralType_UberX);
            new ActUtils(getActContext()).startActWithData(BookingActivity.class, bn);
            finish();
        }, () -> {
            Bundle bn = new Bundle();
            new ActUtils(getActContext()).startActWithData(UberXHomeActivity.class, bn);
            finishAffinity();
        });
    }

    public Context getActContext() {
        return RequestBidInfoActivity.this;
    }


    private void postBidTask() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "PostBid");
        parameters.put("iBiddingId", getIntent().getStringExtra("SelectedVehicleTypeId"));
        parameters.put("tDescription", Utils.getText(commentBox));
        parameters.put("fBiddingAmount", Utils.getText(amountBox));
        parameters.put("dBiddingDate", Utils.convertDateToFormat("yyyy-MM-dd HH:mm:ss", selctedDate));
        parameters.put("iAddressId", iUserAddressId);

        if (getIntent().getBooleanExtra("isOther", false)) {
            parameters.put("vServiceName", Utils.getText(AddCateoryBox));
        }


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {
            if (responseString != null && !responseString.equals("")) {
                String message_str = generalFunc.getJsonValue(Utils.message_str, responseString);
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    managedailog(generalFunc.retrieveLangLBl("", message_str));
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message_str));
                }
            } else {
                generalFunc.showError();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ADD_ADDRESS) {
            iUserAddressId = data.getStringExtra("addressId");
            vServiceAddress = data.getStringExtra("address");
            locBox.setText(vServiceAddress);

        }
    }


    public void onClick(View view) {
        int i = view.getId();
        Utils.hideKeyboard(RequestBidInfoActivity.this);
        if (i == R.id.backImgView) {
            RequestBidInfoActivity.super.onBackPressed();
        } else if (i == confirmTxt.getId()) {
            checkData();
        } else if (i == cancelTxt.getId()) {
            onBackPressed();
        } else if (i == R.id.selectyear_layout) {

            openDateSelection();
        } else if (i == R.id.locBox) {
            Bundle bn = new Bundle();
            bn.putBoolean("isBid", true);
            new ActUtils(getActContext()).startActForResult(ListAddressActivity.class, bn, ADD_ADDRESS);
        }
    }

}