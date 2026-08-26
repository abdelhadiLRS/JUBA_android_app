package com.multixpro.user.trackService;

import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityTrackAnyProfileSetupBinding;
import com.service.handler.ApiHandler;
import com.utils.Utils;

import java.util.HashMap;

public class TrackAnyProfileSetup extends ParentActivity {
    private ActivityTrackAnyProfileSetupBinding binding;
    String memberType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_any_profile_setup);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.calender_selectedcolor));

        memberType = getIntent().getStringExtra("MemberType");

        addToClickHandler(binding.backBtn);
        if (generalFunc.isRTLmode()) {
            binding.backBtn.setRotation(180);
            binding.lottieAnimation.setRotationY(180);
        }

        manageVectorImage(binding.gradientArea, R.drawable.ic_gradient, R.drawable.ic_gradient_compat);

        if (generalFunc.getJsonValueStr("TRACK_ANY_SERVICE_ENABLED", obj_userProfile).equalsIgnoreCase("Yes")) {
            binding.bottomViewTitle.setText(getIntent().getStringExtra("vTitle"));
        } else {
            binding.bottomViewTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_ANY_TXT"));
        }
        binding.bottomView.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_DESC"));

        binding.btnSetProfile.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_SETUP_PROFILE_TXT"));
        addToClickHandler(binding.btnSetProfile);
    }

    private void checkProfileSetup() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "checkTrackingProfileSetup");
        parameters.put("MemberType", memberType);

        ApiHandler.execute(this, parameters, true, false, generalFunc, responseString -> {

            if (Utils.checkText(responseString)) {
                String message = generalFunc.getJsonValue(Utils.message_str, responseString);

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    if (generalFunc.getJsonValue("eCatType", message).equalsIgnoreCase("TrackService")) {
                        Bundle bn = new Bundle();
                        bn.putString("MemberType", memberType);
                        bn.putBoolean("isRestartApp", true);
                        new ActUtils(this).startActWithData(TrackAnyList.class, bn);

                    } else if (generalFunc.getJsonValue("eCatType", message).equalsIgnoreCase("TRACKSERVICEADD")) {
                        Bundle bn = new Bundle();
                        bn.putString("MemberType", memberType);
                        bn.putBoolean("isRestartApp", true);
                        new ActUtils(this).startActWithData(PairCodeGenrateActivity.class, bn);
                    }
                    finishAffinity();
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == binding.backBtn.getId()) {
            onBackPressed();
        } else if (i == binding.btnSetProfile.getId()) {
            checkProfileSetup();
        }
    }
}