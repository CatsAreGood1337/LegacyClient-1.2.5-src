package me.dev.legacy.modules.player;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayer;

public class AntiHunger extends Module {
    public AntiHunger() {
        super("AntiHunger", "AntiHunger", Category.PLAYER, true, false, false);
    }
    @EventHandler
    public Listener<PacketEvent.Send> packetListener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketPlayer) {
            ((CPacketPlayer) event.getPacket()).onGround = false;
        }
    });
}
