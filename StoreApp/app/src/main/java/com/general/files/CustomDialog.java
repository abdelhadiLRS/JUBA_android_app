package com.general.files;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.files.BlueToothDeviceListAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.multixpro.store.R;
import com.utils.Logger;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.anim.loader.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/*
 *  Supported Devices
 *  Pegasus PM5821 - Normal Printer
 *  MP02 in built printer with phone
 *  PT-210 - with Pin
 *
 * */
public class CustomDialog implements BlueToothDeviceListAdapter.OnItemClickListener {

    int iconTintColor = R.color.appThemeColor_TXT_1;
    int roundedViewBackgroundColor = R.color.appThemeColor_1;
    int titleTxtColor = R.color.black;
    int messageTxtColor = R.color.black;
    int cardAreaColor = R.color.white;
    int positiveBtnTextColor = R.color.appThemeColor_TXT_1;
    int positiveBtnBorderColor = R.color.appThemeColor_1;
    int positiveBtnBackColor = R.color.appThemeColor_1;
    int negativeBtnColor = R.color.appThemeColor_1;
    int negativeBtnTextColor = R.color.appThemeColor_TXT_1;
    int negativeBtnBackColor = R.color.appThemeColor_1;
    int negativeBtnBorderColor = R.color.appThemeColor_1;
    int btnRadius = 0;
    int imgStrokWidth = 0;
    int imgBorderColor = R.color.appThemeColor_TXT_1;

    String titleTxt;
    String messageTxt;
    int iconType;
    String positiveBtnTxt;
    String negativeBtnTxt;
    boolean setCancelable;
    boolean setAnimation;
    boolean isList;
    boolean printBill;
    int btnOrientation;

    Context mContext;
    private Dialog customDialog;
    private BottomSheetDialog customDialog1;
    private GeneralFunctions generalFunc;
    private Animation animation;
    private Animation animation2;

    MTextView tv_Proceed_Button;
    MTextView tv_Positive_Button;
    MTextView tv_Negative_Button;
    MTextView tv_Cancel_Button;
    ImageView iv_icon;
    LinearLayout closeArea;
    private RecyclerView dataRecyclerView;
    private AVLoadingIndicatorView loaderView;
    ProgressBar mProgressBar;
    private MTextView waitTxt;

    private MTextView connectTxt;
    private MTextView deviceName;
    private MTextView deviceId;
    private MTextView statusTxt;

    FrameLayout showDeviceList;
    LinearLayout connectToDevice;

    private String TAG = "---DeviceList";

    private BluetoothAdapter mBluetoothAdapter = null;
    private AsyncTask<Void, Void, Void> asyncTask;
    private boolean isCloseClicked = false;

    //    static private ArrayAdapter<String> mArrayAdapter = null;
    public enum OpenDirection {CENTER, BOTTOM}

    private OpenDirection openDirection;


    private static final UUID SPP_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    // Default UUID
    UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static private BluetoothSocket mbtSocket = null;

    ArrayList<HashMap<String, String>> deviceList = new ArrayList<>();
    private BlueToothDeviceListAdapter bluetoothAdapter;
    static private ArrayAdapter<BluetoothDevice> btDevices = null;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    ArrayList<HashMap<String, String>> dataList;
    HashMap<String, String> reqiuredDetails;
    private Closure closeDialog = null;
    boolean otherDialogOpen = false;
    private long notification_permission_launch_time = -1;

    private enum STATUS {
        DISCOVERING,
        CONNECTED,
        FREE,
        DISCONNECTED,
        DISCONNECTREQUESTED,
        PARINGREQUESTED,
        ACTION_UUID,
        EXTRA_UUID,
        FAILED
    }

    private volatile STATUS mCurrStatus = STATUS.FREE;


    public CustomDialog(Context mContext) {
        this.mContext = mContext;
        this.generalFunc = new GeneralFunctions(mContext);
        animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_out);
        animation2 = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);

    }

    /**
     * @param title          -> Set "" if not required
     * @param message        ->  Set "" if not required
     * @param setCancelable  -> true/false
     * @param positiveBtnTxt ->  Set "" if not required
     * @param negativeBtnTxt -> Set "" if not required
     * @param iconType       ->   ICON IMAGE
     *                       //1-Info - rounded icon , 2-Success,3- Notice - filled icon , 4-Error,5-warning,5-DOne, 6 - Subscription,7-Info Italic icon,8- Notice - unfilled icon,9-Info Normal icon
     * @param setAnimation   -> true/false
     * @param btnOrientation ->   1- Horizontal , 2- vertical
     */
    public void setDetails(String title, String message, String positiveBtnTxt, String negativeBtnTxt, boolean setCancelable, int iconType, boolean setAnimation, int btnOrientation) {
        this.titleTxt = title;
        this.messageTxt = message;
        this.iconType = iconType;
        this.positiveBtnTxt = positiveBtnTxt;
        this.negativeBtnTxt = negativeBtnTxt;
        this.setCancelable = setCancelable;
        this.setAnimation = setAnimation;
        this.btnOrientation = btnOrientation; // 1- Horizontal , 2- vertical
    }

    public void setDetails(String title, String message, String positiveBtnTxt, String negativeBtnTxt, boolean setCancelable, int iconType, boolean setAnimation, boolean isList, int btnOrientation) {
        this.titleTxt = title;
        this.messageTxt = message;
        this.iconType = iconType;
        this.setCancelable = setCancelable;
        this.setAnimation = setAnimation;
        this.positiveBtnTxt = positiveBtnTxt;
        this.negativeBtnTxt = negativeBtnTxt;
        this.isList = isList;
        this.btnOrientation = btnOrientation; // 1- Horizontal , 2- vertical
    }

    public void setPrintDetails(boolean printBill, ArrayList<HashMap<String, String>> dataList, HashMap<String, String> reqiuredDetails) {
        this.printBill = printBill;
        this.dataList = dataList;
        this.reqiuredDetails = reqiuredDetails;
    }

    public void setIconTintColor(int iconTintColor) {
        this.iconTintColor = iconTintColor;
    }

    public void setTitleTxtColor(int titleTxtColor) {
        this.titleTxtColor = titleTxtColor;
    }

    public void setMessageTxtColor(int messageTxtColor) {
        this.messageTxtColor = messageTxtColor;
    }

    public void setCardAreaColor(int cardAreaColor) {
        this.cardAreaColor = cardAreaColor;
    }


    public void setNegativeBtnColor(int negativeBtnColor) {
        this.negativeBtnColor = negativeBtnColor;
    }


    public void setPositiveBtnTextColor(int positiveBtnTextColor) {
        this.positiveBtnTextColor = positiveBtnTextColor;
    }

    public void setNegativeBtnBackColor(int negativeBtnBackColor) {
        this.negativeBtnBackColor = negativeBtnBackColor;
    }

    public void setPositiveBtnBackColor(int positiveBtnBackColor) {
        this.positiveBtnBackColor = positiveBtnBackColor;
    }

    public void setRoundedViewBorderColor(int imgBorderColor) {
        this.imgBorderColor = imgBorderColor;
    }

    public void setBtnRadius(int btnRadius) {
        this.btnRadius = btnRadius;
    }


    public void setImgStrokWidth(int imgStrokWidth) {
        this.imgStrokWidth = imgStrokWidth;
    }


    public void setRoundedViewBackgroundColor(int roundedViewBackgroundColor) {
        this.roundedViewBackgroundColor = roundedViewBackgroundColor;
    }

    public void setDirection(OpenDirection openDirection) {
        this.openDirection = openDirection;
    }


    public Dialog show() {
        try {
            if (mContext instanceof Activity) {
                if (!((Activity) mContext).isFinishing()) {
                    showDialog();
                }
            } else {
                showDialog();
            }
        } catch (Exception e) {
            Logger.e("[AwSDialog:showAlert]", "Erro ao exibir alert");
        }

        return customDialog1 != null ? customDialog1 : customDialog;
    }

    public void createDialog() {
        createCustomDialog();
    }

    public void showDialog() {
        if (customDialog != null) {
            customDialog.show();
            if (setAnimation)
                iv_icon.startAnimation(animation2);
        } else if (customDialog1 != null) {
            customDialog1.show();
            if (setAnimation)
                iv_icon.startAnimation(animation2);
        }
    }

    public boolean isShowing() {
        boolean isShowing = false;
        if (customDialog != null) {
            isShowing = customDialog.isShowing();
            isShowing = customDialog.isShowing();
        } else if (customDialog1 != null) {
            isShowing = customDialog1.isShowing();
        }
        return isShowing;
    }

    public void hideDialog() {
        if (customDialog != null) {
            customDialog.hide();
        } else if (customDialog1 != null) {
            customDialog1.hide();
        }
    }

    public void closeDialog(boolean flushData) {

        /*if (otherDialogOpen) {
            showDeviceListView();
        } else {*/
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        } else if (customDialog1 != null) {
            customDialog1.dismiss();
            customDialog1 = null;
        }

        freeBluetoothReferences();

        if (flushData) {
            flushData();
        }
        if (closeDialog != null) {
            closeDialog.exec();
        }

        // }
    }

    private void showDeviceListView() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
        otherDialogOpen = false;
        //flushData();
        setBluetoothAdapter();
        connectToDevice.setVisibility(View.GONE);
        showDeviceList.setVisibility(View.VISIBLE);
    }


    protected void createCustomDialog() {

        if (openDirection == OpenDirection.BOTTOM) {
            customDialog1 = new BottomSheetDialog(mContext);
            customDialog1.setContentView(R.layout.custom_dialog);
            customDialog1.setCancelable(setCancelable);

            setDialogData();
        } else {
            customDialog = new Dialog(mContext);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customDialog.setContentView(R.layout.custom_dialog);
            customDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

            setDialogData();
        }

    }

    private void setDialogData() {
        if (customDialog != null) {
            final CardView cv_detailArea = (CardView) customDialog.findViewById(R.id.cv_detailArea);
            final MTextView tv_title = (MTextView) customDialog.findViewById(R.id.tv_title);
            final MTextView tv_message = (MTextView) customDialog.findViewById(R.id.tv_message);
            iv_icon = (ImageView) customDialog.findViewById(R.id.iv_icon);
            final LinearLayout horizontalButtonArea = (LinearLayout) customDialog.findViewById(R.id.horizontalButtonArea);
            final SelectableRoundedImageView dialogImgView = (SelectableRoundedImageView) customDialog.findViewById(R.id.dialogImgView);
            final LinearLayout verticalButtonArea = (LinearLayout) customDialog.findViewById(R.id.verticalButtonArea);
            tv_Proceed_Button = (MTextView) customDialog.findViewById(R.id.tv_Proceed_Button);
            tv_Cancel_Button = (MTextView) customDialog.findViewById(R.id.tv_Cancel_Button);
            tv_Positive_Button = (MTextView) customDialog.findViewById(R.id.tv_Positive_Button);
            tv_Negative_Button = (MTextView) customDialog.findViewById(R.id.tv_Negative_Button);
            dataRecyclerView = (RecyclerView) customDialog.findViewById(R.id.dataRecyclerView);
            LinearLayout listArea = (LinearLayout) customDialog.findViewById(R.id.listArea);
            loaderView = (AVLoadingIndicatorView) customDialog.findViewById(R.id.loaderView);
            waitTxt = (MTextView) customDialog.findViewById(R.id.waitTxt);


            cv_detailArea.setCardBackgroundColor(mContext.getResources().getColor(cardAreaColor));

            iv_icon.setColorFilter(mContext.getResources().getColor(iconTintColor));


            new CreateRoundedView(mContext.getResources().getColor(roundedViewBackgroundColor), Utils.dipToPixels(mContext, 40), imgStrokWidth,
                    mContext.getResources().getColor(imgBorderColor), dialogImgView);

            if (isList) {
                waitTxt.setText(generalFunc.retrieveLangLBl("Fetching nearby available printers", "LBL_FETCH_PRINTERS_TXT"));
                listArea.setVisibility(View.VISIBLE);
                waitTxt.setVisibility(View.VISIBLE);
                loaderView.setVisibility(View.VISIBLE);

                try {
                    if (initDevicesList() != 0) {
                        closeDialog(false);
                    }

                } catch (Exception ex) {
                    closeDialog(false);
                }

                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) mContext,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Utils.REQUEST_COARSE_LOCATION);
                } else {
                    proceedDiscovery();
                }

            }

            if (btnOrientation == 1) {
                horizontalButtonArea.setVisibility(View.VISIBLE);
            } else if (btnOrientation == 2) {
                verticalButtonArea.setVisibility(View.VISIBLE);
            }

            iv_icon.setImageResource(iconType);

            if (Utils.checkText(titleTxt)) {
                tv_title.setVisibility(View.VISIBLE);
                tv_title.setTextColor(mContext.getResources().getColor(titleTxtColor));
                tv_title.setText(titleTxt);
            }

            if (Utils.checkText(messageTxt)) {
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setTextColor(mContext.getResources().getColor(messageTxtColor));
                tv_message.setText(messageTxt);
            }

            if (Utils.checkText(positiveBtnTxt)) {
                tv_Positive_Button.setVisibility(View.VISIBLE);
                tv_Positive_Button.setTextColor(mContext.getResources().getColor(positiveBtnTextColor));
                tv_Positive_Button.setText(positiveBtnTxt);

                new CreateRoundedView(mContext.getResources().getColor(positiveBtnBackColor), Utils.dipToPixels(mContext, btnRadius), 0,
                        mContext.getResources().getColor(positiveBtnBorderColor), tv_Positive_Button);

                if (btnOrientation == 1) {
                    tv_Proceed_Button.setVisibility(View.VISIBLE);
                    tv_Proceed_Button.setTextColor(mContext.getResources().getColor(negativeBtnTextColor));
                    tv_Proceed_Button.setText(positiveBtnTxt);

                    new CreateRoundedView(mContext.getResources().getColor(positiveBtnBackColor), Utils.dipToPixels(mContext, btnRadius), 0,
                            mContext.getResources().getColor(positiveBtnBorderColor), tv_Proceed_Button);
                }
            }

            if (Utils.checkText(negativeBtnTxt)) {
                tv_Negative_Button.setVisibility(View.VISIBLE);
                tv_Negative_Button.setTextColor(mContext.getResources().getColor(negativeBtnTextColor));
                tv_Negative_Button.setText(negativeBtnTxt);

                new CreateRoundedView(mContext.getResources().getColor(negativeBtnBackColor), Utils.dipToPixels(mContext, btnRadius), 0,
                        mContext.getResources().getColor(negativeBtnBorderColor), tv_Negative_Button);


                if (btnOrientation == 1) {
                    tv_Cancel_Button.setVisibility(View.VISIBLE);
                    tv_Cancel_Button.setTextColor(mContext.getResources().getColor(negativeBtnTextColor));
                    tv_Cancel_Button.setText(negativeBtnTxt);

                    new CreateRoundedView(mContext.getResources().getColor(negativeBtnBackColor), Utils.dipToPixels(mContext, btnRadius), 0,
                            mContext.getResources().getColor(negativeBtnBorderColor), tv_Cancel_Button);
                }

            }

            if (setAnimation) {


                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        iv_icon.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        iv_icon.startAnimation(animation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        } else if (customDialog1 != null) {
            final MTextView tv_title = (MTextView) customDialog1.findViewById(R.id.TitleTxt);
            closeArea = (LinearLayout) customDialog1.findViewById(R.id.closeArea);
            dataRecyclerView = (RecyclerView) customDialog1.findViewById(R.id.mRecyclerView);
            loaderView = (AVLoadingIndicatorView) customDialog1.findViewById(R.id.loaderView1);
            waitTxt = (MTextView) customDialog1.findViewById(R.id.waitTxt1);
            connectTxt = (MTextView) customDialog1.findViewById(R.id.connectTxt);
            deviceName = (MTextView) customDialog1.findViewById(R.id.deviceName);
            deviceId = (MTextView) customDialog1.findViewById(R.id.deviceId);
            statusTxt = (MTextView) customDialog1.findViewById(R.id.statusTxt);
            LinearLayout bottomDataArea = (LinearLayout) customDialog1.findViewById(R.id.bottomDataArea);
            connectToDevice = (LinearLayout) customDialog1.findViewById(R.id.connectToDevice);
            showDeviceList = (FrameLayout) customDialog1.findViewById(R.id.showDeviceList);
            RelativeLayout centerDataArea = (RelativeLayout) customDialog1.findViewById(R.id.centerDataArea);
            mProgressBar = (ProgressBar) customDialog1.findViewById(R.id.mProgressBar);

            bottomDataArea.setVisibility(View.VISIBLE);
            centerDataArea.setVisibility(View.GONE);

            mProgressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.appThemeColor_2), android.graphics.PorterDuff.Mode.SRC_IN);
            mProgressBar.setIndeterminate(true);

            if (Utils.checkText(titleTxt)) {
                tv_title.setVisibility(View.VISIBLE);
                tv_title.setTextColor(mContext.getResources().getColor(titleTxtColor));
                tv_title.setText(titleTxt);
            }

            if (isList) {
                waitTxt.setText(generalFunc.retrieveLangLBl("Fetching nearby available printers", "LBL_FETCH_PRINTERS_TXT"));
                waitTxt.setVisibility(View.VISIBLE);
//                loaderView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                setBluetoothAdapter();
            }


            closeArea.setOnClickListener(v -> {
                isCloseClicked = true;
                Logger.d(TAG, "ACTION_DIALOUGE_CLOSED :: " + isCloseClicked);
                closeDialog(true);
            });


        }
    }

    private void setBluetoothAdapter() {
        try {
            if (initDevicesList() != 0) {
                closeDialog(false);
            }

        } catch (Exception ex) {
            closeDialog(false);
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Utils.REQUEST_COARSE_LOCATION);
        } else {
            proceedDiscovery();
        }

    }

    public CustomDialog setPositiveButtonClick(@Nullable final
                                               Closure selectedNo) {

        if (btnOrientation == 1) {
            tv_Proceed_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedNo != null) {
                        selectedNo.exec();
                    }

                    closeDialog(false);
                }
            });
        } else if (btnOrientation == 2) {
            tv_Positive_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedNo != null) {
                        selectedNo.exec();
                    }

                    closeDialog(false);
                }
            });
        }


        return this;
    }

    public CustomDialog setCloseDialogListener(@Nullable final Closure selectedNo) {

        closeDialog = selectedNo;

        return this;
    }

    public CustomDialog setNegativeButtonClick(@Nullable final Closure selectedNo) {
        if (customDialog1 != null) {
            return this;
        }
        if (btnOrientation == 1) {
            tv_Cancel_Button.setOnClickListener(view -> {
                if (selectedNo != null) {
                    selectedNo.exec();
                }

                closeDialog(false);
            });
        } else if (btnOrientation == 2) {
            tv_Negative_Button.setOnClickListener(view -> {
                if (selectedNo != null) {
                    selectedNo.exec();
                }

                closeDialog(false);
            });
        }

        return this;
    }


    @SuppressLint("MissingPermission")
    protected void proceedDiscovery() {
        try {

            bluetoothAdapter = new BlueToothDeviceListAdapter(mContext, deviceList, new GeneralFunctions(mContext), false);
            dataRecyclerView.setAdapter(bluetoothAdapter);
            bluetoothAdapter.setOnItemClickListener(this);

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
            filter.addAction(BluetoothDevice.EXTRA_UUID);
            filter.addAction(BluetoothDevice.ACTION_UUID);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

//        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
//        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//            Log.d(TAG, "ACTION_REGISTER_RECEIVER :: ");

            ((Activity) mContext).registerReceiver(mBTReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        } catch (Exception e) {
            e.printStackTrace();
            //BluetoothAdapter: Bluetooth binder is null
        }
    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                try {


                    if (btDevices == null) {
                        btDevices = new ArrayAdapter<BluetoothDevice>(mContext.getApplicationContext(), R.layout.layout_list);
                    }


                    if (btDevices.getPosition(device) < 0) {
                        checkDeviceType(device);

                    }
                } catch (Exception ex) {
//                    Log.d(TAG, "mBTReceiver Failed" + ex.toString());
//                    Toast.makeText(mContext.getApplicationContext(), "Can't find bluetooth receivers", Toast.LENGTH_SHORT).show();
                    ex.fillInStackTrace();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mCurrStatus = STATUS.DISCOVERING;
//                Log.d(TAG, "ACTION_DISCOVERY_STARTED :: "+ (showDeviceList.getVisibility()==View.VISIBLE));
                PendingResult pr = goAsync();  // so loading shared prefs doesn't kill animation
//                persistDiscoveringTimestamp(pr, true);
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mCurrStatus = STATUS.FREE;
//                Log.d(TAG, "ACTION_DISCOVERY_FINISHED :: "+ (showDeviceList.getVisibility()==View.VISIBLE));
                waitTxt.setVisibility(View.GONE);
                (customDialog1 != null ? mProgressBar : loaderView).setVisibility(View.GONE);
                PendingResult pr = goAsync();  // so loading shared prefs doesn't kill animation
//                persistDiscoveringTimestamp(pr, false);

                //discovery finishes, dismis progress dialog
            } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
//                Log.d(TAG, "ACTION_CONNECTION_STATE_CHANGED :: ");

                int newState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                int oldState = intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);
                if (newState == BluetoothProfile.STATE_DISCONNECTED &&
                        oldState == BluetoothProfile.STATE_CONNECTING) {
                    mCurrStatus = STATUS.FAILED;
//                    Log.i(TAG, "Failed to connect BT headset");
                }
              /*  mManager.getCachedDeviceManager().onProfileStateChanged(device,
                        Profile.HEADSET, newState);*/

                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
//                Log.d(TAG, "ACTION_PAIRING_REQUEST :: ");
//                Log.d(TAG, "ACTION_DIALOUGE_CLOSED ? :: "+isCloseClicked);
                /*if (isCloseClicked) {
                    return;
                }

                Toast.makeText(mContext.getApplicationContext(), "Pairing Requested", Toast.LENGTH_SHORT).show();

                try {
                    int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
                    //the pin in case you need to accept for an specific pin
                    Log.d("PIN", " " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0));
                    //maybe you look for a name or address
                    Log.d("Bonded", device.getName());
                    byte[] pinBytes;
                    pinBytes = ("" + pin).getBytes("UTF-8");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        device.setPin(pinBytes);
                        //setPairing confirmation if neeeded
                        device.setPairingConfirmation(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mCurrStatus = STATUS.PARINGREQUESTED;*/
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_UUID.equalsIgnoreCase(action) || BluetoothDevice.EXTRA_UUID.equals(action)) {
//                Log.d(TAG, "ACTION_UUID :: ");
              /*  Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                for (int i = 0; i < uuidExtra.length; i++) {
                    Log.d(TAG, "\n  Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());
                }*/
                mCurrStatus = STATUS.EXTRA_UUID;
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
//                Log.d(TAG, "ACTION_NAME_CHANGED :: ");
//                mManager.getCachedDeviceManager().onDeviceNameUpdated(device);
                //discovery finishes, dismis progress dialog
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (bondState == BluetoothDevice.BOND_NONE) {
                    if ((System.currentTimeMillis() - notification_permission_launch_time) < 500) {
                        device.createBond();
                    } else {
                        closeDialog(false);
                    }
                } else if (bondState == BluetoothDevice.BOND_BONDING) {
                    notification_permission_launch_time = System.currentTimeMillis();
                } else if (bondState == BluetoothDevice.BOND_BONDED) {

                    for (int i = 0; i < btDevices.getCount(); i++) {
                        if (btDevices.getItem(i).equals(device)) {
                            connectToDevice(i);
                            break;
                        }
                    }
                }

               /* CachedBluetoothDeviceManager cachedDeviceMgr = mManager.getCachedDeviceManager();
                cachedDeviceMgr.onBondingStateChanged(device, bondState);
                if (bondState == BluetoothDevice.BOND_NONE) {
                    if (device.isBluetoothDock()) {
                        // After a dock is unpaired, we will forget the
                        // settings
                        mManager.removeDockAutoConnectSetting(device.getAddress());
                        // if the device is undocked, remove it from the list as
                        // well
                        if (!device.getAddress().equals(getDockedDeviceAddress(context))) {
                            cachedDeviceMgr.onDeviceDisappeared(device);
                        }
                    }
                    int reason = intent.getIntExtra(BluetoothDevice.EXTRA_REASON,
                            BluetoothDevice.ERROR);
                    cachedDeviceMgr.showUnbondMessage(device, reason);
                }*/
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(mContext.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

                Logger.d(TAG, "ACTION_ACL_CONNECTED :: ");

//                otherDialogOpen=false;
//                closeDialog(false);
                //Device is now connected
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                mCurrStatus = STATUS.DISCONNECTREQUESTED;
                Toast.makeText(mContext.getApplicationContext(), "Disconnect Requested", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                mCurrStatus = STATUS.DISCONNECTED;
                Toast.makeText(mContext.getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
            }
        }


    };

    private void checkDeviceType(BluetoothDevice device) {
        addPrinterToList(device);
    }

    @SuppressLint("MissingPermission")
    private void addPrinterToList(BluetoothDevice device) {
        btDevices.add(device);
        HashMap<String, String> map = new HashMap<>();
        String name = device.getName();
        map.put("Name", Utils.checkText(name) ? name : "UnNamed");
        map.put("Id", device.getAddress());
        deviceList.add(map);
        bluetoothAdapter.notifyDataSetChanged();
        if (deviceList.size() > 1) {
            waitTxt.setVisibility(View.GONE);
        } else {
            waitTxt.setVisibility(View.VISIBLE);
            waitTxt.setText(generalFunc.retrieveLangLBl("Can't find bluetooth receivers", ""));
        }

    }

    private void flushData() {
        try {

            if (mbtSocket != null && mbtSocket.isConnected()) {
                mbtSocket.close();
                mbtSocket = null;
                MyApp.btsocket = null;
                mCurrStatus = STATUS.FREE;
                Logger.d(TAG, "ACTION_CLOSE_SOCKET :: ");

            }

            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }


            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }

            //finalize();
        } catch (Exception ex) {
            Logger.e(TAG, ex.getMessage());
        }

    }

    @SuppressLint("MissingPermission")
    private void freeBluetoothReferences() {
        try {
            ((Activity) mContext).unregisterReceiver(mBTReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (btDevices != null) {
            btDevices.clear();
            btDevices = null;
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter = null;
        }

        if (bluetoothAdapter != null) {
            deviceList.clear();
            bluetoothAdapter.selectedPos = -1;
            bluetoothAdapter.notifyDataSetChanged();
            bluetoothAdapter = null;

        }
    }

    @SuppressLint("MissingPermission")
    private int initDevicesList() {
//        flushData();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
//        if (bluetoothManager != null) {
//            mBluetoothAdapter = bluetoothManager.getAdapter();
//        }

        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext.getApplicationContext(), "Bluetooth not supported!!", Toast.LENGTH_LONG).show();
            return -1;
        } else if ((!mBluetoothAdapter.isEnabled())) {
//            Toast.makeText(mContext, "bluetooth not open", Toast.LENGTH_SHORT).show();
            closeDialog(true);

        }


        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter = null;
        }


        Intent enableBtIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            ((Activity) mContext).startActivityForResult(enableBtIntent, Utils.REQUEST_ENABLE_BT);
        } catch (Exception ex) {

            return -2;
        }

        return 0;

    }

    @SuppressLint("MissingPermission")
    public void startDiscovery() {

        Set<BluetoothDevice> btDeviceList = mBluetoothAdapter.getBondedDevices();
        try {
            if (btDeviceList.size() > 0) {

                for (BluetoothDevice device : btDeviceList) {
                    if (!btDeviceList.contains(device)) {
                        checkDeviceType(device);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.e(TAG, ex.getMessage());
        }
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onItemClickList(View v, int position) {


        if (mBluetoothAdapter == null) {
            return;
        }

        String name = deviceList.get(position).get("Name");
        String udid = deviceList.get(position).get("Id");

        if (customDialog1 != null) {
            otherDialogOpen = true;
            connectToDevice.setVisibility(View.VISIBLE);
            showDeviceList.setVisibility(View.GONE);
            deviceName.setText(name);
            deviceId.setText(udid);
        }


        if (customDialog1 != null) {
            bluetoothAdapter.selectedPos = position;
            bluetoothAdapter.notifyDataSetChanged();

            if (!otherDialogOpen) {
                return;
            }
            new Handler().postDelayed(() -> {
                if (btDevices != null && btDevices.getCount() > 0) {
                    connectToDevice(position);
                }
            }, 2000);

        } else {
            if (!otherDialogOpen) {
                return;
            }
            connectToDevice(position);
        }


    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(int position) {
        if (!otherDialogOpen) {
            return;
        }

        if (btDevices.getCount() <= position) {
            return;
        }

        if (btDevices.getItem(position).getBondState() == BluetoothDevice.BOND_NONE) {
            btDevices.getItem(position).createBond();
        } else {
            asyncTask = new DoSomethingThread(position).execute();
            if (printBill) {
                MyApp.btsocket = getSocket();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public class DoSomethingThread extends AsyncTask<Void, Void, Void> {
        private int position = 0;

        public DoSomethingThread(int position) {
            this.position = position;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (btDevices != null) {
                UUID udid;
                try {
                    udid = btDevices.getItem(position).getUuids()[0].getUuid();
                } catch (Exception ignored) {
                    udid = DEFAULT_UUID;
                }
                UUID finalUdid = udid;
                ((Activity) mContext).runOnUiThread(() -> {
                    try {
                        connectBTDevice(finalUdid, position, false);
                    } catch (Exception ignored) {
                        if (customDialog1 != null) {
                            deviceId.setText("" + DEFAULT_UUID);
                        }
                        try {
                            connectBTDevice(DEFAULT_UUID, position, true);
                        } catch (Exception e) {
                            e.printStackTrace();

                            errorInConnection();

                        }
                    }

                });
//                    connectToSocket(udid, position, false);

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


    }

    @SuppressLint("MissingPermission")
    private void connectBTDevice(UUID udid, int position, boolean isDefaultUdid) throws Exception {
        String name = btDevices.getItem(position).getName();


        if (mbtSocket != null && mbtSocket.isConnected()) {
            mbtSocket.close();
            mbtSocket = null;
            MyApp.btsocket = null;
            mCurrStatus = STATUS.FREE;
        }

        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter = null;
        }

        mbtSocket = btDevices.getItem(position).createRfcommSocketToServiceRecord(udid);
        mbtSocket.connect();

        MyApp.btsocket = mbtSocket;
        mCurrStatus = STATUS.CONNECTED;


        mInputStream = mbtSocket.getInputStream();
        mOutputStream = mbtSocket.getOutputStream();

        if (btDevices != null) {
            btDevices.clear();
            btDevices = null;
        }

        Toast.makeText(mContext.getApplicationContext(), (Utils.checkText(name) ? name + " " : "") + generalFunc.retrieveLangLBl("", "LBL_PRINTER_CONNECTED"), Toast.LENGTH_SHORT).show();
        otherDialogOpen = false;

        freeBluetoothReferences();
        closeDialog(false);
    }


    @SuppressLint("MissingPermission")
    private void errorInConnection() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter = null;
        }

//        ((Activity) mContext).runOnUiThread(socketErrorRunnable);

        try {
            if (mbtSocket != null && mbtSocket.isConnected()) {
                mbtSocket.close();
                mbtSocket = null;
                MyApp.btsocket = null;
                mCurrStatus = STATUS.FREE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mbtSocket = null;
        MyApp.btsocket = null;
        mCurrStatus = STATUS.FREE;


        if (otherDialogOpen)
            closeDialog(true);

    }

    public static BluetoothSocket getSocket() {
        return mbtSocket;
    }


}
