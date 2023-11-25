package com.lysn.clinician.ui.video_session.client_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lysn.clinician.R
import com.lysn.clinician.model.VideoSessionConsultationDetails
import com.lysn.clinician.model.VideoSessionTokenResponse
import com.lysn.clinician.ui.video_session.ARG_VIDEO_SESSION_TOKEN
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_client_info.*


class ClientInfoFragment : Fragment() {

    private val  mapItem = mutableListOf<Item>()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_client_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapItem.clear()
        arguments?.takeIf { it.containsKey(ARG_VIDEO_SESSION_TOKEN) }?.apply {
            val videoSessionTokenResponse: VideoSessionTokenResponse? = this.getParcelable(ARG_VIDEO_SESSION_TOKEN)
            val consultationDetails: VideoSessionConsultationDetails? = videoSessionTokenResponse?.consultation
            consultationDetails?.client?.user?.let { user->
               user.getFullName?.let {
                   mapItem.add(ClientInfoHeaderItem(R.string.client_info))
                   user.getFullName?.let{ mapItem.add(ClientInfoItem(R.drawable.ic_user,user.getFullName)) }
                   user.address?.let{ mapItem.add(ClientInfoItem(R.drawable.ic_map_marker,user.address)) }
                   user.phone?.let{ mapItem.add(ClientInfoItem(R.drawable.ic_phone_client_info,user.phone)) }
                   user.email?.let{ mapItem.add(ClientInfoItem(R.drawable.ic_email,user.email)) }
               }
           }
            consultationDetails?.client?.let { client ->
                if(!client.nextOfKinName.isNullOrEmpty()){
                    mapItem.add(ClientInfoHeaderItem(R.string.clinet_next_to_kin))
                    var kinRelationship=""
                    if(!client.nextOfKinRelationship.isNullOrEmpty())
                        kinRelationship=" (${client.nextOfKinRelationship})"
                    mapItem.add(ClientInfoItem(R.drawable.ic_user,client.nextOfKinName +kinRelationship))
                    client.nextOfKinPhone?.let{ mapItem.add(ClientInfoItem(R.drawable.ic_phone_client_info,client.nextOfKinPhone)) }

                }
            }
            val groupAdapter = GroupAdapter<GroupieViewHolder>()
            recycler_client_info.apply {
                groupAdapter.addAll(mapItem)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
        }
    }
}