package com.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.countryview.view.CountryPicker;
import com.dialogs.OpenListView;
import com.general.files.ConfigureMemberData;
import com.general.files.GeneralFunctions;
import com.general.files.GetUserData;
import com.general.files.MyApp;
import com.multixpro.store.MyProfileActivity;
import com.multixpro.store.R;
import com.service.handler.ApiHandler;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends BaseFragment {

    MyProfileActivity myProfileAct;
    View view;

    GeneralFunctions generalFunc;

    JSONObject userProfileJsonObj;


    MaterialEditText profileDescriptionEditBox;
    MaterialEditText countryBox;
    MaterialEditText mobileBox;
    AVLoadingIndicatorView loaderView;
    FrameLayout langSelectArea, currencySelectArea;

    String selected_language_code = "";


    ArrayList<HashMap<String, String>> languageDataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> currencyDataList = new ArrayList<>();


    String selected_currency = "";

    MButton btn_type2;
    int submitBtnId;

    String required_str = "";
    String error_email_str = "";

    String vCountryCode = "";
    String vPhoneCode = "";
    boolean isCountrySelected = false;
    String vSImage = "";
    ImageView countryimage;
    int imagewidth;
    int imageheight;
    CountryPicker countryPicker;
    Locale locale;

    MTextView companyTextH, emailTextH, langTextH, curTextH, mobileBoxHTxt;
    MaterialEditText txtcompanytxt, txtemailtxt, txtlangtxt, txtcurtxt;
    ImageView langDropDownArrow, curDropDownArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        myProfileAct = (MyProfileActivity) getActivity();

        imagewidth = (int) myProfileAct.getResources().getDimension(R.dimen._30sdp);
        imageheight = (int) myProfileAct.getResources().getDimension(R.dimen._20sdp);

        generalFunc = myProfileAct.generalFunc;
        locale = new Locale(generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        userProfileJsonObj = myProfileAct.obj_userProfile;
        countryimage = (ImageView) view.findViewById(R.id.countryimage);

        View companyBox = view.findViewById(R.id.companyBox);
        companyTextH = companyBox.findViewById(R.id.mTextH);
        txtcompanytxt = companyBox.findViewById(R.id.mEditText);

        View emailBox = view.findViewById(R.id.emailBox);
        emailTextH = emailBox.findViewById(R.id.mTextH);
        txtemailtxt = emailBox.findViewById(R.id.mEditText);

        countryBox = (MaterialEditText) view.findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) view.findViewById(R.id.mobileBox);

        View langBox = view.findViewById(R.id.langBox);
        langTextH = langBox.findViewById(R.id.mTextH);
        txtlangtxt = langBox.findViewById(R.id.mEditText);
        langDropDownArrow = langBox.findViewById(R.id.mDropDownArrow);
        langDropDownArrow.setVisibility(View.VISIBLE);


        View currencyBox = view.findViewById(R.id.currencyBox);
        curTextH = currencyBox.findViewById(R.id.mTextH);
        txtcurtxt = currencyBox.findViewById(R.id.mEditText);
        curDropDownArrow = currencyBox.findViewById(R.id.mDropDownArrow);
        curDropDownArrow.setVisibility(View.VISIBLE);

        mobileBoxHTxt = view.findViewById(R.id.mobileBoxHTxt);

        loaderView = (AVLoadingIndicatorView) view.findViewById(R.id.loaderView);
        profileDescriptionEditBox = (MaterialEditText) view.findViewById(R.id.profileDescriptionEditBox);
        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();

        currencySelectArea = (FrameLayout) view.findViewById(R.id.currencySelectArea);
        langSelectArea = (FrameLayout) view.findViewById(R.id.langSelectArea);

        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        addToClickHandler(btn_type2);
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtemailtxt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        vSImage = generalFunc.retrieveValue(Utils.DefaultCountryImage);
        new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(getActContext(), vSImage, imagewidth, imageheight)), countryimage).build();
        int paddingValStart = (int) getResources().getDimension(R.dimen._35sdp);
        int paddingValEnd = (int) getResources().getDimension(R.dimen._12sdp);

        setLabels();

        removeInput();

        setData();

        buildLanguageList();


        myProfileAct.changePageTitle(generalFunc.retrieveLangLBl("", "LBL_EDIT_PROFILE_TXT"));


        if (myProfileAct.isEmail) {
            emailBox.requestFocus();
        }

        if (myProfileAct.isMobile) {
            mobileBox.requestFocus();
        }

//        emailBox.setVisibility(View.GONE);
        return view;
    }

    public void setLabels() {
        companyTextH.setText(generalFunc.retrieveLangLBl("", "LBL_COMPANY_NAME_SIGNUP"));
        txtcompanytxt.setHint(generalFunc.retrieveLangLBl("", "LBL_COMPANY_NAME_SIGNUP"));
        emailTextH.setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        txtemailtxt.setHint(generalFunc.retrieveLangLBl("", "LBL_EMAIL_LBL_TXT"));
        countryBox.setText(generalFunc.retrieveLangLBl("", "LBL_COUNTRY_TXT"));
        mobileBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));
        langTextH.setText(generalFunc.retrieveLangLBl("", "LBL_LANGUAGE_TXT"));
        curTextH.setText(generalFunc.retrieveLangLBl("", "LBL_CURRENCY_TXT"));
        profileDescriptionEditBox.setBothText(generalFunc.retrieveLangLBl("Service Description", "LBL_SERVICE_DESCRIPTION"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_PROFILE_UPDATE_PAGE_TXT"));


        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");

        if (generalFunc.getJsonValueStr("APP_TYPE", userProfileJsonObj).equalsIgnoreCase("UberX")) {
            profileDescriptionEditBox.setVisibility(View.VISIBLE);
        }
        mobileBoxHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HINT_TXT"));
    }

    public void removeInput() {
        Utils.removeInput(countryBox);
        Utils.removeInput(txtlangtxt);
        Utils.removeInput(txtcurtxt);

        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            view.findViewById(R.id.countrydropimage).setVisibility(View.VISIBLE);

            countryBox.setOnTouchListener(new setOnTouchList());
            addToClickHandler(countryBox);

        }

        txtlangtxt.setOnTouchListener(new setOnTouchList());
        txtcurtxt.setOnTouchListener(new setOnTouchList());

        txtlangtxt.setOnClickListener(view -> {
            //
            showLanguageList();
        });
        txtcurtxt.setOnClickListener(view -> {
            //
            showCurrencyList();
        });
    }

    public void setData() {
        txtcompanytxt.setText(generalFunc.getJsonValueStr("vCompany", userProfileJsonObj));
        txtemailtxt.setText(generalFunc.getJsonValueStr("vEmail", userProfileJsonObj));
        countryBox.setText("+" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vCode", userProfileJsonObj)));
        mobileBox.setText(generalFunc.getJsonValueStr("vPhone", userProfileJsonObj));
        txtcurtxt.setText(generalFunc.getJsonValueStr("vCurrencyCompany", userProfileJsonObj));
        profileDescriptionEditBox.setText(generalFunc.getJsonValueStr("tProfileDescription", userProfileJsonObj));

        if (generalFunc.getJsonValueStr("vSCountryImage", userProfileJsonObj) != null && !generalFunc.getJsonValueStr("vSCountryImage", userProfileJsonObj).equalsIgnoreCase("")) {
            vSImage = generalFunc.getJsonValueStr("vSCountryImage", userProfileJsonObj);
            new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(getActContext(), vSImage, imagewidth, imageheight)), countryimage).build();


        }

        String vCode = generalFunc.getJsonValueStr("vCode", userProfileJsonObj);

        if (!vCode.equals("")) {
            isCountrySelected = true;
            vPhoneCode = vCode;
            vCountryCode = generalFunc.getJsonValueStr("vCountry", userProfileJsonObj);
        }

        selected_currency = generalFunc.getJsonValueStr("vCurrencyCompany", userProfileJsonObj);
    }

    public void buildLanguageList() {

        JSONArray languageList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.LANGUAGE_LIST_KEY));
        languageDataList.clear();

        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.LANGUAGE_LIST_KEY, "");
        data.put(Utils.LANGUAGE_CODE_KEY, "");
        data = generalFunc.retrieveValue(data);

        for (int i = 0; i < languageList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(languageList_arr, i);

            HashMap<String, String> mapData = new HashMap<>();
            mapData.put("vTitle", generalFunc.getJsonValueStr("vTitle", obj_temp));
            mapData.put("vCode", generalFunc.getJsonValueStr("vCode", obj_temp));
            mapData.put("vService_TEXT_color", generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp));
            mapData.put("vService_BG_color", generalFunc.getJsonValueStr("vService_BG_color", obj_temp));

            if (Utils.getText(txtlangtxt).equalsIgnoreCase(generalFunc.getJsonValueStr("vTitle", obj_temp))) {
                selLanguagePosition = i;
            }

            if ((data.get(Utils.LANGUAGE_CODE_KEY)).equalsIgnoreCase(generalFunc.getJsonValueStr("vCode", obj_temp))) {
                selLanguagePosition = i;

                txtlangtxt.setText(generalFunc.getJsonValueStr("vTitle", obj_temp));
            }

            languageDataList.add(mapData);

            if ((generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY)).equals(generalFunc.getJsonValue("vCode", obj_temp))) {
                selected_language_code = generalFunc.getJsonValueStr("vCode", obj_temp);

            }
        }


        if (languageDataList.size() < 2) {
            langSelectArea.setVisibility(View.GONE);
        }

        buildCurrencyList();

    }

    public void buildCurrencyList() {


        JSONArray currencyList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.CURRENCY_LIST_KEY));
        currencyDataList.clear();
        for (int i = 0; i < currencyList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(currencyList_arr, i);

            HashMap<String, String> mapData = new HashMap<>();
            mapData.put("vName", generalFunc.getJsonValueStr("vName", obj_temp));
            mapData.put("vCode", generalFunc.getJsonValueStr("vSymbol", obj_temp));
            mapData.put("vSymbol", generalFunc.getJsonValueStr("vSymbol", obj_temp));
            mapData.put("vService_BG_color", generalFunc.getJsonValueStr("vService_BG_color", obj_temp));
            mapData.put("vService_TEXT_color", generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp));


            if (Utils.getText(txtcurtxt).equalsIgnoreCase(generalFunc.getJsonValueStr("vName", obj_temp))) {
                selCurrancyPosition = i;
            }

            currencyDataList.add(mapData);

        }

        if (currencyDataList.size() < 2) {
            currencySelectArea.setVisibility(View.GONE);

            if (languageDataList.size() < 2) {
                langSelectArea.setVisibility(View.GONE);
            }
        }
        currencySelectArea.setVisibility(View.GONE);
    }

    public void showCurrencyList() {

        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_CURRENCY"), currencyDataList, OpenListView.OpenDirection.CENTER, true, position -> {

            selCurrancyPosition = position;
            HashMap<String, String> mapData = currencyDataList.get(position);


            selected_currency = mapData.get("vName");
            txtcurtxt.setText(mapData.get("vName"));


        }, true, generalFunc.retrieveLangLBl("", "LBL_CURRENCY_PREFER"), false).show(selCurrancyPosition, "vName");
    }

    int selCurrancyPosition = -1;
    int selLanguagePosition = -1;

    public void showLanguageList() {


        OpenListView.getInstance(getActContext(), getSelectLangText(), languageDataList, OpenListView.OpenDirection.CENTER, true, position -> {


            selLanguagePosition = position;
            HashMap<String, String> mapData = languageDataList.get(position);

            selected_language_code = mapData.get("vCode");


            if (!generalFunc.retrieveValue(Utils.DEFAULT_LANGUAGE_VALUE).equals(mapData.get("vTitle"))) {
                txtlangtxt.setText(mapData.get("vTitle"));
                generalFunc.storeData(Utils.DEFAULT_LANGUAGE_VALUE, mapData.get("vTitle"));

            }

        }, true, generalFunc.retrieveLangLBl("", "LBL_LANG_PREFER"), false).show(selLanguagePosition, "vTitle");
    }

    public String getSelectLangText() {
        return ("" + generalFunc.retrieveLangLBl("Select", "LBL_SELECT_LANGUAGE_HINT_TXT"));
    }

    public void checkValues() {
        boolean companyEntered = Utils.checkText(txtcompanytxt) ? true : Utils.setErrorFields(txtcompanytxt, required_str);
        boolean isEmailBlankAndOptional = generalFunc.isEmailBlankAndOptional(generalFunc, Utils.getText(txtemailtxt));
        boolean emailEntered = isEmailBlankAndOptional ? true : (Utils.checkText(txtemailtxt) ?
                (generalFunc.isEmailValid(Utils.getText(txtemailtxt)) ? true : Utils.setErrorFields(txtemailtxt, error_email_str))
                : Utils.setErrorFields(txtemailtxt, required_str));
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, required_str);
        boolean countryEntered = isCountrySelected ? true : Utils.setErrorFields(countryBox, required_str);
        boolean currencyEntered = !selected_currency.equals("") ? true : Utils.setErrorFields(txtcurtxt, required_str);

        if (mobileEntered) {
            mobileEntered = mobileBox.length() >= 3 ? true : Utils.setErrorFields(mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }
        if (companyEntered == false || emailEntered == false || mobileEntered == false
                || countryEntered == false || currencyEntered == false) {
            return;
        }

        String currentMobileNum = generalFunc.getJsonValueStr("vPhone", userProfileJsonObj);
        String currentPhoneCode = generalFunc.getJsonValueStr("vCode", userProfileJsonObj);
        String vEmail = generalFunc.getJsonValueStr("vEmail", userProfileJsonObj);

        if (!currentPhoneCode.equals(vPhoneCode) || !currentMobileNum.equals(Utils.getText(mobileBox)) || !vEmail.equals(Utils.getText(txtemailtxt))) {

            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_EDIT_MOB_EMAIL_NOTE"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> {

                if (buttonId == 1) {
                    updateProfile();
                }

            });

            return;
        }

        updateProfile();
    }

    public void updateProfile() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateUserProfileDetail"); //UpdateRestaurantDetails
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("vName", Utils.getText(txtcompanytxt));
        parameters.put("vPhone", Utils.getText(mobileBox));
        parameters.put("vEmail", Utils.getText(txtemailtxt));
        parameters.put("vPhoneCode", vPhoneCode);
        parameters.put("vCountry", vCountryCode);
        parameters.put("CurrencyCode", selected_currency);
        parameters.put("LanguageCode", selected_language_code);
        parameters.put("UserType", Utils.app_type);


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);
                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                        if (isDataAvail == true) {

                            String currentLangCode = generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY);
                            String vCurrencyCompany = generalFunc.getJsonValueStr("vCurrencyCompany", userProfileJsonObj);

                            try {
                                String messgeJson = generalFunc.getJsonValueStr(Utils.message_str, responseObj);
                                generalFunc.storeData(Utils.USER_PROFILE_JSON, messgeJson);
                                responseString = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

                            } catch (Exception e) {

                            }

                            new ConfigureMemberData(responseString, generalFunc, getActContext(), false);

                            if (!currentLangCode.equals(selected_language_code) || !selected_currency.equals(vCurrencyCompany)) {
                                GenerateAlertBox alertBox = generalFunc.notifyRestartApp();
                                alertBox.setCancelable(false);
                                alertBox.setBtnClickList(btn_id -> {

                                    if (btn_id == 1) {
                                        //  generalFunc.restartApp();
                                        generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, selected_language_code);
                                        generalFunc.storeData(Utils.DEFAULT_CURRENCY_VALUE, selected_currency);
                                        loaderView.setVisibility(View.VISIBLE);

                                        changeLanguagedata(selected_language_code);
                                    }
                                });
                            } else {
                                myProfileAct.changeUserProfileJson(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                            }

                        } else {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });

    }

    public void changeLanguagedata(String langcode) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "changelanguagelabel");
        parameters.put("vLang", langcode);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);
                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                        if (isDataAvail == true) {

                            loaderView.setVisibility(View.GONE);
                            generalFunc.storeData(Utils.languageLabelsKey, generalFunc.getJsonValueStr(Utils.message_str, responseObj));
                            generalFunc.storeData(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValueStr("eType", responseObj));
                            generalFunc.storeData(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValueStr("vGMapLangCode", responseObj));
                            GeneralFunctions.clearAndResetLanguageLabelsData(MyApp.getInstance().getApplicationContext());

                            generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

                            GenerateAlertBox alertBox = generalFunc.notifyRestartApp();
                            alertBox.setCancelable(false);
                            alertBox.setBtnClickList(btn_id -> {

                                if (btn_id == 1) {
                                    //  generalFunc.restartApp();
                                    generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, selected_language_code);
                                    generalFunc.storeData(Utils.DEFAULT_CURRENCY_VALUE, selected_currency);
                                    GetUserData getUserData = new GetUserData(generalFunc, MyApp.getInstance().getApplicationContext());
                                    getUserData.GetConfigDataForLocalStorage();

                                    new Handler().postDelayed(() -> generalFunc.restartApp(), 100);

                                }
                            });
                        } else {

                            loaderView.setVisibility(View.GONE);
                        }
                    } else {
                        loaderView.setVisibility(View.GONE);
                    }

                });

    }

    public Context getActContext() {
        return myProfileAct.getActContext();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == myProfileAct.RESULT_OK && data != null) {
            vCountryCode = data.getStringExtra("vCountryCode");
            vPhoneCode = generalFunc.convertNumberWithRTL(data.getStringExtra("vPhoneCode"));
            isCountrySelected = true;
            vSImage = data.getStringExtra("vSImage");
            new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(getActContext(), vSImage, imagewidth, imageheight)), countryimage).build();
            countryBox.setText("+" + vPhoneCode);
        } else if (requestCode == Utils.VERIFY_MOBILE_REQ_CODE && resultCode == myProfileAct.RESULT_OK) {
            updateProfile();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }

    public class setOnTouchList implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                view.performClick();
            }
            return true;
        }
    }


    public void onClickView(View view) {
        int i = view.getId();
        Utils.hideKeyboard(getActivity());
        if (i == submitBtnId) {

            checkValues();
        } else if (i == R.id.countryBox) {
            if (countryPicker == null) {
                countryPicker = new CountryPicker.Builder(getActContext()).showingDialCode(true)
                        .setLocale(locale).showingFlag(true)
                        .enablingSearch(true)
                        //.setCountries(items_list)
                        .setCountrySelectionListener(country -> setData(country.getCode(), country.getDialCode(), country.getFlagName()))
                        .build();
            }
            countryPicker.show(getActContext());
        }
    }


    public void setData(String vCountryCode, String vPhoneCode, String vSImage) {
        this.vCountryCode = vCountryCode;
        this.vPhoneCode = vPhoneCode;
        isCountrySelected = true;
        this.vSImage = vSImage;

        new LoadImage.builder(LoadImage.bind(vSImage), countryimage).build();

        GeneralFunctions generalFunctions = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
        countryBox.setText("+" + generalFunctions.convertNumberWithRTL(vPhoneCode));
    }
}
