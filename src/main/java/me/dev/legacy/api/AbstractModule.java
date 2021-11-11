package me.dev.legacy.api;

import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.manager.TextManager;
import me.dev.legacy.api.util.Util;
import me.dev.legacy.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class AbstractModule
        implements Util {
    public List<Setting> settings = new ArrayList<Setting>();
    public TextManager renderer = Legacy.textManager;
    private String name;

    public AbstractModule() {
    }

    public AbstractModule(String name) {
        this.name = name;
    }

    public static boolean nullCheck() {
        return AbstractModule.mc.player == null;
    }

    public static boolean fullNullCheck() {
        return AbstractModule.mc.player == null || AbstractModule.mc.world == null;
    }

    public String getName() {
        return this.name;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    public boolean isEnabled() {
        if (this instanceof Module) {
            return ((Module) this).isOn();
        }
        return false;
    }

    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public Setting register(Setting setting) {
        setting.setFeature(this);
        this.settings.add(setting);
        if (this instanceof Module && AbstractModule.mc.currentScreen instanceof LegacyGui) {
            LegacyGui.getInstance().updateModule((Module) this);
        }
        return setting;
    }

    public void unregister(Setting settingIn) {
        ArrayList<Setting> removeList = new ArrayList<Setting>();
        for (Setting setting : this.settings) {
            if (!setting.equals(settingIn)) continue;
            removeList.add(setting);
        }
        if (!removeList.isEmpty()) {
            this.settings.removeAll(removeList);
        }
        if (this instanceof Module && AbstractModule.mc.currentScreen instanceof LegacyGui) {
            LegacyGui.getInstance().updateModule((Module) this);
        }
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.settings) {
            if (!setting.getName().equalsIgnoreCase(name)) continue;
            return setting;
        }
        return null;
    }

    public void reset() {
        for (Setting setting : this.settings) {
            setting.setValue(setting.getDefaultValue());
        }
    }

    public void clearSettings() {
        this.settings = new ArrayList<Setting>();
    }
}

