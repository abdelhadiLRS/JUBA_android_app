package com.general.files;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;

import com.utils.DeviceSettings;
import com.utils.Logger;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 27-06-2016.
 */

public class GetLocationUpdates implements LocationUpdateService.LocationUpdates {

    private final String TAG = GetLocationUpdates.class.getSimpleName();
    Location mLocation;
    private static GetLocationUpdates instance;

    boolean mServiceBound = false;

    LocationUpdateService locUpdateService;

    HashMap<Object, LocationUpdatesListener> listOfListener = new HashMap<>();
    Intent locUpdateServiceIntent;

    boolean isTripStarted = false;
    boolean isStartLocationStorage = false;
    String iTripId = "";

    public static GetLocationUpdates getInstance() {
        Logger.d("GetLocationUpdates", "::getInstance");
        if (instance == null) {
            Logger.d("GetLocationUpdates", "::Create");
            instance = new GetLocationUpdates();
        }
        return instance;
    }

    public static GetLocationUpdates retrieveInstance() {
        return instance;
    }


    public GetLocationUpdates() {
        if (!MyApp.isAppInstanceAvailable() || !DeviceSettings.isDeviceGPSEnabled()) {
            Logger.d("GetLocationUpdates", "::isAppInstanceAvailable");
            return;
        }

        locUpdateServiceIntent = new Intent(MyApp.getInstance().getApplicationContext(), LocationUpdateService.class);

        locUpdateServiceIntent = new ActUtils(MyApp.getInstance().getApplicationContext()).startForegroundService(LocationUpdateService.class);
        MyApp.getInstance().getApplicationContext().bindService(locUpdateServiceIntent, mConnection, Context.BIND_AUTO_CREATE);


    }

    protected void showAppBadgeFloat() {
        if (locUpdateService == null) {
            return;
        }
        locUpdateService.showAppBadgeFloat(null);
    }

    protected void hideAppBadgeFloat() {
        if (locUpdateService == null) {
            return;
        }
        locUpdateService.hideAppBadgeFloat();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdateService.LocUpdatesBinder locUpdatesBinder = (LocationUpdateService.LocUpdatesBinder) service;

            locUpdateService = locUpdatesBinder.getService();
            locUpdateService.createLocationRequest(Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, GetLocationUpdates.this);

            locUpdateService.configureDriverLocUpdates(isStartLocationStorage, isTripStarted, iTripId);

            mServiceBound = true;
        }
    };

    public void setTripStartValue(boolean isStartLocationStorage, boolean isTripStarted, String iTripId) {
        this.isStartLocationStorage = isStartLocationStorage;
        this.isTripStarted = isTripStarted;
        this.iTripId = iTripId;
        if (locUpdateService != null) {
            locUpdateService.configureDriverLocUpdates(isStartLocationStorage, isTripStarted, iTripId);
        }
    }

    public ArrayList<Location> getListOfTripLocations() {
        ArrayList<Location> listOfTripLoc = new ArrayList<>();
        if (locUpdateService != null) {
            listOfTripLoc.addAll(locUpdateService.getListOfTripLocations());
        }
        return listOfTripLoc;
    }

    public Location getLastLocation() {
        if (mLocation == null && locUpdateService != null) {
            mLocation = locUpdateService.getLastLocation();
        }
        return mLocation;
    }

    @Override
    public void onLocationUpdate(Location location) {
        this.mLocation = location;
        Logger.d(TAG, "onLocationUpdate:" + location);

        ArrayList<Object> keyOfListenerList = new ArrayList<>();
        for (Object currentKey : listOfListener.keySet()) {
            try {
                if (listOfListener.get(currentKey) != null) {
                    LocationUpdatesListener listener = listOfListener.get(currentKey);
                    listener.onLocationUpdate(location);
                    Logger.d(TAG, "onLocationUpdate:aaa" + listOfListener.get(currentKey));
                }

            } catch (Exception e) {
                try {
                    Logger.e(TAG, "onLocationUpdate:eee" + e.getMessage());
                    keyOfListenerList.add(currentKey);
                } catch (Exception e1) {
                    Logger.e(TAG, "onLocationUpdate >> Exception:" + e1.getMessage());
                }
            }
        }

        try {

            if (keyOfListenerList.size() > 0) {
                for (int i = 0; i < keyOfListenerList.size(); i++) {
                    listOfListener.remove(keyOfListenerList.get(i));
                }
            }
        } catch (Exception e1) {
            Logger.e(TAG, "onLocationUpdate >> Exception:" + e1.getMessage());
        }

    }

    public void startLocationUpdates(Object obj, LocationUpdatesListener locUpdatesListener) {
        if (obj != null && locUpdatesListener != null) {
            listOfListener.put(obj, locUpdatesListener);
        }

        if (mLocation != null && locUpdatesListener != null) {
            new Handler().postDelayed(() -> locUpdatesListener.onLocationUpdate(mLocation), 500);
        }
    }

    public void stopLocationUpdates(Object obj) {
        if (instance == null || obj == null) {
            return;
        }
        listOfListener.remove(obj);
    }

    public void destroyLocUpdates(Object obj) {
        Logger.d("destroyLocUpdates", "::called");
        if (obj == null) {
            throw new RuntimeException("Object should not be null");
        }
        try {
            listOfListener.clear();
            if (locUpdateService != null && obj != null && !(obj instanceof LocationUpdateService)) {
                locUpdateService.stopLocationUpdateService(obj);
            } else {
                locUpdateService = null;
            }

            if (mServiceBound) {
                MyApp.getInstance().unbindService(mConnection);
                mServiceBound = false;
            }
            instance = null;
        } catch (Exception e) {
            Logger.e(TAG, "destroyLocUpdates >> Exception:" + e.getMessage());
        }
    }

    public interface LocationUpdatesListener {
        void onLocationUpdate(Location location);
    }
}