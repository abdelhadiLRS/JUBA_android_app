package com.multixpro.user.trackService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityTrackAnyVerificationBinding;
import com.service.handler.ApiHandler;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TrackAnyVerification extends ParentActivity {

    private ActivityTrackAnyVerificationBinding binding;
    private String LBL_RESEND_OTP_SIGNIN, MOBILE_NO_VERIFICATION_METHOD = "", phoneVerificationCode = "", required_str = "", error_verification_code = "", mVerificationId;
    private FirebaseAuth mAuth;
    private CountDownTimer countDnTimer;
    private int resendSecInMilliseconds;
    private MButton btn_type2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_any_verification);

        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_SIGNUP_TXT"));
        ImageView backBtn = findViewById(R.id.backImgView);
        addToClickHandler(backBtn);
        if (generalFunc.isRTLmode()) {
            backBtn.setRotation(180);
            binding.bgImgView.setRotationY(180);
        }

        binding.otpHelpTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ENTER_CODE_SENT_TO_MOBILE_TXT") + " " + getIntent().getStringExtra("mob"));
        binding.resendBtn.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        MOBILE_NO_VERIFICATION_METHOD = generalFunc.retrieveValue("MOBILE_NO_VERIFICATION_METHOD");
        error_verification_code = generalFunc.retrieveLangLBl("", "LBL_VERIFICATION_CODE_INVALID");
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(Utils.generateViewId());
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_VERIFY_TXT"));

        addToClickHandler(btn_type2);
        addToClickHandler(binding.resendBtn);

        binding.resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
        LBL_RESEND_OTP_SIGNIN = generalFunc.retrieveLangLBl("Resend code in", "LBL_RESEND_OTP_SIGNIN");
        resendSecInMilliseconds = GeneralFunctions.parseIntegerValue(30, generalFunc.getJsonValue(Utils.VERIFICATION_CODE_RESEND_TIME_IN_SECONDS_KEY, generalFunc.retrieveValue(Utils.USER_PROFILE_JSON))) * 1000;

        binding.mobOtpView.setVisibility(View.GONE);
        binding.firebaseOTPView.setVisibility(View.GONE);

        binding.resendBtn.performClick();
    }

    private Context getActContext() {
        return TrackAnyVerification.this;
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();

        } else if (i == binding.resendBtn.getId()) {

            if (MOBILE_NO_VERIFICATION_METHOD.equalsIgnoreCase("Firebase")) {
                binding.firebaseOTPView.setVisibility(View.VISIBLE);
                sendVerificationCodeFirebase(getIntent().getStringExtra("mob"));
            } else {
                binding.mobOtpView.setVisibility(View.VISIBLE);
                sendVerificationSMS();
            }

        } else if (i == btn_type2.getId()) {

            if (MOBILE_NO_VERIFICATION_METHOD.equalsIgnoreCase("Firebase")) {
                String finalCode = Utils.getText(binding.firebaseOTPTxt);
                if (phoneVerificationCode.equalsIgnoreCase(finalCode)) {
                    verifyVerificationCode(phoneVerificationCode);
                } else {
                    verifyVerificationCode(finalCode);
                }
            } else {
                String finalCode = Utils.getText(binding.mobOtpView);
                boolean isCodeEntered = Utils.checkText(finalCode) ?
                        phoneVerificationCode.equalsIgnoreCase(finalCode) || generalFunc.retrieveValue(Utils.SITE_TYPE_KEY).equalsIgnoreCase("Demo") && finalCode.equalsIgnoreCase("1234") : Utils.setErrorFields(binding.mobOtpView, required_str);
                if (isCodeEntered) {
                    verifyUser();
                }else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ENTER_VERIFICATION_CODE"));

                }
            }
        }
    }

    private void verifyVerificationCode(String code) {
        if (code.equalsIgnoreCase("")) {
            Utils.setErrorFields(binding.firebaseOTPTxt, required_str);
        } else {
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            } catch (Exception e) {
                generalFunc.showGeneralMessage("", error_verification_code);
                //Utils.setErrorFields(binding.firebaseOTPTxt, error_verification_code);
            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(TrackAnyVerification.this, task -> {
                    if (task.isSuccessful()) {
                        verifyUser();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            generalFunc.showGeneralMessage("", error_verification_code);
                            //Utils.setErrorFields(binding.firebaseOTPTxt, error_verification_code);
                        }
                    }
                });
    }

    private void sendVerificationCodeFirebase(String phoneNum) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+" + phoneNum).setTimeout(60L, TimeUnit.SECONDS).setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        mVerificationId = s;
                        binding.resendBtn.setVisibility(View.VISIBLE);
                        showTimer();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            phoneVerificationCode = code;
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Logger.d("onVerificationFailed", "::" + e.getMessage());
                        binding.resendBtn.setVisibility(View.VISIBLE);
                        binding.resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"));
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyUser() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "verifyTrackServiceInviteCode");
        parameters.put("vInviteCode", getIntent().getStringExtra("vInviteCode"));
        parameters.put("MobileNo", getIntent().getStringExtra("vmobile"));
        parameters.put("vPhoneCode", getIntent().getStringExtra("vPhoneCode"));
        parameters.put("UserType", Utils.app_type);

        Utils.hideKeyboard(getActContext());

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {
            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    new ActUtils(getActContext()).setOkResult();
                    finish();
                } else {

                    String msg = generalFunc.getJsonValue(Utils.message_str, responseString);
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", msg), "", generalFunc.retrieveLangLBl("", "LBL_OK"),
                            buttonId -> {
                                if (buttonId == 1) {
                                    if (msg.equalsIgnoreCase("LBL_TRACK_SERVICE_INVITE_CODE_INVALID") || msg.equalsIgnoreCase("LBL_INVALID_PHONE_NUMBER")) {
                                        finish();
                                    } else if (msg.equalsIgnoreCase("LBL_TRACK_SERVICE_INVITE_CODE_IN_USED")) {
                                        Bundle bn = new Bundle();
                                        bn.putBoolean("isRestartApp", true);
                                        new ActUtils(getActContext()).startActWithData(TrackAnyList.class, bn);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void sendVerificationSMS() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "sendAuthOtp");
        parameters.put("MobileNo", getIntent().getStringExtra("vmobile"));
        parameters.put("vPhoneCode", getIntent().getStringExtra("vPhoneCode"));
        parameters.put("UserType", Utils.app_type);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {
            binding.resendBtn.setVisibility(View.VISIBLE);
            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    showTimer();
                    phoneVerificationCode = generalFunc.getJsonValue(Utils.message_str, responseString);
                } else {
                    binding.resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"));
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                binding.resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"));
            }
        });
    }

    private void showTimer() {
        countDnTimer = new CountDownTimer(resendSecInMilliseconds, 1000) {
            @Override
            public void onTick(long milliseconds) {
                setTime(milliseconds);
                enableOrDisable(false);
            }

            @Override
            public void onFinish() {
                enableOrDisable(true);
                if (countDnTimer != null) {
                    countDnTimer.cancel();
                    countDnTimer = null;
                }
            }
        }.start();
    }

    private void setButtonEnabled(MTextView btn, boolean setEnable) {
        btn.setFocusableInTouchMode(setEnable);
        btn.setFocusable(setEnable);
        btn.setEnabled(setEnable);
        if (setEnable) {
            addToClickHandler(btn);
        } else {
            btn.setOnClickListener(null);
        }

        btn.setTextColor(setEnable ? getActContext().getResources().getColor(R.color.appThemeColor_1) : Color.parseColor("#BABABA"));
        btn.setClickable(setEnable);
    }

    private void enableOrDisable(boolean activate) {
        setButtonEnabled(binding.resendBtn, activate);
        if (activate) {
            binding.resendBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RESEND_SMS"));
        }
    }

    private void setTime(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;

        @SuppressLint("DefaultLocale") String formattedTxt = String.format("%02d:%02d", minutes, seconds);
        formattedTxt = LBL_RESEND_OTP_SIGNIN + " " + formattedTxt;

        binding.resendBtn.setTextColor(Color.parseColor("#000000"));
        binding.resendBtn.setText(formattedTxt);
    }
}