package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.multixpro.provider.MainActivity;
import com.multixpro.provider.MainActivity_22;
import com.multixpro.provider.R;
import com.multixpro.provider.deliverAll.LiveTaskListActivity;
import com.model.ServiceModule;
import com.view.MTextView;

import org.json.JSONObject;


public class AddBottomBar {


    public JSONObject userProfileJson;
    Context mContext;
    View view;

    public LinearLayout profileArea, homeArea, historyArea, walletArea;
    MTextView historyTxt, walletTxt, profileTxt, homeTxt;
    ImageView home_img, bookingImg, walletImg, profileImg;
    GeneralFunctions generalFunc;
    private boolean isNewHome_23;


    public AddBottomBar(Context mContext, JSONObject userProfileJson) {

        this.mContext = mContext;
        this.userProfileJson = userProfileJson;
        generalFunc = new GeneralFunctions(mContext);
        view = ((Activity) mContext).findViewById(android.R.id.content);

        isNewHome_23 = generalFunc.getJsonValueStr("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP_23", userProfileJson) != null && generalFunc.getJsonValueStr("ENABLE_NEW_HOME_SCREEN_LAYOUT_APP_23", userProfileJson).equalsIgnoreCase("Yes");


        historyTxt = view.findViewById(R.id.historyTxt);
        walletTxt = view.findViewById(R.id.walletTxt);
        profileTxt = view.findViewById(R.id.profileTxt);
        homeTxt = view.findViewById(R.id.homeTxt);
        home_img = view.findViewById(R.id.home_img);
        bookingImg = view.findViewById(R.id.bookingImg);
        walletImg = view.findViewById(R.id.walletImg);
        profileImg = view.findViewById(R.id.profileImg);


        historyArea = view.findViewById(R.id.historyArea);
        walletArea = view.findViewById(R.id.walletArea);
        if (ServiceModule.IsTrackingProvider) {
            walletArea.setVisibility(View.GONE);
        }
        profileArea = view.findViewById(R.id.profileArea);
        homeArea = view.findViewById(R.id.homeArea);
        profileArea.setOnClickListener(new setOnClickList());
        homeArea.setOnClickListener(new setOnClickList());
        walletArea.setOnClickListener(new setOnClickList());
        historyArea.setOnClickListener(new setOnClickList());
        manageBottomMenu(homeTxt);
        setLabel();
    }

    public void setLabel() {
        profileTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_PROFILE"));

        homeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HOME_BOTTOM_MENU"));
        walletTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_WALLET"));
        if (ServiceModule.isDeliverAllOnly()) {
            historyTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MY_ORDERS_TXT"));
        } else if (ServiceModule.IsTrackingProvider) {
            historyTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRIP"));
        } else {
            historyTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_BOOKINGS"));
        }


    }

    public void manageBottomMenu(MTextView selTextView) {

        int color = R.color.appThemeColor_1;
        int deSelectColor = R.color.homedeSelectColor;
        if (isNewHome_23 && ServiceModule.isRideOnly()) {
            color = R.color.homeSelectColor_23;
            deSelectColor = R.color.homeDeSelectColor_23;
        }
        //manage Select deselect Bottom Menu
        if (selTextView.getId() == homeTxt.getId()) {
            homeTxt.setTextColor(mContext.getResources().getColor(color));
            home_img.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);
            home_img.setImageResource(R.drawable.ic_home_fill);
        } else {
            homeTxt.setTextColor(mContext.getResources().getColor(deSelectColor));
            home_img.setColorFilter(ContextCompat.getColor(mContext, deSelectColor), android.graphics.PorterDuff.Mode.SRC_IN);
            home_img.setImageResource(R.drawable.ic_home);
        }

        if (selTextView.getId() == historyTxt.getId()) {
            historyTxt.setTextColor(mContext.getResources().getColor(color));
            bookingImg.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);
            bookingImg.setImageResource(R.drawable.ic_booking_fill);
        } else {
            historyTxt.setTextColor(mContext.getResources().getColor(deSelectColor));
            bookingImg.setColorFilter(ContextCompat.getColor(mContext, deSelectColor), android.graphics.PorterDuff.Mode.SRC_IN);
            bookingImg.setImageResource(R.drawable.ic_booking);
        }
        if (selTextView.getId() == walletTxt.getId()) {
            walletTxt.setTextColor(mContext.getResources().getColor(color));
            walletImg.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);
            walletImg.setImageResource(R.drawable.ic_wallet_fill);
        } else {
            walletTxt.setTextColor(mContext.getResources().getColor(deSelectColor));
            walletImg.setColorFilter(ContextCompat.getColor(mContext, deSelectColor), android.graphics.PorterDuff.Mode.SRC_IN);
            walletImg.setImageResource(R.drawable.ic_wallet);
        }
        if (selTextView.getId() == profileTxt.getId()) {
            profileTxt.setTextColor(mContext.getResources().getColor(color));
            profileImg.setColorFilter(ContextCompat.getColor(mContext, color), android.graphics.PorterDuff.Mode.SRC_IN);
            profileImg.setImageResource(R.drawable.ic_profile_fill);
        } else {
            profileTxt.setTextColor(mContext.getResources().getColor(deSelectColor));
            profileImg.setColorFilter(ContextCompat.getColor(mContext, deSelectColor), android.graphics.PorterDuff.Mode.SRC_IN);
            profileImg.setImageResource(R.drawable.ic_profile);
        }
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (view.getId() == profileArea.getId()) {
                manageBottomMenu(profileTxt);
                if (mContext instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.openProfileFragment();
                } else if (mContext instanceof MainActivity_22) {
                    MainActivity_22 MainActivity_22 = (MainActivity_22) mContext;
                    MainActivity_22.openProfileFragment();
                } else if (mContext instanceof LiveTaskListActivity) {
                    LiveTaskListActivity liveTaskListActivity = (LiveTaskListActivity) mContext;
                    liveTaskListActivity.openProfileFragment();
                }
            } else if (view.getId() == homeArea.getId()) {
                manageBottomMenu(homeTxt);
                if (mContext instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.manageHome();
                } else if (mContext instanceof MainActivity_22) {
                    MainActivity_22 MainActivity_22 = (MainActivity_22) mContext;
                    MainActivity_22.manageHome();
                } else if (mContext instanceof LiveTaskListActivity) {
                    LiveTaskListActivity liveTaskListActivity = (LiveTaskListActivity) mContext;
                    liveTaskListActivity.manageHome();
                }

            } else if (view.getId() == historyArea.getId()) {
                manageBottomMenu(historyTxt);

                if (mContext instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.openBookingFrgament();
                } else if (mContext instanceof MainActivity_22) {
                    MainActivity_22 MainActivity_22 = (MainActivity_22) mContext;
                    MainActivity_22.openBookingFrgament();
                } else if (mContext instanceof LiveTaskListActivity) {
                    LiveTaskListActivity liveTaskListActivity = (LiveTaskListActivity) mContext;
                    liveTaskListActivity.openBookingFrgament();
                }


            } else if (view.getId() == walletArea.getId()) {
                manageBottomMenu(walletTxt);
                if (mContext instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.openWalletFrgament();
                } else if (mContext instanceof MainActivity_22) {
                    MainActivity_22 MainActivity_22 = (MainActivity_22) mContext;
                    MainActivity_22.openWalletFrgament();
                } else if (mContext instanceof LiveTaskListActivity) {
                    LiveTaskListActivity liveTaskListActivity = (LiveTaskListActivity) mContext;
                    liveTaskListActivity.openWalletFragment();
                }

            }


        }
    }


}
