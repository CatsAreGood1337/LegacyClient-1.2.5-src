package me.dev.legacy.modules.movement;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.other.KeyPressedEvent;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
    public Setting<Boolean> guiMove = register(new Setting<Boolean>("GuiMove", true));
    public Setting<Boolean> noSlow = register(new Setting<Boolean>("NoSlow", true));
    public Setting<Boolean> strict = register(new Setting<Boolean>("Strict", false));
    public Setting<Boolean> sneakPacket = register(new Setting<Boolean>("SneakPacket", false));
    public Setting<Boolean> webs = register(new Setting<Boolean>("Webs", false));
    public final Setting<Double> webHorizontalFactor = register(new Setting<Double>("WebHSpeed", 2.0, 0.0, 100.0));
    public final Setting<Double> webVerticalFactor = register(new Setting<Double>("WebVSpeed", 2.0, 0.0, 100.0));
    private static NoSlow INSTANCE = new NoSlow();
    private boolean sneaking = false;
    private static KeyBinding[] keys = new KeyBinding[]{NoSlow.mc.gameSettings.keyBindForward, NoSlow.mc.gameSettings.keyBindBack, NoSlow.mc.gameSettings.keyBindLeft, NoSlow.mc.gameSettings.keyBindRight, NoSlow.mc.gameSettings.keyBindJump, NoSlow.mc.gameSettings.keyBindSprint};

    public NoSlow() {
        super("NoSlow", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
        setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static NoSlow getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoSlow();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (guiMove.getValue().booleanValue()) {
            if (NoSlow.mc.currentScreen instanceof GuiOptions || NoSlow.mc.currentScreen instanceof GuiVideoSettings || NoSlow.mc.currentScreen instanceof GuiScreenOptionsSounds || NoSlow.mc.currentScreen instanceof GuiContainer || NoSlow.mc.currentScreen instanceof GuiIngameMenu) {
                for (KeyBinding bind : keys) {
                    KeyBinding.setKeyBindState((int)bind.getKeyCode(), (boolean)Keyboard.isKeyDown((int)bind.getKeyCode()));
                }
            } else if (NoSlow.mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown((int)bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState((int)bind.getKeyCode(), (boolean)false);
                }
            }
        }
        if (webs.getValue().booleanValue() && Legacy.moduleManager.getModuleByClass(PacketFly.class).isDisabled() && Legacy.moduleManager.getModuleByClass(PacketFly.class).isDisabled() && NoSlow.mc.player.isInWeb) {
            NoSlow.mc.player.motionX *= webHorizontalFactor.getValue().doubleValue();
            NoSlow.mc.player.motionZ *= webHorizontalFactor.getValue().doubleValue();
            NoSlow.mc.player.motionY *= webVerticalFactor.getValue().doubleValue();
        }
        Item item = NoSlow.mc.player.getActiveItemStack().getItem();
        if (sneaking && !NoSlow.mc.player.isHandActive() && sneakPacket.getValue().booleanValue()) {
            NoSlow.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoSlow.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            sneaking = false;
        }
    }

    @SubscribeEvent
    public void onUseItem(PlayerInteractEvent.RightClickItem event) {
        Item item = NoSlow.mc.player.getHeldItem(event.getHand()).getItem();
        if ((item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion && sneakPacket.getValue().booleanValue()) && !sneaking) {
            NoSlow.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)NoSlow.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (noSlow.getValue().booleanValue() && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }

    @SubscribeEvent
    public void onKeyEvent(KeyPressedEvent event) {
        if (guiMove.getValue().booleanValue() && event.getStage() == 0 && !(NoSlow.mc.currentScreen instanceof GuiChat)) {
            event.info = event.pressed;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && strict.getValue().booleanValue() && noSlow.getValue().booleanValue() && NoSlow.mc.player.isHandActive() && !NoSlow.mc.player.isRiding()) {
            NoSlow.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(NoSlow.mc.player.posX), Math.floor(NoSlow.mc.player.posY), Math.floor(NoSlow.mc.player.posZ)), EnumFacing.DOWN));
        }
    }
}