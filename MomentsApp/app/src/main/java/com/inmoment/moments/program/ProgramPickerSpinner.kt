package com.inmoment.moments.program

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.inmoment.moments.home.model.AccountProgram

class ProgramPickerSpinner constructor(context: Context, resource: Int) :
    ArrayAdapter<AccountProgram>(context, resource) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }
}