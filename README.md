# Android Kotlin SDK for LiveKit

Easily add video & audio capabilities to your Android apps.

## Installation

```groovy title="build.gradle"
...
dependencies {
  implementation "com.github.zeerakMush:RIOT-livekit:<version>"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

## Usage

Riot LiveKit uses WebRTC-provided `org.webrtc.SurfaceViewRenderer` to render video tracks. Subscribed audio tracks are automatically played.

```kt
class MainActivity : AppCompatActivity(), RoomListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RiotLiveKitManager(this)
                .launchRiotLiveKitCallScreenWith(
                    "wss://yourserver.livekit.cloud",
                    "token"
                )
}
```

### Optional (Dev convenience)

1. Use method launchRiotLiveKitCallScreenWith to launch call screen.
2. Custom call screen can also be created using methods inside RiotLiveKitManager class. 
