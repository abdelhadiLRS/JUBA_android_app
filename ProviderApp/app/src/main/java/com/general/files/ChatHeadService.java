package com.general.files;


import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.multixpro.provider.CabRequestedActivity;
import com.multixpro.provider.R;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

public class ChatHeadService extends Service {
    public String message = "";
    ImageView removeImg;
    Handler myHandler = new Handler();
    private WindowManager windowManager;
    private RelativeLayout chatheadView, removeView;
    private LinearLayout txtView, txt_linearlayout;
    Runnable myRunnable = new Runnable() {

        @Override
        public void run() {

            if (txtView != null) {
                txtView.setVisibility(View.GONE);
            }
        }
    };
    private LinearLayout chatheadImg;
    private TextView txt1;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private boolean isLeft = true;
    private String sMsg = "";

    @SuppressWarnings("deprecation")

    @Override
    public void onCreate() {

        super.onCreate();


    }

    private void handleStart() {


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        removeView = (RelativeLayout) inflater.inflate(R.layout.window_notification_remove, null);
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

        removeView.setVisibility(View.GONE);
        removeImg = (ImageView) removeView.findViewById(R.id.remove_img);
        windowManager.addView(removeView, paramRemove);


        chatheadView = (RelativeLayout) inflater.inflate(R.layout.design_window_notification, null);
        chatheadImg = (LinearLayout) chatheadView.findViewById(R.id.windowLayout);

        MButton cancelBtn = ((MaterialRippleLayout) chatheadView.findViewById(R.id.cancelBtn)).getChildView();
        MButton viewDetailBtn = ((MaterialRippleLayout) chatheadView.findViewById(R.id.viewDetailBtn)).getChildView();

        GeneralFunctions generalFunc = MyApp.getInstance().getGeneralFun(this);

        cancelBtn.setText(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"));
        viewDetailBtn.setText(generalFunc.retrieveLangLBl("View Detail", "LBL_VIEW_DETAIL"));
        MTextView waitingTxtView = (MTextView) chatheadView.findViewById(R.id.waitingTxtView);
        waitingTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_TRIP_USER_WAITING"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 75;
        params.y = 200;
        windowManager.addView(chatheadView, params);

        viewDetailBtn.setOnClickListener(view -> chathead_click());
        cancelBtn.setOnClickListener(view -> MyApp.getInstance().stopAlertService());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        if (windowManager == null)
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//	    	Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> landscap");

            if (txtView != null) {
                txtView.setVisibility(View.GONE);
            }

            if (layoutParams.y + (chatheadView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                layoutParams.y = szWindow.y - (chatheadView.getHeight() + getStatusBarHeight());
                windowManager.updateViewLayout(chatheadView, layoutParams);
            }

            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//	    	Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> portrait");

            if (txtView != null) {
                txtView.setVisibility(View.GONE);
            }

            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x);
            }

        }

    }

    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true;

            try {

                moveToLeft(x_cord_now);
            } catch (Exception e) {

            }

        } else {
            isLeft = false;
            try {

                moveToRight(x_cord_now);
            } catch (Exception e) {

            }

        }

    }

    private void moveToLeft(final int x_cord_now) {
        final int x = szWindow.x - x_cord_now;

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = 0 - (int) (double) bounceValue(step, x);
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(chatheadView, mParams);
            }
        }.start();
    }

    private void moveToRight(final int x_cord_now) {
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
        return value;
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

    private void chathead_click() {
//		if(MyDialog.active){
//			MyDialog.myDialog.finish();
//		}else{
        Intent it = new Intent(this, CabRequestedActivity.class);

        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("Message", "" + message);

        if (message != null && message != "") {
            startActivity(it);
        }
//		}


    }

    private void chathead_longclick() {
//		Log.d(Utils.LogTag, "Into ChatHeadService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeView, param_remove);
    }

    private void showMsg(String sMsg) {
        if (txtView != null && chatheadView != null) {
//			Log.d(Utils.LogTag, "ChatHeadService.showMsg -> sMsg=" + sMsg);
            txt1.setText(sMsg);
            myHandler.removeCallbacks(myRunnable);

            WindowManager.LayoutParams param_chathead = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
            WindowManager.LayoutParams param_txt = (WindowManager.LayoutParams) txtView.getLayoutParams();

            txt_linearlayout.getLayoutParams().height = chatheadView.getHeight();
            txt_linearlayout.getLayoutParams().width = szWindow.x / 2;

            if (isLeft) {
                param_txt.x = param_chathead.x + chatheadImg.getWidth();
                param_txt.y = param_chathead.y;

                txt_linearlayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else {
                param_txt.x = param_chathead.x - szWindow.x / 2;
                param_txt.y = param_chathead.y;

                txt_linearlayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            }

            txtView.setVisibility(View.VISIBLE);
            windowManager.updateViewLayout(txtView, param_txt);

            myHandler.postDelayed(myRunnable, 4000);

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//		Log.d(Utils.LogTag, "ChatHeadService.onStartCommand() -> startId=" + startId);

//        message = intent.getStringExtra("Message");
        if (intent != null) {
            Bundle bd = intent.getExtras();

            if (bd != null) {
                message = bd.getString("Message");
            }
//				sMsg = bd.getString(Utils.EXTRA_MSG);

//			if(sMsg != null && sMsg.length() > 0){
//				if(startId == Service.START_STICKY){
//					new Handler().postDelayed(new Runnable() {
//
//						@Override
//						public void run() {
//
//							showMsg(sMsg);
//						}
//					}, 300);
//
//				}else{
//					showMsg(sMsg);
//				}
//
//			}

        }

        if (startId == Service.START_STICKY) {
            //handleStart();

            chathead_click();
            return super.onStartCommand(intent, flags, startId);


        } else {
            return Service.START_NOT_STICKY;
        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (chatheadView != null) {
            windowManager.removeView(chatheadView);
        }

        if (txtView != null) {
            windowManager.removeView(txtView);
        }

        if (removeView != null) {
            windowManager.removeView(removeView);
        }

    }


    @Override
    public IBinder onBind(Intent intent) {

//		Log.d(Utils.LogTag, "ChatHeadService.onBind()");
        return null;
    }


}
