package com.general.files;

import static com.activity.ParentActivity.LOCATION_PERMISSIONS_REQUEST;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.facebook.appevents.AppEventsLogger;
import com.fontanalyzer.SystemFont;
import com.general.PermissionHandlers;
import com.general.call.LocalHandler;
import com.general.call.SinchHandler;
import com.google.gson.Gson;
import com.multixpro.provider.AddAddressActivity;
import com.multixpro.provider.AppRestrictedActivity;
import com.multixpro.provider.BuildConfig;
import com.multixpro.provider.DriverArrivedActivity;
import com.multixpro.provider.LauncherActivity;
import com.multixpro.provider.MainActivity;
import com.multixpro.provider.MainActivity_22;
import com.multixpro.provider.NetworkChangeReceiver;
import com.multixpro.provider.R;
import com.multixpro.provider.WorkingtrekActivity;
import com.multixpro.provider.deliverAll.LiveTaskListActivity;
import com.service.handler.ApiHandler;
import com.service.handler.AppService;
import com.service.server.ServerTask;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.DeviceSettings;
import com.utils.Logger;
import com.utils.NavigationSensor;
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class MyApp extends Application {

    private GeneralFunctions generalFun;
    private GpsReceiver mGpsReceiver = null;
    private ActRegisterReceiver actRegisterReceiver;
    private static MyApp mMyApp;

    boolean isAppInBackground = true;
    public boolean ispoolRequest = false;

    private Activity currentAct = null;

    public MainActivity mainAct;
    public MainActivity_22 main22Act;
    public DriverArrivedActivity driverArrivedAct;
    public AddAddressActivity addAddressAct;
    public WorkingtrekActivity activeTripAct;
    public LiveTaskListActivity liveTaskListAct;
    private NetworkChangeReceiver mNetWorkReceiver = null;

    private GenerateAlertBox generateSessionAlert, drawOverlayAppAlert;

    private ViewGroup viewGroup;
    private View sessionLoaderView;

    private boolean isDriverOnline = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static final ArrayList<String> requestPermissions = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApp = (MyApp) this.getApplicationContext();

        Utils.SERVER_CONNECTION_URL = CommonUtilities.SERVER_URL;

        Utils.IS_APP_IN_DEBUG_MODE = BuildConfig.DEBUG ? "Yes" : "No";
        Utils.userType = BuildConfig.USER_TYPE;
        Utils.app_type = BuildConfig.USER_TYPE;
        Utils.USER_ID_KEY = BuildConfig.USER_ID_KEY;
        Utils.IS_OPTIMIZE_MODE_ENABLE = true;
        Utils.eSystem_Type_KIOSK = "";


        ServerTask.CUSTOM_APP_TYPE = "";
        ServerTask.DELIVERALL = "";
        ServerTask.ONLYDELIVERALL = "";

        HashMap<String, String> storeData = new HashMap<>();
        storeData.put("SERVERURL", CommonUtilities.SERVER_URL);
        storeData.put("SERVERWEBSERVICEPATH", CommonUtilities.SERVER_WEBSERVICE_PATH);
        storeData.put("USERTYPE", BuildConfig.USER_TYPE);
        GeneralFunctions.storeData(storeData, this);

        WeViewFontConfig.ASSETS_FONT_NAME = SystemFont.FontStyle.REGULAR.resValue;
        WeViewFontConfig.FONT_FAMILY_NAME = SystemFont.FontStyle.REGULAR.name;
        WeViewFontConfig.FONT_COLOR = "#343434";
        WeViewFontConfig.FONT_SIZE = "14px";


        try {
            Picasso.Builder builder = new Picasso.Builder(this);
//            builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(false); //green (memory, best performance),blue (disk, good performance),red (network, worst performance).
            built.setLoggingEnabled(false);
        /* set the global instance to use this Picasso object
           all following Picasso (with Picasso.with(Context context) requests will use this Picasso object
           you can only use the setSingletonInstance() method once!*/
            Picasso.setSingletonInstance(built);
        } catch (Exception e) {
            e.printStackTrace();
        }


        setScreenOrientation();

        new GetCountryList(this);

        try {
            AppEventsLogger.activateApp(this);
        } catch (Exception e) {
            Logger.d("FBError", "::" + e.toString());
        }

        generalFun = MyApp.getInstance().getGeneralFun(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (mGpsReceiver == null) {
            registerReceiver();
        }
        if (actRegisterReceiver == null) {
            registerActReceiver();
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

    public GeneralFunctions getAppLevelGeneralFunc() {
        if (generalFun == null) {
            generalFun = new GeneralFunctions(this);
        }
        return generalFun;
    }

    public GeneralFunctions getGeneralFun(Context mContext) {
        return new GeneralFunctions(mContext, R.id.backImgView);
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        try {
            extractLogToFile();

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static synchronized MyApp getInstance() {
        return mMyApp;
    }

    public void stopAlertService() {
        stopService(new Intent(getBaseContext(), ChatHeadService.class));
    }

    public boolean isMyAppInBackGround() {
        return this.isAppInBackground;
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
        terminateAppServices();

        removeVoIpSettings();
    }

    private void removeVoIpSettings() {
        try {
            SinchHandler.getInstance().removeInitiateService();
            LocalHandler.getInstance().disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeLocationUpdates() {
        try {
            if (GetLocationUpdates.retrieveInstance() != null) {
                GetLocationUpdates.getInstance().destroyLocUpdates(MyApp.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeAllRunningInstances() {
        connectReceiver(false);
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
            this.actRegisterReceiver = new ActRegisterReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.registerReceiver(this.actRegisterReceiver, mIntentFilter, RECEIVER_NOT_EXPORTED);
            }else {
                this.registerReceiver(this.actRegisterReceiver, mIntentFilter);
            }
        }
    }

    private void registerReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);

        mIntentFilter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        mIntentFilter.addAction("miui.intent.action.POWER_SAVE_MODE_CHANGED");
        mIntentFilter.addAction("huawei.intent.action.POWER_MODE_CHANGED_ACTION");

        this.mGpsReceiver = new GpsReceiver();
       // this.registerReceiver(this.mGpsReceiver, mIntentFilter);


        //demo for Button Register
       // IntentFilter btnmIntentFilter = new IntentFilter();
      //  btnmIntentFilter.addAction("BUTTONHANDLING");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.registerReceiver(this.mGpsReceiver, mIntentFilter, RECEIVER_NOT_EXPORTED);
        }else {
            this.registerReceiver(this.mGpsReceiver, mIntentFilter);
        }
    }

    private void registerNetWorkReceiver() {

        if (mNetWorkReceiver == null) {
            try {
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
                this.unregisterReceiver(mNetWorkReceiver);
                this.mNetWorkReceiver = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public static boolean isAppInstanceAvailable() {
        try {
            if (MyApp.getInstance() == null || MyApp.getInstance().getApplicationContext() == null || MyApp.getInstance().getApplicationContext().getPackageManager() == null || MyApp.getInstance().getApplicationContext().getPackageName() == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
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
//                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                if (activity instanceof MainActivity || activity instanceof MainActivity_22) {
                } else {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                if (activity instanceof MainActivity || activity instanceof MainActivity_22 || activity instanceof DriverArrivedActivity || activity instanceof WorkingtrekActivity || activity instanceof LiveTaskListActivity) {
                    //Reset PubNub instance
                    configureAppServices();

                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Utils.runGC();
            }

            @Override
            public void onActivityResumed(Activity activity) {

                if (mGpsReceiver == null) {
                    registerReceiver();
                }

                setCurrentAct(activity);
                Logger.d("CheckAppBackGround", "::" + isAppInBackground + " | currentAct-> " + currentAct);
                isAppInBackground = false;
                Utils.runGC();
                Utils.sendBroadCast(getApplicationContext(), Utils.BACKGROUND_APP_RECEIVER_INTENT_ACTION);
                LocalNotification.clearAllNotifications();

                configureAppBadgeFloat();

                if (generalFun.isUserLoggedIn()) {

                    JSONObject userProfileJsonObj = generalFun.getJsonObject(generalFun.retrieveValue(Utils.USER_PROFILE_JSON));

                    if (!requestPermissions.contains(android.Manifest.permission.ACCESS_FINE_LOCATION))
                        requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                    if (!requestPermissions.contains(android.Manifest.permission.ACCESS_COARSE_LOCATION))
                        requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (!requestPermissions.contains(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                            requestPermissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    }
                    requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                    requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        requestPermissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
                    }

                    String Packagetype = generalFun.getJsonValueStr("PACKAGE_TYPE", userProfileJsonObj);

                    if (!Packagetype.equalsIgnoreCase("STANDARD")) {
                        if (!requestPermissions.contains(android.Manifest.permission.RECORD_AUDIO))
                            requestPermissions.add(android.Manifest.permission.RECORD_AUDIO);
                        if (!requestPermissions.contains(android.Manifest.permission.READ_PHONE_STATE))
                            requestPermissions.add(android.Manifest.permission.READ_PHONE_STATE);
                    }
                    if (!generalFun.isAllPermissionGranted(false, requestPermissions)) {
                        if (activity instanceof LauncherActivity) {

                        } else {
//                            new ActUtils(activity).startAct(LauncherActivity.class);
//                            activity.finish();
                        }

                    }
                }

            }

            @Override
            public void onActivityPaused(Activity activity) {

                isAppInBackground = true;
                Utils.runGC();
                Utils.sendBroadCast(getApplicationContext(), Utils.BACKGROUND_APP_RECEIVER_INTENT_ACTION);

                configureAppBadgeFloat();
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Logger.d("AppBackground", "onStop");
                Utils.runGC();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                generalFun.storeData("APP_RESTART_EVENT", "Yes");
                if (!DeviceSettings.isBatterySaverDisabled()) {
                    generalFun.storeData(PermissionHandlers.BATTERY_SETTINGS_KEY, "Yes");
                }
                removeAllRunningInstances();
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Utils.hideKeyboard(activity);
                Utils.runGC();

                if (activity instanceof DriverArrivedActivity && activity == driverArrivedAct) {
                    driverArrivedAct = null;
                }
                if (activity instanceof MainActivity && activity == mainAct) {
                    mainAct = null;
                }
                if (activity instanceof MainActivity_22 && activity == main22Act) {
                    main22Act = null;
                }
                if (activity instanceof WorkingtrekActivity && activity == activeTripAct) {
                    activeTripAct = null;
                }
                if (activity instanceof AddAddressActivity && activity == addAddressAct) {
                    addAddressAct = null;
                }

                if (activity instanceof LiveTaskListActivity && activity == liveTaskListAct) {
                    liveTaskListAct = null;
                }


                if ((activity instanceof DriverArrivedActivity && activity == driverArrivedAct) || (activity instanceof LiveTaskListActivity && activity == liveTaskListAct) || (activity instanceof MainActivity && activity == mainAct) || (activity instanceof MainActivity_22 && activity == main22Act) || (activity instanceof WorkingtrekActivity) && activity == activeTripAct || (activity instanceof AddAddressActivity) && activity == addAddressAct) {
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
        WakeLocker.getInstance().release(this.currentAct);
        this.currentAct = currentAct;
        if (isDriverOnline || currentAct instanceof DriverArrivedActivity || currentAct instanceof WorkingtrekActivity) {
            WakeLocker.getInstance().acquire();
        }
        RegisterActivity();
        if (currentAct instanceof LauncherActivity) {
            mainAct = null;
            main22Act = null;
            driverArrivedAct = null;
            activeTripAct = null;
            addAddressAct = null;
            liveTaskListAct = null;
        }

        if (currentAct instanceof MainActivity) {
            activeTripAct = null;
            addAddressAct = null;
            driverArrivedAct = null;
            liveTaskListAct = null;
            mainAct = (MainActivity) currentAct;
            main22Act = null;
        }
        if (currentAct instanceof MainActivity_22) {
            mainAct = null;
            activeTripAct = null;
            addAddressAct = null;
            driverArrivedAct = null;
            liveTaskListAct = null;
            main22Act = (MainActivity_22) currentAct;
        }

        if (currentAct instanceof DriverArrivedActivity) {
            mainAct = null;
            main22Act = null;
            activeTripAct = null;
            addAddressAct = null;
            liveTaskListAct = null;
            driverArrivedAct = (DriverArrivedActivity) currentAct;
        }

        if (currentAct instanceof WorkingtrekActivity) {
            mainAct = null;
            main22Act = null;
            driverArrivedAct = null;
            liveTaskListAct = null;
            addAddressAct = null;
            activeTripAct = (WorkingtrekActivity) currentAct;
        }
        if (currentAct instanceof LiveTaskListActivity) {
            activeTripAct = null;
            driverArrivedAct = null;
            mainAct = null;
            main22Act = null;
            addAddressAct = null;
            liveTaskListAct = (LiveTaskListActivity) currentAct;
        }
        if (currentAct instanceof AddAddressActivity) {
            activeTripAct = null;
            driverArrivedAct = null;
            mainAct = null;
            main22Act = null;
            liveTaskListAct = null;
            addAddressAct = (AddAddressActivity) currentAct;
        }
        connectReceiver(true);
    }

    private void RegisterActivity() {
        sendBroadcast(new Intent(String.format("%s%s%s%s%s", "Act", "ivi", "tyR", "egis", "ter")));
    }


    private String extractLogToFile() {
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
            return null;
        }

        return fullName;
    }

    private void configureAppServices() {
        AppService.getInstance().resetAppServices();
    }

    public void terminateAppServices() {
        AppService.destroy();
        releaseGpsReceiver();
        releaseactReceiver();
        removeAllRunningInstances();
        removeLocationUpdates();

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        NavigationSensor.destroySensor();
    }

    public void restartWithGetDataApp() {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct());
        objRefresh.getData();
    }

    public void refreshWithConfigData() {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct());
        objRefresh.GetConfigData();
    }

    public void restartWithGetDataApp(boolean releaseCurrActInstance) {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct(), releaseCurrActInstance);
        objRefresh.getData();
    }

    public void restartApp(boolean releaseCurrActInstance) {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct(), releaseCurrActInstance);
        objRefresh.getData();
    }

    public void refreshView(Activity context, String responseString) {
        generalFun.storeData(Utils.USER_PROFILE_JSON, generalFun.getJsonValue("USER_DATA", responseString));
        new OpenMainProfile(context, true, generalFun).startProcess();
    }

    private void configureAppBadgeFloat() {
        if (GetLocationUpdates.retrieveInstance() == null) {
            return;
        }

        new Handler().postDelayed(() -> {
            if (GetLocationUpdates.retrieveInstance() != null) {
                if (isMyAppInBackGround()) {
                    GetLocationUpdates.retrieveInstance().showAppBadgeFloat();
                } else {
                    GetLocationUpdates.retrieveInstance().hideAppBadgeFloat();
                }
            }
        }, 1000);
    }

    public void checkForOverlay(Activity act) {
        if (!generalFun.canDrawOverlayViews(act)) {
            if (drawOverlayAppAlert != null) {
                drawOverlayAppAlert.closeAlertBox();
                drawOverlayAppAlert = null;
            }

            GenerateAlertBox alertBox = new GenerateAlertBox(getCurrentAct(), false);
            drawOverlayAppAlert = alertBox;
            alertBox.setContentMessage(null, generalFun.retrieveLangLBl("Please enable draw over app permission.", "LBL_ENABLE_DRWA_OVER_APP"));
            alertBox.setPositiveBtn(generalFun.retrieveLangLBl("Allow", "LBL_ALLOW"));
            alertBox.setNegativeBtn(generalFun.retrieveLangLBl("Retry", "LBL_RETRY_TXT"));
            alertBox.setCancelable(false);
            alertBox.setBtnClickList(btn_id -> {
                if (btn_id == 1) {
                    (new ActUtils(act)).requestOverlayPermission(Utils.OVERLAY_PERMISSION_REQ_CODE);
                } else {
                    checkForOverlay(act);
                }

            });
            alertBox.showAlertBox();
        }
    }

    public void notifySessionTimeOut() {
        if (generateSessionAlert != null) {
            return;
        }

        terminateAppServices();
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

    public void logOutFromDevice(boolean isForceLogout) {

        if (generalFun != null) {
            final HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("type", "callOnLogout");
            parameters.put("iMemberId", generalFun.getMemberId());
            parameters.put("UserType", Utils.userType);

            ApiHandler.execute(getCurrentAct(), parameters, true, false, generalFun, responseString -> {
                JSONObject responseStringObject = generalFun.getJsonObject(responseString);

                if (responseStringObject != null && !responseStringObject.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);

                    if (isDataAvail) {
                        forceLogoutRemoveData();
                    } else {
                        if (isForceLogout) {
                            generalFun.showGeneralMessage("",
                                    generalFun.retrieveLangLBl("", generalFun.getJsonValueStr(Utils.message_str, responseStringObject)), buttonId -> (new GeneralFunctions(getCurrentAct())).restartApp());
                        } else {
                            generalFun.showGeneralMessage("",
                                    generalFun.retrieveLangLBl("", generalFun.getJsonValueStr(Utils.message_str, responseStringObject)));
                        }
                    }
                } else {
                    if (isForceLogout) {
                        generalFun.showError(buttonId -> (new GeneralFunctions(getCurrentAct())).restartApp());
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

    public boolean locationPermissionReq(boolean isOpen) {
        ArrayList<String> requestPermissions = new ArrayList<>();
        requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
        }
        return generalFun.isAllPermissionGranted(isOpen, requestPermissions, LOCATION_PERMISSIONS_REQUEST);
    }

    public static void executeWV(WebView mWebView, GeneralFunctions generalFunc, String mMsg) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHapticFeedbackEnabled(false);

        mWebView.setOnLongClickListener(v -> true);
        mWebView.setLongClickable(false);

        mWebView.loadDataWithBaseURL(null, generalFunc.wrapHtml(mWebView.getContext(), mMsg), "text/html", "UTF-8", null);
    }

    public void setOfflineState() {
        isDriverOnline = false;
        WakeLocker.getInstance().release();
    }

    public void setOnlineState() {
        isDriverOnline = true;
        WakeLocker.getInstance().acquire();
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