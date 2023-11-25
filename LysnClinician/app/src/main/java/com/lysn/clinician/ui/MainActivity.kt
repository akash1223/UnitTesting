package com.lysn.clinician.ui

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lysn.clinician.R
import com.lysn.clinician.ui.base.BaseActivity
import com.lysn.clinician.ui.consultation_details.ConsultationDetailsViewModel
import com.lysn.clinician.ui.consultation_list.ConsultationListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val mViewModel by viewModel<ConsultationListViewModel>()
    override fun setup() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

}