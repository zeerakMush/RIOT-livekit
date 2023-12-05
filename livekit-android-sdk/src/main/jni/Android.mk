LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SHORT_COMMANDS := true

LOCAL_MODULE := opensles_playback
 
LOCAL_ARM_MODE := arm

LOCAL_SRC_FILES := \
    opensl_stream.cpp\

LOCAL_LDLIBS := -lOpenSLES -llog

include $(BUILD_SHARED_LIBRARY)
