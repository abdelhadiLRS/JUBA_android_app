package com.adapter.files;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.multixpro.user.R;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 09-07-2016.
 */
public class AllBiddingRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    public GeneralFunctions generalFunc;

    ArrayList<HashMap<String, String>> list;
    Context mContext;
    boolean isFooterEnabled = false;
    View footerView;

    FooterViewHolder footerHolder;
    private OnItemClickListener mItemClickListener;
    String userProfileJson;
    int size15_dp;
    int imagewidth;


    public AllBiddingRecycleAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        size15_dp = (int) mContext.getResources().getDimension(R.dimen._15sdp);
        imagewidth = (int) mContext.getResources().getDimension(R.dimen._50sdp);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bidding_layout, parent, false);
            return new ViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;


            viewHolder.cancelBiddingTxt.setText(item.get("LBL_CANCEL_BIDDING"));
            viewHolder.viewDetailsTxt.setText(item.get("LBL_VIEW_DETAILS"));
            viewHolder.viewBiddingTxt.setText(item.get("LBL_VIEW_TASK_BIDDING"));
            viewHolder.historyNoHTxt.setText(item.get("LBL_TASK_TXT"));
            viewHolder.historyNoVTxt.setText("#" + item.get("vBiddingPostNo"));
            String ConvertedTripRequestDate = item.get("ConvertedTripRequestDate");
            String ConvertedTripRequestTime = item.get("ConvertedTripRequestTime");
            if (ConvertedTripRequestDate != null) {
                viewHolder.dateTxt.setText(ConvertedTripRequestDate);
                viewHolder.timeTxt.setText(ConvertedTripRequestTime);
            }

            String tSaddress = item.get("vServiceAddress");
            viewHolder.sourceAddressTxt.setText(tSaddress);
            viewHolder.sAddressTxt.setText(tSaddress);
            viewHolder.sourceAddressHTxt.setText(item.get("LBL_BIDDING_SERVICE_ADDRESS_TXT"));

            String vServiceTitle = item.get("vTitle");
            viewHolder.typeArea.setVisibility(View.VISIBLE);
            viewHolder.SelectedTypeNameTxt.setText(vServiceTitle);
            viewHolder.pickupLocArea.setPadding(0, 0, 0, 0);

            String vService_BG_color = item.get("vService_BG_color");
            if (Utils.checkText(vService_BG_color)) {
                viewHolder.typeArea.setCardBackgroundColor(Color.parseColor(vService_BG_color));
            }

            String vService_TEXT_color = item.get("vService_TEXT_color");
            if (Utils.checkText(vService_TEXT_color)) {
                viewHolder.SelectedTypeNameTxt.setTextColor(Color.parseColor(vService_TEXT_color));
            }

            String iActiveDisplay = item.get("bidding_status");

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


            viewHolder.viewBiddingDetailsArea.setOnClickListener(view -> btnClicked(view, position, "ViewBidding"));
            viewHolder.cancelBiddingArea.setOnClickListener(view -> btnClicked(view, position, "CancelBidding"));
            viewHolder.viewDetailsArea.setOnClickListener(view -> btnClicked(view, position, "ViewDetail"));


            if (item.get("showBiddingTaskBtn").equalsIgnoreCase("Yes")) {
                viewHolder.viewBiddingDetailsArea.setVisibility(View.VISIBLE);
            } else {
                viewHolder.viewBiddingDetailsArea.setVisibility(View.GONE);
            }
            if (item.get("showCancelBtn").equalsIgnoreCase("Yes")) {
                viewHolder.cancelBiddingArea.setVisibility(View.VISIBLE);
            } else {
                viewHolder.cancelBiddingArea.setVisibility(View.GONE);
            }

            if (item.get("showDetailBtn").equalsIgnoreCase("Yes")) {
                viewHolder.viewDetailsArea.setVisibility(View.VISIBLE);
            } else {
                viewHolder.viewDetailsArea.setVisibility(View.GONE);
            }


        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }


    }

    private void btnClicked(View view, int position, String type) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClickList(view, position, type);
        }
    }

    @Override
    public int getItemViewType(int position) {
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

        void onViewServiceClickList(View v, int position);

    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public MTextView providerNameTxt;
        public MTextView sAddressTxt;
        public SelectableRoundedImageView providerImgView;
        public SimpleRatingBar ratingBar;

        public MTextView historyNoHTxt;
        public MTextView historyNoVTxt;
        public MTextView dateTxt;
        public MTextView timeTxt;
        public MTextView sourceAddressTxt;
        public MTextView sourceAddressHTxt;
        public MTextView statusHTxt;
        public MTextView statusVTxt;
        public LinearLayout contentArea;
        public MTextView SelectedTypeNameTxt;
        public MTextView packageTxt;
        public LinearLayout pickupLocArea;
        public LinearLayout statusArea;

        public LinearLayout noneUfxMultiBtnArea;
        public MTextView btn_type_view_cancel_reason;


        public LinearLayout viewDetailsBtnArea;
        public MTextView viewDetailsBtn;
        public LinearLayout viewBiddingDetailsArea, cancelBiddingArea, viewDetailsArea;
        public MTextView viewBiddingTxt, cancelBiddingTxt, viewDetailsTxt;
        public CardView typeArea;


        public ViewHolder(View view) {
            super(view);
            packageTxt = (MTextView) view.findViewById(R.id.packageTxt);
            contentArea = (LinearLayout) view.findViewById(R.id.contentArea);

            providerNameTxt = (MTextView) view.findViewById(R.id.providerNameTxt);
            sAddressTxt = (MTextView) view.findViewById(R.id.sAddressTxt);
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

            noneUfxMultiBtnArea = (LinearLayout) view.findViewById(R.id.noneUfxMultiBtnArea);

            btn_type_view_cancel_reason = (MTextView) view.findViewById(R.id.btn_type_view_cancel_reason);
            viewDetailsBtnArea = (LinearLayout) view.findViewById(R.id.viewDetailsBtnArea);
            viewDetailsBtn = (MTextView) view.findViewById(R.id.viewDetailsBtn);
            viewBiddingDetailsArea = (LinearLayout) view.findViewById(R.id.viewBiddingDetailsArea);
            cancelBiddingArea = (LinearLayout) view.findViewById(R.id.cancelBiddingArea);
            viewDetailsArea = (LinearLayout) view.findViewById(R.id.viewDetailsArea);
            viewBiddingTxt = (MTextView) view.findViewById(R.id.viewBiddingTxt);
            cancelBiddingTxt = (MTextView) view.findViewById(R.id.cancelBiddingTxt);
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
}
