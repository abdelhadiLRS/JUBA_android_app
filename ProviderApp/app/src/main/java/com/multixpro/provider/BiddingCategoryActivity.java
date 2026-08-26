package com.multixpro.provider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.activity.ParentActivity;
import com.adapter.files.CategoryListItem;
import com.adapter.files.PinnedBiddingServicesListAdapter;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.service.handler.ApiHandler;
import com.service.server.ServerTask;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.pinnedListView.PinnedSectionListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BiddingCategoryActivity extends ParentActivity {

    private ImageView imageCancel, editLocation;
    private static final int ADD_ADDRESS = 67;

    private ProgressBar loading;
    private ErrorView errorView;
    private String next_page_str = "", eSelectWorkLocation = "Fixed";

    private final ArrayList<CategoryListItem> mBiddingList = new ArrayList<>();
    private CategoryListItem[] mSections;
    private PinnedBiddingServicesListAdapter pinnedBiddingServicesListAdapter;
    private PinnedSectionListView category_list;

    private boolean mIsLoading = false, isNextPageAvailable = false, isSearch = false;
    private MTextView noResTxt, addressTxt;

    private View footerListView;
    private int submitBtnId;
    private EditText searchTxtView;
    private AVLoadingIndicatorView loaderView;
    private ServerTask currentCallExeWebServer;
    private LinearLayout searchView;
    private MButton btnBiddingService;

    private GenerateAlertBox currentAlertBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidding_category);

        ImageView backImgView = findViewById(R.id.backImgView);
        addToClickHandler(backImgView);

        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANANGE_BIDDING_SERVICES"));

        MTextView introTxt = findViewById(R.id.introTxt);
        introTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MANAGE_SERVICE_INTRO_TXT"));
        noResTxt = findViewById(R.id.noResTxt);

        locationView();

        searchView = findViewById(R.id.searchView);
        searchView.setVisibility(View.GONE);

        imageCancel = findViewById(R.id.imageCancel);
        addToClickHandler(imageCancel);
        imageCancel.setVisibility(View.GONE);
        loaderView = findViewById(R.id.loaderView);
        loaderView.setVisibility(View.GONE);

        loading = findViewById(R.id.loading);
        errorView = findViewById(R.id.errorView);
        category_list = findViewById(R.id.category_list);
        category_list.setShadowVisible(true);

        searchTxtView = findViewById(R.id.searchTxtView);
        searchTxtView.setHint(generalFunc.retrieveLangLBl("", "LBL_SEARCH_SERVICES"));
        searchTxtView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    isSearch = false;
                    imageCancel.setVisibility(View.GONE);
                    loaderView.setVisibility(View.GONE);
                    loaderView.setVisibility(View.VISIBLE);
                    getCategoryList(false, "", true);
                    Utils.hideKeyboard(getActContext());
                } else {
                    if (s.length() > 2) {
                        isSearch = true;
                        loaderView.setVisibility(View.VISIBLE);
                        imageCancel.setVisibility(View.GONE);
                        category_list.setVisibility(View.GONE);
                        new Handler(Looper.myLooper()).postDelayed(() -> {
                            //
                            getCategoryList(false, searchTxtView.getText().toString().trim(), true);
                        }, 750);
                    }
                }
            }
        });

        category_list.setFastScrollEnabled(false);
        category_list.setFastScrollAlwaysVisible(false);

        pinnedBiddingServicesListAdapter = new PinnedBiddingServicesListAdapter(getActContext(), mBiddingList, mSections);
        category_list.setAdapter(pinnedBiddingServicesListAdapter);

        getCategoryList(false, "", false);

        category_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(mIsLoading) && isNextPageAvailable) {
                    mIsLoading = true;
                    addFooterView();
                    getCategoryList(true, searchTxtView.getText().toString().trim(), false);
                } else if (!isNextPageAvailable) {
                    removeFooterView();
                }

            }
        });

        btnBiddingService = ((MaterialRippleLayout) findViewById(R.id.btnBiddingService)).getChildView();
        btnBiddingService.setText(generalFunc.retrieveLangLBl("", "LBL_UPDATE_SERVICES"));
        btnBiddingService.setVisibility(View.GONE);
        addToClickHandler(btnBiddingService);
        submitBtnId = Utils.generateViewId();
        btnBiddingService.setId(submitBtnId);
    }

    private void locationView() {
        MTextView workLocTitleTxt = findViewById(R.id.workLocTitleTxt);
        workLocTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_YOUR_JOB_LOCATION_TXT"));

        addressTxt = findViewById(R.id.addressTxt);
        editLocation = findViewById(R.id.editLocation);
        editLocation.setOnClickListener(v -> {

            ArrayList<String> requestPermissions = new ArrayList<>();
            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPermissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
            }
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            if (generalFunc.isAllPermissionGranted(false, requestPermissions, LOCATION_PERMISSIONS_REQUEST)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (MyApp.getInstance().locationPermissionReq(false)) {
                        addAddressActivity();
                    } else {
                        ArrayList<String> bgRequestPermissions = new ArrayList<>();
                        bgRequestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                        generalFunc.isAllPermissionGranted(true, bgRequestPermissions, BACKGROUND_LOCATION_PERMISSIONS_REQUEST);
                    }
                } else {
                    addAddressActivity();
                }
            } else {
                generalFunc.isAllPermissionGranted(true, requestPermissions, LOCATION_PERMISSIONS_REQUEST);
            }
        });
        handleWorkAddress(generalFunc.retrieveValue(Utils.WORKLOCATION));
    }

    private void addAddressActivity() {
        Bundle bn = new Bundle();

        bn.putString("latitude", "");
        bn.putString("longitude", "");
        bn.putString("address", Utils.getText(addressTxt));
        new ActUtils(getActContext()).startActForResult(AddAddressActivity.class, bn, ADD_ADDRESS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSIONS_REQUEST || requestCode == BACKGROUND_LOCATION_PERMISSIONS_REQUEST) {
            if (requestCode == BACKGROUND_LOCATION_PERMISSIONS_REQUEST) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ArrayList<String> bgRequestPermissions = new ArrayList<>();
                    bgRequestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (generalFunc.isAllPermissionGranted(false, bgRequestPermissions, BACKGROUND_LOCATION_PERMISSIONS_REQUEST)) {
                        addAddressActivity();
                    } else {
                        if (grantResults[0] == 1) {
                            showBackGroundLocationPermission();
                        } else {
                            showNoLocationPermission(requestCode);
                        }
                    }
                }
                return;
            }

            ArrayList<String> requestPermissions = new ArrayList<>();
            Collections.addAll(requestPermissions, permissions);

            if (generalFunc.isAllPermissionGranted(false, requestPermissions, requestCode)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ArrayList<String> bgRequestPermissions = new ArrayList<>();
                    bgRequestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (generalFunc.isAllPermissionGranted(false, bgRequestPermissions, BACKGROUND_LOCATION_PERMISSIONS_REQUEST)) {
                        addAddressActivity();
                    } else {
                        showBackGroundLocationPermission();
                    }
                } else {
                    addAddressActivity();
                }
            } else {
                int myCount = 0;
                for (String permission : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(BiddingCategoryActivity.this, permission)) {
                        myCount++;
                    }
                }
                if (permissions.length == myCount) {
                    showNoLocationPermission(requestCode);
                }
            }
        }
    }

    private void showNoLocationPermission(int requestCode) {
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
                "LBL_LOC_ALLOW_NOTE_ANDROID"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Allow All", "LBL_SETTINGS"),
                buttonId -> {
                    if (buttonId == 0) {
                        currentAlertBox.closeAlertBox();
                    } else {
                        generalFunc.openSettings(true, requestCode);
                    }
                });
    }

    private void showBackGroundLocationPermission() {
        currentAlertBox = generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_BACKGROUND_LOC_PER_TXT"), generalFunc.retrieveLangLBl("",
                "LBL_BG_LOC_ALLOW_NOTE_ANDROID"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"),
                buttonId -> {
                    if (buttonId == 0) {
                        currentAlertBox.closeAlertBox();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            ArrayList<String> bgRequestPermissions = new ArrayList<>();
                            bgRequestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                            generalFunc.isAllPermissionGranted(true, bgRequestPermissions, BACKGROUND_LOCATION_PERMISSIONS_REQUEST);
                        }
                    }
                });
    }

    private void handleWorkAddress(String WORKLOCATION) {
        if (Utils.checkText(WORKLOCATION)) {
            addressTxt.setText(WORKLOCATION);
            editLocation.setImageResource(R.mipmap.ic_edit);
            editLocation.setPadding(0, 0, 0, 0);
        } else {
            addressTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ADDRESS_TXT"));
            editLocation.setImageResource(R.drawable.ic_pic_add);
            editLocation.setPadding(20, 20, 20, 20);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ADDRESS) {
            if (resultCode == RESULT_OK) {
                String worklat = data.getStringExtra("Latitude");
                String worklong = data.getStringExtra("Longitude");
                String workadddress = data.getStringExtra("Address");
                updateWorkLocation(worklat, worklong, workadddress);
            }
        }
        if (requestCode == LOCATION_PERMISSIONS_REQUEST || requestCode == BACKGROUND_LOCATION_PERMISSIONS_REQUEST) {
            if (MyApp.getInstance().locationPermissionReq(false)) {
                addAddressActivity();
            }
        }
    }

    private void updateWorkLocation(String worklat, String worklong, String workaddress) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "UpdateDriverWorkLocationUFX");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("vWorkLocationLatitude", worklat);
        parameters.put("vWorkLocationLongitude", worklong);
        parameters.put("vWorkLocation", workaddress);

        if (generalFunc.retrieveValue(Utils.WORKLOCATION).equals("")) {
            parameters.put("eSelectWorkLocation", eSelectWorkLocation);
        }

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {

                    if (responseString != null && !responseString.equals("")) {
                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                            if (generalFunc.retrieveValue(Utils.WORKLOCATION).equals("")) {
                                eSelectWorkLocation = "Fixed";
                                parameters.put("eSelectWorkLocation", eSelectWorkLocation);
                            }
                            addressTxt.setText(workaddress);
                            generalFunc.storeData(Utils.WORKLOCATION, workaddress);
                            handleWorkAddress(workaddress);
                        }
                    } else {
                        generalFunc.showError();
                    }
                });
    }

    private void addFooterView() {
        removeFooterView();
        if (footerListView == null) {
            footerListView = (LayoutInflater.from(getActContext())).inflate(R.layout.footer_list, category_list, false);
        }
        category_list.addFooterView(footerListView);
    }

    private void removeFooterView() {
        if (footerListView == null) {
            return;
        }
        category_list.removeFooterView(footerListView);
        footerListView = null;
    }

    private void removeNextPageConfig() {
        next_page_str = "";
        isNextPageAvailable = false;
        mIsLoading = false;
        removeFooterView();
    }

    private Context getActContext() {
        return BiddingCategoryActivity.this;
    }

    private void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    private void getCategoryList(final boolean isLoadMore, String searchText, boolean isSearch) {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (!isSearch) {
            if (loading.getVisibility() != View.VISIBLE) {
                loading.setVisibility(!isLoadMore ? View.VISIBLE : View.GONE);
            }
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getBiddingServices");
        parameters.put("iDriverId", generalFunc.getMemberId());
        if (isLoadMore) {
            parameters.put("page", next_page_str);
        }
        if (searchText.length() > 2) {
            loading.setVisibility(View.GONE);
            parameters.put("search_keyword", searchText);
        }
        if (currentCallExeWebServer != null) {
            currentCallExeWebServer.cancel(true);
            currentCallExeWebServer = null;
        }

        currentCallExeWebServer = ApiHandler.execute(getActContext(), parameters, responseString -> {
            if (generalFunc.getJsonValueStr("ENABLE_SEARCH_UFX_SERVICES", obj_userProfile).equalsIgnoreCase("YES")) {
                searchView.setVisibility(View.VISIBLE);
            } else {
                searchView.setVisibility(View.GONE);
            }
            mBiddingList.clear();

            noResTxt.setVisibility(View.GONE);
            loaderView.setVisibility(View.GONE);

            JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

            if (responseStringObject != null && !responseStringObject.toString().equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject)) {
                    category_list.setVisibility(View.VISIBLE);
                    btnBiddingService.setVisibility(View.VISIBLE);

                    if (this.isSearch) {
                        loaderView.setVisibility(View.GONE);
                        imageCancel.setVisibility(View.VISIBLE);
                    }

                    String nextPage = generalFunc.getJsonValueStr("NextPage", responseStringObject);

                    mSections = new CategoryListItem[generalFunc.getJsonArray(Utils.message_str, responseStringObject).length()];
                    JSONArray mainListArr = generalFunc.getJsonArray(Utils.message_str, responseStringObject);

                    int sectionPosition = 0, listPosition = 0;
                    for (int i = 0; i < mainListArr.length(); i++) {
                        JSONObject tempJson = generalFunc.getJsonObject(mainListArr, i);
                        String vCategory = generalFunc.getJsonValueStr("vCategory", tempJson);
                        CategoryListItem section = new CategoryListItem(CategoryListItem.getSECTION(), vCategory);
                        section.setSectionPosition(sectionPosition);
                        section.setListPosition(listPosition++);
                        section.setCountSubItems(GeneralFunctions.parseIntegerValue(0, vCategory));

                        mSections[sectionPosition] = section;

                        mBiddingList.add(section);

                        JSONArray subListArr = generalFunc.getJsonArray("SubCategory", tempJson);

                        for (int j = 0; j < subListArr.length(); j++) {
                            JSONObject subTempJson = generalFunc.getJsonObject(subListArr, j);

                            CategoryListItem categoryListItem = new CategoryListItem(CategoryListItem.getITEM(), generalFunc.getJsonValueStr("vCategory", tempJson));
                            categoryListItem.setSectionPosition(sectionPosition);
                            categoryListItem.setListPosition(listPosition++);
                            categoryListItem.setvTitle(generalFunc.getJsonValueStr("vTitle", subTempJson));
                            String resizeImageUrl = Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr("vLogo_image", subTempJson), 50, 50);
                            categoryListItem.setvLogo(resizeImageUrl);

                            categoryListItem.setiVehicleCategoryId(generalFunc.getJsonValueStr("iBiddingId", subTempJson));
                            categoryListItem.setvCategory(generalFunc.getJsonValueStr("eServiceRequest", subTempJson));

                            mBiddingList.add(categoryListItem);
                        }
                        sectionPosition++;
                    }

                    if (!nextPage.equals("") && !nextPage.equals("0")) {
                        next_page_str = nextPage;
                        isNextPageAvailable = true;
                    } else {
                        removeNextPageConfig();
                    }
                    pinnedBiddingServicesListAdapter.changeSection(mSections);
                    pinnedBiddingServicesListAdapter.notifyDataSetChanged();
                    pinnedBiddingServicesListAdapter.manageBiddingArraySize();
                } else {
                    noResTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                    noResTxt.setVisibility(View.VISIBLE);
                    btnBiddingService.setVisibility(View.GONE);
                }
            } else {
                generateErrorView();
            }
            closeLoader();

            mIsLoading = false;
        });
    }

    private void generateErrorView() {
        closeLoader();
        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");
        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(() -> getCategoryList(false, searchTxtView.getText().toString().trim(), false));
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        } else if (i == R.id.imageCancel) {
            loaderView.setVisibility(View.GONE);
            searchTxtView.setText("");
            category_list.setVisibility(View.GONE);
            getCategoryList(false, "", true);
        } else if (i == submitBtnId) {
            if (Utils.checkText(generalFunc.retrieveValue(Utils.WORKLOCATION))) {
                String selectedIDList = pinnedBiddingServicesListAdapter.getSelectedIDList();
                addService(selectedIDList);
            } else {
                generalFunc.showMessage(addressTxt, generalFunc.retrieveLangLBl("", "LBL_ENTER_WORK_LOC_TXT"));
            }
        }
    }

    private void addService(String selectedIDList) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updateDriverBiddingServices");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("iBiddingId", selectedIDList);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

                    if (responseStringObject != null && !responseStringObject.toString().equals("")) {
                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject)) {
                            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                            generateAlert.setCancelable(false);
                            generateAlert.setBtnClickList(btn_id -> {
                                generateAlert.closeAlertBox();
                                onBackPressed();
                            });
                            generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                            generateAlert.showAlertBox();
                        } else {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });
    }
}