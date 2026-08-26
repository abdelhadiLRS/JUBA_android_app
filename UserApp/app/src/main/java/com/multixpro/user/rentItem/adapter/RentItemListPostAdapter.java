package com.multixpro.user.rentItem.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ItemRentItemListPostBinding;
import com.utils.LoadImageGlide;
import com.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RentItemListPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final GeneralFunctions generalFunc;
    private final OnItemClickListener mItemClickListener;

    private final ArrayList<HashMap<String, String>> list;
    private final String LBL_REVIEW_POST, LBL_DELETE, LBL_EDIT_POST, LBL_RENT_REJECT_REASON, LBL_RENT_CONTACT_US, LBL_RENEW_POST;
    private final int width, height;

    public RentItemListPostAdapter(@NonNull Context context, @NonNull GeneralFunctions generalFunc, @NonNull ArrayList<HashMap<String, String>> list, @NonNull OnItemClickListener mItemClickListener) {
        this.generalFunc = generalFunc;
        this.list = list;
        this.mItemClickListener = mItemClickListener;
        this.LBL_DELETE = generalFunc.retrieveLangLBl("", "LBL_DELETE");
        this.LBL_REVIEW_POST = generalFunc.retrieveLangLBl("", "LBL_REVIEW_POST");
        this.LBL_RENT_REJECT_REASON = generalFunc.retrieveLangLBl("", "LBL_RENT_REJECT_REASON");
        this.LBL_RENT_CONTACT_US = generalFunc.retrieveLangLBl("", "LBL_RENT_CONTACT_US");
        this.LBL_EDIT_POST = generalFunc.retrieveLangLBl("", "LBL_RENT_EDIT_INFORMATION");
        this.LBL_RENEW_POST = generalFunc.retrieveLangLBl("", "LBL_RENT_RENEW_TXT");
        this.width = (int) Utils.getScreenPixelWidth(context) - context.getResources().getDimensionPixelSize(R.dimen._12sdp);
        this.height = Utils.getHeightOfBanner(context, 0, "16:9");
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ListViewHolder(ItemRentItemListPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> mapData = list.get(position);

        ListViewHolder listHolder = (ListViewHolder) holder;

        listHolder.binding.txtPostNo.setText(mapData.get("vRentItemPostNo"));
        listHolder.binding.txtPostTitle.setText(mapData.get("vItemName"));

        if (Utils.checkText(mapData.get("eRentItemDurationDateTxt"))) {
            listHolder.binding.txtPostDurationStatus.setVisibility(View.VISIBLE);
            listHolder.binding.txtPostDurationStatus.setText(mapData.get("eRentItemDurationDateTxt"));
        } else {
            listHolder.binding.txtPostDurationStatus.setVisibility(View.GONE);
        }


        if (Utils.checkText(mapData.get("eStatus"))) {
            listHolder.binding.rentPostStatusTagTxt.setText(mapData.get("eStatus"));
            listHolder.binding.rentTagImage.setVisibility(View.VISIBLE);
        } else {
            listHolder.binding.rentTagImage.setVisibility(View.GONE);
        }

        listHolder.binding.txtPostDelete.setText(LBL_DELETE);
        listHolder.binding.txtPostDelete.setOnClickListener(v -> mItemClickListener.onDeleteButtonClick(position, mapData));

        if (mapData.get("eStatusOrg").equalsIgnoreCase("Approved")) {
            listHolder.binding.txtPostReview.setText(LBL_REVIEW_POST);
            listHolder.binding.txtPostReview.setOnClickListener(v -> mItemClickListener.onReviewButtonClick(position, mapData));
        } else if (mapData.get("eStatusOrg").equalsIgnoreCase("Expired")) {
            listHolder.binding.txtPostReview.setText(LBL_RENEW_POST);
            listHolder.binding.txtPostReview.setOnClickListener(v -> mItemClickListener.onEditButtonClick(position, mapData));
        } else {
            listHolder.binding.txtPostReview.setText(LBL_EDIT_POST);
            listHolder.binding.txtPostReview.setOnClickListener(v -> mItemClickListener.onEditButtonClick(position, mapData));
        }
        listHolder.binding.txtPostDelete.setVisibility(View.VISIBLE);
        listHolder.binding.txtPostReview.setVisibility(View.VISIBLE);

        listHolder.binding.txtPostReject.setVisibility(View.GONE);
        listHolder.binding.extraBtnView.setVisibility(View.GONE);
        listHolder.binding.extraView.setVisibility(View.GONE);
        listHolder.binding.txtContactUs.setVisibility(View.GONE);

        if (mapData.get("eStatusOrg").equalsIgnoreCase("Reject") || mapData.get("eStatusOrg").equalsIgnoreCase("Deleted")) {
            listHolder.binding.extraBtnView.setVisibility(View.VISIBLE);

            listHolder.binding.txtPostReject.setVisibility(View.VISIBLE);
            listHolder.binding.txtPostReject.setText(LBL_RENT_REJECT_REASON);
            listHolder.binding.txtPostReject.setOnClickListener(v -> generalFunc.showGeneralMessage("", mapData.get("vRejectReason")));

            if (mapData.get("eStatusOrg").equalsIgnoreCase("Deleted")) {
                listHolder.binding.txtPostDelete.setVisibility(View.GONE);
                listHolder.binding.txtPostReview.setVisibility(View.GONE);
                listHolder.binding.extraBtnView.setVisibility(View.GONE);

                listHolder.binding.extraView.setVisibility(View.VISIBLE);
                listHolder.binding.txtContactUs.setVisibility(View.VISIBLE);
                listHolder.binding.txtContactUs.setText(LBL_RENT_CONTACT_US);
                listHolder.binding.txtContactUs.setOnClickListener(v -> mItemClickListener.onContactUsClick(position, mapData));
            }
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) listHolder.binding.ivRentItemImage.getLayoutParams();
        params.width = width;
        params.height = height;
        listHolder.binding.ivRentItemImage.setLayoutParams(params);

        JSONArray imagesArr = generalFunc.getJsonArray(mapData.get("Images"));
        String imageUrl = "";
        if (imagesArr != null && imagesArr.length() > 0) {
            JSONObject obj_temp = generalFunc.getJsonObject(imagesArr, 0);

            String eFileType = generalFunc.getJsonValueStr("eFileType", obj_temp);
            String vImage = generalFunc.getJsonValueStr("vImage", obj_temp);
            String ThumbImage = generalFunc.getJsonValueStr("ThumbImage", obj_temp);

            if (eFileType.equals("Image")) {
                if (!TextUtils.isEmpty(vImage)) {
                    imageUrl = vImage;
                }
            } else if (eFileType.equals("Video")) {

                if (!TextUtils.isEmpty(ThumbImage)) {
                    imageUrl = ThumbImage;
                }
            }
        }

        String finalBaseUrlL = imageUrl;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String finalLeftImage = Utils.getResizeImgURL(MyApp.getInstance().getCurrentAct(), finalBaseUrlL, listHolder.binding.ivRentItemImageL.getWidth(), listHolder.binding.ivRentItemImageL.getMeasuredHeight());

            new LoadImageGlide.builder(holder.itemView.getContext(), LoadImageGlide.bind(finalLeftImage), listHolder.binding.ivRentItemImageL).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();
        }, 20);


        String finalTopImage = Utils.getResizeImgURL(MyApp.getInstance().getCurrentAct(), imageUrl, params.width, params.height);
        new LoadImageGlide.builder(holder.itemView.getContext(), LoadImageGlide.bind(finalTopImage), listHolder.binding.ivRentItemImage).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();

        listHolder.binding.ivRentItemImageL.setOnClickListener(view -> {
            listHolder.binding.ivRentItemImage.setVisibility(View.VISIBLE);
            listHolder.binding.ivRentItemImageL.setVisibility(View.GONE);
        });
        listHolder.binding.ivRentItemImage.setOnClickListener(view -> {
            listHolder.binding.ivRentItemImageL.setVisibility(View.VISIBLE);
            listHolder.binding.ivRentItemImage.setVisibility(View.GONE);
        });
        if (position == (list.size() - 1)) {
            listHolder.binding.llExtraArea.setVisibility(View.VISIBLE);
        } else {
            listHolder.binding.llExtraArea.setVisibility(View.GONE);
        }

        if (generalFunc.isRTLmode()) {
            listHolder.binding.rentTagImage.setRotationY(180);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ListViewHolder extends RecyclerView.ViewHolder {

        private final ItemRentItemListPostBinding binding;

        private ListViewHolder(ItemRentItemListPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onDeleteButtonClick(int position, HashMap<String, String> mapData);

        void onReviewButtonClick(int position, HashMap<String, String> mapData);

        void onEditButtonClick(int position, HashMap<String, String> mapData);

        void onContactUsClick(int position, HashMap<String, String> mapData);
    }
}