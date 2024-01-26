package com.zeerak.riotlivekit

import android.media.AudioManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.snakydesign.livedataextensions.combineLatest
import com.xwray.groupie.GroupieAdapter
import com.zeerak.riotlivekit.databinding.CallActivityBinding
import io.livekit.android.room.track.LocalVideoTrack
import kotlinx.parcelize.Parcelize

class CallActivity : AppCompatActivity() {

    val viewModel: CallViewModel by viewModelByFactory {
        val args = intent.getParcelableExtra<BundleArgs>(KEY_ARGS)
            ?: throw NullPointerException("args is null!")
        CallViewModel(args.url, args.token, this)
    }

    val audioDelayViewModel: AudioDelayViewModel by viewModelByFactory {
        AudioDelayViewModel(this)
    }

    lateinit var binding: CallActivityBinding
    var tabLayoutMediator: TabLayoutMediator? = null
    val focusChangeListener = AudioManager.OnAudioFocusChangeListener {}

    private var previousSpeakerphoneOn = true
    private var previousMicrophoneMute = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CallActivityBinding.inflate(layoutInflater)
        binding.viewModel = audioDelayViewModel

        setContentView(binding.root)

        binding.seekbarDelay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    binding.tvValueDelay.text = "${progress/1000f} sec"
                    audioDelayViewModel.onSeekBarChanged(seekBar!!, progress, fromUser)
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        if(Constants.isListener){
            binding.constraintLayout.visibility = View.VISIBLE
            binding.videoButton.visibility = View.GONE
            binding.micButton.visibility = View.GONE
        }else{
            binding.constraintLayout.visibility = View.GONE
            binding.micButton.visibility = View.VISIBLE
            if(Constants.enableVideoBroadcast){
                binding.videoButton.visibility = View.VISIBLE
            }else{
                binding.videoButton.visibility = View.GONE

            }
        }


        binding.micButton.setOnClickListener {
            if(binding.micButton.tag == "enabled"){
                binding.micButton.tag = "disabled"
                binding.micButton.setImageResource(R.drawable.ic_round_mic_off_24)
                viewModel.audioTrack?.enabled = false
            }else{
                binding.micButton.tag = "enabled"
                binding.micButton.setImageResource(R.drawable.ic_round_mic_24)
                viewModel.audioTrack?.enabled = true
            }
        }

        binding.videoButton.setOnClickListener {
            if(binding.videoButton.tag == "enabled"){
                binding.videoButton.tag = "disabled"
                binding.videoButton.setImageResource(R.drawable.ic_round_videocam_off_24)
                viewModel.videoTrack?.enabled = false
            }else{
                binding.videoButton.tag = "enabled"
                binding.videoButton.setImageResource(R.drawable.ic_round_videocam_24)
                viewModel.videoTrack?.enabled = true
            }
        }

        // Viewpager setup
        val adapter = GroupieAdapter()

        binding.viewPager.apply {
            this.adapter = adapter
        }

        combineLatest(
            viewModel.room,
            viewModel.remoteParticipants
        ) { room, participants -> room to participants }
            .observe(this) {
                tabLayoutMediator?.detach()
                tabLayoutMediator = null

                val (room, participants) = it
                val items = participants.map { participant -> ParticipantItem(room, participant) }
                adapter.update(items)

                tabLayoutMediator =
                    TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
                        tab.text = participants[position].identity
                    }
                tabLayoutMediator?.attach()
            }

        viewModel.room.observe(this) { room ->
            room.initVideoRenderer(binding.pipVideoView)
            val videoTrack = room.localParticipant.videoTracks.values.firstOrNull()?.track as? LocalVideoTrack

            videoTrack?.addRenderer(binding.pipVideoView)
        }


        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        with(audioManager) {
            previousSpeakerphoneOn = isSpeakerphoneOn
            previousMicrophoneMute = isMicrophoneMute
            isSpeakerphoneOn = true
            isMicrophoneMute = false
            mode = AudioManager.MODE_IN_COMMUNICATION
        }
        val result = audioManager.requestAudioFocus(
            focusChangeListener,
            AudioManager.STREAM_VOICE_CALL,
            AudioManager.AUDIOFOCUS_GAIN,
        )
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.v ("LIVEKIT", "Audio focus request granted for VOICE_CALL streams")
        } else {
            Log.v ("LIVEKIT", "Audio focus request failed" )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.pipVideoView.release()
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        with(audioManager) {
            isSpeakerphoneOn = previousSpeakerphoneOn
            isMicrophoneMute = previousMicrophoneMute
            abandonAudioFocus(focusChangeListener)
            mode = AudioManager.MODE_NORMAL
        }
    }

    companion object {
        const val KEY_ARGS = "args"
    }

    @Parcelize
    data class BundleArgs(val url: String, val token: String) : Parcelable
}