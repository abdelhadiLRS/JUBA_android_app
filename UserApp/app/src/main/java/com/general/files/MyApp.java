package com.general.files;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.multidex.MultiDex;

import com.facebook.appevents.AppEventsLogger;
import com.fontanalyzer.SystemFont;
import com.general.call.LocalHandler;
import com.general.call.SinchHandler;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.huawei.hms.push.HmsMessaging;
import com.multixpro.user.AccountverificationActivity;
import com.multixpro.user.AddAddressActivity;
import com.multixpro.user.AdditionalChargeActivity;
import com.multixpro.user.AppRestrictedActivity;
import com.multixpro.user.BuildConfig;
import com.multixpro.user.CarWashBookingDetailsActivity;
import com.multixpro.user.CommonDeliveryTypeSelectionActivity;
import com.multixpro.user.LauncherActivity;
import com.multixpro.user.MainActivity;
import com.multixpro.user.NetworkChangeReceiver;
import com.multixpro.user.OnGoingTripDetailsActivity;
import com.multixpro.user.PaymentWebviewActivity;
import com.multixpro.user.R;
import com.multixpro.user.RentalDetailsActivity;
import com.multixpro.user.RideDeliveryActivity;
import com.multixpro.user.SearchPickupLocationActivity;
import com.multixpro.user.UberXActivity;
import com.multixpro.user.UberXHomeActivity;
import com.multixpro.user.deliverAll.FoodDeliveryHomeActivity;
import com.multixpro.user.deliverAll.TrackOrderActivity;
import com.service.handler.ApiHandler;
import com.service.handler.AppService;
import com.service.server.ServerTask;
import com.utils.CommonUtilities;
import com.utils.IntentAction;
import com.utils.Logger;
import com.utils.Utils;
import com.utils.WeViewFontConfig;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Admin on 28-06-2016.
 */
public class MyApp extends Application {

    private static MyApp mMyApp;

    public static boolean isJSEnabled = true;

    public static synchronized MyApp getInstance() {
        return mMyApp;
    }

    private GeneralFunctions generalFun;
    private boolean isAppInBackground = true;

    private GpsReceiver mGpsReceiver;
    private ActRegisterReceiver actRegisterReceiver;
    private LocalNotification.ActionReceiver receiver;
    private LocalNotification localNotification = null;
    private NetworkChangeReceiver mNetWorkReceiver = null;

    private Activity currentAct = null;

    public MainActivity mainAct;
    public UberXActivity uberXAct;
    public UberXHomeActivity uberXHomeAct;
    public CommonDeliveryTypeSelectionActivity commonDeliveryAct;
    public CarWashBookingDetailsActivity carWashAct;
    public OnGoingTripDetailsActivity onGoingTripDetailsAct = null;
    public TrackOrderActivity trackOrderActivity;
    public RideDeliveryActivity rideDeliveryActivity;
    public AdditionalChargeActivity additionalChargesAct = null;

    private GenerateAlertBox generateSessionAlert, drawOverlayAppAlert;
    private long notification_permission_launch_time = -1;

    private ViewGroup viewGroup;
    private View sessionLoaderView;
    public Location currentLocation;
    public Locale scheduleLocale;

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApp = (MyApp) this.getApplicationContext();
        generalFun = new GeneralFunctions(this);

        GeneralFunctions.GMS_AVAILABLE_OVERRIDE = false;
        GeneralFunctions.IS_GMS_AVAILABLE_DEBUG = false;

        HashMap<String, String> default_params = new HashMap<>();
        default_params.put("HMS_DEVICE", isHMSOnly() ? "Yes" : "No");
        default_params.put("HUAWEI_DEVICE", generalFun.isHmsAvailable() ? "Yes" : "No");
        ServerTask.default_params = default_params;

        Utils.SERVER_CONNECTION_URL = CommonUtilities.SERVER_URL;
        Utils.IS_APP_IN_DEBUG_MODE = "Yes";
        Utils.userType = BuildConfig.USER_TYPE;
        Utils.app_type = BuildConfig.USER_TYPE;
        Utils.USER_ID_KEY = BuildConfig.USER_ID_KEY;
        Utils.IS_OPTIMIZE_MODE_ENABLE = true;
        Utils.eSystem_Type_KIOSK = "";

        //ServerTask.CUS_ENABLE_ADD_PROVIDER_FROM_STORE = "";
        //ServerTask.CUS_ENABLE_TAKE_AWAY = "";
        //ServerTask.CUS_IS_SINGLE_STORE_SELECTION = "";
        //ServerTask.CUS_ENABLE_DELIVERY_PREFERENCE = "";


//        WeViewFontConfig.ASSETS_FONT_NAME = getResources().getString(R.string.system_regular);
        WeViewFontConfig.ASSETS_FONT_NAME = SystemFont.FontStyle.REGULAR.resValue;
        WeViewFontConfig.FONT_FAMILY_NAME = SystemFont.FontStyle.REGULAR.name;
        WeViewFontConfig.FONT_COLOR = "#343434";
        WeViewFontConfig.FONT_SIZE = "14px";

        ServerTask.CUSTOM_APP_TYPE = "";
        ServerTask.CUSTOM_UBERX_PARENT_CAT_ID = "";
        ServerTask.DELIVERALL = "";
        ServerTask.ONLYDELIVERALL = "";
        ServerTask.FOODONLY = "";
        HashMap<String, String> storeData = new HashMap<>();
        try {

            storeData.put("SERVERURL", CommonUtilities.SERVER_URL);
            storeData.put("SERVERWEBSERVICEPATH", CommonUtilities.SERVER_WEBSERVICE_PATH);
            storeData.put("USERTYPE", BuildConfig.USER_TYPE);
            // storeData.put("MYAPP", CheckKeys.setMemberId(BuildConfig.APPLICATION_ID));
            // CheckKeys.setMemberId(BuildConfig.APPLICATION_ID);


        } catch (Exception e) {
            e.printStackTrace();
        }
        GeneralFunctions.storeData(storeData, this);
//        try {
//            Picasso.Builder builder = new Picasso.Builder(this);
////            builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
//            Picasso built = builder.build();
//            built.setIndicatorsEnabled(false);
//            built.setLoggingEnabled(false);
//            Picasso.setSingletonInstance(built);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        setScreenOrientation();

        new GetCountryList(this);


        try {
            AppEventsLogger.activateApp(this);
        } catch (Exception e) {
            Logger.d("FBError", "::" + e);
        }

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("FoodApp.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(config);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (mGpsReceiver == null) {
            registerReceiver();
        }
        if (receiver == null) {
            registerAction();
        }

        if (actRegisterReceiver == null) {
            registerActReceiver();
        }

        if (isHMSOnly()) {
            try {
                com.huawei.hms.maps.MapsInitializer.initialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            HmsMessaging.getInstance(this).setAutoInitEnabled(true);
        }

    }

    public boolean isHMSOnly() {
        if (!generalFun.isGmsAvailable() && generalFun.isHmsAvailable()) {
            return true;
        }
        return false;
    }

    private void clearFile(OutputStreamWriter outputStreamWriter) {
        try {
            PrintWriter writer = new PrintWriter(outputStreamWriter);
            writer.print("");
        } catch (Exception e) {
            Logger.d("ClearFile", ":" + e.getMessage());
        }
    }

    public void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config_test.txt", Context.MODE_PRIVATE));
            clearFile(outputStreamWriter);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.getMessage());
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

    public LocalNotification getLoclaNotificationObj() {
        if (localNotification == null) {
            localNotification = new LocalNotification();
        }
        return localNotification;

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

    public boolean isDrakModeOn() {
        int currentNightMode = MyApp.getInstance().getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            return false;
        } else return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static Realm getRealmInstance() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        return Realm.getInstance(config);
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

    public boolean isMyAppInBackGround() {
        return this.isAppInBackground;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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

        if (generalFun.prefHasKey(Utils.iServiceId_KEY)) {
            generalFun.removeValue(Utils.iServiceId_KEY);
        }

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        removeVoIpSettings();
    }


    private void removePubSub() {
        releaseGpsReceiver();
        releaseactionReceiver();
        releaseactReceiver();
        removeAllRunningInstances();
        AppService.destroy();
    }

    public void refreshWithConfigData() {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct());
        objRefresh.GetConfigData();
    }


    private void releaseGpsReceiver() {
        if (mGpsReceiver != null)
            this.unregisterReceiver(mGpsReceiver);
        this.mGpsReceiver = null;
    }

    private void releaseactionReceiver() {
        Logger.d("ActionReceiver", "::releaseactionReceiver");
        if (receiver != null)
            this.unregisterReceiver(receiver);
        this.receiver = null;
    }

    private void releaseactReceiver() {

        if (actRegisterReceiver != null)
            this.unregisterReceiver(actRegisterReceiver);
        this.actRegisterReceiver = null;
    }


   /* private void registerActReceiver() {
        if (actRegisterReceiver == null) {
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(String.format("%s%s%s%s%s", "Act", "ivi", "tyR", "egis", "ter"));
            this.actRegisterReceiver = new ActRegisterReceiver();
            this.registerReceiver(this.actRegisterReceiver, mIntentFilter);
        }
    } */
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
        this.mGpsReceiver = new GpsReceiver();
        this.registerReceiver(this.mGpsReceiver, mIntentFilter);
    }

  /*  private void registerAction() {
        Logger.d("ActionReceiver", "::registerAction");
        this.receiver = new LocalNotification.ActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentAction.NOTIFICATION_CLICK);
        filter.addAction(IntentAction.NOTIFICATION_CLOSE);
        filter.addAction(IntentAction.NOTIFICATION_VIEW_ORDER);
        filter.addAction(IntentAction.NOTIFICATION_TRACK_ORDER);
        this.registerReceiver(receiver, filter);
    } */

/*        if (actRegisterReceiver == null) {
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(String.format("%s%s%s%s%s", "Act", "ivi", "tyR", "egis", "ter"));
            this.actRegisterReceiver = new ActRegisterReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.registerReceiver(this.actRegisterReceiver, mIntentFilter, RECEIVER_NOT_EXPORTED);
            }else {
                this.registerReceiver(this.actRegisterReceiver, mIntentFilter);
            }
        }
    } */
   private void registerAction() {
        Logger.d("ActionReceiver", "::registerAction");
        this.receiver = new LocalNotification.ActionReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentAction.NOTIFICATION_CLICK);
        filter.addAction(IntentAction.NOTIFICATION_CLOSE);
        filter.addAction(IntentAction.NOTIFICATION_VIEW_ORDER);
        filter.addAction(IntentAction.NOTIFICATION_TRACK_ORDER);
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           this.registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED);
       }else {
           this.registerReceiver(receiver, filter);
       }
    }
    private void removeAllRunningInstances() {
        Logger.e("NetWorkDEMO", "removeAllRunningInstances called");
        connectReceiver(false);
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

    private void RegisterActivity() {
        sendBroadcast(new Intent(String.format("%s%s%s%s%s", "Act", "ivi", "tyR", "egis", "ter")));
    }

    public JSONArray GetStringArray(ArrayList<String> data_waypoints) {


        JSONArray jsonArray = new JSONArray();
        if (data_waypoints != null && data_waypoints.size() > 0) {
            for (int i = 0; i < data_waypoints.size(); i++) {
                jsonArray.put(data_waypoints.get(i));
            }
        }
        return jsonArray;
    }

    public boolean isCurrentActByConfigView() {
        return currentAct instanceof MainActivity
                || currentAct instanceof UberXActivity
                /*|| currentAct instanceof UberXHomeActivity*/
                || currentAct instanceof FoodDeliveryHomeActivity
                /*|| currentAct instanceof ServiceHomeActivity*/
                || currentAct instanceof AddAddressActivity
                || currentAct instanceof SearchPickupLocationActivity
                || currentAct instanceof RideDeliveryActivity
                || currentAct instanceof RentalDetailsActivity;
    }

    public void CheckConfIngView() {
        if (isCurrentActByConfigView()) {

            new Handler(Looper.myLooper()).postDelayed(() -> {
                Logger.e("CheckConfIngView", "::stcheck::" + currentAct);

                ViewGroup viewGroup = currentAct.findViewById(android.R.id.content);

                /*if (currentAct instanceof UberXHomeActivity) {
                    UberXHomeActivity uberXHomeActivity = (UberXHomeActivity) currentAct;
                    if (uberXHomeActivity.homeDaynamic_22_fragment != null
                            || uberXHomeActivity.homeDaynamicFragment != null
                            || uberXHomeActivity.homeFragment != null) {
                        if (uberXHomeActivity.isHomeFrg) {
                            viewGroup = currentAct.findViewById(R.id.MainArea);
                        }
                    }
                } else */
                if (currentAct instanceof RideDeliveryActivity) {
                    viewGroup = currentAct.findViewById(R.id.MainLayout);

                }
                if (isCurrentActByConfigView()) {
                    OpenNoLocationView.getInstance(currentAct, viewGroup).configView(false);
                }
            }, 500);
        }
    }

    private void setScreenOrientation() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Utils.runGC();
                // new activity created; force its orientation to portrait
                try {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activity.setTitle(getResources().getString(R.string.app_name));
                setCurrentAct(activity);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                if (!(activity instanceof LauncherActivity) && !(activity instanceof AppRestrictedActivity) && !(activity instanceof AccountverificationActivity) && generalFun.isUserLoggedIn() && activity.isTaskRoot()) {
                    configureAppServices();
                    //checkForOverlay(activity);
                }

                if (!(activity instanceof LauncherActivity) && !(activity instanceof AppRestrictedActivity) && !(activity instanceof AccountverificationActivity) && generalFun.isUserLoggedIn() && activity.isTaskRoot()) {
                    openNotificationPermission();
                }

                /*if (activity instanceof MainActivity || activity instanceof UberXActivity || activity instanceof CarWashBookingDetailsActivity || (activity instanceof FoodDeliveryHomeActivity) || activity instanceof CommonDeliveryTypeSelectionActivity || activity instanceof ServiceHomeActivity) {
                    //Reset PubNub instance
                    configPuSubInstance();
                }*/

//                if (activity instanceof RatingActivity) {
//                    terminatePuSubInstance();
//                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Utils.runGC();
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Utils.runGC();

                setCurrentAct(activity);

                Logger.d("CheckAppBackGround", "::" + isAppInBackground + " | currentAct-> " + currentAct);
                isAppInBackground = false;

                LocalNotification.clearAllNotifications();

                CheckConfIngView();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Utils.runGC();
                isAppInBackground = true;
                Logger.d("CheckAppBackGround", "::" + isAppInBackground);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Utils.runGC();
            }


            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                /*Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated by this method will be passed to both).*/
                //  removeAllRunningInstances();
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Utils.runGC();
                Utils.hideKeyboard(activity);
                if (activity instanceof UberXActivity && uberXAct == activity) {
                    uberXAct = null;
                }

                if (activity instanceof UberXHomeActivity && uberXHomeAct == activity) {
                    uberXHomeAct = null;
                }

                if (activity instanceof CommonDeliveryTypeSelectionActivity && commonDeliveryAct == activity) {
                    commonDeliveryAct = null;
                }

                if (activity instanceof TrackOrderActivity && trackOrderActivity == activity) {
                    trackOrderActivity = null;
                }
                if (currentAct instanceof CarWashBookingDetailsActivity && currentAct == activity) {
                    carWashAct = null;
                }

                if (activity instanceof MainActivity && mainAct == activity) {
                    Logger.d("CheckMainAct", ":: onActivityDestroyed");
                    mainAct = null;
                }

                if (activity instanceof OnGoingTripDetailsActivity && onGoingTripDetailsAct == activity) {
                    onGoingTripDetailsAct = null;
                }
                if (activity instanceof AdditionalChargeActivity && additionalChargesAct == activity) {
                    additionalChargesAct = null;
                }

//                if (activity.isTaskRoot()) {
//                    terminatePuSubInstance();
//                }
                LocalNotification.clearAllNotifications();

                if ((activity instanceof TrackOrderActivity && trackOrderActivity == activity) || (activity instanceof UberXActivity && uberXAct == activity) || (uberXAct == null && commonDeliveryAct == null && activity instanceof MainActivity && mainAct == activity) || (activity instanceof CommonDeliveryTypeSelectionActivity && commonDeliveryAct == activity)) {
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

    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public String getVersionCode() {
        return BuildConfig.VERSION_CODE + "";
    }

    private void setCurrentAct(Activity currentAct) {
        this.currentAct = currentAct;
        RegisterActivity();

        if (currentAct instanceof LauncherActivity) {
            mainAct = null;
            uberXAct = null;
            uberXHomeAct = null;
            onGoingTripDetailsAct = null;
            additionalChargesAct = null;
        }

        if (currentAct instanceof MainActivity) {
            mainAct = (MainActivity) currentAct;
        }
        if (currentAct instanceof AdditionalChargeActivity) {
            additionalChargesAct = (AdditionalChargeActivity) currentAct;
        }

        if (currentAct instanceof RideDeliveryActivity) {
            rideDeliveryActivity = (RideDeliveryActivity) currentAct;
        }

        if (currentAct instanceof CarWashBookingDetailsActivity) {
            carWashAct = (CarWashBookingDetailsActivity) currentAct;
        }

        if (currentAct instanceof TrackOrderActivity) {
            trackOrderActivity = (TrackOrderActivity) currentAct;
        }

        if (currentAct instanceof UberXActivity) {
            uberXAct = (UberXActivity) currentAct;
            mainAct = null;
            additionalChargesAct = null;
        }
        if (currentAct instanceof UberXHomeActivity) {
            uberXHomeAct = (UberXHomeActivity) currentAct;
            mainAct = null;
            additionalChargesAct = null;
        }

        if (currentAct instanceof OnGoingTripDetailsActivity) {
            onGoingTripDetailsAct = (OnGoingTripDetailsActivity) currentAct;
        }

        connectReceiver(true);
    }

    private void configureAppServices() {
        AppService.getInstance().resetAppServices();
        ConfigPassengerTripStatus.getInstance().startUserStatusUpdateTask();
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

    public void refreshData() {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getApplicationContext());
        objRefresh.getLatestDataOnly();
    }

    public void restartWithGetDataApp(String tripId) {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct(), tripId);
        objRefresh.getData();
    }

    public void refreshView(Activity context, String responseString) {
        storeUserData(responseString);
        new OpenMainProfile(context, "", false, generalFun).startProcess();
    }

    public void storeUserData(String responseString) {
        if (!responseString.trim().equalsIgnoreCase("")) {
            generalFun.storeData(Utils.USER_PROFILE_JSON, generalFun.getJsonValue("USER_DATA", responseString));
        }
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

    public void logOutFromDevice(boolean isForceLogout) {

        if (generalFun != null) {
            final HashMap<String, String> parameters = new HashMap<>();

            parameters.put("type", "callOnLogout");
            parameters.put("iMemberId", generalFun.getMemberId());
            parameters.put("UserType", Utils.userType);

            ApiHandler.execute(getCurrentAct(), parameters, true, false, generalFun, responseString -> {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                    if (isDataAvail) {

                        if (getCurrentAct() instanceof MainActivity) {
                            ((MainActivity) getCurrentAct()).releaseScheduleNotificationTask();
                        }

                        forceLogoutRemoveData();
                    } else {
                        if (isForceLogout) {
                            generalFun.showGeneralMessage("",
                                    generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)), buttonId -> (MyApp.getInstance().getGeneralFun(getCurrentAct())).restartApp());
                        } else {
                            generalFun.showGeneralMessage("",
                                    generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)));
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
        generalFun.storeData("SERVICE_HOME_DATA", "");
        generalFun.storeData("SERVICE_HOME_DATA_23", "");
        generalFun.logOutUser(MyApp.this);
        CommonUtilities.ageRestrictServices.clear();
        generalFun.restartApp();
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

    public boolean checkMicWithStorePermission(GeneralFunctions generalFunc, boolean openDialog) {
        ArrayList<String> requestPermissions = new ArrayList<>();
        requestPermissions.add(Manifest.permission.RECORD_AUDIO);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions.add(Manifest.permission.READ_MEDIA_AUDIO);
            } else {
                requestPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return generalFunc.isAllPermissionGranted(openDialog, requestPermissions, 154);
    }

    public void showOutsatandingdilaog(View view) {
        String userProfileJson = generalFun.retrieveValue(Utils.USER_PROFILE_JSON);
        if (userProfileJson != null && generalFun.getJsonValue("fOutStandingAmount", userProfileJson) != null
                && GeneralFunctions.parseFloatValue(0, generalFun.getJsonValue("fOutStandingAmount", userProfileJson)) > 0) {
            Snackbar snackbar = Snackbar
                    .make(view, generalFun.getJsonValue("PaymentPendingMsg", userProfileJson), Snackbar.LENGTH_INDEFINITE)
                    .setAction(generalFun.retrieveLangLBl("", "LBL_BTN_PAYMENT_TXT"), view1 -> {
                        Bundle bn = new Bundle();
                        bn.putString("url", generalFun.getJsonValue("OUTSTANDING_PAYMENT_URL", userProfileJson));
                        bn.putBoolean("handleResponse", true);
                        bn.putBoolean("isBack", false);
                        bn.putBoolean("isApiCall", true);
                        new ActUtils(getCurrentAct()).startActWithData(PaymentWebviewActivity.class, bn);
                    });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.setTextMaxLines(10);
            snackbar.setDuration(8000);

            snackbar.show();

        }

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

    public void updateLangForAllServiceType(@NonNull GeneralFunctions generalFunc, @NonNull String message) {
        JSONArray serviceArrayLbl = generalFunc.getJsonArray("ServiceCategories", message);
        if (serviceArrayLbl != null && serviceArrayLbl.length() > 0) {
            ArrayList<String> serviceMap = new ArrayList<>();
            for (int i = 0; i < serviceArrayLbl.length(); i++) {
                JSONObject serviceObj = generalFunc.getJsonObject(serviceArrayLbl, i);
                serviceMap.add(generalFunc.getJsonValue("iServiceId", serviceObj.toString()));
            }
            new GetUserLanguagesForAllServiceType(MyApp.getInstance().getApplicationContext(), generalFunc, StringUtils.join(serviceMap, ","));
        }

    }


    //use for execute local store for Uberx & Bidding all sub Category list

    /**
     * @param generalFunc is object of GeneralFunction Class
     * @param message     is responseString is used for get all category Id
     * @param latitude    is current address of latitude
     * @param longitude   is current address of longitude  (used fo location wise category)
     * @param isBidding   is request for bidding or not
     */
    public void updateSubcategoryForAllCategoryType(@NonNull GeneralFunctions generalFunc, @NonNull String message, String latitude, String longitude, Boolean isBidding) {
        JSONArray serviceArrayLbl = generalFunc.getJsonArray("SubCategories", message);
        if (serviceArrayLbl != null && serviceArrayLbl.length() > 0) {
            ArrayList<String> serviceMap = new ArrayList<>();
            for (int i = 0; i < serviceArrayLbl.length(); i++) {
                JSONObject serviceObj = generalFunc.getJsonObject(serviceArrayLbl, i);
                if (isBidding)
                    serviceMap.add(generalFunc.getJsonValue("iBiddingId", serviceObj.toString()));
                else
                    serviceMap.add(generalFunc.getJsonValue("iVehicleCategoryId", serviceObj.toString()));

            }
            new GetSubCategoryDataAllCategoryType(MyApp.getInstance().getApplicationContext(), generalFunc, StringUtils.join(serviceMap, ","), latitude, longitude, isBidding);
        }

    }

    public void updatedeliverAllcategoryType(@NonNull GeneralFunctions generalFunc, @NonNull String message, String latitude, String longitude) {
        JSONArray serviceArrayLbl = generalFunc.getJsonArray("SubCategories", message);
        if (serviceArrayLbl != null && serviceArrayLbl.length() > 0) {
            ArrayList<String> serviceMap = new ArrayList<>();
            for (int i = 0; i < serviceArrayLbl.length(); i++) {
                JSONObject serviceObj = generalFunc.getJsonObject(serviceArrayLbl, i);

                serviceMap.add(generalFunc.getJsonValue("iServiceId", serviceObj.toString()));

            }
            new GetDeliverAllCategoryDataType(MyApp.getInstance().getApplicationContext(), generalFunc, StringUtils.join(serviceMap, ","), latitude, longitude);
        }

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