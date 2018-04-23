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
                Android.enabledAirplaneMode();
            }

            if (mobileReconnectType.equals(reconnectType)) {
                Android.disableMobileData();
            }

            Android.disableTetheringNotification();

            TtlApplication.logi(String.format("COCO %b", Android.hasIptables()));
            if (!preferences.ignoreIptables() && Android.hasIptables()) {
                if (Android.canForceTtl()) {
                    Android.forceSetTtl();
                } else {
                    Android.applyWorkaround();
                }

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

            if (preferences.startWifiHotspotOnApplyTtl()) {
                Android.setWifiTetheringEnabled(context);
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

