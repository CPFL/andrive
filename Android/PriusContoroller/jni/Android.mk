LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE	:= sensordatatransmission
LOCAL_SRC_FILES	:= sensor_data_transmission.c
LOCAL_LDLIBS	:= -llog -lGLESv2

include $(BUILD_SHARED_LIBRARY)