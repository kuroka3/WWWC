package io.github.kuroka3.wwwc.api

import com.google.gson.Gson
import java.io.File

object WaveplateManager {

    const val CHARGE_TIME = 360000L
    const val MAX_WAVEPLATE = 240

    private lateinit var saveFile: File
    private lateinit var initedData: WaveplateInitData

    fun init(dataDir: File) {
        saveFile = File(dataDir, "waveplate.json")
        if (!dataDir.exists()) { dataDir.mkdirs() }
        if (!saveFile.exists()) { saveFile.createNewFile(); saveFile.writeText("{\"waveplate\":$MAX_WAVEPLATE,\"initTime\":${System.currentTimeMillis()},\"overplate\":0}") }
    }

    fun load() {
        initedData = Gson().fromJson(saveFile.readText(), WaveplateInitData::class.java)
    }

    fun save() {
        saveFile.writeText(Gson().toJson(initedData))
    }

    val waveplate: Int
        get() {
            val leftTime = System.currentTimeMillis() - initedData.initTime
            val waveplate = initedData.waveplate + (leftTime / CHARGE_TIME).toInt()
            return (if (waveplate >= MAX_WAVEPLATE) MAX_WAVEPLATE else waveplate) + initedData.overplate
        }

    val nextChargeTime: Long
        get() {
            if (waveplate >= MAX_WAVEPLATE) { return CHARGE_TIME }
            val leftTime = System.currentTimeMillis() - initedData.initTime
            return leftTime % CHARGE_TIME
        }

    val leftWholeChargeTime: Long
        get() {
            if (waveplate >= MAX_WAVEPLATE) { return 0L }
            return ((MAX_WAVEPLATE - waveplate) * CHARGE_TIME) - nextChargeTime
        }

    fun useWaveplate(amount: Int) {
        if (waveplate < amount) { return }
        if (waveplate >= MAX_WAVEPLATE) {
            initWaveplate(waveplate-amount, 0L)
        } else {
            initedData = WaveplateInitData(initedData.initTime, initedData.waveplate - amount, 0)
            save()
        }
    }

    fun chargeWaveplate(amount: Int) {
        val chargedWaveplate = waveplate + amount

        if (chargedWaveplate >= MAX_WAVEPLATE) {
            initWaveplate(chargedWaveplate, 0L)
        } else {
            initedData = WaveplateInitData(initedData.initTime, initedData.waveplate + amount, 0)
            save()
        }
    }

    fun initWaveplate(data: WaveplateInitData) {
        initedData = data
        save()
    }

    fun initWaveplate(waveplate: Int, nextChargeTime: Long) {
        val initTime = System.currentTimeMillis() - nextChargeTime
        initWaveplate(WaveplateInitData(initTime, if (waveplate < MAX_WAVEPLATE) waveplate else MAX_WAVEPLATE, if (waveplate < MAX_WAVEPLATE) 0 else waveplate-MAX_WAVEPLATE))
    }

    data class WaveplateInitData(val initTime: Long, val waveplate: Int, val overplate: Int)
}