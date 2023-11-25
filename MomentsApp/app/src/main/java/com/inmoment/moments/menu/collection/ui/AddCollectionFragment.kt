package com.inmoment.moments.menu.collection.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentAddCollectionBinding
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.menu.collection.AddCollectionViewModel
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class AddCollectionFragment : BaseFragment() {

    private val collectionViewModel: AddCollectionViewModel by viewModels()
    private lateinit var mBinding: FragmentAddCollectionBinding
    private val safeArgs: AddCollectionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = baseBinding(
            R.layout.fragment_add_collection,
            inflater,
            container,
            FragmentAddCollectionBinding::class.java
        )
        mBinding.viewModel = collectionViewModel
        mBinding.lifecycleOwner = this
        mBinding.fragment = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        showBackNavigation()
        hideBottomNavigation()
        setToolbarTitle(resources.getString(R.string.moments))
        safeArgs.collection?.let { collectionViewModel.setCollectionData(it) }
        mBinding.toolbar.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
            mNavController.navigate(R.id.action_UserProfileFragment)
        }
        collectionViewModel.collectionName.observe(viewLifecycleOwner) {
            collectionViewModel.validateCollectionName(it)
        }
        showSoftKeyboard(mBinding.editCollectionName)
        collectionViewModel.enableSaveButton.observe(viewLifecycleOwner, Observer {
            if(it)
            {
                mBinding.editCollectionName.imeOptions = EditorInfo.IME_ACTION_NONE
                mBinding.editCollectionName.requestFocus()
            }
            else
            {
                mBinding.editCollectionName.imeOptions = EditorInfo.IME_ACTION_DONE
                mBinding.editCollectionName.requestFocus()

            }
        })
        mBinding.editCollectionName.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEND) {
               // sendMessage()
                hideKeyboard()
                handled = true;
            }
            return@setOnEditorActionListener handled;
        }
    }

    fun createCollection() {
        hideKeyboard()
        mNavController.previousBackStackEntry?.savedStateHandle?.set(
            "create_collection",
            mBinding.editCollectionName.text.toString()
        )
        mNavController.popBackStack()
    }

    override fun onDestroy() {
        hideKeyboard()
        super.onDestroy()
    }
}