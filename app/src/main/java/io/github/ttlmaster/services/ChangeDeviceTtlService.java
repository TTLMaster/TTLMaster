package io.github.ttlmaster.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import net.orange_box.storebox.StoreBox;

import io.github.ttlmaster.Android;
import io.github.ttlmaster.Preferences;
import io.github.ttlmaster.R;
import io.github.ttlmaster.ui.MainActivity;

public class ChangeDeviceTtlService extends IntentService {


    private final Android android = new Android();
    private final String TTL = "ttl";
    private final int NOTIFY_OK = 1;
    private final int NOTIFY_ERRR = 2;


    public ChangeDeviceTtlService() {
        super(ChangeDeviceTtlService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Preferences preferences = StoreBox.create(this, Preferences.class);
        (new ChangeTask()).attach(new Task.OnResult<Void>() {
            @Override
            public void onResult(Void r) {
                if (preferences.showToastsOnBoot()) {
                    fireNotification(NOTIFY_OK, R.string.notification_boot_message);
                }
            }

            @Override
            public void onError(Exception e) {
                if (preferences.showToastsOnBoot()) {
                    fireNotification(NOTIFY_ERRR, R.string.notification_boot_error_message);
                }
            }
        }).runInForeground(new ChangeTask.ChangeTaskParameters(preferences, this));
    }

    private void fireNotification(int id, int contentRes) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ChangeDeviceTtlService.this).
                setSmallIcon(R.drawable.ic_notify).
                setAutoCancel(true).
                setContentTitle(getResources().getString(R.string.app_name)).
                setContentText(getResources().getString(contentRes));

        Intent resultIntent = new Intent(this, MainActivity.class);

        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notifyMgr.notify(id, mBuilder.build());
    }

}