package com.multixpro.user.trackService;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.UberXHomeActivity;
import com.multixpro.user.databinding.ActivityPairCodeGenrateBinding;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.utils.VectorUtils;
import com.view.MButton;
import com.view.MaterialRippleLayout;

import java.util.HashMap;

public class PairCodeGenrateActivity extends ParentActivity {

    private ActivityPairCodeGenrateBinding binding;
    MButton btn_type_sucesss;
    Boolean isBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pair_code_genrate);
        binding.msgTxt.setText(Html.fromHtml(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_PAIRING_PROCESS_DESC")));
        binding.titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_PAIRING_PROCESS_TITLE"));
        binding.paircodeTitleTxt.setText(generalFunc.retrieveLangLBl("Your Pairing code", "LBL_TRACK_SERVICE_YOUR_PAIRING_TXT"));
        isBack=getIntent().getBooleanExtra("isBack",false);
        if(isBack)
        {
            binding.backBtn.setVisibility(View.VISIBLE);
        }
        addToClickHandler(binding.backBtn);
        if (generalFunc.isRTLmode()) {
            binding.backBtn.setRotation(180);
        }

        btn_type_sucesss = ((MaterialRippleLayout) findViewById(R.id.btn_type_sucesss)).getChildView();
        btn_type_sucesss.setId(Utils.generateViewId());
        btn_type_sucesss.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
        binding.sucesstitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_SETUP_PROFILE_SUCCESS_TITLE"));
        binding.sucessmsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_PROFILE_SETUP_SUCCESS_MSG"));
        addToClickHandler(btn_type_sucesss);
        VectorUtils.manageVectorImage(this, binding.imgSuccess, R.drawable.ic_success_new, R.drawable.ic_success_new_compat);
        generatePairingCode();
    }

    private void generatePairingCode() {


        HashMap<String, String> parameters = new HashMap<String, String>();


        parameters.put("type", "GeneratePairingCode");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("eUserType", Utils.app_type);
        parameters.put("tSessionId", generalFunc.retrieveValue(Utils.SESSION_ID_KEY));
        parameters.put("MemberType", getIntent().getStringExtra("MemberType"));


        ApiHandler.execute(this, parameters, false, false, generalFunc, responseString -> {

            binding.loaderView.setVisibility(View.GONE);
            if (responseString != null && !responseString.equals("")) {

                String message = generalFunc.getJsonValue(Utils.message_str, responseString);


                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {


                    binding.paircodeTxt.setText(message);
//                    Handler handler = new Handler();
//                    handler.postDelayed(() ->{binding.sucessArea.setVisibility(View.VISIBLE);
//                        binding.paircodeArea.setVisibility(View.GONE);}, 1000);

                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }

            }
        });

    }

    public void handleNextAct() {
        Bundle bn = new Bundle();
        bn.putBoolean("isRestartApp",true);
        bn.putString("MemberType", getIntent().getStringExtra("MemberType"));
        new ActUtils(this).startActWithData(TrackAnyList.class, bn);
        finishAffinity();
    }

    public void pubNubMsgArrived(final String message) {

        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);

            if (msgType.equalsIgnoreCase("TrackMemberPaired")) {

                binding.sucessArea.setVisibility(View.VISIBLE);
                binding.paircodeArea.setVisibility(View.GONE);

            }
        });
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == binding.backBtn.getId()) {
            onBackPressed();
        } else if (i == btn_type_sucesss.getId()) {
            handleNextAct();
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("isRestartApp", false)) {
            Bundle bn = new Bundle();
            new ActUtils(this).startActWithData(UberXHomeActivity.class, bn);
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }
}