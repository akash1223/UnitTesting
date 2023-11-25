package com.example.lysnclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lysnclient.databinding.ListItemWbtOutputScreenBinding
import com.example.lysnclient.viewmodel.WBTOutputScreenViewModel

class WbtOutputScreenAdapter(
    context: Context,
    private var dataList: List<String>,
    private val viewModel: WBTOutputScreenViewModel
) : RecyclerView.Adapter<WbtOutputScreenAdapter.ItemHolder>() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var binding: ListItemWbtOutputScreenBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WbtOutputScreenAdapter.ItemHolder {
        binding = ListItemWbtOutputScreenBinding.inflate(inflater, parent, false)
        binding.viewModel = viewModel
        return ItemHolder(binding)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: WbtOutputScreenAdapter.ItemHolder, position: Int) {
        holder.bind(dataList[position])
    }

    internal fun setDataList(list: List<String>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    inner class ItemHolder(private val binding: ListItemWbtOutputScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.item = item
        }

    }
}