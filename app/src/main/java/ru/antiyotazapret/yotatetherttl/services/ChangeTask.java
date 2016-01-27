package ru.antiyotazapret.yotatetherttl.services;

import android.content.Context;

import java.io.IOException;

import ru.antiyotazapret.yotatetherttl.Android;
import ru.antiyotazapret.yotatetherttl.Preferences;
import ru.antiyotazapret.yotatetherttl.R;
import ru.antiyotazapret.yotatetherttl.TtlApplication;

/**
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class ChangeTask extends Task<ChangeTask.ChangeTaskParameters,Void> {

    @Override
    Void action(ChangeTaskParameters param) {
        Context context = param.context;
        Preferences preferences = param.preferences;

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
                Android.enabledAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                Android.disableMobileData();
            }

            Android.disableTetheringNotification();

            if (!preferences.ignoreIptables() && Android.hasIptables() && Android.canForceTtl()) {
                Android.forceSetTtl();
            }

            if (!Android.isTtlForced()){
                Android.changeDeviceTtl(preferences.ttlFallbackVaule());
            }

            Android.disableBlockList();
            if (preferences.restrictionsEnabled()) {
                Android.applyBlockList(preferences.getBans());
            }

            if (airplaneReconnectType.equals(reconnectType)) {
                Android.disableAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                Android.enabledMobileData();
            }

        } catch (IOException | InterruptedException e) {
            TtlApplication.Loge(e.toString());
            setException(e);
            return null;
        }



        /*
        TODO Заменить на нотификации
        if (preferences.showToastsOnBoot()) {
            Toast.makeText(context, R.string.done, Toast.LENGTH_LONG).show();
        }
        */

       return null;

    }

    public static class ChangeTaskParameters {
        final Preferences preferences;
        final Context context;

        public ChangeTaskParameters(Preferences preferences, Context context) {
            this.preferences = preferences;
            this.context = context;

        }

    }

}

