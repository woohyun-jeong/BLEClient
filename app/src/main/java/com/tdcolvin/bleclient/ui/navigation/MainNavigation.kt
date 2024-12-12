package com.tdcolvin.bleclient.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tdcolvin.bleclient.ui.screens.DeviceScreen
import com.tdcolvin.bleclient.ui.screens.PermissionsRequiredScreen
import com.tdcolvin.bleclient.ui.screens.ScanningScreen
import com.tdcolvin.bleclient.ui.screens.haveAllPermissions
import com.tdcolvin.bleclient.ui.viewmodel.BLEClientViewModel

@SuppressLint("MissingPermission")
@Composable
fun MainNavigation(viewModel: BLEClientViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var allPermissionsGranted by remember {
        mutableStateOf (haveAllPermissions(context))
    }

    if (!allPermissionsGranted) {
        PermissionsRequiredScreen { allPermissionsGranted = true }
    }
    else if (uiState.activeDevice == null) {
        ScanningScreen(
            isScanning = uiState.isScanning,
            foundDevices = uiState.foundDevices,
            startScanning = viewModel::startScanning,
            stopScanning = viewModel::stopScanning,
            selectDevice = { device ->
                viewModel.stopScanning()
                viewModel.setActiveDevice(device)
            }
        )
    }
    else {
        DeviceScreen(
            unselectDevice = {
                viewModel.disconnectActiveDevice()
                viewModel.setActiveDevice(null)
            },
            isDeviceConnected = uiState.isDeviceConnected,
            discoveredCharacteristics = uiState.discoveredCharacteristics,
            connect = viewModel::connectActiveDevice,
            discoverServices = viewModel::discoverActiveDeviceServices,
            scenarioTest1 = viewModel::scenarioTest1ToActiveDevice,
            scenarioTest2 = viewModel::scenarioTest2ToActiveDevice,
            scenarioTest3 = viewModel::scenarioTest3ToActiveDevice,
            scenarioTest4 = viewModel::scenarioTest4ToActiveDevice,
            scenarioTest5 = viewModel::scenarioTest5ToActiveDevice,
            scenarioTest6 = viewModel::scenarioTest6ToActiveDevice,
            sendPublicKey = viewModel::sendPublicKeyToActiveDevice,
            receivePublicKey = viewModel::receivePublicKeyToActiveDevice,
            publicKey = uiState.publicKey
        )
    }
}