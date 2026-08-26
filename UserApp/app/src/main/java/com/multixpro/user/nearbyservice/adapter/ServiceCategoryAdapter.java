package com.multixpro.user.nearbyservice.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.multixpro.user.R;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ServiceCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnItemClickListener mItemClickListener;
    private final ArrayList<HashMap<String, String>> list;
    private int selCatPos = -1;

    public ServiceCategoryAdapter(ArrayList<HashMap<String, String>> list, OnItemClickListener mItemClickListener) {
        this.list = list;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_near_by_service_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HashMap<String, String> mapData = list.get(position);

        GridViewHolder grideholder = (GridViewHolder) holder;

        grideholder.nameTxt.setVisibility(View.GONE);
        grideholder.SelnameTxt.setVisibility(View.GONE);

        if (mapData.containsKey("isSelect")) {
            if (Objects.requireNonNull(mapData.get("isSelect")).equalsIgnoreCase("Yes")) {
                selCatPos = position;
            }
        }

        if (position == selCatPos) {
            grideholder.nameTxt.setVisibility(View.GONE);
            grideholder.SelnameTxt.setVisibility(View.VISIBLE);
        } else {
            grideholder.nameTxt.setVisibility(View.VISIBLE);
            grideholder.SelnameTxt.setVisibility(View.GONE);
        }
        grideholder.nameTxt.setText(mapData.get("vTitle"));
        grideholder.SelnameTxt.setText(mapData.get("vTitle"));

        grideholder.nameTxt.setOnClickListener(v -> {
            if (mItemClickListener != null && selCatPos != position) {
                mItemClickListener.onItemClickList(position, selCatPos);
                selCatPos = position;
            }
        });
        grideholder.SelnameTxt.setOnClickListener(v -> {
            if (mItemClickListener != null && selCatPos != position) {
                mItemClickListener.onItemClickList(position, selCatPos);
                selCatPos = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class GridViewHolder extends RecyclerView.ViewHolder {

        private final MTextView nameTxt, SelnameTxt;

        public GridViewHolder(View view) {
            super(view);
            nameTxt = view.findViewById(R.id.nameTxt);
            SelnameTxt = view.findViewById(R.id.SelnameTxt);
        }
    }

    public interface OnItemClickListener {
        void onItemClickList(int position, int selCatPos);
    }
}