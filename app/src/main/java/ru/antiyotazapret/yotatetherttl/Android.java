package ru.antiyotazapret.yotatetherttl;

import java.io.IOException;
import java.util.Set;

/**
 * Сборник стандартных команд Android.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class Android {

    private static ShellExecutor executor = new ShellExecutor();

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
        return Integer.parseInt(result.getOutput().trim());
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
        return executor.executeAsRoot("iptables -t mangle -L | grep -q 'TTL set to 64' && echo ok")
                .getOutput().startsWith("ok");
    }

    public static boolean hasRoot() throws IOException, InterruptedException {
        return executor.executeAsRoot("echo ok")
                .getOutput().startsWith("ok");
    }

    public static boolean hasIptables() throws IOException, InterruptedException {
        return executor.executeAsRoot("iptables -L &>/dev/null && echo ok")
                .getOutput().startsWith("ok");
    }

    public static void disableBlockList() throws IOException, InterruptedException {
        executor.executeAsRoot("iptables -F BLACKLIST; iptables -D INPUT -j BLACKLIST");
    }

    public static void applyBlockList(Set<String> rules) throws IOException, InterruptedException {
        executor.executeAsRoot("iptables -N BLACKLIST; iptables -A INPUT -j BLACKLIST");

        StringBuilder sb = new StringBuilder();
        for (String addr : rules) {
            sb.append(addr); sb.append("\n");
        }
        executor.executeAsRootWithInput("while read s; do iptables -A BLACKLIST -s $s -j DROP; done", sb.toString());
    }

}
