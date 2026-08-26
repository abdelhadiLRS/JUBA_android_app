package com.dialogs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import com.multixpro.user.R;
import com.general.call.CommunicationManager;
import com.general.call.MediaDataProvider;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.CommonUtilities;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.MTextView;

import java.util.HashMap;

public class OpenTutorDetailDialog {

    Context mContext;
    HashMap<String, String> data_trip;
    GeneralFunctions generalFunc;
    String vName = "";
    String vImage = "";

    androidx.appcompat.app.AlertDialog alertDialog;

    public OpenTutorDetailDialog(Context mContext, HashMap<String, String> data_trip, GeneralFunctions generalFunc) {
        this.mContext = mContext;
        this.data_trip = data_trip;
        this.generalFunc = generalFunc;
        show();
    }

    public void show() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.design_tutor_detail_dialog, null);
        builder.setView(dialogView);

        ((MTextView) dialogView.findViewById(R.id.rateTxt)).setText(data_trip.get("driverRating"));
        ((MTextView) dialogView.findViewById(R.id.nameTxt)).setText(data_trip.get("driverName"));
        vName = data_trip.get("driverName");

        ((MTextView) dialogView.findViewById(R.id.tutorDTxt)).setText(generalFunc.retrieveLangLBl("Tutor Detail", "LBL_DRIVER_DETAIL"));
        ((MTextView) dialogView.findViewById(R.id.callTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CALL_TXT"));
        ((MTextView) dialogView.findViewById(R.id.msgTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_MESSAGE_TXT"));


        String image_url = CommonUtilities.PROVIDER_PHOTO_PATH + data_trip.get("iDriverId") + "/"
                + data_trip.get("driverImage");

        if (!data_trip.get("driverImage").equals("")) {
            vImage = data_trip.get("driverImage");

        }

        new LoadImage.builder(LoadImage.bind(image_url), dialogView.findViewById(R.id.tutorImgView)).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();

        (dialogView.findViewById(R.id.callArea)).setOnClickListener(view -> {

            MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                    .setCallId(data_trip.get("iDriverId"))
                    .setPhoneNumber("+" + data_trip.get("vCode") + "" + data_trip.get("driverMobile"))
                    .setToMemberType(Utils.CALLTODRIVER)
                    .setToMemberName(vName)
                    .setToMemberImage(vImage)
                    .setMedia(CommunicationManager.MEDIA_TYPE)
                    .setTripId(data_trip.get("iTripId"))
                    .build();
            CommunicationManager.getInstance().communicate(MyApp.getInstance().getCurrentAct(), mDataProvider, CommunicationManager.TYPE.OTHER);
        });


        (dialogView.findViewById(R.id.msgArea)).setOnClickListener(view -> {
            MediaDataProvider mDataProvider1 = new MediaDataProvider.Builder()
                    .setPhoneNumber(data_trip.get("vCode") + "" + data_trip.get("driverMobile"))
                    .setMedia(CommunicationManager.MEDIA.DEFAULT).build();
            CommunicationManager.getInstance().communicate(MyApp.getInstance().getCurrentAct(), mDataProvider1, CommunicationManager.TYPE.CHAT);
        });

        (dialogView.findViewById(R.id.closeImg)).setOnClickListener(view -> {

            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        });


        alertDialog = builder.create();
        if (generalFunc.isRTLmode()) {
            generalFunc.forceRTLIfSupported(alertDialog);
        }
        alertDialog.show();
    }

    public void call(String phoneNumber) {
        try {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            mContext.startActivity(callIntent);

        } catch (Exception e) {
        }


    }

}
