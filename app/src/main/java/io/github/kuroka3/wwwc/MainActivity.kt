package io.github.kuroka3.wwwc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import io.github.kuroka3.wwwc.api.WaveplateManager
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var initActivityButton: Button

    lateinit var indicator: TextView
    lateinit var leftTimeView: TextView
    lateinit var use40Button: Button
    lateinit var use60Button: Button
    lateinit var charge60Button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initActivityButton = findViewById(R.id.open_init)

        indicator = findViewById(R.id.plateView)
        leftTimeView = findViewById(R.id.leftTimeView)
        use40Button = findViewById(R.id.use40Button)
        use60Button = findViewById(R.id.use60Button)
        charge60Button = findViewById(R.id.charge60Button)

        initActivityButton.setOnClickListener {
            startActivity(Intent(applicationContext, InitActivity::class.java))
        }

        use40Button.setOnClickListener {
            WaveplateManager.useWaveplate(40)
        }

        use60Button.setOnClickListener {
            WaveplateManager.useWaveplate(60)
        }

        charge60Button.setOnClickListener {
            WaveplateManager.chargeWaveplate(60)
        }

        WaveplateManager.init(dataDir)
        WaveplateManager.load()

        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    indicator.text = "${WaveplateManager.waveplate} / 240"
                    leftTimeView.text = "다음 충전 : ${(360000L - WaveplateManager.nextChargeTime).toTimeStringmmss()}"
                }
            }
        }

        Timer().schedule(timerTask, 0, 100)
    }

    private fun Long.toTimeStringmmss(): String {
        val totalSeconds = this / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d", minutes, seconds)
    }
}