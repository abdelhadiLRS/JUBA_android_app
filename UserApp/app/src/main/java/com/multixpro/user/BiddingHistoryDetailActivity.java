package com.multixpro.user;

import static com.multixpro.user.R.id.ratingBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;

import com.activity.ParentActivity;
import com.fontanalyzer.SystemFont;
import com.general.files.ActUtils;
import com.general.files.CustomHorizontalScrollView;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SlideAnimationUtil;
import com.service.handler.ApiHandler;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BiddingHistoryDetailActivity extends ParentActivity {


    MTextView titleTxt;
    MTextView subTitleTxt;
    ImageView backImgView;
    ImageView receiptImgView;
    LinearLayout fareDetailDisplayArea;

    LinearLayout beforeServiceArea, afterServiceArea;
    String isRatingDone = "";
    MButton btn_type2;
    MTextView cartypeTxt;
    MTextView ufxratingDriverHTxt;
    SimpleRatingBar ufxratingBar;

    MTextView tipHTxt, tipamtTxt, tipmsgTxt;
    LinearLayout tipArea;
    private int rateBtnId;
    private MaterialEditText commentBox;
    ImageView helpTxt;
    ImageView tipPluseImage;
    MTextView vReasonTitleTxt;


    ProgressBar loading;
    ErrorView errorView;
    RelativeLayout container;

    CardView viewReqServicesArea;
    ImageView driverImageview;

    /* Fav Driver variable declaration end */
    FrameLayout paymentMainArea;


    int width_D;
    int width_;
    LinearLayout safetyRatingArea;
    SimpleRatingBar safetyRatingBar;
    CustomHorizontalScrollView hScrollView;
    String ShowSafetyRating = "";
    LinearLayout lineArea;
    boolean isAnimated = false;
    LinearLayout rideRatingArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidding_history_detail);


        helpTxt = (ImageView) findViewById(R.id.helpTxt);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        subTitleTxt = (MTextView) findViewById(R.id.subTitleTxt);
        receiptImgView = (ImageView) findViewById(R.id.receiptImgView);
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        vReasonTitleTxt = (MTextView) findViewById(R.id.vReasonTitleTxt);
        viewReqServicesArea = (CardView) findViewById(R.id.viewReqServicesArea);
        addToClickHandler(receiptImgView);
        paymentMainArea = findViewById(R.id.paymentMainArea);
        View commentArea = findViewById(R.id.commentArea);
        tipPluseImage = (ImageView) findViewById(R.id.tipPluseImage);
        commentBox.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        commentBox.setSingleLine(false);
        commentBox.setHideUnderline(true);
        commentBox.setGravity(GravityCompat.START | Gravity.TOP);
        commentBox.setLines(5);

        commentBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_WRITE_COMMENT_HINT_TXT"));

        new CreateRoundedView(Color.parseColor("#FFFFFF"), 0, Utils.dipToPixels(getActContext(), 1), Color.parseColor("#F2F2F2"), commentArea);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        fareDetailDisplayArea = (LinearLayout) findViewById(R.id.fareDetailDisplayArea);
        afterServiceArea = (LinearLayout) findViewById(R.id.afterServiceArea);
        beforeServiceArea = (LinearLayout) findViewById(R.id.beforeServiceArea);
        cartypeTxt = (MTextView) findViewById(R.id.cartypeTxt);

        ufxratingDriverHTxt = (MTextView) findViewById(R.id.ufxratingDriverHTxt);
        ufxratingBar = (SimpleRatingBar) findViewById(R.id.ufxratingBar);


        tipHTxt = (MTextView) findViewById(R.id.tipHTxt);
        tipamtTxt = (MTextView) findViewById(R.id.tipamtTxt);
        tipmsgTxt = (MTextView) findViewById(R.id.tipmsgTxt);

        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        container = (RelativeLayout) findViewById(R.id.container);

        tipArea = (LinearLayout) findViewById(R.id.tipArea);
        driverImageview = (SelectableRoundedImageView) findViewById(R.id.driverImgView);


        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        rateBtnId = Utils.generateViewId();
        btn_type2.setId(rateBtnId);
        addToClickHandler(btn_type2);
        addToClickHandler(viewReqServicesArea);
        getDisplayFareBiddingService();


        commentBox.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));
        addToClickHandler(backImgView);
        addToClickHandler(subTitleTxt);
        addToClickHandler(afterServiceArea);
        addToClickHandler(beforeServiceArea);
        addToClickHandler(helpTxt);
        commentBox.setOnTouchListener((v, event) -> {
            ((NestedScrollView) findViewById(R.id.scrollContainer)).requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }


    private void getDisplayFareBiddingService() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            paymentMainArea.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }


        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "displayFareBiddingService");
        parameters.put("memberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("memberType", Utils.app_type);
        if (getIntent().hasExtra("iBiddingPostId")) {
            parameters.put("iBiddingPostId", getIntent().getExtras().getString("iBiddingPostId"));
        }


        ApiHandler.execute(getActContext(), parameters, responseString -> {
            JSONObject responseObj = generalFunc.getJsonObject(responseString);

            if (responseObj != null && !responseObj.equals("")) {
                closeLoader();

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {

                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);


                    String vImage = generalFunc.getJsonValue("driverImage", message);
                    if (vImage == null || vImage.equals("") || vImage.equals("NONE")) {
                        (driverImageview).setImageResource(R.mipmap.ic_no_pic_user);
                    } else {
                        new LoadImage.builder(LoadImage.bind(vImage), driverImageview).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();
                    }

                    ((MTextView) findViewById(R.id.nameDriverVTxt)).setText(generalFunc.getJsonValue("driverName", message));
                    ((MTextView) findViewById(R.id.tripdateVTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(generalFunc.getJsonValue("dBiddingDate", message), Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext()))));

                    ((MTextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", message));
                    ((MTextView) findViewById(R.id.pickUpAddressVTxt)).setText(generalFunc.getJsonValue("tSaddress", message));


                    cartypeTxt.setText(generalFunc.getJsonValue("vServiceDetailTitle", message));

                    JSONArray FareDetailsArrNewObj = null;
                    boolean FareDetailsArrNew = generalFunc.isJSONkeyAvail("FareDetailsNewArr", message);
                    if (FareDetailsArrNew == true) {
                        FareDetailsArrNewObj = generalFunc.getJsonArray("FareDetailsNewArr", message);
                    }
                    if (FareDetailsArrNewObj != null) {
                        addFareDetailLayout(FareDetailsArrNewObj);
                    }

                    isRatingDone = generalFunc.getJsonValue("is_rating", message);

                    if (isRatingDone.equalsIgnoreCase("No") && generalFunc.getJsonValue("vTaskStatus", message).equalsIgnoreCase("Finished")) {
                        findViewById(R.id.rateDriverArea).setVisibility(View.VISIBLE);
                        findViewById(R.id.rateCardDriverArea).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.rateDriverArea).setVisibility(View.GONE);
                        findViewById(R.id.rateCardDriverArea).setVisibility(View.GONE);

                    }
                    ((SimpleRatingBar) findViewById(ratingBar)).setRating(GeneralFunctions.parseFloatValue(0, generalFunc.getJsonValue("driverAvgRating", message)));

                    ((MTextView) findViewById(R.id.rideNoVTxt)).setText("#" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vBiddingPostNo", message)));
                    LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    txtParam.setMargins(2, 10, 2, 0);
                    ((MTextView) findViewById(R.id.rideNoVTxt)).setLayoutParams(txtParam);
                    ((MTextView) findViewById(R.id.rideNoHTxt)).setLayoutParams(txtParam);

                    if (generalFunc.getJsonValue("vBiddingPaymentMode", message).equals("Cash")) {
                        ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.drawable.ic_cash_payment);
                        ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CASH_PAYMENT_TXT"));
                    } else if (generalFunc.getJsonValue("vBiddingPaymentMode", message).equals("Wallet")) {
                        ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_menu_wallet);
                        ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_PAID_VIA_WALLET"));
                    } else {
                        ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Card Payment", "LBL_CARD_PAYMENT"));
                        ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_card_new);

                    }

                    if (generalFunc.getJsonValue("ePayWallet", message).equals("Yes")) {
                        ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Paid By Wallet", "LBL_PAID_VIA_WALLET"));
                        ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_menu_wallet);
                    }

                    if (generalFunc.getJsonValue("vTaskStatus", message).equalsIgnoreCase("Finished")) {
                        receiptImgView.setVisibility(View.VISIBLE);
                    }
                    setLabels();

                    if (!generalFunc.getJsonValue("vTaskStatus", message).equalsIgnoreCase("Finished")) {

                        String cancelLable = "";
                        String cancelableReason = generalFunc.getJsonValue("vCancelReason", message);

                        if (generalFunc.getJsonValue("eCancelledBy", message).equalsIgnoreCase("DRIVER")) {
                            cancelLable = generalFunc.retrieveLangLBl("Task has been cancelled by the provider.", "LBL_PREFIX_JOB_CANCEL_PROVIDER_TXT");

                        } else {
                            cancelLable = generalFunc.retrieveLangLBl("You have cancelled this Task.", "LBL_CANCELED_TASK");
                        }


                        (findViewById(R.id.cancelReasonArea)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.tripStatusArea)).setVisibility(View.GONE);
                        ((MTextView) findViewById(R.id.vReasonHTxt)).setText(cancelLable);
                        ((MTextView) findViewById(R.id.vReasonVTxt)).setText(cancelableReason);

                        if (!generalFunc.getJsonValue("tDaddress", message).equals("")) {
                            (findViewById(R.id.destarea)).setVisibility(View.VISIBLE);
                            (findViewById(R.id.aboveLine)).setVisibility(View.VISIBLE);
                        }

                    }


                } else {
                    generateErrorView();
                }
            } else {
                generateErrorView();
            }
        });
    }

    private void showSafetyRatingArea(boolean show) {
        if (show) {

            if (generalFunc.isRTLmode()) {
                SlideAnimationUtil.slideInFromLeft(getActContext(), safetyRatingArea);
            } else {
                SlideAnimationUtil.slideOutToRight(getActContext(), safetyRatingArea);
            }

            if (!isAnimated) {
                hScrollView.setScrollingEnabled(true);

                ufxratingBar.animate().translationXBy(generalFunc.isRTLmode() ? width_ : -width_);
                rideRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? width_ : -width_);

                ufxratingBar.setIndicator(true);
                ufxratingBar.setFocusable(false);

                safetyRatingArea.setVisibility(View.VISIBLE);
                safetyRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? width_D : -width_D);

                isAnimated = true;

                hScrollView.setScrollingEnabled(false);
            }

        } else {
//

            if (generalFunc.isRTLmode()) {
                SlideAnimationUtil.slideInFromRight(getActContext(), ufxratingBar);
                SlideAnimationUtil.slideInFromRight(getActContext(), rideRatingArea);
            } else {
                SlideAnimationUtil.slideOutToLeft(getActContext(), ufxratingBar);
                SlideAnimationUtil.slideOutToLeft(getActContext(), rideRatingArea);
            }

            if (isAnimated) {
                hScrollView.setScrollingEnabled(true);

                ufxratingBar.animate().translationXBy(generalFunc.isRTLmode() ? -width_ : width_);
                rideRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? -width_ : width_);

                ufxratingBar.setIndicator(false);
                ufxratingBar.setFocusable(true);

                safetyRatingArea.animate().translationXBy(generalFunc.isRTLmode() ? -width_D : width_D);
                safetyRatingArea.setVisibility(View.GONE);

                isAnimated = false;

                hScrollView.setScrollingEnabled(false);
            }


        }
        if (show) {
            addToClickHandler(lineArea);
        } else {
            lineArea.setOnClickListener(null);
        }

    }


    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }

        if (container.getVisibility() == View.GONE) {
            container.setVisibility(View.VISIBLE);
            paymentMainArea.setVisibility(View.VISIBLE);
        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }

        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            paymentMainArea.setVisibility(View.GONE);
        }

        errorView.setOnRetryListener(() -> getDisplayFareBiddingService());
    }


    String headerLable = "", noVal = "", driverhVal = "";

    public void setLabels() {

        ((MTextView) findViewById(R.id.viewReqServicesTxtView)).setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_REQUESTED_SERVICES"));


        titleTxt.setText(generalFunc.retrieveLangLBl("RECEIPT", "LBL_RECEIPT_HEADER_TXT"));
        subTitleTxt.setText(generalFunc.retrieveLangLBl("GET RECEIPT", "LBL_GET_RECEIPT_TXT"));
        headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_TXT");
        noVal = generalFunc.retrieveLangLBl("", "LBL_TASK_TXT");
        driverhVal = generalFunc.retrieveLangLBl("", "LBL_SERVICE_PROVIDER_TXT");


        ((MTextView) findViewById(R.id.headerTxt)).setText(generalFunc.retrieveLangLBl("", headerLable));


        ((MTextView) findViewById(R.id.rideNoHTxt)).setText(noVal);
        String pickupHval = "";
        pickupHval = generalFunc.retrieveLangLBl("", "LBL_SERVICE_LOCATION");


        ((MTextView) findViewById(R.id.pickUpHTxt)).setText(pickupHval);
        ((MTextView) findViewById(R.id.pickUpAddressHTxt)).setText(pickupHval);


        ((MTextView) findViewById(R.id.chargesHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CHARGES_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Rate", "LBL_RATE_DRIVER_TXT"));


        ufxratingDriverHTxt.setText(generalFunc.retrieveLangLBl("How's your Task? Rate provider", "LBL_BIDDING_RATE_HEADING_DRIVER_TXT"));


        tipHTxt.setText(generalFunc.retrieveLangLBl("Tip Amount", "LBL_TIP_AMOUNT"));
        tipmsgTxt.setText(generalFunc.retrieveLangLBl("Thank you for giving tip for this trip.", "LBL_TIP_INFO_SHOW_RIDER"));


        ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("This Task was successfully finished", "LBL_FINISHED_TASK_TXT"));


    }


    public void displayPic(String image_url, ImageView view, final String imgType) {

        new LoadImage.builder(LoadImage.bind(image_url), view).setPlaceholderImagePath(R.color.imageBg).setPicassoListener(new LoadImage.PicassoListener() {
            @Override
            public void onSuccess() {
                if (imgType.equalsIgnoreCase("before")) {
                    findViewById(R.id.before_loading).setVisibility(View.GONE);
                    findViewById(R.id.iv_before_img).setVisibility(View.VISIBLE);
                } else if (imgType.equalsIgnoreCase("after")) {
                    findViewById(R.id.after_loading).setVisibility(View.GONE);
                    findViewById(R.id.iv_after_img).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                if (imgType.equalsIgnoreCase("before")) {
                    findViewById(R.id.before_loading).setVisibility(View.VISIBLE);
                    findViewById(R.id.iv_before_img).setVisibility(View.GONE);
                } else if (imgType.equalsIgnoreCase("after")) {
                    findViewById(R.id.after_loading).setVisibility(View.VISIBLE);
                    findViewById(R.id.iv_after_img).setVisibility(View.GONE);

                }
            }
        }).build();


    }

    private void addFareDetailLayout(JSONArray jobjArray) {

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                addFareDetailRow(jobject.names().getString(0), jobject.get(jobject.names().getString(0)).toString(), (jobjArray.length() - 1) == i ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast) {
        View convertView = null;
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            params.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._5sdp));
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_deatil_row, null);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, (int) getResources().getDimension(R.dimen._10sdp), 0, isLast ? (int) getResources().getDimension(R.dimen._10sdp) : 0);
            convertView.setLayoutParams(params);

            MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
            MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);

            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

            if (isLast) {
                // convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 40));

                // CALCULATE individual fare & show
                titleHTxt.setTextColor(getResources().getColor(R.color.black));
                titleHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                titleHTxt.setTypeface(SystemFont.FontStyle.SEMI_BOLD.font);
                titleVTxt.setTypeface(SystemFont.FontStyle.SEMI_BOLD.font);
                titleVTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                titleVTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));

            }

            fareDetailDisplayArea.addView(convertView);
        }
    }


    public void sendReceipt() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getReceipt");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iBiddingPostId", getIntent().getStringExtra("iBiddingPostId"));

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });

    }

    public Context getActContext() {
        return BiddingHistoryDetailActivity.this;
    }

    public void submitRating() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "submitRatingBiddingService");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iGeneralUserId", generalFunc.getMemberId());
        parameters.put("iBiddingPostId", getIntent().getStringExtra("iBiddingPostId"));
        parameters.put("rating", "" + ufxratingBar.getRating());
        parameters.put("message", Utils.getText(commentBox));
        parameters.put("UserType", Utils.app_type);

        if (ShowSafetyRating.equalsIgnoreCase("Yes")) {
            parameters.put("safetyRating", "" + safetyRatingBar.getRating());
        }

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(true);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    });

                    generateAlert.setContentMessage(generalFunc.retrieveLangLBl("", "LBL_TASK_COMPLETED_TXT"), generalFunc.retrieveLangLBl("", "LBL_TASK_FINISHED_TXT"));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                    generateAlert.setCancelable(false);


                } else {
                    resetRatingData();
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });

    }

    private void resetRatingData() {
        commentBox.setText("");
        ((RatingBar) findViewById(ratingBar)).setRating(0);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("isRestart", false)) {
            MyApp.getInstance().restartWithGetDataApp();
        } else {
            super.onBackPressed();
        }
    }


    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        Bundle bn = new Bundle();

        switch (view.getId()) {
            case R.id.backImgView:
                onBackPressed();
                break;

            case R.id.receiptImgView:
                sendReceipt();
                break;

            case R.id.viewReqServicesArea:
                new ActUtils(getActContext()).startActWithData(MoreServiceInfoActivity.class, bn);
                break;

            case R.id.helpTxt:
                bn.putString("iBiddingPostId", getIntent().getExtras().getString("iBiddingPostId"));
                new ActUtils(getActContext()).startActWithData(HelpMainCategory23Pro.class, bn);
                break;

            case R.id.lineArea:

                showSafetyRatingArea(false);
                break;
            case R.id.hScrollView:

                if (!ufxratingBar.isFocusable()) {
                    lineArea.performClick();
                }
                break;

        }

        if (view.getId() == rateBtnId) {
            if (((SimpleRatingBar) findViewById(R.id.ufxratingBar)).getRating() <= 0.0) {
                generalFunc.showMessage(generalFunc.getCurrentView(BiddingHistoryDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_ERROR_RATING_DIALOG_TXT"));
                return;
            }
            if (safetyRatingArea != null && safetyRatingArea.getVisibility() == View.VISIBLE && safetyRatingBar.getRating() < 0.5) {
                generalFunc.showMessage(generalFunc.getCurrentView(BiddingHistoryDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_ERROR_SAFETY_RATING_DIALOG_TXT"));
                return;
            }
            submitRating();
        }
    }

}
