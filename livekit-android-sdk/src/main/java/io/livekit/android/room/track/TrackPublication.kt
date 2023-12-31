package io.livekit.android.room.track

import io.livekit.android.room.participant.Participant
import livekit.LivekitModels
import java.lang.ref.WeakReference

open class TrackPublication(
    info: LivekitModels.TrackInfo,
    track: Track?,
    participant: Participant
) {
    var track: Track? = track
        internal set
    var name: String
        internal set
    var sid: String
        private set
    var kind: Track.Kind
        private set
    open var muted: Boolean = false
        internal set
    open val subscribed: Boolean
        get() {
            return track != null
        }
    var simulcasted: Boolean? = null
        internal set
    var dimensions: Track.Dimensions? = null
        internal set


    var participant: WeakReference<Participant>

    init {
        sid = info.sid
        name = info.name
        kind = Track.Kind.fromProto(info.type)
        this.participant = WeakReference(participant)
        updateFromInfo(info)
    }

    fun updateFromInfo(info: LivekitModels.TrackInfo) {
        sid = info.sid
        name = info.name
        kind = Track.Kind.fromProto(info.type)
        muted = info.muted
        if (kind == Track.Kind.VIDEO) {
            simulcasted = info.simulcast
            dimensions = Track.Dimensions(info.width, info.height)
        }
    }
}
