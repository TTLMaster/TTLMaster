package ru.antiyotazapret.yotatetherttl.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.orange_box.storebox.StoreBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.antiyotazapret.yotatetherttl.Preferences;
import ru.antiyotazapret.yotatetherttl.R;
import ru.antiyotazapret.yotatetherttl.ShellExecutor;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.current_ttl_view)
    TextView currentTtlView;

    @Bind(R.id.ttl_field)
    EditText ttlField;

    @Bind(R.id.message_text_view)
    TextView messageTextView;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private Preferences preferences;
    private String debuginfo;

    private final ShellExecutor exe = new ShellExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = StoreBox.create(this, Preferences.class);

        tuneLanguage();

        setContentView(R.layout.main);
        ButterKnife.bind(this);

        String version = getAppVersion();

        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(getString(R.string.main_version, version));
        setSupportActionBar(toolbar);

        //Настраиваем выполнение OnRefreshListener для activity:
        swipeRefreshLayout.setOnRefreshListener(this);

        // Настраиваем цветовую тему значка обновления:
        swipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue,
                R.color.middle_blue,
                R.color.deep_blue);

        if (savedInstanceState == null) {
            //TTL в поле ввода при открытии приложения
            int ttl = preferences.getTtlValueForMainScreen();
            ttlField.setText(String.valueOf(ttl));
        }

        currentTtlView.setText(exe.executenoroot().trim());
    }

    private String getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void tuneLanguage() {
        //Настройка языка
        String lang = preferences.getSelectedLanguage();
        if (lang.equals("default")) {
            //Автоматическое назначение языка
            lang = getResources().getConfiguration().locale.getCountry();
        }

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.app_name);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_4pda:
                Uri uri = Uri.parse(getString(R.string.app_web_address)); //Ссылка на тему 4PDA
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;

            case R.id.action_settings: //Кнопка настроек
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                return true;

        }
        return false;
    }

    /**
     * Событие нажатия кнопки Windows
     */
    @OnClick(R.id.windows_ttl_button)
    void windowsClicked() {
        ttlField.setText("127");
    }

    /**
     * Событие нажатия кнопки UNIX
     */
    @OnClick(R.id.unix_ttl_button)
    void unixClicked() {
        ttlField.setText("63");
    }

    /**
     * Событие нажатия кнопки задания TTL
     */
    @OnClick(R.id.apply_ttl_method_button)
    void ttlClicked() {
        //messageTextView.setText(R.string.main_wait);
        if (TextUtils.isEmpty(ttlField.getText().toString())) { //Если поле TTL пустое
            Toast.makeText(this, R.string.main_ttl_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        int ttl;

        try {
            ttl = Integer.parseInt(ttlField.getText().toString()); //Парсинг поля TTL
        } catch (Exception e) {
            //Исключение: невозможность прочтения поля TTL
            e.printStackTrace();
            Toast.makeText(this, R.string.main_ttl_error_cantReadValue, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ttl <= 1 || ttl >= 255) {
            //Если TTL находится вне диапазона допустимых значений...
            Toast.makeText(this, R.string.main_ttl_error_between, Toast.LENGTH_SHORT).show(); //...сообщаем об этом...
            return; //...и закругляемся.
        }

        String command = "settings put global airplane_mode_on 1"; //Включение авиарежима
        command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"; //И это тоже

        //Метод переподключения к сети
        String methoddata = preferences.reconnectType();

        if (getString(R.string.prefs_general_reconnectType_mobile).equals(methoddata)) {
            //Если метод переподключения к сети - мобильные данные
            command = "svc data disable"; //Отключаем их
        } else if (getString(R.string.prefs_general_reconnectType_off).equals(methoddata)) {
            // Если переподключение к сети отключено
            command = ""; //Опустошаем переменую команд
        }

        command += "\nsettings put global tether_dun_required 0"; //Отключение оповещения андроидом оператора о тетеринге
        debuginfo = "\n\n" + getString(R.string.log) + command + "\n" + exe.execute(command); //Заливаем все это дело и записываем в переменную дебага

        command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl); //Меняем TTL
        if (methoddata.equals("airplane")) {
            //Если метод переподключения к сети - авиарежим
            command += "\nsettings put global airplane_mode_on 0"; //Выключаем авиарежим
            command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"; //Тут тоже выключаем
        } else if (methoddata.equals("mobile")) {
            //Если вкл/выкл мобильных данных
            command += "\nsvc data enable"; //То включаем мобильные данные
        }

        debuginfo += "\n" + command + "\n" + exe.execute(command); //И опять заливаем

        if (preferences.startWifiHotspotOnApplyTtl()) {
            //Если стоит галка на включении тетеринга
            setWifiTetheringEnabled(); //Тогда включаем
            messageTextView.setText(getString(R.string.main_ttl_message_done_auto) + (preferences.isDebugMode() ? debuginfo : "")); //И пишем об этом
        } else {
            //А если нет
            messageTextView.setText(getString(R.string.main_ttl_message_done) + (preferences.isDebugMode() ? debuginfo : "")); //Тогда просто пишем о том, что все хорошо.
        }

        currentTtlView.setText(exe.executenoroot().trim()); //И обновляем поле с текущим TTL
    }

    /**
     * IPTABLES правило
     */
    @OnClick(R.id.try_iptables_method_button)
    void tryIptablesMethodButton() {
        //messageTextView.setText(R.string.main_wait);
        String command = "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64"; //Само правило

        debuginfo = "\n" + command + "\n" + exe.execute(command); // Заливаем команду
        messageTextView.setText(getString(R.string.main_iptables_message_done) + ("\n\n") + (preferences.isDebugMode() ? debuginfo : "")); //Выводим отчет
    }

    /**
     * Функция включения тетеринга WiFi
     */
    private void setWifiTetheringEnabled() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, true);
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }

    /**
     * Открытие настроек тетеринга
     */
    @OnClick(R.id.open_tethering_settings_button)
    void openTetheringSettingsButton() {
        Intent tetherSettings = new Intent();
        tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
        startActivity(tetherSettings);
    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentTtlView.setText(exe.executenoroot().trim()); //Обновляем поле с текущим TTL
                //Останавливаем обновление:
                swipeRefreshLayout.setRefreshing(false)
                ;
            }
        }, 2000);
    }

    /**
     * Функция включения тетеринга USB
     */
    private void setUsbTetheringEnabled() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        String[] available = null;
        Method[] methods = cm.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals("getTetherableIfaces")) {
                try {
                    available = (String[]) method.invoke(cm);
                    break;
                } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        for (Method method : methods) {
            if (method.getName().equals("tether")) {
                try {
                    method.invoke(cm, "rndis0");
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            }
        }

    }

}

