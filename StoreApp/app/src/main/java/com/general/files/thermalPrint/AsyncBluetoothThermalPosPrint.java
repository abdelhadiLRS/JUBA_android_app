package com.general.files.thermalPrint;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;

import com.general.files.GeneralFunctions;

public class AsyncBluetoothThermalPosPrint extends AsyncThermalPosPrint {
    public AsyncBluetoothThermalPosPrint(Context context, Dialog myProgressDialog, GeneralFunctions generalFunc, Bitmap bitmap) {
        super(context,myProgressDialog,generalFunc,bitmap);
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        this.publishProgress(AsyncThermalPosPrint.PROGRESS_PROCESSING);
        return super.doInBackground();
    }
}
