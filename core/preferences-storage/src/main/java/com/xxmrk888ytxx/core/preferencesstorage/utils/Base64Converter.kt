package com.xxmrk888ytxx.common.preferencesstorage.utils

import android.util.Base64

internal class Base64Converter {

    fun bytesToString(value:ByteArray) : String {
        return Base64.encodeToString(value,0)
    }

    fun stringToBytes(value: String) : ByteArray {
        return Base64.decode(value,0)
    }
}