package ru.antiyotazapret.yotatetherttl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.orange_box.storebox.StoreBox;

import ru.antiyotazapret.yotatetherttl.method.device_ttl.ChangeDeviceTtlService;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Preferences preferences = StoreBox.create(context, Preferences.class);

        if (!preferences.autoStartOnBoot()) {
            return;
        }

        String action = intent.getAction();

        switch (action) {

            case Intent.ACTION_BOOT_COMPLETED:
            case "android.intent.action.QUICKBOOT_POWERON":
                Intent boot = new Intent(context, ChangeDeviceTtlService.class);
                context.startService(boot);
                break;

            default:
                String message = BootUpReceiver.class.getName() + " can't process action " + action;
                throw new IllegalArgumentException(message);

        }

    }

}