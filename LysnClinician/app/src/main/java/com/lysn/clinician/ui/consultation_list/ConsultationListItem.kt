package com.lysn.clinician.ui.consultation_list


import com.lysn.clinician.R
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.utility.ConsultationStatusCode
import com.lysn.clinician.utils.BindingAdapters
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_consultation_list.*


class ConsultationListItem(
    val consultationDetails: ConsultationDetails, private val clickListener: OnItemClickListener
    , private val readyToJoin: Boolean = false
) : Item(consultationDetails.id.hashCode().toLong()) {

    override fun getLayout() = R.layout.item_consultation_list

    override fun bind(viewBinding: GroupieViewHolder, position: Int) {
        val statusColor =
            ConsultationStatusCode(viewBinding.containerView.context)

        viewBinding.txt_client_name.text =
            "${consultationDetails.client?.firstName} ${consultationDetails.client?.lastName}"
        consultationDetails.dateTime?.let {
            BindingAdapters.bindServerDate(
                viewBinding.txt_date_time,
                consultationDetails.dateTime
            )
        }
        if (readyToJoin) {
            consultationDetails.timerDisplayName?.let {
                viewBinding.txt_session_status.text = consultationDetails.timerDisplayName
            }

            consultationDetails.timerStatus?.let { statusColor.getColorRes(it) }?.let {
                viewBinding.txt_session_status.setTextColor(
                    it
                )
            }

        } else {
            consultationDetails.statusForClientDisplay?.let {
                viewBinding.txt_session_status.text = consultationDetails.statusForClientDisplay
            }
            consultationDetails.status?.let { statusColor.getColorRes(it) }?.let {
                viewBinding.txt_session_status.setTextColor(
                    it
                )
            }
        }

        viewBinding.txt_duration.text = "${consultationDetails.durationMinutes.toString()}  min"

        BindingAdapters.bindCallType(
            viewBinding.txt_duration,
            consultationDetails.type
        )

        viewBinding.cons_view.setOnClickListener {
            clickListener.onItemClick(this, viewBinding.root)
        }
    }

}
