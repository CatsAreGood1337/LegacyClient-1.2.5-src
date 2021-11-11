package me.dev.legacy.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class ClickGui extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<String> prefix = register(new Setting("Prefix", "."));
    public Setting<Boolean> customFov = register(new Setting("CustomFov", false));
    public Setting<Float> fov = register(new Setting("Fov", Float.valueOf(150.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)));
    public Setting<Boolean> gears = register(new Setting("Gears", Boolean.valueOf(false), "draws gears"));
    public Setting<Integer> red = register(new Setting("Red", 210, 0, 255));
    public Setting<Integer> green = register(new Setting("Green", 130, 0, 255));
    public Setting<Integer> blue = register(new Setting("Blue", 255, 0, 255));
    public Setting<Integer> hoverAlpha = register(new Setting("Alpha", 180, 0, 255));
    public Setting<Integer> alpha = register(new Setting("HoverAlpha", 240, 0, 255));
    public Setting<Boolean> rainbow = register(new Setting("Rainbow", false));
    public Setting<rainbowMode> rainbowModeHud = register(new Setting<Object>("HRainbowMode", rainbowMode.Static, v -> rainbow.getValue()));
    public Setting<rainbowModeArray> rainbowModeA = register(new Setting<Object>("ARainbowMode", rainbowModeArray.Static, v -> rainbow.getValue()));
    public Setting<Integer> rainbowHue = register(new Setting<Object>("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), v -> rainbow.getValue()));
    public Setting<Float> rainbowBrightness = register(new Setting<Object>("Brightness ", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> rainbow.getValue()));
    public Setting<Float> rainbowSaturation = register(new Setting<Object>("Saturation", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> rainbow.getValue()));

    public Setting<Integer> startcolorred = register(new Setting("StartRed", 210, 0, 255));
    public Setting<Integer> startcolorgreen = register(new Setting("StartGreen", 210, 0, 255));
    public Setting<Integer> startcolorblue = register(new Setting("StartBlue", 210, 0, 255));
    public Setting<Integer> endcolorred = register(new Setting("EndRed", 210, 0, 255));
    public Setting<Integer> endcolorgreen = register(new Setting("EndGreen", 210, 0, 255));
    public Setting<Integer> endcolorblue = register(new Setting("EndBlue", 210, 0, 255));
    public Setting<Integer> fontcolorr = register(new Setting("FontRed", 210, 0, 255));
    public Setting<Integer> fontcolorg = register(new Setting("FontGreen", 210, 0, 255));
    public Setting<Integer> fontcolorb = register(new Setting("FontBlue", 210, 0, 255));


    public Setting<Boolean> outline = register(new Setting("Outline", false));
    public Setting<Integer> testcolorr = register(new Setting("PanelRed", 210, 0, 255));
    public Setting<Integer> testcolorgreen = register(new Setting("PanelGreen", 210, 0, 255));
    public Setting<Integer> testcolorblue = register(new Setting("PanelBlue", 210, 0, 255));

    public float hue;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (customFov.getValue().booleanValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.getValue().floatValue()); }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(prefix)) {
                Legacy.commandManager.setPrefix(prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Legacy.commandManager.getPrefix());
            }
            Legacy.colorManager.setColor(red.getPlannedValue(), green.getPlannedValue(), blue.getPlannedValue(), hoverAlpha.getPlannedValue());
        }
    }

    public Color getCurrentColor() {
        if (this.rainbow.getValue().booleanValue()) {
            return Color.getHSBColor(this.hue, (float) this.rainbowSaturation.getValue().intValue() / 255.0f, (float) this.rainbowBrightness.getValue().intValue() / 255.0f);
        }
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(LegacyGui.getClickGui());
    }

    @Override
    public void onLoad() {
        Legacy.colorManager.setColor(red.getValue(), green.getValue(), blue.getValue(), hoverAlpha.getValue());
        Legacy.commandManager.setPrefix(prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof LegacyGui)) {
            disable();
        }
    }

    public enum rainbowModeArray {
        Static,
        Up

    }

    public enum rainbowMode {
        Static,
        Sideway

    }
}

