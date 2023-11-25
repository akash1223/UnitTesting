package com.lysn.clinician.ui.join_consultation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lysn.clinician.R
import kotlinx.android.synthetic.main.layout_help_dialog_fragment.*

class HelpDialogFragment() : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.layout_help_dialog_fragment, container, false)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }
    override fun getTheme(): Int {
        return R.style.Theme_App_Dialog_FullScreen
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_btn_dismiss.setOnClickListener {
            dismiss()
        }
    }
}