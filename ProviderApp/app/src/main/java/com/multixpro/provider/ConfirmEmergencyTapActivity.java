package com.multixpro.provider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import java.util.HashMap;

public class ConfirmEmergencyTapActivity extends ParentActivity {

    MTextView titleTxt;
    ImageView backImgView;
    String iTripId;
    ImageView Arrow1, Arrow2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_emergency_tap);


        Arrow1 = (ImageView) findViewById(R.id.Arrow1);
        Arrow2 = (ImageView) findViewById(R.id.Arrow2);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        iTripId = getIntent().getStringExtra("TripId");

        setLabels();


        addToClickHandler(backImgView);
        addToClickHandler((findViewById(R.id.policeContactArea)));
        addToClickHandler((findViewById(R.id.emeContactArea)));


        if (generalFunc.isRTLmode()) {
            Arrow1.setRotation(180);
            Arrow2.setRotation(180);
        }
        //Set Configure For Emergency Option
        /*if (generalFunc.retrieveValue("HIDE_EMERGENCY_CONTACT").equalsIgnoreCase("Yes")) {
            ((findViewById(R.id.emeContactArea))).setVisibility(View.GONE);
        }*/
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EMERGENCY_CONTACT"));
        ((MTextView) findViewById(R.id.pageTitle)).setText(generalFunc.retrieveLangLBl("USE IN CASE OF EMERGENCY", "LBL_CONFIRM_EME_PAGE_TITLE"));
        ((MTextView) findViewById(R.id.callPoliceTxt)).setText(generalFunc.retrieveLangLBl("Call Police Control Room", "LBL_CALL_POLICE"));
        ((MTextView) findViewById(R.id.sendAlertTxt)).setText(generalFunc.retrieveLangLBl("Send message to your emergency contacts.",
                "LBL_SEND_ALERT_EME_CONTACT"));
    }

    public void sendAlertToEmeContacts() {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "sendAlertToEmergencyContacts");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iTripId", iTripId);
        parameters.put("UserType", Utils.userType);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {

                    if (responseString != null && !responseString.equals("")) {

                        String message_str = generalFunc.getJsonValue(Utils.message_str, responseString);

                        if (generalFunc.checkDataAvail(Utils.action_str, responseString) == true) {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", message_str));
                        } else {
                            if (generalFunc.getJsonValue(Utils.message_str_one, responseString).equalsIgnoreCase("SmsError")) {
                                generalFunc.showGeneralMessage("", message_str);
                            } else {
                                final GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                                generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", message_str));

                                generateAlertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                    @Override
                                    public void handleBtnClick(int btn_id) {
                                        new ActUtils(getActContext()).startAct(EmergencyContactActivity.class);
                                        generateAlertBox.closeAlertBox();

                                    }
                                });
                                generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));

                                generateAlertBox.showAlertBox();
                            }

                        }
                    } else {
                        generalFunc.showError();
                    }
                });
    }

    public Context getActContext() {
        return ConfirmEmergencyTapActivity.this;
    }


    public void onClick(View view) {
        int i = view.getId();
        Utils.hideKeyboard(ConfirmEmergencyTapActivity.this);
        if (i == R.id.backImgView) {
            ConfirmEmergencyTapActivity.super.onBackPressed();

        } else if (i == R.id.policeContactArea) {

            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + generalFunc.getJsonValueStr("SITE_POLICE_CONTROL_NUMBER", obj_userProfile)));
                startActivity(callIntent);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else if (i == R.id.emeContactArea) {
            sendAlertToEmeContacts();
        }
    }


}
