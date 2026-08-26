package com.multixpro.user.rideSharing;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.activity.ParentActivity;
import com.dialogs.OpenListView;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SpacesItemDecoration;
import com.multixpro.user.ContactUsActivity;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityRideMyDetailsBinding;
import com.multixpro.user.rideSharing.adapter.RideMyPassengerAdapter;
import com.service.handler.ApiHandler;
import com.utils.LoadImageGlide;
import com.utils.MyUtils;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RideMyDetails extends ParentActivity {

    private ActivityRideMyDetailsBinding binding;
    private HashMap<String, String> myRideDataHashMap;

    private RideMyPassengerAdapter rideMyPassengerAdapter;
    private final ArrayList<HashMap<String, String>> mRideMyPassengerList = new ArrayList<>();

    private AlertDialog dialogRideDecline;
    private int selCurrentPosition = -1;
    private MButton cancelRideBtn;
    private ImageView filterImageview;
    private String cImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_my_details);

        myRideDataHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("myRideDataHashMap");
        if (myRideDataHashMap == null) {
            return;
        }

        initialization();
        rideDetails();

        setPassengerListData();
        setPassengerList(myRideDataHashMap);

        carDetails();

        getRideMyPassengerList(false);
    }

    private void initialization() {
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
        cancelRideBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_CANCEL"));
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

    private void setPassengerListData() {
        binding.passengerHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PASSENGER_DETAILS"));
        binding.noPassengerHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_NO_REQUESTS_MSG"));

        rideMyPassengerAdapter = new RideMyPassengerAdapter(generalFunc, mRideMyPassengerList, new RideMyPassengerAdapter.OnItemClickListener() {
            @Override
            public void onViewReasonClick(HashMap<String, String> hashMap) {
                generalFunc.showGeneralMessage("", hashMap.get("DeclineReason"));
            }

            @Override
            public void onDeclineClick(HashMap<String, String> hashMap, int position) {
                getRideDeclineReasonsList("Yes", hashMap);
            }

            @Override
            public void onAcceptClick(HashMap<String, String> hashMap, int position) {
                setBookingsStatus("Approved", hashMap, "", "");
            }
        });
        binding.rvRidePassengerList.addItemDecoration(new SpacesItemDecoration(1, getResources().getDimensionPixelSize(R.dimen._12sdp), false));
        binding.rvRidePassengerList.setAdapter(rideMyPassengerAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setPassengerList(@NonNull HashMap<String, String> mHashMap) {
        // Passenger List
        mRideMyPassengerList.clear();
        MyUtils.createArrayListJSONArray(generalFunc, mRideMyPassengerList, generalFunc.getJsonArray(mHashMap.get("BookingList")));
        binding.noPassengerHTxt.setVisibility(View.GONE);
        rideMyPassengerAdapter.notifyDataSetChanged();
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

    private void getRideDeclineReasonsList(String isDeclined, @Nullable HashMap<String, String> hashMap) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetCancelReasons");
        parameters.put("iPublishedRideId", myRideDataHashMap.get("iPublishedRideId"));
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eUserType", Utils.app_type);

        if (hashMap != null && Utils.checkText(isDeclined)) {
            parameters.put("PublishedRideDecline", isDeclined);
        }

        ApiHandler.execute(this, parameters, true, false, generalFunc, responseString -> {

            if (Utils.checkText(responseString)) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    showRideDeclineReasonsAlert(responseString, isDeclined, hashMap);
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

    private void showRideDeclineReasonsAlert(String responseString, String isDeclined, @Nullable HashMap<String, String> hashMap) {
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

            if (hashMap != null && isDeclined.equalsIgnoreCase("Yes")) {
                subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DECLINE_RIDE"));
            } else {
                subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_CANCEL_RIDE"));
            }

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
                if (hashMap != null && isDeclined.equalsIgnoreCase("Yes")) {
                    setBookingsStatus("Declined", hashMap, list.get(selCurrentPosition).get("id"), Utils.getText(reasonBox));
                } else {
                    cancelRideSharing(myRideDataHashMap.get("iPublishedRideId"), list.get(selCurrentPosition).get("id"), Utils.getText(reasonBox));
                }
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

    private void cancelRideSharing(String iPublishedRideId, String iCancelReasonId, String reason) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "CancelPublishRide");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        parameters.put("iPublishedRideId", iPublishedRideId);
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

    @SuppressLint("NotifyDataSetChanged")
    private void setBookingsStatus(String status, HashMap<String, String> hashMap, String iCancelReasonId, String reason) {

        if (binding.loading.getVisibility() == View.VISIBLE) {
            return;
        }
        binding.mainArea.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "UpdateBookingsStatus");
        parameters.put("eStatus", status);

        parameters.put("iPublishedRideId", myRideDataHashMap.get("iPublishedRideId"));
        parameters.put("iBookingId", hashMap.get("iBookingId"));

        if (status.equalsIgnoreCase("Declined")) {
            parameters.put("iCancelReasonId", iCancelReasonId);
            parameters.put("tCancelReason", reason);
        }

        ApiHandler.execute(getApplicationContext(), parameters, true, false, generalFunc, responseString -> {

            binding.mainArea.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    getRideMyPassengerList(true);
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getRideMyPassengerList(boolean isLoader) {

        if (isLoader) {
            if (binding.loading.getVisibility() == View.VISIBLE) {
                return;
            }
            binding.loading.setVisibility(View.VISIBLE);
        }
        binding.mainArea.setVisibility(View.GONE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetPublishedRides");
        parameters.put("iPublishedRideId", myRideDataHashMap.get("iPublishedRideId"));

        ApiHandler.execute(this, parameters, responseString -> {

            binding.mainArea.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    JSONArray dataArray = generalFunc.getJsonArray(Utils.message_str, responseString);

                    final ArrayList<HashMap<String, String>> mRideMyList = new ArrayList<>();
                    MyUtils.createArrayListJSONArray(generalFunc, mRideMyList, dataArray);
                    if (mRideMyList.size() > 0) {
                        myRideDataHashMap = mRideMyList.get(0);
                        if (myRideDataHashMap != null) {
                            setPassengerList(myRideDataHashMap);
                        }
                        binding.noPassengerHTxt.setVisibility(mRideMyPassengerList.size() > 0 ? View.GONE : View.VISIBLE);
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
        } else if (i == cancelRideBtn.getId()) {
            getRideDeclineReasonsList("", null);
        } else if (i == filterImageview.getId()) {
            new ActUtils(this).startAct(ContactUsActivity.class);
        } else if (i == binding.setImgView.getId()) {
            new ActUtils(this).openURL(cImage);
        }
    }
}