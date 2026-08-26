package com.multixpro.store;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.activity.ParentActivity;
import com.general.files.Closure;
import com.general.files.CustomDialog;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SetGeneralData;
import com.service.handler.ApiHandler;
import com.utils.Logger;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ThermalPrintSettingActivity extends ParentActivity {

    MTextView titleTxt, descTxt, txtStatus, txtStatusVal, txtAllowPrint, txtAutoPrint;
    public MTextView connectTxt, disConnectTxt;
    ImageView backImgView, allowAutoPrintInfo, autoPrintInfo;
    CheckBox cbAllowPrint, cbAllowAutoPrint;
    private MButton printSettingsBtn;
    boolean isConnected = false;

    private CustomDialog customDialog;
    private static final int REQUEST_COARSE_LOCATION = 200, REQUEST_CONNECT_SCAN_BT = 213;
    private GenerateAlertBox currentAlertBox;
    BluetoothAdapter bAdapter;
    private static String TAG = "---DeviceList";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermal_print_setting);

        init();
        setLabels();
        showConnectedStatusArea();

    }

    private void showConnectedStatusArea() {
        if (generalFunc.retrieveValue(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes")) {
            findViewById(R.id.statusArea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.statusArea).setVisibility(View.GONE);
        }
    }

    private void init() {

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        disConnectTxt = (MTextView) findViewById(R.id.disConnectTxt);
        connectTxt = (MTextView) findViewById(R.id.connectTxt);
        allowAutoPrintInfo = (ImageView) findViewById(R.id.allowAutoPrintInfo);
        autoPrintInfo = (ImageView) findViewById(R.id.autoPrintInfo);

        descTxt = (MTextView) findViewById(R.id.descTxt);
        txtAllowPrint = (MTextView) findViewById(R.id.txtAllowPrint);
        txtAutoPrint = (MTextView) findViewById(R.id.txtAutoPrint);
        txtStatus = (MTextView) findViewById(R.id.txtStatus);
        txtStatusVal = (MTextView) findViewById(R.id.txtStatusVal);
        cbAllowPrint = (CheckBox) findViewById(R.id.cbAllowPrint);
        cbAllowAutoPrint = (CheckBox) findViewById(R.id.cbAllowAutoPrint);

        printSettingsBtn = ((MaterialRippleLayout) findViewById(R.id.printSettingsBtn)).getChildView();
        printSettingsBtn.setId(Utils.generateViewId());


        addToClickHandler(allowAutoPrintInfo);
        addToClickHandler(autoPrintInfo);
        addToClickHandler(connectTxt);
        addToClickHandler(backImgView);
        addToClickHandler(disConnectTxt);
        addToClickHandler(printSettingsBtn);


        cbAllowPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.statusArea).setVisibility(View.VISIBLE);
                    findViewById(R.id.autoPrintArea).setVisibility(View.VISIBLE);

                    boolean nwStatus = MyApp.btsocket != null;
                    if (isConnected != nwStatus) {
                        isConnected = MyApp.btsocket != null;
                        reSetDetails(false);
                    }

                } else {
                    findViewById(R.id.statusArea).setVisibility(View.GONE);
                    findViewById(R.id.autoPrintArea).setVisibility(View.GONE);
                    cbAllowAutoPrint.setChecked(false);
                }
            }
        });
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }

    }

    public Context getActContext() {
        return ThermalPrintSettingActivity.this;
    }

    public void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_T_PRINT_TITLE_TXT"));
        descTxt.setText(generalFunc.retrieveLangLBl("", "LBL_T_PRINT_DESC_TXT"));
        txtAllowPrint.setText(generalFunc.retrieveLangLBl("", "LBL_T_PRINT_ALLOW_TXT"));
        txtAutoPrint.setText(generalFunc.retrieveLangLBl("", "LBL_T_AUTO_PRINT_TXT"));
        printSettingsBtn.setText(generalFunc.retrieveLangLBl("", "LBL_T_PRINT_UPDATE_TXT"));
        txtStatus.setText(generalFunc.retrieveLangLBl("", "LBL_T_PRINTER_STATUS_TXT"));
        isConnected = MyApp.btsocket != null;

        reSetDetails(true);

    }

    private void reSetDetails(boolean resetCheckBox) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resetCheckBox) {
                    if (generalFunc.retrieveValue(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes")) {
                        cbAllowPrint.setChecked(true);
                        findViewById(R.id.autoPrintArea).setVisibility(View.VISIBLE);
                    } else {
                        cbAllowPrint.setChecked(false);
                        findViewById(R.id.autoPrintArea).setVisibility(View.GONE);
                    }


                    if (generalFunc.retrieveValue(Utils.AUTO_PRINT_KEY).equalsIgnoreCase("Yes")) {
                        cbAllowAutoPrint.setChecked(true);
                    } else {
                        cbAllowAutoPrint.setChecked(false);
                    }
                }

                if (!isConnected) {
                    connectTxt.setVisibility(View.VISIBLE);
                    disConnectTxt.setVisibility(View.GONE);
                    connectTxt.setText(generalFunc.retrieveLangLBl("", "LBL_T_PRINT_CONNECT_TXT"));
                    txtStatusVal.setText(generalFunc.retrieveLangLBl("Dis Connected", "LBL_PRINTER_STATUS_DISCONNECTED"));
                } else {
                    disConnectTxt.setVisibility(View.VISIBLE);
                    connectTxt.setVisibility(View.GONE);
                    disConnectTxt.setText(generalFunc.retrieveLangLBl("", "LBL_T_PRINT_DISCONNECT_TXT"));
                    txtStatusVal.setText(generalFunc.retrieveLangLBl("Connected", "LBL_PRINTER_STATUS_CONNECTED"));
                }
            }
        });

    }


    public void onClick(View view) {
        Utils.hideKeyboard(getActContext());

        int id = view.getId();
        if (id == printSettingsBtn.getId()) {
            printSettingsBtn.setEnabled(false);

            if (MyApp.btsocket != null && !cbAllowPrint.isChecked() && generalFunc.retrieveValue(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes")) {
                if (currentAlertBox != null) {
                    currentAlertBox.closeAlertBox();
                    currentAlertBox = null;
                }
                currentAlertBox = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PRINTER_DISCONNECT_NOTE"/*LBL_T_PRINT_WARNING_TXT*/), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), buttonId -> {
                    if (buttonId == 0) {
                        currentAlertBox.closeAlertBox();
                        currentAlertBox = null;

                    } else if (buttonId == 1) {
                        currentAlertBox.closeAlertBox();
                        currentAlertBox = null;

                        confirmPrintSettings();
                    }
                });
            } else {
                confirmPrintSettings();
            }

            return;
        } else if (id == R.id.backImgView) {
//                onBackPressed();
            finish();
            return;
        } else if (id == R.id.allowAutoPrintInfo) {
            showMessage(true);
            return;
        } else if (id == R.id.autoPrintInfo) {
            showMessage(false);
            return;
        } else if (id == disConnectTxt.getId()) {
            disconnectPrinter();
            return;
        } else if (id == connectTxt.getId()) {
            connectBluetooth();

            return;
        }
    }


    @SuppressLint("MissingPermission")
    private void connectBluetooth() {
        if (customDialog != null && customDialog.isShowing()) {
            return;
        }

        if (generalFunc.retrieveValue(Utils.THERMAL_PRINT_ENABLE_KEY).equalsIgnoreCase("Yes") && MyApp.btsocket == null) {

            ArrayList<String> locationRequestPermissions = new ArrayList<>();
            locationRequestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            locationRequestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (generalFunc.isAllPermissionGranted(true, locationRequestPermissions, REQUEST_COARSE_LOCATION)) {
                bAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ArrayList<String> requestPermissions = new ArrayList<>();
                        requestPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
                        requestPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
                        if (generalFunc.isAllPermissionGranted(true, requestPermissions, REQUEST_CONNECT_SCAN_BT)) {
                            if (!bAdapter.isEnabled()) {
                                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), Utils.REQUEST_CONNECT_BT);
                            } else {
                                OpenPrinterList();
                            }
                        }
                    } else {
                        if (!bAdapter.isEnabled()) {
                            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), Utils.REQUEST_CONNECT_BT);
                        } else {
                            OpenPrinterList();
                        }
                    }
                }
            }
        }
    }

    private void showMessage(boolean isAllowAutoPrint) {
        final GenerateAlertBox generateAlertBox = new GenerateAlertBox(getActContext());
        generateAlertBox.setCancelable(false);
        generateAlertBox.setContentMessage("", generalFunc.retrieveLangLBl("", isAllowAutoPrint ? "LBL_AUTO_PRINT_NOTE" : "LBL_ALLOW_PRINT_NOTE"));
        generateAlertBox.setBtnClickList(btn_id -> generateAlertBox.closeAlertBox());
        generateAlertBox.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlertBox.showAlertBox();
    }

    private void confirmPrintSettings() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateThermalPrintStatus");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eThermalAutoPrint", cbAllowAutoPrint.isChecked() ? "Yes" : "No");
        parameters.put("eThermalPrintEnable", cbAllowPrint.isChecked() ? "Yes" : "No");


        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    printSettingsBtn.setEnabled(true);
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);
                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                        if (isDataAvail) {

                            if (!cbAllowPrint.isChecked() && MyApp.btsocket != null && generalFunc.retrieveValue(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes")) {
                                disConnectTxt.performClick();
                            }
                            generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValueStr(Utils.message_str, responseObj));
                            JSONObject messageObj = generalFunc.getJsonObject(generalFunc.getJsonValueStr(Utils.message_str, responseObj));

                            new SetGeneralData(generalFunc, messageObj);
//                    setResult(RESULT_OK);
//                    backImgView.performClick();

                            showConnectedStatusArea();

                            String message = generalFunc.getJsonValueStr(Utils.message_str_one, responseObj);
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", Utils.checkText(message) ? message : "LBL_INFO_UPDATED_TXT"));
                        } else {
                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                        }
                    } else {
                        generalFunc.showError();
                    }
                });

    }


    public void OpenPrinterList() {
        if (customDialog != null) {
            customDialog.closeDialog(true);
        }

        customDialog = new CustomDialog(getActContext());
        customDialog.setDetails(generalFunc.retrieveLangLBl("Select Printer", "LBL_SELECT_PRINTER"), generalFunc.retrieveLangLBl("", "LBL_T_PRINTER_ALERT_TITLE_TXT"), null, generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), false, R.drawable.ic_printer, true, true, 2);
        customDialog.setDirection(CustomDialog.OpenDirection.BOTTOM);
        customDialog.setRoundedViewBackgroundColor(R.color.appThemeColor_1);
        customDialog.setIconTintColor(R.color.white);
        customDialog.setBtnRadius(10);
        customDialog.setImgStrokWidth(10);
        customDialog.setTitleTxtColor(R.color.appThemeColor_1);
        customDialog.createDialog();
       /* customDialog.setNegativeButtonClick(new Closure() {
            @Override
            public void exec() {

            }
        });*/
        customDialog.setCloseDialogListener(new Closure() {
            @Override
            public void exec() {
                customDialog = null;
                isConnected = MyApp.btsocket != null;
                Logger.d(TAG, "in setCloseDialogListener" + isConnected);
                reSetDetails(false);
            }
        });
        customDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ArrayList<String> requestPermissions = new ArrayList<>();

        switch (requestCode) {
            case REQUEST_COARSE_LOCATION:
                Collections.addAll(requestPermissions, permissions);
                if (!generalFunc.isAllPermissionGranted(false, requestPermissions)) {
                    showNoPermission();
                }
                break;
            case Utils.REQUEST_ENABLE_BT:
                break;
            case REQUEST_CONNECT_SCAN_BT:
                Collections.addAll(requestPermissions, permissions);
                if (generalFunc.isAllPermissionGranted(false, requestPermissions)) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), Utils.REQUEST_CONNECT_BT);
                } else {
                    showNoPermission();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  disconnectPrinter();
    }

    public void changeConnectionState() {
        isConnected = MyApp.btsocket != null;
        reSetDetails(false);
    }

    private void disconnectPrinter() {
        try {

            if (customDialog != null) {
                customDialog.closeDialog(false);
            }


            if (MyApp.btsocket != null) {

                if (MyApp.outputStream != null) {
                    MyApp.outputStream.close();
                }

                if (MyApp.btsocket.isConnected()) {
                    MyApp.btsocket.close();
                    generalFunc.showMessage(connectTxt, generalFunc.retrieveLangLBl("", "LBL_PRINTER_DISCONNECTED"));
                }
                MyApp.btsocket = null;
            }
            changeConnectionState();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.REQUEST_CONNECT_BT && resultCode == Activity.RESULT_OK) {
            connectBluetooth();
        } else if (requestCode == 52) {
            ArrayList<String> requestPermissions = new ArrayList<>();
            requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (generalFunc.isAllPermissionGranted(false, requestPermissions)) {
                connectBluetooth();
            }
        } else {
            if (requestCode == Utils.REQUEST_CONNECT_BT) {
                generalFunc.showMessage(connectTxt, generalFunc.retrieveLangLBl("", "LBL_ALLOW_BLUETOOTH"));
            }
        }
    }
}
