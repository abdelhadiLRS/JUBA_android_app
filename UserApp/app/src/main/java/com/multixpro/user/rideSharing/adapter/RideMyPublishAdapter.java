package com.multixpro.user.rideSharing.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.databinding.ItemRideMyListBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class RideMyPublishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final GeneralFunctions generalFunc;
    private final ArrayList<HashMap<String, String>> list;
    private final OnClickListener onClickListener;

    public RideMyPublishAdapter(GeneralFunctions generalFunc, @NonNull ArrayList<HashMap<String, String>> list, @NonNull OnClickListener listener) {
        this.generalFunc = generalFunc;
        this.list = list;
        this.onClickListener = listener;
    }

    @NotNull
    @Override
    public DataViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int i) {
        return new DataViewHolder(ItemRideMyListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DataViewHolder viewHolder = (DataViewHolder) holder;
        HashMap<String, String> mapData = list.get(position);

        viewHolder.binding.bookNoTxt.setText(mapData.get("vPublishedRideNoTxt"));

        viewHolder.binding.startCityTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DETAILS_START_LOC_TXT"));
        viewHolder.binding.startAddressTxt.setText(mapData.get("tStartLocation"));
        viewHolder.binding.startTimeTxt.setText(mapData.get("StartTime"));

        viewHolder.binding.endCityTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DETAILS_END_LOC_TXT"));
        viewHolder.binding.endAddressTxt.setText(mapData.get("tEndLocation"));
        viewHolder.binding.endTimeTxt.setText(mapData.get("EndTime"));

        viewHolder.binding.dateTxt.setText(mapData.get("StartDate"));
        viewHolder.binding.priceTxt.setText(mapData.get("fPrice"));
        viewHolder.binding.priceMsgTxt.setText(mapData.get("PriceLabel"));

        viewHolder.binding.itemArea.setOnClickListener(v -> onClickListener.onItemClick(position, mapData));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class DataViewHolder extends RecyclerView.ViewHolder {

        private final ItemRideMyListBinding binding;

        private DataViewHolder(@NonNull ItemRideMyListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onItemClick(int position, HashMap<String, String> mapData);
    }
}