package com.multixpro.user;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.activity.ParentActivity;
import com.general.files.GeneralFunctions;
import com.service.handler.ApiHandler;
import com.utils.CommonUtilities;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MTextView;
import com.view.simpleratingbar.SimpleRatingBar;

import java.util.HashMap;

public class ProviderInfoActivity extends ParentActivity {

    private MTextView descTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_info);
        ImageView backImgView = findViewById(R.id.backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        descTxt = findViewById(R.id.descTxt);
        MTextView descTitleTxt = findViewById(R.id.descTitleTxt);
        MTextView nameTxt = findViewById(R.id.bottomViewnameTxt);
        SimpleRatingBar bottomViewratingBar = findViewById(R.id.bottomViewratingBar);

        // MTextView titleTxt = findViewById(R.id.titleTxt);
        // titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SERVICE_DESCRIPTION"));

        bottomViewratingBar.setRating(GeneralFunctions.parseFloatValue(0, getIntent().getStringExtra("average_rating")));
        String image_url = CommonUtilities.PROVIDER_PHOTO_PATH + getIntent().getStringExtra("iDriverId") + "/" + getIntent().getStringExtra("driver_img");
        nameTxt.setText(getIntent().getStringExtra("name"));

        //descTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ABOUT") + " " + getIntent().getStringExtra("fname"));
        descTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ABOUT_EXPERT"));

        new LoadImage.builder(LoadImage.bind(image_url), findViewById(R.id.bottomViewdriverImgView)).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();


        getProviderInfo();
    }

    private Context getActContext() {
        return ProviderInfoActivity.this;
    }

    private void getProviderInfo() {
        final HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getProviderServiceDescription");
        parameters.put("iDriverId", getIntent().getStringExtra("iDriverId"));

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc, responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    descTxt.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl(
                                    "", generalFunc.getJsonValue(Utils.message_str, responseString)),
                            "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"),
                            buttonId -> onBackPressed());

                }
            }
        });
    }

    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());
        if (view.getId() == R.id.backImgView) {
            onBackPressed();
        }
    }
}