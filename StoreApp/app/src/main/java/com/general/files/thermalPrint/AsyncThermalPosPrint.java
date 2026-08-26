package com.general.files.thermalPrint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.store.R;
import com.multixpro.store.ThermalPrintSettingActivity;
import com.utils.CommonUtilities;
import com.utils.Logger;
import com.view.MTextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public abstract class AsyncThermalPosPrint extends AsyncTask<Integer, Integer, Integer> {
    protected final static int FINISH_SUCCESS = 1;
    protected final static int FINISH_NO_PRINTER = 2;
    protected final static int FINISH_PRINTER_DISCONNECTED = 3;
    protected final static int FINISH_PARSER_ERROR = 4;
    protected final static int FINISH_ENCODING_ERROR = 5;
    protected final static int FINISH_BARCODE_ERROR = 6;

    protected final static int PROGRESS_PROCESSING = 5;
    protected final static int PROGRESS_CONNECTING = 20;
    protected final static int PROGRESS_CONNECTED = 50;
    protected final static int PROGRESS_PREPARING = 70;
    protected final static int PROGRESS_PRINTING = 99;
    protected final static int PROGRESS_PRINTED = 100;
    protected GeneralFunctions generalFunc;
    //    protected ProgressDialog dialog;
    protected Dialog myProgressDialog;
    protected Bitmap bitmap;
    protected WeakReference<Context> weakContext;
    String TAG = "PRINT_UTILS";

    public static final int BITMAP_ZOOM_NONE = 0;
    public static final int BITMAP_ZOOM_WIDTH = 1;
    public static final int BITMAP_ZOOM_HEIGHT = 2;
    public static final int BITMAP_ZOOM_BOTH = 3;
    public static final int TYPE_PAPER_WIDTH_58MM = 0;
    public static final int TYPE_PAPER_WIDTH_76MM = 1;
    public static final int TYPE_PAPER_WIDTH_80MM = 2;

    private static int mPaperWidthType = TYPE_PAPER_WIDTH_58MM;
    private static final int PRINTER_BUFFER_LEN = 24576;
    private static final byte code_page[][] = {
            {(byte) 0x1D, (byte) 0xFE},
            {(byte) 0x1C, (byte) 0xFF},
    };

    private static final int[] bmp_byte_width = {
            48,//58mm
            50,//76mm
            72,//80mm
    };

    private static final int[] dots_per_line = {
            384,//58mm
            400,//76mm
            576,//80mm
    };

    static public byte[] cmdGSv0pwLwHhLhHd(int p, int wL, int wH, int hL, int hH, byte[] d) {
        int i;
        byte[] cmd = new byte[d.length + 8];
        cmd[0] = (byte) 0x1D;
        cmd[1] = (byte) 0x76;
        cmd[2] = (byte) 0x30;
        cmd[3] = (byte) p;
        cmd[4] = (byte) wL;
        cmd[5] = (byte) wH;
        cmd[6] = (byte) hL;
        cmd[7] = (byte) hH;
        System.arraycopy(d, 0, cmd, 8, d.length);
        return cmd;
    }


    public AsyncThermalPosPrint(Context context, Dialog myProgressDialog, GeneralFunctions generalFunc, Bitmap bitmap) {
        this.weakContext = new WeakReference<>(context);
        this.generalFunc = generalFunc;
        this.myProgressDialog = myProgressDialog;
        this.bitmap = bitmap;
    }

    protected Integer doInBackground() {
        if (bitmap == null || MyApp.btsocket == null) {
            return AsyncThermalPosPrint.FINISH_NO_PRINTER;
        }

        this.publishProgress(AsyncThermalPosPrint.PROGRESS_CONNECTING);

        try {

            if (bitmap == null || MyApp.btsocket == null) {
                return AsyncThermalPosPrint.FINISH_NO_PRINTER;
            }

            /*Reference Links Used
              https://github.com/therezacuet/PosPrinterDemo-Android-App
              https://github.com/dashu4096/PrinterDemo
            */
            getBytes(bitmap);

        } catch (IOException e) {
            Logger.d(TAG, "IOException>>" + e.getLocalizedMessage());
            Logger.d(TAG, "IOException>1>" + e.getMessage());
            return AsyncThermalPosPrint.FINISH_PRINTER_DISCONNECTED;
        } catch (Exception e) {
            Logger.d(TAG, "Exception>>" + e.getLocalizedMessage());
            e.printStackTrace();
            return AsyncThermalPosPrint.FINISH_ENCODING_ERROR;
        }

        return AsyncThermalPosPrint.FINISH_SUCCESS;
    }

    protected void onPreExecute() {

    }

    protected void onProgressUpdate(Integer... progress) {
        Logger.d(TAG, "PROGRESS Level>>" + progress[0]);
        switch (progress[0]) {
            case AsyncThermalPosPrint.PROGRESS_PROCESSING:
//                dialog.setMessage("Connecting printer...");
                break;
            case AsyncThermalPosPrint.PROGRESS_CONNECTING:
//                dialog.setMessage("Connecting printer...");
                break;
            case AsyncThermalPosPrint.PROGRESS_CONNECTED:
//                dialog.setMessage("Printer is connected...");
                break;
            case AsyncThermalPosPrint.PROGRESS_PREPARING:
//                dialog.setMessage("Printer is printing...");
                break;
            case AsyncThermalPosPrint.PROGRESS_PRINTING:
//                dialog.setMessage("Printer is printing...");
                break;
            case AsyncThermalPosPrint.PROGRESS_PRINTED:
//                dialog.setMessage("Printer has finished...");
                Logger.d(TAG, "PROGRESS_PRINTED 1>>" + PROGRESS_PRINTED);
                break;
        }

        MTextView tvProgressCount = myProgressDialog.findViewById(R.id.tvProgressCount);
        ProgressBar progressBar = myProgressDialog.findViewById(R.id.progressbar);
        tvProgressCount.setText("" + progress[0]);
        progressBar.setProgress(progress[0]);

//        dialog.setProgress(progress[0]);
//        dialog.setMax(4);
    }

    protected void onPostExecute(Integer result) {
        if (myProgressDialog != null) {
            myProgressDialog.dismiss();
            myProgressDialog = null;
        }


        Context context = weakContext.get();

        if (context == null) {
            return;
        }

        switch (result) {
            case AsyncThermalPosPrint.FINISH_SUCCESS:
                Logger.d(TAG, "FINISH_SUCCESS>>" + FINISH_SUCCESS);
               /* new AlertDialog.Builder(context)
                        .setTitle("Success")
                        .setMessage("Congratulation ! The text is printed !")
                        .show();*/
                break;
            case AsyncThermalPosPrint.FINISH_NO_PRINTER:
                new AlertDialog.Builder(context)
                        .setTitle("No printer")
                        .setMessage("The application can't find any printer connected.")
                        .show();
                break;
            case AsyncThermalPosPrint.FINISH_PRINTER_DISCONNECTED:
                context.sendBroadcast(new Intent("ACTION_ACL_DISCONNECTED"));
                generalFunc.showGeneralMessage(
                        generalFunc.retrieveLangLBl("", "LBL_CONNECTION_ERROR_TXT"), generalFunc.retrieveLangLBl("", "LBL_CONNECTION_ERROR_BT_MSG"),
                        generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"), buttonId -> {
                            if (buttonId == 1) {
                                new ActUtils(context).startActForResult(ThermalPrintSettingActivity.class, new Bundle(), CommonUtilities.MY_THERMAL_REQ_CODE);
                            }
                        });
                //new AlertDialog.Builder(context).setTitle("Broken connection").setMessage("Unable to connect the printer.").show();
                break;
            case AsyncThermalPosPrint.FINISH_PARSER_ERROR:
                new AlertDialog.Builder(context)
                        .setTitle("Invalid formatted text")
                        .setMessage("It seems to be an invalid syntax problem.")
                        .show();
                break;
            case AsyncThermalPosPrint.FINISH_ENCODING_ERROR:
                context.sendBroadcast(new Intent("ACTION_ACL_DISCONNECTED"));
                generalFunc.showGeneralMessage(
                        generalFunc.retrieveLangLBl("Connection Error", "LBL_CONNECTION_ERROR_TXT"), generalFunc.retrieveLangLBl("", "LBL_CONNECTION_ERROR_BT_MSG"),
                        generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"), buttonId -> {
                            if (buttonId == 1) {
                                new ActUtils(context).startActForResult(ThermalPrintSettingActivity.class, new Bundle(), CommonUtilities.MY_THERMAL_REQ_CODE);
                            }
                        });
                //new AlertDialog.Builder(context).setTitle("Bad selected encoding").setMessage("The selected encoding character returning an error.").show();
                break;
            case AsyncThermalPosPrint.FINISH_BARCODE_ERROR:
                new AlertDialog.Builder(context)
                        .setTitle("Invalid barcode")
                        .setMessage("Data send to be converted to barcode or QR code seems to be invalid.")
                        .show();
                break;
        }
    }


    private void getBytes(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		Bitmap resized = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getWidth(), true);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        int x_offset = (dots_per_line[mPaperWidthType] - compressedBitmap.getWidth()) >> 1;//horizontal centre
        int y_offset = 0;
        this.publishProgress(AsyncThermalPosPrint.PROGRESS_CONNECTED);
        cmdBitmapPrint(bitmap2Binary(compressedBitmap), BITMAP_ZOOM_NONE, x_offset, y_offset, 0);

    }

    public Bitmap bitmap2Binary(Bitmap src) {
        int w, h;
        h = src.getHeight();
        w = src.getWidth();
        int[] pixels = new int[w * h];
        src.getPixels(pixels, 0, w, 0, 0, w, h);
        int alpha = 0xff << 24;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int gray = pixels[w * y + x];
                int red = ((gray & 0x00ff0000) >> 16);
                int green = ((gray & 0x0000ff00) >> 8);
                int blue = ((gray & 0x000000ff) >> 8);
                gray = (red + green + blue) / 3;
                gray = alpha | (gray << 16) | (gray << 8) | gray;
                pixels[w * y + x] = gray;
            }
        }
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, w, 0, 0, w, h);
        return result;
    }

    public void cmdBitmapPrint(Bitmap bitmap, int zoom, int left, int top, int delay) throws IOException {
        byte[] result = null;

        if (bitmap == null) {
            Log.d(TAG, "bitmap is null");
            return;
        }

        int w = bitmap.getWidth();

        if (((w + left) > dots_per_line[mPaperWidthType]) /*|| ((bitmap.getHeight() + top) > dots_per_line[mPaperWidthType]*/) {
            Log.d(TAG, "bitmap dosen't match");
            return;
        }

        //limits width
        int h = bitmap.getHeight();
        int lines = h + top;
        int bitmapSize = bmp_byte_width[mPaperWidthType];
        result = new byte[lines * bitmapSize];
        this.publishProgress(AsyncThermalPosPrint.PROGRESS_PREPARING);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int color = bitmap.getPixel(x, y);
                //int alpha=Color.alpha(color);
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                if (red < 128) {
                    int bitX = x + left;
                    int byteX = bitX >> 3;
                    int byteY = y + top;
                    result[byteY * bitmapSize + byteX] |= (0x80 >> (bitX - (byteX << 3)));
                }
            }
        }
        this.publishProgress(AsyncThermalPosPrint.PROGRESS_PRINTING);
        sendCmd(cmdGSv0pwLwHhLhHd(zoom, bitmapSize, 0, lines & 0xff, (lines >> 8) & 0xff, result), delay);

//        byte[] data=cmdGSv0pwLwHhLhHd(zoom, bmp_byte_width[mPaperWidthType], 0, lines&0xff, (lines>>8)&0xff, result);
//        write(data, data.length);
    }

    public void sendCmd(byte[] cmd, int delay) throws IOException {
        //PT486 serial print need delay
        boolean needDelay = false;
        int wait = ((delay > 0) ? (delay) : (50));


        if (cmd.length > PRINTER_BUFFER_LEN) {
            int i = 0;
            int count = (int) (cmd.length / PRINTER_BUFFER_LEN);
            int end = cmd.length - (count * PRINTER_BUFFER_LEN);
            byte[] tmp = new byte[PRINTER_BUFFER_LEN];
            byte[] last = new byte[end];
            while (count-- > 0) {
                System.arraycopy(cmd, i, tmp, 0, PRINTER_BUFFER_LEN);
                write(tmp, tmp.length);
                if (needDelay) {
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                i += PRINTER_BUFFER_LEN;
            }
            System.arraycopy(cmd, i, last, 0, end);
            write(last, last.length);
        } else {
            write(cmd, cmd.length);
        }
        this.publishProgress(AsyncThermalPosPrint.PROGRESS_PRINTED);
        Logger.d(TAG, "PROGRESS_PRINTED>>" + PROGRESS_PRINTED);
        if (needDelay) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(byte[] buffer, int dataLen) throws IOException {
           /* for (int i = 0; i < dataLen; ++i) {
                MyApp.btsocket.getOutputStream().write(buffer[i]);
            }*/
        MyApp.btsocket.getOutputStream().write(buffer);


    }

}
