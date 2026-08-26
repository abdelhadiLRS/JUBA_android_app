package com.multixpro.user.rideSharing.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.fragments.BaseFragment;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.R;
import com.multixpro.user.databinding.FragmentRidePublishStep4Binding;
import com.multixpro.user.rideSharing.RidePublish;
import com.multixpro.user.rideSharing.RideUploadDocActivity;
import com.multixpro.user.rideSharing.adapter.RideDocumentAdapter;
import com.multixpro.user.rideSharing.model.RidePublishData;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class RidePublishStep4Fragment extends BaseFragment {

    private FragmentRidePublishStep4Binding binding;
    @Nullable
    private RidePublish mActivity;
    private RideDocumentAdapter rideDocumentAdapter;
    public ArrayList<HashMap<String, String>> verificationDocumentList = new ArrayList<>();
    private boolean isDone = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_publish_step_4, container, false);

        binding.rvVerificationDocument.setAdapter(rideDocumentAdapter);

        binding.MainArea.setVisibility(View.GONE);
        binding.loading.setVisibility(View.GONE);

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApp.getInstance().getCurrentAct() instanceof RidePublish) {
            mActivity = (RidePublish) requireActivity();

            rideDocumentAdapter = new RideDocumentAdapter(verificationDocumentList, new RideDocumentAdapter.OnItemClickListener() {
                @Override
                public void onItemClickList(HashMap<String, String> mapData) {
                    Intent intent = new Intent(mActivity, RideUploadDocActivity.class);
                    Bundle bn = new Bundle();
                    bn.putBoolean("isOnlyShow", false);
                    bn.putSerializable("documentDataHashMap", mapData);
                    intent.putExtras(bn);
                    launchActivity.launch(intent);
                    isDone = false;
                }

                @Override
                public void onUpdateDocumentIds(String documentIds) {
                    mActivity.mPublishData.setDocumentIds(documentIds);
                }
            });
        }
    }

    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (mActivity != null && result.getResultCode() == Activity.RESULT_OK) {
                    isDone = true;
                }
            });

    @Override
    public void onResume() {
        super.onResume();
        if (mActivity != null) {
            binding.selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_REVIEW_PUBLISH_DETAILS_TXT"));
            if (isDone) {
                getVerificationDoc(mActivity.generalFunc);
            }
        }
    }

    private void getVerificationDoc(GeneralFunctions generalFunc) {
        assert mActivity != null;

        RidePublishData.LocationDetails locationDetails = mActivity.mPublishData.getLocationDetails();
        if (locationDetails == null) {
            return;
        }

        binding.MainArea.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetVerificationDocuments");
        parameters.put("iMemberId", generalFunc.getMemberId());

        parameters.put("tStartLat", locationDetails.getFromLatitude());
        parameters.put("tStartLong", locationDetails.getFromLongitude());

        parameters.put("tEndLat", locationDetails.getToLatitude());
        parameters.put("tEndLong", locationDetails.getToLongitude());

        parameters.put("dStartDate", mActivity.mPublishData.getDateTime());

        ApiHandler.execute(requireActivity(), parameters, (String responseString) -> {

            binding.loading.setVisibility(View.GONE);
            binding.MainArea.setVisibility(View.VISIBLE);

            if (responseString != null && !responseString.equalsIgnoreCase("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    binding.verifyDocHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_VERIFY_DOCS_TITLE"));
                    binding.ridePlanHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_RIDE_PLAN_TITLE"));
                    binding.ridePlanVTxt.setText(generalFunc.getJsonValueStr("StartDate", generalFunc.getJsonObject(responseString)));

                    binding.startTimeTxt.setText(generalFunc.getJsonValueStr("StartTime", generalFunc.getJsonObject(responseString)));
                    binding.endTimeTxt.setText(generalFunc.getJsonValueStr("EndTime", generalFunc.getJsonObject(responseString)));

                    binding.startCityTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DETAILS_START_LOC_TXT"));
                    binding.endCityTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DETAILS_END_LOC_TXT"));

                    mActivity.mPublishData.setStartCity(generalFunc.getJsonValueStr("StartCity", generalFunc.getJsonObject(responseString)));
                    mActivity.mPublishData.setEndCity(generalFunc.getJsonValueStr("EndCity", generalFunc.getJsonObject(responseString)));

                    binding.startAddressTxt.setText(generalFunc.getJsonValueStr("StartAddress", generalFunc.getJsonObject(responseString)));
                    binding.endAddressTxt.setText(generalFunc.getJsonValueStr("EndAddress", generalFunc.getJsonObject(responseString)));

                    JSONArray messageArr = generalFunc.getJsonArray(Utils.message_str, responseString);
                    verificationDocumentList.clear();
                    if (messageArr != null && messageArr.length() > 0) {
                        MyUtils.createArrayListJSONArray(generalFunc, verificationDocumentList, messageArr);
                    }
                    rideDocumentAdapter.updateData();
                    mActivity.setPagerHeight();
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    public void checkPageNext() {
        if (mActivity != null) {

            if (mActivity.mPublishData.getDocumentIds() != null) {
                if (Utils.checkText(mActivity.mPublishData.getDocumentIds())) {
                    String[] separated = mActivity.mPublishData.getDocumentIds().split(",");
                    if (verificationDocumentList.size() == separated.length) {
                        mActivity.sendToPublishRide();
                    } else {
                        mActivity.generalFunc.showMessage(binding.selectServiceTxt, mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_UPLOAD_ALL_DOCUMENT"));
                    }
                } else {
                    mActivity.generalFunc.showMessage(binding.selectServiceTxt, mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_UPLOAD_ALL_DOCUMENT"));
                }
            }
        }
    }
}