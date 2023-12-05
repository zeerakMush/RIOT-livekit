//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import javax.annotation.Nullable;

public interface VideoEncoder {
  @CalledByNative
  default long createNativeVideoEncoder() {
    return 0L;
  }

  @CalledByNative
  default boolean isHardwareEncoder() {
    return true;
  }

  @CalledByNative
  VideoCodecStatus initEncode(Settings var1, Callback var2);

  @CalledByNative
  VideoCodecStatus release();

  @CalledByNative
  VideoCodecStatus encode(VideoFrame var1, EncodeInfo var2);

  VideoCodecStatus setRateAllocation(BitrateAllocation var1, int var2);

  @CalledByNative
  default VideoCodecStatus setRates(RateControlParameters rcParameters) {
    int framerateFps = (int)Math.ceil(rcParameters.framerateFps);
    return this.setRateAllocation(rcParameters.bitrate, framerateFps);
  }

  @CalledByNative
  ScalingSettings getScalingSettings();

  @CalledByNative
  default ResolutionBitrateLimits[] getResolutionBitrateLimits() {
    ResolutionBitrateLimits[] bitrate_limits = new ResolutionBitrateLimits[0];
    return bitrate_limits;
  }

  @CalledByNative
  String getImplementationName();

  @CalledByNative
  default EncoderInfo getEncoderInfo() {
    return new EncoderInfo(1, false);
  }

  public interface Callback {
    void onEncodedFrame(EncodedImage var1, CodecSpecificInfo var2);
  }

  public static class EncoderInfo {
    public final int requestedResolutionAlignment;
    public final boolean applyAlignmentToAllSimulcastLayers;

    public EncoderInfo(int requestedResolutionAlignment, boolean applyAlignmentToAllSimulcastLayers) {
      this.requestedResolutionAlignment = requestedResolutionAlignment;
      this.applyAlignmentToAllSimulcastLayers = applyAlignmentToAllSimulcastLayers;
    }

    @CalledByNative("EncoderInfo")
    public int getRequestedResolutionAlignment() {
      return this.requestedResolutionAlignment;
    }

    @CalledByNative("EncoderInfo")
    public boolean getApplyAlignmentToAllSimulcastLayers() {
      return this.applyAlignmentToAllSimulcastLayers;
    }
  }

  public static class RateControlParameters {
    public final BitrateAllocation bitrate;
    public final double framerateFps;

    @CalledByNative("RateControlParameters")
    public RateControlParameters(BitrateAllocation bitrate, double framerateFps) {
      this.bitrate = bitrate;
      this.framerateFps = framerateFps;
    }
  }

  public static class ResolutionBitrateLimits {
    public final int frameSizePixels;
    public final int minStartBitrateBps;
    public final int minBitrateBps;
    public final int maxBitrateBps;

    public ResolutionBitrateLimits(int frameSizePixels, int minStartBitrateBps, int minBitrateBps, int maxBitrateBps) {
      this.frameSizePixels = frameSizePixels;
      this.minStartBitrateBps = minStartBitrateBps;
      this.minBitrateBps = minBitrateBps;
      this.maxBitrateBps = maxBitrateBps;
    }

    @CalledByNative("ResolutionBitrateLimits")
    public int getFrameSizePixels() {
      return this.frameSizePixels;
    }

    @CalledByNative("ResolutionBitrateLimits")
    public int getMinStartBitrateBps() {
      return this.minStartBitrateBps;
    }

    @CalledByNative("ResolutionBitrateLimits")
    public int getMinBitrateBps() {
      return this.minBitrateBps;
    }

    @CalledByNative("ResolutionBitrateLimits")
    public int getMaxBitrateBps() {
      return this.maxBitrateBps;
    }
  }

  public static class ScalingSettings {
    public final boolean on;
    @Nullable
    public final Integer low;
    @Nullable
    public final Integer high;
    public static final ScalingSettings OFF = new ScalingSettings();

    public ScalingSettings(int low, int high) {
      this.on = true;
      this.low = low;
      this.high = high;
    }

    private ScalingSettings() {
      this.on = false;
      this.low = null;
      this.high = null;
    }

    /** @deprecated */
    @Deprecated
    public ScalingSettings(boolean on) {
      this.on = on;
      this.low = null;
      this.high = null;
    }

    /** @deprecated */
    @Deprecated
    public ScalingSettings(boolean on, int low, int high) {
      this.on = on;
      this.low = low;
      this.high = high;
    }

    public String toString() {
      return this.on ? "[ " + this.low + ", " + this.high + " ]" : "OFF";
    }
  }

  public static class BitrateAllocation {
    public final int[][] bitratesBbs;

    @CalledByNative("BitrateAllocation")
    public BitrateAllocation(int[][] bitratesBbs) {
      this.bitratesBbs = bitratesBbs;
    }

    public int getSum() {
      int sum = 0;
      int[][] var2 = this.bitratesBbs;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        int[] spatialLayer = var2[var4];
        int[] var6 = spatialLayer;
        int var7 = spatialLayer.length;

        for(int var8 = 0; var8 < var7; ++var8) {
          int bitrate = var6[var8];
          sum += bitrate;
        }
      }

      return sum;
    }
  }

  public static class CodecSpecificInfoAV1 extends CodecSpecificInfo {
    public CodecSpecificInfoAV1() {
    }
  }

  public static class CodecSpecificInfoH264 extends CodecSpecificInfo {
    public CodecSpecificInfoH264() {
    }
  }

  public static class CodecSpecificInfoVP9 extends CodecSpecificInfo {
    public CodecSpecificInfoVP9() {
    }
  }

  public static class CodecSpecificInfoVP8 extends CodecSpecificInfo {
    public CodecSpecificInfoVP8() {
    }
  }

  public static class CodecSpecificInfo {
    public CodecSpecificInfo() {
    }
  }

  public static class EncodeInfo {
    public final EncodedImage.FrameType[] frameTypes;

    @CalledByNative("EncodeInfo")
    public EncodeInfo(EncodedImage.FrameType[] frameTypes) {
      this.frameTypes = frameTypes;
    }
  }

  public static class Capabilities {
    public final boolean lossNotification;

    @CalledByNative("Capabilities")
    public Capabilities(boolean lossNotification) {
      this.lossNotification = lossNotification;
    }
  }

  public static class Settings {
    public final int numberOfCores;
    public final int width;
    public final int height;
    public final int startBitrate;
    public final int maxFramerate;
    public final int numberOfSimulcastStreams;
    public final boolean automaticResizeOn;
    public final Capabilities capabilities;

    /** @deprecated */
    @Deprecated
    public Settings(int numberOfCores, int width, int height, int startBitrate, int maxFramerate, int numberOfSimulcastStreams, boolean automaticResizeOn) {
      this(numberOfCores, width, height, startBitrate, maxFramerate, numberOfSimulcastStreams, automaticResizeOn, new Capabilities(false));
    }

    @CalledByNative("Settings")
    public Settings(int numberOfCores, int width, int height, int startBitrate, int maxFramerate, int numberOfSimulcastStreams, boolean automaticResizeOn, Capabilities capabilities) {
      this.numberOfCores = numberOfCores;
      this.width = width;
      this.height = height;
      this.startBitrate = startBitrate;
      this.maxFramerate = maxFramerate;
      this.numberOfSimulcastStreams = numberOfSimulcastStreams;
      this.automaticResizeOn = automaticResizeOn;
      this.capabilities = capabilities;
    }
  }
}
