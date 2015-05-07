package ru.antiyotazapret.yotatetherttl;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BootUpService extends IntentService {
    private SharedPreferences sp;
    private Handler mHandler;
    private final ShellExecutor exe = new ShellExecutor();
    int ttl;
    public BootUpService() {
        super("BootUpService");
    }

    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if(sp.getBoolean("bootup", false)) {
            ttl = Integer.parseInt(sp.getString("bootup_ttl", null));
            if(sp.getBoolean("bootup_toast", false)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BootUpService.this, R.string.applying, Toast.LENGTH_LONG).show(); //Оповещения
                    }
                });
            }
            String command = "settings put global airplane_mode_on 1"; //Включение авиарежима
            command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"; //И это тоже
            String methoddata=sp.getString("method", "airplane"); //Метод переподключения к сети
            if(methoddata.equals("mobile")) //Если метод переподключения к сети - мобильные данные
            {
                command="svc data disable"; //Отключаем их
            }
            else if(methoddata.equals("off")) // Если переподключение к сети отключено
            {
                command=""; //Опустошаем переменую команд
            }
            command += "\nsettings put global tether_dun_required 0"; //Отключение оповещения андроидом оператора о тетеринге
            exe.execute(command); //Заливаем все это дело и записываем в переменную дебага

            command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl); //Меняем TTL
                if(methoddata.equals("airplane")) { //Если метод переподключения к сети - авиарежим
                command += "\nsettings put global airplane_mode_on 0"; //Выключаем авиарежим
                command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"; //Тут тоже выключаем
            }
            else if(methoddata.equals("mobile")) //Если вкл/выкл мобильных данных
            {
                command+="\nsvc data enable"; //То включаем мобильные данные
            }
            exe.execute(command); //И опять заливаем

            if(sp.getBoolean("bootup_toast", false)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BootUpService.this, R.string.done, Toast.LENGTH_LONG).show(); //Оповещения
                    }
                });
            }
        }
    }
}