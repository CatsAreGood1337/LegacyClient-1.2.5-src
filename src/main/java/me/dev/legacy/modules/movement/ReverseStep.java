package me.dev.legacy.modules.movement;

import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReverseStep extends Module {
    public ReverseStep() {
        super("ReverseStep", "Reverse step", Module.Category.MOVEMENT, false, false, false);
    }

    private final Setting<Float> speed = this.register(new Setting<Float>("Speed", 0.0F, 0.0F, 20.0F));

    @SubscribeEvent
    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.isInWater() || mc.player.isInLava()) {
            return;
        }
        if (mc.player.onGround) {
            mc.player.motionY -= speed.getValue() / 10;
        }
    }
}
