package ru.antiyotazapret.yotatetherttl.services;

import com.stericson.rootshell.exceptions.RootDeniedException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ru.antiyotazapret.yotatetherttl.Android;

public class UpdateTtlTask extends Task<Object, UpdateTtlTask.TtlStatus> {
    @Override
    public TtlStatus action(Object o) {
        try {
            final boolean forced = Android.isTtlForced();
            final boolean workaround = Android.isWorkaroundApplied();

            final int ttl = Android.getDeviceTtl();

            return new TtlStatus(ttl, forced, workaround);
        } catch (IOException | InterruptedException e) {
            setException(e);
            return null;
        } catch (RootDeniedException e) {
            setException(e);
        } catch (TimeoutException e) {
            setException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class TtlStatus {
        public final int ttl;
        public final boolean forced;
        public final boolean workaround;

        public TtlStatus(int ttl, boolean forced, boolean workaround) {
            this.ttl = ttl;
            this.forced = forced;
            this.workaround = workaround;
        }

    }
}
