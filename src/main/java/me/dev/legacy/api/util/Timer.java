package me.dev.legacy.api.util;

public class Timer {
    private long current = -1L;

    private long time = -1L;

    public boolean passedS(double s) {
        return (getMs(System.nanoTime() - this.time) >= (long)(s * 1000.0D));
    }

    public boolean passedM(double m) {
        return (getMs(System.nanoTime() - this.time) >= (long)(m * 1000.0D * 60.0D));
    }

    public boolean passedDms(double dms) {
        return (getMs(System.nanoTime() - this.time) >= (long)(dms * 10.0D));
    }

    public boolean passedDs(double ds) {
        return (getMs(System.nanoTime() - this.time) >= (long)(ds * 100.0D));
    }

    public boolean passedMs(long ms) {
        return (getMs(System.nanoTime() - this.time) >= ms);
    }

    public boolean passedNS(long ns) {
        return (System.nanoTime() - this.time >= ns);
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - ms * 1000000L;
    }

    public long getPassedTimeMs() {
        return getMs(System.nanoTime() - this.time);
    }

    public boolean passed(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public boolean hasReached(long delay) {
        return (System.currentTimeMillis() - this.current >= delay);
    }

    public boolean hasReached(long delay, boolean reset) {
        if (reset)
            reset();
        return (System.currentTimeMillis() - this.current >= delay);
    }

    public boolean sleep(long time) {
        if (System.nanoTime() / 1000000L - time >= time) {
            reset();
            return true;
        }
        return false;
    }

    public final boolean hasReachedRealth(long delay) {
        return (System.currentTimeMillis() - this.current >= delay);
    }

    public boolean hasReachedRealth(long delay, boolean reset) {
        if (reset)
            reset();
        return (System.currentTimeMillis() - this.current >= delay);
    }

    public final void resetRealth() {
        this.current = System.currentTimeMillis();
    }

    public void resetTimeSkipTo(long p_MS)
    {
        this.time = System.currentTimeMillis() + p_MS;
    }
}
