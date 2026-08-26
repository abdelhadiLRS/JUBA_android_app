package com.multixpro.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DriverRewardActivity extends ParentActivity {

    private JSONObject rewardObj;
    private LinearLayout llChartContainerView;
    MTextView progressTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reward);

        initViews();

        charView();

        dataListView();

        howItWork();

        rewardCompleted();

    }

    private void rewardCompleted() {
        if (generalFunc.getJsonValueStr("all_reward_completed", rewardObj).equalsIgnoreCase("Yes")) {
            LinearLayout topView = findViewById(R.id.topView);
            topView.setVisibility(View.INVISIBLE);
            if (llChartContainerView.getChildCount() > 0) {
                llChartContainerView.removeAllViewsInLayout();
            }
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            MTextView tv = new MTextView(this);
            tv.setLayoutParams(lparams);
            tv.setTextSize(25f);
            tv.setText(generalFunc.getJsonValueStr("reward_completed_text", rewardObj));
            llChartContainerView.addView(tv);
        }
    }

    private void initViews() {
        rewardObj = generalFunc.getJsonObject(generalFunc.getJsonValue("reward", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));

        progressTitle = findViewById(R.id.progressTitle);
        progressTitle.setText(generalFunc.retrieveLangLBl("Your Progress", "LBL_YOUR_PROGRESS"));
        ImageView backImgView = findViewById(R.id.backImgView);
        backImgView.setOnClickListener(v -> finish());
        MTextView titleTxt = findViewById(R.id.titleTxt);
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_REWARD_PROGRAM"));

        MTextView txtUnLocK = findViewById(R.id.txtUnLocK);
        MTextView txtUnLocKBy = findViewById(R.id.txtUnLocKBy);

        txtUnLocK.setTextSize(getActContext().getResources().getDimension(R.dimen._10sdp));
        txtUnLocKBy.setTextSize(getActContext().getResources().getDimension(R.dimen._8sdp));

        String[] separated = generalFunc.getJsonValueStr("unlock_date", rewardObj).split("###");
        if (separated.length == 1) {
            txtUnLocK.setText(separated[0]);
        }
        if (separated.length == 2) {
            txtUnLocK.setText(separated[0]);
            txtUnLocKBy.setText(separated[1]);
        }

        MTextView txtRewardTitle = findViewById(R.id.txtRewardTitle);
        txtRewardTitle.setText(generalFunc.getJsonValueStr("vTitle", rewardObj));
        String img = generalFunc.getJsonValueStr("vImage", rewardObj);
        if (Utils.checkText(img)) {
            ImageView ivRewordImg = findViewById(R.id.ivRewardImg);
            new LoadImage.builder(LoadImage.bind(img), ivRewordImg).build();
        } else {
            findViewById(R.id.ivRewardImg).setVisibility(View.GONE);
        }


        ImageView ivArrowView = findViewById(R.id.ivArrowView);
        ivArrowView.setOnClickListener(v -> new ActUtils(getActContext()).startAct(DriverRewardDetailsActivity.class));
        if (generalFunc.isRTLmode()) {
            ivArrowView.setRotation(180);
        }
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
    }

    private Context getActContext() {
        return DriverRewardActivity.this;
    }

    private void charView() {
        llChartContainerView = findViewById(R.id.llChartContainerView);

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.pie_chart_view, null);
        PieChart pieChart = (PieChart) view.findViewById(R.id.pieChart);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String completetrip = generalFunc.getJsonValueStr("completed_trip", rewardObj);
        SpannableString redSpannable = new SpannableString(completetrip);
        redSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.appThemeColor_1)), 0, completetrip.length(), 0);
        builder.append(redSpannable);

        String totaltrip = "\n" + generalFunc.retrieveLangLBl("", "LBL_OF_TXT") + " "
                + generalFunc.getJsonValueStr("Total_trip", rewardObj) + " "
                + generalFunc.retrieveLangLBl("", "LBL_TRIP");
        SpannableString whiteSpannable = new SpannableString(totaltrip);
        whiteSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, totaltrip.length(), 0);
        builder.append(whiteSpannable);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterText(builder);
        pieChart.setUsePercentValues(false);
        pieChart.setRotationEnabled(true);
        pieChart.setTouchEnabled(false);
        pieChart.setEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(85);
        pieChart.setCenterTextSize(20f);

        if (llChartContainerView.getChildCount() > 0) {
            llChartContainerView.removeAllViewsInLayout();
        }

        llChartContainerView.addView(view);

        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(Float.parseFloat(generalFunc.getJsonValueStr("completed_trip_percentage", rewardObj)), ""));
        values.add(new PieEntry(Float.parseFloat(generalFunc.getJsonValueStr("uncompleted_trip_percentage", rewardObj)), ""));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.appThemeColor_1));
        colors.add(getResources().getColor(R.color.cardView23ProBG));

        PieDataSet set1;

        if (pieChart.getData() != null && pieChart.getData().getDataSetCount() > 0) {
            set1 = (PieDataSet) pieChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            pieChart.getData().notifyDataChanged();
            pieChart.notifyDataSetChanged();
        } else {
            set1 = new PieDataSet(values, "");
            set1.setDrawValues(false);
            set1.setColors(colors);
            set1.setValueLineVariableLength(false);

            PieData data = new PieData(set1);
            pieChart.setData(data);
        }
    }

    private void dataListView() {
        LinearLayout rewardDetailDisplayArea = findViewById(R.id.rewardDetailDisplayArea);
        rewardDetailDisplayArea.removeAllViewsInLayout();

        JSONArray rewardDetails = generalFunc.getJsonArray(generalFunc.getJsonValueStr("reward_details", rewardObj));
        if (rewardDetails != null) {
            for (int j = 0; j < rewardDetails.length(); j++) {

                LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") View convertView = infalInflater.inflate(R.layout.design_reward_detail_row, null);

                MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
                MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);
                AppCompatImageView completedView = (AppCompatImageView) convertView.findViewById(R.id.completedView);
                AppCompatImageView incompletedView = (AppCompatImageView) convertView.findViewById(R.id.incompletedView);


                JSONObject jobject = generalFunc.getJsonObject(rewardDetails, j);

                titleHTxt.setText(generalFunc.getJsonValueStr("vTitle", jobject));
                titleVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("vValue", jobject)));
                if (generalFunc.getJsonValueStr("is_completed", jobject).equalsIgnoreCase("Yes")) {
                    completedView.setVisibility(View.VISIBLE);
                    incompletedView.setVisibility(View.GONE);
                } else {
                    completedView.setVisibility(View.GONE);
                    incompletedView.setVisibility(View.VISIBLE);
                }

                rewardDetailDisplayArea.addView(convertView);
            }
        }
    }

    private void howItWork() {
        MTextView txtHowItWorks = findViewById(R.id.txtHowItWorks);
        txtHowItWorks.setText(generalFunc.retrieveLangLBl("", "LBL_HOW_IT_WORKS_TXT"));
        txtHowItWorks.setOnClickListener(v -> new ActUtils(getActContext()).startAct(DriverRewardDetailsActivity.class));
    }
}