package com.lysn.clinician.ui.join_consultation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lysn.clinician.R
import kotlinx.android.synthetic.main.layout_switch_camera_bottom_sheet.*


/**
 * Interface used for callback methods for bottom sheet
 */
interface SwitchCameraInterface {
    fun onSwitchCamera()
    fun onHelp()
}

/**
 *  This class is used to display bottom sheet for switching Camera
 */
class SwitchCameraDialogFragment(private val switchCameraInterface: SwitchCameraInterface) :
    BottomSheetDialogFragment(),
    View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_switch_camera_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        txt_switch_camera.setOnClickListener(this)
        txt_help.setOnClickListener(this)
        txt_cancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_switch_camera ->
            {
                dismiss()
                switchCameraInterface.onSwitchCamera()
            }
            R.id.txt_help -> {
                dismiss()
                switchCameraInterface.onHelp()
            }
            else -> dismiss()

        }
    }
}