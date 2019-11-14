package com.suntech.iot.rft

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.suntech.iot.rft.base.BaseActivity
import com.suntech.iot.rft.common.AppGlobal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.tv_title
import kotlinx.android.synthetic.main.layout_top_menu.*
import java.util.*

class MainActivity : BaseActivity() {

    private var _selected_factory_idx: String = ""
    private var _selected_zone_idx: String = ""
    private var _selected_line_idx: String = ""


    private var _selected_line_time: String = ""

    private var _doubleBackToExitPressedOnce = false

    private val _broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                    btn_wifi_state.isSelected = true
                } else {
                    btn_wifi_state.isSelected = false
                }
            }
        }
    }

    fun parentSpaceClick(view: View) {
        var v = this.currentFocus
        if (v != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppGlobal.instance.setContext(this)

//        val main_title = AppGlobal.instance.get_main_title().trim()
//        tv_title?.text = if (main_title == "") "Input & Defect Q'ty Transmission" else main_title

        _selected_factory_idx = AppGlobal.instance.get_factory_idx()
        _selected_zone_idx = AppGlobal.instance.get_zone_idx()
        _selected_line_idx = AppGlobal.instance.get_line_idx()

        tv_factory.text = if (AppGlobal.instance.get_factory()=="") "-" else AppGlobal.instance.get_factory()
        tv_zone.text = if (AppGlobal.instance.get_zone()=="") "-" else AppGlobal.instance.get_zone()
//        tv_line.text = if (AppGlobal.instance.get_line()=="") "-" else AppGlobal.instance.get_line()

        val line_name = AppGlobal.instance.get_line()
        if (line_name=="" || line_name=="-") {
            tv_line.text = "-"
            resetLine("")
        } else {
            tv_line.text = line_name
            resetLine(line_name)
        }

        tv_factory.setOnClickListener {
            fetchDataFactory()
        }
        tv_factory_down.setOnClickListener {
            fetchDataFactory()
        }
        tv_zone.setOnClickListener {
            fetchDataZone()
        }
        tv_zone_down.setOnClickListener {
            fetchDataZone()
        }
        tv_line.setOnClickListener {
            fetchDataLine()
        }
        tv_line_down.setOnClickListener {
            fetchDataLine()
        }

        tv_time2.setOnClickListener {
            if (_selected_line_idx == "") {
                Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            } else {
                choiceTime()
            }
        }
        tv_time2_down.setOnClickListener {
            if (_selected_line_idx == "") {
                Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            } else {
                choiceTime()
            }
        }

        btn_input2.setOnClickListener { sendEditCount("I") }
        btn_defect2.setOnClickListener { sendEditCount("D") }

        btn_setting.setOnClickListener { startActivity(Intent(this, SettingActivity::class.java)) }
        btn_setting_text.setOnClickListener { startActivity(Intent(this, SettingActivity::class.java)) }

        start_timer()
    }

    public override fun onResume() {
        super.onResume()
        registerReceiver(_broadcastReceiver, IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION))

        val main_title = AppGlobal.instance.get_main_title().trim()
        tv_title?.text = if (main_title == "") "Input & Defect Q'ty Transmission" else main_title

        if (AppGlobal.instance.isOnline(this)) btn_wifi_state.isSelected = true
        else btn_wifi_state.isSelected = false
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(_broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel_timer()
    }

    override fun onBackPressed() {
        if (_doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this._doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ _doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun fetchDataFactory() {
        val uri = "/getlist1.php"
        var params = listOf("code" to "factory_parent")

        request(this, uri, false, params, { result ->
            var code = result.getString("code")
            if (code == "00"){
                var arr: ArrayList<String> = arrayListOf<String>()
                var list = result.getJSONArray("item")
                var lists : ArrayList<HashMap<String, String>> = arrayListOf()

                for (i in 0..(list.length() - 1)) {
                    val item = list.getJSONObject(i)
                    var map = hashMapOf(
                        "idx" to item.getString("idx"),
                        "name" to item.getString("name")
                    )
                    lists.add(map)
                    arr.add(item.getString("name"))
                }

                val intent = Intent(this, PopupSelectList::class.java)
                intent.putStringArrayListExtra("list", arr)
                startActivity(intent, { r, c, m, d ->
                    if (r) {
                        val idx = lists[c]["idx"] ?: ""
                        if (idx != "" && idx != _selected_factory_idx) {
                            _selected_factory_idx = idx
                            AppGlobal.instance.set_factory_idx(_selected_factory_idx)
                            AppGlobal.instance.set_factory(lists[c]["name"] ?: "-")

                            _selected_zone_idx = ""
                            AppGlobal.instance.set_zone_idx("")
                            AppGlobal.instance.set_zone("")

                            _selected_line_idx = ""
                            AppGlobal.instance.set_line_idx("")
                            AppGlobal.instance.set_line("")

                            tv_factory.text = lists[c]["name"] ?: "-"
                            tv_zone.text = "-"
                            tv_line.text = "-"
                            resetLine("")
                        }
                    }
                })
            } else {
                Toast.makeText(this, result.getString("msg"), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDataZone() {

        if (_selected_factory_idx == "") {
            Toast.makeText(this, R.string.msg_select_factory, Toast.LENGTH_SHORT).show()
            return
        }

        val uri = "/getlist1.php"
        var params = listOf(
            "code" to "factory",
            "factory_parent_idx" to _selected_factory_idx)

        request(this, uri, false, params, { result ->
            var code = result.getString("code")
            if (code == "00") {
                var arr: ArrayList<String> = arrayListOf<String>()
                var list = result.getJSONArray("item")
                var lists : ArrayList<HashMap<String, String>> = arrayListOf()

                for (i in 0..(list.length() - 1)) {
                    val item = list.getJSONObject(i)
                    var map=hashMapOf(
                        "idx" to item.getString("idx"),
                        "name" to item.getString("name")
                    )
                    lists.add(map)
                    arr.add(item.getString("name"))
                }

                val intent = Intent(this, PopupSelectList::class.java)
                intent.putStringArrayListExtra("list", arr)
                startActivity(intent, { r, c, m, d ->
                    if (r) {
                        val idx = lists[c]["idx"] ?: ""
                        if (idx != "" && idx != _selected_zone_idx) {
                            _selected_zone_idx = idx
                            AppGlobal.instance.set_zone_idx(_selected_zone_idx)
                            AppGlobal.instance.set_zone(lists[c]["name"] ?: "-")

                            _selected_line_idx = ""
                            AppGlobal.instance.set_line_idx("")
                            AppGlobal.instance.set_line("")

                            tv_zone.text = lists[c]["name"] ?: "-"
                            tv_line.text = "-"
                            resetLine("")
                        }
                    }
                })
            } else {
                Toast.makeText(this, result.getString("msg"), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDataLine() {

        if (_selected_factory_idx == "") {
            Toast.makeText(this, R.string.msg_select_factory, Toast.LENGTH_SHORT).show()
            return
        }
        if (_selected_zone_idx == "") {
            Toast.makeText(this, R.string.msg_select_zone, Toast.LENGTH_SHORT).show()
            return
        }

        val uri = "/getlist1.php"
        var params = listOf(
            "code" to "line",
            "factory_parent_idx" to _selected_factory_idx,
            "factory_idx" to _selected_zone_idx)

        request(this, uri, false, params, { result ->
            var code = result.getString("code")
            if (code == "00") {
                var arr: ArrayList<String> = arrayListOf<String>()
                var list = result.getJSONArray("item")
                var lists : ArrayList<HashMap<String, String>> = arrayListOf()

                for (i in 0..(list.length() - 1)) {
                    val item = list.getJSONObject(i)
                    var map=hashMapOf(
                        "idx" to item.getString("idx"),
                        "name" to item.getString("name")
                    )
                    lists.add(map)
                    arr.add(item.getString("name"))
                }

                val intent = Intent(this, PopupSelectList::class.java)
                intent.putStringArrayListExtra("list", arr)
                startActivity(intent, { r, c, m, d ->
                    if (r) {
                        val idx = lists[c]["idx"] ?: ""
                        if (idx != "" && idx != _selected_line_idx) {
                            _selected_line_idx = idx
                            AppGlobal.instance.set_line_idx(_selected_line_idx)
                            AppGlobal.instance.set_line(lists[c]["name"] ?: "-")

                            val line_name = lists[c]["name"] ?: "-"
                            tv_line.text = line_name
                            resetLine(line_name)
                        }
                    }
                })
            } else {
                Toast.makeText(this, result.getString("msg"), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetLine(name: String) {
        if (name != "") {
            tv_line_title2.text = name
            tv_line_title2.setTextColor(Color.parseColor("#f8ad13"))
//            ll_time2.setBackgroundResource(R.color.colorSelector)
            et_qty2.isEnabled = true
//            et_qty2.setBackgroundResource(R.color.colorEditor1)
            disable_block.visibility = View.GONE
        } else {
            tv_line_title2.text = "LINE"
            tv_line_title2.setTextColor(Color.parseColor("#888888"))
            _selected_line_time = ""
            tv_time2.text = "-"
//            ll_time2.setBackgroundResource(R.color.colorSelectorDisable)
            et_qty2.isEnabled = false
            et_qty2.setText("")
//            et_qty2.setBackgroundResource(R.color.colorEditor1Disable)
            disable_block.visibility = View.VISIBLE
        }
    }

    private fun choiceTime() {
        if (_selected_line_idx == "") {
            Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            return
        }

        var arr: ArrayList<String> = arrayListOf<String>()
        var lists : ArrayList<HashMap<String, String>> = arrayListOf()

        for (i in 7..11) {
            val text = (if (i<10) "0" else "") + i.toString() + " - " + (if (i<9) "0" else "") + (i+1).toString()
            var map=hashMapOf(
                "time" to i.toString(),
                "text" to text
            )
            lists.add(map)
            arr.add(text)
        }
        for (i in 13..19) {
            val text = (if (i<10) "0" else "") + i.toString() + " - " + (if (i<9) "0" else "") + (i+1).toString()
            var map=hashMapOf(
                "time" to i.toString(),
                "text" to text
            )
            lists.add(map)
            arr.add(text)
        }

        val intent = Intent(this, PopupSelectList::class.java)
        intent.putStringArrayListExtra("list", arr)
        startActivity(intent, { r, c, m, d ->
            if (r) {
                _selected_line_time = lists[c]["time"] ?: ""
                tv_time2.text = lists[c]["text"] ?: "Select a time"
            }
        })
    }

    private fun sendEditCount(gubun: String = "") {
        if (_selected_line_idx == "") {
            Toast.makeText(this, "Line not set", Toast.LENGTH_SHORT).show()
            return
        }

        if (gubun != "I" && gubun != "D") {
            Toast.makeText(this, "Invalid separator code", Toast.LENGTH_SHORT).show()
            return
        }

        val time = _selected_line_time
        val qty = et_qty2.text.toString().trim()

        if (time == "") {
            Toast.makeText(this, "No time selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (qty == "") {
            Toast.makeText(this, "No quantity entered", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, PasswordCheckActivity::class.java)
        startActivity(intent, { r, c, m, d ->
            if (r) {
                val uri = "/Srft.php"
                var params = listOf(
                    "line_idx" to _selected_line_idx,
                    "time" to time,
                    "gubun" to gubun,
                    "qty" to qty)
//        Log.e("Srft params", params.toString())
                request(this, uri, true,true, params, { result ->
                    var code = result.getString("code")
                    var msg = result.getString("msg")

                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

                    if(code == "00") {
                        _selected_line_time = ""
                        tv_time2.text = "-"
                        et_qty2.setText("")
                    }
                })
            }
        })
    }


    /////// 쓰레드
    private val _timer_task1 = Timer()          // 서버 접속 체크 Ping test. Shift의 Target 정보

    private fun start_timer() {

        val task1 = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    sendPing()
                }
            }
        }
        _timer_task1.schedule(task1, 2000, 10000)   // 10초마다
    }

    private fun cancel_timer() {
        _timer_task1.cancel()
    }

    private fun sendPing() {
        tv_ms.text = "-" + " ms"
        if (AppGlobal.instance.get_server_ip() == "") return

        val currentTimeMillisStart = System.currentTimeMillis()
        val uri = "/ping.php"
        request(this, uri, false, false, null, { result ->
            val currentTimeMillisEnd = System.currentTimeMillis()
            val millis = currentTimeMillisEnd - currentTimeMillisStart

            var code = result.getString("code")
            if (code == "00") {
                btn_server_state.isSelected = true
                tv_ms.text = "" + millis + " ms"
            } else {
                Toast.makeText(this, result.getString("msg"), Toast.LENGTH_SHORT).show()
            }
        }, {
            btn_server_state.isSelected = false
        })
    }
}
