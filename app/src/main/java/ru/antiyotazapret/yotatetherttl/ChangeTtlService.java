package ru.antiyotazapret.yotatetherttl;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import net.orange_box.storebox.StoreBox;

public class ChangeTtlService extends IntentService {

    private Preferences preferences;
    private Handler mHandler = new Handler();
    private final ShellExecutor exe = new ShellExecutor();

    public ChangeTtlService() {
        super("BootUpService");
    }

    public void onCreate() {
        super.onCreate();
        preferences = StoreBox.create(this, Preferences.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!preferences.isBootup()) {
            return;
        }

        int ttl = Integer.parseInt(preferences.bootupTtl());

        if (preferences.bootupToast()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChangeTtlService.this, R.string.applying, Toast.LENGTH_LONG).show(); //Оповещения
                }
            });
        }

        String command = "settings put global airplane_mode_on 1"; //Включение авиарежима
        command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"; //И это тоже

        String methoddata = preferences.method(); //Метод переподключения к сети

        if (methoddata.equals("mobile")) {
            //Если метод переподключения к сети - мобильные данные
            command = "svc data disable"; //Отключаем их
        } else if (methoddata.equals("off")) {
            // Если переподключение к сети отключено
            command = ""; //Опустошаем переменую команд
        }

        command += "\nsettings put global tether_dun_required 0"; //Отключение оповещения андроидом оператора о тетеринге
        exe.execute(command); //Заливаем все это дело и записываем в переменную дебага

        command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl); //Меняем TTL

        if (getString(R.string.prefs_method_airplane).equals(methoddata)) {
            //Если метод переподключения к сети - авиарежим
            command += "\nsettings put global airplane_mode_on 0"; //Выключаем авиарежим
            command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"; //Тут тоже выключаем
        } else if (getString(R.string.prefs_method_mobile).equals(methoddata)) {
            //Если вкл/выкл мобильных данных
            //То включаем мобильные данные
            command += "\nsvc data enable";
        }

        exe.execute(command); //И опять заливаем

        if (preferences.bootupToast()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChangeTtlService.this, R.string.done, Toast.LENGTH_LONG).show(); //Оповещения
                }
            });
        }

    }

}