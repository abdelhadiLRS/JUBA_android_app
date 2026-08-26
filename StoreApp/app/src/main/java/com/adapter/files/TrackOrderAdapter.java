package com.adapter.files;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fontanalyzer.SystemFont;
import com.multixpro.store.R;
import com.general.call.CommunicationManager;
import com.general.call.MediaDataProvider;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.TimelineView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 01/05/18.
 */

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.ViewHolder> {

    Context mContext;
    ArrayList<HashMap<String, String>> listData;
    GeneralFunctions generalFunctions;

    String userprofileJson = "";


    public TrackOrderAdapter(Context mContext, ArrayList<HashMap<String, String>> listData) {
        this.mContext = mContext;
        this.listData = listData;
        generalFunctions = MyApp.getInstance().getGeneralFun(mContext);
        userprofileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);
    }

    @Override
    public TrackOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_track_item_design, parent, false);

        TrackOrderAdapter.ViewHolder viewHolder = new TrackOrderAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrackOrderAdapter.ViewHolder holder, int position) {

        HashMap<String, String> mapData = listData.get(position);

        holder.contentTxtView.setText(mapData.get("vStatus"));

        int color=Color.parseColor("#FFFFFF");
        new CreateRoundedView(color, Utils.dipToPixels(mContext, 5), 0, color, holder.containerView);

        String eShowCallImg=mapData.get("eShowCallImg");
        if (eShowCallImg != null && eShowCallImg.equalsIgnoreCase("Yes")) {
            holder.callImgView.setVisibility(View.VISIBLE);
        } else {
            holder.callImgView.setVisibility(View.GONE);
        }

        holder.callImgView.setOnClickListener(v -> {
            MediaDataProvider mDataProvider = new MediaDataProvider.Builder()
                    .setCallId(mapData.get("iDriverId"))
                    .setPhoneNumber(mapData.get("DriverPhone"))
                    .setToMemberType(Utils.CALLTODRIVER)
                    .setToMemberName(mapData.get("driverName"))
                    .setToMemberImage(mapData.get("driverImage"))
                    .setMedia(CommunicationManager.MEDIA_TYPE)
                    .build();
            CommunicationManager.getInstance().toCall(MyApp.getInstance().getCurrentAct(), mDataProvider);
        });
        holder.contentTxtView.setTypeface(SystemFont.FontStyle.LIGHT.font);
        if (position == listData.size() - 1) {
            holder.contentTxtView.setTypeface(SystemFont.FontStyle.SEMI_BOLD.font);

        }


        holder.mTimelineView.initLine(TimelineView.LineType.NORMAL);

        holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_check_mark_button));


        if (listData.size() == 1) {
            holder.mTimelineView.initLine(TimelineView.LineType.ONLYONE);

            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_check_mark_button));

        } else if (listData.size() > 1) {

            if (position == 0) {
                holder.mTimelineView.initLine(TimelineView.LineType.BEGIN);

                holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_check_mark_button));

            } else {
                holder.mTimelineView.initLine(TimelineView.LineType.NORMAL);

                holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_check_mark_button));
            }

            if (position == listData.size() - 1) {
                holder.mTimelineView.initLine(TimelineView.LineType.END);

                holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_brightness_1_black_24dp));

            }

        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public LinearLayout containerView;
        public MTextView contentTxtView;
        public ImageView callImgView;
        public TimelineView mTimelineView;

        public ViewHolder(View view) {
            super(view);

            contentTxtView = (MTextView) view.findViewById(R.id.contentTxtView);
            containerView = (LinearLayout) view.findViewById(R.id.containerView);
            callImgView = (ImageView) view.findViewById(R.id.callImgView);
            mTimelineView = (TimelineView) view.findViewById(R.id.time_marker);

        }
    }
}
