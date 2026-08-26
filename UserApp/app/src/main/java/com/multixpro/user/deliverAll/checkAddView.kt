package com.multixpro.user.deliverAll

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.activity.ParentActivity
import com.multixpro.user.R
import com.general.files.GeneralFunctions
import com.utils.Logger
import com.view.MTextView
import com.view.editBox.MaterialEditText
import java.util.*


class checkAddView : ParentActivity() {
    var list = ArrayList<HashMap<String, String>>()
    lateinit var additemTxt:MTextView
    lateinit var itemContainer:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_add_view)

        additemTxt=findViewById(R.id.additemTxt) as MTextView
        itemContainer=findViewById(R.id.itemContainer) as LinearLayout
        additemTxt.setOnClickListener {

            Logger.d("GenericTextWatcherSize","::"+list.size)
            if(list.size==0) {
                addData(0)
            }
            else
            {
                addData(list.size+1)
            }
        }


    }
   lateinit var contentView :Any
    fun addView( pos:Int )
    {
        val contentView = View.inflate(this, R.layout.design_add_item, null)

        var itemName: MaterialEditText
        var img_delete: RelativeLayout? = null
        var itemBulletinImg: ImageView? = null
        var addRemoveItemQtyArea: LinearLayout? = null
        var removeQtyArea: LinearLayout?=null
        var mainDataArea: LinearLayout? = null
        var addItemQtyArea: LinearLayout? = null
        var ItemQTY: MTextView? = null
        itemName = contentView.findViewById(R.id.itemName)
        ItemQTY = contentView.findViewById(R.id.ItemQTY)
        addRemoveItemQtyArea = contentView.findViewById(R.id.addRemoveItemQtyArea)
        addItemQtyArea = contentView.findViewById(R.id.addItemQtyArea)
        removeQtyArea = contentView.findViewById(R.id.removeQtyArea)
        img_delete = contentView.findViewById(R.id.img_delete)
        addRemoveItemQtyArea!!.visibility=View.VISIBLE
        itemName.setTag(pos)
        itemName.setText(list.get(pos).get("ItemName"))
        ItemQTY.setText(list.get(pos).get("ItemQty"))
        itemName.addTextChangedListener(GenericTextWatcher(pos))
        itemName.requestFocus()

        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        addItemQtyArea.setOnClickListener {


            var qty:String = ItemQTY.getText().toString();
            var qtyVal:Int = GeneralFunctions.parseIntegerValue(1, qty);
            var qtyNw:Int = qtyVal + 1;
            ItemQTY.setText("" + qtyNw);
            list.get(pos).put("ItemQty", "" + qtyNw);
//            var map = HashMap<String, String>()
//            map=list.get(pos)
//            map["ItemQty"] = qtyNw.toString()
//            list.set(pos,map);
        }

        removeQtyArea.setOnClickListener {

            val qty: String = ItemQTY.getText().toString()
            val qtyVal = GeneralFunctions.parseIntegerValue(1, qty)
            val qtyNw = qtyVal - 1
            if (qtyNw != 0) {
                ItemQTY.setText("" + qtyNw)
                list.get(pos).put("ItemQty", "" + qtyNw);
            }

        }
        img_delete.setOnClickListener(View.OnClickListener {

           list.remove(list.get(pos))
            if (itemContainer.getChildCount() > 0) {
                itemContainer.removeAllViewsInLayout()
            }
            checkList()
        })


        itemContainer.addView(contentView)
    }
   inner  class GenericTextWatcher(val view: Int) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
Logger.d("GenericTextWatcher","::"+view)
            var map = HashMap<String, String>()
            map=list.get(view)
            map["ItemName"] = text
             list.set(view,map)
        }

    }


    fun checkList()
    {
   var j:Int=0
      for (i in list)
      {
          addView(j)
          j=j+1
      }
    }


    private fun addData(position: Int) {

        val map = HashMap<String, String>()
        map["ItemName"] = ""
        map["ItemQty"] = "1"
        map["StoreName"] = ""
        map["StoreId"] = ""
        map["itemId"] = "" + (list.size + 1)
        map["StoreAddress"] = ""
        map["pos"] = "$position"
        list.add(map)


        if (itemContainer.getChildCount() > 0) {
            itemContainer.removeAllViewsInLayout()
        }
        checkList()
    }
}