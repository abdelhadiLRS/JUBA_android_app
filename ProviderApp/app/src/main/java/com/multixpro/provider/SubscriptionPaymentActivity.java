package com.multixpro.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.service.handler.ApiHandler;
import com.service.server.ServerTask;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.anim.loader.AVLoadingIndicatorView;
import org.json.JSONObject;

import java.util.HashMap;

public class SubscriptionPaymentActivity extends ParentActivity {

    String SYSTEM_PAYMENT_FLOW;
    String APP_PAYMENT_MODE;
    String APP_PAYMENT_METHOD;

    MTextView titleTxt;
    MTextView subscriptionDesTxt;
    MTextView walletBalanceTxt;
    MTextView walletBalanceValTxt;
    MTextView planNameTxt;
    MTextView planPriceTxt;
    MTextView planPriceHTxt;
    MTextView planNameHTxt;
    MTextView cardPaymentTxt;
    MButton subScribeBtnTxt;
    ImageView backImgView;

    AppCompatCheckBox cb_wallet;
    RadioButton cardPaymentRadioBtn;
    private HashMap<String, String> planDetails;

    int TRANSACTION_COMPLETED = 12345;
    int WALLET_MONEY_ADDED = 12789;

    AVLoadingIndicatorView loaderView;
    WebView paymentWebview;
    View paymentWebViewArea;
    AlertDialog cashBalAlertDialog;
    String isRenew = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subcription_payment_activity);


        getUserPeofileJson(generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));

        planDetails = (HashMap<String, String>) getIntent().getSerializableExtra("PlanDetails");
        isRenew = getIntent().hasExtra("isRenew") ? getIntent().getStringExtra("isRenew") : "";

        initView();
        setLables();

        ((ImageView) findViewById(R.id.iv_icon)).setImageResource(R.drawable.ic_subscription_icon);


        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 20), 2,
                getActContext().getResources().getColor(R.color.light_back_color), findViewById(R.id.subScribeBtnTxt));

        managePaymentMethod();


        cardPaymentRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {


        });

        cb_wallet.setOnCheckedChangeListener((buttonView, isChecked) -> findViewById(R.id.iv_wallet).setVisibility(isChecked ? View.VISIBLE : View.GONE));

        setValues();
    }

    private void getUserPeofileJson(JSONObject object) {
        obj_userProfile = object;
        APP_PAYMENT_MODE = generalFunc.getJsonValueStr("APP_PAYMENT_MODE", obj_userProfile);
        APP_PAYMENT_METHOD = generalFunc.getJsonValueStr("APP_PAYMENT_METHOD", obj_userProfile);
        SYSTEM_PAYMENT_FLOW = generalFunc.getJsonValueStr("SYSTEM_PAYMENT_FLOW", obj_userProfile);


    }

    private void setValues() {
//        String htmlString = "<b><font color=" + getActContext().getResources().getColor(R.color.black) + ">" + generalFunc.retrieveLangLBl("", "LBL_SUB_NOTE_TXT") + ": " + "</font></b>";
        ((MTextView) findViewById(R.id.noteText)).setText(generalFunc.retrieveLangLBl("", "LBL_SUB_NOTE_TXT") + ": ");
        ((MTextView) findViewById(R.id.noteDetailsText)).setText(generalFunc.retrieveLangLBl("LBL_UPGRADE_NOTE_TXT", "LBL_UPGRADE_NOTE_TXT"));


        planNameHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUBSCRIPTION_PLAN_NAME")+": ");
        planNameTxt.setText(planDetails.get("vPlanName"));

        planPriceHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUB_PLAN_PRICE_TXT")+": ");
        planPriceTxt.setText(planDetails.get("fPlanPrice"));

        walletBalanceValTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("user_available_balance", obj_userProfile))));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWalletBalDetails();
    }

    private void managePaymentMethod() {


        if (APP_PAYMENT_MODE.contains("Card")) {
            findViewById(R.id.cardArea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.cardArea).setVisibility(View.GONE);
        }
    }

    private void setLables() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_PAYMENT_METHOD_TXT"));
        subscriptionDesTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_PAYMENT_METHOD_DESC_TXT"));
        subScribeBtnTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUBSCRIPTION_TXT"));
        walletBalanceTxt.setText(generalFunc.retrieveLangLBl("", "LBL_USE_WALLET_BALANCE"));
        cardPaymentTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
        //cardPaymentRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));

        addToClickHandler(findViewById(R.id.walletArea));
        addToClickHandler(findViewById(R.id.cardArea));
    }

    private void initView() {
        titleTxt = findViewById(R.id.titleTxt);
        backImgView = findViewById(R.id.backImgView);

        subscriptionDesTxt = findViewById(R.id.subscriptionDesTxt);
        subScribeBtnTxt = ((MaterialRippleLayout) findViewById(R.id.subScribeBtnTxt)).getChildView();
        cardPaymentRadioBtn = findViewById(R.id.cardPaymentRadioBtn);
        cb_wallet = findViewById(R.id.cb_wallet);
        walletBalanceTxt = findViewById(R.id.walletBalanceTxt);
        walletBalanceValTxt = findViewById(R.id.walletBalanceValTxt);
        planNameTxt = findViewById(R.id.planNameTxt);
        planNameHTxt = findViewById(R.id.planNameHTxt);
        planPriceTxt = findViewById(R.id.planPriceTxt);
        planPriceHTxt = findViewById(R.id.planPriceHTxt);
        cardPaymentTxt = findViewById(R.id.cardPaymentTxt);

        paymentWebview =  findViewById(R.id.paymentWebview);
        paymentWebViewArea = findViewById(R.id.paymentWebViewArea);
        loaderView =  findViewById(R.id.loaderView);


        addToClickHandler(backImgView);
        subScribeBtnTxt.setId(Utils.generateViewId());
        addToClickHandler(subScribeBtnTxt);
    }


    public void onClick(View view) {
        Utils.hideKeyboard(SubscriptionPaymentActivity.this);
        int i = view.getId();
        if (i == R.id.backImgView) {
            SubscriptionPaymentActivity.super.onBackPressed();
        } else if (i == subScribeBtnTxt.getId()){
            checkValues();
        } else if (i == R.id.walletArea) {
            cb_wallet.setChecked(!cb_wallet.isChecked());
        } else if (i == R.id.cardArea) {
            cardPaymentRadioBtn.setChecked(!cardPaymentRadioBtn.isChecked());
        }
    }


    private void checkValues() {

        if (!cb_wallet.isChecked() && !cardPaymentRadioBtn.isChecked()) {
            generalFunc.showMessage(subScribeBtnTxt, generalFunc.retrieveLangLBl("", "LBL_SELECT_PAYMENT_METHOD_DESC_TXT"));
            return;
        }


        confirmSubscription();


    }


    public void checkPaymentCard() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "CheckCard");
        parameters.put("iUserId", generalFunc.getMemberId());

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

                    if (responseStringObject != null && !responseStringObject.equals("")) {

                        String action = generalFunc.getJsonValueStr(Utils.action_str, responseStringObject);
                        if (action.equals("1")) {
                            confirmSubscription();
                        } else {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });

    }

    private void confirmSubscription() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();

            } else {
                generateAlert.closeAlertBox();
                subscribePlan("");
            }

        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_ENABLE_SUBSCRIPTION_NOTE"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
        generateAlert.showAlertBox();

    }



    private void subscribePlan(String isUpgrade) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "SubscribePlan");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("isCard", cardPaymentRadioBtn.isChecked() ? "Yes" : "No");
        parameters.put("isWallet", cb_wallet.isChecked() ? "Yes" : "No");
        parameters.put("iDriverSubscriptionPlanId", planDetails.get("iDriverSubscriptionPlanId"));

        if (isUpgrade.equalsIgnoreCase("Yes")) {
            parameters.put("isUpgrade", isUpgrade);
        }


        ServerTask exeWebServer = ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

                    if (responseString != null && !responseString.equals("")) {

                        if (generalFunc.checkDataAvail(Utils.action_str, responseStringObject)) {

                            String message = generalFunc.getJsonValueStr(Utils.message_str, responseStringObject);
                            String isUpgradeStr = generalFunc.getJsonValueStr("isUpgrade", responseStringObject);
                            String loadWebView = generalFunc.getJsonValueStr("loadWebView", responseStringObject);

                            if (isUpgradeStr.equalsIgnoreCase("Yes")) {
                                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                                generateAlert.setCancelable(false);
                                generateAlert.setBtnClickList(btn_id -> {
                                    if (btn_id == 0) {
                                        generateAlert.closeAlertBox();

                                    } else {
                                        generateAlert.closeAlertBox();
                                        subscribePlan(isUpgradeStr);

                                    }

                                });
                                generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                                generateAlert.showAlertBox();

                            } else {
                                //LBL_LOW_WALLET_BAL_NOTE
                                if (loadWebView.equalsIgnoreCase("Yes")) {
                                    paymentWebview.setWebViewClient(new myWebClient());
                                    paymentWebview.getSettings().setJavaScriptEnabled(true);
                                    paymentWebview.loadUrl(message);
                                    paymentWebview.setFocusable(true);
                                    paymentWebview.setVisibility(View.VISIBLE);
                                    paymentWebViewArea.setVisibility(View.VISIBLE);
                                    loaderView.setVisibility(View.VISIBLE);
                                } else {
                                    redirectToThankYouScreen();
                                }
                            }

                        } else {

                            if (!generalFunc.getJsonValue("ADD_CARD_URL", responseString).equalsIgnoreCase("")) {
                                paymentWebview.setWebViewClient(new myWebClient());
                                paymentWebview.getSettings().setJavaScriptEnabled(true);
                                paymentWebview.loadUrl(generalFunc.getJsonValue("ADD_CARD_URL", responseString));
                                paymentWebview.setFocusable(true);
                                paymentWebview.setVisibility(View.VISIBLE);
                                paymentWebViewArea.setVisibility(View.VISIBLE);
                                loaderView.setVisibility(View.VISIBLE);
                            } else {
                                generalFunc.showMessage(subScribeBtnTxt, generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseStringObject)));
                            }
                        }
                    } else {
                        generalFunc.showError();
                    }
                });
        exeWebServer.setCancelAble(false);
    }

    private void redirectToThankYouScreen() {
        new ActUtils(getActContext()).startActForResult(SubscribedPlanConfirmationActivity.class, TRANSACTION_COMPLETED);
    }


    public void buildLowBalanceMessage(final Context context, String message, final Bundle bn) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_cash_balance_dialoge, null);
        builder.setView(dialogView);

        final MTextView addNowTxtArea =  dialogView.findViewById(R.id.addNowTxtArea);
        final MTextView msgTxt =  dialogView.findViewById(R.id.msgTxt);
        final MTextView skipTxtArea =  dialogView.findViewById(R.id.skipTxtArea);
        final MTextView titileTxt =  dialogView.findViewById(R.id.titileTxt);
        titileTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LOW_BALANCE"));

        if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
            addNowTxtArea.setText(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
        } else {
            addNowTxtArea.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_NOW"));
        }


        skipTxtArea.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        msgTxt.setText(message);


        skipTxtArea.setOnClickListener(view -> cashBalAlertDialog.dismiss());

        addNowTxtArea.setOnClickListener(view -> {
            cashBalAlertDialog.dismiss();
            if (APP_PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                new ActUtils(context).startAct(ContactUsActivity.class);

            } else {
                new ActUtils(context).startActForResult(MyWalletActivity.class, bn, WALLET_MONEY_ADDED);
            }

        });
        cashBalAlertDialog = builder.create();
        cashBalAlertDialog.setCancelable(false);
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(cashBalAlertDialog);
        }
        cashBalAlertDialog.show();
    }

    public Context getActContext() {
        return SubscriptionPaymentActivity.this; // Must be context of activity not application
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TRANSACTION_COMPLETED && resultCode == RESULT_OK) {
            Logger.d("DEBUG", "TRANSACTION_COMPLETED::PAYMENT");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else if (requestCode == WALLET_MONEY_ADDED && resultCode == RESULT_OK) {
            Logger.d("DEBUG", "WALLET_MONEY_ADDED::");
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            getUserPeofileJson(generalFunc.getJsonObject(userProfileJson));

            walletBalanceValTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("user_available_balance", userProfileJson))));

        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            getUserPeofileJson(generalFunc.getJsonObject(userProfileJson));
            walletBalanceValTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("user_available_balance", userProfileJson))));
        }
    }

    public class myWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            String data = url;
            data = data.substring(data.indexOf("data") + 5, data.length());
            loaderView.setVisibility(View.VISIBLE);
            view.setOnTouchListener(null);

            if (url.contains("success=1")) {
                paymentWebview.setVisibility(View.GONE);
                paymentWebViewArea.setVisibility(View.GONE);
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("", "LBL_SUBSCRIBED_SUCCESFULLY_TXT")), "", generalFunc.retrieveLangLBl("", "LBL_OK"), i -> redirectToThankYouScreen());
            }

            if (url.contains("success=0")) {
                paymentWebview.setVisibility(View.GONE);
                paymentWebViewArea.setVisibility(View.GONE);

                String message = null;
                if (Utils.checkText(url) && url.contains("&message=")) {
                    String msg = GeneralFunctions.substringAfterLast(url, "&message=");
                    message = Utils.checkText(msg) ? msg.replaceAll("%20", " ") : message;
                } else {
                    message = generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS");

                }

                generalFunc.showGeneralMessage("", message, "", generalFunc.retrieveLangLBl("", "LBL_OK"), i -> {
                });

            }

        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            generalFunc.showError();
            loaderView.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            loaderView.setVisibility(View.GONE);

            view.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            });

        }
    }


    @Override
    public void onBackPressed() {

        if (paymentWebview.getVisibility() == View.VISIBLE) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_CANCEL_PAYMENT_PROCESS"), generalFunc.retrieveLangLBl("", "LBL_NO"), generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                if (buttonId == 1) {
                    paymentWebview.setVisibility(View.GONE);
                    paymentWebview.stopLoading();
                    loaderView.setVisibility(View.GONE);

                    cardPaymentRadioBtn.setChecked(false);
                    cb_wallet.setChecked(false);
                }
            });

            return;
        }

        super.onBackPressed();
    }

    public void getWalletBalDetails() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetMemberWalletBalance");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);

        ApiHandler.execute(getActContext(), parameters,
                responseString -> {
                    JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

                    if (responseStringObject != null && !responseStringObject.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject);

                        if (isDataAvail) {
                            try {
                                String userProfileJsonStr = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                                JSONObject object = generalFunc.getJsonObject(userProfileJsonStr);
                                String MemberBalance = generalFunc.getJsonValueStr("MemberBalance", responseStringObject);
                                object.put("user_available_balance", MemberBalance);
                                generalFunc.storeData(Utils.USER_PROFILE_JSON, object.toString());

                                getUserPeofileJson(object);


                                walletBalanceValTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("user_available_balance", obj_userProfile))));

                            } catch (Exception e) {

                            }
                        }
                    }
                });
    }
}