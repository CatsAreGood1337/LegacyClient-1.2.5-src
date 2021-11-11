package me.dev.legacy.modules.combat;

import me.dev.legacy.api.util.Minecraft.InventoryUtil;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.*;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HoleFill extends Module {
    private static final BlockPos[] surroundOffset;
    private static HoleFill INSTANCE;

    static {
        HoleFill.INSTANCE = new HoleFill();
        surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
    }

    private final Setting<Integer> range;
    private final Setting<Integer> delay;
    private final Setting<Boolean> rotate;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> packet = register(new Setting("Packet", false));
    private final Timer offTimer;
    private final Timer timer;
    private boolean isSneaking;
    private boolean hasOffhand = false;
    private final Map<BlockPos, Integer> retries;
    private final Timer retryTimer;
    private int blocksThisTick;
    private ArrayList<BlockPos> holes;
    private int trie;

    public HoleFill() {
        super("HoleFill", "Fills holes around you.", Category.COMBAT, true, false, true);
        range = (Setting<Integer>) register(new Setting("PlaceRange", 8, 0, 10));
        delay = (Setting<Integer>) register(new Setting("Delay", 0, 0, 250));
        blocksPerTick = (Setting<Integer>) register(new Setting("BlocksPerTick", 20, 8, 30));
        rotate = (Setting<Boolean>) register(new Setting("Rotate", false));
        offTimer = new Timer();
        timer = new Timer();
        blocksThisTick = 0;
        retries = new HashMap<BlockPos, Integer>();
        retryTimer = new Timer();
        holes = new ArrayList<BlockPos>();
        setInstance();
    }

    public static HoleFill getInstance() {
        if (HoleFill.INSTANCE == null) {
            HoleFill.INSTANCE = new HoleFill();
        }
        return HoleFill.INSTANCE;
    }

    private void setInstance() {
        HoleFill.INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            disable();
        }
        offTimer.reset();
        trie = 0;
    }

    @Override
    public void onTick() {
        if(isOn() && !(blocksPerTick.getValue() == 1 && rotate.getValue())) {
            doHoleFill();
        }
    }

    @Override
    public void onDisable() {
        retries.clear();
    }

    private void doHoleFill() {
        if (check()) {
            return;
        }
        holes = new ArrayList<BlockPos>();
        final Iterable<BlockPos> blocks = BlockPos.getAllInBox(HoleFill.mc.player.getPosition().add(-range.getValue(), -range.getValue(), -range.getValue()), HoleFill.mc.player.getPosition().add(range.getValue(), range.getValue(), range.getValue()));
        for (final BlockPos pos : blocks) {
            if (!HoleFill.mc.world.getBlockState(pos).getMaterial().blocksMovement() && !HoleFill.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) {
                final boolean solidNeighbours = (HoleFill.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN) && (HoleFill.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN) && (HoleFill.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN) && (HoleFill.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | HoleFill.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN) && HoleFill.mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR && HoleFill.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR && HoleFill.mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR;
                if (!solidNeighbours) {
                    continue;
                }
                holes.add(pos);
            }
        }
        holes.forEach(this::placeBlock);
        toggle();
    }

    private void placeBlock(final BlockPos pos) {
        for (final Entity entity : HoleFill.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityLivingBase) {
                return;
            }
        }
        if (blocksThisTick < blocksPerTick.getValue()) {
            final int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            final int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                toggle();
            }
            boolean smartRotate = blocksPerTick.getValue() == 1 && rotate.getValue();
            if (smartRotate) {
                isSneaking = BlockUtil.placeBlockSmartRotate(pos, hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, packet.getValue(), isSneaking);
            } else {
                isSneaking = BlockUtil.placeBlock(pos, hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), isSneaking);
            }
            final int originalSlot = HoleFill.mc.player.inventory.currentItem;
            HoleFill.mc.player.inventory.currentItem = ((obbySlot == -1) ? eChestSot : obbySlot);
            HoleFill.mc.playerController.updateController();
            TestUtil.placeBlock(pos);
            if (HoleFill.mc.player.inventory.currentItem != originalSlot) {
                HoleFill.mc.player.inventory.currentItem = originalSlot;
                HoleFill.mc.playerController.updateController();
            }
            timer.reset();
            ++blocksThisTick;
        }
    }

    private boolean check() {
        if (fullNullCheck()) {
            disable();
            return true;
        }
        blocksThisTick = 0;
        if (retryTimer.passedMs(2000L)) {
            retries.clear();
            retryTimer.reset();
        }
        return !timer.passedMs(delay.getValue());
    }
}
