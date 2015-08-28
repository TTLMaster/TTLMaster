package ru.antiyotazapret.yotatetherttl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        switch (action) {

            case Intent.ACTION_BOOT_COMPLETED:
            case "android.intent.action.QUICKBOOT_POWERON":
                Intent boot = new Intent(context, ChangeTtlService.class);
                context.startService(boot);
                break;

            default:
                String message = BootUpReceiver.class.getName() + " can't process action " + action;
                throw new IllegalArgumentException(message);

        }

    }

}