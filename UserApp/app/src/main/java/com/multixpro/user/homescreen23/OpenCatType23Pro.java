package com.multixpro.user.homescreen23;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.CommonDeliveryTypeSelectionActivity;
import com.multixpro.user.DonationActivity;
import com.multixpro.user.MainActivity;
import com.multixpro.user.RequestBidInfoActivity;
import com.multixpro.user.deliverAll.FoodDeliveryHomeActivity;
import com.multixpro.user.deliverAll.GenieDeliveryHomeActivity;
import com.multixpro.user.deliverAll.RestaurantAllDetailsNewActivity;
import com.multixpro.user.nearbyservice.NearByServicesActivity;
import com.multixpro.user.rentItem.RentItemHomeActivity;
import com.multixpro.user.rideSharing.RideBook;
import com.multixpro.user.rideSharing.RideMyList;
import com.multixpro.user.rideSharing.RidePublish;
import com.multixpro.user.trackService.TrackAnyList;
import com.multixpro.user.trackService.TrackAnyProfileSetup;
import com.model.ServiceModule;
import com.service.handler.ApiHandler;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class OpenCatType23Pro {

    private final Context mContext;
    private final GeneralFunctions generalFunc;
    private final JSONObject mDataObject;
    boolean isServiceIdMatch = false;
    private final String mLatitude, mLongitude, mAddress;

    public OpenCatType23Pro(Context mContext, GeneralFunctions generalFunc, JSONObject dataObject, String latitude, String longitude, String address) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.mDataObject = dataObject;

        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mAddress = address;
    }

    public void execute() {
        String eCatType = generalFunc.getJsonValueStr("eCatType", mDataObject);
        if (eCatType != null) {
            Bundle bn = new Bundle();
            String s = eCatType.toUpperCase(Locale.US);
            generalFunc.storeData(Utils.iServiceId_KEY, "");

            if (!ServiceModule.isDeliveronly()) {
                getLatLongAddress(bn);
            }

            switch (s) {
                case "RIDE":
                case "RIDEPOOL":
                    bn.putString("selType", Utils.CabGeneralType_Ride);
                    bn.putBoolean("isRestart", false);
                    if (mDataObject.has("isHome")) {
                        bn.putBoolean("isHome", true);
                    } else if (mDataObject.has("isWork")) {
                        bn.putBoolean("isWork", true);
                    } else if (mDataObject.has("eForMedicalService")) {
                        bn.putBoolean("eForMedicalService", generalFunc.getJsonValueStr("eForMedicalService", mDataObject).equalsIgnoreCase("Yes"));
                    }
                    if (s.equalsIgnoreCase("RIDEPOOL")) {
                        bn.putBoolean("isRidePool", true);
                    }
                    new ActUtils(mContext).startActWithData(MainActivity.class, bn);
                    break;

                case "FLY":
                    bn.putString("selType", Utils.CabGeneralType_Ride);
                    bn.putBoolean("eFly", true);
                    bn.putBoolean("isRestart", false);
                    new ActUtils(mContext).startActWithData(MainActivity.class, bn);
                    break;
                case "RIDERECENTLOCATION":

                    bn.putString("selType", Utils.CabGeneralType_Ride);

                    bn.putString("vPlacesLocation", generalFunc.getJsonValueStr("tDaddress", mDataObject));
                    bn.putString("vPlacesLocationLat", generalFunc.getJsonValueStr("tEndLat", mDataObject));
                    bn.putString("vPlacesLocationLong", generalFunc.getJsonValueStr("tEndLong", mDataObject));
                    bn.putBoolean("isPlacesLocation", true);
                    new ActUtils(mContext).startActWithData(MainActivity.class, bn);
                    break;

                case "MOTORIDE":
                    bn.putString("selType", Utils.CabGeneralType_Ride);
                    bn.putBoolean("isRestart", false);
                    bn.putBoolean("emoto", true);
                    new ActUtils(mContext).startActWithData(MainActivity.class, bn);
                    break;

                case "DELIVERY":
                case "MOTODELIVERY":
                    boolean isMulti = mDataObject.has("eDeliveryType") && generalFunc.getJsonValueStr("eDeliveryType", mDataObject).equalsIgnoreCase("Multi");
                    if (isMulti) {
                        bn.putBoolean("fromMulti", true);
                    }
                    /*Single Delivery UI as Multi Delivery - SdUiAsMd*/
                    else if (generalFunc.retrieveValue("ENABLE_MULTI_VIEW_IN_SINGLE_DELIVERY").equalsIgnoreCase("Yes")) {
                        bn.putBoolean("fromMulti", true);
                        bn.putString("maxDestination", "1");
                    }
                    bn.putString("selType", Utils.CabGeneralType_Deliver);
                    bn.putBoolean("isRestart", false);
                    if (s.equalsIgnoreCase("MOTODELIVERY")) {
                        bn.putBoolean("emoto", true);
                    }
                    new ActUtils(mContext).startActWithData(MainActivity.class, bn);
                    break;

                case "RENTAL":
                case "MOTORENTAL":
                    bn.putString("selType", "rental");
                    bn.putBoolean("isRestart", false);
                    if (s.equalsIgnoreCase("MOTORENTAL")) {
                        bn.putBoolean("emoto", true);
                    }
                    new ActUtils(mContext).startActWithData(MainActivity.class, bn);
                    break;

                case "SERVICEPROVIDER":
                    getLatLongAddress(bn);
                    bn.putBoolean("isufx", true);
                    bn.putBoolean("isCarwash", true);
                    bn.putBoolean("isVideoConsultEnable", generalFunc.getJsonValueStr("eVideoConsultEnable", mDataObject).equalsIgnoreCase("Yes"));

                    bn.putString("SelectvVehicleType", generalFunc.getJsonValueStr("SelectvVehicleType", mDataObject));
                    bn.putString("SelectedVehicleTypeId", generalFunc.getJsonValueStr("iVehicleCategoryId", mDataObject));
                    bn.putString("parentId", generalFunc.getJsonValueStr("iParentId", mDataObject));

                    new ActUtils(mContext).startActWithData(MainActivity.class, bn);
                    break;

                case "GENIE":
                case "RUNNER":
                case "ANYWHERE":
                    bn.putString("vCategory", generalFunc.getJsonValueStr("vCategory", mDataObject));
                    bn.putString("eCatType", generalFunc.getJsonValueStr("eCatType", mDataObject));
                    bn.putString("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleCategoryId", mDataObject));
                    new ActUtils(mContext).startActWithData(GenieDeliveryHomeActivity.class, bn);
                    break;

                case "DELIVERALL":
                    goToDeliverAllScreen(generalFunc.getJsonValueStr("iServiceId", mDataObject));
                    break;

                case "MOREDELIVERY":
                    bn.putString("iVehicleCategoryId", generalFunc.getJsonValueStr("iVehicleCategoryId", mDataObject));
                    bn.putString("vCategory", generalFunc.getJsonValueStr("vCategory", mDataObject));
                    if (mDataObject.has("eFor") && generalFunc.getJsonValueStr("eFor", mDataObject).equalsIgnoreCase("DeliverAllCategory")) {
                        bn.putBoolean("isDeliverAll", true);
                    }
                    new ActUtils(mContext).startActWithData(CommonDeliveryTypeSelectionActivity.class, bn);
                    break;

                case "DONATION":
                    new ActUtils(mContext).startActWithData(DonationActivity.class, bn);
                    break;

                case "BIDDING":
                    getLatLongAddress(bn);
                    bn.putString("SelectvVehicleType", generalFunc.getJsonValueStr("vCategory", mDataObject));
                    bn.putString("SelectedVehicleTypeId", generalFunc.getJsonValueStr("iBiddingId", mDataObject));
                    //bn.putBoolean("isOther", true);
                    //bn.putString("SelectvVehicleType", generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
                    new ActUtils(mContext).startActWithData(RequestBidInfoActivity.class, bn);
                    break;

                case "RENTITEM":
                case "RENTESTATE":
                case "RENTCARS":
                    getLatLongAddress(bn);
                    bn.putString("iCategoryId", generalFunc.getJsonValueStr("iCategoryId", mDataObject));
                    bn.putString("eType", generalFunc.getJsonValueStr("eCatType", mDataObject));
                    new ActUtils(mContext).startActWithData(RentItemHomeActivity.class, bn);
                    break;

                case "NEARBY":
                    if (mDataObject.has("iCategoryId")) {
                        bn.putString("iCategoryId", generalFunc.getJsonValueStr("iCategoryId", mDataObject));
                    }
                    new ActUtils(mContext).startActWithData(NearByServicesActivity.class, bn);
                    break;

                case "RIDESHAREPUBLISH":
                    new ActUtils(mContext).startAct(RidePublish.class);
                    break;

                case "RIDESHAREBOOK":
                    new ActUtils(mContext).startAct(RideBook.class);
                    break;

                case "RIDESHAREMYRIDES":
                    new ActUtils(mContext).startAct(RideMyList.class);
                    break;

                case "TRACKSERVICE":
                    new ActUtils(mContext).startAct(TrackAnyList.class);
                    break;

                case "TRACKANYSERVICE":
                    bn.putString("vTitle", generalFunc.getJsonValueStr("vTitle", mDataObject));
                    bn.putString("MemberType", generalFunc.getJsonValueStr("MemberType", mDataObject));
                    new ActUtils(mContext).startActWithData(TrackAnyProfileSetup.class, bn);
                    break;
                case "TRACKSERVICEADD":
                    new ActUtils(mContext).startAct(TrackAnyProfileSetup.class);
                    break;


                default:
                    Logger.d("Action", "no open screen");
                    //generalFunc.showGeneralMessage("", "no open screen");
            }
        }
    }

    private void goToDeliverAllScreen(String iServiceId) {
        if (generalFunc.prefHasKey(Utils.iServiceId_KEY)) {
            generalFunc.removeValue(Utils.iServiceId_KEY);
        }
        HashMap<String, String> storeData = new HashMap<>();
        storeData.put(Utils.iServiceId_KEY, iServiceId);
        storeData.put("DEFAULT_SERVICE_CATEGORY_ID", iServiceId);
        generalFunc.storeData(storeData);

        getLanguageLabelServiceWise();
    }

    private void getLanguageLabelServiceWise() {
        new Handler().post(() -> {
            String LBLresponseString;
            LBLresponseString = generalFunc.retrieveValue("LanguagesForAllServiceType");
            if (LBLresponseString != null && !LBLresponseString.equalsIgnoreCase("")) {

                JSONArray messageArray = generalFunc.getJsonArray(Utils.message_str, LBLresponseString);
                if (messageArray != null) {
                    isServiceIdMatch = false;
                    for (int i = 0; i < messageArray.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(messageArray, i);
                        String iServiceId = generalFunc.getJsonValueStr("iServiceId", obj_temp);
                        if (iServiceId.equalsIgnoreCase(generalFunc.retrieveValue("DEFAULT_SERVICE_CATEGORY_ID"))) {

                            if (generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY).equalsIgnoreCase(generalFunc.getJsonValue("vCode", LBLresponseString))) {
                                manageView(LBLresponseString, generalFunc.getJsonValueStr("dataDic", obj_temp));
                                isServiceIdMatch = true;

                                break;
                            }
                        }
                    }
                    if (!isServiceIdMatch) {

                        getLBLServiceIDWise();

                        if (!(generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY).equalsIgnoreCase(generalFunc.getJsonValue("vCode", LBLresponseString)))) {
                            MyApp.getInstance().updateLangForAllServiceType(generalFunc, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                        }
                    }
                }
            }

        });
    }

    private void getLBLServiceIDWise() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "changelanguagelabel");
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("eSystem", Utils.eSystem_Type);

        ApiHandler.execute(mContext, parameters, true, false, generalFunc, responseString -> {
            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    manageView(responseString, generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    errorCallApi();
                }
            } else {
                errorCallApi();

            }
        });
    }

    private void manageView(String responseString, String lblkey) {
        generalFunc.storeData(Utils.languageLabelsKey, lblkey);
        generalFunc.storeData(Utils.LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vCode", responseString));
        generalFunc.storeData(Utils.LANGUAGE_IS_RTL_KEY, generalFunc.getJsonValue("eType", responseString));

        generalFunc.storeData(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY, generalFunc.getJsonValue("vGMapLangCode", responseString));
        GeneralFunctions.clearAndResetLanguageLabelsData(MyApp.getInstance().getApplicationContext());
        Utils.setAppLocal(mContext);

        Bundle bn = new Bundle();
        getLatLongAddress(bn);
        bn.putBoolean("isback", true);

        if (generalFunc.retrieveValue("CHECK_SYSTEM_STORE_SELECTION").equalsIgnoreCase("Yes")) {
            bn.putString("iCompanyId", generalFunc.getJsonValue("STORE_ID", responseString));
            bn.putString("ispriceshow", generalFunc.getJsonValue("ispriceshow", responseString));
            bn.putString("eAvailable", generalFunc.getJsonValue("eAvailable", responseString));
            bn.putString("timeslotavailable", generalFunc.getJsonValue("timeslotavailable", responseString));
            new ActUtils(mContext).startActForResult(RestaurantAllDetailsNewActivity.class, bn, 111);
        } else {
            new ActUtils(mContext).startActWithData(FoodDeliveryHomeActivity.class, bn);
        }
    }

    private void getLatLongAddress(Bundle bn) {
        bn.putString("address", Utils.checkText(mAddress) ? mAddress : "");

        bn.putString("latitude", Utils.checkText(mLatitude) ? mLatitude : "");
        bn.putString("lat", Utils.checkText(mLatitude) ? mLatitude : "");

        bn.putString("longitude", Utils.checkText(mLongitude) ? mLongitude : "");
        bn.putString("long", Utils.checkText(mLongitude) ? mLongitude : "");

    }

    private void errorCallApi() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id != 0) {
                getLanguageLabelServiceWise();
            }
            generateAlert.closeAlertBox();
        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_ERROR_TXT"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();
    }
}