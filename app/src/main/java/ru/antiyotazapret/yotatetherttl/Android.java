package ru.antiyotazapret.yotatetherttl;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * Сборник стандартных команд Android.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class Android {

    private static ShellExecutor executor = new ShellExecutor();
    private static String[] INTERFACE_MASKS = new String[] {"rmnet+", "rev_rmnet+"};

    public static void enabledAirplaneMode() throws IOException, InterruptedException {
        executor.executeAsRoot("settings put global airplane_mode_on 1");
        executor.executeAsRoot("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
    }

    public static void disableAirplaneMode() throws IOException, InterruptedException {
        executor.executeAsRoot("settings put global airplane_mode_on 0");
        executor.executeAsRoot("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
    }

    public static void enabledMobileData() throws IOException, InterruptedException {
        executor.executeAsRoot("svc data enable");
    }

    public static void disableMobileData() throws IOException, InterruptedException {
        executor.executeAsRoot("svc data disable");
    }

    /**
     * Отключение оповещения андроидом оператора о тетеринге.
     */
    public static void disableTetheringNotification() throws IOException, InterruptedException {
        executor.executeAsRoot("settings put global tether_dun_required 0");
    }

    /**
     * Изменение TTL устройства.
     *
     * @param ttl новое значение TTL.
     */
    public static void changeDeviceTtl(int ttl) throws IOException, InterruptedException {
        executor.executeAsRoot(String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl));
    }

    public static int getDeviceTtl() throws IOException, InterruptedException {
        ShellExecutor.Result result = executor.execute("cat /proc/sys/net/ipv4/ip_default_ttl");
        final int default_ttl = Integer.parseInt(result.getOutput().trim());
        final boolean forced = isTtlForced();
        final boolean workaround = isWorkaroundApplied();

        return workaround ? 63 : (forced ? 64 : default_ttl);
    }

    /**
     * Проверка возможности использования ttl-set
     */
    public static boolean canForceTtl() throws IOException, InterruptedException {
        return executor.executeAsRoot("cat /proc/net/ip_tables_matches | grep -q ttl && echo ok")
                .getOutput().startsWith("ok");
    }

    public static void forceSetTtl() throws  IOException, InterruptedException {
        executor.executeAsRoot("iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64");
    }

    public static boolean isTtlForced() throws IOException, InterruptedException {
        return executor.executeAsRoot("(iptables -t mangle -L | grep -q 'TTL set to 64' && echo ok)")
                .getOutput().startsWith("ok");
    }

    public static boolean isWorkaroundApplied() throws IOException, InterruptedException {
        return executor.executeAsRoot("(iptables -t filter -S sort_out_interface >/dev/null && echo ok)")
                .getOutput().startsWith("ok");
    }

    public static boolean hasRoot() throws IOException, InterruptedException {
        return executor.executeAsRoot("echo ok")
                .getOutput().startsWith("ok");
    }

    public static boolean hasIptables() throws IOException, InterruptedException {
        return executor.executeAsRoot("iptables -S &>/dev/null && echo ok")
                .getOutput().startsWith("ok");
    }

    public static void disableBlockList() throws IOException, InterruptedException {
        executor.executeAsRoot("iptables -F BLACKLIST; iptables -D INPUT -j BLACKLIST");
    }

    public static void applyBlockList(Set<String> rules) throws IOException, InterruptedException {
        executor.executeAsRoot("iptables -N BLACKLIST; iptables -A INPUT -j BLACKLIST");

        StringBuilder sb = new StringBuilder();
        for (String addr : rules) {
            sb.append(addr).append('\n');
        }
        executor.executeAsRootWithInput("while read s; do iptables -A BLACKLIST -s $s -j DROP; done", sb.toString());
    }

    public static void applyWorkaround() throws IOException, InterruptedException {

        // packets from the device should be 63 too
        Android.changeDeviceTtl(63);

        executor.executeAsRoot("iptables -t filter -F sort_out_interface");
        executor.executeAsRoot("iptables -t filter -N sort_out_interface");

        executor.executeAsRoot(
                "iptables -t filter -N sort_out_interface;" +
                "iptables -t filter -A sort_out_interface -m ttl --ttl-lt 63 -j REJECT;" +
                "iptables -t filter -A sort_out_interface -m ttl --ttl-eq 63 -j RETURN" + // Skip all packets with TTL == 63
                "iptables -t filter -A sort_out_interface -j CONNMARK --set-mark 64"); // All other are marked as 64 (TTL > 63)

        for (String iface : INTERFACE_MASKS) {
            for (String cmd : new String[]{
                    "iptables -t filter -D OUTPUT -o %s -j sort_out_interface",
                    "iptables -t filter -D FORWARD -o %s -j sort_out_interface ",
                    "iptables -t filter -I OUTPUT -o %s -j sort_out_interface",
                    "iptables -t filter -I FORWARD -o %s -j sort_out_interface ",
            }) {
                ShellExecutor.Result r = executor.executeAsRoot(String.format(cmd, iface));
                TtlApplication.Logi(r.getOutput());
            }
        }

        executor.executeAsRoot("ip rule add fwmark 64 table 164");
        executor.executeAsRoot("ip route add default dev lo table 164");
        executor.executeAsRoot("ip route flush cache");
    }

}
