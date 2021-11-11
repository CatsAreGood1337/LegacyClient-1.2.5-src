package me.dev.legacy.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;

public class MinDamage extends Module {
    public MinDamage() {
        super("MinDamage", "Set minimal damage for auto crystal.", Module.Category.COMBAT, true, false, false); INSTANCE = this;
    }
    private static MinDamage INSTANCE = new MinDamage();

    private final Setting<Float> EnableDamage = this.register(new Setting<Float>("EnableDamage", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(36.0f)));
    private final Setting<Float> DisableDamage = this.register(new Setting<Float>("DisableDamage", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(36.0f)));

    public static MinDamage getInstance() {
        return INSTANCE;
    }

    @Subscribe
    public void onEnable(){
        AutoCrystal.getInstance().minDamage.setValue(EnableDamage.getValue());
    }

    @Subscribe
    public void onDisable(){
        AutoCrystal.getInstance().minDamage.setValue(DisableDamage.getValue());
    }
}
