package com.multixpro.user.homescreen23.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.multixpro.user.R;
import com.multixpro.user.UberXHomeActivity;
import com.multixpro.user.databinding.Item23BannerItemBinding;
import com.multixpro.user.databinding.Item23RentEstateCarBinding;
import com.multixpro.user.databinding.Item23RentItemBinding;
import com.multixpro.user.databinding.Item23RideShareBinding;
import com.utils.LoadImage;
import com.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class Banner23Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_RENT_ESTATE_CAR = 1;
    private final int TYPE_RENT_ITEM = 2;
    private final int TYPE_RIDE_SHARE = 3;

    private final UberXHomeActivity mActivity;
    private final JSONObject mItemObject;
    private JSONArray mBannerArray;
    private final OnClickListener listener;

    private boolean isScroll, isOnlyImage, isClickable;
    private final boolean isRTL, isFullView, AddTopPadding, AddBottomPadding;

    int displayCount, itemLRMargin, itemSpacing, sWidth, minHeight;
    int bannerWidth, bannerHeight;
    int vImageWidth, vImageHeight;

    public Banner23Adapter(@NonNull UberXHomeActivity activity, boolean isDeliverAll, @NonNull JSONObject itemObject, @NonNull OnClickListener listener) {
        this.mActivity = activity;
        this.mItemObject = itemObject;
        if (itemObject.has("imagesArr")) {
            this.mBannerArray = mActivity.generalFunc.getJsonArray("imagesArr", itemObject);
        } else if (itemObject.has("BANNER_DATA")) {
            this.mBannerArray = mActivity.generalFunc.getJsonArray("BANNER_DATA", itemObject);
        }
        this.listener = listener;

        isRTL = activity.generalFunc.isRTLmode();

        this.itemLRMargin = mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp);
        this.itemSpacing = mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp);

        this.isScroll = mActivity.generalFunc.getJsonValueStr("isScroll", itemObject).equalsIgnoreCase("Yes");
        this.isFullView = mActivity.generalFunc.getJsonValueStr("isFullView", itemObject).equalsIgnoreCase("Yes");
        this.isOnlyImage = mActivity.generalFunc.getJsonValueStr("isOnlyImage", itemObject).equalsIgnoreCase("Yes");
        this.isClickable = mActivity.generalFunc.getJsonValueStr("isClickable", itemObject).equalsIgnoreCase("Yes");
        this.displayCount = GeneralFunctions.parseIntegerValue(1, mActivity.generalFunc.getJsonValueStr("displayCount", itemObject));

        this.AddTopPadding = mActivity.generalFunc.getJsonValueStr("AddTopPadding", itemObject).equalsIgnoreCase("Yes");
        this.AddBottomPadding = mActivity.generalFunc.getJsonValueStr("AddBottomPadding", itemObject).equalsIgnoreCase("Yes");

        this.vImageWidth = GeneralFunctions.parseIntegerValue(0, mActivity.generalFunc.getJsonValueStr("vImageWidth", itemObject));
        this.vImageHeight = GeneralFunctions.parseIntegerValue(0, mActivity.generalFunc.getJsonValueStr("vImageHeight", itemObject));

        double ratio = GeneralFunctions.parseDoubleValue(0.0, String.valueOf(vImageWidth)) / GeneralFunctions.parseDoubleValue(0.0, String.valueOf(vImageHeight));

        this.sWidth = (int) Utils.getScreenPixelWidth(mActivity);
        this.minHeight = (sWidth / 2) - mActivity.getResources().getDimensionPixelSize(R.dimen._40sdp);

        if (isDeliverAll) {
            isScroll = true;
            isOnlyImage = true;
            isClickable = true;
            ratio = 2.33;
        }

        setImageRatio(ratio);
    }

    private void setImageRatio(double ratio) {
        if (isFullView) {
            bannerWidth = sWidth;
        } else {
            if (isScroll) {
                if (mBannerArray != null && mBannerArray.length() == 1) {
                    bannerWidth = sWidth - (itemLRMargin * 2);
                } else {
                    bannerWidth = sWidth - mActivity.getResources().getDimensionPixelSize(R.dimen._50sdp);
                }
            } else {
                if (displayCount == 1) {
                    bannerWidth = (sWidth / displayCount) - (itemLRMargin * 2);
                } else if (displayCount == 2) {
                    bannerWidth = (sWidth - (itemLRMargin * 2) - itemSpacing) / displayCount;
                }
            }
        }
        bannerHeight = (int) (bannerWidth / ratio);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_RENT_ESTATE_CAR) {
            return new EStateCarViewHolder(Item23RentEstateCarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_RENT_ITEM) {
            return new RentItemViewHolder(Item23RentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_RIDE_SHARE) {
            return new RideShareViewHolder(Item23RideShareBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new NormalViewHolder(Item23BannerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject mBannerObject = mActivity.generalFunc.getJsonObject(mBannerArray, position);

        if (holder instanceof NormalViewHolder) {
            NormalViewHolder bHolder = (NormalViewHolder) holder;

            RelativeLayout.LayoutParams bannerLayoutParams = (RelativeLayout.LayoutParams) bHolder.binding.bannerImgView.getLayoutParams();
            bannerLayoutParams.width = bannerWidth;
            bannerLayoutParams.height = bannerHeight;
            bHolder.binding.bannerImgView.setLayoutParams(bannerLayoutParams);

            String Url = mActivity.generalFunc.getJsonValueStr("vImage", mBannerObject);
            commonCode_2(bHolder.binding.bannerImgView, Url, true, mActivity.getResources().getDimensionPixelSize(R.dimen._6sdp));

            if (mActivity.generalFunc.getJsonValueStr("isClickable", mBannerObject).equalsIgnoreCase("Yes")) {
                bHolder.binding.mainArea.setOnClickListener(v -> listener.onBannerItemClick(position, mBannerObject));
            }

            int leftP = 0, centerP = 0;
            if (!isFullView && isOnlyImage && displayCount <= 2 || isScroll) {
                if (position == 0) {
                    leftP = mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp);
                    centerP = isRTL ? mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp) : 0;
                }
                if (position != (mBannerArray.length() - 1)) {
                    centerP = itemSpacing;
                } else if (position == (mBannerArray.length() - 1)) {
                    centerP = isRTL ? 0 : itemLRMargin;
                }
            }
            bHolder.binding.mainArea.setPadding(isRTL ? centerP : leftP, AddTopPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0, isRTL ? leftP : centerP, AddBottomPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0);

        } else if (holder instanceof EStateCarViewHolder) {
            EStateCarViewHolder estateHolder = (EStateCarViewHolder) holder;

            estateHolder.binding.txtCategoryName.setText(mActivity.generalFunc.getJsonValueStr("vCategoryName", mBannerObject));
            String vTextColor = mActivity.generalFunc.getJsonValueStr("vTextColor", mBannerObject);
            if (Utils.checkText(vTextColor)) {
                estateHolder.binding.txtCategoryName.setTextColor(Color.parseColor(vTextColor));
            }
            String vBgColor = mActivity.generalFunc.getJsonValueStr("vBgColor", mBannerObject);
            if (Utils.checkText(vBgColor)) {
                estateHolder.binding.cardViewBanner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(vBgColor)));
            }

            String promotionalTagText = mActivity.generalFunc.getJsonValueStr("PromotionalTagText", mBannerObject);
            if (Utils.checkText(promotionalTagText)) {
                estateHolder.binding.promoTagView.setVisibility(View.VISIBLE);
                estateHolder.binding.txtPromotionalTag.setText(promotionalTagText);
            } else {
                estateHolder.binding.promoTagView.setVisibility(View.GONE);
            }

            RelativeLayout.LayoutParams bannerLayoutParams = (RelativeLayout.LayoutParams) estateHolder.binding.cardViewBanner.getLayoutParams();
            bannerLayoutParams.width = bannerWidth;
            bannerLayoutParams.height = minHeight;
            estateHolder.binding.cardViewBanner.setLayoutParams(bannerLayoutParams);

            String Url = mActivity.generalFunc.getJsonValueStr("vImage", mBannerObject);
            commonCode_1(estateHolder.binding.bannerImgView, Url, estateHolder.binding.cardViewBanner, false);
            if (isRTL) {
                estateHolder.binding.bannerImgView.setRotationY(180);
            }

            int leftP = 0, centerP = 0;
            if (!isFullView && displayCount <= 2 || isScroll) {
                if (position == 0) {
                    leftP = mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp);
                    centerP = isRTL ? mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp) : 0;
                }
                if (position != (mBannerArray.length() - 1)) {
                    centerP = itemSpacing;
                } else if (position == (mBannerArray.length() - 1)) {
                    centerP = isRTL ? 0 : itemLRMargin;
                }
            }
            estateHolder.binding.mainArea.setPadding(isRTL ? centerP : leftP, AddTopPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0, isRTL ? leftP : centerP, AddBottomPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0);
            if (isClickable) {
                estateHolder.binding.cardViewBanner.setOnClickListener(v -> listener.onBannerItemClick(position, mBannerObject));
            }

        } else if (holder instanceof RentItemViewHolder) {
            RentItemViewHolder rentItemHolder = (RentItemViewHolder) holder;

            rentItemHolder.binding.txtCategoryName.setText(mActivity.generalFunc.getJsonValueStr("vCategoryName", mBannerObject));
            String vTextColor = mActivity.generalFunc.getJsonValueStr("vTextColor", mBannerObject);
            if (Utils.checkText(vTextColor)) {
                rentItemHolder.binding.txtCategoryName.setTextColor(Color.parseColor(vTextColor));
            }
            String vBgColor = mActivity.generalFunc.getJsonValueStr("vBgColor", mBannerObject);
            if (Utils.checkText(vBgColor)) {
                rentItemHolder.binding.cardViewBanner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(vBgColor)));
            }

            String promotionalTagText = mActivity.generalFunc.getJsonValueStr("PromotionalTagText", mBannerObject);
            if (Utils.checkText(promotionalTagText)) {
                rentItemHolder.binding.promoTagView.setVisibility(View.VISIBLE);
                rentItemHolder.binding.txtPromotionalTag.setText(promotionalTagText);
            } else {
                rentItemHolder.binding.promoTagView.setVisibility(View.GONE);
            }

            RelativeLayout.LayoutParams bannerLayoutParams = (RelativeLayout.LayoutParams) rentItemHolder.binding.cardViewBanner.getLayoutParams();
            bannerLayoutParams.width = bannerWidth;
            bannerLayoutParams.height = minHeight;
            rentItemHolder.binding.cardViewBanner.setLayoutParams(bannerLayoutParams);

            String Url = mActivity.generalFunc.getJsonValueStr("vImage", mBannerObject);
            commonCode_1(rentItemHolder.binding.bannerImgView, Url, rentItemHolder.binding.cardViewBanner, false);
            if (isRTL) {
                rentItemHolder.binding.bannerImgView.setRotationY(180);
            }

            int leftP = 0, centerP = 0;
            if (!isFullView && displayCount <= 2 || isScroll) {
                if (position == 0) {
                    leftP = mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp);
                }
                if (position != (mBannerArray.length() - 1)) {
                    centerP = itemSpacing;
                } else if (position == (mBannerArray.length() - 1)) {
                    centerP = itemLRMargin;
                }
            }
            rentItemHolder.binding.mainArea.setPadding(isRTL ? centerP : leftP, AddTopPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0, isRTL ? leftP : centerP, AddBottomPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0);
            if (isClickable) {
                rentItemHolder.binding.cardViewBanner.setOnClickListener(v -> listener.onBannerItemClick(position, mBannerObject));
            }

        } else if (holder instanceof RideShareViewHolder) {
            RideShareViewHolder rsHolder = (RideShareViewHolder) holder;

            rsHolder.binding.txtTitle.setText(mActivity.generalFunc.getJsonValueStr("vCategoryTitle", mBannerObject));
            rsHolder.binding.txtSubTitle.setText(mActivity.generalFunc.getJsonValueStr("vCategoryDesc", mBannerObject));
            rsHolder.binding.txtBookNow.setText(mActivity.generalFunc.getJsonValueStr("BookBtnText", mItemObject));

            String vBgColor = mActivity.generalFunc.getJsonValueStr("vBgColor", mBannerObject);
            if (Utils.checkText(vBgColor)) {
                rsHolder.binding.cardViewBanner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(vBgColor)));
            }

            LinearLayout.LayoutParams bannerLayoutParams = (LinearLayout.LayoutParams) rsHolder.binding.bannerImgView.getLayoutParams();
            bannerLayoutParams.width = bannerWidth;
            bannerLayoutParams.height = bannerHeight;
            rsHolder.binding.bannerImgView.setLayoutParams(bannerLayoutParams);

            String Url = mActivity.generalFunc.getJsonValueStr("vImage", mBannerObject);
            commonCode_1(rsHolder.binding.bannerImgView, Url, rsHolder.binding.cardViewBanner, false);

            if (isClickable) {
                rsHolder.binding.cardViewBanner.setOnClickListener(v -> listener.onBannerItemClick(position, mBannerObject));
            }
            rsHolder.binding.mainArea.setPadding(0, AddTopPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0, 0, AddBottomPadding ? mActivity.getResources().getDimensionPixelSize(R.dimen._11sdp) : 0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject itemObject = mActivity.generalFunc.getJsonObject(mBannerArray, position);
        if (itemObject == null) {
            return 0;
        }

        if (itemObject.has("rentitemSection") || itemObject.has("rentestateSection") || itemObject.has("rentcarSection")) {

            if (displayCount == 2) {
                return TYPE_RENT_ESTATE_CAR;
            } else {
                return TYPE_RENT_ITEM;
            }

        } else if (itemObject.has("RideShareSection") && !isOnlyImage) {
            return TYPE_RIDE_SHARE;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        if (mBannerArray == null) {
            return 0;
        }
        return mBannerArray.length();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(JSONObject mResponseObject) {
        if (mResponseObject.has("BANNER_DATA")) {
            this.mBannerArray = mActivity.generalFunc.getJsonArray("BANNER_DATA", mResponseObject);
            this.bannerWidth = sWidth - itemLRMargin;
            this.bannerHeight = (int) ((int) sWidth / 2.33);
        }
        notifyDataSetChanged();
    }
    /////////////////////////////-----------------//////////////////////////////////////////////

    private static class EStateCarViewHolder extends RecyclerView.ViewHolder {

        private final Item23RentEstateCarBinding binding;

        private EStateCarViewHolder(Item23RentEstateCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class RentItemViewHolder extends RecyclerView.ViewHolder {

        private final Item23RentItemBinding binding;

        private RentItemViewHolder(Item23RentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class RideShareViewHolder extends RecyclerView.ViewHolder {

        private final Item23RideShareBinding binding;

        private RideShareViewHolder(Item23RideShareBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {

        private final Item23BannerItemBinding binding;

        private NormalViewHolder(Item23BannerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    //..............................
    private void commonCode_1(AppCompatImageView holder, String Url, LinearLayout cardViewBanner, boolean isResize) {
        if (!Utils.checkText(Url)) {
            Url = "Temp";
        }
        if (isResize) {
            Url = Utils.getResizeImgURL(mActivity, Url, bannerWidth, bannerHeight);
        }
        new LoadImage.builder(LoadImage.bind(Url), holder).build();

        ///////////////////////////////////////////////////////////////-----------------------------
        if (isFullView) {
            cardViewBanner.setBackground(null);
            cardViewBanner.setClipToOutline(false);
        } else {
            cardViewBanner.setClipToOutline(true);
            cardViewBanner.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.card_view_23_gray_flat));
        }
    }

    private void commonCode_2(ShapeableImageView mImgView, String Url, boolean isResize, int allCorners) {
        if (!Utils.checkText(Url)) {
            Url = "Temp";
        }
        if (isResize) {
            Url = Utils.getResizeImgURL(mActivity, Url, bannerWidth, bannerHeight);
        }
        new LoadImage.builder(LoadImage.bind(Url), mImgView).build();

        ///////////////////////////////////////////////////////////////-----------------------------
        if (isFullView) {
            allCorners = 0;
        }
        mImgView.setShapeAppearanceModel(mImgView.getShapeAppearanceModel()
                .toBuilder().setAllCorners(CornerFamily.ROUNDED, allCorners).build());
    }

    public interface OnClickListener {
        void onBannerItemClick(int position, JSONObject jsonObject);
    }
}