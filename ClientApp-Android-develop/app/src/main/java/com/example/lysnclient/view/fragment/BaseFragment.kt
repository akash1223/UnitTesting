package com.example.lysnclient.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.example.lysnclient.R
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.CustomProgressBar
import com.example.lysnclient.utils.MixPanelData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_no_internet.*
import kotlinx.android.synthetic.main.dialog_no_internet.view.*
import org.json.JSONObject


abstract class BaseFragment : Fragment() {

    abstract fun setup()
    private var mProgressDialog: CustomProgressBar? = null
    protected var mView: View? = null

    fun replaceFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String,
        addToBackStack: Boolean
    ) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            .add(containerViewId, fragment, fragmentTag)

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragmentTag)
        }
        fragmentTransaction.commit()
    }

    protected fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    protected fun showSnackMsg(msg: String, isLengthLong: Boolean = true) {
        var snackLength = Snackbar.LENGTH_LONG
        if (!isLengthLong) {
            snackLength = Snackbar.LENGTH_SHORT
        }
        mView?.let {
            val snackBar: Snackbar = Snackbar
                .make(mView!!, msg.trim(), snackLength)
            snackBar.show()
        }
    }

    protected fun showToast(msg: String, isLengthLong: Boolean = true) {
        if (isLengthLong) {
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressBar(requireActivity())
        }
        if (!mProgressDialog?.isShowing!!) {
            mProgressDialog?.show()
        }
    }

    protected fun getProgressBar(): CustomProgressBar {
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressBar(requireActivity())
        }
        return mProgressDialog!!
    }

    open fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog?.isShowing!!) {
            mProgressDialog?.dismiss()
        }
    }

    protected fun simpleDialog() {
        val viewGroup: ViewGroup = activity!!.findViewById(android.R.id.content)
        val dialogView: View = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_no_internet, viewGroup, false)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()
        dialogView.btn_dismiss_dialog.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun showNoInternetDialog() {
        val alertDialog = MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        ).setView(R.layout.dialog_no_internet)
            .show()
        alertDialog.btn_dismiss_dialog.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun addVisitedQueEvent(
        assessmentCode: String, assQuestion: String, eventName: String
    ) {
        val jsonEvent = JSONObject()
        jsonEvent.put(
            MixPanelData.KEY_ASSESSMENT_CODE,
            assessmentCode
        )
        jsonEvent.put(
            MixPanelData.KEY_QUESTION,
            assQuestion
        )
        MixPanelData.getInstance(requireActivity()).addEvent(jsonEvent, eventName)
    }

    fun addQueAnsweredEvent(
        assessmentCode: String,
        assQuestion: String,
        answer: String,
        eventName: String,
        isAnswerUpdated: Boolean = false,
        oldAnswer: String = AppConstants.EMPTY_VALUE
    ) {
        val jsonEvent = JSONObject()
        jsonEvent.put(
            MixPanelData.KEY_ASSESSMENT_CODE,
            assessmentCode
        )
        jsonEvent.put(
            MixPanelData.KEY_QUESTION,
            assQuestion
        )
        if (isAnswerUpdated) {
            jsonEvent.put(
                MixPanelData.KEY_PREVIOUS_ANSWER,
                oldAnswer
            )
            jsonEvent.put(
                MixPanelData.KEY_New_ANSWER,
                answer
            )
        } else {
            jsonEvent.put(
                MixPanelData.KEY_ANSWER,
                answer
            )
        }

        MixPanelData.getInstance(requireActivity()).addEvent(jsonEvent, eventName)
    }

    fun showAlertDialogWithOK(
        title: String,
        message: String,
        okBtnCallback: () -> Unit
    ) {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
            okBtnCallback.invoke()
        }
        alertDialog.show()
    }
}


