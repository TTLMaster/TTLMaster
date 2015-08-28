package ru.antiyotazapret.yotatetherttl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent boot = new Intent(context, BootUpService.class);
            context.startService(boot);
        }
    }

}