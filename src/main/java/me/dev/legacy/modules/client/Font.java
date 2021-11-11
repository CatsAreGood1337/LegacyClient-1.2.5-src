package me.dev.legacy.modules.client;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.ClientEvent;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Font extends Module {
    private boolean reloadFont = false;
    public Setting<String> fontName = this.register(new Setting<String>("FontName", "Arial", "Name of the font."));
    public Setting<Integer> fontSize = this.register(new Setting<Integer>("FontSize", Integer.valueOf(18), "Size of the font."));
    public Setting<Integer> fontStyle = this.register(new Setting<Integer>("FontStyle", Integer.valueOf(0), "Style of the font."));
    public Setting<Boolean> antiAlias = this.register(new Setting<Boolean>("AntiAlias", Boolean.valueOf(true), "Smoother font."));
    public Setting<Boolean> fractionalMetrics = this.register(new Setting<Boolean>("Metrics", Boolean.valueOf(true), "Thinner font."));
    public Setting<Boolean> shadow = this.register(new Setting<Boolean>("Shadow", Boolean.valueOf(true), "Less shadow offset font."));
    public Setting<Boolean> showFonts = this.register(new Setting<Boolean>("Fonts", Boolean.valueOf(false), "Shows all fonts."));
    public Setting<Boolean> full = this.register(new Setting<Boolean>("Full", false));
    private static Font INSTANCE = new Font();

    public Font() {
        super("CustomFont", "CustomFont for all of the clients text. Use the font command.", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Font getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Font();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this)) {
            if (setting.getName().equals("FontName") && !Font.checkFont(setting.getPlannedValue().toString(), false)) {
                Command.sendMessage("\u00a7cThat font doesnt exist.");
                event.setCanceled(true);
                return;
            }
            this.reloadFont = true;
        }
    }

    @Override
    public void onTick() {
        if (this.showFonts.getValue().booleanValue()) {
            Font.checkFont("Hello", true);
            Command.sendMessage("Current Font: " + this.fontName.getValue());
            this.showFonts.setValue(false);
        }
        if (this.reloadFont) {
            Legacy.textManager.init(false);
            this.reloadFont = false;
        }
    }

    public static boolean checkFont(String font, boolean message) {
        String[] fonts;
        for (String s : fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!message && s.equals(font)) {
                return true;
            }
            if (!message) continue;
            Command.sendMessage(s);
        }
        return false;
    }
}