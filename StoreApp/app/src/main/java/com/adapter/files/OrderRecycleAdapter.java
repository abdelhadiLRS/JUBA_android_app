package com.adapter.files;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.store.R;
import com.multixpro.store.databinding.ItemOrderDesignBinding;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Esite on 30-03-2018.
 */

public class OrderRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final ArrayList<HashMap<String, String>> list;
    private final OnItemClickListener mItemClickListener;
    private int newOrder = -1;

    private boolean isFooterEnabled;
    private View footerView;
    private FooterViewHolder footerHolder;

    public OrderRecycleAdapter(ArrayList<HashMap<String, String>> list, boolean isFooterEnabled, OnItemClickListener mItemClickListener) {
        this.list = list;
        this.isFooterEnabled = isFooterEnabled;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            return new ViewHolder(ItemOrderDesignBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;


            if (item.get("eTakeaway") != null && item.get("eTakeaway").equalsIgnoreCase("Yes")) {
                viewHolder.binding.takeAwayTxt.setVisibility(View.VISIBLE);
                viewHolder.binding.takeAwayTxt.setText(item.get("LBL_TAKE_AWAY"));
            } else {
                viewHolder.binding.takeAwayTxt.setVisibility(View.GONE);
            }

            if (item.get("eOrderType") != null && item.get("eOrderType").equalsIgnoreCase("Dine In")) {
                viewHolder.binding.takeAwayTxt.setVisibility(View.VISIBLE);
                viewHolder.binding.takeAwayTxt.setText(item.get("LBL_DINE_IN_TXT"));
            } else if (item.get("eTakeaway") != null && item.get("eTakeaway").equalsIgnoreCase("Yes")) {
                viewHolder.binding.takeAwayTxt.setVisibility(View.VISIBLE);
                viewHolder.binding.takeAwayTxt.setText(item.get("LBL_TAKE_AWAY"));
            } else {
                viewHolder.binding.takeAwayTxt.setVisibility(View.GONE);
            }

            if (viewHolder.binding.takeAwayTxt.getVisibility() == View.VISIBLE) {
                if (Utils.checkText(item.get("vBgColor"))) {
                    viewHolder.binding.cardTakeAway.setCardBackgroundColor(Color.parseColor(item.get("vBgColor")));
                }
                if (Utils.checkText(item.get("vTextColor"))) {
                    viewHolder.binding.takeAwayTxt.setTextColor(Color.parseColor(item.get("vTextColor")));
                }
            }

            viewHolder.binding.orderIdTxt.setText("#" + item.get("vOrderNoConverted"));
            viewHolder.binding.noItemsTxt.setText((GeneralFunctions.parseIntegerValue(1, item.get("TotalItems")) > 1 ? item.get("TotalItemsConverted") + " " + item.get("LBL_ITEMS") : item.get("TotalItemsConverted") + " " + item.get("LBL_ITEM")));


            viewHolder.binding.timeTxt.setText(item.get("tOrderRequestDateConverted"));

            viewHolder.binding.orderItem.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position);
                }
            });


            /*if (newOrder == 1) {
                if (position == 0) {
                    new CreateRoundedView(Color.parseColor("#ffffff"), (int) mContext.getResources().getDimension(R.dimen._8sdp), 2, mContext.getResources().getColor(R.color.requestTimerColor), viewHolder.binding.orderItem);
                }
            } else {
                new CreateRoundedView(Color.parseColor("#ffffff"), (int) mContext.getResources().getDimension(R.dimen._8sdp), 2, mContext.getResources().getColor(R.color.white), viewHolder.binding.orderItem);
            }*/

            if (position == list.size() - 1) {
                viewHolder.binding.extraView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.extraView.setVisibility(View.GONE);
            }

        } else {
            this.footerHolder = (FooterViewHolder) holder;
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

    @SuppressLint("NotifyDataSetChanged")
    public void addFooterView() {
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
        if (footerHolder != null) {
            footerHolder.progressArea.setVisibility(View.GONE);
            try {
                footerHolder.progressArea.setPadding(0, -1 * footerView.getMeasuredHeight(), 0, 0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemOrderDesignBinding binding;

        private ViewHolder(ItemOrderDesignBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    protected static class FooterViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressArea = (LinearLayout) itemView;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void hilightNewOrder() {
        newOrder = 1;
        notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void delightNewOrder() {
        newOrder = -1;
        notifyDataSetChanged();
    }
}