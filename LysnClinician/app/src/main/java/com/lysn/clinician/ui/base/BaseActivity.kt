package com.lysn.clinician.ui.base

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.R
import com.lysn.clinician.utility.CustomProgressBar
import com.lysn.clinician.utility.extensions.justTry
import com.lysn.clinician.utils.MixPanelData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject


abstract class BaseActivity :AppCompatActivity () {

    abstract fun setup()




    val activityScope = CoroutineScope(Dispatchers.Main)
    private var mProgressDialog: CustomProgressBar? = null
    lateinit var context: Context
    lateinit var interfaceTimeChange: BaseFragment.InterfaceTimeChange
    private var tickReceiver: BroadcastReceiver? = null


    protected fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        context=this
    }

    override fun onStart() {
        super.onStart()
      //  requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        MixPanelData.getInstance(this).flushMixPanel()
        tickReceiver = null
        super.onDestroy()
    }

    protected fun showToast(msg: String, isLengthLong: Boolean = false) {
        if (isLengthLong) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showSnackBar(view: View, message: String, isLengthLong: Boolean = false) {
        if (isLengthLong) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .show()
            return
        }
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .show()
    }
    protected fun showSnackBarWithOK(view: View, message: String, isLengthLong: Boolean = false) {
        if (isLengthLong) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .show()
            return
        }
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .show()
    }
    protected fun showDebugToast(msg: String, isLengthLong: Boolean = false) {
        if (BuildConfig.DEBUG) {
            if (isLengthLong) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                return
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
    protected fun showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressBar(this)
        }
        if (!mProgressDialog?.isShowing!!) {
            mProgressDialog?.show()
        }
    }

    open fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog?.isShowing!!) {
            mProgressDialog?.dismiss()
        }
    }

    fun showNoInternetMaterialDialog() {
        val alertDialog = MaterialAlertDialogBuilder(
            this,
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        ).setView(R.layout.dialog_no_internet)
            .show()
        alertDialog.findViewById<MaterialButton>(R.id.btn_dismiss_dialog)?.setOnClickListener {
            alertDialog.dismiss()
        }
    }


    fun mixPanelButtonClickEvent(textView: TextView, eventName: String, context: Context){
        val props = JSONObject()
        props.put(MixPanelData.KEY_SCREEN_NAME, context.javaClass.simpleName)
        props.put(MixPanelData.BUTTON_TITLE, textView.text)
        MixPanelData.getInstance(this)
            .addEvent(
                props,
                eventName
            )
    }



    fun mixPanelButtonClickEvent(button: Button, eventName: String){

        val properties = JSONObject()
        properties.put(MixPanelData.KEY_SCREEN_NAME, context.javaClass.simpleName)
        properties.put(MixPanelData.BUTTON_TITLE, button.text)
        MixPanelData.getInstance(this)
            .addEvent(
                properties,
                eventName
            )
    }

    fun mixPanelScreenVisitedEvent(eventName: String, userName: String, context: Context) {

        val props = JSONObject()
        props.put(MixPanelData.KEY_USER_NAME, userName)
        props.put(MixPanelData.KEY_SCREEN_NAME, context.javaClass.simpleName)
        MixPanelData.getInstance(this)
            .addEvent(
                props,
                eventName
            )
    }

    fun mixPanelActionEvent(eventName: String) {

        MixPanelData.getInstance(this)
            .addEvent(
                MixPanelData.KEY_SCREEN_NAME, context.javaClass.simpleName, eventName
            )
    }

    fun mixPanelScreenVisitedEvent(eventName: String) {

        MixPanelData.getInstance(this)
            .addEvent(
                eventName
            )
    }
    protected fun setTimeChangeBroadcast(timeChangedListener: BaseFragment.InterfaceTimeChange)
    {
        this.interfaceTimeChange = timeChangedListener
        interfaceTimeChange.timeChanged()
        if(tickReceiver == null) {
            tickReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action!!.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                        interfaceTimeChange.timeChanged()
                    }
                }
            }
           this.registerReceiver(
               tickReceiver as BroadcastReceiver,
               IntentFilter(Intent.ACTION_TIME_TICK)
           )
        }
    }
    override fun onPause() {

        justTry {
                tickReceiver?.let { this.unregisterReceiver(it) }
        }
        super.onPause()
    }

    override fun onResume() {

        justTry {
              tickReceiver?.let {
                   this.registerReceiver(
                       it,
                       IntentFilter(Intent.ACTION_TIME_TICK)
                   )
                  interfaceTimeChange.timeChanged()
                }
        }
        super.onResume()
    }

    interface InterfaceTimeChange
    {
        fun timeChanged()
    }


}
