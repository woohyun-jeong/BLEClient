package com.tdcolvin.bleclient.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

val CTF_SERVICE_UUID: UUID = UUID.fromString("8c380000-10bd-4fdb-ba21-1922d6cf860d")
val PUBLICK_KEY_DATA_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380001-10bd-4fdb-ba21-1922d6cf860d")
val DATA_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380002-10bd-4fdb-ba21-1922d6cf860d")

@Suppress("DEPRECATION")
class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice
) {
    val isConnected = MutableStateFlow(false)
    val passwordRead = MutableStateFlow<String?>(null)
    val successfulNameWrites = MutableStateFlow(0)

    var publicKeyRead = MutableStateFlow<String?>(null)

    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())
    private var gatter: BluetoothGatt? = null
    var mtuSize = 0
    private var scenarioTest:ScenarioTest? = null

    private val TAG: String = BLEDeviceConnection::class.java.simpleName

    private val callback = object: BluetoothGattCallback() {
        // MTU 요청 결과를 처리하는 콜백
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            val functionName = "onConnectionStateChange"

            mtuSize = mtu

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG + functionName, "MTU Size : $mtu bytes")
                Log.d(TAG + functionName, "mtuSize " + mtuSize.toString())

            } else {
                Log.e(TAG + functionName, "MTU size request failed with status: $status")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val functionName = "onConnectionStateChange"

            val connected = newState == BluetoothGatt.STATE_CONNECTED
            if (connected) {
                gatter?.requestMtu(256)
                services.value = gatter?.services!!
                Log.d(TAG + functionName, "services : ${services.value.toString()}")
                Log.d(TAG + functionName, "gatter.services : ${gatter?.services!!.toString()}")
                Log.d(TAG + functionName, "discover : ${gatt.discoverServices().toString()}")
            }
            isConnected.value = connected
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            services.value = gatt.services
            val functionName = "onServicesDiscovered"
            Log.d(TAG + functionName, "status : $status")

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            val functionName = "onCharacteristicChanged"
            Log.d(TAG + functionName, "onCharacteristicChanged")
            Log.d(TAG + functionName, "characteristic value : ${characteristic.value.decodeToString()}")
            Log.d(TAG + functionName, "characteristic String value : ${String(characteristic.value)}")
            Log.d(TAG + functionName, "value : $value.decodeToString()")

            if (characteristic != null) {
                scenarioTest?.senarioTest_onNotify(gatt, characteristic)
            }

        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            val functionName = "onCharacteristicRead"
            super.onCharacteristicRead(gatt, characteristic, value, status)
            Log.d(TAG + functionName, "status: $status")
            Log.d(TAG + functionName, "characteristic value: ${characteristic.value}")
            Log.d(TAG + functionName, "value: $value")

            if (characteristic.value != null) {
                Log.d(TAG + functionName, "characteristic not null")
                scenarioTest?.let {
                    it.senarioTest_onRead(characteristic)
                }
            }

        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            val functionName = "onCharacteristicWrite"
            Log.d(TAG + functionName, "status : $status")
            Log.d(TAG + functionName, "characteristic : ${characteristic.value.decodeToString()}")
            Log.d(TAG + functionName, "characteristic : ${characteristic.value.joinToString()}")
            Log.d(TAG + functionName, "status : $status")

            if (characteristic.value != null) {
                Log.d(TAG + functionName, "characteristic not null")
                scenarioTest?.let {
                    it.senarioTest_onWrite()
                }
            }
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
            value: ByteArray
        ) {
            super.onDescriptorRead(gatt, descriptor, status, value)
            val functionName = "onDescriptorRead"
            Log.d(TAG + functionName, "descriptor : $descriptor")
            Log.d(TAG + functionName, "status : $status")
            Log.d(TAG + functionName, "value : $value")

        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            val functionName = "onDescriptorWrite"
            Log.d(TAG + functionName, "descriptor : $descriptor")
            Log.d(TAG + functionName, "status : $status")

        }

        override fun onServiceChanged(gatt: BluetoothGatt) {
            super.onServiceChanged(gatt)
            val functionName = "onServiceChanged"
            Log.d(TAG + functionName, "tttt")

        }

        override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
            super.onReliableWriteCompleted(gatt, status)
            val functionName = "onReliableWriteCompleted"
            Log.d(TAG + functionName, "status : $status")
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            val functionName = "onReadRemoteRssi"
            Log.d(TAG + functionName, "rssi : $rssi")
            Log.d(TAG + functionName, "status : $status")

        }

    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnect() {
        val functionName = "disconnect"
        Log.d(TAG + functionName, "disconnect")

        gatter?.disconnect()
        gatter?.close()
        gatter = null
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connect() {
        val functionName = "connect"
        Log.d(TAG + functionName, "connect()")

        gatter = bluetoothDevice.connectGatt(context, false, callback)
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverServices() {
        val functionName = "discoverServices"
        Log.d(TAG + functionName, "discoverServices()")

        gatter?.discoverServices()
    }

    fun getScenarioTest(scenario: SCENARIO): ScenarioTest? {
        if (scenarioTest == null) {
            scenarioTest = ScenarioTest(gatter!!, mtuSize, scenario)
        }
        return scenarioTest

    }

}