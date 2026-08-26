package com.multixpro.user.rideSharing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.dialogs.OpenListView;
import com.fontanalyzer.SystemFont;
import com.general.call.CommunicationManager;
import com.general.call.DefaultCommunicationHandler;
import com.general.call.MediaDataProvider;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.ContactUsActivity;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityRideBookDetailsBinding;
import com.multixpro.user.databinding.ItemRideDetailsSummaryBinding;
import com.service.handler.ApiHandler;
import com.utils.LoadImageGlide;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RideBookDetails extends ParentActivity {

    private ActivityRideBookDetailsBinding binding;
    private HashMap<String, String> myRideDataHashMap;

    private AlertDialog dialogRideDecline;
    private ImageView filterImageview;
    private MButton cancelRideBtn, continueBtn;
    private int selCurrentPosition = -1;
    private boolean isDeclineCancel = false;
    private String cImage = "";
    private String callingMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_book_details);

        myRideDataHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("myRideDataHashMap");
        if (myRideDataHashMap == null) {
            return;
        }

        initialization();
        rideDetails();

        setPaymentData();
        driverDetails();
        carDetails();


        if (myRideDataHashMap.containsKey("eStatus")) {

            isDeclineCancel = myRideDataHashMap.get("eStatus").equalsIgnoreCase("Declined") || myRideDataHashMap.get("eStatus").equalsIgnoreCase("Cancelled");
            if (isDeclineCancel) {
                cancelRideBtn.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_REASON"));
            } else {
                cancelRideBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_CANCEL"));
            }
        }
        if (getIntent().getBooleanExtra("isSearchView", false)) {
            binding.paymentArea.setVisibility(View.GONE);
            binding.cancelRideBtnArea.setVisibility(View.GONE);

            binding.bottomArea.setVisibility(View.VISIBLE);
            binding.totalHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_TOTAL_PRICE"));
            binding.noOfPassengerText.setText(myRideDataHashMap.get("vNoOfPassengerText"));
            binding.fPriceTxt.setText(myRideDataHashMap.get("fPrice"));

            continueBtn = ((MaterialRippleLayout) binding.continueBtn).getChildView();
            continueBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_CONTINUE"));
            continueBtn.setId(Utils.generateViewId());
            addToClickHandler(continueBtn);
        } else {
            binding.bottomArea.setVisibility(View.GONE);
        }
    }

    private void initialization() {
        callingMethod = generalFunc.getJsonValue("CALLING_METHOD_RIDE_SHARE", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_RIDE_DETAILS"));
        filterImageview = findViewById(R.id.filterImageview);
        filterImageview.setVisibility(View.VISIBLE);
        filterImageview.setImageResource(R.drawable.ic_contacus_mail);
        addToClickHandler(filterImageview);

        cancelRideBtn = ((MaterialRippleLayout) binding.cancelRideBtn).getChildView();
        cancelRideBtn.setId(Utils.generateViewId());
        addToClickHandler(cancelRideBtn);
    }

    private void rideDetails() {
        binding.rideDetailsTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_ROUTE_DETAILS"));
        binding.startTimeTxt.setText(myRideDataHashMap.get("StartTime"));
        binding.endTimeTxt.setText(myRideDataHashMap.get("EndTime"));

        binding.startCityTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DETAILS_START_LOC_TXT"));
        binding.endCityTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DETAILS_END_LOC_TXT"));

        binding.startAddressTxt.setText(myRideDataHashMap.get("tStartLocation"));
        binding.endAddressTxt.setText(myRideDataHashMap.get("tEndLocation"));

        binding.dateTxt.setText(myRideDataHashMap.get("StartDate"));
        binding.priceTxt.setText(myRideDataHashMap.get("fPrice"));
        binding.priceMsgTxt.setText(myRideDataHashMap.get("PriceLabel"));
    }

    @SuppressLint("SetTextI18n")
    private void setPaymentData() {
        binding.paymentHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PYMENT_DETAILS"));
        binding.paymentModeHTxt.setText(myRideDataHashMap.get("PaymentModeTitle") + ": ");
        binding.paymentModeVTxt.setText(myRideDataHashMap.get("PaymentModeLabel"));

        JSONArray summaryArray = generalFunc.getJsonArray(myRideDataHashMap.get("PriceBreakdown"));
        if (summaryArray != null) {

            if (binding.summaryData.getChildCount() > 0) {
                binding.summaryData.removeAllViewsInLayout();
            }

            for (int i = 0; i < summaryArray.length(); i++) {
                JSONObject jobject = generalFunc.getJsonObject(summaryArray, i);
                try {
                    String data = Objects.requireNonNull(jobject.names()).getString(0);

                    addSummaryRow(data, jobject.get(data).toString(), (summaryArray.length() - 1) == i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        binding.summaryDataArea.setVisibility(0 < binding.summaryData.getChildCount() ? View.VISIBLE : View.GONE);
    }

    private void addSummaryRow(String rName, String rValue, boolean isLast) {
        if (rName.equalsIgnoreCase("eDisplaySeperator")) {
            View convertView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(this, 1));
            convertView.setLayoutParams(params);
            convertView.setBackgroundColor(getResources().getColor(R.color.view23ProBG));
            binding.summaryData.addView(convertView);
        } else {
            final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @NonNull ItemRideDetailsSummaryBinding bindingItem = ItemRideDetailsSummaryBinding.inflate(inflater, binding.summaryData, false);

            bindingItem.summaryHTxt.setText(generalFunc.convertNumberWithRTL(rName));
            bindingItem.summaryVTxt.setText(generalFunc.convertNumberWithRTL(rValue));

            binding.summaryData.addView(bindingItem.getRoot());

            if (isLast) {
                bindingItem.summaryHTxt.setTextColor(getResources().getColor(R.color.text23Pro_Dark));
                bindingItem.summaryHTxt.setTypeface(SystemFont.FontStyle.BOLD.font);
                bindingItem.summaryHTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                bindingItem.summaryVTxt.setTextColor(getResources().getColor(R.color.appThemeColor_1));
                bindingItem.summaryVTxt.setTypeface(SystemFont.FontStyle.BOLD.font);
                bindingItem.summaryVTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
        }
    }


    private void driverDetails() {
        binding.driverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DRIVER_DETAILS_TITLE"));
        binding.rideDriverName.setText(myRideDataHashMap.get("DriverName"));
        binding.rideDriverPhone.setText(myRideDataHashMap.get("DriverPhone"));
        binding.rideDriverPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                        .setCallId(myRideDataHashMap.get("iDriverId"))
                        .setToMemberName(myRideDataHashMap.get("DriverName"))
                        .setPhoneNumber(myRideDataHashMap.get("DriverPhone"))
                        .setToMemberType(Utils.CALLTOPASSENGER)
                        .setToMemberImage(myRideDataHashMap.get("rider_ProfileImg"))
                        .setMedia(CommunicationManager.MEDIA_TYPE)
                        .build();
                if (callingMethod.equalsIgnoreCase("VOIP")) {
                    CommunicationManager.getInstance().communicatePhoneOrVideo(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.PHONE_CALL);
                } else if (callingMethod.equalsIgnoreCase("VIDEOCALL")) {
                    CommunicationManager.getInstance().communicatePhoneOrVideo(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.VIDEO_CALL);
                } else if (callingMethod.equalsIgnoreCase("VOIP-VIDEOCALL")) {
                    CommunicationManager.getInstance().communicatePhoneOrVideo(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.BOTH_CALL);
                } else if (!Utils.checkText(callingMethod) ||
                        callingMethod.equalsIgnoreCase("NORMAL")) {
                    DefaultCommunicationHandler.getInstance().executeAction(MyApp.getInstance().getCurrentAct(), CommunicationManager.TYPE.PHONE_CALL, mDataProvider);
                }
            }

        });
        String dImage = myRideDataHashMap.get("DriverImg");
        if (!Utils.checkText(dImage)) {
            dImage = "Temp";
        }
        new LoadImageGlide.builder(this, LoadImageGlide.bind(dImage), binding.rideDriverImg).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();
    }

    @SuppressLint("SetTextI18n")
    private void carDetails() {
        binding.carDetailsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_CAR_DETAILS_TITLE"));
        binding.addNotesHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_ADDITIONAL_NOTES_TXT"));

        JSONObject carDetailsObj = generalFunc.getJsonObject(myRideDataHashMap.get("carDetails"));
        binding.carModelColorTxt.setText(generalFunc.getJsonValueStr("cModel", carDetailsObj));
        binding.carMakeTxt.setText(generalFunc.getJsonValueStr("cMake", carDetailsObj));
        binding.carNumberPlateTxt.setText(generalFunc.getJsonValueStr("cNumberPlate", carDetailsObj));
        binding.addNotesMsgHTxt.setText(generalFunc.getJsonValueStr("cNote", carDetailsObj));

        LinearLayout.LayoutParams lyParamsBannerArea = (LinearLayout.LayoutParams) binding.setImgView.getLayoutParams();
        lyParamsBannerArea.width = Utils.getWidthOfBanner(this, (int) getResources().getDimension(R.dimen._36sdp));
        lyParamsBannerArea.height = Utils.getHeightOfBanner(this, 0, "16:9");
        binding.setImgView.setLayoutParams(lyParamsBannerArea);
        cImage = generalFunc.getJsonValueStr("cImage", carDetailsObj);
        String cImage_view = Utils.getResizeImgURL(this, Objects.requireNonNull(generalFunc.getJsonValueStr("cImage", carDetailsObj)), lyParamsBannerArea.width, lyParamsBannerArea.height);
        if (!Utils.checkText(cImage)) {
            cImage = "Temp";
        }
        new LoadImageGlide.builder(this, LoadImageGlide.bind(cImage_view), binding.setImgView).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();
        addToClickHandler(binding.setImgView);
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        } else if (i == binding.setImgView.getId()) {
            new ActUtils(this).openURL(cImage);
        } else if (i == filterImageview.getId()) {
            new ActUtils(this).startAct(ContactUsActivity.class);
        } else if (i == cancelRideBtn.getId()) {
            if (myRideDataHashMap.containsKey("eStatus")) {
                if (isDeclineCancel) {
                    generalFunc.showGeneralMessage("", myRideDataHashMap.get("tCancelReason"));
                } else {
                    getRideDeclineReasonsList();
                }
            } else {
                Bundle bn = new Bundle();
                bn.putSerializable("myRideDataHashMap", myRideDataHashMap);
                new ActUtils(this).startActWithData(RideBookSummary.class, bn);
            }
        } else if (i == continueBtn.getId()) {
            Bundle bn = new Bundle();
            bn.putSerializable("myRideDataHashMap", myRideDataHashMap);
            new ActUtils(this).startActWithData(RideBookSummary.class, bn);
        }
    }

    private void getRideDeclineReasonsList() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetCancelReasons");
        parameters.put("iPublishedRideId", myRideDataHashMap.get("iPublishedRideId"));
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eUserType", Utils.app_type);

        ApiHandler.execute(this, parameters, true, false, generalFunc, responseString -> {

            if (Utils.checkText(responseString)) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    showRideDeclineReasonsAlert(responseString);
                } else {
                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    if (message.equals("DO_RESTART") || message.equals(Utils.GCM_FAILED_KEY) || message.equals(Utils.APNS_FAILED_KEY)
                            || message.equals("LBL_SERVER_COMM_ERROR")) {

                        MyApp.getInstance().restartWithGetDataApp();
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message));
                    }
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    private void showRideDeclineReasonsAlert(String responseString) {
        if (dialogRideDecline != null) {
            if (dialogRideDecline.isShowing()) {
                dialogRideDecline.dismiss();
            }
            dialogRideDecline = null;
        }
        selCurrentPosition = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.decline_order_dialog_design, null);
        builder.setView(dialogView);

        MaterialEditText reasonBox = dialogView.findViewById(R.id.inputBox);
        RelativeLayout commentArea = dialogView.findViewById(R.id.commentArea);
        reasonBox.setHideUnderline(true);
        int size10sdp = (int) getResources().getDimension(R.dimen._10sdp);
        if (generalFunc.isRTLmode()) {
            reasonBox.setPaddings(0, 0, size10sdp, 0);
        } else {
            reasonBox.setPaddings(size10sdp, 0, 0, 0);
        }

        reasonBox.setSingleLine(false);
        reasonBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        reasonBox.setGravity(Gravity.TOP);
        if (generalFunc.isRTLmode()) {
            reasonBox.setPaddings(0, 0, size10sdp, 0);
        } else {
            reasonBox.setPaddings(size10sdp, 0, 0, 0);
        }
        reasonBox.setVisibility(View.GONE);
        commentArea.setVisibility(View.GONE);
        new CreateRoundedView(getResources().getColor(R.color.white), 5, 1, Color.parseColor("#C5C3C3"), commentArea);
        reasonBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_ENTER_REASON"));

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        JSONArray arr_msg = generalFunc.getJsonArray(Utils.message_str, responseString);
        if (arr_msg != null) {
            int arrSize = arr_msg.length();
            for (int i = 0; i < arrSize; i++) {
                JSONObject obj_tmp = generalFunc.getJsonObject(arr_msg, i);
                HashMap<String, String> datamap = new HashMap<>();
                datamap.put("title", generalFunc.getJsonValueStr("vTitle", obj_tmp));
                datamap.put("id", generalFunc.getJsonValueStr("iCancelReasonId", obj_tmp));
                list.add(datamap);
            }

            HashMap<String, String> othermap = new HashMap<>();
            othermap.put("title", generalFunc.retrieveLangLBl("", "LBL_OTHER_TXT"));
            othermap.put("id", "");
            list.add(othermap);
            MTextView cancelTxt = dialogView.findViewById(R.id.cancelTxt);
            MTextView submitTxt = dialogView.findViewById(R.id.submitTxt);
            MTextView subTitleTxt = dialogView.findViewById(R.id.subTitleTxt);
            ImageView cancelImg = dialogView.findViewById(R.id.cancelImg);

            subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_CANCEL_RIDE"));

            submitTxt.setText(generalFunc.retrieveLangLBl("", "LBL_YES"));
            cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO"));
            MTextView declinereasonBox = dialogView.findViewById(R.id.declinereasonBox);
            declinereasonBox.setText("-- " + generalFunc.retrieveLangLBl("", "LBL_SELECT_CANCEL_REASON") + " --");
            submitTxt.setClickable(false);
            submitTxt.setTextColor(getResources().getColor(R.color.gray_holo_light));

            submitTxt.setOnClickListener(v -> {
                if (selCurrentPosition == -1) {
                    return;
                }
                if (!Utils.checkText(reasonBox) && selCurrentPosition == (list.size() - 1)) {
                    reasonBox.setError(generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT"));
                    return;
                }
                cancelRideSharing(myRideDataHashMap.get("iBookingId"), list.get(selCurrentPosition).get("id"), Utils.getText(reasonBox));
                dialogRideDecline.dismiss();
            });
            cancelTxt.setOnClickListener(v -> {
                Utils.hideKeyboard(this);
                dialogRideDecline.dismiss();
            });

            cancelImg.setOnClickListener(v -> {
                Utils.hideKeyboard(this);
                dialogRideDecline.dismiss();
            });

            declinereasonBox.setOnClickListener(v -> OpenListView.getInstance(this, generalFunc.retrieveLangLBl("", "LBL_SELECT_REASON"), list, OpenListView.OpenDirection.CENTER, true, position -> {
                selCurrentPosition = position;
                HashMap<String, String> mapData = list.get(position);
                declinereasonBox.setText(mapData.get("title"));
                if (selCurrentPosition == (list.size() - 1)) {
                    reasonBox.setVisibility(View.VISIBLE);
                    commentArea.setVisibility(View.VISIBLE);
                } else {
                    commentArea.setVisibility(View.GONE);
                    reasonBox.setVisibility(View.GONE);
                }
                submitTxt.setClickable(true);
                submitTxt.setTextColor(getResources().getColor(R.color.white));
            }).show(selCurrentPosition, "title"));
            dialogRideDecline = builder.create();
            dialogRideDecline.setCancelable(false);
            dialogRideDecline.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.all_roundcurve_card));
            dialogRideDecline.show();
        } else {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_NO_DATA_AVAIL"));
        }
    }

    private void cancelRideSharing(String iBookingId, String iCancelReasonId, String reason) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "CancelRideShareBooking");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("iBookingId", iBookingId);
        parameters.put("iCancelReasonId", iCancelReasonId);
        parameters.put("tCancelReason", reason);

        ApiHandler.execute(this, parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), "", generalFunc.retrieveLangLBl("", "LBL_OK"), i -> {
                        (new ActUtils(this)).setOkResult();
                        finish();
                    });
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });

    }
}