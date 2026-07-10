package com.demos.nfc

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.view.View
import com.demos.Logger
import com.demos.activity.BaseActivity
import com.demos.databinding.ActivityNfcBinding
import com.demos.nfc.tool.NfcTagUtils


/**
 * by DAD FZ
 * 2026/4/18
 * desc：
 **/
class NfcActivity : BaseActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null

    private val binding: ActivityNfcBinding by lazy {
        ActivityNfcBinding.inflate(layoutInflater)
    }

    override fun getApplyWindowView(): View {
        return binding.root
    }

    override fun initView() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        try {
            val info = NfcTagUtils.dumpTag(tag)
            log(info)
            runOnUiThread {
                binding.info.text = info
            }
        } catch (e: Exception) {
            log("info err:${e.message}")
            runOnUiThread {
                binding.info.text = "info err:${e.message}"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableMode()
    }

    private fun enableMode() {
        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE
        nfcAdapter?.enableReaderMode(this, this, flags, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    private fun log(l: String) {
        Logger.e("nfc", l)
    }
}