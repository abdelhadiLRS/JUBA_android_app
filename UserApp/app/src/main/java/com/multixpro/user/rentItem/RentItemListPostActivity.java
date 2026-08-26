package com.multixpro.user.rentItem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.ParentActivity;
import com.general.files.ActUtils;
import com.general.files.GeneralFunctions;
import com.multixpro.user.ContactUsActivity;
import com.multixpro.user.R;
import com.multixpro.user.rentItem.adapter.RentItemListPostAdapter;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;
import com.view.FloatingAction.FloatingActionButton;
import com.view.MTextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class RentItemListPostActivity extends ParentActivity {

    private static final int GET_POST_REQ = 484848;
    private RentItemListPostAdapter mRentItemListPostAdapter;
    private final ArrayList<HashMap<String, String>> mPostList = new ArrayList<>();
    private ProgressBar loading;
    private MTextView noDataTxt;
    private String eType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item_list_post);

        eType = getIntent().getStringExtra("eType");

        toolbarData();
        RecyclerView rvRentItemListPost = findViewById(R.id.rvRentItemListPost);

        mRentItemListPostAdapter = new RentItemListPostAdapter(this, generalFunc, mPostList, new RentItemListPostAdapter.OnItemClickListener() {
            @Override
            public void onDeleteButtonClick(int position, HashMap<String, String> mapData) {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_RENT_DELETE_POST"),
                        generalFunc.retrieveLangLBl("", "LBL_NO"),
                        generalFunc.retrieveLangLBl("", "LBL_YES"), buttonId -> {
                            if (buttonId == 1) {
                                getMyPost(true, mapData.get("iRentItemPostId"));
                            }
                        });
            }

            @Override
            public void onReviewButtonClick(int position, HashMap<String, String> mapData) {
                if (loading.getVisibility() == View.GONE) {
                    Bundle bn = new Bundle();
                    bn.putSerializable("rentEditHashMap", mapData);
                    new ActUtils(RentItemListPostActivity.this).startActWithData(RentItemReviewPostActivity.class, bn);
                }
            }

            @Override
            public void onEditButtonClick(int position, HashMap<String, String> mapData) {
                if (loading.getVisibility() == View.GONE) {
                    Bundle bn = new Bundle();
                    bn.putString("eType", eType);
                    bn.putSerializable("rentEditHashMap", mapData);
                    new ActUtils(RentItemListPostActivity.this).startActForResult(RentItemNewPostActivity.class, bn, GET_POST_REQ);
                }
            }

            @Override
            public void onContactUsClick(int position, HashMap<String, String> mapData) {
                new ActUtils(RentItemListPostActivity.this).startAct(ContactUsActivity.class);
            }
        });
        rvRentItemListPost.setAdapter(mRentItemListPostAdapter);
        getMyPost(false, "");
    }

    private void toolbarData() {
        ImageView backImgView = findViewById(R.id.backImgView);
        addToClickHandler(backImgView);
        if (generalFunc.isRTLmode()) {
            backImgView.setRotation(180);
        }

        MTextView titleTxt = findViewById(R.id.titleTxt);
        if (eType.equalsIgnoreCase("RentEstate")) {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_POST_REALESTATE_BY_USER"));
        } else if (eType.equalsIgnoreCase("RentCars")) {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_POST_CARS_BY_USER"));
        } else if (eType.equalsIgnoreCase("RentItem")) {
            titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_POST_ITEM_BY_USER"));
        }

        FloatingActionButton floatBtn = findViewById(R.id.floatBtn);
        addToClickHandler(floatBtn);
        loading = findViewById(R.id.loading_images);
        noDataTxt = findViewById(R.id.noDataTxt);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getMyPost(boolean isDelete, String iRentItemPostId) {

        loading.setVisibility(isDelete ? View.GONE : View.VISIBLE);
        noDataTxt.setVisibility(View.GONE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("iMemberId", generalFunc.getMemberId());
        if (isDelete) {
            parameters.put("type", "DeletePost");
            parameters.put("iRentItemPostId", iRentItemPostId);
            parameters.put("eDeletedBy", "User");
        } else {
            parameters.put("type", "GetAllPost");
            parameters.put("eType", eType);
        }

        ApiHandler.execute(this, parameters, isDelete, false, generalFunc, responseString -> {

            mPostList.clear();
            loading.setVisibility(View.GONE);

            if (responseString != null && !responseString.equals("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    JSONArray itemArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                    MyUtils.createArrayListJSONArray(generalFunc, mPostList, itemArr);
                    if (isDelete) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue("message2", responseString)));
                    }
                }
                noDataTxt.setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                mRentItemListPostAdapter.notifyDataSetChanged();
            } else {
                generalFunc.showError(true);
            }
            noDataTxt.setVisibility(mPostList.size() > 0 ? View.GONE : View.VISIBLE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_POST_REQ && resultCode == RESULT_OK && data != null) {
            if (data.getBooleanExtra("isGetList", false)) {
                getMyPost(false, "");
            }
        }
    }

    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.backImgView) {
            onBackPressed();
        } else if (i == R.id.floatBtn) {
            Bundle bn = new Bundle();
            bn.putString("iCategoryId", "");
            bn.putString("eType", eType);
            new ActUtils(this).startActForResult(RentItemNewPostActivity.class, bn, GET_POST_REQ);
        }
    }

    public void pubNubMsgArrived(final String message) {

        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);

            if (msgType.equals("PostRejectByAdmin") || msgType.equals("PostApprovedByAdmin")) {
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue("vTitle", message), buttonId -> {
                    getMyPost(false, "");
                });
            }
        });
    }
}