package com.lysn.clinician.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.lysn.clinician.R
import com.lysn.clinician.ui.base.BaseActivity
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.PreferenceUtil
import kotlinx.android.synthetic.main.activity_authenticate.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class AuthenticateActivity : BaseActivity() {


    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)
        setup()
    }

    override fun setup() {
        val preferenceUtil : PreferenceUtil = get()
        val navHostFragment = nav_startup_fragment as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.auth_nav_graph)
        navController = navHostFragment.navController

        val destination = if (!preferenceUtil.isUserLogin())
                            R.id.WelcomeFragment
                           else if(preferenceUtil.isUserLogin() && !preferenceUtil.isTermsAndConditionAccepted())
                             R.id.TermsAndConditionFragment
                           else navGraph.startDestination
        navGraph.startDestination = destination
        navController.graph = navGraph
    }
}