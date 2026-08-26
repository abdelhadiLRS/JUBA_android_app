package com.multixpro.user.deliverAll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.activity.ParentActivity;
import com.multixpro.user.R;
import com.general.files.GeneralFunctions;
import com.general.files.ActUtils;

import com.service.handler.ApiHandler;
import com.utils.ModelUtils;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.MyProgressDialog;
import com.view.editBox.MaterialEditText;

import java.util.Calendar;
import java.util.HashMap;

public class PaymentCardActivity extends ParentActivity {


    ImageView backImgView;
    MTextView titleTxt;


    MaterialEditText creditCardBox;
    MaterialEditText cvvBox;
    MaterialEditText mmBox;
    MaterialEditText yyBox;
    String required_str = "";
    MButton btn_type2;
    private static final int SEL_CARD = 001;
    String iOrderId = "";
    boolean isWallet = false;

    String LBL_INVALID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card);


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        addToClickHandler(backImgView);
        creditCardBox = (MaterialEditText) findViewById(R.id.creditCardBox);
        cvvBox = (MaterialEditText) findViewById(R.id.cvvBox);
        mmBox = (MaterialEditText) findViewById(R.id.mmBox);
        yyBox = (MaterialEditText) findViewById(R.id.yyBox);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(Utils.generateViewId());
        addToClickHandler(btn_type2);
        isWallet = getIntent().getBooleanExtra("isWallet", false);

        String iOrderId = getIntent().getStringExtra("iOrderId");

        if (iOrderId != null && !iOrderId.equalsIgnoreCase("")) {
            iOrderId = iOrderId;
        }


        setLabels();

        mmBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        yyBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        cvvBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        creditCardBox.setInputType(InputType.TYPE_CLASS_PHONE);
        int maxLength = 24;
        int monthmaxLegth = 2;
        int cvvMaxLegth = 5;
        int yearMaxLength = 4;
        creditCardBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        mmBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(monthmaxLegth)});
        cvvBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(cvvMaxLegth)});
        yyBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(yearMaxLength)});
        creditCardBox.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }

            }
        });


    }

    public MyProgressDialog showLoader() {
        MyProgressDialog myPDialog = new MyProgressDialog(getActContext(), false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        myPDialog.show();

        return myPDialog;
    }


    public void setLabels() {
        LBL_INVALID = generalFunc.retrieveLangLBl("", "LBL_INVALID");
        creditCardBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_CARD_NUMBER_HEADER_TXT"), generalFunc.retrieveLangLBl("", "LBL_CARD_NUMBER_HINT_TXT"));
        cvvBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_CVV_HEADER_TXT"), generalFunc.retrieveLangLBl("", "LBL_CVV_HINT_TXT"));
        mmBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EXP_MONTH_HINT_TXT"), generalFunc.retrieveLangLBl("", "LBL_EXP_MONTH_HINT_TXT"));
        yyBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EXP_YEAR_HINT_TXT"), generalFunc.retrieveLangLBl("", "LBL_EXP_YEAR_HINT_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
    }


    public Context getActContext() {
        return PaymentCardActivity.this;
    }


    public boolean validateExpYear(Calendar now) {
        return yyBox.getText().toString() != null && !ModelUtils.hasYearPassed(GeneralFunctions.parseIntegerValue(0, yyBox.getText().toString()), now);
    }


    private void addMoneyToWallet(String token) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "addMoneyUserWalletByChargeCard");
        if (getIntent().getStringExtra("CheckUserWallet") != null) {
            parameters.put("CheckUserWallet", getIntent().getStringExtra("CheckUserWallet"));
        }
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("fAmount", getIntent().getStringExtra("fAmount"));
        parameters.put("UserType", Utils.app_type);
        parameters.put("vStripeToken", token);
        parameters.put("eSystem", Utils.eSystem_Type);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                    generateAlertBox.setCancelable(false);
                    generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)));

                    generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_OK"));
                    generateAlertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                            generateAlertBox.closeAlertBox();

                        }
                    });
                    generateAlertBox.showAlertBox();
                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });


    }

    public void orderPlaceCallForCard(String token) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CaptureCardPaymentOrder");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("ePaymentOption", "Card");
        parameters.put("iOrderId", iOrderId);
        parameters.put("vStripeToken", token);
        if (getIntent().getStringExtra("CheckUserWallet") != null) {
            parameters.put("CheckUserWallet", getIntent().getStringExtra("CheckUserWallet"));
        }
        parameters.put("eSystem", Utils.eSystem_Type);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isRestart", true);
                    bn.putString("iOrderId", getIntent().getStringExtra("iOrderId"));
                    // bn.putString("eTakeAway", eTakeAway);
                    new ActUtils(getActContext()).startActWithData(OrderPlaceConfirmActivity.class, bn);

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEL_CARD) {
            if (resultCode == RESULT_OK) {

            } else {
                onBackPressed();
            }

        }
    }

    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        }
    }


}
