package com.multixpro.provider;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.activity.ParentActivity;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import java.util.ArrayList;

public class AllPermissionsHandleActivity extends ParentActivity {

    MButton btn_type2;
    int submitBtnId;
    ArrayList<String> requestPermissions = new ArrayList<>();
    GenerateAlertBox currentAlertBox;
    boolean isopenAllowAllDialog = false;

    ImageView permisssionImg;
    MTextView titleTxt, noteTxt;
    private boolean isOneTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_permissions_handle);

        permisssionImg = findViewById(R.id.permisssionImg);
        titleTxt = findViewById(R.id.titleTxt);
        noteTxt = findViewById(R.id.noteTxt);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ALLOW"));
        submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);

        RelativeLayout.LayoutParams lyParams_permisssionImg = (RelativeLayout.LayoutParams) permisssionImg.getLayoutParams();
        lyParams_permisssionImg.height = (int) ((Utils.getScreenPixelWidth(getActContext()) - getResources().getDimensionPixelSize(R.dimen._50sdp)) / 1.3888);
        permisssionImg.setLayoutParams(lyParams_permisssionImg);


        final String callingMethod = generalFunc.getJsonValueStr("RIDE_DRIVER_CALLING_METHOD", obj_userProfile);

        boolean isCallingMethod = callingMethod.equalsIgnoreCase("Voip")
                || callingMethod.equalsIgnoreCase("VideoCall")
                || callingMethod.equalsIgnoreCase("Voip-VideoCall")
                || callingMethod.equalsIgnoreCase("Normal");

        if (isCallingMethod) {
            permisssionImg.setImageDrawable(ContextCompat.getDrawable(getActContext(), R.drawable.ic_permission_all));
            titleTxt.setText(generalFunc.retrieveLangLBl("Location & Call Permission required", "LBL_LOC_PHONE_PERMISSION"));
            String notLBL = generalFunc.retrieveLangLBl("", "LBL_LOC_CALL_PERMISSION_NOTE");
            notLBL = notLBL.replace("###", "\n");
            noteTxt.setText(notLBL);

        } else {
            titleTxt.setText(generalFunc.retrieveLangLBl("Location Permission required", "LBL_LOC_PERMISSION"));
            String notLBL = generalFunc.retrieveLangLBl("", "LBL_LOC_PERMISSION_NOTE");
            notLBL = notLBL.replace("###", "\n");
            noteTxt.setText(notLBL);
            permisssionImg.setImageDrawable(ContextCompat.getDrawable(getActContext(), R.drawable.ic_permission_location));

        }

        btn_type2.setOnClickListener(view -> {

            if (isCallingMethod) {
                requestPermissions.add(Manifest.permission.RECORD_AUDIO);
                requestPermissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPermissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
            }
            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }

            if (isCallingMethod) {
                if (generalFunc.isCallPermissionGranted(false) || generalFunc.isAllPermissionGranted(false)) {
                    isopenAllowAllDialog = true;
                }
            } else {
                if (generalFunc.isAllPermissionGranted(false)) {
                    isopenAllowAllDialog = true;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (generalFunc.isAllPermissionGranted(false, requestPermissions)) {
                    isOneTime = false;
                }
                if (!isOneTime) {
                    requestPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (generalFunc.isAllPermissionGranted(true, requestPermissions)) {
                        generalFunc.restartApp();
                    } else {
                        if (!generalFunc.isAllPermissionGranted(true, requestPermissions)) {
                            showNoPermission();
                        }
                    }
                    return;
                }
            }

            generalFunc.isAllPermissionGranted(!isopenAllowAllDialog, requestPermissions);
            if (isopenAllowAllDialog) {
                showNoPermission();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestPermissions.size() > 0) {
            if (generalFunc.isAllPermissionGranted(false, requestPermissions)) {
                generalFunc.restartApp();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!generalFunc.isCallPermissionGranted(false)) {
            isopenAllowAllDialog = true;
        }
        if (!generalFunc.isAllPermissionGranted(false)) {
            isopenAllowAllDialog = true;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isOneTime) {
                isOneTime = false;
                requestPermissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                if (generalFunc.isAllPermissionGranted(true, requestPermissions)) {
                    generalFunc.restartApp();
                }
            }
        } else {
            if (generalFunc.isAllPermissionGranted(false, requestPermissions)) {
                generalFunc.restartApp();
            }
        }
    }

    public Context getActContext() {
        return AllPermissionsHandleActivity.this;
    }

    public void showNoPermission() {
        currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Application requires some permission to be granted to work. Please allow it.",
                "LBL_ALLOW_PERMISSIONS_APP"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Allow All", "LBL_SETTINGS"),
                buttonId -> {
                    if (buttonId == 0) {
                        currentAlertBox.closeAlertBox();
                    } else {
                        generalFunc.openSettings();
                    }
                });
    }
}