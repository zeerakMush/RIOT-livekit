package com.zeerak.riotlivekit

import android.util.Log
import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder
import io.livekit.android.R
import io.livekit.android.databinding.ParticipantItemBinding
import io.livekit.android.room.Room
import io.livekit.android.room.participant.ParticipantListener
import io.livekit.android.room.participant.RemoteParticipant
import io.livekit.android.room.track.*

class ParticipantItem(
    val room: Room,
    val remoteParticipant: RemoteParticipant
) :
    BindableItem<ParticipantItemBinding>() {

    private var videoBound = false

    override fun initializeViewBinding(view: View): ParticipantItemBinding {
        val binding = ParticipantItemBinding.bind(view)
        room.initVideoRenderer(binding.renderer)
        return binding
    }

    override fun bind(viewBinding: ParticipantItemBinding, position: Int) {
        viewBinding.run {

            remoteParticipant.listener = object : ParticipantListener {
                override fun onTrackSubscribed(
                    track: Track,
                    publication: RemoteTrackPublication,
                    participant: RemoteParticipant
                ) {
                    if (track is VideoTrack) {
                        setupVideoIfNeeded(track, viewBinding)
                    }
                }

                override fun onTrackUnpublished(
                    publication: RemoteTrackPublication,
                    participant: RemoteParticipant
                ) {
                    super.onTrackUnpublished(publication, participant)
                    Log.e ("LIVEKIT","Track unpublished" )
                }
            }
            val existingTrack = getVideoTrack()
            if (existingTrack != null) {
                setupVideoIfNeeded(existingTrack, viewBinding)
            }
        }
    }

    private fun getVideoTrack(): VideoTrack? {
        return remoteParticipant
            .videoTracks.values.firstOrNull()?.track as? VideoTrack
    }

    internal fun setupVideoIfNeeded(videoTrack: VideoTrack, viewBinding: ParticipantItemBinding) {
        if (videoBound) {
            return
        }

        videoBound = true
        Log.v ("LIVEKIT","adding renderer to $videoTrack" )
        videoTrack.addRenderer(viewBinding.renderer)
    }

    override fun unbind(viewHolder: GroupieViewHolder<ParticipantItemBinding>) {
        super.unbind(viewHolder)
        videoBound = false
    }

    override fun getLayout(): Int = R.layout.participant_item
}