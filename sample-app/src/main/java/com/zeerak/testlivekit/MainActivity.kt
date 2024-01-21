package com.zeerak.testlivekit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
/*
import com.zeerak.riotlivekit.RiotLiveKitManager
*/

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.button).setOnClickListener {
           /* RiotLiveKitManager(this)
                .launchRiotLiveKitCallScreenWith(
                    "Your URL",
                    "Your Token",
                    true
                )*/

        }
    }
}