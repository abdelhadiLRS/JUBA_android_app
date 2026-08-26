package com.adapter.files.deliverAll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.multixpro.user.R;
import com.general.files.GeneralFunctions;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class StoreCategoryAdapter extends RecyclerView.Adapter<StoreCategoryAdapter.ViewHolder> {

    Context mContext;
    ArrayList<HashMap<String, String>> list;
    GeneralFunctions generalFunc;
    CuisinesOnClickListener CuisinesOnClickListener;

    public StoreCategoryAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, CuisinesOnClickListener CuisinesOnClickListener) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.CuisinesOnClickListener = CuisinesOnClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_category, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.cuisinesTxt.setText(list.get(position).get("cuisineName"));

        if (list.get(position).get("vImage") != null) {
            String imageUrl = list.get(position).get("vImage");
            if (list.get(position).get("vImage").equalsIgnoreCase("")) {
                imageUrl = "Temp";
            }

            new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(mContext, imageUrl, holder.cuisinesImg.getWidth(), holder.cuisinesImg.getHeight())), holder.cuisinesImg).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();


        }


        holder.mainArea.setOnClickListener(view -> CuisinesOnClickListener.setOnCuisinesclick(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface CuisinesOnClickListener {
        void setOnCuisinesclick(int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        MTextView cuisinesTxt;

        ImageView cuisinesImg;
        LinearLayout mainArea;
        View selitemView;

        public ViewHolder(View itemView) {
            super(itemView);

            cuisinesTxt = itemView.findViewById(R.id.cuisinesTxt);
            cuisinesImg = itemView.findViewById(R.id.cuisinesImg);
            mainArea = itemView.findViewById(R.id.mainArea);
            selitemView = itemView.findViewById(R.id.itemView);
        }
    }
}
