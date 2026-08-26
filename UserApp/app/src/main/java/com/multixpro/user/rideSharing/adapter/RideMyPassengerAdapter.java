package com.multixpro.user.rideSharing.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.call.CommunicationManager;
import com.general.call.DefaultCommunicationHandler;
import com.general.call.MediaDataProvider;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.FareBreakDownActivity;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ItemRideMyPassengerListBinding;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MButton;
import com.view.MaterialRippleLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RideMyPassengerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<HashMap<String, String>> list;
    private final OnItemClickListener listener;
    private final String LBL_VIEW_REASON, LBL_DECLINE_TXT, LBL_ACCEPT_TXT, callingMethod;

    public RideMyPassengerAdapter(GeneralFunctions generalFunc, ArrayList<HashMap<String, String>> list, OnItemClickListener mItemClickListener) {
        this.list = list;
        this.listener = mItemClickListener;
        this.LBL_VIEW_REASON = generalFunc.retrieveLangLBl("", "LBL_VIEW_REASON");
        this.LBL_DECLINE_TXT = generalFunc.retrieveLangLBl("", "LBL_DECLINE_TXT");
        this.LBL_ACCEPT_TXT = generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TXT");
        this.callingMethod = generalFunc.getJsonValue("CALLING_METHOD_RIDE_SHARE", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemRideMyPassengerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> mapData = list.get(position);

        if (holder instanceof ViewHolder) {
            ViewHolder listHolder = (ViewHolder) holder;

            listHolder.binding.passengerNameTxt.setText(mapData.get("rider_Name"));
            listHolder.binding.statusTxt.setText(mapData.get("statusMessage"));
            if (mapData.containsKey("BookedSeatsTxt")) {
                listHolder.binding.availableSeatsTxt.setText(mapData.get("BookedSeatsTxt"));
            }
            if (mapData.containsKey("BookedSeatsTxt") && Objects.requireNonNull(mapData.get("eShowCallImg")).equalsIgnoreCase("Yes")) {

                listHolder.binding.callArea.setVisibility(View.VISIBLE);
                listHolder.binding.callArea.setOnClickListener(view -> {
                    MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                            .setCallId(mapData.get("rider_iUserId"))
                            .setToMemberName(mapData.get("rider_Name"))
                            .setPhoneNumber(mapData.get("rider_Phone"))
                            .setToMemberType(Utils.CALLTOPASSENGER)
                            .setToMemberImage(mapData.get("rider_ProfileImg"))
                            .setMedia(CommunicationManager.MEDIA_TYPE)
                            .build();
                    if (callingMethod.equalsIgnoreCase("VOIP")) {
                        CommunicationManager.getInstance().communicatePhoneOrVideo(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.PHONE_CALL);
                    } else if (callingMethod.equalsIgnoreCase("VIDEOCALL")) {
                        CommunicationManager.getInstance().communicatePhoneOrVideo(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.VIDEO_CALL);
                    } else if (callingMethod.equalsIgnoreCase("VOIP-VIDEOCALL")) {
                        CommunicationManager.getInstance().communicatePhoneOrVideo(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.BOTH_CALL);
                    } else if (!Utils.checkText(callingMethod) || callingMethod.equalsIgnoreCase("NORMAL")) {
                        DefaultCommunicationHandler.getInstance().executeAction(MyApp.getInstance().getCurrentAct(), CommunicationManager.TYPE.PHONE_CALL, mDataProvider);
                    }
                });
            } else {
                listHolder.binding.callArea.setVisibility(View.GONE);
            }
            String riderImage = mapData.get("rider_ProfileImg");
            if (!Utils.checkText(riderImage)) {
                riderImage = "Temp";
            }
            new LoadImage.builder(LoadImage.bind(riderImage), listHolder.binding.rideDriverImg).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();

            MButton viewReasonBtn, declineBtn, acceptBtn;
            viewReasonBtn = ((MaterialRippleLayout) listHolder.binding.viewReasonBtn).getChildView();
            declineBtn = ((MaterialRippleLayout) listHolder.binding.declineBtn).getChildView();
            acceptBtn = ((MaterialRippleLayout) listHolder.binding.acceptBtn).getChildView();

            viewReasonBtn.setText(LBL_VIEW_REASON);
            declineBtn.setText(LBL_DECLINE_TXT);
            acceptBtn.setText(LBL_ACCEPT_TXT);

            listHolder.binding.viewReasonBtnArea.setVisibility(View.GONE);
            listHolder.binding.declineBtnArea.setVisibility(View.GONE);
            listHolder.binding.acceptBtnArea.setVisibility(View.GONE);

            String eStatus = mapData.get("eStatus");
            if (eStatus != null) {
                if (eStatus.equalsIgnoreCase("Pending")) {

                    listHolder.binding.declineBtnArea.setVisibility(View.VISIBLE);
                    listHolder.binding.acceptBtnArea.setVisibility(View.VISIBLE);
                    declineBtn.setOnClickListener(v -> listener.onDeclineClick(mapData, position));
                    acceptBtn.setOnClickListener(v -> listener.onAcceptClick(mapData, position));

                } else if (eStatus.equalsIgnoreCase("Declined")) {
                    listHolder.binding.viewReasonBtnArea.setVisibility(View.VISIBLE);
                    viewReasonBtn.setOnClickListener(v -> listener.onViewReasonClick(mapData));
                }
            }

            listHolder.binding.paymentModeHTxt.setText(mapData.get("PaymentModeTitle") + ": ");
            listHolder.binding.paymentModeVTxt.setText(mapData.get("PaymentModeLabel"));

            String priceBreakdown = mapData.get("PriceBreakdown");
            if (Utils.checkText(priceBreakdown)) {
                listHolder.binding.totalPriceTxt.setVisibility(View.VISIBLE);
                listHolder.binding.totalPriceTxt.setText(mapData.get("TotalFare"));
                listHolder.binding.totalPriceTxt.setOnClickListener(v -> {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isData", true);
                    bn.putString("fareData", priceBreakdown);
                    bn.putString("PriceBreakdownTitle", mapData.get("PriceBreakdownTitle"));
                    new ActUtils(listHolder.itemView.getContext()).startActWithData(FareBreakDownActivity.class, bn);
                });
            } else {
                listHolder.binding.totalPriceTxt.setVisibility(View.GONE);
            }
            String PaymentLabel = mapData.get("PaymentLabel");
            if (Utils.checkText(PaymentLabel)) {
                listHolder.binding.paymentNoteTxt.setVisibility(View.VISIBLE);
                listHolder.binding.paymentNoteTxt.setText(PaymentLabel);
            } else {
                listHolder.binding.paymentNoteTxt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemRideMyPassengerListBinding binding;

        private ViewHolder(ItemRideMyPassengerListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onViewReasonClick(HashMap<String, String> hashMap);

        void onDeclineClick(HashMap<String, String> hashMap, int position);

        void onAcceptClick(HashMap<String, String> hashMap, int position);
    }
}