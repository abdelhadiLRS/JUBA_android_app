package com.general.files;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.multixpro.store.ThermalPrintSettingActivity;

public class BTReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
            MyApp.getInstance().closeBtSocket();
            if (MyApp.getInstance().getCurrentAct() instanceof ThermalPrintSettingActivity) {
                ThermalPrintSettingActivity thermalPrintSettingActivity = (ThermalPrintSettingActivity) MyApp.getInstance().getCurrentAct();
                thermalPrintSettingActivity.disConnectTxt.performClick();
            }
        } else if (intent.getAction().equalsIgnoreCase("ACTION_ACL_DISCONNECTED")) {
            MyApp.getInstance().closeBtSocket();
        }
    }
}