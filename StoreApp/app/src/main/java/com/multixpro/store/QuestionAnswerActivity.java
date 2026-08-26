package com.multixpro.store;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.MyApp;
import com.utils.Utils;
import com.view.MTextView;

public class QuestionAnswerActivity extends ParentActivity {

    MTextView titleTxt;
    MTextView vQuestion, vAnswer, textstillneedhelp;
    ImageView backImgView;
    // List<String> listDataHeader;
    // HashMap<String, List<String>> listDataChild;

    // ExpandableListView expandableList;

    // QuestionAnswerEAdapter adapter;

    // private int lastExpandedPosition = -1;

    String answer = "";
    MTextView contact_us_btn;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);


        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        contact_us_btn = (MTextView) findViewById(R.id.contact_us_btn);
        textstillneedhelp = (MTextView) findViewById(R.id.textstillneedhelp);

        vQuestion = (MTextView) findViewById(R.id.vQuestion);
        clearCookies(getApplication());
        webView = findViewById(R.id.webView);

        vAnswer = (MTextView) findViewById(R.id.vAnswer);
      /*  expandableList = (ExpandableListView) findViewById(R.id.list);

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        listDataHeader = new ArrayList<String>();*/
        /*answer = getIntent().getStringExtra("ANSWER_MAP");
        String[] answerArrray = answer.split(",");
        vQuestion.setText(answerArrray[0]);
        vAnswer.setVisibility(View.GONE);
        webView.loadDataWithBaseURL(null, generalFunc.wrapHtml(webView.getContext(), answerArrray[1]),
                "text/html", "UTF-8", null);*/

        if (getIntent().getStringExtra("QUESTION") != null) {
            vQuestion.setText(Html.fromHtml(getIntent().getStringExtra("QUESTION") + ""));
            vAnswer.setVisibility(View.GONE);
            MyApp.executeWV(webView, generalFunc, getIntent().getStringExtra("ANSWER"), false);
        }


      /*  adapter = new QuestionAnswerEAdapter(getActContext(), listDataHeader, listDataChild);

        expandableList.setAdapter(adapter);


        expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });*/

        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
        addToClickHandler(backImgView);
        addToClickHandler(contact_us_btn);
        setData();
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("Api", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("Api", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public void setData() {
        titleTxt.setText(generalFunc.retrieveLangLBl("FAQ", "LBL_FAQ_TXT"));
        contact_us_btn.setText("" + generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_HEADER_TXT"));
        textstillneedhelp.setText("" + generalFunc.retrieveLangLBl("", "LBL_STILL_NEED_HELP"));

       /* JSONArray obj_ques = generalFunc.getJsonArray("Questions", getIntent().getStringExtra("QUESTION_LIST"));
        for (int i = 0; i < obj_ques.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(obj_ques, i);


          //  listDataHeader.add(generalFunc.getJsonValueStr("vTitle", obj_temp));

            List<String> answer = new ArrayList<String>();
            answer.add(generalFunc.getJsonValueStr("tAnswer", obj_temp));

          //  listDataChild.put(listDataHeader.get(i), answer);
        }

       // adapter.notifyDataSetChanged();*/
    }

    public Context getActContext() {
        return QuestionAnswerActivity.this;
    }


    public void onClick(View view) {
        Utils.hideKeyboard(QuestionAnswerActivity.this);
        switch (view.getId()) {
            case R.id.backImgView:
                QuestionAnswerActivity.super.onBackPressed();
                break;
            case R.id.contact_us_btn:
                new ActUtils(getActContext()).startAct(ContactUsActivity.class);
                break;
        }
    }

}
