package com.multixpro.user.trackService.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ItemTrackAnyUserBinding;
import com.utils.LoadImage;
import com.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackAnyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ItemClickListener listener;
    private final ArrayList<HashMap<String, String>> mList;
    private final String LBL_TRACK_SERVICE_PROFILE_TXT, LBL_VEHICLE_DETAILS_TXT, LBL_LIVE_TRACK_TXT,LBL_TRACK_SERVICE_ACTIVE_STATUS,LBL_TRACK_SERVICE_INACTIVE_STATUS,LBL_TRACK_SERVICE_TRACKING_STATUS_TXT;

    public TrackAnyAdapter(GeneralFunctions generalFunc, ArrayList<HashMap<String, String>> trackAnyList, ItemClickListener itemClickListener) {
        this.listener = itemClickListener;
        this.mList = trackAnyList;
        this.LBL_TRACK_SERVICE_PROFILE_TXT = generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_PROFILE_TXT");
        this.LBL_VEHICLE_DETAILS_TXT = generalFunc.retrieveLangLBl("", "LBL_VEHICLE_DETAILS_TXT");
        this.LBL_LIVE_TRACK_TXT = generalFunc.retrieveLangLBl("", "LBL_LIVE_TRACK_TXT");
        this.LBL_TRACK_SERVICE_INACTIVE_STATUS = generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_INACTIVE_STATUS");
        this.LBL_TRACK_SERVICE_ACTIVE_STATUS = generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_ACTIVE_STATUS");

        this.LBL_TRACK_SERVICE_TRACKING_STATUS_TXT = generalFunc.retrieveLangLBl("", "LBL_TRACK_SERVICE_TRACKING_STATUS_TXT");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(ItemTrackAnyUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        HashMap<String, String> item = mList.get(position);

        holder.binding.txtUserName.setText(item.get("userName"));
        holder.binding.txtStatus.setText(item.get("TripStatusDisplay"));
        String vUserImage = item.get("vImage");
        if (!Utils.checkText(vUserImage)) {
            vUserImage = "Temp";
        }
        new LoadImage.builder(LoadImage.bind(vUserImage), holder.binding.imvUser).setErrorImagePath(R.mipmap.ic_no_pic_user).setPlaceholderImagePath(R.mipmap.ic_no_pic_user).build();
        holder.binding.txtLabelFeatured.setText(LBL_TRACK_SERVICE_PROFILE_TXT);
        holder.binding.imvUser.setOnClickListener(v -> listener.onProfileClick(position, item));
        holder.binding.txtPhone.setText(item.get("userPhone"));

        holder.binding.txtVehicle.setText(LBL_VEHICLE_DETAILS_TXT);
        holder.binding.txtVehicle.setOnClickListener(v -> listener.onVehicleClick(position, item));

        holder.binding.txtLiveTrack.setText(LBL_LIVE_TRACK_TXT);
        holder.binding.txtLiveTrack.setOnClickListener(v -> listener.onLiveTrackClick(position, item));
        holder.binding.dltUser.setOnClickListener(v -> listener.ondelUserclick(position, item));


        holder.binding.txtLiveTrack.setVisibility(View.VISIBLE);
        holder.binding.viewExtra.setVisibility(View.VISIBLE);
        holder.binding.txtLblStatus.setText(LBL_TRACK_SERVICE_TRACKING_STATUS_TXT);
        if (item.get("LocationTrackingStatus").equalsIgnoreCase("Yes")) {
            holder.binding.txtStatus.setText(LBL_TRACK_SERVICE_ACTIVE_STATUS);
            holder.binding.txtStatus.setTextColor(Color.parseColor( "#34C759"));

        }
        else
        {
            holder.binding.txtStatus.setText(LBL_TRACK_SERVICE_INACTIVE_STATUS);
            holder.binding.txtStatus.setTextColor(Color.parseColor( "#800000"));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface ItemClickListener {
        void onProfileClick(int position, HashMap<String, String> itemList);

        void onVehicleClick(int position, HashMap<String, String> itemList);
        void ondelUserclick(int position, HashMap<String, String> itemList);

        void onLiveTrackClick(int position, HashMap<String, String> itemList);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemTrackAnyUserBinding binding;

        private ViewHolder(ItemTrackAnyUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}