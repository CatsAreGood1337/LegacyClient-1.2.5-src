package me.dev.legacy.modules.player;

import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", "Fast place items.", Module.Category.PLAYER, true, false, false);
    }

    Setting<Boolean> xp = this.register(new Setting<Boolean>("XP", true));
    Setting<Boolean> crystals = this.register(new Setting<Boolean>("Crystals", true));
    Setting<Boolean> everything = this.register(new Setting<Boolean>("Everything", false));

    @Override
    public void onUpdate() {
        Item main = mc.player.getHeldItemMainhand().getItem();
        Item off  = mc.player.getHeldItemOffhand().getItem();

        boolean mainXP = main instanceof ItemExpBottle;
        boolean offXP  = off instanceof ItemExpBottle;
        boolean mainC = main instanceof ItemEndCrystal;
        boolean offC  = off instanceof ItemEndCrystal;

        if (mainXP | offXP && xp.getValue()) {
            mc.rightClickDelayTimer = 0;
        }

        if (mainC | offC && crystals.getValue()) {
            mc.rightClickDelayTimer = 0;
        }

        if (everything.getValue()) {
            mc.rightClickDelayTimer = 0;
        }
    }
}

