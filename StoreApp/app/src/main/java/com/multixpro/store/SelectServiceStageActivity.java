package com.multixpro.store;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.activity.ParentActivity;
import com.dialogs.OpenListView;
import com.general.files.ActUtils;
import com.general.files.SetOnTouchList;
import com.utils.Utils;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectServiceStageActivity extends ParentActivity {

    MaterialEditText serviceTypeEditBox;

    MTextView titleTxt;
    ImageView backBtn, nextBtn;
    String required_str = "";
    View contentArea;
    static final int NEXT_STAGE = 002;
    int iServiceTypePosition = -1;
    String iSelectedServiceId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service_stage);
        init();
    }

    private void init() {

        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        titleTxt = findViewById(R.id.titleTxt);

        contentArea = findViewById(R.id.contentArea);
        serviceTypeEditBox = (MaterialEditText) findViewById(R.id.serviceTypeEditBox);
        serviceTypeEditBox.setOnTouchListener(new SetOnTouchList());

        addToClickHandler(serviceTypeEditBox);

        titleTxt.setText(generalFunc.retrieveLangLBl("what's your Store name ?", "LBL_STORE_TYPE"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        addToClickHandler(backBtn);
        addToClickHandler(nextBtn);
        if (generalFunc.isRTLmode()) {
            nextBtn.setRotation(180);
            backBtn.setRotation(180);
        }
        manageAnimation(contentArea);
        serviceTypeEditBox.setBothText(generalFunc.retrieveLangLBl("Service Type", "LBL_SERVICES_TYPE"), generalFunc.retrieveLangLBl("", "LBL_SELECT_SERVICE_TYPE"));

    }

    private Context getActContext() {
        return SelectServiceStageActivity.this;
    }

    public ArrayList<HashMap<String, String>> configServiceData() {
        String serviceDataStr = generalFunc.retrieveValue(Utils.serviceCategoriesKey);
        JSONArray serviceDataArr = generalFunc.getJsonArray(serviceDataStr);

        ArrayList<HashMap<String, String>> serviceDataList = new ArrayList<>();
        if (serviceDataArr != null) {

            boolean setInitialValue = false;
            if (serviceDataArr.length() == 1) {

                setInitialValue = true;
            }

            for (int i = 0; i < serviceDataArr.length(); i++) {
                JSONObject obj_tmp = generalFunc.getJsonObject(serviceDataArr, i);

                HashMap<String, String> mapData = new HashMap<>();
                mapData.put("vServiceName", generalFunc.getJsonValueStr("vServiceName", obj_tmp));
                mapData.put("iServiceId", generalFunc.getJsonValueStr("iServiceId", obj_tmp));

                serviceDataList.add(mapData);

                if (setInitialValue) {
                    iSelectedServiceId = generalFunc.getJsonValueStr("iServiceId", obj_tmp);
                }
            }
        }

        return serviceDataList;
    }

    public void buildServiceData() {


        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_SERVICE_TYPE"), configServiceData(), OpenListView.OpenDirection.CENTER, true, position -> {

            iServiceTypePosition = position;

            HashMap<String, String> mapData = configServiceData().get(position);

            iSelectedServiceId = mapData.get("iServiceId");

            serviceTypeEditBox.setText(mapData.get("vServiceName"));

        }).show(iServiceTypePosition, "vServiceName");

    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == backBtn.getId()) {
            onBackPressed();
        } else if (i == nextBtn.getId()) {
            boolean cNameEntered = Utils.checkText(serviceTypeEditBox) || Utils.setErrorFields(serviceTypeEditBox, required_str);
            if (!cNameEntered) {
                return;
            }
            Bundle bn = new Bundle();
            bn.putString("mob", "+" + getIntent().getStringExtra("mob"));
            bn.putString("vPhoneCode", getIntent().getStringExtra("vPhoneCode"));
            bn.putString("vmobile", getIntent().getStringExtra("vmobile"));
            bn.putString("vCompany", getIntent().getStringExtra("vCompany"));
            bn.putString("vPassword", getIntent().getStringExtra("vPassword"));
            bn.putString("iServiceId", iSelectedServiceId);
            bn.putString("vCountryCode", getIntent().getStringExtra("vCountryCode"));
            new ActUtils(getActContext()).startActForResult(EmailStageActivity.class, bn, NEXT_STAGE);
        } else if (i == R.id.serviceTypeEditBox) {
            buildServiceData();
        }

    }


}