package com.adapter.files.deliverAll;

import static com.adapter.files.DrawerAdapter.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.autofit.et.lib.AutoFitEditText;
import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 09-07-2016.
 */

public class ActiveOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    public GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> list;
    Context mContext;
    boolean isFooterEnabled = false;
    View footerView;
    FooterViewHolder footerHolder;
    private OnItemClickListener mItemClickListener;

    public ActiveOrderAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, boolean isFooterEnabled) {
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

        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_list, parent, false);
            this.footerView = v;
            return new FooterViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_orders, parent, false);
            return new ViewHolder(view);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;

            HashMap<String, String> item = list.get(position);

            String image_url = item.get("vImage");
            if (Utils.checkText(image_url)) {

                new LoadImage.builder(LoadImage.bind(image_url), viewHolder.OrderHotelImage).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();
            } else {
                viewHolder.OrderHotelImage.setImageResource(R.color.imageBg);
            }


            viewHolder.orderHotelName.setText(item.get("vCompany"));
            viewHolder.orderHotelAddress.setText(item.get("vRestuarantLocation"));
            if (item.get("vRestuarantLocation").equalsIgnoreCase("")) {
                viewHolder.location.setVisibility(View.GONE);
            }
            if (item.containsKey("tOrderRequestDate")) {
                viewHolder.orderDateVTxt.setText(item.get("tOrderRequestDate"));
            } else if (item.containsKey("ConvertedOrderRequestDate")) {
                viewHolder.orderDateVTxt.setText(item.get("ConvertedOrderRequestDate"));
                viewHolder.orderTimeVTxt.setText(item.get("ConvertedOrderRequestTime"));
            }


            viewHolder.totalVtxt.setText(item.get("fNetTotal"));
            viewHolder.totalHtxt.setText(item.get("LBL_TOTAL_TXT"));
            viewHolder.btn_help.setText(item.get("LBL_HELP"));
            viewHolder.btn_Rating.setText(generalFunc.retrieveLangLBl("", "LBL_RATE_ORDER"));
            viewHolder.btn_viewDetails.setText(item.get("LBL_VIEW_DETAILS"));
            String vServiceCategoryName = item.get("vServiceCategoryName");
            viewHolder.serviceNameTxt.setText(vServiceCategoryName);
            viewHolder.orderNoTxtView.setText("#" + item.get("vOrderNo"));

            if (!vServiceCategoryName.isEmpty()) {
                viewHolder.typeArea.setVisibility(View.VISIBLE);
                viewHolder.orderNoTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen._13sdp));
                viewHolder.orderNoTxtView.setTextColor(Color.parseColor("#141414"));
//                viewHolder.orderNoTxtView.setGravity(Gravity.);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.contentArea.getLayoutParams();
                params.setMargins(0, 8, 0, 0);
                viewHolder.contentArea.setLayoutParams(params);

            } else {
                viewHolder.orderNoTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen._11sdp));
                viewHolder.typeArea.setVisibility(View.GONE);
                viewHolder.orderNoTxtView.setTextColor(Color.parseColor("#777676"));
//                viewHolder.orderNoTxtView.setGravity(Gravity.START);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.contentArea.getLayoutParams();
                params.setMargins(0, -14, 0, 0);
                viewHolder.contentArea.setLayoutParams(params);
            }

            String vService_BG_color = item.get("vService_BG_color");
            if (Utils.checkText(vService_BG_color)) {
                viewHolder.typeArea.setCardBackgroundColor(Color.parseColor(vService_BG_color));
            }
            String vService_TEXT_color = item.get("vService_TEXT_color");
            if (Utils.checkText(vService_TEXT_color)) {
                viewHolder.serviceNameTxt.setTextColor(Color.parseColor(vService_TEXT_color));
            }


            viewHolder.serviceNameTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            viewHolder.serviceNameTxt.setSelected(true);
            viewHolder.serviceNameTxt.setSingleLine(true);

            viewHolder.orderStatus.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            viewHolder.orderStatus.setSelected(true);
            viewHolder.orderStatus.setSingleLine(true);
            String vOrderStatus = item.get("vOrderStatus");
            if (vOrderStatus != null && !vOrderStatus.equals("")) {
                viewHolder.statusArea.setVisibility(View.VISIBLE);
                viewHolder.orderStatus.setText(vOrderStatus);
            } else {
                viewHolder.statusArea.setVisibility(View.GONE);
            }

            if (generalFunc.isRTLmode()) {
                viewHolder.statusArea.setBackground(mContext.getResources().getDrawable(R.drawable.login_border_rtl));
            }

            if (item.get("DisplayLiveTrack").equalsIgnoreCase("Yes")) {
                viewHolder.TrackOrderBtnArea.setVisibility(View.VISIBLE);
                viewHolder.vieDetailsArea.setVisibility(View.GONE);
            } else {
                viewHolder.TrackOrderBtnArea.setVisibility(View.GONE);
                viewHolder.vieDetailsArea.setVisibility(View.VISIBLE);
            }

            if (item.get("isRatingButtonShow").equalsIgnoreCase("Yes")) {
                viewHolder.helpArea.setVisibility(View.GONE);
                viewHolder.RatingArea.setVisibility(View.VISIBLE);
            } else {
                viewHolder.helpArea.setVisibility(View.VISIBLE);
                viewHolder.RatingArea.setVisibility(View.GONE);
            }

            viewHolder.btn_trackOrder.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position, "track");
                }
            });

            viewHolder.btn_trackOrder.setText(item.get("LBL_TRACK_ORDER"));

            viewHolder.btn_help.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position, "help");
                }
            });
            viewHolder.btn_Rating.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position, "rating");
                }
            });
            viewHolder.btn_viewDetails.setOnClickListener(v -> mItemClickListener.onItemClickList(view, position, "view"));


        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
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
//        Logger.d("Footer", "added");
        this.isFooterEnabled = true;
        notifyDataSetChanged();
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.VISIBLE);
    }

    public void removeFooterView() {
//        Logger.d("Footer", "removed");
        if (footerHolder != null)
            footerHolder.progressArea.setVisibility(View.GONE);
//        footerHolder.progressArea.setPadding(0, -1 * footerView.getHeight(), 0, 0);
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position, String isSelect);
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView OrderHotelImage;
        public MTextView orderHotelName;
        public MTextView orderHotelAddress;
        public MTextView orderDateHTxt;
        public MTextView orderDateVTxt;
        public MTextView orderTimeVTxt;
        public MTextView totalHtxt;
        public AutoFitEditText totalVtxt;
        public RelativeLayout TrackOrderBtnArea;
        public RelativeLayout vieDetailsArea, helpArea, RatingArea;
        public LinearLayout statusArea;

        public MTextView btn_trackOrder;
        public MTextView btn_help, btn_Rating;
        public MTextView btn_viewDetails;
        public MTextView orderStatus;
        public MTextView serviceNameTxt;
        public MTextView orderNoTxtView;
        public LinearLayout contentArea;
        public ImageView location;
        LinearLayout dataArea;
        CardView typeArea;

        public ViewHolder(View view) {
            super(view);

            OrderHotelImage = (ImageView) view.findViewById(R.id.OrderHotelImage);
            orderHotelName = (MTextView) view.findViewById(R.id.orderHotelName);
            orderHotelAddress = (MTextView) view.findViewById(R.id.orderHotelAddress);
            orderDateVTxt = (MTextView) view.findViewById(R.id.orderDateVTxt);
            orderTimeVTxt = (MTextView) view.findViewById(R.id.orderTimeVTxt);
            totalHtxt = (MTextView) view.findViewById(R.id.totalHtxt);
            totalVtxt = (AutoFitEditText) view.findViewById(R.id.totalVtxt);
            TrackOrderBtnArea = (RelativeLayout) view.findViewById(R.id.TrackOrderBtnArea);
            vieDetailsArea = (RelativeLayout) view.findViewById(R.id.vieDetailsArea);
            helpArea = (RelativeLayout) view.findViewById(R.id.helpArea);
            RatingArea = (RelativeLayout) view.findViewById(R.id.RatingArea);
            statusArea = (LinearLayout) view.findViewById(R.id.statusArea);
            btn_trackOrder = (MTextView) view.findViewById(R.id.btn_trackOrder);
            btn_help = (MTextView) view.findViewById(R.id.btn_help);
            btn_Rating = (MTextView) view.findViewById(R.id.btn_Rating);
            btn_viewDetails = (MTextView) view.findViewById(R.id.btn_viewDetails);
            orderStatus = (MTextView) view.findViewById(R.id.orderStatus);
            serviceNameTxt = (MTextView) view.findViewById(R.id.serviceNameTxt);
            orderNoTxtView = (MTextView) view.findViewById(R.id.orderNoTxtView);
            dataArea = (LinearLayout) view.findViewById(R.id.dataArea);
            typeArea = (CardView) view.findViewById(R.id.typeArea);
            contentArea = (LinearLayout) view.findViewById(R.id.contentArea);
            location = (ImageView) view.findViewById(R.id.location);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressArea;

        public FooterViewHolder(View itemView) {
            super(itemView);
            progressArea = (LinearLayout) itemView;
        }
    }
}