package com.adapter.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.fontanalyzer.SystemFont;
import com.multixpro.provider.R;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.LoadImage;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.pinnedListView.CountryListItem;
import com.view.pinnedListView.PinnedSectionListView;

import java.util.ArrayList;

public class PinnedBiddingServicesListAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter, SectionIndexer {

    private final Context mContext;
    private GeneralFunctions generalFunc;
    private LayoutInflater inflater;

    private final int mColor;
    private final Typeface font1, font2;

    private CategoryListItem[] sections;
    private final ArrayList<CategoryListItem> categoryListItems;
    private final ArrayList<Boolean> biddingTypesStatusArr = new ArrayList<>();

    public PinnedBiddingServicesListAdapter(Context mContext, ArrayList<CategoryListItem> categoryListItems, CategoryListItem[] sections) {
        this.mContext = mContext;
        this.generalFunc = MyApp.getInstance().getGeneralFun(mContext);
        this.categoryListItems = categoryListItems;
        this.sections = sections;
        mColor = Color.parseColor("#000000");

        font1 = SystemFont.FontStyle.SEMI_BOLD.font;
        font2 = SystemFont.FontStyle.REGULAR.font;

    }

    public void manageBiddingArraySize() {
        biddingTypesStatusArr.clear();
        for (int i = 0; i < categoryListItems.size(); i++) {
            biddingTypesStatusArr.add(i, false);
        }

    }

    public void changeSection(CategoryListItem[] sections) {
        this.sections = sections;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_bidding_category_list, null);

        TextView txt_view = (TextView) convertView.findViewById(R.id.txt);
        LinearLayout serviceArea = (LinearLayout) convertView.findViewById(R.id.serviceArea);
        LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
        RelativeLayout layoutBackground = (RelativeLayout) convertView.findViewById(R.id.layoutBackground);
        ImageView roundImageView = (ImageView) convertView.findViewById(R.id.roundImageView);

        AppCompatCheckBox cbBiddingService = (AppCompatCheckBox) convertView.findViewById(R.id.cbBiddingService);
        cbBiddingService.setTag(position);

        txt_view.setTextColor(Color.BLACK);
        txt_view.setTag("" + position);
        cbBiddingService.setTag("" + position);
        final CategoryListItem categoryListItem = categoryListItems.get(position);

        serviceArea.setClickable(false);
        serviceArea.setEnabled(false);
        if (categoryListItem.getType() == CountryListItem.SECTION) {
            convertView.setBackgroundColor(Color.parseColor("#f1f1f1"));
            txt_view.setText(categoryListItem.getvTitle());
            txt_view.setTextColor(mColor);
            txt_view.setText(categoryListItem.getText());
            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen._16ssp));
            txt_view.setTypeface(font1);
            cbBiddingService.setVisibility(View.GONE);
            layoutBackground.setVisibility(View.GONE);
            serviceArea.setMinimumHeight((int) mContext.getResources().getDimension(R.dimen._40sdp));
            txt_view.setMinimumHeight((int) mContext.getResources().getDimension(R.dimen._40sdp));
            itemLayout.setBackgroundColor(Color.parseColor("#f1f1f1"));
            txt_view.setGravity(Gravity.BOTTOM | Gravity.START);
        } else {
            itemLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            txt_view.setText(categoryListItem.getvTitle());
            txt_view.setTextColor(mColor);
            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen._12ssp));
            layoutBackground.setVisibility(View.VISIBLE);
            cbBiddingService.setVisibility(View.VISIBLE);
            txt_view.setTypeface(font2);
            if (Utils.checkText(categoryListItem.getvLogo())) {

                new LoadImage.builder(LoadImage.bind(categoryListItem.getvLogo()), roundImageView).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();

            } else {

                new LoadImage.builder(LoadImage.bind(R.mipmap.ic_no_icon), roundImageView).setErrorImagePath(R.mipmap.ic_no_icon).setPlaceholderImagePath(R.mipmap.ic_no_icon).build();

            }
            serviceArea.setMinimumHeight((int) mContext.getResources().getDimension(R.dimen._40sdp));

        }

        // Set data
        // biddingTypesStatusArr.add(position, false);
        String eServiceStatus = categoryListItem.getvCategory();

        cbBiddingService.setButtonDrawable(R.drawable.checkbox_selector);

        if (eServiceStatus.equalsIgnoreCase("Active")) {
            cbBiddingService.setChecked(true);
            biddingTypesStatusArr.set(position, true);
        } else if (eServiceStatus.equalsIgnoreCase("Pending")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cbBiddingService.setButtonDrawable(R.drawable.ic_mark_gray);
            }
            cbBiddingService.setChecked(true);
            biddingTypesStatusArr.set(position, true);
        } else if (eServiceStatus.equalsIgnoreCase("Inactive")) {
            cbBiddingService.setChecked(false);
            biddingTypesStatusArr.set(position, false);
        } else if (eServiceStatus.equalsIgnoreCase("select")) {
            cbBiddingService.setChecked(true);
            biddingTypesStatusArr.set(position, true);
        } else if (eServiceStatus.equalsIgnoreCase("deselect")) {
            cbBiddingService.setChecked(false);
            biddingTypesStatusArr.set(position, false);
        }


        final int finalI = position;
        cbBiddingService.setOnClickListener(v -> {
            if (eServiceStatus.equalsIgnoreCase("Pending")) {
                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {
                    if (btn_id == 1) {
                        generateAlert.closeAlertBox();
                    }
                });
                generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_BIDDING_SERVICE_REQUEST_PENDING"));
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_OK"));
                generateAlert.showAlertBox();
            } else if (eServiceStatus.equalsIgnoreCase("Active")) {
                if (!cbBiddingService.isChecked()) {
                    GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> {
                        if (btn_id == 0) {
                            biddingTypesStatusArr.set(finalI, true);
                            cbBiddingService.setChecked(true);
                        } else {
                            biddingTypesStatusArr.set(finalI, false);
                        }
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_UNSELECT_CHECKBOX_FOR_BIDDING_SERVICE"));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
                    generateAlert.showAlertBox();
                }
            } else {
                categoryListItem.setvCategory(cbBiddingService.isChecked() ? "select" : "deselect");
                biddingTypesStatusArr.set(finalI, cbBiddingService.isChecked());
            }
        });
        return convertView;
    }

    public String getSelectedIDList() {
        String carTypes = "";
        for (int j = 0; j < biddingTypesStatusArr.size(); j++) {
            try {

                if (biddingTypesStatusArr.get(j) != null && biddingTypesStatusArr.get(j)) {
                    String iVehicleTypeId = categoryListItems.get(j).getiVehicleCategoryId();
                    carTypes = carTypes.equals("") ? iVehicleTypeId : (carTypes + "," + iVehicleTypeId);
                }
            } catch (Exception e) {

            }
        }
        return carTypes;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public CategoryListItem[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int section) {
        if (section >= sections.length) {
            section = sections.length - 1;
        }
        return sections[section].getListPosition();
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= getCount()) {
            position = getCount() - 1;
        }
        return categoryListItems.get(position).getSectionPosition();
    }

    @Override
    public int getItemViewType(int position) {
        return categoryListItems.get(position).getType();
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == CountryListItem.SECTION;
    }

    @Override
    public int getCount() {
        return categoryListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}