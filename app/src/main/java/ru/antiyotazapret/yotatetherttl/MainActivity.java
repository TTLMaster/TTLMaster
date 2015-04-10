package ru.antiyotazapret.yotatetherttl;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private EditText ttlField;
    private ShellExecutor exe = new ShellExecutor();

    private TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView versionTextView = (TextView) findViewById(R.id.version_text_view);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionTextView.setText(getString(R.string.main_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        findViewById(R.id.web_page_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getString(R.string.app_web_address));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        messageTextView = (TextView) findViewById(R.id.message_text_view);
        ttlField = (EditText) findViewById(R.id.ttl_field);

    }

    public void onMyClick(View v) {

        int ttl = 63;
        String error = null;

        String command;
        switch (v.getId()) {

            case R.id.windows_ttl_button:
                ttl = 127;
                break;

            case R.id.unix_ttl_button:
                ttl = 63;
                break;

            case R.id.set_button:

                if (TextUtils.isEmpty(ttlField.getText().toString())) {
                    error = "Пожалуйста, введите что-нибудь.";
                    break;
                }

                ttl = Integer.parseInt(ttlField.getText().toString());
                if (ttl <= 1 || ttl >= 255) {
                    error = "Пожалуйста, введите правильный TTL!";
                    break;
                }

                break;

            case R.id.iptables_button:
                command = "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64";
                exe.execute(command);
                error = getString(R.string.main_iptables_message_done);
                break;

        }

        if (!TextUtils.isEmpty(error)) {
            messageTextView.setText(error);
            return;
        }

        command = "settings put global airplane_mode_on 1";
        command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
        command += "\nsettings put global tether_dun_required 0";
        exe.execute(command);

        command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttl);
        command += "\nsettings put global airplane_mode_on 0";
        command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
        exe.execute(command);

        messageTextView.setText(getString(R.string.main_ttl_message_done));

    }

}

