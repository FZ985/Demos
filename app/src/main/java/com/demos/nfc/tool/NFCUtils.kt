package com.demos.nfc.tool

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.text.TextUtils
import android.util.Log
import java.nio.charset.Charset


/**
 * by DAD FZ
 * 2025/11/18
 * desc：
 **/

fun ByteArray.toHexStr(): String = joinToString(":") { "%02X".format(it) }

object NFCUtils {

    fun writeTextToTag(
        text: String,
        packageName: String?,
        tag: Tag,
        isParseNdef: Boolean
    ): NFCResult {
        try {
            val langBytes = "en".toByteArray(Charset.forName("US-ASCII"))
            val textBytes = text.toByteArray(Charset.forName("UTF-8"))
            val payload = ByteArray(1 + langBytes.size + textBytes.size)
            payload[0] = langBytes.size.toByte()
            System.arraycopy(langBytes, 0, payload, 1, langBytes.size)
            System.arraycopy(textBytes, 0, payload, 1 + langBytes.size, textBytes.size)

            val record =
                NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
            val arrRecords = if (TextUtils.isEmpty(packageName)) arrayOf(record) else arrayOf(
                record,
                NdefRecord.createApplicationRecord(packageName)
            )
            val message = NdefMessage(arrRecords)
            return writeNdefMessageToTag(message, tag, isParseNdef)
        } catch (e: Exception) {
            e.printStackTrace()
            return NFCResult(false, "异常:" + e.message)
        }
    }

    fun writeUriToTag(
        uri: String,
        packageName: String?,
        tag: Tag,
        isParseNdef: Boolean
    ): NFCResult {
        try {
            val record = NdefRecord.createUri(uri)
            val arrRecords = if (TextUtils.isEmpty(packageName)) arrayOf(record) else arrayOf(
                record,
                NdefRecord.createApplicationRecord(packageName)
            )
            val message = NdefMessage(arrRecords)
            return writeNdefMessageToTag(message, tag, isParseNdef)
        } catch (e: Exception) {
            e.printStackTrace()
            return NFCResult(false, "操作失败:" + e.message)
        }
    }

    fun writeNdefMessageToTag(message: NdefMessage, tag: Tag, isParseNdef: Boolean): NFCResult {
        try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) {
                    // 标签不可写
                    ndef.close()
                    return NFCResult(false, "标签不可写入")
                }
                val size = message.toByteArray().size
                val maxSize = ndef.maxSize
                if (maxSize < size) {
                    // 空间不足
                    ndef.close()
                    return NFCResult(false, "空间不足,最大支持写入长度:$maxSize")
                }

                val nMsg = ndef.ndefMessage
                if (isParseNdef && nMsg != null) {
                    val p = NFCTest.parseNdefMessage(nMsg)
                    log(p)
                }

                ndef.writeNdefMessage(message)
                ndef.close()
                return NFCResult(true)
            } else {
                // 不是 NDEF，但可以尝试格式化
                val format = NdefFormatable.get(tag)
                if (format != null) {
                    format.connect()
                    format.format(message)
                    format.close()
                    return NFCResult(true)
                } else {
                    // 不支持格式化
                    return NFCResult(false, "不支持格式化")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return NFCResult(false, "操作失败啦:" + e.message)
        }
    }


    private fun log(p: ParsedNdef) {
        p.records.forEach {
            Log.e("nfc", it)
        }
    }

}