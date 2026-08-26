package com.multixpro.user.homescreen23.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.Item23MoreServicesBinding;
import com.utils.LoadImage;
import com.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class MoreService23Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final GeneralFunctions generalFunc;
    private JSONArray mServicesArr;
    private final OnClickListener listener;

    private final int dynamicHeight, dynamicWidth;

    public MoreService23Adapter(@NonNull Context context, @NonNull GeneralFunctions generalFunctions, @NonNull JSONArray servicesArr, @NonNull OnClickListener listener) {
        this.mContext = context;
        this.generalFunc = generalFunctions;
        this.mServicesArr = servicesArr;
        this.listener = listener;

        this.dynamicHeight = mContext.getResources().getDimensionPixelSize(R.dimen._60sdp);
        this.dynamicWidth = mContext.getResources().getDimensionPixelSize(R.dimen._60sdp);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(Item23MoreServicesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder bHolder = (ViewHolder) holder;

        JSONObject mServiceObject = generalFunc.getJsonObject(mServicesArr, position);

        bHolder.binding.catNameTxt.setText(generalFunc.getJsonValueStr("vCategory", mServiceObject));

        bHolder.binding.catDescTxt.setVisibility(View.GONE);
        if (mServiceObject.has("tListDescription")) {
            String tDesc = generalFunc.getJsonValueStr("tListDescription", mServiceObject);
            if (Utils.checkText(tDesc)) {
                bHolder.binding.catDescTxt.setVisibility(View.VISIBLE);
                bHolder.binding.catDescTxt.setText(tDesc);

                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (bHolder.binding.catNameTxt.getLineCount() == 1) {
                        bHolder.binding.catNameTxt.setMinLines(1);
                        bHolder.binding.catDescTxt.setMinLines(4);
                    }
                }, 20);
            }

        }

        String vListLogo = generalFunc.getJsonValueStr("vListLogo", mServiceObject);
        String imageURL = Utils.getResizeImgURL(mContext, vListLogo != null ? vListLogo : "", dynamicWidth, dynamicHeight);
        new LoadImage.builder(LoadImage.bind(imageURL), bHolder.binding.catImgView).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();

        ///////////////////////////////////////////////////////////////-----------------------------
        bHolder.binding.contentArea.setOnClickListener(v -> listener.onMoreServiceItemClick(position, mServiceObject));
    }

    @Override
    public int getItemCount() {
        if (mServicesArr == null) {
            return 0;
        }
        return mServicesArr.length();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(JSONArray servicesArr) {
        this.mServicesArr = servicesArr;
        notifyDataSetChanged();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final Item23MoreServicesBinding binding;

        private ViewHolder(Item23MoreServicesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickListener {
        void onMoreServiceItemClick(int morePos, JSONObject mServiceObject);
    }
}