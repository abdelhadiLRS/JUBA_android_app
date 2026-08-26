package com.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.general.PermissionHandlers;
import com.general.files.MyApp;

public class DeviceSettings {
    public static boolean isDeviceGPSEnabled() {
        int locationMode = 0;
        if (MyApp.getInstance().getCurrentAct() == null) {
            return false;
        }
        try {
            locationMode = Settings.Secure.getInt(MyApp.getInstance().getCurrentAct().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        final LocationManager manager = (LocationManager) MyApp.getInstance().getCurrentAct().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return locationMode != Settings.Secure.LOCATION_MODE_OFF && statusOfGPS;
    }

    public static boolean isForegroundLocationEnabled() {
        return (ActivityCompat.checkSelfPermission(MyApp.getInstance().getCurrentAct(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApp.getInstance().getCurrentAct(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

    }

    public static boolean isBackgroundLocationEnabled() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || (ActivityCompat.checkSelfPermission(MyApp.getInstance().getCurrentAct(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isNotificationEnable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true;
        }
        return NotificationManagerCompat.from(MyApp.getInstance().getCurrentAct()).areNotificationsEnabled();
    }

    public static boolean isDrawOverlayEnable() {
        return MyApp.getInstance().getAppLevelGeneralFunc().canDrawOverlayViews(MyApp.getInstance().getCurrentAct());
    }

    public static boolean isBatterySaverDisabled() {
        if (MyApp.getInstance().getAppLevelGeneralFunc().retrieveValue(PermissionHandlers.BATTERY_SETTINGS_KEY).equalsIgnoreCase("Yes")) {
            return false;
        }
        return isBatteryOptimizationsEnable() && !isPowerSaveMode();
    }

    public static boolean isBatteryOptimizationsEnable() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                String pkg = MyApp.getInstance().getApplicationContext().getPackageName();
                PowerManager pm = MyApp.getInstance().getApplicationContext().getSystemService(PowerManager.class);
                return pm.isIgnoringBatteryOptimizations(pkg);
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPowerSaveMode() {
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            return isPowerSaveModeHuaweiXiaomi(MyApp.getInstance().getApplicationContext());
        } else if (Build.MANUFACTURER.equalsIgnoreCase("Huawei")) {
            return isPowerSaveModeHuawei(MyApp.getInstance().getApplicationContext());
        } else {
            return isPowerSaveModeAndroid(MyApp.getInstance().getApplicationContext());
        }
    }

    private static Boolean isPowerSaveModeAndroid(Context context) {
        boolean isPowerSaveMode = false;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) isPowerSaveMode = pm.isPowerSaveMode();
        return isPowerSaveMode;
    }

    public static boolean isPowerSaveModeHuaweiXiaomi(Context context) {
        try {
            int value = android.provider.Settings.System.getInt(MyApp.getInstance().getApplicationContext().getContentResolver(), "POWER_SAVE_MODE_OPEN");
            return (value == 1);
        } catch (Settings.SettingNotFoundException e) {
            return isPowerSaveModeAndroid(context);
        }
    }

    private static Boolean isPowerSaveModeHuawei(Context context) {
        try {
            int value = android.provider.Settings.System.getInt(context.getContentResolver(), "SmartModeStatus");
            return (value == 4);
        } catch (Settings.SettingNotFoundException e) {
            return isPowerSaveModeAndroid(context);
        }
    }
}