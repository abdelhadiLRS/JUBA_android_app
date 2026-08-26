package com.multixpro.store;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;

import com.activity.ParentActivity;
import com.fragments.EditProfileFragment;
import com.general.files.ActUtils;
import com.general.files.FilePath;
import com.general.files.GeneralFunctions;
import com.general.files.UploadProfileImage;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import java.util.ArrayList;

import static com.utils.Utils.generateImageParams;

public class MyProfileActivity extends ParentActivity {

    public boolean isMobile = false, isEmail = false;
    private MTextView titleTxt;
    private SelectableRoundedImageView userProfileImgView;
    private EditProfileFragment editProfileFrag;
    private String SITE_TYPE = "", SITE_TYPE_DEMO_MSG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        isMobile = getIntent().getBooleanExtra("isMobile", false);
        isEmail = getIntent().getBooleanExtra("isEmail", false);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        ImageView backImgView = (ImageView) findViewById(R.id.backImgView);
        userProfileImgView = (SelectableRoundedImageView) findViewById(R.id.userProfileImgView);
        SelectableRoundedImageView editIconImgView = (SelectableRoundedImageView) findViewById(R.id.editIconImgView);
        RelativeLayout userImgArea = (RelativeLayout) findViewById(R.id.userImgArea);

        addToClickHandler(backImgView);
        addToClickHandler(userImgArea);

        new CreateRoundedView(getResources().getColor(R.color.editBox_primary), Utils.dipToPixels(getActContext(), 15), 0,
                Color.parseColor("#00000000"), editIconImgView);

        editIconImgView.setColorFilter(getResources().getColor(R.color.appThemeColor_TXT_1));

        userProfileImgView.setImageResource(R.mipmap.ic_no_pic_user);
        String url = CommonUtilities.COMPANY_PHOTO_PATH + generalFunc.getMemberId() + "/" + generalFunc.getJsonValue("vImage", obj_userProfile);
        generalFunc.checkProfileImage(userProfileImgView, url, R.mipmap.ic_no_pic_user, R.mipmap.ic_no_pic_user);


        SITE_TYPE = generalFunc.getJsonValueStr("SITE_TYPE", obj_userProfile);
        SITE_TYPE_DEMO_MSG = generalFunc.getJsonValueStr("SITE_TYPE_DEMO_MSG", obj_userProfile);


        openEditProfileFragment();

        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
    }

    public void changePageTitle(String title) {
        titleTxt.setText(title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            // perform your desired action here

            // return 'true' to prevent further propagation of the key event
            return true;
        }
        // let the system handle all other key events
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
        obj_userProfile = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        Bundle bn = new Bundle();
        bn.putString("UserProfileJson", userProfileJson);

        generalFunc.storeData(Utils.WALLET_ENABLE, generalFunc.getJsonValue("WALLET_ENABLE", userProfileJson));
        generalFunc.storeData(Utils.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValue("REFERRAL_SCHEME_ENABLE", userProfileJson));

        new ActUtils(getActContext()).setOkResult(bn);
        generalFunc.showMessage(getCurrView(), generalFunc.retrieveLangLBl("", "LBL_INFO_UPDATED_TXT"));
    }

    public View getCurrView() {
        return generalFunc.getCurrentView(MyProfileActivity.this);
    }

    private boolean isValidImageResolution(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;

        if (width >= Utils.ImageUpload_MINIMUM_WIDTH && height >= Utils.ImageUpload_MINIMUM_HEIGHT) {
            return true;
        }
        return false;
    }

    private void imageUpload(Uri fileUri) {
        if (SITE_TYPE.equalsIgnoreCase("Demo") && generalFunc.getJsonValue("vEmail", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)).equalsIgnoreCase("Driver@gmail.com")) {
            generalFunc.showGeneralMessage("", SITE_TYPE_DEMO_MSG);
            return;
        }

        ArrayList<String[]> paramsList = new ArrayList<>();

        String iMemberId = generalFunc.getMemberId();

        paramsList.add(generateImageParams("iMemberId", iMemberId));
        paramsList.add(generateImageParams("MemberType", Utils.app_type));
        paramsList.add(generateImageParams("tSessionId", iMemberId.equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(generateImageParams("GeneralMemberId", iMemberId));
        paramsList.add(generateImageParams("type", "uploadImage"));

        String selPath = FilePath.getPath(getActContext(), fileUri);

        if (isValidImageResolution(selPath)) {
            new UploadProfileImage(MyProfileActivity.this, selPath, Utils.TempProfileImageName, paramsList, "").execute();

        } else {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please select image which has minimum is 256 * 256 resolution.", "LBL_MIN_RES_IMAGE"));
        }
    }

    public void handleImgUploadResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {
            if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                changeUserProfileJson(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
                String url = CommonUtilities.COMPANY_PHOTO_PATH + generalFunc.getMemberId() + "/" + generalFunc.getJsonValue("vImage", obj_userProfile);
                generalFunc.checkProfileImage(userProfileImgView, url, R.mipmap.ic_no_pic_user, R.mipmap.ic_no_pic_user);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        } else {
            generalFunc.showError();
        }
    }


    @Override
    public void onClick(View view) {
        Utils.hideKeyboard(MyProfileActivity.this);

        switch (view.getId()) {
            case R.id.backImgView:
                MyProfileActivity.super.onBackPressed();
                break;

            case R.id.userImgArea:
                break;

        }
    }

}