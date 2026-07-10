package com.demos.nfc.tool

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import java.nio.charset.Charset


/**
 * by DAD FZ
 * 2026/4/18
 * desc：
 **/
object NfcTagUtils {

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


    fun dumpTag(tag: Tag?): String {
        if (tag == null) return "Tag is null"

        val sb = StringBuilder()

        // 1. Tag ID
        sb.appendLine("=== Tag ID ===")
        sb.appendLine(toHex(tag.id))
        sb.appendLine()

        // 2. Tech List
        sb.appendLine("=== Tech List ===")
        tag.techList.forEach {
            sb.appendLine(it)
        }
        sb.appendLine()

        // 3. 遍历每种技术
        tag.techList.forEach { tech ->
            when (tech) {
                NfcA::class.java.name -> {
                    val nfcA = NfcA.get(tag)
                    sb.appendLine("=== NfcA ===")
                    sb.appendLine("ATQA: ${toHex(nfcA.atqa)}")
                    sb.appendLine("SAK: ${nfcA.sak}")
                    sb.appendLine()
                }

                NfcB::class.java.name -> {
                    val nfcB = NfcB.get(tag)
                    sb.appendLine("=== NfcB ===")
                    sb.appendLine("ApplicationData: ${toHex(nfcB.applicationData)}")
                    sb.appendLine("ProtocolInfo: ${toHex(nfcB.protocolInfo)}")
                    sb.appendLine()
                }

                NfcF::class.java.name -> {
                    val nfcF = NfcF.get(tag)
                    sb.appendLine("=== NfcF ===")
                    sb.appendLine("SystemCode: ${toHex(nfcF.systemCode)}")
                    sb.appendLine("Manufacturer: ${toHex(nfcF.manufacturer)}")
                    sb.appendLine()
                }

                NfcV::class.java.name -> {
                    val nfcV = NfcV.get(tag)
                    sb.appendLine("=== NfcV ===")
                    sb.appendLine("DSF ID: ${nfcV.dsfId}")
                    sb.appendLine()
                }

                IsoDep::class.java.name -> {
                    val isoDep = IsoDep.get(tag)
                    sb.appendLine("=== IsoDep ===")
                    sb.appendLine("HistoricalBytes: ${toHex(isoDep.historicalBytes)}")
                    sb.appendLine("HiLayerResponse: ${toHex(isoDep.hiLayerResponse)}")
                    sb.appendLine()
                }

                MifareClassic::class.java.name -> {
                    val mifare = MifareClassic.get(tag)
                    sb.appendLine("=== MifareClassic ===")
                    sb.appendLine("Type: ${mifare.type}")
                    sb.appendLine("SectorCount: ${mifare.sectorCount}")
                    sb.appendLine("BlockCount: ${mifare.blockCount}")
                    sb.appendLine("Size: ${mifare.size}")
                    sb.appendLine()
                }

                MifareUltralight::class.java.name -> {
                    val ultra = MifareUltralight.get(tag)
                    sb.appendLine("=== MifareUltralight ===")
                    sb.appendLine("Type: ${ultra.type}")
                    sb.appendLine()
                }

                Ndef::class.java.name -> {
                    val ndef = Ndef.get(tag)
                    sb.appendLine("=== Ndef ===")
                    sb.appendLine("Type: ${ndef.type}")
                    sb.appendLine("MaxSize: ${ndef.maxSize}")
                    sb.appendLine("IsWritable: ${ndef.isWritable}")
                    sb.appendLine("CanMakeReadOnly: ${ndef.canMakeReadOnly()}")

                    val message = ndef.cachedNdefMessage
                    if (message != null) {
                        appendNdefMessage(sb, message)
                    } else {
                        ndef.connect()
                        val msg = ndef.ndefMessage
                        if (msg != null) {
                            appendNdefMessage(sb, msg)
                        }
                        ndef.close()
                    }
                    sb.appendLine()
                }

                NdefFormatable::class.java.name -> {
                    sb.appendLine("=== NdefFormatable ===")
                    sb.appendLine("This tag can be formatted to NDEF")
                    sb.appendLine()
                }
            }
        }

        return sb.toString()
    }


    private fun appendNdefMessage(sb: StringBuilder, message: NdefMessage) {
        sb.appendLine("Records count: ${message.records.size}")
        message.records.forEachIndexed { index, record ->

            when {
                isTextRecord(record) -> sb.appendLine(
                    "Record[$index]-Text:: ${
                        parseTextRecord(
                            record.payload
                        )
                    }"
                )

                isUriRecord(record) -> sb.appendLine(
                    "Record[$index]-URI: ${
                        parseUriRecord(
                            record
                        )
                    }"
                )

                record.tnf == NdefRecord.TNF_MIME_MEDIA -> {
                    val mime = String(record.type)
                    sb.appendLine("Record[$index]-MIME($mime): ${record.payload.size} bytes")
                }

                // AAR（重点）
                record.tnf == NdefRecord.TNF_EXTERNAL_TYPE &&
                        record.type.contentEquals("android.com:pkg".toByteArray()) -> {
                    sb.appendLine("Record[$index]-AAR: ${parseAar(record.payload)}")
                }

                else -> sb.appendLine(
                    "Record[$index]-Unknown record TNF=${record.tnf} type=${
                        String(
                            record.type
                        )
                    }"
                )
            }
        }
    }

    fun parseAar(payload: ByteArray): String {
        return try {
            val packageName = String(payload, Charsets.UTF_8)
            "AAR -> package: $packageName"
        } catch (e: Exception) {
            "AAR parse error,${e.message}"
        }
    }

    private fun isTextRecord(record: NdefRecord) =
        record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT)

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


    private fun toHex(bytes: ByteArray?): String {
        if (bytes == null) return "null"
        return bytes.joinToString(" ") { String.format("%02X", it) }
    }

    private fun parseTextRecord(payload: ByteArray): String {
        return try {
            val textEncoding = if ((payload[0].toInt() and 0x80) == 0) {
                Charset.forName("UTF-8")
            } else {
                Charset.forName("UTF-16")
            }
            val langCodeLen = payload[0].toInt() and 0x3F
            String(payload, 1 + langCodeLen, payload.size - 1 - langCodeLen, textEncoding)
        } catch (e: Exception) {
            "parse error,${e.message}"
        }
    }
}