package com.multixpro.provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.activity.ParentActivity;
import com.adapter.files.GalleryImagesRecyclerAdapter;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.UploadProfileImage;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.model.ServiceModule;
import com.service.handler.ApiHandler;
import com.utils.LoadImageGlide;
import com.utils.Utils;
import com.view.FloatingAction.FloatingActionButton;
import com.view.FloatingAction.FloatingActionMenu;
import com.view.MTextView;
import com.view.carouselview.CarouselView;
import com.view.carouselview.ViewListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MyGalleryActivity extends ParentActivity implements GalleryImagesRecyclerAdapter.OnItemClickListener {

    RecyclerView galleryRecyclerView;
    MTextView titleTxt, closeCarouselTxtView;
    ImageView backImgView, filterImageview;
    AppCompatImageView noImgView;
    ProgressBar loading_images;
    CarouselView carouselView;
    FloatingActionMenu imgAddOptionMenu;
    FloatingActionButton cameraItem, galleryItem;

    private View carouselContainerView;
    GalleryImagesRecyclerAdapter adapter;
    ArrayList<HashMap<String, String>> listData = new ArrayList<>();
    String userProfileJson;

    ViewListener viewListener = position -> {
        ImageView customView = new ImageView(getActContext());

        CarouselView.LayoutParams layParams = new CarouselView.LayoutParams(CarouselView.LayoutParams.MATCH_PARENT, CarouselView.LayoutParams.MATCH_PARENT);
        customView.setLayoutParams(layParams);

        customView.setPadding(Utils.dipToPixels(getActContext(), 15), 0, Utils.dipToPixels(getActContext(), 15), 0);


        final HashMap<String, String> item = listData.get(position);

        if (listData.get(position).get("eFileType").equals("Video")) {
            customView.setImageResource(R.drawable.ic_novideo__icon);
            if (!TextUtils.isEmpty(item.get("ThumbImage"))) {
                String imageUrl = Utils.getResizeImgURL(getActContext(), item.get("ThumbImage"), ((int) Utils.getScreenPixelWidth(getActContext())) - Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30));

                new LoadImageGlide.builder(getActContext(), LoadImageGlide.bind(imageUrl), customView).setErrorImagePath(R.drawable.ic_novideo__icon).setPlaceholderImagePath(R.drawable.ic_novideo__icon).build();
                customView.setOnClickListener(v -> showVideoDialog(item.get("ThumbImage"), item.get("vImage")));
            }

        } else {
            if (!TextUtils.isEmpty(item.get("vImage"))) {
                customView.setImageResource(R.mipmap.ic_no_icon);
                String imageUrl = Utils.getResizeImgURL(getActContext(), item.get("vImage"), ((int) Utils.getScreenPixelWidth(getActContext())) - Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30));
                new LoadImageGlide.builder(getActContext(), LoadImageGlide.bind(imageUrl), customView).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();
                customView.setOnClickListener(null);


            }

        }

        return customView;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        noImgView = (AppCompatImageView) findViewById(R.id.noImgView);
        galleryRecyclerView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        imgAddOptionMenu = (FloatingActionMenu) findViewById(R.id.imgAddOptionMenu);
        loading_images = (ProgressBar) findViewById(R.id.loading_images);
        carouselContainerView = findViewById(R.id.carouselContainerView);
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        closeCarouselTxtView = (MTextView) findViewById(R.id.closeCarouselTxtView);
        filterImageview = (ImageView) findViewById(R.id.filterImageview);

        cameraItem = (FloatingActionButton) findViewById(R.id.cameraItem);
        galleryItem = (FloatingActionButton) findViewById(R.id.galleryItem);

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

        adapter = new GalleryImagesRecyclerAdapter(getActContext(), listData, generalFunc, false);

        galleryRecyclerView.setAdapter(adapter);

        addToClickHandler(backImgView);
        addToClickHandler(cameraItem);
        addToClickHandler(galleryItem);
        addToClickHandler(closeCarouselTxtView);

        setLabels();

        if (ServiceModule.ServiceProvider) {
            filterImageview.setImageDrawable(ContextCompat.getDrawable(getActContext(), R.mipmap.ic_menu_help));
            filterImageview.setVisibility(View.VISIBLE);
            addToClickHandler(filterImageview);
        }

        Drawable mGalleryDrawable = ContextCompat.getDrawable(getActContext(), R.mipmap.ic_gallery_fab);
        Drawable mCameraDrawable = ContextCompat.getDrawable(getActContext(), R.mipmap.ic_camera_fab);

        if (mGalleryDrawable != null && mCameraDrawable != null) {
            mGalleryDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.SRC_IN));
            mCameraDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.SRC_IN));
            galleryItem.setImageDrawable(mGalleryDrawable);
            cameraItem.setImageDrawable(mCameraDrawable);
        }

        GridLayoutManager gridLay = new GridLayoutManager(getActContext(), 3);

        galleryRecyclerView.setLayoutManager(gridLay);

        adapter.setOnItemClickListener(this);
        getImages();

        carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                manageIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }

        FloatingActionButton btnMenu = (FloatingActionButton) findViewById(R.id.btnMenu);
        if (generalFunc.getJsonValueStr("ENABLE_VIDEO_UPLOAD_SP", obj_userProfile).equalsIgnoreCase("Yes")) {
            imgAddOptionMenu.setVisibility(View.VISIBLE);
            btnMenu.setVisibility(View.GONE);
        } else {
            imgAddOptionMenu.setVisibility(View.GONE);
            btnMenu.setVisibility(View.VISIBLE);
            btnMenu.setOnClickListener(v -> getFileSelector().openFileSelection(FileSelector.FileType.Image));
        }
    }

    private void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("Manage Gallery", "LBL_MANAGE_GALLARY"));
        cameraItem.setLabelText(generalFunc.retrieveLangLBl("", "LBL_PHOTO"));
        galleryItem.setLabelText(generalFunc.retrieveLangLBl("", "LBL_VIDEO"));
        closeCarouselTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));
    }

    private void getImages() {
        loading_images.setVisibility(View.VISIBLE);
        noImgView.setVisibility(View.GONE);
        listData.clear();

        adapter.notifyDataSetChanged();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getProviderImages");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iDriverId", generalFunc.getMemberId());
        parameters.put("SelectedCabType", Utils.CabGeneralType_UberX);

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
                                noImgView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            noImgView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        generalFunc.showError(true);
                    }
                    loading_images.setVisibility(View.GONE);
                });

    }

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
            case R.id.filterImageview:
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_GALLERY_IMG_NOTE"));
                break;
            case R.id.closeCarouselTxtView:
                if (carouselContainerView.getVisibility() == View.VISIBLE) {
                    carouselContainerView.setVisibility(View.GONE);
                }
                break;
            case R.id.cameraItem:
                imgAddOptionMenu.close(true);
                ImageSourceDialog(true);
                break;
            case R.id.galleryItem:
                imgAddOptionMenu.close(true);
                ImageSourceDialog(false);
                break;
        }
    }

    private void configProviderImage(String iImageId, String action_type, String selectedImagePath) {

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generalFunc.generateImageParams("type", "configProviderImages"));
        paramsList.add(generalFunc.generateImageParams("iDriverId", generalFunc.getMemberId()));
        paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
        paramsList.add(generalFunc.generateImageParams("action_type", action_type));
        paramsList.add(generalFunc.generateImageParams("iImageId", iImageId));

        UploadProfileImage uploadProfileImage = new UploadProfileImage(MyGalleryActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, "GALLERY");
        uploadProfileImage.execute(Utils.checkText(selectedImagePath), generalFunc.retrieveLangLBl("", "LBL_IMAGES_UPLOADING"));

    }

    private void configProviderVideo(String iVideoId, String action_type, String selectedVideoPath) {

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generalFunc.generateImageParams("type", "configProviderImages"));
        paramsList.add(generalFunc.generateImageParams("iDriverId", generalFunc.getMemberId()));
        paramsList.add(generalFunc.generateImageParams("UserType", Utils.app_type));
        paramsList.add(generalFunc.generateImageParams("action_type", action_type));
        paramsList.add(generalFunc.generateImageParams("iImageId", iVideoId));

        String videoFormat;
        int index = selectedVideoPath.lastIndexOf(".");
        if (index > 0) {
            videoFormat = selectedVideoPath.substring(index + 1, selectedVideoPath.length());
            UploadProfileImage uploadProfileImage = new UploadProfileImage(MyGalleryActivity.this, selectedVideoPath, "temp_video." + videoFormat, paramsList, "GALLERY");
            uploadProfileImage.execute(Utils.checkText(selectedVideoPath), generalFunc.retrieveLangLBl("", "LBL_VIDEO_UPLOADING"));
        }
    }

    public void handleImgUploadResponse(String responseString, String imageUploadedType) {
        if (responseString != null && !responseString.equals("")) {
            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail) {
                getImages();
            }

            generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
        } else {
            generalFunc.showError();
        }
    }


    @Override
    public void onItemClickList(View v, int position) {
        manageIcon(position);
        carouselContainerView.setVisibility(View.VISIBLE);
        carouselView.setViewListener(viewListener);
        carouselView.setPageCount(listData.size());
        carouselView.setCurrentItem(position);

    }

    private void manageIcon(int pos) {
        manageVectorImage(findViewById(R.id.playIconBtn), R.drawable.ic_play_video, R.drawable.ic_play_video_compat);
        if (listData.get(pos).get("eFileType").equals("Video")) {
            findViewById(R.id.playIconBtn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.playIconBtn).setVisibility(View.GONE);
        }
    }

    private void showVideoDialog(String thumbUrl, String videoUrl) {
        AlertDialog builder = new AlertDialog.Builder(getActContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen).create();

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_play_video, null);
        builder.setView(dialogView);

        final PlayerView exoVideoPlayer = (PlayerView) dialogView.findViewById(R.id.player_view);

        final ImageView closeVideoView = (ImageView) dialogView.findViewById(R.id.closeVideoView);
        ProgressBar mProgressBar = (ProgressBar) dialogView.findViewById(R.id.mProgressBar);
        /*final Player player;
        player = ExoPlayerFactory.newSimpleInstance(this);*/
        ExoPlayer player = new ExoPlayer.Builder(this).build();

        int width = 0;
        int height = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            width = windowMetrics.getBounds().width() - insets.left - insets.right;
            height = windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            height = dm.heightPixels;
            width = dm.widthPixels;
        }
        exoVideoPlayer.setMinimumWidth(width);
        exoVideoPlayer.setMinimumHeight(height);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(videoUrl)
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .build();
        player.setMediaItem(mediaItem);
        // Bind the player to the view.
        exoVideoPlayer.setPlayer(player);
        player.prepare();
        mProgressBar.setVisibility(View.VISIBLE);

        final ImageView thumbnailImage = (ImageView) dialogView.findViewById(R.id.thumbnailImage);

        String imageUrl = Utils.getResizeImgURL(getActContext(), thumbUrl, ((int) Utils.getScreenPixelWidth(getActContext())) -
                Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30));

        new LoadImageGlide.builder(getActContext(), LoadImageGlide.bind(imageUrl), thumbnailImage).setErrorImagePath(R.drawable.ic_novideo__icon).setPlaceholderImagePath(R.drawable.ic_novideo__icon).build();
        thumbnailImage.setVisibility(View.VISIBLE);
        player.addListener(new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                    thumbnailImage.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (player.getPlaybackState() == ExoPlayer.STATE_READY) {
                    mProgressBar.setVisibility(View.GONE);
                    thumbnailImage.setVisibility(View.GONE);
                } else if (player.getPlaybackState() == ExoPlayer.STATE_IDLE) {
                    thumbnailImage.setVisibility(View.VISIBLE);
                }
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) exoVideoPlayer.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            exoVideoPlayer.setLayoutParams(params);
        }

        closeVideoView.setOnClickListener(v -> {
            player.release();
            builder.dismiss();
        });

        player.setPlayWhenReady(true); //run file/link when ready to play.
        builder.show();
    }

    @Override
    public void onDeleteClick(View v, int position) {
        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DELETE_IMG_CONFIRM_NOTE"), generalFunc.retrieveLangLBl("", "LBL_NO"), generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {

            if (buttonId == 1) {
                configProviderImage(listData.get(position).get("iImageId"), "DELETE", "");
            }

        });
    }

    @Override
    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {
        if (mFileType == FileSelector.FileType.Video) {
            configProviderVideo("", "ADD", mFilePath);
        } else {
            configProviderImage("", "ADD", mFilePath);
        }
    }

    @Override
    public void onBackPressed() {
        if (carouselContainerView.getVisibility() == View.VISIBLE) {
            carouselContainerView.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    private void ImageSourceDialog(boolean isCamera) {
        if (isCamera) {
            getFileSelector().openFileSelection(FileSelector.FileType.Image);
        } else {
            getFileSelector().openFileSelection(FileSelector.FileType.Video);
        }
    }
}