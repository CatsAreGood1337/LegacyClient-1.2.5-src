package me.dev.legacy.modules.player;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.block.BlockEvent;
import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.combat.Surround;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.BlockUtil;
import me.dev.legacy.api.util.Minecraft.InventoryUtil;
import me.dev.legacy.api.util.Render.RenderUtil;
import me.dev.legacy.api.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FastBreak extends Module {
    private static FastBreak INSTANCE = new FastBreak();
    private final Timer timer = new Timer();
    public Setting<Mode> mode = register(new Setting<Mode>("Mode", Mode.PACKET));
    public Setting<Float> damage = register(new Setting<Object>("Damage", Float.valueOf(0.7f), Float.valueOf(0.0f), Float.valueOf(1.0f), v -> mode.getValue() == Mode.DAMAGE));
    public Setting<Boolean> webSwitch = register(new Setting<Boolean>("WebSwitch", false));
    public Setting<Boolean> doubleBreak = register(new Setting<Boolean>("DoubleBreak", false));
    public Setting<Boolean> autosw = register(new Setting<Boolean>("AutoSwitch", false));
    public Setting<Boolean> render = register(new Setting<Boolean>("Render", false));
    public Setting<Boolean> box = register(new Setting<Object>("Box", Boolean.valueOf(false), v -> render.getValue()));
    private final Setting<Integer> boxAlpha = register(new Setting<Object>("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), v -> box.getValue() != false && render.getValue() != false));
    public Setting<Boolean> outline = register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> render.getValue()));
    private final Setting<Float> lineWidth = register(new Setting<Object>("Width", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> outline.getValue() != false && render.getValue() != false));
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    private int lasthotbarslot;

    public FastBreak() {
        super("FastBreak", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
        setInstance();
    }

    public static FastBreak getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FastBreak();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (currentPos != null) {
            if (!FastBreak.mc.world.getBlockState(currentPos).equals(currentBlockState) || FastBreak.mc.world.getBlockState(currentPos).getBlock() == Blocks.AIR) {
                currentPos = null;
                currentBlockState = null;
            } else if (webSwitch.getValue().booleanValue() && currentBlockState.getBlock() == Blocks.WEB && FastBreak.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (FastBreak.fullNullCheck()) {
            return;
        }
        FastBreak.mc.playerController.blockHitDelay = 0;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (render.getValue().booleanValue() && currentPos != null && currentBlockState.getBlock() == Blocks.OBSIDIAN) {
            Color color = new Color(timer.passedMs((int) (2000.0f * Legacy.serverManager.getTpsFactor())) ? 0 : 255, timer.passedMs((int) (2000.0f * Legacy.serverManager.getTpsFactor())) ? 255 : 0, 0, 255);
            RenderUtil.drawBoxESP(currentPos, color, false, color, lineWidth.getValue().floatValue(), outline.getValue(), box.getValue(), boxAlpha.getValue(), false);
            if (autosw.getValue()) {
                boolean hasPickaxe = mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;
                if (!hasPickaxe) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.player.inventory.getStackInSlot(i);
                        if (stack.isEmpty())
                            continue;
                        lasthotbarslot = Surround.mc.player.inventory.currentItem;
                        if (FastBreak.mc.player.inventory.currentItem != lasthotbarslot) {
                            lasthotbarslot = FastBreak.mc.player.inventory.currentItem;
                        }
                        if (stack.getItem() == Items.DIAMOND_PICKAXE) {
                            hasPickaxe = true;
                            mc.player.inventory.currentItem = i;
                            mc.playerController.updateController();
                            break;
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (FastBreak.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && FastBreak.mc.playerController.curBlockDamageMP > 0.1f) {
            FastBreak.mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4) {
            BlockPos above;
            if (BlockUtil.canBreak(event.pos)) {
                FastBreak.mc.playerController.isHittingBlock = false;
                switch (mode.getValue()) {
                    case PACKET: {
                        if (currentPos == null) {
                            currentPos = event.pos;
                            currentBlockState = FastBreak.mc.world.getBlockState(currentPos);
                            timer.reset();
                        }
                        FastBreak.mc.player.swingArm(EnumHand.MAIN_HAND);
                        FastBreak.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        FastBreak.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        event.setCanceled(true);
                        break;
                    }
                    case DAMAGE: {
                        if (!(FastBreak.mc.playerController.curBlockDamageMP >= damage.getValue().floatValue()))
                            break;
                        FastBreak.mc.playerController.curBlockDamageMP = 1.0f;
                        break;
                    }
                    case INSTANT: {
                        FastBreak.mc.player.swingArm(EnumHand.MAIN_HAND);
                        FastBreak.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        FastBreak.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        FastBreak.mc.playerController.onPlayerDestroyBlock(event.pos);
                        FastBreak.mc.world.setBlockToAir(event.pos);
                    }
                }
            }
            if (doubleBreak.getValue().booleanValue() && BlockUtil.canBreak(above = event.pos.add(0, 1, 0)) && FastBreak.mc.player.getDistance(above.getX(), above.getY(), above.getZ()) <= 5.0) {
                FastBreak.mc.player.swingArm(EnumHand.MAIN_HAND);
                FastBreak.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
                FastBreak.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
                FastBreak.mc.playerController.onPlayerDestroyBlock(above);
                FastBreak.mc.world.setBlockToAir(above);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.currentEnumName();
    }

    public enum Mode {
        PACKET,
        DAMAGE,
        INSTANT
    }

}


