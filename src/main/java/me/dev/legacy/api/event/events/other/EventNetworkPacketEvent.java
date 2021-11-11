package me.dev.legacy.api.event.events.other;

import me.dev.legacy.api.event.events.MinecraftEvent;

public class EventNetworkPacketEvent extends MinecraftEvent
{
    public net.minecraft.network.Packet m_Packet;

    public EventNetworkPacketEvent(Packet p_Packet)
    {
        super();
        m_Packet = (net.minecraft.network.Packet) p_Packet;
    }

    public net.minecraft.network.Packet GetPacket()
    {
        return (net.minecraft.network.Packet) m_Packet;
    }

    public net.minecraft.network.Packet getPacket()
    {
        return (net.minecraft.network.Packet) m_Packet;
    }
}
