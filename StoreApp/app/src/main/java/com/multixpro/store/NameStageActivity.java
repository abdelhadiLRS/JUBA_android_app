package com.multixpro.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.utils.Utils;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;

public class NameStageActivity extends ParentActivity {

    MaterialEditText companyNameBox;

    MTextView titleTxt;
    ImageView backBtn, nextBtn;
    String required_str = "";
    View contentArea;
    static final int NEXT_STAGE = 002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_stage);
        init();
    }

    private void init() {

        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        titleTxt = findViewById(R.id.titleTxt);
        companyNameBox = (MaterialEditText) findViewById(R.id.companyNameBox);
        contentArea = findViewById(R.id.contentArea);
        companyNameBox.setInputType(InputType.TYPE_CLASS_TEXT);
        companyNameBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_STORE_NAME"));
        titleTxt.setText(generalFunc.retrieveLangLBl("what's your Store name ?", "LBL_WHATS_STORE_NAME"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        addToClickHandler(backBtn);
        addToClickHandler(nextBtn);
        if (generalFunc.isRTLmode()) {
            nextBtn.setRotation(180);
            backBtn.setRotation(180);
        }
        manageAnimation(contentArea);

    }

    @Override
    public void onBackPressed() {
        new ActUtils(getActContext()).setOkResult();
        super.onBackPressed();
    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == backBtn.getId()) {
            onBackPressed();
        } else if (i == nextBtn.getId()) {
            boolean cNameEntered = Utils.checkText(companyNameBox) ? true : Utils.setErrorFields(companyNameBox, required_str);


            if (cNameEntered == false) {
                return;
            }


            if (isServiceList()) {
                Bundle bn = new Bundle();
                bn.putString("mob", "+" + getIntent().getStringExtra("mob"));
                bn.putString("vPhoneCode", getIntent().getStringExtra("vPhoneCode"));
                bn.putString("vmobile", getIntent().getStringExtra("vmobile"));
                bn.putString("vCompany", Utils.getText(companyNameBox));
                bn.putString("vPassword", getIntent().getStringExtra("vPassword"));
                bn.putString("vCountryCode", getIntent().getStringExtra("vCountryCode"));
                new ActUtils(getActContext()).startActForResult(SelectServiceStageActivity.class, bn, NEXT_STAGE);

            } else {
                Bundle bn = new Bundle();
                bn.putString("mob", "+" + getIntent().getStringExtra("mob"));
                bn.putString("vPhoneCode", getIntent().getStringExtra("vPhoneCode"));
                bn.putString("vmobile", getIntent().getStringExtra("vmobile"));
                bn.putString("vCompany", Utils.getText(companyNameBox));
                bn.putString("vPassword", getIntent().getStringExtra("vPassword"));
                bn.putString("vCountryCode", getIntent().getStringExtra("vCountryCode"));

                String iServiceId = generalFunc.getJsonValueStr("iServiceId", generalFunc.getJsonObject(
                        generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.serviceCategoriesKey)),
                        0));
                bn.putString("iServiceId", iServiceId);
                new ActUtils(getActContext()).startActForResult(EmailStageActivity.class, bn, NEXT_STAGE);
            }
        }
    }


    public boolean isServiceList() {
        String serviceDataStr = generalFunc.retrieveValue(Utils.serviceCategoriesKey);
        JSONArray serviceDataArr = generalFunc.getJsonArray(serviceDataStr);

        if (serviceDataArr != null) {

            if (serviceDataArr.length() == 1) {
                return false;
            } else {
                return true;
            }


        }

        return false;
    }

    private Context getActContext() {
        return NameStageActivity.this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        manageAnimation(contentArea);
    }
}