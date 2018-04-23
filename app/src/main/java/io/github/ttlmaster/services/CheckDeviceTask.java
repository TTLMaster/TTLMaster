package io.github.ttlmaster.services;

import io.github.ttlmaster.rootshell.exceptions.RootDeniedException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import io.github.ttlmaster.Android;
import io.github.ttlmaster.TtlApplication;

/**
 * Created by user on 14.02.16.
 */
public class CheckDeviceTask extends Task<Void,CheckDeviceTask.DeviceCheckResult>{

    DeviceCheckResult action(Void p) {
        try {
            return new DeviceCheckResult(Android.INSTANCE.hasRoot(), Android.INSTANCE.hasIptables());
        } catch (IOException | InterruptedException e) {
            TtlApplication.loge(e.toString());
            return new DeviceCheckResult(false, false);
        } catch (RootDeniedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class DeviceCheckResult {
        public boolean hasRoot;
        public boolean hasIptables;

        public DeviceCheckResult(boolean hasRoot, boolean hasIptables) {
            this.hasRoot = hasRoot;
            this.hasIptables = hasIptables;
        }
    }
}
