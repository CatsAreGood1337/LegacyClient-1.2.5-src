package me.dev.legacy.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.Render.Colour;
import me.dev.legacy.api.util.Render.HudUtil;
import me.dev.legacy.api.util.Render.RainbowUtil;
import me.dev.legacy.api.util.Render.RenderUtil;

public class Watermark extends Module {
    private static Watermark INSTANCE = new Watermark();

    public Setting<Integer> waterMarkX = register(new Setting("WatermarkPosX", Integer.valueOf(740), Integer.valueOf(0), Integer.valueOf(885)));
    public Setting<Integer> waterMarkY = register(new Setting("WatermarkPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(100)));
    public Setting<Integer> waterMarkoffset = register(new Setting("Offset", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(1000)));
    public Setting<Boolean> Fps = register(new Setting("Fps", false));
    public Setting<Boolean> Tps = register(new Setting("Tps", false));
    public Setting<Boolean> Time = register(new Setting("Time", false));
    public Setting<Boolean> Ping = register(new Setting("Ping", false));

    String text = "";
    public Watermark() {
        super("Watermark", "CSGO watermark", Module.Category.CLIENT, true, false, false);
        setInstance();
    }

    public static Watermark getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Watermark();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }



    public int getHeight() {
        return HudUtil.getHudStringHeight(text);
    }


    public int getWidth(){
        return HudUtil.getHudStringWidth(text);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int padding = 5;
        Colour fill = new Colour(0, 0, 0, 255);
        Colour outline = new Colour(127, 127, 127, 255);
        RenderUtil.drawBorderedRect(waterMarkX.getValue() - padding, waterMarkY.getValue() - padding,
                waterMarkX.getValue() + padding + this.getWidth(), waterMarkY.getValue() + padding + this.getHeight() - 1, 1, fill.hashCode(), outline.hashCode(), false);
        RenderUtil.drawHLineG(waterMarkX.getValue() - padding, waterMarkY.getValue() - padding + 1,
                (waterMarkX.getValue() + padding + this.getWidth()) - 1 - (waterMarkX.getValue() - padding - 1), RainbowUtil.getColour().hashCode(), RainbowUtil.getFurtherColour(Watermark.INSTANCE.waterMarkoffset.getValue()).hashCode());
        text = "legacy" + ChatFormatting.RESET + " | " + mc.player.getName();

        if (Watermark.getInstance().Fps.getValue()) {
            text += " |" + HudUtil.getFpsLine() + "Fps" + ChatFormatting.RESET;
        }
        if (Watermark.getInstance().Tps.getValue()) {
            text += " |" + HudUtil.getTpsLine()+ "Tps"  + ChatFormatting.RESET;
        }
        if (Watermark.getInstance().Ping.getValue()) {
            text += " |" + HudUtil.getPingLine() + "Ms" + ChatFormatting.RESET;
        }
        if (Watermark.getInstance().Time.getValue()) {
            text += " | " + HudUtil.getAnaTimeLine() + ChatFormatting.RESET;
        }

        HudUtil.drawHudString(text, waterMarkX.getValue(), waterMarkY.getValue(), new Colour(255, 255, 255, 255).hashCode()) ;
    }

}