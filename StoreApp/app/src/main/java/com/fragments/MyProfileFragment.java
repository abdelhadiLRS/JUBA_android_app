package com.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.dialogs.BottomInfoDialog;
import com.dialogs.OpenListView;
import com.general.files.ActUtils;
import com.general.files.ConfigureMemberData;
import com.general.files.GeneralFunctions;
import com.general.files.GetUserData;
import com.general.files.InternetConnection;
import com.general.files.MyApp;
import com.general.files.TrendyDialog;
import com.multixpro.store.BankDetailActivity;
import com.multixpro.store.ContactUsActivity;
import com.multixpro.store.HelpActivity23Pro;
import com.multixpro.store.HistoryActivity;
import com.multixpro.store.ItemAvailabilityActivity;
import com.multixpro.store.ListOfDocumentActivity;
import com.multixpro.store.ManageAccountActivity;
import com.multixpro.store.MyGalleryActivity;
import com.multixpro.store.MyProfileActivity;
import com.multixpro.store.NotificationActivity;
import com.multixpro.store.R;
import com.multixpro.store.RestaurantDetailActivity;
import com.multixpro.store.RestaurantSettingsActivity;
import com.multixpro.store.SetWorkingHoursActivity;
import com.multixpro.store.StaticPageActivity;
import com.multixpro.store.StatisticsActivity;
import com.multixpro.store.ThermalPrintSettingActivity;
import com.multixpro.store.VerifyInfoActivity;
import com.kyleduo.switchbutton.SwitchButton;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.service.handler.ApiHandler;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProfileFragment extends BaseFragment {

    private static final String TAG = "MyProfileFragment";
    public String userProfileJson = "";
    public JSONObject obj_userProfile;
    GeneralFunctions generalFunc;

    ImageView editProfileImage, imgSmartLoginQuery;

    SelectableRoundedImageView userImgView, userImgView_toolbar;
    ImageView infoImg;
    MTextView userNameTxt, userNameTxt_toolbar, userEmailTxt, txtUserMobile, generalSettingHTxt, accountHTxt;
    MTextView bookingTxt, headerResSettingTxt, headerStatisTxt;
    MTextView notificationHTxt, privacyHTxt, termsHTxt, myPaymentHTxt, mybookingHTxt, personalDetailsHTxt, changePasswordHTxt, changeCurrencyHTxt, changeLanguageHTxt, supportHTxt, livechatHTxt, contactUsHTxt;
    LinearLayout notificationArea, privacyArea, myBookingArea,
            personalDetailsArea, changesPasswordArea, changesCurrancyArea, changeslanguageArea, termsArea, liveChatArea, contactUsArea, verifyMobArea, smartLoginArea, verifyEmailArea, storeMultiImageArea;
    View notificationView, myBookingView, aboutUsView, privacyView, statisticsView, changeCurrencyView, changeLangView, termsView, livechatView, verifyMobView, smartLoginView, verifyEmailView, storeMultiImageView;
    LinearLayout bookingArea, headerResSettingArea, headerStatisArea, logOutArea, itemsArea, documentArea;
    LinearLayout helpArea, aboutusArea, headeritemsArea, bankDetailsArea, statisticsArea, resSettingArea, resSettingDetailsArea, resSettingHourArea;
    MTextView helpHTxt, aboutusHTxt, logoutTxt, otherHTxt, headeritemsTxt, bankDetailsHTxt, statisticsHTxt, itemsHTxt, documentHTxt, resSettingHTxt, resSettingDetalisHTxt, resSettingHourHTxt, verifyMobHTxt, smartLoginHTxt, verifyEmailHTxt, storeMultiImageHtxt;
    ImageView notificationArrow, privacyArrow, termsArrow, helpArrow, aboutusArrow, statisticsArrow,
            mybookingArrow, personalDetailsArrow, documentArrow, resSettingArrow, resSettingDetailsArrow, resSettingHourArrow,
            changePasswordArrow, changeCurrencyArrow, changeLangArrow, livechatArrow, contactUsArrow, logoutArrow, bankDetailsArrow, itemsArrow, verifyMobsArrow, verifyEmailArrow, storeMultiImageArrow;

    MTextView printerHTxt;
    ImageView printerArrow;
    LinearLayout printerArea;
    ImageView printerImage;

    View view;
    InternetConnection internetConnection;
    String ENABLE_FAVORITE_DRIVER_MODULE_KEY = "";
    boolean isDeliverOnlyEnabled;
    boolean isAnyDeliverOptionEnabled;
    AlertDialog alertDialog;
    String SITE_TYPE = "";
    String SITE_TYPE_DEMO_MSG = "";
    private SwitchButton smartLoginSwitchBtn;

    ArrayList<HashMap<String, String>> language_list = new ArrayList<>();
    ArrayList<HashMap<String, String>> currency_list = new ArrayList<>();
    String selected_language_code = "";
    String default_selected_language_code = "";

    private int selCurrancyPosition = -1, selLanguagePosition = -1;
    String selected_currency = "", default_selected_currency = "", selected_currency_symbol = "";

    LinearLayout toolbar_profile;
    String app_type = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        if (view != null) {
//            return view;
//        }
        view = inflater.inflate(R.layout.activity_my_profile_new, container, false);
        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

        ENABLE_FAVORITE_DRIVER_MODULE_KEY = generalFunc.retrieveValue(Utils.ENABLE_FAVORITE_DRIVER_MODULE_KEY);
        isDeliverOnlyEnabled = generalFunc.isDeliverOnlyEnabled();
        isAnyDeliverOptionEnabled = generalFunc.isAnyDeliverOptionEnabled();
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);
        app_type = generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile);
        SITE_TYPE = generalFunc.getJsonValueStr("SITE_TYPE", obj_userProfile);
        SITE_TYPE_DEMO_MSG = generalFunc.getJsonValueStr("SITE_TYPE_DEMO_MSG", obj_userProfile);
        selected_currency = generalFunc.getJsonValue("vCurrencyCompany", userProfileJson);
        default_selected_currency = selected_currency;
        internetConnection = new InternetConnection(getActContext());
        initViews();
        setLabel();
        setuserInfo();
        manageView();
        buildLanguageList();

        return view;

    }

    private void manageView() {
        if (generalFunc.getJsonValue("ENABLE_LIVE_CHAT", userProfileJson).equalsIgnoreCase("Yes")) {
            liveChatArea.setVisibility(View.VISIBLE);
            livechatView.setVisibility(View.VISIBLE);
        } else {
            liveChatArea.setVisibility(View.GONE);
            livechatView.setVisibility(View.GONE);
        }

        if (!generalFunc.getJsonValue("showTermsCondition", userProfileJson).equalsIgnoreCase("No")) {
            termsArea.setVisibility(View.VISIBLE);
            termsView.setVisibility(View.VISIBLE);
        } else {
            termsArea.setVisibility(View.GONE);
            termsView.setVisibility(View.GONE);
        }

        if (!generalFunc.getJsonValue("showPrivacyPolicy", userProfileJson).equalsIgnoreCase("No")) {
            privacyArea.setVisibility(View.VISIBLE);
            privacyView.setVisibility(View.VISIBLE);
        } else {
            privacyArea.setVisibility(View.GONE);
            privacyView.setVisibility(View.GONE);
        }

        if (!generalFunc.getJsonValue("showAboutUs", userProfileJson).equalsIgnoreCase("No")) {
            aboutusArea.setVisibility(View.VISIBLE);
            aboutUsView.setVisibility(View.VISIBLE);
        } else {
            aboutusArea.setVisibility(View.GONE);
            aboutUsView.setVisibility(View.GONE);
        }

        if (generalFunc.getJsonValue("ENABLE_NEWS_SECTION", userProfileJson) != null && generalFunc.getJsonValue("ENABLE_NEWS_SECTION", userProfileJson).equalsIgnoreCase("yes")) {
            notificationArea.setVisibility(View.VISIBLE);
            notificationView.setVisibility(View.VISIBLE);

        } else {
            notificationArea.setVisibility(View.GONE);
            notificationView.setVisibility(View.GONE);

        }


        //JSONArray currencyList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.CURRENCY_LIST_KEY));
//
//        if (currencyList_arr.length() < 2) {
//            changesCurrancyArea.setVisibility(View.GONE);
//            changeCurrencyView.setVisibility(View.GONE);
//        } else {
//            changesCurrancyArea.setVisibility(View.VISIBLE);
//            changeCurrencyView.setVisibility(View.VISIBLE);
//        }

        changesCurrancyArea.setVisibility(View.GONE);
        changeCurrencyView.setVisibility(View.GONE);
        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.LANGUAGE_LIST_KEY, "");
        data.put(Utils.LANGUAGE_CODE_KEY, "");
        data = generalFunc.retrieveValue(data);

        JSONArray languageList_arr = generalFunc.getJsonArray(data.get(Utils.LANGUAGE_LIST_KEY));

        if (languageList_arr.length() < 2) {
            changeslanguageArea.setVisibility(View.GONE);
        } else {
            changeslanguageArea.setVisibility(View.VISIBLE);
        }

    }

    private void initViews() {

        editProfileImage = view.findViewById(R.id.editProfileImage);
        userImgView = view.findViewById(R.id.userImgView);
        infoImg = view.findViewById(R.id.infoImg);
        addToClickHandler(infoImg);
        userImgView_toolbar = view.findViewById(R.id.userImgView_toolbar);
        userNameTxt = view.findViewById(R.id.userNameTxt);
        userNameTxt_toolbar = view.findViewById(R.id.userNameTxt_toolbar);
        userEmailTxt = view.findViewById(R.id.userEmailTxt);
        txtUserMobile = view.findViewById(R.id.txtUserMobile);
        bookingTxt = view.findViewById(R.id.bookingTxt);
        headerResSettingTxt = view.findViewById(R.id.headerResSettingTxt);
        headerStatisTxt = view.findViewById(R.id.headerStatisTxt);
        headeritemsTxt = view.findViewById(R.id.headeritemsTxt);
        generalSettingHTxt = view.findViewById(R.id.generalSettingHTxt);
        accountHTxt = view.findViewById(R.id.accountHTxt);
        notificationHTxt = view.findViewById(R.id.notificationHTxt);

        privacyHTxt = view.findViewById(R.id.privacyHTxt);
        termsHTxt = view.findViewById(R.id.termsHTxt);
        logoutTxt = view.findViewById(R.id.logoutTxt);
        otherHTxt = view.findViewById(R.id.otherHTxt);

        notificationArea = view.findViewById(R.id.notificationArea);

        privacyArea = view.findViewById(R.id.privacyArea);
        logoutTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOGOUT"));
        otherHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));

        myBookingArea = view.findViewById(R.id.myBookingArea);

        personalDetailsArea = view.findViewById(R.id.personalDetailsArea);
        changesPasswordArea = view.findViewById(R.id.changesPasswordArea);
        changesCurrancyArea = view.findViewById(R.id.changesCurrancyArea);
        changeslanguageArea = view.findViewById(R.id.changeslanguageArea);
        termsArea = view.findViewById(R.id.termsArea);
        liveChatArea = view.findViewById(R.id.liveChatArea);
        contactUsArea = view.findViewById(R.id.contactUsArea);
        notificationView = view.findViewById(R.id.notificationView);
        verifyMobArea = view.findViewById(R.id.verifyMobArea);
        verifyEmailArea = view.findViewById(R.id.verifyEmailArea);
        storeMultiImageArea = view.findViewById(R.id.storeMultiImageArea);
        addToClickHandler(verifyEmailArea);

        myBookingView = view.findViewById(R.id.myBookingView);

        aboutUsView = view.findViewById(R.id.aboutUsView);
        privacyView = view.findViewById(R.id.privacyView);
        statisticsView = view.findViewById(R.id.statisticsView);
        changeCurrencyView = view.findViewById(R.id.changeCurrencyView);
        changeLangView = view.findViewById(R.id.changeLangView);
        termsView = view.findViewById(R.id.termsView);
        livechatView = view.findViewById(R.id.livechatView);
        verifyMobView = view.findViewById(R.id.verifyMobView);
        verifyEmailView = view.findViewById(R.id.verifyEmailView);
        storeMultiImageView = view.findViewById(R.id.storeMultiImageView);

        bookingArea = view.findViewById(R.id.bookingArea);
        headerResSettingArea = view.findViewById(R.id.headerResSettingArea);
        headerStatisArea = view.findViewById(R.id.headerStatisArea);
        itemsArea = view.findViewById(R.id.itemsArea);
        documentArea = view.findViewById(R.id.documentArea);

        logOutArea = view.findViewById(R.id.logOutArea);

        myPaymentHTxt = view.findViewById(R.id.myPaymentHTxt);
        mybookingHTxt = view.findViewById(R.id.mybookingHTxt);

        personalDetailsHTxt = view.findViewById(R.id.personalDetailsHTxt);
        changePasswordHTxt = view.findViewById(R.id.changePasswordHTxt);
        changeCurrencyHTxt = view.findViewById(R.id.changeCurrencyHTxt);
        changeLanguageHTxt = view.findViewById(R.id.changeLanguageHTxt);
        supportHTxt = view.findViewById(R.id.supportHTxt);
        livechatHTxt = view.findViewById(R.id.livechatHTxt);
        contactUsHTxt = view.findViewById(R.id.contactUsHTxt);

        headeritemsArea = view.findViewById(R.id.headeritemsArea);

        bankDetailsArea = view.findViewById(R.id.bankDetailsArea);

        statisticsArea = view.findViewById(R.id.statisticsArea);
        resSettingArea = view.findViewById(R.id.resSettingArea);
        resSettingDetailsArea = view.findViewById(R.id.resSettingDetailsArea);
        resSettingHourArea = view.findViewById(R.id.resSettingHourArea);

        helpArea = view.findViewById(R.id.helpArea);
        aboutusArea = view.findViewById(R.id.aboutusArea);

        notificationArrow = view.findViewById(R.id.notificationArrow);

        privacyArrow = view.findViewById(R.id.privacyArrow);
        termsArrow = view.findViewById(R.id.termsArrow);

        helpArrow = view.findViewById(R.id.helpArrow);
        aboutusArrow = view.findViewById(R.id.aboutusArrow);
        statisticsArrow = view.findViewById(R.id.statisticsArrow);
        mybookingArrow = view.findViewById(R.id.mybookingArrow);

        personalDetailsArrow = view.findViewById(R.id.personalDetailsArrow);
        changePasswordArrow = view.findViewById(R.id.changePasswordArrow);
        changeCurrencyArrow = view.findViewById(R.id.changeCurrencyArrow);
        changeLangArrow = view.findViewById(R.id.changeLangArrow);
        livechatArrow = view.findViewById(R.id.livechatArrow);
        contactUsArrow = view.findViewById(R.id.contactUsArrow);
        logoutArrow = view.findViewById(R.id.logoutArrow);

        bankDetailsArrow = view.findViewById(R.id.bankDetailsArrow);
        itemsArrow = view.findViewById(R.id.itemsArrow);
        documentArrow = view.findViewById(R.id.documentArrow);
        resSettingArrow = view.findViewById(R.id.resSettingArrow);
        resSettingDetailsArrow = view.findViewById(R.id.resSettingDetailsArrow);
        resSettingHourArrow = view.findViewById(R.id.resSettingHourArrow);
        verifyMobsArrow = view.findViewById(R.id.verifyMobsArrow);
        verifyEmailArrow = view.findViewById(R.id.verifyEmailArrow);
        storeMultiImageArrow = view.findViewById(R.id.storeMultiImageArrow);


        printerHTxt = view.findViewById(R.id.printerHTxt);
        printerArrow = view.findViewById(R.id.printerArrow);
        printerArea = view.findViewById(R.id.printerArea);
        printerImage = view.findViewById(R.id.printerImage);

        bankDetailsHTxt = view.findViewById(R.id.bankDetailsHTxt);

        statisticsHTxt = view.findViewById(R.id.statisticsHTxt);
        itemsHTxt = view.findViewById(R.id.itemsHTxt);
        documentHTxt = view.findViewById(R.id.documentHTxt);
        resSettingHTxt = view.findViewById(R.id.resSettingHTxt);
        resSettingDetalisHTxt = view.findViewById(R.id.resSettingDetalisHTxt);
        resSettingHourHTxt = view.findViewById(R.id.resSettingHourHTxt);
        verifyMobHTxt = view.findViewById(R.id.verifyMobHTxt);
        verifyEmailHTxt = view.findViewById(R.id.verifyEmailHTxt);
        storeMultiImageHtxt = view.findViewById(R.id.storeMultiImageHtxt);

        helpHTxt = view.findViewById(R.id.helpHTxt);
        aboutusHTxt = view.findViewById(R.id.aboutusHTxt);
        toolbar_profile = view.findViewById(R.id.toolbar_profile);

        smartLoginArea = view.findViewById(R.id.smartLoginArea);
        smartLoginView = view.findViewById(R.id.smartLoginView);
        smartLoginHTxt = view.findViewById(R.id.smartLoginHTxt);
        smartLoginSwitchBtn = view.findViewById(R.id.smartLoginSwitchBtn);
        smartLoginSwitchBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                generalFunc.storeData("isSmartLogin", "Yes");
                generalFunc.storeData("isUserSmartLogin", "Yes");
            } else {
                generalFunc.storeData("isSmartLogin", "No");
                generalFunc.storeData("isUserSmartLogin", "No");
            }
        });
        imgSmartLoginQuery = view.findViewById(R.id.imgSmartLoginQuery);
        imgSmartLoginQuery.setOnClickListener(v -> {
            BottomInfoDialog bottomInfoDialog = new BottomInfoDialog(getActContext(), generalFunc);
            bottomInfoDialog.showPreferenceDialog(generalFunc.retrieveLangLBl("", "LBL_QUICK_LOGIN"), generalFunc.retrieveLangLBl("", "LBL_QUICK_LOGIN_NOTE_2_TXT"),
                    R.raw.biometric, false, generalFunc.retrieveLangLBl("", "LBL_OK"), "", true);
        });


        addToClickHandler(notificationArea);
        addToClickHandler(privacyArea);
        addToClickHandler(myBookingArea);
        addToClickHandler(bookingArea);
        addToClickHandler(headerResSettingArea);
        addToClickHandler(headerStatisArea);
        addToClickHandler(logOutArea);
        addToClickHandler(headeritemsArea);
        addToClickHandler(itemsArea);
        addToClickHandler(documentArea);
        addToClickHandler(helpArea);
        addToClickHandler(aboutusArea);
        addToClickHandler(personalDetailsArea);
        addToClickHandler(changesPasswordArea);
        addToClickHandler(changesCurrancyArea);
        addToClickHandler(changeslanguageArea);
        addToClickHandler(termsArea);
        addToClickHandler(liveChatArea);
        addToClickHandler(contactUsArea);
        addToClickHandler(verifyMobArea);
        addToClickHandler(editProfileImage);
        addToClickHandler(printerArea);
        addToClickHandler(bankDetailsArea);
        addToClickHandler(statisticsArea);
        addToClickHandler(resSettingArea);
        addToClickHandler(resSettingDetailsArea);
        addToClickHandler(resSettingHourArea);
        addToClickHandler(storeMultiImageArea);


        if (generalFunc.isRTLmode()) {

            notificationArrow.setRotation(180);

            privacyArrow.setRotation(180);
            termsArrow.setRotation(180);

            helpArrow.setRotation(180);
            aboutusArrow.setRotation(180);
            mybookingArrow.setRotation(180);
            printerArrow.setRotation(180);
            personalDetailsArrow.setRotation(180);
            changePasswordArrow.setRotation(180);
            changeCurrencyArrow.setRotation(180);
            changeLangArrow.setRotation(180);
            livechatArrow.setRotation(180);
            contactUsArrow.setRotation(180);
            logoutArrow.setRotation(180);
            bankDetailsArrow.setRotation(180);
            itemsArrow.setRotation(180);
            documentArrow.setRotation(180);
            resSettingArrow.setRotation(180);
            resSettingDetailsArrow.setRotation(180);
            resSettingHourArrow.setRotation(180);
            statisticsArrow.setRotation(180);
            verifyMobsArrow.setRotation(180);
            verifyEmailArrow.setRotation(180);
            storeMultiImageArrow.setRotation(180);


        }


        NestedScrollView scroller = (NestedScrollView) view.findViewById(R.id.scroll);
        scroller.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY > oldScrollY) {

                Logger.d(TAG, "Scroll DOWN");

                if (scrollY > getResources().getDimension(R.dimen._75sdp)) {
                    toolbar_profile.setVisibility(View.VISIBLE);
                }
            }
            if (scrollY < oldScrollY) {
                Logger.d(TAG, "Scroll UP");
                if (scrollY < getResources().getDimension(R.dimen._75sdp)) {
                    toolbar_profile.setVisibility(View.GONE);
                }

            }

            if (scrollY == 0) {
                Logger.d(TAG, "TOP SCROLL");

            }

            if (scrollY == (v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight())) {
                Logger.d(TAG, "BOTTOM SCROLL");
            }
        });

    }

    private void setLabel() {
        storeMultiImageHtxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANAGE_GALLARY"));
        verifyEmailHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EMAIL_VERIFY"));
        smartLoginHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_QUICK_LOGIN"));
        verifyMobHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MOBILE_VERIFY"));
        resSettingHourHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SET_BUSINESS_HOURS"));
        resSettingDetalisHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SET_RESTAURANT_DETAILS"));
        resSettingHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RESTAURANT_SETTINGS"));
        documentHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANAGE_DOCUMENT"));
        itemsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ITEMS"));
        statisticsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STATISTICS"));
        printerHTxt.setText(generalFunc.retrieveLangLBl("Printer settings", "LBL_T_PRINT_TITLE_TXT"));
        bankDetailsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_BANK_DETAIL"));

        headerStatisTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STATISTICS"));
        headeritemsTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ITEMS"));
        headerResSettingTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SETTINGS"));
        bookingTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EARNING_HISTORY"));
        generalSettingHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_SETTING"));
        notificationHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NOTIFICATIONS"));

        privacyHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PRIVACY_POLICY_TEXT"));
        termsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TERMS_CONDITION"));
        myPaymentHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAYMENT"));


        // helpHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HELP_CENTER"));
        helpHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FAQ_TXT"));
        aboutusHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ABOUT_US_TXT"));
        mybookingHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_EARNING_HISTORY"));

        if (generalFunc.getJsonValue("ENABLE_ACCOUNT_DELETION", userProfileJson).equalsIgnoreCase("Yes")) {
            personalDetailsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANAGE_ACCOUNT_TXT"));
        } else {
            personalDetailsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PERSONAL_DETAILS"));
        }

        accountHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_SETTING"));
        changePasswordHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_PASSWORD_TXT"));
        changeCurrencyHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_CURRENCY"));
        changeLanguageHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_LANGUAGE"));
        supportHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUPPORT"));
        livechatHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LIVE_CHAT"));
        contactUsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
        logoutTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOGOUT"));
        otherHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));


    }

    private void setuserInfo() {
        if (generalFunc.getJsonValueStr("ENABLE_STORE_WISE_BANNER", obj_userProfile).equalsIgnoreCase("Yes")) {
            storeMultiImageArea.setVisibility(View.VISIBLE);
            storeMultiImageView.setVisibility(View.VISIBLE);
        } else {
            storeMultiImageArea.setVisibility(View.GONE);
            storeMultiImageView.setVisibility(View.GONE);
        }
        userNameTxt.setText(generalFunc.getJsonValueStr("vCompany", obj_userProfile));
        userNameTxt_toolbar.setText(generalFunc.getJsonValueStr("vCompany", obj_userProfile));
        String vEmail = generalFunc.getJsonValueStr("vEmail", obj_userProfile);
        String vPhone = "(+" + generalFunc.getJsonValueStr("vCode", obj_userProfile) + ") " + generalFunc.getJsonValueStr("vPhone", obj_userProfile);
        if (Utils.checkText(vEmail)) {
            userEmailTxt.setText(vEmail);
        } else {
            userEmailTxt.setVisibility(View.GONE);
        }
        txtUserMobile.setText(vPhone);

        String url = CommonUtilities.COMPANY_PHOTO_PATH + generalFunc.getMemberId() + "/" + generalFunc.getJsonValue("vImage", userProfileJson);
        generalFunc.checkProfileImage(userImgView, url, R.mipmap.ic_no_pic_user, R.mipmap.ic_no_pic_user);
        generalFunc.checkProfileImage(userImgView_toolbar, url, R.mipmap.ic_no_pic_user, R.mipmap.ic_no_pic_user);


        if (!generalFunc.getJsonValueStr("ePhoneVerified", obj_userProfile).equalsIgnoreCase("YES")) {
            verifyMobArea.setVisibility(View.VISIBLE);
            verifyMobView.setVisibility(View.VISIBLE);
        } else {
            verifyMobArea.setVisibility(View.GONE);
            verifyMobView.setVisibility(View.GONE);
        }

        if (!generalFunc.getJsonValueStr("eEmailVerified", obj_userProfile).equalsIgnoreCase("YES") && !generalFunc.getMemberId().equalsIgnoreCase("")) {
            verifyEmailArea.setVisibility(View.VISIBLE);
            infoImg.setVisibility(View.VISIBLE);
            verifyEmailView.setVisibility(View.VISIBLE);
        } else {
            verifyEmailArea.setVisibility(View.GONE);
            infoImg.setVisibility(View.GONE);
            verifyEmailView.setVisibility(View.GONE);
        }

        if (generalFunc.retrieveValue("isSmartLoginEnable").equalsIgnoreCase("Yes")) {
            smartLoginArea.setVisibility(View.VISIBLE);
            smartLoginView.setVisibility(View.VISIBLE);
        } else {
            smartLoginArea.setVisibility(View.GONE);
            smartLoginView.setVisibility(View.GONE);
        }

        smartLoginSwitchBtn.setCheckedNoEvent(generalFunc.retrieveValue("isSmartLogin").equalsIgnoreCase("Yes"));


        if (MyApp.getInstance().isThermalPrintAllowed(false)) {
            printerArea.setVisibility(View.VISIBLE);
            printerImage.setVisibility(View.VISIBLE);
        } else {
            printerArea.setVisibility(View.GONE);
            printerImage.setVisibility(View.GONE);
        }
    }

    private void buildLanguageList() {
        JSONArray languageList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.LANGUAGE_LIST_KEY));

        for (int i = 0; i < languageList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(languageList_arr, i);


            if ((generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY)).equals(generalFunc.getJsonValueStr("vCode", obj_temp))) {
                selected_language_code = generalFunc.getJsonValueStr("vCode", obj_temp);

                default_selected_language_code = selected_language_code;
                selLanguagePosition = i;
            }

            HashMap<String, String> data = new HashMap<>();
            data.put("vTitle", generalFunc.getJsonValueStr("vTitle", obj_temp));
            data.put("vCode", generalFunc.getJsonValueStr("vCode", obj_temp));
            data.put("vService_TEXT_color", generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp));
            data.put("vService_BG_color", generalFunc.getJsonValueStr("vService_BG_color", obj_temp));


            language_list.add(data);
        }
        if (language_list.size() < 2) {
            changeslanguageArea.setVisibility(View.GONE);
        } else {
            changeslanguageArea.setVisibility(View.VISIBLE);

        }
        if (language_list.size() < 2) {
            changeslanguageArea.setVisibility(View.GONE);
        } else {
            changeslanguageArea.setVisibility(View.VISIBLE);

        }

        buildCurrencyList();


    }

    private void updateProfile() {


        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updateUserProfileDetail");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("vName", generalFunc.getJsonValue("vCompany", userProfileJson));
        parameters.put("vLastName", generalFunc.getJsonValue("vLastName", userProfileJson));
        parameters.put("vPhone", generalFunc.getJsonValue("vPhone", userProfileJson));
        parameters.put("vPhoneCode", generalFunc.getJsonValue("vCode", userProfileJson));
        parameters.put("vCountry", generalFunc.getJsonValue("vCountry", userProfileJson));
        parameters.put("vEmail", generalFunc.getJsonValue("vEmail", userProfileJson));
        parameters.put("CurrencyCode", selected_currency);
        parameters.put("LanguageCode", selected_language_code);
        parameters.put("UserType", Utils.app_type);


        ApiHandler.execute(getActContext(), parameters,
                responseString -> {

                    if (responseString != null && !responseString.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                        if (isDataAvail) {

                            String currentLangCode = generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY);
                            String vCurrencyPassenger = generalFunc.getJsonValue("vCurrencyCompany", userProfileJson);

                            String messgeJson = generalFunc.getJsonValue(Utils.message_str, responseString);
                            generalFunc.storeData(Utils.USER_PROFILE_JSON, messgeJson);
                            responseString = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);


                            if (!currentLangCode.equals(selected_language_code) || !selected_currency.equals(vCurrencyPassenger)) {
                                new ConfigureMemberData(responseString, generalFunc, getActContext(), false);
                                changeLanguagedata(selected_language_code, true);
                            }

                        } else {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });

    }

    private void buildCurrencyList() {
        JSONArray currencyList_arr = generalFunc.getJsonArray(generalFunc.retrieveValue(Utils.CURRENCY_LIST_KEY));

        for (int i = 0; i < currencyList_arr.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(currencyList_arr, i);

            HashMap<String, String> data = new HashMap<>();
            data.put("vName", generalFunc.getJsonValueStr("vName", obj_temp));
            data.put("vCode", generalFunc.getJsonValueStr("vSymbol", obj_temp));
            data.put("vSymbol", generalFunc.getJsonValueStr("vSymbol", obj_temp));
            data.put("vService_BG_color", generalFunc.getJsonValueStr("vService_BG_color", obj_temp));
            data.put("vService_TEXT_color", generalFunc.getJsonValueStr("vService_TEXT_color", obj_temp));
            if (!selected_currency.equalsIgnoreCase("") && selected_currency.equalsIgnoreCase(generalFunc.getJsonValueStr("vName", obj_temp))) {
                selCurrancyPosition = i;
            }
            currency_list.add(data);
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);
        Logger.d("Onresume", ":: fragment called" + "::" + generalFunc.getJsonValueStr("user_available_balance", obj_userProfile));


        setuserInfo();
    }

    private void showLanguageList() {

//        OpenListView.getInstance(getActContext(), getSelectLangText(), language_list, OpenListView.OpenDirection.CENTER, true, position -> {
//
//
//            selLanguagePosition = position;
//            selected_language_code = language_list.get(selLanguagePosition).get("vCode");
//            generalFunc.storeData(Utils.DEFAULT_LANGUAGE_VALUE, language_list.get(selLanguagePosition).get("vTitle"));
//
//            if (generalFunc.getMemberId().equalsIgnoreCase("")) {
//                generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, selected_language_code);
//                generalFunc.storeData(Utils.DEFAULT_CURRENCY_VALUE, selected_currency);
//                changeLanguagedata(selected_language_code,false);
//            } else {
//                updateProfile();
//            }
//        }).show(selLanguagePosition, "vTitle");
        OpenListView.getInstance(getActContext(), getSelectLangText(), language_list, OpenListView.OpenDirection.CENTER, true, position -> {


            selLanguagePosition = position;
            selected_language_code = language_list.get(selLanguagePosition).get("vCode");
            generalFunc.storeData(Utils.DEFAULT_LANGUAGE_VALUE, language_list.get(selLanguagePosition).get("vTitle"));

            if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, selected_language_code);
                generalFunc.storeData(Utils.DEFAULT_CURRENCY_VALUE, selected_currency);
                changeLanguagedata(selected_language_code, false);
            } else {
                updateProfile();
            }

        }, true, generalFunc.retrieveLangLBl("", "LBL_LANG_PREFER"), true).show(selLanguagePosition, "vTitle");
    }

    private void showCurrencyList() {
//        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_CURRENCY"), currency_list, OpenListView.OpenDirection.CENTER, true, position -> {
//
//
//            selCurrancyPosition = position;
//            selected_currency_symbol = currency_list.get(selCurrancyPosition).get("vSymbol");
//            selected_currency = currency_list.get(selCurrancyPosition).get("vName");
//            if (generalFunc.getMemberId().equalsIgnoreCase("")) {
//                generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, selected_language_code);
//                generalFunc.storeData(Utils.DEFAULT_CURRENCY_VALUE, selected_currency);
//                changeLanguagedata(selected_language_code,false);
//            } else {
//                updateProfile();
//            }
//        }).show(selCurrancyPosition, "vName");
        OpenListView.getInstance(getActContext(), generalFunc.retrieveLangLBl("", "LBL_SELECT_CURRENCY"), currency_list, OpenListView.OpenDirection.CENTER, true, position -> {

            selCurrancyPosition = position;
            selected_currency_symbol = currency_list.get(selCurrancyPosition).get("vSymbol");
            selected_currency = currency_list.get(selCurrancyPosition).get("vName");
            if (generalFunc.getMemberId().equalsIgnoreCase("")) {
                generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, selected_language_code);
                generalFunc.storeData(Utils.DEFAULT_CURRENCY_VALUE, selected_currency);
                changeLanguagedata(selected_language_code, false);

            } else {
                updateProfile();
            }
        }, true, generalFunc.retrieveLangLBl("", "LBL_CURRENCY_PREFER"), true).show(selCurrancyPosition, "vName");
    }

    private void showPasswordBox() {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.change_passoword_layout, null);

        final String required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        final String noWhiteSpace = generalFunc.retrieveLangLBl("", "LBL_ERROR_NO_SPACE_IN_PASS");
        final String pass_length = generalFunc.retrieveLangLBl("", "LBL_ERROR_PASS_LENGTH_PREFIX")
                + " " + Utils.minPasswordLength + " " + generalFunc.retrieveLangLBl("", "LBL_ERROR_PASS_LENGTH_SUFFIX");
        final String vPassword = generalFunc.getJsonValueStr("vPassword", obj_userProfile);

        final MaterialEditText previous_passwordBox = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        setCommandEditView(previous_passwordBox, generalFunc.retrieveLangLBl("", "LBL_CURR_PASS_HEADER"), generalFunc.retrieveLangLBl("", "LBL_CURR_PASS_HEADER"));

        if (vPassword.equals("")) {
            previous_passwordBox.setVisibility(View.GONE);
        }

        final MaterialEditText newPasswordBox = (MaterialEditText) dialogView.findViewById(R.id.newPasswordBox);
        setCommandEditView(newPasswordBox, generalFunc.retrieveLangLBl("", "LBL_UPDATE_PASSWORD_HEADER_TXT"), generalFunc.retrieveLangLBl("", "LBL_UPDATE_PASSWORD_HINT_TXT"));

        ImageView cancelImg = (ImageView) dialogView.findViewById(R.id.cancelImg);
        MTextView submitTxt = (MTextView) dialogView.findViewById(R.id.submitTxt);
        MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);
        MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_PASSWORD_TXT"));

        submitTxt.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        final MaterialEditText reNewPasswordBox = (MaterialEditText) dialogView.findViewById(R.id.reNewPasswordBox);
        setCommandEditView(reNewPasswordBox, generalFunc.retrieveLangLBl("", "LBL_UPDATE_CONFIRM_PASSWORD_HEADER_TXT"), generalFunc.retrieveLangLBl("", "LBL_UPDATE_CONFIRM_PASSWORD_HEADER_TXT"));

        builder.setView(dialogView);


        cancelImg.setOnClickListener(v -> alertDialog.dismiss());
        cancelTxt.setOnClickListener(v -> alertDialog.dismiss());
        submitTxt.setOnClickListener(v -> {

            boolean isCurrentPasswordEnter = Utils.checkText(previous_passwordBox) ?
                    (Utils.getText(previous_passwordBox).contains(" ") ? Utils.setErrorFields(previous_passwordBox, noWhiteSpace)
                            : (Utils.getText(previous_passwordBox).length() >= Utils.minPasswordLength || Utils.setErrorFields(previous_passwordBox, pass_length)))
                    : Utils.setErrorFields(previous_passwordBox, required_str);

            boolean isNewPasswordEnter = Utils.checkText(newPasswordBox) ?
                    (Utils.getText(newPasswordBox).contains(" ") ? Utils.setErrorFields(newPasswordBox, noWhiteSpace)
                            : (Utils.getText(newPasswordBox).length() >= Utils.minPasswordLength || Utils.setErrorFields(newPasswordBox, pass_length)))
                    : Utils.setErrorFields(newPasswordBox, required_str);

            boolean isReNewPasswordEnter = Utils.checkText(reNewPasswordBox) ?
                    (Utils.getText(reNewPasswordBox).contains(" ") ? Utils.setErrorFields(reNewPasswordBox, noWhiteSpace)
                            : (Utils.getText(reNewPasswordBox).length() >= Utils.minPasswordLength || Utils.setErrorFields(reNewPasswordBox, pass_length)))
                    : Utils.setErrorFields(reNewPasswordBox, required_str);

            if ((!vPassword.equals("") && !isCurrentPasswordEnter) || !isNewPasswordEnter || !isReNewPasswordEnter) {
                return;
            }

            if (!Utils.getText(newPasswordBox).equals(Utils.getText(reNewPasswordBox))) {
                Utils.setErrorFields(reNewPasswordBox, generalFunc.retrieveLangLBl("", "LBL_VERIFY_PASSWORD_ERROR_TXT"));
                return;
            }

            changePassword(Utils.getText(previous_passwordBox), Utils.getText(newPasswordBox), previous_passwordBox);

        });

        builder.setView(dialogView);
        alertDialog = builder.create();

        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(alertDialog);
        }

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActContext(), R.drawable.all_roundcurve_card));
        alertDialog.show();
    }

    private void setCommandEditView(MaterialEditText editText, String floatingLabelText, String hintText) {
        editText.setFloatingLabelText(floatingLabelText);
        editText.setHint(hintText);
        editText.setTypeface(Typeface.DEFAULT);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        editText.setTransformationMethod(new AsteriskPasswordTransformationMethod());
    }

    private static class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }

    private void changePassword(String currentPassword, String password, MaterialEditText previous_passwordBox) {

        if (SITE_TYPE.equals("Demo")) {
            generalFunc.showGeneralMessage("", SITE_TYPE_DEMO_MSG);
            return;
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updatePassword");
        parameters.put("UserID", generalFunc.getMemberId());
        parameters.put("pass", password);
        parameters.put("CurrentPassword", currentPassword);
        parameters.put("UserType", Utils.app_type);


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

                    if (responseStringObject != null && !responseStringObject.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);

                        if (isDataAvail) {
                            alertDialog.dismiss();
                            generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValueStr(Utils.message_str, responseStringObject));
                            userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str_one, responseStringObject)));
                        } else {
                            previous_passwordBox.setText("");

                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });

    }

    @SuppressLint("NonConstantResourceId")
    public void onClickView(View view) {
        Utils.hideKeyboard(getActContext());
        Bundle bn = new Bundle();
        switch (view.getId()) {
            case R.id.printerArea:
                new ActUtils(getActContext()).startActForResult(ThermalPrintSettingActivity.class, bn, CommonUtilities.MY_THERMAL_REQ_CODE);
                break;

            case R.id.storeMultiImageArea:
                new ActUtils(getActContext()).startActWithData(MyGalleryActivity.class, bn);
                break;

            case R.id.infoImg:
                TrendyDialog customDialog = new TrendyDialog(getActContext());
                customDialog.setDetails(generalFunc.retrieveLangLBl("", "LBL_EMAIL_VERIFY"), generalFunc.retrieveLangLBl("", "LBL_EMAIL_VERIFY_NOTE_TXT"), generalFunc.retrieveLangLBl("Continue", "LBL_CONTINUE_BTN"), true, getActContext().getResources().getDrawable(R.drawable.ic_verify_email));
                customDialog.setNegativeBtnText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
                customDialog.setNegativeButtonVisibility(View.VISIBLE);
                customDialog.setTitleTextVisibility(View.GONE);
                customDialog.showDialog();
                customDialog.setNegativeBtnClick(() -> {

                });
                customDialog.setPositiveBtnClick(() -> verifyEmailArea.performClick());
                break;

            case R.id.personalDetailsArea:
                if (generalFunc.getJsonValue("ENABLE_ACCOUNT_DELETION", userProfileJson).equalsIgnoreCase("Yes")) {
                    new ActUtils(getActContext()).startAct(ManageAccountActivity.class);
                } else {
                    new ActUtils(getActContext()).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);
                }
                break;
            case R.id.editProfileImage:

                new ActUtils(getActContext()).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);
                break;


            case R.id.bookingArea:
            case R.id.myBookingArea:
                new ActUtils(getActContext()).startActWithData(HistoryActivity.class, bn);
                break;


            case R.id.notificationArea:
                new ActUtils(getActContext()).startAct(NotificationActivity.class);
                break;

            case R.id.itemsArea:
            case R.id.headeritemsArea:
                new ActUtils(getActContext()).startActWithData(ItemAvailabilityActivity.class, bn);
                break;


            case R.id.documentArea:
                bn.putString("PAGE_TYPE", "Company");
                bn.putString("iDriverVehicleId", "");
                bn.putString("doc_file", "");
                bn.putString("iDriverVehicleId", "");
                new ActUtils(getActContext()).startActWithData(ListOfDocumentActivity.class, bn);


                break;

            case R.id.resSettingArea:
            case R.id.headerResSettingArea:
                new ActUtils(getActContext()).startAct(RestaurantSettingsActivity.class);
                break;

            case R.id.resSettingDetailsArea:
                (new ActUtils(getActContext())).startAct(RestaurantDetailActivity.class);
                break;

            case R.id.resSettingHourArea:
                (new ActUtils(getActContext())).startAct(SetWorkingHoursActivity.class);
                break;
            case R.id.privacyArea:
                bn.putString("staticpage", "33");
                new ActUtils(getActContext()).startActWithData(StaticPageActivity.class, bn);
                break;


            case R.id.changesPasswordArea:
                showPasswordBox();
                break;
            case R.id.changesCurrancyArea:
                showCurrencyList();
                break;
            case R.id.changeslanguageArea:
                showLanguageList();
                break;
            case R.id.termsArea:
                bn.putString("staticpage", "4");
                new ActUtils(getActContext()).startActWithData(StaticPageActivity.class, bn);
                break;


            case R.id.helpArea:
                new ActUtils(getActContext()).startAct(HelpActivity23Pro.class);
                break;
            case R.id.liveChatArea:
                startChatActivity();
                break;
            case R.id.aboutusArea:
                bn.putString("staticpage", "1");
                new ActUtils(getActContext()).startActWithData(StaticPageActivity.class, bn);
                break;
            case R.id.contactUsArea:
                new ActUtils(getActContext()).startAct(ContactUsActivity.class);
                break;


            case R.id.bankDetailsArea:
                new ActUtils(getActContext()).startActWithData(BankDetailActivity.class, bn);
                break;

            case R.id.headerStatisArea:
            case R.id.statisticsArea:
                new ActUtils(getActContext()).startActWithData(StatisticsActivity.class, bn);
                break;

            case R.id.verifyMobArea:
                bn.putString("msg", "DO_PHONE_VERIFY");
                new ActUtils(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);

                break;

            case R.id.verifyEmailArea:
                bn.putString("msg", "DO_EMAIL_VERIFY");
                new ActUtils(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_MOBILE_REQ_CODE);
                break;


            case R.id.logOutArea:

                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    if (btn_id == 0) {
                        generateAlert.closeAlertBox();
                    } else {
                        if (internetConnection.isNetworkConnected()) {
                            MyApp.getInstance().logOutFromDevice(false);
                        } else {
                            generalFunc.showMessage(logOutArea, generalFunc.retrieveLangLBl("", "LBL_NO_INTERNET_TXT"));
                        }
                    }

                });
                generateAlert.setContentMessage(generalFunc.retrieveLangLBl("Logout", "LBL_LOGOUT"), generalFunc.retrieveLangLBl("Are you sure you want to logout?", "LBL_WANT_LOGOUT_APP_TXT"));
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                generateAlert.showAlertBox();
                break;


        }
    }

    private String getSelectLangText() {
        return ("" + generalFunc.retrieveLangLBl("Select", "LBL_SELECT_LANGUAGE_HINT_TXT"));
    }

    private void changeLanguagedata(String langcode, boolean showDialog) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "changelanguagelabel");
        parameters.put("vLang", langcode);

        ApiHandler.execute(getActContext(), parameters,
                responseString -> {
                    if (responseString != null && !responseString.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                        if (isDataAvail) {


                            generalFunc.storeData(Utils.languageLabelsKey, generalFunc.getJsonValue(Utils.message_str, responseString));
                            generalFunc.storeData(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("eType", responseString));
                            generalFunc.storeData(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", responseString));
                            GeneralFunctions.clearAndResetLanguageLabelsData(MyApp.getInstance().getApplicationContext());

                            if (showDialog) {
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
                                return;
                            }
                            new Handler().postDelayed(() -> generalFunc.restartApp(), 100);

                        }
                    }
                });

    }

    private void startChatActivity() {

        String driverName = generalFunc.getJsonValue("vCompany", userProfileJson);
        String driverEmail = generalFunc.getJsonValue("vEmail", userProfileJson);

        Utils.LIVE_CHAT_LICENCE_NUMBER = generalFunc.getJsonValue("LIVE_CHAT_LICENCE_NUMBER", userProfileJson);
        HashMap<String, String> map = new HashMap<>();
        map.put("FNAME", generalFunc.getJsonValue("vCompany", userProfileJson));
        map.put("LNAME", "");
        map.put("EMAIL", generalFunc.getJsonValue("vEmail", userProfileJson));
        map.put("USERTYPE", Utils.userType);

        Intent intent = new Intent(getActivity(), ChatWindowActivity.class);
        intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, Utils.LIVE_CHAT_LICENCE_NUMBER);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_NAME, driverName);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_EMAIL, driverEmail);
        intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, Utils.userType + "_" + generalFunc.getMemberId());

        intent.putExtra("myParam", map);
        startActivity(intent);
    }

    private Context getActContext() {
        return getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);
        setuserInfo();
    }
}
