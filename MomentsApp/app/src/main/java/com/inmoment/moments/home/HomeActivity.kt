package com.inmoment.moments.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inmoment.moments.R
import com.inmoment.moments.framework.ui.BaseActivity
import com.inmoment.moments.framework.ui.setStatusBarGradiant
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.M)
class HomeActivity : BaseActivity() {

    companion object {
        fun newInstance() = HomeActivity()
    }

    private var isUserProfileScreen: Boolean = false
    // private lateinit var navController: NavController

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarGradiant(this)
        setContentView(R.layout.home_activity)
        // setSupportActionBar(findViewById(R.id.toolbar))
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        //   navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        navView.setOnNavigationItemReselectedListener {}

        /*navView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack()
                }
                R.id.navigation_menu -> {
                    navController.navigate(R.id.navigation_menu)
                }
            }
            true
        }*/

    }


}



