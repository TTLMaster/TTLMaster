package ru.antiyotazapret.yotatetherttl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefresh;
    private SharedPreferences sp;
    private String debuginfo;
    private boolean debugm;
    @InjectView(R.id.current_TTL)
    TextView CurrentTTL;
    @InjectView(R.id.ttl_field)
    EditText ttlField;
    @InjectView(R.id.message_text_view)
    TextView messageTextView;

    private final ShellExecutor exe = new ShellExecutor();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.app_name);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_4pda: Uri uri = Uri.parse(getString(R.string.app_web_address)); //Ссылка на тему 4PDA
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this); //Настройки
        String lang = sp.getString("lang", "default"); //Настройка языка
        assert lang != null;
        if (lang.equals("default")) {
            lang =getResources().getConfiguration().locale.getCountry();} //Автоматическое назначение языка
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
        setContentView(R.layout.activity_main);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh); //Pull to refresh
        mSwipeRefresh.setOnRefreshListener(this); //Настраиваем выполнение OnRefreshListener для activity:
        mSwipeRefresh.setColorSchemeResources
                (R.color.light_blue, R.color.middle_blue, R.color.deep_blue);  // Настраиваем цветовую тему значка обновления:
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        ButterKnife.inject(this);
        if (savedInstanceState == null) {
                       ttlField.setText("63"); //TTL в поле ввода при открытии приложения
        }
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            toolbar.setSubtitle(getString(R.string.main_version, version));
            CurrentTTL.setText(exe.executenoroot()); //Отображение версии
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        messageTextView = (TextView) findViewById(R.id.message_text_view); //Поле вывода
        ttlField = (EditText) findViewById(R.id.ttl_field); //Поле ввода TTL
    }
    private void setWifiTetheringEnabled() { //Функция включения тетеринга WiFi
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

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

    private void setUsbTetheringEnabled() { //Функция включения тетеринга USB
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        String[] available = null;
        Method[] methods = cm.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if(method.getName().equals("getTetherableIfaces")){
                try {
                    available= (String[]) method.invoke(cm);
                    break;
                } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        for (Method method: methods) {
            if(method.getName().equals("tether")){
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

    @OnClick(R.id.windows_ttl_button)
    void windowsClicked() {
        ttlField.setText("127");
    } //Событие нажатия кнопки Windows

    @OnClick(R.id.unix_ttl_button)
    void unixClicked() {
        ttlField.setText("63");
    } //Событие нажатия кнопки UNIX

    @OnClick(R.id.set_button)
    void ttlClicked() { //Событие нажатия кнопки задания TTL
        //messageTextView.setText(R.string.main_wait);
        if (TextUtils.isEmpty(ttlField.getText().toString())) { //Если поле TTL пустое
            Toast.makeText(this, R.string.main_ttl_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        int ttl;

        try {
            ttl = Integer.parseInt(ttlField.getText().toString()); //Парсинг поля TTL
        } catch (Exception e) { //Исключение: невозможность прочтения поля TTL
            e.printStackTrace();
            Toast.makeText(this, R.string.main_ttl_error_cantReadValue, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ttl <= 1 || ttl >= 255) { //Если TTL находится вне диапазона допустимых значений...
            Toast.makeText(this, R.string.main_ttl_error_between, Toast.LENGTH_SHORT).show(); //...сообщаем об этом...
            return; //...и закругляемся.
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
        debuginfo=command+"\n"+exe.execute(command); //Заливаем все это дело и записываем в переменную дебага
        debugm = sp.getBoolean("debugm", false); //Включен ли режим Debug

        command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl); //Меняем TTL
        if(methoddata.equals("airplane")) { //Если метод переподключения к сети - авиарежим
            command += "\nsettings put global airplane_mode_on 0"; //Выключаем авиарежим
            command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"; //Тут тоже выключаем
        }
        else if(methoddata.equals("mobile")) //Если вкл/выкл мобильных данных
        {
            command+="\nsvc data enable"; //То включаем мобильные данные
        }
        debuginfo+="\n"+command+"\n"+exe.execute(command); //И опять заливаем
        if(sp.getBoolean("wifi",false)) //Если стоит галка на включении тетеринга
        {
            setWifiTetheringEnabled(); //Тогда включаем

            messageTextView.setText(getString(R.string.main_ttl_message_done_auto) + ("\n\n") + (debugm ? debuginfo : "")); //И пишем об этом
        }
        else //А если нет
            messageTextView.setText(getString(R.string.main_ttl_message_done) + ("\n\n") + (debugm ? debuginfo : "")); //Тогда просто пишем о том, что все хорошо.

        CurrentTTL.setText(exe.executenoroot()); //И обновляем поле с текущим TTL
    }

    @OnClick(R.id.iptables_button) //IPTABLES правило
    void iptablesClicked() {
        //messageTextView.setText(R.string.main_wait);
        String command = "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64"; //Само правило

        debugm = sp.getBoolean("debugm", false); // Включен ли Debug mode?
        debuginfo="\n"+command+"\n"+exe.execute(command); // Заливаем команду
        messageTextView.setText(getString(R.string.main_iptables_message_done) + ("\n\n") + (debugm ? debuginfo : "")); //Выводим отчет
    }

    @OnClick(R.id.settings_button)
    void usbClicked() {
        Intent tetherSettings = new Intent();
        tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
        startActivity(tetherSettings);
    } // Открытие настроек тетеринга
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                CurrentTTL.setText(exe.executenoroot()); //Обновляем поле с текущим TTL
                //Останавливаем обновление:
                mSwipeRefresh.setRefreshing(false)
                ;}}, 2000);
    }
}

