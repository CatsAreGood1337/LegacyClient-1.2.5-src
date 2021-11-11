package me.dev.legacy.modules.client;

import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.api.event.events.update.UpdateEvent;
import me.dev.legacy.api.util.Render.ColorUtil;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.Render.ColorHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Colors extends Module {
    private static Colors INSTANCE;

    public Setting<Boolean> rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", true));
    public Setting<rainbowMode> rainbowModeHud = (Setting<rainbowMode>)this.register(new Setting("HRainbowMode", rainbowMode.Static, v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowHue = (Setting<Integer>)this.register(new Setting("Delay", 200, 0, 600, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowBrightness = (Setting<Float>)this.register(new Setting("Brightness ", 255.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowSaturation = (Setting<Float>)this.register(new Setting("Saturation", 100.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));

    public static Setting<Object> saturation;

    public Colors () {
        super ("Colors", "Colors for sync.", Category.CLIENT, true, false,false);
        this.setInstance();
    }

    Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
    Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
    Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
    Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));

    public static Colors getInstance() {
        if (Colors.INSTANCE == null) {
            Colors.INSTANCE = new Colors();
        }
        return Colors.INSTANCE;
    }

    private void setInstance() {
        Colors.INSTANCE = this;
    }

    static {
        Colors.INSTANCE = new Colors();
    }

    int ticks;

    public enum rainbowModeArray
    {
        Static,
        Up;
    }

    public enum rainbowMode
    {
        Static;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (ticks++ < 10) {
            ColorHandler.setColor(red.getValue(), green.getValue(), blue.getValue());
        }
    }

    @SubscribeEvent
    public void onClientEvent(ClientEvent event) {
        if (event.getProperty() == red || event.getProperty() == green || event.getProperty() == blue) {
            ColorHandler.setColor(red.getValue(), green.getValue(), blue.getValue());
        }
    }

    public int getCurrentColorHex() {
        if (this.rainbow.getValue().booleanValue()) {
            return Color.HSBtoRGB(this.rainbowHue.getValue(), (float) this.rainbowSaturation.getValue().intValue() / 255.0f, (float) this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return ColorUtil.toARGB(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

}
