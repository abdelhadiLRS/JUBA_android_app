package com.adapter.files;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.store.R;
import com.utils.LoadImage;
import com.utils.Logger;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryImagesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    public GeneralFunctions generalFunc;
    ArrayList<String> list;
    Context mContext;
    boolean isFooterEnabled = false;
    View footerView;
    FooterViewHolder footerHolder;
    private OnItemClickListener mItemClickListener;
    boolean isDelete = false;
    boolean isSel = false;
    int itemWidth;
    int width_;
    int width;
    ArrayList<HashMap<String, String>> hashlist;
    boolean isMultiBanner = false;


    public GalleryImagesRecyclerAdapter(Context mContext, ArrayList<String> list, GeneralFunctions generalFunc, boolean isFooterEnabled, boolean isDelete, boolean isSel, boolean isMultiBanner) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
        this.isDelete = isDelete;
        this.isSel = isSel;
        itemWidth = (int) (Utils.getScreenPixelWidth(mContext) / 3) - mContext.getResources().getDimensionPixelSize(R.dimen._20sdp);
        this.isMultiBanner = isMultiBanner;


    }

    public GalleryImagesRecyclerAdapter(Context mContext, ArrayList<HashMap<String, String>> hashlist, GeneralFunctions generalFunc, boolean isFooterEnabled, boolean isMultiBanner) {
        this.mContext = mContext;
        this.hashlist = hashlist;
        this.generalFunc = generalFunc;
        this.isFooterEnabled = isFooterEnabled;
        itemWidth = (int) (Utils.getScreenPixelWidth(mContext) / 3) - mContext.getResources().getDimensionPixelSize(R.dimen._20sdp);
        this.isMultiBanner = isMultiBanner;
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
            if (isMultiBanner) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_list_multi, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_list, parent, false);
                return new ViewHolder(view);
            }
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ViewHolder) {


            final ViewHolder viewHolder = (ViewHolder) holder;

//            viewHolder.contentView.setMinimumHeight(width);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.contentAreaView.getLayoutParams();

            params.width = itemWidth;
            params.height = itemWidth;

            viewHolder.contentAreaView.setLayoutParams(params);
            HashMap<String, String> item = null;


            if (isMultiBanner) {
                item = hashlist.get(position);
                Logger.d("vImage", "::" + Utils.getResizeImgURL(mContext, list != null ? list.get(position) : item.get("vImage"), params.width, params.height));
//                if (generalFunc.isRTLmode()) {
//                    viewHolder.deleteArea.setBackground(ContextCompat.getDrawable(mContext, R.drawable.drawable_rounded_left_curve));
//                }
                viewHolder.deleteImgView.setOnClickListener(view -> {
                    if (mItemClickListener != null) {
                        mItemClickListener.onDeleteClick(view, position);
                    }
                });
            }

            new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(mContext, list != null ? list.get(position) : item.get("vImage"), params.width, params.height)), viewHolder.galleryImgView).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).setPicassoListener(new LoadImage.PicassoListener() {
                @Override
                public void onSuccess() {
                    if (isMultiBanner) {
                        viewHolder.deleteArea.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onError() {
                    if (isMultiBanner) {
                        viewHolder.deleteArea.setVisibility(View.VISIBLE);

                    }
                }
            }).build();


            viewHolder.galleryImgView.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(view, position);

                }
            });


        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            this.footerHolder = footerHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position) && isFooterEnabled == true) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        if (list != null) {
            return position == list.size();
        }
        if (hashlist != null) {
            return position == hashlist.size();
        }
        return false;
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (isFooterEnabled) {
            if (list != null) {
                return list.size() + 1;
            }
            if (hashlist != null) {
                return hashlist.size() + 1;
            }
        } else {
            if (list != null) {
                return list.size();
            }
            if (hashlist != null) {
                return hashlist.size();
            }
        }
        return list.size();

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
        void onItemClickList(View v, int position);

        void onDeleteClick(View v, int position);
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatImageView galleryImgView;

        public View contentView;
        public View contentAreaView;
        public AppCompatImageView deleteImgView;
        ImageView selImage;
        RelativeLayout deleteArea;


        public ViewHolder(View view) {
            super(view);
            contentView = view;
            contentAreaView = view.findViewById(R.id.contentAreaView);
            galleryImgView = (AppCompatImageView) view.findViewById(R.id.galleryImgView);
            deleteImgView = (AppCompatImageView) view.findViewById(R.id.deleteImgView);
            selImage = (ImageView) view.findViewById(R.id.selImage);
            if (isMultiBanner) {
                deleteArea = view.findViewById(R.id.deleteArea);
            }

        }


    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        LinearLayout progressContainer;

        public FooterViewHolder(View itemView) {
            super(itemView);

            progressContainer = (LinearLayout) itemView.findViewById(R.id.progressContainer);

        }
    }

    public float getScreenDPWidth() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = (displayMetrics.widthPixels - Utils.dipToPixels(mContext, 10)) / displayMetrics.density; // 10 is for recycler view left-right margin
        return dpWidth;
    }

//    public Integer getNumOfColumns() {
//        int noOfColumns = (int) (getScreenDPWidth() / 130);
//        return noOfColumns;
//    }
}
