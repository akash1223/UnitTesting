package com.inmoment.moments.login.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentLoginBinding
import com.inmoment.moments.framework.common.*
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.login.UserSignConfigWrapper
import com.inmoment.moments.login.model.UserDetails
import com.inmoment.moments.login.ui.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.*
import net.openid.appauth.connectivity.ConnectionBuilder
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import java.util.concurrent.atomic.AtomicReference

@AndroidEntryPoint
class LoginFragment : BaseFragment(), DoubleButtonDialogInf {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = baseBinding(
            R.layout.fragment_login, inflater, container,
            FragmentLoginBinding::class.java
        ).apply { loginViewModel = this@LoginFragment.loginViewModel }
        return mBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel.setErrorMessages(
            getString(R.string.invalid_email),
            getString(R.string.empty_password)
        )

        mBinding.signInBtn.setOnClickListener {
            pressButtonProgrammaticallyWithAnimation(mBinding.signInButton)
            loginViewModel.signInUser()
        }
        mBinding.signInWithGoogleButton.setOnClickListener {
            googleSignIn()
        }

        mBinding.emailET.doAfterTextChanged { text ->
            loginViewModel.email = text?.toString() ?: ""
        }
        mBinding.passwordET.doAfterTextChanged { text ->
            loginViewModel.password = text?.toString() ?: ""
        }
        loginViewModel.isFormValid.observe(viewLifecycleOwner, { valid ->
            mBinding.signInButton.isEnabled = valid ?: false
        })
        loginViewModel.makeSignInRequest.observe(viewLifecycleOwner, {
            enableProgressBar()

            // the stored AuthState is incomplete, so check if we are currently receiving the result of
            // the authorization flow from the browser.
            loginViewModel.checkLastLoginDetails(loginViewModel.email).observe(viewLifecycleOwner) {
                val op = loginViewModel.getUserSignManager(
                    UserDetails(
                        loginViewModel.email,
                        loginViewModel.password
                    )
                )

                op.successLiveData?.observe(viewLifecycleOwner, {


                    validateLoginDetails(it)
                })

                op.errorLiveData?.observe(viewLifecycleOwner, {
                    Logger.v(TAG, "Failed to get user configuration")
                })
            }
        })
        // mBinding.emailET.setText("pawan.patidar@inmoment.com")
        // mBinding.passwordET.setText("Moonraker@112233")
        //pressButtonProgrammaticallyWithAnimation(signInButton)
        // loginViewModel.signInUser()
    }

    private fun validateLoginDetails(
        userSignConfigWrapper: UserSignConfigWrapper
    ) {

        createAuthorizationService().performTokenRequest(
            userSignConfigWrapper.tokenRequest
        ) { response1, ex1 ->
            disableProgressBar()
            if (response1?.accessToken != null && response1.accessToken!!.isNotEmpty()) {

                val authState = AuthState()
                authState.update(loginViewModel.getAuthResponse(response1), ex1)
                authState.update(response1, ex1)
                createAuthorizationService().performTokenRequest(
                    authState.createTokenRefreshRequest()
                ) { response, ex ->


                    val accessToken = loginViewModel.saveAuthTokensToSharedPref(response!!, ex)
                    Logger.d(TAG, "AccessToken->" + response.accessToken!!)
                    accessToken.successLiveData?.observe(viewLifecycleOwner, {

                        handleSignInResult(mBinding.emailET.text.toString().trim())
                    })
                    accessToken.errorLiveData?.observe(viewLifecycleOwner, {
                        showAlertMessage(
                            getString(R.string.error),
                            R.string.error_login_failed,
                            R.string.ok
                        )
                    })
                }
            } else {
                showAlertMessage(
                    getString(R.string.error),
                    R.string.error_login_failed,
                    R.string.ok
                )
            }
        }
    }


    private fun googleSignIn() {
        enableProgressBar()
        val authRequest = AtomicReference<AuthorizationRequest>()
        val authIntent: AtomicReference<CustomTabsIntent> = AtomicReference<CustomTabsIntent>()
        val op = loginViewModel.getUserSignAuthManager(
            UserDetails(
                "passport_social",
                "eyJwcm92aWRlciI6Imdvb2dsZSJ9"
            )
        )
        op.successLiveData?.observe(viewLifecycleOwner, {
            authRequest.set(it.authorizationRequest.build())
            val intentBuilder: CustomTabsIntent.Builder =
                createAuthorizationService().createCustomTabsIntentBuilder(
                    authRequest.get().toUri()
                )
            authIntent.set(intentBuilder.build())
            val intent = createAuthorizationService().getAuthorizationRequestIntent(
                authRequest.get(),
                authIntent.get()
            )
            startActivityForResult(intent, GOOGLE_SIGN_IN)
        })

        op.errorLiveData?.observe(viewLifecycleOwner, {
            Logger.v(TAG, "Failed to get user configuration")
        })
    }


    private fun handleSignInResult(emailId: String) {
        loginViewModel.saveQuery(
            emailId
        )
        replaceFragment(getActivity(), WelcomeFragment.newInstance())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val authorizationResponse = AuthorizationResponse.fromIntent(data!!)
        if (requestCode == GOOGLE_SIGN_IN && authorizationResponse != null) {
            createAuthorizationService().performTokenRequest(
                authorizationResponse.createTokenExchangeRequest()
            ) { response, ex ->
                if (response?.accessToken != null && response.accessToken!!.isNotEmpty()) {
                    val accessToken =
                        loginViewModel.saveAuthTokensToSharedPref(
                            response,
                            ex,
                            authorizationResponse
                        )
                    accessToken.successLiveData?.observe(viewLifecycleOwner) {
                        //  val loginViewModel: LoginViewModel by viewModels()
                        val result = loginViewModel.getUserInfoFromOAuth()
                        result.successLiveData?.observe(viewLifecycleOwner, {
                            disableProgressBar()
                            it.email?.let { it1 -> handleSignInResult(it1) }
                        })
                        result.errorLiveData?.observe(viewLifecycleOwner, {
                            disableProgressBar()
                            showAlertMessage(
                                getString(R.string.error),
                                R.string.error_login_failed,
                                R.string.ok
                            )
                        })
                    }
                    accessToken.errorLiveData?.observe(viewLifecycleOwner, {
                        disableProgressBar()
                        showAlertMessage(
                            getString(R.string.error),
                            R.string.error_login_failed,
                            R.string.ok
                        )
                    })
                } else {
                    disableProgressBar()
                    showAlertMessage(
                        getString(R.string.error),
                        R.string.error_login_failed,
                        R.string.ok
                    )
                }
            }
        } else {
            disableProgressBar()
        }
    }

    private fun enableProgressBar() {
        mBinding.loginPB.visibility = View.VISIBLE
        blockUserTouchEvents(activity)
    }

    private fun disableProgressBar() {
        mBinding.loginPB.visibility = View.GONE
        unBlockUserTouchEvents(activity)
    }

    private fun createAuthorizationService(): AuthorizationService {
        val builder: AppAuthConfiguration.Builder = AppAuthConfiguration.Builder()
        builder.setConnectionBuilder(getConnectionBuilder())
        return AuthorizationService(this.requireContext(), builder.build())
    }

    private fun getConnectionBuilder(): ConnectionBuilder {
        return DefaultConnectionBuilder.INSTANCE
    }

    override fun onNegativeButtonClick(identifier: Int) {
        // do nothing }
    }

    fun browserLogin()
    {
        val clientId = "49c4a057-bb10-44dd-8723-e03977a97a5d"
        val redirectUri = Uri.parse("com.inmoment.moments:/oauth2redirect/gluu")
        val builder = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )
        builder.setScopes("profile")

        val authRequest = builder.build()
        val authService = AuthorizationService(requireContext())
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, 204)
    }

    override fun onPositiveButtonClick(identifier: Int) {
        // do nothing
    }
    val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse("https://identity.demo.inmoment.com/oxauth/restv1/authorize"), // authorization endpoint
        Uri.parse("https://identity.demo.inmoment.com/oxauth/restv1/token") // token endpoint
    )
}