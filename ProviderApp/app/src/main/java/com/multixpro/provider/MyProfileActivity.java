package com.multixpro.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.activity.ParentActivity;
import com.fragments.EditProfileFragment;
import com.general.files.ActUtils;
import com.general.files.FilePath;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.UploadProfileImage;
import com.model.ServiceModule;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProfileActivity extends ParentActivity {

    public boolean isEdit = false, isMobile = false, isEmail = false;
    private MTextView titleTxt;
    private SelectableRoundedImageView userProfileImgView;
    private ImageView editIconImgView;

    private EditProfileFragment editProfileFrag;
    private String SITE_TYPE = "", SITE_TYPE_DEMO_MSG = "";

    public boolean isDriverOnline, isUfxServicesEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        updateObj();

        isDriverOnline = getIntent().getExtras().getBoolean("isDriverOnline");
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        isMobile = getIntent().getBooleanExtra("isMobile", false);
        isEmail = getIntent().getBooleanExtra("isEmail", false);

        titleTxt = findViewById(R.id.titleTxt);
        ImageView backImgView = findViewById(R.id.backImgView);
        userProfileImgView = findViewById(R.id.userProfileImgView);

        editIconImgView = findViewById(R.id.editIconImgView);
        FrameLayout userImgArea = findViewById(R.id.userImgArea);

        if (ServiceModule.IsTrackingProvider) {
            LinearLayout editArea = findViewById(R.id.editArea);
            editArea.setVisibility(View.GONE);
            editIconImgView.setVisibility(View.GONE);
        } else {
            addToClickHandler(userImgArea);
        }
        addToClickHandler(backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        userProfileImgView.setImageResource(R.mipmap.ic_no_pic_user);
        setImage();

        if (generalFunc.retrieveValue("ENABLE_EDIT_DRIVER_PROFILE").equalsIgnoreCase("No") && !ServiceModule.IsTrackingProvider) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROFILE_EDIT_BLOCK_TXT"));
        }

        SITE_TYPE = generalFunc.getJsonValueStr("SITE_TYPE", obj_userProfile);
        SITE_TYPE_DEMO_MSG = generalFunc.getJsonValueStr("SITE_TYPE_DEMO_MSG", obj_userProfile);

        if (!isDriverOnline) {
            openEditProfileFragment();
        } else {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_EDIT_PROFILE_BLOCK_DRIVER"));
        }
    }

    private void setImage() {
        String url = CommonUtilities.PROVIDER_PHOTO_PATH + generalFunc.getMemberId() + "/" + generalFunc.getJsonValue("vImage", obj_userProfile);
        generalFunc.checkProfileImage(userProfileImgView, url, R.mipmap.ic_no_pic_user, R.mipmap.ic_no_pic_user);

        String vImgName_str = generalFunc.getJsonValueStr("vImage", obj_userProfile);
        if (vImgName_str == null || vImgName_str.equals("") || vImgName_str.equals("NONE")) {
            editIconImgView.setImageResource(R.drawable.ic_add_);
        } else {
            editIconImgView.setImageResource(R.drawable.ic_edit_icon);
        }
    }

    public void changePageTitle(String title) {
        titleTxt.setText(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public Context getActContext() {
        return MyProfileActivity.this;
    }

    private void openEditProfileFragment() {
        if (editProfileFrag != null) {
            editProfileFrag = null;
            Utils.runGC();
        }
        editProfileFrag = new EditProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer, editProfileFrag).commit();
    }

    public EditProfileFragment getEditProfileFrag() {
        return this.editProfileFrag;
    }

    public void changeUserProfileJson(String userProfileJson) {
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);
        updateObj();
        Bundle bn = new Bundle();
        HashMap<String, String> storeData = new HashMap<>();
        storeData.put(Utils.WALLET_ENABLE, generalFunc.getJsonValueStr("WALLET_ENABLE", obj_userProfile));
        storeData.put(Utils.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValueStr("REFERRAL_SCHEME_ENABLE", obj_userProfile));
        generalFunc.storeData(storeData);
        new ActUtils(getActContext()).setOkResult(bn);
        generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_INFO_UPDATED_TXT"));
    }

    private void updateObj() {
        String UFX_SERVICE_AVAILABLE = generalFunc.getJsonValueStr("UFX_SERVICE_AVAILABLE", obj_userProfile);
        isUfxServicesEnabled = !Utils.checkText(UFX_SERVICE_AVAILABLE) || UFX_SERVICE_AVAILABLE.equalsIgnoreCase("Yes");
    }

    private View getCurrView() {
        return generalFunc.getCurrentView(MyProfileActivity.this);
    }

    private String[] generateImageParams(String key, String content) {
        String[] tempArr = new String[2];
        tempArr[0] = key;
        tempArr[1] = content;
        return tempArr;
    }

    private void imageUpload(Uri fileUri) {
        if (SITE_TYPE.equalsIgnoreCase("Demo") && generalFunc.getJsonValue("vEmail", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)).equalsIgnoreCase("Driver@gmail.com")) {
            generalFunc.showGeneralMessage("", SITE_TYPE_DEMO_MSG);
            return;
        }

        if (fileUri == null) {
            generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_ERROR_OCCURED"));
            return;
        }

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("MemberType", Utils.app_type));
        paramsList.add(generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add(generateImageParams("MemberType", Utils.app_type));
        paramsList.add(generateImageParams("type", "uploadImage"));

        String selectedImagePath = FilePath.getPath(getActContext(), fileUri);

        if (selectedImagePath == null || selectedImagePath.equalsIgnoreCase("")) {
            generalFunc.showGeneralMessage("Can't read selected image. Please try again.", generalFunc.retrieveLangLBl("", "LBL_IMAGE_READ_FAILED"));
            return;
        }
        if (Utils.isValidImageResolution(selectedImagePath)) {
            new UploadProfileImage(MyProfileActivity.this, selectedImagePath, Utils.TempProfileImageName, paramsList, "").execute();
        } else {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please select image which has minimum is 256 * 256 resolution.", "LBL_MIN_RES_IMAGE"));
        }
    }

    public void handleImgUploadResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail) {
                generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                obj_userProfile = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                changeUserProfileJson(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                setImage();
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        } else {
            generalFunc.showError();
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        switch (view.getId()) {
            case R.id.backImgView:
                MyProfileActivity.super.onBackPressed();
                break;
            case R.id.userImgArea:
                selectImage();
                break;
        }
    }

    @Override
    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {
        imageUpload(mFileUri);
    }

    private void selectImage() {
        if (isProfileEditRestricted()) {
            boolean isProfileImageBlank = isProfileImageBlank();
            if (isProfileImageBlank) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROFILE_IMAGE_BLOCK"), "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> getFileSelector().openFileSelection(FileSelector.FileType.CroppedImage));
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_EDIT_PROFILE_DISABLED"), "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> {
                });
            }
        } else {
            getFileSelector().openFileSelection(FileSelector.FileType.CroppedImage);
        }
    }

    private boolean isProfileEditRestricted() {
        String ENABLE_EDIT_DRIVER_PROFILE = generalFunc.retrieveValue("ENABLE_EDIT_DRIVER_PROFILE");
        return ENABLE_EDIT_DRIVER_PROFILE.equalsIgnoreCase("No");
    }

    private boolean isProfileImageBlank() {
        String vImgName_str = generalFunc.getJsonValueStr("vImage", obj_userProfile);
        boolean isImageBlank = vImgName_str == null || vImgName_str.equals("") || vImgName_str.equals("NONE");
        //String eStatus = generalFunc.getJsonValueStr("eStatus", obj_userProfile);
        String ENABLE_EDIT_DRIVER_PROFILE = generalFunc.retrieveValue("ENABLE_EDIT_DRIVER_PROFILE");
        return ENABLE_EDIT_DRIVER_PROFILE.equalsIgnoreCase("No") && isImageBlank /*&& !eStatus.equalsIgnoreCase("inactive")*/;
    }
}