package com.adapter.files.deliverAll;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.multixpro.user.R;
import com.general.files.GeneralFunctions;

import com.utils.Logger;
import com.utils.Utils;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;


public class AddStoreItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    GeneralFunctions generalFunc;
    addStoreItemClickListener addStoreItemClickListener;


    public AddStoreItemAdapter(Context mContext, ArrayList<HashMap<String, String>> list, GeneralFunctions generalFunc, addStoreItemClickListener addStoreItemClickListener) {
        this.mContext = mContext;
        this.list = list;
        this.generalFunc = generalFunc;
        this.addStoreItemClickListener = addStoreItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_add_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        setData(holder, position);
        Logger.d("CheckValLIst", "::" + list.size() + "::" + position + "::::" + holder.getAdapterPosition());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    private void setData(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        HashMap<String, String> posData = list.get(position);
        holder.itemName.setTag(position);
        holder.ItemQTY.setTag(position);
        holder.removeQtyArea.setTag(position);
        holder.addItemQtyArea.setTag(position);
        holder.img_delete.setTag(position);
        holder.itemName.setText(posData.get("ItemName"));
        holder.itemName.setHideUnderline(true);
        holder.ItemQTY.setText(posData.get("ItemQty"));

        holder.itemName.addTextChangedListener(new TextWatcher() {
            int selPosi = (int) holder.itemName.getTag();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Logger.d("CheckValPOs", "::" + selPosi + "::" + (list.size() - 1) + "::::" + s.toString());
                if (selPosi == list.size() - 1) {

                    list.get(selPosi).put("ItemName", s.toString());
                    if (Utils.checkText(s.toString()) && (selPosi == list.size() - 1)) {
                        addData(selPosi);
                    }
                    holder.addRemoveItemQtyArea.setVisibility(Utils.checkText(s.toString()) ? View.VISIBLE : View.GONE);

                } else {
                    holder.addRemoveItemQtyArea.setVisibility(Utils.checkText(s.toString()) ? View.VISIBLE : View.GONE);
                }


            }
        });

        holder.addRemoveItemQtyArea.setVisibility(Utils.checkText(list.get(position).get("ItemName")) ? View.VISIBLE : View.GONE);
        holder.addItemQtyArea.setOnClickListener(v -> {
            int selPosi = (int) holder.addItemQtyArea.getTag();
            String qty = holder.ItemQTY.getText().toString();
            int qtyVal = generalFunc.parseIntegerValue(1, qty);
            int qtyNw = qtyVal + 1;
            holder.ItemQTY.setText("" + qtyNw);
            list.get(selPosi).put("ItemQty", "" + qtyNw);
        });

        holder.img_delete.setOnClickListener(v -> {
            //int selPosi = (int) holder.img_delete.getTag();
            int selPosi = position;
            list.remove(selPosi);
            notifyItemRemoved(selPosi);
            notifyItemRangeChanged(selPosi, list.size());


        });

        holder.removeQtyArea.setOnClickListener(v -> {
            int selPosi = (int) holder.removeQtyArea.getTag();
            String qty = holder.ItemQTY.getText().toString();
            int qtyVal = generalFunc.parseIntegerValue(1, qty);
            int qtyNw = qtyVal - 1;

            if (qtyNw != 0) {
                holder.ItemQTY.setText("" + qtyNw);
                list.get(selPosi).put("ItemQty", "" + qtyNw);
            }

        });


    }

    private void addData(int position) {
        Logger.d("CheckValAddData", "::" + position);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemName", "");
        map.put("ItemQty", "1");
        map.put("StoreName", "");
        map.put("StoreId", "");
        map.put("itemId", "" + (list.size() + 1));
        map.put("StoreAddress", "");
        list.add(map);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface addStoreItemClickListener {
        void setAddStoreItemClick(int position, ArrayList<HashMap<String, String>> list);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialEditText itemName;
        RelativeLayout img_delete;
        ImageView itemBulletinImg;
        LinearLayout addRemoveItemQtyArea;
        LinearLayout removeQtyArea;
        LinearLayout mainDataArea;
        LinearLayout addItemQtyArea;
        MTextView ItemQTY;

        public ViewHolder(View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            img_delete = itemView.findViewById(R.id.img_delete);
            ItemQTY = itemView.findViewById(R.id.ItemQTY);
            itemBulletinImg = itemView.findViewById(R.id.itemBulletinImg);
            addRemoveItemQtyArea = itemView.findViewById(R.id.addRemoveItemQtyArea);
            removeQtyArea = itemView.findViewById(R.id.removeQtyArea);
            addItemQtyArea = itemView.findViewById(R.id.addItemQtyArea);
            mainDataArea = itemView.findViewById(R.id.mainDataArea);
            itemName.setText("Add Item Name");
        }
    }
}
