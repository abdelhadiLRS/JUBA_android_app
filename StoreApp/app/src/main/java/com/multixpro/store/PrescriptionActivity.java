package com.multixpro.store;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.adapter.files.GalleryImagesRecyclerAdapter;
import com.squareup.picasso.Picasso;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.carouselview.CarouselView;
import com.view.carouselview.ViewListener;

import java.util.ArrayList;

public class PrescriptionActivity extends ParentActivity implements GalleryImagesRecyclerAdapter.OnItemClickListener {

    ImageView rightImgView;
    RecyclerView imageListRecyclerView;
    MButton btn_type2, btn_type2_confirm;
    GalleryImagesRecyclerAdapter adapter;
    ArrayList<String> listData = new ArrayList<>();
    AppCompatImageView noImgView;
    ProgressBar loading_images;
    MTextView attechTxt;
    View carouselContainerView;
    CarouselView carouselView;
    MTextView closeCarouselTxtView;
    LinearLayout confirmBtnArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        rightImgView = findViewById(R.id.rightImgView);
        MTextView titleTxt = findViewById(R.id.titleTxt);
        attechTxt = findViewById(R.id.attechTxt);
        carouselContainerView = findViewById(R.id.carouselContainerView);
        carouselView = findViewById(R.id.carouselView);
        MTextView noteTxt = findViewById(R.id.noteTxt);
        MTextView noDescTxt = findViewById(R.id.noDescTxt);
        closeCarouselTxtView = findViewById(R.id.closeCarouselTxtView);
        imageListRecyclerView = findViewById(R.id.imageListRecyclerView);
        confirmBtnArea = findViewById(R.id.confirmBtnArea);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2_confirm = ((MaterialRippleLayout) findViewById(R.id.btn_type2_confirm)).getChildView();
        addToClickHandler(btn_type2_confirm);


        btn_type2.setId(Utils.generateViewId());
        noImgView = findViewById(R.id.noImgView);
        loading_images = findViewById(R.id.loading_images);
        addToClickHandler(btn_type2);
        addToClickHandler(attechTxt);

        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));
        noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GALLERY_IMG_NOTE"));

        addToClickHandler(backImgView);
        addToClickHandler(rightImgView);
        addToClickHandler(closeCarouselTxtView);
        closeCarouselTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PRESCRIPTION"));
        String LBL_ATTACH_PRESCRIPTION = generalFunc.retrieveLangLBl("", "LBL_ATTACH_PRESCRIPTION");
        btn_type2.setText(LBL_ATTACH_PRESCRIPTION);
        attechTxt.setText(LBL_ATTACH_PRESCRIPTION);
        noDescTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NOPRESCRIPTION"));
        noteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PRESCRIPTION_BODY_TEXT"));
        btn_type2_confirm.setText(generalFunc.retrieveLangLBl("", "LBL_CONFIRM_TXT"));

        rightImgView.setVisibility(View.GONE);

        listData = (ArrayList<String>) getIntent().getSerializableExtra("imageList");
        adapter = new GalleryImagesRecyclerAdapter(getActContext(), listData, generalFunc, false, true, false, false);

        GridLayoutManager gridLay = new GridLayoutManager(getActContext(), 3);

        imageListRecyclerView.setLayoutManager(gridLay);
        imageListRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClickList(View v, int position) {
        carouselContainerView.setVisibility(View.VISIBLE);
        carouselView.setViewListener(viewListener);
        carouselView.setPageCount(listData.size());
        carouselView.setCurrentItem(position);
    }

    @Override
    public void onDeleteClick(View v, int position) {

    }

    ViewListener viewListener = position -> {
        ImageView customView = new ImageView(getActContext());

        CarouselView.LayoutParams layParams = new CarouselView.LayoutParams(CarouselView.LayoutParams.MATCH_PARENT, CarouselView.LayoutParams.MATCH_PARENT);
//        layParams.leftMargin = Utils.dipToPixels(getActContext(), 15);
//        layParams.rightMargin = Utils.dipToPixels(getActContext(), 15);
        customView.setLayoutParams(layParams);

        customView.setPadding(Utils.dipToPixels(getActContext(), 15), 0, Utils.dipToPixels(getActContext(), 15), 0);
        customView.setImageResource(R.mipmap.ic_no_icon);


        Picasso.get()
                .load(Utils.getResizeImgURL(getActContext(), listData.get(position), ((int) Utils.getScreenPixelWidth(getActContext())) - Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30)))
                .placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon)
                .into(customView, null);

        return customView;
    };

    private Context getActContext() {
        return PrescriptionActivity.this;
    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            PrescriptionActivity.super.onBackPressed();
        } else if (i == closeCarouselTxtView.getId()) {
            if (carouselContainerView.getVisibility() == View.VISIBLE) {
                carouselContainerView.setVisibility(View.GONE);
            }
        }
    }

}