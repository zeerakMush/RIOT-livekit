package io.livekit.android.util

import android.util.Log
import io.livekit.android.util.LoggingLevel.*

/*
Copyright 2017-2018 AJ Alt
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Original repo can be found at: https://github.com/ajalt/LKLogkt
 */

internal class LKLog {

    companion object {
        var loggingLevel = OFF


        /** Log a verbose exception and a message that will be evaluated lazily when the message is printed */
        @JvmStatic
        inline fun v(t: Throwable? = null, message: () -> String) =
            log(VERBOSE) { Log.v("LIVEKIT", message()) }

        @JvmStatic
        inline fun v(t: Throwable?) = log(VERBOSE) { Log.v("LIVEKIT", t?.message.toString()) }

        /** Log a debug exception and a message that will be evaluated lazily when the message is printed */
        @JvmStatic
        inline fun d(t: Throwable? = null, message: () -> String) =
            log(DEBUG) { Log.d("LIVEKIT", message()) }

        @JvmStatic
        inline fun d(t: Throwable?) = log(DEBUG) { Log.d("LIVEKIT", t?.message.toString()) }

        /** Log an info exception and a message that will be evaluated lazily when the message is printed */
        @JvmStatic
        inline fun i(t: Throwable? = null, message: () -> String) =
            log(INFO) { Log.i("LIVEKIT", message()) }

        @JvmStatic
        inline fun i(t: Throwable?) = log(INFO) { Log.i("LIVEKIT", t?.message.toString()) }

        /** Log a warning exception and a message that will be evaluated lazily when the message is printed */
        @JvmStatic
        inline fun w(t: Throwable? = null, message: () -> String) =
            log(WARN) { Log.w("LIVEKIT", message()) }

        @JvmStatic
        inline fun w(t: Throwable?) = log(WARN) { Log.w("LIVEKIT", t?.message.toString()) }

        /** Log an error exception and a message that will be evaluated lazily when the message is printed */
        @JvmStatic
        inline fun e(t: Throwable? = null, message: () -> String) =
            log(ERROR) { Log.e("LIVEKIT", message()) }

        @JvmStatic
        inline fun e(t: Throwable?) = log(ERROR) { Log.e("LIVEKIT", t?.message.toString()) }

        /** Log an assert exception and a message that will be evaluated lazily when the message is printed */
        @JvmStatic
        inline fun wtf(t: Throwable? = null, message: () -> String) =
            log(WTF) { Log.wtf("LIVEKIT", message()) }

        @JvmStatic
        inline fun wtf(t: Throwable?) = log(WTF) { Log.wtf("LIVEKIT", t?.message.toString()) }

        /** @suppress */
        internal inline fun log(loggingLevel: LoggingLevel, block: () -> Unit) {
            if (loggingLevel >= LKLog.loggingLevel) block()
        }
    }
}
