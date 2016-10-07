package ru.antiyotazapret.yotatetherttl.services;

import com.stericson.rootshell.exceptions.RootDeniedException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ru.antiyotazapret.yotatetherttl.Android;
import ru.antiyotazapret.yotatetherttl.TtlApplication;

/**
 * Created by user on 14.02.16.
 */
public class CheckDeviceTask extends Task<Void,CheckDeviceTask.DeviceCheckResult>{

    @Override
    DeviceCheckResult action(Void p) {
        try {
            return new DeviceCheckResult(Android.hasRoot(), Android.hasIptables());
        } catch (IOException | InterruptedException e) {
            TtlApplication.Loge(e.toString());
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
