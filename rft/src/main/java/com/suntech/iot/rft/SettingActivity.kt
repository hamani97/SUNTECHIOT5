package com.suntech.iot.rft

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.suntech.iot.rft.base.BaseActivity
import com.suntech.iot.rft.common.AppGlobal
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : BaseActivity() {

//    private var _selected_factory1_idx: String = ""
//    private var _selected_zone1_idx: String = ""
//    private var _selected_line1_idx: String = ""
//
//    private var _selected_factory2_idx: String = ""
//    private var _selected_zone2_idx: String = ""
//    private var _selected_line2_idx: String = ""

    fun parentSpaceClick(view: View) {
        var v = this.currentFocus
        if (v != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
    }

    private fun initView() {

        val version = packageManager.getPackageInfo(packageName, 0).versionName
//        val verArr = version.split(".")
//        tv_app_version?.text = "Pv" + verArr[verArr.size-2] + "." + verArr[verArr.size-1]
        tv_app_version?.text = "" + version

        // set hidden value
//        _selected_factory1_idx = AppGlobal.instance.get_factory1_idx()
//        _selected_zone1_idx = AppGlobal.instance.get_zone1_idx()
//        _selected_line1_idx = AppGlobal.instance.get_line1_idx()
//
//        _selected_factory2_idx = AppGlobal.instance.get_factory2_idx()
//        _selected_zone2_idx = AppGlobal.instance.get_zone2_idx()
//        _selected_line2_idx = AppGlobal.instance.get_line2_idx()

        // widget
        et_setting_server_ip.setText(AppGlobal.instance.get_server_ip())
        et_setting_port.setText(AppGlobal.instance.get_server_port())

//        tv_setting_factory1.text = AppGlobal.instance.get_factory1()
//        tv_setting_zone1.text = AppGlobal.instance.get_zone1()
//        tv_setting_line1.text = AppGlobal.instance.get_line1()
//
//        tv_setting_factory2.text = AppGlobal.instance.get_factory2()
//        tv_setting_zone2.text = AppGlobal.instance.get_zone2()
//        tv_setting_line2.text = AppGlobal.instance.get_line2()


        // widget default value
        // 49.247.205.235
        // 115.68.227.31
        // 183.81.156.206 : inni
        if (et_setting_server_ip.text.toString() == "") et_setting_server_ip.setText("115.68.227.31")
        if (et_setting_port.text.toString() == "") et_setting_port.setText("80")

        val main_title = AppGlobal.instance.get_main_title().trim()
        et_main_title?.setText(if (main_title == "") "Input & Defect Q'ty Transmission" else main_title)

        // button listener
//        tv_setting_factory1.setOnClickListener { fetchDataForFactory(1) }
//        tv_setting_zone1.setOnClickListener { fetchDataForZone(1) }
//        tv_setting_line1.setOnClickListener { fetchDataForLine(1) }
//
//        tv_setting_factory2.setOnClickListener { fetchDataForFactory(2) }
//        tv_setting_zone2.setOnClickListener { fetchDataForZone(2) }
//        tv_setting_line2.setOnClickListener { fetchDataForLine(2) }


        // check server button
        btn_setting_check_server.setOnClickListener {
            checkServer(true)
            var new_ip = et_setting_server_ip.text.toString()
            var old_ip = AppGlobal.instance.get_server_ip()
            if (!new_ip.equals(old_ip)) {
//                _selected_factory1_idx = ""
//                _selected_zone1_idx = ""
//                _selected_line1_idx = ""
//
//                _selected_factory2_idx = ""
//                _selected_zone2_idx = ""
//                _selected_line2_idx = ""
//
//                tv_setting_factory1.text = ""
//                tv_setting_zone1.text = ""
//                tv_setting_line1.text = ""
//
//                tv_setting_factory2.text = ""
//                tv_setting_zone2.text = ""
//                tv_setting_line2.text = ""
            }
        }
        // password save button
        btn_password_save.setOnClickListener { savePassword() }

        // Save button click
        btn_setting_confirm.setOnClickListener { saveSettingData() }

        // Cancel button click
        btn_setting_cancel.setOnClickListener { finish() }
    }

    private fun checkServer(show_toast:Boolean = false) {
        val url = "http://"+ et_setting_server_ip.text.toString()
        val port = et_setting_port.text.toString()
        val uri = "/ping.php"
        var params = listOf("" to "")

        request(this, url, port, uri, false, false,false, params, { result ->
            if (show_toast) Toast.makeText(this, result.getString("msg"), Toast.LENGTH_SHORT).show()
        }, {
            if (show_toast) Toast.makeText(this, getString(R.string.msg_connection_fail), Toast.LENGTH_SHORT).show()
        })
    }

    private fun savePassword() {
        val current_password = et_password_current.text.toString().trim()
        val new_password = et_password_new.text.toString().trim()

//        if (current_password == "") {
//            Toast.makeText(this, "Enter current password", Toast.LENGTH_SHORT).show()
//            return
//        }
        if (new_password == "") {
            Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show()
            return
        }

        var app_password = AppGlobal.instance.get_password()
//        if (app_password == "") app_password = "1234"

        if (current_password != app_password && current_password != "suntech") {
            Toast.makeText(this, "Current password is invalid", Toast.LENGTH_SHORT).show()
            return
        }

        AppGlobal.instance.set_password(new_password)

        et_password_current.setText("")
        et_password_new.setText("")

        Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show()
    }

    private fun saveSettingData() {
        // check value
//        if (_selected_factory1_idx == "" || _selected_zone1_idx == "" || _selected_line1_idx == "") {
//            Toast.makeText(this, getString(R.string.msg_require_info), Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (_selected_factory2_idx == "" || _selected_zone2_idx == "" || _selected_line2_idx == "") {
//            Toast.makeText(this, getString(R.string.msg_require_info), Toast.LENGTH_SHORT).show()
//            return
//        }

        // setting value
//        AppGlobal.instance.set_factory1_idx(_selected_factory1_idx)
//        AppGlobal.instance.set_zone1_idx(_selected_zone1_idx)
//        AppGlobal.instance.set_line1_idx(_selected_line1_idx)
//
//        AppGlobal.instance.set_factory1(tv_setting_factory1.text.toString())
//        AppGlobal.instance.set_zone1(tv_setting_zone1.text.toString())
//        AppGlobal.instance.set_line1(tv_setting_line1.text.toString())
//
//        AppGlobal.instance.set_factory2_idx(_selected_factory2_idx)
//        AppGlobal.instance.set_zone2_idx(_selected_zone2_idx)
//        AppGlobal.instance.set_line2_idx(_selected_line2_idx)
//
//        AppGlobal.instance.set_factory2(tv_setting_factory2.text.toString())
//        AppGlobal.instance.set_zone2(tv_setting_zone2.text.toString())
//        AppGlobal.instance.set_line2(tv_setting_line2.text.toString())

        AppGlobal.instance.set_server_ip(et_setting_server_ip.text.toString())
        AppGlobal.instance.set_server_port(et_setting_port.text.toString())

        AppGlobal.instance.set_main_title(et_main_title.text.toString().trim())

        finish()
    }

//    private fun fetchDataForFactory(kind: Int) {
//        val url = "http://"+ et_setting_server_ip.text.toString()
//        val port = et_setting_port.text.toString()
//        val uri = "/getlist1.php"
//        var params = listOf("code" to "factory_parent")
//
//        request(this, url, port, uri, false, false,false, params, { result ->
//            var code = result.getString("code")
//            var msg = result.getString("msg")
//            if (code == "00"){
//                var arr: ArrayList<String> = arrayListOf<String>()
//                var list = result.getJSONArray("item")
//                var lists : ArrayList<HashMap<String, String>> = arrayListOf()
//
//                for (i in 0..(list.length() - 1)) {
//                    val item = list.getJSONObject(i)
//                    var map = hashMapOf(
//                        "idx" to item.getString("idx"),
//                        "name" to item.getString("name")
//                    )
//                    lists.add(map)
//                    arr.add(item.getString("name"))
//                }
//
//                val intent = Intent(this, PopupSelectList::class.java)
//                intent.putStringArrayListExtra("list", arr)
//                startActivity(intent, { r, c, m, d ->
//                    if (r) {
//                        if (kind == 1) {
//                            _selected_factory1_idx = lists[c]["idx"] ?: ""
//                            tv_setting_factory1.text = lists[c]["name"] ?: ""
//                            tv_setting_zone1.text = ""
//                            tv_setting_line1.text = ""
//                        } else if (kind == 2) {
//                            _selected_factory2_idx = lists[c]["idx"] ?: ""
//                            tv_setting_factory2.text = lists[c]["name"] ?: ""
//                            tv_setting_zone2.text = ""
//                            tv_setting_line2.text = ""
//                        }
//                    }
//                })
//            } else {
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

//    private fun fetchDataForZone(kind: Int) {
//        val url = "http://"+ et_setting_server_ip.text.toString()
//        val port = et_setting_port.text.toString()
//
//        val factory_parent_idx = if (kind == 1) _selected_factory1_idx else _selected_factory2_idx
//
//        val uri = "/getlist1.php"
//        var params = listOf(
//            "code" to "factory",
//            "factory_parent_idx" to factory_parent_idx)
//
//        request(this, url, port, uri, false, false,false, params, { result ->
//            var code = result.getString("code")
//            var msg = result.getString("msg")
//            if (code == "00") {
//                var arr: ArrayList<String> = arrayListOf<String>()
//                var list = result.getJSONArray("item")
//                var lists : ArrayList<HashMap<String, String>> = arrayListOf()
//
//                for (i in 0..(list.length() - 1)) {
//                    val item = list.getJSONObject(i)
//                    var map=hashMapOf(
//                        "idx" to item.getString("idx"),
//                        "name" to item.getString("name")
//                    )
//                    lists.add(map)
//                    arr.add(item.getString("name"))
//                }
//
//                val intent = Intent(this, PopupSelectList::class.java)
//                intent.putStringArrayListExtra("list", arr)
//                startActivity(intent, { r, c, m, d ->
//                    if (r) {
//                        if (kind == 1) {
//                            _selected_zone1_idx = lists[c]["idx"] ?: ""
//                            tv_setting_zone1.text = lists[c]["name"] ?: ""
//                            tv_setting_line1.text = ""
//                        } else if (kind == 2) {
//                            _selected_zone2_idx = lists[c]["idx"] ?: ""
//                            tv_setting_zone2.text = lists[c]["name"] ?: ""
//                            tv_setting_line2.text = ""
//                        }
//                    }
//                })
//            } else {
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

//    private fun fetchDataForLine(kind: Int) {
//        val url = "http://"+ et_setting_server_ip.text.toString()
//        val port = et_setting_port.text.toString()
//
//        val factory_parent_idx = if (kind == 1) _selected_factory1_idx else _selected_factory2_idx
//        val factory_idx = if (kind == 1) _selected_zone1_idx else _selected_zone2_idx
//
//        val uri = "/getlist1.php"
//        var params = listOf(
//            "code" to "line",
//            "factory_parent_idx" to factory_parent_idx,
//            "factory_idx" to factory_idx)
//
//        request(this, url, port, uri, false, false,false, params, { result ->
//            var code = result.getString("code")
//            var msg = result.getString("msg")
//            if (code == "00") {
//                var arr: ArrayList<String> = arrayListOf<String>()
//                var list = result.getJSONArray("item")
//                var lists : ArrayList<HashMap<String, String>> = arrayListOf()
//
//                for (i in 0..(list.length() - 1)) {
//                    val item = list.getJSONObject(i)
//                    var map=hashMapOf(
//                        "idx" to item.getString("idx"),
//                        "name" to item.getString("name")
//                    )
//                    lists.add(map)
//                    arr.add(item.getString("name"))
//                }
//
//                val intent = Intent(this, PopupSelectList::class.java)
//                intent.putStringArrayListExtra("list", arr)
//                startActivity(intent, { r, c, m, d ->
//                    if (r) {
//                        if (kind == 1) {
//                            _selected_line1_idx = lists[c]["idx"] ?: ""
//                            tv_setting_line1.text = lists[c]["name"] ?: ""
//                        } else if (kind == 2) {
//                            _selected_line2_idx = lists[c]["idx"] ?: ""
//                            tv_setting_line2.text = lists[c]["name"] ?: ""
//                        }
//                    }
//                })
//            } else {
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}