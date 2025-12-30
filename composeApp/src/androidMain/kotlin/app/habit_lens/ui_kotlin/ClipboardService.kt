package app.habit_lens.ui_kotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder

class ClipboardService : Service() {

    override fun onBind(intent: Intent?) = null

    private lateinit var clipboard: ClipboardManager

    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener {
            val clip = clipboard.primaryClip
            val text = clip?.getItemAt(0)?.coerceToText(this)?.toString()

            if (!text.isNullOrBlank()) {
                handleClipboardText(text)
            }
        }

        startForegroundNotification()
    }

    fun handleClipboardText(text: String) {
        val request = Request.Builder()
            .url("http://192.168.1.38:8915/ai-dictionary?text=${URLEncoder.encode(text, "UTF-8")}")
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                showOverlay(body)
            }

            override fun onFailure(call: Call, e: IOException) {
                showOverlay(e.message.toString())
            }
        })

    }

    fun showOverlay(text: String) {
        mainScope.launch {
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            val view = LayoutInflater.from(this@ClipboardService)
                .inflate(R.layout.overlay_view, null)

            view.findViewById<TextView>(R.id.textView).text = text

            // Optional: remove on click/tap
            view.setOnClickListener {
                windowManager.removeView(view)
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,           // ← key change
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888                               // better quality than TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP
                // Optional: small margin from top edge
                y = (resources.displayMetrics.density * 32).toInt() // ~32dp from top
            }

            try {
                windowManager.addView(view, params)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Auto dismiss after 12 seconds (adjust as needed)
            delay(12_000)
            try {
                windowManager.removeView(view)
            } catch (_: Exception) {
            }
        }
    }

    fun startForegroundNotification() {
        val channelId = "clipboard_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Clipboard Service",
                NotificationManager.IMPORTANCE_MIN
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Clipboard Utility Running")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        startForeground(1, notification)
    }



}
