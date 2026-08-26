package com.multixpro.provider;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import com.activity.ParentActivity;
import com.general.PermissionHandlers;
import com.general.call.CommunicationManager;
import com.general.files.AESEnDecryption;
import com.general.files.ActUtils;
import com.general.files.ConfigureMemberData;
import com.general.files.GeneralFunctions;
import com.general.files.GetFeatureClassList;
import com.general.files.GetLocationUpdates;
import com.general.files.GetUserData;
import com.general.files.MyApp;
import com.general.files.OnClearFromRecentService;
import com.general.files.OpenMainProfile;
import com.general.files.SetGeneralData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.snackbar.Snackbar;
import com.service.handler.ApiHandler;
import com.service.server.ServerTask;
import com.utils.CabRequestStatus;
import com.utils.CommonUtilities;
import com.utils.DeviceSettings;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.anim.loader.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LauncherActivity extends ParentActivity implements ProviderInstaller.ProviderInstallListener, ServerTask.FileDataResponse {

    AVLoadingIndicatorView loaderView;


    long autoLoginStartTime = 0;
    boolean isnotification = false;

    /*4.4 lower Device SSl CERTIFICATE ISSUE*/

    private static final int ERROR_DIALOG_REQUEST_CODE = 1;
    private boolean mRetryProviderInstall;
    RelativeLayout rlContentArea;
    MTextView drawOverMsgTxtView;

    GenerateAlertBox currentAlertBox;

    String LBL_BTN_OK_TXT, LBL_CANCEL_TXT, LBL_RETRY_TXT, LBL_TRY_AGAIN_TXT;

    boolean isPermissionShown_general;
    String response_str_generalConfigData = "";
    String response_str_autologin = "";
    private static ArrayList<String> requestPermissions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            requestPermissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
        }
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> false);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        generalFunc.storeData("isInLauncher", "true");
        loaderView = findViewById(R.id.loaderView);
        rlContentArea = findViewById(R.id.rlContentArea);
        drawOverMsgTxtView = findViewById(R.id.drawOverMsgTxtView);
        drawOverMsgTxtView.setText(generalFunc.retrieveLangLBl("Please wait while we are checking app's configuration. This will take few seconds.", "LBL_DRAW_OVER_APP_NOTE"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        LBL_RETRY_TXT = generalFunc.retrieveLangLBl("Retry", "LBL_RETRY_TXT");
        LBL_CANCEL_TXT = generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT");
        LBL_BTN_OK_TXT = generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT");
        LBL_TRY_AGAIN_TXT = generalFunc.retrieveLangLBl("Please try again.", "LBL_TRY_AGAIN_TXT");

        ProviderInstaller.installIfNeededAsync(this, this);


        if (generalFunc.isUserLoggedIn() && Utils.checkText(generalFunc.getMemberId()) && DeviceSettings.isDeviceGPSEnabled()) {
            GetLocationUpdates.getInstance().startLocationUpdates(null, null);
        }
    }


    public void checkConfigurations(boolean isPermissionShown) {
        drawOverMsgTxtView.setVisibility(View.GONE);

        isPermissionShown_general = isPermissionShown;

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

    public void continueProcess() {
        closeAlert();
        showLoader();

        Utils.setAppLocal(getActContext());

        if (generalFunc.isUserLoggedIn() && Utils.checkText(generalFunc.getMemberId())) {

            boolean isAppRestarted = generalFunc.retrieveValue("APP_RESTART_EVENT").equalsIgnoreCase("Yes");
            boolean isBatterySetting = generalFunc.retrieveValue(PermissionHandlers.BATTERY_SETTINGS_KEY).equalsIgnoreCase("Yes");
            if (isAppRestarted && isBatterySetting) {
                loaderView.setVisibility(View.GONE);
                generalFunc.storeData("APP_RESTART_EVENT", "No");
                new OpenMainProfile(getActContext(), true, generalFunc, isnotification).startProcess();
                return;
            }

            generalFunc.storeData("APP_RESTART_EVENT", "No");
            generalFunc.storeData(PermissionHandlers.BATTERY_SETTINGS_KEY, "No");

            if (this.response_str_autologin.trim().equalsIgnoreCase("")) {
                CommunicationManager.getInstance().initiateService(generalFunc, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                new OpenMainProfile(getActContext(), true, generalFunc, isnotification).startProcess();
                autoLogin();
            } else {
                continueAutoLogin(this.response_str_autologin);
            }
        } else {
            if (this.response_str_generalConfigData.trim().equalsIgnoreCase("")) {
                //downloadGeneralData();
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

    public void restartAppDailog() {
        closeAlert();
        generalFunc.showGeneralMessage("", LBL_TRY_AGAIN_TXT, LBL_BTN_OK_TXT, "", buttonId -> generalFunc.restartApp());
    }

    public void downloadGeneralData() {
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
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);
                    if (isFinishing()) {
                        restartAppDailog();
                        return;
                    }

                    if (responseObj != null && !responseObj.equals("")) {

                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {
                            if (!generalFunc.isAllPermissionGranted(isPermissionShown_general, requestPermissions)) {
                                response_str_generalConfigData = responseString;
                                showNoPermission();
                                return;
                            }

                            continueDownloadGeneralData(responseString);

                        } else {
                            if (!generalFunc.getJsonValueStr("isAppUpdate", responseObj).trim().equals("")
                                    && generalFunc.getJsonValueStr("isAppUpdate", responseObj).equals("true")) {

                                showAppUpdateDialog(generalFunc.retrieveLangLBl("New update is available to download. " +
                                                "Downloading the latest update, you will get latest features, improvements and bug fixes.",
                                        generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                            } else {
                                String setMsg = LBL_TRY_AGAIN_TXT;
                                if (Utils.checkText(generalFunc.getJsonValueStr(Utils.message_str, responseObj))) {
                                    setMsg = generalFunc.getJsonValueStr(Utils.message_str, responseObj);
                                }
                                currentAlertBox = generalFunc.showGeneralMessage("", setMsg, LBL_CANCEL_TXT, LBL_RETRY_TXT, buttonId -> {
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

    public void continueDownloadGeneralData(String responseString) {
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


        if (!generalFunc.isAllPermissionGranted(true, requestPermissions)) {
            showNoPermission();
            return;
        }

        Bundle bn = new Bundle();
        bn.putBoolean("isAnimated", true);
        new ActUtils(getActContext()).startActWithData(AppLoginActivity.class, bn);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        try {
            ActivityCompat.finishAffinity(LauncherActivity.this);
        } catch (Exception ignored) {

        }
    }

    private void storeImportantData(String responseString) {
        generalFunc.storeData("TSITE_DB", generalFunc.getJsonValue("TSITE_DB", responseString));
        generalFunc.storeData("GOOGLE_API_REPLACEMENT_URL", generalFunc.getJsonValue("GOOGLE_API_REPLACEMENT_URL", responseString));
        generalFunc.storeData("APP_LAUNCH_IMAGES", generalFunc.getJsonValue("APP_LAUNCH_IMAGES", responseString));
    }

    public void autoLogin() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
        }
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

        parameters.putAll((new CabRequestStatus(getActContext())).getAllStatusParam());

        ApiHandler.execute(getActContext(), parameters, false, true, generalFunc,
                responseString -> {

//                    closeLoader();
//                    if (isFinishing()) {
//                        return;
//                    }

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

                            if (!generalFunc.isAllPermissionGranted(isPermissionShown_general, requestPermissions)) {
                                response_str_autologin = responseString;
                                showNoPermission();
                                return;
                            }
                            generalFunc.storeData(Utils.USER_PROFILE_JSON, message);
                            if (generalFunc.getJsonValue("UPDATE_USER_DATA", responseString).equals("No")) {
                                return;
                            }
                            MyApp.getInstance().openSessionLoaderView();
                            new Handler().postDelayed(() -> {
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


                                showError("",
                                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                            }
                        }
                    } else {
                        autoLoginStartTime = 0;
                        showError();
                    }
                });


    }


    public void continueAutoLogin(String responseString) {
        JSONObject responseObj = generalFunc.getJsonObject(responseString);

        final String message = generalFunc.getJsonValueStr(Utils.message_str, responseObj);

        ((new CabRequestStatus(getActContext()))).removeOldRequestsData();

        if (generalFunc.getJsonValue("SERVER_MAINTENANCE_ENABLE", message).equalsIgnoreCase("Yes")) {
            new ActUtils(getActContext()).startAct(AppRestrictedActivity.class);
            finish();
            return;
        }

        generalFunc.storeData(Utils.USER_PROFILE_JSON, message);

        generalFunc.storeData(Utils.SESSION_ID_KEY, generalFunc.getJsonValue("tSessionId", message));
        generalFunc.storeData(Utils.DEVICE_SESSION_ID_KEY, generalFunc.getJsonValue("tDeviceSessionId", message));
        generalFunc.storeData(Utils.WORKLOCATION, generalFunc.getJsonValue("vWorkLocation", message));

        CommunicationManager.getInstance().initiateService(generalFunc, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        if (Calendar.getInstance().getTimeInMillis() - autoLoginStartTime < 2000) {
            new Handler(Looper.myLooper()).postDelayed(() -> {
                String vTripStatus = generalFunc.getJsonValue("vTripStatus", message);
                if (!vTripStatus.equalsIgnoreCase("Not Active")) {
                    if (vTripStatus.contains("Arrived") || vTripStatus.contains("Active") || vTripStatus.contains("On Going Trip")) {
                        new OpenMainProfile(getActContext(), true, generalFunc, isnotification).startProcess();
                    } else {
                        new OpenMainProfile(getActContext(), true, generalFunc, isnotification).startProcess();
                    }
                } else {
                    new OpenMainProfile(getActContext(), true, generalFunc, isnotification).startProcess();
                }
            }, 2000);
        } else {
            String vTripStatus = generalFunc.getJsonValue("vTripStatus", message);
            if (vTripStatus.contains("Arrived") || vTripStatus.contains("Active") || vTripStatus.contains("On Going Trip")) {
                new OpenMainProfile(getActContext(), true, generalFunc, isnotification).startProcess();
            } else {
                new OpenMainProfile(getActContext(), true, generalFunc, isnotification).startProcess();
            }
        }

    }

    public void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
    }

    public void closeLoader() {
        loaderView.setVisibility(View.GONE);
    }

    private void closeAlert() {
        try {
            if (currentAlertBox != null) {
                currentAlertBox.closeAlertBox();
                currentAlertBox = null;
            }
        } catch (Exception e) {

        }
    }

    public void showContactUs(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", content, generalFunc.retrieveLangLBl("Contact Us", "LBL_CONTACT_US_TXT"), LBL_BTN_OK_TXT, buttonId -> {
            if (buttonId == 0) {
                new ActUtils(getActContext()).startAct(ContactUsActivity.class);
                showContactUs(content);
            } else if (buttonId == 1) {
                MyApp.getInstance().logOutFromDevice(true);
            }
        });
    }

    public void showError() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", LBL_TRY_AGAIN_TXT, LBL_CANCEL_TXT, LBL_RETRY_TXT, buttonId -> handleBtnClick(buttonId, "ERROR"));
    }

    public void showError(String title, String contentMsg) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage(title, contentMsg, LBL_CANCEL_TXT, LBL_RETRY_TXT, buttonId -> handleBtnClick(buttonId, "ERROR"));
    }

    public void showNoInternetDialog() {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT"), LBL_CANCEL_TXT, LBL_RETRY_TXT, buttonId -> handleBtnClick(buttonId, "NO_INTERNET"));
    }


    public void showNoPermission() {
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
                "LBL_ALLOW_PERMISSIONS_APP"), LBL_CANCEL_TXT, generalFunc.retrieveLangLBl("Allow All", "LBL_SETTINGS"), buttonId -> handleBtnClick(buttonId, "NO_PERMISSION"));
    }

    public void showErrorOnPlayServiceDialog(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage("", content, LBL_RETRY_TXT, generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"), buttonId -> handleBtnClick(buttonId, "NO_PLAY_SERVICE"));
    }

    public void showAppUpdateDialog(String content) {
        closeAlert();
        currentAlertBox = generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("New update available", "LBL_NEW_UPDATE_AVAIL"), content, LBL_RETRY_TXT, generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"), buttonId -> handleBtnClick(buttonId, "APP_UPDATE"));
    }


    public Context getActContext() {
        return LauncherActivity.this;
    }

    public void handleBtnClick(int buttonId, String alertType) {

        if (buttonId == 0) {
            if (!alertType.equals("NO_PLAY_SERVICE") && !alertType.equals("APP_UPDATE")) {
                finish();
            } else {
                checkConfigurations(false);
            }

        } else if (alertType.equals("APP_UPDATE")) {
            boolean isSuccessfulOpen = new ActUtils(getActContext()).openURL("market://details?id=" + BuildConfig.APPLICATION_ID);
            if (!isSuccessfulOpen) {
                new ActUtils(getActContext()).openURL("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            }
            checkConfigurations(false);
        } else if (alertType.equals("NO_PERMISSION")) {
            generalFunc.openSettings();

        } else {
            if (alertType.equals("NO_PLAY_SERVICE")) {
                boolean isSuccessfulOpen = new ActUtils(getActContext()).openURL("market://details?id=com.google.android.gms");
                if (!isSuccessfulOpen) {
                    new ActUtils(getActContext()).openURL("http://play.google.com/store/apps/details?id=com.google.android.gms");
                }
                checkConfigurations(false);
            } else if (!alertType.equals("NO_GPS")) {
                checkConfigurations(false);
            } else {
                new ActUtils(getActContext()).
                        startActForResult(Settings.ACTION_LOCATION_SOURCE_SETTINGS, Utils.REQUEST_CODE_GPS_ON);
                checkConfigurations(false);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generalFunc.storeData("isInLauncher", "false");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utils.REQUEST_CODE_GPS_ON:
                checkConfigurations(false);
                break;
            case GeneralFunctions.MY_SETTINGS_REQUEST:
                checkConfigurations(false);
                break;
            case Utils.OVERLAY_PERMISSION_REQ_CODE:
                drawOverMsgTxtView.setVisibility(View.GONE);
                if (!generalFunc.canDrawOverlayViews(getActContext())) {
                    drawOverMsgTxtView.setVisibility(View.VISIBLE);
                    new Handler(Looper.myLooper()).postDelayed(() -> checkConfigurations(true), 15000);
                } else {
                    checkConfigurations(true);
                }
                break;
            case ERROR_DIALOG_REQUEST_CODE:
                mRetryProviderInstall = true;
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GeneralFunctions.MY_PERMISSIONS_REQUEST:
                if (!generalFunc.isAllPermissionGranted(false, requestPermissions)) {
                    return;
                }
                checkConfigurations(false);
                break;
        }
    }

    @Override
    public void onProviderInstalled() {
        checkConfigurations(true);
    }

    @Override
    public void onProviderInstallFailed(int errorCode, Intent intent) {

        int resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().showErrorDialogFragment(this,
                    errorCode,
                    ERROR_DIALOG_REQUEST_CODE,
                    dialog -> onProviderInstallerNotAvailable());
        } else {
            onProviderInstallerNotAvailable();
        }
    }

    private void onProviderInstallerNotAvailable() {
        checkConfigurations(true);
        showMessageWithAction(rlContentArea, generalFunc.retrieveLangLBl("provider cannot be updated for some reason.", "LBL_PROVIDER_NOT_AVALIABLE_TXT"));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (mRetryProviderInstall) {
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        mRetryProviderInstall = false;
    }

    public void showMessageWithAction(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(10000);
        snackbar.show();
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
