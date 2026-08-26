package com.general.files;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.multixpro.store.SearchPickupLocationActivity;
import com.multixpro.store.TrackOrderActivity;

public class GpsReceiver extends BroadcastReceiver {

    Context context;
    MyApp mApplication;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        mApplication = ((MyApp) context.getApplicationContext());
        checkGPSSettings();
    }

    private void checkGPSSettings() {
        Activity currentActivity = MyApp.getInstance().getCurrentAct();
        if (currentActivity != null) {
            if (currentActivity instanceof TrackOrderActivity || currentActivity instanceof SearchPickupLocationActivity) {
                ViewGroup viewGroup = currentActivity.findViewById(android.R.id.content);
                handleGPSView(currentActivity, viewGroup);
            }
        }
    }

    private void handleGPSView(Activity activity, ViewGroup viewGroup) {
        try {
            //OpenNoLocationView.getInstance(activity, viewGroup).configView(true, MyApp.getInstance().checkGPS());
            OpenNoLocationView.getInstance(activity, viewGroup).configView(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}