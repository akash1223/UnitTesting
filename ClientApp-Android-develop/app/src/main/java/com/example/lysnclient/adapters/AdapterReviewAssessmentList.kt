package com.example.lysnclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lysnclient.databinding.ListItemReviewAssessmentBinding
import com.example.lysnclient.model.AssessmentAnswer
import com.example.lysnclient.viewmodel.ReviewAssessmentViewModel

class AdapterReviewAssessmentList(
    context: Context,
    private var dataList: List<AssessmentAnswer>,
    private val viewModel: ReviewAssessmentViewModel
) : RecyclerView.Adapter<AdapterReviewAssessmentList.ItemHolder>() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var binding: ListItemReviewAssessmentBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        binding = ListItemReviewAssessmentBinding.inflate(inflater)
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

    internal fun setDataList(list: List<AssessmentAnswer>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    inner class ItemHolder(private val binding: ListItemReviewAssessmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AssessmentAnswer) {
            binding.assessmentAnswer = item
        }

        fun setPosition(position: Int) {
            binding.itemPosition = position
        }
    }

}