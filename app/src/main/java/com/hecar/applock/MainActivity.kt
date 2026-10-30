package com.hecar.applock

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("hecar_lock", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(25, 30, 25, 30)
            setBackgroundColor(Color.rgb(0, 15, 10))
        }

        val title = TextView(this).apply {
            text = "H E C A R"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.GREEN)
        }
        layout.addView(title)

        val password = EditText(this).apply {
            hint = "পাসওয়ার্ড দিন"
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
        }
        layout.addView(password)

        val save = Button(this).apply {
            text = "পাসওয়ার্ড সেট করুন"
        }
        layout.addView(save)

        save.setOnClickListener {
            val p = password.text.toString()
            if (p.length >= 4) {
                prefs.edit().putString("password", p).apply()
                Toast.makeText(this, "পাসওয়ার্ড সেভ হয়েছে", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "কমপক্ষে ৪ অক্ষর দিন", Toast.LENGTH_SHORT).show()
            }
        }

        val toggle = Button(this).apply {
            text = "Protection ON / OFF"
        }
        layout.addView(toggle)

        toggle.setOnClickListener {
            val saved = prefs.getString("password", null)
            if (saved == null || password.text.toString() != saved) {
                Toast.makeText(this, "পাসওয়ার্ড ভুল", Toast.LENGTH_SHORT).show()
            } else {
                val newState = !prefs.getBoolean("enabled", false)
                prefs.edit().putBoolean("enabled", newState).apply()
                Toast.makeText(
                    this,
                    if (newState) "Protection ON" else "Protection OFF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val settings = Button(this).apply {
            text = "Accessibility Settings"
        }
        layout.addView(settings)

        settings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        setContentView(layout)
    }
}
