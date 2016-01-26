package ru.antiyotazapret.yotatetherttl.services;

import java.io.IOException;

import ru.antiyotazapret.yotatetherttl.Android;

public class UpdateTtlTask extends Task<Object, UpdateTtlTask.TtlStatus> {
    @Override
    public TtlStatus action(Object o) {
        try {
            return new TtlStatus(Android.getDeviceTtl(), Android.isTtlForced());
        } catch (IOException | InterruptedException e) {
            setException(e);
            return null;
        }
    }

    public class TtlStatus {
        public int ttl;
        public boolean forced;

        public TtlStatus(int ttl, boolean forced) {
            this.ttl = ttl;
            this.forced = forced;
        }

    }
}
