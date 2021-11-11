package me.dev.legacy.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class AutoCity extends Module {
    public AutoCity() {
        super("AutoCity", "AutoCity", Category.COMBAT, true, false, false);
    }

    private boolean firstRun;
    private BlockPos mineTarget;
    private EntityPlayer closestTarget;
    Setting<Double> range = this.register(new Setting<Double>("Range", 5.0, 1.0, 6.0));
    Setting<Boolean> announceUsage = this.register(new Setting<Boolean>("Announce", false));

    public void onEnable() {
        if (AutoCity.mc.player == null) {
            this.toggle();
            return;
        }
        MinecraftForge.EVENT_BUS.register((Object)this);
        this.firstRun = true;
    }

    public void onDisable() {
        if (AutoCity.mc.player == null) {
            return;
        }
        MinecraftForge.EVENT_BUS.unregister((Object)this);
        Command.sendMessage(ChatFormatting.RED.toString() + " Disabled");
        return;
    }

    @Override
    public void onUpdate() {
        if (AutoCity.mc.player == null) {
            return;
        }
        this.findClosestTarget();
        if (this.closestTarget == null) {
            if (this.firstRun) {
                this.firstRun = false;
                if (this.announceUsage.getValue()) {
                    Command.sendMessage(ChatFormatting.WHITE.toString() + "Enabled" + ChatFormatting.RESET.toString() + ", no one to city!");
                }
            }
            this.toggle();
            return;
        }
        if (this.firstRun && this.mineTarget != null) {
            this.firstRun = false;
            if (this.announceUsage.getValue()) {
                Command.sendMessage(" Trying to mine: " + ChatFormatting.AQUA.toString() + this.closestTarget.getName());
            }
        }
        this.findCityBlock();
        if (this.mineTarget != null) {
            int newSlot = -1;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = AutoCity.mc.player.inventory.getStackInSlot(i);
                if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemPickaxe) {
                    newSlot = i;
                    break;
                }
            }
            if (newSlot != -1) {
                AutoCity.mc.player.inventory.currentItem = newSlot;
            }
            AutoCity.mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.mineTarget, EnumFacing.UP));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.mineTarget, EnumFacing.UP));
            this.toggle();
        }
        else {
            Command.sendMessage(TextFormatting.BLUE + "] No city blocks to mine!");
            this.toggle();
        }
        return;
    }

    public BlockPos findCityBlock() {
        final Double dist = this.range.getValue();
        final Vec3d vec = this.closestTarget.getPositionVector();
        if (AutoCity.mc.player.getPositionVector().distanceTo(vec) <= dist) {
            final BlockPos targetX = new BlockPos(vec.add(1.0, 0.0, 0.0));
            final BlockPos targetXMinus = new BlockPos(vec.add(-1.0, 0.0, 0.0));
            final BlockPos targetZ = new BlockPos(vec.add(0.0, 0.0, 1.0));
            final BlockPos targetZMinus = new BlockPos(vec.add(0.0, 0.0, -1.0));
            if (this.canBreak(targetX)) {
                this.mineTarget = targetX;
            }
            if (!this.canBreak(targetX) && this.canBreak(targetXMinus)) {
                this.mineTarget = targetXMinus;
            }
            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && this.canBreak(targetZ)) {
                this.mineTarget = targetZ;
            }
            if (!this.canBreak(targetX) && !this.canBreak(targetXMinus) && !this.canBreak(targetZ) && this.canBreak(targetZMinus)) {
                this.mineTarget = targetZMinus;
            }
            if ((!this.canBreak(targetX) && !this.canBreak(targetXMinus) && !this.canBreak(targetZ) && !this.canBreak(targetZMinus)) || AutoCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                this.mineTarget = null;
            }
        }
        return this.mineTarget;
    }

    private boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = AutoCity.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)AutoCity.mc.world, pos) != -1.0f;
    }

    private void findClosestTarget() {
        final List<EntityPlayer> playerList = (List<EntityPlayer>)AutoCity.mc.world.playerEntities;
        this.closestTarget = null;
        for (final EntityPlayer target : playerList) {
            if (target == AutoCity.mc.player) {
                continue;
            }
            if (Legacy.friendManager.isFriend(target.getName())) {
                continue;
            }
            if (!isLiving((Entity)target)) {
                continue;
            }
            if (target.getHealth() <= 0.0f) {
                continue;
            }
            if (this.closestTarget == null) {
                this.closestTarget = target;
            }
            else {
                if (AutoCity.mc.player.getDistance((Entity)target) >= AutoCity.mc.player.getDistance((Entity)this.closestTarget)) {
                    continue;
                }
                this.closestTarget = target;
            }
        }
    }

    public static boolean isLiving(final Entity e) {
        return e instanceof EntityLivingBase;
    }
}
