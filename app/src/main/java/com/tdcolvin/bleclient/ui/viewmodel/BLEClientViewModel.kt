package com.tdcolvin.bleclient.ui.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tdcolvin.bleclient.ble.BLEDeviceConnection
import com.tdcolvin.bleclient.ble.BLEScanner
import com.tdcolvin.bleclient.ble.PERMISSION_BLUETOOTH_CONNECT
import com.tdcolvin.bleclient.ble.PERMISSION_BLUETOOTH_SCAN
import com.tdcolvin.bleclient.ble.SCENARIO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BLEClientViewModel(private val application: Application): AndroidViewModel(application) {
    private val bleScanner = BLEScanner(application).apply {
    }
    private var activeConnection = MutableStateFlow<BLEDeviceConnection?>(null)

    private val isDeviceConnected = activeConnection.flatMapLatest { it?.isConnected ?: flowOf(false) }
    private val activeDeviceServices = activeConnection.flatMapLatest {
        it?.services ?: flowOf(emptyList())
    }
    private val activePublicKey = activeConnection.flatMapLatest {
        it?.publicKeyRead ?: flowOf(null)
    }

    private val _uiState = MutableStateFlow(BLEClientUIState())
    val uiState = combine(
        _uiState,
        isDeviceConnected,
        activeDeviceServices,
        activePublicKey
    ) { state, isDeviceConnected, services, publickey ->
        state.copy(
            isDeviceConnected = isDeviceConnected,
            discoveredCharacteristics = services.associate { service -> Pair(service.uuid.toString(), service.characteristics.map { it.uuid.toString() }) },
            publicKey = publickey,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BLEClientUIState())

    init {
        viewModelScope.launch {
            bleScanner.foundDevices.collect { devices ->
                _uiState.update { it.copy(foundDevices = devices) }
            }
        }
        viewModelScope.launch {
            bleScanner.isScanning.collect { isScanning ->
                _uiState.update { it.copy(isScanning = isScanning) }
            }
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun startScanning() {
        bleScanner.startScanning()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun stopScanning() {
        bleScanner.stopScanning()
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(allOf = [PERMISSION_BLUETOOTH_CONNECT, PERMISSION_BLUETOOTH_SCAN])
    fun setActiveDevice(device: BluetoothDevice?) {
        activeConnection.value = device?.run { BLEDeviceConnection(application, device) }
        _uiState.update { it.copy(activeDevice = device) }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connectActiveDevice() {
        activeConnection.value?.connect()
        Log.d("BLEDeviceConnection ", activeConnection.value.hashCode().toString())
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnectActiveDevice() {
        activeConnection.value?.disconnect()
        activeConnection.value = null
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverActiveDeviceServices() {
        activeConnection.value?.discoverServices()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest1ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_1)?.senarioTest1_write()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest2ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_2)?.senarioTest2_write()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest3ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_3)?.senarioTest3_write()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest4ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_4)?.senarioTest4_write()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest5ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_5)?.senarioTest5_write()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest6ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_6)?.senarioTest6_read_request()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest7ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_7)?.senarioTest2_write()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun scenarioTest8ToActiveDevice() {
        activeConnection.value?.getScenarioTest(SCENARIO.SCENARIO_8)?.senarioTest2_write()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun sendPublicKeyToActiveDevice() {
//        activeConnection.value?.sendPublicKey()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun receivePublicKeyToActiveDevice() {
//        activeConnection.value?.receivePublicKey()
    }

    override fun onCleared() {
        super.onCleared()

        //when the ViewModel dies, shut down the BLE client with it
        if (bleScanner.isScanning.value) {
            if (ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bleScanner.stopScanning()
            }
        }
    }
}

data class BLEClientUIState(
    val isScanning: Boolean = false,
    val foundDevices: List<BluetoothDevice> = emptyList(),
    val activeDevice: BluetoothDevice? = null,
    val isDeviceConnected: Boolean = false,
    val discoveredCharacteristics: Map<String, List<String>> = emptyMap(),
    val publicKey: String? = null
)