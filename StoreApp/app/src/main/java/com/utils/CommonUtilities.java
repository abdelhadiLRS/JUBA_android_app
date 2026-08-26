package com.utils;

public class CommonUtilities {

    public static final String idNo = "86";
    public static final String bNumber = "627526";
    public static final String pName = "multixpro";
    //public static final String SERVER = "https://www.v3cprojectsdemo.com/deploy/lspro/";
    //public static final String SERVER = "https://www.v3cprojectsdemo.com/deploy/lspro/beta/";
    public static final String SERVER = "https://multixpro.cloneappsolutions.com/";
    //public static final String SERVER = "https://www.localservicespro.com/beta/";
    //--------------------------------------------------------------------------------------------
    public static final String SERVER_FOLDER_PATH = "";
    public static final String SERVER_WEBSERVICE_PATH = SERVER_FOLDER_PATH + "webservice_shark.php?";

    public static final String SERVER_URL = SERVER + SERVER_FOLDER_PATH;
    public static final String SERVER_URL_WEBSERVICE = SERVER + SERVER_WEBSERVICE_PATH + "?";
    public static final String SERVER_URL_PHOTOS = SERVER_URL + "webimages/";

    public static final String USER_PHOTO_PATH = CommonUtilities.SERVER_URL_PHOTOS + "upload/Passenger/";
    public static final String PROVIDER_PHOTO_PATH = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/";
    public static final String COMPANY_PHOTO_PATH = CommonUtilities.SERVER_URL_PHOTOS + "upload/Company/";

    public static final String BUCKET_NAME = "system_" + pName + "_" + bNumber;
    public static final String BUCKET_FILE_NAME = "ANDROID_STORE_" + pName + "_" + bNumber + ".txt";
    public static final String BUCKET_PATH = "https://storage.googleapis.com/" + BUCKET_NAME + "/" + BUCKET_FILE_NAME;

    public static String OriginalDateFormate = "dd MMM, yyyy (EEE)";
    public static String WithoutDayFormat = "dd MMM, yyyy";
    public static String DayFormatEN = "yyyy-MM-dd";
    public static String OriginalTimeFormate = "hh:mm aa";

    public static final int MY_THERMAL_REQ_CODE = 100;
}