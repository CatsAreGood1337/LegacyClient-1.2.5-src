package me.dev.legacy.api.event.events.update;

import me.dev.legacy.api.event.EventStage;

public class UpdateEvent
        extends EventStage {
    private final int stage;

    public UpdateEvent(int stage) {
        this.stage = stage;
    }

    public final int getStage() {
        return this.stage;
    }
}
