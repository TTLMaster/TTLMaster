package ru.antiyotazapret.yotatetherttl.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.orange_box.storebox.StoreBox;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.antiyotazapret.yotatetherttl.Android;
import ru.antiyotazapret.yotatetherttl.Preferences;
import ru.antiyotazapret.yotatetherttl.R;
import ru.antiyotazapret.yotatetherttl.method.device_ttl.ChangeTask;

public class MainActivity extends AppCompatActivity implements ChangeTask.ChangeTaskParameters.OnResult {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.current_ttl_view)
    TextView currentTtlView;

    @Bind(R.id.message_text_view)
    TextView messageTextView;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.current_ttl_scope)
    TextView ttlScopeTextView;

    private Preferences preferences;

    private Thread thread;

    private final Android android = new Android();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = StoreBox.create(this, Preferences.class);

        setContentView(R.layout.main);
        ButterKnife.bind(this);

        String version = getAppVersion();

        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(getString(R.string.main_version, version));
        setSupportActionBar(toolbar);

        //Настраиваем выполнение OnRefreshListener для activity:
        swipeRefreshLayout.setOnRefreshListener(new RefreshListener());

        // Настраиваем цветовую тему значка обновления:
        swipeRefreshLayout.setColorSchemeResources(
                R.color.light_blue,
                R.color.middle_blue,
                R.color.deep_blue);

        try {
            updateTtl();
        if (!android.hasRoot()) {
            new AlertDialog.Builder(this)
            .setTitle(R.string.root_no_root_rights)
            .setMessage(R.string.root_no_root_rights_message)
            .setCancelable(false)
            .setPositiveButton(R.string.root_exit,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    })
            .create().show();
        }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
     * Событие нажатия кнопки задания TTL
     */
    @OnClick(R.id.apply_ttl_method_button)
    void ttlClicked() {

        new ChangeTask().execute(new ChangeTask.ChangeTaskParameters(preferences, this));

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

    private void updateTtl() throws IOException, InterruptedException {
        class TtlStatus {
            int ttl;

            public TtlStatus(int ttl, boolean forced) {
                this.ttl = ttl;
                this.forced = forced;
            }

            boolean forced;
        }

        new AsyncTask<Void, Void, TtlStatus>() {
            @Override
            protected TtlStatus doInBackground(Void... params) {
                try {
                    return new TtlStatus(android.getDeviceTtl(), android.isTtlForced());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(TtlStatus ttlStatus) {
                if (ttlStatus == null) {
                    currentTtlView.setText("?");
                    return;
                }
                if (!ttlStatus.forced) {
                    ttlScopeTextView.setText(getResources().getText(R.string.main_ttl_this_device));
                } else {
                    ttlScopeTextView.setText(getResources().getText(R.string.main_ttl_iptables));
                    ttlStatus.ttl = 64;
                }
                currentTtlView.setText(String.valueOf(ttlStatus.ttl));
            }
        }.execute();

    }

    @Override
    public void OnResult() {
        try {
            updateTtl();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            try {
                updateTtl();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}

