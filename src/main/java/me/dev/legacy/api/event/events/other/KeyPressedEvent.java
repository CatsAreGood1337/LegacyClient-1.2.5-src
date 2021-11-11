package me.dev.legacy.api.event.events.other;

import me.dev.legacy.api.event.EventStage;

public class KeyPressedEvent
        extends EventStage {
    public boolean info;
    public boolean pressed;

    public KeyPressedEvent(boolean info, boolean pressed) {
        this.info = info;
        this.pressed = pressed;
    }
}

