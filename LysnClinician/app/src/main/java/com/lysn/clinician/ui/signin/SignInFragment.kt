package com.lysn.clinician.ui.signin

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.lysn.clinician.R
import com.lysn.clinician.databinding.FragmentSignInBinding
import com.lysn.clinician.http.Resource
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.utils.NetworkManager
import com.lysn.clinician.utility.extensions.observeNetworkCall
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This class is used for sign in user
 */
class SignInFragment : BaseFragment() {

    private val mViewModel: SignInViewModel by viewModel()
    private lateinit var mSignInFragmentBinding: FragmentSignInBinding
    private val mNetworkManager : NetworkManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mSignInFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_in, container, false
        )
        return mSignInFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(view)
        setup()

    }

    override fun setup() {

        mSignInFragmentBinding.lifecycleOwner = this
        mSignInFragmentBinding.viewModel = mViewModel
        mSignInFragmentBinding.lifecycleOwner=viewLifecycleOwner
        mixPanelScreenVisitedEvent(MixPanelData.SIGN_IN_VIEW_SHOWN_EVENT)

        mViewModel.passwordField.observeForever {
            if(TextUtils.isEmpty(it))
                mSignInFragmentBinding.inputPassword.setEndIconTintList(ColorStateList.valueOf(requireContext().getColor(R.color.password_toggle_light)))
            else
                mSignInFragmentBinding.inputPassword.setEndIconTintList(ColorStateList.valueOf(requireContext().getColor(R.color.password_toggle_dark)))

        }
        // Sign in observer handled for store data locally and redirect to dashboard



            mViewModel.signInResponseLiveData.observe(viewLifecycleOwner, Observer { response ->
                if (response.status == Resource.Status.LOADING) showLoading() else hideLoading()

                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        mViewModel.saveDataInPreference(response.data)

                        // Create mix panel profile
                        MixPanelData.getInstance(requireActivity())
                            .createProfile(mSignInFragmentBinding.etEmailAddress.text.toString())

                        // Sign in complete mix panel event
                        mixPanelEvent(
                            MixPanelData.SIGN_IN_COMPLETED_EVENT
                        )
                        mNavController.navigate(R.id.action_SignInFragment_to_TermsAndConditionFragment)
                    }

                    Resource.Status.NO_INTERNET -> {
                        showNoInternetMaterialDialog()
                    }
                    Resource.Status.ERROR -> {
                        mViewModel.showErrorMessage()
                    }
                    else -> {
                        response.message?.let { showToast(it, false) }
                    }
                }
            })


        // This observer used for redirect to forgot password web view
        mViewModel.onForgotPasswordClickObservable.observe(viewLifecycleOwner, Observer {
            if(mNetworkManager.isNetworkAvailable) {
                mNavController.navigate(
                    R.id.action_SignInFragment_to_ForgotPasswordFragment)
                }else{
                    showNoInternetMaterialDialog()
                }
        })

        // Sign in button clicked mix panel event
        mViewModel.onMixPanelEventObservable.observe(viewLifecycleOwner, Observer {
            mixPanelButtonClickEvent(
                mSignInFragmentBinding.btnSignIn,
                MixPanelData.SIGN_IN_BUTTON_CLICKED_EVENT
            )
        })
    }


}