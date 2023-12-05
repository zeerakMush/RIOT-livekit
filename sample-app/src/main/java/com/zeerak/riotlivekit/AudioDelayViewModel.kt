package com.zeerak.riotlivekit

import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.webrtc.audio.JavaAudioDeviceModule
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioDelayViewModel constructor(
    private val mPreferencesHelper: PreferencesHelper
) : ViewModel()  {
    companion object {
        const val MAX_DELAY = 70000L
        const val MIN_DELAY = 0L
    }

    private val _delay = MutableLiveData(0L)
    val delay: LiveData<Long> = _delay

    private val _delaySec = MutableLiveData(0f)
    val delaySec: LiveData<Float> = _delaySec

    init {
        setDefaultDelay(mPreferencesHelper.getAudioLatencyMS())
    }

    fun setDefaultDelay(delay: Long) {
        _delay.value = delay
        _delaySec.value = delay / 1000f
    }

    fun updateDelay(view: View, sec: Long) {
        try {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val newDelay = (delay.value?.plus(sec) ?: 0L).coerceIn(MIN_DELAY, MAX_DELAY)
        _delay.value = newDelay
        updatePref()
    }

    fun onSeekBarChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            _delay.value = progress.toLong()
            updatePref()
        }
    }
    var async: Deferred<Unit>? = null
    private fun updatePref() {
        if(async != null){
            async?.cancel()
        }
        mPreferencesHelper.saveAudioLatencyMS((_delay.value?.toLong() ?: 0L))
        mPreferencesHelper.cacheAudioLatencyMS((_delay.value?.toLong() ?: 0L))
        _delaySec.value = (delay.value?.toFloat() ?: 0f) / 1000f
        async = viewModelScope.async {
            Log.d("XDDD", "updatePref: trying ")
            delay(1000)
            JavaAudioDeviceModule.delayDirty = true
            Log.d("XDDD", "updatePref: updated ")
        }
        viewModelScope.launch {
            async?.await()
        }

    }

    sealed class Event {

    }
}