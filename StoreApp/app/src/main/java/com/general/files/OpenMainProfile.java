package com.general.files;

import android.content.Context;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.multixpro.store.AccountverificationActivity;
import com.multixpro.store.MainActivity;
import com.multixpro.store.SuspendedDriver_Activity;
import com.utils.MarkerAnim;
import com.utils.Utils;

import org.json.JSONObject;

/**
 * Created by Admin on 29-06-2016.
 */
public class OpenMainProfile {
    private final JSONObject userProfileJsonObj;
    Context mContext;
    String responseString;
    boolean isCloseOnError;
    GeneralFunctions generalFun;
    boolean isnotification = false;
    MarkerAnim MarkerAnim;

    public OpenMainProfile(Context mContext, String responseString, boolean isCloseOnError, GeneralFunctions generalFun) {
        this.mContext = mContext;
        //this.responseString = responseString;
        this.isCloseOnError = isCloseOnError;
        this.generalFun = generalFun;

        this.responseString = generalFun.retrieveValue(Utils.USER_PROFILE_JSON);

        userProfileJsonObj = generalFun.getJsonObject(this.responseString);
        MarkerAnim = new MarkerAnim();

    }

    public OpenMainProfile(Context mContext, String responseString, boolean isCloseOnError, GeneralFunctions generalFun, boolean isnotification) {
        this.mContext = mContext;
        //this.responseString = responseString;
        this.isCloseOnError = isCloseOnError;
        this.generalFun = generalFun;
        this.isnotification = isnotification;

        this.responseString = generalFun.retrieveValue(Utils.USER_PROFILE_JSON);

        userProfileJsonObj = generalFun.getJsonObject(this.responseString);
        MarkerAnim = new MarkerAnim();
//        HashMap<String,String> storeData=new HashMap<>();
//        storeData.put(Utils.DefaultCountry, generalFun.getJsonValueStr("vDefaultCountry", userProfileJsonObj));
//        storeData.put(Utils.DefaultCountryCode, generalFun.getJsonValueStr("vDefaultCountryCode", userProfileJsonObj));
//        storeData.put(Utils.DefaultPhoneCode, generalFun.getJsonValueStr("vDefaultPhoneCode", userProfileJsonObj));
//        storeData.put(Utils.DefaultCountryImage, generalFun.getJsonValueStr("vDefaultCountryImage", userProfileJsonObj));
//        generalFun.storeData(storeData);

    }

    public void startProcess() {
        generalFun.sendHeartBeat();

        // responseString = generalFun.retrieveValue(Utils.USER_PROFILE_JSON);
        //setGeneralData();
        new SetGeneralData(generalFun, userProfileJsonObj);


        MarkerAnim.driverMarkerAnimFinished = true;

        Bundle bn = new Bundle();
        bn.putString("USER_PROFILE_JSON", responseString);
        bn.putString("IsAppReStart", "true"); // flag for retrieving data to en route trip pages

        boolean isEmailBlankAndOptional = generalFun.isEmailBlankAndOptional(generalFun, generalFun.getJsonValueStr("vEmail", userProfileJsonObj));
        if (generalFun.getJsonValue("vPhone", userProfileJsonObj).equals("") || (generalFun.getJsonValue("vEmail", userProfileJsonObj).equals("") && !isEmailBlankAndOptional)) {
            if (generalFun.getMemberId() != null && !generalFun.getMemberId().equals("")) {
                new ActUtils(mContext).startActWithData(AccountverificationActivity.class, bn);
            }
        } else {

            String eStatus = generalFun.getJsonValueStr("eStatus", userProfileJsonObj);

            if (eStatus.equalsIgnoreCase("suspend")) {
                new ActUtils(mContext).startAct(SuspendedDriver_Activity.class);
            } else {
                new ActUtils(mContext).startActWithData(MainActivity.class, bn);

            }
        }
        try {
            ActivityCompat.finishAffinity(MyApp.getInstance().getCurrentAct());
        } catch (Exception e) {

        }
    }
}