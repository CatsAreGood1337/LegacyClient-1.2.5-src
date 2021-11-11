package me.dev.legacy.modules.render;

import me.dev.legacy.modules.Module;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityHunger extends Module {

    public EntityHunger() {
        super("EntityHunger", "Renders hunger while riding entities", Category.RENDER, true, false, false);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        GuiIngameForge.renderFood = true;
    }
}
