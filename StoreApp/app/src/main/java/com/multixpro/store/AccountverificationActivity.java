package com.multixpro.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.activity.ParentActivity;
import com.countryview.view.CountryPicker;

import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.OpenMainProfile;
import com.general.files.SetOnTouchList;
import com.general.files.ConfigureMemberData;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;

public class AccountverificationActivity extends ParentActivity {


    static MaterialEditText countryBox;

    LinearLayout emailarea, mobileNoArea;
    MaterialEditText emailBox;
    MaterialEditText mobileBox;
    String vCountryCode = "";
    String vPhoneCode = "";
    boolean isCountrySelected = false;
    String required_str = "";
    String error_email_str = "";
    MTextView accountverifyHint;
    MButton btn_type2;
    int submitBtnId;
    MTextView titleTxt;
    ImageView backImgView, logoutImageview;

    ImageView imageView2, imageView1;

    CountryPicker countryPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountverification);

        initView();
        setLabel();
        removeInput();
    }

    public Context getActContext() {
        return AccountverificationActivity.this;
    }

    private void initView() {

        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        countryBox = (MaterialEditText) findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) findViewById(R.id.mobileBox);
        emailarea = (LinearLayout) findViewById(R.id.emailarea);
        mobileNoArea = (LinearLayout) findViewById(R.id.mobileNoArea);
        accountverifyHint = (MTextView) findViewById(R.id.accountverifyHint);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        logoutImageview = (ImageView) findViewById(R.id.logoutImageview);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        logoutImageview.setVisibility(View.VISIBLE);
        addToClickHandler(logoutImageview);
        addToClickHandler(logoutImageview);
        backImgView.setVisibility(View.GONE);

        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.DefaultCountryCode, "");
        data.put(Utils.DefaultPhoneCode, "");
        data = generalFunc.retrieveValue(data);


        vCountryCode = data.get(Utils.DefaultCountryCode);
        vPhoneCode = data.get(Utils.DefaultPhoneCode);

        if (!vPhoneCode.equalsIgnoreCase("")) {
            countryBox.setText("+" + vPhoneCode);
            isCountrySelected = true;
        }

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        addToClickHandler(btn_type2);


        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        String vPhone = generalFunc.getJsonValueStr("vPhone", obj_userProfile);
        if (vPhone.equals("")) {
            mobileNoArea.setVisibility(View.VISIBLE);
        } else {
            mobileBox.setText(vPhone);
            mobileNoArea.setVisibility(View.GONE);

        }
        String vEmail = generalFunc.getJsonValueStr("vEmail", obj_userProfile);

        if (vEmail.equals("")) {
            emailarea.setVisibility(View.VISIBLE);
        } else {
            emailBox.setText(vEmail);
            emailarea.setVisibility(View.GONE);
        }


        countryBox.setShowClearButton(false);

    }

    public void removeInput() {
        Utils.removeInput(countryBox);

        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
            countryBox.setOnTouchListener(new SetOnTouchList());

            addToClickHandler(countryBox);
        }
    }

    private void setLabel() {

        emailBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        countryBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));
        mobileBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
        accountverifyHint.setText(generalFunc.retrieveLangLBl("", "LBL_ACC_SUB_INFO"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ARRIVED_DIALOG_BTN_CONTINUE_TXT"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ACC_INFO"));


        emailBox.getLabelFocusAnimator().start();
        countryBox.getLabelFocusAnimator().start();
        mobileBox.getLabelFocusAnimator().start();

    }

    public void checkValues() {

        boolean isEmailBlankAndOptional = generalFunc.isEmailBlankAndOptional(generalFunc, Utils.getText(emailBox));
        boolean emailEntered = isEmailBlankAndOptional ? true : (Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, error_email_str))
                : Utils.setErrorFields(emailBox, required_str));
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, required_str);
        boolean countryEntered = isCountrySelected ? true : Utils.setErrorFields(countryBox, required_str);

        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            if (countryEntered == false) {
                imageView2.setVisibility(View.GONE);
                imageView1.setVisibility(View.VISIBLE);
            } else {
                imageView2.setVisibility(View.VISIBLE);
                imageView1.setVisibility(View.GONE);
            }
        }

        if (mobileNoArea.getVisibility() == View.GONE) {
            mobileEntered = true;
            countryEntered = true;
        }
        if (emailarea.getVisibility() == View.GONE) {
            emailEntered = true;
        }

        if (mobileEntered) {
            mobileEntered = mobileBox.length() >= 3 ? true : Utils.setErrorFields(mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }

        if (emailEntered == false || mobileEntered == false
                || countryEntered == false) {
            return;
        }


        updateProfile();
    }

    public void updateProfile() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateUserProfileDetail");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("vName", generalFunc.getJsonValueStr("vCompany", obj_userProfile));
        parameters.put("vLastName", generalFunc.getJsonValueStr("vLastName", obj_userProfile));
        parameters.put("vPhone", Utils.getText(mobileBox));
        parameters.put("vPhoneCode", vPhoneCode);
        parameters.put("vCountry", vCountryCode);
        parameters.put("vEmail", Utils.getText(emailBox));

        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.DEFAULT_CURRENCY_VALUE, "");
        data.put(Utils.LANGUAGE_CODE_KEY, "");
        data = generalFunc.retrieveValue(data);

        parameters.put("CurrencyCode", data.get(Utils.DEFAULT_CURRENCY_VALUE));
        parameters.put("LanguageCode", data.get(Utils.LANGUAGE_CODE_KEY));
        parameters.put("UserType", Utils.app_type);



        ApiHandler.execute(getActContext(), parameters,true,false,generalFunc,
              responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                        if (isDataAvail == true) {

                            new ConfigureMemberData(responseString, generalFunc, getActContext(), true);

                            generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValueStr(Utils.message_str, responseObj));
                            new OpenMainProfile(getActContext(),
                                    generalFunc.getJsonValueStr(Utils.message_str, responseObj), false, generalFunc).startProcess();

                        } else {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = data.getStringExtra("vPhoneCode");
            isCountrySelected = true;

            countryBox.setText("+" + vPhoneCode);
            imageView2.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.GONE);
        }
    }

    public void setData(String vCountryCode, String vPhoneCode, String vSImage) {
        this.vCountryCode = vCountryCode;
        this.vPhoneCode = vPhoneCode;
        isCountrySelected = true;


        GeneralFunctions generalFunctions = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
        countryBox.setText("+" + generalFunctions.convertNumberWithRTL(vPhoneCode));
    }


    public void onClick(View view) {

        int i = view.getId();
        Utils.hideKeyboard(AccountverificationActivity.this);

        if (i == R.id.countryBox) {
            //new ActUtils(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            if (countryPicker == null) {
                countryPicker = new CountryPicker.Builder(getActContext()).showingDialCode(true)
                        .setLocale(locale).showingFlag(true)
                        .enablingSearch(true)
                        //.setCountries(items_list)
                        .setCountrySelectionListener(country -> setData(country.getCode(), country.getDialCode(), country.getFlagName()))
                        .build();
            }
            countryPicker.show(getActContext());
        } else if (i == submitBtnId) {
            Utils.hideKeyboard(AccountverificationActivity.this);
            checkValues();
        } else if (i == logoutImageview.getId()) {

            MyApp.getInstance().logOutFromDevice(false);

        }

    }

}
