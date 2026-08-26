package com.multixpro.store;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import com.activity.ParentActivity;
import com.general.call.CommunicationManager;
import com.general.files.AESEnDecryption;
import com.general.files.ActUtils;
import com.general.files.ConfigureMemberData;
import com.general.files.GeneralFunctions;
import com.general.files.GetFeatureClassList;
import com.general.files.GetUserData;
import com.general.files.MyApp;
import com.general.files.OpenMainProfile;
import com.general.files.SetGeneralData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.service.handler.ApiHandler;
import com.service.server.ServerTask;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.anim.loader.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

public class LauncherActivity extends ParentActivity implements ServerTask.FileDataResponse {

    private GenerateAlertBox currentAlertBox;
    private AVLoadingIndicatorView loaderView;
    private long autoLoginStartTime = 0;
    private String response_str_generalConfigData = "", response_str_autologin = "";
    boolean isnotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        generalFunc.storeData("isInLauncher", "true");
        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        checkConfigurations();
    }

    private void checkConfigurations() {
        closeAlert();
        int status = (GoogleApiAvailability.getInstance()).isGooglePlayServicesAvailable(getActContext());
        if (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            showErrorOnPlayServiceDialog(generalFunc.retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        } else if (status != ConnectionResult.SUCCESS) {
            showErrorOnPlayServiceDialog(generalFunc.retrieveLangLBl("This application requires updated google play service. " +
                    "Please install Or update it from play store", "LBL_UPDATE_PLAY_SERVICE_NOTE"));
            return;
        }
        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            showNoInternetDialog();
            return;
        }

        continueProcess();
    }

    private void continueProcess() {
        closeAlert();
        showLoader();

        Utils.setAppLocal(getActContext());
        if (generalFunc.isUserLoggedIn() && Utils.checkText(generalFunc.getMemberId())) {
            CommunicationManager.getInstance().initiateService(generalFunc, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
            new OpenMainProfile(getActContext(), obj_userProfile.toString(), true, generalFunc).startProcess();
            if (this.response_str_autologin.trim().equalsIgnoreCase("")) {
                autoLogin();
            } else {
                continueAutoLogin(this.response_str_autologin);
            }
        } else {
            if (this.response_str_generalConfigData.trim().equalsIgnoreCase("")) {
                // downloadGeneralData();
                if (MyApp.getInstance().readFromFile(this) != null && !MyApp.getInstance().readFromFile(this).equalsIgnoreCase("")) {

                    continueDownloadGeneralData(MyApp.getInstance().readFromFile(this));
                    manageConfigData();

                } else {

                    ApiHandler.downloadFile(this, CommonUtilities.BUCKET_PATH, this).execute();

                }
            } else {
                continueDownloadGeneralData(this.response_str_generalConfigData);
            }
        }
    }

    private void reStartAppDialog() {
        closeAlert();
        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please try again.", "LBL_TRY_AGAIN_TXT"),
                generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), "", buttonId -> generalFunc.restartApp());
    }

    private void downloadGeneralData() {
        closeAlert();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "generalConfigData");
        parameters.put("UserType", Utils.app_type);
        parameters.put("AppVersion", BuildConfig.VERSION_NAME);
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("vCurrency", generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE));
        parameters.putAll(GetFeatureClassList.getAllGeneralClasses());


        ApiHandler.execute(getActContext(), parameters,
                responseString -> {

                    if (isFinishing()) {
                        reStartAppDialog();
                        return;
                    }
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.equals("")) {

                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {
                            response_str_generalConfigData = responseString;
                            continueDownloadGeneralData(responseString);
                        } else {
                            String isAppUpdate = generalFunc.getJsonValueStr("isAppUpdate", responseObj);
                            if (!isAppUpdate.trim().equals("") && isAppUpdate.equals("true")) {
                                showAppUpdateDialog(generalFunc.retrieveLangLBl("New update is available to download. " +
                                                "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                        generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                            } else {
                                String setMsg = generalFunc.retrieveLangLBl("Please try again.", "LBL_TRY_AGAIN_TXT");
                                if (Utils.checkText(generalFunc.getJsonValue(Utils.message_str, responseString))) {
                                    setMsg = generalFunc.getJsonValue(Utils.message_str, responseString);
                                }
                                currentAlertBox = generalFunc.showGeneralMessage("", setMsg, generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> {
                                    if (buttonId == 1) {
                                        continueProcess();
                                    }
                                });
                            }
                        }
                    } else {
                        showError();
                    }
                });

    }

    private void continueDownloadGeneralData(String responseString) {
        JSONObject responseObj = generalFunc.getJsonObject(responseString);

        storeImportantData(responseString);
        new SetGeneralData(generalFunc, responseObj);
        Utils.setAppLocal(getActContext());

        closeLoader();

        if (generalFunc.getJsonValueStr("SERVER_MAINTENANCE_ENABLE", responseObj).equalsIgnoreCase("Yes")) {
            new ActUtils(getActContext()).startAct(AppRestrictedActivity.class);
            finish();
            return;
        }
        redirectToLogin();
    }

    private void storeImportantData(String responseString) {
        generalFunc.storeData("TSITE_DB", generalFunc.getJsonValue("TSITE_DB", responseString));
        generalFunc.storeData("GOOGLE_API_REPLACEMENT_URL", generalFunc.getJsonValue("GOOGLE_API_REPLACEMENT_URL", responseString));
        generalFunc.storeData("APP_LAUNCH_IMAGES", generalFunc.getJsonValue("APP_LAUNCH_IMAGES", responseString));
    }

    private void redirectToLogin() {
        Bundle bn = new Bundle();
        bn.putBoolean("isAnimated", true);
        new ActUtils(getActContext()).startActWithData(AppLoginActivity.class, bn);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        ActivityCompat.finishAffinity(LauncherActivity.this);
    }

    private void autoLogin() {
        closeAlert();
        autoLoginStartTime = Calendar.getInstance().getTimeInMillis();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getDetail");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.app_type);
        parameters.put("AppVersion", BuildConfig.VERSION_NAME);
        if (!generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY).equalsIgnoreCase("")) {
            parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        }
        if (obj_userProfile != null) {
            parameters.put("OLD_PROFILE_RESPONSE", obj_userProfile + "");
        }

        ApiHandler.execute(getActContext(), parameters, false, true, generalFunc, responseString -> {

//            closeLoader();
//            if (isFinishing()) {
//                return;
//            }
            JSONObject responseObj = generalFunc.getJsonObject(responseString);
            if (responseObj != null && !responseObj.equals("")) {

                if (generalFunc.getJsonValueStr("changeLangCode", responseObj).equalsIgnoreCase("Yes")) {
                    new ConfigureMemberData(responseString, generalFunc, getActContext(), false);
                }
                final String message = generalFunc.getJsonValueStr(Utils.message_str, responseObj);

                if (message.equals("SESSION_OUT")) {
                    autoLoginStartTime = 0;
                    MyApp.getInstance().notifySessionTimeOut();
                    Utils.runGC();
                    return;
                }

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {
                    storeImportantData(responseString);

                    generalFunc.storeData(Utils.USER_PROFILE_JSON, message);
                    if (generalFunc.getJsonValue("UPDATE_USER_DATA", responseString).equals("No")) {
                        return;
                    }
                    MyApp.getInstance().openSessionLoaderView();
                    new Handler().postDelayed(() -> {
                        response_str_autologin = responseString;
                        continueAutoLogin(responseString);
                        MyApp.getInstance().closeSessionLoaderView();
                    }, 2000);

                } else {
                    autoLoginStartTime = 0;
                    if (!generalFunc.getJsonValueStr("isAppUpdate", responseObj).trim().equals("")
                            && generalFunc.getJsonValueStr("isAppUpdate", responseObj).equals("true")) {

                        showAppUpdateDialog(generalFunc.retrieveLangLBl("New update is available to download. " +
                                        "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                    } else {
                        if (generalFunc.getJsonValueStr(Utils.message_str, responseObj).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_COMPANY") ||
                                generalFunc.getJsonValueStr(Utils.message_str, responseObj).equalsIgnoreCase("LBL_ACC_DELETE_TXT") ||
                                generalFunc.getJsonValueStr(Utils.message_str, responseObj).equalsIgnoreCase("LBL_CONTACT_US_STATUS_NOTACTIVE_DRIVER")) {

                            showContactUs(generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                            return;
                        }
                        showError("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                    }
                }
            } else {
                autoLoginStartTime = 0;
                showError();
            }
        });

    }

    private void continueAutoLogin(String responseString) {
        final String message = generalFunc.getJsonValue(Utils.message_str, responseString);
        if (generalFunc.getJsonValue("SERVER_MAINTENANCE_ENABLE", message).equalsIgnoreCase("Yes")) {
            new ActUtils(getActContext()).startAct(AppRestrictedActivity.class);
            finish();
            return;
        }
        HashMap<String, String> storeData = new HashMap<>();
        storeData.put(Utils.USER_PROFILE_JSON, message);

        storeData.put(Utils.SESSION_ID_KEY, generalFunc.getJsonValue("tSessionId", message));
        storeData.put(Utils.DEVICE_SESSION_ID_KEY, generalFunc.getJsonValue("tDeviceSessionId", message));
        //this keyword is use for ufx
        //driver can set the work location
        storeData.put(Utils.WORKLOCATION, generalFunc.getJsonValue("vWorkLocation", message));

        generalFunc.storeData(storeData);

        CommunicationManager.getInstance().initiateService(generalFunc, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        if (Calendar.getInstance().getTimeInMillis() - autoLoginStartTime < 2000) {
            new Handler(Looper.myLooper()).postDelayed(() -> {
                //
                new OpenMainProfile(getActContext(), generalFunc.getJsonValue(Utils.message_str, responseString), true, generalFunc, isnotification).startProcess();
            }, 2000);
        } else {
            new OpenMainProfile(getActContext(), generalFunc.getJsonValue(Utils.message_str, responseString), true, generalFunc, isnotification).startProcess();
        }
    }

    private void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
    }

    private void closeLoader() {
        loaderView.setVisibility(View.GONE);
    }

    private void closeAlert() {
        try {
            if (currentAlertBox != null) {
                currentAlertBox.closeAlertBox();
                currentAlertBox = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showContactUs(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", content, generalFunc.retrieveLangLBl("Contact Us", "LBL_CONTACT_US_TXT"), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), buttonId -> {
            if (buttonId == 0) {
                new ActUtils(getActContext()).startAct(ContactUsActivity.class);
                showContactUs(content);
            } else if (buttonId == 1) {
                MyApp.getInstance().logOutFromDevice(true);
            }
        });
    }

    private void showError() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please try again.", "LBL_TRY_AGAIN_TXT"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> handleBtnClick(buttonId, "ERROR"));
    }

    private void showError(String title, String contentMsg) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage(title, contentMsg, generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> handleBtnClick(buttonId, "ERROR"));
    }

    private void showNoInternetDialog() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> handleBtnClick(buttonId, "NO_INTERNET"));
    }

    private void showNoGPSDialog() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Your GPS seems to be disabled, do you want to enable it?", "LBL_ENABLE_GPS"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), buttonId -> handleBtnClick(buttonId, "NO_GPS"));
    }


    private void showErrorOnPlayServiceDialog(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", content, generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"), buttonId -> handleBtnClick(buttonId, "NO_PLAY_SERVICE"));
    }

    private void showAppUpdateDialog(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("New update available", "LBL_NEW_UPDATE_AVAIL"), content, generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"), buttonId -> handleBtnClick(buttonId, "APP_UPDATE"));
    }

    private void showNoLocationDialog() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Location not found. Please try later.", "LBL_NO_LOCATION_FOUND_TXT"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT"), buttonId -> handleBtnClick(buttonId, "NO_LOCATION"));
    }

    private Context getActContext() {
        return LauncherActivity.this;
    }

    private void handleBtnClick(int buttonId, String alertType) {
        if (buttonId == 0) {
            if (!alertType.equals("NO_PLAY_SERVICE") && !alertType.equals("APP_UPDATE")) {
                finish();
            } else {
                checkConfigurations();
            }
        } else if (alertType.equals("APP_UPDATE")) {
            boolean isSuccessfulOpen = new ActUtils(getActContext()).openURL("market://details?id=" + BuildConfig.APPLICATION_ID);
            if (!isSuccessfulOpen) {
                new ActUtils(getActContext()).openURL("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            }
            checkConfigurations();
        } else if (alertType.equals("NO_PERMISSION")) {
            generalFunc.openSettings();
        } else {
            if (alertType.equals("NO_PLAY_SERVICE")) {
                boolean isSuccessfulOpen = new ActUtils(getActContext()).openURL("market://details?id=com.google.android.gms");
                if (!isSuccessfulOpen) {
                    new ActUtils(getActContext()).openURL("http://play.google.com/store/apps/details?id=com.google.android.gms");
                }
                checkConfigurations();
            } else if (!alertType.equals("NO_GPS")) {
                checkConfigurations();
            } else {
                new ActUtils(getActContext()).startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON);
                checkConfigurations();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generalFunc.storeData("isInLauncher", "false");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utils.REQUEST_CODE_GPS_ON:
            case GeneralFunctions.MY_SETTINGS_REQUEST:
                checkConfigurations();
                break;
            case Utils.OVERLAY_PERMISSION_REQ_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (generalFunc.canDrawOverlayViews(getActContext())) {
                        generalFunc.restartApp();
                    } else {
                        checkConfigurations();
                    }
                }
                break;
        }
    }

    private void manageConfigData() {
        GetUserData objRefresh = new GetUserData(generalFunc, MyApp.getInstance().getCurrentAct());
        objRefresh.GetConfigDataForLocalStorage();
    }

    @Override
    public void onDownload(File file) {
        MyApp.getInstance().writeToFile(AESEnDecryption.getInstance().decrypt(AESEnDecryption.getInstance().fetchKeyAndIVAnData(MyApp.getInstance().readFromFile(file))), this);
        continueDownloadGeneralData(MyApp.getInstance().readFromFile(this));
        manageConfigData();

    }


    @Override
    public void onDownloadError(String s) {
        downloadGeneralData();

    }
}