package ru.antiyotazapret.yotatetherttl.services;

import java.io.IOException;

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
        }
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
