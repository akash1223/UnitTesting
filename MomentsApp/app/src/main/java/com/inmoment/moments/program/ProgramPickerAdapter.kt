package com.inmoment.moments.program

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.inmoment.moments.R
import com.inmoment.moments.databinding.ListItemProgramPickerBinding
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.ui.GenericRecycleAdapter
import com.inmoment.moments.program.model.Program
import com.lysn.clinician.utility.extensions.getBackgroundByShape


abstract class ProgramPickerAdapter(
    context: Context,
    var sharedPrefsInf: SharedPrefsInf,
    mArrayList: MutableList<Program> = mutableListOf()
) :
    GenericRecycleAdapter<Program, ListItemProgramPickerBinding>(context, mArrayList) {
    override val layoutResId = R.layout.list_item_program_picker


    override fun onBindData(
        model: Program,
        position: Int,
        dataBinding: ListItemProgramPickerBinding
    ) {

        val (defaultAccountId, defaultProgramId) = sharedPrefsInf.getDefaultAccountAndProgramId()
        if (defaultAccountId == model.accountId && defaultProgramId == model.id)
            dataBinding.blueDotIV.visibility = View.VISIBLE
        else
            dataBinding.blueDotIV.visibility = View.INVISIBLE

        dataBinding.programNameTV.text = model.programName
        dataBinding.programDomainIV.text = model.cloudShortText
        dataBinding.programPickerLL.getBackgroundByShape(R.id.shape_cloud_type)
            .setColor(model.domainColor)
        (dataBinding.programDomainIV.background as GradientDrawable).setColor(model.domainColor)
    }
}


/*

class ProgramPickerAdapter(private val context: FragmentActivity, private val programList : List<Program>) : RecyclerView.Adapter<ProgramPickerAdapter.ProgramPickerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramPickerHolder {
        return ProgramPickerHolder(
            parent.inflate(
                R.layout.list_item_program_picker,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProgramPickerHolder, position: Int) {
        holder.programDomainIV.setImageResource(programList[position].programDomainColor)
        holder.programNameTV.text = programList[position].programName
        holder.programTimePeriod.text = programList[position].timePeriod

        when (programList[position].domainColor) {
            "blue" -> {
                holder.programPickerLL.background =
                    context.getDrawable(R.drawable.border_circular_program_picker_blue)
            }
            "green" -> {
                holder.programPickerLL.background =
                    context.getDrawable(R.drawable.border_circular_program_picker_green)
            }
            "violet" -> {
                holder.programPickerLL.background =
                    context.getDrawable(R.drawable.border_circular_program_picker_violet)
            }
        }
        if (position == 0){
            holder.blueDotIV.visibility = View.VISIBLE
        }else{
            holder.blueDotIV.visibility = View.INVISIBLE
        }

        holder.programPickerLL.setOnClickListener {
            replaceFragment(context, HomeFragment.newInstance())
        }
    }

    override fun getItemCount(): Int {
        return programList.size
    }

    class ProgramPickerHolder(view : View) : RecyclerView.ViewHolder(view){
        val programDomainIV : ImageView = view.findViewById(R.id.programDomainIV)
        val programNameTV : TextView = view.findViewById(R.id.programNameTV)
        val programTimePeriod : TextView = view.findViewById(R.id.programTimePeriodTV)
        val programPickerLL : LinearLayout = view.findViewById(R.id.programPickerLL)
        val blueDotIV : ImageView = view.findViewById(R.id.blueDotIV)
    }

}*/

