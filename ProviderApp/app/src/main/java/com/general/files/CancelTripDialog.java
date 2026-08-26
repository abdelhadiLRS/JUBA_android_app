package com.general.files;

import android.content.Context;
import android.location.Location;

import com.multixpro.provider.WorkingtrekActivity;
import com.service.handler.ApiHandler;
import com.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Admin on 21-07-2016.
 */
public class CancelTripDialog {

    Context mContext;
    GeneralFunctions generalFunc;
    boolean isTripStart = false;
    HashMap<String, String> data_trip;
    androidx.appcompat.app.AlertDialog alertDialog;
    Location userLocation;

    public CancelTripDialog(Context mContext, HashMap<String, String> data_trip, GeneralFunctions generalFunc, boolean isTripStart) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.data_trip = data_trip;
        this.isTripStart = isTripStart;
        //show();
    }

    public CancelTripDialog(Context mContext, HashMap<String, String> data_trip, GeneralFunctions generalFunc, String iCancelReasonId, String comment, boolean isTripStart, String reason) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.data_trip = data_trip;

        this.isTripStart = isTripStart;

        if (isTripStart == false) {
            cancelTrip(iCancelReasonId, comment, reason);
        } else {
            ((WorkingtrekActivity) mContext).cancelTrip(reason, comment);
        }

    }


    public CancelTripDialog(Context mContext, HashMap<String, String> data_trip, GeneralFunctions generalFunc, String iCancelReasonId, String comment, boolean isTripStart, String reason, Location userLocation) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.userLocation = userLocation;
        this.data_trip = data_trip;

        this.isTripStart = isTripStart;

        if (isTripStart == false) {
            cancelTrip(iCancelReasonId, comment, reason);
        } else {
            ((WorkingtrekActivity) mContext).cancelTrip(reason, comment);
        }

    }

    public void cancelTrip(String iCancelReasonId, String comment, String reason) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "cancelTrip");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("iUserId", data_trip.get("PassengerId"));
        parameters.put("iTripId", data_trip.get("TripId"));
        parameters.put("UserType", Utils.app_type);
        parameters.put("Reason", reason);
        parameters.put("Comment", comment);
        parameters.put("iCancelReasonId", iCancelReasonId);
        if (userLocation != null) {
            parameters.put("vLatitude", "" + userLocation.getLatitude());
            parameters.put("vLongitude", "" + userLocation.getLongitude());
        }

        ApiHandler.execute(mContext, parameters, true, false, generalFunc, responseString -> {
            JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

            if (responseStringObject != null && !responseStringObject.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);
                String message = generalFunc.getJsonValueStr(Utils.message_str, responseStringObject);
                if (isDataAvail) {


                    generalFunc.saveGoOnlineInfo();
                    // generalFunc.restartApp();
                   // MyApp.getInstance().restartWithGetDataApp();
                    MyApp.getInstance().refreshView(MyApp.getInstance().getCurrentAct(),responseString);


                } else {
                    if (message.equals("DO_RESTART") || message.equals(Utils.GCM_FAILED_KEY) || message.equals(Utils.APNS_FAILED_KEY) || message.equals("LBL_SERVER_COMM_ERROR")) {
                        MyApp.getInstance().restartWithGetDataApp();
                    } else {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                    }
                }
            } else {
                generalFunc.showError();
            }
        });
    }
}
