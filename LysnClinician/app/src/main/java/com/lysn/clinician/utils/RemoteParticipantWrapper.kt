package com.lysn.clinician.utils

import com.twilio.video.*
import timber.log.Timber

interface RemoteParticipantInterface
{
     fun addRemoteParticipantVideo(remoteVideoTrack: VideoTrack)

     fun removeParticipantVideo(remoteVideoTrack: VideoTrack)
}

class RemoteParticipantWrapper(val remoteParticipantInterface: RemoteParticipantInterface): RemoteParticipant.Listener{
    override fun onAudioTrackPublished(remoteParticipant: RemoteParticipant,
                                       remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i( "onAudioTrackPublished: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                "name=${remoteAudioTrackPublication.trackName}]")

    }

    override fun onAudioTrackUnpublished(remoteParticipant: RemoteParticipant,
                                         remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
        Timber.i(  "onAudioTrackUnpublished: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                "name=${remoteAudioTrackPublication.trackName}]")

    }

    override fun onDataTrackPublished(remoteParticipant: RemoteParticipant,
                                      remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        Timber.i( "onDataTrackPublished: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                "name=${remoteDataTrackPublication.trackName}]")

    }

    override fun onDataTrackUnpublished(remoteParticipant: RemoteParticipant,
                                        remoteDataTrackPublication: RemoteDataTrackPublication
    ) {
        Timber.i( "onDataTrackUnpublished: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                "name=${remoteDataTrackPublication.trackName}]")

    }

    override fun onVideoTrackPublished(remoteParticipant: RemoteParticipant,
                                       remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i( "onVideoTrackPublished: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                "name=${remoteVideoTrackPublication.trackName}]")

    }

    override fun onVideoTrackUnpublished(remoteParticipant: RemoteParticipant,
                                         remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
        Timber.i( "onVideoTrackUnpublished: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                "name=${remoteVideoTrackPublication.trackName}]")

    }

    override fun onAudioTrackSubscribed(remoteParticipant: RemoteParticipant,
                                        remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                        remoteAudioTrack: RemoteAudioTrack
    ) {
        Timber.i(  "onAudioTrackSubscribed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                "name=${remoteAudioTrack.name}]")

    }

    override fun onAudioTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                          remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                          remoteAudioTrack: RemoteAudioTrack
    ) {
        Timber.i( "onAudioTrackUnsubscribed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                "name=${remoteAudioTrack.name}]")

    }

    override fun onAudioTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                                twilioException: TwilioException
    ) {
        Timber.i( "onAudioTrackSubscriptionFailed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                "name=${remoteAudioTrackPublication.trackName}]" +
                "[TwilioException: code=${twilioException.code}, " +
                "message=${twilioException.message}]")

    }

    override fun onDataTrackSubscribed(remoteParticipant: RemoteParticipant,
                                       remoteDataTrackPublication: RemoteDataTrackPublication,
                                       remoteDataTrack: RemoteDataTrack
    ) {
        Timber.i(  "onDataTrackSubscribed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                "name=${remoteDataTrack.name}]")

    }

    override fun onDataTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                         remoteDataTrackPublication: RemoteDataTrackPublication,
                                         remoteDataTrack: RemoteDataTrack
    ) {
        Timber.i( "onDataTrackUnsubscribed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                "name=${remoteDataTrack.name}]")

    }

    override fun onDataTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                               remoteDataTrackPublication: RemoteDataTrackPublication,
                                               twilioException: TwilioException
    ) {
        Timber.i(  "onDataTrackSubscriptionFailed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                "name=${remoteDataTrackPublication.trackName}]" +
                "[TwilioException: code=${twilioException.code}, " +
                "message=${twilioException.message}]")

    }

    override fun onVideoTrackSubscribed(remoteParticipant: RemoteParticipant,
                                        remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                        remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i(  "onVideoTrackSubscribed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                "name=${remoteVideoTrack.name}]")
        remoteParticipantInterface.addRemoteParticipantVideo(remoteVideoTrack)
    }

    override fun onVideoTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                          remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                          remoteVideoTrack: RemoteVideoTrack
    ) {
        Timber.i( "onVideoTrackUnsubscribed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                "name=${remoteVideoTrack.name}]")
        remoteParticipantInterface.removeParticipantVideo(remoteVideoTrack)
    }

    override fun onVideoTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                                twilioException: TwilioException
    ) {
        Timber.i( "onVideoTrackSubscriptionFailed: " +
                "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                "name=${remoteVideoTrackPublication.trackName}]" +
                "[TwilioException: code=${twilioException.code}, " +
                "message=${twilioException.message}]")

    }

    override fun onAudioTrackEnabled(remoteParticipant: RemoteParticipant,
                                     remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
    }

    override fun onVideoTrackEnabled(remoteParticipant: RemoteParticipant,
                                     remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
    }

    override fun onVideoTrackDisabled(remoteParticipant: RemoteParticipant,
                                      remoteVideoTrackPublication: RemoteVideoTrackPublication
    ) {
    }

    override fun onAudioTrackDisabled(remoteParticipant: RemoteParticipant,
                                      remoteAudioTrackPublication: RemoteAudioTrackPublication
    ) {
    }
}