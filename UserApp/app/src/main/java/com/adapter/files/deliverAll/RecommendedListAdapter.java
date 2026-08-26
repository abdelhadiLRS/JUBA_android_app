package com.adapter.files.deliverAll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.RecommendedItemListDesignBinding;
import com.utils.LoadImage;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RecommendedListAdapter extends RecyclerView.Adapter<RecommendedListAdapter.DataViewHolder> {

    private final Context mContext;
    private final GeneralFunctions generalFunc;
    private final OnClickListener onClickListener;
    private final ArrayList<HashMap<String, String>> list;

    public RecommendedListAdapter(ArrayList<HashMap<String, String>> list, Context mContext, GeneralFunctions generalFunc, OnClickListener listener) {
        this.list = list;
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.onClickListener = listener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new DataViewHolder(RecommendedItemListDesignBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        HashMap<String, String> mapData = list.get(position);

        holder.binding.title.setText(mapData.get("vItemType"));

        String vItemDesc = mapData.get("vItemDesc");
        if (Utils.checkText(vItemDesc)) {
            Spanned descdata = GeneralFunctions.fromHtml(vItemDesc);
            holder.binding.desc.setText(GeneralFunctions.fromHtml(descdata + ""));
            holder.binding.desc.setVisibility(View.VISIBLE);
            generalFunc.makeTextViewResizable(holder.binding.desc, 3, "...\n+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, R.color.appThemeColor_1, R.dimen.txt_size_10);
        } else {
            holder.binding.desc.setVisibility(View.GONE);
        }

        int parentWidth = generalFunc.getViewHeightWidth(holder.binding.parent, false);
        String vImage = Utils.getResizeImgURL(mContext, Objects.requireNonNull(mapData.get("vImage")), parentWidth, 0, 0);
        boolean isBlank = Objects.requireNonNull(mapData.get("vImage")).equalsIgnoreCase("https");
        new LoadImage.builder(LoadImage.bind(isBlank ? mapData.get("vImage") : vImage), holder.binding.itemImageView).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.binding.itemImageView.getLayoutParams();
        params.width = parentWidth;
        holder.binding.itemImageView.requestLayout();

        String eFoodType = mapData.get("eFoodType");

        if (eFoodType != null) {
            if (eFoodType.equalsIgnoreCase("NonVeg")) {
                holder.binding.nonVegImage.setVisibility(View.VISIBLE);
                holder.binding.vegImage.setVisibility(View.GONE);
            } else if (eFoodType.equals("Veg")) {
                holder.binding.nonVegImage.setVisibility(View.GONE);
                holder.binding.vegImage.setVisibility(View.VISIBLE);
            }
        }

        if (Objects.requireNonNull(mapData.get("prescription_required")).equalsIgnoreCase("Yes")) {
            holder.binding.presImage.setVisibility(View.VISIBLE);
        } else {
            holder.binding.presImage.setVisibility(View.GONE);
        }

        if (Objects.requireNonNull(mapData.get("fOfferAmtNotZero")).equalsIgnoreCase("Yes")) {
            holder.binding.price.setText(mapData.get("StrikeoutPriceConverted"));
            holder.binding.price.setPaintFlags(holder.binding.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.price.setTextColor(mContext.getResources().getColor(R.color.gray));
            holder.binding.offerPrice.setText(mapData.get("fDiscountPricewithsymbolConverted"));
            holder.binding.offerPrice.setVisibility(View.VISIBLE);
        } else {
            holder.binding.price.setText(mapData.get("StrikeoutPriceConverted"));
            holder.binding.price.setPaintFlags(0);
            holder.binding.offerPrice.setVisibility(View.GONE);
        }

        String vHighlightName = mapData.get("vHighlightName");
        if (vHighlightName != null && !vHighlightName.equals("")) {
            holder.binding.tagArea.setVisibility(View.VISIBLE);
            holder.binding.tagTxt.setText(vHighlightName);
        } else {
            holder.binding.tagArea.setVisibility(View.GONE);
        }

        holder.binding.addBtn.setText(mapData.get("LBL_ADD"));
        holder.binding.addBtn.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onItemClick(holder.binding.addBtn, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class DataViewHolder extends RecyclerView.ViewHolder {

        private final RecommendedItemListDesignBinding binding;

        private DataViewHolder(RecommendedItemListDesignBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);
    }
}