package com.inmoment.moments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.inmoment.moments.R
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.framework.ui.CustomTabLayout
import com.inmoment.moments.framework.ui.ViewPagerAdapter
import com.inmoment.moments.home.MomentType
import com.inmoment.moments.menu.collection.ui.CollectionFragment
import com.inmoment.moments.menu.saved_views.ui.SaveViewsFragment
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MenuFragment"

@AndroidEntryPoint
class MenuFragment : BaseFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var tabLayout: CustomTabLayout
    private val safeArgs: MenuFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.view_pager)
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragment(SaveViewsFragment(), "Saved Views")
        safeArgs.feeds?.let {
            viewPagerAdapter.addFragment(CollectionFragment.newInstance(it), "Collection")
        }?: kotlin.run {
            viewPagerAdapter.addFragment(CollectionFragment(), "Collection")
        }

        viewPager.adapter = viewPagerAdapter
        tabLayout = view.findViewById(R.id.tab_layout)
        view.findViewById<ImageView>(R.id.iv_menu).setOnClickListener {
            mNavController.navigate(R.id.action_UserProfileFragment)
        }

        /* val custView = tabLayout.getTabAt(0)?.customView
          if(custView is AppCompatTextView) {
              custView.typeface =
                  ResourcesCompat.getFont(requireContext(), R.font.ibm_plex_sans_light)
          }*/

        tabLayout.setupWithViewPager(viewPager, true)
        tabLayout.setTitlesAtTabs(viewPagerAdapter.getPageTitleList())

        safeArgs.menu?.let {

            if(it == MomentType.COLLECTION.value)
            {
                viewPager.setCurrentItem(1,false)
            }
        }
    }
}