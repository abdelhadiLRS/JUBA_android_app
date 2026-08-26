package com.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.CabTypeAdapter;
import com.general.files.ActUtils;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.UploadProfileImage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.multixpro.provider.FareBreakDownActivity;
import com.multixpro.provider.HailActivity;
import com.multixpro.provider.R;
import com.multixpro.provider.RentalDetailsActivity;
import com.multixpro.provider.databinding.DesginMaskVerificationBinding;
import com.model.ServiceModule;
import com.service.handler.ApiHandler;
import com.service.handler.AppService;
import com.service.model.DataProvider;
import com.service.server.ServerTask;
import com.utils.CommonUtilities;
import com.utils.LoadImage;
import com.utils.LoadImageGlide;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.editBox.MaterialEditText;
import com.view.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CabSelectionFragment extends BaseFragment implements CabTypeAdapter.OnItemClickList {

    private MButton ride_now_btn;
    private View view = null;
    HailActivity mainAct;
    GeneralFunctions generalFunc;
    JSONObject userProfileJsonObj;
    RecyclerView carTypeRecyclerView;
    CabTypeAdapter adapter;
    ArrayList<HashMap<String, String>> cabTypeList;
    public ArrayList<HashMap<String, String>> rentalTypeList;
    public ArrayList<HashMap<String, String>> tempCabTypeList = new ArrayList<>();

    String currency_sign = "";
    boolean isKilled = false;
    LinearLayout paymentArea, promoArea, cashcardarea;
    // View payTypeSelectArea;
    String appliedPromoCode = "";
    boolean isCardValidated = true, isFirstTime = false;
    MTextView payTypeTxt;
    // RadioButton cashRadioBtn;
    //  RadioButton cardRadioBtn;

    public int selpos = 0;

    // LinearLayout casharea;
    // LinearLayout cardarea;
    ImageView payImgView;
    public int isSelcted = -1;

    AVLoadingIndicatorView loaderView;
    MTextView noServiceTxt;
    boolean dialogShowOnce = true, isRental = false, isGoogle = false;

    Location tempDestLocation, tempPickUpLocation;

    double tollamount = 0.0;
    String distance = "", time = "", SelectedCarTypeID = "", tollcurrancy = "", payableAmount = "", tollskiptxt = "";
    boolean istollIgnore = false, isRouteFail = false, isTollCostdilaogshow = false, isFixFare = false;

    AlertDialog alertDialog_surgeConfirm, tolltax_dialog, uploadImgAlertDialog;

    public static int RENTAL_REQ_CODE = 1234;
    public String iRentalPackageId = "";
    //  LinearLayout rentView;
    public ImageView rentalBackImage, rentalinfoimage;
    MTextView rentalPkg;
    public MTextView rentalPkgDesc;
    View titleLineView;
    public RelativeLayout rentalarea;
    SelectableRoundedImageView rentPkgImage, rentBackPkgImage;

    ImageView clearImg;
    LinearLayout maskVerificationUploadImgArea, mCardView;
    private CardView detailArea;
    private boolean isFaceMaskVerification = false;
    private String selectedImagePath = "", RESTRICT_PASSENGER_LIMIT_NOTE = "", isDestinationAdded = "Yes";
    ProgressBar mProgressBar;
    View verticalscrollIndicator;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }

        cabTypeList = new ArrayList<>();
        rentalTypeList = new ArrayList<>();

        view = inflater.inflate(R.layout.fragment_new_cab_selection, container, false);

        mainAct = (HailActivity) requireActivity();
        generalFunc = mainAct.generalFunc;
        findRoute();
        // RideDeliveryType = getArguments().getString("RideDeliveryType");

        detailArea = (CardView) view.findViewById(R.id.detailArea);
        if (!mainAct.isVerticalCabscroll) {
            detailArea.setCardBackgroundColor(Color.parseColor("#f1f1f1"));
        }


        rentalBackImage = (ImageView) view.findViewById(R.id.rentalBackImage);
        rentalarea = (RelativeLayout) view.findViewById(R.id.rentalarea);
        rentPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentPkgImage);
        rentBackPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentBackPkgImage);
        addToClickHandler(rentalBackImage);
        carTypeRecyclerView = (RecyclerView) view.findViewById(R.id.carTypeRecyclerView);
        loaderView = (AVLoadingIndicatorView) view.findViewById(R.id.loaderView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getActContext().getResources().getColor(R.color.appThemeColor_1), android.graphics.PorterDuff.Mode.SRC_IN);
        // payTypeSelectArea = view.findViewById(R.id.payTypeSelectArea);
        payTypeTxt = (MTextView) view.findViewById(R.id.payTypeTxt);
        ride_now_btn = ((MaterialRippleLayout) view.findViewById(R.id.ride_now_btn)).getChildView();
        noServiceTxt = (MTextView) view.findViewById(R.id.noServiceTxt);
        //  rentView = view.findViewById(R.id.rentView);

        //casharea = (LinearLayout) view.findViewById(R.id.casharea);
        // cardarea = (LinearLayout) view.findViewById(R.id.cardarea);
        rentalPkg = (MTextView) view.findViewById(R.id.rentalPkg);
        rentalPkgDesc = (MTextView) view.findViewById(R.id.rentalPkgDesc);
        titleLineView = view.findViewById(R.id.titleLineView);
        rentalinfoimage = (ImageView) view.findViewById(R.id.rentalinfoimage);
        rentalPkg.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_A_CAR"));

        rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_CHOOSE_TRIP_OR_SWIPE"));

        if (mainAct.isVerticalCabscroll) {

            verticalscrollIndicator = view.findViewById(R.id.verticalscrollIndicator);
            verticalscrollIndicator.setVisibility(View.VISIBLE);
            MTextView titleTxt = view.findViewById(R.id.titleTxt);
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHOOSE_RIDE_TXT"));


            carTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mainAct, LinearLayoutManager.VERTICAL, false));
            carTypeRecyclerView.setMinimumHeight(getActContext().getResources().getDimensionPixelSize(R.dimen._400sdp));


            view.findViewById(R.id.imageViewArrow).setOnClickListener(v -> mainAct.sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));

        }


        Drawable drawable = ContextCompat.getDrawable(getActContext(), R.drawable.ic_sedan_car_front);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.appThemeColor_1));
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        }

        //rentalPkg.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);


        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 14), 1,
                getActContext().getResources().getColor(R.color.gray), rentBackPkgImage);
        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 12), 0,
                getActContext().getResources().getColor(R.color.appThemeColor_2), rentPkgImage);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_car);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        d.setColorFilter(getActContext().getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.MULTIPLY);
        rentPkgImage.setImageDrawable(d);


        //  casharea.setOnClickListener(new setOnClickList());
        // cardarea.setOnClickListener(new setOnClickList());
        addToClickHandler(rentalPkg);
        addToClickHandler(rentPkgImage);

        if (generalFunc.isRTLmode()) {
            ((ImageView) view.findViewById(R.id.rentalBackImage)).setRotationY(180);
        }

        paymentArea = (LinearLayout) view.findViewById(R.id.paymentArea);
        promoArea = (LinearLayout) view.findViewById(R.id.promoArea);
        addToClickHandler(promoArea);
        // cashRadioBtn = (RadioButton) view.findViewById(R.id.cashRadioBtn);
        // cardRadioBtn = (RadioButton) view.findViewById(R.id.cardRadioBtn);

        payImgView = (ImageView) view.findViewById(R.id.payImgView);

        cashcardarea = (LinearLayout) view.findViewById(R.id.cashcardarea);

        userProfileJsonObj = mainAct.obj_userProfile;

        currency_sign = generalFunc.getJsonValueStr("CurrencySymbol", userProfileJsonObj);


        if (ServiceModule.isServiceProviderOnly()) {
            view.setVisibility(View.GONE);
            return view;
        }

        isKilled = false;

        //cashRadioBtn.setVisibility(View.VISIBLE);
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
        //cardRadioBtn.setVisibility(View.GONE);

        setLabels();

        ride_now_btn.setId(Utils.generateViewId());
        addToClickHandler(ride_now_btn);


        configRideLaterBtnArea(false);

        mainAct.sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                if (isKilled) {
                    return;
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                if (isKilled) {
                    return;
                }
//                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                    configRideLaterBtnArea(false);
//                }
            }
        });


        if (mainAct.isVerticalCabscroll) {
            view.findViewById(R.id.requestPayAreashadowView).setVisibility(View.VISIBLE);

            mainAct.sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

                @Override
                public void onPanelSlide(View view, float v) {
                    view.findViewById(R.id.imageViewArrow).setRotation(-180 * v);

                    Logger.d("onPanelSlide", "::" + v);
                    if (v == 0.0) {
                        mainAct.sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        return;
                    }

                    if (v < 0.95) {
                        Logger.d("onPanelSlide", ":1:" + v);
                        mainAct.toolbararea.setVisibility(View.VISIBLE);
                        mainAct.destarea.setVisibility(View.VISIBLE);
                        rentalPkgDesc.setVisibility(View.GONE);
                        titleLineView.setVisibility(View.GONE);
                        view.findViewById(R.id.tollbarArea).setVisibility(View.GONE);


                        if (rentalTypeList != null && rentalTypeList.size() > 1) {
                            if (rentalBackImage.getVisibility() == View.GONE) {
                                rentalarea.setVisibility(View.VISIBLE);
                            }
                        }


                    }

                    if (v > 0.004) {
                        verticalscrollIndicator.setVisibility(View.GONE);
                        (view.findViewById(R.id.requestPayArea)).setVisibility(View.GONE);
                        rentalPkgDesc.setVisibility(View.GONE);
                        titleLineView.setVisibility(View.GONE);
                        FrameLayout.LayoutParams lyParams = (FrameLayout.LayoutParams) carTypeRecyclerView.getLayoutParams();
                        lyParams.height = (int) Utils.getScreenPixelHeight(getActContext()) - getActContext().getResources().getDimensionPixelSize(R.dimen._28sdp) - (view.findViewById(R.id.mainArea).getMeasuredHeight());
                        carTypeRecyclerView.setLayoutParams(lyParams);

                    }

                    if (v > 0.072) {
                        mainAct.destarea.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPanelStateChanged(View panelView, SlidingUpPanelLayout.PanelState panelState, SlidingUpPanelLayout.PanelState panelState1) {

                    Logger.d("onPanelStateChanged", "::" + panelState + "::" + panelState1);
                    if (panelState1 == SlidingUpPanelLayout.PanelState.EXPANDED || panelState1 == SlidingUpPanelLayout.PanelState.DRAGGING) {


                        //  mProgressBar.setVisibility(View.GONE);
                        view.findViewById(R.id.backImgView).setVisibility(View.GONE);


                        /*FrameLayout.LayoutParams lyParams = (FrameLayout.LayoutParams) carTypeRecyclerView.getLayoutParams();
                        lyParams.height = (int) Utils.getScreenPixelHeight(getActContext()) - getResources().getDimensionPixelSize(R.dimen._18sdp);

                       carTypeRecyclerView.setLayoutParams(lyParams);*/


                        if (panelState1 == SlidingUpPanelLayout.PanelState.EXPANDED) {
                            detailArea.setElevation(0);
                            //  carTypeRecyclerView.setPadding(0, getResources().getDimensionPixelSize(R.dimen._10sdp), 0, 0);
                            carTypeRecyclerView.setLayoutManager(new LinearLayoutManager(mainAct, LinearLayoutManager.VERTICAL, false));
                        }

                    } else {
                        verticalscrollIndicator.setVisibility(View.VISIBLE);
                        mainAct.toolbararea.setVisibility(View.VISIBLE);
                        mainAct.destarea.setVisibility(View.VISIBLE);
                        rentalPkgDesc.setVisibility(View.VISIBLE);
                        titleLineView.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.tollbarArea).setVisibility(View.GONE);

                        (view.findViewById(R.id.requestPayArea)).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.imageViewArrow).setVisibility(View.GONE);
//                    carTypeRecyclerView.getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
                        FrameLayout.LayoutParams lyParams = (FrameLayout.LayoutParams) carTypeRecyclerView.getLayoutParams();
                        lyParams.height = getActContext().getResources().getDimensionPixelSize(R.dimen._220sdp);
                        carTypeRecyclerView.setLayoutParams(lyParams);

                        LinearLayoutManager lm = new LinearLayoutManager(getActContext()) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        };
                        lm.setOrientation(LinearLayoutManager.VERTICAL);
                        lm.setReverseLayout(false);
                        carTypeRecyclerView.setLayoutManager(lm);
                        //   carTypeRecyclerView.setPadding(0, 0, 0, 0);
//                        carTypeRecyclerView.getLayoutManager().scrollToPosition(selpos);
                        carTypeRecyclerView.scrollToPosition(selpos > 1 ? (selpos - 2) : selpos);
                        carTypeRecyclerView.setPadding(0, 0, 0, 0);

                    }

                /*if (panelState1 == SlidingUpPanelLayout.PanelState.DRAGGING && mainAct.sliding_layout.getCurrentParallaxOffset() < 0.9) {
                    Logger.d("onPanelSlide", ":2:" + mainAct.sliding_layout.getCurrentParallaxOffset());
                    view.findViewById(R.id.tollbarArea).setVisibility(View.INVISIBLE);
                } else */
                    if (panelState1 == SlidingUpPanelLayout.PanelState.EXPANDED && (view.findViewById(R.id.tollbarArea)).getVisibility() != View.VISIBLE) {

                        Logger.d("onPanelSlide", ":3:" + mainAct.sliding_layout.getCurrentParallaxOffset());
                        view.findViewById(R.id.tollbarArea).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.imageViewArrow).setVisibility(View.VISIBLE);
                        mainAct.toolbararea.setVisibility(View.GONE);
                        mainAct.destarea.setVisibility(View.GONE);
                        rentalPkgDesc.setVisibility(View.GONE);
                        titleLineView.setVisibility(View.GONE);

                        rentalarea.setVisibility(View.GONE);

                        detailArea.setRadius(0);
                        carTypeRecyclerView.setPadding(0, getActContext().getResources().getDimensionPixelSize(R.dimen._10sdp), 0, 0);
                    }

//                    if (panelState1 == SlidingUpPanelLayout.PanelState.ANCHORED) {
//                        (view.findViewById(R.id.requestPayArea)).setVisibility(View.GONE);
//                    }


                }

            });

            (view.findViewById(R.id.requestPayAreashadowView)).setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void setTextViewDrawableColor(TextView textView) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getActContext().getResources().getColor(R.color.appThemeColor_1), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void checkSurgePrice() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "checkSurgePrice");
        parameters.put("SelectedCarTypeID", "" + SelectedCarTypeID);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);

        if (!iRentalPackageId.equalsIgnoreCase("")) {
            parameters.put("iRentalPackageId", iRentalPackageId);
        }
        if (mainAct.userLocation != null) {
            parameters.put("PickUpLatitude", "" + mainAct.userLocation.getLatitude());
            parameters.put("PickUpLongitude", "" + mainAct.userLocation.getLongitude());
        }

        if (mainAct.destlat != null && !mainAct.destlat.equalsIgnoreCase("")) {
            parameters.put("DestLatitude", "" + mainAct.destlat);
            parameters.put("DestLongitude", "" + mainAct.destlong);
        }


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseStringObj = generalFunc.getJsonObject(responseString);

                    if (responseStringObj != null && !responseStringObj.equals("")) {
                        generalFunc.sendHeartBeat();
                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObj);

                        String eFlatTrip = generalFunc.getJsonValueStr("eFlatTrip", responseStringObj);

                        if (isDataAvail) {
                            if (eFlatTrip.equalsIgnoreCase("Yes")) {
                                openFixChargeDialog(responseStringObj, false);
                            } else {
                                getTollCostValue();
                            }
                        } else {
                            if (eFlatTrip.equalsIgnoreCase("Yes")) {
                                openFixChargeDialog(responseStringObj, true);
                            } else {
                                openSurgeConfirmDialog(responseStringObj);
                            }
                        }
                    } else {
                        generalFunc.showError();
                    }
                });

    }

    private void getTollCostValue() {

        if (isFixFare) {
            startTrip();
            return;
        }


        HashMap<String, String> data = new HashMap<>();
        data.put(Utils.ENABLE_TOLL_COST, "");
        data.put(Utils.TOLL_COST_APP_ID, "");
        data.put(Utils.TOLL_COST_APP_CODE, "");
        data = generalFunc.retrieveValue(data);

        if (data.get(Utils.ENABLE_TOLL_COST).equalsIgnoreCase("Yes")) {
            String vCurrencyDriver = generalFunc.getJsonValueStr("vCurrencyDriver", userProfileJsonObj);
            String url = CommonUtilities.TOLLURL + data.get(Utils.TOLL_COST_APP_ID)
                    + "&app_code=" + data.get(Utils.TOLL_COST_APP_CODE) + "&waypoint0=" + mainAct.userLocation.getLatitude()
                    + "," + mainAct.userLocation.getLongitude() + "&waypoint1=" + mainAct.destlat + "," + mainAct.destlong + "&mode=fastest;car&tollVehicleType=car" + "&currency=" + vCurrencyDriver.toUpperCase(Locale.ENGLISH);

            ApiHandler.execute(getActContext(), url, true, true, generalFunc,
                    responseString -> {

                        if (responseString != null && !responseString.equals("")) {

                            String response = generalFunc.getJsonValue("response", responseString);
                            JSONArray route = generalFunc.getJsonArray("route", response);
                            JSONObject routeObj = generalFunc.getJsonObject(route, 0);
                            JSONObject tollCostMain = generalFunc.getJsonObject("tollCost", routeObj);

                            if (generalFunc.getJsonValueStr("onError", tollCostMain).equalsIgnoreCase("FALSE")) {
                                try {

                                    JSONObject costs = generalFunc.getJsonObject("cost", routeObj);
                                    String currency = generalFunc.getJsonValueStr("currency", costs);
                                    JSONObject details = generalFunc.getJsonObject("details", costs);
                                    String tollCost = generalFunc.getJsonValueStr("tollCost", details);
                                    if (currency != null && !currency.equals("")) {
                                        tollcurrancy = currency;
                                    }
                                    tollamount = 0.0;
                                    if (tollCost != null && !tollCost.equals("") && !tollCost.equals("0.0")) {
                                        tollamount = GeneralFunctions.parseDoubleValue(0.0, tollCost);
                                    }


                                    TollTaxDialog();


                                } catch (Exception e) {

                                    TollTaxDialog();
                                }

                            } else {
                                TollTaxDialog();
                            }


                        } else {
                            generalFunc.showError();
                        }

                    });


        } else {
            startTrip();
        }
    }

    @SuppressLint("SetTextI18n")
    public void TollTaxDialog() {

        if (!isTollCostdilaogshow) {
            if (tollamount != 0.0 && tollamount != 0 && tollamount != 0.00) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());

                LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.dialog_tolltax, null);

                final MTextView tolltaxTitle = (MTextView) dialogView.findViewById(R.id.tolltaxTitle);
                final MTextView tollTaxMsg = (MTextView) dialogView.findViewById(R.id.tollTaxMsg);
                final MTextView tollTaxpriceTxt = (MTextView) dialogView.findViewById(R.id.tollTaxpriceTxt);
                final MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);

                final CheckBox checkboxTolltax = (CheckBox) dialogView.findViewById(R.id.checkboxTolltax);

                checkboxTolltax.setOnCheckedChangeListener((buttonView, isChecked) -> istollIgnore = checkboxTolltax.isChecked());

                MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
                int submitBtnId = Utils.generateViewId();
                btn_type2.setId(submitBtnId);
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
                btn_type2.setOnClickListener(v -> {
                    tolltax_dialog.dismiss();
                    isTollCostdilaogshow = true;
                    startTrip();
                });


                builder.setView(dialogView);
                tolltaxTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_ROUTE"));
                tollTaxMsg.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_PRICE_DESC"));

                String payAmount = payableAmount;
                int pos;
                if (isSelcted == -1) {
                    pos = 0;
                } else {
                    pos = isSelcted;
                }

                if (cabTypeList != null && !SelectedCarTypeID.equals("") && cabTypeList.size() > 0 && !cabTypeList.get(pos).get("SubTotal").equals("") && !cabTypeList.get(pos).get("eRental").equalsIgnoreCase("Yes") /*&& payAmount.equalsIgnoreCase("")*/) {
                    try {
                        payAmount = generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("SubTotal"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (payAmount.equalsIgnoreCase("")) {
                    /* tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + generalFunc.convertNumberWithRTL(tollamount + ""));*/
                    tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + new GeneralFunctions(getActContext()).formatNumAsPerCurrency(generalFunc, generalFunc.convertNumberWithRTL(tollamount + ""), tollcurrancy, true));
                } else {
                  /*  tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl(
                            "Current Fare", "LBL_CURRENT_FARE") + ": " + payAmount + "\n" + "+" + "\n" +
                            generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + generalFunc.convertNumberWithRTL(tollamount + ""));*/
                    tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl(
                            "Current Fare", "LBL_CURRENT_FARE") + ": " + payAmount + "\n" + "+" + "\n" +
                            generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + new GeneralFunctions(getActContext()).formatNumAsPerCurrency(generalFunc, generalFunc.convertNumberWithRTL(tollamount + ""), tollcurrancy, true));
                }


                checkboxTolltax.setText(generalFunc.retrieveLangLBl("", "LBL_IGNORE_TOLL_ROUTE"));
                cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

                cancelTxt.setOnClickListener(v -> tolltax_dialog.dismiss());

                tolltax_dialog = builder.create();
                if (generalFunc.isRTLmode()) {
                    generalFunc.forceRTLIfSupported(tolltax_dialog);
                }
                tolltax_dialog.setCancelable(false);
                tolltax_dialog.show();
            } else {
                startTrip();
            }
        } else {

            startTrip();

        }
    }

    private void openFixChargeDialog(JSONObject responseString, boolean isSurCharge) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);

        MTextView payableAmountTxt;
        MTextView payableTxt;

        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("", "LBL_FIX_FARE_HEADER")));


        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));

        payableTxt = (MTextView) dialogView.findViewById(R.id.payableTxt);
        payableAmountTxt = (MTextView) dialogView.findViewById(R.id.payableAmountTxt);
        if (!generalFunc.getJsonValueStr("fFlatTripPricewithsymbol", responseString).equalsIgnoreCase("")) {
            payableAmountTxt.setVisibility(View.VISIBLE);
            payableTxt.setVisibility(View.GONE);

            if (isSurCharge) {
                payableAmount = generalFunc.getJsonValueStr("fFlatTripPricewithsymbol", responseString) + " " + "(" + generalFunc.retrieveLangLBl("", "LBL_AT_TXT") + " " +
                        generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("SurgePrice", responseString)) + ")";
            } else {
                payableAmount = generalFunc.getJsonValueStr("fFlatTripPricewithsymbol", responseString);

            }
            ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(payableAmount));
        } else {
            payableAmountTxt.setVisibility(View.GONE);
            payableTxt.setVisibility(View.VISIBLE);

        }

        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TXT"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {

            alertDialog_surgeConfirm.dismiss();
            startTrip();
        });

        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(view -> alertDialog_surgeConfirm.dismiss());

        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.40);

        alertDialog_surgeConfirm.getWindow().setLayout(width, height);

    }

    @SuppressLint("SetTextI18n")
    public void openSurgeConfirmDialog(JSONObject responseString) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);

        MTextView payableAmountTxt;
        MTextView payableTxt;
        MTextView payableAmountValue;


        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseString)));
        ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("SurgePrice", responseString)));

        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));
        payableTxt = (MTextView) dialogView.findViewById(R.id.payableTxt);
        payableAmountTxt = (MTextView) dialogView.findViewById(R.id.payableAmountTxt);
        payableAmountValue = (MTextView) dialogView.findViewById(R.id.payableAmountValue);

        payableTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAYABLE_AMOUNT"));

        int pos;
        if (isSelcted == -1) {
            pos = 0;
        } else {
            pos = isSelcted;
        }

        if (cabTypeList != null && !SelectedCarTypeID.equals("") && !cabTypeList.get(pos).get("SubTotal").equals("") && !cabTypeList.get(pos).get("eRental").equalsIgnoreCase("Yes")) {
            payableAmountTxt.setVisibility(View.VISIBLE);
            payableAmountValue.setVisibility(View.VISIBLE);
            payableTxt.setVisibility(View.GONE);
            payableAmount = generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("SubTotal"));

            payableAmountTxt.setText(generalFunc.retrieveLangLBl("Approx payable amount", "LBL_APPROX_PAY_AMOUNT") + ": ");
            payableAmountValue.setText(payableAmount);
        } else {
            payableAmountTxt.setVisibility(View.GONE);
            payableTxt.setVisibility(View.VISIBLE);
            payableAmountValue.setVisibility(View.GONE);

        }


        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_SURGE"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {
            alertDialog_surgeConfirm.dismiss();
            getTollCostValue();
        });

        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(view -> alertDialog_surgeConfirm.dismiss());

        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getActContext(), R.drawable.all_roundcurve_card));
        alertDialog_surgeConfirm.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.40);

        alertDialog_surgeConfirm.getWindow().setLayout(width, height);

    }

    private void showLoader() {
        //loaderView.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
        ride_now_btn.setEnabled(false);
        ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
    }

    private void closeLoader() {
        //.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        ride_now_btn.setEnabled(true);
        ride_now_btn.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void setUserProfileJson() {
        userProfileJsonObj = mainAct.obj_userProfile;
    }

    private void checkCardConfig() {
        setUserProfileJson();

        String vStripeCusId = generalFunc.getJsonValueStr("vStripeCusId", userProfileJsonObj);

        if (vStripeCusId.equals("")) {
            // Open CardPaymentActivity
            mainAct.OpenCardPaymentAct(true);
        } else {
            showPaymentBox();
        }
    }

    private void showPaymentBox() {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        final MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);

        Utils.removeInput(input);

        subTitleTxt.setVisibility(View.VISIBLE);
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TITLE_PAYMENT_ALERT"));
        input.setText(generalFunc.getJsonValueStr("vCreditCard", userProfileJsonObj));

        builder.setPositiveButton(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), (dialog, which) -> dialog.cancel());
        builder.setNeutralButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), (dialog, which) -> {
            dialog.cancel();
            mainAct.OpenCardPaymentAct(true);
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), (dialog, which) -> dialog.cancel());

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void setCashSelection() {
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));

        isCardValidated = false;
        //cashRadioBtn.setChecked(true);

        payImgView.setImageResource(R.mipmap.ic_cash_new);
    }

    private void setLabels() {
        ride_now_btn.setText(generalFunc.retrieveLangLBl("Start Trip", "LBL_START_TRIP"));
        noServiceTxt.setText(generalFunc.retrieveLangLBl("service not available in this location", "LBL_NO_SERVICE_AVAILABLE_TXT"));
    }

    private void configRideLaterBtnArea(boolean isGone) {
        //  mainAct.setPanelHeight(305);
    }

    public Context getActContext() {
        return mainAct.getActContext();
    }

    @Override
    public void onItemClick(int position) {
        selpos = position;
        SelectedCarTypeID = cabTypeList.get(position).get("iVehicleTypeId");
        RESTRICT_PASSENGER_LIMIT_NOTE = cabTypeList.get(position).get("RESTRICT_PASSENGER_LIMIT_NOTE");
        ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
        tempList.addAll(cabTypeList);
        adapter.setSelectedVehicleTypeId(SelectedCarTypeID);
        cabTypeList.clear();

        for (int i = 0; i < tempList.size(); i++) {
            HashMap<String, String> map = tempList.get(i);

            if (i != position) {
                map.put("isHover", "false");
            } else if (i == position) {
                if (dialogShowOnce && tempList.get(i).get("isHover").equals("true")) {
                    dialogShowOnce = true;
                } else if (!dialogShowOnce && tempList.get(i).get("isHover").equals("true")) {
                    dialogShowOnce = true;
                } else {
                    dialogShowOnce = false;
                }

                map.put("isHover", "true");
                isSelcted = position;
                if (tempList.get(i).get("eFlatTrip") != null &&
                        !tempList.get(i).get("eFlatTrip").equalsIgnoreCase("") &&
                        tempList.get(i).get("eFlatTrip").equalsIgnoreCase("Yes")) {
                    isFixFare = true;
                } else {
                    isFixFare = false;
                }
            }
            cabTypeList.add(map);
        }

        if (isSelcted == position) {
            if (dialogShowOnce) {
                dialogShowOnce = false;
                openFareDetailsDilaog(position);
            }
        }

        if (position > (cabTypeList.size() - 1)) {
            return;
        }

        adapter.notifyDataSetChanged();
        if (mainAct.isVerticalCabscroll) {
            mainAct.sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            carTypeRecyclerView.scrollToPosition(selpos > 1 ? (selpos - 2) : selpos);

        }
    }

    private void hidePayTypeSelectionArea() {
        //payTypeSelectArea.setVisibility(View.GONE);
        cashcardarea.setVisibility(View.VISIBLE);
        mainAct.setPanelHeight(232);
    }

    private void checkPromoCode(final String promoCode) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "CheckPromoCode");
        parameters.put("PromoCode", promoCode);
        parameters.put("iUserId", generalFunc.getMemberId());

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseStringObj = generalFunc.getJsonObject(responseString);

                    if (responseStringObj != null && !responseStringObj.equals("")) {

                        String action = generalFunc.getJsonValueStr(Utils.action_str, responseStringObj);
                        if (action.equals("1")) {
                            appliedPromoCode = promoCode;
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_APPLIED"));
                        } else if (action.equals("01")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_USED"));
                        } else {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });

    }

    private void showPromoBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_ENTER_TITLE"));

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);


        if (!appliedPromoCode.equals("")) {
            input.setText(appliedPromoCode);
        }
        builder.setPositiveButton(generalFunc.retrieveLangLBl("OK", "LBL_BTN_OK_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().trim().equals("") && appliedPromoCode.equals("")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ENTER_PROMO"));
                } else if (input.getText().toString().trim().equals("") && !appliedPromoCode.equals("")) {
                    appliedPromoCode = "";
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_REMOVED"));
                } else if (input.getText().toString().contains(" ")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
                } else {
                    checkPromoCode(input.getText().toString().trim());
                }
            }
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("", "LBL_SKIP_TXT"), (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(alertDialog);
        }
        alertDialog.show();
        alertDialog.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(mainAct));

    }

    private boolean calculateDistance(Location start, Location end) {
        float distance = start.distanceTo(end);
        return distance > 200;
    }

    public void findRoute() {
        try {
            if (isRental) {
                mainAct.hideprogress();
                return;
            }
            HashMap<String, String> hashMap = new HashMap<>();

            showLoader();
            if (tempDestLocation != null && tempPickUpLocation != null) {

                boolean isPickup = calculateDistance(tempPickUpLocation, mainAct.userLocation);
                boolean isDest = calculateDistance(tempDestLocation, mainAct.destLocation);

                if (isPickup || isDest) {
                    if (isPickup) {
                        tempPickUpLocation.setLatitude(mainAct.userLocation.getLatitude());
                        tempPickUpLocation.setLongitude(mainAct.userLocation.getLongitude());

                    }
                    if (isDest) {
                        tempDestLocation.setLatitude(mainAct.destLocation.getLatitude());
                        tempDestLocation.setLongitude(mainAct.destLocation.getLongitude());

                    }
                } else {
                    closeLoader();
                    mainAct.hideprogress();
                }

            } else {
                if (tempPickUpLocation == null) {
                    tempPickUpLocation = new Location("gps");
                    tempPickUpLocation.setLatitude(mainAct.userLocation.getLatitude());
                    tempPickUpLocation.setLongitude(mainAct.userLocation.getLongitude());
                }
                if (tempDestLocation == null) {
                    tempDestLocation = new Location("gps");
                    tempDestLocation.setLatitude(mainAct.destLocation.getLatitude());
                    tempDestLocation.setLongitude(mainAct.destLocation.getLongitude());
                }
            }

            String originLoc = mainAct.userLocation.getLatitude() + "," + mainAct.userLocation.getLongitude();
            String destLoc = mainAct.destlat + "," + mainAct.destlong;

            String parameters = "origin=" + originLoc + "&destination=" + destLoc;
            hashMap.put("parameters", parameters);

            hashMap.put("s_latitude", mainAct.userLocation.getLatitude() + "");
            hashMap.put("s_longitude", mainAct.userLocation.getLongitude() + "");
            hashMap.put("d_latitude", mainAct.destlat + "");
            hashMap.put("d_longitude", mainAct.destlong + "");

            isRouteFail = true;

            AppService.getInstance().executeService(mainAct.getActContext(), new DataProvider.DataProviderBuilder(mainAct.userLocation.getLatitude() + "", mainAct.userLocation.getLongitude() + "").setDestLatitude(mainAct.destlat + "").setDestLongitude(mainAct.destlong + "").setWayPoints(new JSONArray()).build(), AppService.Service.DIRECTION, new AppService.ServiceDelegate() {
                @Override
                public void onResult(HashMap<String, Object> data) {

                    mainAct.hideprogress();
                    if (data.get("RESPONSE_TYPE") != null && data.get("RESPONSE_TYPE").toString().equalsIgnoreCase("FAIL")) {
                        isRouteFail = true;
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DEST_ROUTE_NOT_FOUND"));
                        return;
                    }
                    String responseString = "";
                    if (data.get("ROUTES") != null) {
                        responseString = data.get("ROUTES").toString();
                    }
                    if (responseString.equalsIgnoreCase("")) {
                        isRouteFail = true;
                        GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                        alertBox.setContentMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                        alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                        alertBox.setBtnClickList(btn_id -> {
                            alertBox.closeAlertBox();
                        });
                        alertBox.showAlertBox();
                        getCabdetails(null, null);
                        return;
                    }
                    if (responseString.equalsIgnoreCase("null")) {
                        responseString = null;
                    }

                    if (responseString != null && !responseString.equalsIgnoreCase("") && data.get("DISTANCE") == null) {
                        isGoogle = true;
                        JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                        isRouteFail = false;
                        ride_now_btn.setEnabled(true);
                        ride_now_btn.setTextColor(getActContext().getResources().getColor(R.color.btn_text_color_type2));


                        if (obj_routes != null && obj_routes.length() > 0) {
                            JSONObject obj_legs = generalFunc.getJsonObject(generalFunc.getJsonArray("legs", generalFunc.getJsonObject(obj_routes, 0).toString()), 0);


                            distance = "" + (GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                    generalFunc.getJsonValue("distance", obj_legs.toString()).toString())));

                            time = "" + (GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                    generalFunc.getJsonValue("duration", obj_legs.toString()).toString())));

                            LatLng sourceLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("start_location", obj_legs.toString()))),
                                    GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("start_location", obj_legs.toString()))));

                            LatLng destLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("end_location", obj_legs.toString()))),
                                    GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("end_location", obj_legs.toString()))));

                            isDestinationAdded = "Yes";

                        }


                    } else if (responseString == null) {

                        closeLoader();
                        isRouteFail = true;

                        distance = "";
                        time = "";
                        mainAct.destlat = "";
                        mainAct.destlong = "";
                        isDestinationAdded = "No";

                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));


                        getCabdetails(null, null);
                    } else {
                        isGoogle = false;
                        isRouteFail = false;
                        distance = data.get("DISTANCE").toString();
                        time = data.get("DURATION").toString();
                        isDestinationAdded = "Yes";
                    }


                    getCabdetails(distance, time);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFareDetailsDilaog(final int pos) {

        if (cabTypeList.get(isSelcted).get("SubTotal") != null) {
            String vehicleIconPath = CommonUtilities.SERVER_URL + "webimages/icons/VehicleType/";
            String vehicleDefaultIconPath = CommonUtilities.SERVER_URL + "webimages/icons/DefaultImg/";
            // final Dialog faredialog = new Dialog(getActContext());
            final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());

            View contentView = View.inflate(getContext(), R.layout.dailog_faredetails, null);
            if (generalFunc.isRTLmode()) {
                contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }


            faredialog.setContentView(contentView);
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
            mBehavior.setPeekHeight(1500);
            View bottomSheetView = faredialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
            setCancelable(faredialog, false);

            ImageView imagecar = (ImageView) faredialog.findViewById(R.id.imagecar);
            MTextView carTypeTitle = (MTextView) faredialog.findViewById(R.id.carTypeTitle);
            MTextView capacityHTxt = (MTextView) faredialog.findViewById(R.id.capacityHTxt);
            MTextView capacityVTxt = (MTextView) faredialog.findViewById(R.id.capacityVTxt);
            MTextView fareHTxt = (MTextView) faredialog.findViewById(R.id.fareHTxt);
            MTextView fareVTxt = (MTextView) faredialog.findViewById(R.id.fareVTxt);
            MTextView pkgMsgTxt = (MTextView) faredialog.findViewById(R.id.pkgMsgTxt);
            MTextView mordetailsTxt = (MTextView) faredialog.findViewById(R.id.mordetailsTxt);
            ImageView morwArrow;
            MTextView farenoteTxt = (MTextView) faredialog.findViewById(R.id.farenoteTxt);
            MButton btn_type2 = ((MaterialRippleLayout) faredialog.findViewById(R.id.btn_type2)).getChildView();

            btn_type2.setId(Utils.generateViewId());


            capacityHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAPACITY"));
            fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FARE_TXT"));

            mordetailsTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MORE_DETAILS"));
            morwArrow = (ImageView) faredialog.findViewById(R.id.morwArrow);
            if (isFixFare) {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FLAT_FARE_EST"));
            } else {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FARE_EST"));
            }
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));


            if (cabTypeList.get(selpos).get("eRental") != null && cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {
                mordetailsTxt.setVisibility(View.GONE);
                morwArrow.setVisibility(View.GONE);

                pkgMsgTxt.setVisibility(View.VISIBLE);
                fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PKG_STARTING_AT"));
                pkgMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_MSG"));
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_DETAILS"));
                carTypeTitle.setText(cabTypeList.get(isSelcted).get("vRentalVehicleTypeName"));

            } else {
                carTypeTitle.setText(cabTypeList.get(isSelcted).get("vVehicleTypeName"));
            }
            if (cabTypeList.get(isSelcted).get("SubTotal").equals("")) {
                fareVTxt.setText("--");
            } else {
                fareVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(isSelcted).get("SubTotal")));
            }
            capacityVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(isSelcted).get("iPersonSize")) + " " + generalFunc.retrieveLangLBl("", "LBL_PEOPLE_TXT"));

            String imgName = cabTypeList.get(pos).get("vLogo1");
            if (imgName.equals("")) {
                imgName = vehicleDefaultIconPath + "hover_ic_car.png";
            } else {
                imgName = vehicleIconPath + cabTypeList.get(pos).get("iVehicleTypeId") + "/android/" + "xxxhdpi_" +
                        cabTypeList.get(pos).get("vLogo1");

            }

            new LoadImage.builder(LoadImage.bind(imgName), imagecar).build();


            btn_type2.setOnClickListener(v -> {
                dialogShowOnce = true;
                faredialog.dismiss();
            });

            mordetailsTxt.setOnClickListener(v -> {
                dialogShowOnce = true;
                Bundle bn = new Bundle();
                bn.putString("SelectedCar", cabTypeList.get(isSelcted).get("iVehicleTypeId"));
                bn.putString("iUserId", generalFunc.getMemberId());
                bn.putString("distance", distance);
                bn.putString("time", time);
                //  bn.putString("PromoCode", appliedPromoCode);
                bn.putString("vVehicleType", cabTypeList.get(isSelcted).get("vVehicleTypeName"));

                if (mainAct.userLocation != null) {
                    bn.putString("picupLat", mainAct.userLocation.getLatitude() + "");
                    bn.putString("pickUpLong", mainAct.userLocation.getLongitude() + "");
                }
                if (mainAct.destlat != null & !mainAct.destlat.equalsIgnoreCase("")) {
                    bn.putString("destLat", mainAct.destlat + "");
                    bn.putString("destLong", mainAct.destlong + "");
                }

                bn.putBoolean("isFixFare", isFixFare);
                bn.putString("isDestinationAdded", isDestinationAdded);


                new ActUtils(getActContext()).startActWithData(FareBreakDownActivity.class, bn);
                faredialog.dismiss();
            });

            faredialog.setOnDismissListener(dialog -> {
                dialogShowOnce = true;
            });

            faredialog.show();
        }
    }

    private void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(v -> {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }

    private String getAvailableCarTypesIds() {
        String carTypesIds = "";
        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            String iVehicleTypeId = generalFunc.getJsonValue("iVehicleTypeId", mainAct.cabTypesArrList.get(i));

            carTypesIds = carTypesIds.equals("") ? iVehicleTypeId : (carTypesIds + "," + iVehicleTypeId);
        }
        return carTypesIds;
    }

    private void getCabdetails(final String distance, final String time) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getDriverVehicleDetails");
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("UserType", "Driver");

        if (mainAct.userLocation != null) {
            parameters.put("StartLatitude", mainAct.userLocation.getLatitude() + "");
            parameters.put("EndLongitude", mainAct.userLocation.getLongitude() + "");
        }
        if (!mainAct.destlat.equalsIgnoreCase("")) {
            parameters.put("DestLatitude", mainAct.destlat + "");
            parameters.put("DestLongitude", mainAct.destlong + "");

        }

        parameters.put("VehicleTypeIds", getAvailableCarTypesIds());
        if (distance != null) {
            parameters.put("distance", distance);
        }
        if (time != null) {
            parameters.put("time", time);
        }

        if (mainAct.userLocation != null) {
            parameters.put("StartLatitude", mainAct.userLocation.getLatitude() + "");
            parameters.put("EndLongitude", mainAct.userLocation.getLongitude() + "");
        }

        ApiHandler.execute(getActContext(), parameters,
                new ServerTask.SetDataResponse() {
                    @Override
                    public void setResponse(String responseString) {
                        JSONObject responseStringObj = generalFunc.getJsonObject(responseString);

                        if (responseStringObj != null && !responseStringObj.equals("")) {

                            closeLoader();

                            if (generalFunc.getJsonValueStr(Utils.message_str, responseStringObj).equals("SESSION_OUT")) {
                                MyApp.getInstance().notifySessionTimeOut();
                                Utils.runGC();
                                return;
                            }

                            cabTypeList.clear();
                            rentalTypeList.clear();
                            JSONArray messageArray = generalFunc.getJsonArray(Utils.message_str, responseStringObj);


                            for (int i = 0; i < messageArray.length(); i++) {
                                HashMap<String, String> vehicleTypeMap = new HashMap<>();
                                JSONObject tempObj = generalFunc.getJsonObject(messageArray, i);


                                vehicleTypeMap.put("iVehicleTypeId", generalFunc.getJsonValue("iVehicleTypeId", tempObj.toString()));
                                vehicleTypeMap.put("vVehicleTypeName", generalFunc.getJsonValue("vVehicleTypeName", tempObj.toString()));
                                vehicleTypeMap.put("vRentalVehicleTypeName", generalFunc.getJsonValue("vRentalVehicleTypeName", tempObj.toString()));
                                vehicleTypeMap.put("vLogo", generalFunc.getJsonValue("vLogo", tempObj.toString()));
                                vehicleTypeMap.put("vLogo1", generalFunc.getJsonValue("vLogo1", tempObj.toString()));
                                vehicleTypeMap.put("RESTRICT_PASSENGER_LIMIT_NOTE", generalFunc.getJsonValue("RESTRICT_PASSENGER_LIMIT_NOTE", tempObj.toString()));
                                vehicleTypeMap.put("tInfoText", generalFunc.getJsonValue("tInfoText", tempObj.toString()));
                                if (distance != null && time != null) {
                                    vehicleTypeMap.put("SubTotal", generalFunc.getJsonValue("SubTotal", tempObj.toString()));
                                } else {
                                    vehicleTypeMap.put("SubTotal", generalFunc.getJsonValue("SubTotal", 0 + ""));
                                }
                                vehicleTypeMap.put("iPersonSize", generalFunc.getJsonValue("iPersonSize", tempObj.toString()));
                                vehicleTypeMap.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", tempObj.toString()));
                                vehicleTypeMap.put("eRental", generalFunc.getJsonValue("eRental", tempObj.toString()));
                                String eRental = generalFunc.getJsonValue("eRental", tempObj.toString());

                                if (cabTypeList.size() == 0) {
                                    vehicleTypeMap.put("isHover", "true");
                                } else {
                                    vehicleTypeMap.put("isHover", "false");
                                }
                                vehicleTypeMap.put("eRental", "No");

                                // if (eRental != null && !eRental.equalsIgnoreCase("") && eRental.equalsIgnoreCase("No")) {
                                cabTypeList.add(vehicleTypeMap);
                                //  }
                                if (eRental != null && eRental.equalsIgnoreCase("Yes")) {
                                    HashMap<String, String> rentalVehicleTypeMap = (HashMap<String, String>) vehicleTypeMap.clone();
                                    rentalVehicleTypeMap.put("SubTotal", generalFunc.getJsonValue("RentalSubtotal", tempObj.toString()));
                                    rentalVehicleTypeMap.put("vRentalVehicleTypeName", generalFunc.getJsonValue("vRentalVehicleTypeName", tempObj.toString()));
                                    rentalVehicleTypeMap.put("eRental", "Yes");
                                    rentalTypeList.add(rentalVehicleTypeMap);
                                }
                                if (!isFirstTime) {
                                    isFirstTime = true;
                                    if (cabTypeList.size() > 0) {
                                        SelectedCarTypeID = cabTypeList.get(0).get("iVehicleTypeId");
                                        RESTRICT_PASSENGER_LIMIT_NOTE = cabTypeList.get(0).get("RESTRICT_PASSENGER_LIMIT_NOTE");
                                    }
                                    if (i == 0) {
                                        if (adapter != null) {
                                            adapter.setSelectedVehicleTypeId(SelectedCarTypeID);
                                        }
                                    }
                                }
                            }

                            if (rentalTypeList.size() > 0) {
                                rentalPkg.setVisibility(View.VISIBLE);

                                rentalarea.setVisibility(View.VISIBLE);
                                //rentBackPkgImage.setVisibility(View.VISIBLE);

                                Runnable r = new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            //int height = (int) getResources().getDimension(R.dimen._95sdp);

                                            if (mainAct.isVerticalCabscroll) {
                                                // mainAct.setPanelHeight(getActContext().getResources().getDimensionPixelSize(R.dimen._75sdp));
                                                mainAct.setPanelHeight(345);
                                            } else {
                                                mainAct.setPanelHeight(345);
                                            }
                                        } catch (Exception e2) {
                                            new Handler().postDelayed(this, 20);
                                        }
                                    }
                                };
                                new Handler().postDelayed(r, 20);


                            } else {
                                Runnable r = new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            if (mainAct.isVerticalCabscroll) {
                                                // mainAct.setPanelHeight(getActContext().getResources().getDimensionPixelSize(R.dimen._75sdp));
                                                mainAct.setPanelHeight(300);
                                            } else {
                                                mainAct.setPanelHeight(255);
                                            }
                                        } catch (Exception e2) {
                                            new Handler().postDelayed(this, 20);
                                        }
                                    }
                                };
                                new Handler().postDelayed(r, 20);

                            }
                            setAdapterData();

                        }
                    }
                });

    }

    private void setAdapterData() {
        try {
            if (cabTypeList.size() == 0) {
                ride_now_btn.setEnabled(false);
                ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
            } else {
//                ride_now_btn.setEnabled(true);
//                ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
            }

            if (adapter == null) {
                selpos = 0;
                adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
                adapter.setSelectedVehicleTypeId(SelectedCarTypeID);
                adapter.setOnItemClickList(this);
                carTypeRecyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();

            }


        } catch (Exception e) {

        }
    }

    private void startTrip() {
        if (generalFunc.retrieveValue("ENABLE_SAFETY_FEATURE_RIDE").equalsIgnoreCase("Yes")) {
            if ((generalFunc.retrieveValue("ENABLE_FACE_MASK_VERIFICATION").equalsIgnoreCase("Yes") && !isFaceMaskVerification) || generalFunc.retrieveValue("ENABLE_RESTRICT_PASSENGER_LIMIT").equalsIgnoreCase("Yes")) {
                showSafetyDialog();
            } else {
                callStartTrip();
            }
        } else {
            callStartTrip();
        }
    }

    private void showSafetyDialog() {

        if (uploadImgAlertDialog != null) {
            uploadImgAlertDialog.cancel();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActContext());
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @NonNull DesginMaskVerificationBinding binding = DesginMaskVerificationBinding.inflate(inflater, null, false);

        mCardView = binding.mCardView;

        maskVerificationUploadImgArea = binding.uploadImgArea;
        clearImg = binding.clearImg;
        clearImg.setVisibility(View.GONE);

        if (generalFunc.retrieveValue("ENABLE_SAFETY_FEATURE_RIDE").equalsIgnoreCase("Yes")) {

            if (generalFunc.retrieveValue("ENABLE_RESTRICT_PASSENGER_LIMIT").equalsIgnoreCase("Yes")) {
                binding.capacityTxt.setText(RESTRICT_PASSENGER_LIMIT_NOTE);
                binding.capacityTxt1.setText(RESTRICT_PASSENGER_LIMIT_NOTE);
            }
        }

        boolean isOnlyPassengerLimitOn = generalFunc.retrieveValue("ENABLE_RESTRICT_PASSENGER_LIMIT").equalsIgnoreCase("Yes") && !generalFunc.retrieveValue("ENABLE_FACE_MASK_VERIFICATION").equalsIgnoreCase("Yes");


        binding.capacityTxt1.setVisibility(isOnlyPassengerLimitOn ? View.VISIBLE : View.GONE);
        binding.capacityTxt.setVisibility(isOnlyPassengerLimitOn ? View.GONE : View.VISIBLE);

        MButton btn_type2 = ((MaterialRippleLayout) binding.btnType2).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", isOnlyPassengerLimitOn ? "LBL_BTN_OK_TXT" : "LBL_BTN_SUBMIT_TXT"));
        btn_type2.setId(Utils.generateViewId());
        binding.uploadArea.setVisibility(View.VISIBLE);
        binding.titileTxt.setText(generalFunc.retrieveLangLBl("Safety Essential", "LBL_SAFETY_ESSENTIAL_VERIFICATION_TXT"));
        binding.imageUploadNoteTxt.setText(generalFunc.retrieveLangLBl("Kindly upload mask selfie to prove that your following safety rules,After than you can start the trip", "LBL_MASK_VERIFICATION_UPLOAD_PHOTO_TXT"));

        binding.uploadTitleTxt.setText(generalFunc.retrieveLangLBl("Upload Photo", "LBL_UPLOAD_PHOTO"));

        if (generalFunc.retrieveValue("ENABLE_SAFETY_FEATURE_RIDE").equalsIgnoreCase("Yes") ||
                (generalFunc.retrieveValue("ENABLE_SAFETY_FEATURE_DELIVERY").equalsIgnoreCase("Yes") ||
                        (generalFunc.retrieveValue("ENABLE_SAFETY_FEATURE_UFX").equalsIgnoreCase("Yes")))) {
            if (generalFunc.retrieveValue("ENABLE_FACE_MASK_VERIFICATION").equalsIgnoreCase("Yes")) {
                mCardView.setVisibility(View.VISIBLE);
                binding.uploadArea.setVisibility(View.VISIBLE);

                maskVerificationUploadImgArea.setOnClickListener(view -> mainAct.getFileSelector().openFileSelection(FileSelector.FileType.Image));
            } else {
                mCardView.setVisibility(View.GONE);
                binding.uploadArea.setVisibility(View.GONE);
            }

        } else {
            mCardView.setVisibility(View.GONE);
            binding.uploadArea.setVisibility(View.GONE);
        }


        binding.cancelImg.setOnClickListener(v -> {
            uploadImgAlertDialog.dismiss();
            isFaceMaskVerification = false;

        });


        btn_type2.setOnClickListener(view -> {
            if (mCardView.getVisibility() == View.VISIBLE) {
                boolean isImageSelect = Utils.checkText(selectedImagePath);

                if (!isImageSelect) {
                    mCardView.setBackgroundResource(R.drawable.error_border);

                }

                if ((!isImageSelect && !isFaceMaskVerification)) {
                    return;
                }
            }

            callStartTrip();
        });
        builder.setView(binding.getRoot());
        uploadImgAlertDialog = builder.create();
        uploadImgAlertDialog.setCancelable(false);
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(uploadImgAlertDialog);
        }
        uploadImgAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        uploadImgAlertDialog.show();
    }

    public void removeImage(View v) {
        isFaceMaskVerification = false;
        selectedImagePath = "";
        ((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)).setImageDrawable(null);
        // ((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew)).setVisibility(View.GONE);
        uploadImgAlertDialog.findViewById(R.id.camImgVIew).setVisibility(View.VISIBLE);
        uploadImgAlertDialog.findViewById(R.id.ic_add).setVisibility(View.VISIBLE);
//        ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)) .setBackgroundColor(Color.parseColor("#F1F5F8"));
//        ((CardView) uploadImgAlertDialog.findViewById(R.id.mCardView)) .setRadius(R.dimen._12sdp);


        maskVerificationUploadImgArea.setClickable(true);
        clearImg.setVisibility(View.GONE);
    }

    private void callStartTrip() {
        if (isFaceMaskVerification) {

            ArrayList<String[]> paramsList = new ArrayList<>();
            paramsList.add(generalFunc.generateImageParams("type", "StartHailTrip"));
            paramsList.add(generalFunc.generateImageParams("iDriverId", generalFunc.getMemberId()));
            paramsList.add(generalFunc.generateImageParams("UserType", "Driver"));
            paramsList.add(generalFunc.generateImageParams("SelectedCarTypeID", SelectedCarTypeID));


            paramsList.add(generalFunc.generateImageParams("DestLatitude", "" + mainAct.destlat));
            paramsList.add(generalFunc.generateImageParams("DestLongitude", "" + mainAct.destlong));
            paramsList.add(generalFunc.generateImageParams("DestAddress", "" + mainAct.Destinationaddress));


            paramsList.add(generalFunc.generateImageParams("PickUpLatitude", "" + mainAct.userLocation.getLatitude()));
            paramsList.add(generalFunc.generateImageParams("PickUpLongitude", "" + mainAct.userLocation.getLongitude()));
            paramsList.add(generalFunc.generateImageParams("PickUpAddress", "" + mainAct.pickupaddress));
            if (!iRentalPackageId.equalsIgnoreCase("")) {
                paramsList.add(generalFunc.generateImageParams("iRentalPackageId", iRentalPackageId));
            }
            if (istollIgnore) {
                tollamount = 0;
                tollskiptxt = "Yes";
            } else {
                tollskiptxt = "No";
            }
            paramsList.add(generalFunc.generateImageParams("fTollPrice", tollamount + ""));
            paramsList.add(generalFunc.generateImageParams("eTollSkipped", tollskiptxt));
            paramsList.add(generalFunc.generateImageParams("vTollPriceCurrencyCode", tollcurrancy));
            if (distance != null && time != null) {
                paramsList.add(generalFunc.generateImageParams("vDistance", distance));
                paramsList.add(generalFunc.generateImageParams("vDuration", time));

            }
            paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
            paramsList.add(Utils.generateImageParams("iMemberId", generalFunc.getMemberId()));
            paramsList.add(Utils.generateImageParams("MemberType", Utils.app_type));
            paramsList.add(Utils.generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
            paramsList.add(Utils.generateImageParams("GeneralUserType", Utils.app_type));
            paramsList.add(Utils.generateImageParams("GeneralMemberId", generalFunc.getMemberId()));

            new UploadProfileImage(getActContext(), selectedImagePath, Utils.TempProfileImageName, paramsList, "uploadImageWithMask").execute();

        } else {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("type", "StartHailTrip");
            parameters.put("iDriverId", generalFunc.getMemberId());
            parameters.put("UserType", "Driver");
            parameters.put("SelectedCarTypeID", SelectedCarTypeID);
            parameters.put("DestLatitude", mainAct.destlat);
            parameters.put("DestLongitude", mainAct.destlong);
            parameters.put("DestAddress", mainAct.Destinationaddress);

            parameters.put("PickUpLatitude", "" + mainAct.userLocation.getLatitude());
            parameters.put("PickUpLongitude", "" + mainAct.userLocation.getLongitude());
            parameters.put("PickUpAddress", "" + mainAct.pickupaddress);
            if (!iRentalPackageId.equalsIgnoreCase("")) {
                parameters.put("iRentalPackageId", "" + iRentalPackageId);
            }

            if (istollIgnore) {
                tollamount = 0;
                tollskiptxt = "Yes";
            } else {
                tollskiptxt = "No";
            }
            parameters.put("fTollPrice", tollamount + "");
            parameters.put("eTollSkipped", tollskiptxt);
            parameters.put("vTollPriceCurrencyCode", tollcurrancy);
            if (distance != null && time != null) {
                parameters.put("vDistance", distance);
                parameters.put("vDuration", time);
            }

            ServerTask exeWebServer = ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, this::hailResponse);
            exeWebServer.setCancelAble(false);
        }
    }

    public void handleImgUploadResponse(String responseString) {
        if (responseString != null && !responseString.equals("")) {
            hailResponse(responseString);
        } else {
            generalFunc.showError();
        }
    }

    private void hailResponse(String responseString) {
        ride_now_btn.setEnabled(true);

        JSONObject responseStringObj = generalFunc.getJsonObject(responseString);
        if (responseStringObj != null && !responseStringObj.equals("")) {
            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObj);
            String message = generalFunc.getJsonValueStr(Utils.message_str, responseStringObj);
            if (isDataAvail) {
                if (uploadImgAlertDialog != null) {
                    uploadImgAlertDialog.cancel();
                    uploadImgAlertDialog = null;
                }
                MyApp.getInstance().restartWithGetDataApp();
            } else {
                if (message.equalsIgnoreCase("DO_RESTART") ||
                        message.equalsIgnoreCase("LBL_SERVER_COMM_ERROR") ||
                        message.equalsIgnoreCase("GCM_FAILED") ||
                        message.equalsIgnoreCase("APNS_FAILED")) {
                    MyApp.getInstance().restartWithGetDataApp();
                    return;
                }
                final GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                generateAlertBox.setCancelable(false);
                generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", message));
                generateAlertBox.setBtnClickList(btn_id -> {
                    generateAlertBox.closeAlertBox();
                    if (btn_id == 1) {
                        startTrip();
                    } else if (btn_id == 0) {
                        generateAlertBox.closeAlertBox();

                    }
                });
                //generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"));
                generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlertBox.showAlertBox();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }

//    @Override
//    public void directionResult(HashMap<String, String> directionlist) {
//
//        mainAct.hideprogress();
//        String responseString = directionlist.get("routes");
//        if (responseString.equalsIgnoreCase("null")) {
//            responseString = null;
//        }
//
//        if (responseString != null && !responseString.equalsIgnoreCase("") && directionlist.get("distance") == null) {
//            isGoogle = true;
//            isRouteFail = false;
////            JSONArray obj_routes = generalFunc.getJsonArray(responseString);
//            JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
//            isRouteFail = false;
//            ride_now_btn.setEnabled(true);
//            ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
//
//
//            if (obj_routes != null && obj_routes.length() > 0) {
//                JSONObject obj_legs = generalFunc.getJsonObject(generalFunc.getJsonArray("legs", generalFunc.getJsonObject(obj_routes, 0).toString()), 0);
//
//
//                distance = "" + (GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("value",
//                        generalFunc.getJsonValue("distance", obj_legs.toString()).toString())));
//
//                time = "" + (GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("value",
//                        generalFunc.getJsonValue("duration", obj_legs.toString()).toString())));
//
//                LatLng sourceLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("start_location", obj_legs.toString()))),
//                        GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("start_location", obj_legs.toString()))));
//
//                LatLng destLocation = new LatLng(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("end_location", obj_legs.toString()))),
//                        GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("end_location", obj_legs.toString()))));
//
//                isDestinationAdded = "Yes";
//
//            }
//
//
//        } else if (responseString == null) {
//
//            closeLoader();
//            isRouteFail = true;
//
//            distance = "";
//            time = "";
//            mainAct.destlat = "";
//            mainAct.destlong = "";
//            isDestinationAdded = "No";
//
//            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
//
//
//            getCabdetails(null, null);
//        } else {
//            isGoogle = false;
//            distance = directionlist.get("distance");
//            time = directionlist.get("duration");
//            isDestinationAdded = "Yes";
//        }
//
//
//        getCabdetails(distance, time);
//
//
//    }


    public void onClickView(View view) {
        int i = view.getId();

        if (i == ride_now_btn.getId()) {

            if (SelectedCarTypeID == null || SelectedCarTypeID.equalsIgnoreCase("") || SelectedCarTypeID.equalsIgnoreCase("0")) {
                return;
            }

            if (isRouteFail) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                return;
            }

            if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                    cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

                Bundle bn = new Bundle();
                bn.putString("address", mainAct.pickupaddress);
                bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
                bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
                bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
                // bn.putString("eta", etaTxt.getText().toString());
                new ActUtils(getActContext()).startActForResult(RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
                return;
            }

            final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
            generateAlert.setCancelable(false);
            generateAlert.setBtnClickList(btn_id -> {
                if (btn_id == 0) {
                    generateAlert.closeAlertBox();
                } else {
                    generateAlert.closeAlertBox();
                    checkSurgePrice();
                }
            });
            generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_CONFIRM_START_TRIP_TXT"));
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
            generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
            generateAlert.showAlertBox();
        } else if (i == R.id.paymentArea) {
//                if (payTypeSelectArea.getVisibility() == View.VISIBLE) {
//                    hidePayTypeSelectionArea();
//                } else {
//                    if (generalFunc.getJsonValueStr("APP_PAYMENT_MODE", userProfileJsonObj).equalsIgnoreCase("Cash-Card")) {
//                        mainAct.setPanelHeight(283);
//                       // payTypeSelectArea.setVisibility(View.VISIBLE);
//                        cashcardarea.setVisibility(View.GONE);
//                    } else {
//                        mainAct.setPanelHeight(283 - 48);
//                    }
//                }

        } else if (i == R.id.promoArea) {
            showPromoBox();
        } else if (i == R.id.cardarea) {
            hidePayTypeSelectionArea();
            setCashSelection();
            checkCardConfig();
        } else if (i == R.id.rentalBackImage) {
            selpos = 0;
            iRentalPackageId = "";
            cabTypeList = (ArrayList<HashMap<String, String>>) tempCabTypeList.clone();
            tempCabTypeList.clear();
            tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
            isRental = false;
            adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
            SelectedCarTypeID = cabTypeList.get(0).get("iVehicleTypeId");
            RESTRICT_PASSENGER_LIMIT_NOTE = cabTypeList.get(0).get("RESTRICT_PASSENGER_LIMIT_NOTE");
            adapter.setRentalItem(cabTypeList);
            adapter.notifyDataSetChanged();
            rentalBackImage.setVisibility(View.GONE);
            rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_CHOOSE_TRIP_OR_SWIPE"));
            rentalinfoimage.setVisibility(View.GONE);
            rentalPkg.setVisibility(View.VISIBLE);
            //  rentView.setVisibility(View.VISIBLE);
            //rentBackPkgImage.setVisibility(View.VISIBLE);
            //rentPkgImage.setVisibility(View.VISIBLE);
            rentalarea.setVisibility(View.VISIBLE);
            mainAct.setPanelHeight(375);
            // carTypeRecyclerView.startAnimation(bottomUp);
//            Runnable r = new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        RelativeLayout.LayoutParams lyParams = (RelativeLayout.LayoutParams) requestPayArea.getLayoutParams();
//                        lyParams.setMargins(0, getActContext().getResources().getDimensionPixelSize(R.dimen._130sdp), 0, 0);
//                        requestPayArea.setLayoutParams(lyParams);
//                        if (mainAct.isVerticalCabscroll) {
//                            // mainAct.setPanelHeight(getActContext().getResources().getDimensionPixelSize(R.dimen._75sdp));
//                            mainAct.setPanelHeight(345);
//                        } else {
//                            mainAct.setPanelHeight(345);
//                        }
//                    } catch (Exception e2) {
//                        new Handler().postDelayed(this, 20);
//                    }
//                }
//            };
//            new Handler().postDelayed(r, 20);

        } else if (i == R.id.rentalPkg) {
            selpos = 0;
            iRentalPackageId = "";
            tempCabTypeList.clear();
            tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
            cabTypeList.clear();
            cabTypeList = (ArrayList<HashMap<String, String>>) rentalTypeList.clone();
            adapter.setRentalItem(cabTypeList);
            isRental = true;
            adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
            SelectedCarTypeID = cabTypeList.get(0).get("iVehicleTypeId");
            RESTRICT_PASSENGER_LIMIT_NOTE = cabTypeList.get(0).get("RESTRICT_PASSENGER_LIMIT_NOTE");
            adapter.notifyDataSetChanged();
            iRentalPackageId = "";

            rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_INFO"));

            rentalBackImage.setVisibility(View.VISIBLE);
            rentalPkgDesc.setVisibility(View.VISIBLE);
            rentalinfoimage.setVisibility(View.GONE);
            rentalPkg.setVisibility(View.GONE);
            rentalarea.setVisibility(View.GONE);
            //rentView.setVisibility(View.GONE);
            rentBackPkgImage.setVisibility(View.GONE);
            rentPkgImage.setVisibility(View.GONE);
            mainAct.setPanelHeight(340);
            //  carTypeRecyclerView.startAnimation(bottomUp);

//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        RelativeLayout.LayoutParams lyParams = (RelativeLayout.LayoutParams) requestPayArea.getLayoutParams();
//                        if (mainAct.isVerticalCabscroll) {
//                            lyParams.setMargins(0, getActContext().getResources().getDimensionPixelSize(R.dimen._140sdp), 0, 0);
//                            requestPayArea.setLayoutParams(lyParams);
//                            //mainAct.setPanelHeight(getActContext().getResources().getDimensionPixelSize(R.dimen._65sdp));
//                            mainAct.setPanelHeight(320);
//                        } else {
//                            lyParams.setMargins(0, getActContext().getResources().getDimensionPixelSize(R.dimen._140sdp), 0, 0);
//                            requestPayArea.setLayoutParams(lyParams);
//                            mainAct.setPanelHeight(345);
//                        }
//                    } catch (Exception e2) {
//                        new Handler().postDelayed(this, 20);
//                    }
//                }
//            };
//            new Handler().postDelayed(r, 20);
        } else if (i == rentPkgImage.getId()) {
            rentalPkg.performClick();
        }
    }


    public void RentalTripHandle() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 0) {
                generateAlert.closeAlertBox();
                iRentalPackageId = "";
            } else {
                generateAlert.closeAlertBox();
                checkSurgePrice();
            }
        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_CONFIRM_START_TRIP_TXT"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        generateAlert.showAlertBox();
    }

    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {
        isFaceMaskVerification = true;
        if (mFileUri != null && uploadImgAlertDialog != null) {

            this.selectedImagePath = mFilePath;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);

                /*int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                double ratioOfImage = (double) imageWidth / (double) imageHeight;
                double widthOfImage = ratioOfImage * Utils.dipToPixels(getActContext(), 200);*/
                if (isFaceMaskVerification) {
                    clearImg.setVisibility(View.VISIBLE);
                    maskVerificationUploadImgArea.setClickable(false);
                    new LoadImageGlide.builder(getActContext(), LoadImageGlide.bind(mFileUri), ((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew))).build();
                    (uploadImgAlertDialog.findViewById(R.id.mCardView)).setBackgroundResource(R.drawable.update_border);
                }
            } catch (Exception e) {
                if (isFaceMaskVerification) {
                    clearImg.setVisibility(View.VISIBLE);
                    maskVerificationUploadImgArea.setClickable(false);
                    new LoadImageGlide.builder(getActContext(), LoadImageGlide.bind(mFileUri), ((ImageView) uploadImgAlertDialog.findViewById(R.id.uploadImgVIew))).build();
                    (uploadImgAlertDialog.findViewById(R.id.mCardView)).setBackgroundResource(R.drawable.update_border);
                }
            }

            if (isFaceMaskVerification) {
                uploadImgAlertDialog.findViewById(R.id.camImgVIew).setVisibility(View.GONE);
                uploadImgAlertDialog.findViewById(R.id.ic_add).setVisibility(View.GONE);
            }
        }
    }
}