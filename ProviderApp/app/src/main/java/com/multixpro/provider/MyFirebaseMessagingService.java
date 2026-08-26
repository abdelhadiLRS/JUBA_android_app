package com.multixpro.provider;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.general.call.SinchHandler;
import com.general.files.FireTripStatusMsg;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.SinchHelpers;
import com.utils.Utils;

import java.util.Map;

/**
 * Created by Admin on 29-07-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String authorizedEntity; // Project id from Google Developer Console
    String scope = "GCM"; // e.g. communicating using GCM, but you can use any
    // URL-safe characters up to a maximum of 1000, or
    // you can also leave it blank.

    @Override
    public void onNewToken(String s) {
        // depricated
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (!Utils.checkText(authorizedEntity)) {
            authorizedEntity = MyApp.getInstance().getGeneralFun(this).retrieveValue(Utils.APP_GCM_SENDER_ID_KEY);
        }

        //Displaying token on logcat
        super.onNewToken(s);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map data = remoteMessage.getData();

        if (SinchHelpers.isSinchPushPayload(remoteMessage.getData())) {

            new ServiceConnection() {
                private Map payload;

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (payload != null) {
                        SinchHandler.getInstance().relayRemotePushNotificationPayload(payload);
                    }
                    payload = null;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }

                public void relayMessageData(Map<String, String> data) {
                    payload = data;
                    GeneralFunctions generalFunc = MyApp.getInstance().getAppLevelGeneralFunc();
                    String jsonValue = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                    String mName = generalFunc.getJsonValue("vName", jsonValue);
                    String mImage = generalFunc.getJsonValue("vImage", jsonValue);
                    SinchHandler.getInstance().initiateService(mName, mImage);
                }
            }.relayMessageData(data);
            return;

        }

        if (!Utils.checkText(authorizedEntity)) {
            authorizedEntity = MyApp.getInstance().getGeneralFun(this).retrieveValue(Utils.APP_GCM_SENDER_ID_KEY);
        }


        if (remoteMessage == null || remoteMessage.getData() == null/* || remoteMessage.getNotification().getBody() == null*/) {
            return;
        }

        String message = remoteMessage.getData().get("message");

        new FireTripStatusMsg(MyApp.getInstance() != null ? MyApp.getInstance().getCurrentAct() : getApplicationContext(), "Push").fireTripMsg(message);
    }
}