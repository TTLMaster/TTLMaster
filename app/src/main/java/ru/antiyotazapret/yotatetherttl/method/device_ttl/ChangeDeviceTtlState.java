package ru.antiyotazapret.yotatetherttl.method.device_ttl;

/**
 * Created by pavel on 01/09/15.
 */
public class ChangeDeviceTtlState {

    private boolean isFinished;
    private Exception exception;

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
