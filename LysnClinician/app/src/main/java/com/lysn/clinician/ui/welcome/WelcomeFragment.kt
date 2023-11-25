package com.lysn.clinician.ui.welcome

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.lysn.clinician.R
import com.lysn.clinician.databinding.FragmentWelcomeBinding
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.utils.NetworkManager
import org.koin.android.ext.android.inject

/**
 * This class is used for redirect user to login screen and register page
 */
class WelcomeFragment : BaseFragment() {

    private lateinit var mWelcomeFragmentBinding: FragmentWelcomeBinding
    private val mNetworkManager : NetworkManager by inject()

    // created for future
    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mWelcomeFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_welcome, container, false
        )

        return mWelcomeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun setup() {
        mWelcomeFragmentBinding.lifecycleOwner = this
        mWelcomeFragmentBinding.clickHandler = WelcomeClickHandler()
        mWelcomeFragmentBinding.tvSubheading.text = (Html.fromHtml(getString(R.string.welcome_subheading)));
        mixPanelScreenVisitedEvent(MixPanelData.WELCOME_VIEW_SHOWN_EVENT)
    }

    // This class used for handle views click event
    inner class WelcomeClickHandler {

        fun onLoginButtonClicked() {
            mNavController.navigate(
                R.id.action_WelcomeFragment_to_SignInFragment
            )
            mixPanelButtonClickEvent(mWelcomeFragmentBinding.btnLogIn, MixPanelData.LOGIN_BUTTON_CLICKED_EVENT)
        }
        fun onRegisterButtonClicked(){
            if(mNetworkManager.isNetworkAvailable) {
                mNavController.navigate(
                    R.id.action_WelcomeFragment_to_SignUpFragment
                )
            }else{
                showNoInternetMaterialDialog()
            }
            mixPanelButtonClickEvent(mWelcomeFragmentBinding.btnRegister, MixPanelData.REGISTER_WITH_LYSN_BUTTON_CLICKED_EVENT)
        }
    }
}