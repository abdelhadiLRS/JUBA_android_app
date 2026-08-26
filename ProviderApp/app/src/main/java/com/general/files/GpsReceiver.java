package com.general.files;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.view.ViewGroup;

import com.general.PermissionHandlers;
import com.multixpro.provider.MainActivity;
import com.multixpro.provider.MainActivity_22;
import com.utils.Logger;

/**
 * Created by Admin on 23-11-2016.
 */
public class GpsReceiver extends BroadcastReceiver {
    Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Logger.d("IntentAction", "::" + intent.getAction() + "::DATA::" + intent.getData());
//        if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
//            //checkGps(context);
//        }

        PermissionHandlers.getInstance().checkPermissions();
    }

    public void checkGps(Context context) {

//        GeneralFunctions generalFunc = MyApp.getInstance().getGeneralFun(context);
        /*boolean foregroud = false;
        try {
            foregroud = new ForegroundCheckTask().execute(context).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (foregroud == true && generalFunc.isLocationEnabled() == false && isApplicationBroughtToBackground() == false) {
            restartApp();
        }
        if (foregroud == true && generalFunc.isLocationEnabled() == true && isApplicationBroughtToBackground() == false) {
            restartApp();
        }*/


        checkGPSSettings();
    }

    private void checkGPSSettings() {
        Activity currentActivity = MyApp.getInstance().getCurrentAct();

        if (currentActivity != null) {

            if (MyApp.getInstance().driverArrivedAct == null && MyApp.getInstance().activeTripAct == null && MyApp.getInstance().addAddressAct == null) {
                MainActivity mainAct = MyApp.getInstance().mainAct;
                MainActivity_22 main22Act = null;
                if (MyApp.getInstance().getCurrentAct() instanceof MainActivity_22) {
                    main22Act = MyApp.getInstance().main22Act;
                }

                if (mainAct != null) {
                    ViewGroup viewGroup = (ViewGroup) mainAct.findViewById(android.R.id.content);
                    handleGPSView(mainAct, viewGroup);
                }
                if (main22Act != null) {
                    ViewGroup viewGroup = (ViewGroup) main22Act.findViewById(android.R.id.content);
                    if (!main22Act.iswalletFragemnt && !main22Act.isbookingFragemnt && !main22Act.isProfileFragment) {
                        handleGPSView(main22Act, viewGroup);
                    }
                }
            } else {
                Activity finalActivity = currentActivity;
                if (MyApp.getInstance().activeTripAct != null) {
                    finalActivity = MyApp.getInstance().activeTripAct;
                } else if (MyApp.getInstance().driverArrivedAct != null) {
                    finalActivity = MyApp.getInstance().driverArrivedAct;
                } else if (MyApp.getInstance().addAddressAct != null) {
                    finalActivity = MyApp.getInstance().addAddressAct;
                }
                ViewGroup viewGroup = (ViewGroup) finalActivity.findViewById(android.R.id.content);
                handleGPSView(finalActivity, viewGroup);
            }
        }
    }


    public boolean checkBatteryOptimized() {
        boolean isOptimize = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            PowerManager powerManager = (PowerManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(MyApp.getInstance().getApplicationContext().getPackageName())) {
                isOptimize = true;

            }
        }
        return isOptimize;
    }

    private void handleGPSView(Activity activity, ViewGroup viewGroup) {

        if (checkBatteryOptimized()) {
            return;
        }
        try {
            Logger.d("handleGPSView", "::called::" + activity);
            Logger.d("configView", "::handleGPSView::" + activity);
            OpenNoLocationView.getInstance(activity, viewGroup).configView(false);
        } catch (Exception e) {
            Logger.d("handleGPSView", "::called" + e.toString());

        }
    }

}
