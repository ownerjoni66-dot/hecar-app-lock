package com.hecar.applock

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.WindowManager
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.Color
import android.graphics.PixelFormat
import android.content.Intent

class LockService : AccessibilityService() {

    private var lockView: View? = null

    private val targetApps = setOf(
        "org.telegram.messenger",
        "com.zhiliaoapp.musically",
        "com.facebook.katana",
        "com.facebook.orca",
        "com.android.chrome"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType !=
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return
        val prefs = getSharedPreferences("hecar_lock", MODE_PRIVATE)

        val shouldLock =
            prefs.getBoolean("enabled", false) &&
            pkg in targetApps

        if (shouldLock) {
            showLock()
        } else {
            removeLock()
        }
    }

    private fun showLock() {
        if (lockView != null) return

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(25, 25, 25, 25)
            setBackgroundColor(Color.rgb(0, 12, 8))
        }

        layout.addView(TextView(this).apply {
            text = "H E C A R\\nACCESS BLOCKED"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(Color.GREEN)
        })

        layout.addView(Button(this).apply {
            text = "HECAR খুলুন"
            setOnClickListener {
                val intent = Intent(
                    this@LockService,
                    MainActivity::class.java
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                removeLock()
            }
        })

        lockView = layout

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            0,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START

        try {
            (getSystemService(WINDOW_SERVICE) as WindowManager)
                .addView(layout, params)
        } catch (e: Exception) {
            lockView = null
        }
    }

    private fun removeLock() {
        val view = lockView ?: return
        try {
            (getSystemService(WINDOW_SERVICE) as WindowManager)
                .removeView(view)
        } catch (e: Exception) {
        }
        lockView = null
    }

    override fun onInterrupt() {
        removeLock()
    }

    override fun onDestroy() {
        removeLock()
        super.onDestroy()
    }
}
