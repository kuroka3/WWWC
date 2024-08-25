package io.github.kuroka3.wwwc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import io.github.kuroka3.wwwc.api.WaveplateManager

class InitActivity : AppCompatActivity() {

    lateinit var waveplateEdit: EditText
    lateinit var nextChargeEdit: EditText
    lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        waveplateEdit = findViewById(R.id.init_waveplate_edit)
        nextChargeEdit = findViewById(R.id.init_nextcharge_edit)
        confirmButton = findViewById(R.id.init_confirm)

        confirmButton.setOnClickListener{
            val waveplateInput = waveplateEdit.text.toString().toIntOrNull()

            if (waveplateInput == null) {
                Toast.makeText(applicationContext, "Invalid Waveplate Int", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidTimeFormat(nextChargeEdit.text.toString())) {
                Toast.makeText(applicationContext, "Invalid Time Format (mm:ss)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            WaveplateManager.initWaveplate(waveplateInput, convertToMilliseconds(nextChargeEdit.text.toString()))
            finish()
        }
    }

    private fun convertToMilliseconds(time: String): Long {
        val parts = time.split(":")
        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        return (minutes * 60 + seconds) * 1000
    }

    private fun isValidTimeFormat(time: String): Boolean {
        val regex = Regex("^([0-5][0-9]):([0-5][0-9]\$)")
        return regex.matches(time)
    }
}