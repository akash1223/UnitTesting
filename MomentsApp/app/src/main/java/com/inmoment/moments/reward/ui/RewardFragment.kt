package com.inmoment.moments.reward.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentRewardBinding
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.home.ActivityLogEnum
import com.inmoment.moments.reward.RewardViewModel
import com.lysn.clinician.utility.extensions.getQueryTextChangeStateFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class RewardFragment : BaseFragment() {

    private val rewardViewModel: RewardViewModel by viewModels()
    private lateinit var mBinding: FragmentRewardBinding

    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val safeArgs: RewardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = baseBinding(
            R.layout.fragment_reward,
            inflater,
            container,
            FragmentRewardBinding::class.java
        )
        mBinding.viewModel = rewardViewModel
        mBinding.lifecycleOwner = this
        mBinding.fragment = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }


    @ExperimentalCoroutinesApi
    private fun setData() {
        showBackNavigation()
        hideBottomNavigation()
        setToolbarTitle(resources.getString(R.string.reward_screen))
        mBinding.toolbar.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
            mNavController.navigate(R.id.action_UserProfileFragment)
        }
        mBinding.btnCancel.setOnClickListener {
            mNavController.popBackStack()
        }

        mBinding.edtName.threshold = 3

        arrayAdapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_list_item_1,
            listOf()
        )
        mBinding.edtName.setAdapter(arrayAdapter)
        rewardViewModel.setAutoCompleteEvent(mBinding.edtName.getQueryTextChangeStateFlow())
        rewardViewModel.searchEmployeeList.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                arrayAdapter.clear()
                arrayAdapter.addAll(it.map { it1 -> it1.firstName + " " + it1.lastName })
                mBinding.edtName.showDropDown()
                rewardViewModel.applyFilter.value = true
            } else {
                //  TODO("Not yet implemented")
            }
        })
        mBinding.edtName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && rewardViewModel.nameField.value.isNullOrEmpty()) {
                mBinding.edtName.setText(AppConstants.EMPTY_VALUE, false)
            }
        }
        rewardViewModel.applyFilter.observe(viewLifecycleOwner, {
            arrayAdapter.filter.filter(mBinding.edtName.text, null)
        })
        mBinding.edtName.overScrollMode = View.OVER_SCROLL_ALWAYS
        mBinding.edtName.setOnItemClickListener { parent, view, position, id ->
            rewardViewModel.itemSelected.set(true)
            rewardViewModel.selectedSearchItem.value =
                rewardViewModel.searchEmployeeList.value?.get(position)
            rewardViewModel.nameField.value =
                rewardViewModel.searchEmployeeList.value?.get(position)?.userName
        }
    }

    fun sendDataButtonClick() {

        if (!rewardViewModel.isSamePerson()) {
            Logger.d(TAG, "Send Reward Data To Server")
            val callback = rewardViewModel.postRewardPoints()
            showLoading()
            callback.successLiveData?.observe(viewLifecycleOwner) { it ->
                setLogActivity()
            }
            callback.errorLiveData?.observe(viewLifecycleOwner) {
                hideLoading()
                showError(R.string.error_reward_recognition)
            }
        } else {
            showError(R.string.error_self_recognition)
        }
    }

    private fun setLogActivity() {
        val callback = rewardViewModel.logActivity(
            safeArgs.feeds?.experienceId!!, ActivityLogEnum.EMPLOYEE_RECOGNITION,
            mBinding.edtRecognitionReason.text.toString()
        )
        showLoading()
        callback.successLiveData?.observe(viewLifecycleOwner) { it ->
            hideLoading()
            mNavController.previousBackStackEntry?.savedStateHandle?.set(
                AppConstants.NAV_BACK_REWARD,
                it
            )
            mNavController.popBackStack()
        }
        callback.errorLiveData?.observe(viewLifecycleOwner) {
            hideLoading()
            showError(R.string.error_reward_recognition)
        }
    }

    fun cancelButtonClick() {
        mNavController.popBackStack()
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }
}