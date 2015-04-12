package ru.antiyotazapret.yotatetherttl;

import android.util.Log;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.ttl_field) EditText ttlField;
    @InjectView(R.id.message_text_view) TextView messageTextView;

    private ShellExecutor exe = new ShellExecutor();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.app_name);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_4pda: Uri uri = Uri.parse("http://4pda.ru/forum/index.php?showtopic=647126");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            ttlField.setText("63");
        }

        TextView versionTextView = (TextView) findViewById(R.id.version_text_view);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionTextView.setText(getString(R.string.main_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }


        messageTextView = (TextView) findViewById(R.id.message_text_view);
        ttlField = (EditText) findViewById(R.id.ttl_field);

    }

    @OnClick(R.id.windows_ttl_button)
    void windowsClicked() {
        ttlField.setText("127");
    }

    @OnClick(R.id.unix_ttl_button)
    void unixClicked() {
        ttlField.setText("63");
    }

    @OnClick(R.id.set_button)
    void ttlClicked() {

        if (TextUtils.isEmpty(ttlField.getText().toString())) {
            Toast.makeText(this, R.string.main_ttl_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        int ttl;

        try {
            ttl = Integer.parseInt(ttlField.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.main_ttl_error_cantReadValue, Toast.LENGTH_SHORT).show();
            return;
        }

        if (ttl <= 1 || ttl >= 255) {
            Toast.makeText(this, R.string.main_ttl_error_between, Toast.LENGTH_SHORT).show();
            return;
        }

        String command = "settings put global airplane_mode_on 1";
        command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
        command += "\nsettings put global tether_dun_required 0";
        exe.execute(command);

        command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl);
        command += "\nsettings put global airplane_mode_on 0";
        command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
        exe.execute(command);

        messageTextView.setText(getString(R.string.main_ttl_message_done));
    }

    @OnClick(R.id.iptables_button)
    void iptablesClicked() {
        String command = "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64";
        exe.execute(command);
        messageTextView.setText(R.string.main_iptables_message_done);
    }

}

