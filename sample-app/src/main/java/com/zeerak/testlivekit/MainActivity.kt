package com.zeerak.testlivekit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.zeerak.riotlivekit.RiotLiveKitManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.button).setOnClickListener {
            RiotLiveKitManager(this)
                .launchRiotLiveKitCallScreenWith(
                    "wss://demo-htzvtmdl.livekit.cloud",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDU4NjU3ODEsImlzcyI6IkFQSWVkUWY3aUM3dHI0ayIsIm5iZiI6MTcwNTc4Mzc4MSwic3ViIjoibGlzdGVuZXIiLCJ2aWRlbyI6eyJjYW5QdWJsaXNoIjpmYWxzZSwiY2FuUHVibGlzaERhdGEiOnRydWUsImNhblN1YnNjcmliZSI6dHJ1ZSwicm9vbSI6ImJhc2VtZW50Iiwicm9vbUpvaW4iOnRydWV9fQ.avfbH69deBzAgcD34jbbQe72hdX6boVX5S0VfQzZG-E",
                    true
                )

        }
    }
}