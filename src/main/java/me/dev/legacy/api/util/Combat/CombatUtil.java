package me.dev.legacy.api.util.Combat;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.Combat.HoleUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class CombatUtil {

    public static final List<Block> blackList = Arrays.asList(Blocks.TALLGRASS, Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR);
    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);

    private static Minecraft mc = Minecraft.getMinecraft();

    public static final Vec3d[] cityOffsets = {
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(2, 0, 0),
            new Vec3d(0, 0, 2),
            new Vec3d(-2, 0, 0),
            new Vec3d(0, 0, -2),
    };

    private static final List<Integer> invalidSlots = Arrays.asList(0, 5, 6, 7, 8);

    public static int findCrapple() {
        if (mc.player == null) {
            return -1;
        }
        for (int x = 0; x < mc.player.inventoryContainer.getInventory().size(); x++) {
            if(invalidSlots.contains(x)) {
                continue;
            }
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(x);
            if(stack.isEmpty()) {
                continue;
            }
            if(stack.getItem().equals(Items.GOLDEN_APPLE) && !(stack.getItemDamage() == 1)) {
                return x;
            }
        }
        return -1;
    }

    public static int findItemSlotDamage1(Item i) {
        if (mc.player == null) {
            return -1;
        }
        for (int x = 0; x < mc.player.inventoryContainer.getInventory().size(); x++) {
            if(invalidSlots.contains(x)) {
                continue;
            }
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(x);
            if(stack.isEmpty()) {
                continue;
            }
            if(stack.getItem().equals(i) && (stack.getItemDamage() == 1)) {
                return x;
            }
        }
        return -1;
    }

    public static int findItemSlot(Item i) {
        if (mc.player == null) {
            return -1;
        }
        for (int x = 0; x < mc.player.inventoryContainer.getInventory().size(); x++) {
            if(invalidSlots.contains(x)) {
                continue;
            }
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(x);
            if(stack.isEmpty()) {
                continue;
            }
            if(stack.getItem().equals(i)) {
                return x;
            }
        }
        return -1;
    }

    public static boolean isHoldingCrystal(boolean onlyMainHand) {
        if(onlyMainHand) {
            return (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
        } else {
            return (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        }
    }

    public static boolean requiredDangerSwitch(double dangerRange) {
        int dangerousCrystals = (int) mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .filter(entity -> mc.player.getDistance(entity) <= dangerRange)
                .filter(entity -> calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player) >= (mc.player.getHealth() + mc.player.getAbsorptionAmount()))
                .count();
        return dangerousCrystals > 0;
    }

    public static boolean passesOffhandCheck(double requiredHealth, Item item, boolean isCrapple) {
        double totalPlayerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if(!isCrapple) {
            if (findItemSlot(item) == -1) {
                return false;
            }
        } else {
            if(findCrapple() == -1) {
                return false;
            }
        }
        if(totalPlayerHealth < requiredHealth) {
            return false;
        }
        return true;
    }

    public static void switchOffhandStrict(int targetSlot, int step) {
        switch(step) {
            case 0:
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, mc.player);
                break;
            case 1:
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
                break;
            case 2:
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, targetSlot, 0,ClickType.PICKUP, mc.player);
                mc.playerController.updateController();
                break;
        }
    }

    public static boolean placeBlock(BlockPos blockPos, boolean offhand, boolean rotate, boolean packetRotate, boolean doSwitch, boolean silentSwitch, int toSwitch) {
        if(!checkCanPlace(blockPos)) {
            return false;
        }

        EnumFacing placeSide = getPlaceSide(blockPos);
        BlockPos adjacentBlock = blockPos.offset(placeSide);
        EnumFacing opposingSide = placeSide.getOpposite();
        if(!mc.world.getBlockState(adjacentBlock).getBlock().canCollideCheck(mc.world.getBlockState(adjacentBlock), false)) {
            return false;
        }
        if(doSwitch) {
            if(silentSwitch) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(toSwitch));
            } else {
                if(mc.player.inventory.currentItem != toSwitch) {
                    mc.player.inventory.currentItem = toSwitch;
                }
            }
        }
        boolean isSneak = false;
        if(blackList.contains(mc.world.getBlockState(adjacentBlock).getBlock()) || shulkerList.contains(mc.world.getBlockState(adjacentBlock).getBlock())) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneak = true;
        }
        Vec3d hitVector = getHitVector(adjacentBlock, opposingSide);
        if(rotate) {
            final float[] angle = getLegitRotations(hitVector);
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
        }

        EnumHand actionHand = offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        mc.playerController.processRightClickBlock(mc.player, mc.world, adjacentBlock, opposingSide, hitVector, actionHand);
        mc.player.connection.sendPacket(new CPacketAnimation(actionHand));
        if(isSneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return true;
    }

    private static Vec3d getHitVector(BlockPos pos, EnumFacing opposingSide) {
        return new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(opposingSide.getDirectionVec()).scale(0.5));
    }

    public static Vec3d getHitAddition(double x, double y, double z, BlockPos pos, EnumFacing opposingSide) {
        return new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(opposingSide.getDirectionVec()).scale(0.5));
    }

    private static EnumFacing getPlaceSide(BlockPos blockPos) {
        EnumFacing placeableSide = null;
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos adjacent = blockPos.offset(side);
            if (mc.world.getBlockState(adjacent).getBlock().canCollideCheck(mc.world.getBlockState(adjacent), false) && !mc.world.getBlockState(adjacent).getMaterial().isReplaceable()) {
                placeableSide = side;
            }
        }
        return placeableSide;
    }

    public static boolean checkCanPlace(BlockPos pos) {
        if (!(mc.world.getBlockState(pos).getBlock() instanceof BlockAir) && !(mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) {
            return false;
        }
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityArrow)) {
                return false;
            }
        }
        return getPlaceSide(pos) != null;
    }

    public static boolean isInCity(EntityPlayer player, double range, double placeRange, boolean checkFace, boolean topBlock, boolean checkPlace, boolean checkRange) {
        BlockPos pos = new BlockPos(player.getPositionVector());
        for (EnumFacing face : EnumFacing.values()) {
            BlockPos pos1;
            BlockPos pos2;
            BlockPos pos3;
            if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;
            pos1 = pos.offset(face);
            pos2 = pos1.offset(face);
            if (mc.world.getBlockState(pos1).getBlock() == Blocks.AIR
                    && ((mc.world.getBlockState(pos2).getBlock() == Blocks.AIR && isHard(mc.world.getBlockState(pos2.up()).getBlock())) || !checkFace)
                    && !checkRange || mc.player.getDistanceSq(pos2) <= (placeRange * placeRange)
                    && mc.player.getDistanceSq(player) <= (range * range)
                    && isHard(mc.world.getBlockState(pos.up(3)).getBlock()) || !topBlock) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHard(Block block) {
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
    }

    public static boolean canPlaceCrystal(BlockPos pos, double range, double wallsRange, boolean raytraceCheck) {
        BlockPos up = pos.up();
        BlockPos up1 = up.up();
        AxisAlignedBB bb = new AxisAlignedBB(up).expand(0.0d, 1.0d, 0.0d);
        return ((mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN
                || mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK)
                && mc.world.getBlockState(up).getBlock() == Blocks.AIR
                && mc.world.getBlockState(up1).getBlock() == Blocks.AIR
                && mc.world.getEntitiesWithinAABB(Entity.class, bb).isEmpty()
                && mc.player.getDistanceSq(pos) <= range * range
                && !raytraceCheck || rayTraceRangeCheck(pos, wallsRange, 0.0d));
    }

    public static Map<BlockPos, Double> mapBlockDamage(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
        Map<BlockPos, Double> damageMap = new HashMap<>();
        for (BlockPos pos : getSphere(new BlockPos(getFlooredPosition(mc.player)), (float) placeRange, (int) placeRange, false, true, 0)) {
            if (!canPlaceCrystal(pos, placeRange, wallsRange, rayTrace)) continue;
            if (!checkFriends(pos, maxFriendDamage, friendRange, antiFriendPop)) continue;;
            if (!checkSelf(pos, maxSelfDamage, antiSuicide)) continue;
            if (rayTrace && !rayTraceRangeCheck(pos, wallsRange, 0.0d)) continue;
            double damage = calculateDamage(pos, player);
            if (damage < minDamage) continue;
            damageMap.put(pos, damage);
        }
        return damageMap;
    }

    public static boolean checkFriends(BlockPos pos, double maxFriendDamage, double friendRange, boolean antiFriendPop) {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (mc.player.getDistanceSq(player) > friendRange * friendRange) continue;
                if (calculateDamage(pos, player) > maxFriendDamage) {
                    return false;
                }
                if (calculateDamage(pos, player) > player.getHealth() + player.getAbsorptionAmount() && antiFriendPop) {
                    return false;
                }
            }
        return true;
    }

    public static boolean checkFriends(EntityEnderCrystal crystal, double maxFriendDamage, double friendRange, boolean antiFriendPop) {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (mc.player.getDistanceSq(player) > friendRange * friendRange) continue;
                if (calculateDamage(crystal, player) > maxFriendDamage) {
                    return false;
                }
                if (calculateDamage(crystal, player) > player.getHealth() + player.getAbsorptionAmount() && antiFriendPop) {
                    return false;
                }
            }
        return true;
    }

    public static boolean checkSelf(BlockPos pos, double maxSelfDamage, boolean antiSuicide) {
        boolean willPopSelf = calculateDamage(pos, mc.player) > (mc.player.getHealth() + mc.player.getAbsorptionAmount());
        boolean willDamageSelf = calculateDamage(pos, mc.player) > maxSelfDamage;
        return (!antiSuicide || !willPopSelf) && !willDamageSelf;
    }

    public static boolean checkSelf(EntityEnderCrystal crystal, double maxSelfDamage, boolean antiSuicide) {
        boolean willPopSelf = calculateDamage(crystal, mc.player) > (mc.player.getHealth() + mc.player.getAbsorptionAmount());
        boolean willDamageSelf = calculateDamage(crystal, mc.player) > maxSelfDamage;
        return (!antiSuicide || !willPopSelf) && !willDamageSelf;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static boolean rayTraceRangeCheck(Entity target, double range) {
        // RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(target.posX, target.posY + height, target.posZ), false, true, false);
        boolean isVisible = mc.player.canEntityBeSeen(target);
        return !isVisible || mc.player.getDistanceSq(target) <= range * range;
    }

    public static boolean rayTraceRangeCheck(BlockPos pos, double range, double height) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), false, true, false);
        return result == null || mc.player.getDistanceSq(pos) <= range * range;
        //.boolean isVisible = mc.player.canEntityBeSeen(target);
    }

    public static List<BlockPos> getDisc(BlockPos pos, float r) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < r * r) {
                    BlockPos position = new BlockPos(x, cy, z);
                    circleblocks.add(position);
                }
            }
        }

        return circleblocks;
    }

    public static BlockPos getFlooredPosition(Entity entity) {
        return new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch(Exception ignored) {}
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(Minecraft.getMinecraft().world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ignored) {}
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage = damage * (1.0F - f / 25.0F);

            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage = damage - (damage / 4);
            }

            damage = Math.max(damage, 0.0F);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = Minecraft.getMinecraft().world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(Entity crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    public static float calculateDamage(BlockPos pos, Entity entity) {
        return calculateDamage(pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, entity);
    }

    public static Vec3d interpolateEntity(Entity entity) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.getRenderPartialTicks(), entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.getRenderPartialTicks(), entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.getRenderPartialTicks());
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[] { mc.player.rotationYaw +
                MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch +
                MathHelper.wrapDegrees(pitch - mc.player.rotationPitch) };
    }

}
