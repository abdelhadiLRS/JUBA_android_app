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
import com.multixpro.user.databinding.FragmentRidePublishBinding;
import com.multixpro.user.rideSharing.RideMyDetails;
import com.multixpro.user.rideSharing.RideMyList;
import com.multixpro.user.rideSharing.adapter.RideMyPublishAdapter;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class RidePublishFragment extends BaseFragment {

    private FragmentRidePublishBinding binding;
    private RideMyList mActivity;
    private GeneralFunctions generalFunc;
    private RideMyPublishAdapter mRideMyPublishAdapter;
    private final ArrayList<HashMap<String, String>> mRideMyList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_publish, container, false);

        binding.loading.setVisibility(View.GONE);
        binding.noDataArea.setVisibility(View.GONE);

        mRideMyPublishAdapter = new RideMyPublishAdapter(generalFunc, mRideMyList, (position, mapData) -> {
            Bundle bn = new Bundle();
            bn.putSerializable("myRideDataHashMap", mapData);
            new ActUtils(mActivity).startActForResult(RideMyDetails.class, bn, MyUtils.REFRESH_DATA_REQ_CODE);
        });
        binding.rvRidePublishList.addItemDecoration(new SpacesItemDecoration(1, getResources().getDimensionPixelSize(R.dimen._12sdp), false));
        binding.rvRidePublishList.setAdapter(mRideMyPublishAdapter);

        getPublishRidesList();

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
    public void getPublishRidesList() {
        binding.loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetPublishedRides");

        ApiHandler.execute(mActivity, parameters, responseString -> {

            binding.loading.setVisibility(View.GONE);
            binding.noDataArea.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {
                mRideMyList.clear();
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    JSONArray dataArray = generalFunc.getJsonArray(Utils.message_str, responseString);
                    MyUtils.createArrayListJSONArray(generalFunc, mRideMyList, dataArray);
                }
                if (mRideMyList.size() <= 0) {
                    binding.noDataArea.setVisibility(View.VISIBLE);
                    binding.noDataTitleTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message_title", responseString)));
                    binding.noDataMsgTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
                mRideMyPublishAdapter.notifyDataSetChanged();
            } else {
                generalFunc.showError();
            }
        });
    }
}