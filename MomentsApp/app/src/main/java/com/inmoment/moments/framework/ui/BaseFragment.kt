package com.inmoment.moments.framework.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.inmoment.moments.R
import com.inmoment.moments.framework.common.AlertParams
import com.inmoment.moments.framework.common.CustomProgressBar
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.common.showAlertDialog
import com.inmoment.moments.framework.utils.OnSwipeTouchListener
import com.lysn.clinician.utility.extensions.getToolbarHeight
import com.lysn.clinician.utility.extensions.justTry


/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

abstract class BaseFragment : Fragment() {
    var TAG = "BaseFragment"
    private lateinit var ctx: Context
    lateinit var activity: Activity
    private var mProgressDialog: CustomProgressBar? = null
    var mViewBinding: ViewDataBinding? = null
    lateinit var mNavController: NavController
    var backButton: ImageView? = null
    private var toolbarTitle: TextView? = null
    private var isUpNavigation: Boolean = false
    private var isShowBackNavigation: Boolean = false
    private var previousScreenName: String = ""
    var hasInitializedRootView = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.ctx = context
        TAG = context.javaClass.simpleName
        if (context is Activity) {
            this.activity = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        justTry {
            mNavController = Navigation.findNavController(view)
            backButton = view.findViewById<ImageView>(R.id.iv_back)
            toolbarTitle = view.findViewById<TextView>(R.id.txt_title)
            backButton?.let {
                it.setOnClickListener {
                    if (!mNavController.popBackStack()) {
                        requireActivity().onBackPressed()
                    } else {
                        hideKeyboard()
                    }
                }
            }
        }
    }

    protected fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    protected fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showBackNavigation() {
        backButton?.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        if (activity.findViewById<BottomNavigationView>(R.id.nav_view).isVisible) {
            isUpNavigation = true
            activity.findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.GONE
        }
    }

    fun setToolbarTitle(screenTitle: String) {
        toolbarTitle?.let {
            it.text = screenTitle
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (isUpNavigation) {
            activity.findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.VISIBLE
        }

    }


    protected fun showAlertMessage(
        title: String,
        messageId: Int,
        positiveButtonId: Int
    ) {
        val alertParams =
            AlertParams(
                title,
                messageId,
                ctx.getString(positiveButtonId),
            )

        showAlertDialog(ctx, alertParams)
    }

    protected fun showError(error: Int) {
        Logger.v(TAG, "Error")
        showAlertDialog(
            this.requireContext(), AlertParams(
                getString(R.string.error), error, getString(
                    R.string.ok
                )
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showCardViewSnackBar(
        parentView: View,
        customSnackView: View,
        displayDuration: Int = 300
    ): Snackbar {
        val snackBar = Snackbar.make(parentView, "", Snackbar.LENGTH_LONG)
        snackBar.duration = displayDuration
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        snackBarLayout.setPadding(0, 0, 0, 0)
        snackBarLayout.addView(customSnackView, 0)
        snackBarLayout.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeUp() {
                super.onSwipeLeft()
                snackBar.animationMode = ANIMATION_MODE_SLIDE
                snackBar.dismiss()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                snackBar.animationMode = ANIMATION_MODE_SLIDE
                snackBar.dismiss()
            }
        })
        val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.setMargins(0, requireActivity().getToolbarHeight(), 0, 0)
        snackBar.view.layoutParams = params
        snackBar.show()
        return snackBar;
    }

    fun <VB : ViewDataBinding> baseBinding(layoutId : Int,
                                           inflater: LayoutInflater,
                                           container: ViewGroup?,
                                           vb: Class<VB>
    ): VB {
        val fragmentStationFinderBinding: VB =
            DataBindingUtil.inflate(inflater, layoutId, container, false)
        fragmentStationFinderBinding.lifecycleOwner = this
        return fragmentStationFinderBinding
    }
    protected fun showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = CustomProgressBar(requireActivity())
        }
        if (!mProgressDialog?.isShowing!!) {
            mProgressDialog?.show()
        }
    }

    protected fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog?.isShowing!!) {
            mProgressDialog?.dismiss()
        }
    }

    override fun onDestroyView() {
        hideLoading()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mViewBinding = null
        super.onDestroy()
    }

}

