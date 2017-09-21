package com.mpavkovic.bluetoothbmw.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.widget.Toast
import com.mpavkovic.bluetoothbmw.util.Constants

import java.io.IOException
import java.io.OutputStream
import java.util.UUID

/**
 * Handles bluetooth connection to the car
 */

class CarBluetooth(val activity: Activity) {
    private var device: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null

    private var outputStream: OutputStream? = null

    private var connected = false

    // Initializes bluetooth module
    fun init(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(activity.applicationContext, "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
        } else {
            if (!bluetoothAdapter.isEnabled) {
                val enableAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(enableAdapter, 0)

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            } else {
                val bondedDevices = bluetoothAdapter.bondedDevices

                if (bondedDevices.isEmpty()) {
                    // Check for paired bluetooth devices
                    Toast.makeText(activity.applicationContext, "Please pair the car first", Toast.LENGTH_SHORT).show()
                } else {
                    for (iterator in bondedDevices) {
                        if (iterator.address == Constants.DEVICE_ADDRESS) {
                            device = iterator

                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    fun connect(): Boolean {
        try {
            socket = device!!.createRfcommSocketToServiceRecord(Constants.PORT_UUID) // Creates a socket to handle the outgoing connection
            socket!!.connect()

            Toast.makeText(activity.applicationContext,
                    "Connection to car successful", Toast.LENGTH_LONG).show()

            connected = true
        } catch (e: IOException) {
            e.printStackTrace()

            Toast.makeText(activity.applicationContext,
                    "Connection to car successful", Toast.LENGTH_LONG).show()

            connected = false
        }

        if (connected) {
            try {
                outputStream = socket!!.outputStream // Gets the output stream of the socket
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return connected
    }

    fun send(data: String) {
        try {
            outputStream!!.write((data + "\n").toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
