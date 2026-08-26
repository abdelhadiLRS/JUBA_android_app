package com.multixpro.store;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.adapter.files.GalleryImagesRecyclerAdapter;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.UploadProfileImage;
import com.service.handler.ApiHandler;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.FloatingAction.FloatingActionButton;
import com.view.MTextView;
import com.view.carouselview.CarouselView;
import com.view.carouselview.ViewListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MyGalleryActivity extends ParentActivity implements GalleryImagesRecyclerAdapter.OnItemClickListener {

    private MTextView titleTxt, closeCarouselTxtView;
    private WebView noteview;
    private ProgressBar loading_images;

    private CarouselView carouselView;
    private View carouselContainerView;

    private GalleryImagesRecyclerAdapter adapter;
    private final ArrayList<HashMap<String, String>> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        ImageView backImgView = (ImageView) findViewById(R.id.backImgView);
        noteview = (WebView) findViewById(R.id.noteview);

        MyApp.executeWV(noteview, generalFunc, generalFunc.getJsonValueStr("MANAGE_GALLERY_INFO", obj_userProfile), true);
        //MyApp.executeWV(noteview, generalFunc, generalFunc.retrieveLangLBl("", "LBL_UPLOAD_IMAGES_SAFETY_INFO"));

        RecyclerView galleryRecyclerView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        //imgAddOptionMenu = (FloatingActionMenu) findViewById(R.id.imgAddOptionMenu);
        loading_images = (ProgressBar) findViewById(R.id.loading_images);
        carouselContainerView = findViewById(R.id.carouselContainerView);
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        closeCarouselTxtView = (MTextView) findViewById(R.id.closeCarouselTxtView);
        // filterImageview = (ImageView) findViewById(R.id.filterImageview);

        FloatingActionButton btnMenu = (FloatingActionButton) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> getFileSelector().openFileSelection(FileSelector.FileType.Image));

        /*cameraItem = (FloatingActionButton) findViewById(R.id.cameraItem);
        galleryItem = (FloatingActionButton) findViewById(R.id.galleryItem);*/


        adapter = new GalleryImagesRecyclerAdapter(getActContext(), listData, generalFunc, false, true);

        galleryRecyclerView.setAdapter(adapter);


        addToClickHandler(backImgView);
        /*cameraItem.setOnClickListener(this);
        galleryItem.setOnClickListener(this);*/
//        carouselContainerView.setOnClickListener(this);
        addToClickHandler(closeCarouselTxtView);

        setLabels();
//
//        if (APP_TYPE.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
//            filterImageview.setImageDrawable(getResources().getDrawable(R.mipmap.ic_menu_help));
//            filterImageview.setPadding(Utils.dipToPixels(getActContext(), 13), Utils.dipToPixels(getActContext(), 13), Utils.dipToPixels(getActContext(), 13), Utils.dipToPixels(getActContext(), 13));
//            filterImageview.setVisibility(View.VISIBLE);
//
//            filterImageview.setOnClickListener(this);
//        }

        /*Drawable mCameraDrawable = ContextCompat.getDrawable(getActContext(), R.mipmap.ic_camera_fab);
        Drawable mGalleryDrawable = ContextCompat.getDrawable(getActContext(), R.mipmap.ic_gallery_fab);
        if (mCameraDrawable != null && mGalleryDrawable != null) {
            mCameraDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.SRC_IN));
            cameraItem.setImageDrawable(mCameraDrawable);

            mGalleryDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.SRC_IN));
            galleryItem.setImageDrawable(mGalleryDrawable);
        }*/


        GridLayoutManager gridLay = new GridLayoutManager(getActContext(), 3);

        galleryRecyclerView.setLayoutManager(gridLay);

        adapter.setOnItemClickListener(this);
        getImages();
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
    }

    private void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Manage Gallery", "LBL_MANAGE_GALLARY"));
        /*cameraItem.setLabelText(generalFunc.retrieveLangLBl("", "LBL_CAMERA"));
        galleryItem.setLabelText(generalFunc.retrieveLangLBl("", "LBL_GALLERY"));*/
        closeCarouselTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));
    }

    private void getImages() {
        loading_images.setVisibility(View.VISIBLE);
        noteview.setVisibility(View.GONE);
        listData.clear();

        adapter.notifyDataSetChanged();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getStoreBannerImages");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iCompanyId", generalFunc.getMemberId());


        ApiHandler.execute(getActContext(), parameters,
                responseString -> {
                    JSONObject responseStringObject = generalFunc.getJsonObject(responseString);

                    if (responseStringObject != null && !responseStringObject.toString().equalsIgnoreCase("")) {

                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObject.toString())) {
                            listData.clear();

                            JSONArray arr_data = generalFunc.getJsonArray(Utils.message_str, responseStringObject.toString());

                            if (arr_data != null) {
                                for (int i = 0; i < arr_data.length(); i++) {
                                    JSONObject obj_tmp = generalFunc.getJsonObject(arr_data, i);

                                    HashMap<String, String> mapData = new HashMap<>();
                                    Iterator<String> keysItr = obj_tmp.keys();
                                    while (keysItr.hasNext()) {
                                        String key = keysItr.next();
                                        String value = generalFunc.getJsonValueStr(key, obj_tmp);

                                        mapData.put(key, value);
                                    }
                                    listData.add(mapData);
                                }
                            }

                            adapter.notifyDataSetChanged();

                            if (listData.size() == 0) {
                                noteview.setVisibility(View.VISIBLE);
                            }
                        } else {
                            noteview.setVisibility(View.VISIBLE);
                        }
                    } else {
                        generalFunc.showError(true);
                    }
                    loading_images.setVisibility(View.GONE);
                });

    }

    ViewListener viewListener = position -> {
        ImageView customView = new ImageView(getActContext());

        CarouselView.LayoutParams layParams = new CarouselView.LayoutParams(CarouselView.LayoutParams.MATCH_PARENT, CarouselView.LayoutParams.MATCH_PARENT);
//        layParams.leftMargin = Utils.dipToPixels(getActContext(), 15);
//        layParams.rightMargin = Utils.dipToPixels(getActContext(), 15);
        customView.setLayoutParams(layParams);

        customView.setPadding(Utils.dipToPixels(getActContext(), 15), 0, Utils.dipToPixels(getActContext(), 15), 0);
        customView.setImageResource(R.mipmap.ic_no_icon);

        final HashMap<String, String> item = listData.get(position);
        new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(getActContext(), item.get("vImage"), ((int) Utils.getScreenPixelWidth(getActContext())) - Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30))), customView).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();


        return customView;
    };

    private Context getActContext() {
        return MyGalleryActivity.this;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImgView:
                MyGalleryActivity.super.onBackPressed();
                break;
//            case R.id.filterImageview:
//                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_GALLERY_IMG_NOTE"));
//                break;
            case R.id.closeCarouselTxtView:
                if (carouselContainerView.getVisibility() == View.VISIBLE) {
                    carouselContainerView.setVisibility(View.GONE);
                }
                break;
           /* case R.id.cameraItem:
                imgAddOptionMenu.close(true);
                if (MyApp.getInstance().checkPermission(generalFunc, true, true)) {
                    if (!isDeviceSupportCamera()) {
                        generalFunc.showMessage(generalFunc.getCurrentView(MyGalleryActivity.this), generalFunc.retrieveLangLBl("", "LBL_NOT_SUPPORT_CAMERA_TXT"));
                    } else {
                        chooseFromCamera();
                    }
                }
                break;
            case R.id.galleryItem:
                imgAddOptionMenu.close(true);
                if (MyApp.getInstance().checkPermission(generalFunc, true, false)) {
                    chooseFromGallery();
                }
                break;*/
        }
    }

    @Override
    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {
        configProviderImage("", "ADD", mFilePath);
    }

    private void configProviderImage(String iImageId, String action_type, String selectedImagePath) {

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generalFunc.generateImageParams("type", "configStoreBannerImages"));
        paramsList.add(generalFunc.generateImageParams("iCompanyId", generalFunc.getMemberId()));
        paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
        paramsList.add(generalFunc.generateImageParams("action_type", action_type));
        paramsList.add(generalFunc.generateImageParams("iImageId", iImageId));

        UploadProfileImage uploadProfileImage = new UploadProfileImage(MyGalleryActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, "GALLERY");
        uploadProfileImage.execute(Utils.checkText(selectedImagePath), generalFunc.retrieveLangLBl("", "LBL_IMAGES_UPLOADING"));
    }

    public void handleImgUploadResponse(String responseString, String imageUploadedType) {
        //selectedImagePath = "";
        if (responseString != null && !responseString.equals("")) {
            if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                getImages();
            }
            generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
        } else {
            generalFunc.showError();
        }
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
        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DELETE_IMG_CONFIRM_NOTE"), generalFunc.retrieveLangLBl("", "LBL_NO"), generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
            if (buttonId == 1) {
                //selectedImagePath = "";
                configProviderImage(listData.get(position).get("iImageId"), "DELETE", "");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (carouselContainerView.getVisibility() == View.VISIBLE) {
            carouselContainerView.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }
}