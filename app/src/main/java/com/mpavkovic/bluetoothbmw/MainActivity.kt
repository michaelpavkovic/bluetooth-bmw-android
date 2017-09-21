package com.mpavkovic.bluetoothbmw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*

import com.mpavkovic.bluetoothbmw.bluetooth.CarBluetooth

class MainActivity : AppCompatActivity() {
    private lateinit var carBluetooth: CarBluetooth
    private lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        carBluetooth = CarBluetooth(this)
    }

    fun onClickSend(view: View) {
        thread = Thread(Runnable { runCar() })

        thread.start()
    }

    fun onClickConnect(view: View) {

        if (carBluetooth.init()) {
            carBluetooth.connect()
        }
    }

    private fun getSpeed(): Int {
        return Math.round(seek_speed.progress.toFloat() / seek_speed.max * 5.0f + 62.0f)
    }

    private val steeringAngle: Int
        get() = Math.round(seek_steering.progress.toFloat() / seek_steering.max * 40.0f + 65.0f)

    private fun runCar() {
        while (true) {
            val steeringAngle = steeringAngle

            if (steeringAngle >= 100) carBluetooth.send("a" + steeringAngle)
            else carBluetooth.send("a0" + steeringAngle)

            delay()

            carBluetooth.send("b0" + getSpeed())

            delay()
        }
    }

    private fun delay() {
        try {
            Thread.sleep(25)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
