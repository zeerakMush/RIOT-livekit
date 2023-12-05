
#ifndef OPENSL_STREAM_H
#define OPENSL_STREAM_H


#include <jni.h>

extern "C" JNIEXPORT jboolean JNICALL Java_org_webrtc_audio_WebRtcAudioTrack_wantsAudioSignal(JNIEnv *env, jclass clazz);
extern "C" JNIEXPORT void JNICALL Java_org_webrtc_audio_WebRtcAudioTrack_audioReady(JNIEnv *env, jclass clazz);
extern "C" void Java_org_webrtc_audio_WebRtcAudioTrack_startOpenSLES(JNIEnv* env, jclass clazz, jobject byte_buffer);
extern "C" JNIEXPORT void JNICALL Java_org_webrtc_audio_WebRtcAudioTrack_PauseOpenSLIOLoop(JNIEnv* env, jclass clazz, jboolean bPaused);
extern "C" void Java_org_webrtc_audio_WebRtcAudioTrack_stopOpenSLES(JNIEnv* env, jclass clazz);


#endif // #ifndef OPENSL_STREAM_H
