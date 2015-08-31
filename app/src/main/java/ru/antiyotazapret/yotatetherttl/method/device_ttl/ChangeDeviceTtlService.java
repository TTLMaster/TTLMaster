package ru.antiyotazapret.yotatetherttl.method.device_ttl;

import android.app.IntentService;
import android.content.Intent;

import net.orange_box.storebox.StoreBox;

import de.greenrobot.event.EventBus;
import ru.antiyotazapret.yotatetherttl.Preferences;

public class ChangeDeviceTtlService extends IntentService {

    private Thread thread;

    public ChangeDeviceTtlService() {
        super(ChangeDeviceTtlService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (thread != null && thread.isAlive()) {
            // Нам не надо запускать новый поток пока выполняется старый
            return;
        }

        final EventBus eventBus = EventBus.getDefault();

        eventBus.register(new Object() {
            public void onEventMainThread(ChangeDeviceTtlState state) {
                if (state.isFinished()) {
                    eventBus.unregister(this);
                    stopSelf();
                }
            }
        });

        Preferences preferences = StoreBox.create(this, Preferences.class);

        thread = new Thread(new ChangeRunnable(this, eventBus, preferences));
        thread.start();
    }

}