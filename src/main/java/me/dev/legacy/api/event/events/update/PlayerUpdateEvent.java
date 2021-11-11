package me.dev.legacy.api.event.events.update;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerUpdateEvent extends LivingEvent {
    public PlayerUpdateEvent(EntityLivingBase e) {
        super(e);
    }
}
