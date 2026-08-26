package com.adapter.files;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.multixpro.store.R;
import com.general.files.GeneralFunctions;
import com.kyleduo.switchbutton.SwitchButton;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 22-05-18.
 */

public class ItemAvailabilityRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_HEADER = 3;
    private static final int TYPE_FOOTER = 2;
    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list;
    Context mContext;
    boolean isFooterEnabled = false;
    View footerView;
    FooterViewHolder footerHolder;
    boolean isOnlineAvoid = false;
    private OnItemClickListener mItemClickListener;


    public ItemAvailabilityRecycleAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_availability_header_design, parent, false);
            return new HeaderViewHolder(v);
        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_availability_design, parent, false);
            return new ViewHolder(view);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.nameVTxt.setText(item.get("MenuItemNameConverted"));
            viewHolder.nameVTxt.setSelected(true);
            viewHolder.amountVTxt.setText(item.get("fPriceConverted"));
            viewHolder.inStockVTxt.setText(item.get("eAvailable"));

            viewHolder.inStockVTxt.setTextColor(Color.parseColor(item.get("vService_TEXT_color")));
            viewHolder.inStockVTxt.getBackground().setColorFilter(Color.parseColor(item.get("vService_BG_color")),
                    PorterDuff.Mode.SRC_ATOP);

            /*if (item.get("eAvailable").equalsIgnoreCase("Yes")) {
                viewHolder.inStockVTxt.setText(generalFunc.retrieveLangLBl("","LBL_IN_STOCK"));
                viewHolder.onlineOfflineSwitch.setCheckedNoEvent(true);
                viewHolder.onlineOfflineSwitch.setThumbColorRes(R.color.white);
                viewHolder.onlineOfflineSwitch.setBackColorRes(R.color.Green);
            }
            else
            {
                viewHolder.inStockVTxt.setText(generalFunc.retrieveLangLBl("Not Available", "LBL_NOT_AVAILABLE"));
                viewHolder.onlineOfflineSwitch.setCheckedNoEvent(false);
                viewHolder.onlineOfflineSwitch.setThumbColorRes(android.R.color.white);
                viewHolder.onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);
            }*/

            if (item.get("eAvailable").equalsIgnoreCase("Yes")) {
                viewHolder.onlineOfflineSwitch.setCheckedNoEvent(true);
                viewHolder.onlineOfflineSwitch.setThumbColorRes(R.color.Green);
                viewHolder.inStockVTxt.setText(item.get("LBL_IN_STOCK"));
            } else {
                viewHolder.onlineOfflineSwitch.setCheckedNoEvent(false);
                viewHolder.onlineOfflineSwitch.setThumbColorRes(android.R.color.holo_red_dark);
                viewHolder.inStockVTxt.setText(item.get("LBL_NOT_AVAILABLE"));
            }



            /*viewHolder.onlineOfflineSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

                if (b == true)
                {
                    viewHolder.onlineOfflineSwitch.setThumbColorRes(R.color.white);
                    viewHolder.onlineOfflineSwitch.setBackColorRes(R.color.Green);
                    if (mItemClickListener != null) {
                        mItemClickListener.switchOnlineOffline(b, position);
                    }
                }
                else
                {
                    viewHolder.onlineOfflineSwitch.setThumbColorRes(android.R.color.white);
                    viewHolder.onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);
                    if (mItemClickListener != null) {
                        mItemClickListener.switchOnlineOffline(b, position);
                    }
                }
            });*/

            viewHolder.onlineOfflineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (mItemClickListener != null) {
                    mItemClickListener.switchOnlineOffline(isChecked, position);
                }
            });

        } else if (holder instanceof HeaderViewHolder) {
            final HashMap<String, String> item = list.get(position);
            final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.headerTxtView.setText(item.get("CategoryName"));


        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        HashMap<String, String> item = position < list.size() ? list.get(position) : new HashMap<>();
        if (isPositionFooter(position) && isFooterEnabled == true) {
            return TYPE_FOOTER;
        } else if (item.get("TYPE") != null && item.get("TYPE").equalsIgnoreCase("" + TYPE_HEADER)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == list.size();
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (isFooterEnabled == true) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    public void addFooterView() {

        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null) {
            footerHolder.progressContainer.setVisibility(View.VISIBLE);
        }
    }

    public void removeFooterView() {
        if (footerHolder != null)
            footerHolder.progressContainer.setVisibility(View.GONE);
    }

    public interface OnItemClickListener {
        void onItemClickList(int position);

        void switchOnlineOffline(boolean isOnlineAvoid, int position);
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public MTextView nameVTxt;
        public MTextView inStockVTxt;
        public MTextView amountVTxt;
        public View containerView;
        public SwitchButton onlineOfflineSwitch;

        public ViewHolder(View view) {
            super(view);

            containerView = view;

            nameVTxt = (MTextView) view.findViewById(R.id.nameVTxt);
            inStockVTxt = (MTextView) view.findViewById(R.id.inStockVTxt);
            amountVTxt = (MTextView) view.findViewById(R.id.amountVTxt);
            onlineOfflineSwitch = (SwitchButton) view.findViewById(R.id.onlineOfflineSwitch);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressContainer;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressContainer = (LinearLayout) itemView.findViewById(R.id.progressContainer);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        MTextView headerTxtView;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerTxtView = (MTextView) itemView.findViewById(R.id.headerTxtView);

        }
    }
}

