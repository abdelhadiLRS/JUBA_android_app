package com.multixpro.user.nearbyservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.activity.ParentActivity;
import com.adapter.files.CommonBanner23Adapter;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.GetLocationUpdates;
import com.multixpro.user.R;
import com.multixpro.user.SearchLocationActivity;
import com.multixpro.user.databinding.ActivityNearByServiceBinding;
import com.multixpro.user.nearbyservice.adapter.NearByCategoryAdapter;
import com.multixpro.user.nearbyservice.adapter.NearByServiceAdapter;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NearByServicesActivity extends ParentActivity implements GetAddressFromLocation.AddressFound, GetLocationUpdates.LocationUpdates {

    private ActivityNearByServiceBinding binding;

    private GetLocationUpdates getLastLocation;
    private GetAddressFromLocation getAddressFromLocation;
    private String mAddress = "", mLatitude = "0.0", mLongitude = "0.0";
    private boolean isFirst = false, isFilter = true;

    private String iCategoryId = "", mSearchText = "";

    private CommonBanner23Adapter mBannerAdapter;
    private JSONArray bannerListArray;

    private NearByCategoryAdapter nearByCategoryAdapter;
    private final ArrayList<HashMap<String, String>> mServiceCategoryList = new ArrayList<>();

    private NearByServiceAdapter mNearByServiceAdapter;
    private final ArrayList<HashMap<String, String>> placesList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_near_by_service);

        iCategoryId = getIntent().getStringExtra("iCategoryId");
        if (!Utils.checkText(iCategoryId)) {
            return;
        }

        mLatitude = getIntent().getStringExtra("latitude");
        mLongitude = getIntent().getStringExtra("longitude");
        mAddress = getIntent().getStringExtra("address");

        if (Utils.checkText(mLatitude) && Utils.checkText(mLongitude) && Utils.checkText(mAddress)) {
            isFirst = true;
            Location temploc = new Location("source");
            temploc.setLatitude(GeneralFunctions.parseDoubleValue(0.0, mLatitude));
            temploc.setLongitude(GeneralFunctions.parseDoubleValue(0.0, mLongitude));
            onLocationUpdate(temploc);
            binding.headerAddressTxt.setText(mAddress);
        } else {
            binding.headerAddressTxt.setHint(generalFunc.retrieveLangLBl("", "LBL_LOAD_ADDRESS"));
            getLastLocation = new GetLocationUpdates(this, Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, false, this);
        }

        topAreaDataSet();
        bannerData();
        mainData();
    }

    private void topAreaDataSet() {
        addToClickHandler(binding.headerAddressTxt);

        if (generalFunc.isRTLmode()) {
            binding.backImgView.setRotation(180);
        }

        addToClickHandler(binding.backImgView);
        addToClickHandler(binding.imageCancel);
        binding.imageCancel.setVisibility(View.GONE);
        binding.loaderView.setVisibility(View.INVISIBLE);

        binding.searchTxtView.setHint(generalFunc.retrieveLangLBl("", "LBL_NEARBY_PLACE_SEARCH"));
        binding.searchTxtView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchText = s.toString();
                if (mSearchText.length() >= 3) {
                    binding.imageCancel.setVisibility(View.VISIBLE);
                    binding.loaderView.setVisibility(View.VISIBLE);
                } else {
                    if (mSearchText.length() == 0) {
                        binding.imageCancel.setVisibility(View.GONE);
                    } else {
                        binding.imageCancel.setVisibility(View.VISIBLE);
                    }
                    binding.loaderView.setVisibility(View.INVISIBLE);
                }
                if (mSearchText.length() == 0 || mSearchText.length() >= 3) {
                    getNearByPlacesList(false, false, true);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bannerData() {
        mBannerAdapter = new CommonBanner23Adapter(this, generalFunc, bannerListArray);
        binding.rvBanner.setAdapter(mBannerAdapter);
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(binding.rvBanner);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void mainData() {
        // category list
        nearByCategoryAdapter = new NearByCategoryAdapter(mServiceCategoryList, (position, selCatPos) -> {
            HashMap<String, String> selectData = mServiceCategoryList.get(position);
            selectData.put("isSelect", "Yes");
            mServiceCategoryList.set(position, selectData);

            if (selCatPos != position && selCatPos != -1) {
                HashMap<String, String> removeData = mServiceCategoryList.get(selCatPos);
                removeData.put("isSelect", "No");
                mServiceCategoryList.set(selCatPos, removeData);
            }
            iCategoryId = selectData.get("iCategoryId");
            nearByCategoryAdapter.notifyDataSetChanged();
            getNearByPlacesList(false, true, false);
        });
        binding.rvNearByCategory.setAdapter(nearByCategoryAdapter);

        /// main data
        binding.nearByHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NEARBY_TXT"));
        mNearByServiceAdapter = new NearByServiceAdapter(generalFunc, placesList, (position, mapData) -> {
            Utils.hideKeyboard(this);
            Bundle bn = new Bundle();
            bn.putString("vLatitude", mLatitude);
            bn.putString("vLongitude", mLongitude);
            bn.putString("vLocation", mAddress);
            bn.putSerializable("placesHashMap", mapData);
            new ActUtils(this).startActWithData(NearByDetailsActivity.class, bn);
        });
        binding.rvNearByService.setAdapter(mNearByServiceAdapter);
    }

    @Override
    public void onLocationUpdate(Location mLastLocation) {
        mLatitude = mLastLocation.getLatitude() + "";
        mLongitude = mLastLocation.getLongitude() + "";

        if (isFirst) {
            isFirst = false;
            getNearByPlacesList(true, false, false);
        } else {
            getAddressFromLocation = new GetAddressFromLocation(this, generalFunc);
            getAddressFromLocation.setLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            getAddressFromLocation.setAddressList(this);
            getAddressFromLocation.execute();
        }
    }

    @Override
    public void onAddressFound(String address, double latitude, double longitude, String geocodeobject) {
        if (Utils.checkText(address)) {
            mLatitude = latitude + "";
            mLongitude = longitude + "";
            binding.headerAddressTxt.setText(address);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getNearByPlacesList(boolean isLocationChanges, boolean isDataUpdate, boolean isSearch) {
        if (isLocationChanges) {
            binding.mainDataArea.setVisibility(View.GONE);
            binding.loading.setVisibility(View.VISIBLE);

        }
        if (isDataUpdate) {
            binding.titleArea.setVisibility(View.GONE);
            binding.listArea.setVisibility(View.GONE);
            binding.DataLoadingArea.setVisibility(View.VISIBLE);
        }
        if (isSearch) {
            binding.loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getNearByPlaces");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iCategoryId", iCategoryId);

        parameters.put("vLatitude", "" + mLatitude);
        parameters.put("vLongitude", "" + mLongitude);

        parameters.put("searchWord", mSearchText);

        ApiHandler.execute(this, parameters, responseString -> {

            binding.loaderView.setVisibility(View.INVISIBLE);
            binding.loading.setVisibility(View.GONE);
            if (Utils.checkText(mSearchText)) {
                Utils.hideKeyboard(this);
            }

            if (Utils.checkText(responseString)) {

                binding.mainDataArea.setVisibility(View.VISIBLE);
                binding.listArea.setVisibility(View.VISIBLE);
                binding.DataLoadingArea.setVisibility(View.GONE);
                binding.NoDataTxt.setVisibility(View.GONE);

                bannerListArray = generalFunc.getJsonArray("BANNER_DATA", responseString);
                mBannerAdapter.updateData(bannerListArray);

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    //handling category
                    if (isFilter) {
                        isFilter = false;
                        JSONArray categoryArr = generalFunc.getJsonArray("CATEGORY", responseString);
                        mServiceCategoryList.clear();
                        if (categoryArr != null && categoryArr.length() > 0) {
                            for (int i = 0; i < categoryArr.length(); i++) {
                                JSONObject obj_tmp = generalFunc.getJsonObject(categoryArr, i);

                                HashMap<String, String> mapData = new HashMap<>();
                                MyUtils.createHashMap(generalFunc, mapData, obj_tmp);
                                if (i == 0) {
                                    mapData.put("isSelect", "Yes");
                                }

                                mServiceCategoryList.add(mapData);
                            }
                        }
                        nearByCategoryAdapter.notifyDataSetChanged();
                    }

                    //handling category Near By Item Data
                    JSONArray placesArr = generalFunc.getJsonArray("Places", responseString);
                    placesList.clear();
                    if (placesArr != null && placesArr.length() > 0) {
                        MyUtils.createArrayListJSONArray(generalFunc, placesList, placesArr);
                    } else {
                        binding.titleArea.setVisibility(View.GONE);
                        binding.NoDataTxt.setVisibility(View.VISIBLE);
                        binding.NoDataTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    }
                    mNearByServiceAdapter.notifyDataSetChanged();
                } else {
                    if (placesList.size() == 0) {
                        if (!generalFunc.getJsonValue(Utils.message_str_one, responseString).equalsIgnoreCase("")) {
                            binding.titleArea.setVisibility(View.GONE);
                            binding.NoDataTxt.setVisibility(View.VISIBLE);
                            binding.NoDataTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)));
                        }
                        placesList.clear();
                    }
                    mNearByServiceAdapter.notifyDataSetChanged();
                }
            } else {
                generalFunc.showError();
            }
            if (binding.NoDataTxt.getVisibility() == View.VISIBLE) {
                binding.titleArea.setVisibility(View.GONE);
            } else {
                binding.titleArea.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getLastLocation != null) {
            getLastLocation.stopLocationUpdates();
        }
        try {
            getAddressFromLocation.setAddressList(null);
            getAddressFromLocation = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null) {

            binding.headerAddressTxt.setText(data.getStringExtra("Address"));
            mLatitude = data.getStringExtra("Latitude") == null ? "0.0" : data.getStringExtra("Latitude");
            mLongitude = data.getStringExtra("Longitude") == null ? "0.0" : data.getStringExtra("Longitude");

            if (!mLatitude.equalsIgnoreCase("0.0") && !mLongitude.equalsIgnoreCase("0.0")) {
                getNearByPlacesList(true, false, false);
                isFilter = true;
            }
        } else if (requestCode == 111 && resultCode == RESULT_OK) {
            getNearByPlacesList(true, false, false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Utils.hideKeyboard(this);
        int i = view.getId();
        if (i == binding.backImgView.getId()) {
            onBackPressed();

        } else if (i == binding.headerAddressTxt.getId()) {
            Bundle bn = new Bundle();
            bn.putString("locationArea", "source");
            bn.putBoolean("isaddressview", true);
            bn.putDouble("lat", GeneralFunctions.parseDoubleValue(0.0, mLatitude));
            bn.putDouble("long", GeneralFunctions.parseDoubleValue(0.0, mLongitude));
            bn.putString("address", mAddress);
            bn.putString("eSystem", Utils.eSystem_Type);
            new ActUtils(this).startActForResult(SearchLocationActivity.class, bn, Utils.UBER_X_SEARCH_PICKUP_LOC_REQ_CODE);

        } else if (i == binding.imageCancel.getId()) {
            binding.searchTxtView.setText("");

        }
    }
}