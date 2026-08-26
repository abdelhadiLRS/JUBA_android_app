package com.multixpro.store;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.activity.ParentActivity;
import com.adapter.files.ViewPagerAdapter;
import com.datepicker.files.SlideDateTimeListener;
import com.datepicker.files.SlideDateTimePicker;
import com.fragments.Slot1Fragment;
import com.fragments.Slot2Fragment;
import com.general.files.GeneralFunctions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Admin on 24-05-18.
 */

public class SetWorkingHoursActivity extends ParentActivity {


    ImageView backImgView;
    MTextView titleTxt;


    // Old Slots Element Declaration
    RelativeLayout oldTimeSlotsArea;
    MButton submitBtn;
    MTextView fromtimeSlotMonVTxt;
    MTextView totimeSlotFriVTxt;
    MTextView fromtimeSlotTwoMonVTxt;
    MTextView totimeSlotTwoFriVTxt;
    MTextView fromtimeSlotSatVTxt;
    MTextView totimeSlotSunVTxt;
    MTextView fromtimeSlotTwoSatVTxt;
    MTextView totimeSlotTwoSunVTxt;

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

    String required_str = "";
    String error_email_str = "";

    String iCompanyId = "";

    View loadingBar;
    View containerView;

    // New Slots Area
    LinearLayout newTimeSlotsArea;
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    ViewPager appLogin_view_pager;
    CharSequence[] titles;
    Slot1Fragment slot1Frag;
    Slot2Fragment slot2Frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_working_hour);


        titleTxt = findViewById(R.id.titleTxt);
        backImgView = findViewById(R.id.backImgView);
        oldTimeSlotsArea = findViewById(R.id.oldTimeSlotsArea);
        newTimeSlotsArea = findViewById(R.id.newTimeSlotsArea);
        boolean isNewTimeSlotsEnabled = generalFunc.retrieveValue("ENABLE_TIMESLOT_ADDON").equalsIgnoreCase("Yes");
        if (isNewTimeSlotsEnabled) {
            oldTimeSlotsArea.setVisibility(View.GONE);
            newTimeSlotsArea.setVisibility(View.VISIBLE);
            initNewTimeSlotElements();
        } else {
            oldTimeSlotsArea.setVisibility(View.VISIBLE);
            newTimeSlotsArea.setVisibility(View.GONE);
            initOldTimeSlotElements();
        }
        addToClickHandler(backImgView);
        setData(isNewTimeSlotsEnabled);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
    }

    private void initNewTimeSlotElements() {
        appLogin_view_pager = (ViewPager) findViewById(R.id.appLogin_view_pager);
        TabLayout material_tabs = (TabLayout) findViewById(R.id.material_tabs);
        LinearLayout tabArea = (LinearLayout) findViewById(R.id.tabArea);

        titles = new CharSequence[]{generalFunc.retrieveLangLBl("Slot 1", "LBL_SLOT_1"), generalFunc.retrieveLangLBl("Slot 2", "LBL_SLOT_2")};
        fragmentList.add(generateSlot1Frag("Slot1"));
        fragmentList.add(generateSlot2Frag("Slot2"));

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, fragmentList);
        appLogin_view_pager.setAdapter(adapter);
        material_tabs.setupWithViewPager(appLogin_view_pager);
    }

    private void initOldTimeSlotElements() {
        iCompanyId = generalFunc.getJsonValueStr("iCompanyId", obj_userProfile);

        submitBtn = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        monfriSlotOneTxtView = (MTextView) findViewById(R.id.monfriSlotOneTxtView);
        monfriSlotTwoTxtView = (MTextView) findViewById(R.id.monfriSlotTwoTxtView);
        satSunSlotOneTxtView = (MTextView) findViewById(R.id.satSunSlotOneTxtView);
        satSunSlotTwoTxtView = (MTextView) findViewById(R.id.satSunSlotTwoTxtView);

        fromtimeSlotMonVTxt = (MTextView) findViewById(R.id.fromtimeSlotMonVTxt);
        totimeSlotFriVTxt = (MTextView) findViewById(R.id.totimeSlotFriVTxt);
        fromtimeSlotTwoMonVTxt = (MTextView) findViewById(R.id.fromtimeSlotTwoMonVTxt);
        totimeSlotTwoFriVTxt = (MTextView) findViewById(R.id.totimeSlotTwoFriVTxt);
        fromtimeSlotSatVTxt = (MTextView) findViewById(R.id.fromtimeSlotSatVTxt);
        totimeSlotSunVTxt = (MTextView) findViewById(R.id.totimeSlotSunVTxt);
        fromtimeSlotTwoSatVTxt = (MTextView) findViewById(R.id.fromtimeSlotTwoSatVTxt);
        totimeSlotTwoSunVTxt = (MTextView) findViewById(R.id.totimeSlotTwoSunVTxt);

        loadingBar = findViewById(R.id.loadingBar);
        containerView = findViewById(R.id.containerView);


        slotMonCalenderArea = findViewById(R.id.slotMonCalenderArea);
        addToClickHandler(slotMonCalenderArea);

        slotFriCalenderArea = findViewById(R.id.slotFriCalenderArea);
        addToClickHandler(slotFriCalenderArea);


        slotTwoMonCalenderArea = findViewById(R.id.slotTwoMonCalenderArea);
        addToClickHandler(slotTwoMonCalenderArea);

        slotTwoFriCalenderArea = findViewById(R.id.slotTwoFriCalenderArea);
        addToClickHandler(slotTwoFriCalenderArea);


        slotSatCalenderArea = findViewById(R.id.slotSatCalenderArea);
        addToClickHandler(slotSatCalenderArea);

        slotSunCalenderArea = findViewById(R.id.slotSunCalenderArea);
        addToClickHandler(slotSunCalenderArea);

        slotTwoSatCalenderArea = findViewById(R.id.slotTwoSatCalenderArea);
        addToClickHandler(slotTwoSatCalenderArea);

        slotTwoSunCalenderArea = findViewById(R.id.slotTwoSunCalenderArea);
        addToClickHandler(slotTwoSunCalenderArea);


        submitBtn.setId(Utils.generateViewId());
        addToClickHandler(submitBtn);
    }

    private Fragment generateSlot1Frag(String slot1) {
        slot1Frag = new Slot1Fragment();
        Bundle bn = new Bundle();
        bn.putString("SLOT_TYPE", "UpdateCompanyTiming");
        bn.putString("slot", slot1);
        slot1Frag.setArguments(bn);
        return slot1Frag;
    }

    private Fragment generateSlot2Frag(String slot2) {
        slot2Frag = new Slot2Fragment();
        Bundle bn = new Bundle();
        bn.putString("SLOT_TYPE", "UpdateCompanyTiming");
        bn.putString("slot", slot2);
        slot2Frag.setArguments(bn);

        return slot2Frag;
    }


    public Context getActContext() {
        return SetWorkingHoursActivity.this;
    }

    public void checkData() {

        if (Utils.getText(fromtimeSlotMonVTxt).equalsIgnoreCase("00:00") || Utils.getText(totimeSlotFriVTxt).equalsIgnoreCase("00:00")) {
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
        }

        /*generalFunc.retrieveLangLBl("","LB_SET_TIMING")*/

        sendTimeData("Update");
    }

    public void sendTimeData(String callType) {

        if (callType.equalsIgnoreCase("Display")) {
            containerView.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateCompanyTiming");
        parameters.put("iCompanyId", iCompanyId);
        parameters.put("vFromMonFriTimeSlot1", Utils.getText(fromtimeSlotMonVTxt));
        parameters.put("vToMonFriTimeSlot1", Utils.getText(totimeSlotFriVTxt));
        parameters.put("vFromMonFriTimeSlot2", Utils.getText(fromtimeSlotTwoMonVTxt));
        parameters.put("vToMonFriTimeSlot2", Utils.getText(totimeSlotTwoFriVTxt));
        parameters.put("vFromSatSunTimeSlot1", Utils.getText(fromtimeSlotSatVTxt));
        parameters.put("vToSatSunTimeSlot1", Utils.getText(totimeSlotSunVTxt));
        parameters.put("vFromSatSunTimeSlot2", Utils.getText(fromtimeSlotTwoSatVTxt));
        parameters.put("vToSatSunTimeSlot2", Utils.getText(totimeSlotTwoSunVTxt));
        parameters.put("CALL_TYPE", callType);


        ApiHandler.execute(getActContext(), parameters, callType.equalsIgnoreCase("Display") ? false : true, false, generalFunc,
                responseString -> {
                    loadingBar.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.VISIBLE);
                    if (responseString != null && !responseString.equals("")) {

                        if (generalFunc.checkDataAvail(Utils.action_str, responseString) == true) {

                            if (callType.equalsIgnoreCase("Display")) {

                                JSONObject msg_obj = generalFunc.getJsonObject(Utils.message_str, responseString);

                                String vFromMonFriTimeSlot1 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vFromMonFriTimeSlot1", msg_obj));
                                String vToMonFriTimeSlot1 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vToMonFriTimeSlot1", msg_obj));
                                String vFromMonFriTimeSlot2 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vFromMonFriTimeSlot2", msg_obj));
                                String vToMonFriTimeSlot2 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vToMonFriTimeSlot2", msg_obj));
                                String vFromSatSunTimeSlot1 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vFromSatSunTimeSlot1", msg_obj));
                                String vToSatSunTimeSlot1 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vToSatSunTimeSlot1", msg_obj));
                                String vFromSatSunTimeSlot2 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vFromSatSunTimeSlot2", msg_obj));
                                String vToSatSunTimeSlot2 = Utils.formatDate("HH:mm:ss", "HH:mm", generalFunc.getJsonValueStr("vToSatSunTimeSlot2", msg_obj));

                                fromtimeSlotMonVTxt.setText(generalFunc.convertNumberWithRTL(vFromMonFriTimeSlot1));
                                totimeSlotFriVTxt.setText(generalFunc.convertNumberWithRTL(vToMonFriTimeSlot1));
                                fromtimeSlotTwoMonVTxt.setText(generalFunc.convertNumberWithRTL(vFromMonFriTimeSlot2));
                                totimeSlotTwoFriVTxt.setText(generalFunc.convertNumberWithRTL(vToMonFriTimeSlot2));
                                fromtimeSlotSatVTxt.setText(generalFunc.convertNumberWithRTL(vFromSatSunTimeSlot1));
                                totimeSlotSunVTxt.setText(generalFunc.convertNumberWithRTL(vToSatSunTimeSlot1));
                                fromtimeSlotTwoSatVTxt.setText(generalFunc.convertNumberWithRTL(vFromSatSunTimeSlot2));
                                totimeSlotTwoSunVTxt.setText(generalFunc.convertNumberWithRTL(vToSatSunTimeSlot2));

//                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), false);
                            } else {

                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), true);
//                        generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
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

        new SlideDateTimePicker.Builder(getSupportFragmentManager())
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
//                                fromtimeSlotTwoMonVTxt.setText("00:00");
//                                totimeSlotTwoFriVTxt.setText("00:00");
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
//                                fromtimeSlotTwoSatVTxt.setText("00:00");
//                                totimeSlotTwoSunVTxt.setText("00:00");
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

    // slot one mon-fri


    public void onClick(View view) {
        int i = view.getId();

        if (i == R.id.backImgView) {
            SetWorkingHoursActivity.this.onBackPressed();
        } else if (i == submitBtn.getId()) {
            checkData();
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


    public void setData(boolean isNewTimeSlotsEnabled) {
        titleTxt.setText(generalFunc.retrieveLangLBl("SET TIMINGS", "LBL_SET_TIMING"));

        if (!isNewTimeSlotsEnabled) {
            // old timeslot's elements title set
            sendTimeData("Display");
            submitBtn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT")); //LBL_BTN_SUBMIT_TXT

            monfriSlotOneTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_MON_TO_FRI_SLOT1"));
            monfriSlotTwoTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_MON_TO_FRI_SLOT2"));
            satSunSlotOneTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_SAT_AND_SUN_SLOT1"));
            satSunSlotTwoTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_SAT_AND_SUN_SLOT2"));


            required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
            error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
        }

    }


}
