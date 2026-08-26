package com.general.files

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getDrawable
import com.multixpro.user.R
import com.view.MTextView

class PreferenceDailog(val actContext: Context) {

    lateinit var hotoUseDialog: AlertDialog

    var generalFunc = GeneralFunctions(actContext)

    fun showPreferenceDialog(title: String, Msg: String, img: Int) {
        val builder = AlertDialog.Builder(actContext)
        val inflater =
            actContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.desgin_preference_help, null)
        builder.setView(dialogView)

        val imageArea = dialogView.findViewById<View>(R.id.imageArea) as LinearLayout
        val iamage_source = dialogView.findViewById<View>(R.id.iamage_source) as ImageView

        if (img != null && !img.equals("") && img != 0) {
            iamage_source!!.setImageResource(img)
            imageArea.visibility = View.VISIBLE
        } else {
            imageArea.visibility = View.GONE
        }

        val okTxt = dialogView.findViewById<View>(R.id.okTxt) as MTextView
        val titileTxt = dialogView.findViewById<View>(R.id.titileTxt) as MTextView
        val msgTxt = dialogView.findViewById<View>(R.id.msgTxt) as WebView

        titileTxt.text = title
        okTxt.text = generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT ")
        MyApp.executeWV(msgTxt, generalFunc, Msg)
        okTxt.setOnClickListener { hotoUseDialog.dismiss() }
        hotoUseDialog = builder.create()
        hotoUseDialog.setCancelable(true)
        if (generalFunc.isRTLmode == true) {
            generalFunc.forceRTLIfSupported(hotoUseDialog)
        }
        hotoUseDialog!!.getWindow()!!.setBackgroundDrawable(
            getDrawable(actContext, R.drawable.all_roundcurve_card)
        )
        hotoUseDialog.show()
    }

}