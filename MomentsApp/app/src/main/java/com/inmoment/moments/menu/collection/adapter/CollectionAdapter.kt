package com.inmoment.moments.menu.collection.adapter

import android.content.Context
import com.inmoment.moments.R
import com.inmoment.moments.databinding.ListItemCollectionBinding
import com.inmoment.moments.framework.datamodel.CollectionModel
import com.inmoment.moments.framework.ui.GenericRecycleAdapter
import com.inmoment.moments.menu.collection.CollectionViewModel


class CollectionAdapter(
    context: Context,
    mArrayList: MutableList<CollectionModel> = mutableListOf(),
    val viewModel: CollectionViewModel,
    val collectionItemClickEvent: CollectionItemClickEvent
) :
    GenericRecycleAdapter<CollectionModel, ListItemCollectionBinding>(context, mArrayList) {
    override val layoutResId = R.layout.list_item_collection

    override fun onBindData(
        model: CollectionModel,
        position: Int,
        dataBinding: ListItemCollectionBinding
    ) {
         dataBinding.viewModel = viewModel
        dataBinding.item = model
    }

    override fun onItemClick(model: CollectionModel, position: Int) {
        if (viewModel.isDeleteEnable.value == true) {
            if (model.label != context.getString(R.string.my_favorites))
                collectionItemClickEvent.itemDelete(model)
        } else if (viewModel.addToFavorite.value == true) {
            if (!model.records.contains(viewModel.experienceId))
                collectionItemClickEvent.addToFavorite(model)
        } else {
            collectionItemClickEvent.itemSelect(model)
        }

    }

    interface CollectionItemClickEvent {
        fun itemSelect(collectionModel: CollectionModel)
        fun itemDelete(collectionModel: CollectionModel)
        fun addToFavorite(collectionModel: CollectionModel)
    }

}