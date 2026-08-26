package com.fragments;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.GalleryImagesRecyclerAdapter;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.MoreInfoActivity;
import com.multixpro.user.R;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryFragment extends BaseFragment implements GalleryImagesRecyclerAdapter.OnItemClickListener {

    View view;
    RecyclerView galleryRecyclerView;
    ProgressBar loading_images;
    GeneralFunctions generalFunc;
    GalleryImagesRecyclerAdapter adapter;

    ArrayList<HashMap<String, String>> listData = new ArrayList<>();
    MoreInfoActivity moreInfoActivity;
    MTextView noDataTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);

        moreInfoActivity = (MoreInfoActivity) getActivity();
        generalFunc = MyApp.getInstance().getGeneralFun(moreInfoActivity.getActContext());
        galleryRecyclerView = view.findViewById(R.id.galleryRecyclerView);
        loading_images = view.findViewById(R.id.loading_images);

        noDataTxt = view.findViewById(R.id.noDataTxt);
        noDataTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO_DATA_AVAIL"));

        adapter = new GalleryImagesRecyclerAdapter(moreInfoActivity.getActContext(), listData, generalFunc, false, false, false);
        galleryRecyclerView.setAdapter(adapter);
        galleryRecyclerView.setClipToPadding(false);

        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.SRC_IN);

        Drawable mGalleryDrawable = moreInfoActivity.getActContext().getResources().getDrawable(R.mipmap.ic_gallery_fab);
        mGalleryDrawable.setColorFilter(colorFilter);

        Drawable mCameraDrawable = moreInfoActivity.getActContext().getResources().getDrawable(R.mipmap.ic_camera_fab);
        mCameraDrawable.setColorFilter(colorFilter);


        GridLayoutManager gridLay = new GridLayoutManager(getActivity(), adapter.getNumOfColumns());

        galleryRecyclerView.setLayoutManager(gridLay);

        adapter.setOnItemClickListener(this);
        getImages();

        return view;
    }

    private void getImages() {
        loading_images.setVisibility(View.VISIBLE);
        noDataTxt.setVisibility(View.GONE);
        listData.clear();

        adapter.notifyDataSetChanged();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getProviderImages");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iDriverId", moreInfoActivity.getIntent().getStringExtra("iDriverId"));
        parameters.put("SelectedCabType", Utils.CabGeneralType_UberX);

        ApiHandler.execute(moreInfoActivity.getActContext(), parameters, responseString -> {
            if (responseString != null && !responseString.equalsIgnoreCase("")) {
                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    listData.clear();

                    MyUtils.createArrayListJSONArray(generalFunc, listData, generalFunc.getJsonArray(Utils.message_str, responseString));

                    adapter.notifyDataSetChanged();


                    if (listData.size() == 0) {
                        noDataTxt.setVisibility(View.VISIBLE);
                    }

                } else {

                    noDataTxt.setVisibility(View.VISIBLE);
                }

            } else {
                generalFunc.showError(true);
            }

            loading_images.setVisibility(View.GONE);
        });

    }

    @Override
    public void onItemClickList(View v, int position) {
        moreInfoActivity.openCarouselView(listData, position);
    }

    @Override
    public void onLongItemClickList(View v, int position) {

    }

    @Override
    public void onDeleteClick(View v, int position) {

    }
}