package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.modules.Module;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

public class BebraESP extends Module {
    public BebraESP() {
        super("BebraESP", "b- b- b- bebra", Module.Category.RENDER, true, false, false);
    }


}
