package com.tdcolvin.bleclient.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ECTest {
    private val TAG: String = ECTest::class.java.simpleName
    var publicKey: PublicKey? = null
    private var privateKey: PrivateKey? = null

    fun ecPublicKeyReadReceive(characteristic: BluetoothGattCharacteristic): PublicKey {
        val ecParameterSpec: ECParameterSpec = KeyFactory
            .getInstance("EC")
            .getKeySpec(
                publicKey,
                ECPublicKeySpec::class.java
            ).params
        return getEcPublicKey(characteristic.value, ecParameterSpec)

    }

    @SuppressLint("MissingPermission")
    fun ecPublicKeyReadRequest(gatter: BluetoothGatt) {
        val service = gatter.getService(CTF_SERVICE_UUID)
        if (service == null) {
            Log.v("bluetooth", "service error")

        }
        val characteristic = service?.getCharacteristic(PUBLICK_KEY_DATA_CHARACTERISTIC_UUID)
        gatter.readCharacteristic(characteristic)

    }

    @SuppressLint("MissingPermission")
    fun ecPublicKeyWrite(gatter: BluetoothGatt) {
        val functionName = "writeTest99"

        val service = gatter.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(PUBLICK_KEY_DATA_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            generateKeyPair().apply {
                publicKey = this.first
                privateKey = this.second
            }

            Log.d(TAG + functionName,"public key (String): " + publicKey!!.encoded.decodeToString())
            Log.d(TAG + functionName,"public key (String) size: " + publicKey!!.encoded.decodeToString().length.toString())
            Log.d(TAG + functionName,"public key (Byte): " + "${publicKey}")
            Log.d(TAG + functionName,"public key (Byte) size: " + publicKey!!.encoded.size.toString())
            Log.d(TAG + functionName,"public format: " + "${publicKey!!.format}")
            Log.d(TAG + functionName,"public key :" + publicKeyToString(publicKey!!))

            Log.d("TTTT public Base64 size :", publicKeyToString(publicKey!!).length.toString())
            Log.d("TTTT privateKey key :", priveKeyToString(privateKey!!))
            Log.d("TTTT publicKey key byte size :", base64ToByteArray(publicKeyToString(publicKey!!)).size.toString())
            Log.d("TTTT sendData :", "String = ${android.util.Base64.encode(sendData(publicKey!!), android.util.Base64.NO_PADDING).decodeToString()}")

            characteristic.value = sendData(publicKey!!)

            val success = gatter?.writeCharacteristic(characteristic)
            Log.v("bluetooth", "Write status: $success")
        } else {
            Log.v("bluetooth", "Write func error")

        }
    }

    private fun sendData(publicKey: PublicKey): ByteArray? {
        if (publicKey is ECPublicKey) {
            val ecPublicKey = publicKey as ECPublicKey
            val affineXByteArray = ecPublicKey.w.affineX.toByteArray()
            val filteredAffineXByteArray = filterMostSignificantByte(affineXByteArray)
            val affineYByteArray = ecPublicKey.w.affineY.toByteArray()
            val filteredAffineYByteArray = filterMostSignificantByte(affineYByteArray)
            val keyByteArray = byteArrayOf(0x04).plus(filteredAffineXByteArray).plus(filteredAffineYByteArray)
            Log.d(TAG, "ECPublicKey affineXByteArray = ${affineXByteArray.contentToString()}, size = ${affineXByteArray.size}")
            Log.d(TAG, "ECPublicKey affineYByteArray = ${affineYByteArray.contentToString()}, size = ${affineYByteArray.size}")
            Log.d(TAG, "ECPublicKey filteredAffineXByteArray = ${filteredAffineXByteArray.contentToString()}, size = ${filteredAffineXByteArray.size}")
            Log.d(TAG, "ECPublicKey filteredAffineYByteArray = ${filteredAffineYByteArray.contentToString()}, size = ${filteredAffineYByteArray.size}")
            Log.d(TAG, "ECPublicKey keyByteArray = ${keyByteArray.contentToString()}, size = ${keyByteArray.size}")
            Log.d(TAG, "String = ${android.util.Base64.encode(keyByteArray, android.util.Base64.NO_PADDING).decodeToString()}")

            return keyByteArray
        } else {
            Log.d(TAG, "ECPublicKey This is not an EC public key.")
        }
        return null
    }

    fun filterMostSignificantByte(byteArray: ByteArray): ByteArray {
        val xBytes32 = ByteArray(32)
        val byteArraySize = byteArray.size
        Log.d(TAG, "filterMostSignificantByte byteArray = ${byteArray.decodeToString()}, byteArraySize = $byteArraySize")

        if (byteArraySize <= 32) {
            // 패딩 추가
            System.arraycopy(byteArray, 0, xBytes32, 32 - byteArraySize, byteArraySize)
        } else if (byteArraySize == 33) {
            // 33바이트인 경우, 최상위 바이트 제거
            System.arraycopy(byteArray, 1, xBytes32, 0, 32)
        } else {
            throw Throwable("removeMostSignificantByte Too many Byte")
        }

        return xBytes32
    }

    fun generateKeyPair(): Pair<PublicKey, PrivateKey> {
        val keyGen = KeyPairGenerator.getInstance("EC")
        keyGen.initialize(ECGenParameterSpec("secp256r1")) // P-256은 secp256r1로 정의됨
        val keyPair = keyGen.generateKeyPair()
        return Pair(keyPair.public, keyPair.private)
    }

    fun generateSharedSecret(privateKey: PrivateKey, publicKey: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        return keyAgreement.generateSecret()
    }

    fun priveKeyToString(privateKey: PrivateKey): String {
        return Base64.getEncoder().encodeToString(privateKey.encoded)
    }

    fun publicKeyToString(publicKey: PublicKey): String {
        return Base64.getEncoder().encodeToString(publicKey.encoded)
    }

    fun base64ToByteArray(base64String: String): ByteArray {
        return Base64.getDecoder().decode(base64String)
    }

    private fun getEcPublicKey(ecPublicKey: ByteArray, params: ECParameterSpec): PublicKey {
        val ecPointX = ecPublicKey.sliceArray(IntRange(1, 32))
        val ecPointY = ecPublicKey.sliceArray(IntRange(33, 64))
        // x와 y를 BigInteger로 변환
        val x = BigInteger(1, ecPointX) // 1은 부호를 나타냄 (양수)
        val y = BigInteger(1, ecPointY)

        // ECPoint를 사용하여 공개 키의 포인트 정의
        val ecPoint = ECPoint(x, y)
        val keyFactory = KeyFactory.getInstance("EC") // ECDH 알고리즘 사용
        val pubSpec = ECPublicKeySpec(ecPoint, params)
        return keyFactory.generatePublic(pubSpec)

    }

    // AES-256 암호화
    fun encrypt(data: ByteArray, secret: ByteArray): ByteArray {
        val key: SecretKey = SecretKeySpec(secret.copyOf(32), "AES") // 32 bytes for AES-256
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16).apply { java.security.SecureRandom().nextBytes(this) } // 랜덤 IV 생성
        val ivParameterSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec)
        val encrypted = cipher.doFinal(data)

        // IV와 암호문을 Base64로 인코딩하여 반환
        return iv + encrypted
    }

    // AES-256 복호화
    fun decrypt(encryptedData: String, secret: ByteArray): ByteArray {
        val key: SecretKey = SecretKeySpec(secret.copyOf(32), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val decodedData = Base64.getDecoder().decode(encryptedData)
        val iv = decodedData.copyOfRange(0, 16) // IV를 추출
        val encryptedBytes = decodedData.copyOfRange(16, decodedData.size)

        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec)
        return cipher.doFinal(encryptedBytes)
    }

}