package com.multixpro.user.nearbyservice.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceActionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final GeneralFunctions generalFunc;
    private final ArrayList<HashMap<String, String>> list;
    private final OnItemClickListener mItemClickListener;

    public ServiceActionAdapter(GeneralFunctions generalFunc, ArrayList<HashMap<String, String>> list, OnItemClickListener mItemClickListener) {
        this.generalFunc = generalFunc;
        this.list = list;
        this.mItemClickListener = mItemClickListener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_near_by_service_action, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> mapData = list.get(position);

        ListViewHolder listHolder = (ListViewHolder) holder;
        listHolder.txtSubCategoryName.setText(mapData.get("vTitle"));

        //---------
        String imageUrl = mapData.get("vImage");
        if (!Utils.checkText(imageUrl)) {
            imageUrl = "Temp";
        }
        imageUrl = Utils.getResizeImgURL(holder.itemView.getContext(), imageUrl, listHolder.imgServiceIcon.getWidth(), listHolder.imgServiceIcon.getHeight());
        new LoadImage.builder(LoadImage.bind(imageUrl), listHolder.imgServiceIcon).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();
        /////---------

        listHolder.rowArea.setOnClickListener(v -> mItemClickListener.onServiceActionClick(mapData, listHolder.imgServiceIcon.getDrawable()));

        if (position == (getItemCount() - 1)) {
            listHolder.viewLine.setVisibility(View.GONE);
        }
        if (generalFunc.isRTLmode()) {
            listHolder.imgArrow.setRotation(180);
        }
    }

    private static class ListViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout rowArea;
        private final MTextView txtSubCategoryName;
        private final ImageView imgArrow, imgServiceIcon;
        private final View viewLine;

        private ListViewHolder(View itemView) {
            super(itemView);

            rowArea = itemView.findViewById(R.id.rowArea);
            imgServiceIcon = itemView.findViewById(R.id.imgServiceIcon);
            txtSubCategoryName = itemView.findViewById(R.id.txtSubCategoryName);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            viewLine = itemView.findViewById(R.id.viewLine);
            viewLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onServiceActionClick(HashMap<String, String> mapData, Drawable drawable);
    }
}