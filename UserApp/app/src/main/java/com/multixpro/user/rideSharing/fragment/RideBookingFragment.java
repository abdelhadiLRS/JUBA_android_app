package com.multixpro.user.rideSharing.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.fragments.BaseFragment;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.SpacesItemDecoration;
import com.multixpro.user.R;
import com.multixpro.user.databinding.FragmentRideBookingBinding;
import com.multixpro.user.rideSharing.RideBookDetails;
import com.multixpro.user.rideSharing.RideMyList;
import com.multixpro.user.rideSharing.adapter.RideBookSearchAdapter;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class RideBookingFragment extends BaseFragment {

    private FragmentRideBookingBinding binding;
    private RideMyList mActivity;
    private GeneralFunctions generalFunc;
    private RideBookSearchAdapter mRideBookSearchAdapter;
    private final ArrayList<HashMap<String, String>> mRideBookSearchList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_booking, container, false);

        binding.loading.setVisibility(View.GONE);
        binding.noDataArea.setVisibility(View.GONE);

        mRideBookSearchAdapter = new RideBookSearchAdapter(generalFunc, mRideBookSearchList, (position, mapData) -> {
            Bundle bn = new Bundle();
            bn.putSerializable("myRideDataHashMap", mapData);
            new ActUtils(mActivity).startActForResult(RideBookDetails.class, bn, MyUtils.REFRESH_DATA_REQ_CODE);
        });
        binding.rvRideBookingList.addItemDecoration(new SpacesItemDecoration(1, getResources().getDimensionPixelSize(R.dimen._12sdp), false));
        binding.rvRideBookingList.setAdapter(mRideBookSearchAdapter);

        GetBookingsRidesList();

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requireActivity() instanceof RideMyList) {
            mActivity = (RideMyList) requireActivity();
            generalFunc = mActivity.generalFunc;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void GetBookingsRidesList() {
        binding.loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetBookings");

        ApiHandler.execute(mActivity, parameters, responseString -> {

            binding.loading.setVisibility(View.GONE);
            binding.noDataArea.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {
                mRideBookSearchList.clear();
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    JSONArray dataArray = generalFunc.getJsonArray(Utils.message_str, responseString);
                    MyUtils.createArrayListJSONArray(generalFunc, mRideBookSearchList, dataArray);
                }
                if (mRideBookSearchList.size() <= 0) {
                    binding.noDataArea.setVisibility(View.VISIBLE);
                    binding.noDataTitleTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message_title", responseString)));
                    binding.noDataMsgTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
                mRideBookSearchAdapter.notifyDataSetChanged();
            } else {
                generalFunc.showError();
            }
        });
    }
}