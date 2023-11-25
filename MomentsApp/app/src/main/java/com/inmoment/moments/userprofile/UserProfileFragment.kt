package com.inmoment.moments.userprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.inmoment.moments.BuildConfig
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentUserProfileBinding
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.login.LoginActivity
import com.inmoment.moments.userprofile.ui.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


private const val TAG = "UserProfileFragment"

@AndroidEntryPoint
class UserProfileFragment : BaseFragment() {

    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private lateinit var mBinding: FragmentUserProfileBinding

    @Inject
    lateinit var sharedPrefsInf: SharedPrefsInf
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = baseBinding(
            R.layout.fragment_user_profile,
            inflater,
            container,
            FragmentUserProfileBinding::class.java
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun setData() {
        val firstName = userProfileViewModel.getUserFirstName()
        val lastName = userProfileViewModel.getUserLastName()
        var firstNameCapitalized = ""
        var lastNameCapitalized = ""
        if (!firstName.isNullOrEmpty()) {
            firstNameCapitalized =
                capitalizeTextViewFirstLetter(firstName)
        }
        if (!lastName.isNullOrEmpty()) {
            lastNameCapitalized = capitalizeTextViewFirstLetter(lastName)

        }
        if (firstNameCapitalized.isNotEmpty() || lastNameCapitalized.isNotEmpty()) {
            mBinding.userNameTV.visibility = View.VISIBLE
            mBinding.userNameTV.text = "$firstNameCapitalized $lastNameCapitalized"
        } else {
            mBinding.userNameTV.visibility = View.GONE
        }

        setHyperlinkText()
        mBinding.versionTV.append(" " + BuildConfig.VERSION_NAME)


        //logout button click listener
        mBinding.logoutButton.setOnClickListener {
            userProfileViewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity.finish()

        }
        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add("English (United States)")

        // Creating adapter for spinner
        val spinnerArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item_user_profile, categories)
        // Drop down layout style - list view with radio button
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        mBinding.languageSpinner.adapter = spinnerArrayAdapter

        //as we are supporting only english language for initial release, language spinner is disabled, later , we will enable it again in future release.
        mBinding.languageSpinner.isEnabled = false
        mBinding.toolbar.findViewById<ImageView>(R.id.iv_back)?.setOnClickListener {
            mNavController.popBackStack()
        }
        mBinding.toolbar.findViewById<ImageView>(R.id.iv_menu)?.visibility = View.GONE
        showBackNavigation()
        hideBottomNavigation()
        setToolbarTitle(resources.getString(R.string.user_profile))

    }

    private fun setHyperlinkText() {
        val hyperLinkText = getString(R.string.product_tour)
        val content = SpannableString(hyperLinkText)
        content.setSpan(UnderlineSpan(), 0, hyperLinkText.length, 0)
        mBinding.productTourHyperlinkTV.text = content
    }

    private fun capitalizeTextViewFirstLetter(username: String): String {
        return username.substring(0, 1).toUpperCase(Locale.ROOT) + username.substring(1)
            .toLowerCase(Locale.ROOT)
    }
}