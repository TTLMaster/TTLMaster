package ru.antiyotazapret.yotatetherttl.method.device_ttl;

import android.content.Context;

import de.greenrobot.event.EventBus;
import ru.antiyotazapret.yotatetherttl.Preferences;
import ru.antiyotazapret.yotatetherttl.R;
import ru.antiyotazapret.yotatetherttl.ShellExecutor;

/**
 * @author Pavel Savinov (swapii@gmail.com)
 */
class ChangeRunnable implements Runnable {

    private final Context context;
    private final EventBus eventBus;
    private final Preferences preferences;

    private final ShellExecutor exe = new ShellExecutor();

    public ChangeRunnable(Context context, EventBus eventBus, Preferences preferences) {
        this.context = context;
        this.eventBus = eventBus;
        this.preferences = preferences;
    }

    @Override
    public void run() {

        ChangeDeviceTtlState state = new ChangeDeviceTtlState();

        /*
        TODO Заменить на нотификации
        if (preferences.showToastsOnBoot()) {
            Toast.makeText(context, R.string.applying, Toast.LENGTH_LONG).show();
        }
        */

        String command = "settings put global airplane_mode_on 1"; //Включение авиарежима
        command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"; //И это тоже

        String reconnectType = preferences.reconnectType(); //Метод переподключения к сети

        if (reconnectType.equals("mobile")) {
            //Если метод переподключения к сети - мобильные данные
            command = "svc data disable"; //Отключаем их
        }

        if (reconnectType.equals("off")) {
            // Если переподключение к сети отключено
            command = ""; //Опустошаем переменую команд
        }

        command += "\nsettings put global tether_dun_required 0"; //Отключение оповещения андроидом оператора о тетеринге
        exe.execute(command); //Заливаем все это дело и записываем в переменную дебага

        int ttl = preferences.onBootTtlValue();
        command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl); //Меняем TTL

        if (context.getString(R.string.prefs_general_reconnectType_airplane).equals(reconnectType)) {
            //Если метод переподключения к сети - авиарежим
            command += "\nsettings put global airplane_mode_on 0"; //Выключаем авиарежим
            command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"; //Тут тоже выключаем
        }

        if (context.getString(R.string.prefs_general_reconnectType_mobile).equals(reconnectType)) {
            //Если вкл/выкл мобильных данных
            //То включаем мобильные данные
            command += "\nsvc data enable";
        }

        exe.execute(command); //И опять заливаем

        /*
        TODO Заменить на нотификации
        if (preferences.showToastsOnBoot()) {
            Toast.makeText(context, R.string.done, Toast.LENGTH_LONG).show();
        }
        */

        state.setFinished(true);
        eventBus.post(state);
    }

}
