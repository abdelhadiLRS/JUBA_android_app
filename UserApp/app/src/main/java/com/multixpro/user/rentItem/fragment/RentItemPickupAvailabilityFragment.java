package com.multixpro.user.rentItem.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.fragments.BaseFragment;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.R;
import com.multixpro.user.rentItem.RentItemNewPostActivity;
import com.multixpro.user.rentItem.adapter.RentPickupTimeSlotAdapter;
import com.utils.CommonUtilities;
import com.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class RentItemPickupAvailabilityFragment extends BaseFragment {

    private GeneralFunctions generalFunc;
    @Nullable
    private RentItemNewPostActivity mActivity;
    private RecyclerView rvPickupTimeSlots;
    private final ArrayList<HashMap<String, String>> pickupTimeSlotList = new ArrayList<>();
    private RentPickupTimeSlotAdapter mTimeSlotAdapter;

    private CheckBox showCallMeCheckbox;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rent_item_pickup_availability, container, false);

        rvPickupTimeSlots = view.findViewById(R.id.rvPickupTimeSlots);
        rvPickupTimeSlots.setAdapter(mTimeSlotAdapter);

        showCallMeCheckbox = view.findViewById(R.id.showCallMeCheckbox);

        if (mActivity != null) {
            showCallMeCheckbox.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ALLOW_USER_TO_CALL"));
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApp.getInstance().getCurrentAct() instanceof RentItemNewPostActivity) {
            mActivity = (RentItemNewPostActivity) requireActivity();
            generalFunc = mActivity.generalFunc;
            mTimeSlotAdapter = new RentPickupTimeSlotAdapter(generalFunc, mActivity.getSupportFragmentManager(), pickupTimeSlotList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActivity != null) {
            if (mActivity.eType.equalsIgnoreCase("RentEstate") || mActivity.eType.equalsIgnoreCase("RentCars")) {
                mActivity.selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ESTATE_AVAILABILTY_TXT"));
            } else {
                mActivity.selectServiceTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_ITEM_PICKUP_AVAILBILITY"));
            }
            showCallMeCheckbox.setChecked(mActivity.mRentItemData.isShowCallMe());
            setTimeData();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setTimeData() {
        pickupTimeSlotList.clear();
        if (mActivity != null) {
            JSONArray obj_date_temp = mActivity.mRentItemData.getPickupTimeSlot();
            if (obj_date_temp != null) {
                for (int k = 0; k < obj_date_temp.length(); k++) {
                    JSONObject jsonObject = generalFunc.getJsonObject(obj_date_temp, k);
                    HashMap<String, String> map = new HashMap<>();

                    String field = generalFunc.getJsonValueStr("field", jsonObject);
                    String fromSlotName = ("v" + field + "FromSlot");
                    String toSlotName = ("v" + field + "ToSlot");
                    String fromSlotValue = generalFunc.getJsonValueStr(fromSlotName, jsonObject);
                    String toSlotValue = generalFunc.getJsonValueStr(toSlotName, jsonObject);

                    map.put("dayname", generalFunc.getJsonValueStr("dayname", jsonObject));
                    map.put("field", field);

                    Date fromSlotDate = Utils.convertStringToDate(CommonUtilities.OriginalTimeFormate, fromSlotValue);
                    String fromSlot;
                    if (fromSlotDate != null) {
                        fromSlot = Utils.convertDateToFormat(CommonUtilities.OriginalTimeFormate, fromSlotDate);
                    } else {
                        fromSlot = Utils.formatDate("HH:mm", CommonUtilities.OriginalTimeFormate, fromSlotValue);
                    }
                    map.put("FromSlot", Utils.checkText(fromSlot) ? fromSlot : fromSlotValue);

                    Date toSlotDate = Utils.convertStringToDate(CommonUtilities.OriginalTimeFormate, toSlotValue);
                    String toSlot;
                    if (toSlotDate != null) {
                        toSlot = Utils.convertDateToFormat(CommonUtilities.OriginalTimeFormate, toSlotDate);
                    } else {
                        toSlot = Utils.formatDate("HH:mm", CommonUtilities.OriginalTimeFormate, toSlotValue);
                    }
                    map.put("ToSlot", Utils.checkText(toSlot) ? toSlot : toSlotValue);

                    map.put("FromSlotKeyName", fromSlotName);
                    map.put("ToSlotKeyName", toSlotName);
                    pickupTimeSlotList.add(map);
                }
            }
            mTimeSlotAdapter.notifyDataSetChanged();
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> mActivity.setPagerHeight(), 200);
    }

    public void checkPageNext() {
        if (mActivity != null) {

            boolean mandatoryFieldsBlank = false;
            for (int i = 0; i < pickupTimeSlotList.size(); i++) {
                if (!(mTimeSlotAdapter.getMyTime("", pickupTimeSlotList.get(i).get("FromSlot")).equalsIgnoreCase(""))) {
                    mandatoryFieldsBlank = true;
                }
            }

            if (mandatoryFieldsBlank) {
                JSONArray arrFor = new JSONArray();
                for (int i = 0; i < pickupTimeSlotList.size(); i++) {
                    try {
                        JSONObject item = new JSONObject();
                        item.put("dayname", pickupTimeSlotList.get(i).get("dayname"));
                        item.put("field", pickupTimeSlotList.get(i).get("field"));

                        String fromSlot = Utils.formatDate(CommonUtilities.OriginalTimeFormate, "HH:mm", pickupTimeSlotList.get(i).get("FromSlot"));
                        item.put(Objects.requireNonNull(pickupTimeSlotList.get(i).get("FromSlotKeyName")), Utils.checkText(fromSlot) ? fromSlot : pickupTimeSlotList.get(i).get("FromSlot"));

                        String toSlot = Utils.formatDate(CommonUtilities.OriginalTimeFormate, "HH:mm", pickupTimeSlotList.get(i).get("ToSlot"));
                        item.put(Objects.requireNonNull(pickupTimeSlotList.get(i).get("ToSlotKeyName")), Utils.checkText(toSlot) ? toSlot : pickupTimeSlotList.get(i).get("ToSlot"));
                        arrFor.put(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mActivity.mRentItemData.setPickupTimeSlot(arrFor);
                mActivity.mRentItemData.setShowCallMe(showCallMeCheckbox.isChecked());
                mActivity.createRentPost();
            } else {
                generalFunc.showMessage(rvPickupTimeSlots, generalFunc.retrieveLangLBl("", "LBL_RENT_ITEM_SELECT_TIMESLOT_VALIDATION_MSG"));
            }
        }
    }
}