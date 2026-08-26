package com.general.files;

import android.app.Activity;
import android.content.Context;

import com.general.call.CommunicationManager;
import com.multixpro.store.MainActivity;
import com.multixpro.store.OrderDetailActivity;
import com.multixpro.store.R;
import com.multixpro.store.TrackOrderActivity;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Admin on 21/03/18.
 */

public class FireTripStatusMsg {

    private final String TAGS = FireTripStatusMsg.class.getSimpleName();
    private Context mContext;
    private static String tmp_msg_chk = "";

    public FireTripStatusMsg() {
        // TODO: 13-06-2022 | Do not delete this Constructor | Socket Message not come
    }

    public FireTripStatusMsg(Context mContext) {
        this.mContext = mContext;
    }

    public void fireTripMsg(String message) {

        Logger.d(TAGS, "MsgReceived :: called");
        if (!Utils.checkText(message) || tmp_msg_chk.equals(message)) {
            Logger.d(TAGS, "MsgReceived :: return");
            return;
        }
        tmp_msg_chk = message;

        Logger.e(TAGS, "MsgReceived::" + message);
        String finalMsg = message;

        if (!GeneralFunctions.isJsonObj(finalMsg)) {
            try {
                finalMsg = new JSONTokener(message).nextValue().toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!GeneralFunctions.isJsonObj(finalMsg)) {
                finalMsg = finalMsg.replaceAll("^\"|\"$", "");

                if (!GeneralFunctions.isJsonObj(finalMsg)) {
                    finalMsg = message.replaceAll("\\\\", "");

                    finalMsg = finalMsg.replaceAll("^\"|\"$", "");

                    if (!GeneralFunctions.isJsonObj(finalMsg)) {
                        finalMsg = message.replace("\\\"", "\"").replaceAll("^\"|\"$", "");
                    }
                    finalMsg = finalMsg.replace("\\\\\"", "\\\"");
                }
            }
        }
        if (MyApp.getInstance() == null) {
            if (mContext != null) {
                dispatchNotification(finalMsg);
            }
            return;
        }

        if (MyApp.getInstance().getCurrentAct() != null) {
            mContext = MyApp.getInstance().getCurrentAct();
        }

        if (mContext == null) {
            dispatchNotification(finalMsg);
            return;
        }

        GeneralFunctions generalFunc = MyApp.getInstance().getGeneralFun(mContext);

        if (!GeneralFunctions.isJsonObj(finalMsg)) {
            String passMessage = generalFunc.convertNumberWithRTL(message);
            LocalNotification.dispatchLocalNotification(mContext, passMessage, true);
            generalFunc.showGeneralMessage("", passMessage);
            return;
        }
        JSONObject obj_msg = generalFunc.getJsonObject(finalMsg);
        String tSessionId = generalFunc.getJsonValueStr("tSessionId", obj_msg);

        if (!tSessionId.equals("") && !tSessionId.equals(generalFunc.retrieveValue(Utils.SESSION_ID_KEY))) {
            return;
        }

        if (!generalFunc.isUserLoggedIn() && !Utils.checkText(generalFunc.getMemberId())) {
            return;
        }

        Logger.d("finalMsg", ":" + finalMsg);
        boolean isMsgExist = isTripStatusMsgExist(generalFunc, finalMsg, mContext);

        if (isMsgExist) {
            return;
        }

        if (msgHandling(generalFunc, obj_msg)) {
            return;
        }

        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(() -> continueDispatchMsg(generalFunc, obj_msg));
        } else {
            dispatchNotification(finalMsg);
        }
    }

    private void continueDispatchMsg(GeneralFunctions generalFunc, JSONObject obj_msg) {
        String messageStr = generalFunc.getJsonValueStr("Message", obj_msg);
        String msgType = generalFunc.getJsonValueStr("MsgType", obj_msg);

        if (messageStr.equals("")) {

            String msgTypeStr = generalFunc.getJsonValueStr("MsgType", obj_msg);
            if (msgTypeStr.equalsIgnoreCase("VOIP")) {

            } else if (!msgTypeStr.equalsIgnoreCase("LocationUpdate") && !msgTypeStr.equalsIgnoreCase("LocationUpdateOnTrip")) {

                String vTitle = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg));


                LocalNotification.dispatchLocalNotification(mContext, vTitle, true);

                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
//                    generateAlert.setSystemAlertWindow(true);
                generateAlert.setBtnClickList(btn_id -> doOperations());
                generateAlert.setContentMessage("", vTitle);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();
            }

            if (msgTypeStr.equalsIgnoreCase("LocationUpdateOnTrip")) {
                if (mContext instanceof TrackOrderActivity) {
                    ((TrackOrderActivity) mContext).validateIncomingMessage(obj_msg.toString());
                }
            }


        } else if (!messageStr.equals("")) {
            String vTitle = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg));

//            LocalNotification.dispatchLocalNotification(mContext,vTitle,false);

            if (messageStr.equalsIgnoreCase("TripCancelled") || messageStr.equalsIgnoreCase("DestinationAdded")) {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> MyApp.getInstance().restartWithGetDataApp());
                generateAlert.setContentMessage("", vTitle);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();
            } else if (messageStr.equalsIgnoreCase("CabRequestAccepted")) {

                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
                generateAlert.setCustomView(R.layout.dialog_accept_driver_design);
                ((MTextView) generateAlert.getView(R.id.messageTxtView)).setText(vTitle);
                ((MButton) ((MaterialRippleLayout) generateAlert.getView(R.id.okBtn)).getChildView()).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.getView(R.id.okBtn).setOnClickListener(v -> generateAlert.closeAlertBox());
                generateAlert.showAlertBox();

                if (MyApp.getInstance().getCurrentAct() != null && MyApp.getInstance().getCurrentAct() instanceof OrderDetailActivity) {
                    ((OrderDetailActivity) MyApp.getInstance().getCurrentAct()).confirmIncomingDriver(generalFunc.getJsonValueStr("iOrderId", obj_msg));
                }

            } else if (messageStr.equalsIgnoreCase("OrderRequested") || messageStr.equalsIgnoreCase("OrderAutoAccept")) {

                if (MyApp.getInstance().mainAct != null) {
                    dispatchCabRequest(generalFunc, obj_msg.toString());
                    String eOrderplacedBy = generalFunc.getJsonValue("eOrderplacedBy", obj_msg.toString());
                    if (eOrderplacedBy != null && !eOrderplacedBy.equalsIgnoreCase("")) {
                        MyApp.getInstance().mainAct.newOrderReceived(true);
                    } else {
                        MyApp.getInstance().mainAct.newOrderReceived(false);
                    }
                }

                if (!(MyApp.getInstance().getCurrentAct() instanceof MainActivity)) {
                    LocalNotification.dispatchLocalNotification(mContext, vTitle, false);
                }

            } else if (messageStr.equalsIgnoreCase("OrderPickedup") || messageStr.equalsIgnoreCase("OrderDelivered")) {

                if (mContext instanceof TrackOrderActivity) {
                    ((TrackOrderActivity) mContext).validateIncomingOrderMessage(obj_msg.toString());
                } else if (mContext instanceof OrderDetailActivity) {
                    boolean isOnSameOrderDetailsPage = ((OrderDetailActivity) mContext).confirmIncomingDriver(generalFunc.getJsonValueStr("iOrderId", obj_msg), true);

                    if (isOnSameOrderDetailsPage && (messageStr.equalsIgnoreCase("OrderDelivered")
                            || (messageStr.equalsIgnoreCase("OrderCancelByAdmin") || messageStr.equalsIgnoreCase("OrderCancelByDriver")))) {
                        generalFunc.showGeneralMessage("", vTitle, true);

                        ((OrderDetailActivity) mContext).getOrderDetails();
                    } else if (isOnSameOrderDetailsPage) {
                        generalFunc.showGeneralMessage("", vTitle);
                        ((OrderDetailActivity) mContext).getOrderDetails();
                    }

                }

            } else if (messageStr.equalsIgnoreCase("OrderCancelByAdmin") || messageStr.equalsIgnoreCase("OrderCancelByDriver")) {
                if (MyApp.getInstance().mainAct != null) {
                    dispatchCabRequest(generalFunc, obj_msg.toString());

//                    MyApp.getInstance().getMainAct().isFirstCall = false;
                    MyApp.getInstance().mainAct.updateOrders();
                }

                if (mContext instanceof TrackOrderActivity) {
                    ((TrackOrderActivity) mContext).validateIncomingOrderMessage(obj_msg.toString());
                } else if (mContext instanceof OrderDetailActivity) {
                    boolean isOnSameOrderDetailsPage = ((OrderDetailActivity) mContext).confirmIncomingDriver(generalFunc.getJsonValueStr("iOrderId", obj_msg), true);

                    if (isOnSameOrderDetailsPage) {
                        generalFunc.showGeneralMessage("", vTitle, true);
                    } else {

                        generalFunc.showGeneralMessage("", vTitle);
                    }
                } else {
                    generalFunc.showGeneralMessage("", vTitle);
                }

            } else {
                LocalNotification.dispatchLocalNotification(mContext, vTitle, false);
            }
        } else if (!msgType.equalsIgnoreCase("") && mContext instanceof TrackOrderActivity) {

            if (mContext instanceof TrackOrderActivity) {
                ((TrackOrderActivity) mContext).validateIncomingMessage(obj_msg.toString());
            }
        }
    }

    private void doOperations() {
//        MyApp.getInstance().restartWithGetDataApp()
    }

    private void dispatchCabRequest(GeneralFunctions generalFunc, String message) {
        String vTitle = generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vTitle", message));
        LocalNotification.dispatchLocalNotification(mContext, vTitle, true);
//        MyApp.getInstance().getApplicationContext().startActivity(cabReqAct);
    }


    private void dispatchNotification(String message) {
        Context mLocContext = this.mContext;

        if (mLocContext == null && MyApp.getInstance() != null && MyApp.getInstance().getCurrentAct() == null) {
            mLocContext = MyApp.getInstance().getApplicationContext();
        }

//        if (mLocContext != null && MyApp.getInstance().getCurrentAct() == null) {
        if (mLocContext != null) {
            GeneralFunctions generalFunc = MyApp.getInstance().getGeneralFun(mLocContext);

            if (!GeneralFunctions.isJsonObj(message)) {
                LocalNotification.dispatchLocalNotification(mLocContext, message, true);
                return;
            }

            JSONObject obj_msg = generalFunc.getJsonObject(message);

            if (msgHandling(generalFunc, obj_msg)) {
                return;
            }

            String message_str = generalFunc.getJsonValueStr("Message", obj_msg);

            if (message_str.equals("")) {
                String msgType_str = generalFunc.getJsonValueStr("MsgType", obj_msg);

                switch (msgType_str) {
                    case "CHAT":
                        generalFunc.storeData("OPEN_CHAT", obj_msg.toString());
                        LocalNotification.dispatchLocalNotification(mLocContext, generalFunc.getJsonValueStr("Msg", obj_msg), false);
                        break;
                    default:
                        LocalNotification.dispatchLocalNotification(mLocContext, generalFunc.getJsonValueStr("vTitle", obj_msg), false);
                }

            } else {
                String pass_msg = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg));
                switch (message_str) {

                    case "CabRequestAccepted":
                    case "OrderRequested":
                    case "OrderAutoAccept":
                    case "TripCancelled":
                    case "DestinationAdded":
                        LocalNotification.dispatchLocalNotification(mLocContext, pass_msg, false);
                        break;
                }
            }
        }
    }

    private boolean msgHandling(GeneralFunctions generalFunc, JSONObject obj_msg) {
        String MsgType = generalFunc.getJsonValueStr("MsgType", obj_msg);
        if (MsgType != null) {
            //String messageStr = generalFunc.getJsonValueStr("Message", obj_msg);
            switch (MsgType) {
                case "TwilioVideocall":
                    CommunicationManager.getInstance().incomingCommunicate(mContext, generalFunc, null, obj_msg);
                    return true;
            }
        }
        return false;
    }

    public boolean isTripStatusMsgExist(GeneralFunctions generalFunc, String msg, Context mContext) {

        JSONObject obj_tmp = generalFunc.getJsonObject(msg);

        if (obj_tmp != null) {
            String message = generalFunc.getJsonValueStr("Message", obj_tmp);

            if (!message.equals("")) {
                String iTripId = generalFunc.getJsonValueStr("iOrderId", obj_tmp);

                if (!iTripId.equals("")) {
                    String vTitle = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_tmp));
                    String time = generalFunc.getJsonValueStr("time", obj_tmp);
                    String key = Utils.TRIP_REQ_CODE_PREFIX_KEY + iTripId + "_" + message;
                    if (message.equals("DestinationAdded")) {

                        long newMsgTime = GeneralFunctions.parseLongValue(0, time);
                        String destKeyValueStr = GeneralFunctions.retrieveValue(key, mContext);
                        if (!destKeyValueStr.equals("")) {

                            long destKeyValue = GeneralFunctions.parseLongValue(0, destKeyValueStr);
                            if (newMsgTime > destKeyValue) {
                                generalFunc.removeValue(key);
                            } else {
                                return true;
                            }
                        }
                    }

                    String data = generalFunc.retrieveValue(key);

                    if (data.equals("")) {
                        LocalNotification.dispatchLocalNotification(mContext, vTitle, true);
                        if (time.equals("")) {
                            generalFunc.storeData(key, "" + System.currentTimeMillis());
                        } else {
                            generalFunc.storeData(key, "" + time);
                        }
                        return false;
                    } else {
                        return true;
                    }
                } else if (!message.equals("") && message.equalsIgnoreCase("CabRequested")) {
                    String msgCode = generalFunc.getJsonValueStr("MsgCode", obj_tmp);
                    String key = Utils.DRIVER_REQ_CODE_PREFIX_KEY + msgCode;

                    String data = generalFunc.retrieveValue(key);

                    if (data.equals("")) {
                        generalFunc.storeData(key, "" + System.currentTimeMillis());
                        return false;
                    }
                } else {
                    String msgType = generalFunc.getJsonValueStr("MsgType", obj_tmp);
                    if (msgType != null) {
                        String key, data, tRandomValue = "";
                        switch (msgType) {
                            case "TwilioVideocall":
                                tRandomValue = generalFunc.getJsonValueStr("tRandomCode", obj_tmp);
                                break;
                        }
                        if (Utils.checkText(tRandomValue)) {
                            key = Utils.TRIP_REQ_CODE_PREFIX_KEY + tRandomValue + "_" + msgType;
                            data = generalFunc.retrieveValue(key);
                            generalFunc.storeData(key, "" + System.currentTimeMillis());
                            return !data.equals("");
                        }
                    }
                }
            }
        }
        return false;
    }
}