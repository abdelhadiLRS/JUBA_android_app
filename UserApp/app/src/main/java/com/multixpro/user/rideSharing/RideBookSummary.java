package com.multixpro.user.rideSharing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.CustomDialog;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.MyWalletActivity;
import com.multixpro.user.PaymentWebviewActivity;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityRideBookSummaryBinding;
import com.multixpro.user.databinding.ItemRideBookSummaryBinding;
import com.service.handler.ApiHandler;
import com.service.server.ServerTask;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class RideBookSummary extends ParentActivity {

    private ActivityRideBookSummaryBinding binding;
    private HashMap<String, String> myRideDataHashMap;
    private ServerTask currentExeTask;
    private MButton continueBtn;
    private boolean isProcessAPI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_book_summary);

        myRideDataHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("myRideDataHashMap");
        if (myRideDataHashMap == null) {
            return;
        }

        initialization();
        getBookSummaryList();
    }

    private void initialization() {
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_BOOKING_SUMMARY"));

        binding.requestReplayHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_REQUEST_REPLY_BY_TXT"));
        binding.totalHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_TOTAL_PRICE"));

        continueBtn = ((MaterialRippleLayout) binding.continueBtn).getChildView();
        continueBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_CONTINUE"));
        continueBtn.setId(Utils.generateViewId());
        addToClickHandler(continueBtn);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getBookSummaryList() {
        binding.mainDetailArea.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "BookingSummary");

        parameters.put("iPublishedRideId", myRideDataHashMap.get("iPublishedRideId"));
        parameters.put("iBookNoOfSeats", myRideDataHashMap.get("selectedNoOfSeats"));

        ApiHandler.execute(this, parameters, responseString -> {

            binding.loading.setVisibility(View.GONE);
            binding.mainDetailArea.setVisibility(View.VISIBLE);

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);

                    binding.requestReplayTxt.setText(generalFunc.getJsonValue("dStartDate", message));
                    binding.fPriceTxt.setText(generalFunc.getJsonValue("TotalPrice", message));
                    binding.noOfPassengerText.setText(myRideDataHashMap.get("vNoOfPassengerText"));

                    JSONArray summaryArray = generalFunc.getJsonArray("Summary", message);
                    if (summaryArray != null) {

                        if (binding.summaryData.getChildCount() > 0) {
                            binding.summaryData.removeAllViewsInLayout();
                        }

                        for (int i = 0; i < summaryArray.length(); i++) {
                            JSONObject jobject = generalFunc.getJsonObject(summaryArray, i);
                            try {
                                String data = Objects.requireNonNull(jobject.names()).getString(0);

                                addSummaryRow(data, jobject.get(data).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)),
                            "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> onBackPressed());
                }
            } else {
                generalFunc.showError();

            }
        });
    }

    private void addSummaryRow(String rName, String rValue) {
        if (rName.equalsIgnoreCase("eDisplaySeperator")) {
            View convertView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(this, 1));
            convertView.setLayoutParams(params);
            convertView.setBackgroundColor(getResources().getColor(R.color.view23ProBG));
            binding.summaryData.addView(convertView);
        } else {
            final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @NonNull ItemRideBookSummaryBinding bindingItem = ItemRideBookSummaryBinding.inflate(inflater, binding.summaryData, false);

            bindingItem.summaryHTxt.setText(generalFunc.convertNumberWithRTL(rName));
            bindingItem.summaryVTxt.setText(generalFunc.convertNumberWithRTL(rValue));

            binding.summaryData.addView(bindingItem.getRoot());
        }
    }

    ActivityResultLauncher<Intent> webViewPaymentActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                    if (isProcessAPI) {
                        return;
                    }
                    isProcessAPI = true;
                    if (data.hasExtra("iAuthorizePaymentId")) {
                        String iAuthorizePaymentId = data.getStringExtra("iAuthorizePaymentId");
                        createBookRide("Yes", iAuthorizePaymentId);
                    } else {
                        createBookRide("No", "");
                    }
                }
            });

    private void createBookRide(String isFareAuthorized, String iAuthorizePaymentId) {

        if (binding.loading.getVisibility() == View.VISIBLE) {
            return;
        }
        binding.loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "bookRide");

        parameters.put("iPublishedRideId", myRideDataHashMap.get("iPublishedRideId"));
        parameters.put("iBookNoOfSeats", myRideDataHashMap.get("selectedNoOfSeats"));

        parameters.put("isFareAuthorized", isFareAuthorized);
        parameters.put("iAuthorizePaymentId", iAuthorizePaymentId);

        if (currentExeTask != null) {
            currentExeTask.cancel(true);
            currentExeTask = null;
        }
        currentExeTask = ApiHandler.execute(this, parameters, responseString -> {

            isProcessAPI = false;
            currentExeTask = null;
            binding.loading.setVisibility(View.GONE);

            if (Utils.checkText(responseString)) {
                String message = generalFunc.getJsonValue(Utils.message_str, responseString);

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    String WebviewPayment = generalFunc.getJsonValue("WebviewPayment", responseString);

                    if (WebviewPayment.equalsIgnoreCase("Yes")) {

                        String message1 = generalFunc.getJsonValue(Utils.message_str_one, responseString);

                        generalFunc.showGeneralMessage("", message1, "", generalFunc.retrieveLangLBl("", "LBL_OK"), buttonId -> {
                            if (buttonId == 1) {

                                Intent intent = new Intent(this, PaymentWebviewActivity.class);
                                Bundle bn = new Bundle();
                                bn.putString("url", message);
                                bn.putBoolean("handleResponse", true);
                                bn.putBoolean("isBack", false);
                                bn.putString("eType", "RideShare");
                                intent.putExtras(bn);
                                webViewPaymentActivity.launch(intent);
                            }
                        });


                    } else {
                        CustomDialog customDialog = new CustomDialog(this, generalFunc);
                        customDialog.setDetails(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message_title", responseString)),
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)),
                                generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_VIEW_PUBLISHED_RIDES_TXT"),
                                generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"),
                                false, R.drawable.ic_correct_2, false, 1, true);
                        customDialog.setRoundedViewBackgroundColor(R.color.appThemeColor_1);
                        customDialog.setRoundedViewBorderColor(R.color.white);
                        customDialog.setImgStrokWidth(15);
                        customDialog.setBtnRadius(10);
                        customDialog.setIconTintColor(R.color.white);
                        customDialog.setPositiveBtnBackColor(R.color.appThemeColor_2);
                        customDialog.setPositiveBtnTextColor(R.color.white);
                        customDialog.createDialog();
                        customDialog.setPositiveButtonClick(() -> {
                            Bundle bn = new Bundle();
                            bn.putBoolean("isRestartApp", true);
                            new ActUtils(this).startActWithData(RideMyList.class, bn);
                        });
                        customDialog.setNegativeButtonClick(() -> MyApp.getInstance().restartWithGetDataApp());
                        customDialog.show();
                    }

                } else {
                    if (message.equalsIgnoreCase("LOW_WALLET_AMOUNT")) {

                        String walletMsg;
                        String low_balance_content_msg = generalFunc.getJsonValue("low_balance_content_msg", responseString);

                        if (low_balance_content_msg != null && !low_balance_content_msg.equalsIgnoreCase("")) {
                            walletMsg = low_balance_content_msg;
                        } else {
                            walletMsg = generalFunc.retrieveLangLBl("", "LBL_WALLET_LOW_AMOUNT_MSG_TXT");
                        }

                        generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("", "LBL_LOW_WALLET_BALANCE"),
                                walletMsg, generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), generalFunc.retrieveLangLBl("", "LBL_ADD_NOW"), button_Id -> {
                                    if (button_Id == 1) {
                                        new ActUtils(this).startAct(MyWalletActivity.class);
                                    }
                                });
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message));
                    }
                }
            } else {
                generalFunc.showError();

            }
        });
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();

        } else if (i == continueBtn.getId()) {

            if (isProcessAPI) {
                return;
            }

            String url = generalFunc.getJsonValue("PAYMENT_MODE_URL", obj_userProfile) + "&eType=RideShare";

            url = url + "&tSessionId=" + (generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY));
            url = url + "&GeneralUserType=" + Utils.app_type;
            url = url + "&GeneralMemberId=" + generalFunc.getMemberId();
            url = url + "&ePaymentOption=" + "Card";
            url = url + "&vPayMethod=" + "Instant";
            url = url + "&SYSTEM_TYPE=" + "APP";
            url = url + "&vCurrentTime=" + generalFunc.getCurrentDateHourMin();

            Intent intent = new Intent(this, PaymentWebviewActivity.class);
            Bundle bn = new Bundle();
            bn.putString("url", url);
            bn.putBoolean("handleResponse", true);
            bn.putBoolean("isBack", false);
            bn.putString("eType", "RideShare");
            intent.putExtras(bn);
            webViewPaymentActivity.launch(intent);

        }
    }
}