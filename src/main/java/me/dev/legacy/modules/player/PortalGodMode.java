package me.dev.legacy.modules.player;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

public class PortalGodMode extends Module {
    public PortalGodMode() {
        super("PortalGodMode", "PortalGodMode", Module.Category.PLAYER, true, false, false);
    }
    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (isEnabled() && event.getPacket() instanceof CPacketConfirmTeleport) {
            event.setCanceled(true);
        }
    });
}
