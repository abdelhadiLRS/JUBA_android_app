package com.adapter.files.deliverAll;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ItemMenuHeaderviewBinding;
import com.multixpro.user.databinding.ItemMenuListBinding;
import com.multixpro.user.databinding.ItemResmenuGridviewBinding;
import com.utils.LoadImage;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RestaurantmenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_HEADER = 1;
    private final int TYPE_GRID = 2;

    private final Context mContext;
    private final GeneralFunctions generalFunc;
    private final OnItemClickListener mItemClickListener;

    private final ArrayList<HashMap<String, String>> list;
    private final int grayColor, imageBackColor, mHeight;

    public RestaurantmenuAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, OnItemClickListener mItemClickListener) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.mItemClickListener = mItemClickListener;
        this.list = list;

        this.grayColor = mContext.getResources().getColor(R.color.gray);
        this.imageBackColor = mContext.getResources().getColor(R.color.appThemeColor_1);

        mHeight = (int) Utils.getScreenPixelHeight(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new ViewHolder(ItemMenuHeaderviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_GRID) {
            return new GridViewHolder(ItemResmenuGridviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ListViewHolder(ItemMenuListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HashMap<String, String> mapData = list.get(position);


        if (holder instanceof ViewHolder) {
            ViewHolder headerholder = (ViewHolder) holder;
            headerholder.binding.menuHeaderTxt.setText(mapData.get("menuName"));

        } else if (holder instanceof GridViewHolder) {
            GridViewHolder grideholder = (GridViewHolder) holder;
            grideholder.binding.title.setText(mapData.get("vItemType"));

            String eFoodType = mapData.get("eFoodType");
            assert eFoodType != null;
            if (eFoodType.equalsIgnoreCase("NonVeg")) {
                grideholder.binding.nonVegImage.setVisibility(View.VISIBLE);
                grideholder.binding.vegImage.setVisibility(View.GONE);
            } else if (eFoodType.equals("Veg")) {
                grideholder.binding.nonVegImage.setVisibility(View.GONE);
                grideholder.binding.vegImage.setVisibility(View.VISIBLE);
            }

            if (Objects.requireNonNull(mapData.get("prescription_required")).equalsIgnoreCase("Yes")) {
                grideholder.binding.presImage.setVisibility(View.VISIBLE);
            } else {
                grideholder.binding.presImage.setVisibility(View.GONE);
            }
            String StrikeoutPriceConverted = mapData.get("StrikeoutPriceConverted");
            if (Objects.requireNonNull(mapData.get("fOfferAmtNotZero")).equalsIgnoreCase("Yes")) {
                grideholder.binding.price.setText(StrikeoutPriceConverted);
                grideholder.binding.price.setTextColor(grayColor);
            } else {
                grideholder.binding.price.setText(StrikeoutPriceConverted);
                grideholder.binding.price.setPaintFlags(0);
            }

            boolean isBlank = Objects.requireNonNull(mapData.get("vImage")).equalsIgnoreCase("https");
            new LoadImage.builder(LoadImage.bind(isBlank ? mapData.get("vImage") : mapData.get("vImageResized")), grideholder.binding.menuImage).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) grideholder.binding.menuImage.getLayoutParams();
            layoutParams.height = GeneralFunctions.parseIntegerValue(0, mapData.get("heightOfImage"));
            grideholder.binding.menuImage.setLayoutParams(layoutParams);
            grideholder.binding.addBtn.setText(mapData.get("LBL_ADD"));
            grideholder.binding.addBtn.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(grideholder.binding.addBtn, position);
                }
            });
            if (generalFunc.isRTLmode()) {
                grideholder.binding.tagImage.setRotation(180);
                grideholder.binding.tagTxt.setPadding(10, 15, 0, 0);
            }
            String vHighlightName = mapData.get("vHighlightName");
            if (vHighlightName != null && !vHighlightName.equals("")) {
                grideholder.binding.tagImage.setVisibility(View.VISIBLE);
                grideholder.binding.tagTxt.setVisibility(View.VISIBLE);
                grideholder.binding.tagTxt.setText(vHighlightName);
            } else {
                grideholder.binding.tagImage.setVisibility(View.GONE);
                grideholder.binding.tagTxt.setVisibility(View.GONE);
            }
            grideholder.binding.vCategoryNameTxt.setText(mapData.get("vCategoryName"));
        } else {
            ListViewHolder listholder = (ListViewHolder) holder;
            String isFromSearch = mapData.get("isFromSearch");
            boolean isFromSearchAvail = Utils.checkText(isFromSearch);
            if (Utils.checkText(isFromSearch)) {

                listholder.binding.storeTitle.setTransformationMethod(null);
                listholder.binding.storeTitleEx.setTransformationMethod(null);

                Drawable image = ContextCompat.getDrawable(mContext, R.drawable.ic_star_color1_24dp);
                assert image != null;
                image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());

                // Replace blank spaces with image icon
                String myText = mapData.get("vCompany") + "(";
                int textLength = myText.length();
                SpannableString sb = new SpannableString(myText + "  " + mapData.get("vAvgRatingConverted") + ")");
                ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
                sb.setSpan(imageSpan, textLength, textLength + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                listholder.binding.storeTitle.setText(sb);
                listholder.binding.storeTitleEx.setText(sb);

                listholder.binding.storeTitle.setSelected(true);
                listholder.binding.storeTitleEx.setSelected(true);
                listholder.binding.title.setTextColor(imageBackColor);
                listholder.binding.titleEx.setTextColor(imageBackColor);
            }
            listholder.binding.title.setText(mapData.get("vItemType"));
            listholder.binding.title.setSelected(true);
            listholder.binding.titleEx.setText(mapData.get("vItemType"));
            listholder.binding.titleEx.setSelected(true);
            listholder.binding.desc.setText(mapData.get("vItemDesc"));
            listholder.binding.descEx.setText(mapData.get("vItemDesc"));


            if (Objects.requireNonNull(mapData.get("vItemDesc")).equalsIgnoreCase("")) {
                listholder.binding.desc.setVisibility(View.GONE);
                listholder.binding.descEx.setVisibility(View.GONE);
            } else {
                listholder.binding.desc.setVisibility(View.VISIBLE);
                listholder.binding.descEx.setVisibility(View.VISIBLE);
            }

            //String vImage = mapData.get("vImageResized");
            int padding = (int) mContext.getResources().getDimension(R.dimen._5sdp);
            int wdt = generalFunc.getViewHeightWidth(listholder.binding.parent, false);
            ImageView imageView = isFromSearchAvail ? listholder.binding.searchExpandImg : listholder.binding.expandImg;
            ImageView imageView1 = isFromSearchAvail ? listholder.binding.searchMenuImg : listholder.binding.menuImg;

            listholder.binding.expandImg.setVisibility(View.GONE);
            listholder.binding.searchMenuImg.setVisibility(View.GONE);
            if (isFromSearchAvail) {
                listholder.binding.menuImg.setVisibility(View.GONE);
                listholder.binding.searchMenuImg.setVisibility(View.INVISIBLE);
            } else {
                listholder.binding.menuImg.setVisibility(View.INVISIBLE);
                listholder.binding.searchMenuImg.setVisibility(View.GONE);
            }

            String imgUrl = Utils.getResizeImgURL(mContext, Objects.requireNonNull(mapData.get("vImage")), wdt, 0, 0);
            imageView.setVisibility(View.INVISIBLE);
            new LoadImage.builder(LoadImage.bind(imgUrl), imageView).setPicassoListener(new LoadImage.PicassoListener() {
                @Override
                public void onSuccess() {
                    imageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {

                }
            }).build();

            final long[] mLastClickTime = {0};

            imageView.setOnClickListener(v -> {

                if (SystemClock.elapsedRealtime() - mLastClickTime[0] < 800) {
                    return;
                }
                mLastClickTime[0] = SystemClock.elapsedRealtime();

                try {
                    TransitionManager.beginDelayedTransition(listholder.binding.parent);
                    int height = listholder.binding.parent.getHeight();
                    int width = listholder.binding.parent.getWidth();
                    int ht = imageView.getMeasuredHeight();

                    if (ht >= height || ht > mHeight / 4 || ht > Utils.dpToPx(120, mContext)) {
                        new Handler(Looper.myLooper()).postDelayed(() -> setMargins(imageView, padding, padding, padding, padding), 600);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        params.width = (int) mContext.getResources().getDimension(isFromSearchAvail ? R.dimen._80sdp : R.dimen._60sdp);
                        params.height = (int) mContext.getResources().getDimension(isFromSearchAvail ? R.dimen._80sdp : R.dimen._60sdp);
                        imageView.requestLayout();

                        generalFunc.slideAnimView(listholder.binding.expandTempImg, listholder.binding.expandDetailArea, listholder.binding.expandTempImg.getHeight(), 0, 400);

                        listholder.binding.expandDetailArea.setVisibility(View.GONE);
                        imageView1.setVisibility(View.INVISIBLE);
                        listholder.binding.mainDetailArea.setVisibility(View.VISIBLE);
                        listholder.binding.addBtnArea.setVisibility(View.VISIBLE);

                        HashMap<String, String> hashMap = list.get(position);
                        hashMap.put("isExpand", "No");
                        list.set(position, hashMap);
                    } else {
                        //expanded

                        new Handler(Looper.myLooper()).postDelayed(() -> {
                            int pd = (int) mContext.getResources().getDimension(R.dimen._minus10sdp);
                            setMargins(imageView, 0, pd, 0, 0);
                        }, 500);

                        int ParentImageHeight = generalFunc.getViewHeightWidth(imageView, true);
                        HashMap<String, String> hashMap = list.get(position);
                        hashMap.put("isExpand", "Yes");
                        hashMap.put("tempheightImg", ParentImageHeight + "");

                        listholder.binding.descEx.setTag(null);
                        listholder.binding.descEx.setText(GeneralFunctions.fromHtml(GeneralFunctions.fromHtml(hashMap.get("vItemDesc")) + ""));
                        generalFunc.makeTextViewResizable(listholder.binding.descEx, 3, "...+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, R.color.appThemeColor_1, R.dimen.txt_size_10);

                        list.set(position, hashMap);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listholder.binding.expandTempImg.getLayoutParams();
                        layoutParams.width = width;
                        layoutParams.height = ParentImageHeight;//height;
                        listholder.binding.expandTempImg.requestLayout();
                        listholder.binding.expandTempImg.setVisibility(View.INVISIBLE);

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        params.width = width;
                        params.height = ParentImageHeight;
                        imageView.requestLayout();

                        listholder.binding.expandDetailArea.setVisibility(View.VISIBLE);
                        listholder.binding.mainDetailArea.setVisibility(View.GONE);
                        listholder.binding.addBtnArea.setVisibility(View.GONE);
                        imageView1.setVisibility(View.GONE);

                        TransitionManager.beginDelayedTransition(listholder.binding.expandDetailArea);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listholder.binding.expandDetailArea.getLayoutParams();
                        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        listholder.binding.expandDetailArea.requestLayout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


            if (!Objects.requireNonNull(list.get(position).get("isExpand")).equalsIgnoreCase("Yes")) {

                setMargins(imageView, padding, padding, padding, padding);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.width = (int) mContext.getResources().getDimension(isFromSearchAvail ? R.dimen._80sdp : R.dimen._60sdp);
                params.height = (int) mContext.getResources().getDimension(isFromSearchAvail ? R.dimen._80sdp : R.dimen._60sdp);
                imageView.requestLayout();
                generalFunc.slideAnimView(listholder.binding.expandTempImg, listholder.binding.expandDetailArea, listholder.binding.expandTempImg.getHeight(), 0, 400);

                listholder.binding.expandDetailArea.setVisibility(View.GONE);
                imageView1.setVisibility(View.INVISIBLE);
                listholder.binding.mainDetailArea.setVisibility(View.VISIBLE);
                listholder.binding.addBtnArea.setVisibility(View.VISIBLE);

            } else {

                listholder.binding.descEx.setTag(null);
                listholder.binding.descEx.setText(GeneralFunctions.fromHtml(GeneralFunctions.fromHtml(list.get(position).get("vItemDesc")) + ""));
                generalFunc.makeTextViewResizable(listholder.binding.descEx, 3, "...+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, R.color.appThemeColor_1, R.dimen.txt_size_10);

                TransitionManager.beginDelayedTransition(listholder.binding.parent);
                //int height = listholder.binding.parent.getHeight();
                int width = listholder.binding.parent.getWidth();

                int pd = (int) mContext.getResources().getDimension(R.dimen._minus10sdp);
                setMargins(imageView, 0, pd, 0, 0);

                int ParentImageHeight = Integer.parseInt(Objects.requireNonNull(list.get(position).get("tempheightImg")));


                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listholder.binding.expandTempImg.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = ParentImageHeight;//height;
                listholder.binding.expandTempImg.requestLayout();
                listholder.binding.expandTempImg.setVisibility(View.INVISIBLE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                params.width = width;
                params.height = ParentImageHeight;
                imageView.requestLayout();

                listholder.binding.expandDetailArea.setVisibility(View.VISIBLE);
                listholder.binding.mainDetailArea.setVisibility(View.GONE);
                listholder.binding.addBtnArea.setVisibility(View.GONE);
                imageView1.setVisibility(View.GONE);


                TransitionManager.beginDelayedTransition(listholder.binding.expandDetailArea);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listholder.binding.expandDetailArea.getLayoutParams();
                lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;

                listholder.binding.expandDetailArea.requestLayout();


            }

            String eFoodType = mapData.get("eFoodType");

            if (eFoodType != null) {
                if (eFoodType.equalsIgnoreCase("NonVeg")) {
                    listholder.binding.nonVegImage.setVisibility(View.VISIBLE);
                    listholder.binding.nonVegImageEx.setVisibility(View.VISIBLE);
                    listholder.binding.vegImage.setVisibility(View.GONE);
                    listholder.binding.vegImageEx.setVisibility(View.GONE);
                } else if (eFoodType.equals("Veg")) {
                    listholder.binding.nonVegImage.setVisibility(View.GONE);
                    listholder.binding.nonVegImageEx.setVisibility(View.GONE);
                    listholder.binding.vegImage.setVisibility(View.VISIBLE);
                    listholder.binding.vegImageEx.setVisibility(View.VISIBLE);
                }
            }

            if (Objects.requireNonNull(mapData.get("prescription_required")).equalsIgnoreCase("Yes")) {
                listholder.binding.presImage.setVisibility(View.VISIBLE);
                listholder.binding.presImageEx.setVisibility(View.VISIBLE);
            } else {
                listholder.binding.presImage.setVisibility(View.GONE);
                listholder.binding.presImageEx.setVisibility(View.GONE);
            }
            String StrikeoutPriceConverted = mapData.get("StrikeoutPriceConverted");
            if (Objects.requireNonNull(mapData.get("fOfferAmtNotZero")).equalsIgnoreCase("Yes")) {
                listholder.binding.price.setText(StrikeoutPriceConverted);
                listholder.binding.price.setPaintFlags(listholder.binding.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                listholder.binding.price.setTextColor(grayColor);
                listholder.binding.priceEx.setText(StrikeoutPriceConverted);
                String fDiscountPricewithsymbolConverted = mapData.get("fDiscountPricewithsymbolConverted");
                listholder.binding.priceEx.setPaintFlags(listholder.binding.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                listholder.binding.priceEx.setTextColor(grayColor);
                listholder.binding.offerPrice.setText(fDiscountPricewithsymbolConverted);
                listholder.binding.offerPrice.setVisibility(View.VISIBLE);
                listholder.binding.offerPriceEx.setText(fDiscountPricewithsymbolConverted);
                listholder.binding.offerPriceEx.setVisibility(View.VISIBLE);
            } else {
                listholder.binding.price.setTextColor(imageBackColor);
                listholder.binding.price.setText(StrikeoutPriceConverted);
                listholder.binding.price.setPaintFlags(0);
                listholder.binding.priceEx.setTextColor(imageBackColor);
                listholder.binding.priceEx.setText(StrikeoutPriceConverted);
                listholder.binding.priceEx.setPaintFlags(0);
                listholder.binding.offerPrice.setVisibility(View.GONE);
                listholder.binding.offerPriceEx.setVisibility(View.GONE);
            }
            String LBL_ADD = mapData.get("LBL_ADD");
            listholder.binding.addBtn.setText(LBL_ADD);
            listholder.binding.addBtnEx.setText(LBL_ADD);

            listholder.binding.addBtn.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(listholder.binding.addBtn, position);
                }
            });

            listholder.binding.addBtnEx.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickList(listholder.binding.addBtnEx, position);
                }
            });

            String vHighlightName = mapData.get("vHighlightName");
            if (vHighlightName != null && !vHighlightName.equals("") && !isFromSearchAvail) {
                listholder.binding.tagArea.setVisibility(View.VISIBLE);
                listholder.binding.tagTxt.setText(vHighlightName);
            } else {
                listholder.binding.tagArea.setVisibility(View.GONE);
            }

            if (isFromSearchAvail) {
                listholder.binding.storeTitle.setVisibility(View.VISIBLE);
                listholder.binding.storeTitleEx.setVisibility(View.VISIBLE);
            } else {
                listholder.binding.storeTitle.setVisibility(View.GONE);
                listholder.binding.storeTitleEx.setVisibility(View.GONE);
            }

            String isLastLine = mapData.get("isLastLine");
            if (isLastLine != null && isLastLine.equals("Yes")) {
                listholder.binding.bottomLine.setVisibility(View.GONE);
            } else {
                listholder.binding.bottomLine.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        String rowType = list.get(position).get("Type");
        if (Objects.requireNonNull(rowType).equalsIgnoreCase("HEADER")) {
            return TYPE_HEADER;
        } else if (rowType.equalsIgnoreCase("GRID")) {
            return TYPE_GRID;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemMenuHeaderviewBinding binding;

        private ViewHolder(ItemMenuHeaderviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    protected static class GridViewHolder extends RecyclerView.ViewHolder {

        private final ItemResmenuGridviewBinding binding;

        private GridViewHolder(ItemResmenuGridviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    protected static class ListViewHolder extends RecyclerView.ViewHolder {

        private final ItemMenuListBinding binding;

        private ListViewHolder(ItemMenuListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onItemClickList(View v, int position);
    }
}