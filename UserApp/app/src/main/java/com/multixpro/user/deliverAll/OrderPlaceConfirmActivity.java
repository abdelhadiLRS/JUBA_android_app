package com.multixpro.user.deliverAll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.MyApp;
import com.multixpro.user.BookingActivity;
import com.multixpro.user.R;
import com.multixpro.user.rideSharing.RideMyList;
import com.model.ServiceModule;
import com.realmModel.Cart;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import io.realm.Realm;

public class OrderPlaceConfirmActivity extends ParentActivity {

    private MButton btn_type2;
    private boolean isRideSharing, isRideBookingAccept;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_place_confirm);

        MTextView titleTxt = findViewById(R.id.titleTxt);
        ImageView backImgView = findViewById(R.id.backImgView);
        addToClickHandler(backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }

        MTextView placeOrderTitle = findViewById(R.id.placeOrderTitle);
        placeOrderTitle.setVisibility(View.GONE);

        MTextView placeOrderNote = findViewById(R.id.placeOrderNote);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(Utils.generateViewId());
        addToClickHandler(btn_type2);

        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.delete(Cart.class);
        realm.commitTransaction();

        isRideSharing = getIntent().getBooleanExtra("isRideSharing", false);
        isRideBookingAccept = getIntent().getBooleanExtra("isRideBookingAccept", false);

        if (isRideSharing) {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PUBLISHED_RIDE_TXT"));
            placeOrderNote.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PUBLISH_RIDE_SUCCESS_TEXT"));
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_SEE_MY_RIDE_TXT"));

        } else if (isRideBookingAccept) {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PUBLISHED_RIDE_TXT"));

            placeOrderTitle.setVisibility(View.VISIBLE);
            placeOrderTitle.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_BOOKING_APPROVED_TXT"));
            placeOrderNote.setText(getIntent().getStringExtra("driverName") + " " + generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_TRAVEL_WITH_YOU_TXT"));

            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_SEE_MY_RIDE_TXT"));

        } else {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_PLACED"));
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_YOUR_ORDER"));

            if (getIntent().getStringExtra("eTakeAway") != null && getIntent().getStringExtra("eTakeAway").equalsIgnoreCase("Yes")) {
                placeOrderNote.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_PLACE_MSG_TAKE_AWAY"));
            } else {
                placeOrderNote.setText(generalFunc.retrieveLangLBl("", "LBL_ORDER_PLACE_MSG"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (generalFunc.prefHasKey(Utils.iServiceId_KEY) && generalFunc != null && !ServiceModule.isDeliverAllOnly()) {
            generalFunc.removeValue(Utils.iServiceId_KEY);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        MyApp.getInstance().restartWithGetDataApp();
    }

    private Context getActContext() {
        return OrderPlaceConfirmActivity.this;
    }

    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        int i = view.getId();
        if (i == R.id.backImgView) {

            MyApp.getInstance().restartWithGetDataApp();
        } else if (i == btn_type2.getId()) {

            if (isRideSharing || isRideBookingAccept) {
                new ActUtils(getActContext()).startAct(RideMyList.class);
                finish();
            } else {
                Bundle bn = new Bundle();
                bn.putBoolean("isOrder", true);
                bn.putString("iOrderId", getIntent().getStringExtra("iOrderId"));
                new ActUtils(getActContext()).startActWithData(BookingActivity.class, bn);
            }
        }
    }
}