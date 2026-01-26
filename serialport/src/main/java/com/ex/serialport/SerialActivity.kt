package com.ex.serialport

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ex.serialport.adapter.LogListAdapter
import com.ex.serialport.adapter.SpAdapter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import android_serialport_api.SerialPortFinder
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import tp.xmaihh.serialport.utils.ByteUtil

class SerialActivity : AppCompatActivity() {

    private lateinit var recy: RecyclerView
    private lateinit var spSerial: Spinner
    private lateinit var edInput: EditText
    private lateinit var btSend: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var serialPortFinder: SerialPortFinder
    private lateinit var serialHelper: SerialHelper
    private lateinit var spBote: Spinner
    private lateinit var btOpen: Button
    private lateinit var btnSave: Button
    private lateinit var btnUnsave: Button
    private lateinit var logListAdapter: LogListAdapter
    private lateinit var spDatab: Spinner
    private lateinit var spParity: Spinner
    private lateinit var spStopb: Spinner
    private lateinit var spFlowcon: Spinner
    private lateinit var customBaudrate: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onDestroy() {
        super.onDestroy()
        serialHelper.close()
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recy = findViewById(R.id.recyclerView)
        spSerial = findViewById(R.id.sp_serial)
        edInput = findViewById(R.id.ed_input)
        btSend = findViewById(R.id.btn_send)
        spBote = findViewById(R.id.sp_baudrate)
        btOpen = findViewById(R.id.btn_open)
        radioGroup = findViewById(R.id.radioGroup)
        radioButton1 = findViewById(R.id.radioButton1)
        radioButton2 = findViewById(R.id.radioButton2)
        spDatab = findViewById(R.id.sp_databits)
        spParity = findViewById(R.id.sp_parity)
        spStopb = findViewById(R.id.sp_stopbits)
        spFlowcon = findViewById(R.id.sp_flowcon)
        customBaudrate = findViewById(R.id.tv_custom_baudrate)
        btnSave = findViewById(R.id.btn_save)
        btnUnsave = findViewById(R.id.btn_unsave)

        initView()
        logListAdapter = LogListAdapter(null)
        recy.layoutManager = LinearLayoutManager(this)
        recy.adapter = logListAdapter
        recy.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        serialPortFinder = SerialPortFinder()
        serialHelper = object : SerialHelper("dev/ttyS4", 19200) {
            override fun onDataReceived(comBean: ComBean) {
                runOnUiThread {
                    val rec = if (radioGroup.checkedRadioButtonId == R.id.radioButton1) {
                        " Rx:<==" + String(comBean.bRec, StandardCharsets.UTF_8)
                    } else {
                        " Rx:<==" + ByteUtil.ByteArrToHex(comBean.bRec)
                    }
                    logListAdapter.addData(comBean.sRecTime + rec)
                    if (logListAdapter.data != null && logListAdapter.data!!.size > 0) {
                        recy.smoothScrollToPosition(logListAdapter.data!!.size)
                    }
                    if (radioGroup.checkedRadioButtonId != R.id.radioButton1) {
                        Log.i("Rx", rec)
                    }
                }
            }
        }

        val ports = serialPortFinder.allDevicesPath
        val botes = arrayOf("0", "50", "75", "110", "134", "150", "200", "300", "600", "1200", "1800", "2400", "4800", "9600", "19200", "38400", "57600", "115200", "230400", "460800", "500000", "576000", "921600", "1000000", "1152000", "1500000", "2000000", "2500000", "3000000", "3500000", "4000000", "CUSTOM")
        val databits = arrayOf("8", "7", "6", "5")
        val paritys = arrayOf("NONE", "ODD", "EVEN", "SPACE", "MARK")
        val stopbits = arrayOf("1", "2")
        val flowcons = arrayOf("NONE", "RTS/CTS", "XON/XOFF")

        val spAdapter = SpAdapter(this)
        spAdapter.setDatas(ports)
        spSerial.adapter = spAdapter

        spSerial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                closeTip()
                editor.putInt(port_index, position)
                editor.commit()
                serialHelper.port = ports[position]
                btOpen.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val spAdapter2 = SpAdapter(this)
        spAdapter2.setDatas(botes)
        spBote.adapter = spAdapter2

        spBote.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == botes.size - 1) {
                    showInputDialog()
                    return
                }
                findViewById<View>(R.id.tv_custom_baudrate).visibility = View.GONE
                closeTip()
                editor.putInt(bote_index, position)
                editor.commit()
                serialHelper.baudRate = botes[position]
                btOpen.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val spAdapter3 = SpAdapter(this)
        spAdapter3.setDatas(databits)
        spDatab.adapter = spAdapter3

        spDatab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                closeTip()
                serialHelper.dataBits = databits[position].toInt()
                btOpen.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val spAdapter4 = SpAdapter(this)
        spAdapter4.setDatas(paritys)
        spParity.adapter = spAdapter4

        spParity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                closeTip()
                serialHelper.parity = position
                editor.putInt(paruty_index, position)
                editor.commit()
                btOpen.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val spAdapter5 = SpAdapter(this)
        spAdapter5.setDatas(stopbits)
        spStopb.adapter = spAdapter5

        spStopb.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                closeTip()
                serialHelper.stopBits = stopbits[position].toInt()
                btOpen.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val spAdapter6 = SpAdapter(this)
        spAdapter6.setDatas(flowcons)
        spFlowcon.adapter = spAdapter6

        spFlowcon.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                closeTip()
                serialHelper.flowCon = position
                btOpen.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btOpen.setOnClickListener {
            try {
                serialHelper.open()
                btOpen.isEnabled = false
                if (serialHelper.isOpen) {
                    toasttt("已打开")
                }
            } catch (e: IOException) {
                Toast.makeText(this@SerialActivity, "IO" + e.localizedMessage + getString(R.string.tips_cannot_be_opened, e.message), Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (se: SecurityException) {
                Toast.makeText(this@SerialActivity, se.localizedMessage + "" + getString(R.string.tips_cannot_be_opened, se.message), Toast.LENGTH_SHORT).show()
            }
        }

        btSend.setOnClickListener {
            val sDateFormat = SimpleDateFormat("hh:mm:ss.SSS")
            if (radioGroup.checkedRadioButtonId == R.id.radioButton1) {
                if (edInput.text.toString().isNotEmpty()) {
                    if (serialHelper.isOpen) {
                        serialHelper.sendTxt(edInput.text.toString())
                        val rec = edInput.text.toString()
                        logListAdapter.addData(sDateFormat.format(Date()) + " Tx:==>$rec")
                        Log.i("Tx 1", "Tx:==>$rec")
                        if (logListAdapter.data != null && logListAdapter.data!!.size > 0) {
                            recy.smoothScrollToPosition(logListAdapter.data!!.size)
                        }
                    } else {
                        Toast.makeText(baseContext, R.string.tips_serial_port_not_open, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(baseContext, R.string.tips_please_enter_a_data, Toast.LENGTH_SHORT).show()
                }
            } else {
                if (edInput.text.toString().isNotEmpty()) {
                    if (serialHelper.isOpen) {
                        try {
                            edInput.text.toString().toLong(16)
                        } catch (e: NumberFormatException) {
                            Toast.makeText(baseContext, R.string.tips_formatting_hex_error, Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        serialHelper.sendHex(edInput.text.toString())
                        val rec = edInput.text.toString()
                        Log.i("Tx 2", "Tx:==>$rec")
                        logListAdapter.addData(sDateFormat.format(Date()) + " Tx:==>$rec")
                        if (logListAdapter.data != null && logListAdapter.data!!.size > 0) {
                            recy.smoothScrollToPosition(logListAdapter.data!!.size)
                        }
                    } else {
                        Toast.makeText(baseContext, R.string.tips_serial_port_not_open, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(baseContext, R.string.tips_please_enter_a_data, Toast.LENGTH_SHORT).show()
                }
            }
        }

        spSerial.setSelection(sharedPreferences.getInt(port_index, 0))
        spParity.setSelection(sharedPreferences.getInt(paruty_index, 0))
        spBote.setSelection(sharedPreferences.getInt(bote_index, 0))
    }

    private fun initView() {
        sharedPreferences = getSharedPreferences("juhui_serial", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        toasttt("如果切换了任何设置需要重新开启端口!")

        btnUnsave.setOnClickListener { back() }
        btnSave.setOnClickListener { save() }
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.tips_please_enter_custom_baudrate)

        val inputField = EditText(this)
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        inputField.filters = arrayOf(filter)
        builder.setView(inputField)

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface?, i: Int ->
            val userInput = inputField.text.toString().trim { it <= ' ' }
            try {
                val value = userInput.toInt()
                if (value in 0..4000000) {
                    customBaudrate.visibility = View.VISIBLE
                    customBaudrate.text = getString(R.string.title_custom_buardate, userInput)
                    closeTip()
                    serialHelper.baudRate = userInput
                    btOpen.isEnabled = true
                }
            } catch (e: NumberFormatException) {
            }
        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun closeTip() {
        serialHelper.close()
    }

    private fun save() {
        val intent = Intent()
        if (!serialHelper.isOpen) {
            toasttt("先开启")
            return
        }

        intent.putExtra("port", serialHelper.port)
        intent.putExtra("baudrate", serialHelper.baudRate)
        intent.putExtra("databits", serialHelper.dataBits)
        intent.putExtra("parity", serialHelper.parity)
        intent.putExtra("stopbits", serialHelper.stopBits)
        intent.putExtra("flowcon", serialHelper.flowCon)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun back() {
        onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_clean) {
            logListAdapter.clean()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toasttt(msg: String) {
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val port_index = "port_index"
        const val bote_index = "bote_index"
        const val paruty_index = "paruty_index"
    }
}
