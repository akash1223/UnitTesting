package com.inmoment.moments.framework.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class GenericRecycleAdapter<T, D>(
    val context: Context,
    private var mArrayList: MutableList<T> = mutableListOf<T>(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract val layoutResId: Int
    abstract fun onBindData(model: T, position: Int, dataBinding: D)

    abstract fun onItemClick(model: T, position: Int)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutResId,
            parent,
            false
        )
        return ItemViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(
            mArrayList[position], position,
            (holder as GenericRecycleAdapter<*, *>.ItemViewHolder).mDataBinding as D
        )

        (holder.mDataBinding as ViewDataBinding).root
            .setOnClickListener { onItemClick(mArrayList[position], position) }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }


    fun addItems(arrayList: List<T>) {
        mArrayList.clear()
        mArrayList.addAll(arrayList)
        notifyDataSetChanged()
    }

    fun addItem(item: T) {
        mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return mArrayList[position]
    }

    inner class ItemViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var mDataBinding: D = binding as D
    }


}