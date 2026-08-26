package com.multixpro.provider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.livechatinc.inappchat.ChatWindowActivity;
import com.utils.Utils;
import com.view.MTextView;

import java.util.HashMap;

public class SupportActivity extends ParentActivity {


    MTextView titleTxt;
    ImageView backImgView;


    LinearLayout aboutusarea, privacyarea, contactarea, helparea, termsCondArea, chatarea;

    MTextView helpHTxt, contactHTxt, privacyHTxt, aboutusHTxt, termsHTxt, livechatHTxt;

    View seperationLine, seperationLine_contact, seperationLine_help, chatlineView;

    boolean islogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        initView();
        setLabel();

        islogin = getIntent().getBooleanExtra("islogin", false);
        if (islogin) {
            aboutusarea.setVisibility(View.GONE);
            contactarea.setVisibility(View.GONE);
            helparea.setVisibility(View.GONE);
            seperationLine_help.setVisibility(View.GONE);
            seperationLine_contact.setVisibility(View.GONE);
            seperationLine.setVisibility(View.GONE);
            chatarea.setVisibility(View.GONE);
            chatlineView.setVisibility(View.GONE);

        }
    }


    private void initView() {

        titleTxt = findViewById(R.id.titleTxt);
        backImgView = findViewById(R.id.backImgView);
        addToClickHandler(backImgView);

        helpHTxt = findViewById(R.id.helpHTxt);
        contactHTxt = findViewById(R.id.contactHTxt);
        privacyHTxt = findViewById(R.id.privacyHTxt);
        aboutusHTxt = findViewById(R.id.aboutusHTxt);
        termsHTxt = findViewById(R.id.termsHTxt);
        livechatHTxt = findViewById(R.id.livechatHTxt);

        aboutusarea = findViewById(R.id.aboutusarea);
        privacyarea = findViewById(R.id.privacyarea);
        contactarea = findViewById(R.id.contactarea);
        helparea = findViewById(R.id.helparea);
        termsCondArea = findViewById(R.id.termsCondArea);
        chatarea = findViewById(R.id.chatarea);

        seperationLine = findViewById(R.id.seperationLine);
        seperationLine_contact = findViewById(R.id.seperationLine_contact);
        seperationLine_help = findViewById(R.id.seperationLine_help);
        chatlineView = findViewById(R.id.chatlineView);


        addToClickHandler(aboutusarea);
        addToClickHandler(privacyarea);
        addToClickHandler(contactarea);
        addToClickHandler(helparea);
        addToClickHandler(termsCondArea);
        addToClickHandler(chatarea);


        if (generalFunc.getJsonValueStr("ENABLE_LIVE_CHAT", obj_userProfile).equalsIgnoreCase("Yes")) {
            chatarea.setVisibility(View.VISIBLE);
            chatlineView.setVisibility(View.VISIBLE);

        }


    }

    public Context getActContext() {
        return SupportActivity.this;
    }


    private void setLabel() {

        helpHTxt.setText(generalFunc.retrieveLangLBl("FAQ", "LBL_FAQ_TXT"));
        contactHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_TXT"));
        privacyHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PRIVACY_POLICY_TEXT"));
        aboutusHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ABOUT_US_TXT"));
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SUPPORT_HEADER_TXT"));
        termsHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TERMS_AND_CONDITION"));
        livechatHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_LIVE_CHAT"));

    }


    public void onClick(View view) {
        Utils.hideKeyboard(SupportActivity.this);
        Bundle bn = new Bundle();
        switch (view.getId()) {
            case R.id.backImgView:
                SupportActivity.super.onBackPressed();
                break;
            case R.id.aboutusarea:
                bn.putString("staticpage", "1");
                new ActUtils(getActContext()).startActWithData(StaticPageActivity.class, bn);
                break;
            case R.id.privacyarea:
                bn.putString("staticpage", "33");
                new ActUtils(getActContext()).startActWithData(StaticPageActivity.class, bn);
                break;
            case R.id.contactarea:
                new ActUtils(getActContext()).startAct(ContactUsActivity.class);
                break;
            case R.id.helparea:
                new ActUtils(getActContext()).startAct(HelpActivity23Pro.class);
                break;
            case R.id.termsCondArea:
                bn.putString("staticpage", "4");
                new ActUtils(getActContext()).startActWithData(StaticPageActivity.class, bn);
                break;
            case R.id.chatarea:
                startChatActivity();
                break;

        }
    }


    private void startChatActivity() {


        String driverName = generalFunc.getJsonValueStr("vName", obj_userProfile) + " " + generalFunc.getJsonValueStr("vLastName", obj_userProfile);
        String driverEmail = generalFunc.getJsonValueStr("vEmail", obj_userProfile);

        Utils.LIVE_CHAT_LICENCE_NUMBER = generalFunc.getJsonValueStr("LIVE_CHAT_LICENCE_NUMBER", obj_userProfile);
        HashMap<String, String> map = new HashMap<>();
        map.put("FNAME", generalFunc.getJsonValueStr("vName", obj_userProfile));
        map.put("LNAME", generalFunc.getJsonValueStr("vLastName", obj_userProfile));
        map.put("EMAIL", generalFunc.getJsonValueStr("vEmail", obj_userProfile));
        map.put("USERTYPE", Utils.userType);

        Intent intent = new Intent(this, ChatWindowActivity.class);
        intent.putExtra(ChatWindowActivity.KEY_LICENCE_NUMBER, Utils.LIVE_CHAT_LICENCE_NUMBER);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_NAME, driverName);
        intent.putExtra(ChatWindowActivity.KEY_VISITOR_EMAIL, driverEmail);
        intent.putExtra(ChatWindowActivity.KEY_GROUP_ID, Utils.userType + "_" + generalFunc.getMemberId());

        intent.putExtra("myParam", map);
        startActivity(intent);
    }
}
