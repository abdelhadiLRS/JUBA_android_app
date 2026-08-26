package com.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adapter.files.OrderRecycleAdapter;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.multixpro.store.MainActivity;
import com.multixpro.store.OrderDetailActivity;
import com.multixpro.store.R;
import com.multixpro.store.RestaurantSettingsActivity;
import com.kyleduo.switchbutton.SwitchButton;
import com.service.handler.ApiHandler;
import com.service.server.ServerTask;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Esite on 30-03-2018.
 */

public class OrderFragment extends BaseFragment implements OrderRecycleAdapter.OnItemClickListener {


    public View newOrderNotificationArea;
    View view;
    ProgressBar loading_order_detail;
    MTextView noOrdersTxt, noOrdersMsgTxt;
    ImageView noOrderImg;
    RecyclerView orderRecyclerView;
    ErrorView errorView;
    ImageView removeNewOrderNotificationAreaImgView;
    SwipeRefreshLayout swipeRefresh;

    public OrderRecycleAdapter orderRecyclerAdapter;

    ArrayList<HashMap<String, String>> listData = new ArrayList<>();

    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;

    String next_page_str = "";

    GeneralFunctions generalFunc;

    MainActivity mainAct;

    JSONObject userProfileJsonObj;

    String APP_TYPE;

    View availabilityArea;
    SwitchButton onlineOfflineSwitch;
    ServerTask currentExeServer;
    MTextView newOrderArrivedTxtView;
    MTextView acceptingOrdersTxtView;
    FrameLayout connectPrinterlayout;

    boolean isNewOrder = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);

        loading_order_detail = (ProgressBar) view.findViewById(R.id.loading_order_detail);
        noOrdersTxt = (MTextView) view.findViewById(R.id.noOrdersTxt);
        noOrdersMsgTxt = (MTextView) view.findViewById(R.id.noOrdersMsgTxt);
        noOrderImg = (ImageView) view.findViewById(R.id.noOrderImg);

        orderRecyclerView = (RecyclerView) view.findViewById(R.id.orderRecyclerView);
        errorView = (ErrorView) view.findViewById(R.id.errorView);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        removeNewOrderNotificationAreaImgView = (ImageView) view.findViewById(R.id.removeNewOrderNotificationAreaImgView);
        newOrderNotificationArea = (View) view.findViewById(R.id.newOrderNotificationArea);
        availabilityArea = view.findViewById(R.id.availabilityArea);
        onlineOfflineSwitch = (SwitchButton) view.findViewById(R.id.onlineOfflineSwitch);
        newOrderArrivedTxtView = (MTextView) view.findViewById(R.id.newOrderArrivedTxtView);
        acceptingOrdersTxtView = (MTextView) view.findViewById(R.id.acceptingOrdersTxtView);


        if (getActivity() instanceof MainActivity) {
            mainAct = (MainActivity) getActivity();
            generalFunc = mainAct.generalFunc;
        }


        connectPrinterlayout = (FrameLayout) view.findViewById(R.id.connectPrinterlayout);
        addToClickHandler(connectPrinterlayout);

        newOrderArrivedTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_NEW_ORDER_ARRIVED"));
        acceptingOrdersTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPTING_ORDERS"));


        userProfileJsonObj = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));
        APP_TYPE = generalFunc.getJsonValueStr("APP_TYPE", userProfileJsonObj);

        orderRecyclerAdapter = new OrderRecycleAdapter(listData, false, this);
        orderRecyclerView.setAdapter(orderRecyclerAdapter);

        addToClickHandler(removeNewOrderNotificationAreaImgView);

        orderRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(mIsLoading) && isNextPageAvailable == true) {

                    mIsLoading = true;
                    orderRecyclerAdapter.addFooterView();

                    getOrders(true);

                } else if (isNextPageAvailable == false) {
                    orderRecyclerAdapter.removeFooterView();
                }
            }
        });

        swipeRefresh.setOnRefreshListener(() -> {
            getOrders(false);
            if (getArguments().getString("ORDER_TYPE").equalsIgnoreCase("NEW")) {
                configRestaurantSettings("Display", onlineOfflineSwitch.isChecked());
            }
        });

        onlineOfflineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mainAct.viewPager.getCurrentItem() == 0) {
                configRestaurantSettings("Update", isChecked);
            }
        });
        getOrders(false);

        if (getArguments().getString("ORDER_TYPE").equalsIgnoreCase("NEW")) {
            availabilityArea.setVisibility(View.VISIBLE);

            String eAvailable = generalFunc.getJsonValueStr("eAvailable", userProfileJsonObj);

            onlineOfflineSwitch.setCheckedNoEvent(false);
            onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);


            configRestaurantSettings("Display", eAvailable.equalsIgnoreCase("Yes") ? true : false);

            isNewOrder = true;

        } else {
            availabilityArea.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        connectPrinterlayout.setVisibility(View.GONE);
        if (isNewOrder) {
            HashMap<String, String> data = new HashMap<>();
            data.put(Utils.THERMAL_PRINT_ALLOWED_KEY, "");
            data = new GeneralFunctions(getActContext()).retrieveValue(data);
            if (MyApp.getInstance().isThermalPrintAllowed(false) && data.get(Utils.THERMAL_PRINT_ALLOWED_KEY).equalsIgnoreCase("Yes")) {
                connectPrinterlayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void configRestaurantSettings(String callType, boolean isChecked) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "UpdateRestaurantAvailability");
        parameters.put("iCompanyId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);
        parameters.put("CALL_TYPE", callType);
        parameters.put("eAvailable", isChecked == true ? "Yes" : "No");


        ApiHandler.execute(getActContext(), parameters, callType.equals("Update") ? true : false, false, generalFunc,

                responseString -> {
                    JSONObject responseObj = generalFunc.getJsonObject(responseString);
                    if (responseObj != null && !responseObj.equals("")) {

                        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseObj);

                        String message = generalFunc.getJsonValueStr(Utils.message_str, responseObj);
                        String isAllInformationUpdate = generalFunc.getJsonValueStr("isAllInformationUpdate", responseObj);

                        Bundle bn = new Bundle();
                        bn.putString("msg", "" + message);
                        String eStatus = generalFunc.getJsonValueStr("eStatus", userProfileJsonObj);

                        if (!eStatus.equalsIgnoreCase("inactive")) {
                            if (message.equals("DO_PHONE_VERIFY")) {
                                /*if (message.equals("DO_EMAIL_PHONE_VERIFY") || message.equals("DO_PHONE_VERIFY") || message.equals("DO_EMAIL_VERIFY")) {*/

                                onlineOfflineSwitch.setCheckedNoEvent(false);
                                onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);


                                mainAct.accountVerificationAlert(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_TXT"), bn);
                                return;
                            }
                        }

                        if (isAllInformationUpdate.equalsIgnoreCase("No")) {

                            onlineOfflineSwitch.setCheckedNoEvent(false);
                            onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);

                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_MISSED_DETAILS_MSG"), "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> {
                                bn.putString("isFromMain", "Yes");
                                new ActUtils(getActContext()).startActWithData(RestaurantSettingsActivity.class, bn);

                            });

                            return;
                        }

                        if (message.equalsIgnoreCase("LBL_NO_FOOD_MENU_ITEM_AVAILABLE_TXT")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message), false);

                            onlineOfflineSwitch.setCheckedNoEvent(false);
                            onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);

                            return;
                        }

                        if (message.equalsIgnoreCase("LBL_NO_CUISINES_AVAILABLE_FOR_RESTAURANT")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message), false);

                            onlineOfflineSwitch.setCheckedNoEvent(false);
                            // onlineOfflineSwitch.setBackColorRes(android.R.color.white);
                            onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);
                            return;
                        }

                        if (message.equalsIgnoreCase("LBL_DELIVER_ALL_SERVICE_DISABLE_TXT")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", message), false);

                            onlineOfflineSwitch.setCheckedNoEvent(false);
                            onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);

                            return;
                        }


                        if (isDataAvail) {

                            if (!callType.equals("Update")) {
                                JSONObject msgObj = generalFunc.getJsonObject(Utils.message_str, responseObj);
                                String eAvailable = generalFunc.getJsonValueStr("eAvailable", msgObj);


                                if (eAvailable.equalsIgnoreCase("Yes")) {
                                    onlineOfflineSwitch.setCheckedNoEvent(true);
                                    onlineOfflineSwitch.setBackColorRes((R.color.Green));

                                    //    connectPrinter();
                                } else {
                                    onlineOfflineSwitch.setCheckedNoEvent(false);
                                    onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);

                                }
                            } else {
                                if (isChecked) {
                                    onlineOfflineSwitch.setCheckedNoEvent(true);
                                    onlineOfflineSwitch.setBackColorRes((R.color.Green));

                                    //    connectPrinter();
                                } else {
                                    onlineOfflineSwitch.setCheckedNoEvent(false);
                                    onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);

                                }
                                generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", "LBL_INFO_UPDATED_TXT"));
                            }

                        } else {
//                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_TRY_AGAIN_LATER"), false);

                            generalFunc.showGeneralMessage("",
                                    generalFunc.retrieveLangLBl(generalFunc.getJsonValue(Utils.message_str, responseString),
                                            generalFunc.getJsonValue(Utils.message_str, responseString)));


                            onlineOfflineSwitch.setCheckedNoEvent(false);
                            onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);

                        }
                    } else {
//                generalFunc.showError(false);

                        generalFunc.showMessage(generalFunc.getCurrentView((Activity) getActContext()), generalFunc.retrieveLangLBl("", "LBL_TRY_AGAIN_LATER"));
                        if (isChecked) {
                            onlineOfflineSwitch.setCheckedNoEvent(false);
                            onlineOfflineSwitch.setBackColorRes(android.R.color.holo_red_dark);
                        } else {
                            onlineOfflineSwitch.setCheckedNoEvent(true);
                            onlineOfflineSwitch.setBackColorRes((R.color.Green));
                        }
                    }
                });

    }

    private void setData(int totalSize) {

        orderRecyclerAdapter.notifyDataSetChanged();

        if (getArguments().getString("ORDER_TYPE").equalsIgnoreCase("NEW")) {
            if (mainAct != null) {
                mainAct.resetNames(totalSize, -1, -1, getArguments().getString("ORDER_TYPE"));
            }
        } else if (getArguments().getString("ORDER_TYPE").equalsIgnoreCase("INPROCESS")) {
            if (mainAct != null) {
                mainAct.resetNames(-1, totalSize, -1, getArguments().getString("ORDER_TYPE"));
            }
        } else {
            if (mainAct != null) {
                mainAct.resetNames(-1, -1, totalSize, getArguments().getString("ORDER_TYPE"));
            }
        }
    }

    @Override
    public void onItemClickList(View v, int position) {

        Utils.hideKeyboard(getActivity());

        if (swipeRefresh.isRefreshing()) {
            return;
        }

        Bundle bn = new Bundle();

        bn.putSerializable("OrderData", listData.get(position));

//        bn.putString("OrderData", listData.get(position).get("JSON"));

        new ActUtils(getActContext()).startActForResult(this, OrderDetailActivity.class, Utils.ORDER_DETAILS_REQ_CODE, bn);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getOrders(final boolean isLoadMore) {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading_order_detail.getVisibility() != View.VISIBLE && !isLoadMore) {
            loading_order_detail.setVisibility(View.VISIBLE);
        }
        noOrdersTxt.setVisibility(View.GONE);
        noOrdersMsgTxt.setVisibility(View.GONE);
        mainAct.orderCountTxt.setVisibility(View.GONE);
        noOrderImg.setVisibility(View.GONE);

        swipeRefresh.setRefreshing(false);

        String orderType = getArguments().getString("ORDER_TYPE");

        final HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "GetAllOrderDetailsRestaurant");
        parameters.put("OrderType", orderType.toUpperCase());
        parameters.put("iCompanyId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);
        if (isLoadMore) {
            parameters.put("page", next_page_str);
        } else {
            removeNextPageConfig();
            mIsLoading = true;
            listData.clear();
            orderRecyclerAdapter.notifyDataSetChanged();
        }

        if (currentExeServer != null) {
            currentExeServer.cancel(true);
            currentExeServer = null;
        }
        currentExeServer = ApiHandler.execute(getActContext(), parameters, responseString -> {
            currentExeServer = null;

            noOrdersTxt.setVisibility(View.GONE);
            noOrdersMsgTxt.setVisibility(View.GONE);
            noOrderImg.setVisibility(View.GONE);
            JSONObject responseObj = generalFunc.getJsonObject(responseString);
            if (responseObj != null && !responseObj.toString().equals("")) {

                closeLoader();

                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseObj)) {

                    String nextPage = generalFunc.getJsonValueStr("NextPage", responseObj);
                    String totalOrders = generalFunc.getJsonValueStr("TotalOrders", responseObj);
                    JSONArray ordersArr = generalFunc.getJsonArray(Utils.message_str, responseObj);

                    if (!isLoadMore) {
                        removeNextPageConfig();
                        mIsLoading = true;
                        listData.clear();
                        orderRecyclerAdapter.notifyDataSetChanged();
                    }

                    if (ordersArr != null && ordersArr.length() > 0) {
                        for (int i = 0; i < ordersArr.length(); i++) {
                            JSONObject obj_temp = generalFunc.getJsonObject(ordersArr, i);

                            HashMap<String, String> map = new HashMap<String, String>();


                            String vOrderNo = generalFunc.getJsonValueStr("vOrderNo", obj_temp);
                            map.put("vOrderNo", vOrderNo);
                            map.put("vOrderNoConverted", generalFunc.convertNumberWithRTL(vOrderNo));

                            map.put("iOrderId", generalFunc.getJsonValueStr("iOrderId", obj_temp));
                            map.put("tOrderRequestDate", generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("tOrderRequestDate", obj_temp)));

                            String TotalItems = generalFunc.getJsonValueStr("TotalItems", obj_temp);
                            map.put("TotalItems", TotalItems);
                            map.put("TotalItemsConverted", generalFunc.convertNumberWithRTL(TotalItems));

                            String tOrderRequestDate_Org = generalFunc.getJsonValueStr("tOrderRequestDate_Org", obj_temp);
                            map.put("tOrderRequestDate_Org", tOrderRequestDate_Org);
                            map.put("tOrderRequestDateConverted", generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(tOrderRequestDate_Org, Utils.OriginalDateFormate, Utils.dateFormateTimeOnly)));

                            map.put("tOrderRequestDateFormatted", generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(tOrderRequestDate_Org, Utils.OriginalDateFormate, CommonUtilities.OriginalDateFormate)) + ", " + generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(tOrderRequestDate_Org, Utils.OriginalDateFormate, CommonUtilities.OriginalTimeFormate)));


                            map.put("LBL_ITEMS", generalFunc.retrieveLangLBl("", "LBL_ITEMS"));
                            map.put("LBL_ITEM", generalFunc.retrieveLangLBl("", "LBL_ITEM"));
                            map.put("eTakeaway", generalFunc.getJsonValueStr("eTakeaway", obj_temp));
                            map.put("eOrderType", generalFunc.getJsonValueStr("eOrderType", obj_temp));
                            map.put("LBL_TAKE_AWAY", generalFunc.retrieveLangLBl("", "LBL_TAKE_AWAY"));
                            map.put("LBL_DINE_IN_TXT", generalFunc.retrieveLangLBl("Dine In", "LBL_DINE_IN_TXT"));
                            map.put("vBgColor", generalFunc.getJsonValueStr("vBgColor", obj_temp));
                            map.put("vTextColor", generalFunc.getJsonValueStr("vTextColor", obj_temp));

                            listData.add(map);

                        }
                    }
                    setData(GeneralFunctions.parseIntegerValue(0, totalOrders));

                    if (!nextPage.equals("") && !nextPage.equals("0")) {
                        next_page_str = nextPage;
                        isNextPageAvailable = true;
                    } else {
                        removeNextPageConfig();
                    }

                    orderRecyclerAdapter.notifyDataSetChanged();

                } else {
                    if (listData.size() == 0) {
                        setData(0);
                        removeNextPageConfig();
                        noOrdersTxt.setText(generalFunc.retrieveLangLBl("No Orders Found", generalFunc.getJsonValueStr(Utils.message_str, responseObj)));
                        noOrdersTxt.setVisibility(View.VISIBLE);
                        noOrdersMsgTxt.setText(generalFunc.retrieveLangLBl("No Orders Found", generalFunc.getJsonValueStr(Utils.message_str_one, responseObj)));
                        noOrdersMsgTxt.setVisibility(View.VISIBLE);
                        noOrderImg.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (!isLoadMore) {
                    /*if (mainAct.fragmentList.get(mainAct.viewPager.getCurrentItem()).getArguments().getString("ORDER_TYPE").equalsIgnoreCase(orderType)) {
                        orderCountTxt.setVisibility(View.VISIBLE);
                    }*/
                    setData(0);
                    removeNextPageConfig();
                    generateErrorView();
                }

            }

            mIsLoading = false;
        });
    }

    public void removeNextPageConfig() {
        next_page_str = "";
        isNextPageAvailable = false;
        mIsLoading = false;
        orderRecyclerAdapter.removeFooterView();
    }

    public void closeLoader() {
        if (loading_order_detail.getVisibility() == View.VISIBLE) {
            loading_order_detail.setVisibility(View.GONE);
        }
        swipeRefresh.setRefreshing(false);
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(() -> getOrders(false));
    }


    public Context getActContext() {
        if (mainAct != null) {
            return mainAct.getActContext();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.REQUEST_ENABLE_BT/*&& resultCode == Activity.RESULT_OK*/) {
            try {
                if (mainAct.customDialog != null) {
                    MyApp.btsocket = mainAct.customDialog.getSocket();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK) {
            getOrders(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }


    public void onClickView(View v) {
        if (v.getId() == R.id.removeNewOrderNotificationAreaImgView) {
            newOrderNotificationArea.setVisibility(View.GONE);
        } else if (v.getId() == R.id.connectPrinterlayout) {
            mainAct.connectThermalPrinter();
        }
    }
}