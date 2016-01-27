package ru.antiyotazapret.yotatetherttl.services;

import java.io.IOException;

import ru.antiyotazapret.yotatetherttl.Android;

public class UpdateTtlTask extends Task<Object, UpdateTtlTask.TtlStatus> {
    @Override
    public TtlStatus action(Object o) {
        try {
            final boolean forced = Android.isTtlForced();
            final int ttl = forced ? 64 : Android.getDeviceTtl();

            return new TtlStatus(ttl, forced);
        } catch (IOException | InterruptedException e) {
            setException(e);
            return null;
        }
    }

    public class TtlStatus {
        public final int ttl;
        public final boolean forced;

        public TtlStatus(int ttl, boolean forced) {
            this.ttl = ttl;
            this.forced = forced;
        }

    }
}
