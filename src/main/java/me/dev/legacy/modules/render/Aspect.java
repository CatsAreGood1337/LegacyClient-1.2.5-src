package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.render.PerspectiveEvent;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect extends Module
{
    private Setting<Double> aspect;

    public Aspect() {
        super("Aspect", "esdxzdfwa", Category.RENDER, true, false, false);
        this.aspect = (Setting<Double>)this.register(new Setting("Aspect", (Aspect.mc.displayWidth / (double)Aspect.mc.	displayHeight), 0.0, 3.0));
    }

    @SubscribeEvent
    public void onPerspectiveEvent(final PerspectiveEvent event) {
        event.setAspect(this.aspect.getValue().floatValue());
    }
}

