package com.multixpro.user.rideSharing.fragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.dialogs.OpenListView;
import com.fragments.BaseFragment;
import com.general.DatePicker;
import com.general.files.DecimalDigitsInputFilter;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.R;
import com.multixpro.user.databinding.FragmentRidePublishStep2Binding;
import com.multixpro.user.rideSharing.RidePublish;
import com.utils.CommonUtilities;
import com.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RidePublishStep2Fragment extends BaseFragment {

    private FragmentRidePublishStep2Binding binding;
    @Nullable
    private RidePublish mActivity;
    private final Calendar dateTimeCalender = Calendar.getInstance(Locale.getDefault());
    private final Calendar maxDateCalender = Calendar.getInstance(Locale.getDefault());
    private int selCurrentPosition = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_publish_step_2, container, false);

        maxDateCalender.set(Calendar.MONTH, dateTimeCalender.get(Calendar.MONTH) + 2);

        setLabels();
        setData();

        assert mActivity != null;
        if (mActivity.generalFunc.isRTLmode()) {
            binding.currencyTxt.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.right_radius_rtl));
        }

        return binding.getRoot();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setLabels() {
        binding.recommendedPriceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        binding.recommendedPriceEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        if (mActivity != null) {
            binding.dateTimeHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_DATE_TIME_TXT") + " *");
            binding.dateTimeEditBox.setHint(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_ADD_DATE_TIME_TXT"));
            binding.pricePerSeatHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PASSENGER_AVAILABLE_SEAT"));
            binding.recommendedPriceHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PRICE_PER_SEAT_TXT"));

            binding.pricePerSeatTxt.setHint(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_AND_SELECT"));
            binding.recommendedPriceEditText.setHint(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_ENATER_AMOUNT"));
            binding.currencyTxt.setText(mActivity.generalFunc.getJsonValueStr("vCurrencyPassenger", mActivity.obj_userProfile));

            if (mActivity.indexView != null) {
                binding.recommendedHTxt.setVisibility(View.GONE);
                binding.recommendedPriceVTxt.setVisibility(View.GONE);
            }
        }

        addToClickHandler(binding.pricePerSeatTxt);
        addToClickHandler(binding.dateTimeEditBox);
    }

    private void setData() {
        if (mActivity != null && mActivity.myRideDataHashMap != null) {
            binding.dateTimeEditBox.setText(mActivity.mPublishData.getDateTime());
            binding.pricePerSeatTxt.setText(mActivity.mPublishData.getPerSeat());
            binding.recommendedPriceEditText.setText(mActivity.mPublishData.getRecommendedPrice());

            binding.recommendedHTxt.setText(mActivity.mPublishData.getRecommdedPriceText());
            binding.recommendedPriceVTxt.setText(mActivity.mPublishData.getRecommdedPriceRange());

            binding.dateTimeEditBox.setClickable(false);
            binding.pricePerSeatTxt.setClickable(false);
            Utils.removeInput(binding.recommendedPriceEditText);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApp.getInstance().getCurrentAct() instanceof RidePublish) {
            mActivity = (RidePublish) requireActivity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActivity != null) {
            binding.selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PRICE_AVAILABILITY_TITLE"));
            if (mActivity != null) {
                binding.recommendedPriceEditText.setText(mActivity.mPublishData.getRecommendedPrice());
                binding.recommendedHTxt.setText(mActivity.mPublishData.getRecommdedPriceText());
                binding.recommendedPriceVTxt.setText(mActivity.mPublishData.getRecommdedPriceRange());
            }
        }
    }

    public void onClickView(View view) {
        Utils.hideKeyboard(getActivity());
        int i = view.getId();
        if (i == binding.dateTimeEditBox.getId()) {
            if (mActivity != null) {
                DatePicker.show(mActivity, mActivity.generalFunc, Calendar.getInstance(), maxDateCalender,
                        Utils.convertDateToFormat(CommonUtilities.DayFormatEN, dateTimeCalender.getTime()), null, (year, monthOfYear, dayOfMonth) -> {

                            dateTimeCalender.set(Calendar.YEAR, year);
                            dateTimeCalender.set(Calendar.MONTH, monthOfYear - 1);
                            dateTimeCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            new TimePickerDialog(mActivity, R.style.calender_dialog_theme, (timePicker, selectedHour, selectedMinute) -> {

                                dateTimeCalender.set(Calendar.HOUR_OF_DAY, selectedHour);
                                dateTimeCalender.set(Calendar.MINUTE, selectedMinute);
                                dateTimeCalender.set(Calendar.SECOND, 0);
                                dateTimeCalender.set(Calendar.MILLISECOND, 0);
                                if (Calendar.getInstance().getTimeInMillis() <= dateTimeCalender.getTimeInMillis()) {
                                    binding.dateTimeEditBox.setText(Utils.convertDateToFormat(CommonUtilities.DayTimeFormat, dateTimeCalender.getTime()));

                                } else {
                                    mActivity.generalFunc.showMessage(binding.selectServiceTxt, mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_INVALID_PUBLISH_TIME_MSG"));

                                }

                            }, dateTimeCalender.get(Calendar.HOUR_OF_DAY), dateTimeCalender.get(Calendar.MINUTE), false).show();
                        });
            }
        } else if (i == binding.pricePerSeatTxt.getId()) {
            assert mActivity != null;

            ArrayList<String> arrayList = new ArrayList<>();
            JSONArray arr_msg = mActivity.generalFunc.getJsonArray(mActivity.mPublishData.getPassengerNo());
            if (arr_msg != null) {
                for (int j = 0; j < arr_msg.length(); j++) {
                    Object value = mActivity.generalFunc.getJsonValue(arr_msg, j);
                    if (value.toString().equalsIgnoreCase(binding.pricePerSeatTxt.getText().toString())) {
                        selCurrentPosition = j;
                    }
                    arrayList.add("" + mActivity.generalFunc.getJsonValue(arr_msg, j));
                }
            }

            OpenListView.getInstance(mActivity, mActivity.generalFunc.retrieveLangLBl("", "LBL_RIDE_SHARE_PASSENGER_AVAILABLE_SEAT"), arrayList, OpenListView.OpenDirection.CENTER, true, true, position -> {
                selCurrentPosition = position;
                binding.pricePerSeatTxt.setText(arrayList.get(position));
            }).show(selCurrentPosition, "vTitle");
        }
    }

    public void checkPageNext() {
        if (mActivity != null) {
            boolean isPerSeat = GeneralFunctions.parseIntegerValue(0, binding.pricePerSeatTxt.getText().toString()) >= 1;
            boolean isRPrice = GeneralFunctions.parseDoubleValue(0.00, binding.recommendedPriceEditText.getText().toString()) >= 1.00;
            String dateTime = Utils.convertDateToFormat("yyyy-MM-ddhh:mm aa", dateTimeCalender.getTime());
            if (Utils.checkText(binding.dateTimeEditBox.getText().toString()) && Utils.checkText(dateTime) && isPerSeat && isRPrice) {
                mActivity.mPublishData.setDateTime(dateTime);
                mActivity.mPublishData.setPerSeat(binding.pricePerSeatTxt.getText().toString());
                mActivity.mPublishData.setRecommendedPrice(binding.recommendedPriceEditText.getText().toString());
                mActivity.setPageNext();
            } else {
                mActivity.generalFunc.showMessage(binding.selectServiceTxt, mActivity.generalFunc.retrieveLangLBl("", "LBL_ENTER_REQUIRED_FIELDS"));
            }
        }
    }
}