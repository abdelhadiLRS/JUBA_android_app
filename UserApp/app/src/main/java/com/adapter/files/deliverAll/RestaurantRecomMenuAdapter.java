package com.adapter.files.deliverAll;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ItemResmenuGridviewBinding;
import com.utils.LoadImage;
import com.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RestaurantRecomMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final GeneralFunctions generalFunc;
    private final OnItemClickListener mItemClickListener;
    private final ArrayList<HashMap<String, String>> list;
    private final int grayColor, imageBackColor;

    public RestaurantRecomMenuAdapter(Context mContext, GeneralFunctions generalFunc, ArrayList<HashMap<String, String>> list, OnItemClickListener mItemClickListener) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.list = list;
        this.mItemClickListener = mItemClickListener;

        this.grayColor = mContext.getResources().getColor(R.color.gray);
        this.imageBackColor = mContext.getResources().getColor(R.color.appThemeColor_1);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridViewHolder(ItemResmenuGridviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> mapData = list.get(position);

        GridViewHolder grideholder = (GridViewHolder) holder;
        grideholder.binding.title.setText(mapData.get("vItemType"));
        grideholder.binding.title.setSelected(true);

        String eFoodType = mapData.get("eFoodType");

        if (Objects.requireNonNull(eFoodType).equalsIgnoreCase("NonVeg")) {
            grideholder.binding.nonVegImage.setVisibility(View.VISIBLE);
            grideholder.binding.vegImage.setVisibility(View.GONE);
        } else if (eFoodType.equals("Veg")) {
            grideholder.binding.nonVegImage.setVisibility(View.GONE);
            grideholder.binding.vegImage.setVisibility(View.VISIBLE);
        }

        if (Objects.requireNonNull(mapData.get("prescription_required")).equalsIgnoreCase("Yes")) {
            grideholder.binding.presImage.setVisibility(View.VISIBLE);
        } else {
            grideholder.binding.presImage.setVisibility(View.GONE);
        }

        if (Objects.requireNonNull(mapData.get("fOfferAmtNotZero")).equalsIgnoreCase("Yes")) {
            grideholder.binding.price.setText(mapData.get("StrikeoutPriceConverted"));
            grideholder.binding.price.setPaintFlags(grideholder.binding.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            grideholder.binding.price.setTextColor(grayColor);
            grideholder.binding.offerPrice.setText(mapData.get("fDiscountPricewithsymbolConverted"));
            grideholder.binding.offerPrice.setVisibility(View.VISIBLE);
        } else {
            grideholder.binding.price.setVisibility(View.INVISIBLE);
            grideholder.binding.offerPrice.setText(mapData.get("StrikeoutPriceConverted"));
            grideholder.binding.offerPrice.setVisibility(View.VISIBLE);
        }
        String img_url;

        boolean isBlank = Objects.requireNonNull(mapData.get("vImage")).equalsIgnoreCase("https");
        if (!isBlank) {
            img_url = Utils.getResizeImgURL(mContext, Objects.requireNonNull(mapData.get("vImage")), mContext.getResources().getDimensionPixelSize(R.dimen._225sdp), mContext.getResources().getDimensionPixelSize(R.dimen._100sdp));
        } else {
            img_url = Utils.getResizeImgURL(mContext, Objects.requireNonNull(mapData.get("vImageResized")), mContext.getResources().getDimensionPixelSize(R.dimen._225sdp), mContext.getResources().getDimensionPixelSize(R.dimen._100sdp));
        }

        new LoadImage.builder(LoadImage.bind(img_url), grideholder.binding.menuImage).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();


        grideholder.binding.addBtn.setText(mapData.get("LBL_ADD"));
        grideholder.binding.addBtn.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onRecomItemClickList(grideholder.binding.addBtn, position, false);
            }
        });

        grideholder.binding.menuImage.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onRecomItemClickList(grideholder.binding.menuImage, position, true);
            }
        });

        if (generalFunc.isRTLmode()) {
            grideholder.binding.tagImage.setRotation(180);
            grideholder.binding.tagTxt.setPadding(10, 15, 0, 0);
        }
        String vHighlightName = mapData.get("vHighlightName");
        if (vHighlightName != null && !vHighlightName.equals("")) {
            grideholder.binding.tagImage.setVisibility(View.VISIBLE);
            grideholder.binding.tagTxt.setVisibility(View.VISIBLE);
            grideholder.binding.tagTxt.setText(vHighlightName);
        } else {
            grideholder.binding.tagImage.setVisibility(View.GONE);
            grideholder.binding.tagTxt.setVisibility(View.GONE);
        }
        grideholder.binding.vCategoryNameTxt.setText(mapData.get("vCategoryName"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class GridViewHolder extends RecyclerView.ViewHolder {

        private final ItemResmenuGridviewBinding binding;

        private GridViewHolder(ItemResmenuGridviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onRecomItemClickList(View v, int position, boolean openGrid);
    }
}