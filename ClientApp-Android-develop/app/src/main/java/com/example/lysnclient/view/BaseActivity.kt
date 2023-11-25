package com.example.lysnclient.view

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lysnclient.R
import com.example.lysnclient.utils.CustomProgressBar
import com.example.lysnclient.utils.MixPanelData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_exit_assessment.*
import kotlinx.android.synthetic.main.dialog_no_internet.*

abstract class BaseActivity : AppCompatActivity() {
    abstract fun setup()
    private var mProgressDialog: CustomProgressBar? = null
    protected var mView: View? = null

    fun replaceFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String,
        addToBackStack: Boolean
    ) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
            .replace(containerViewId, fragment, fragmentTag)

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragmentTag)
        }
        fragmentTransaction.commit()
    }

    protected fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    protected fun showSnackMsg(msg: String, isLengthLong: Boolean = true) {
        var snackLength = Snackbar.LENGTH_LONG
        if (!isLengthLong) {
            snackLength = Snackbar.LENGTH_SHORT
        }
        mView?.let {
            val snackbar: Snackbar = Snackbar
                .make(mView!!, msg.trim(), snackLength)
            snackbar.show()
        }
    }

    override fun onDestroy() {
        MixPanelData.getInstance(this).flushMixPanel()
        super.onDestroy()
    }

    protected fun showToast(msg: String, isLengthLong: Boolean = true) {
        if (isLengthLong) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showNoInternetDialog() {
        val alertDialog = MaterialAlertDialogBuilder(
            this,
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        ).setView(R.layout.dialog_no_internet)
            .show()
        alertDialog.setCancelable(false)
        alertDialog.btn_dismiss_dialog.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun showExitConfirmDialog(
        dialogTitle: String, dialogMessage: String,
        positiveBtnText: String = getString(R.string.exit_title),
        negativeBtnText: String = getString(R.string.cancel_title),
        positiveBtnCallback: () -> Unit
    ) {
        val alertDialog = MaterialAlertDialogBuilder(
            this,
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        ).setView(R.layout.dialog_exit_assessment)
            .show()
        alertDialog.txt_dialog_title.text = dialogTitle
        alertDialog.txt_dialog_message.text = dialogMessage
        alertDialog.btn_exit.text = positiveBtnText
        alertDialog.btn_cancel.text = negativeBtnText

        alertDialog.setCancelable(false)
        alertDialog.btn_exit.setOnClickListener {
            positiveBtnCallback.invoke()
            alertDialog.dismiss()
        }
        alertDialog.btn_cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun showAlertDialogWithOK(
        title: String,
        message: String,
        okBtnCallback: () -> Unit
    ) {
        val alertDialog = MaterialAlertDialogBuilder(
            this,
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
            okBtnCallback.invoke()
        }
        alertDialog.show()
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

    fun launchHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
