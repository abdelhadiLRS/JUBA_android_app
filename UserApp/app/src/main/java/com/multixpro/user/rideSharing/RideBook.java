package com.multixpro.user.rideSharing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.dialogs.OpenListView;
import com.general.DatePicker;
import com.general.files.ActUtils;
import com.multixpro.user.R;
import com.multixpro.user.SearchPickupLocationActivity;
import com.multixpro.user.databinding.ActivityRideBookBinding;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MButton;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RideBook extends ParentActivity {

    private ActivityRideBookBinding binding;
    private final Calendar dateTimeCalender = Calendar.getInstance(Locale.getDefault());
    private final Calendar maxDateCalender = Calendar.getInstance(Locale.getDefault());
    private final ArrayList<String> passengersNoList = new ArrayList<>();
    private MButton searchBtn;
    private boolean isStartLocationClick = false;
    private String mSLatitude, mSLongitude, mELatitude, mELongitude;
    private int selCurrentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_book);

        maxDateCalender.set(Calendar.MONTH, dateTimeCalender.get(Calendar.MONTH) + 2);

        initialization();
        setData();
    }

    @SuppressLint("SetTextI18n")
    private void initialization() {
        addToClickHandler(binding.backBtn);
        if (generalFunc.isRTLmode()) {
            binding.backBtn.setRotation(0);
            binding.imgBanner.setRotationY(180);
        }

        binding.imageHeaderTxt.setText(generalFunc.retrieveLangLBl("", "LBL_BOOK_RIDE_HEADER_TXT"));

        //
        binding.startLocationBox.setHint(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_START_LOC_TXT"));
        binding.endLocationBox.setHint(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_END_LOC_TXT"));

        addToClickHandler(binding.startLocationBox);
        addToClickHandler(binding.endLocationBox);

        binding.dateTimeHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_SELECT_DATE") + " *");
        binding.dateTimeEditBox.setHint(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_SELECT_DATE"));
        binding.dateTimeEditBox.setText(Utils.convertDateToFormat(CommonUtilities.WithoutDayFormat, dateTimeCalender.getTime()));
        addToClickHandler(binding.dateTimeEditBox);

        binding.personHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PERSON"));
        binding.personSeatTxt.setHint(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_AND_SELECT"));
        addToClickHandler(binding.personSeatTxt);

        searchBtn = ((MaterialRippleLayout) binding.searchBtn).getChildView();
        searchBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_SEARCH"));
        searchBtn.setId(Utils.generateViewId());
        addToClickHandler(searchBtn);
    }

    private void setData() {
        passengersNoList.clear();
        JSONArray arr_msg = generalFunc.getJsonArray(generalFunc.getJsonValueStr("RIDE_SHARE_PASSENGER_NOS", obj_userProfile));
        if (arr_msg != null) {
            for (int j = 0; j < arr_msg.length(); j++) {
                Object value = generalFunc.getJsonValue(arr_msg, j);
                if (j == 0) {
                    binding.personSeatTxt.setText(value.toString());
                }
                if (value.toString().equalsIgnoreCase(binding.personSeatTxt.getText().toString())) {
                    selCurrentPosition = j;
                }
                passengersNoList.add("" + generalFunc.getJsonValue(arr_msg, j));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ADD_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {
            if (isStartLocationClick) {
                binding.startLocationBox.setText(data.getStringExtra("Address"));
                mSLatitude = data.getStringExtra("Latitude");
                mSLongitude = data.getStringExtra("Longitude");
            } else {
                binding.endLocationBox.setText(data.getStringExtra("Address"));
                mELatitude = data.getStringExtra("Latitude");
                mELongitude = data.getStringExtra("Longitude");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void onClick(View view) {
        int i = view.getId();
        if (i == binding.backBtn.getId()) {
            onBackPressed();

        } else if (i == binding.startLocationBox.getId() || i == binding.endLocationBox.getId()) {
            Bundle bndl = new Bundle();
            isStartLocationClick = i == binding.startLocationBox.getId();
            bndl.putString("IS_FROM_SELECT_LOC", "Yes");
            new ActUtils(this).startActForResult(SearchPickupLocationActivity.class, bndl, Utils.ADD_LOC_REQ_CODE);

        } else if (i == binding.dateTimeEditBox.getId()) {

            DatePicker.show(this, generalFunc, Calendar.getInstance(), maxDateCalender,
                    Utils.convertDateToFormat(CommonUtilities.DayFormatEN, dateTimeCalender.getTime()), null, (year, monthOfYear, dayOfMonth) -> {

                        dateTimeCalender.set(Calendar.YEAR, year);
                        dateTimeCalender.set(Calendar.MONTH, monthOfYear - 1);
                        dateTimeCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        binding.dateTimeEditBox.setText(Utils.convertDateToFormat(CommonUtilities.WithoutDayFormat, dateTimeCalender.getTime()));
                    });
        } else if (i == binding.personSeatTxt.getId()) {

            OpenListView.getInstance(this, generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PASSENGER_AVAILABLE_SEAT"), passengersNoList, OpenListView.OpenDirection.CENTER, true, true, position -> {
                selCurrentPosition = position;
                binding.personSeatTxt.setText(passengersNoList.get(position));
            }).show(selCurrentPosition, "vTitle");

        } else if (i == searchBtn.getId()) {
            if (Utils.checkText(mSLatitude) && Utils.checkText(mSLongitude)
                    && Utils.checkText(mELatitude) && Utils.checkText(mELongitude)) {

                Bundle bn = new Bundle();
                bn.putString("tStartLat", mSLatitude);
                bn.putString("tStartLong", mSLongitude);
                bn.putString("tEndLat", mELatitude);
                bn.putString("tEndLong", mELongitude);

                bn.putString("dStartDate", Utils.getText(binding.dateTimeEditBox));
                bn.putString("NoOfSeats", Utils.getText(binding.personSeatTxt));

                new ActUtils(this).startActWithData(RideBookSearchList.class, bn);
            } else {
                generalFunc.showMessage(binding.backBtn, generalFunc.retrieveLangLBl("", "LBL_ENTER_REQUIRED_FIELDS"));
            }
        }
    }
}