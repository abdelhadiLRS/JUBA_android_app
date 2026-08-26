package com.multixpro.provider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import com.activity.ParentActivity;
import com.utils.Logger;
import com.utils.Utils;
import com.view.MTextView;

public class SubscribedPlanConfirmationActivity extends ParentActivity {

    MTextView tv_tap_anywhere;
    MTextView thanksTxt;
    MTextView subscribedTxt;


    MTextView titleTxt;
    ImageView backImgView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_purchased);


        initView();
        setLables();

        findViewById(R.id.contentArea).setOnTouchListener((v, event) -> {
            backImgView.performClick();
            return true;
        });

        new Handler().postDelayed(() -> backImgView.performClick(), 10000);

    }



    private void setLables() {
        tv_tap_anywhere.setText(generalFunc.retrieveLangLBl("", "LBL_TAP_TO_GOBACK_TXT"));
        thanksTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUBSCRIBED_THANK_YOU_TXT"));
        subscribedTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUBSCRIBED_DESCRIPTION_TXT"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUBSCRIPTION_COMPLETED_TITLE_TXT"));
    }

    private void initView() {
        tv_tap_anywhere = findViewById(R.id.tv_tap_anywhere);
        thanksTxt = findViewById(R.id.thanksTxt);
        subscribedTxt = findViewById(R.id.subscribedTxt);
        titleTxt = findViewById(R.id.titleTxt);
        backImgView = findViewById(R.id.backImgView);
        backImgView.setVisibility(View.GONE);
        addToClickHandler(backImgView);
    }


    public void onClick(View view) {
        Utils.hideKeyboard(SubscribedPlanConfirmationActivity.this);
        int i = view.getId();
        if (i == R.id.backImgView) {
            Logger.d("DEBUG", "TRANSACTION_COMPLETED::ON BACK PRESS");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

    }


    @Override
    public void onBackPressed() {
        return;
    }
}
