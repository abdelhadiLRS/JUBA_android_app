package com.multixpro.user.rentItem.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datepicker.files.SlideDateTimeListener;
import com.datepicker.files.SlideDateTimePicker;
import com.general.files.GeneralFunctions;
import com.multixpro.user.R;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RentPickupTimeSlotAdapter extends RecyclerView.Adapter<RentPickupTimeSlotAdapter.ViewHolder> {

    private final GeneralFunctions generalFunc;
    private final FragmentManager fragmentManager;
    private final ArrayList<HashMap<String, String>> timeSlotsList;

    public RentPickupTimeSlotAdapter(GeneralFunctions generalFunc, FragmentManager fragmentManager, ArrayList<HashMap<String, String>> timeSlotsList) {
        this.generalFunc = generalFunc;
        this.fragmentManager = fragmentManager;
        this.timeSlotsList = timeSlotsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rent_pickup_time_slot, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        HashMap<String, String> timeSlotsData = timeSlotsList.get(position);

        holder.SlotDayNameTxtView.setText(timeSlotsData.get("dayname"));
        holder.fromTimeSlotVTxt.setText(timeSlotsData.get("FromSlot"));
        holder.toTimeSlotVTxt.setText(timeSlotsData.get("ToSlot"));

        holder.fromSLotArea.setOnClickListener(v -> selectTimeSlot(holder, true, position));
        holder.toSLotArea.setOnClickListener(v -> selectTimeSlot(holder, false, position));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectTimeSlot(ViewHolder holder, boolean isFromSlot, int position) {

        String fromTime = getMyTime("", Utils.getText(holder.fromTimeSlotVTxt));
        String toTime = getMyTime("", Utils.getText(holder.toTimeSlotVTxt));

        if (!isFromSlot && GeneralFunctions.parseIntegerValue(-1, fromTime.replace(":", "")) < 0) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_FROM_TIME"));
            return;
        }

        MTextView selectedTextView = isFromSlot ? holder.fromTimeSlotVTxt : holder.toTimeSlotVTxt;

        String preSelectedTime = getMyTime(null, Utils.getText(selectedTextView));

        new SlideDateTimePicker.Builder(fragmentManager)
                .setListener(new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date) {
                        int selectedTimeLong = GeneralFunctions.parseIntegerValue(0, Utils.convertDateToFormat("HH:mm", date).replace(":", ""));

                        if (!isFromSlot && GeneralFunctions.parseIntegerValue(0, fromTime.replace(":", "")) > selectedTimeLong) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_RENT_TO_GREATER_FROM_TXT"));
                            return;
                        } else if (isFromSlot && GeneralFunctions.parseIntegerValue(0, fromTime.replace(":", "")) > 1 && Utils.checkText(toTime)) {
                            if (GeneralFunctions.parseIntegerValue(0, toTime.replace(":", "")) < selectedTimeLong) {
                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_RENT_TO_GREATER_FROM_TXT"));
                                return;
                            }
                        }
                        String selectedTime = Utils.convertDateToFormat(CommonUtilities.OriginalTimeFormate, date);
                        selectedTextView.setText(selectedTime);
                        timeSlotsList.get(position).put(isFromSlot ? "FromSlot" : "ToSlot", selectedTime);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onDateTimeCancel() {

                    }
                })
                .setDatePickerEnabled(false)
                .setTimePickerEnabled(true)
                .setPreSetTimeEnabled(Utils.checkText(selectedTextView.toString()) && !selectedTextView.getText().toString().equalsIgnoreCase("00:00"))
                .setPreSelectedTime(preSelectedTime)
                .setInitialDate(new Date())
                .setMaxDate(new Date())
                .setIs24HourTime(false)
                .setIndicatorColor(holder.itemView.getContext().getResources().getColor(R.color.appThemeColor_2))
                .build()
                .show();
    }

    public String getMyTime(String time, String textDate) {
        try {
            Date calStart = new SimpleDateFormat(CommonUtilities.OriginalTimeFormate).parse(textDate);
            time = Utils.convertDateToFormat("HH:mm", calStart);
        } catch (ParseException e) {
            e.printStackTrace();
            if (time == null) {
                time = Utils.convertDateToFormat("HH:mm", Calendar.getInstance(Locale.getDefault()).getTime());
            }
        }
        return time;
    }

    @Override
    public int getItemCount() {
        return timeSlotsList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView toSLotArea, fromSLotArea;
        private final MTextView SlotDayNameTxtView, fromTimeSlotVTxt, toTimeSlotVTxt;

        public ViewHolder(View itemView) {
            super(itemView);

            SlotDayNameTxtView = (MTextView) itemView.findViewById(R.id.SlotDayNameTxtView);
            fromTimeSlotVTxt = (MTextView) itemView.findViewById(R.id.fromTimeSlotVTxt);
            toTimeSlotVTxt = (MTextView) itemView.findViewById(R.id.toTimeSlotVTxt);
            toSLotArea = (CardView) itemView.findViewById(R.id.toSLotArea);
            fromSLotArea = (CardView) itemView.findViewById(R.id.fromSLotArea);
        }
    }
}