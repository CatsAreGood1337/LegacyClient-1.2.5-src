package me.dev.legacy.modules.client;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.modules.Module;
import me.dev.legacy.api.util.ModuleManifest;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.Render.Colour;
import me.dev.legacy.api.util.Render.HudUtil;
import me.dev.legacy.api.util.Render.RainbowUtil;
import me.dev.legacy.api.util.Render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.List;

@ModuleManifest(label = "CsArrsyList", category = Module.Category.CLIENT)
public class ModulesList extends Module {
    public Setting<Integer> arrayX = register(new Setting("arraylistPosX", Integer.valueOf(740), Integer.valueOf(0), Integer.valueOf(885)));
    public Setting<Integer> arrayY = register(new Setting("arraylistPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000)));
    public Setting<Integer> arrayoffset = register(new Setting("arraylistOffSet", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000)));
    private static ModulesList INSTANCE = new ModulesList();
    public static final Minecraft mc = Minecraft.getMinecraft();

    public ModulesList() {
        super("ArrayList", "CSGO arraylist", Module.Category.CLIENT, true, false, false);
        setInstance();
    }

    public static ModulesList getInstance() {
        if ( INSTANCE == null )
            INSTANCE = new ModulesList();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    int width = 0;
    int height = 0;


    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int padding = 5;
        Colour fill = new Colour(0, 0, 0, 255);
        Colour outline = new Colour(127, 127, 127, 255);
        RenderUtil.drawBorderedRect(arrayX.getValue() - padding, arrayY.getValue() - padding,
                arrayX.getValue() + padding + this.getWidth(), arrayY.getValue() + this.getHeight() - 1, 1, fill.hashCode(), outline.hashCode(), false);
        RenderUtil.drawHLineG(arrayX.getValue() - padding, arrayY.getValue() - padding,
                (arrayX.getValue() + padding + this.getWidth()) - (arrayX.getValue() - padding), RainbowUtil.getColour().hashCode(), RainbowUtil.getFurtherColour(arrayoffset.getValue()).hashCode());

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        boolean isTop = false;
        boolean isLeft = false;
        if ( arrayX.getValue() < scaledResolution.getScaledHeight() / 2f ) {
            isTop = true;
        }
        if ( arrayX.getValue() < scaledResolution.getScaledWidth() / 2f ) {
            isLeft = true;
        }
        List<Module> hacks = Legacy.moduleManager.getEnabledModules();
        int bestWidth = 0;
        int y = 0;
        if ( HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.ABC ) {
            for (int k = 0; k < Legacy.moduleManager.sortedModulesABC.size(); k++) {
                String str = Legacy.moduleManager.sortedModulesABC.get(k);

                HudUtil.drawHudString(str,  arrayX.getValue(), arrayY.getValue() + y, new Colour(255, 255, 255, 255).hashCode());
                int w = HudUtil.getHudStringWidth(str);
                if ( w > bestWidth ) {
                    bestWidth = w;
                }

                y += 11;
            }
            height = y;
            width = bestWidth;
        }

    }
}



