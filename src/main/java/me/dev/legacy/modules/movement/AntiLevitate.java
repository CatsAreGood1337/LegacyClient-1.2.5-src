package me.dev.legacy.modules.movement;

import me.dev.legacy.modules.Module;
import net.minecraft.potion.Potion;

import java.util.Objects;

public class AntiLevitate extends Module {

    public AntiLevitate() {
        super("AntiLevitate", "Removes levitation effect", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (AntiLevitate.mc.player.isPotionActive((Potion) Objects.requireNonNull(Potion.getPotionFromResourceLocation("levitation")))) {
            AntiLevitate.mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("levitation"));
        }
    }
}
