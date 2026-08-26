package com.multixpro.user.rentItem.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.general.files.ActUtils;
import com.general.files.FileSelector;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.UploadProfileImage;
import com.multixpro.user.R;
import com.multixpro.user.rentItem.RentItemNewPostActivity;
import com.multixpro.user.rentItem.adapter.RentGalleryImagesAdapter;
import com.service.handler.ApiHandler;
import com.utils.MyUtils;
import com.utils.Utils;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RentItemPhotosFragment extends Fragment implements RentGalleryImagesAdapter.OnItemClickListener {

    @Nullable
    private RentItemNewPostActivity mActivity;
    private RecyclerView rvRentPostImages;
    private RentGalleryImagesAdapter mAdapter;
    private final ArrayList<HashMap<String, String>> listData = new ArrayList<>();
    private boolean isSelectMode = false;
    private ImageView galleryImgViewselect;
    private MTextView selectServiceTxt;
    private LinearLayout choosearea;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rent_item_photos, container, false);
        galleryImgViewselect = view.findViewById(R.id.galleryImgViewselect);
        selectServiceTxt = view.findViewById(R.id.selectServiceTxt);
        choosearea = view.findViewById(R.id.choosearea);
        selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_CHOOSE_FILE"));
        choosearea.setOnClickListener(view1 -> mActivity.generalFunc.showGeneralMessage("", mActivity.generalFunc.retrieveLangLBl("", "LBL_SELECT_MEDIA_TYPE_TXT"),
                mActivity.generalFunc.retrieveLangLBl("", "LBL_VIDEO"),
                mActivity.generalFunc.retrieveLangLBl("", "LBL_IMAGE"), buttonId -> {

                    isSelectMode = true;
                    if (buttonId == 0) {
                        // video
                        mActivity.getFileSelector().openFileSelection(FileSelector.FileType.Video);
                    } else if (buttonId == 1) {
                        // image
                        mActivity.getFileSelector().openFileSelection(FileSelector.FileType.Image);
                    }
                }));
        rvRentPostImages = view.findViewById(R.id.rvRentPostImages);
        rvRentPostImages.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApp.getInstance().getCurrentAct() instanceof RentItemNewPostActivity) {
            mActivity = (RentItemNewPostActivity) requireActivity();

            mAdapter = new RentGalleryImagesAdapter(requireActivity(), listData, mActivity.generalFunc, true);
            mAdapter.setOnItemClickListener(this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mActivity != null && !isSelectMode) {
            getImages(mActivity.generalFunc);
        }
        isSelectMode = false;
        mActivity.selectServiceTxt.setText(mActivity.generalFunc.retrieveLangLBl("", "LBL_RENT_ITEM_PHOTOS"));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getImages(GeneralFunctions generalFunc) {
        assert mActivity != null;
        if (mActivity.mRentItemData.getiItemCategoryId() == null) {
            return;
        }

        mActivity.setPagerHeight();

        mActivity.loading.setVisibility(View.VISIBLE);
        rvRentPostImages.setVisibility(View.GONE);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "RentItemImages");
        parameters.put("iUserId", generalFunc.getMemberId());

        parameters.put("iItemCategoryId", mActivity.mRentItemData.getiItemCategoryId());
        parameters.put("iItemSubCategoryId", mActivity.mRentItemData.getiItemSubCategoryId());

        parameters.put("iRentItemPostId", mActivity.mRentItemData.getiRentItemPostId() == null ? "" : mActivity.mRentItemData.getiRentItemPostId());
        parameters.put("iTmpRentItemPostId", mActivity.mRentItemData.getiTmpRentItemPostId() == null ? "" : mActivity.mRentItemData.getiTmpRentItemPostId());

        parameters.put("iImageId", "");
        parameters.put("action_type", "History");

        ApiHandler.execute(requireActivity(), parameters, responseString -> {

            mActivity.loading.setVisibility(View.GONE);
            rvRentPostImages.setVisibility(View.VISIBLE);

            if (responseString != null && !responseString.equalsIgnoreCase("")) {
                if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                    listData.clear();

                    JSONArray arr_data = generalFunc.getJsonArray("AllImages", responseString);

//                    HashMap<String, String> mapData1 = new HashMap<>();
//                    mapData1.put("add", "add");
//                    listData.add(mapData1);
                    String iRentImageId = "";
                    if (arr_data != null) {

                        for (int i = 0; i < arr_data.length(); i++) {
                            JSONObject obj_tmp = generalFunc.getJsonObject(arr_data, i);

                            String imageId = generalFunc.getJsonValueStr("iRentImageId", obj_tmp);
                            if (iRentImageId.equalsIgnoreCase("")) {
                                iRentImageId = imageId;
                            } else {
                                iRentImageId = iRentImageId + "," + imageId;
                            }

                            HashMap<String, String> mapData = new HashMap<>();
                            MyUtils.createHashMap(generalFunc, mapData, obj_tmp);
                            mapData.put("isDelete", "Yes");
                            listData.add(mapData);
                        }
                    }
                    mActivity.mRentItemData.setiRentImageId(iRentImageId);

                    mAdapter.notifyDataSetChanged();
                    mActivity.setPagerHeight();
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });

    }

    @Override
    public void onItemClickList(View v, int position) {

        (new ActUtils(getActContext())).openURL(listData.get(position).get("vImage"));

//        if (position == 0 && mActivity != null) {
//            mActivity.generalFunc.showGeneralMessage("", mActivity.generalFunc.retrieveLangLBl("", "LBL_SELECT_MEDIA_TYPE_TXT"),
//                    mActivity.generalFunc.retrieveLangLBl("", "LBL_VIDEO"),
//                    mActivity.generalFunc.retrieveLangLBl("", "LBL_IMAGE"), buttonId -> {
//
//                        isSelectMode = true;
//                        if (buttonId == 0) {
//                            // video
//                            mActivity.getFileSelector().openFileSelection(FileSelector.FileType.Video);
//                        } else if (buttonId == 1) {
//                            // image
//                            mActivity.getFileSelector().openFileSelection(FileSelector.FileType.Image);
//                        }
//                    });
//
//        } else {
//            (new ActUtils(getActContext())).openURL( listData.get(position).get("vImage"));
//        }
    }

    public Context getActContext() {
        return getActivity();
    }

    @Override
    public void onLongItemClickList(View v, int position) {

    }

    @Override
    public void onDeleteClick(View v, int position) {
        if (mActivity != null) {
            String msg = "";
            if (listData.get(position).get("eFileType").equalsIgnoreCase("Image")) {
                msg = mActivity.generalFunc.retrieveLangLBl("", "LBL_DELETE_IMG_CONFIRM_MSG");

            } else if (listData.get(position).get("eFileType").equalsIgnoreCase("Video")) {
                msg = mActivity.generalFunc.retrieveLangLBl("", "LBL_DELETE_VIDEO_CONFIRM_MSG");
            }

            mActivity.generalFunc.showGeneralMessage("", msg,
                    mActivity.generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"),
                    mActivity.generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), buttonId -> {
                        if (buttonId == 1) {
                            deleteImage(mActivity.generalFunc, listData.get(position).get("iRentImageId"));
                        }
                    });
        }
    }

    private void deleteImage(GeneralFunctions generalFunc, String iImageId) {
        assert mActivity != null;

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "RentItemImages");
        parameters.put("iUserId", generalFunc.getMemberId());

        parameters.put("iItemCategoryId", mActivity.mRentItemData.getiItemCategoryId());
        parameters.put("iItemSubCategoryId", mActivity.mRentItemData.getiItemSubCategoryId());

        parameters.put("iRentItemPostId", mActivity.mRentItemData.getiRentItemPostId() == null ? "" : mActivity.mRentItemData.getiRentItemPostId());
        parameters.put("iTmpRentItemPostId", mActivity.mRentItemData.getiTmpRentItemPostId() == null ? "" : mActivity.mRentItemData.getiTmpRentItemPostId());

        parameters.put("iImageId", iImageId);
        parameters.put("action_type", "Delete");

        ApiHandler.execute(requireActivity(), parameters, true, false, generalFunc, responseString -> handleImgUploadResponse(generalFunc, responseString));
    }

    public void configMedia(GeneralFunctions generalFunc, String selectedImagePath, FileSelector.FileType mFileType) {
        assert mActivity != null;

        ArrayList<String[]> paramsList = new ArrayList<>();
        paramsList.add(generalFunc.generateImageParams("type", "RentItemImages"));
        paramsList.add(generalFunc.generateImageParams("iUserId", generalFunc.getMemberId()));

        paramsList.add(generalFunc.generateImageParams("iItemCategoryId", mActivity.mRentItemData.getiItemCategoryId()));
        paramsList.add(generalFunc.generateImageParams("iItemSubCategoryId", mActivity.mRentItemData.getiItemSubCategoryId()));

        paramsList.add(generalFunc.generateImageParams("iRentItemPostId", mActivity.mRentItemData.getiRentItemPostId() == null ? "" : mActivity.mRentItemData.getiRentItemPostId()));
        paramsList.add(generalFunc.generateImageParams("iTmpRentItemPostId", mActivity.mRentItemData.getiTmpRentItemPostId() == null ? "" : mActivity.mRentItemData.getiTmpRentItemPostId()));

        paramsList.add(generalFunc.generateImageParams("iImageId", ""));
        paramsList.add(generalFunc.generateImageParams("action_type", "Add"));

        /////////////////////////////////////////////////////////////////////////////////
        if (mFileType == FileSelector.FileType.Image) {
            new UploadProfileImage(true, requireActivity(), selectedImagePath, Utils.TempProfileImageName, paramsList).execute(mActivity.loading);

        } else if (mFileType == FileSelector.FileType.Video) {
            String videoFormat;
            int index = selectedImagePath.lastIndexOf(".");
            if (index > 0) {
                videoFormat = selectedImagePath.substring(index + 1);
                new UploadProfileImage(true, requireActivity(), selectedImagePath, "temp_video." + videoFormat, paramsList).execute();
            }
        }
    }

    public void handleImgUploadResponse(GeneralFunctions generalFunc, String responseString) {
        if (responseString != null && !responseString.equals("")) {
            if (GeneralFunctions.checkDataAvail(Utils.action_str, responseString)) {
                generalFunc.showMessage(rvRentPostImages, generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                getImages(generalFunc);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        } else {
            generalFunc.showError();
        }
    }

    public void checkPageNext() {
        if (mActivity != null && mActivity.loading.getVisibility() == View.GONE) {
            if (mActivity.mRentItemData.getiRentImageId() != null && mActivity.mRentItemData.getiRentImageId().equalsIgnoreCase("")) {
                mActivity.generalFunc.showMessage(mActivity.selectServiceTxt, mActivity.generalFunc.retrieveLangLBl("", "LBL_UPLOAD_IMAGE_NOTE"));
            } else {
                mActivity.setPageNext();
            }
        }
    }
}