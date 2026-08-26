package com.adapter.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.provider.R;
import com.utils.Utils;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.simpleratingbar.SimpleRatingBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BiddingListRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public GeneralFunctions generalFunc;
    Context mContext;
    View footerView;

    ArrayList<HashMap<String, String>> list;
    private static final int TYPE_ITEM = 1, TYPE_FOOTER = 2, TYPE_HEADER = 3;
    boolean isFooterEnabled;

    FooterViewHolder footerHolder;
    private OnItemClickListener mItemClickListener;
    String userProfileJson;
    int size15_dp;
    int imagewidth;
    private final String LBL_TASK_TXT, LBL_BIDDING_TASK_BUDGET_TXT, LBL_BIDDING_SERVICE_ADDRESS_TXT, LBL_VIEW_DETAILS, LBL_EARNED_AMOUNT;

    public BiddingListRecycleAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        size15_dp = (int) mContext.getResources().getDimension(R.dimen._15sdp);
        imagewidth = (int) mContext.getResources().getDimension(R.dimen._50sdp);

        LBL_TASK_TXT = generalFunc.retrieveLangLBl("", "LBL_TASK_TXT");
        LBL_BIDDING_TASK_BUDGET_TXT = generalFunc.retrieveLangLBl("", "LBL_BIDDING_BUDGET_TXT");
        LBL_EARNED_AMOUNT = generalFunc.retrieveLangLBl("", "LBL_EARNED_AMOUNT");
        LBL_BIDDING_SERVICE_ADDRESS_TXT = generalFunc.retrieveLangLBl("", "LBL_BIDDING_SERVICE_ADDRESS_TXT");
        LBL_VIEW_DETAILS = generalFunc.retrieveLangLBl("", "LBL_VIEW_DETAILS");
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earning_amount_layout, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bidding_layout, parent, false);
            return new ViewHolder(view);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;

            String iActiveDisplay = item.get("bidding_status");

            viewHolder.viewDetailsTxt.setText(LBL_VIEW_DETAILS);
            viewHolder.historyNoHTxt.setText(LBL_TASK_TXT);
            if (Utils.checkText(iActiveDisplay) && iActiveDisplay.equalsIgnoreCase("Completed")) {
                viewHolder.txtBiddingAmount.setText(LBL_EARNED_AMOUNT + ": " + generalFunc.convertNumberWithRTL(item.get("totalEarning")));
            } else {
                viewHolder.txtBiddingAmount.setText(LBL_BIDDING_TASK_BUDGET_TXT + ": " + generalFunc.convertNumberWithRTL(item.get("fBiddingAmount")));
            }
            viewHolder.historyNoVTxt.setText("#" + item.get("vBiddingPostNo"));
            String ConvertedTripRequestDate = item.get("ConvertedTripRequestDate");
            String ConvertedTripRequestTime = item.get("ConvertedTripRequestTime");
            if (ConvertedTripRequestDate != null) {
                viewHolder.dateTxt.setText(ConvertedTripRequestDate);
                viewHolder.timeTxt.setText(ConvertedTripRequestTime);
            }

            viewHolder.sourceAddressTxt.setText(item.get("vServiceAddress"));
            viewHolder.sourceAddressHTxt.setText(LBL_BIDDING_SERVICE_ADDRESS_TXT);

            String vServiceTitle = item.get("vTitle");
            viewHolder.typeArea.setVisibility(View.VISIBLE);
            viewHolder.SelectedTypeNameTxt.setText(vServiceTitle);
            viewHolder.typeArea.setCardBackgroundColor(Color.parseColor(item.get("vService_BG_color")));
            viewHolder.SelectedTypeNameTxt.setTextColor(Color.parseColor(item.get("vService_TEXT_color")));
            viewHolder.pickupLocArea.setPadding(0, 0, 0, 0);

            if (Utils.checkText(iActiveDisplay)) {
                viewHolder.statusArea.setVisibility(View.VISIBLE);
                viewHolder.statusVTxt.setText(iActiveDisplay);
                viewHolder.statusArea.setBackgroundColor(Color.parseColor(item.get("vStatus_BG_color")));
            } else {
                viewHolder.statusArea.setVisibility(View.GONE);
            }

            if (generalFunc.isRTLmode()) {
                viewHolder.statusArea.setRotation(180);
                viewHolder.statusVTxt.setRotation(180);
            }

            viewHolder.SelectedTypeNameTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            viewHolder.SelectedTypeNameTxt.setSelected(true);
            viewHolder.SelectedTypeNameTxt.setSingleLine(true);

            viewHolder.statusVTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            viewHolder.statusVTxt.setSelected(true);
            viewHolder.statusVTxt.setSingleLine(true);

            if (Objects.requireNonNull(item.get("showDetailBtn")).equalsIgnoreCase("Yes")) {
                viewHolder.viewDetailsArea.setVisibility(View.VISIBLE);
                viewHolder.viewDetailsArea.setOnClickListener(view -> btnClicked(view, position, "ViewDetail"));
            } else {
                viewHolder.viewDetailsArea.setVisibility(View.GONE);
            }

        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            Map<String, String> map = list.get(position);

            headerHolder.tripsCountTxt.setText(map.get("TripCount"));
            headerHolder.fareTxt.setText(map.get("TotalEarning"));
            headerHolder.avgRatingCalcTxt.setText(map.get("AvgRating"));

            if (map.containsKey("isCalenderView") && Objects.requireNonNull(map.get("isCalenderView")).equalsIgnoreCase("yes")) {
                headerHolder.earnedAmountArea.setVisibility(View.VISIBLE);
            } else {
                headerHolder.earnedAmountArea.setVisibility(View.GONE);
            }

        } else {
            this.footerHolder = (FooterViewHolder) holder;
        }
    }

    private void btnClicked(View view, int position, String type) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClickList(view, position, type);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position) == 0) {
            return TYPE_HEADER;
        }
        if (isPositionFooter(position) && isFooterEnabled) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == list.size();
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (isFooterEnabled) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    public void addFooterView() {
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
        if (footerHolder != null) {
            isFooterEnabled = false;
            footerHolder.progressArea.setVisibility(View.GONE);
        }
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position, String type);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SelectableRoundedImageView providerImgView;
        public SimpleRatingBar ratingBar;

        public MTextView viewDetailsBtn, viewDetailsTxt;
        public MTextView txtBiddingAmount, historyNoHTxt, historyNoVTxt, dateTxt, timeTxt, btn_type_view_cancel_reason;
        public MTextView sourceAddressTxt, sourceAddressHTxt, statusHTxt, statusVTxt, SelectedTypeNameTxt, packageTxt;
        public LinearLayout contentArea, pickupLocArea, statusArea;
        public LinearLayout noneUfxMultiArea, noneUfxMultiBtnArea;
        public LinearLayout viewDetailsBtnArea, viewDetailsArea;

        public CardView typeArea;

        public ViewHolder(View view) {
            super(view);
            packageTxt = (MTextView) view.findViewById(R.id.packageTxt);
            contentArea = (LinearLayout) view.findViewById(R.id.contentArea);
            txtBiddingAmount = (MTextView) view.findViewById(R.id.txtBiddingAmount);

            providerImgView = (SelectableRoundedImageView) view.findViewById(R.id.providerImgView);
            ratingBar = (SimpleRatingBar) view.findViewById(R.id.ratingBar);
            historyNoHTxt = (MTextView) view.findViewById(R.id.historyNoHTxt);
            historyNoVTxt = (MTextView) view.findViewById(R.id.historyNoVTxt);
            dateTxt = (MTextView) view.findViewById(R.id.dateTxt);
            timeTxt = (MTextView) view.findViewById(R.id.timeTxt);
            sourceAddressTxt = (MTextView) view.findViewById(R.id.sourceAddressTxt);
            sourceAddressHTxt = (MTextView) view.findViewById(R.id.sourceAddressHTxt);
            statusHTxt = (MTextView) view.findViewById(R.id.statusHTxt);
            statusVTxt = (MTextView) view.findViewById(R.id.statusVTxt);

            SelectedTypeNameTxt = (MTextView) view.findViewById(R.id.SelectedTypeNameTxt);
            statusArea = (LinearLayout) view.findViewById(R.id.statusArea);
            pickupLocArea = (LinearLayout) view.findViewById(R.id.pickupLocArea);

            noneUfxMultiArea = (LinearLayout) view.findViewById(R.id.noneUfxMultiArea);
            noneUfxMultiBtnArea = (LinearLayout) view.findViewById(R.id.noneUfxMultiBtnArea);

            btn_type_view_cancel_reason = (MTextView) view.findViewById(R.id.btn_type_view_cancel_reason);
            viewDetailsBtnArea = (LinearLayout) view.findViewById(R.id.viewDetailsBtnArea);
            viewDetailsBtn = (MTextView) view.findViewById(R.id.viewDetailsBtn);
            viewDetailsArea = (LinearLayout) view.findViewById(R.id.viewDetailsArea);
            viewDetailsTxt = (MTextView) view.findViewById(R.id.viewDetailsTxt);
            typeArea = (CardView) view.findViewById(R.id.typeArea);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressArea = (LinearLayout) itemView;
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout earnedAmountArea;
        private final MTextView earnTitleTxt, fareTxt, tripsCompletedTxt, tripsCountTxt, avgRatingTxt, avgRatingCalcTxt;

        private HeaderViewHolder(View view) {
            super(view);
            earnTitleTxt = view.findViewById(R.id.earnTitleTxt);
            fareTxt = view.findViewById(R.id.fareTxt);
            tripsCompletedTxt = view.findViewById(R.id.tripsCompletedTxt);
            tripsCountTxt = view.findViewById(R.id.tripsCountTxt);
            avgRatingTxt = view.findViewById(R.id.avgRatingTxt);
            avgRatingCalcTxt = view.findViewById(R.id.avgRatingCalcTxt);
            earnedAmountArea = view.findViewById(R.id.earnedAmountArea);

            earnTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOTAL_EARNINGS"));
            tripsCompletedTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOTAL_BIDS_TXT"));
            avgRatingTxt.setText(generalFunc.retrieveLangLBl("", "LBL_AVG_RATING"));
        }
    }
}