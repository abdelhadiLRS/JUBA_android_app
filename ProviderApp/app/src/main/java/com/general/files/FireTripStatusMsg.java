package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.general.call.CommunicationManager;
import com.multixpro.provider.BiddingViewDetailsActivity;
import com.multixpro.provider.BookingsActivity;
import com.multixpro.provider.CabRequestedActivity;
import com.multixpro.provider.ChatActivity;
import com.multixpro.provider.MainActivity;
import com.multixpro.provider.MainActivity_22;
import com.multixpro.provider.deliverAll.LiveTrackOrderDetailActivity;
import com.utils.CabRequestStatus;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;

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

    private String receivedBy = "";

    public FireTripStatusMsg() {
        // TODO: 13-06-2022 | Do not delete this Constructor | Socket Message not come
    }

    public FireTripStatusMsg(Context mContext, String receivedBy) {
        this.mContext = mContext;
        this.receivedBy = receivedBy;
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

        JSONObject obj_msg = generalFunc.getJsonObject(finalMsg);

        String tSessionId = generalFunc.getJsonValueStr("tSessionId", obj_msg);

        if (!tSessionId.equals("") && !tSessionId.equals(generalFunc.retrieveValue(Utils.SESSION_ID_KEY))) {
            return;
        }

        if (!generalFunc.isUserLoggedIn() && !Utils.checkText(generalFunc.getMemberId())) {
            return;
        }

        if (!GeneralFunctions.isJsonObj(finalMsg)) {
            String passMessage = generalFunc.convertNumberWithRTL(message);
            LocalNotification.dispatchLocalNotification(mContext, passMessage, true);
            generalFunc.showGeneralMessage("", passMessage);
            return;
        }

        if (generalFunc.getJsonValue("Message", finalMsg).equals("CabRequested")) {
            (new CabRequestStatus(mContext)).updateDriverRequestStatus(1, generalFunc.getJsonValue("PassengerId", finalMsg), "Received", receivedBy, generalFunc.getJsonValue("MsgCode", finalMsg));
        }

        boolean isMsgExist = isTripStatusMsgExist(generalFunc, finalMsg, mContext);
        Logger.d(TAGS, "MsgReceived:: MsgExist-> " + isMsgExist);

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
        if (messageStr.equals("")) {

            String msgTypeStr = generalFunc.getJsonValueStr("MsgType", obj_msg);
            String messageType_str = generalFunc.getJsonValueStr("MessageType", obj_msg);
            String vTitle = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg));

            if (msgTypeStr.equalsIgnoreCase("CHAT")) {


                if (!(MyApp.getInstance().getCurrentAct() instanceof ChatActivity)) {
                    LocalNotification.dispatchLocalNotification(mContext, generalFunc.getJsonValueStr("Msg", obj_msg), false);
                    /*Bundle bn = new Bundle();
                    bn.putString("iFromMemberId", generalFunc.getJsonValueStr("iFromMemberId", obj_msg));
                    bn.putString("FromMemberImageName", generalFunc.getJsonValueStr("FromMemberImageName", obj_msg));
                    bn.putString("iTripId", generalFunc.getJsonValueStr("iTripId", obj_msg));
                    bn.putString("FromMemberName", generalFunc.getJsonValueStr("FromMemberName", obj_msg));
                    bn.putString("vBookingNo", generalFunc.getJsonValueStr("vBookingNo", obj_msg));*/

                    Intent chatActInt = new Intent(MyApp.getInstance().getApplicationContext(), ChatActivity.class);
                    if (obj_msg != null) {
                        chatActInt.putExtras(generalFunc.createChatBundle(obj_msg));
                        if (!obj_msg.isNull("iBiddingPostId")) {
                            chatActInt.putExtra("iBiddingPostId", generalFunc.getJsonValueStr("iBiddingPostId", obj_msg));
                            chatActInt.putExtra("iUserId", generalFunc.getJsonValueStr("iUserId", obj_msg));
                        }
                    }
                    chatActInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    MyApp.getInstance().getApplicationContext().startActivity(chatActInt);
                } else if (MyApp.getInstance() != null && MyApp.getInstance().getCurrentAct() instanceof ChatActivity) {
                    //generalFunc.storeData("OPEN_CHAT", obj_msg.toString());
                       /* Bundle bn = new Bundle();
                        bn.putString("iFromMemberId", generalFunc.getJsonValueStr("iFromMemberId", obj_msg));
                        bn.putString("FromMemberImageName", generalFunc.getJsonValueStr("FromMemberImageName", obj_msg));
                        bn.putString("iTripId", generalFunc.getJsonValueStr("iTripId", obj_msg));
                        bn.putString("FromMemberName", generalFunc.getJsonValueStr("FromMemberName", obj_msg));
                        bn.putString("vBookingNo", generalFunc.getJsonValueStr("vBookingNo", obj_msg));*/
                    //((ChatActivity) MyApp.getInstance().getCurrentAct()).setCurrentTripData(generalFunc.createChatBundle(obj_msg));

                    ChatActivity chatActivity = (ChatActivity) MyApp.getInstance().getCurrentAct();
                    chatActivity.playNotificationSound();
                    return;
                }
            } else if (msgTypeStr.equalsIgnoreCase("VOIP")) {

            }
            //else if (msgTypeStr.equalsIgnoreCase("Notification"))
            else {
                LocalNotification.dispatchLocalNotification(mContext, vTitle, true);

                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
//                    generateAlert.setSystemAlertWindow(true);
                generateAlert.setBtnClickList(btn_id -> doOperations());
                generateAlert.setContentMessage("", vTitle);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();

            }

        } else if (!messageStr.equals("")) {
            String vTitle = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg));

//            LocalNotification.dispatchLocalNotification(mContext,vTitle,false);

            if (messageStr.equalsIgnoreCase("TripCancelled") || messageStr.equalsIgnoreCase("DestinationAdded") || messageStr.equalsIgnoreCase("OrderCancelByAdmin") || messageStr.equalsIgnoreCase("RewardProgramCancelled")) {
                if (messageStr.equalsIgnoreCase("TripCancelled") || messageStr.equalsIgnoreCase("OrderCancelByAdmin")) {
                    generalFunc.saveGoOnlineInfo();
                }

                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> MyApp.getInstance().restartWithGetDataApp());
                generateAlert.setContentMessage("", vTitle);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();
            } else if (messageStr.equalsIgnoreCase("CabRequested")) {
                if (((MyApp.getInstance().mainAct != null || MyApp.getInstance().main22Act != null) && MyApp.getInstance().driverArrivedAct == null && MyApp.getInstance().activeTripAct == null) || generalFunc.getJsonValueStr("ePoolRequest", obj_msg).equalsIgnoreCase("Yes") || generalFunc.getJsonValueStr("eAcceptTripRequest", obj_msg).equalsIgnoreCase("Yes")) {
                    dispatchCabRequest(generalFunc, obj_msg.toString());
                } else if (((MyApp.getInstance().mainAct == null || MyApp.getInstance().main22Act == null) && MyApp.getInstance().driverArrivedAct == null && MyApp.getInstance().activeTripAct == null) || generalFunc.getJsonValueStr("ePoolRequest", obj_msg).equalsIgnoreCase("Yes") || generalFunc.getJsonValueStr("eAcceptTripRequest", obj_msg).equalsIgnoreCase("Yes")) {
                    dispatchCabRequest(generalFunc, obj_msg.toString());
                }

            } else if (messageStr.equalsIgnoreCase("OrderItemsReviewed") || messageStr.equalsIgnoreCase("OrderPaymentByUser")) {
                if (MyApp.getInstance().getCurrentAct() instanceof LiveTrackOrderDetailActivity) {
                    LiveTrackOrderDetailActivity instance = (LiveTrackOrderDetailActivity) MyApp.getInstance().getCurrentAct();
                    instance.pubnubmsg(vTitle);
                    LocalNotification.dispatchLocalNotification(mContext, vTitle, true);
                }
            } else if (messageStr.equalsIgnoreCase("GoPayVerifyAmount")) {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
                generateAlert.setContentMessage("", vTitle);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();
                LocalNotification.dispatchLocalNotification(mContext, vTitle, true);
            } else {
                LocalNotification.dispatchLocalNotification(mContext, vTitle, false);
            }
        }
    }

    private void doOperations() {
//        MyApp.getInstance().restartWithGetDataApp()
    }

    private void dispatchCabRequest(GeneralFunctions generalFunc, String message) {
        if (generalFunc.containsKey(Utils.DRIVER_REQ_COMPLETED_MSG_CODE_KEY + (generalFunc.getJsonValue("MsgCode", message)))) {
            return;
        }
        if (MyApp.getInstance().ispoolRequest) {
            return;
        }
        if (generalFunc.getJsonValue("REQUEST_TYPE", message) != null) {
            if (generalFunc.getJsonValue("REQUEST_TYPE", message).equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                LocalNotification.dispatchLocalNotification(mContext, generalFunc.retrieveLangLBl("", "LBL_TRIP_USER_WAITING"), true);
            } else if (generalFunc.getJsonValue("REQUEST_TYPE", message).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                LocalNotification.dispatchLocalNotification(mContext, generalFunc.retrieveLangLBl("", "LBL_USER_WAITING"), true);
            } else {
                LocalNotification.dispatchLocalNotification(mContext, generalFunc.retrieveLangLBl("", "LBL_DELIVERY_SENDER_WAITING"), true);
            }
        } else {
            LocalNotification.dispatchLocalNotification(mContext, generalFunc.retrieveLangLBl("", "LBL_TRIP_USER_WAITING"), true);
        }
        generalFunc.storeData(Utils.DRIVER_ACTIVE_REQ_MSG_KEY, message);

        Intent cabReqAct = new Intent(MyApp.getInstance().getApplicationContext(), CabRequestedActivity.class);
        // Intent cabReqAct = new Intent(MyApp.getInstance().getApplicationContext(),  CabRequestedActivity.class);
        cabReqAct.putExtra("Message", message);
        cabReqAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        if (MyApp.getInstance() != null && MyApp.getInstance().getApplicationContext() != null) {
            MyApp.getInstance().getApplicationContext().startActivity(cabReqAct);
        } else if (this.mContext != null) {
            this.mContext.startActivity(cabReqAct);
        }
    }

    private void dispatchNotification(String message) {

        Context mLocContext = this.mContext;

        if (mLocContext == null && MyApp.getInstance() != null && MyApp.getInstance().getCurrentAct() == null) {
            mLocContext = MyApp.getInstance().getApplicationContext();
        }

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
                String title_msg = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg));
                switch (message_str) {

                    case "TripCancelled":
                        generalFunc.saveGoOnlineInfo();
                        LocalNotification.dispatchLocalNotification(mLocContext, title_msg, false);
                        break;
                    case "OrderCancelByAdmin":
                        generalFunc.saveGoOnlineInfo();
                        LocalNotification.dispatchLocalNotification(mLocContext, title_msg, false);
                        break;
                    case "DestinationAdded":
                        LocalNotification.dispatchLocalNotification(mLocContext, title_msg, false);
                        break;
                    case "CabRequested":
                        dispatchCabRequest(generalFunc, message);
                        break;
                }
            }
        }
    }

    private boolean msgHandling(GeneralFunctions generalFunc, JSONObject obj_msg) {
        String MsgType = generalFunc.getJsonValueStr("MsgType", obj_msg);
        if (MsgType != null) {
            //String messageStr = generalFunc.getJsonValueStr("Message", obj_msg);

            LocalNotification.dispatchLocalNotification(mContext, generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg)), true);
            switch (MsgType) {
                case "TwilioVideocall":
                    CommunicationManager.getInstance().incomingCommunicate(mContext, generalFunc, null, obj_msg);
                    return true;
                case "BiddingTaskCancelled":
                case "BiddingTaskDeclined":
                case "BiddingTaskAcceptedOther":
                    GenerateAlertBox alertBox1 = new GenerateAlertBox(mContext);
                    alertBox1.setContentMessage("", generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg)));
                    alertBox1.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    alertBox1.setBtnClickList(btn_id -> {
                        alertBox1.closeAlertBox();
                        if (btn_id == 1) {
                            if (MyApp.getInstance().driverArrivedAct != null || MyApp.getInstance().activeTripAct != null) {
                                if (MsgType.equalsIgnoreCase("BiddingTaskCancelled")) {
                                    generalFunc.restartApp();
                                }
                                return;
                            }
                            if (MyApp.getInstance().getCurrentAct() instanceof BiddingViewDetailsActivity) {
                                BiddingViewDetailsActivity biddingViewDetailsActivity = (BiddingViewDetailsActivity) MyApp.getInstance().getCurrentAct();
                                biddingViewDetailsActivity.finish();
                            }
                        }
                    });
                    alertBox1.showAlertBox();
                    return true;
                case "BiddingTaskReoffered":
                case "BiddingTaskAccepted":
                    GenerateAlertBox alertBox2 = new GenerateAlertBox(mContext);
                    alertBox2.setContentMessage("", generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg)));
                    alertBox2.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    alertBox2.setBtnClickList(btn_id -> {
                        alertBox2.closeAlertBox();
                        if (btn_id == 1) {
                            if (MyApp.getInstance().driverArrivedAct != null || MyApp.getInstance().activeTripAct != null) {
                                return;
                            }
                            if (MyApp.getInstance().getCurrentAct() instanceof BiddingViewDetailsActivity) {
                                BiddingViewDetailsActivity biddingViewDetailsActivity = (BiddingViewDetailsActivity) MyApp.getInstance().getCurrentAct();
                                new Handler(Looper.getMainLooper()).post(biddingViewDetailsActivity::getBiddingViewDetailsList);
                            } else {
                                Intent bidActInt = new Intent(MyApp.getInstance().getApplicationContext(), BiddingViewDetailsActivity.class);
                                bidActInt.putExtra("iBiddingPostId", generalFunc.getJsonValueStr("iBiddingPostId", obj_msg));
                                bidActInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MyApp.getInstance().getApplicationContext().startActivity(bidActInt);
                            }
                        }
                    });
                    alertBox2.showAlertBox();
                    return true;
                case "BiddingTaskReceived":
                    GenerateAlertBox alertBox3 = new GenerateAlertBox(mContext);
                    alertBox3.setContentMessage("", generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_msg)));
                    alertBox3.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    alertBox3.setBtnClickList(btn_id -> {
                        alertBox3.closeAlertBox();
                        if (btn_id == 1) {
                            if (MyApp.getInstance().driverArrivedAct != null || MyApp.getInstance().activeTripAct != null) {
                                return;
                            }
                            if (MyApp.getInstance().getCurrentAct() instanceof MainActivity) {
                                MainActivity mainActivity = (MainActivity) MyApp.getInstance().getCurrentAct();
                                mainActivity.checkBiddingView(2);
                            } else if (MyApp.getInstance().getCurrentAct() instanceof MainActivity_22) {
                                MainActivity_22 mainActivity_22 = (MainActivity_22) MyApp.getInstance().getCurrentAct();
                                mainActivity_22.checkBiddingView(2);
                            } else {
                                if (MyApp.getInstance().getCurrentAct() instanceof BookingsActivity) {
                                    BookingsActivity bookingsActivity = (BookingsActivity) MyApp.getInstance().getCurrentAct();
                                    bookingsActivity.setFrag(2);
                                } else {
                                    Intent booksActInt = new Intent(MyApp.getInstance().getApplicationContext(), BookingsActivity.class);
                                    if (obj_msg != null) {
                                        booksActInt.putExtras(generalFunc.createChatBundle(obj_msg));
                                    }
                                    booksActInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    booksActInt.putExtra("viewPos", 2);
                                    MyApp.getInstance().getApplicationContext().startActivity(booksActInt);
                                }
                            }
                        }
                    });
                    alertBox3.showAlertBox();
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
                String iTripId = "";
                String iBiddingPostId = "";

                if (generalFunc.getJsonValue("eSystem", msg).equalsIgnoreCase(Utils.eSystem_Type)) {
                    if (!message.equalsIgnoreCase("CabRequested")) {
                        iTripId = Utils.checkText(generalFunc.getJsonValueStr("iOrderId", obj_tmp)) ? generalFunc.getJsonValueStr("iOrderId", obj_tmp) : generalFunc.getJsonValueStr("iTripId", obj_tmp);
                    }
                } else {
                    iTripId = generalFunc.getJsonValueStr("iTripId", obj_tmp);
                }
                if (generalFunc.getJsonValue("iBiddingPostId", obj_tmp) != null && !generalFunc.getJsonValue("iBiddingPostId", obj_tmp).equals("")) {
                    iBiddingPostId = generalFunc.getJsonValueStr("iBiddingPostId", obj_tmp);
                }
                // String iTripId = getJsonValueStr("iTripId", obj_tmp);
                String iTripDeliveryLocationId = generalFunc.getJsonValueStr("iTripDeliveryLocationId", obj_tmp);

                if (!iTripId.equals("")) {
                    String vTitle = generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vTitle", obj_tmp));
                    String time = generalFunc.getJsonValueStr("time", obj_tmp);
                    String key = "";
                    if (generalFunc.getJsonValue("eType", msg).equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
                        key = Utils.TRIP_REQ_CODE_PREFIX_KEY + iTripId + "_" + iTripDeliveryLocationId + "_" + message;
                    } else {
                        key = Utils.TRIP_REQ_CODE_PREFIX_KEY + iTripId + "_" + message;
                    }
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
                } else if (!iBiddingPostId.equalsIgnoreCase("")) {
                    String time = generalFunc.getJsonValueStr("time", obj_tmp);
                    String key = "";
                    key = Utils.TRIP_REQ_CODE_PREFIX_KEY + iBiddingPostId + "_" + message + "" + time;
                    String data = generalFunc.retrieveValue(key);
                    if (data.equals("")) {
                        if (time.equals("")) {
                            generalFunc.storeData(key, "" + System.currentTimeMillis());
                        } else {
                            generalFunc.storeData(key, "" + time);
                        }
                        return false;
                    } else {
                        return true;
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