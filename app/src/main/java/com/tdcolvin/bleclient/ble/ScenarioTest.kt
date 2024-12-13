package com.tdcolvin.bleclient.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.charset.Charset

enum class SCENARIO {
    SCENARIO_1,
    SCENARIO_2,
    SCENARIO_3,
    SCENARIO_4,
    SCENARIO_5,
    SCENARIO_6,
    SCENARIO_7,
    SCENARIO_8
}

class ScenarioTest(gatter: BluetoothGatt, mtuSize: Int, scenario: SCENARIO) {
    private val TAG: String = ScenarioTest::class.java.simpleName
    val gatter = gatter
    val mtuSize = mtuSize
    var clientOffset = 0
    var scenario = scenario
    var fullData:ByteArray? = byteArrayOf()
    val data = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbccccccccccccccccccccccccccccccccceeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeefffffffffffffffffffffffffffffffffffffffffdddddddddddddddddddddddddddddddddddddddddd하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이하이END_OF_DATA"

    @SuppressLint("MissingPermission")
    fun senarioTest1_write(offset:Int = 0) {
        val functionName = "senarioTest1_write"

        var clientOffset = offset
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

        val dataBytes = data.toByteArray(Charset.forName("UTF-8")) // 특성 값이 너무 크면 데이터를 MTU 크기만큼 나누어 전송
        Log.d(TAG + functionName,"byte total length :" + "${dataBytes.size}")

//        ATT 헤더: 3 바이트
//        L2CAP 헤더: 4 바이트
        val mtuSize = mtuSize - 7 // 헤더 크기 등 고려 (오버헤드를 고려하여 실제 데이터 전송 크기 계산)
        val endOffset = minOf(clientOffset + mtuSize, dataBytes.size) // 요청된 오프셋에 해당하는 데이터 범위 계산
        Log.d(TAG + functionName,"offset :" + "${clientOffset}")
        Log.d(TAG + functionName,"endOffset :" + "${endOffset}")

        if (clientOffset < dataBytes.size) {
            val dataChunk = dataBytes.copyOfRange(clientOffset, endOffset)
            Log.d(TAG + functionName,"Sending data chunk: " + "${dataChunk.decodeToString()}")
            Log.d(TAG + functionName,"Sending data chunk size: " + "${dataChunk.size}")

            characteristic?.value = dataChunk
            // 서버에 데이터 전송
            val success = gatter.writeCharacteristic(characteristic)
            Log.d(TAG + functionName,"Write status :" + "${success}")

            this.clientOffset = endOffset
            // 데이터가 아직 남아 있다면, 이어서 요청을 처리
            if (endOffset < dataBytes.size) {
                Log.d(TAG + functionName,"Remaining data, waiting for next read request")
            } else {
                this.clientOffset = 0
                Log.d(TAG + functionName,"Data transmission complete.")
            }
        } else {
            this.clientOffset = 0
            // 요청된 offset이 데이터 범위를 벗어나면 오류 응답
        }

    }

    @SuppressLint("MissingPermission")
    fun senarioTest2_write(offset:Int = 0) {
        val functionName = "senarioTest1_write"

        var clientOffset = offset
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        gatter.setCharacteristicNotification(characteristic, true);

        val dataBytes = data.toByteArray(Charset.forName("UTF-8")) // 특성 값이 너무 크면 데이터를 MTU 크기만큼 나누어 전송
        Log.d(TAG + functionName,"byte total length :" + "${dataBytes.size}")

//        ATT 헤더: 3 바이트
//        L2CAP 헤더: 4 바이트
        val mtuSize = mtuSize - 7 // 헤더 크기 등 고려 (오버헤드를 고려하여 실제 데이터 전송 크기 계산)
        val endOffset = minOf(clientOffset + mtuSize, dataBytes.size) // 요청된 오프셋에 해당하는 데이터 범위 계산
        Log.d(TAG + functionName,"offset :" + "${clientOffset}")
        Log.d(TAG + functionName,"endOffset :" + "${endOffset}")

        if (clientOffset < dataBytes.size) {
            val dataChunk = dataBytes.copyOfRange(clientOffset, endOffset)
            Log.d(TAG + functionName,"Sending data chunk: " + "${dataChunk.decodeToString()}")
            Log.d(TAG + functionName,"Sending data chunk size: " + "${dataChunk.size}")

            characteristic?.value = dataChunk
            // 서버에 데이터 전송
            val success = gatter.writeCharacteristic(characteristic)
            Log.d(TAG + functionName,"Write status :" + "${success}")

            this.clientOffset = endOffset
            // 데이터가 아직 남아 있다면, 이어서 요청을 처리
            if (endOffset < dataBytes.size) {
                Log.d(TAG + functionName,"Remaining data, waiting for next read request")
            } else {
                this.clientOffset = 0
                Log.d(TAG + functionName,"Data transmission complete.")
            }
        } else {
            this.clientOffset = 0
            // 요청된 offset이 데이터 범위를 벗어나면 오류 응답
        }

    }

    @SuppressLint("MissingPermission")
    fun senarioTest3_write(offset:Int = 0) {
        val functionName = "senarioTest1_write"

        var clientOffset = offset
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        gatter.setCharacteristicNotification(characteristic, true);

        val dataBytes = data.toByteArray(Charset.forName("UTF-8")) // 특성 값이 너무 크면 데이터를 MTU 크기만큼 나누어 전송
        Log.d(TAG + functionName,"byte total length :" + "${dataBytes.size}")

//        ATT 헤더: 3 바이트
//        L2CAP 헤더: 4 바이트
        val mtuSize = mtuSize - 7 // 헤더 크기 등 고려 (오버헤드를 고려하여 실제 데이터 전송 크기 계산)
        val endOffset = minOf(clientOffset + mtuSize, dataBytes.size) // 요청된 오프셋에 해당하는 데이터 범위 계산
        Log.d(TAG + functionName,"offset :" + "${clientOffset}")
        Log.d(TAG + functionName,"endOffset :" + "${endOffset}")

        if (clientOffset < dataBytes.size) {
            val dataChunk = dataBytes.copyOfRange(clientOffset, endOffset)
            Log.d(TAG + functionName,"Sending data chunk: " + "${dataChunk.decodeToString()}")
            Log.d(TAG + functionName,"Sending data chunk size: " + "${dataChunk.size}")

            characteristic?.value = dataChunk
            // 서버에 데이터 전송
            val success = gatter.writeCharacteristic(characteristic)
            Log.d(TAG + functionName,"Write status :" + "${success}")

            this.clientOffset = endOffset
            // 데이터가 아직 남아 있다면, 이어서 요청을 처리
            if (endOffset < dataBytes.size) {
                Log.d(TAG + functionName,"Remaining data, waiting for next read request")
                CoroutineScope(Dispatchers.IO).launch {
                    delay(200)
                    senarioTest3_write(endOffset)
                }
            } else {
                this.clientOffset = 0
                Log.d(TAG + functionName,"Data transmission complete.")
            }
        } else {
            this.clientOffset = 0
            // 요청된 offset이 데이터 범위를 벗어나면 오류 응답
        }

    }

    @SuppressLint("MissingPermission")
    fun senarioTest4_write(offset:Int = 0) {
        val functionName = "senarioTest1_write"

        var clientOffset = offset
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        gatter.setCharacteristicNotification(characteristic, true)

        val dataBytes = data.toByteArray(Charset.forName("UTF-8")) // 특성 값이 너무 크면 데이터를 MTU 크기만큼 나누어 전송
        Log.d(TAG + functionName,"byte total length :" + "${dataBytes.size}")

//        ATT 헤더: 3 바이트
//        L2CAP 헤더: 4 바이트
        val mtuSize = mtuSize - 7 // 헤더 크기 등 고려 (오버헤드를 고려하여 실제 데이터 전송 크기 계산)
        val endOffset = minOf(clientOffset + mtuSize, dataBytes.size) // 요청된 오프셋에 해당하는 데이터 범위 계산
        Log.d(TAG + functionName,"offset :" + "${clientOffset}")
        Log.d(TAG + functionName,"endOffset :" + "${endOffset}")

        if (clientOffset < dataBytes.size) {
            val dataChunk = dataBytes.copyOfRange(clientOffset, endOffset)
            Log.d(TAG + functionName,"Sending data chunk: " + "${dataChunk.decodeToString()}")
            Log.d(TAG + functionName,"Sending data chunk size: " + "${dataChunk.size}")

            characteristic?.value = dataChunk
            // 서버에 데이터 전송
            val success = gatter.writeCharacteristic(characteristic)
            Log.d(TAG + functionName,"Write status :" + "${success}")

            this.clientOffset = endOffset
            // 데이터가 아직 남아 있다면, 이어서 요청을 처리
            if (endOffset < dataBytes.size) {
                Log.d(TAG + functionName,"Remaining data, waiting for next read request")
                CoroutineScope(Dispatchers.IO).launch {
                    delay(200)
                    senarioTest4_write(endOffset)
                }
            } else {
                this.clientOffset = 0
                Log.d(TAG + functionName,"Data transmission complete.")
            }
        } else {
            this.clientOffset = 0
            // 요청된 offset이 데이터 범위를 벗어나면 오류 응답
        }

    }

    @SuppressLint("MissingPermission")
    fun senarioTest5_write(offset:Int = 0) {
        val functionName = "senarioTest1_write"

        var clientOffset = offset
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        gatter.setCharacteristicNotification(characteristic, true)

        val dataBytes = data.toByteArray(Charset.forName("UTF-8")) // 특성 값이 너무 크면 데이터를 MTU 크기만큼 나누어 전송
        Log.d(TAG + functionName,"byte total length :" + "${dataBytes.size}")

//        ATT 헤더: 3 바이트
//        L2CAP 헤더: 4 바이트
        val mtuSize = mtuSize - 7 // 헤더 크기 등 고려 (오버헤드를 고려하여 실제 데이터 전송 크기 계산)
        val endOffset = minOf(clientOffset + mtuSize, dataBytes.size) // 요청된 오프셋에 해당하는 데이터 범위 계산
        Log.d(TAG + functionName,"offset :" + "${clientOffset}")
        Log.d(TAG + functionName,"endOffset :" + "${endOffset}")

        if (clientOffset < dataBytes.size) {
            val dataChunk = dataBytes.copyOfRange(clientOffset, endOffset)
            Log.d(TAG + functionName,"Sending data chunk: " + "${dataChunk.decodeToString()}")
            Log.d(TAG + functionName,"Sending data chunk size: " + "${dataChunk.size}")

            characteristic?.value = dataChunk
            // 서버에 데이터 전송
            val success = gatter.writeCharacteristic(characteristic)
            Log.d(TAG + functionName,"Write status :" + "${success}")

            this.clientOffset = endOffset
            // 데이터가 아직 남아 있다면, 이어서 요청을 처리
            if (endOffset < dataBytes.size) {
                Log.d(TAG + functionName,"Remaining data, waiting for next read request")
                CoroutineScope(Dispatchers.IO).launch {
                    delay(200)
                    senarioTest5_write(endOffset)
                }
            } else {
                this.clientOffset = 0
                Log.d(TAG + functionName,"Data transmission complete.")
            }
        } else {
            this.clientOffset = 0
            // 요청된 offset이 데이터 범위를 벗어나면 오류 응답
        }

    }

    @SuppressLint("MissingPermission")
    fun senarioTest1_read(
        characteristic: BluetoothGattCharacteristic,
    ) {
        val functionName = "senarioTest1_read"

        val receivedData = characteristic.value
        fullData = fullData?.plus(receivedData) // 데이터를 StringBuilder에 추가

        val strReceivedData = String(receivedData)
        Log.d(TAG + functionName,"Reeived data chunk: " + "$strReceivedData")

        // 받은 데이터가 "END_OF_DATA"인지 확인
        if (String(receivedData).contains("END_OF_DATA")) {
            // "END_OF_DATA"가 포함되었다면, 데이터 수신 완료
            val hexString = fullData?.joinToString(" ") { String.format("%02X", it) }
            Log.d(TAG + functionName,"Complete Byte Data: " + "ByteArray: $hexString")
            Log.d(TAG + functionName,"Complete Data: " + java.lang.String(fullData))
            fullData = null
            fullData = byteArrayOf()
        } else {
            // 다음 데이터를 읽기 위해 호출
            val success = gatter.readCharacteristic(characteristic)
            Log.d(TAG + functionName, "success: " + success.toString())

        }
    }

    @SuppressLint("MissingPermission")
    fun senarioTest1_read_request() {
        val functionName = "senarioTest1_read_request"
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)

        val success = gatter.readCharacteristic(characteristic)
        Log.d(TAG + functionName, "success: " + success.toString())

    }

    @SuppressLint("MissingPermission")
    fun senarioTest4_read_request() {
        val functionName = "senarioTest1_read_request"
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)

        val success = gatter.readCharacteristic(characteristic)
        Log.d(TAG + functionName, "success: " + success.toString())

    }

    @SuppressLint("MissingPermission")
    fun senarioTest6_read_request() {
        val functionName = "senarioTest1_read_request"
        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)

        val success = gatter.readCharacteristic(characteristic)
        Log.d(TAG + functionName, "success: " + success.toString())

    }

    @SuppressLint("MissingPermission")
    fun senarioTest4_read(
        characteristic: BluetoothGattCharacteristic,
    ) {
        val functionName = "senarioTest1_read"

        val receivedData = characteristic.value
        fullData = fullData?.plus(receivedData) // 데이터를 StringBuilder에 추가

        val strReceivedData = String(receivedData)
        Log.d(TAG + functionName,"Reeived data chunk: " + "$strReceivedData")

        // 받은 데이터가 "END_OF_DATA"인지 확인
        if (String(receivedData).contains("END_OF_DATA")) {
            // "END_OF_DATA"가 포함되었다면, 데이터 수신 완료
            val hexString = fullData?.joinToString(" ") { String.format("%02X", it) }
            Log.d(TAG + functionName,"Complete Byte Data: " + "ByteArray: $hexString")
            Log.d(TAG + functionName,"Complete Data: " + java.lang.String(fullData))
            fullData = null
            fullData = byteArrayOf()
        } else {
            // 다음 데이터를 읽기 위해 호출
            val success = gatter.readCharacteristic(characteristic)
            Log.d(TAG + functionName, "success: " + success.toString())

        }
    }

    @SuppressLint("MissingPermission")
    fun senarioTest6_read(
        characteristic: BluetoothGattCharacteristic,
    ) {
        val functionName = "senarioTest1_read"

        val receivedData = characteristic.value
        fullData = fullData?.plus(receivedData) // 데이터를 StringBuilder에 추가

        val strReceivedData = String(receivedData)
        Log.d(TAG + functionName,"Reeived data chunk: " + "$strReceivedData")

        // 받은 데이터가 "END_OF_DATA"인지 확인
        if (String(receivedData).contains("END_OF_DATA")) {
            // "END_OF_DATA"가 포함되었다면, 데이터 수신 완료
            val hexString = fullData?.joinToString(" ") { String.format("%02X", it) }
            Log.d(TAG + functionName,"Complete Byte Data: " + "ByteArray: $hexString")
            Log.d(TAG + functionName,"Complete Data: " + java.lang.String(fullData))
            fullData = null
            fullData = byteArrayOf()
        } else {
            // 다음 데이터를 읽기 위해 호출
            val success = gatter.readCharacteristic(characteristic)
            Log.d(TAG + functionName, "success: " + success.toString())

        }
    }

    fun senarioTest_onRead(characteristic: BluetoothGattCharacteristic) {
        when (scenario) {
            SCENARIO.SCENARIO_1 -> {
                senarioTest1_read(characteristic)
            }
            SCENARIO.SCENARIO_2 -> {
                // notification
            }
            SCENARIO.SCENARIO_3 -> {
                // notification
            }
            SCENARIO.SCENARIO_4 -> {
                senarioTest4_read(characteristic)

            }
            SCENARIO.SCENARIO_5 -> {

            }
            SCENARIO.SCENARIO_6 -> {
                senarioTest6_read(characteristic)

            }
            SCENARIO.SCENARIO_7 -> {

            }
            SCENARIO.SCENARIO_8 -> {

            }


        }
    }

    fun senarioTest_onWrite() {
        when (scenario) {
            SCENARIO.SCENARIO_1 -> {
                if (clientOffset == 0) {
                    senarioTest1_read_request()
                } else {
                    senarioTest1_write(clientOffset)
                }

            }
            SCENARIO.SCENARIO_2 -> {
                if (clientOffset == 0) {
                } else {
                    senarioTest2_write(clientOffset)
                }
            }
            SCENARIO.SCENARIO_3 -> {
                // nothing...
            }
            SCENARIO.SCENARIO_4 -> {
                // nothing...

            }
            SCENARIO.SCENARIO_5 -> {

            }
            SCENARIO.SCENARIO_6 -> {

            }
            SCENARIO.SCENARIO_7 -> {

            }
            SCENARIO.SCENARIO_8 -> {

            }

        }
    }

    fun senarioTest_onNotify(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        when (scenario) {
            SCENARIO.SCENARIO_1 -> {
            }
            SCENARIO.SCENARIO_2 -> {
            }
            SCENARIO.SCENARIO_3 -> {
            }
            SCENARIO.SCENARIO_4 -> {
                senarioTest4_read_request()
            }
            SCENARIO.SCENARIO_5 -> {

            }
            SCENARIO.SCENARIO_6 -> {

            }
            SCENARIO.SCENARIO_7 -> {

            }
            SCENARIO.SCENARIO_8 -> {

            }

        }
    }

}