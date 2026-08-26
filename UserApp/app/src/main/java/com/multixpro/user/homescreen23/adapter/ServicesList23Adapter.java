package com.multixpro.user.homescreen23.adapter;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.UberXHomeActivity;
import com.multixpro.user.databinding.ItemServicesListType1Binding;
import com.multixpro.user.databinding.ItemServicesListType2Binding;
import com.utils.LoadImage;
import com.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServicesList23Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int TYPE_1 = 0, TYPE_2 = 1;
    private final UberXHomeActivity mActivity;
    private final JSONArray mImagesArr;
    private final OnClickListener listener;

    private final boolean isRTL, isScroll, isFullView, AddTopPadding, AddBottomPadding;
    private String DisplaySize;
    int bannerWidth, bannerHeight, displayCount, itemLRMargin, itemSpacing, sWidth;

    public ServicesList23Adapter(@NonNull UberXHomeActivity activity, @NonNull JSONObject itemObject, @NonNull OnClickListener listener) {
        this.mActivity = activity;
        this.mImagesArr = mActivity.generalFunc.getJsonArray("imagesArr", itemObject);
        this.listener = listener;

        this.isRTL = activity.generalFunc.isRTLmode();

        this.itemLRMargin = mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp);
        this.itemSpacing = mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp);

        this.isScroll = mActivity.generalFunc.getJsonValueStr("isScroll", itemObject).equalsIgnoreCase("Yes");
        this.displayCount = GeneralFunctions.parseIntegerValue(1, mActivity.generalFunc.getJsonValueStr("displayCount", itemObject));
        this.isFullView = mActivity.generalFunc.getJsonValueStr("isFullView", itemObject).equalsIgnoreCase("Yes");

        this.DisplaySize = mActivity.generalFunc.getJsonValueStr("DisplaySize", itemObject);

        this.AddTopPadding = mActivity.generalFunc.getJsonValueStr("AddTopPadding", itemObject).equalsIgnoreCase("Yes");
        this.AddBottomPadding = mActivity.generalFunc.getJsonValueStr("AddBottomPadding", itemObject).equalsIgnoreCase("Yes");

        //-------------------
        this.sWidth = (int) Utils.getScreenPixelWidth(mActivity);
        sWidth = sWidth - mActivity.getResources().getDimensionPixelSize(R.dimen._30sdp);

        if (isFullView) {
            bannerWidth = sWidth;
        } else {
            if (displayCount == 1) {
                bannerWidth = (sWidth / displayCount) + (itemLRMargin * 2);
            } else if (displayCount == 2) {
                bannerWidth = (sWidth + (itemLRMargin * displayCount)) / displayCount;
            }
        }
        if (DisplaySize.equalsIgnoreCase("Big")) {
            bannerHeight = (int) (bannerWidth / 1.55);
            if (DisplaySize.equalsIgnoreCase("Big") && displayCount == 1) {
                bannerHeight = (int) (bannerWidth / 4.7);
            }
        } else if (DisplaySize.equalsIgnoreCase("Small")) {
            bannerHeight = (int) (bannerWidth / 2.5);
        } else {
            bannerHeight = bannerWidth;
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_1) {
            return new Type1ViewHolder(ItemServicesListType1Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new Type2ViewHolder(ItemServicesListType2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject mServiceObject = mActivity.generalFunc.getJsonObject(mImagesArr, position);

        int leftP = 0, centerP = 0;
        if (!isFullView && displayCount <= 2 || isScroll) {
            if (position == 0) {
                leftP = mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp);
            }
            if (position != (mImagesArr.length() - 1)) {
                centerP = itemSpacing;
            } else if (position == (mImagesArr.length() - 1)) {
                centerP = itemLRMargin;
            }
        }

        if (holder instanceof Type1ViewHolder) {

            Type1ViewHolder t1Holder = (Type1ViewHolder) holder;

            RelativeLayout.LayoutParams bannerLayoutParams = (RelativeLayout.LayoutParams) t1Holder.binding.mainArea.getLayoutParams();
            bannerLayoutParams.width = bannerWidth;
            bannerLayoutParams.height = bannerHeight;
            t1Holder.binding.mainArea.setLayoutParams(bannerLayoutParams);

            t1Holder.binding.txtCategoryName.setText(mActivity.generalFunc.getJsonValueStr("vCategoryName", mServiceObject));
            if (DisplaySize.equalsIgnoreCase("Big")) {
                t1Holder.binding.txtCategoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                t1Holder.binding.txtCategoryName.setPadding(0, mActivity.getResources().getDimensionPixelSize(R.dimen._18sdp), 0, 0);
            } else if (DisplaySize.equalsIgnoreCase("Small")) {
                t1Holder.binding.txtCategoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                t1Holder.binding.txtCategoryName.setPadding(0, mActivity.getResources().getDimensionPixelSize(R.dimen._7sdp), 0, 0);
            }

            RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) t1Holder.binding.imgView.getLayoutParams();
            if (DisplaySize.equalsIgnoreCase("Big")) {
                imgParams.width = (bannerWidth / 2);
            } else if (DisplaySize.equalsIgnoreCase("Small")) {
                imgParams.setMargins(0, mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp), 0, 0);
                imgParams.width = (int) (bannerWidth / 2.5);
            }
            t1Holder.binding.imgView.setLayoutParams(imgParams);


            String Url = mActivity.generalFunc.getJsonValueStr("vImage", mServiceObject);
            commonCode_1(t1Holder.binding.imgView, Url, t1Holder.binding.cardViewBanner, mServiceObject);

            t1Holder.binding.mainArea.setPadding(isRTL ? centerP : leftP, AddTopPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0, isRTL ? leftP : centerP, AddBottomPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0);

            ///////////////////////////////////////////////////////////////-----------------------------
            if (mActivity.generalFunc.getJsonValueStr("isClickable", mServiceObject).equalsIgnoreCase("Yes")) {
                t1Holder.binding.mainArea.setOnClickListener(v -> listener.onServicesItemClick(position, mServiceObject));
            }

        } else if (holder instanceof Type2ViewHolder) {

            Type2ViewHolder t2Holder = (Type2ViewHolder) holder;
            RelativeLayout.LayoutParams bannerLayoutParams = (RelativeLayout.LayoutParams) t2Holder.binding.mainArea.getLayoutParams();
            bannerLayoutParams.width = bannerWidth;
            bannerLayoutParams.height = bannerHeight;
            t2Holder.binding.mainArea.setLayoutParams(bannerLayoutParams);

            t2Holder.binding.txtCategoryName.setText(mActivity.generalFunc.getJsonValueStr("vCategoryName", mServiceObject));
            t2Holder.binding.txtCategoryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            t2Holder.binding.txtCategoryName.setPadding(0, mActivity.getResources().getDimensionPixelSize(R.dimen._7sdp), 0, 0);

            String Url = mActivity.generalFunc.getJsonValueStr("vImage", mServiceObject);
            commonCode_1(t2Holder.binding.imgView, Url, t2Holder.binding.cardViewBanner, mServiceObject);

            t2Holder.binding.mainArea.setPadding(isRTL ? centerP : leftP, AddTopPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0, isRTL ? leftP : centerP, AddBottomPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0);

            ///////////////////////////////////////////////////////////////-----------------------------
            if (mActivity.generalFunc.getJsonValueStr("isClickable", mServiceObject).equalsIgnoreCase("Yes")) {
                t2Holder.binding.mainArea.setOnClickListener(v -> listener.onServicesItemClick(position, mServiceObject));
            }
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (DisplaySize.equalsIgnoreCase("Big")) {
            if (DisplaySize.equalsIgnoreCase("Big") && displayCount == 1) {
                return TYPE_2;
            } else {
                return TYPE_1;
            }
        } else if (DisplaySize.equalsIgnoreCase("Small")) {
            return TYPE_1;
        }
        return position;
    }

    @Override
    public int getItemCount() {
        if (mImagesArr == null) {
            return 0;
        }
        return mImagesArr.length();
    }

    /////////////////////////////-----------------//////////////////////////////////////////////
    private static class Type1ViewHolder extends RecyclerView.ViewHolder {

        private final ItemServicesListType1Binding binding;

        private Type1ViewHolder(ItemServicesListType1Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class Type2ViewHolder extends RecyclerView.ViewHolder {

        private final ItemServicesListType2Binding binding;

        private Type2ViewHolder(ItemServicesListType2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    //..............................
    private void commonCode_1(AppCompatImageView imageView, String Url, LinearLayout cardViewBanner, JSONObject mServiceObject) {
        if (!Utils.checkText(Url)) {
            Url = "Temp";
        }
        int vImageWidth = GeneralFunctions.parseIntegerValue(0, mActivity.generalFunc.getJsonValueStr("vImageWidth", mServiceObject));
        int vImageHeight = GeneralFunctions.parseIntegerValue(0, mActivity.generalFunc.getJsonValueStr("vImageHeight", mServiceObject));

        Url = Utils.getResizeImgURL(mActivity, Url, vImageWidth, vImageHeight);
        new LoadImage.builder(LoadImage.bind(Url), imageView).build();

        ///////////////////////////////////////////////////////////////-----------------------------
        if (isFullView) {
            cardViewBanner.setBackground(null);
            cardViewBanner.setClipToOutline(false);
        } else {
            cardViewBanner.setClipToOutline(true);
            cardViewBanner.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.card_view_23_gray_flat));
        }
    }

    public interface OnClickListener {
        void onServicesItemClick(int position, JSONObject jsonObject);
    }
}