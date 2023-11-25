package com.lysn.clinician.ui.video_session


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lysn.clinician.model.ConsultationDetails
import com.lysn.clinician.model.VideoSessionConsultationDetails
import com.lysn.clinician.model.VideoSessionTokenResponse
import com.lysn.clinician.ui.video_session.chat.ChatFragment
import com.lysn.clinician.ui.video_session.client_info.ClientInfoFragment

const val ARG_VIDEO_SESSION_TOKEN = "videoSessionTokenResponse"
@Suppress("DEPRECATION")
class PagerAdapter(fm: FragmentManager,private val videoSessionTokenResponse: VideoSessionTokenResponse) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle().apply {
            // Our object is just an integer :-P
            putParcelable(ARG_VIDEO_SESSION_TOKEN, videoSessionTokenResponse)
        }
        return when (position) {
            0 ->
            {
                val fragment = ClientInfoFragment()
                fragment.arguments = bundle
                fragment
            }
            1 ->{
                val fragment = ChatFragment()
                fragment.arguments = bundle
                fragment
            }
            2 -> AssessmentsFragment()
            else -> AssessmentsFragment()

        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
       return  ""
    }


}