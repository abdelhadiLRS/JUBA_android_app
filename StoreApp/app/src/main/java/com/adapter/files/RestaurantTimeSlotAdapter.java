package com.adapter.files;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.multixpro.store.R;
import com.datepicker.files.SlideDateTimeListener;
import com.datepicker.files.SlideDateTimePicker;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RestaurantTimeSlotAdapter extends RecyclerView.Adapter<RestaurantTimeSlotAdapter.ViewHolder> {

    Context mContext;
    View view;
    settTimeSlotClickList setTimeSlotClickList;
    ArrayList<HashMap<String, String>> timeSlotsList;
    GeneralFunctions generalFunc;
    FragmentManager fragmentManager;

    public RestaurantTimeSlotAdapter(Context context, ArrayList<HashMap<String, String>> timeSlotsList, GeneralFunctions generalFunc, FragmentManager fragmentManager) {
        this.mContext = context;
        this.timeSlotsList = timeSlotsList;
        this.generalFunc = generalFunc;
        this.fragmentManager = fragmentManager;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mContext).inflate(R.layout.item_restaurant_time_slot_cell, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        HashMap<String, String> timeSlotsData = timeSlotsList.get(position);

        if (timeSlotsData.get("isMandatory").equalsIgnoreCase("Yes")) {
            holder.tv_mandatory.setVisibility(View.VISIBLE);
        }

        /*String SlotName =timeSlotsData.get("SlotName");
        if (Utils.checkText(SlotName)) {
            holder.slotName.setVisibility(View.VISIBLE);
            holder.slotName.setText(SlotName);

        }*/

        holder.SlotDayNameTxtView.setText(timeSlotsData.get("dayname"));
        holder.fromtimeSlotVTxt.setText( timeSlotsData.get("FromSlot"));
        holder.totimeSlotVTxt.setText(timeSlotsData.get("ToSlot"));

        holder.fromSLotArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectTimeSlot(holder, true,position);
            }
        });

        holder.toSLotArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeSlot(holder, false, position);

            }
        });

        holder.iv_clearSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeSlotsList.get(position).put("FromSlot","00:00");
                timeSlotsList.get(position).put("ToSlot","00:00");
                notifyDataSetChanged();

            }
        });


    }

    public void selectTimeSlot(ViewHolder holder, boolean isFromSlot, int position) {

        if (isFromSlot == false && GeneralFunctions.parseIntegerValue(0, Utils.getText(holder.fromtimeSlotVTxt).replace(":", "")) < 1) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_FROM_TIME"));
            return;
        }

        MTextView selectedTextView = isFromSlot ? holder.fromtimeSlotVTxt : holder.totimeSlotVTxt;

        new SlideDateTimePicker.Builder(fragmentManager)
                .setListener(new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date) {
                        String selectedTime = Utils.convertDateToFormat("HH:mm", date);

                        boolean isSetTime = true;

//                        int fromTimeSlot= GeneralFunctions.parseIntegerValue(0, Utils.getText(holder.fromtimeSlotVTxt).replace(":", ""));
//                        int toTimeSlot= GeneralFunctions.parseIntegerValue(0, Utils.getText(holder.totimeSlotVTxt).replace(":", ""));
//                        int selectedTimeSlot= GeneralFunctions.parseIntegerValue(0, selectedTime.replace(":", ""));

//                        if (isFromSlot==false)
//                        {
//                             if (selectedTimeSlot < fromTimeSlot ) {
//                                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SLT_2_FRM_RESTRICT"));
//                                isSetTime = false;
//                            }
//
//
//                        }
//                        else if (isFromSlot && selectedTimeSlot>toTimeSlot && GeneralFunctions.parseIntegerValue(0, Utils.getText(holder.totimeSlotVTxt).replace(":", "")) > 0) {
//                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_SLT_2_TO_RESTRICT"));
//                            isSetTime = false;
//                        }


                        if (isSetTime) {
                            selectedTextView.setText(selectedTime);

                            timeSlotsList.get(position).put(isFromSlot?"FromSlot":"ToSlot",selectedTime);
                          /*  isSelectedPos = position;

                            if (setTimeSlotClickList != null) {
                                setTimeSlotClickList.itemTimeSlotLocClick(position);
                            }*/

                          notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onDateTimeCancel() {

                    }

                })
                .setDatePickerEnabled(false)
                .setTimePickerEnabled(true)
                .setPreSetTimeEnabled( Utils.checkText(selectedTextView.toString()) && !selectedTextView.getText().toString().equalsIgnoreCase("00:00") ? true : false)
                .setPreSelectedTime(Utils.checkText(selectedTextView.toString()) && !selectedTextView.getText().toString().equalsIgnoreCase("00:00") ? selectedTextView.getText().toString() : "")
                .setInitialDate(new Date())
                .setMaxDate(new Date())
                .setIs24HourTime(false)
                .setIndicatorColor(mContext.getResources().getColor(R.color.appThemeColor_2))
                .build()
                .show();
    }

    @Override
    public int getItemCount() {
        //  return recentList.size();
        return timeSlotsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnClickList(settTimeSlotClickList settTimeSlotClickList) {
        this.setTimeSlotClickList = settTimeSlotClickList;
    }

    public interface settTimeSlotClickList {
        void itemTimeSlotLocClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView toSLotArea,fromSLotArea;
        MTextView slotName, SlotDayNameTxtView, fromtimeSlotVTxt, toTxtView, totimeSlotVTxt, tv_mandatory;
        CardView slotsArea;
        ImageView iv_clearSlots;
        LinearLayout containViewSlotOne,slotFromCalenderArea,slotToCalenderArea;


        public ViewHolder(View itemView) {
            super(itemView);

            slotName = (MTextView) itemView.findViewById(R.id.slotName);
            SlotDayNameTxtView = (MTextView) itemView.findViewById(R.id.SlotDayNameTxtView);
            fromtimeSlotVTxt = (MTextView) itemView.findViewById(R.id.fromtimeSlotVTxt);
            toTxtView = (MTextView) itemView.findViewById(R.id.toTxtView);
            totimeSlotVTxt = (MTextView) itemView.findViewById(R.id.totimeSlotVTxt);
            tv_mandatory = (MTextView) itemView.findViewById(R.id.tv_mandatory);
            slotsArea = (CardView) itemView.findViewById(R.id.slotsArea);
            containViewSlotOne = (LinearLayout) itemView.findViewById(R.id.containViewSlotOne);
            slotFromCalenderArea = (LinearLayout) itemView.findViewById(R.id.slotFromCalenderArea);
            toSLotArea = (CardView) itemView.findViewById(R.id.toSLotArea);
            fromSLotArea = (CardView) itemView.findViewById(R.id.fromSLotArea);
            slotToCalenderArea = (LinearLayout) itemView.findViewById(R.id.slotToCalenderArea);
            iv_clearSlots = (ImageView) itemView.findViewById(R.id.iv_clearSlots);

        }
    }


}

