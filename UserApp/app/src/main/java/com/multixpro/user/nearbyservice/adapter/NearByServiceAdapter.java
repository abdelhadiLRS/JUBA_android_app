package com.multixpro.user.nearbyservice.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ItemNearByServicesBinding;
import com.utils.LoadImage;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class NearByServiceAdapter extends RecyclerView.Adapter<NearByServiceAdapter.DataViewHolder> {

    private final GeneralFunctions generalFunc;
    private final OnClickListener onClickListener;
    private final ArrayList<HashMap<String, String>> list;

    public NearByServiceAdapter(@NonNull GeneralFunctions generalFunc, @NonNull ArrayList<HashMap<String, String>> list, @NonNull OnClickListener listener) {
        this.list = list;
        this.generalFunc = generalFunc;
        this.onClickListener = listener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new DataViewHolder(ItemNearByServicesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        HashMap<String, String> mapData = list.get(position);

        holder.binding.restaurantNameTxt.setText(mapData.get("vTitle"));
        holder.binding.placesLocationTXT.setText(mapData.get("vAddress"));
        holder.binding.deliveryTimeTxt.setText(mapData.get("duration"));

        holder.binding.statusMessageTxt.setText(mapData.get("statusMessage"));
        if (Utils.checkText(mapData.get("placesStatus")) && Objects.requireNonNull(mapData.get("placesStatus")).equalsIgnoreCase("Open")) {
            holder.binding.statusMessageTxt.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Green));
        } else {
            holder.binding.statusMessageTxt.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Red));
        }

        JSONArray imagesArr = generalFunc.getJsonArray(mapData.get("vImages"));
        String imageUrl = null;
        if (imagesArr != null && imagesArr.length() > 0) {
            imageUrl = (String) generalFunc.getValueFromJsonArr(imagesArr, 0);
        }
        if (!Utils.checkText(imageUrl)) {
            imageUrl = "Temp";
        }
        imageUrl = Utils.getResizeImgURL(holder.itemView.getContext(), imageUrl, holder.binding.ivNearByService.getWidth(), holder.binding.ivNearByService.getHeight());
        new LoadImage.builder(LoadImage.bind(imageUrl), holder.binding.ivNearByService).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();

        holder.binding.nearByItemArea.setOnClickListener(v -> onClickListener.onItemClick(position, mapData));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class DataViewHolder extends RecyclerView.ViewHolder {

        private final ItemNearByServicesBinding binding;

        private DataViewHolder(ItemNearByServicesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onItemClick(int position, HashMap<String, String> mapData);
    }
}