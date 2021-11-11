package me.dev.legacy.modules.combat;

import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.BlockUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public class AutoBed extends Module {
    boolean moving = false;

    //Double
    Setting<Double> range = this.register(new Setting("Range", 4.5D, 0D, 10D));
    //Boolean
    Setting<Boolean> rotate = this.register(new Setting("Rotate", true));
    Setting<Boolean> dimensionCheck = this.register(new Setting("DimensionCheck", true));
    Setting<Boolean> refill = this.register(new Setting("RefillBed", true));


    public AutoBed() {
        super("AutoBed", "Fucked (Future)", Category.COMBAT, true, false, false);
    }

    public void onUpdate() {
        if(refill.getValue()) {
            //search for empty hotbar slots
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (mc.player.inventory.getStackInSlot(i) == ItemStack.EMPTY) {
                    slot = i;
                    break;
                }
            }

            if (moving && slot != -1) {
                mc.playerController.windowClick(0, slot + 36, 0, ClickType.PICKUP, mc.player);
                moving = false;
                slot = -1;
            }

            if (slot != -1 && !(mc.currentScreen instanceof GuiContainer) && mc.player.inventory.getItemStack().isEmpty()) {
                //search for beds in inventory
                int t = -1;
                for (int i = 0; i < 45; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.BED && i >= 9) {
                        t = i;
                        break;
                    }
                }
                //click bed item
                if (t != -1) {
                    mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, mc.player);
                    moving = true;
                }
            }
        }

        mc.world.loadedTileEntityList.stream()
                .filter(e -> e instanceof TileEntityBed)
                .filter(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()) <= range.getValue())
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ())))
                .forEach(bed -> {

                    if(dimensionCheck.getValue() && mc.player.dimension == 0) return;

                    if(rotate.getValue()) BlockUtil.faceVectorPacketInstant(new Vec3d(bed.getPos().add(0.5, 0.5, 0.5)));
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bed.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                });
    }
}
