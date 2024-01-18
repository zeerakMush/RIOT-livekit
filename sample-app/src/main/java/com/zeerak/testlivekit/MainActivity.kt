package com.zeerak.testlivekit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zeerak.riotlivekit.RiotLiveKitManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RiotLiveKitManager(this)
            .launchRiotLiveKitCallScreenWith("http://ws.test.com","avsad",true)
    }
}