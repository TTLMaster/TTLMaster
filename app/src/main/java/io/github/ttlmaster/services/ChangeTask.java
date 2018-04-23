package io.github.ttlmaster.services;

import android.content.Context;

import io.github.ttlmaster.rootshell.exceptions.RootDeniedException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.github.ttlmaster.Android;
import io.github.ttlmaster.Preferences;
import io.github.ttlmaster.R;
import io.github.ttlmaster.TtlApplication;

/**
 * @author Pavel Savinov (swapii@gmail.com)
 */
public class ChangeTask extends Task<ChangeTask.ChangeTaskParameters,Void> {

    @Override
    Void action(ChangeTaskParameters param) {
        Context context = param.context;
        Preferences preferences = param.preferences;
        String airplaneReconnectType = context.getString(R.string.prefs_general_reconnectType_airplane);
        String mobileReconnectType = context.getString(R.string.prefs_general_reconnectType_mobile);

        try {


            String reconnectType = preferences.reconnectType();

            if (airplaneReconnectType.equals(reconnectType)) {
                Android.INSTANCE.enabledAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                Android.INSTANCE.disableMobileData();
            }

            Android.INSTANCE.disableTetheringNotification();

            TtlApplication.logi(String.format("COCO %b", Android.INSTANCE.hasIptables()));
            if (!preferences.ignoreIptables() && Android.INSTANCE.hasIptables()) {
                if (Android.INSTANCE.canForceTtl()) {
                    Android.INSTANCE.forceSetTtl();
                } else {
                    Android.INSTANCE.applyWorkaround();
                }

            }

            if (!Android.INSTANCE.isTtlForced()){
                Android.INSTANCE.changeDeviceTtl(preferences.ttlFallbackVaule());
            }

            Android.INSTANCE.disableBlockList();
            if (preferences.restrictionsEnabled()) {
                Android.INSTANCE.applyBlockList(preferences.getBans());
            }

            if (airplaneReconnectType.equals(reconnectType)) {
                Android.INSTANCE.disableAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                Android.INSTANCE.enabledMobileData();
            }

            if (preferences.startWifiHotspotOnApplyTtl()) {
                Android.INSTANCE.setWifiTetheringEnabled(context);
            }

        } catch (IOException | InterruptedException e) {
            TtlApplication.loge(e.toString());
            setException(e);
            return null;
        } catch (RootDeniedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }

    public static class ChangeTaskParameters {
        private final Preferences preferences;
        final Context context;

        public ChangeTaskParameters(Preferences preferences, Context context) {
            this.preferences = preferences;
            this.context = context;

        }

    }

}

