package com.multixpro.user.rideSharing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.activity.ParentActivity;
import com.adapter.ViewPager2Adapter;
import com.general.files.ActUtils;
import com.general.files.CustomDialog;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.UploadProfileImage;
import com.multixpro.user.MyWalletActivity;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ActivityRidePublishBinding;
import com.multixpro.user.rideSharing.fragment.RidePublishStep1Fragment;
import com.multixpro.user.rideSharing.fragment.RidePublishStep2Fragment;
import com.multixpro.user.rideSharing.fragment.RidePublishStep3Fragment;
import com.multixpro.user.rideSharing.fragment.RidePublishStep4Fragment;
import com.multixpro.user.rideSharing.model.RidePublishData;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RidePublish extends ParentActivity {

    public ActivityRidePublishBinding binding;
    public Toolbar mToolbar;
    private ViewPager2Adapter mViewPager2Adapter;
    private final ArrayList<Fragment> listOfFrag = new ArrayList<>();

    public RidePublishData mPublishData;
    public HashMap<String, String> myRideDataHashMap;
    public String indexView, dTitle, isBook, vImage = "";
    public boolean isUploadImageNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_publish);

        mPublishData = new RidePublishData();

        indexView = getIntent().getStringExtra("indexView");
        dTitle = getIntent().getStringExtra("dTitle");
        isBook = getIntent().getStringExtra("isBook");

        myRideDataHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("myRideDataHashMap");
        if (myRideDataHashMap != null) {
            setEditData(myRideDataHashMap);
        }

        initialization();
        mainDataSet();
    }

    private void setEditData(HashMap<String, String> rideDataHashMap) {
        RidePublishData.LocationDetails mLocationDetails = new RidePublishData.LocationDetails();
        mLocationDetails.setFromLatitude(rideDataHashMap.get("tStartLat"));
        mLocationDetails.setFromLongitude(rideDataHashMap.get("tStartLong"));
        mLocationDetails.setFromAddress(rideDataHashMap.get("tStartLocation"));
        mLocationDetails.setToLatitude(rideDataHashMap.get("tEndLat"));
        mLocationDetails.setToLongitude(rideDataHashMap.get("tEndLong"));
        mLocationDetails.setToAddress(rideDataHashMap.get("tEndLocation"));
        mPublishData.setLocationDetails(mLocationDetails);

        //String mDate = Utils.convertDateToFormat(CommonUtilities.WithoutDayFormat, Utils.convertStringToDate("EEE dd MMMM", rideDataHashMap.get("StartDate")));
        //String mTime = Utils.convertDateToFormat(CommonUtilities.onlyTimeAMPM, Utils.convertStringToDate(CommonUtilities.onlyTimeAMPM, rideDataHashMap.get("StartTime")));
        mPublishData.setDateTime(rideDataHashMap.get("StartDate") + " " + rideDataHashMap.get("StartTime"));
        mPublishData.setPerSeat(rideDataHashMap.get("AvailableSeats"));
        mPublishData.setRecommendedPrice(rideDataHashMap.get("fPrice"));

        mPublishData.setDocumentIds(rideDataHashMap.get("tDocumentIds"));
        mPublishData.setDynamicDetailsArray(generalFunc.getJsonArray(rideDataHashMap.get("tDriverDetails")));

        mPublishData.setStartCity(rideDataHashMap.get("tStartCity"));
        mPublishData.setEndCity(rideDataHashMap.get("tEndCity"));
    }

    private void initialization() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        MTextView titleTxt = findViewById(R.id.titleTxt);
        if (dTitle != null) {
            titleTxt.setText(dTitle);
        } else {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PUBLISH_TXT"));
        }

        binding.bottomAreaView.setVisibility(View.VISIBLE);
        binding.loading.setVisibility(View.GONE);

        binding.publishRideBtn.setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PUBLISH_BTN_TXT"));
        binding.publishRideBtn.setVisibility(View.GONE);
        addToClickHandler(binding.previousBtn);
        addToClickHandler(binding.nextBtn);
        addToClickHandler(binding.publishRideBtn);
        if (generalFunc.isRTLmode()) {
            binding.previousBtn.setRotation(0);
            binding.nextBtn.setRotation(180);
        }

        mViewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager(), this.getLifecycle(), listOfFrag);
        binding.rideSharingStepViewPager.setAdapter(mViewPager2Adapter);
        binding.rideSharingStepViewPager.setUserInputEnabled(false);
        binding.rideSharingStepViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setPagerHeight();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void mainDataSet() {
        listOfFrag.clear();

        if (Utils.checkText(indexView)) {
            if (indexView.equalsIgnoreCase("0")) {
                listOfFrag.add(new RidePublishStep1Fragment());
            } else if (indexView.equalsIgnoreCase("1")) {
                listOfFrag.add(new RidePublishStep2Fragment());
            } else if (indexView.equalsIgnoreCase("2")) {
                listOfFrag.add(new RidePublishStep3Fragment());
            }
            binding.bottomAreaView.setVisibility(View.GONE);
        } else {
            listOfFrag.add(new RidePublishStep1Fragment());
            listOfFrag.add(new RidePublishStep2Fragment());
            listOfFrag.add(new RidePublishStep3Fragment());
            listOfFrag.add(new RidePublishStep4Fragment());
        }

        binding.rideSharingStepViewPager.setOffscreenPageLimit(listOfFrag.size());
        Objects.requireNonNull(binding.rideSharingStepViewPager.getAdapter()).notifyDataSetChanged();
        if (listOfFrag.size() >= 2) {
            binding.previousBtn.setVisibility(View.INVISIBLE);
            binding.nextBtn.setVisibility(View.VISIBLE);
            setToolSubTitle();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setToolSubTitle() {
        int currItemPos = binding.rideSharingStepViewPager.getCurrentItem();
        if (generalFunc.isRTLmode()) {
            binding.StepHTxt.setText(listOfFrag.size() + "/" + (currItemPos + 1) + " " + generalFunc.retrieveLangLBl("", "LBL_STEP_TXT"));
        } else {
            binding.StepHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_STEP_TXT") + " " + (currItemPos + 1) + "/" + listOfFrag.size() + " ");
        }
        if (currItemPos > 0) {
            binding.previousBtn.setVisibility(View.VISIBLE);
        } else {
            binding.previousBtn.setVisibility(View.INVISIBLE);
        }
        if ((currItemPos + 1) == listOfFrag.size()) {
            binding.publishRideBtn.setVisibility(View.VISIBLE);
            binding.nextBtn.setVisibility(View.GONE);
        } else {
            binding.publishRideBtn.setVisibility(View.GONE);
            binding.nextBtn.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPagePrevious() {
        Utils.hideKeyboard(this);
        binding.rideSharingStepViewPager.setCurrentItem(binding.rideSharingStepViewPager.getCurrentItem() - 1, true);
        setToolSubTitle();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPageNext() {
        Utils.hideKeyboard(this);

        int currItemPos = binding.rideSharingStepViewPager.getCurrentItem();
        if (currItemPos == (listOfFrag.size() - 1)) {

            generalFunc.showMessage(binding.bottomAreaView, "Done");

        } else {
            binding.rideSharingStepViewPager.setCurrentItem(currItemPos + 1, true);
            setToolSubTitle();
        }
    }

    public void setPagerHeight() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Fragment fragment = mViewPager2Adapter.createFragment(binding.rideSharingStepViewPager.getCurrentItem());
            View childView = fragment.getView();
            if (childView == null) return;

            int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(childView.getWidth(), View.MeasureSpec.EXACTLY);
            int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            childView.measure(wMeasureSpec, hMeasureSpec);

            LinearLayout.LayoutParams lyParams = (LinearLayout.LayoutParams) binding.rideSharingStepViewPager.getLayoutParams();
            if (lyParams.height != childView.getMeasuredHeight()) {
                lyParams.height = childView.getMeasuredHeight();
                binding.rideSharingStepViewPager.setLayoutParams(lyParams);
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        if (indexView == null) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PUBLISHED_RIDE_EXIT_TXT"), generalFunc.retrieveLangLBl("", "LBL_NO"),
                    generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                        if (buttonId == 1) {
                            super.onBackPressed();
                        }
                    });
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        } else if (i == R.id.previousBtn) {
            if (binding.previousBtn.getVisibility() == View.VISIBLE) {
                setPagePrevious();
            }

        } else if (i == R.id.nextBtn) {
            if (listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem()) instanceof RidePublishStep1Fragment) {
                RidePublishStep1Fragment fragment = (RidePublishStep1Fragment) listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem()) instanceof RidePublishStep2Fragment) {
                RidePublishStep2Fragment fragment = (RidePublishStep2Fragment) listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem());
                fragment.checkPageNext();
            } else if (listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem()) instanceof RidePublishStep3Fragment) {
                RidePublishStep3Fragment fragment = (RidePublishStep3Fragment) listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem());
                fragment.checkPageNext();
            }

        } else if (i == R.id.publishRideBtn) {
            if (listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem()) instanceof RidePublishStep4Fragment) {
                RidePublishStep4Fragment fragment = (RidePublishStep4Fragment) listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem());
                fragment.checkPageNext();
            }
        }
    }

    private Context getActContext() {
        return RidePublish.this;
    }

    @Override
    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {
        if (listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem()) instanceof RidePublishStep3Fragment) {
            RidePublishStep3Fragment fragment = (RidePublishStep3Fragment) listOfFrag.get(binding.rideSharingStepViewPager.getCurrentItem());
            fragment.onFileSelected(mFileUri, mFilePath, mFileType);
        }
    }

    public void handleImgUploadResponse(String responseString) {
        if (responseString != null && !responseString.equalsIgnoreCase("")) {
            if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                CustomDialog customDialog = new CustomDialog(this, generalFunc);
                customDialog.setDetails(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message_title", responseString)),
                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)),
                        generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_VIEW_MY_RIDES_TXT"),
                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"),
                        false, R.drawable.ic_correct_2, false, 1, true);
                customDialog.setRoundedViewBackgroundColor(R.color.appThemeColor_1);
                customDialog.setRoundedViewBorderColor(R.color.white);
                customDialog.setImgStrokWidth(15);
                customDialog.setBtnRadius(10);
                customDialog.setIconTintColor(R.color.white);
                customDialog.setPositiveBtnBackColor(R.color.appThemeColor_2);
                customDialog.setPositiveBtnTextColor(R.color.white);
                customDialog.createDialog();
                customDialog.setPositiveButtonClick(() -> {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isRestartApp", true);
                    new ActUtils(this).startActWithData(RideMyList.class, bn);
                });
                customDialog.setNegativeButtonClick(() -> MyApp.getInstance().restartWithGetDataApp());
                customDialog.show();
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)), generalFunc.retrieveLangLBl("", "LBL_ADD_NOW"),
                        generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> {
                            if (buttonId == 0) {
                                new ActUtils(getActContext()).startAct(MyWalletActivity.class);
                            }
                        });
            }
        } else {
            generalFunc.showError();
        }
    }

    public void sendToPublishRide() {

        RidePublishData.LocationDetails locationDetails = mPublishData.getLocationDetails();
        if (locationDetails == null || binding.loading.getVisibility() == View.VISIBLE) {
            return;
        }

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(Utils.generateImageParams("type", "PublishRide"));

        paramsList.add(Utils.generateImageParams("tStartCity", mPublishData.getStartCity()));
        paramsList.add(Utils.generateImageParams("tEndCity", mPublishData.getEndCity()));

        // Step1 Data
        paramsList.add(Utils.generateImageParams("tStartLat", locationDetails.getFromLatitude()));
        paramsList.add(Utils.generateImageParams("tStartLong", locationDetails.getFromLongitude()));
        paramsList.add(Utils.generateImageParams("tEndLat", locationDetails.getToLatitude()));
        paramsList.add(Utils.generateImageParams("tEndLong", locationDetails.getToLongitude()));

        // Step2 Data
        paramsList.add(Utils.generateImageParams("dStartDate", mPublishData.getDateTime()));
        paramsList.add(Utils.generateImageParams("iAvailableSeats", mPublishData.getPerSeat()));
        paramsList.add(Utils.generateImageParams("fPrice", mPublishData.getRecommendedPrice()));

        // step3Data
        paramsList.add(Utils.generateImageParams("tDriverDetails", mPublishData.getDynamicDetailsArray() != null ? mPublishData.getDynamicDetailsArray().toString() : ""));

        // step4Data
        paramsList.add(Utils.generateImageParams("documentIds", mPublishData.getDocumentIds()));

        UploadProfileImage uploadProfileImage;
        if (Utils.checkText(vImage)) {

            if (isUploadImageNew) {
                uploadProfileImage = new UploadProfileImage(true, this, vImage, "TempFile." + Utils.getFileExt(vImage), paramsList);
            } else {
                paramsList.add(Utils.generateImageParams("rideImage", vImage));
                uploadProfileImage = new UploadProfileImage(true, this, "", "TempFile." + Utils.getFileExt(vImage), paramsList);
            }
        } else {
            uploadProfileImage = new UploadProfileImage(true, this, vImage, "TempFile." + Utils.getFileExt(vImage), paramsList);
        }
        uploadProfileImage.execute(binding.loading);
    }
}