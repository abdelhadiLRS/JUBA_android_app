package com.multixpro.user.giftcard;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityGiftcardRedeemBinding;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import java.util.HashMap;

public class GiftCardRedeemActivity extends ParentActivity {

    private ActivityGiftcardRedeemBinding binding;
    private MTextView titleTxt;
    private String required_str = "";
    private MButton redeemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_giftcard_redeem);

        initViews();
        setLabel();
    }

    private void initViews() {
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        titleTxt = findViewById(R.id.titleTxt);

        redeemBtn = ((MaterialRippleLayout) findViewById(R.id.redeemBtn)).getChildView();
        redeemBtn.setId(Utils.generateViewId());
        addToClickHandler(redeemBtn);
    }

    private Context getActContext() {
        return GiftCardRedeemActivity.this;
    }

    private void setLabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_REDEEM_GIFT_CARD_TXT"));
        redeemBtn.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_REDEEM_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");

        binding.voucherHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_ADD_TXT"));
        binding.voucherCodeEditBox.setHint(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_VOUCHER_CODE_TXT"));
        binding.voucherCodeEditBox.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.voucherCodeHelperTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_ENTER_CODE_TO_CLAIM_TXT"));
    }

    private void checkValues() {
        boolean VoucherEntered = Utils.checkText(binding.voucherCodeEditBox) || Utils.setErrorFields(binding.voucherCodeEditBox, required_str);
        if (!VoucherEntered) {
            return;
        }
        RedeemGiftCard();
    }

    private void RedeemGiftCard() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "RedeemGiftCard");
        parameters.put("vGiftCardCode", binding.voucherCodeEditBox.getText().toString().trim());

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    GiftRedeemDialog bottomInfoDialog = new GiftRedeemDialog(getActContext(), generalFunc);
                    bottomInfoDialog.showPreferenceDialog(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_OK_BTN_TXT"));
                    bottomInfoDialog.setListener(this::finish);
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
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
        } else if (i == redeemBtn.getId()) {
            checkValues();
        }
    }
}