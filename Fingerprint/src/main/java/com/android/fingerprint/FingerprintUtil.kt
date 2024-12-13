package com.android.fingerprint

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.os.SystemProperties

fun isSupportFingerprint(context: Context): Boolean {
    try {
        val fpm: FingerprintManager =
            context.getSystemService(FingerprintManager::class.java) as FingerprintManager
        if (fpm.isHardwareDetected) return true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

private var mEnrollmentCancel: CancellationSignal? = null
private var mFingerprintManager: FingerprintManager? = null
private val mToken = ByteArray(69)
var mEnrolling = false


interface FingerprintEnrollmentCallback {
    fun onEnrollmentProgress(remaining: Int)
    fun onEnrollmentHelp(helpMsgId: Int, helpString: CharSequence)
    fun onEnrollmentError(errMsgId: Int, errString: CharSequence)
}


fun startEnrollment(context: Context, callback: FingerprintEnrollmentCallback) {
    mEnrollmentCancel = CancellationSignal()
    mFingerprintManager =
        context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
    mFingerprintManager!!.generateChallenge(
        0,
        (FingerprintManager.GenerateChallengeCallback { _: Int, _: Int, challenge: Long ->
            if (SystemProperties.get("persist.vendor.fingerprint.chip", "none") == "chipone") {
                for (i in mToken.indices) {
                    mToken[i] = (i % 10).toByte()
                }
            } else {
                val tmp: ByteArray = getBytes(challenge)
                for (i in tmp.indices) {
                    mToken[i + 1] = tmp[i]
                }
            }
            mFingerprintManager!!.enroll(
                mToken, mEnrollmentCancel,
                0, object : FingerprintManager.EnrollmentCallback() {
                    override fun onEnrollmentProgress(remaining: Int) {
                        callback.onEnrollmentProgress(remaining)
                    }

                    override fun onEnrollmentHelp(helpMsgId: Int, helpString: CharSequence) {
                        callback.onEnrollmentHelp(helpMsgId, helpString)
                    }

                    override fun onEnrollmentError(errMsgId: Int, errString: CharSequence) {
                        callback.onEnrollmentError(errMsgId, errString)
                    }
                }, 0
            )
            mEnrolling = true
        })
    )
}

fun cancelEnrollment() {
    if (mEnrolling) {
        mEnrollmentCancel?.cancel()
        mEnrolling = false
    }
}

fun getBytes(data: Long): ByteArray {
    val bytes = ByteArray(28)
    bytes[0] = (data and 0xffL).toByte()
    bytes[1] = ((data shr 8) and 0xffL).toByte()
    bytes[2] = ((data shr 16) and 0xffL).toByte()
    bytes[3] = ((data shr 24) and 0xffL).toByte()
    bytes[4] = ((data shr 32) and 0xffL).toByte()
    bytes[5] = ((data shr 40) and 0xffL).toByte()
    bytes[6] = ((data shr 48) and 0xffL).toByte()
    bytes[7] = ((data shr 56) and 0xffL).toByte()
    if (SystemProperties.get("persist.vendor.fingerprint.chip", "none") == "focaltech")
        bytes[8] = (1 and 0xff).toByte()
    bytes[24] = (0 and 0xff).toByte()
    bytes[25] = (0 and 0xff).toByte()
    bytes[26] = (0 and 0xff).toByte()
    bytes[27] = (2 and 0xff).toByte()
    return bytes
}