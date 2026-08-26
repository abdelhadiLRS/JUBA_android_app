package com.multixpro.user.homescreen23.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.multixpro.user.UberXHomeActivity;
import com.multixpro.user.databinding.Item23ServiceBannerListBinding;
import com.multixpro.user.databinding.ItemServicesTitleViewBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_TITLE = 0;
    private final int TYPE_BANNER = 1;

    private final UberXHomeActivity mActivity;
    private JSONArray mainArray;
    private final OnClickListener listener;

    public ServicesAdapter(@NonNull UberXHomeActivity activity, @NonNull JSONArray list, @NonNull OnClickListener listener) {
        this.mActivity = activity;
        this.mainArray = list;
        this.listener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BANNER) {
            return new ServiceBannerViewHolder(Item23ServiceBannerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new TitleViewHolder(ItemServicesTitleViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JSONObject itemObject = mActivity.generalFunc.getJsonObject(mainArray, position);

        if (holder instanceof ServiceBannerViewHolder) {

            ServiceBannerViewHolder sbHolder = (ServiceBannerViewHolder) holder;
            ServicesList23Adapter mAdapter = new ServicesList23Adapter(mActivity, itemObject, listener::onServiceBannerItemClick);
            sbHolder.binding.rvBanner.setAdapter(mAdapter);

        } else if (holder instanceof TitleViewHolder) {

            TitleViewHolder titleHolder = (TitleViewHolder) holder;
            titleHolder.binding.titleTxtView.setText(mActivity.generalFunc.getJsonValueStr("vTitle", itemObject));
        }

    }

    @Override
    public int getItemViewType(int position) {
        JSONObject itemObject = mActivity.generalFunc.getJsonObject(mainArray, position);
        String eShowType = mActivity.generalFunc.getJsonValueStr("eViewType", itemObject);
        if (eShowType != null && eShowType.equalsIgnoreCase("BannerView")) {
            return TYPE_BANNER;
        } else {
            return TYPE_TITLE;
        }
    }

    @Override
    public int getItemCount() {
        if (mainArray == null) {
            return 0;
        }
        return mainArray.length();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(JSONArray homeScreenDataArray) {
        this.mainArray = homeScreenDataArray;
        notifyDataSetChanged();
    }

    /////////////////////////////-----------------//////////////////////////////////////////////
    private static class ServiceBannerViewHolder extends RecyclerView.ViewHolder {

        private final Item23ServiceBannerListBinding binding;

        private ServiceBannerViewHolder(Item23ServiceBannerListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {

        private final ItemServicesTitleViewBinding binding;

        private TitleViewHolder(ItemServicesTitleViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {

        void onServiceBannerItemClick(int position, JSONObject jsonObject);

        void onSeeAllClick(int position, JSONObject itemObject);
    }
}