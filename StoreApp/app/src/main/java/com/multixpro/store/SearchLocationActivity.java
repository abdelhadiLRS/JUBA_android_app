package com.multixpro.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.adapter.files.PlacesAdapter;
import com.general.files.ActUtils;
import com.general.files.DividerItemDecoration;
import com.general.files.GeneralFunctions;
import com.general.files.RecurringTask;
import com.service.handler.ApiHandler;
import com.service.handler.AppService;
import com.service.model.DataProvider;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchLocationActivity extends ParentActivity implements PlacesAdapter.setRecentLocClickList {

    public boolean isAddressEnable;
    String whichLocation = "";
    MTextView cancelTxt;
    RecyclerView placesRecyclerView;
    EditText searchTxt;
    ArrayList<HashMap<String, String>> placelist;
    PlacesAdapter placesAdapter;
    ImageView imageCancel;
    MTextView noPlacedata;

    ImageView googleimagearea;

    String session_token = "";
    int MIN_CHAR_REQ_GOOGLE_AUTO_COMPLETE = 2;
    String currentSearchQuery = "";
    RecurringTask sessionTokenFreqTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);


        googleimagearea = (ImageView) findViewById(R.id.googleimagearea);
        cancelTxt = (MTextView) findViewById(R.id.cancelTxt);
        cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

        placesRecyclerView = (RecyclerView) findViewById(R.id.placesRecyclerView);
        placesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Utils.hideKeyboard(getActContext());
            }
        });
        searchTxt = (EditText) findViewById(R.id.searchTxt);
        searchTxt.setHint(generalFunc.retrieveLangLBl("Search", "LBL_Search"));


        addToClickHandler(cancelTxt);
        imageCancel = (ImageView) findViewById(R.id.imageCancel);
        noPlacedata = (MTextView) findViewById(R.id.noPlacedata);
        addToClickHandler(imageCancel);

        placelist = new ArrayList<>();
        MIN_CHAR_REQ_GOOGLE_AUTO_COMPLETE = GeneralFunctions.parseIntegerValue(2, generalFunc.getJsonValueStr("MIN_CHAR_REQ_GOOGLE_AUTO_COMPLETE", obj_userProfile));

        setWhichLocationAreaSelected(getIntent().getStringExtra("locationArea"));


        searchTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If it loses focus...
                if (!hasFocus) {
                    Utils.hideSoftKeyboard((Activity) getActContext(), searchTxt);
                } else {
                    Utils.showSoftKeyboard((Activity) getActContext(), searchTxt);
                }
            }
        });


        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (currentSearchQuery.equals(s.toString().trim())) {
                    return;
                }

                currentSearchQuery = searchTxt.getText().toString();

                if (s.length() >= MIN_CHAR_REQ_GOOGLE_AUTO_COMPLETE) {
                    if (session_token.trim().equalsIgnoreCase("")) {
                        session_token = Utils.userType + "_" + generalFunc.getMemberId() + "_" + System.currentTimeMillis();
                        initializeSessionRegeneration();
                    }

                    placesRecyclerView.setVisibility(View.VISIBLE);

                    getGooglePlaces(currentSearchQuery);
                } else {
                    placesRecyclerView.setVisibility(View.GONE);
                    noPlacedata.setVisibility(View.GONE);
                }

            }
        });

        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    getSearchGooglePlace(v.getText().toString());


                    return true;
                }
                return false;
            }
        });


    //    placesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        placesRecyclerView.setLayoutManager(mLayoutManager);
        placesRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        placesRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    public void getSearchGooglePlace(String input) {
        noPlacedata.setVisibility(View.GONE);

        String serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_COMPANY_APP_KEY);

        String url = null;
        //   URLEncoder.encode(input.replace(" ", "%20"), "UTF-8")
        try {


            String s = input.trim();
            String[] split = s.split("\\s+");


            /* url = "https://maps.googleapis.com/maps/api/place/queryautocomplete/json?input=" + *//*input.replace(" ", "%20")*//*URLEncoder.encode(input*//*.replace(" ", "%20")*//*, "utf8") + "&key=" + serverKey +
                    "&language=" + generalFunc.retrieveValue(CommonUtilities.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";
*/
            url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + /*input.replace(" ", "%20")*/URLEncoder.encode(input/*.replace(" ", "%20")*/, "utf8") + "&key=" + serverKey + "&inputtype=" + "textquery" + "&fields=" + "photos,formatted_address,name,rating,geometry" +
                    "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";


            if (getIntent().getDoubleExtra("long", 0.0) != 0.0) {

                url = url + "&location=" + getIntent().getDoubleExtra("lat", 0.0) + "," + getIntent().getDoubleExtra("long", 0.0) + "&radius=20";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (url == null) {
            return;
        }

        ApiHandler.execute(getActContext(), url, true, false, generalFunc,

                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);
                    if (generalFunc.getJsonValue("status", responseObj).equals("OK")) {
                        JSONArray candidatesArr = generalFunc.getJsonArray("candidates", responseObj);

                        if (searchTxt.getText().toString().length() == 0) {
                            placesRecyclerView.setVisibility(View.GONE);
                            noPlacedata.setVisibility(View.GONE);
                            googleimagearea.setVisibility(View.GONE);

                            return;
                        }

                        placelist.clear();
                        for (int i = 0; i < candidatesArr.length(); i++) {
                            JSONObject item = generalFunc.getJsonObject(candidatesArr, i);

                            if (!generalFunc.getJsonValue("formatted_address", item).equals("")) {

                                HashMap<String, String> map = new HashMap<String, String>();

                                String structured_formatting = generalFunc.getJsonValueStr("structured_formatting", item);
                                /* map.put("main_text", generalFunc.getJsonValue("formatted_address", structured_formatting));*/

                                map.put("main_text", generalFunc.getJsonValueStr("formatted_address", item));
                                map.put("secondary_text", "");
                                map.put("place_id", "");
                                map.put("description", generalFunc.getJsonValueStr("name", item));

                                map.put("lat", generalFunc.getJsonValueStr("lat", generalFunc.getJsonObject("location", generalFunc.getJsonObject("geometry", item))));
                                map.put("lng", generalFunc.getJsonValueStr("lng", generalFunc.getJsonObject("location", generalFunc.getJsonObject("geometry", item))));


                                placelist.add(map);
                            }

                        }

                        if (placelist.size() > 0) {

                            String RESPONSE_DATA = generalFunc.getJsonValueStr("RESPONSE_DATA", responseObj);
                            if (Utils.checkText(RESPONSE_DATA)) {
                                JSONObject RESPONSE_DATA_OBJ = generalFunc.getJsonObject(RESPONSE_DATA);
                                String vServiceName = generalFunc.getJsonValueStr("vServiceName", RESPONSE_DATA_OBJ);
                                if (!RESPONSE_DATA_OBJ.has("vServiceName") || (Utils.checkText(vServiceName) && vServiceName.equalsIgnoreCase("Google"))) {
                                    googleimagearea.setVisibility(View.VISIBLE);
                                }
                            }
                            placesRecyclerView.setVisibility(View.VISIBLE);
                            noPlacedata.setVisibility(View.GONE);

                            if (placesAdapter == null) {
                                placesAdapter = new PlacesAdapter(getActContext(), placelist);
                                placesRecyclerView.setAdapter(placesAdapter);
                                placesAdapter.itemRecentLocClick(SearchLocationActivity.this);

                            } else {
                                placesAdapter.notifyDataSetChanged();
                            }
                        }
                    } else if (generalFunc.getJsonValue("status", responseObj).equals("ZERO_RESULTS")) {
                        placelist.clear();
                        if (placesAdapter != null) {
                            placesAdapter.notifyDataSetChanged();
                        }

                        String msg = generalFunc.retrieveLangLBl("We didn't find any places matched to your entered place. Please try again with another text.", "LBL_NO_PLACES_FOUND");
                        noPlacedata.setText(msg);
                        placesRecyclerView.setVisibility(View.GONE);
                        googleimagearea.setVisibility(View.GONE);
                        noPlacedata.setVisibility(View.VISIBLE);


                    } else {
                        placelist.clear();
                        if (placesAdapter != null) {
                            placesAdapter.notifyDataSetChanged();
                        }
                        String msg = "";
                        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
                            msg = generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT");

                        } else {
                            msg = generalFunc.retrieveLangLBl("Error occurred while searching nearest places. Please try again later.", "LBL_PLACE_SEARCH_ERROR");

                        }

                        noPlacedata.setText(msg);

                        placesRecyclerView.setVisibility(View.GONE);
                        noPlacedata.setVisibility(View.VISIBLE);

                    }

                });

    }

    public void initializeSessionRegeneration() {

        if (sessionTokenFreqTask != null) {
            sessionTokenFreqTask.stopRepeatingTask();
        }
        sessionTokenFreqTask = new RecurringTask(170000);
        sessionTokenFreqTask.setTaskRunListener(() -> session_token = Utils.userType + "_" + generalFunc.getMemberId() + "_" + System.currentTimeMillis());

        sessionTokenFreqTask.startRepeatingTask();
    }

    @Override
    public void itemRecentLocClick(int position) {

        //  getSelectAddresLatLong(placelist.get(position).get("place_id"), placelist.get(position).get("description"));
        // getSelectAddresLatLong(placelist.get(position).get("place_id"), placelist.get(position).get("description"), placelist.get(position).get("session_token"), placelist.get(position).get("lat"), placelist.get(position).get("lng"));

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Place_id", placelist.get(position).get("Place_id"));
        hashMap.put("description", placelist.get(position).get("description"));
        hashMap.put("session_token", placelist.get(position).get("session_token"));

        String latitude = "";
        String longitude = "";

        if (getIntent().getDoubleExtra("long", 0.0) != 0.0) {
            latitude = getIntent().getDoubleExtra("lat", 0.0) + "";
            longitude = getIntent().getDoubleExtra("long", 0.0) + "";
        }

        if (placelist.get(position).get("Place_id") == null || placelist.get(position).get("Place_id").equals("")) {


            HashMap<String, Object> data_dict = new HashMap<>();
            data_dict.put("ADDRESS", placelist.get(position).get("description"));
            data_dict.put("LATITUDE", GeneralFunctions.parseDoubleValue(0.0, placelist.get(position).get("latitude")));
            data_dict.put("LONGITUDE", GeneralFunctions.parseDoubleValue(0.0, placelist.get(position).get("longitude")));
            data_dict.put("RESPONSE_TYPE", AppService.Service.PLACE_DETAILS);
            handlePlaceDeailsRespose(data_dict);
            Logger.d("Address", "::111" + placelist.get(position).get("description"));
        } else {
            hashMap.put("vServiceId", placelist.get(position).get("vServiceId"));
            Logger.d("Address", "::222" + placelist.get(position).get("description"));
            AppService.getInstance().executeService(getActContext(), new DataProvider.DataProviderBuilder(latitude, longitude).setPlaceId(placelist.get(position).get("Place_id")).setServiceId(placelist.get(position).get("vServiceId")).setData_Str(placelist.get(position).get("description")).setToken(session_token).build(), AppService.Service.PLACE_DETAILS, data -> handlePlaceDeailsRespose(data)
            );
        }

    }

    private void handlePlaceDeailsRespose(HashMap<String, Object> data) {

        if (data.get("RESPONSE_TYPE") != null && data.get("RESPONSE_TYPE").toString().equalsIgnoreCase("FAIL")) {
            return;
        }
        Logger.d("handlePlaceDeailsRespose", "::" + data);
        Bundle bn = new Bundle();
        bn.putString("Address", data.get("ADDRESS").toString());
        bn.putString("Latitude", data.get("LATITUDE").toString());
        bn.putString("Longitude", data.get("LONGITUDE").toString());

        Utils.hideKeyboard(getActContext());

        new ActUtils(getActContext()).setOkResult(bn);


        finish();

    }

    public void setWhichLocationAreaSelected(String locationArea) {
        this.whichLocation = locationArea;
    }

    public Context getActContext() {
        return SearchLocationActivity.this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void getGooglePlaces(String input) {

        noPlacedata.setVisibility(View.GONE);
        String session_token = this.session_token;
        String latitude = "";
        String longitude = "";

        if (getIntent().getDoubleExtra("long", 0.0) != 0.0) {

            latitude = getIntent().getDoubleExtra("lat", 0.0) + "";
            longitude = getIntent().getDoubleExtra("long", 0.0) + "";
        }

        AppService.getInstance().executeService(getActContext(), new DataProvider.DataProviderBuilder(latitude, longitude).setData_Str(input).setToken(session_token).build(), AppService.Service.PLACE_SUGGESTIONS, new AppService.ServiceDelegate() {
            @Override
            public void onResult(HashMap<String, Object> data) {
                noPlacedata.setVisibility(View.GONE);
                if (data.get("RESPONSE_TYPE") != null && data.get("RESPONSE_TYPE").toString().equalsIgnoreCase("FAIL")) {

                    placelist.clear();
                    if (placesAdapter != null) {
                        placesAdapter.notifyDataSetChanged();
                    }

                    String msg = generalFunc.retrieveLangLBl("We didn't find any places matched to your entered place. Please try again with another text.", "LBL_NO_PLACES_FOUND");
                    noPlacedata.setText(msg);
                    placesRecyclerView.setVisibility(View.VISIBLE);

                    noPlacedata.setVisibility(View.VISIBLE);

                    return;
                }
                searchResult((ArrayList<HashMap<String, String>>) data.get("PLACE_SUGGESTION_DATA"), data);

            }
        });
    }

    private void searchResult(JSONObject responseString) {
        if (generalFunc.getJsonValueStr("status", responseString).equals("OK")) {
            JSONArray predictionsArr = generalFunc.getJsonArray("predictions", responseString);

            placelist.clear();
            for (int i = 0; i < predictionsArr.length(); i++) {
                JSONObject item = generalFunc.getJsonObject(predictionsArr, i);

                if (!generalFunc.getJsonValue("place_id", item).equals("")) {

                    HashMap<String, String> map = new HashMap<String, String>();

                    String structured_formatting = generalFunc.getJsonValueStr("structured_formatting", item);
                    map.put("main_text", generalFunc.getJsonValue("main_text", structured_formatting));
                    map.put("secondary_text", generalFunc.getJsonValue("secondary_text", structured_formatting));
                    map.put("place_id", generalFunc.getJsonValueStr("place_id", item));
                    map.put("description", generalFunc.getJsonValueStr("description", item));
                    map.put("session_token", session_token);

                    placelist.add(map);

                }
            }
            if (placelist.size() > 0) {
                placesRecyclerView.setVisibility(View.VISIBLE);

                if (placesAdapter == null) {
                    placesAdapter = new PlacesAdapter(getActContext(), placelist);
                    placesRecyclerView.setAdapter(placesAdapter);
                    placesAdapter.itemRecentLocClick(SearchLocationActivity.this);

                } else {
                    placesAdapter.notifyDataSetChanged();
                }
            }
        } else if (generalFunc.getJsonValue("status", responseString).equals("ZERO_RESULTS")) {
            placelist.clear();
            if (placesAdapter != null) {
                placesAdapter.notifyDataSetChanged();
            }

            String msg = generalFunc.retrieveLangLBl("We didn't find any places matched to your entered place. Please try again with another text.", "LBL_NO_PLACES_FOUND");
            noPlacedata.setText(msg);
            placesRecyclerView.setVisibility(View.VISIBLE);

            noPlacedata.setVisibility(View.VISIBLE);

        } else {
            placelist.clear();
            if (placesAdapter != null) {
                placesAdapter.notifyDataSetChanged();
            }
            String msg = "";
            if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
                msg = generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT");

            } else {
                msg = generalFunc.retrieveLangLBl("Error occurred while searching nearest places. Please try again later.", "LBL_PLACE_SEARCH_ERROR");

            }

            noPlacedata.setText(msg);
            placesRecyclerView.setVisibility(View.VISIBLE);

            noPlacedata.setVisibility(View.VISIBLE);
        }

    }

    public void getSelectAddresLatLong(String Place_id, final String address, String session_token, String lat, String lng) {


        if (lat == null || lat.equalsIgnoreCase("") || lng
                == null || lng.equalsIgnoreCase("")) {
            String serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_COMPANY_APP_KEY);


            String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + Place_id + "&key=" + serverKey +
                    "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true&fields=formatted_address,name,geometry&sessiontoken=" + session_token;


            ApiHandler.execute(getActContext(), url, true, true, generalFunc,
                    responseString -> {
                        JSONObject responseObj = generalFunc.getJsonObject(responseString);
                        if (generalFunc.getJsonValue("status", responseObj).equals("OK")) {
                            String resultObj = generalFunc.getJsonValueStr("result", responseObj);
                            String geometryObj = generalFunc.getJsonValue("geometry", resultObj);
                            String locationObj = generalFunc.getJsonValue("location", geometryObj);
                            String latitude = generalFunc.getJsonValue("lat", locationObj);
                            String longitude = generalFunc.getJsonValue("lng", locationObj);

                            Bundle bn = new Bundle();
                            bn.putString("Address", address);
                            bn.putString("Latitude", "" + latitude);
                            bn.putString("Longitude", "" + longitude);

                            new ActUtils(getActContext()).setOkResult(bn);

                            finish();


                        }


                    });
        } else if (!lat.equals("") && !lng.equalsIgnoreCase("")) {
            Bundle bn = new Bundle();
            bn.putString("Address", address);
            bn.putString("Latitude", "" + lat);
            bn.putString("Longitude", "" + lng);
            bn.putBoolean("isSkip", false);
            new ActUtils(getActContext()).setOkResult(bn);
            finish();

        }

    }


    public void searchResult(ArrayList<HashMap<String, String>> placelist, HashMap<String, Object> data) {
        if (placelist == null) {

            String msg = generalFunc.retrieveLangLBl("We didn't find any places matched to your entered place. Please try again with another text.", "LBL_TRY_AGAIN_TXT");
            noPlacedata.setText(msg);
            placesRecyclerView.setVisibility(View.VISIBLE);

            noPlacedata.setVisibility(View.VISIBLE);

            return;
        }

        this.placelist.clear();
        this.placelist.addAll(placelist);
        imageCancel.setVisibility(View.VISIBLE);


        if (currentSearchQuery.length() == 0) {
            placesRecyclerView.setVisibility(View.GONE);
            noPlacedata.setVisibility(View.GONE);

            return;
        }


        if (placelist.size() > 0) {
            placesRecyclerView.setVisibility(View.VISIBLE);
            if (placesAdapter == null) {
                placesAdapter = new PlacesAdapter(getActContext(), this.placelist);
                placesRecyclerView.setAdapter(placesAdapter);
                placesAdapter.itemRecentLocClick(SearchLocationActivity.this);

            } else {
                placesAdapter.notifyDataSetChanged();
            }
            JSONObject jsonObject = new JSONObject(data);

            String RESPONSE_DATA = generalFunc.getJsonValueStr("RESPONSE_DATA", jsonObject);
            googleimagearea.setVisibility(View.GONE);
            if (Utils.checkText(RESPONSE_DATA)) {
                JSONObject RESPONSE_DATA_OBJ = generalFunc.getJsonObject(RESPONSE_DATA);
                String vServiceName = generalFunc.getJsonValueStr("vServiceName", RESPONSE_DATA_OBJ);
                if (Utils.checkText(vServiceName) && vServiceName.equalsIgnoreCase("Google")) {
                    googleimagearea.setVisibility(View.VISIBLE);
                }
            }
        } else if (currentSearchQuery.length() == 0) {
            placelist.clear();
            if (placesAdapter != null) {
                placesAdapter.notifyDataSetChanged();
            }

            String msg = generalFunc.retrieveLangLBl("We didn't find any places matched to your entered place. Please try again with another text.", "LBL_NO_PLACES_FOUND");
            noPlacedata.setText(msg);
            placesRecyclerView.setVisibility(View.VISIBLE);

            noPlacedata.setVisibility(View.VISIBLE);

            return;
        } else {

            placelist.clear();
            if (placesAdapter != null) {
                placesAdapter.notifyDataSetChanged();
            }
            String msg = "";
            if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
                msg = generalFunc.retrieveLangLBl("No Internet Connection", "LBL_NO_INTERNET_TXT");

            } else {
                msg = generalFunc.retrieveLangLBl("Error occurred while searching nearest places. Please try again later.", "LBL_PLACE_SEARCH_ERROR");

            }

            noPlacedata.setText(msg);
            placesRecyclerView.setVisibility(View.VISIBLE);
            noPlacedata.setVisibility(View.VISIBLE);

            //} else if (generalFunc.getJsonValue("status", responseString).equals("ZERO_RESULTS")) {
        }


    }

//    @Override
//    public void resetOrAddDest(int selPos, String address, double latitude, double longitude, String isSkip) {
//        Bundle bn = new Bundle();
//        bn.putString("Address", address);
//        bn.putString("Latitude", "" + latitude);
//        bn.putString("Longitude", "" + longitude);
//        if (Utils.checkText(isSkip)) {
//            bn.putBoolean("isSkip", isSkip.equalsIgnoreCase("true") ? true : false);
//        }
//
//        Utils.hideKeyboard(this);
//
//        new ActUtils(getActContext()).setOkResult(bn);
//
//
//        finish();
//    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.cancelTxt) {
            finish();

        } else if (i == R.id.imageCancel) {
            placesRecyclerView.setVisibility(View.GONE);
            searchTxt.setText("");
            noPlacedata.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onDestroy() {
        if (sessionTokenFreqTask != null) {
            sessionTokenFreqTask.stopRepeatingTask();
        }
        super.onDestroy();
    }
}
