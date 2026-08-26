package com.adapter.files.deliverAll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.multixpro.user.R;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CuisinesAdapter extends RecyclerView.Adapter<CuisinesAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<HashMap<String, String>> list;
    private final CuisinesOnClickListener CuisinesOnClickListener;

    public CuisinesAdapter(Context mContext, ArrayList<HashMap<String, String>> list, CuisinesOnClickListener CuisinesOnClickListener) {
        this.mContext = mContext;
        this.list = list;
        this.CuisinesOnClickListener = CuisinesOnClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuisines_new, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.cuisinesTxt.setText(list.get(position).get("cuisineName"));

        String imageUrl = list.get(position).get("vImage");
        if (!Utils.checkText(imageUrl)) {
            imageUrl = "Temp";
        }
        new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(mContext, imageUrl, holder.cuisinesImg.getWidth(), holder.cuisinesImg.getHeight())), holder.cuisinesImg).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();

        // holder.mainArea.setBackgroundColor(Color.parseColor(list.get(position).get("vBgColor")));
        //holder.mainArea.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(list.get(position).get("vBgColor"))));
//        holder.cuisinesTxt.setTextColor(Color.parseColor(list.get(position).get("vTextColor")));

        if (list.get(position).containsKey("isCheck")) {
            if (Objects.requireNonNull(list.get(position).get("isCheck")).equalsIgnoreCase("Yes")) {
                holder.selitemView.setVisibility(View.GONE);
//                GradientDrawable drawable = (GradientDrawable) holder.mainArea.getBackground();
//                drawable.setStroke(Utils.dipToPixels(mContext, 2), Color.parseColor(list.get(position).get("vBorderColor")));
//                drawable.setColor(Color.parseColor(list.get(position).get("vBgColor")));
                holder.cuisinesTxt.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.mainArea.setBackground(mContext.getResources().getDrawable(R.drawable.card_view_23_food_cuisin_bg_food_theme));
//                drawable.setColor(mContext.getResources().getColor(R.color.foodThemeColor));

            } else {
                holder.selitemView.setVisibility(View.GONE);
                holder.cuisinesTxt.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.mainArea.setBackground(mContext.getResources().getDrawable(R.drawable.card_view_23_food_cuisin_bg_gray));
//                GradientDrawable drawable = (GradientDrawable) holder.mainArea.getBackground();
//                drawable.setStroke(Utils.dipToPixels(mContext, 2), Color.parseColor(list.get(position).get("vBgColor")));
//                drawable.setColor(Color.parseColor(list.get(position).get("vBgColor")));
            }
        } else {
            holder.selitemView.setVisibility(View.GONE);
            holder.cuisinesTxt.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.mainArea.setBackground(mContext.getResources().getDrawable(R.drawable.card_view_23_food_cuisin_bg_gray));
//            GradientDrawable drawable = (GradientDrawable) holder.mainArea.getBackground();
//            drawable.setStroke(Utils.dipToPixels(mContext, 2), Color.parseColor(list.get(position).get("vBgColor")));
//            drawable.setColor(Color.parseColor(list.get(position).get("vBgColor")));
        }

        holder.mainArea.setOnClickListener(view -> CuisinesOnClickListener.setOnCuisinesClick(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface CuisinesOnClickListener {
        void setOnCuisinesClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MTextView cuisinesTxt;
        private final ImageView cuisinesImg;
        private final LinearLayout mainArea;
        private final View selitemView;

        public ViewHolder(View itemView) {
            super(itemView);

            cuisinesTxt = itemView.findViewById(R.id.cuisinesTxt);
            cuisinesImg = itemView.findViewById(R.id.cuisinesImg);
            mainArea = itemView.findViewById(R.id.mainArea);
            selitemView = itemView.findViewById(R.id.itemView);
        }
    }
}