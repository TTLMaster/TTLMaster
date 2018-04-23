package io.github.ttlmaster

import android.net.wifi.WifiManager

import java.io.IOException
import java.util.concurrent.TimeoutException

import io.github.ttlmaster.rootshell.RootShell
import io.github.ttlmaster.rootshell.exceptions.RootDeniedException
import io.github.ttlmaster.rootshell.execution.Command

import android.content.Context

import io.github.ttlmaster.rootshell.RootShell.commandWait
import io.github.ttlmaster.roottools.RootTools.checkUtil

/**
 * Сборник стандартных команд Android.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 */
object Android {

    private val INTERFACE_MASKS = arrayOf("rmnet+", "rev_rmnet+")

    //MUST call the super method when overriding!
    val deviceTtl: Int?
        @Throws(Exception::class)
        get() {
            val arrayOfInts = arrayOfNulls<Int>(1)
            val command = object : Command(0, "cat /proc/sys/net/ipv4/ip_default_ttl") {
                override fun commandOutput(id: Int, line: String) {
                    arrayOfInts[0] = Integer.parseInt(line.trim { it <= ' ' })
                    super.commandOutput(id, line)
                }
            }
            RootShell.getShell(false).add(command)
            commandWait(RootShell.getShell(false), command)
            return arrayOfInts[0]
        }

    //MUST call the super method when overriding!
    val isTtlForced: Boolean
        @Throws(Exception::class)
        get() {
            val ret = BooleanArray(1)
            val command = object : Command(0, "(iptables -t mangle -L | grep -q 'TTL set to 64' && echo ok)") {
                override fun commandOutput(id: Int, line: String) {
                    ret[0] = line.startsWith("ok")
                    super.commandOutput(id, line)
                }
            }
            RootShell.getShell(true).add(command)
            try {
                commandWait(RootShell.getShell(true), command)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ret[0]
        }

    //MUST call the super method when overriding!
    val isWorkaroundApplied: Boolean
        @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
        get() {
            val ret = BooleanArray(1)
            val command = object : Command(0, "(iptables -t mangle -L | grep -q 'TTL set to 64' && echo ok)") {
                override fun commandOutput(id: Int, line: String) {
                    ret[0] = line.startsWith("ok")
                    super.commandOutput(id, line)
                }
            }
            RootShell.getShell(true).add(command)
            try {
                commandWait(RootShell.getShell(true), command)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ret[0]
        }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun enabledAirplaneMode() {
        var command = Command(0, "settings put global airplane_mode_on 1")
        RootShell.getShell(true).add(command)
        command = Command(0, "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true")
        RootShell.getShell(true).add(command)
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun disableAirplaneMode() {
        var command = Command(0, "settings put global airplane_mode_on 0")
        RootShell.getShell(true).add(command)
        command = Command(0, "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false")
        RootShell.getShell(true).add(command)
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun enabledMobileData() {
        val command = Command(0, "svc data enable")
        RootShell.getShell(true).add(command)
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun disableMobileData() {
        val command = Command(0, "svc data disable")
        RootShell.getShell(true).add(command)
    }

    /**
     * Отключение оповещения андроидом оператора о тетеринге.
     */
    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun disableTetheringNotification() {
        val command = Command(0, "settings put global tether_dun_required 0")
        RootShell.getShell(true).add(command)
    }

    /**
     * Изменение TTL устройства.
     *
     * @param ttl новое значение TTL.
     */
    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun changeDeviceTtl(ttl: Int) {
        val command = Command(0, String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl))
        RootShell.getShell(true).add(command)
    }

    /**
     * Проверка возможности использования ttl-set
     */
    @Throws(Exception::class)
    fun canForceTtl(): Boolean {
        val ret = BooleanArray(1)
        val command = object : Command(0, "cat /proc/net/ip_tables_matches | grep -q ttl && echo ok") {
            override fun commandOutput(id: Int, line: String) {
                ret[0] = line.startsWith("ok")
                //MUST call the super method when overriding!
                super.commandOutput(id, line)
            }
        }
        RootShell.getShell(true).add(command)
        try {
            commandWait(RootShell.getShell(true), command)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ret[0]
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun forceSetTtl() {
        val command = Command(0, "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64")
        RootShell.getShell(true).add(command)
    }

    @Throws(IOException::class, InterruptedException::class)
    fun hasRoot(): Boolean {
        return RootShell.isRootAvailable() && RootShell.isAccessGiven()
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun hasIptables(): Boolean {
        return checkUtil("iptables")
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun disableBlockList() {
        val command = Command(0, "iptables -F BLACKLIST; iptables -D INPUT -j BLACKLIST")
        RootShell.getShell(true).add(command)
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun applyBlockList(rules: Set<String>?) {
        if (rules == null) { //rules not recieved
            return
        }

        var command = Command(0, "iptables -N BLACKLIST; iptables -A INPUT -j BLACKLIST")
        RootShell.getShell(true).add(command)
        val sb = StringBuilder()
        for (addr in rules) {
            sb.append(addr).append('\n')
        }
        command = Command(0, "while read s; do iptables -A BLACKLIST -s \$s -j DROP; done", sb.toString())
        RootShell.getShell(true).add(command)
    }

    @Throws(IOException::class, InterruptedException::class, TimeoutException::class, RootDeniedException::class)
    fun applyWorkaround() {

        // packets from the device should be 63 too
        Android.changeDeviceTtl(63)
        var command = Command(0, "iptables -t filter -F sort_out_interface", "iptables -t filter -N sort_out_interface", "iptables -t filter -N sort_out_interface;" +
                "iptables -t filter -A sort_out_interface -m ttl --ttl-lt 63 -j REJECT;" +
                "iptables -t filter -A sort_out_interface -m ttl --ttl-eq 63 -j RETURN" + // Skip all packets with TTL == 63

                "iptables -t filter -A sort_out_interface -j CONNMARK --set-mark 64") // All other are marked as 64 (TTL > 63)
        RootShell.getShell(true).add(command)
        for (iface in INTERFACE_MASKS) {
            for (cmd in arrayOf("iptables -t filter -D OUTPUT -o %s -j sort_out_interface", "iptables -t filter -D FORWARD -o %s -j sort_out_interface ", "iptables -t filter -I OUTPUT -o %s -j sort_out_interface", "iptables -t filter -I FORWARD -o %s -j sort_out_interface ")) {
                command = object : Command(0, String.format(cmd, iface)) {
                    override fun commandOutput(id: Int, line: String) {
                        TtlApplication.logi(line)
                        //MUST call the super method when overriding!
                        super.commandOutput(id, line)
                    }
                }
                RootShell.getShell(true).add(command)
                try {
                    commandWait(RootShell.getShell(true), command)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        command = Command(0, "ip rule add fwmark 64 table 164", "ip route add default dev lo table 164", "ip route flush cache")
        RootShell.getShell(true).add(command)
    }

    /**
     * Функция включения тетеринга WiFi
     */
    fun setWifiTetheringEnabled(ctx: Context) {
        val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = false
        val methods = wifiManager.javaClass.declaredMethods
        for (method in methods) {
            if (method.name == "setWifiApEnabled") {
                try {
                    method.invoke(wifiManager, null, true)
                } catch (e: Exception) {
                    TtlApplication.logi(e.toString())
                }

                break
            }
        }
    }

}
