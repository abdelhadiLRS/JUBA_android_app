package com.multixpro.user.rentItem.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.user.R;
import com.multixpro.user.databinding.ItemRentItemPaymentPlanBinding;
import com.multixpro.user.rentItem.RentItemNewPostActivity;
import com.multixpro.user.rentItem.adapter.RentGalleryImagesAdapter;
import com.multixpro.user.rentItem.model.RentItemData;
import com.map.GeoMapLoader;
import com.map.models.LatLng;
import com.map.models.MarkerOptions;
import com.utils.MyUtils;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RentItemReviewAllDetailsFragment extends Fragment implements GeoMapLoader.OnMapReadyCallback, RentGalleryImagesAdapter.OnItemClickListener {

    @Nullable
    private RentItemNewPostActivity mActivity;
    private GeoMapLoader.GeoMap geoMap;
    private MTextView txtPostTitle, txtPostPrice, txtPostDurationStatus, txtPostDescription, locationTxt, CategoryHTxt, selectedcategoryHTxt;
    private LinearLayout pickupTimeSlotContainer, rentDetailsArea, rentItemDetailsContainer, rentSelectedPlanArea, rentSelectedPlanContainer;
    private RelativeLayout photosArea;
    private final ArrayList<HashMap<String, String>> mediaList = new ArrayList<>();
    private AlertDialog list_navigation;
    private RecyclerView rvRentPostImages;
    private RentGalleryImagesAdapter mAdapter;

    private Location userLocation;
    private LinearLayout cardPostDescription, pickupAvailibilityArea;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rent_item_review_all_details, container, false);
        assert mActivity != null;

        txtPostTitle = view.findViewById(R.id.txtPostTitle);
        txtPostPrice = view.findViewById(R.id.txtPostPrice);
        txtPostDurationStatus = view.findViewById(R.id.txtPostDurationStatus);
        pickupAvailibilityArea = view.findViewById(R.id.pickupAvailibilityArea);

        cardPostDescription = view.findViewById(R.id.cardPostDescription);
        MTextView postDescriptionHTxt = view.findViewById(R.id.postDescriptionHTxt);
        postDescriptionHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_DESCRIPTION_TXT"));
        txtPostDescription = view.findViewById(R.id.txtPostDescription);

        CategoryHTxt = view.findViewById(R.id.CategoryHTxt);
        selectedcategoryHTxt = view.findViewById(R.id.selectedcategoryHTxt);

        ImageView MapBtnImgView = view.findViewById(R.id.MapBtnImgView);
        ImageView DirectionBtnImgView = view.findViewById(R.id.DirectionBtnImgView);
        MapBtnImgView.setOnClickListener(view1 -> openNavigationDialog(false));
        DirectionBtnImgView.setOnClickListener(view12 -> openNavigationDialog(true));

        MTextView pickUpAvailabilityHTxt = view.findViewById(R.id.pickUpAvailabilityHTxt);
        if (mActivity.eType.equalsIgnoreCase("RentEstate") || mActivity.eType.equalsIgnoreCase("RentCars")) {
            pickUpAvailabilityHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ESTATE_AVAILABILTY_TXT"));
        } else {
            pickUpAvailabilityHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ITEM_PICKUP_AVAILBILITY"));
        }
        pickupTimeSlotContainer = view.findViewById(R.id.pickupTimeSlotContainer);

        MTextView detailsHTxt = view.findViewById(R.id.detailsHTxt);
        if (mActivity.eType.equalsIgnoreCase("RentEstate")) {
            detailsHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_PROPERTY_DETAIL"));
        } else if (mActivity.eType.equalsIgnoreCase("RentCars")) {
            detailsHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_CAR_DETAILS"));
        } else {
            detailsHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ITEM_DETAILS"));
        }
        rentDetailsArea = view.findViewById(R.id.rentDetailsArea);
        rentItemDetailsContainer = view.findViewById(R.id.rentItemDetailsContainer);

        MTextView photosHTxt = view.findViewById(R.id.photosHTxt);
        photosHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ITEM_PHOTOS"));
        photosArea = view.findViewById(R.id.photosArea);
        photosArea.setVisibility(GONE);

        MTextView locationHTxt = view.findViewById(R.id.locationHTxt);
        locationHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_LOCATION_DETAILS"));
        locationTxt = view.findViewById(R.id.locationTxt);

        rentSelectedPlanArea = view.findViewById(R.id.rentSelectedPlanArea);
        MTextView planHTxt = view.findViewById(R.id.planHTxt);
        planHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_SELECTED_PAYMENT_PLAN"));
        rentSelectedPlanContainer = view.findViewById(R.id.rentSelectedPlanContainer);

        rvRentPostImages = view.findViewById(R.id.rvRentPostImages_review);
        if (MyApp.getInstance().getCurrentAct() instanceof RentItemNewPostActivity) {
            mAdapter = new RentGalleryImagesAdapter(requireActivity(), mediaList, mActivity.generalFunc, false);
            mAdapter.setOnItemClickListener(this);
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApp.getInstance().getCurrentAct() instanceof RentItemNewPostActivity) {
            mActivity = (RentItemNewPostActivity) requireActivity();

            mAdapter = new RentGalleryImagesAdapter(requireActivity(), mediaList, mActivity.generalFunc, false);
            mAdapter.setOnItemClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActivity != null) {
            if (mActivity.eType.equalsIgnoreCase("RentEstate")) {
                mActivity.selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ESTATE_PROPERTY_REVIEW_DETAILS"));
            } else if (mActivity.eType.equalsIgnoreCase("RentCars")) {
                mActivity.selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_CAR_REVIEWS_DETAILS"));
            } else {
                mActivity.selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ITEM_REVIEW_DETAILS"));
            }
            (new GeoMapLoader(mActivity, R.id.mapRentContainer)).bindMap(this);
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GeoMapLoader.GeoMap googleMap) {
        assert mActivity != null;
        this.geoMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        if (mActivity.generalFunc.checkLocationPermission(true)) {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        googleMap.setOnMarkerClickListener(marker -> {
            marker.hideInfoWindow();
            return true;
        });

        RentItemData.LocationDetails locationDetails = mActivity.mRentItemData.getLocationDetails();
        if (locationDetails != null) {
            userLocation = new Location("source");
            userLocation.setLatitude(GeneralFunctions.parseDoubleValue(0.0, locationDetails.getvLatitude()));
            userLocation.setLongitude(GeneralFunctions.parseDoubleValue(0.0, locationDetails.getvLongitude()));

            //locationTxt.setText(locationDetails.getvLocation());
            locationTxt.setText(locationDetails.getvAddress());

            LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            googleMap.moveCamera(latLng);
            googleMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    @SuppressLint("SetTextI18n")
    public void setData(@NonNull GeneralFunctions generalFunc, @NonNull String responseString) {
        assert mActivity != null;
        // pickup Time Slot Data
        pickupTimeSlotFieldRow(generalFunc, responseString);

        txtPostPrice.setVisibility(Utils.checkText(generalFunc.getJsonValue("fAmount", responseString)) ? VISIBLE : GONE);
        txtPostPrice.setText(generalFunc.getJsonValue("fAmount", responseString));

        txtPostDurationStatus.setVisibility(Utils.checkText(generalFunc.getJsonValue("eRentItemDuration", responseString)) ? VISIBLE : GONE);
        txtPostDurationStatus.setText("/ " + generalFunc.getJsonValue("eRentItemDuration", responseString));

        selectedcategoryHTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_SELECTED_CATEGORY"));
        if (Utils.checkText(generalFunc.getJsonValue("vSubCatName", responseString))) {
            CategoryHTxt.setText(generalFunc.getJsonValue("vCatName", responseString) + " (" + generalFunc.getJsonValue("vSubCatName", responseString) + ")");
        } else {
            CategoryHTxt.setText(generalFunc.getJsonValue("vCatName", responseString));
        }

        // rentItem Field Data set
        rentItemFieldRow(generalFunc, responseString);

        // Images data set
        JSONArray arr_data = generalFunc.getJsonArray("Images", responseString);
        mediaList.clear();
        if (arr_data != null && arr_data.length() > 0) {
            photosArea.setVisibility(VISIBLE);

            for (int i = 0; i < arr_data.length(); i++) {
                JSONObject obj_tmp = generalFunc.getJsonObject(arr_data, i);
                HashMap<String, String> mapData = new HashMap<>();
                MyUtils.createHashMap(generalFunc, mapData, obj_tmp);
                mapData.put("isDelete", "No");
                mediaList.add(mapData);
            }
            rvRentPostImages.setAdapter(mAdapter);
        } else {
            photosArea.setVisibility(GONE);
        }

        // Location data set
        userLocation = new Location("source");
        userLocation.setLatitude(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("vLatitude", responseString)));
        userLocation.setLongitude(GeneralFunctions.parseDoubleValue(0.0, generalFunc.getJsonValue("vLongitude", responseString)));
        if (geoMap != null) {
            LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            geoMap.moveCamera(latLng);
            geoMap.addMarker(new MarkerOptions().position(latLng));
        }
        //locationTxt.setText(generalFunc.getJsonValue("vLocation", responseString));
        locationTxt.setText(generalFunc.getJsonValue("vAddress", responseString));
        locationTxt.setOnClickListener(view -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", Utils.getText(locationTxt));
            clipboard.setPrimaryClip(clip);
            generalFunc.showMessage(generalFunc.getCurrentView(mActivity), generalFunc.retrieveLangLBl("Address is copied to clipboard", "LBL_RENT_ADDRESS_COPY_CLIPBOARD"));
        });
        if (mActivity != null) {
            mActivity.setPagerHeight();
        }

        // selected Plan
        rentSelectedPlanArea.setVisibility(GONE);
        if (mActivity.mRentEditHashMap != null) {
            rentSelectedPlanArea.setVisibility(VISIBLE);
            if (rentSelectedPlanContainer.getChildCount() > 0) {
                rentSelectedPlanContainer.removeAllViewsInLayout();
            }
            LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @NonNull ItemRentItemPaymentPlanBinding binding = ItemRentItemPaymentPlanBinding.inflate(inflater, rentSelectedPlanContainer, false);

            binding.mainArea.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.appThemeColor_1)));

            binding.check.setVisibility(GONE);
            binding.unCheck.setVisibility(GONE);
            binding.txtPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            binding.txtPlanName.setTextColor(ContextCompat.getColor(mActivity, R.color.white));

            JSONObject planData = generalFunc.getJsonObject(mActivity.mRentEditHashMap.get("RentItemPlanData"));
            String fAmount = generalFunc.getJsonValueStr("fAmount", planData);
            if (Utils.checkText(fAmount)) {
                binding.txtPrice.setVisibility(VISIBLE);
                binding.txtPrice.setText(fAmount);
            } else {
                binding.txtPrice.setVisibility(GONE);
            }
            binding.txtPlanName.setText(generalFunc.getJsonValueStr("vPlanName", planData));

            rentSelectedPlanContainer.addView(binding.getRoot());
        }
    }

    private void pickupTimeSlotFieldRow(@NonNull GeneralFunctions generalFunc, @NonNull String responseString) {

        JSONArray fieldArray = generalFunc.getJsonArray("timeslot", responseString);
        if (fieldArray != null) {
            if (pickupTimeSlotContainer.getChildCount() > 0) {
                pickupTimeSlotContainer.removeAllViewsInLayout();
            }
            for (int i = 0; i < fieldArray.length(); i++) {
                JSONObject jobject = generalFunc.getJsonObject(fieldArray, i);
                try {
                    String data = Objects.requireNonNull(jobject.names()).getString(0);
                    String row_value = jobject.get(data).toString();
                    pickupTimeSlotContainer.addView(addItemFieldData(generalFunc, data, row_value, i == (fieldArray.length() - 1)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (pickupTimeSlotContainer.getChildCount() > 0) {
                pickupAvailibilityArea.setVisibility(VISIBLE);
            } else {
                pickupAvailibilityArea.setVisibility(GONE);
            }
        }
    }

    private void rentItemFieldRow(@NonNull GeneralFunctions generalFunc, @NonNull String responseString) {

        JSONArray fieldArray = generalFunc.getJsonArray("RentitemFieldarray", responseString);
        if (fieldArray != null) {
            if (rentItemDetailsContainer.getChildCount() > 0) {
                rentItemDetailsContainer.removeAllViewsInLayout();
            }
            txtPostTitle.setVisibility(GONE);
            cardPostDescription.setVisibility(GONE);
            rentDetailsArea.setVisibility(GONE);
            for (int i = 0; i < fieldArray.length(); i++) {
                JSONObject jobject = generalFunc.getJsonObject(fieldArray, i);
                try {
                    String data = Objects.requireNonNull(jobject.names()).getString(0);

                    String row_value = jobject.get(data).toString();
                    if (data.equalsIgnoreCase("eName") && row_value.equalsIgnoreCase("Yes")) {
                        txtPostTitle.setVisibility(VISIBLE);
                        txtPostTitle.setText(jobject.get(Objects.requireNonNull(jobject.names()).getString(1)).toString());
                        if (Utils.checkText(generalFunc.getJsonValueStr("vItemName", jobject))) {
                            txtPostTitle.setText(generalFunc.getJsonValueStr("vItemName", jobject));
                        }
                    } else if (data.equalsIgnoreCase("eDescription") && row_value.equalsIgnoreCase("Yes")) {
                        cardPostDescription.setVisibility(VISIBLE);
                        txtPostDescription.setText(jobject.get(Objects.requireNonNull(jobject.names()).getString(1)).toString());
                    } else {
                        rentItemDetailsContainer.addView(addItemFieldData(generalFunc, data, row_value, i == (fieldArray.length() - 1)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (rentItemDetailsContainer.getChildCount() > 0) {
                rentDetailsArea.setVisibility(VISIBLE);
            }
        }
    }

    private View addItemFieldData(@NonNull GeneralFunctions generalFunc, String data, String row_value, boolean divider_view_gone) {
        LayoutInflater infalInflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View convertView = infalInflater.inflate(R.layout.item_rent_daynamic_field, null);

        convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        MTextView titleHTxt = convertView.findViewById(R.id.titleHTxt);
        MTextView titleVTxt = convertView.findViewById(R.id.titleVTxt);
        View divider_view = convertView.findViewById(R.id.divider_view);
        if (divider_view_gone) {
            divider_view.setVisibility(GONE);
        }

        titleHTxt.setText(generalFunc.convertNumberWithRTL(data));
        titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

        return convertView;
    }

    public void checkPageNext() {
        if (mActivity != null) {
            mActivity.setPageNext();
        }
    }

    @SuppressLint("MissingInflatedId")
    private void openNavigationDialog(boolean isDirection) {
        assert mActivity != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_selectnavigation_view, null);

        final MTextView NavigationTitleTxt = dialogView.findViewById(R.id.NavigationTitleTxt);
        final MTextView wazemapTxtView = dialogView.findViewById(R.id.wazemapTxtView);
        final MTextView googlemmapTxtView = dialogView.findViewById(R.id.googlemmapTxtView);
        LinearLayout ll_wazemapTxtView = dialogView.findViewById(R.id.ll_wazemapTxtView);
        LinearLayout ll_googlemmapTxtView = dialogView.findViewById(R.id.ll_googlemmapTxtView);
        ImageView cancelImg = dialogView.findViewById(R.id.cancelImg);

        builder.setView(dialogView);
        NavigationTitleTxt.setText(mActivity.generalFunc.retrieveLangLBl("Choose Option", "LBL_CHOOSE_OPTION"));
        googlemmapTxtView.setText(mActivity.generalFunc.retrieveLangLBl("Google map navigation", "LBL_NAVIGATION_GOOGLE_MAP"));
        wazemapTxtView.setText(mActivity.generalFunc.retrieveLangLBl("Waze navigation", "LBL_NAVIGATION_WAZE"));

        cancelImg.setOnClickListener(v -> list_navigation.dismiss());

        ll_googlemmapTxtView.setOnClickListener(v -> {
            try {
                list_navigation.dismiss();
                if (isDirection) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + userLocation.getLatitude() + "," + userLocation.getLongitude()));
                    startActivity(intent);
                } else {
                    String url_view = "http://maps.google.com/maps?q=loc:" + userLocation.getLatitude() + "," + userLocation.getLongitude();
                    (new ActUtils(mActivity)).openURL(url_view, "com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                }
            } catch (Exception e) {
                mActivity.generalFunc.showMessage(wazemapTxtView, mActivity.generalFunc.retrieveLangLBl("Please install Google Maps in your device.", "LBL_INSTALL_GOOGLE_MAPS"));
            }
        });

        ll_wazemapTxtView.setOnClickListener(v -> {
            try {
                if (isDirection) {
                    String uri = "https://waze.com/ul?q=" + MyApp.getInstance().currentLocation.getLatitude() + "," + MyApp.getInstance().currentLocation.getLongitude() + "&ll=" + userLocation.getLatitude() + "," + userLocation.getLatitude() + "&navigate=yes";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                } else {
                    String uri = "https://waze.com/ul?ll=" + userLocation.getLatitude() + "," + userLocation.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
                list_navigation.dismiss();

            } catch (Exception e) {
                mActivity.generalFunc.showMessage(wazemapTxtView, mActivity.generalFunc.retrieveLangLBl("Please install Waze navigation app in your device.", "LBL_INSTALL_WAZE"));
            }
        });


        list_navigation = builder.create();
        if (mActivity.generalFunc.isRTLmode()) {
            mActivity.generalFunc.forceRTLIfSupported(list_navigation);
        }
        list_navigation.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.all_roundcurve_card));
        list_navigation.show();
        list_navigation.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(mActivity));
    }

    @Override
    public void onItemClickList(View v, int position) {
        (new ActUtils(getContext())).openURL(mediaList.get(position).get("vImage"));
    }

    @Override
    public void onLongItemClickList(View v, int position) {

    }

    @Override
    public void onDeleteClick(View v, int position) {

    }
}