package me.dev.legacy.api.HwidSystem;

import me.dev.legacy.HWIDAuthMod;

public class NoStackTraceThrowable extends RuntimeException {

    public NoStackTraceThrowable(final String msg) {
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public String toString() {
        return "" + HWIDAuthMod.getVersion();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
