package com.inmoment.moments.framework.ui

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.inmoment.moments.R
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.permission.PermissionHelperFragment

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

abstract class BaseActivity : AppCompatActivity() {

    var TAG = "BaseActivity"
    private var mPermissionHelperFragment: PermissionHelperFragment? = null
    var height: Int? = 0
    fun addPermissionHelperFragment() {
        mPermissionHelperFragment =
            supportFragmentManager.findFragmentByTag(PermissionHelperFragment.TAG) as PermissionHelperFragment?
        if (mPermissionHelperFragment == null) {
            val permissionHelperFragment = PermissionHelperFragment.getPermissionHelperFragment()
            supportFragmentManager.beginTransaction()
                .add(permissionHelperFragment, PermissionHelperFragment.TAG)
                .commit()
            mPermissionHelperFragment = permissionHelperFragment
        }
    }

    interface PermissionCheck {
        fun getPermissionCheckFragment(): PermissionHelperFragment?
    }
}

fun setStatusBarGradiant(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window: Window = activity.window
        val background = activity.resources.getDrawable(R.drawable.gradient_bg)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }
}
