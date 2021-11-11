package me.dev.legacy.modules.combat;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class EAOffhand extends Module {

    public enum Mode {
        Crystal,
        Totem,
        Gapple
    }

    Setting<Mode> switch_mode = register(new Setting("Mode", Mode.Crystal));
    Setting<Integer> totem_switch = register(new Setting("Totem HP", 16, 0, 36));
    Setting<Boolean> ca_check = register(new Setting("CACheck", false));
    Setting<Boolean> delay = register(new Setting("Delay", false));
    private boolean switching = false;
    private int last_slot;

    public EAOffhand () {
        super("OffhandBypass", "eaoffhand", Category.COMBAT, true, false, false);
    }

    
}
