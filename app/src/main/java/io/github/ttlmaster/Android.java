package io.github.ttlmaster;

import android.net.wifi.WifiManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import io.github.ttlmaster.rootshell.RootShell;
import io.github.ttlmaster.rootshell.exceptions.RootDeniedException;
import io.github.ttlmaster.rootshell.execution.Command;

import android.content.Context;

import static io.github.ttlmaster.rootshell.RootShell.commandWait;
import static io.github.ttlmaster.roottools.RootTools.checkUtil;

/**
 * Сборник стандартных команд Android.
 *
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class Android {

    private static ShellExecutor executor = new ShellExecutor();
    private static String[] INTERFACE_MASKS = new String[] {"rmnet+", "rev_rmnet+"};

    public static void enabledAirplaneMode() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,"settings put global airplane_mode_on 1");
        RootShell.getShell(true).add(command);
        command = new Command(0,"am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
        RootShell.getShell(true).add(command);
    }

    public static void disableAirplaneMode() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,"settings put global airplane_mode_on 0");
        RootShell.getShell(true).add(command);
        command = new Command(0,"am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
        RootShell.getShell(true).add(command);
    }

    public static void enabledMobileData() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,"svc data enable");
        RootShell.getShell(true).add(command);
    }

    public static void disableMobileData() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,"svc data disable");
        RootShell.getShell(true).add(command);
    }

    /**
     * Отключение оповещения андроидом оператора о тетеринге.
     */
    public static void disableTetheringNotification() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,"settings put global tether_dun_required 0");
        RootShell.getShell(true).add(command);
    }

    /**
     * Изменение TTL устройства.
     *
     * @param ttl новое значение TTL.
     */
    public static void changeDeviceTtl(int ttl) throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl));
        RootShell.getShell(true).add(command);
    }

    public static int getDeviceTtl() throws Exception {
        final Integer[] finalDefault_ttl = new Integer[1];
        Command command = new Command(0, "cat /proc/sys/net/ipv4/ip_default_ttl")
        {
            @Override
            public void commandOutput(int id, String line)
            {
                finalDefault_ttl[0] = Integer.parseInt(line.trim());
                //MUST call the super method when overriding!
                super.commandOutput(id, line);
            }
        };
        RootShell.getShell(false).add(command);
        commandWait(RootShell.getShell(false), command);
        return finalDefault_ttl[0];
    }

    /**
     * Проверка возможности использования ttl-set
     */
    public static boolean canForceTtl() throws Exception {
        final boolean[] ret = new boolean[1];
        Command command = new Command(0, "cat /proc/net/ip_tables_matches | grep -q ttl && echo ok")
        {
            @Override
            public void commandOutput(int id, String line)
            {
                ret[0] =line.startsWith("ok");
                //MUST call the super method when overriding!
                super.commandOutput(id, line);
            }
        };
        RootShell.getShell(true).add(command);
        try {
            commandWait(RootShell.getShell(true), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret[0];
    }

    public static void forceSetTtl() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,"iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64");
        RootShell.getShell(true).add(command);
    }

    public static boolean isTtlForced() throws Exception {
        final boolean[] ret = new boolean[1];
        Command command = new Command(0, "(iptables -t mangle -L | grep -q 'TTL set to 64' && echo ok)")
        {
            @Override
            public void commandOutput(int id, String line)
            {
                ret[0] =line.startsWith("ok");
                //MUST call the super method when overriding!
                super.commandOutput(id, line);
            }
        };
        RootShell.getShell(true).add(command);
        try {
            commandWait(RootShell.getShell(true), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret[0];
    }

    public static boolean isWorkaroundApplied() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        final boolean[] ret = new boolean[1];
        Command command = new Command(0, "(iptables -t mangle -L | grep -q 'TTL set to 64' && echo ok)")
        {
            @Override
            public void commandOutput(int id, String line)
            {
                ret[0] =line.startsWith("ok");
                //MUST call the super method when overriding!
                super.commandOutput(id, line);
            }
        };
        RootShell.getShell(true).add(command);
        try {
            commandWait(RootShell.getShell(true), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret[0];
    }

    public static boolean hasRoot() throws IOException, InterruptedException {
        return RootShell.isRootAvailable()&&RootShell.isAccessGiven();
    }

    public static boolean hasIptables() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        return checkUtil("iptables");
    }

    public static void disableBlockList() throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        Command command = new Command(0,"iptables -F BLACKLIST; iptables -D INPUT -j BLACKLIST");
        RootShell.getShell(true).add(command);
    }

    public static void applyBlockList(Set<String> rules) throws IOException, InterruptedException, TimeoutException, RootDeniedException {
        if (rules == null) { //rules not recieved
            return;
        }

        Command command = new Command(0,"iptables -N BLACKLIST; iptables -A INPUT -j BLACKLIST");
        RootShell.getShell(true).add(command);
        StringBuilder sb = new StringBuilder();
        for (String addr : rules) {
            sb.append(addr).append('\n');
        }
        command = new Command(0,"while read s; do iptables -A BLACKLIST -s $s -j DROP; done", sb.toString());
        RootShell.getShell(true).add(command);
    }

    public static void applyWorkaround() throws IOException, InterruptedException, TimeoutException, RootDeniedException {

        // packets from the device should be 63 too
        Android.changeDeviceTtl(63);
        Command command = new Command(0, "iptables -t filter -F sort_out_interface", "iptables -t filter -N sort_out_interface", "iptables -t filter -N sort_out_interface;" +
                "iptables -t filter -A sort_out_interface -m ttl --ttl-lt 63 -j REJECT;" +
                "iptables -t filter -A sort_out_interface -m ttl --ttl-eq 63 -j RETURN" + // Skip all packets with TTL == 63
                "iptables -t filter -A sort_out_interface -j CONNMARK --set-mark 64"); // All other are marked as 64 (TTL > 63)
        RootShell.getShell(true).add(command);
        for (String iface : INTERFACE_MASKS) {
            for (String cmd : new String[]{
                    "iptables -t filter -D OUTPUT -o %s -j sort_out_interface",
                    "iptables -t filter -D FORWARD -o %s -j sort_out_interface ",
                    "iptables -t filter -I OUTPUT -o %s -j sort_out_interface",
                    "iptables -t filter -I FORWARD -o %s -j sort_out_interface ",
            }) {
                command = new Command(0, String.format(cmd, iface)) {
                    @Override
                    public void commandOutput(int id, String line) {
                        TtlApplication.Logi(line);
                        //MUST call the super method when overriding!
                        super.commandOutput(id, line);
                    }
                };
                RootShell.getShell(true).add(command);
                try {
                    commandWait(RootShell.getShell(true), command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        command = new Command(0, "ip rule add fwmark 64 table 164", "ip route add default dev lo table 164", "ip route flush cache");
        RootShell.getShell(true).add(command);
    }
    /**
     * Функция включения тетеринга WiFi
     */
    public static void setWifiTetheringEnabled(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, true);
                } catch (Exception e) {
                    TtlApplication.Logi(e.toString());
                }
                break;
            }
        }
    }

}
