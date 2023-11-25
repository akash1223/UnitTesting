package com.inmoment.moments.menu.collection.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentCollectionBinding
import com.inmoment.moments.framework.common.AppConstants.NAV_BACK_ADD_TO_FAV
import com.inmoment.moments.framework.common.AppConstants.NAV_BACK_MOMENT_TYPE
import com.inmoment.moments.framework.datamodel.CollectionModel
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.home.MomentType
import com.inmoment.moments.home.model.Feed
import com.inmoment.moments.menu.collection.CollectionViewModel
import com.inmoment.moments.menu.collection.adapter.CollectionAdapter
import com.lysn.clinician.utility.extensions.getHtmlSpannedString
import com.lysn.clinician.utility.extensions.observeOnce
import com.lysn.clinician.utility.extensions.setStyle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionFragment : BaseFragment() {

    private val collectionViewModel: CollectionViewModel by viewModels()
    private lateinit var mBinding: FragmentCollectionBinding

    private lateinit var collectionAdapter: CollectionAdapter


    companion object {
        @JvmStatic
        fun newInstance(feed: Feed) = CollectionFragment().apply {
            arguments = Bundle().apply {
                putParcelable("feedModel", feed)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = baseBinding(
            R.layout.fragment_collection,
            inflater,
            container,
            FragmentCollectionBinding::class.java
        )
        mBinding.viewModel = collectionViewModel
        mBinding.lifecycleOwner = this
        mBinding.fragment = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycleViewData(view)
        setData()
    }

    private fun setData() {
        mNavController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            "create_collection"
        )?.observe(viewLifecycleOwner) {
            if (collectionViewModel.addToFavorite.value == true)
                addToFavouriteCollection(collectionName = it)
            else
                createCollection(it)
            mNavController.currentBackStackEntry?.savedStateHandle?.remove<String>("create_collection")
        }
        collectionViewModel.isDeleteEnable.observe(viewLifecycleOwner) {
            collectionAdapter.notifyDataSetChanged()
        }
    }

    private fun setRecycleViewData(view: View) {
        arguments?.get("feedModel")?.let { any ->
            val experienceId = (any as Feed).experienceId
            experienceId?.let {
                mBinding.txtAction.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_add_collection,
                    0,
                    0,
                    0
                )
                mBinding.txtAction.text = getString(R.string.create_new_collection_msg)
                mBinding.txtAction.setOnClickListener { addCollection() }
                collectionViewModel.showActionButton(true)
                collectionViewModel.setAddToFavorite(true)
                collectionViewModel.experienceId = it
            }
        }
        view.findViewById<RecyclerView>(R.id.recycler_collection).apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            collectionAdapter = CollectionAdapter(
                context,
                viewModel = collectionViewModel,
                collectionItemClickEvent = collectionItemClickEvent()
            )
            adapter = collectionAdapter
        }

        collectionViewModel.collectionList.observe(viewLifecycleOwner) {
            collectionAdapter.addItems(it)
        }
        collectionViewModel.initViewModel.observe(viewLifecycleOwner) {
            collectionViewModel.getDataSourceList().observeOnce(viewLifecycleOwner) {
                getCollectionList(it.filter { it1 -> it1.isNonDxDataSource }
                    .map { it2 -> it2.dataSourceId })
            }
        }
        collectionViewModel.getActiveDataSource().observe(viewLifecycleOwner) {
            if (it != null && !it.isNonDxDataSource) {
                mBinding.imgBtnAddCollection.visibility = View.GONE
            }
        }
    }

    //region API call methods

    private fun addToFavouriteCollection(
        collectionModel: CollectionModel? = null,
        collectionName: String? = null
    ) {
        val callback = collectionViewModel.addToFavouriteCollection(collectionModel, collectionName)
        showLoading()
        callback.successLiveData?.observe(viewLifecycleOwner) { it ->
            hideLoading()
            mNavController.previousBackStackEntry?.savedStateHandle?.set(
                NAV_BACK_ADD_TO_FAV,
                it.data.collections
            )
            mNavController.popBackStack()

        }
        callback.errorLiveData?.observe(viewLifecycleOwner) {
            hideLoading()
            showError(R.string.error_create_and_add_to_fav_collection)
        }
    }

    private fun getCollectionList(list: List<String>) {

        if (list.isNullOrEmpty()) {
            mBinding.groupButton.visibility = View.GONE
            return
        }

        mBinding.loader.visibility = View.VISIBLE
        val savedViewsData =
            collectionViewModel.getCollectionInfo(list, getString(R.string.my_favorites))
        savedViewsData.successLiveData?.observe(viewLifecycleOwner) {
            mBinding.loader.visibility = View.GONE
            collectionViewModel.setCollectionData(it)
            it.data.collections?.let { it1 ->
                collectionAdapter.addItems(it1)
            }
        }
        savedViewsData.errorLiveData?.observe(viewLifecycleOwner) {
            mBinding.loader.visibility = View.GONE
            showError(R.string.error_getting_collection_data)
        }

    }

    private fun createCollection(collectionName: String) {
        val callback = collectionViewModel.createCollection(collectionName)

        callback.successLiveData?.observe(viewLifecycleOwner) { it ->
            hideLoading()
            if (collectionViewModel.addToFavorite.value == true) {
                mNavController.previousBackStackEntry?.savedStateHandle?.set(
                    NAV_BACK_ADD_TO_FAV,
                    it.data.collections
                )
                mNavController.popBackStack()
            } else {
                collectionViewModel.addItemToCollectionList(it.data.collections)
                collectionCreatedSnackBar(it.data.collections.label)
            }
        }
        callback.errorLiveData?.observe(viewLifecycleOwner) {
            hideLoading()
            showError(R.string.error_create_collection)
        }
    }

    private fun deleteCollection(collectionModel: CollectionModel) {
        showLoading()
        val savedViewsData = collectionViewModel.deleteCollection(collectionModel.id)
        savedViewsData.successLiveData?.observe(viewLifecycleOwner) {
            hideLoading()
            deletedCollectionSnackBar(collectionModel.label)
            collectionViewModel.showActionButton(false)
            collectionViewModel.setDeleteAction(false)
            collectionViewModel.deleteItemFromCollectionList(it.data.collections)
            mNavController.previousBackStackEntry?.savedStateHandle?.set(
                NAV_BACK_MOMENT_TYPE,
                MomentType.COLLECTION
            )
            collectionViewModel.activeCollection?.let { it1 ->
                if (it1.id == it.data.collections.id)
                    collectionViewModel.storeCollectionData(null)
            }
        }
        savedViewsData.errorLiveData?.observe(viewLifecycleOwner) {
            hideLoading()
            showError(R.string.error_delete_collection_data)
        }
    }
    //endregion

    //region Adapter Item Callback
    private fun collectionItemClickEvent() = object : CollectionAdapter.CollectionItemClickEvent {
        override fun itemSelect(collectionModel: CollectionModel) {
            collectionModel.let {
                if (collectionViewModel.storeCollectionData(collectionModel)) {
                    mNavController.previousBackStackEntry?.savedStateHandle?.set(
                        NAV_BACK_MOMENT_TYPE,
                        MomentType.COLLECTION
                    )
                }
            }
            mNavController.popBackStack()
        }

        override fun itemDelete(collectionModel: CollectionModel) {
            collectionModel.let {
                deleteCollectionAlertDialog(collectionModel)
            }
        }

        override fun addToFavorite(collectionModel: CollectionModel) {
            collectionModel.let {
                addToFavouriteCollection(it)
            }
        }
    }
    //endregion

    //region Button Click Events

    fun backNavigation() {
        if (collectionViewModel.addToFavorite.value == true) {
            collectionViewModel.showActionButton(false)
            collectionViewModel.setAddToFavorite(false)
            mNavController.popBackStack()
        } else {
            collectionViewModel.showActionButton(false)
            collectionViewModel.setDeleteAction(false)
        }
    }

    fun deleteActionButtonClick() {
        mBinding.txtAction.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete, 0, 0, 0)
        mBinding.txtAction.text = getString(R.string.delete_collection_msg)
        collectionViewModel.showActionButton(true)
        collectionViewModel.setDeleteAction(true)
    }

    fun addCollection() {
        val direction =
            AddCollectionFragmentDirections.actionAddCollectionFragment(collectionViewModel.getCollectionData())
        mNavController.navigate(direction)

    }
    //endregion

    //region Toast Message
    private fun collectionCreatedSnackBar(collectionName: String) {

        val customSnackView: View = layoutInflater.inflate(
            R.layout.layout_create_collection_success_dialog,
            null
        )
        customSnackView.findViewById<MaterialTextView>(R.id.txt_collection_name).text =
            getString(R.string.create_collection_message, collectionName)
        showCardViewSnackBar(mBinding.root, customSnackView)
    }

    private fun deletedCollectionSnackBar(collectionName: String) {

        val customSnackView: View = layoutInflater.inflate(
            R.layout.layout_create_collection_success_dialog,
            null
        )
        val txtMessage = customSnackView.findViewById<MaterialTextView>(R.id.txt_collection_name)
        txtMessage.gravity = Gravity.START
        txtMessage.setStyle(R.style.MyTitleStyle)
        txtMessage.text =
            context?.getHtmlSpannedString(R.string.deleted_collection_message, collectionName)
        showCardViewSnackBar(mBinding.root, customSnackView)
    }

    private fun deleteCollectionAlertDialog(collectionModel: CollectionModel) {
        val alertDialog = MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        ).setView(R.layout.layout_delete_collection_dialog)
            .show()
        alertDialog.findViewById<MaterialTextView>(R.id.txt_dialog_message)?.text =
            getString(R.string.delete_collection_message, collectionModel.label)
        alertDialog.findViewById<MaterialButton>(R.id.btn_cancel)?.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.findViewById<MaterialButton>(R.id.btn_proceed)?.setOnClickListener {
            alertDialog.dismiss()
            deleteCollection(collectionModel)
        }
    }
    //endregion
}
