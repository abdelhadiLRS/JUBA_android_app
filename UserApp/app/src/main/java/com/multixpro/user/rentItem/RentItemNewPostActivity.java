package com.multixpro.user.rentItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.activity.ParentActivity;
import com.adapter.ViewPager2Adapter;
import com.general.files.ActUtils;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.SuccessDialog;
import com.multixpro.user.PaymentWebviewActivity;
import com.multixpro.user.R;
import com.multixpro.user.rentItem.adapter.RentItemStepsAdapter;
import com.multixpro.user.rentItem.fragment.RentCategoryFragment;
import com.multixpro.user.rentItem.fragment.RentItemDynamicDetailsFragment;
import com.multixpro.user.rentItem.fragment.RentItemLocationDetailsFragment;
import com.multixpro.user.rentItem.fragment.RentItemPaymentPlanFragment;
import com.multixpro.user.rentItem.fragment.RentItemPhotosFragment;
import com.multixpro.user.rentItem.fragment.RentItemPickupAvailabilityFragment;
import com.multixpro.user.rentItem.fragment.RentItemPricingDetailsFragment;
import com.multixpro.user.rentItem.fragment.RentItemReviewAllDetailsFragment;
import com.multixpro.user.rentItem.fragment.RentSubCategoryFragment;
import com.multixpro.user.rentItem.model.RentItemData;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RentItemNewPostActivity extends ParentActivity {

    private MTextView subTitleTxt;
    public ProgressBar loading;

    private RecyclerView rvStepViews;
    private RentItemStepsAdapter mStepsAdapter;
    public final ArrayList<HashMap<String, String>> mStepList = new ArrayList<>();

    private LinearLayout mainArea, bottomAreaView;
    private ImageView previousBtn, nextBtn;
    public MTextView selectServiceTxt, publishPostBtn;

    public boolean isFirst = false, isEListing = false, isPickupAvailabilityRemove = true;

    private ViewPager2 mViewPager2;
    private ViewPager2Adapter mViewPager2Adapter;
    private final ArrayList<Fragment> listOfFrag = new ArrayList<>();

    public JSONArray mCategoriesArr;

    public RentItemData mRentItemData;
    public HashMap<String, String> mRentEditHashMap;
    public String eType;
    public boolean isAvailabilityDisplay = false;
    private View ll_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item_new_post);

        mRentItemData = new RentItemData();
        bottomAreaView = findViewById(R.id.bottomAreaView);
        bottomAreaView.setVisibility(View.GONE);
        mRentItemData.setiItemCategoryId(getIntent().getStringExtra("iCategoryId"));

        NestedScrollView rentItemStepNScrollp = findViewById(R.id.rentItemStepNScrollp);
        View shadowHeaderView = findViewById(R.id.shadowHeaderView);
        shadowHeaderView.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rentItemStepNScrollp.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (!v.canScrollVertically(-1)) {
                    shadowHeaderView.setVisibility(View.INVISIBLE);
                } else {
                    shadowHeaderView.setVisibility(View.VISIBLE);
                }
            });
        }
        eType = getIntent().getStringExtra("eType");
        if (!Utils.checkText(eType)) {
            eType = "";
        }

        mRentEditHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("rentEditHashMap");
        if (mRentEditHashMap != null) {
            setEditData(mRentEditHashMap);
        }

        toolbarData();
        setStepView();
        mainDataSet();
        extraImageDelete();
        getNewPostDataList();

        ll_progressbar = findViewById(R.id.ll_progressbar);
        ll_progressbar.setVisibility(View.GONE);
    }

    private void setEditData(HashMap<String, String> rentEditHashMap) {
        mRentItemData.seteStatusOrg(rentEditHashMap.get("eStatusOrg"));
        mRentItemData.setiRentItemPostId(rentEditHashMap.get("iRentItemPostId"));
        mRentItemData.setiTmpRentItemPostId(rentEditHashMap.get("iTmpRentItemPostId"));
        mRentItemData.setiItemCategoryId(rentEditHashMap.get("iItemCategoryId"));
        mRentItemData.setiItemSubCategoryId(rentEditHashMap.get("iItemSubCategoryId"));

        RentItemData.LocationDetails mLocationDetails = new RentItemData.LocationDetails();
        mLocationDetails.setvLocation(rentEditHashMap.get("vLocation"));
        mLocationDetails.setvLatitude(rentEditHashMap.get("vLatitude"));
        mLocationDetails.setvLongitude(rentEditHashMap.get("vLongitude"));
        mLocationDetails.setvBuildingNo(rentEditHashMap.get("vBuildingNo"));
        mLocationDetails.setvAddress(rentEditHashMap.get("vAddress"));
        mLocationDetails.setShowMyAddress(rentEditHashMap.get("eIsUserAddressDisplay").equalsIgnoreCase("Yes"));
        mRentItemData.setLocationDetails(mLocationDetails);

        mRentItemData.setfAmountWithoutSymbol(rentEditHashMap.get("fAmountWithoutSymbol"));
        mRentItemData.seteRentItemDuration(rentEditHashMap.get("eRentItemDuration"));
        mRentItemData.setisBuySell(rentEditHashMap.get("isBuySell"));

        mRentItemData.setPickupTimeSlot(generalFunc.getJsonArray(rentEditHashMap.get("timeslot")));
        mRentItemData.setShowCallMe(rentEditHashMap.get("eIsUserNumberDisplay").equalsIgnoreCase("Yes"));
        mRentItemData.setiPaymentPlanId(rentEditHashMap.get("iPaymentPlanId"));
    }

    private void toolbarData() {
        ImageView backImgView = findViewById(R.id.backImgView);
        addToClickHandler(backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }

        MTextView titleTxt = findViewById(R.id.titleTxt);
        if (mRentEditHashMap != null) {
            titleTxt.setText(generalFunc.retrieveLangLBl("edit post", "LBL_RENT_EDIT_POST"));
        } else {
            titleTxt.setText(generalFunc.retrieveLangLBl("new post", "LBL_RENT_NEW_POST"));
        }

        subTitleTxt = findViewById(R.id.StepHTxt);
        subTitleTxt.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void setToolSubTitle() {
        int currItemPos = mViewPager2.getCurrentItem();

        int totalItem = isEListing || !isPickupAvailabilityRemove ? listOfFrag.size() - 1 : listOfFrag.size();


        if (generalFunc.isRTLmode()) {
            bottomAreaView.setVisibility(View.VISIBLE);
            subTitleTxt.setText(totalItem + "/" + (currItemPos + 1) + " " + generalFunc.retrieveLangLBl("", "LBL_STEP_TXT"));
        } else {
            bottomAreaView.setVisibility(View.VISIBLE);
            if (isAvailabilityDisplay) {
                currItemPos = currItemPos - 1;
                // subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STEP_TXT") + " " + (currItemPos - 1) + "/" + totalItem + " ");
            }
            subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STEP_TXT") + " " + (currItemPos + 1) + "/" + totalItem + " ");
        }
        if (currItemPos > 0) {
            previousBtn.setVisibility(View.VISIBLE);
        } else {
            previousBtn.setVisibility(View.INVISIBLE);
        }
        if ((currItemPos + 1) == mStepList.size()) {
            nextBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_correct_1, null));
            DrawableCompat.setTint(nextBtn.getDrawable().mutate(), ContextCompat.getColor(this, R.color.text23Pro_Dark));
            if (generalFunc.isRTLmode()) {
                nextBtn.setRotation(0);
            }
        } else {
            nextBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_right_arrow_1, null));
            DrawableCompat.setTint(nextBtn.getDrawable().mutate(), ContextCompat.getColor(this, R.color.text23Pro_Dark));
            if (generalFunc.isRTLmode()) {
                nextBtn.setRotation(180);
            }
        }

        int k = 2;
        if (isPickupAvailabilityRemove) {
            k = 1;
        }

        if ((currItemPos + k) == listOfFrag.size()) {
            publishPostBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            publishPostBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private void setStepView() {
        rvStepViews = findViewById(R.id.rvStepViews);
        rvStepViews.setOnTouchListener((v, event) -> true);
        mStepsAdapter = new RentItemStepsAdapter(generalFunc, mStepList);
        rvStepViews.setAdapter(mStepsAdapter);

        selectServiceTxt = findViewById(R.id.selectServiceTxt);
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);
        publishPostBtn = findViewById(R.id.publishPostBtn);
        publishPostBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PUBLISH_POST_TXT"));
        publishPostBtn.setVisibility(View.GONE);
        addToClickHandler(previousBtn);
        addToClickHandler(nextBtn);
        addToClickHandler(publishPostBtn);
        if (generalFunc.isRTLmode()) {
            previousBtn.setRotation(0);
            nextBtn.setRotation(180);
        }
    }

    private void mainDataSet() {
        mainArea = findViewById(R.id.mainArea);
        loading = findViewById(R.id.loading);
        mViewPager2 = findViewById(R.id.rentItemStepViewPager);

        mViewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager(), this.getLifecycle(), listOfFrag);
        mViewPager2.setAdapter(mViewPager2Adapter);
        mViewPager2.setUserInputEnabled(false);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setPagerHeight();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPagePrevious() {
        Utils.hideKeyboard(this);
        int currItemPos = mViewPager2.getCurrentItem();
        HashMap<String, String> updateData = mStepList.get(currItemPos);
        updateData.put("selPos", "");
        mStepList.set(currItemPos, updateData);

        int previousPos = currItemPos - 1;

        HashMap<String, String> updateData1 = mStepList.get(previousPos);
        updateData1.put("selPos", "" + previousPos);
        mStepList.set(previousPos, updateData1);

        mStepsAdapter.notifyDataSetChanged();

        mViewPager2.setCurrentItem(previousPos, true);
        setToolSubTitle();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //
            rvStepViews.smoothScrollToPosition(mViewPager2.getCurrentItem());
        }, 20);
        isAvailabilityDisplay = false;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPageNext() {
        Utils.hideKeyboard(this);

        int currItemPos = mViewPager2.getCurrentItem();
        if (currItemPos == (listOfFrag.size() - 1)) {

            if (listOfFrag.get(currItemPos) instanceof RentItemPaymentPlanFragment) {
                finalPost(mRentItemData.getiPaymentPlanId());
                //generalFunc.showMessage(selectServiceTxt, generalFunc.retrieveLangLBl("done", ""));
            } else {
                Bundle bn = new Bundle();
                bn.putBoolean("isGetList", true);
                (new ActUtils(this)).setOkResult(bn);
                finish();
            }
        } else {
            int nextPos = currItemPos + 1;

            HashMap<String, String> updateData = mStepList.get(currItemPos);
            updateData.put("selPos", "-" + nextPos);
            mStepList.set(currItemPos, updateData);

            HashMap<String, String> updateData1 = mStepList.get(nextPos);
            updateData1.put("selPos", "" + nextPos);
            mStepList.set(nextPos, updateData1);

            mStepsAdapter.notifyDataSetChanged();
            rvStepViews.smoothScrollToPosition(nextPos);

            mViewPager2.setCurrentItem(nextPos, true);
            setToolSubTitle();
        }
    }

    private void finalPost(String iPaymentPlanId) {
        ll_progressbar.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GenerateFinalPost");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iTmpRentItemPostId", mRentItemData.getiTmpRentItemPostId() == null ? "" : mRentItemData.getiTmpRentItemPostId());
        parameters.put("iPaymentPlanId", iPaymentPlanId);
        parameters.put("eType", eType);

        ApiHandler.execute(this, parameters, responseString -> {

            ll_progressbar.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    String paymentURL = generalFunc.getJsonValue("RENTITEM_PAYMENT_URL", responseString);
                    if (Utils.checkText(paymentURL)) {
                        Intent intent = new Intent(this, PaymentWebviewActivity.class);
                        Bundle bn = new Bundle();
                        bn.putString("url", paymentURL);
                        bn.putBoolean("handleResponse", true);
                        intent.putExtras(bn);
                        webViewPaymentActivity.launch(intent);
                    } else {
                        showRentItemPostDoneAlert();
                    }
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    ActivityResultLauncher<Intent> webViewPaymentActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    showRentItemPostDoneAlert();
                }
            });

    private void showRentItemPostDoneAlert() {
        SuccessDialog.showSuccessDialog(this, generalFunc.retrieveLangLBl("", "LBL_RENT_POST_SUCCESS_TXT"),
                generalFunc.retrieveLangLBl("", "LBL_RENT_POST_SUCCESS_MSG"),
                generalFunc.retrieveLangLBl("Ok", "LBL_RENT_VIEW_ITEMS_USER_TXT"),
                generalFunc.retrieveLangLBl("Okay", "LBL_GIFT_CARD_OK_BTN_TXT"), false, () -> {
                    if (getIntent().getStringExtra("iCategoryId") != null && !getIntent().getStringExtra("iCategoryId").equals("")) {
                        Bundle bn = new Bundle();
                        bn.putString("eType", eType);
                        new ActUtils(this).startActWithData(RentItemListPostActivity.class, bn);
                        finish();
                    } else {
                        Bundle bn = new Bundle();
                        bn.putBoolean("isGetList", true);
                        (new ActUtils(this)).setOkResult(bn);
                        finish();
                    }

                }, () -> {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isGetList", true);
                    (new ActUtils(this)).setOkResult(bn);
                    finish();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getNewPostDataList() {

        mainArea.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getRentItemCategories");
        parameters.put("userId", generalFunc.getMemberId());
        parameters.put("iCategoryId", mRentItemData.getiItemCategoryId() == null ? "" : mRentItemData.getiItemCategoryId());
        parameters.put("eType", eType);

        ApiHandler.execute(this, parameters, responseString -> {

            listOfFrag.clear();

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                    JSONArray rentItemCategoriesArr = generalFunc.getJsonArray(Utils.message_str, responseString);
                    boolean isCategoryList = false, isSubCategoryList = false;
                    if (rentItemCategoriesArr != null && rentItemCategoriesArr.length() > 0) {
                        isCategoryList = true;
                        mCategoriesArr = rentItemCategoriesArr;
                        for (int k = 0; k < rentItemCategoriesArr.length(); k++) {
                            JSONArray rentSubCategoriesArr = generalFunc.getJsonArray("SubCategory", generalFunc.getJsonObject(rentItemCategoriesArr, k));
                            if (rentSubCategoriesArr != null && rentSubCategoriesArr.length() > 0) {
                                isSubCategoryList = true;
                            }
                        }
                    }
                    if (mRentEditHashMap == null) {
                        mRentItemData.setPickupTimeSlot(generalFunc.getJsonArray("timeslot", responseString));

                        if (isCategoryList) {
                            listOfFrag.add(new RentCategoryFragment());
                        }
                        if (isSubCategoryList && !(eType.equalsIgnoreCase("RentEstate") || eType.equalsIgnoreCase("RentCars"))) {
                            listOfFrag.add(new RentSubCategoryFragment());
                        }
                    }


                    listOfFrag.add(new RentItemDynamicDetailsFragment());
                    listOfFrag.add(new RentItemPhotosFragment());
                    listOfFrag.add(new RentItemLocationDetailsFragment());
                    listOfFrag.add(new RentItemPricingDetailsFragment());
                    listOfFrag.add(new RentItemPickupAvailabilityFragment());
                    listOfFrag.add(new RentItemReviewAllDetailsFragment());
                    listOfFrag.add(new RentItemPaymentPlanFragment());

                    if (mRentEditHashMap != null && !mRentItemData.geteStatusOrg().equalsIgnoreCase("Expired")) {
                        listOfFrag.remove(listOfFrag.size() - 1);
                    }

                    mViewPager2.setOffscreenPageLimit(listOfFrag.size());

                    Objects.requireNonNull(mViewPager2.getAdapter()).notifyDataSetChanged();

                    for (int i = 1; i <= listOfFrag.size(); i++) {
                        HashMap<String, String> stepHashMap = new HashMap<>();
                        if (i == 1) {
                            stepHashMap.put("selPos", "0");
                        }
                        stepHashMap.put("vNumber", "" + i);
                        stepHashMap.put("vTitle", generalFunc.retrieveLangLBl("", "LBL_STEP_TXT"));
                        mStepList.add(stepHashMap);
                    }
                    mStepsAdapter.notifyDataSetChanged();
                    setToolSubTitle();

                    loading.setVisibility(View.GONE);
                    mainArea.setVisibility(View.VISIBLE);
                    if (listOfFrag.size() > 0) {
                        previousBtn.setVisibility(View.INVISIBLE);
                        nextBtn.setVisibility(View.VISIBLE);
                        tempDisableNextBtn(1000);
                    }

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
    }

    private void tempDisableNextBtn(int millis) {
        nextBtn.setEnabled(false);
        new Handler(Looper.getMainLooper()).postDelayed(() -> nextBtn.setEnabled(true), millis);
    }

    private void extraImageDelete() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "DeleteRentItemImages");
        parameters.put("iMemberId", generalFunc.getMemberId());

        ApiHandler.execute(this, parameters, responseString -> {

        });
    }

    @Override
    public void onBackPressed() {
        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_RENT_EXIT_CONFIRMATION_TXT"),
                generalFunc.retrieveLangLBl("", "LBL_NO"),
                generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                    if (buttonId == 1) {
                        super.onBackPressed();
                    }
                });

    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        } else if (i == R.id.previousBtn) {
            if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemReviewAllDetailsFragment) {
                if (isEListing || !isPickupAvailabilityRemove) {
                    setPagePrevious();
                }
            }
            setPagePrevious();
        } else if (i == R.id.nextBtn) {
            tempDisableNextBtn(500);
            if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentCategoryFragment) {
                RentCategoryFragment fragment = (RentCategoryFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentSubCategoryFragment) {
                RentSubCategoryFragment fragment = (RentSubCategoryFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemDynamicDetailsFragment) {
                RentItemDynamicDetailsFragment fragment = (RentItemDynamicDetailsFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemPhotosFragment) {
                RentItemPhotosFragment fragment = (RentItemPhotosFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemLocationDetailsFragment) {
                RentItemLocationDetailsFragment fragment = (RentItemLocationDetailsFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemPricingDetailsFragment) {
                RentItemPricingDetailsFragment fragment = (RentItemPricingDetailsFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemPickupAvailabilityFragment) {
                RentItemPickupAvailabilityFragment fragment = (RentItemPickupAvailabilityFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemReviewAllDetailsFragment) {
                RentItemReviewAllDetailsFragment fragment = (RentItemReviewAllDetailsFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemPaymentPlanFragment) {
                RentItemPaymentPlanFragment fragment = (RentItemPaymentPlanFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            }
        } else if (i == R.id.publishPostBtn) {
            if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemPaymentPlanFragment) {
                RentItemPaymentPlanFragment fragment = (RentItemPaymentPlanFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemReviewAllDetailsFragment) {
                RentItemReviewAllDetailsFragment fragment = (RentItemReviewAllDetailsFragment) listOfFrag.get(mViewPager2.getCurrentItem());
                fragment.checkPageNext();
            }
        }
    }

    @Override
    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {
        super.onFileSelected(mFileUri, mFilePath, mFileType);
        if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemPhotosFragment) {
            RentItemPhotosFragment fragment = (RentItemPhotosFragment) listOfFrag.get(mViewPager2.getCurrentItem());
            fragment.configMedia(generalFunc, mFilePath, mFileType);
        }
    }

    public void handleImgUploadResponse(String responseString) {
        if (listOfFrag.get(mViewPager2.getCurrentItem()) instanceof RentItemPhotosFragment) {
            RentItemPhotosFragment fragment = (RentItemPhotosFragment) listOfFrag.get(mViewPager2.getCurrentItem());
            fragment.handleImgUploadResponse(generalFunc, responseString);
        }
    }

    public void setPagerHeight() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Fragment fragment = mViewPager2Adapter.createFragment(mViewPager2.getCurrentItem());
            View childView = fragment.getView();
            if (childView == null) return;

            int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(childView.getWidth(), View.MeasureSpec.EXACTLY);
            int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            childView.measure(wMeasureSpec, hMeasureSpec);

            LinearLayout.LayoutParams lyParams = (LinearLayout.LayoutParams) mViewPager2.getLayoutParams();
            if (lyParams.height != childView.getMeasuredHeight()) {
                lyParams.height = childView.getMeasuredHeight();
                mViewPager2.setLayoutParams(lyParams);
            }
        }, 200);
    }

    public Fragment getNextFragment() {
        try {
            return listOfFrag.get(mViewPager2.getCurrentItem() + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createRentPost() {
        loading.setVisibility(View.VISIBLE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "CreateRentPostData");
        parameters.put("iMemberId", generalFunc.getMemberId());

        if (mRentItemData.geteStatusOrg().equalsIgnoreCase("Expired")) {
            parameters.put("exiprePaymentPending", "Yes");
        }

        // Step : 1, 2
        parameters.put("iItemCategoryId", mRentItemData.getiItemCategoryId());
        parameters.put("iItemSubCategoryId", mRentItemData.getiItemSubCategoryId());
        if (Utils.checkText(mRentItemData.getiRentItemPostId())) {
            parameters.put("iRentItemPostId", mRentItemData.getiRentItemPostId());
        } else {
            parameters.put("iTmpRentItemPostId", mRentItemData.getiTmpRentItemPostId() == null ? "" : mRentItemData.getiTmpRentItemPostId());
        }

        // Step : 3
        parameters.put("tFieldsArr", mRentItemData.getDynamicDetailsArray() == null ? "" : mRentItemData.getDynamicDetailsArray().toString());

        // Step : 4
        parameters.put("vImageIds", mRentItemData.getiRentImageId());

        // Step : 5
        RentItemData.LocationDetails locationDetails = mRentItemData.getLocationDetails();
        if (locationDetails != null) {
            parameters.put("vLocation", locationDetails.getvLocation());
            parameters.put("vLatitude", locationDetails.getvLatitude());
            parameters.put("vLongitude", locationDetails.getvLongitude());

            parameters.put("vAddress", locationDetails.getvAddress());
            parameters.put("vBuildingNo", locationDetails.getvBuildingNo());

            parameters.put("eIsUserAddressDisplay", locationDetails.isShowMyAddress() ? "Yes" : "No");
        }

        // Step : 6
        parameters.put("fAmount", mRentItemData.getfAmountWithoutSymbol());
        parameters.put("eRentItemDuration", mRentItemData.geteRentItemDuration());

        // Step : 7
        parameters.put("timeslot", mRentItemData.getPickupTimeSlot() == null ? "" : mRentItemData.getPickupTimeSlot().toString());
        parameters.put("eIsUserNumberDisplay", mRentItemData.isShowCallMe() ? "Yes" : "No");

        ApiHandler.execute(this, parameters, responseString -> {

            loading.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    if (isEListing || !isPickupAvailabilityRemove) {
                        isAvailabilityDisplay = true;
                        setPageNext();

                    }
                    if (getNextFragment() instanceof RentItemReviewAllDetailsFragment) {
                        RentItemReviewAllDetailsFragment fragment = (RentItemReviewAllDetailsFragment) getNextFragment();
                        String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                        mRentItemData.setiTmpRentItemPostId(generalFunc.getJsonValue("iTmpRentItemPostId", message));
                        fragment.setData(generalFunc, message);
                        setPageNext();
                    }
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                }
            } else {
                generalFunc.showError();
            }
        });
    }
}