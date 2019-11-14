package com.suntech.iot.rft.common

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.suntech.iot.rft.util.UtilLocalStorage
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

class AppGlobal private constructor() {

    private var _context : Context? = null

    private object Holder { val INSTANCE = AppGlobal() }

    companion object {
        val instance: AppGlobal by lazy { Holder.INSTANCE }
    }
    fun setContext(ctx : Context) { _context = ctx }

    fun set_password(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_password", idx) }
    fun get_password() : String { return UtilLocalStorage.getString(instance._context!!, "current_password") }

    // Default Setting
    fun set_main_title(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_main_title", idx) }
    fun get_main_title() : String { return UtilLocalStorage.getString(instance._context!!, "current_main_title") }

    fun set_server_ip(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_server_ip", idx) }
    fun get_server_ip() : String { return UtilLocalStorage.getString(instance._context!!, "current_server_ip") }
    fun set_server_port(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_server_port", idx) }
    fun get_server_port() : String { return UtilLocalStorage.getString(instance._context!!, "current_server_port") }

    fun set_factory_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_factory_idx", idx) }
    fun get_factory_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_factory_idx") }
    fun set_factory(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_factory", idx) }
    fun get_factory() : String { return UtilLocalStorage.getString(instance._context!!, "current_factory") }
    fun set_zone_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_zone_idx", idx) }
    fun get_zone_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_zone_idx") }
    fun set_zone(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_zone", idx) }
    fun get_zone() : String { return UtilLocalStorage.getString(instance._context!!, "current_zone") }
    fun set_line_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_line_idx", idx) }
    fun get_line_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_line_idx") }
    fun set_line(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_line", idx) }
    fun get_line() : String { return UtilLocalStorage.getString(instance._context!!, "current_line") }

    fun set_factory1_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_factory1_idx", idx) }
    fun get_factory1_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_factory1_idx") }
    fun set_factory1(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_factory1", idx) }
    fun get_factory1() : String { return UtilLocalStorage.getString(instance._context!!, "current_factory1") }
    fun set_zone1_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_zone1_idx", idx) }
    fun get_zone1_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_zone1_idx") }
    fun set_zone1(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_zone1", idx) }
    fun get_zone1() : String { return UtilLocalStorage.getString(instance._context!!, "current_zone1") }
    fun set_line1_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_line1_idx", idx) }
    fun get_line1_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_line1_idx") }
    fun set_line1(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_line1", idx) }
    fun get_line1() : String { return UtilLocalStorage.getString(instance._context!!, "current_line1") }

    fun set_factory2_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_factory2_idx", idx) }
    fun get_factory2_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_factory2_idx") }
    fun set_factory2(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_factory2", idx) }
    fun get_factory2() : String { return UtilLocalStorage.getString(instance._context!!, "current_factory2") }
    fun set_zone2_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_zone2_idx", idx) }
    fun get_zone2_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_zone2_idx") }
    fun set_zone2(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_zone2", idx) }
    fun get_zone2() : String { return UtilLocalStorage.getString(instance._context!!, "current_zone2") }
    fun set_line2_idx(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_line2_idx", idx) }
    fun get_line2_idx() : String { return UtilLocalStorage.getString(instance._context!!, "current_line2_idx") }
    fun set_line2(idx: String) { UtilLocalStorage.setString(instance._context!!, "current_line2", idx) }
    fun get_line2() : String { return UtilLocalStorage.getString(instance._context!!, "current_line2") }

    // 디바이스
    @Throws(java.io.IOException::class)
    fun loadFileAsString(filePath: String): String {
        val data = StringBuffer(1000)
        val reader = BufferedReader(FileReader(filePath))
        val buf = CharArray(1024)
        var numRead = 0
        while (true) {
            numRead = reader.read(buf)
            if (numRead == -1) break
            val readData = String(buf, 0, numRead)
            data.append(readData)
        }
        reader.close()
        return data.toString()
    }
    fun getMACAddress(): String? {
        var mac = ""
        try {
            mac = loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17)
        } catch (e: IOException) {
            //e.printStackTrace()
        }
        if (mac == "") {
            mac = getMACAddress2()
            if (mac == "") mac = "NO_MAC_ADDRESS"
        }
        return mac
    }
    fun getMACAddress2(): String {
        val interfaceName = "wlan0"
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (!intf.getName().equals(interfaceName)) continue

                val mac = intf.getHardwareAddress() ?: return ""
                val buf = StringBuilder()
                for (idx in mac.indices)
                    buf.append(String.format("%02X:", mac[idx]))
                if (buf.length > 0) buf.deleteCharAt(buf.length - 1)
                return buf.toString()
            }
        } catch (ex: Exception) {
            Log.e("Error", ex.toString())
        }
        return ""
    }
    fun get_local_ip(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val interf = en.nextElement()
                val ips = interf.inetAddresses
                while (ips.hasMoreElements()) {
                    val inetAddress = ips.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("Error", ex.toString())
        }
        return ""
    }
    fun isOnline(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}