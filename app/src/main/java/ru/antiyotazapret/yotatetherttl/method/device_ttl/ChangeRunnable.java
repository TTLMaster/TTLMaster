package ru.antiyotazapret.yotatetherttl.method.device_ttl;

import android.content.Context;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import ru.antiyotazapret.yotatetherttl.Android;
import ru.antiyotazapret.yotatetherttl.Preferences;
import ru.antiyotazapret.yotatetherttl.R;

/**
 * @author Pavel Savinov (swapii@gmail.com)
 */
class ChangeRunnable implements Runnable {

    private final Context context;
    private final EventBus eventBus;
    private final Preferences preferences;

    private final Android android = new Android();

    public ChangeRunnable(Context context, EventBus eventBus, Preferences preferences) {
        this.context = context;
        this.eventBus = eventBus;
        this.preferences = preferences;
    }

    @Override
    public void run() {

        ChangeDeviceTtlState state = new ChangeDeviceTtlState();

        /*
        TODO Заменить на нотификации
        if (preferences.showToastsOnBoot()) {
            Toast.makeText(context, R.string.applying, Toast.LENGTH_LONG).show();
        }
        */

        String airplaneReconnectType = context.getString(R.string.prefs_general_reconnectType_airplane);
        String mobileReconnectType = context.getString(R.string.prefs_general_reconnectType_mobile);

        try {


            String reconnectType = preferences.reconnectType();

            if (airplaneReconnectType.equals(reconnectType)) {
                android.enabledAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                android.disableMobileData();
            }

            android.disableTetheringNotification();

            int ttl = preferences.onBootTtlValue();

            if (!preferences.ignoreIptables() && android.canForceTtl()) {
                android.forceSetTtl();
            }

            if (!android.isTtlForced()){
                android.changeDeviceTtl(ttl);
            }

            if (airplaneReconnectType.equals(reconnectType)) {
                android.disableAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                android.enabledMobileData();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            state.setException(e);
        }

        /*
        TODO Заменить на нотификации
        if (preferences.showToastsOnBoot()) {
            Toast.makeText(context, R.string.done, Toast.LENGTH_LONG).show();
        }
        */

        state.setFinished(true);
        eventBus.post(state);
    }

}
