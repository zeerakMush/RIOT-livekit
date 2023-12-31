package io.livekit.android.room.track

import android.content.Context
import io.livekit.android.util.LKLog
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpSender
import org.webrtc.RtpTransceiver
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import java.util.*

/**
 * A representation of a local video track (generally input coming from camera or screen).
 *
 * [startCapture] should be called before use.
 */
class LocalVideoTrack(
    private var capturer: VideoCapturer,
    private var source: VideoSource,
    name: String,
    var options: LocalVideoTrackOptions,
    rtcTrack: org.webrtc.VideoTrack,
    private val peerConnectionFactory: PeerConnectionFactory,
    private val context: Context,
    private val eglBase: EglBase,
) : VideoTrack(name, rtcTrack) {

    override var rtcTrack: org.webrtc.VideoTrack = rtcTrack
        internal set

    /**
     * Note: these dimensions are only requested params, and may differ
     * from the actual capture format used by the camera.
     */
    val dimensions: Dimensions
        get() = Dimensions(options.captureParams.width, options.captureParams.height)

    internal var transceiver: RtpTransceiver? = null
    private val sender: RtpSender?
        get() = transceiver?.sender

    fun startCapture() {
        capturer.startCapture(
            options.captureParams.width,
            options.captureParams.height,
            options.captureParams.maxFps
        )
    }

    override fun stop() {
        capturer.stopCapture()
        super.stop()
    }

    fun restartTrack(options: LocalVideoTrackOptions = LocalVideoTrackOptions()) {
        val newTrack = createTrack(
            peerConnectionFactory,
            context,
            name,
            options,
            eglBase
        )

        val oldCapturer = capturer
        val oldSource = source
        val oldRtcTrack = rtcTrack

        oldCapturer.stopCapture()
        oldCapturer.dispose()
        oldSource.dispose()

        // sender owns rtcTrack, so it'll take care of disposing it.
        oldRtcTrack.setEnabled(false)

        // migrate video sinks to the new track
        for (sink in sinks) {
            oldRtcTrack.removeSink(sink)
            newTrack.addRenderer(sink)
        }

        capturer = newTrack.capturer
        source = newTrack.source
        rtcTrack = newTrack.rtcTrack
        this.options = options
        startCapture()
        sender?.setTrack(newTrack.rtcTrack, true)
    }

    companion object {
        internal fun createTrack(
            peerConnectionFactory: PeerConnectionFactory,
            context: Context,
            name: String,
            options: LocalVideoTrackOptions,
            rootEglBase: EglBase,
        ): LocalVideoTrack {
            val source = peerConnectionFactory.createVideoSource(options.isScreencast)
            val capturer = createVideoCapturer(context, options.position) ?: TODO()
            capturer.initialize(
                SurfaceTextureHelper.create("VideoCaptureThread", rootEglBase.eglBaseContext),
                context,
                source.capturerObserver
            )
            val track = peerConnectionFactory.createVideoTrack(UUID.randomUUID().toString(), source)

            return LocalVideoTrack(
                capturer = capturer,
                source = source,
                options = options,
                name = name,
                rtcTrack = track,
                peerConnectionFactory = peerConnectionFactory,
                context = context,
                eglBase = rootEglBase,
            )
        }

        private fun createVideoCapturer(context: Context, position: CameraPosition): VideoCapturer? {
            val videoCapturer: VideoCapturer? = if (Camera2Enumerator.isSupported(context)) {
                createCameraCapturer(Camera2Enumerator(context), position)
            } else {
                createCameraCapturer(Camera1Enumerator(true), position)
            }
            if (videoCapturer == null) {
                LKLog.d { "Failed to open camera" }
                return null
            }
            return videoCapturer
        }

        private fun createCameraCapturer(enumerator: CameraEnumerator, position: CameraPosition): VideoCapturer? {
            val deviceNames = enumerator.deviceNames

            for (deviceName in deviceNames) {
                if (enumerator.isFrontFacing(deviceName) && position == CameraPosition.FRONT) {
                    LKLog.v { "Creating front facing camera capturer." }
                    val videoCapturer = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                } else if (enumerator.isBackFacing(deviceName) && position == CameraPosition.BACK) {
                    LKLog.v { "Creating back facing camera capturer." }
                    val videoCapturer = enumerator.createCapturer(deviceName, null)
                    if (videoCapturer != null) {
                        return videoCapturer
                    }
                }
            }
            return null
        }
    }
}