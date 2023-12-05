package com.zeerak.riotlivekit

import android.content.Context
import androidx.core.content.edit
import com.zeerak.riotlivekit.BuildConfig


private const val TOKEN_PREF = "token"
private const val PROFILE_PREF = "profile"
private const val STREAM_PREF = "stream_model"
private const val BROADCAST_START_TIME = "broadcast_start_time"
private const val AUTO_TOP_UP = "auto_top_up"
private const val AUDIO_LATENCY_MS = "audio_latency_ms"
private const val CACHED_AUDIO_LATENCY_MS = "cache_audio_latency_ms"

class PreferencesHelper constructor(
    private val context: Context
) {

    private val preferences by lazy {
        context.getSharedPreferences("${BuildConfig.APPLICATION_ID}_prefs", Context.MODE_PRIVATE)
    }

    fun saveBroadcastingStartTime(startTime: Long) {
        preferences.edit { putLong(BROADCAST_START_TIME, startTime) }
    }

    fun getBroadcastingStartTime(): Long {
        return preferences.getLong(BROADCAST_START_TIME, -1L)
    }

    fun saveToken(token: String) {
        preferences.edit { putString(TOKEN_PREF, token) }
    }

    fun getToken(): String? {
        return preferences.getString(TOKEN_PREF, null)
    }

    fun saveAutoTopUpValue(isAutoTopOn: Boolean) {
        preferences.edit { putBoolean(AUTO_TOP_UP, isAutoTopOn) }
    }

    fun getAutoTopUp(): Boolean {
        return preferences.getBoolean(AUTO_TOP_UP, false)
    }

    fun getAudioLatencyMS(): Long {
        return preferences.getLong(AUDIO_LATENCY_MS, 0L)
    }

    fun saveAudioLatencyMS(ms: Long) {
        preferences.edit { putLong(AUDIO_LATENCY_MS, ms) }
    }

    fun cacheAudioLatencyMS(ms: Long) {
        preferences.edit { putLong(CACHED_AUDIO_LATENCY_MS, ms) }
    }

    fun getCacheAudioLatencyMS(): Long {
        return preferences.getLong(CACHED_AUDIO_LATENCY_MS, 0)
    }
    fun clearToken() {
        preferences.edit { remove(TOKEN_PREF) }
    }

    fun clearProfile() {
        preferences.edit { remove(PROFILE_PREF) }
    }

    fun clearAllPrefs() {
        preferences.edit {
            clear()
        }
    }
}