package app.habit_lens.ui_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        requestLocalNetworkPermission()
        startClipboardService()

//        setContent {
//            App()
//        }
    }

    private fun startClipboardService() {
        val intent = Intent(this, ClipboardService::class.java)
        startForegroundService(intent)
    }
    private val nearbyPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Now safe to make network requests to local IPs
            // You can show a toast/message: "Local network access granted!"
        } else {
            // Show explanation: "This app needs local network access to communicate with your AI dictionary server"
        }
    }

    fun requestLocalNetworkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                nearbyPermissionLauncher.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
            }
        }
    }

}


//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}