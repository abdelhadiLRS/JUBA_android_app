package com.utils;

import java.util.ArrayList;

public class CommonUtilities {

    public static final String idNo = "86";
    public static final String bNumber = "627526";
   // public static final String pName = "lsprox";
 public static final String pName = "multixpro";

 //   public static final String SERVER = "https://yupixpro.yupiglobal.com/";
// public static final String SERVER = "https://oskey.fun";

   public static final String SERVER = "https://multixpro.cloneappsolutions.com/";

    public static final String TOLLURL = "https://fleet.api.here.com/2/calculateroute.json?app_id=";
    public static final String SERVER_FOLDER_PATH = "";
    public static final String WEBSERVICE = "webservice_shark.php";
    public static final String SERVER_WEBSERVICE_PATH = SERVER_FOLDER_PATH + WEBSERVICE + "?";

    public static final String SERVER_URL = SERVER + SERVER_FOLDER_PATH;
    public static final String SERVER_URL_WEBSERVICE = SERVER + SERVER_WEBSERVICE_PATH + "?";
    public static final String SERVER_URL_PHOTOS = SERVER_URL + "webimages/";

    public static final String LINKEDINLOGINLINK = SERVER + "linkedin-login/linkedin-app.php";
    public static final String PAYMENTLINK = SERVER + "assets/libraries/webview/payment_configuration_trip.php?";

    public static final String USER_PHOTO_PATH = CommonUtilities.SERVER_URL_PHOTOS + "upload/Passenger/";
    public static final String PROVIDER_PHOTO_PATH = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/";
    public static final String STORE_PHOTO_PATH = CommonUtilities.SERVER_URL_PHOTOS + "upload/Company/";

    public static final String BUCKET_NAME = "system_" + pName + "_" + bNumber;
    public static final String BUCKET_FILE_NAME = "ANDROID_USER_" + pName + "_" + bNumber + ".txt";
    public static final String BUCKET_PATH = "https://storage.googleapis.com/" + BUCKET_NAME + "/" + BUCKET_FILE_NAME;

//    public static final String DELIVER_ALL_PATH = "https://storage.googleapis.com/" + BUCKET_NAME + "/" + BUCKET_FILE_NAME;

    public static String OriginalDateFormate = "dd MMM, yyyy (EEE)";
    public static String OriginalTimeFormate = "hh:mm aa";
    public static String WithoutDayFormat = "dd MMM, yyyy";
    public static String DayFormatEN = "yyyy-MM-dd";
    public static String DayTimeFormat = "dd MMM, yyyy hh:mm aa";
    public static ArrayList<String> ageRestrictServices = new ArrayList<>();
}