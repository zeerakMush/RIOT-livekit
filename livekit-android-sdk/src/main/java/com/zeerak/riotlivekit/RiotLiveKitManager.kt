package com.zeerak.riotlivekit

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.livekit.android.ConnectOptions
import io.livekit.android.LiveKit
import io.livekit.android.room.Room
import io.livekit.android.room.RoomListener
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.participant.RemoteParticipant
import org.webrtc.EglBase.Context
import org.webrtc.audio.JavaAudioDeviceModule

class RiotLiveKitManager(private var mContext : android.content.Context) {

    private var url : String? = null
    private var token : String? = null
    private var mPreferencesHelper : PreferencesHelper = PreferencesHelper(mContext)
    private val mutableRoom = MutableLiveData<Room>()
    private val mutableRemoteParticipants = MutableLiveData<List<RemoteParticipant>>()

    val room: LiveData<Room> = mutableRoom
    val remoteParticipants: LiveData<List<RemoteParticipant>> = mutableRemoteParticipants

    fun init(url : String, token : String){
        this.url = url
        this.token = token
    }

    fun setDelay(delay : Long){
        mPreferencesHelper.saveAudioLatencyMS((delay))
        mPreferencesHelper.cacheAudioLatencyMS((delay))
        JavaAudioDeviceModule.delayDirty = true
    }

    fun launchRiotLiveKitCallScreenWith(url : String, token : String, asListener : Boolean){
        Constants.isListener = asListener
        val intent = Intent(mContext, CallActivity::class.java).apply {
            putExtra(
                CallActivity.KEY_ARGS,
                CallActivity.BundleArgs(
                    url,
                    token
                )
            )
        }
        mContext.startActivity(intent)
    }

    suspend fun connect(asListener : Boolean, error : (exception : Exception) -> Unit, listener : RoomListener) {
        when{
            url == null -> throw Exception("URL is null, please provide valid url in init Method")
            token == null -> throw Exception("Token is null, please provide valid token in init Method")
        }

        try {
            val room = LiveKit.connect(
                mContext,
                url!!,
                token!!,
                ConnectOptions(),
                Listener(object : CustomRoomListener{
                    override fun onDisconnect(room: Room, error: Exception?) {
                        mutableRoom.value?.disconnect()
                    }

                    override fun onParticipantConnected(
                        room: Room,
                        participant: RemoteParticipant
                    ) {
                         updateParticipants(room)
                    }

                    override fun onParticipantDisconnected(
                        room: Room,
                        participant: RemoteParticipant
                    ) {
                         updateParticipants(room)
                    }

                })
            )

            val localParticipant = room.localParticipant
            val audioTrack = localParticipant.createAudioTrack()
            val videoTrack = localParticipant.createVideoTrack()

            if (asListener) {
                //Listener
                audioTrack.enabled = false
                videoTrack.enabled = false
            } else {
                //Broadcaster
                audioTrack.enabled = true
                videoTrack.enabled = true
                localParticipant.publishAudioTrack(audioTrack)
                localParticipant.publishVideoTrack(videoTrack)
                videoTrack.startCapture()
            }

            updateParticipants(room)
            mutableRoom.value = room
        } catch (e: Exception) {
            error.invoke(e)
        }
    }

    fun updateParticipants(room: Room) {
        mutableRemoteParticipants.postValue(
            room.remoteParticipants
                .keys
                .sortedBy { it }
                .mapNotNull { room.remoteParticipants[it] }
        )
    }

    class Listener(private var customListener : CustomRoomListener) : RoomListener{
        override fun onDisconnect(room: Room, error: Exception?) {
            customListener.onDisconnect(room, error)
            Log.i("LIVEKIT", error?.message.toString())

        }

        override fun onParticipantConnected(
            room: Room,
            participant: RemoteParticipant
        ) {
            customListener.onParticipantConnected(room, participant)

        }

        override fun onParticipantDisconnected(
            room: Room,
            participant: RemoteParticipant
        ) {
            customListener.onParticipantDisconnected(room, participant)
        }

        override fun onFailedToConnect(room: Room, error: Exception) {
            Log.i("LIVEKIT", error?.message.toString())
        }

        override fun onActiveSpeakersChanged(speakers: List<Participant>, room: Room) {
            Log.i ("LIVEKIT", "active speakers changed ${speakers.count()}" )
        }

        override fun onMetadataChanged(participant: Participant, prevMetadata: String?, room: Room) {
            Log.i ("LIVEKIT", "Participant metadata changed: ${participant.identity}" )
        }
    }
   /* */

    interface CustomRoomListener{
        fun onDisconnect(room: Room, error: Exception?)
        fun onParticipantConnected(
            room: Room,
            participant: RemoteParticipant
        )
        fun onParticipantDisconnected(
            room: Room,
            participant: RemoteParticipant
        )

    }
}