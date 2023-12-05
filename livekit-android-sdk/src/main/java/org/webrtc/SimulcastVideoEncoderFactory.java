package org.webrtc;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public class SimulcastVideoEncoderFactory implements VideoEncoderFactory {
    VideoEncoderFactory primary;
    VideoEncoderFactory fallback;

    public SimulcastVideoEncoderFactory(VideoEncoderFactory primary, VideoEncoderFactory fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Nullable
    public VideoEncoder createEncoder(VideoCodecInfo info) {
        return new SimulcastVideoEncoder(this.primary, this.fallback, info);
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        List<VideoCodecInfo> codecs = new ArrayList();
        codecs.addAll(Arrays.asList(this.primary.getSupportedCodecs()));
        codecs.addAll(Arrays.asList(this.fallback.getSupportedCodecs()));
        return (VideoCodecInfo[])codecs.toArray(new VideoCodecInfo[codecs.size()]);
    }
}