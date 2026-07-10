package com.demos.nfc.tool

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.tech.Ndef
import android.util.Log


/**
 * by DAD FZ
 * 2025/11/19
 * desc：
 **/
object NFCTest {

    private val uriPrefixMap = mapOf(
        0 to "",
        1 to "http://www.",
        2 to "https://www.",
        3 to "http://",
        4 to "https://",
        5 to "tel:",
        6 to "mailto:",
        7 to "ftp://anonymous:anonymous@",
        8 to "ftp://ftp.",
        9 to "ftps://",
        10 to "sftp://",
        11 to "smb://",
        12 to "nfs://",
        13 to "ftp://",
        14 to "dav://",
        15 to "news:",
        16 to "telnet://",
        17 to "imap:",
        18 to "rtsp://",
        19 to "urn:",
        20 to "pop:",
        21 to "sip:",
        22 to "sips:",
        23 to "tftp:",
        24 to "btspp://",
        25 to "btl2cap://",
        26 to "btgoep://",
        27 to "tcpobex://",
        28 to "irdaobex://",
        29 to "file://",
        30 to "urn:epc:id:",
        31 to "urn:epc:tag:",
        32 to "urn:epc:raw:",
        33 to "urn:epc:",
        34 to "urn:nfc:"
    )


    private fun log(m: String) {
        Log.e("nfcTest", m)
    }

    fun parseNdef(ndef: Ndef): ParsedNdef? {
        try {
            ndef.connect()
            val writable = ndef.isWritable
            val maxSize = ndef.maxSize
            val msg = ndef.ndefMessage
            if (msg != null) {
                return parseNdefMessage(msg)
            }
            ndef.close()
            log("NDEF writable=$writable maxSize=$maxSize")
        } catch (e: Exception) {
            log("NDEF check failed: ${e.message}")
        }
        return null
    }

    fun parseNdefMessage(msg: NdefMessage): ParsedNdef {
        val out = mutableListOf<String>()
        for (record in msg.records) {
            when {
                isTextRecord(record) -> out.add("Text: ${parseTextRecord(record)}")
                isUriRecord(record) -> out.add("URI: ${parseUriRecord(record)}")
                record.tnf == NdefRecord.TNF_MIME_MEDIA -> {
                    val mime = String(record.type)
                    out.add("MIME($mime): ${record.payload.size} bytes")
                }

                else -> out.add("Unknown record TNF=${record.tnf} type=${String(record.type)}")
            }
        }
        return ParsedNdef(out)
    }

    private fun isTextRecord(record: NdefRecord) =
        record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT)

    private fun parseTextRecord(record: NdefRecord): String {
        val payload = record.payload
        // payload[0] : status byte, bit7 = encoding (0 = UTF-8, 1 = UTF-16), bits 5..0 = lang length
        val status = payload[0].toInt()
        val isUtf16 = (status and 0x80) != 0
        val langLength = status and 0x3F
        val textEncoding = if (isUtf16) Charsets.UTF_16 else Charsets.UTF_8
        return String(payload, 1 + langLength, payload.size - 1 - langLength, textEncoding)
    }

    private fun isUriRecord(record: NdefRecord) =
        record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_URI)

    private fun parseUriRecord(record: NdefRecord): String {
        val payload = record.payload
        if (payload.isEmpty()) return ""
        val prefixIndex = payload[0].toInt() and 0xFF
        val uriPrefix = uriPrefixMap[prefixIndex] ?: ""
        val uri = uriPrefix + String(payload, 1, payload.size - 1, Charsets.UTF_8)
        return uri
    }


}