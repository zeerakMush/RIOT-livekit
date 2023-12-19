#include <cassert>
#include <cstddef>
#include <cstdlib>
#include <memory.h>
#include <unistd.h>

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>


#include <android/log.h>
#include "opensl_stream.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "opensles_playback", __VA_ARGS__)

const int MAX_BUFFERS = 32;
const int BUFFER_SIZE = 480; // 10ms @ 48kHz - Fixed on the server side unfortunately

static short buffer[MAX_BUFFERS * BUFFER_SIZE];
#define BUFFER_SPACING 1 // 0 = Minimal latency, should not exceed MAX_BUFFERS-1
static int write_idx = BUFFER_SPACING;
static int read_idx = 0;

// engine interfaces
static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;

// output mix interfaces
static SLObjectItf outputMixObject = NULL;

// buffer queue player interfaces
static SLObjectItf bqPlayerObject = NULL;
static SLPlayItf bq_player_play;
static SLAndroidSimpleBufferQueueItf bq_player_buffer_queue;

static jbyte* s_byNativeBuf = NULL; // used to cache address of Java ByteBuffer object to transfer samples

// Implements a local buffer queue based on a fixed SPACING or number of buffers
extern "C" jboolean JNICALL Java_org_webrtc_audio_WebRtcAudioTrack_wantsAudioSignal(JNIEnv *env, jclass clazz)
{
  if (write_idx >= read_idx)
  {
    return (write_idx - read_idx) <= BUFFER_SPACING;
  }
  else
  {
    return ((write_idx + MAX_BUFFERS) - read_idx) <= BUFFER_SPACING;
  }
}

extern "C" void JNICALL Java_org_webrtc_audio_WebRtcAudioTrack_audioReady(JNIEnv *env, jclass clazz)
{
  memcpy(buffer + (write_idx * BUFFER_SIZE), s_byNativeBuf, BUFFER_SIZE * sizeof(short));
  write_idx = (write_idx+1) % MAX_BUFFERS;
}

extern "C" void BqPlayerCallback(SLAndroidSimpleBufferQueueItf queueItf, void *data)
{
  SLresult result = (*queueItf)->Enqueue(bq_player_buffer_queue, buffer + (read_idx * BUFFER_SIZE), BUFFER_SIZE * sizeof(short));
  assert(SL_RESULT_SUCCESS == result);
  read_idx = (read_idx+1) % MAX_BUFFERS;
}

void CreateEngine() 
{
  SLresult result;
  result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
  assert(SL_RESULT_SUCCESS == result);

  result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);

  result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
  assert(SL_RESULT_SUCCESS == result);

  result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, NULL, NULL);
  assert(SL_RESULT_SUCCESS == result);

  result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);
}

extern "C"
{
void Java_org_webrtc_audio_WebRtcAudioTrack_startOpenSLES(JNIEnv* env, jclass clazz, jobject byte_buf)
{
  // Cache
  s_byNativeBuf = (jbyte*)env->GetDirectBufferAddress(byte_buf);

  CreateEngine();
  SLDataLocator_AndroidSimpleBufferQueue loc_bufq = { SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, MAX_BUFFERS};
  SLDataFormat_PCM format_pcm = 
  {
    SL_DATAFORMAT_PCM, 
    1,
    SL_SAMPLINGRATE_48,
    SL_PCMSAMPLEFORMAT_FIXED_16, 
    SL_PCMSAMPLEFORMAT_FIXED_16,
    SL_SPEAKER_FRONT_LEFT,
    SL_BYTEORDER_LITTLEENDIAN
  };

  SLDataSource audio_src = {&loc_bufq, &format_pcm};
  SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
  SLDataSink audio_sink = {&loc_outmix, NULL};
  const SLInterfaceID ids[2] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME};
  const SLboolean req[2] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
  SLresult result;
  result = (*engineEngine)->CreateAudioPlayer(engineEngine, &bqPlayerObject, &audio_src, &audio_sink, 2, ids, req);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bqPlayerObject)->Realize(bqPlayerObject, SL_BOOLEAN_FALSE);
  assert(SL_RESULT_SUCCESS == result);
  
  result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_PLAY, &bq_player_play);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bqPlayerObject)->GetInterface(bqPlayerObject, SL_IID_BUFFERQUEUE, &bq_player_buffer_queue);
  assert(SL_RESULT_SUCCESS == result);

  result = (*bq_player_buffer_queue)->RegisterCallback(bq_player_buffer_queue, &BqPlayerCallback, NULL);
  assert(SL_RESULT_SUCCESS == result);

  BqPlayerCallback(bq_player_buffer_queue, NULL);

  LOGI("Started OpenSLIOLoop");
}

void JNICALL Java_org_webrtc_audio_WebRtcAudioTrack_PauseOpenSLIOLoop(JNIEnv* env, jclass clazz, jboolean bPaused)
{
  LOGI("PauseOpenSLIOLoop = %s", bPaused ? "true" :"false");

  if (*bq_player_play != NULL)
  {
    SLresult result = (*bq_player_play)->SetPlayState(bq_player_play, bPaused ? SL_PLAYSTATE_PAUSED : SL_PLAYSTATE_PLAYING);
    assert(SL_RESULT_SUCCESS == result);
  }
}

void Java_org_webrtc_audio_WebRtcAudioTrack_stopOpenSLES(JNIEnv* env, jclass clazz)
{
  LOGI("StopOpenSLIOLoop");
  if (bqPlayerObject != NULL) 
  {
    (*bqPlayerObject)->Destroy(bqPlayerObject);
    bqPlayerObject = NULL;
    bq_player_play = NULL;
    bq_player_buffer_queue = NULL;
  }
  
  if (outputMixObject != NULL) 
  {
    (*outputMixObject)->Destroy(outputMixObject);
    outputMixObject = NULL;
  }
  
  if (engineObject != NULL) 
  {
    (*engineObject)->Destroy(engineObject);
    engineObject = NULL;
    engineEngine = NULL;
  }
}


}


