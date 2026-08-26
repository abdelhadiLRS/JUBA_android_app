package com.multixpro.user.deliverAll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.activity.ParentActivity;
import com.adapter.files.MyImageAdapter;
import com.adapter.files.deliverAll.MultiItemOptionAddonPagerAdapter;
import com.autofit.et.lib.AutoFitEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.general.files.EnhancedWrapContentViewPager;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.material.tabs.TabLayout;
import com.multixpro.user.R;
import com.realmModel.Cart;
import com.realmModel.Options;
import com.realmModel.Topping;
import com.service.handler.ApiHandler;
import com.shuhart.stepview.StepView;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.carouselview.CarouselView;
import com.view.carouselview.ViewListener;
import com.view.editBox.MaterialEditText;
import com.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AddBasketActivity extends ParentActivity implements MyImageAdapter.OnActivePosition {

    ImageView backImgView, productimage;
    MTextView titleTxt;

    MTextView vItemNameTxt, vItemDescTxt, baseFareHTxt, baseFareVTxt, topingTitleTxt, optionTitleTxt;
    LinearLayout optionContainer, topingContainer;
    LinearLayout topingArea;
    LinearLayout optionArea;
    String MenuItemOptionToppingArr;
    String itemImageVideoArr;
    String data;
    //ImageView minuscntImgView, addcntImgView;
    MTextView totalHTxt, totalPriceTxt, closeCarouselTxtView;
    ImageView vegImage, nonvegImage;
    MTextView vegNonvegTxtView;

    RealmList<Topping> realmToppingList = new RealmList<>();
    RealmList<Options> realmOptionsList = new RealmList<>();

    RealmResults<Options> realmOptionResult;
    RealmResults<Topping> realmToppingResult;

    HashMap<String, String> searchList;
    //MTextView QTYNumberTxtView;
    MTextView addItemCartBtn;
    String[] selToppingarray;
    ArrayList<String> selToppingList;
    DotsIndicator dotsIndicator;
    RelativeLayout dotsArea;

    String selOptionId = "";

    ArrayList<HashMap<String, String>> optionList = new ArrayList<>();
    ArrayList<HashMap<String, String>> topingList = new ArrayList<>();
    ArrayList<HashMap<String, String>> mediaList = new ArrayList<>();

    ArrayList<String> toppingListId = new ArrayList<>();
    LinearLayout addarea;
    LinearLayout toppingsarea;

    double toppingPrice = 0;

    double seloptionPrice = 0;
    String isTooping = "No";
    String isOption = "No";
    String optionId = "";
    String toppingId = "";
    RealmResults<Cart> realmCartList;
    boolean isCartNull;


    String LBL_SUB_TOTAL = "";

    ImageView minusImageView, addImageView;
    AutoFitEditText autofitEditText;
    MaterialEditText rechargeBox;
    ImageView closeImg;
    RelativeLayout rlImageSlider, rlImage;
    ViewPager pager;
    TabLayout tabLayout;
    CarouselView carouselView;
    View carouselContainerView;
    private int currentStep = 0, btnentStep = 0, btnstep = 1;
    RadioButton lastCheckedRB = null;


    //multiple Option
    EnhancedWrapContentViewPager mViewPager;
    MultiItemOptionAddonPagerAdapter mMultiItemOptionAddonPagerAdapter;
    private CardView multiItemCardView;
    private LinearLayout previousArea, nextArea;
    StepView step_view;
    HorizontalScrollView stepScrollview;
    int x, y = 0;

    ViewListener viewListener = position -> {
        ImageView customView = new ImageView(getActContext());

        CarouselView.LayoutParams layParams = new CarouselView.LayoutParams(CarouselView.LayoutParams.MATCH_PARENT, CarouselView.LayoutParams.MATCH_PARENT);
        customView.setLayoutParams(layParams);

        customView.setPadding(Utils.dipToPixels(getActContext(), 15), 0, Utils.dipToPixels(getActContext(), 15), 0);

        final HashMap<String, String> item = mediaList.get(position);
        if (mediaList.get(position).get("eFileType").equalsIgnoreCase("Video")) {
            customView.setImageResource(R.drawable.ic_novideo__icon);
            if (!TextUtils.isEmpty(item.get("ThumbImage"))) {
                String imageUrl = Utils.getResizeImgURL(getActContext(), item.get("ThumbImage"), ((int) Utils.getScreenPixelWidth(getActContext())) - Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30));
                Glide.with(getActContext())
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_novideo__icon).error(R.drawable.ic_novideo__icon))
                        .into(customView);

                customView.setOnClickListener(v -> showVideoDialog(item.get("ThumbImage"), item.get("vImage")));
            }
        } else {
            customView.setImageResource(R.mipmap.ic_no_icon);
            if (!TextUtils.isEmpty(item.get("vImage"))) {
                String imageUrl = Utils.getResizeImgURL(getActContext(), item.get("vImage"), ((int) Utils.getScreenPixelWidth(getActContext())) - Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30));
                Glide.with(getActContext())
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon))
                        .into(customView);
                customView.setOnClickListener(null);
            }
        }

        return customView;
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_basket);
        //  try {
        x = Utils.dpToPx(50, getActContext());
        searchList = (HashMap<String, String>) getIntent().getSerializableExtra("data");


        initView();


        Realm realm = MyApp.getRealmInstance();
        realmCartList = getCartData();
        Cart cart = realm.where(Cart.class).equalTo("iMenuItemId", searchList.get("iMenuItemId")).findFirst();
        if (cart != null) {
            if (cart.getiToppingId() != null) {
                selToppingarray = cart.getiToppingId().split(",");
                selToppingList = new ArrayList<>(Arrays.asList(selToppingarray));
                selOptionId = cart.getiOptionId();
                // optionId = selOptionId;
                // optionId = selOptionId;
                //toppingListId = selToppingList;
            }
            //QTYNumberTxtView.setText(cart.getQty());
        }
        setData();

        if (generalFunc.getJsonValueStr("ENABLE_MULTI_OPTIONS_ADDONS", obj_userProfile).equalsIgnoreCase("YES")) {
            addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NEXT"));
            getMultiOptionsList();
        } else {
            realmOptionResult = getOptionsData();
            realmToppingResult = getToppingData();
            if (realmOptionResult.size() > 0 || realmToppingResult.size() > 0) {
                if (searchList.get("iCompanyId") != null && !searchList.get("iCompanyId").equalsIgnoreCase(generalFunc.retrieveValue(Utils.COMPANY_ID))) {
                    getOptionsList();
                }
            } else {
                getOptionsList();
            }
        }
        //  } catch (Exception e) {
        //    e.printStackTrace();
        //  }
        manageVectorImage(findViewById(R.id.playIconBtn), R.drawable.ic_play_button, R.drawable.ic_play_button_compat);

    }

    @Override
    public void onActivePosition(int pos) {
        if (pager != null) pager.setCurrentItem(pos);
        Objects.requireNonNull(tabLayout.getTabAt(pos)).select();
        manageIcon(pos);
        carouselContainerView.setVisibility(View.VISIBLE);
        carouselView.setViewListener(viewListener);
        carouselView.setPageCount(mediaList.size());
        carouselView.setCurrentItem(pos);
    }

    private void manageIcon(int pos) {
        if (mediaList.get(pos).get("eFileType").equalsIgnoreCase("Video")) {
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
                if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                    thumbnailImage.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (player.getPlaybackState() == ExoPlayer.STATE_READY) {
                    mProgressBar.setVisibility(View.GONE);
                    thumbnailImage.setVisibility(View.GONE);
                    //exoVideoPlayer.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
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

    public void setupSlider() {

        itemImageVideoArr = searchList.get("MenuItemMedia");
        JSONArray imageArray = generalFunc.getJsonArray(itemImageVideoArr);
        if (imageArray != null && imageArray.length() > 0) {
            mediaList.clear();
            if (generalFunc.isRTLmode()) {
                for (int x = imageArray.length() - 1; x >= 0; x--) {
                    JSONObject imageObject = generalFunc.getJsonObject(imageArray, x);
                    HashMap<String, String> data = new HashMap<>();
                    try {
                        data.put("vImage", imageObject.get("vImage").toString());
                        data.put("eFileType", imageObject.get("eFileType").toString());
                        data.put("ThumbImage", imageObject.get("ThumbImage").toString());
                        mediaList.add(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int i = 0; i < imageArray.length(); i++) {
                    JSONObject imageObject = generalFunc.getJsonObject(imageArray, i);
                    HashMap<String, String> data = new HashMap<>();
                    try {
                        data.put("vImage", imageObject.get("vImage").toString());
                        data.put("eFileType", imageObject.get("eFileType").toString());
                        data.put("ThumbImage", imageObject.get("ThumbImage").toString());
                        mediaList.add(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


            pager = (ViewPager) findViewById(R.id.photos_viewpager);
            MyImageAdapter adapter = new MyImageAdapter(this, mediaList, this);
            pager.setAdapter(adapter);

            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(pager, true);

            //configurable for normal case
            if (mediaList.size() > 1) {
                if (generalFunc.isRTLmode()) {
                    pager.setCurrentItem(mediaList.size() - 1);
                }
                dotsIndicator.setViewPager(pager);
                dotsIndicator.setVisibility(View.VISIBLE);
                dotsArea.setVisibility(View.VISIBLE);
            } else {
                dotsArea.setVisibility(View.GONE);
            }


            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });


        } else {
            rlImageSlider.setVisibility(View.GONE);
            rlImage.setVisibility(View.VISIBLE);
        }

    }

    public void getMultiOptionsList() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetItemOptionAddonDetails");
        parameters.put("iCompanyId", searchList.get("iCompanyId"));
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("eSystem", Utils.eSystem_Type);
        parameters.put("iMenuItemId", searchList.get("iMenuItemId"));

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {
            if (responseString != null && !responseString.equals("")) {
                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    String message = generalFunc.getJsonValue("message", responseString);
                    JSONArray categoryArray = generalFunc.getJsonArray("Category", message);

                    if (categoryArray != null) {
                        if (categoryArray.length() > 0) {
                            multiItemCardView.setVisibility(View.VISIBLE);
                        }
                        if (categoryArray.length() > 1) {
                            nextArea.setVisibility(View.VISIBLE);
                        }
                        mViewPager.setOffscreenPageLimit(categoryArray.length());
                        step_view.setStepsNumber(categoryArray.length());
                        if (categoryArray.length() > 5) {


                            ViewGroup.LayoutParams layoutParams = step_view.getLayoutParams();
                            layoutParams.width = Utils.dpToPx(categoryArray.length() * 100, getActContext());
                            step_view.setLayoutParams(layoutParams);
                        }
                        if (generalFunc.isRTLmode()) {
                            btnstep = categoryArray.length() - 1;
                        }
                        mMultiItemOptionAddonPagerAdapter.setCategoryArrayList(categoryArray, false);
                        setBottomValue();

                        if (step_view.getStepCount() == 1) {
                            step_view.setVisibility(View.GONE);
                            addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));
                        }
                    }


                } else {
                    addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));
                    //    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    private void setBottomValue() {
        totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(((GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * (getQty())) + Math.abs(toppingPrice) + Math.abs(seloptionPrice)))), searchList.get("currencySymbol"), true));
        if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate")/* && optionList.size() > 0*/) {
            if (seloptionPrice == 0) {
                //seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                seloptionPrice = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice"));
            }
            totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay((Math.abs(toppingPrice) + Math.abs(seloptionPrice) * (getQty())) + Math.abs(toppingPrice))), searchList.get("currencySymbol"), true));
        }
    }

    public RealmResults<Cart> getCartData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Cart.class).findAll();
    }

    public void deleteOptionToRealm() {
        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.delete(Options.class);
        realm.commitTransaction();

        Realm realmTopping = MyApp.getRealmInstance();
        realmTopping.beginTransaction();
        realmTopping.delete(Topping.class);
        realmTopping.commitTransaction();
    }

    public Context getActContext() {
        return AddBasketActivity.this;
    }


    public void initView() {
        LBL_SUB_TOTAL = generalFunc.retrieveLangLBl("", "LBL_SUB_TOTAL");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent_full));
        toolbar.getBackground().setAlpha(0);
        step_view = findViewById(R.id.step_view);
        stepScrollview = findViewById(R.id.stepScrollview);
        //stepScrollview.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> Logger.d("onScrollChange", "::" + scrollX + "::" + scrollY));
        mViewPager = (EnhancedWrapContentViewPager) findViewById(R.id.multiItemViewPager);
        //   mStepperAdapter = new StepperAdapter(getSupportFragmentManager(), this,mViewPager);
        //  step_viewLayout.setAdapter(mStepperAdapter);
        //  step_viewLayout.setListener(this);

        closeImg = findViewById(R.id.closeImg);
        addToClickHandler(closeImg);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        productimage = (ImageView) findViewById(R.id.productimage);
        vegImage = (ImageView) findViewById(R.id.vegImage);
        nonvegImage = (ImageView) findViewById(R.id.nonvegImage);
        vegNonvegTxtView = (MTextView) findViewById(R.id.vegNonvegTxtView);
        vItemNameTxt = (MTextView) findViewById(R.id.vItemNameTxt);
        vItemDescTxt = (MTextView) findViewById(R.id.vItemDescTxt);
        baseFareHTxt = (MTextView) findViewById(R.id.baseFareHTxt);
        baseFareVTxt = (MTextView) findViewById(R.id.baseFareVTxt);
        topingTitleTxt = (MTextView) findViewById(R.id.topingTitleTxt);
        optionTitleTxt = (MTextView) findViewById(R.id.optionTitleTxt);
        toppingsarea = (LinearLayout) findViewById(R.id.toppingsarea);
        optionContainer = (LinearLayout) findViewById(R.id.optionContainer);
        topingContainer = (LinearLayout) findViewById(R.id.topingContainer);
        topingArea = (LinearLayout) findViewById(R.id.topingArea);
        optionArea = (LinearLayout) findViewById(R.id.optionArea);
        // addcntImgView = (ImageView) findViewById(R.id.addImgView);
        // minuscntImgView = (ImageView) findViewById(R.id.minusImgView);
        totalHTxt = (MTextView) findViewById(R.id.totalHTxt);
        totalPriceTxt = (MTextView) findViewById(R.id.totalPriceTxt);
        //  QTYNumberTxtView = (MTextView) findViewById(R.id.QTYNumberTxtView);
        addItemCartBtn = (MTextView) findViewById(R.id.addItemCartBtn);
        addarea = (LinearLayout) findViewById(R.id.addarea);
        //  minusarea = (LinearLayout) findViewById(R.id.minusarea);
        addToClickHandler(addarea);
        carouselContainerView = findViewById(R.id.carouselContainerView);
        carouselView = (CarouselView) findViewById(R.id.carouselView);
        closeCarouselTxtView = (MTextView) findViewById(R.id.closeCarouselTxtView);
        closeCarouselTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));
        addToClickHandler(closeCarouselTxtView);
        dotsIndicator = (DotsIndicator) findViewById(R.id.dots_indicator);
        dotsArea = (RelativeLayout) findViewById(R.id.dotsArea);
        //  minusarea.setOnClickListener(new setOnClickList());


        minusImageView = (ImageView) findViewById(R.id.minusImageView);
        addImageView = (ImageView) findViewById(R.id.addImageView);
        autofitEditText = (AutoFitEditText) findViewById(R.id.autofitEditText);
        //autofitEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        //autofitEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        autofitEditText.setClickable(false);
        rechargeBox = (MaterialEditText) findViewById(R.id.rechargeBox);
        rechargeBox.setBackgroundResource(android.R.color.transparent);
        rechargeBox.setHideUnderline(true);
        rechargeBox.setTextSize(getActContext().getResources().getDimension(R.dimen._18ssp));
        autofitEditText.setText(generalFunc.convertNumberWithRTL("" + 1));
        rechargeBox.setTextColor(getActContext().getResources().getColor(R.color.black));
        rechargeBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        addImageView.setOnClickListener(view -> mangePluseView(autofitEditText));
        minusImageView.setOnClickListener(view -> mangeMinusView(autofitEditText));

        rlImageSlider = (RelativeLayout) findViewById(R.id.rlImageSlider);
        rlImageSlider.setOnClickListener(v -> generalFunc.showMessage(rlImageSlider, "Test"));

        rlImage = (RelativeLayout) findViewById(R.id.rlImage);
        rlImageSlider.setVisibility(View.VISIBLE);
        rlImage.setVisibility(View.GONE);
        setupSlider();
        // addItemCartBtn.setOnClickListener(new setOnClickList());
        //minuscntImgView.setOnClickListener(new setOnClickList());
        // addcntImgView.setOnClickListener(new setOnClickList());

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
        addToClickHandler(backImgView);

        setlabel();
        if (optionContainer.getChildCount() > 0) {
            optionContainer.removeAllViewsInLayout();
        }
        if (topingContainer.getChildCount() > 0) {
            topingContainer.removeAllViewsInLayout();
        }


        if (searchList.get("eFoodType") != null && searchList.get("eFoodType").equalsIgnoreCase("Veg")) {
            vegImage.setVisibility(View.VISIBLE);
            nonvegImage.setVisibility(View.GONE);
            vegNonvegTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_VEGETARIAN"));
        } else if (searchList.get("eFoodType") != null && searchList.get("eFoodType").equalsIgnoreCase("NonVeg")) {
            vegImage.setVisibility(View.GONE);
            nonvegImage.setVisibility(View.VISIBLE);
            vegNonvegTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_NONVEGETARIAN"));

        }

        // QTYNumberTxtView.setText(generalFunc.convertNumberWithRTL("" + 1));
        multiItemCardView = (CardView) findViewById(R.id.multiItemCardView);
        multiItemCardView.setVisibility(View.GONE);
        mViewPager = (EnhancedWrapContentViewPager) findViewById(R.id.multiItemViewPager);
        mMultiItemOptionAddonPagerAdapter = new MultiItemOptionAddonPagerAdapter(this, generalFunc, new MultiItemOptionAddonPagerAdapter.MultiItemOptionAddonListener() {
            @Override
            public void radioButtonPressed(String iOptionsCategoryId, ArrayList<String> mOptionIdList, List<Double> mTotalAmountList, RealmList<Options> mRealmOptionsList) {
                isOption = "Yes";
                seloptionPrice = 0;
                realmOptionsList = mRealmOptionsList;
                for (Double s : mTotalAmountList) {
                    seloptionPrice = seloptionPrice + s;
                }
                optionId = "";
                if (mOptionIdList.size() > 0) {
                    for (int i = 0; i < mOptionIdList.size(); i++) {
                        if (optionId.equals("")) {
                            optionId = mOptionIdList.get(i);
                        } else {
                            optionId = optionId + "," + mOptionIdList.get(i);
                        }
                    }
                }
                updateBottomData();
            }

            @Override
            public void checkBoxPressed(String iOptionsCategoryId, ArrayList<String> mToppingListId, List<Double> mToppingPriceAmountList, RealmList<Topping> mRealmToppingList) {
                isTooping = "Yes";
                toppingPrice = 0;
                toppingListId = mToppingListId;
                realmToppingList = mRealmToppingList;
                for (Double s : mToppingPriceAmountList) {
                    toppingPrice = toppingPrice + s;
                }
                updateBottomData();
            }
        });
        mViewPager.setAdapter(mMultiItemOptionAddonPagerAdapter);


        ImageView arrowPrevious = (ImageView) findViewById(R.id.arrowPrevious);
        ImageView arrowNext = (ImageView) findViewById(R.id.arrowNext);
        if (generalFunc != null && generalFunc.isRTLmode()) {
            arrowPrevious.setRotation(0);
            arrowNext.setRotation(180);
        }

        previousArea = (LinearLayout) findViewById(R.id.previousArea);
        previousArea.setVisibility(View.INVISIBLE);
        nextArea = (LinearLayout) findViewById(R.id.nextArea);
        nextArea.setVisibility(View.INVISIBLE);

        TextView txtPrevious = (TextView) findViewById(R.id.txtPrevious);
        txtPrevious.setText(generalFunc.retrieveLangLBl("Previous", "LBL_PREVIOUS"));
        TextView txtNext = (TextView) findViewById(R.id.txtNext);
        txtNext.setText(generalFunc.retrieveLangLBl("Next", "LBL_NEXT"));


        previousArea.setOnClickListener(v -> {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            if (currentStep > 0) {
                currentStep--;
            }
            step_view.done(false);
            step_view.go(currentStep, true);

        });
        nextArea.setOnClickListener(v -> {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);


            if (currentStep < step_view.getStepCount() - 1) {
                currentStep++;
                step_view.go(currentStep, true);
            } else {
                step_view.done(true);
            }
        });

        boolean isStepViewDisable = false;
        if (searchList.get("Restaurant_Status") != null && searchList.get("Restaurant_Status").equalsIgnoreCase("closed")) {
            if (searchList.get("timeslotavailable") != null && searchList.get("timeslotavailable").equalsIgnoreCase("Yes")) {
                if (searchList.get("eAvailable").equalsIgnoreCase("No")) {
                    isStepViewDisable = true;
                }
            } else {
                isStepViewDisable = true;
            }
        }
        if (isStepViewDisable) {
            step_view.setOnStepClickListener(null);
        } else {
            step_view.setOnStepClickListener(step -> {

                if (step_view.getCurrentStep() == step) {
                    return;
                }

                if (generalFunc.isRTLmode()) {

                    currentStep = step_view.getStepCount() - step - 1;
                    btnstep = step;

                    step_view.go(currentStep, true);

                    mViewPager.setCurrentItem(currentStep, true);
                    if (step_view.getStepCount() > 5) {
                        x = (step_view.getStepCount() - currentStep) * Utils.dpToPx(70, getActContext());
                        stepScrollview.scrollTo(x, y);
                    }
                    if (step == 0) {
                        addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));
                    } else {
                        addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NEXT"));
                    }
                    return;
                }
                currentStep = step;
                btnentStep = step + 1;

                step_view.go(step, true);
                mViewPager.setCurrentItem(step, true);
                if (step_view.getStepCount() > 5) {
                    x = step * Utils.dpToPx(90, getActContext());
                    stepScrollview.scrollTo(x, y);
                }

                if (step_view.getStepCount() == step + 1) {
                    addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));
                } else {
                    addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NEXT"));
                }
            });
        }


        mViewPager.setOnTouchListener((v, event) -> true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                //mViewPager.reMeasureCurrentPage(mViewPager.getCurrentItem());
                //mViewPager.reMeasureCurrent(mViewPager.getCurrentItem());
                if (position == 0) {
                    previousArea.setVisibility(View.INVISIBLE);
                } else {
                    previousArea.setVisibility(View.VISIBLE);
                }
                if (position < mViewPager.getAdapter().getCount() - 1) {
                    nextArea.setVisibility(View.VISIBLE);
                } else {
                    nextArea.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        productimage.setOnClickListener(v -> {

            ViewListener viewListener = position -> {
                ImageView customView = new ImageView(getActContext());

                CarouselView.LayoutParams layParams = new CarouselView.LayoutParams(CarouselView.LayoutParams.MATCH_PARENT, CarouselView.LayoutParams.MATCH_PARENT);
                customView.setLayoutParams(layParams);

                customView.setPadding(Utils.dipToPixels(getActContext(), 15), 0, Utils.dipToPixels(getActContext(), 15), 0);
                customView.setImageResource(R.mipmap.ic_no_icon);

                String imageUrl = Utils.getResizeImgURL(getActContext(), searchList.get("vImage"), ((int) Utils.getScreenPixelWidth(getActContext())) - Utils.dipToPixels(getActContext(), 30), 0, Utils.getScreenPixelHeight(getActContext()) - Utils.dipToPixels(getActContext(), 30));
                Glide.with(getActContext())
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.mipmap.ic_no_icon).error(R.mipmap.ic_no_icon))
                        .into(customView);
                customView.setOnClickListener(null);
                return customView;
            };

            carouselContainerView.setVisibility(View.VISIBLE);
            carouselView.setViewListener(viewListener);
            carouselView.setPageCount(1);
            carouselView.setCurrentItem(0);
        });

    }

    private void updateBottomData() {
        double totalamout = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) + Math.abs(toppingPrice) + Math.abs(seloptionPrice);
        totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout * getQty())), searchList.get("currencySymbol"), true));

        if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate")) {
            totalamout = Math.abs(seloptionPrice) * getQty();
            totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout)), searchList.get("currencySymbol"), true));
        }
    }

    public void setlabel() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_TO_BASKET"));
        baseFareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_BASIC_PRICE"));
        topingTitleTxt.setText(generalFunc.retrieveLangLBl("Select Topping", "LBL_SELECT_TOPPING"));
        optionTitleTxt.setText(generalFunc.retrieveLangLBl("Select Options", "LBL_SELECT_OPTIONS"));
        totalHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TOTAL_TXT"));
        addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));
    }

    public void setData() {
        try {

            //  baseFareVTxt.setText(searchList.get("StrikeoutPrice"));
            if (GeneralFunctions.parseDoubleValue(0, searchList.get("fOfferAmt")) > 0) {
                baseFareVTxt.setText(generalFunc.convertNumberWithRTL(searchList.get("StrikeoutPrice")));

                baseFareVTxt.setTextColor(getActContext().getResources().getColor(R.color.gray));
                SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
                SpannableString origSpan = new SpannableString(baseFareVTxt.getText());

                origSpan.setSpan(new StrikethroughSpan(), 0, baseFareVTxt.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                spanBuilder.append(origSpan);

                String priceStr = " " + generalFunc.convertNumberWithRTL(generalFunc.convertNumberWithRTL(searchList.get("fDiscountPricewithsymbol")));

                SpannableString discountSpan = new SpannableString(priceStr);
                discountSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#272727")), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spanBuilder.append(discountSpan);
                baseFareVTxt.setText(spanBuilder);


            } else {
                baseFareVTxt.setText(generalFunc.convertNumberWithRTL(searchList.get("StrikeoutPrice")));
                baseFareVTxt.setPaintFlags(0);
            }
            vItemNameTxt.setText(searchList.get("vItemType"));
            // android.view.animation.Animation marquee = AnimationUtils.loadAnimation(this, R.anim.marquee);
            // vItemNameTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            // vItemNameTxt.setSingleLine(true);
            // vItemNameTxt.startAnimation(marquee);
            // vItemNameTxt.setMarqueeRepeatLimit(-1);
            vItemNameTxt.setSelected(true);


            String vItemDesc = searchList.get("vItemDesc");

            if (Utils.checkText(vItemDesc)) {
                Spanned descdata = generalFunc.fromHtml(vItemDesc);
                vItemDescTxt.setText(generalFunc.fromHtml(descdata + ""));
                vItemDescTxt.setVisibility(View.VISIBLE);
                generalFunc.makeTextViewResizable(vItemDescTxt, 2, "...\n+ " + generalFunc.retrieveLangLBl("View More", "LBL_VIEW_MORE_TXT"), true, R.color.appThemeColor_1, R.dimen.txt_size_10);
            } else {
                vItemDescTxt.setVisibility(View.GONE);
            }

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int heightOfImage = (int) getActContext().getResources().getDimension(R.dimen._230sdp);


            new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(getActContext(), searchList.get("vImage"), 0, heightOfImage, width)), productimage).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();


            if (!generalFunc.getJsonValueStr("ENABLE_MULTI_OPTIONS_ADDONS", obj_userProfile).equalsIgnoreCase("YES")) {
                MenuItemOptionToppingArr = searchList.get("MenuItemOptionToppingArr");
            }


            JSONObject MainObject = null;
            try {
                MainObject = new JSONObject(convertStandardJSONString(MenuItemOptionToppingArr));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (MainObject != null) {

                JSONArray optionArray = generalFunc.getJsonArray("options", MainObject);
                if (optionArray != null && optionArray.length() > 0) {
                    for (int i = 0; i < optionArray.length(); i++) {
                        isOption = "Yes";
                        int pos = i;
                        JSONObject optionObject = generalFunc.getJsonObject(optionArray, i);
                        HashMap<String, String> optionMap = new HashMap<>();
                        LayoutInflater optioninflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View optionview = optioninflater.inflate(R.layout.item_basket_option, null);
                        MTextView optionName = optionview.findViewById(R.id.optionName);
                        MTextView optionPrice = optionview.findViewById(R.id.optionPrice);
                        RadioGroup optionRadioGroup = optionview.findViewById(R.id.optionRadioGroup);
                        RadioButton optionradioBtn = optionview.findViewById(R.id.optionradioBtn);
                        LinearLayout rowArea = optionview.findViewById(R.id.rowArea);
                        optionradioBtn.setTag(pos);
                        optionRadioGroup.setTag(pos);


//                        if (selOptionId != null && !selOptionId.equalsIgnoreCase("")) {
//                            if (selOptionId.equalsIgnoreCase(generalFunc.getJsonValue("iOptionId", optionObject.toString()))) {
//                                optionRadioGroup.check(optionradioBtn.getId());
//                                lastCheckedRB = optionradioBtn;
//                                seloptionPrice = GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("fUserPrice", optionObject.toString()));
//
//                                optionradioBtn.setChecked(true);
//                            }
//                        }

                        rowArea.setOnClickListener(v -> optionradioBtn.setChecked(true));

                        optionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                            try {


                                if (lastCheckedRB != null) {
                                    if (lastCheckedRB == optionradioBtn) {
                                        return;
                                    }
                                }
                                if (seloptionPrice == 0) {
                                    seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(pos).get("fUserPrice"));
                                } else {
                                    // String totalamout = totalPriceTxt.getText().toString().replace(searchList.get("currencySymbol"), "").trim();
                                    double totalamout = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * getQty();

//                                        totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(Math.abs(toppingPrice) + totalamout - Math.abs(seloptionPrice))));
                                    totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(Math.abs(toppingPrice) + totalamout - Math.abs(seloptionPrice))), searchList.get("currencySymbol"), true));
                                    if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate")) {

                                        totalamout = Math.abs(seloptionPrice) * getQty();

//                                            totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout)));
                                        totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout)), searchList.get("currencySymbol"), true));

                                    }

                                }

                                if (lastCheckedRB != null) {
                                    lastCheckedRB.setChecked(false);
                                }


                                optionradioBtn.setChecked(true);


                                lastCheckedRB = optionradioBtn;


                                seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(pos).get("fUserPrice"));
                                optionId = optionList.get(pos).get("iOptionId");
                                // String totalamout = totalPriceTxt.getText().toString().replace(searchList.get("currencySymbol"), "").trim();
                                double totalamout = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) + Math.abs(toppingPrice) + Math.abs(seloptionPrice);
//                                    totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout * getQty())));

                                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout * getQty())), searchList.get("currencySymbol"), true));

                                if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate")) {
                                    totalamout = Math.abs(seloptionPrice) * getQty();

//                                        totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout)));
                                    totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout)), searchList.get("currencySymbol"), true));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        optionMap.put("iOptionId", generalFunc.getJsonValueStr("iOptionId", optionObject));
                        optionMap.put("vOptionName", generalFunc.getJsonValueStr("vOptionName", optionObject));
                        optionMap.put("fPrice", generalFunc.getJsonValueStr("fPrice", optionObject));
                        optionMap.put("eOptionType", generalFunc.getJsonValueStr("eOptionType", optionObject));
                        optionMap.put("fUserPrice", generalFunc.getJsonValueStr("fUserPrice", optionObject));
                        optionMap.put("fUserPriceWithSymbol", generalFunc.getJsonValueStr("fUserPriceWithSymbol", optionObject));
                        optionName.setText(generalFunc.getJsonValueStr("vOptionName", optionObject));
                        optionPrice.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("fUserPriceWithSymbol", optionObject)));

                        optionMap.put("eDefault", generalFunc.getJsonValueStr("eDefault", optionObject));
                        if (generalFunc.getJsonValueStr("eDefault", optionObject) != null && generalFunc.getJsonValueStr("eDefault", optionObject).equalsIgnoreCase("Yes")) {
                            optionradioBtn.setChecked(true);
                            lastCheckedRB = optionradioBtn;
                            optionId = generalFunc.getJsonValueStr("iOptionId", optionObject);

                        }

                        optionList.add(optionMap);
                        optionContainer.addView(optionview);


                        optionArea.setVisibility(View.VISIBLE);
                    }


                } else {

                    optionTitleTxt.setVisibility(View.GONE);
                }


                JSONArray addOnArray = generalFunc.getJsonArray("addon", MenuItemOptionToppingArr.toString());
                if (addOnArray != null && addOnArray.length() > 0) {

                    for (int i = 0; i < addOnArray.length(); i++) {
                        isTooping = "Yes";
                        int pos = i;
                        JSONObject topingObject = generalFunc.getJsonObject(addOnArray, i);
                        HashMap<String, String> topingMap = new HashMap<>();
                        LayoutInflater topinginflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View topingView = topinginflater.inflate(R.layout.item_basket_toping, null);
                        MTextView topingTxtView = topingView.findViewById(R.id.topingTxtView);
                        MTextView topingPriceTxtView = topingView.findViewById(R.id.topingPriceTxtView);
                        CheckBox topingCheckBox = topingView.findViewById(R.id.topingCheckBox);
                        LinearLayout row_area = topingView.findViewById(R.id.row_area);


                        topingMap.put("iOptionId", generalFunc.getJsonValue("iOptionId", topingObject.toString()));
                        topingMap.put("vOptionName", generalFunc.getJsonValue("vOptionName", topingObject.toString()));
                        topingMap.put("fPrice", generalFunc.getJsonValue("fPrice", topingObject.toString()));
                        topingMap.put("eOptionType", generalFunc.getJsonValue("eOptionType", topingObject.toString()));
                        topingMap.put("fUserPrice", generalFunc.getJsonValue("fUserPrice", topingObject.toString()));
                        topingMap.put("fUserPriceWithSymbol", generalFunc.getJsonValue("fUserPriceWithSymbol", topingObject.toString()));
                        topingTxtView.setText(generalFunc.getJsonValue("vOptionName", topingObject.toString()));
                        topingPriceTxtView.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("fUserPriceWithSymbol", topingObject.toString())));
                        topingList.add(topingMap);
                        topingContainer.addView(topingView);


//                        if (selToppingList != null) {
//                            if (selToppingList.contains(topingMap.get("iOptionId"))) {
//                                topingCheckBox.setChecked(true);
//                                toppingPrice = toppingPrice + GeneralFunctions.parseDoubleValue(0, topingList.get(pos).get("fUserPrice"));
//                            }
//                        }

                        row_area.setOnClickListener(v -> topingCheckBox.setChecked(!topingCheckBox.isChecked()));

                        topingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                            //   String totalamout = totalPriceTxt.getText().toString().replace(searchList.get("currencySymbol"), "").trim();

                            double totalamout = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice"));
                            //Double totalValAmount =totalamout - Math.abs(seloptionPrice);
                            if (isChecked) {
                                toppingListId.add(topingList.get(pos).get("iOptionId"));
                                toppingPrice = toppingPrice + GeneralFunctions.parseDoubleValue(0, topingList.get(pos).get("fUserPrice"));


                                totalamout = totalamout + Math.abs(seloptionPrice) + Math.abs(toppingPrice);
//                                    totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout * getQty())));
                                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout * getQty())), searchList.get("currencySymbol"), true));
                            } else {
                                if (toppingListId.size() > 0) {
                                    if (toppingListId.contains(topingList.get(pos).get("iOptionId"))) {
                                        toppingListId.remove(topingList.get(pos).get("iOptionId"));
                                    }
                                }
                                toppingPrice = toppingPrice - GeneralFunctions.parseDoubleValue(0, topingList.get(pos).get("fUserPrice"));


                                totalamout = totalamout + Math.abs(seloptionPrice) + Math.abs(toppingPrice);

//                                    totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout * getQty())));

                                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(totalamout * getQty())), searchList.get("currencySymbol"), true));
                            }
                        });
                        topingArea.setVisibility(View.VISIBLE);
                    }
                } else {
                    topingTitleTxt.setVisibility(View.GONE);
                }
//                totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(((GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * (getQty())) + Math.abs(toppingPrice) + Math.abs(seloptionPrice)))));
                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(((GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * (getQty())) + Math.abs(toppingPrice) + Math.abs(seloptionPrice)))), searchList.get("currencySymbol"), true));

                if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate") && optionList.size() > 0) {

                    if (seloptionPrice == 0) {
                        seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                    }


//                    totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay((Math.abs(toppingPrice) + Math.abs(seloptionPrice) * (getQty())) + Math.abs(toppingPrice))));
                    totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay((Math.abs(toppingPrice) + Math.abs(seloptionPrice) * (getQty())) + Math.abs(toppingPrice))), searchList.get("currencySymbol"), true));
                }
            } else {
                toppingsarea.setVisibility(View.GONE);
//                totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(((GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * (getQty())) + Math.abs(toppingPrice) + Math.abs(seloptionPrice)))));
                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(((GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * (getQty())) + Math.abs(toppingPrice) + Math.abs(seloptionPrice)))), searchList.get("currencySymbol"), true));
                if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate") && optionList.size() > 0) {

                    if (seloptionPrice == 0) {
                        seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                    }


//                    totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay((Math.abs(toppingPrice) + Math.abs(seloptionPrice) * (getQty())) + Math.abs(toppingPrice))));
                    totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay((Math.abs(toppingPrice) + Math.abs(seloptionPrice) * (getQty())) + Math.abs(toppingPrice))), searchList.get("currencySymbol"), true));
                }
            }
        } catch (Exception e) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int heightOfImage = (int) getActContext().getResources().getDimension(R.dimen._230sdp);


            new LoadImage.builder(LoadImage.bind(Utils.getResizeImgURL(getActContext(), searchList.get("vImage"), 0, heightOfImage, width)), productimage).setErrorImagePath(R.color.imageBg).setPlaceholderImagePath(R.color.imageBg).build();


            toppingsarea.setVisibility(View.GONE);
//            totalPriceTxt.setText(searchList.get("currencySymbol") + " " +
//                    generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(((GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * (getQty())) + Math.abs(toppingPrice) + Math.abs(seloptionPrice)))));
            totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(((GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) * (getQty())) + Math.abs(toppingPrice) + Math.abs(seloptionPrice)))), searchList.get("currencySymbol"), true));

            if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate") && optionList.size() > 0) {

                if (seloptionPrice == 0) {
                    seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                }


//                totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay((Math.abs(toppingPrice) + Math.abs(seloptionPrice) * (getQty())) + Math.abs(toppingPrice))));
                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay((Math.abs(toppingPrice) + Math.abs(seloptionPrice) * (getQty())) + Math.abs(toppingPrice))), searchList.get("currencySymbol"), true));
            }

        }


    }

    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }


    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        int i = view.getId();
        if (i == R.id.backImgView || i == R.id.closeImg) {
            onBackPressed();
        } else if (i == R.id.addItemCartBtn) {


               /* if (searchList.get("Restaurant_Status") != null && searchList.get("Restaurant_Status").equalsIgnoreCase("closed")) {

                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                    return;
                }


                Realm realm = MyApp.getRealmInstance();
                cartRealmList = realm.where(Cart.class).findAll();

                if (cartRealmList != null && cartRealmList.size() > 0 && !searchList.get("iCompanyId").equalsIgnoreCase(generalFunc.retrieveValue(Utils.COMPANY_ID))) {


                    if (optionArea.getVisibility() == View.VISIBLE) {
                        if (optionId.equalsIgnoreCase("")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_OPTIONS_REQUIRED"));
                            return;
                        }
                    }


                    GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                    generateAlertBox.setCancelable(false);
                    generateAlertBox.setContentMessage(generalFunc.retrieveLangLBl("", "LBL_UPDATE_CART"), generalFunc.retrieveLangLBl("Are you sure you'd like to change restaurants ? Your order will be lost.", "LBL_ORDER_LOST_ALERT_TXT"));
                    generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                    generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_PROCEED"));
                    generateAlertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            if (btn_id == 1) {
                                deleteOptionToRealm();
                                ArrayList<String> removeData=new ArrayList<>();
                                removeData.add(Utils.COMPANY_ID);
                                removeData.add(Utils.COMPANY_MINIMUM_ORDER);
                                removeData.add(Utils.COMPANY_MAX_QTY);
                                generalFunc.removeValue(removeData);

                                generalFunc.removeAllRealmData(realm);
                                addDataToList();

                            } else {
                                generateAlertBox.closeAlertBox();
                            }
                        }
                    });
                    generateAlertBox.showAlertBox();
                } else {

                    if (optionArea.getVisibility() == View.VISIBLE) {
                        if (optionId.equalsIgnoreCase("")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_OPTIONS_REQUIRED"));
                            return;
                        }
                    }
                    addDataToList();
                }*/


        } else if (i == R.id.addarea) {


            if (searchList.get("Restaurant_Status") != null && searchList.get("Restaurant_Status").equalsIgnoreCase("closed")) {


                if (searchList.get("timeslotavailable") != null && searchList.get("timeslotavailable").equalsIgnoreCase("Yes")) {
                    if (searchList.get("eAvailable").equalsIgnoreCase("No")) {
                        // resholder.resStatusTxt.setText(data.get("LBL_NOT_ACCEPT_ORDERS_TXT"));
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_NOT_ACCEPT_ORDERS_TXT"));
                    } else {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                    }
                } else {
                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                }
                return;

            }

            if (generalFunc.getJsonValueStr("ENABLE_MULTI_OPTIONS_ADDONS", obj_userProfile).equalsIgnoreCase("YES")) {


                if (addItemCartBtn.getText().toString().equalsIgnoreCase(generalFunc.retrieveLangLBl("", "LBL_NEXT"))) {

                    if (generalFunc.isRTLmode()) {
                        btnentStep = step_view.getStepCount() - btnstep;
                        btnstep--;


                        //   Toast.makeText(SimpleActivity.this, "Step " + step, Toast.LENGTH_SHORT).show();


                        step_view.go(btnentStep, true);
                        mViewPager.setCurrentItem(btnentStep, true);
                        if (step_view.getStepCount() > 5) {
                            x = (step_view.getStepCount() - btnentStep) * Utils.dpToPx(70, getActContext());
                            stepScrollview.scrollTo(x, y);
                        }

                        if (btnstep == 0) {
                            addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));


                        } else {
                            addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NEXT"));
                        }
                        return;


                    }

                    step_view.go(currentStep + 1, true);
                    currentStep = currentStep + 1;
                    mViewPager.setCurrentItem(currentStep, true);
                    if (step_view.getStepCount() > 5) {


                        x = currentStep * Utils.dpToPx(90, getActContext());


                        stepScrollview.scrollTo(x, y);
                    }

                    if (step_view.getStepCount() == currentStep + 1) {
                        addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_ITEM"));


                    } else {
                        addItemCartBtn.setText(generalFunc.retrieveLangLBl("", "LBL_NEXT"));
                    }

                    return;
                }

            }


            Realm realm = MyApp.getRealmInstance();
            RealmResults<Cart> cartRealmList = realm.where(Cart.class).findAll();

            if (cartRealmList != null && cartRealmList.size() > 0 && !searchList.get("iCompanyId").equalsIgnoreCase(generalFunc.retrieveValue(Utils.COMPANY_ID))) {


                if (optionArea.getVisibility() == View.VISIBLE) {
                    if (optionId.equalsIgnoreCase("")) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_OPTIONS_REQUIRED"));
                        return;
                    }
                }


                GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
                generateAlertBox.setCancelable(false);
                generateAlertBox.setContentMessage(generalFunc.retrieveLangLBl("", "LBL_UPDATE_CART"), generalFunc.retrieveLangLBl("Are you sure you'd like to change restaurants ? Your order will be lost.", "LBL_ORDER_LOST_ALERT_TXT"));
                generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                generateAlertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_PROCEED"));
                generateAlertBox.setBtnClickList(btn_id -> {
                    if (btn_id == 1) {
                        deleteOptionToRealm();
                        ArrayList<String> removeData = new ArrayList<>();
                        removeData.add(Utils.COMPANY_ID);
                        removeData.add(Utils.COMPANY_MINIMUM_ORDER);
                        removeData.add(Utils.COMPANY_MAX_QTY);
                        generalFunc.removeValue(removeData);

                        generalFunc.removeAllRealmData(realm);
                        addDataToList();

                    } else {
                        generateAlertBox.closeAlertBox();
                    }
                });
                generateAlertBox.showAlertBox();
            } else {

                if (optionArea.getVisibility() == View.VISIBLE) {
                    if (optionId.equalsIgnoreCase("")) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_OPTIONS_REQUIRED"));
                        return;
                    }
                }
                addDataToList();
            }



               /* if (searchList.get("Restaurant_Status") != null && searchList.get("Restaurant_Status").equalsIgnoreCase("closed")) {

                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                    return;
                }
//                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
                int QUANTITY = getQty();


                if (QUANTITY >= 1) {
                    QUANTITY = QUANTITY + 1;
                    Double itemTotal = GeneralFunctions.parseDoubleValue(0, searchList.get("fDiscountPrice")) + Math.abs(toppingPrice) + Math.abs(seloptionPrice);

                    if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate") && optionList.size() > 0) {
                        if (seloptionPrice == 0) {
                            seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                        }

                        itemTotal = 0.0;
                        itemTotal = Math.abs(toppingPrice) + Math.abs(seloptionPrice);

                    }
                    totalPriceTxt.setText(LBL_SUB_TOTAL + " " + searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(QUANTITY * itemTotal)));
                    rechargeBox.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));
                    //minusarea.setEnabled(true);
                }*/

        } else if (i == R.id.minusarea) {
              /*  if (searchList.get("Restaurant_Status") != null && searchList.get("Restaurant_Status").equalsIgnoreCase("closed")) {

                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                    return;
                }


//                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
                int QUANTITY = getQty();
                if (QUANTITY > 1) {
                    QUANTITY = QUANTITY - 1;

                    Double itemTotal = GeneralFunctions.parseDoubleValue(0, searchList.get("fDiscountPrice")) + Math.abs(toppingPrice) + Math.abs(seloptionPrice);
                    if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate") && optionList.size() > 0) {
                        if (seloptionPrice == 0) {
                            seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                        }

                        itemTotal = 0.0;
                        itemTotal = Math.abs(toppingPrice) + Math.abs(seloptionPrice);

                    }

                    totalPriceTxt.setText(LBL_SUB_TOTAL + " " + searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(QUANTITY * itemTotal)));
                    QTYNumberTxtView.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));
                } else {

                }*/

        } else if (i == R.id.closeCarouselTxtView) {
            if (carouselContainerView.getVisibility() == View.VISIBLE) {
                carouselContainerView.setVisibility(View.GONE);
            }
        }

    }


    public void addDataToList() {
        try {

            if ((realmOptionsList != null && realmOptionsList.size() > 0) || (realmToppingList != null && realmToppingList.size() > 0)) {
                storeAllOptionsToRealm();
            }

            HashMap<String, String> map = new HashMap<>();
//        map.put("Qty", QTYNumberTxtView.getText().toString().trim());
            map.put("Qty", "" + getQty());
            map.put("vItemType", searchList.get("vItemType"));
            map.put("vImage", searchList.get("vImage"));
            map.put("fDiscountPrice", searchList.get("fPrice"));
            map.put("iMenuItemId", searchList.get("iMenuItemId"));
            map.put("eFoodType", searchList.get("eFoodType"));
            map.put("iFoodMenuId", searchList.get("iFoodMenuId"));
            map.put("iCompanyId", searchList.get("iCompanyId"));
            map.put("vCompany", searchList.get("vCompany"));
            if (toppingListId.size() > 0) {
                for (int i = 0; i < toppingListId.size(); i++) {
                    if (toppingId.equals("")) {
                        toppingId = toppingListId.get(i);
                    } else {
                        toppingId = toppingId + "," + toppingListId.get(i);
                    }
                }
            }
            map.put("iToppingId", toppingId);


            //  basketList.add(map);
            //  generalFunc.setBasketData(basketList);
            HashMap<String, String> storeData = new HashMap<>();
            storeData.put(Utils.COMPANY_MINIMUM_ORDER, searchList.get("fMinOrderValue"));
            storeData.put(Utils.COMPANY_MAX_QTY, searchList.get("iMaxItemQty"));
            storeData.put(Utils.COMPANY_ID, searchList.get("iCompanyId"));
            generalFunc.storeData(storeData);
            setRealmData();
            // onBackPressed();

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (Exception e) {
        }

    }

    public Cart checksameRecordExist(Realm realm, String toppingId, String optionId, String iFoodMenuId, String iMenuItemId) {
        Cart cart = null;
        String[] list_topping_ids = toppingId.split(",");
        List<String> list_topping_ids_list = Arrays.asList(list_topping_ids);
        Collections.sort(list_topping_ids_list);


        RealmResults<Cart> cartlist = realm.where(Cart.class).findAll();

        if (cartlist != null && cartlist.size() > 0)

            for (int i = 0; i < realmCartList.size(); i++) {
                String[] topping_ids = realmCartList.get(i).getiToppingId().split(",");
                List<String> topping_idsList = Arrays.asList(topping_ids);
                Collections.sort(topping_idsList);
                if (topping_idsList.equals(list_topping_ids_list) &&
                        realmCartList.get(i).getiOptionId().equalsIgnoreCase(optionId) && realmCartList.get(i).getiFoodMenuId().equalsIgnoreCase(iFoodMenuId) && realmCartList.get(i).getiMenuItemId().equalsIgnoreCase(iMenuItemId)) {
                    return realmCartList.get(i);
                }
            }


        // cart = realm.where(Cart.class).("iToppingId", list_topping_ids).equalTo("iOptionId", optionId).findFirst();

        return cart;
    }

    public void setRealmData() {
        String toppingId = "";
        Realm realm = MyApp.getRealmInstance();
        //    Cart cart = realm.where(Cart.class).equalTo("iMenuItemId", searchList.get("iMenuItemId")).findFirst();

        if (toppingListId.size() > 0) {
            for (int i = 0; i < toppingListId.size(); i++) {
                if (toppingId.equals("")) {
                    toppingId = toppingListId.get(i).toString();
                } else {
                    toppingId = toppingId + "," + toppingListId.get(i).toString();
                }
            }
        }

        Cart cart = checksameRecordExist(realm, toppingId, optionId, searchList.get("iFoodMenuId"), searchList.get("iMenuItemId"));

        realm.beginTransaction();
        if (cart == null) {
            isCartNull = true;
            cart = new Cart();
            cart.setvItemType(searchList.get("vItemType"));
            cart.setvImage(searchList.get("vImage"));
            cart.setfDiscountPrice(searchList.get("fDiscountPrice"));
            cart.setiMenuItemId(searchList.get("iMenuItemId"));
            cart.seteFoodType(searchList.get("eFoodType"));
            cart.setiFoodMenuId(searchList.get("iFoodMenuId"));
            cart.setiCompanyId(searchList.get("iCompanyId"));
            cart.setvCompany(searchList.get("vCompany"));
            cart.setCurrencySymbol(searchList.get("currencySymbol"));
            cart.setMultiItemJsonData(mMultiItemOptionAddonPagerAdapter.getCategoryArray());
            if (Utils.checkText(generalFunc.getServiceId())) {
                cart.setiServiceId(generalFunc.getServiceId());
            }
            //cart.setQty(QTYNumberTxtView.getText().toString().trim());
            cart.setQty("" + getQty());
            cart.setIsOption(isOption);
            cart.setIsTooping(isTooping);
            cart.setiOptionId(optionId);
            cart.setiToppingId(toppingId);
            cart.setMilliseconds(Calendar.getInstance().getTimeInMillis());
            cart.setIspriceshow(searchList.get("ispriceshow"));
            if (isCartNull) {
                realm.insert(cart);
            } else {
                realm.insertOrUpdate(cart);
            }
        } else {

            int qty = GeneralFunctions.parseIntegerValue(0, cart.getQty());
//            int newqty = GeneralFunctions.parseIntegerValue(0, QTYNumberTxtView.getText().toString().trim());
            int newqty = getQty();

            cart.setQty((qty + newqty) + "");
            realm.insertOrUpdate(cart);

        }
        realm.commitTransaction();
    }

    public RealmResults<Options> getOptionsData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Options.class).findAll();
    }

    public RealmResults<Topping> getToppingData() {
        Realm realm = MyApp.getRealmInstance();
        return realm.where(Topping.class).findAll();
    }

    public void storeAllOptionsToRealm() {
        Realm realm = MyApp.getRealmInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(realmToppingList);
        realm.insertOrUpdate(realmOptionsList);
        realm.commitTransaction();
    }

    public void getOptionsList() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetItemOptionAddonDetails");
        parameters.put("iCompanyId", searchList.get("iCompanyId"));
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("eSystem", Utils.eSystem_Type);

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {
            if (responseString != null && !responseString.equals("")) {
                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    String message = generalFunc.getJsonValue("message", responseString);

                    JSONArray optionArray = generalFunc.getJsonArray("options", message);
                    for (int i = 0; i < optionArray.length(); i++) {
                        JSONObject optionObject = generalFunc.getJsonObject(optionArray, i);

                        Options optionsObj = new Options();
                        optionsObj.setfPrice(generalFunc.getJsonValue("fPrice", optionObject.toString()));
                        optionsObj.setfUserPrice(generalFunc.getJsonValue("fUserPrice", optionObject.toString()));
                        optionsObj.setfUserPriceWithSymbol(generalFunc.getJsonValue("fUserPriceWithSymbol", optionObject.toString()));
                        optionsObj.setiFoodMenuId(generalFunc.getJsonValue("iFoodMenuId", optionObject.toString()));
                        optionsObj.setiMenuItemId(generalFunc.getJsonValue("iMenuItemId", optionObject.toString()));
                        optionsObj.setvOptionName(generalFunc.getJsonValue("vOptionName", optionObject.toString()));
                        optionsObj.setiOptionId(generalFunc.getJsonValue("iOptionId", optionObject.toString()));
                        optionsObj.seteDefault(generalFunc.getJsonValue("eDefault", optionObject.toString()));
                        realmOptionsList.add(optionsObj);


                    }
                    JSONArray addOnArray = generalFunc.getJsonArray("addon", message);
                    for (int i = 0; i < addOnArray.length(); i++) {
                        JSONObject topingObject = generalFunc.getJsonObject(addOnArray, i);
                        Topping topppingObj = new Topping();
                        topppingObj.setfPrice(generalFunc.getJsonValue("fPrice", topingObject.toString()));
                        topppingObj.setfUserPrice(generalFunc.getJsonValue("fUserPrice", topingObject.toString()));
                        topppingObj.setfUserPriceWithSymbol(generalFunc.getJsonValue("fUserPriceWithSymbol", topingObject.toString()));
                        topppingObj.setiFoodMenuId(generalFunc.getJsonValue("iFoodMenuId", topingObject.toString()));
                        topppingObj.setiMenuItemId(generalFunc.getJsonValue("iMenuItemId", topingObject.toString()));
                        topppingObj.setvOptionName(generalFunc.getJsonValue("vOptionName", topingObject.toString()));
                        topppingObj.setiOptionId(generalFunc.getJsonValue("iOptionId", topingObject.toString()));
                        realmToppingList.add(topppingObj);
                    }

                }
            } else {
                generalFunc.showError();
            }
        });

    }

    public int getQty() {
        int qty = 1;


        String strVal = autofitEditText.getText().toString().trim().toUpperCase(Locale.US);

        qty = GeneralFunctions.parseIntegerValue(1, strVal);

        return qty;


    }

    public void mangeMinusView(AutoFitEditText rechargeBox) {
        if (Utils.checkText(rechargeBox) && getQty() > 0) {

            if (searchList.get("Restaurant_Status") != null && searchList.get("Restaurant_Status").equalsIgnoreCase("closed")) {
                if (searchList.get("timeslotavailable").equalsIgnoreCase("Yes")) {
                    if (searchList.get("eAvailable").equalsIgnoreCase("No")) {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_NOT_ACCEPT_ORDERS_TXT"));
                    } else {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                    }
                } else {
                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                }
                return;
            }


//                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
            int QUANTITY = getQty();
            if (QUANTITY > 1) {
                QUANTITY = QUANTITY - 1;

                double itemTotal = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) + Math.abs(toppingPrice) + Math.abs(seloptionPrice);
                if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate")/* && optionList.size() > 0*/) {
                    if (seloptionPrice == 0) {
                        if (optionList.size() > 0) {
                            seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                        } else {
                            seloptionPrice = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice"));
                        }
                    }
                    itemTotal = Math.abs(toppingPrice) + Math.abs(seloptionPrice);

                }

//                totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(QUANTITY * itemTotal)));
                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(QUANTITY * itemTotal)), searchList.get("currencySymbol"), true));
                rechargeBox.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));
                rechargeBox.setEnabled(false);
            }

        } else {
            // rechargeBox.setText(generalFunc.convertNumberWithRTL("" + 1));
        }
    }

    public void mangePluseView(AutoFitEditText rechargeBox) {
        if (Utils.checkText(rechargeBox)) {


            if (searchList.get("Restaurant_Status") != null && searchList.get("Restaurant_Status").equalsIgnoreCase("closed")) {
                if (searchList.get("timeslotavailable") != null && searchList.get("timeslotavailable").equalsIgnoreCase("Yes")) {
                    if (searchList.get("eAvailable").equalsIgnoreCase("No")) {
                        // resholder.resStatusTxt.setText(data.get("LBL_NOT_ACCEPT_ORDERS_TXT"));
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_NOT_ACCEPT_ORDERS_TXT"));
                    } else {
                        generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                    }
                } else {
                    generalFunc.showMessage(backImgView, generalFunc.retrieveLangLBl("", "LBL_RESTAURANTS_CLOSE_NOTE"));
                }
                return;
            }
//                int QUANTITY = Integer.parseInt(QTYNumberTxtView.getText().toString());
            int QUANTITY = getQty();


            if (QUANTITY >= 1) {
                QUANTITY = QUANTITY + 1;
                double itemTotal = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice")) + Math.abs(toppingPrice) + Math.abs(seloptionPrice);

                if (searchList.get("ispriceshow") != null && searchList.get("ispriceshow").equalsIgnoreCase("separate")/* && optionList.size() > 0*/) {
                    if (seloptionPrice == 0) {
                        if (optionList.size() > 0) {
                            seloptionPrice = GeneralFunctions.parseDoubleValue(0, optionList.get(0).get("fUserPrice"));
                        } else {
                            seloptionPrice = GeneralFunctions.parseDoubleValue(0, searchList.get("fPrice"));
                        }
                    }
                    itemTotal = Math.abs(toppingPrice) + Math.abs(seloptionPrice);

                }
//                totalPriceTxt.setText(searchList.get("currencySymbol") + " " + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(QUANTITY * itemTotal)));
                totalPriceTxt.setText(generalFunc.formatNumAsPerCurrency(generalFunc, "" + generalFunc.convertNumberWithRTL(GeneralFunctions.convertDecimalPlaceDisplay(QUANTITY * itemTotal)), searchList.get("currencySymbol"), true));
                // rechargeBox.setText(""+QUANTITY);

                rechargeBox.setText(generalFunc.convertNumberWithRTL("" + QUANTITY));
                rechargeBox.setEnabled(false);
            }

        } else {
            //  rechargeBox.setText("1");

        }
    }

}
