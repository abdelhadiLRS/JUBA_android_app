package com.general.files;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.multidex.MultiDex;

import com.fontanalyzer.SystemFont;
import com.general.call.LocalHandler;
import com.general.call.SinchHandler;
import com.google.gson.Gson;
import com.multixpro.store.AccountverificationActivity;
import com.multixpro.store.AppRestrictedActivity;
import com.multixpro.store.BuildConfig;
import com.multixpro.store.LauncherActivity;
import com.multixpro.store.MainActivity;
import com.multixpro.store.NetworkChangeReceiver;
import com.multixpro.store.R;
import com.multixpro.store.SearchPickupLocationActivity;
import com.multixpro.store.TrackOrderActivity;
import com.service.handler.ApiHandler;
import com.service.handler.AppService;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.utils.Utils;
import com.utils.WeViewFontConfig;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 28-06-2016.
 */
public class MyApp extends Application {

    protected static MyApp mMyApp;

    public static synchronized MyApp getInstance() {
        return mMyApp;
    }

    private GeneralFunctions generalFun;
    private boolean isAppInBackground = true;

    private Activity currentAct = null;

    public MainActivity mainAct;
    public TrackOrderActivity trackOrderAct;
    private GpsReceiver mGpsReceiver;
    private BTReceiver mBTReceiver;
    private ActRegisterReceiver actRegisterReceiver;
    private NetworkChangeReceiver mNetWorkReceiver = null;

    private GenerateAlertBox generateSessionAlert;
    private long notification_permission_launch_time = -1;

    /*Thermal Print*/
    public static BluetoothSocket btsocket;
    public static OutputStream outputStream;

    private ViewGroup viewGroup;
    private View sessionLoaderView;

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApp = (MyApp) this.getApplicationContext();

        Utils.SERVER_CONNECTION_URL = CommonUtilities.SERVER_URL;
        //GeneralFunctions generalFunctions = new GeneralFunctions(this);
        HashMap<String, String> storeData = new HashMap<>();
        storeData.put("SERVERURL", CommonUtilities.SERVER_URL);
        storeData.put("SERVERWEBSERVICEPATH", CommonUtilities.SERVER_WEBSERVICE_PATH);
        storeData.put("USERTYPE", BuildConfig.USER_TYPE);
        GeneralFunctions.storeData(storeData, this);
        Utils.eSystem_Type_KIOSK = "";


        WeViewFontConfig.ASSETS_FONT_NAME = SystemFont.FontStyle.REGULAR.resValue;
        WeViewFontConfig.FONT_FAMILY_NAME = SystemFont.FontStyle.REGULAR.name;
        WeViewFontConfig.FONT_COLOR = "#343434";
        WeViewFontConfig.FONT_SIZE = "14px";

        try {
            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(false);
            built.setLoggingEnabled(false);
            Picasso.setSingletonInstance(built);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.IS_APP_IN_DEBUG_MODE = BuildConfig.DEBUG ? "Yes" : "No";
        Utils.userType = BuildConfig.USER_TYPE;
        Utils.app_type = BuildConfig.USER_TYPE;
        Utils.USER_ID_KEY = BuildConfig.USER_ID_KEY;

        setScreenOrientation();

        new GetCountryList(this);
        generalFun = new GeneralFunctions(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (mGpsReceiver == null) {
            registerReceiver();
        }
        if (actRegisterReceiver == null) {
            registerActReceiver();
        }
        if (mBTReceiver == null) {
            registerBTReceiver();
        }
    }


    private void clearFile(OutputStreamWriter outputStreamWriter) {
        try {
            PrintWriter writer = new PrintWriter(outputStreamWriter);
            writer.print("");
        } catch (Exception e) {

        }
    }

    public void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config_test.txt", Context.MODE_PRIVATE));
            clearFile(outputStreamWriter);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config_test.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("File not found: ", e.toString());
        } catch (IOException e) {
            Log.e("Can not read file: ", e.toString());
        }

        return ret;
    }

    public String readFromFile(File file) {

        String ret = "";

        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.config_data));

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append("\n").append(receiveString);
            }
            ret = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            Log.e("File not found: ", e.toString());
        } catch (IOException e) {
            Log.e("Can not read file: ", e.toString());
        }

        return ret;
    }

    public GeneralFunctions getGeneralFun(Context mContext) {
        return new GeneralFunctions(mContext, R.id.backImgView);
    }

    public GeneralFunctions getAppLevelGeneralFunc() {
        if (generalFun == null) {
            generalFun = new GeneralFunctions(this);
        }
        return generalFun;
    }

    private void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        try {
            extractLogToFile();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @SuppressLint("InflateParams")
    public void openSessionLoaderView() {
        viewGroup = getCurrentAct().findViewById(android.R.id.content);
        LayoutInflater inflater = (LayoutInflater) currentAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionLoaderView = inflater.inflate(R.layout.layout_session_loader_view, null);
        MTextView noteTxt = sessionLoaderView.findViewById(R.id.noteTxt);
        noteTxt.setText(generalFun.retrieveLangLBl("Updating Your Session...", "LBL_UPDATE_SESSION"));
        viewGroup.addView(sessionLoaderView);
        viewGroup.bringChildToFront(sessionLoaderView);
    }

    public void closeSessionLoaderView() {
        if (viewGroup != null) {
            viewGroup.removeView(sessionLoaderView);
            viewGroup = null;
        }
    }

    public boolean isThermalPrintAllowed(boolean checkAutoPrint) {
        boolean isAllowed = false;
        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.THERMAL_PRINT_ENABLE_KEY, "");
        data.put(Utils.THERMAL_PRINT_ALLOWED_KEY, "");
        if (checkAutoPrint) {
            data.put(Utils.AUTO_PRINT_KEY, "");

        }
        data = new GeneralFunctions(getCurrentAct()).retrieveValue(data);

        boolean printAllowed = data.get(Utils.THERMAL_PRINT_ENABLE_KEY).equalsIgnoreCase("Yes");//&& data.get(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes")
        boolean checkSettings = checkAutoPrint ? printAllowed && data.get(Utils.AUTO_PRINT_KEY).equalsIgnoreCase("Yes") : printAllowed;
        if (checkSettings) {
            isAllowed = true;
        }
        return isAllowed;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public boolean isMyAppInBackGround() {
        return this.isAppInBackground;
    }

    public void refreshWithConfigData() {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct());
        objRefresh.GetConfigData();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Logger.d("Api", "Object Destroyed >> MYAPP onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Logger.d("Api", "Object Destroyed >> MYAPP onTrimMemory");
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.d("Api", "Object Destroyed >> MYAPP onTerminate");
        removePubSub();
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        removeVoIpSettings();
    }

    private void removePubSub() {
        releaseBTReceiver();
        releaseGpsReceiver();
        releaseactReceiver();
        removeAllRunningInstances();
        AppService.destroy();
    }


    private void removeAllRunningInstances() {
        Logger.e("NetWorkDEMO", "removeAllRunningInstances called");
        connectReceiver(false);
    }

    private void registerReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        this.mGpsReceiver = new GpsReceiver();
        this.registerReceiver(this.mGpsReceiver, mIntentFilter);
    }

    private void releaseBTReceiver() {
        if (mBTReceiver != null) {
            this.unregisterReceiver(mBTReceiver);
            mBTReceiver = null;
        }
        closeBtSocket();
    }

    // Bluetooth Receiver action connect or disconnect
    private void registerBTReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction("ACTION_ACL_DISCONNECTED");

        this.mBTReceiver = new BTReceiver();
       // this.registerReceiver(mBTReceiver, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.registerReceiver(mBTReceiver, filter, RECEIVER_NOT_EXPORTED);
        }else {
            this.registerReceiver(mBTReceiver, filter);
        }
    }

    protected void closeBtSocket() {
        if (MyApp.btsocket != null && MyApp.btsocket.isConnected()) {
            try {
                MyApp.btsocket.close();
                MyApp.btsocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseGpsReceiver() {
        if (mGpsReceiver != null)
            this.unregisterReceiver(mGpsReceiver);
        this.mGpsReceiver = null;
    }

    private void releaseactReceiver() {

        if (actRegisterReceiver != null)
            this.unregisterReceiver(actRegisterReceiver);
        this.actRegisterReceiver = null;
    }


    private void registerActReceiver() {
        if (actRegisterReceiver == null) {
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(String.format("%s%s%s%s%s", "Act", "ivi", "tyR", "egis", "ter"));
            this.registerReceiver(this.actRegisterReceiver, mIntentFilter);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.registerReceiver(this.actRegisterReceiver, mIntentFilter, RECEIVER_NOT_EXPORTED);
            }else {
                this.registerReceiver(this.actRegisterReceiver, mIntentFilter);
            }
        }
    }

    private void registerNetWorkReceiver() {

        if (mNetWorkReceiver == null) {
            try {
                Logger.e("NetWorkDemo", "Network connectivity registered");
                IntentFilter mIntentFilter = new IntentFilter();
                mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
                /*Extra Filter Started */
                mIntentFilter.addAction(ConnectivityManager.EXTRA_IS_FAILOVER);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_REASON);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_EXTRA_INFO);
                /*Extra Filter Ended */
//                mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//                mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

                this.mNetWorkReceiver = new NetworkChangeReceiver();
                this.registerReceiver(this.mNetWorkReceiver, mIntentFilter);
            } catch (Exception e) {
                Logger.e("NetWorkDemo", "Network connectivity register error occurred");
            }
        }
    }

    private void unregisterNetWorkReceiver() {
        if (mNetWorkReceiver != null)
            try {
                Logger.e("NetWorkDemo", "Network connectivity unregistered");
                this.unregisterReceiver(mNetWorkReceiver);
                this.mNetWorkReceiver = null;
            } catch (Exception e) {
                Logger.e("NetWorkDemo", "Network connectivity register error occurred");
                e.printStackTrace();
            }
    }

    private void setScreenOrientation() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                try {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activity.setTitle(getResources().getString(R.string.app_name));

                setCurrentAct(activity);
                Utils.runGC();

                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                if (activity instanceof MainActivity || activity instanceof TrackOrderActivity) {
                    //Reset PubNub instance
                    configureAppServices();
                }
                if (!(activity instanceof LauncherActivity) && !(activity instanceof AppRestrictedActivity) && !(activity instanceof AccountverificationActivity) && generalFun.isUserLoggedIn() && activity.isTaskRoot()) {
                    openNotificationPermission();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Utils.runGC();
            }

            @Override
            public void onActivityResumed(Activity activity) {

                setCurrentAct(activity);

                isAppInBackground = false;
                Utils.runGC();
                Logger.d("CheckAppBackGround", "::" + isAppInBackground + " | currentAct-> " + currentAct);
                Utils.sendBroadCast(getApplicationContext(), Utils.BACKGROUND_APP_RECEIVER_INTENT_ACTION);
                LocalNotification.clearAllNotifications();

                if (currentAct instanceof SearchPickupLocationActivity) {
                    new Handler(Looper.myLooper()).postDelayed(() -> {
                        if (currentAct instanceof SearchPickupLocationActivity) {
                            ViewGroup viewGroup = currentAct.findViewById(android.R.id.content);
                            OpenNoLocationView.getInstance(currentAct, viewGroup).configView(false);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

                isAppInBackground = true;
                Utils.runGC();
                Logger.d("AppBackground", "FromPause");
                Utils.sendBroadCast(getApplicationContext(), Utils.BACKGROUND_APP_RECEIVER_INTENT_ACTION);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Logger.d("AppBackground", "onStop");
                Utils.runGC();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                /*Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated by this method will be passed to both).*/
                removeAllRunningInstances();
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Utils.hideKeyboard(activity);
                Utils.runGC();

//                connectReceiver(false);
                if (activity instanceof TrackOrderActivity && activity == trackOrderAct) {
                    trackOrderAct = null;
                }
                if (activity instanceof MainActivity && activity == mainAct) {
                    mainAct = null;
                }
                if ((activity instanceof TrackOrderActivity && activity == trackOrderAct) || (activity instanceof MainActivity && activity == mainAct)) {
                    AppService.destroy();
                }
            }
        });
    }

    private void connectReceiver(boolean isConnect) {
        if (isConnect && mNetWorkReceiver == null) {
            registerNetWorkReceiver();
        } else if (!isConnect && mNetWorkReceiver != null) {
            unregisterNetWorkReceiver();
        }
    }

    public Activity getCurrentAct() {
        return currentAct;
    }

    private void setCurrentAct(Activity currentAct) {
        this.currentAct = currentAct;
        RegisterActivity();

        if (currentAct instanceof LauncherActivity) {
            mainAct = null;
            trackOrderAct = null;
        }

        if (currentAct instanceof MainActivity) {
            trackOrderAct = null;
            mainAct = (MainActivity) currentAct;
        }

        if (currentAct instanceof TrackOrderActivity) {
            mainAct = null;
            trackOrderAct = (TrackOrderActivity) currentAct;
        }

        connectReceiver(true);
    }

    private void RegisterActivity() {
        sendBroadcast(new Intent(String.format("%s%s%s%s%s", "Act", "ivi", "tyR", "egis", "ter")));
    }

    private void extractLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        String path = Environment.getExternalStorageDirectory() + "/" + "MyApp/";
        String fullName = path + "Log";
        Logger.d("Api", "fullName" + fullName);
        // Extract to file.
        File file = new File(fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        } catch (IOException e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            // You might want to write a failure message to the log here.
        }
    }

    private void configureAppServices() {
        AppService.getInstance().resetAppServices();
    }

    private void removeVoIpSettings() {
        try {
            SinchHandler.getInstance().removeInitiateService();
            LocalHandler.getInstance().disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartWithGetDataApp() {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct());
        objRefresh.getData();
    }

    public void notifySessionTimeOut() {
        if (generateSessionAlert != null) {
            return;
        }
        generateSessionAlert = new GenerateAlertBox(MyApp.getInstance().getCurrentAct());
        generateSessionAlert.setContentMessage(generalFun.retrieveLangLBl("", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"),
                generalFun.retrieveLangLBl("Your session is expired. Please login again.", "LBL_SESSION_TIME_OUT"));
        generateSessionAlert.setPositiveBtn(generalFun.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateSessionAlert.setCancelable(false);
        generateSessionAlert.setBtnClickList(btn_id -> {
            if (btn_id == 1) {
                forceLogoutRemoveData();
            }
        });
        generateSessionAlert.showSessionOutAlertBox();
    }

    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public String getVersionCode() {
        return BuildConfig.VERSION_CODE + "";
    }

    public ArrayList<String> checkCameraWithMicPermission(boolean isCamera, boolean isPhone) {
        ArrayList<String> requestPermissions = new ArrayList<>();
        if (isCamera) {
            requestPermissions.add(Manifest.permission.CAMERA);
        }
        if (isPhone) {
            requestPermissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        requestPermissions.add(Manifest.permission.RECORD_AUDIO);
        return requestPermissions;
    }

    public void logOutFromDevice(boolean isForceLogout) {
        if (generalFun != null) {
            final HashMap<String, String> parameters = new HashMap<>();
            parameters.put("type", "callOnLogout");
            parameters.put("iMemberId", generalFun.getMemberId());
            parameters.put("UserType", Utils.userType);


            ApiHandler.execute(getCurrentAct(), parameters, true, false, generalFun,
                    responseString -> {
                        JSONObject responseObj = generalFun.getJsonObject(responseString);
                        if (responseObj != null && !responseObj.equals("")) {

                            if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {
                                forceLogoutRemoveData();
                            } else {
                                if (isForceLogout) {
                                    generalFun.showGeneralMessage("", generalFun.retrieveLangLBl("", generalFun.getJsonValueStr(Utils.message_str, responseObj)), buttonId -> (MyApp.getInstance().getGeneralFun(getCurrentAct())).restartApp());
                                } else {
                                    generalFun.showGeneralMessage("", generalFun.retrieveLangLBl("", generalFun.getJsonValueStr(Utils.message_str, responseObj)));
                                }
                            }
                        } else {
                            if (isForceLogout) {
                                generalFun.showError(buttonId -> (MyApp.getInstance().getGeneralFun(getCurrentAct())).restartApp());
                            } else {
                                generalFun.showError();
                            }
                        }
                    });

        }
    }

    public void forceLogoutRemoveData() {
        onTerminate();
        if (generalFun.retrieveValue("isUserSmartLogin").equalsIgnoreCase("Yes")) {
            HashMap<String, String> storeData = new HashMap<>();
            storeData.put(Utils.iMemberId_KEY, generalFun.retrieveValue(Utils.iMemberId_KEY));
            storeData.put(Utils.isUserLogIn, generalFun.retrieveValue(Utils.isUserLogIn));
            storeData.put(Utils.USER_PROFILE_JSON, generalFun.retrieveValue(Utils.USER_PROFILE_JSON));
            generalFun.storeData("QUICK_LOGIN_DIC", new Gson().toJson(storeData));
        } else {
            generalFun.storeData("isFirstTimeSmartLoginView", "No");
            generalFun.storeData("isUserSmartLogin", "No");
        }
        generalFun.logOutUser(MyApp.this);
        generalFun.restartApp();
    }

    public static void executeWV(WebView mWebView, GeneralFunctions generalFunc, String mMsg, boolean isLoadUrl) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHapticFeedbackEnabled(false);

        mWebView.setOnLongClickListener(v -> true);
        mWebView.setLongClickable(false);

        if (isLoadUrl) {
            mWebView.loadUrl(mMsg);
        } else {
            mWebView.loadDataWithBaseURL(null, generalFunc.wrapHtml(mWebView.getContext(), mMsg), "text/html", "UTF-8", null);
        }
    }

    public void openNotificationPermission() {
        if (getCurrentAct() == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.S || NotificationManagerCompat.from(getCurrentAct()).areNotificationsEnabled()) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            openNotificationPermissionDialogView();
            return;
        }

        ActivityResultLauncher<String> notificationActivityResult = ((ComponentActivity) getCurrentAct()).registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if ((System.currentTimeMillis() - notification_permission_launch_time < 1500) && !isGranted) {
                        openNotificationPermissionDialogView();
                    }
                }
        );
        notification_permission_launch_time = System.currentTimeMillis();
        notificationActivityResult.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    @SuppressLint("InflateParams")
    private void openNotificationPermissionDialogView() {

        GenerateAlertBox alert = new GenerateAlertBox(getCurrentAct());
        alert.setCustomView(R.layout.notification_permission_layout);

        MTextView titleTxt = (MTextView) alert.getView(R.id.titleTxt);
        MTextView btnAccept = (MTextView) alert.getView(R.id.btnAccept);
        MTextView btnReject = (MTextView) alert.getView(R.id.btnReject);

        String sourceString = generalFun.retrieveLangLBl("", "LBL_ALLOW_RUNTIME_NOTI_TXT").replace("#PROJECT_NAME#", "<b>" + getString(R.string.app_name) + "</b> ");
        titleTxt.setText(Html.fromHtml(sourceString));

        btnAccept.setText(generalFun.retrieveLangLBl("", "LBL_ALLOW"));
        btnReject.setText(generalFun.retrieveLangLBl("", "LBL_DONT_ALLOW_TXT"));

        btnAccept.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getCurrentAct().getPackageName());
                getCurrentAct().startActivity(intent);
                btnReject.performClick();
            }
        });
        btnReject.setOnClickListener(v -> alert.closeAlertBox());

        alert.showAlertBox();
        alert.alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // TODO: 30-01-2023 >> Do not delete this Method OR Rename
    public boolean validateApiResponse(String response) {

        JSONObject responseObj = generalFun.getJsonObject(response);

        if (generalFun.getJsonValueStr("RESTRICT_APP", responseObj).equalsIgnoreCase("Yes")) {
            if (currentAct instanceof AppRestrictedActivity) {
                return true;
            }
            Bundle bn = new Bundle();
            bn.putString("RESTRICT_APP", response);
            new ActUtils(currentAct).startActWithData(AppRestrictedActivity.class, bn);
            currentAct.finishAffinity();
            return true;
        }
        return false;
    }
}