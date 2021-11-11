package me.dev.legacy.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.BlockInteractionHelper;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.world.GameType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;

public class AntiCity extends Module {

    private final Setting<Boolean> triggerable;
    private final Setting<Boolean> turnOffCauras;
    private final Setting<Integer> timeoutTicks;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Integer> tickDelay;
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> noGlitchBlocks;
    private int playerHotbarSlot;
    private int lastHotbarSlot;
    private int offsetStep;
    private int delayStep;
    private int totalTicksRunning;
    private boolean firstRun;
    private boolean isSneaking;
    double oldY;
    int cDelay;
    String caura;
    boolean isDisabling;
    boolean hasDisabled;

    public AntiCity() {
        super("AntiCity", "AntiCity.", Module.Category.COMBAT, true, false, false);
        this.triggerable = this.register(new Setting("Triggerable", true));
        this.turnOffCauras = this.register(new Setting<Boolean>("Toggle Other Cauras", true));
        this.timeoutTicks = this.register(new Setting<Integer>("TimeoutTicks", 40, 1, 100));
        this.blocksPerTick = this.register(new Setting<Integer>("BlocksPerTick", 4, 1, 9));
        this.tickDelay = this.register(new Setting<Integer>("TickDelay",  0, 0, 10));
        this.rotate = this.register(new Setting<Boolean>("Rotate", true));
        this.noGlitchBlocks = this.register(new Setting<Boolean>("NoGlitchBlocks", true));
        this.cDelay = 0;
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
        this.offsetStep = 0;
        this.delayStep = 0;
        this.totalTicksRunning = 0;
        this.isSneaking = false;
    }

    @Override
    public void onEnable() {
        if (Surround.mc.player == null) {
            this.disable();
            return;
        }
        this.hasDisabled = false;
        this.oldY = AntiCity.mc.player.posY;
        this.firstRun = true;
        this.playerHotbarSlot = Surround.mc.player.inventory.currentItem;
        this.lastHotbarSlot = -1;
    }

    @Override
    public void onDisable() {
        if (Surround.mc.player == null) {
            return;
        }
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            Surround.mc.player.inventory.currentItem = this.playerHotbarSlot;
        }
        if (this.isSneaking) {
            Surround.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Surround.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
    }

    @Override
    public void onUpdate() {
        if (this.cDelay > 0) {
            --this.cDelay;
        }
        if (this.cDelay == 0 && this.isDisabling) {
            Legacy.moduleManager.getModuleByName(this.caura).toggle();
            this.isDisabling = false;
            this.hasDisabled = true;
        }
        if (Surround.mc.player == null || Legacy.moduleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (Legacy.moduleManager.getModuleByName("AutoCrystal") != null && Legacy.moduleManager.getModuleByName("AutoCrystal").isEnabled() && this.turnOffCauras.getValue() && !this.hasDisabled) {
            this.caura = "AutoCrystal";
            this.cDelay = 19;
            this.isDisabling = true;
            Legacy.moduleManager.getModuleByName(this.caura).toggle();
        }
        if (this.triggerable.getValue() && this.totalTicksRunning >= this.timeoutTicks.getValue()) {
            this.totalTicksRunning = 0;
            this.disable();
            return;
        }
        if (AntiCity.mc.player.posY != this.oldY) {
            this.disable();
            return;
        }
        if (!this.firstRun) {
            if (this.delayStep < this.tickDelay.getValue()) {
                ++this.delayStep;
                return;
            }
            this.delayStep = 0;
        }
        if (this.firstRun) {
            this.firstRun = false;
        }
        int blocksPlaced = 0;
        while (blocksPlaced < this.blocksPerTick.getValue()) {
            Vec3d[] offsetPattern = new Vec3d[0];
            int maxSteps = 0;
            offsetPattern = Offsets.SURROUND;
            maxSteps = Offsets.SURROUND.length;
            if (this.offsetStep >= maxSteps) {
                this.offsetStep = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetStep]);
            final BlockPos targetPos = new BlockPos(Surround.mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            if (this.placeBlock(targetPos)) {
                ++blocksPlaced;
            }
            ++this.offsetStep;
        }
        if (blocksPlaced > 0) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                Surround.mc.player.inventory.currentItem = this.playerHotbarSlot;
                this.lastHotbarSlot = this.playerHotbarSlot;
            }
            if (this.isSneaking) {
                Surround.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Surround.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
        ++this.totalTicksRunning;
    }

    private boolean placeBlock(final BlockPos pos) {
        final Block block = Surround.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        for (final Entity entity : Surround.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return false;
            }
        }
        final EnumFacing side = BlockInteractionHelper.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockInteractionHelper.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = Surround.mc.world.getBlockState(neighbour).getBlock();
        final int obiSlot = this.findObiInHotbar();
        if (obiSlot == -1) {
            this.disable();
        }
        if (this.lastHotbarSlot != obiSlot) {
            Surround.mc.player.inventory.currentItem = obiSlot;
            this.lastHotbarSlot = obiSlot;
        }
        if ((!this.isSneaking && BlockInteractionHelper.blackList.contains(neighbourBlock)) || BlockInteractionHelper.shulkerList.contains(neighbourBlock)) {
            Surround.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Surround.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (this.rotate.getValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        Surround.mc.playerController.processRightClickBlock(Surround.mc.player, Surround.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Surround.mc.player.swingArm(EnumHand.MAIN_HAND);
        Surround.mc.rightClickDelayTimer = 4;
        if (this.noGlitchBlocks.getValue() && !Surround.mc.playerController.getCurrentGameType().equals((Object)GameType.CREATIVE)) {
            Surround.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite));
        }
        return true;
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Surround.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }

    private enum Mode
    {
        SURROUND,
        FULL;
    }

    private static class Offsets
    {
        private static final Vec3d[] SURROUND;

        static {
            SURROUND = new Vec3d[] { new Vec3d(2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 2.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -2.0) };
        }
    }
}