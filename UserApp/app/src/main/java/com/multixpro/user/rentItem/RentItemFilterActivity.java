package com.multixpro.user.rentItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RentItemFilterActivity extends ParentActivity {

    private ProgressBar loadingBar;
    private View contentView;
    private String eType, iCategoryId, optionId = "", subcategoryId = "";
    private LinearLayout serviceSelectHArea;
    private final List<AppCompatCheckBox> mList = new ArrayList<>();
    private final List<String> mListSubcategory = new ArrayList<>(), mListOption = new ArrayList<>();
    MButton resetTxt,applyTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item_filter);

        iCategoryId = getIntent().getStringExtra("iCategoryId");
        eType = getIntent().getStringExtra("eType");
        subcategoryId = getIntent().getStringExtra("subcategoryId");
        optionId = getIntent().getStringExtra("optionId");

        MTextView titleTxt = findViewById(R.id.titleTxt);
        ImageView backBtn = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backBtn.setRotation(180);
        }

        loadingBar = findViewById(R.id.loadingBar);
        contentView = findViewById(R.id.contentView);
        serviceSelectHArea = findViewById(R.id.serviceSelectHArea);
        resetTxt = ((MaterialRippleLayout) findViewById(R.id.resetTxt)).getChildView();
        applyTxt = ((MaterialRippleLayout) findViewById(R.id.applyTxt)).getChildView();

        getSubCategoryList();
        addToClickHandler(backBtn);
        resetTxt.setId(Utils.generateViewId());
        applyTxt.setId(Utils.generateViewId());
        addToClickHandler(resetTxt);
        addToClickHandler(applyTxt);

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_FILTER"));
        resetTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_RESET"));
        applyTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_APPLY"));
    }

    private Context getActContext() {
        return RentItemFilterActivity.this;
    }


    private void getSubCategoryList() {

        loadingBar.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getFilterRentData");
        parameters.put("userId", generalFunc.getMemberId());
        parameters.put("iCategoryId", iCategoryId);
        parameters.put("eType", eType);

        ApiHandler.execute(getActContext(), parameters, responseString -> {

            loadingBar.setVisibility(View.GONE);
            JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

            if (responseStringObject != null && !responseStringObject.toString().equals("")) {


                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject)) {
                    mListSubcategory.clear();
                    mListOption.clear();

                    JSONArray carList_arr = generalFunc.getJsonArray(Utils.message_str, responseStringObject);
                    if (carList_arr != null) {
                        contentView.setVisibility(View.VISIBLE);

                        if (serviceSelectHArea.getChildCount() > 0) {
                            serviceSelectHArea.removeAllViewsInLayout();
                        }

                        for (int i = 0; i < carList_arr.length(); i++) {
                            buildServices(generalFunc.getJsonObject(carList_arr, i));
                        }
                    }

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void buildServices(JSONObject obj) {
        if (obj == null) {
            return;
        }
        final LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_rent_item_filter_header, null);

        MTextView rentItemSubCategoryHTxt = view.findViewById(R.id.rentItemSubCategoryHTxt);
        rentItemSubCategoryHTxt.setText(generalFunc.getJsonValueStr("headerTitle", obj));

        LinearLayout serviceSelectArea = view.findViewById(R.id.serviceSelectArea);

        JSONArray mCategory = generalFunc.getJsonArray("subData", obj);
        if (mCategory == null || mCategory.length() == 0) {
            return;
        }
        for (int j = 0; j < mCategory.length(); j++) {
            categoryServices(generalFunc.getJsonObject(mCategory, j), serviceSelectArea);
        }
        serviceSelectHArea.addView(view);
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    private void categoryServices(JSONObject jsonObject, LinearLayout serviceSelectArea) {
        final LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_rent_item_filter_service, null);

        MTextView filterServiceNameTxt = view.findViewById(R.id.filterServiceNameTxt);
        filterServiceNameTxt.setText(generalFunc.getJsonValueStr("vTitle", jsonObject));

        AppCompatCheckBox chkBox = view.findViewById(R.id.filterServiceCheckbox);

        filterServiceNameTxt.setOnClickListener(v -> chkBox.performClick());

        if (jsonObject.has("iSubCategoryId")) {
            chkBox.setId(Integer.parseInt(generalFunc.getJsonValueStr("iSubCategoryId", jsonObject)));
        } else {
            chkBox.setId(Integer.parseInt(generalFunc.getJsonValueStr("iOptionId", jsonObject)));
        }
        mList.add(chkBox);

        if (jsonObject.has("iSubCategoryId")) {
            List<String> list = Arrays.asList(subcategoryId.split(","));
            if (list.contains(generalFunc.getJsonValueStr("iSubCategoryId", jsonObject))) {
                chkBox.setChecked(true);
                chkBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.appThemeColor_1));
                mListSubcategory.add(generalFunc.getJsonValueStr("iSubCategoryId", jsonObject));
            }
        } else {
            List<String> list2 = Arrays.asList(optionId.split(","));
            if (list2.contains(generalFunc.getJsonValueStr("iOptionId", jsonObject))) {
                chkBox.setChecked(true);
                chkBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.appThemeColor_1));
                mListOption.add(generalFunc.getJsonValueStr("iOptionId", jsonObject));
            }
        }

        chkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                chkBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.appThemeColor_1));
                if (jsonObject.has("iSubCategoryId")) {
                    mListSubcategory.add(String.valueOf(chkBox.getId()));
                } else {
                    mListOption.add(String.valueOf(chkBox.getId()));
                }
            } else {
                chkBox.setButtonTintList(ContextCompat.getColorStateList(this, R.color.text23Pro_Light));
                if (jsonObject.has("iSubCategoryId")) {
                    mListSubcategory.remove(String.valueOf(chkBox.getId()));
                } else {
                    mListOption.remove(String.valueOf(chkBox.getId()));
                }
            }
        });
        serviceSelectArea.addView(view);
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();

        } else if (i == applyTxt.getId()) {

            Bundle bn = new Bundle();
            bn.putString("iCategoryId", iCategoryId);
            bn.putString("eType", eType);
            bn.putString("subcategoryId", TextUtils.join(",", mListSubcategory));
            bn.putString("optionId", TextUtils.join(",", mListOption));
            new ActUtils(getActContext()).setOkResult(bn);
            finish();

        } else if (i == resetTxt.getId()) {
            for (int j = 0; j < mList.size(); j++) {
                mList.get(j).setChecked(false);
                subcategoryId = "";
                optionId = "";
                mListSubcategory.clear();
                mListOption.clear();
            }
        }
    }
}