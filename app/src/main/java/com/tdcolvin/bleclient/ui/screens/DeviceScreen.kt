package com.tdcolvin.bleclient.ui.screens

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tdcolvin.bleclient.ble.CTF_SERVICE_UUID

@Composable
fun DeviceScreen(
    unselectDevice: () -> Unit,
    isDeviceConnected: Boolean,
    discoveredCharacteristics: Map<String, List<String>>,
    connect: () -> Unit,
    discoverServices: () -> Unit,
    scenarioTest1: () -> Unit,
    scenarioTest2: () -> Unit,
    scenarioTest3: () -> Unit,
    scenarioTest4: () -> Unit,
    scenarioTest5: () -> Unit,
    scenarioTest6: () -> Unit,
    sendPublicKey: () -> Unit,
    receivePublicKey: () -> Unit,
    publicKey: String?
) {
    val foundTargetService = discoveredCharacteristics.contains(CTF_SERVICE_UUID.toString())

    Column(
        Modifier.scrollable(rememberScrollState(), Orientation.Vertical)
    ) {
        Button(onClick = connect) {
            Text("1. Connect")
        }
        Text("Device connected: $isDeviceConnected")
        Button(onClick = discoverServices, enabled = isDeviceConnected) {
            Text("2. Discover Services")
        }
//        LazyColumn {
//            items(discoveredCharacteristics.keys.sorted()) { serviceUuid ->
//                Text(text = serviceUuid, fontWeight = FontWeight.Black)
//                Column(modifier = Modifier.padding(start = 10.dp)) {
//                    discoveredCharacteristics[serviceUuid]?.forEach {
//                        Text(it)
//                    }
//                }
//            }
//        }
        Button(onClick = scenarioTest1, enabled = isDeviceConnected && foundTargetService) {
            Text("시나리오 테스트 1")
        }
        Button(onClick = scenarioTest2, enabled = isDeviceConnected && foundTargetService) {
            Text("시나리오 테스트 2")
        }
        Button(onClick = scenarioTest3, enabled = isDeviceConnected && foundTargetService) {
            Text("시나리오 테스트 3")
        }
        Button(onClick = scenarioTest4, enabled = isDeviceConnected && foundTargetService) {
            Text("시나리오 테스트 4")
        }
        Button(onClick = scenarioTest5, enabled = isDeviceConnected && foundTargetService) {
            Text("시나리오 테스트 5")
        }
        Button(onClick = scenarioTest6, enabled = isDeviceConnected && foundTargetService) {
            Text("시나리오 테스트 6")
        }
        if (publicKey != null) {
            Text("public key: $publicKey")
        }

        OutlinedButton(modifier = Modifier.padding(top = 40.dp),  onClick = unselectDevice) {
            Text("Disconnect")
        }
    }
}
