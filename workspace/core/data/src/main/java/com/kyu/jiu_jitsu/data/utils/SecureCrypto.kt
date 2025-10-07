package com.kyu.jiu_jitsu.data.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// 안드로이드 키 스토어 제공자 이름(고정 문자열)
private const val ANDROID_KEYSTORE = "AndroidKeyStore"
// 키 별칭(앱 내에서 이 이름으로 키를 찾아 씁니다)
private const val KEY_ALIAS = "jiu_jitsu_prefs_aes_key"
// AES + GCM 모드 + 패딩 없음 (GCM은 AEAD라 패딩 불필요)
private const val TRANSFORMATION = "AES/GCM/NoPadding"
// GCM 인증 태그 길이(비트). 일반적으로 128비트를 사용(강력 권장).
private const val GCM_TAG_BITS = 128
// GCM 권장 IV 길이: 12바이트(=96비트). 암호화마다 "고유"해야 안전.
private const val IV_LEN = 12

private fun getOrCreateAesKey(): SecretKey {
    val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    // 이미 만들어둔 키가 있으면 재사용
    (ks.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)
        ?.secretKey
        ?.let { return it }

    // 새 키 생성: AES, GCM 모드, NoPadding만 허용
    val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
    gen.init(
        KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            // 각 암호화 시 매번 랜덤 IV 요구 (GCM 특성상 매우 중요)
            .setRandomizedEncryptionRequired(true)
            .build()
    )
    return gen.generateKey()
}

fun encryptToBlob(plain: ByteArray): ByteArray {
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, getOrCreateAesKey())
    val iv = cipher.iv // 12 bytes
    val cipherText = cipher.doFinal(plain)
    return iv + cipherText // [IV||CIPHERTEXT(TAG 포함)]
}

fun decryptFromBlob(blob: ByteArray): ByteArray {
    require(blob.size > IV_LEN) { "Invalid blob" }
    val iv = blob.copyOfRange(0, IV_LEN)
    val payload = blob.copyOfRange(IV_LEN, blob.size)
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.DECRYPT_MODE, getOrCreateAesKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
    return cipher.doFinal(payload)
}