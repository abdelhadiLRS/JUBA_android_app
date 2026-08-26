package com.multixpro.user.rideSharing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.SpacesItemDecoration;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityRideBookSearchBinding;
import com.multixpro.user.rideSharing.adapter.RideBookSearchAdapter;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RideBookSearchList extends ParentActivity {

    private ActivityRideBookSearchBinding binding;
    private RideBookSearchAdapter mRideBookSearchAdapter;
    private final ArrayList<HashMap<String, String>> mRideBookSearchList = new ArrayList<>();
    private String mPage = "1", selectedNoOfSeats = "";
    boolean mIsLoading = false, isNextPageAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_book_search);

        selectedNoOfSeats = getIntent().getStringExtra("NoOfSeats");

        initialization();
        getBookSearchList(false);
    }

    private void initialization() {
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_RIDE_SEARCH_TITLE"));

        binding.loading.setVisibility(View.GONE);
        binding.noDataArea.setVisibility(View.GONE);

        mRideBookSearchAdapter = new RideBookSearchAdapter(generalFunc, mRideBookSearchList, (position, mapData) -> {
            Bundle bn = new Bundle();
            bn.putBoolean("isSearchView", true);
            mapData.put("selectedNoOfSeats", selectedNoOfSeats);
            bn.putSerializable("myRideDataHashMap", mapData);
            new ActUtils(this).startActForResult(RideBookDetails.class, bn, MyUtils.REFRESH_DATA_REQ_CODE);
        });
        binding.rvRideBookSearchList.addItemDecoration(new SpacesItemDecoration(1, getResources().getDimensionPixelSize(R.dimen._12sdp), false));
        binding.rvRideBookSearchList.setAdapter(mRideBookSearchAdapter);

        //////////////////////
        binding.rvRideBookSearchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = Objects.requireNonNull(recyclerView.getLayoutManager()).getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !mIsLoading && isNextPageAvailable) {

                    mIsLoading = true;
                    mRideBookSearchAdapter.addFooterView();
                    getBookSearchList(true);

                } else if (!isNextPageAvailable) {
                    mRideBookSearchAdapter.removeFooterView();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getBookSearchList(boolean isLoadMore) {
        binding.loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "SearchRide");

        parameters.put("tStartLat", getIntent().getStringExtra("tStartLat"));
        parameters.put("tStartLong", getIntent().getStringExtra("tStartLong"));

        parameters.put("tEndLat", getIntent().getStringExtra("tEndLat"));
        parameters.put("tEndLong", getIntent().getStringExtra("tEndLong"));

        parameters.put("dStartDate", getIntent().getStringExtra("dStartDate"));
        parameters.put("NoOfSeats", selectedNoOfSeats);

        parameters.put("vFilterParam", "");

        if (isLoadMore) {
            parameters.put("page", mPage);
        }

        ApiHandler.execute(this, parameters, responseString -> {

            binding.loading.setVisibility(View.GONE);
            binding.noDataArea.setVisibility(View.GONE);

            String nextPage = generalFunc.getJsonValue("NextPage", responseString);

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    if (!isLoadMore) {
                        mRideBookSearchList.clear();
                    }

                    JSONArray dataArray = generalFunc.getJsonArray(Utils.message_str, responseString);
                    if (dataArray != null && dataArray.length() > 0) {

                        MyUtils.createArrayListJSONArray(generalFunc, mRideBookSearchList, dataArray);
                    } else {
                        binding.noDataArea.setVisibility(View.VISIBLE);
                        binding.noDataTitleTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message_title", responseString)));
                        binding.noDataMsgTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    }
                    mRideBookSearchAdapter.notifyDataSetChanged();

                    if (!nextPage.equals("") && !nextPage.equals("0")) {
                        mPage = nextPage;
                        isNextPageAvailable = true;
                    } else {
                        removeNextPageConfig();
                    }
                } else {
                    binding.noDataArea.setVisibility(View.VISIBLE);
                    binding.noDataTitleTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message_title", responseString)));
                    binding.noDataMsgTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    removeNextPageConfig();
                }
            } else {
                generalFunc.showError();
                if (!isLoadMore) {
                    removeNextPageConfig();
                }
            }
            mIsLoading = false;
        });
    }

    public void removeNextPageConfig() {
        mPage = "";
        isNextPageAvailable = false;
        mIsLoading = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyUtils.REFRESH_DATA_REQ_CODE && resultCode == Activity.RESULT_OK) {
            getBookSearchList(false);
        }
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        }
    }
}