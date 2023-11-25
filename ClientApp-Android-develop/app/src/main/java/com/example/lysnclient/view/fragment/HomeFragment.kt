package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment() {

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        return homeFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun setup() {
        homeFragmentBinding.lifecycleOwner = this
        mView = homeFragmentBinding.homeFragmentLayout
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment().apply { }
    }
}