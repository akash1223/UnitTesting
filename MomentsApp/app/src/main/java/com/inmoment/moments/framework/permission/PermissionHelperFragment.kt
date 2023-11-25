package com.inmoment.moments.framework.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.jetbrains.annotations.NotNull

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

class PermissionHelperFragment : Fragment() {
    companion object {
        const val TAG = "PermissionHelperFragment"
        private const val PERMISSIONS_REQUEST = 1

        @NotNull
        fun getPermissionHelperFragment(): PermissionHelperFragment {
            return PermissionHelperFragment()
        }
    }

    private lateinit var permissionList: Array<String>
    private lateinit var onPermissionCompletionCallback: OnPermissionCompletionCallback

    interface OnPermissionCompletionCallback {
        fun onPermissionGranted()
        fun userDeniedPermissions(nonGrantedPermissionList: List<String>)

        fun userPermanentlyDeniedPermissions(
            showRationalePermissionList: List<String>,
            nonGrantedRationalePermissionList: List<String>
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun checkPermissions(
        context: Context,
        onPermissionCompletionCallback: OnPermissionCompletionCallback,
        permissionList: Array<String>
    ) {

        this.permissionList = permissionList
        this.onPermissionCompletionCallback = onPermissionCompletionCallback
        var isPermissionGranted = true
        for (permission in permissionList) {
            val permissionResult = ContextCompat.checkSelfPermission(context, permission)
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = false
                break
            }
        }

        if (isPermissionGranted) {
            // Call back permission granted
            onPermissionCompletionCallback.onPermissionGranted()
        } else {
            // request permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionList, PERMISSIONS_REQUEST)
            } else {
                // Call back permission granted
                onPermissionCompletionCallback.onPermissionGranted()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                handlePermissionCallBack(permissions, grantResults)
            }
        }
    }

    private fun handlePermissionCallBack(
        permissions: Array<String>,
        grantResults: IntArray
    ) {
// If request is cancelled, the result arrays are empty.
        val deniedPermissionList = ArrayList<String>()
        if (grantResults.size >= permissionList.size) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permissions[i])
                    break
                }
            }
            if (deniedPermissionList.isEmpty()) {
// Call back permission granted
                onPermissionCompletionCallback.onPermissionGranted()
            } else {
                handleDeniedPermissions(deniedPermissionList)
            }
        }
    }

    private fun handleDeniedPermissions(deniedPermissionList: ArrayList<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val showRationalePermissionList = ArrayList<String>()
            for (permission in permissionList) {
                val showRationale = shouldShowRequestPermissionRationale(
                    permission
                )
                if (!showRationale) {
                    showRationalePermissionList.add(permission)
                }
            }

// Call back to give Rationale permission list
            if (showRationalePermissionList.isEmpty()) {
                onPermissionCompletionCallback
                    .userDeniedPermissions(deniedPermissionList)
            } else {
                onPermissionCompletionCallback
                    .userPermanentlyDeniedPermissions(
                        showRationalePermissionList, deniedPermissionList
                    )
            }
        }
    }
}
