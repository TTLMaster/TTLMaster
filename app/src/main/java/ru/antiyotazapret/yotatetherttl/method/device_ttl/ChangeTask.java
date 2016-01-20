package ru.antiyotazapret.yotatetherttl.method.device_ttl;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import ru.antiyotazapret.yotatetherttl.Android;
import ru.antiyotazapret.yotatetherttl.Preferences;
import ru.antiyotazapret.yotatetherttl.R;

/**
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class ChangeTask extends AsyncTask<ChangeTask.ChangeTaskParameters, Void, Void> {

    ChangeTaskParameters.OnResult callback;

    public static void doInForeground(ChangeTaskParameters param) {
        Context context = param.context;
        Preferences preferences = param.preferences;
        Integer ttl = param.ttl;


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

            if (!preferences.ignoreIptables() && Android.canForceTtl()) {
                Android.forceSetTtl();
            }

            if (!Android.isTtlForced()){
                Android.changeDeviceTtl(ttl);
            }

            if (airplaneReconnectType.equals(reconnectType)) {
                Android.disableAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                Android.enabledMobileData();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        /*
        TODO Заменить на нотификации
        if (preferences.showToastsOnBoot()) {
            Toast.makeText(context, R.string.done, Toast.LENGTH_LONG).show();
        }
        */

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callback != null) {
            callback.OnResult();
        }
    }

    @Override
    protected Void doInBackground(ChangeTaskParameters... params) {

        if (params.length != 1) {
            return null;
        }

        callback = params[0].callback;
        doInForeground(params[0]);

        return null;
    }


    public static class ChangeTaskParameters {
        final Preferences preferences;
        final Context context;
        final Integer ttl;
        OnResult callback;


        public ChangeTaskParameters(Preferences preferences, Context context, Integer ttl) {
            this.preferences = preferences;
            this.context = context;
            this.ttl = ttl;

            if (context instanceof OnResult) {
                callback = (OnResult) context;
            }
        }


        public interface OnResult {
            public void OnResult();
        }


    }

}

