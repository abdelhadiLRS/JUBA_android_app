package com.general.files;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.view.SelectableRoundedImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Admin on 15-07-2016.
 */
public class DownloadImage extends AsyncTask<Void, Void, Bitmap> {

    Bitmap myBitmap = null;
    private String url;
    private SelectableRoundedImageView imageView;

    private ImageDownloadListener imgDwnListener;

    public DownloadImage(String url, SelectableRoundedImageView imageView, ImageDownloadListener imgDwnListener) {
        this.url = url;
        this.imageView = imageView;
        this.imgDwnListener = imgDwnListener;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {

            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            myBitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result == null) {

        } else if (imageView != null) {
            imageView.setImageBitmap(result);
        }

        if (imgDwnListener != null) {
            if (result != null) {
                imgDwnListener.onSuccess(result);
            } else {
                imgDwnListener.onError();
            }
        }
    }

    public interface ImageDownloadListener {
        void onSuccess(Bitmap result);

        void onError();
    }

}
