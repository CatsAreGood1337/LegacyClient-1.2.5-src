package me.dev.legacy.modules.combat;

import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class BetterXP extends Module {
    public BetterXP() {
        super("BetterXP", "uses exp with packets", Category.COMBAT, true, false, false);
    }

    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Integer> lookPitch = this.register(new Setting<Integer>("LookPitch", 90, 0, 100, v -> rotate.getValue()));

    private int delay_count;
    int prvSlot;

    @Override
    public void onEnable() {
        delay_count = 0;
    }

    @Override
    public void onUpdate() {
            if (mc.currentScreen == null) {
                usedXp();
            }
    }

    private int findExpInHotbar() {
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    private void usedXp() {
        int oldPitch = (int)mc.player.rotationPitch;
        prvSlot = mc.player.inventory.currentItem;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(findExpInHotbar()));
        if (rotate.getValue()) {
        mc.player.rotationPitch = lookPitch.getValue();
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, lookPitch.getValue(), true)); }
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (rotate.getValue()) {
        mc.player.rotationPitch = oldPitch; }
        mc.player.inventory.currentItem = prvSlot;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(prvSlot));
    }
}