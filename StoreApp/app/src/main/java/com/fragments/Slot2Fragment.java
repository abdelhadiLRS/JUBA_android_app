package com.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.RestaurantTimeSlotAdapter;
import com.multixpro.store.R;
import com.datepicker.files.SlideDateTimeListener;
import com.datepicker.files.SlideDateTimePicker;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.google.android.material.snackbar.Snackbar;
import com.service.handler.ApiHandler;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Slot2Fragment extends BaseFragment implements RestaurantTimeSlotAdapter.settTimeSlotClickList {

    View view;
    public GeneralFunctions generalFunc;
    public String userProfileJson;
    JSONObject userProfileJsonObj;

    MButton submitBtn;
    MTextView fromtimeSlotMonVTxt;
    MTextView totimeSlotFriVTxt;
    MTextView fromtimeSlotTwoMonVTxt;
    MTextView totimeSlotTwoFriVTxt;
    MTextView fromtimeSlotSatVTxt;
    MTextView totimeSlotSunVTxt;
    MTextView fromtimeSlotTwoSatVTxt;
    MTextView totimeSlotTwoSunVTxt;
    MTextView noteTxt;

    View slotMonCalenderArea;
    View slotFriCalenderArea;
    View slotTwoMonCalenderArea;
    View slotTwoFriCalenderArea;
    View slotSatCalenderArea;
    View slotSunCalenderArea;
    View slotTwoSatCalenderArea;
    View slotTwoSunCalenderArea;

    MTextView monfriSlotOneTxtView;
    MTextView monfriSlotTwoTxtView;
    MTextView satSunSlotOneTxtView;
    MTextView satSunSlotTwoTxtView;

//    MTextView toTxtView;

    String required_str = "";
    String error_email_str = "";

    String iCompanyId = "";

    View loadingBar;
    View containerView;
    ArrayList<HashMap<String, String>> timeSlotList = new ArrayList<>();
    private RecyclerView timeslotsRecyclerView;
    private View content;
    private RestaurantTimeSlotAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.design_hr_layout, container, false);
        Logger.d("STATE", "onCreateView");
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        userProfileJsonObj = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        iCompanyId = generalFunc.getJsonValueStr("iCompanyId", userProfileJsonObj);

        submitBtn = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();

        monfriSlotOneTxtView = (MTextView) view.findViewById(R.id.monfriSlotOneTxtView);
        monfriSlotTwoTxtView = (MTextView) view.findViewById(R.id.monfriSlotTwoTxtView);
        satSunSlotOneTxtView = (MTextView) view.findViewById(R.id.satSunSlotOneTxtView);
        satSunSlotTwoTxtView = (MTextView) view.findViewById(R.id.satSunSlotTwoTxtView);

        fromtimeSlotMonVTxt = (MTextView) view.findViewById(R.id.fromtimeSlotMonVTxt);
        totimeSlotFriVTxt = (MTextView) view.findViewById(R.id.totimeSlotFriVTxt);
        fromtimeSlotTwoMonVTxt = (MTextView) view.findViewById(R.id.fromtimeSlotTwoMonVTxt);
        totimeSlotTwoFriVTxt = (MTextView) view.findViewById(R.id.totimeSlotTwoFriVTxt);
        fromtimeSlotSatVTxt = (MTextView) view.findViewById(R.id.fromtimeSlotSatVTxt);
        totimeSlotSunVTxt = (MTextView) view.findViewById(R.id.totimeSlotSunVTxt);
        fromtimeSlotTwoSatVTxt = (MTextView) view.findViewById(R.id.fromtimeSlotTwoSatVTxt);
        totimeSlotTwoSunVTxt = (MTextView) view.findViewById(R.id.totimeSlotTwoSunVTxt);
        noteTxt = (MTextView) view.findViewById(R.id.noteTxt);
        noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HR_SELECT_NOTE"));
        timeslotsRecyclerView = (RecyclerView) view.findViewById(R.id.timeslotsRecyclerView);

        loadingBar = view.findViewById(R.id.loadingBar);
        containerView = view.findViewById(R.id.containerView);

//        toTxtView = (MTextView) view.findViewById(R.id.toTxtView);

        content = view.findViewById(R.id.content);
        slotMonCalenderArea = view.findViewById(R.id.slotMonCalenderArea);
        addToClickHandler(slotMonCalenderArea);

        slotFriCalenderArea = view.findViewById(R.id.slotFriCalenderArea);
        addToClickHandler(slotFriCalenderArea);

        slotTwoMonCalenderArea = view.findViewById(R.id.slotTwoMonCalenderArea);
        addToClickHandler(slotTwoMonCalenderArea);

        slotTwoFriCalenderArea = view.findViewById(R.id.slotTwoFriCalenderArea);
        addToClickHandler(slotTwoFriCalenderArea);

        slotSatCalenderArea = view.findViewById(R.id.slotSatCalenderArea);
        addToClickHandler(slotSatCalenderArea);

        slotSunCalenderArea = view.findViewById(R.id.slotSunCalenderArea);
        addToClickHandler(slotSunCalenderArea);


        slotTwoSatCalenderArea = view.findViewById(R.id.slotTwoSatCalenderArea);
        addToClickHandler(slotTwoSatCalenderArea);

        slotTwoSunCalenderArea = view.findViewById(R.id.slotTwoSunCalenderArea);
        addToClickHandler(slotTwoSunCalenderArea);


        submitBtn.setId(Utils.generateViewId());
        addToClickHandler(submitBtn);

        adapter = new RestaurantTimeSlotAdapter(getActContext(), timeSlotList, generalFunc, getActivity().getSupportFragmentManager());
        timeslotsRecyclerView.setAdapter(adapter);
        adapter.setOnClickList(this);

        sendTimeData("Display", new JSONObject());

        setLabels();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("STATE", "onResume");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d("STATE", "onViewCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Logger.d("STATE", "onViewStateRestored");
    }


    public void setLabels() {
        submitBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT")); //LBL_BTN_SUBMIT_TXT

        monfriSlotOneTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_MON_TO_FRI_SLOT1"));
        monfriSlotTwoTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_MON_TO_FRI_SLOT2"));
        satSunSlotOneTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_SAT_AND_SUN_SLOT1"));
        satSunSlotTwoTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_SAT_AND_SUN_SLOT2"));

//        toTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_TO"));

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
    }


    public Context getActContext() {
        return getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

        }
    }


    public void checkData() {

        boolean mandatoryFieldsBlank = false;

        for (int i = 0; i < timeSlotList.size(); i++) {

            if ((timeSlotList.get(i).get("FromSlot").equalsIgnoreCase("00:00") || timeSlotList.get(i).get("ToSlot").equalsIgnoreCase("00:00")) && timeSlotList.get(i).get("isMandatory").equalsIgnoreCase("Yes")) {
                mandatoryFieldsBlank = true;

                String slotName = "";
                if (timeSlotList.get(i).get("FromSlot").equalsIgnoreCase("00:00") && timeSlotList.get(i).get("ToSlot").equalsIgnoreCase("00:00")) {
                    slotName = "";
                } else /*if (!timeSlotList.get(i).get("FromSlot").equalsIgnoreCase("00:00") && timeSlotList.get(i).get("ToSlot").equalsIgnoreCase("00:00"))*/ {
                    slotName = timeSlotList.get(i).get("Slot");
                }/*else
                {
                    slotName="1";
                }*/

                String lblName = "LBL_SELECT_" + timeSlotList.get(i).get("field").toUpperCase(Locale.US) + "_SLT" + (slotName);
                Snackbar.make(content, generalFunc.retrieveLangLBl("", lblName), Snackbar.LENGTH_LONG)
                        .show();
                break;
            }

        /*    if (mandatoryFieldsBlank) {
                return;
            }*/

            if ((!timeSlotList.get(i).get("FromSlot").equalsIgnoreCase("00:00") &&
                    timeSlotList.get(i).get("ToSlot").equalsIgnoreCase("00:00")) || (timeSlotList.get(i).get("FromSlot").equalsIgnoreCase("00:00") &&
                    !timeSlotList.get(i).get("ToSlot").equalsIgnoreCase("00:00"))) {
                mandatoryFieldsBlank = true;
                String slotName = "";
                if (timeSlotList.get(i).get("FromSlot").equalsIgnoreCase("00:00") && timeSlotList.get(i).get("ToSlot").equalsIgnoreCase("00:00")) {
                    slotName = "";
                } else /*if (!timeSlotList.get(i).get("FromSlot").equalsIgnoreCase("00:00") && timeSlotList.get(i).get("ToSlot").equalsIgnoreCase("00:00"))*/ {
                    slotName = timeSlotList.get(i).get("Slot");
                }/*else
                {
                    slotName="1";
                }*/

                String lblName = "LBL_SELECT_" + timeSlotList.get(i).get("field").toUpperCase(Locale.US) + "_SLT" + (slotName);
                Snackbar.make(content, generalFunc.retrieveLangLBl("", lblName), Snackbar.LENGTH_LONG)
                        .show();
                break;
            }
        }
       /* if (Utils.getText(fromtimeSlotMonVTxt).equalsIgnoreCase("00:00") || Utils.getText(totimeSlotFriVTxt).equalsIgnoreCase("00:00")) {
            Snackbar.make(findViewById(android.R.id.content), generalFunc.retrieveLangLBl("", "LBL_SELECT_MON_FRI_SLT1"), Snackbar.LENGTH_LONG)
                    .show();
            return;
        } else if (Utils.getText(fromtimeSlotSatVTxt).equalsIgnoreCase("00:00") ||
                Utils.getText(totimeSlotSunVTxt).equalsIgnoreCase("00:00")) {
            Snackbar.make(findViewById(android.R.id.content), generalFunc.retrieveLangLBl("", "LBL_SELECT_SAT_SUN_SLT1"), Snackbar.LENGTH_LONG)
                    .show();
            return;
        } else if (!Utils.getText(fromtimeSlotTwoMonVTxt).equalsIgnoreCase("00:00") &&
                Utils.getText(totimeSlotTwoFriVTxt).equalsIgnoreCase("00:00")) {
            Snackbar.make(findViewById(android.R.id.content), generalFunc.retrieveLangLBl("", "LBL_MON_FRI_SLT2_RESTRICT"), Snackbar.LENGTH_LONG)
                    .show();
            return;
        } else if (!Utils.getText(fromtimeSlotTwoSatVTxt).equalsIgnoreCase("00:00") &&
                Utils.getText(totimeSlotTwoSunVTxt).equalsIgnoreCase("00:00")) {
            Snackbar.make(findViewById(android.R.id.content), generalFunc.retrieveLangLBl("", "LBL_SAT_SUN_SLT2_RESTRICT"), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }*/

        /*generalFunc.retrieveLangLBl("","LB_SET_TIMING")*/

        if (mandatoryFieldsBlank == false) {
            JSONObject objMainList = new JSONObject();
            JSONArray arrFor1 = new JSONArray();
            JSONArray arrFor2 = new JSONArray();

            for (int i = 0; i < timeSlotList.size(); i++) {

                try {
                    JSONObject item = new JSONObject();
                    item.put("dayname", timeSlotList.get(i).get("dayname"));
                    item.put("field", timeSlotList.get(i).get("field"));
                    item.put(timeSlotList.get(i).get("FromSlotKeyName"), Utils.formatDate("HH:mm", "HH:mm:ss", timeSlotList.get(i).get("FromSlot")));
                    item.put(timeSlotList.get(i).get("ToSlotKeyName"), Utils.formatDate("HH:mm", "HH:mm:ss", timeSlotList.get(i).get("ToSlot")));
                    if (timeSlotList.get(i).get("Slot").equalsIgnoreCase("1")) {
                        arrFor1.put(item);
                    } else {
                        arrFor2.put(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            try {
                //Finally add item arrays for "1" and "2" to main list with key
                objMainList.put("1", arrFor1);
                objMainList.put("2", arrFor2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sendTimeData("Update", objMainList);
        }
    }

    public void sendTimeData(String callType, JSONObject objMainList) {

        if (callType.equalsIgnoreCase("Display")) {
            containerView.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }


        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("type", "UpdateCompanyTiming");
        parameters.put("iCompanyId", iCompanyId);
      /*  parameters.put("vFromMonFriTimeSlot1", Utils.getText(fromtimeSlotMonVTxt));
        parameters.put("vToMonFriTimeSlot1", Utils.getText(totimeSlotFriVTxt));
        parameters.put("vFromMonFriTimeSlot2", Utils.getText(fromtimeSlotTwoMonVTxt));
        parameters.put("vToMonFriTimeSlot2", Utils.getText(totimeSlotTwoFriVTxt));
        parameters.put("vFromSatSunTimeSlot1", Utils.getText(fromtimeSlotSatVTxt));
        parameters.put("vToSatSunTimeSlot1", Utils.getText(totimeSlotSunVTxt));
        parameters.put("vFromSatSunTimeSlot2", Utils.getText(fromtimeSlotTwoSatVTxt));
        parameters.put("vToSatSunTimeSlot2", Utils.getText(totimeSlotTwoSunVTxt));*/
        parameters.put("CALL_TYPE", callType);
        parameters.put("restaurantTiming", objMainList);
        parameters.put("slot", "2");



        ApiHandler.execute(getActContext(), parameters, callType.equals("Display") ? false : true, generalFunc,
       responseString -> {
            loadingBar.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            if (responseString != null && !responseString.equals("")) {

                if (generalFunc.checkDataAvail(Utils.action_str, responseString) == true) {

                    if (callType.equalsIgnoreCase("Display")) {

                        JSONObject msg_obj = generalFunc.getJsonObject(Utils.message_str, responseString);
                        if (msg_obj.length() > 0) {
                            timeSlotList = new ArrayList<>();
                        }
                        for (int j = 0; j < msg_obj.length(); j++) {

                            JSONArray obj_date_temp = generalFunc.getJsonArray("" + (j + 2), msg_obj.toString());
                            for (int k = 0; k < obj_date_temp.length(); k++) {
                                try {
                                    JSONObject jsonObject = obj_date_temp.getJSONObject(k);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("Slot", "" + (2));
                                    map.put("SlotName", (k == 0) ? ("Slot " + (2)) : "");
                                    map.put("SlotName", generalFunc.retrieveLangLBl("", "LBL_SLOT_2"));
                                    map.put("isMandatory", "No");
//                                    map.put("isMandatory", "No");
                                    String field = generalFunc.getJsonValue("field", jsonObject.toString());
                                    String fromSlotName = ("v" + field + "FromSlot" + "" + (2));
                                    String toSlotName = ("v" + field + "ToSlot" + "" + (2));
                                    String fromSlotValue = generalFunc.getJsonValue(fromSlotName, jsonObject.toString());
                                    String toSlotValue = generalFunc.getJsonValue(toSlotName, jsonObject.toString());
                                    map.put("dayname", generalFunc.getJsonValue("dayname", jsonObject.toString()));
                                    map.put("field", field);
                                    map.put("FromSlot", Utils.formatDate("HH:mm:ss", "HH:mm", fromSlotValue));
                                    map.put("ToSlot", Utils.formatDate("HH:mm:ss", "HH:mm", toSlotValue));
                                    map.put("FromSlotKeyName", fromSlotName);
                                    map.put("ToSlotKeyName", toSlotName);
                                    map.put("toLbl", generalFunc.retrieveLangLBl("to", "LBL_TO"));
                                    timeSlotList.add(map);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                        adapter = new RestaurantTimeSlotAdapter(getActContext(), timeSlotList, generalFunc, getActivity().getSupportFragmentManager());
                        timeslotsRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {

                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), true);
                    }


                    containerView.setVisibility(View.VISIBLE);
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), true);
                }
            } else {
                generalFunc.showError();
            }
        });

    }

    public void selectTimeSlot(MTextView txtView) {
//        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//        int minute = mcurrentTime.get(Calendar.MINUTE);
//        TimePickerDialog mTimePicker;
//        mTimePicker = new TimePickerDialog(getActContext(), (timePicker, selectedHour, selectedMinute) -> txtView.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute)), hour, minute, true);
//        mTimePicker.setTitle(generalFunc.retrieveLangLBl("Select Time", "LBL_SELECT_TIME_TXT"));
//        mTimePicker.show();

//        Calendar mCurrCal = Calendar.getInstance();
//        mCurrCal.set(Calendar.HOUR,GeneralFunctions.parseIntegerValue(0,Utils.getText(totimeSlotFriVTxt).toString().split(":")[0]));
//        mCurrCal.set(Calendar.MINUTE,GeneralFunctions.parseIntegerValue(0,Utils.getText(totimeSlotFriVTxt).toString().split(":")[1]));


        int i = txtView.getId();
        if (i == R.id.totimeSlotFriVTxt) {
            if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotMonVTxt).replace(":", "")) < 1) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_FROM_TIME"));
                return;
            }
        } else if (i == R.id.totimeSlotTwoFriVTxt) {
            if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotTwoMonVTxt).replace(":", "")) < 1) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_FROM_TIME"));
                return;
            }
        } else if (i == R.id.totimeSlotSunVTxt) {
            if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotSatVTxt).replace(":", "")) < 1) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_FROM_TIME"));
                return;
            }
        } else if (i == R.id.totimeSlotTwoSunVTxt) {
            if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotTwoSatVTxt).replace(":", "")) < 1) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_FROM_TIME"));
                return;
            }
        }

        new SlideDateTimePicker.Builder(getActivity().getSupportFragmentManager())
                .setListener(new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date) {
                        String selectedTime = Utils.convertDateToFormat("HH:mm", date);

                        boolean isSetTime = true;

                        switch (txtView.getId()) {
                            /*case R.id.fromtimeSlotMonVTxt:
                                if (GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotFriVTxt).replace(":", "")) != 0 && GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotFriVTxt).replace(":", "")) < GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_FROM_DATE_RESTRICT"));
                                    isSetTime = false;
                                }
                                break;*/
                            case R.id.totimeSlotFriVTxt:
                                /*if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotMonVTxt).replace(":", "")) > GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TO_DATE_RESTRICT"));
                                    isSetTime = false;
                                } else {*/
                                fromtimeSlotTwoMonVTxt.setText("00:00");
                                totimeSlotTwoFriVTxt.setText("00:00");
                                /*}*/
                                break;
                            case R.id.fromtimeSlotTwoMonVTxt:
                                /*if (GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotTwoFriVTxt).replace(":", "")) != 0 && GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotTwoFriVTxt).replace(":", "")) < GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_FROM_DATE_RESTRICT"));
                                    isSetTime = false;
                                } else */
                                if (GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", "")) < GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotFriVTxt).replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SLT_2_FRM_RESTRICT"));
                                    isSetTime = false;
                                }
                                break;
                            /*case R.id.totimeSlotTwoFriVTxt:
                                if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotTwoMonVTxt).replace(":", "")) > GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TO_DATE_RESTRICT"));
                                    isSetTime = false;
                                }
                                break;*/
                            /*case R.id.fromtimeSlotSatVTxt:
                                if (GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotSunVTxt).replace(":", "")) != 0 && GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotSunVTxt).replace(":", "")) < GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_FROM_DATE_RESTRICT"));
                                    isSetTime = false;
                                }
                                break;*/
                            case R.id.totimeSlotSunVTxt:
                                /*if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotSatVTxt).replace(":", "")) > GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TO_DATE_RESTRICT"));
                                    isSetTime = false;
                                } else {*/
                                fromtimeSlotTwoSatVTxt.setText("00:00");
                                totimeSlotTwoSunVTxt.setText("00:00");
                                /*}*/
                                break;
                            case R.id.fromtimeSlotTwoSatVTxt:
                                /*if (GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotTwoSunVTxt).replace(":", "")) != 0 && GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotTwoSunVTxt).replace(":", "")) < GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_FROM_DATE_RESTRICT"));
                                    isSetTime = false;
                                } else */
                                if (GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", "")) < GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotSunVTxt).replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SLT_2_FRM_RESTRICT"));
                                    isSetTime = false;
                                }
                                break;
                            /*case R.id.totimeSlotTwoSunVTxt:
                                if (GeneralFunctions.parseIntegerValue(0, Utils.getText(fromtimeSlotTwoSatVTxt).replace(":", "")) > GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""))) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TO_DATE_RESTRICT"));
                                    isSetTime = false;
                                }
                                break;*/
                        }


                        if (isSetTime) {
                            txtView.setText(selectedTime);
                        }

                    }

                    @Override
                    public void onDateTimeCancel() {

                    }

                })
                .setDatePickerEnabled(false)
                .setTimePickerEnabled(true)
                .setPreSetTimeEnabled(Utils.checkText(txtView.toString()) && !txtView.getText().toString().equalsIgnoreCase("00:00") ? true : false)
                .setPreSelectedTime(Utils.checkText(txtView.toString()) && !txtView.getText().toString().equalsIgnoreCase("00:00") ? txtView.getText().toString() : "")
                .setInitialDate(new Date())
                .setMaxDate(new Date())
                .setIs24HourTime(false)
                .setIndicatorColor(getResources().getColor(R.color.appThemeColor_2))
                .build()
                .show();
    }

    @Override
    public void itemTimeSlotLocClick(int position) {

    }

    // slot one mon-fri


    public void onClickView(View view) {
        int i = view.getId();

        if (i == submitBtn.getId()) {
            checkData();
        } else if (i == R.id.backImgView) {
            getActivity().onBackPressed();
        } else if (i == R.id.slotMonCalenderArea) {
            selectTimeSlot(fromtimeSlotMonVTxt);
        } else if (i == R.id.slotFriCalenderArea) {
            selectTimeSlot(totimeSlotFriVTxt);
        } else if (i == R.id.slotTwoMonCalenderArea) {
            int slotToTime = GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotFriVTxt).replace(":", ""));
            if (slotToTime > 0) {
                selectTimeSlot(fromtimeSlotTwoMonVTxt);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SELECT_MON_FRI_SLT_1"));
            }
        } else if (i == R.id.slotTwoFriCalenderArea) {
            int slotToTime = GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotFriVTxt).replace(":", ""));
            if (slotToTime > 0) {
                selectTimeSlot(totimeSlotTwoFriVTxt);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SELECT_MON_FRI_SLT_1"));
            }
        } else if (i == R.id.slotSatCalenderArea) {
            selectTimeSlot(fromtimeSlotSatVTxt);
        } else if (i == R.id.slotSunCalenderArea) {
            selectTimeSlot(totimeSlotSunVTxt);
        } else if (i == R.id.slotTwoSatCalenderArea) {
            int slotToTime = GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotSunVTxt).replace(":", ""));
            if (slotToTime > 0) {
                selectTimeSlot(fromtimeSlotTwoSatVTxt);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SELECT_SAT_SUN_SLT_1"));
            }
        } else if (i == R.id.slotTwoSunCalenderArea) {
            int slotToTime = GeneralFunctions.parseIntegerValue(0, Utils.getText(totimeSlotSunVTxt).replace(":", ""));
            if (slotToTime > 0) {
                selectTimeSlot(totimeSlotTwoSunVTxt);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SELECT_SAT_SUN_SLT_1"));
            }
        }
    }


}
