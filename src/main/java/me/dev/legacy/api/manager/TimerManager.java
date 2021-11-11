package me.dev.legacy.api.manager;

import me.dev.legacy.api.AbstractModule;

public class TimerManager
        extends AbstractModule {
    private float timer = 1.0f;

    public void unload() {
        this.timer = 1.0f;
        TimerManager.mc.timer.tickLength = 50.0f;
    }

    public void update() {
        TimerManager.mc.timer.tickLength = 50.0f / (this.timer <= 0.0f ? 0.1f : this.timer);
    }

    public void setTimer(float timer) {
        if (timer > 0.0f) {
            this.timer = timer;
        }
    }

    public float getTimer() {
        return this.timer;
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
    }
}

