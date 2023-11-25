package com.example.lysnclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lysnclient.databinding.ListItemAssessmentTypeBinding
import com.example.lysnclient.model.AssessmentType
import com.example.lysnclient.viewmodel.ListOfAssessmentViewModel
import kotlinx.android.synthetic.main.list_item_assessment_type.view.*

class AdapterAssessmentList(
    context: Context,
    private var dataList: List<AssessmentType>,
    private val viewModel: ListOfAssessmentViewModel
) : RecyclerView.Adapter<AdapterAssessmentList.ItemHolder>() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    private lateinit var binding: ListItemAssessmentTypeBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        binding = ListItemAssessmentTypeBinding.inflate(inflater)
        binding.viewModel = viewModel
        return ItemHolder(binding)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(dataList[position])
        holder.position = position
    }

    internal fun setDataList(list: List<AssessmentType>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    inner class ItemHolder(private val binding: ListItemAssessmentTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AssessmentType) {
            binding.item = item
        }

        fun setPosition(position: Int) {
            binding.itemPosition = position
        }
    }

}