package com.lysn.clinician.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.R
import com.lysn.clinician.utility.CustomProgressBar
import com.lysn.clinician.utility.extensions.justTry
import com.lysn.clinician.utils.MixPanelData
import org.json.JSONObject



abstract class BaseFragment : Fragment() {



    abstract fun setup()
    private var mProgressDialog: CustomProgressBar? = null
    lateinit var mNavController: NavController
    var backButton: ImageView? = null
    private var toolbarTitle: TextView? = null
    private var tickReceiver: BroadcastReceiver? = null

    lateinit var interfaceTimeChange: InterfaceTimeChange

    protected fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        backButton = view.findViewById<ImageView>(R.id.iv_back)
        toolbarTitle = view.findViewById<TextView>(R.id.txt_title)
        backButton?.let {
            it.setOnClickListener {
               if(!mNavController.popBackStack())
                {
                    requireActivity().onBackPressed()
                }
            }
        }
    }


    fun setToolbarTitle(screenTitle: String) {
        toolbarTitle?.let {
            it.text = screenTitle
        }
    }

    protected fun showToast(msg: String, isLengthLong: Boolean = false) {
        if (isLengthLong) {
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }
    protected fun showDebugToast(msg: String, isLengthLong: Boolean = false) {
        if (BuildConfig.DEBUG) {
            if (isLengthLong) {
                Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
                return
            }
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
        }
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

    protected fun showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressBar(requireActivity())
        }
        if (!mProgressDialog?.isShowing!!) {
            mProgressDialog?.show()
        }
    }
    protected fun setTimeChangeBroadcast(timeChangedListener: InterfaceTimeChange)
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
            requireContext().registerReceiver(
                tickReceiver as BroadcastReceiver,
                IntentFilter(Intent.ACTION_TIME_TICK))
        }

    }

    open fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog?.isShowing!!) {
            mProgressDialog?.dismiss()
        }
    }

    fun showNoInternetMaterialDialog() {
        val alertDialog = MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        ).setView(R.layout.dialog_no_internet)
            .show()
        alertDialog.findViewById<MaterialButton>(R.id.btn_dismiss_dialog)?.setOnClickListener {
            alertDialog.dismiss()
        }
    }


    fun mixPanelButtonClickEvent(button: Button, eventName:String){

        val properties = JSONObject()
        properties.put(MixPanelData.KEY_SCREEN_NAME, mNavController.currentDestination?.label.toString())
        properties.put(MixPanelData.BUTTON_TITLE,button.text )
        MixPanelData.getInstance(requireActivity())
            .addEvent(
                properties,
                eventName
            )
    }

    fun mixPanelButtonClickEvent(textView:TextView, eventName:String,context: Context){
        val props = JSONObject()
        props.put(MixPanelData.KEY_SCREEN_NAME, context.javaClass.simpleName)
        props.put(MixPanelData.BUTTON_TITLE,textView.text )
        MixPanelData.getInstance(requireActivity())
            .addEvent(
                props,
                eventName
            )
    }



    fun mixPanelScreenVisitedEvent(eventName: String) {
        MixPanelData.getInstance(requireActivity())
            .addEvent(
                MixPanelData.KEY_SCREEN_NAME,
                mNavController.currentDestination?.label.toString(),
                eventName
            )
    }
    fun mixPanelEvent(eventName: String){
        MixPanelData.getInstance(requireActivity())
            .addEvent(
                eventName
            )
    }

    override fun onDestroy() {
        tickReceiver = null
        super.onDestroy()
    }

    override fun onPause() {

            tickReceiver?.let {
                requireContext().unregisterReceiver(it)
            }
        super.onPause()
    }

    override fun onResume() {

            tickReceiver?.let {
                requireContext().registerReceiver(
                    it,
                    IntentFilter(Intent.ACTION_TIME_TICK)
                )
                interfaceTimeChange.timeChanged()
            }

        super.onResume()
    }

    interface InterfaceTimeChange
    {
        fun timeChanged()
    }


}


