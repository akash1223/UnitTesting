package com.example.lysnclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ListItemUserDashboardBinding
import com.example.lysnclient.model.DashboardUser
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.viewmodel.HomeDashboardViewModel

class AdapterUserDashboard(
    private var context: Context,
    private var dataList: List<DashboardUser>,
    private val viewModel: HomeDashboardViewModel
) : RecyclerView.Adapter<AdapterUserDashboard.ItemHolder>() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var binding: ListItemUserDashboardBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        binding = ListItemUserDashboardBinding.inflate(inflater)
        binding.viewModel = viewModel
        return ItemHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(dataList[position])
        holder.position = position

        if (position == AppConstants.USER_CHAT_POSITION) {
            binding.tvUserSubTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.greenTextColor
                )
            )
        }
    }

    internal fun setDataList(list: List<DashboardUser>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    inner class ItemHolder(private val binding: ListItemUserDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DashboardUser) {
            binding.userDashboard = item
        }

        fun setPosition(position: Int) {
            binding.itemPosition = position
        }
    }

}
