package me.dev.legacy.api.event.events.event;

public class EventStageable {

    private EventStage stage;

    public EventStageable() {

    }

    public EventStageable(EventStage stage) {
        this.stage = stage;
    }

    public EventStage getStage() {
        return stage;
    }

    public void setStage(EventStage stage) {
        this.stage = stage;
    }

    public enum EventStage {
        PRE, POST
    }
}
