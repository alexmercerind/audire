package com.alexmercerind.audire.native

class ShazamSignature {
    init {
        System.loadLibrary("shazam_signature_jni")
    }

    external fun create(input: ShortArray): String
}
