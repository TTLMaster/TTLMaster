package ru.antiyotazapret.yotatetherttl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BootUpReceiver extends BroadcastReceiver {

    private final ShellExecutor exe = new ShellExecutor();

    @Override
    public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                Toast.makeText(context, "Booted", Toast.LENGTH_SHORT).show();
                //String command = "settings put global airplane_mode_on 1"; //Включение авиарежима
                //exe.execute(command);
                //command = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"; //И это тоже
                //exe.execute(command);
                //command = "settings put global tether_dun_required 0"; //Отключение оповещения андроидом оператора о тетеринге
                //exe.execute(command);
                //command = "settings put global airplane_mode_on 0"; //Выключаем авиарежим
                //exe.execute(command);
                //command = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"; //Тут тоже выключаем
                //exe.execute(command); //И опять заливаем
            }
    }
}