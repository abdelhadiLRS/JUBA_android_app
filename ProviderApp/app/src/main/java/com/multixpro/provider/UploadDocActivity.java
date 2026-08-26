package com.multixpro.provider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.activity.ParentActivity;
import com.general.DatePicker;
import com.general.files.ActUtils;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.UploadProfileImage;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UploadDocActivity extends ParentActivity {

    private MTextView titleTxt, helpInfoTxtView, expBox, expBoxLBL, expBoxTXT, editTxtView, viewTxtView;
    private ImageView backImgView, dummyInfoCardImgView, imgSelectView;
    private MButton btn_type2;

    private FrameLayout expDateSelectArea;
    private String selectedDocumentPath = "", vImage = "";
    private boolean isUploadImageNew = true, isBtnClick = false;

    private RelativeLayout selectYearLayout;
    private LinearLayout editArea;
    private View editView;
    private String SELECTED_DATE = "";

    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_doc);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (!getIntent().getStringExtra("vimage").equalsIgnoreCase("")) {
            vImage = getIntent().getStringExtra("vimage");
        }

        editView = findViewById(R.id.editView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        dummyInfoCardImgView = (ImageView) findViewById(R.id.dummyInfoCardImgView);
        helpInfoTxtView = (MTextView) findViewById(R.id.helpInfoTxtView);
        expBox = (MTextView) findViewById(R.id.expBox);
        expBoxLBL = (MTextView) findViewById(R.id.expBoxLBL);
        expBoxTXT = (MTextView) findViewById(R.id.expBoxTxt);
        imgSelectView = (ImageView) findViewById(R.id.imgeselectview);
        expDateSelectArea = (FrameLayout) findViewById(R.id.expDateSelectArea);
        selectYearLayout = (RelativeLayout) findViewById(R.id.selectyear_layout);
        editArea = (LinearLayout) findViewById(R.id.editArea);
        editTxtView = (MTextView) findViewById(R.id.editTxtView);
        viewTxtView = (MTextView) findViewById(R.id.viewTxtView);
        addToClickHandler(editView);
        addToClickHandler(viewTxtView);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        addToClickHandler(backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }

        btn_type2.setId(Utils.generateViewId());
        addToClickHandler(btn_type2);
        addToClickHandler(helpInfoTxtView);
        addToClickHandler(dummyInfoCardImgView);

        new CreateRoundedView(Color.parseColor("#ffffff"), 8, 2, Color.parseColor("#9c9c9c"), selectYearLayout);

        setLabels();
        SimpleDateFormat date_format = new SimpleDateFormat(CommonUtilities.DayFormatEN, Locale.US);
        SELECTED_DATE = date_format.format(Calendar.getInstance(Locale.getDefault()).getTime());


        if (getIntent().getStringExtra("allow_date_change").equalsIgnoreCase("No")) {
            btn_type2.setEnabled(false);
            ((MTextView) findViewById(R.id.noteTxt)).setText(getIntent().getStringExtra("doc_update_disable"));
        }
    }

    private void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_UPLOAD_DOC"));
        editTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_EDIT"));
        viewTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_VIEW"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_SUBMIT_TXT"));
        helpInfoTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_DOC"));
        expBox.setText(generalFunc.retrieveLangLBl("", "LBL_SELECT_TXT"));
        expBoxLBL.setText(generalFunc.retrieveLangLBl("", "LBL_EXPIRY_DATE"));

        if (getIntent().getStringExtra("ex_status").equals("yes")) {
            expBoxTXT.setText(generalFunc.getDateFormatedType(getIntent().getStringExtra("ex_date"), CommonUtilities.DayFormatEN, CommonUtilities.WithoutDayFormat));
            expDateSelectArea.setVisibility(View.VISIBLE);
        } else {
            expDateSelectArea.setVisibility(View.GONE);
        }

        String doc_file = getIntent().getStringExtra("doc_file");
        if (!doc_file.equals("")) {
            selectedDocumentPath = doc_file;
            imgSelectView.setVisibility(View.VISIBLE);
            helpInfoTxtView.setVisibility(View.GONE);
            editArea.setVisibility(View.VISIBLE);

            if (!vImage.equalsIgnoreCase("")) {
                viewTxtView.setVisibility(View.VISIBLE);
                editView.setVisibility(View.VISIBLE);

            } else {
                viewTxtView.setVisibility(View.GONE);
                editView.setVisibility(View.GONE);
            }
            dummyInfoCardImgView.setAlpha(0.2f);
            dummyInfoCardImgView.setImageDrawable(ContextCompat.getDrawable(getActContext(), R.drawable.ic_card_documents));
            isUploadImageNew = false;
        }

        selectYearLayout.setOnTouchListener(new setOnTouchList());
        addToClickHandler(selectYearLayout);
    }

    private static class setOnTouchList implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP && !view.hasFocus()) {
                view.performClick();
            }
            return true;
        }
    }

    private Context getActContext() {
        return UploadDocActivity.this;
    }

    private void checkData() {

        if (selectedDocumentPath.equals("")) {
            generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Please attach your document.", "LBL_SELECT_DOC_ERROR"));
            return;
        }
        if (expDateSelectArea.getVisibility() == View.VISIBLE && !Utils.checkText(expBoxTXT.getText().toString())) {
            generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("Expiry date is required.", "LBL_EXP_DATE_REQUIRED"));
            return;
        }

        if (isBtnClick) {
            return;
        }
        isBtnClick = true;
        new Handler(Looper.myLooper()).postDelayed(() -> isBtnClick = false, 1000);

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(Utils.generateImageParams("type", "uploaddrivedocument"));
        paramsList.add(Utils.generateImageParams("iMemberId", generalFunc.getMemberId()));
        paramsList.add(Utils.generateImageParams("MemberType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("doc_usertype", getIntent().getStringExtra("PAGE_TYPE")));
        paramsList.add(Utils.generateImageParams("doc_masterid", getIntent().getStringExtra("doc_masterid")));
        paramsList.add(Utils.generateImageParams("doc_name", getIntent().getStringExtra("doc_name")));
        paramsList.add(Utils.generateImageParams("doc_id", getIntent().getStringExtra("doc_id")));
        paramsList.add(Utils.generateImageParams("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY)));
        paramsList.add(Utils.generateImageParams("GeneralUserType", Utils.app_type));
        paramsList.add(Utils.generateImageParams("GeneralMemberId", generalFunc.getMemberId()));
        paramsList.add(Utils.generateImageParams("ex_date", getIntent().getStringExtra("ex_status").equals("yes") ? generalFunc.getDateFormatedType(Utils.getText(expBoxTXT), CommonUtilities.WithoutDayFormat, CommonUtilities.DayFormatEN, Locale.US) : ""));
        if (!getIntent().getStringExtra("iDriverVehicleId").equals("")) {
            paramsList.add(Utils.generateImageParams("iDriverVehicleId", getIntent().getStringExtra("iDriverVehicleId")));
        }

        UploadProfileImage uploadProfileImage;
        if (!getIntent().getStringExtra("doc_file").equals("")) {

            if (isUploadImageNew) {
                uploadProfileImage = new UploadProfileImage(UploadDocActivity.this, selectedDocumentPath, "TempFile." + Utils.getFileExt(selectedDocumentPath), paramsList, "FILE");
                uploadProfileImage.execute(true, generalFunc.retrieveLangLBl("", "LBL_DOCUMET_UPLOADING"));
            } else {
                paramsList.add(Utils.generateImageParams("doc_file", selectedDocumentPath));
                uploadProfileImage = new UploadProfileImage(UploadDocActivity.this, "", "TempFile." + Utils.getFileExt(selectedDocumentPath), paramsList, "FILE");
                uploadProfileImage.execute(false, generalFunc.retrieveLangLBl("", "LBL_DOCUMET_UPLOADING"));
            }
        } else {
            uploadProfileImage = new UploadProfileImage(UploadDocActivity.this, selectedDocumentPath, "TempFile." + Utils.getFileExt(selectedDocumentPath), paramsList, "FILE");
            uploadProfileImage.execute(true, generalFunc.retrieveLangLBl("", "LBL_DOCUMET_UPLOADING"));
        }

    }

    public void handleImgUploadResponse(String responseString) {

        if (responseString != null && !responseString.equals("")) {

            if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {

                String msgTxt;
                if (!generalFunc.getJsonValue("doc_under_review", responseString).equalsIgnoreCase("")) {
                    msgTxt = generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("doc_under_review", responseString));
                } else {
                    msgTxt = generalFunc.retrieveLangLBl("Your document is uploaded successfully", "LBL_UPLOAD_DOC_SUCCESS");
                }

                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    generateAlert.closeAlertBox();
                    setResult(RESULT_OK);
                    backImgView.performClick();
                });
                generateAlert.setContentMessage("", msgTxt);
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                generateAlert.showAlertBox();
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        } else {
            generalFunc.showError();
        }
    }

    @Override
    public void onFileSelected(Uri mFileUri, String mFilePath, FileSelector.FileType mFileType) {

        this.selectedDocumentPath = mFilePath;
        imgSelectView.setVisibility(View.VISIBLE);
        dummyInfoCardImgView.setAlpha(0.2f);
        dummyInfoCardImgView.setImageDrawable(ContextCompat.getDrawable(getActContext(), R.drawable.ic_card_documents));
        isUploadImageNew = true;

        helpInfoTxtView.setVisibility(View.GONE);
        editArea.setVisibility(View.VISIBLE);

        if (!vImage.equalsIgnoreCase("")) {
            viewTxtView.setVisibility(View.VISIBLE);
            editView.setVisibility(View.VISIBLE);

        } else {
            viewTxtView.setVisibility(View.GONE);
            editView.setVisibility(View.GONE);
        }
    }

    private void openDocChoose() {
        getFileSelector().openFileSelection(FileSelector.FileType.Document);
    }

    private void openCalender() {
        DatePicker.show(getActContext(), generalFunc, Calendar.getInstance(), null, SELECTED_DATE, null, (year, monthOfYear, dayOfMonth) -> {
            SimpleDateFormat date_format1 = new SimpleDateFormat(CommonUtilities.DayFormatEN, Locale.US);
            try {
                Date cal = date_format1.parse(year + "-" + monthOfYear + "-" + dayOfMonth);
                if (cal != null) {
                    SELECTED_DATE = date_format1.format(cal.getTime());
                    expBoxTXT.setText(Utils.convertDateToFormat(CommonUtilities.WithoutDayFormat, cal));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }


    public void onClick(View view) {
        int i = view.getId();
        Utils.hideKeyboard(UploadDocActivity.this);
        if (i == R.id.backImgView) {
            UploadDocActivity.super.onBackPressed();
        } else if (i == btn_type2.getId()) {
            checkData();
        } else if (i == helpInfoTxtView.getId() || i == dummyInfoCardImgView.getId()) {
            openDocChoose();
        } else if (i == R.id.selectyear_layout) {
            openCalender();
        } else if (i == R.id.editTxtView) {
            openDocChoose();
        } else if (i == R.id.viewTxtView) {
            new ActUtils(getActContext()).openURL(vImage);
        }
    }

}