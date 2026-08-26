package com.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.CustomDialogListAdapter;
import com.general.files.GeneralFunctions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.multixpro.user.R;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OpenListView {

    public static AppCompatDialog mListDialog = null;
    public static boolean showCartAlert = false;

    public enum OpenDirection {CENTER, BOTTOM}

    private Context mContext;
    private String title;
    private String subTitle;
    private OpenDirection openDirection;
    private boolean isCancelable;
    private boolean isStringArrayList = false;
    private ArrayList<HashMap<String, String>> arrayList;
    private ArrayList<String> stringArrayList;
    private OnItemClickList onItemClickList;
    private AppCompatDialog listDialog = null;

    GeneralFunctions generalFunctions;
    boolean isImageinList = false;
    boolean isloading = false;

    private OpenListView(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList) {
        this.mContext = mContext;
        this.title = title;
        this.openDirection = openDirection;
        this.isCancelable = isCancelable;
        this.arrayList = arrayList;
        this.onItemClickList = onItemClickList;
        generalFunctions = new GeneralFunctions(mContext);
    }

    private OpenListView(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, boolean isImageinList, OnItemClickList onItemClickList, String subTitle, boolean isloading) {
        this.mContext = mContext;
        this.title = title;
        this.openDirection = openDirection;
        this.isCancelable = isCancelable;
        this.arrayList = arrayList;
        this.onItemClickList = onItemClickList;
        generalFunctions = new GeneralFunctions(mContext);
        this.isImageinList = isImageinList;
        this.subTitle = subTitle;
        this.isloading = isloading;
    }

    private OpenListView(Context mContext, String title, ArrayList<String> stringArrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList, boolean isStringArrayList) {
        this.mContext = mContext;
        this.title = title;
        this.openDirection = openDirection;
        this.isCancelable = isCancelable;
        this.isStringArrayList = isStringArrayList;
        this.stringArrayList = stringArrayList;
        this.onItemClickList = onItemClickList;
        generalFunctions = new GeneralFunctions(mContext);
    }


    public void show(int selectedItemPosition, String keyToShowAsTitle) {
        if (mListDialog != null && mListDialog.isShowing()) {
            return;
        }
        if (openDirection == OpenDirection.BOTTOM) {
            final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
            listDialog = dialog;
        }

        if (openDirection == OpenDirection.CENTER) {
            AppCompatDialog dialog = new AppCompatDialog(mContext);
            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
            listDialog = dialog;
        }
        mListDialog = listDialog;

        if (isImageinList) {
            showDialogWithImage(selectedItemPosition, keyToShowAsTitle, isStringArrayList);
        } else {
            showDialog(selectedItemPosition, keyToShowAsTitle, isStringArrayList);
        }
    }

    private void showDialogWithImage(int selectedItemPosition, String keyToShowAsTitle, boolean isStringArrayList) {
        if (listDialog == null) {
            return;
        }
        //final BottomSheetDialog optionDailog = new BottomSheetDialog(mContext);
        final AppCompatDialog optionDailog = new AppCompatDialog(mContext);
        // optionDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        optionDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mListDialog = optionDailog;
        // optionDailog.getBehavior().setPeekHeight(2000);


        View contentView = View.inflate(mContext, R.layout.dialog_img_filter, null);

        optionDailog.setContentView(contentView);
        int width = (int) Utils.getScreenPixelWidth(mContext) - mContext.getResources().getDimensionPixelSize(R.dimen._30sdp);
        int height = (int) Utils.getScreenPixelHeight(mContext) - mContext.getResources().getDimensionPixelSize(R.dimen._80sdp);

        optionDailog.getWindow().setLayout(width, height);
        // BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        // mBehavior.setPeekHeight(Utils.dpToPx(600, mContext));
        optionDailog.setCancelable(false);
        //  View bottomSheetView = optionDailog.getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
        // bottomSheetView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));

        if (generalFunctions.isRTLmode()) {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        CustomDialogListAdapter listAdapter;
        if (isStringArrayList) {
            listAdapter = new CustomDialogListAdapter(mContext, stringArrayList, selectedItemPosition, isStringArrayList, isImageinList, generalFunctions);
        } else {
            listAdapter = new CustomDialogListAdapter(mContext, arrayList, selectedItemPosition, keyToShowAsTitle, isImageinList, generalFunctions);
        }

        ImageView closeImg = (ImageView) optionDailog.findViewById(R.id.closeImg);
        MTextView titleTxt = (MTextView) optionDailog.findViewById(R.id.TitleTxt);
        MTextView subtitleTxt = (MTextView) optionDailog.findViewById(R.id.subTitleTxt);
        titleTxt.setText(title);
        subtitleTxt.setText(subTitle);
        if (subtitleTxt.equals("")) {
            subtitleTxt.setVisibility(View.GONE);
        } else {
            subtitleTxt.setVisibility(View.VISIBLE);
        }

//        SpringAnimation animation = new SpringAnimation(bottomSheetView, DynamicAnimation.TRANSLATION_Y);
//        SpringForce spring = new SpringForce();
//        spring.setFinalPosition(Utils.dpToPx(15, mContext));
//        spring.setStiffness(SpringForce.STIFFNESS_LOW);
//        spring.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        // animation.setStartVelocity(1500);
        // animation.setSpring(spring);

        closeImg.setOnClickListener(v -> {
            optionDailog.dismiss();
            mListDialog = null;
        });

        RecyclerView mRecyclerView = optionDailog.findViewById(R.id.mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.scrollToPosition(selectedItemPosition);

        listAdapter.setOnItemClickList(position -> {
            if (!isloading) {
                optionDailog.dismiss();
            }
            if (onItemClickList != null) {
                onItemClickList.onItemClick(position);
            }
            onItemClickList = null;
            closeImg.setClickable(false);
            optionDailog.setCancelable(false);
            if (!showCartAlert) {
                mListDialog = null;
            }
        });

        try {
            if (!optionDailog.isShowing()) {
                optionDailog.show();
                //    animation.start();
            }
        } catch (Exception e) {

        }
    }

    private void showDialog(int selectedItemPosition, String keyToShowAsTitle, boolean isStringArrayList) {
        if (listDialog == null) {
            return;
        }
        View contentView = View.inflate(mContext, R.layout.dialog_filter, null);
        final AppCompatDialog optionDailog = new AppCompatDialog(mContext);
        optionDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        listDialog = optionDailog;

        if (generalFunctions.isRTLmode()) {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            contentView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        listDialog.setContentView(contentView);
        listDialog.setCancelable(isCancelable);
        CustomDialogListAdapter listAdapter;
        if (isStringArrayList) {
            listAdapter = new CustomDialogListAdapter(mContext, stringArrayList, selectedItemPosition, isStringArrayList, isImageinList, generalFunctions);
        } else {
            listAdapter = new CustomDialogListAdapter(mContext, arrayList, selectedItemPosition, keyToShowAsTitle, isImageinList, generalFunctions);
        }


        ImageView closeImg = (ImageView) listDialog.findViewById(R.id.closeImg);
        MTextView titleTxt = (MTextView) listDialog.findViewById(R.id.TitleTxt);
        titleTxt.setText(title);
        closeImg.setOnClickListener(v -> listDialog.dismiss());

        RecyclerView mRecyclerView = listDialog.findViewById(R.id.mRecyclerView);
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.scrollToPosition(selectedItemPosition);
        listAdapter.setOnItemClickList(position -> {
            listDialog.dismiss();
            mListDialog = null;
            if (onItemClickList != null) {
                onItemClickList.onItemClick(position);
            }
        });


        try {
            if (!listDialog.isShowing()) {
                listDialog.show();
            }
        } catch (Exception e) {

        }
    }

    public interface OnItemClickList {
        void onItemClick(int position);
    }

    public static OpenListView getInstance(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList) {
        return new OpenListView(mContext, title, arrayList, openDirection, isCancelable, onItemClickList);
    }

    public static OpenListView getInstance(Context mContext, String title, ArrayList<String> arrayList, OpenDirection openDirection, boolean isCancelable, boolean isStringArrayList, OnItemClickList onItemClickList) {
        return new OpenListView(mContext, title, arrayList, openDirection, isCancelable, onItemClickList, isStringArrayList);
    }

    public static OpenListView getInstance(Context mContext, String title, ArrayList<HashMap<String, String>> arrayList, OpenDirection openDirection, boolean isCancelable, OnItemClickList onItemClickList, boolean isImageinList, String subTitleTxt, boolean isloading) {
        return new OpenListView(mContext, title, arrayList, openDirection, isCancelable, isImageinList, onItemClickList, subTitleTxt, isloading);
    }
}