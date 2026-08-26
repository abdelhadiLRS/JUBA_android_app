package com.multixpro.store;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.adapter.files.ItemAvailabilityRecycleAdapter;
import com.general.files.GeneralFunctions;
import com.service.handler.ApiHandler;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ItemAvailabilityActivity extends ParentActivity implements ItemAvailabilityRecycleAdapter.OnItemClickListener {


    MTextView titleTxt;
    ImageView backImgView;

    ProgressBar loading_history;

    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;

    MTextView noOrdersTxt;

    RecyclerView historyRecyclerView;
    ErrorView errorView;

    String next_page_str = "";

    ItemAvailabilityRecycleAdapter itemAvailabilityRecyclerAdapter;

    ArrayList<HashMap<String, String>> listData = new ArrayList<>();

    String previousHeaderCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_availability);


        titleTxt = findViewById(R.id.titleTxt);
        backImgView = findViewById(R.id.backImgView);

        loading_history = findViewById(R.id.loading_history);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        noOrdersTxt = findViewById(R.id.noOrdersTxt);
        errorView = findViewById(R.id.errorView);

        itemAvailabilityRecyclerAdapter = new ItemAvailabilityRecycleAdapter(getActContext(), listData, generalFunc, true);
        historyRecyclerView.setAdapter(itemAvailabilityRecyclerAdapter);
        itemAvailabilityRecyclerAdapter.setOnItemClickListener(this);

        addToClickHandler(backImgView);

        historyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = Objects.requireNonNull(recyclerView.getLayoutManager()).getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(mIsLoading) && isNextPageAvailable) {

                    mIsLoading = true;
                    itemAvailabilityRecyclerAdapter.addFooterView();

                    getPastOrders(true);

                } else if (!isNextPageAvailable) {
                    itemAvailabilityRecyclerAdapter.removeFooterView();
                }
            }
        });

        setLabels();

        getPastOrders(false);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }
    }

    private void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ITEMS"));
    }

    private Context getActContext() {
        return ItemAvailabilityActivity.this;
    }

    @Override
    public void onItemClickList(int position) {

    }

    @Override
    public void switchOnlineOffline(boolean isOnlineAvoid, int position) {
        switchOnOff(listData.get(position).get("iMenuItemId"), position, isOnlineAvoid);
    }

    private void switchOnOff(String iMenuItemId, int position, boolean isOnline) {
        final HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "UpdateFoodMenuItemForRestaurant");
        parameters.put("iMenuItemId", iMenuItemId);
        parameters.put("eAvailable", isOnline ? "Yes" : "No");

        ApiHandler.execute(getActContext(), parameters, true, false, generalFunc,
                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.toString().equals("")) {

                        closeLoader();

                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {

                            HashMap<String, String> newData = listData.get(position);
                            newData.put("eAvailable", isOnline ? "Yes" : "No");
                            listData.set(position, newData);
                            itemAvailabilityRecyclerAdapter.notifyDataSetChanged();

                            //getPastOrders(false);
                        }
                        generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                    } else {
                        generalFunc.showError();
                    }
                });

    }

    private void getPastOrders(boolean isLoadMore) {

        if (!isLoadMore) {
            listData.clear();
            previousHeaderCategory = "";
            itemAvailabilityRecyclerAdapter.notifyDataSetChanged();
            isNextPageAvailable = false;
            mIsLoading = true;
        }

        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading_history.getVisibility() != View.VISIBLE && !isLoadMore) {
            loading_history.setVisibility(View.VISIBLE);
        }

        noOrdersTxt.setVisibility(View.GONE);

        final HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "ManageFoodItem");
        parameters.put("iGeneralUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        if (isLoadMore) {
            parameters.put("page", next_page_str);
        }


        ApiHandler.execute(getActContext(), parameters,
                responseString -> {
                    noOrdersTxt.setVisibility(View.GONE);
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);

                    if (responseObj != null && !responseObj.toString().equals("")) {

                        closeLoader();

                        if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {
                            String nextPage = generalFunc.getJsonValueStr("NextPage", responseObj);
                            JSONArray arr_orders = generalFunc.getJsonArray(Utils.message_str, responseObj);

                            if (arr_orders != null) {
                                for (int i = 0; i < arr_orders.length(); i++) {
                                    JSONObject obj_temp = generalFunc.getJsonObject(arr_orders, i);


                                    String CategoryName = generalFunc.getJsonValueStr("CategoryName", obj_temp);

                                    if (!previousHeaderCategory.equalsIgnoreCase(CategoryName)) {

                                        HashMap<String, String> mapHeader = new HashMap<>();
                                        mapHeader.put("CategoryName", CategoryName);
                                        mapHeader.put("TYPE", "" + ItemAvailabilityRecycleAdapter.TYPE_HEADER);

                                        listData.add(mapHeader);
                                        previousHeaderCategory = CategoryName;
                                    }


                                    JSONArray arr_date = generalFunc.getJsonArray(Utils.data_str, obj_temp);
                                    for (int j = 0; j < arr_date.length(); j++) {
                                        HashMap<String, String> map = new HashMap<>();

                                        JSONObject obj_date_temp = generalFunc.getJsonObject(arr_date, j);
                                        String MenuItemName = generalFunc.getJsonValueStr("MenuItemName", obj_date_temp);
                                        String fPrice = generalFunc.getJsonValueStr("fPrice", obj_date_temp);
                                        map.put("MenuItemName", MenuItemName);
                                        map.put("MenuItemNameConverted", generalFunc.convertNumberWithRTL(MenuItemName));

                                        map.put("iMenuItemId", generalFunc.getJsonValueStr("iMenuItemId", obj_date_temp));
                                        map.put("vService_BG_color", generalFunc.getJsonValueStr("vService_BG_color", obj_date_temp));
                                        map.put("vService_TEXT_color", generalFunc.getJsonValueStr("vService_TEXT_color", obj_date_temp));

                                        map.put("fPrice", fPrice);
                                        map.put("fPriceConverted", generalFunc.convertNumberWithRTL(fPrice));

                                        map.put("eAvailable", generalFunc.getJsonValueStr("eAvailable", obj_date_temp));
                                        map.put("TYPE", "" + ItemAvailabilityRecycleAdapter.TYPE_ITEM);
                                        map.put("LBL_IN_STOCK", generalFunc.retrieveLangLBl("", "LBL_IN_STOCK"));
                                        map.put("LBL_NOT_AVAILABLE", generalFunc.retrieveLangLBl("", "LBL_NOT_AVAILABLE"));

                                        listData.add(map);
                                    }
                                }
                            }
                            if (!nextPage.equals("") && !nextPage.equals("0")) {
                                next_page_str = nextPage;
                                isNextPageAvailable = true;
                            } else {
                                removeNextPageConfig();
                            }

                            itemAvailabilityRecyclerAdapter.notifyDataSetChanged();

                        } else {
                            if (listData.size() == 0) {
                                removeNextPageConfig();
                                noOrdersTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                                noOrdersTxt.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (!isLoadMore) {
                            removeNextPageConfig();
                            generateErrorView();
                        }

                    }
                    mIsLoading = false;

                });

    }

    private void removeNextPageConfig() {
        next_page_str = "";
        isNextPageAvailable = false;
        mIsLoading = false;
        itemAvailabilityRecyclerAdapter.removeFooterView();
    }

    private void closeLoader() {
        if (loading_history.getVisibility() == View.VISIBLE) {
            loading_history.setVisibility(View.GONE);
        }
    }

    private void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(() -> getPastOrders(false));
    }


    public void onClick(View view) {
        Utils.hideKeyboard(ItemAvailabilityActivity.this);
        if (view.getId() == R.id.backImgView) {
            ItemAvailabilityActivity.super.onBackPressed();
        }
    }


}
