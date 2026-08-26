package com.multixpro.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.activity.ParentActivity;
import com.adapter.files.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fragments.GalleryFragment;
import com.fragments.ReviewsFragment;
import com.fragments.ServiceFragment;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.material.tabs.TabLayout;
import com.realmModel.CarWashCartData;
import com.utils.CommonUtilities;
import com.utils.LoadImage;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CounterFab;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.carouselview.CarouselView;
import com.view.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class MoreInfoActivity extends ParentActivity {

    MTextView titleTxt, bottomViewDescTxt;
    public boolean isVideoConsultEnable;
    ImageView backImgView;

    CharSequence[] titles;
    ImageView rightImgView;
    ArrayList<Fragment> fragmentList = new ArrayList<>();

    ViewPager view_pager;
    RelativeLayout bottomCartView, bottomContinueView;
    MTextView itemNpricecartTxt, viewCartTxt;
    RealmResults<CarWashCartData> realmCartList;
    CounterFab cartView;

    CarouselView carouselView;
    MTextView closeCarouselTxtView;
    View carouselContainerView;
    ArrayList<HashMap<String, String>> galleryListData = new ArrayList<>();
    ImageView driverStatus;
    public ImageView searchImgView;
    int SEARCH_CATEGORY = 001;
    private ServiceFragment mServiceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);


        // SimpleRatingBar bottomViewratingBar = (SimpleRatingBar) findViewById(R.id.bottomViewratingBar);
        MTextView nameTxt = (MTextView) findViewById(R.id.bottomViewnameTxt);
        MTextView rateTxt = (MTextView) findViewById(R.id.rateTxt);
        LinearLayout Rating = findViewById(R.id.Rating);

        searchImgView = findViewById(R.id.searchImgView);
        addToClickHandler(searchImgView);
        if (generalFunc.getJsonValueStr("ENABLE_SEARCH_UFX_SERVICES", obj_userProfile) != null &&
                generalFunc.getJsonValueStr("ENABLE_SEARCH_UFX_SERVICES", obj_userProfile).equalsIgnoreCase("Yes")) {
            searchImgView.setVisibility(View.VISIBLE);
        }
        view_pager = (ViewPager) findViewById(R.id.view_pager);

        isVideoConsultEnable = getIntent().getBooleanExtra("isVideoConsultEnable", false);

        bottomViewDescTxt = (MTextView) findViewById(R.id.bottomViewDescTxt);
        bottomViewDescTxt.setVisibility(View.GONE);

        bottomContinueView = (RelativeLayout) findViewById(R.id.bottomContinueView);
        bottomContinueView.setVisibility(View.GONE);
        if (isVideoConsultEnable) {
            searchImgView.setVisibility(View.GONE);
            addToClickHandler(bottomContinueView);
            MTextView txtContinue = findViewById(R.id.txtContinue);
            txtContinue.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
        }

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        bottomCartView = (RelativeLayout) findViewById(R.id.bottomCartView);
        addToClickHandler(bottomCartView);
        cartView = (CounterFab) findViewById(R.id.cartView);
        itemNpricecartTxt = (MTextView) findViewById(R.id.itemNpricecartTxt);
        viewCartTxt = (MTextView) findViewById(R.id.viewCartTxt);
        viewCartTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CHECKOUT"));
        backImgView = (ImageView) findViewById(R.id.backImgView);
        addToClickHandler(backImgView);
        rightImgView = (ImageView) findViewById(R.id.rightImgView);
        driverStatus = (ImageView) findViewById(R.id.driverStatus);


        carouselContainerView = findViewById(R.id.carouselContainerView);
        carouselView = (CarouselView) findViewById(R.id.carouselView);
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
        closeCarouselTxtView = (MTextView) findViewById(R.id.closeCarouselTxtView);
        addToClickHandler(rightImgView);
        TabLayout material_tabs = (TabLayout) findViewById(R.id.material_tabs);
        nameTxt.setText(getIntent().getStringExtra("name"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SERVICE_DETAIL"));
        int padding = Utils.dipToPixels(getActContext(), 14);
        rightImgView.setPadding(padding, padding, padding, padding);
        rightImgView.setImageResource(R.drawable.ic_information);

        titles = new CharSequence[]{generalFunc.retrieveLangLBl("", "LBL_SERVICES"), generalFunc.retrieveLangLBl("", "LBL_GALLERY"), generalFunc.retrieveLangLBl("", "LBL_REVIEWS")};
        fragmentList.add(generatServiceFrag());
        fragmentList.add(generatGalleryFrag());
        fragmentList.add(generatReviewsFrag());
        addToClickHandler(closeCarouselTxtView);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, fragmentList);
        view_pager.setAdapter(adapter);
        material_tabs.setupWithViewPager(view_pager);
        // bottomViewratingBar.setRating(GeneralFunctions.parseFloatValue(0, getIntent().getStringExtra("average_rating")));
        rateTxt.setText(getIntent().getStringExtra("average_rating"));
        if (getIntent().getStringExtra("average_rating").equalsIgnoreCase("")) {
            Rating.setVisibility(View.GONE);
        }

        String image_url = CommonUtilities.PROVIDER_PHOTO_PATH + getIntent().getStringExtra("iDriverId") + "/" + getIntent().getStringExtra("driver_img");

        if (getIntent().getStringExtra("IS_PROVIDER_ONLINE").equalsIgnoreCase("Yes")) {
            driverStatus.setColorFilter(ContextCompat.getColor(getActContext(), R.color.Green));
        } else {
            driverStatus.setColorFilter(ContextCompat.getColor(getActContext(), R.color.Red));
        }


        new LoadImage.builder(LoadImage.bind(image_url), ((SelectableRoundedImageView) findViewById(R.id.bottomViewdriverImgView))).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();


        closeCarouselTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    if (generalFunc.getJsonValueStr("ENABLE_SEARCH_UFX_SERVICES", obj_userProfile) != null &&
                            generalFunc.getJsonValueStr("ENABLE_SEARCH_UFX_SERVICES", obj_userProfile).equalsIgnoreCase("Yes")) {
                        if (isVideoConsultEnable) {
                            searchImgView.setVisibility(View.GONE);
                        } else {
                            searchImgView.setVisibility(View.VISIBLE);
                        }
                    }

                } else {

                    searchImgView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    public void onResumeCall() {
        onResume();
        if (getIntent().getStringExtra("tProfileDescription") != null && !getIntent().getStringExtra("tProfileDescription").equalsIgnoreCase("")) {
            bottomViewDescTxt.setVisibility(View.VISIBLE);
            bottomViewDescTxt.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW_PROFILE_DESCRIPTION"));
            addToClickHandler(bottomViewDescTxt);
        }

        if (isVideoConsultEnable) {
            bottomViewDescTxt.setVisibility(View.VISIBLE);
            bottomViewDescTxt.setText(mServiceFragment.eVideoConsultServiceCharge);
            bottomViewDescTxt.setOnClickListener(null);
            bottomContinueView.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams pagerLayoutParams = (ViewGroup.MarginLayoutParams) view_pager.getLayoutParams();
            pagerLayoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.all_btn_height));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.UFX_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getBooleanExtra("isVideoConsultEnable", false)) {
                bottomCartView.performClick();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        realmCartList = getCartData();
        double finlaTotal = 0;
        ViewGroup.MarginLayoutParams pagerLayoutParams = (ViewGroup.MarginLayoutParams) view_pager.getLayoutParams();
        if (realmCartList.size() > 0) {
            int cnt = 0;
            for (int i = 0; i < realmCartList.size(); i++) {
                if (realmCartList.get(i).isVideoConsultEnable()) {
                    bottomCartView.setVisibility(View.GONE);
                    return;
                }
                CarWashCartData itemPos = realmCartList.get(i);
                cnt = cnt + GeneralFunctions.parseIntegerValue(0, itemPos.getItemCount());

                double price = GeneralFunctions.parseDoubleValue(0, itemPos.getFinalTotal().replace(itemPos.getvSymbol(), ""));
                finlaTotal = finlaTotal + price;


            }

            bottomCartView.setVisibility(View.VISIBLE);
            pagerLayoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._55sdp));

            itemNpricecartTxt.setText("");
            if (finlaTotal > 0) {
                itemNpricecartTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(finlaTotal) + ""), realmCartList.get(0).getvSymbol(), true));
            }


            cartView.setCount(cnt);

        } else {
            bottomCartView.setVisibility(View.GONE);
            pagerLayoutParams.setMargins(0, 0, 0, 0);
        }
    }

    public ServiceFragment generatServiceFrag() {
        mServiceFragment = new ServiceFragment();
        Bundle bn = new Bundle();
        bn.putString("parentId", getIntent().getStringExtra("parentId"));
        bn.putString("SelectedVehicleTypeId", getIntent().getStringExtra("SelectedVehicleTypeId"));
        bn.putBoolean("isVideoConsultEnable", isVideoConsultEnable);
        mServiceFragment.setArguments(bn);
        return mServiceFragment;
    }

    public GalleryFragment generatGalleryFrag() {
        GalleryFragment frag = new GalleryFragment();
        Bundle bn = new Bundle();
        frag.setArguments(bn);
        return frag;
    }

    public ReviewsFragment generatReviewsFrag() {
        ReviewsFragment frag = new ReviewsFragment();
        Bundle bn = new Bundle();
        frag.setArguments(bn);
        return frag;
    }

    public RealmResults<CarWashCartData> getCartData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(CarWashCartData.class).findAll();
    }

    ViewListener viewListener = position -> {
        ImageView customView = new ImageView(getActContext());

        CarouselView.LayoutParams layParams = new CarouselView.LayoutParams(CarouselView.LayoutParams.MATCH_PARENT, CarouselView.LayoutParams.MATCH_PARENT);
        customView.setLayoutParams(layParams);

        int padding = Utils.dipToPixels(getActContext(), 15);
        customView.setPadding(padding, 0, padding, 0);


        final HashMap<String, String> item = galleryListData.get(position);

        int dip = Utils.dipToPixels(getActContext(), 30);
        if (item.get("eFileType").equals("Video")) {
            customView.setImageResource(R.drawable.ic_novideo__icon);
            if (!TextUtils.isEmpty(item.get("ThumbImage"))) {
                String imageUrl = Utils.getResizeImgURL(getActContext(), item.get("ThumbImage"), (int) Utils.getScreenPixelWidth(getActContext()) - dip, 0, Utils.getScreenPixelHeight(getActContext()) - dip);
                Glide.with(getActContext())
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_novideo__icon).error(R.drawable.ic_novideo__icon))
                        .into(customView);
                customView.setOnClickListener(v -> showVideoDialog(item.get("ThumbImage"), item.get("vImage")));
            }

        } else {
            customView.setImageResource(R.mipmap.ic_no_icon);
            if (!TextUtils.isEmpty(item.get("vImage"))) {
                String imageUrl = Utils.getResizeImgURL(getActContext(), item.get("vImage"), (int) Utils.getScreenPixelWidth(getActContext()) - dip, 0, Utils.getScreenPixelHeight(getActContext()) - dip);
                Glide.with(getActContext())
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon))
                        .into(customView);
                customView.setOnClickListener(null);
            }
        }


        return customView;
    };

    public void openCarouselView(ArrayList<HashMap<String, String>> galleryListData, int currentPosition) {
        this.galleryListData = galleryListData;
        manageIcon(currentPosition);
        carouselContainerView.setVisibility(View.VISIBLE);
        carouselView.setViewListener(viewListener);
        carouselView.setPageCount(galleryListData.size());
        carouselView.setCurrentItem(currentPosition);
    }

    private void manageIcon(int pos) {
        manageVectorImage(findViewById(R.id.playIconBtn), R.drawable.ic_play_video, R.drawable.ic_play_video_compat);
        if (galleryListData.get(pos).get("eFileType").equals("Video")) {
            findViewById(R.id.playIconBtn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.playIconBtn).setVisibility(View.GONE);
        }
    }

    public void showVideoDialog(String thumbUrl, String videoUrl) {
        AlertDialog builder = new AlertDialog.Builder(getActContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen).create();

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_play_video, null);
        builder.setView(dialogView);

        final PlayerView exoVideoPlayer = (PlayerView) dialogView.findViewById(R.id.player_view);

        final ImageView closeVideoView = (ImageView) dialogView.findViewById(R.id.closeVideoView);
        ProgressBar mProgressBar = (ProgressBar) dialogView.findViewById(R.id.mProgressBar);
        //final Player player;
        //player = ExoPlayerFactory.newSimpleInstance(this);
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
        Glide.with(getActContext())
                .load(imageUrl)
                .apply(new RequestOptions().placeholder(R.drawable.ic_novideo__icon).error(R.drawable.ic_novideo__icon))
                .into(thumbnailImage);
        thumbnailImage.setVisibility(View.VISIBLE);
        player.addListener(new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                Logger.d("onPlaybackStateChanged", "::" + player.getPlaybackState());
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
    public void onBackPressed() {
        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.delete(CarWashCartData.class);
        realm.commitTransaction();
        super.onBackPressed();
    }

    public Context getActContext() {
        return MoreInfoActivity.this;
    }


    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        switch (view.getId()) {

            case R.id.searchImgView:

                Bundle bundlesearch = new Bundle();
                bundlesearch.putString("iDriverId", getIntent().getStringExtra("iDriverId"));
                bundlesearch.putString("parentId", getIntent().getStringExtra("parentId"));
                bundlesearch.putString("SelectedVehicleTypeId", getIntent().getStringExtra("SelectedVehicleTypeId"));
                bundlesearch.putString("latitude", getIntent().getStringExtra("latitude"));
                bundlesearch.putString("longitude", getIntent().getStringExtra("longitude"));
                bundlesearch.putString("address", getIntent().getStringExtra("address"));

                new ActUtils(getActContext()).startActForResult(SearchCategoryActivity.class, bundlesearch, SEARCH_CATEGORY);


                break;
            case R.id.closeCarouselTxtView:
                if (carouselContainerView.getVisibility() == View.VISIBLE) {
                    carouselContainerView.setVisibility(View.GONE);
                }
                break;
            case R.id.backImgView:
                onBackPressed();
                break;
            case R.id.bottomContinueView:
                if (mServiceFragment == null || mServiceFragment.allCategoryItemsList == null || mServiceFragment.allCategoryItemsList.size() == 0) {
                    return;
                }
                Bundle bn1 = new Bundle();
                bn1.putAll(getIntent().getExtras());
                bn1.putSerializable("data", mServiceFragment.allCategoryItemsList.get(mServiceFragment.allCategoryItemsList.size() - 1));
                bn1.putString("iDriverId", getIntent().getStringExtra("iDriverId"));
                bn1.putBoolean("isVideoConsultEnable", isVideoConsultEnable);
                new ActUtils(getActContext()).startActForResult(UberxCartActivity.class, bn1, Utils.UFX_REQUEST_CODE);
                break;
            case R.id.bottomCartView:
                Bundle bn = new Bundle();
                bn.putString("name", getIntent().getStringExtra("name"));
                bn.putString("serviceName", getIntent().getStringExtra("serviceName"));
                bn.putString("iDriverId", getIntent().getStringExtra("iDriverId"));
                bn.putString("latitude", getIntent().getStringExtra("latitude"));
                bn.putString("longitude", getIntent().getStringExtra("longitude"));
                bn.putString("average_rating", getIntent().getStringExtra("average_rating"));
                bn.putString("driver_img", getIntent().getStringExtra("driver_img"));
                bn.putString("address", getIntent().getStringExtra("address"));
                bn.putString("vProviderLatitude", getIntent().getStringExtra("vProviderLatitude"));
                bn.putString("vProviderLongitude", getIntent().getStringExtra("vProviderLongitude"));
                bn.putBoolean("isVideoConsultEnable", isVideoConsultEnable);
                new ActUtils(getActContext()).startActWithData(CarWashBookingDetailsActivity.class, bn);
                break;
            case R.id.bottomViewDescTxt:
                Bundle bundle = new Bundle();
                bundle.putString("iDriverId", getIntent().getStringExtra("iDriverId"));
                bundle.putString("average_rating", getIntent().getStringExtra("average_rating"));
                bundle.putString("driver_img", getIntent().getStringExtra("driver_img"));
                bundle.putString("name", getIntent().getStringExtra("name"));
                bundle.putString("fname", getIntent().getStringExtra("fname"));

                new ActUtils(getActContext()).startActWithData(ProviderInfoActivity.class, bundle);
                break;
        }
    }


}
