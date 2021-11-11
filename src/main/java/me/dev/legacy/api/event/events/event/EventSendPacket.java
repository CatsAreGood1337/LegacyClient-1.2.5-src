package me.dev.legacy.api.event.events.event;

import me.dev.legacy.api.event.events.other.Packet;

public final class EventSendPacket extends EventCancellable {

    private Packet packet;

    public EventSendPacket(EventStage stage, Packet packet) {
        super(stage);
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
