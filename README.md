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

### Launch Default Call Screen

```kt
class MainActivity : AppCompatActivity(), RoomListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val riotManager = RiotLiveKitManager(this).init(
            "URL",
            "TOKEN",
        )
        riotManager.launchRiotLiveKitCallScreenWith()
    }
```

### OR By Using Custom Connect Method
```kt
class MainActivity : AppCompatActivity(), RoomListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val riotManager = RiotLiveKitManager(this).init(
            "URL",
            "TOKEN",
        )
        riotManager.connect({}, object : RoomListener {})
    }
```

### set Delay
```kt
class MainActivity : AppCompatActivity(), RoomListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val riotManager = RiotLiveKitManager(this).init(
            "URL",
            "TOKEN",
        )
        riotManager.launchRiotLiveKitCallScreenWith()
        riotManager.setDelay(500L) 
    }
```

### Optional (Dev convenience)

1. Use method launchRiotLiveKitCallScreenWith to launch call screen.
2. Custom call screen can also be created using methods inside RiotLiveKitManager class. 
3. setDelay() method uses value in milliseconds the upper limit is 70000L default is 0.

