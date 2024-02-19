package com.armada.wmshandler.utils

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

object BeepHelper {

    fun beep(duration: Int) {
        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        toneG.startTone(ToneGenerator.TONE_DTMF_S, duration)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            toneG.release()
        }, 1000L)
    }
}