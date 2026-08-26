package com.multixpro.user.trackService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.countryview.view.CountryPicker;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SetOnTouchList;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityTrackAnySignUpBinding;
import com.utils.LoadImage;
import com.utils.Utils;
import com.utils.VectorUtils;
import com.view.MTextView;

import java.util.HashMap;

public class TrackAnySignUp extends ParentActivity {

    private ActivityTrackAnySignUpBinding binding;
    private static final int NEXT_STAGE = 45;
    private String vPhoneCode = "", required_str = "", vSImage = "";
    private MTextView titleTxt;
    private CountryPicker countryPicker;
    private boolean isDone = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_any_sign_up);

        initView();
        setLabel();
        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            binding.countryDropImg.setVisibility(View.VISIBLE);
            binding.countryCodeTxt.setOnTouchListener(new SetOnTouchList());
            addToClickHandler(binding.countryArea);
        }
    }

    private Context getActContext() {
        return TrackAnySignUp.this;
    }

    private void initView() {
        titleTxt = findViewById(R.id.titleTxt);
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
            binding.bgImgView.setRotationY(180);
        }
        addToClickHandler(backImgView);
        addToClickHandler(binding.inviteQueryImg);

        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.DefaultCountryCode, "");
        data.put(Utils.DefaultPhoneCode, "");
        data = generalFunc.retrieveValue(data);

        vSImage = generalFunc.retrieveValue(Utils.DefaultCountryImage);

        new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();
        vPhoneCode = data.get(Utils.DefaultPhoneCode);
        if (!vPhoneCode.equalsIgnoreCase("")) {
            binding.countryCodeTxt.setText("+" + vPhoneCode);
        }

        addToClickHandler(binding.btnSubmit);

        if (generalFunc.getJsonValueStr("vSCountryImage", obj_userProfile) != null && !generalFunc.getJsonValueStr("vSCountryImage", obj_userProfile).equalsIgnoreCase("")) {
            vSImage = generalFunc.getJsonValueStr("vSCountryImage", obj_userProfile);
            new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();
        }

        /// Success View
        binding.txtSuccessTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_SETUP_PROFILE_SUCCESS_TITLE"));
        binding.txtSuccessSubTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_SETUP_PROFILE_SUCCESS_MSG"));
        VectorUtils.manageVectorImage(getActContext(), binding.imgView, R.drawable.ic_success_new, R.drawable.ic_success_new_compat);
        addToClickHandler(binding.btnContinue);

    }

    private void setLabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_SIGNUP_TXT"));
        binding.invite.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_INVITE_CODE_TXT"));
        binding.inviteCodeBox.setHint(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_INVITE_CODE_TXT"));
        binding.inviteCodeBox.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.UserPhone.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_PHONE_NO_TXT"));
        binding.mobileBox.setHint(generalFunc.retrieveLangLBl("", "LBL_MOBILE_NUMBER_HEADER_TXT"));
        binding.mobileBox.getLabelFocusAnimator().start();
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");

        binding.btnSubmit.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));
        binding.btnContinue.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_TXT"));
    }

    private void checkValues() {

        boolean mobileEntered = Utils.checkText(binding.mobileBox) || Utils.setErrorFields(binding.mobileBox, required_str);
        boolean InviteEntered = Utils.checkText(binding.inviteCodeBox) || Utils.setErrorFields(binding.inviteCodeBox, required_str);

        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            binding.countryDropImg.setVisibility(View.VISIBLE);
        } else {
            binding.countryDropImg.setVisibility(View.GONE);
        }
        if (mobileEntered) {
            mobileEntered = binding.mobileBox.length() >= 3 || Utils.setErrorFields(binding.mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }
        if (InviteEntered) {
            InviteEntered = binding.inviteCodeBox.length() >= 3 || Utils.setErrorFields(binding.inviteCodeBox, generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_INVITE_CODE_INVALID"));
        }
        if (!mobileEntered || !InviteEntered) {
            return;
        }

        updateProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.inputArea.setVisibility(isDone ? View.GONE : View.VISIBLE);
        binding.successArea.setVisibility(isDone ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            vPhoneCode = data.getStringExtra("vPhoneCode");

            binding.countryCodeTxt.setText("+" + vPhoneCode);
            vSImage = data.getStringExtra("vSImage");
            new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();

        } else if (requestCode == NEXT_STAGE && resultCode == RESULT_OK) {
            isDone = true;
        }
    }

    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        int i = view.getId();
        if (i == binding.countryArea.getId()) {
            if (countryPicker == null) {
                countryPicker = new CountryPicker.Builder(getActContext()).showingDialCode(true)
                        .setLocale(locale).showingFlag(true)
                        .enablingSearch(true)
                        .setCountrySelectionListener(country -> setData(country.getCode(), country.getDialCode(), country.getFlagName()))
                        .build();
            }
            countryPicker.show(getActContext());
        } else if (i == binding.btnSubmit.getId()) {
            checkValues();
        } else if (i == binding.btnContinue.getId()) {
            //
            Bundle bn = new Bundle();
            bn.putBoolean("isRestartApp", true);
            new ActUtils(getActContext()).startActWithData(TrackAnyList.class, bn);
        } else if (i == binding.inviteQueryImg.getId()) {
            generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl(" ", "LBL_TRACK_SERVICE_INVITE_CODE_QUES"),
                    generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_INVITE_CODE_ANS"));
        } else if (i == R.id.backImgView) {
            onBackPressed();
        }
    }

    private void setData(String vCountryCode, String vPhoneCode, String vSImage) {
        this.vPhoneCode = vPhoneCode;
        this.vSImage = vSImage;
        new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();
        GeneralFunctions generalFunctions = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
        binding.countryCodeTxt.setText("+" + generalFunctions.convertNumberWithRTL(vPhoneCode));
    }

    private void updateProfile() {
        Bundle bn = new Bundle();
        bn.putString("mob", "+" + vPhoneCode + binding.mobileBox.getText().toString().trim());
        bn.putString("vPhoneCode", vPhoneCode);
        bn.putString("vmobile", binding.mobileBox.getText().toString().trim());
        bn.putString("vInviteCode", binding.inviteCodeBox.getText().toString().trim());
        new ActUtils(getActContext()).startActForResult(TrackAnyVerification.class, bn, NEXT_STAGE);
    }
}