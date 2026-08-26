package com.multixpro.user.trackService;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.RecurringTask;
import com.multixpro.user.R;
import com.multixpro.user.UberXHomeActivity;
import com.multixpro.user.databinding.ActivityTrackAnyListBinding;
import com.multixpro.user.trackService.adapter.TrackAnyAdapter;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackAnyList extends ParentActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivityTrackAnyListBinding binding;
    private ImageView backImgView, ic_iv_add;

    private TrackAnyAdapter mTrackAnyAdapter;
    private final ArrayList<HashMap<String, String>> trackAnyList = new ArrayList<>();

    @Nullable
    private RecurringTask mRecurringTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_any_list);

        toolBarView();

        binding.noDataTrackAnyTxt.setVisibility(View.GONE);
        binding.noDataTrackAnyTxt.setText(generalFunc.retrieveLangLBl("","LBL_NO_DATA_AVAIL"));

        mTrackAnyAdapter = new TrackAnyAdapter(generalFunc, trackAnyList, new TrackAnyAdapter.ItemClickListener() {
            @Override
            public void onProfileClick(int position, HashMap<String, String> itemList) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("trackAnyHashMap", itemList);
                new ActUtils(TrackAnyList.this).startActWithData(TrackAnyProfileVehicle.class, bundle);
            }

            @Override
            public void onVehicleClick(int position, HashMap<String, String> itemList) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isVehicleView", true);
                bundle.putSerializable("trackAnyHashMap", itemList);
                new ActUtils(TrackAnyList.this).startActWithData(TrackAnyProfileVehicle.class, bundle);
            }

            @Override
            public void ondelUserclick(int position, HashMap<String, String> itemList) {
                dltUser(itemList.get("iTrackServiceUserId"));
            }

            @Override
            public void onLiveTrackClick(int position, HashMap<String, String> itemList) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("trackAnyHashMap", itemList);
                new ActUtils(TrackAnyList.this).startActWithData(TrackAnyLiveTracking.class, bundle);
            }
        });
        binding.rvTrackAny.setAdapter(mTrackAnyAdapter);
        binding.swipeRefreshTrackAny.setOnRefreshListener(this);
        getTrackList(true);
    }



    private void dltUser(String PairedUserId) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(this);
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
            } else {
                dltMember(PairedUserId);
            }

        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Logout", "LBL_DELETE_CONFIRM_MSG"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
        generateAlert.showAlertBox();
    }

    private void dltMember(String iTrackServiceUserId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "removeLinkedMember");
        parameters.put("PairedUserId",iTrackServiceUserId );




        ApiHandler.execute(this, parameters, responseString -> {


            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
    }

    private void toolBarView() {
        backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_ANY_TXT"));
        ic_iv_add = findViewById(R.id.ic_iv_add);
        ic_iv_add.setVisibility(View.VISIBLE);
        addToClickHandler(backImgView);
        addToClickHandler(ic_iv_add);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getTrackList(boolean isLoader) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "listTrackingUsers");

        if (getIntent().getStringExtra("MemberType") != null) {
            parameters.put("MemberType", getIntent().getStringExtra("MemberType"));
        }

        binding.loadingTrackAny.setVisibility(isLoader ? View.VISIBLE : View.GONE);

        ApiHandler.execute(this, parameters, responseString -> {

            binding.swipeRefreshTrackAny.setRefreshing(false);
            binding.loadingTrackAny.setVisibility(View.GONE);
            binding.noDataTrackAnyTxt.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    trackAnyList.clear();

                    JSONArray userArr = generalFunc.getJsonArray(Utils.message_str, responseString);
                    if (userArr != null && userArr.length() > 0) {
                        MyUtils.createArrayListJSONArray(generalFunc, trackAnyList, userArr);
                    } else {
//                        ic_iv_add.performClick();
//                        finish();
//                        return;

                        binding.noDataTrackAnyTxt.setVisibility(View.VISIBLE);
                    }
                    mTrackAnyAdapter.notifyDataSetChanged();
                    setTimer();
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
    }

    private void setTimer() {
        if (mRecurringTask == null) {
            mRecurringTask = new RecurringTask(10000);
            mRecurringTask.startRepeatingTask();
            mRecurringTask.setTaskRunListener(() -> {
                if (MyApp.getInstance().getCurrentAct() instanceof TrackAnyList) {
                    getTrackList(false);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        isNotificationOpen=true;
        if (getIntent().getBooleanExtra("isRestartApp", false)) {
            Bundle bn = new Bundle();
            new ActUtils(this).startActWithData(UberXHomeActivity.class, bn);
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecurringTask != null) {
            mRecurringTask.stopRepeatingTask();
            mRecurringTask = null;
        }
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshTrackAny.setRefreshing(true);
        getTrackList(false);
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == backImgView.getId()) {
            onBackPressed();
        } else if (i == ic_iv_add.getId()) {

            Bundle bn=new Bundle();
            bn.putBoolean("isBack",true);
            bn.putString("MemberType",getIntent().getStringExtra("MemberType"));
            new ActUtils(this).startActWithData(PairCodeGenrateActivity.class,bn);


        }
    }

    boolean isNotificationOpen=false;

    public void pubNubMsgArrived(final String message) {

        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);

            if (msgType.equals("TrackMember") && !isNotificationOpen) {
                isNotificationOpen=true;
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue("vTitle", message), buttonId -> {
                    isNotificationOpen=false;
                    getTrackList(true);
                });
            }
            else if (msgType.equals("TrackMemberRemoved") && !isNotificationOpen) {
                isNotificationOpen=true;
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue
                        ("vTitle", message), buttonId -> {
                    isNotificationOpen=false;
                    getTrackList(true);
                });
            }
        });
    }
}