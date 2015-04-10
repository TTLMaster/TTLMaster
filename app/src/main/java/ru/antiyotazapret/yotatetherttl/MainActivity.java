package ru.antiyotazapret.yotatetherttl;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    EditText input;
    String command;
    ShellExecuter exe = new ShellExecuter();

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
                Uri uri = Uri.parse("http://4pda.ru/forum/index.php?showtopic=647126");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    public void onMyClick(View v) {

        int ttlnumber = 63;
        String error = null;

        switch (v.getId()) {

            case R.id.windows_ttl_button:
                ttlnumber = 127;
                break;

            case R.id.unix_ttl_button:
                ttlnumber = 63;
                break;

            case R.id.set_button:
                TextView tv = (TextView) findViewById(R.id.message_text_view);
                input = (EditText) findViewById(R.id.txt);
                if (input.length() > 0) {
                    ttlnumber = Integer.parseInt(input.getText().toString());
                    if (ttlnumber > 1 && ttlnumber < 255) {

                    } else {
                        error = "Пожалуйста, введите правильный TTL!";
                    }
                } else {
                    error = "Пожалуйста, введите что-нибудь.";
                }
                break;

            case R.id.iptables_button:
                command = "iptables -t mangle -A POSTROUTING -j TTL --ttl-set 64";
                exe.execute(command);
                error = "ОК. Перезагрузите устройство и проверьте, работает ли правило.";
                break;

        }

        TextView tv = (TextView) findViewById(R.id.message_text_view);

        if (error == null) {

            command = "settings put global airplane_mode_on 1";
            command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state true";
            command += "\nsettings put global tether_dun_required 0";
            exe.execute(command);

            command = String.format("echo '%d' > /proc/sys/net/ipv4/ip_default_ttl", ttlnumber);
            command += "\nsettings put global airplane_mode_on 0";
            command += "\nam broadcast -a android.intent.action.AIRPLANE_MODE --ez state false";
            exe.execute(command);

            tv.setText("ОК. Теперь Вы можете включить тетеринг!");

        } else {
            tv.setText(error);
        }

    }

}

