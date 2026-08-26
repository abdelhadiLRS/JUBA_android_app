package com.multixpro.provider.giftcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.activity.ParentActivity;
import com.countryview.view.CountryPicker;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.provider.PaymentWebviewActivity;
import com.multixpro.provider.R;
import com.multixpro.provider.databinding.ActivityGiftcardSendBinding;
import com.multixpro.provider.giftcard.adapter.GiftCardImagePagerAdapter;
import com.service.handler.ApiHandler;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GiftCardSendActivity extends ParentActivity {

    private ActivityGiftcardSendBinding binding;
    private static final int WEBVIEWPAYMENT = 1;
    private MTextView titleTxt;
    private MButton payNowBtn;
    private String vPhoneCode = "", required_str = "", vSImage = "", error_email_str = "", selectedFilterId = "0";
    private int selFilterPos = -1, maxAmount = 0;

    private CountryPicker countryPicker;
    private GiftCardImagePagerAdapter mSendGiftCardAdapter;
    private final ArrayList<HashMap<String, String>> filterList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_giftcard_send);

        maxAmount = GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValueStr("GIFT_CARD_MAX_AMOUNT", obj_userProfile));

        initViews();
        setLabel();
        getFilterList();

        mSendGiftCardAdapter = new GiftCardImagePagerAdapter(getActContext(), filterList, position -> {
            binding.designthemeHTxt.setTextColor(getResources().getColor(R.color.black));

            selectedFilterId = filterList.get(position).get("iGiftCardImageId");
            HashMap<String, String> checkBox = filterList.get(position);

            checkBox.put("isCheck", "Yes");
            filterList.set(position, checkBox);
            if (selFilterPos != position && selFilterPos != -1) {
                HashMap<String, String> ChangecheckBox = filterList.get(selFilterPos);
                ChangecheckBox.put("isCheck", "No");
                filterList.set(selFilterPos, ChangecheckBox);
            }
            selFilterPos = position;

            if (position != 0) {
                HashMap<String, String> ChangecheckBox = filterList.get(0);
                ChangecheckBox.put("isCheck", "No");
                filterList.set(0, ChangecheckBox);
            }

            mSendGiftCardAdapter.notifyDataSetChanged();
            binding.SendGiftListRecyclerView.scrollToPosition(position);
        });
        binding.SendGiftListRecyclerView.setAdapter(mSendGiftCardAdapter);
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(binding.SendGiftListRecyclerView);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void initViews() {
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        titleTxt = findViewById(R.id.titleTxt);

        int amountlength = generalFunc.getJsonValueStr("GIFT_CARD_MAX_AMOUNT", obj_userProfile).length();
        binding.amountBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(amountlength)});
        binding.amountBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int txtValue = GeneralFunctions.parseIntegerValue(0, s.toString());
                if (maxAmount < txtValue) {
                    binding.amountBox.setText("" + maxAmount);
                    binding.amountBox.setSelection(amountlength);
                } else {
                    if (s.length() != 0) {
                        binding.PayAmountHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_PAYABLE_AMOUNT_TXT") + ": " + generalFunc.getJsonValueStr("CurrencySymbol", obj_userProfile) + " " + binding.amountBox.getText().toString());
                    } else {

                        binding.PayAmountHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_PAYABLE_AMOUNT_TXT") + ": --");
                    }
                }
            }
        });

        editBoxMultiLine(binding.personalMsgBox);

        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.DefaultCountryCode, "");
        data.put(Utils.DefaultPhoneCode, "");
        data = generalFunc.retrieveValue(data);
        vSImage = generalFunc.retrieveValue(Utils.DefaultCountryImage);

        new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();
        vPhoneCode = data.get(Utils.DefaultPhoneCode);

        if (vPhoneCode != null && !vPhoneCode.equalsIgnoreCase("")) {
            binding.countryCodeTxt.setText("+" + vPhoneCode);
        }

        String vPhone = generalFunc.getJsonValueStr("vPhone", obj_userProfile);
        if (vPhone.equals("")) {
            binding.mobileNoArea.setVisibility(View.VISIBLE);
        }

        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            binding.countryDropImg.setVisibility(View.VISIBLE);
            addToClickHandler(binding.countryArea);
            binding.countryArea.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                    view.performClick();
                }
                return true;
            });
        }

        if (generalFunc.getJsonValueStr("vSCountryImage", obj_userProfile) != null && !generalFunc.getJsonValueStr("vSCountryImage", obj_userProfile).equalsIgnoreCase("")) {
            vSImage = generalFunc.getJsonValueStr("vSCountryImage", obj_userProfile);
            new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();

        }
        addToClickHandler(binding.PreviewGiftBtn);
        payNowBtn = ((MaterialRippleLayout) findViewById(R.id.payNowBtn)).getChildView();
        payNowBtn.setId(Utils.generateViewId());
        addToClickHandler(payNowBtn);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void editBoxMultiLine(MaterialEditText editText) {
        editText.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        editText.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        editText.setOnTouchListener((v, event) -> {
            if (this.binding.personalMsgBox.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        editText.setSingleLine(false);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setGravity(Gravity.TOP);
    }

    @SuppressLint("SetTextI18n")
    private void setLabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARDT_SEND_TXT"));
        binding.designthemeHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_SELECT_IMAGE_THEME_TXT"));
        binding.amountTitle.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_ENTER_AMOUNT_TXT") + " (" + generalFunc.retrieveLangLBl("", "LBL_IN") + " " + generalFunc.getJsonValueStr("vCurrencyDriver", obj_userProfile) + ")");
        binding.AmountHintTxt.setHint(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARDT_MAX_AMOUNT_TXT") + " " + generalFunc.getJsonValueStr("CurrencySymbol", obj_userProfile) + " " + generalFunc.getJsonValueStr("GIFT_CARD_MAX_AMOUNT", obj_userProfile));
        binding.PersonalMsgHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_ENTER_MESSAGE_TXT"));
        binding.receiverDetailsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_ENTER_RECEIVER_DETAILS_TXT"));
        binding.PreviewGiftBtn.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARDT_PREVIEW_BTN_TXT"));
        binding.PaymentHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_PAYMENT_TITLE"));
        binding.PayAmountHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_PAYABLE_AMOUNT_TXT") + ": -- ");
        String firstVal = generalFunc.retrieveLangLBl("", "LBL_TERMS_CONDITION_PREFIX_TXT");
        String secondVal = generalFunc.retrieveLangLBl("", "LBL_TERMS_CONDITION_PRIVACY");
        String termsVal = firstVal + " " + secondVal;
        manageSpanView(termsVal, secondVal, binding.termsTxt);
        payNowBtn.setText(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_PAYMENT_BTN_TXT"));
        binding.mobileBoxHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_PHONE_NO_TXT"));
        binding.mobileBox.setHint(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_RECEIVER_PHONE_TXT"));
        error_email_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_EMAIL_ERROR_TXT");
        binding.receiverNameEditBox.setHint(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_RECEIVER_NAME_TXT"));
        binding.receiverEmailEditBox.setHint(generalFunc.retrieveLangLBl("", "LBL_GIFT_CARD_RECEIVER_EMAIL_TXT"));
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD");

        binding.amountBox.setHint(generalFunc.convertNumberWithRTL("0.0"));
        binding.mobileBox.getLabelFocusAnimator().start();
    }

    private Context getActContext() {
        return GiftCardSendActivity.this;
    }

    private boolean checkValues() {
        boolean amountEntered = Utils.checkText(binding.amountBox) || Utils.setErrorFields(binding.amountBox, required_str);
        boolean personalEntered = Utils.checkText(binding.personalMsgBox) || Utils.setErrorFields(binding.personalMsgBox, required_str);
        boolean mobileEntered = Utils.checkText(binding.mobileBox) || Utils.setErrorFields(binding.mobileBox, required_str);
        boolean RNameEntered = Utils.checkText(binding.receiverNameEditBox) || Utils.setErrorFields(binding.receiverNameEditBox, required_str);
        boolean REmailEntered = (Utils.checkText(binding.receiverEmailEditBox) ?
                (generalFunc.isEmailValid(binding.receiverEmailEditBox.getText().toString().trim()) || Utils.setErrorFields(binding.receiverEmailEditBox, error_email_str))
                : Utils.setErrorFields(binding.receiverEmailEditBox, required_str));
        boolean isselectedFilterId;
        if (generalFunc.retrieveValue("showCountryList").equalsIgnoreCase("Yes")) {
            binding.countryDropImg.setVisibility(View.VISIBLE);
        } else {
            binding.countryDropImg.setVisibility(View.GONE);
        }

        if (mobileEntered) {
            mobileEntered = binding.mobileBox.length() >= 3 || Utils.setErrorFields(binding.mobileBox, generalFunc.retrieveLangLBl("", "LBL_INVALID_MOBILE_NO"));
        }

        if (binding.mobileNoArea.getVisibility() == View.GONE) {
            mobileEntered = true;
        }

        if (selectedFilterId != null && !selectedFilterId.equals("") && selectedFilterId.equals("0")) {
            isselectedFilterId = false;
            binding.designthemeHTxt.setTextColor(getResources().getColor(R.color.Red));
            binding.profileContainer.fullScroll(ScrollView.FOCUS_UP);
        } else {
            isselectedFilterId = true;
            binding.designthemeHTxt.setTextColor(getResources().getColor(R.color.black));
        }
        return mobileEntered && RNameEntered && REmailEntered && isselectedFilterId && amountEntered && personalEntered;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            vPhoneCode = data.getStringExtra("vPhoneCode");
            binding.countryCodeTxt.setText("+" + vPhoneCode);
            vSImage = data.getStringExtra("vSImage");
            new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();
        }

        if (resultCode == RESULT_OK && requestCode == WEBVIEWPAYMENT) {
            finish();
        }
    }

    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        } else if (i == binding.countryArea.getId()) {

            if (countryPicker == null) {
                countryPicker = new CountryPicker.Builder(getActContext()).showingDialCode(true)
                        .setLocale(locale).showingFlag(true)
                        .enablingSearch(true)
                        .setCountrySelectionListener(country -> setData(country.getDialCode(), country.getFlagName()))
                        .build();
            }
            countryPicker.show(getActContext());

        } else if (i == binding.PreviewGiftBtn.getId() || i == payNowBtn.getId()) {

            if (checkValues()) {
                if (i == binding.PreviewGiftBtn.getId()) {
                    String Previewurl = generalFunc.getJsonValueStr("PREVIEW_GIFT_CARD_URL", obj_userProfile);
                    Previewurl = Previewurl + "&ReceiverName=" + binding.receiverNameEditBox.getText().toString();
                    Previewurl = Previewurl + "&Amount=" + binding.amountBox.getText().toString();
                    Previewurl = Previewurl + "&GiftCardImageId=" + selectedFilterId;
                    Previewurl = Previewurl + "&GeneralMemberId=" + generalFunc.getMemberId();
                    Previewurl = Previewurl + "&GeneralUserType=" + Utils.userType;
                    Previewurl = Previewurl + "&SenderMsg=" + binding.personalMsgBox.getText().toString();
                    (new ActUtils(getActContext())).openURL(Previewurl);
                } else if (i == payNowBtn.getId()) {
                    updateProfile();
                }
            }
        }
    }

    private void updateProfile() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "SendGiftCard");
        parameters.put("fAmount", binding.amountBox.getText().toString());
        parameters.put("tReceiverName", binding.receiverNameEditBox.getText().toString());
        parameters.put("tReceiverEmail", binding.receiverEmailEditBox.getText().toString().trim());
        parameters.put("tReceiverMessage", binding.personalMsgBox.getText().toString());
        parameters.put("vReceiverPhone", binding.mobileBox.getText().toString());
        parameters.put("vReceiverPhoneCode", vPhoneCode);
        parameters.put("iGiftCardImageId", selectedFilterId);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    Bundle bn = new Bundle();
                    bn.putString("url", generalFunc.getJsonValue("GIFT_CARD_PAYMENT_URL", responseString));
                    bn.putBoolean("handleResponse", true);
                    new ActUtils(getActContext()).startActForResult(PaymentWebviewActivity.class, bn, WEBVIEWPAYMENT);

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void setData(String vPhoneCode, String vSImage) {
        this.vPhoneCode = vPhoneCode;
        this.vSImage = vSImage;
        new LoadImage.builder(LoadImage.bind(vSImage), binding.countryImg).build();
        GeneralFunctions generalFunctions = new GeneralFunctions(MyApp.getInstance().getCurrentAct());
        binding.countryCodeTxt.setText("+" + generalFunctions.convertNumberWithRTL(vPhoneCode));
    }

    private void manageSpanView(String finalString, String clickAbleString, MTextView txtView) {
        SpannableString termsSpan = new SpannableString(finalString);
        termsSpan.setSpan(new ClickableSpan() {
                              @Override
                              public void onClick(View view) {
                                  if (view == binding.termsTxt) {
                                      (new ActUtils(getActContext())).openURL(generalFunc.getJsonValueStr("TERMS_&_CONDITIONS_GIFT_CARD_URL", obj_userProfile));
                                  }
                              }

                              @Override
                              public void updateDrawState(TextPaint ds) {
                                  ds.setColor(getResources().getColor(R.color.appThemeColor_1));    // you can use custom color
                                  ds.setUnderlineText(false);    // this remove the underline
                              }
                          }, // Span to add
                finalString.indexOf(clickAbleString), // Start of the span (inclusive)
                finalString.indexOf(clickAbleString) + clickAbleString.length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );
        txtView.setText(termsSpan);
        txtView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void getFilterList() {
        JSONArray arr = generalFunc.getJsonArray("GIFT_CARD_IMAGES", obj_userProfile);
        if (arr != null && arr.length() > 0) {
            int arrSize = arr.length();

            for (int i = 0; i < arrSize; i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(arr, i);
                HashMap<String, String> map = new HashMap<>();
                map.put("vImage", generalFunc.getJsonValueStr("vImage", obj_temp));
                map.put("iGiftCardImageId", generalFunc.getJsonValueStr("iGiftCardImageId", obj_temp));
                filterList.add(map);
            }
        }
    }
}