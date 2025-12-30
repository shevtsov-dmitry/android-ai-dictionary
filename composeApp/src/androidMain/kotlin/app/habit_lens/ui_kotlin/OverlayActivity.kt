package app.habit_lens.ui_kotlin;

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.TextView;

class OverlayActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getStringExtra("text") ?: return

                val view = LayoutInflater.from(this)
                .inflate(R.layout.overlay_view, null)

        view.findViewById<TextView>(R.id.textView).text = text

        setContentView(view)

        view.setOnClickListener {
            finish()
        }

        // Auto-close
        Handler(Looper.getMainLooper()).postDelayed({
                finish()
        }, 20_000)
    }
}
