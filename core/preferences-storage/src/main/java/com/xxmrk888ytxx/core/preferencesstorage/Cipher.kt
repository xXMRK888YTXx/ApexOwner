package com.xxmrk888ytxx.common.preferencesstorage

interface Cipher {

    fun encrypt(byteArray: ByteArray) : ByteArray

    fun decrypt(byteArray: ByteArray) : ByteArray
}