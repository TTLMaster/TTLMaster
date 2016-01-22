package ru.antiyotazapret.yotatetherttl.method.device_ttl;

import android.app.IntentService;
import android.content.Intent;

import net.orange_box.storebox.StoreBox;

import ru.antiyotazapret.yotatetherttl.Android;
import ru.antiyotazapret.yotatetherttl.Preferences;

public class ChangeDeviceTtlService extends IntentService {


    private final Android android = new Android();
    private final String TTL = "ttl";

    public ChangeDeviceTtlService() {
        super(ChangeDeviceTtlService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Preferences preferences = StoreBox.create(this, Preferences.class);
        ChangeTask.doInForeground(new ChangeTask.ChangeTaskParameters(preferences, this));
    }

}