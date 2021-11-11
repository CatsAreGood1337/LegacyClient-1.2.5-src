package me.dev.legacy.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.dev.legacy.api.event.events.move.UpdateWalkingPlayerEvent;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.api.util.Minecraft.InventoryUtil;
import me.dev.legacy.api.util.Minecraft.ItemUtil;
import me.dev.legacy.api.util.Render.ColorUtil;
import me.dev.legacy.api.util.Render.RenderUtil;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.client.ClickGui;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.mixin.mixins.accessors.AccessorCPacketUseEntity;
import me.dev.legacy.api.util.BlockUtil;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;;



public class AutoCrystal extends Module {
    private static AutoCrystal INSTANCE = new AutoCrystal();

    public enum InfoMode
    {
        Target,
        Damage,
        Both,
    }

    public enum Settings {
        Place,
        Break,
        Render,
        Misc;
    }

    public enum Raytrace {
        None,
        Place,
        Break,
        Both;
    }

    //Settings
    Setting<Settings> setting = register(new Setting("Settings", Settings.Place));

    //Place settings
    Setting<Integer> placeDelay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), v -> (this.setting.getValue() == Settings.Place)));
    Setting<Float> placeRange = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Place)));
    Setting<Float> minDamage = register(new Setting("MinDamage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(30.0F), v -> (this.setting.getValue() == Settings.Place)));

    //Break settings
    Setting<Integer> breakDelay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), v -> (this.setting.getValue() == Settings.Break)));
    Setting<Float> breakRange = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Break)));
    Setting<Float> breakWallRange = register(new Setting("WallRange", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Break)));
    Setting<Float> maxSelf = register(new Setting("MaxSelfDamage", Float.valueOf(18.5F), Float.valueOf(0.0F), Float.valueOf(36.0F), v -> (this.setting.getValue() == Settings.Break)));
    Setting<Boolean> cancelcrystal = register(new Setting("FastMode", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Break)));
    Setting<Boolean> antiWeakness = register(new Setting("AntiWeakness", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Break)));
    Setting<Boolean> antiWeaknessSilent = register(new Setting("SilentWeakness", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Break && ((Boolean)this.antiWeakness.getValue()).booleanValue())));
    Setting<Boolean> predict = this.register(new Setting("Predict", true, v -> this.setting.getValue() == Settings.Break));

    //Misc settings
    Setting<Float> range = register(new Setting("TargetRange", Float.valueOf(9.5F), Float.valueOf(0.0F), Float.valueOf(16.0F), v -> (this.setting.getValue() == Settings.Misc)));
    Setting<Float> lethalMult = register(new Setting("LethalMult", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Misc)));
    Setting<Float> armorScale = register(new Setting("ArmorFucker", Float.valueOf(25.0F), Float.valueOf(0.0F), Float.valueOf(100.0F), v -> (this.setting.getValue() == Settings.Misc)));
    Setting<Boolean> autoSwitch = register(new Setting("AutoSwitch", false, v -> (this.setting.getValue() == Settings.Misc)));
    Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", false, v -> (this.setting.getValue() == Settings.Misc)));
    Setting<Boolean> autoDisable = register(new Setting("AutoDisable", true, v -> this.setting.getValue() == Settings.Misc));


    //Render settings
    Setting<InfoMode> infomode = this.register(new Setting("Display", InfoMode.Target, v -> this.setting.getValue() == Settings.Render));
    Setting<Boolean> offhandS = this.register(new Setting("Offhand", true, v -> this.setting.getValue() == Settings.Render));
    Setting<Boolean> render = this.register(new Setting("Render", true, v -> this.setting.getValue() == Settings.Render));
    Setting<Integer> red = this.register(new Setting("Red", 80, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Integer> green = this.register(new Setting("Green", 120, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Integer> alpha = this.register(new Setting("Alpha", 120, 0,255, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Boolean> colorSync = this.register(new Setting("ColorSync", false, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Boolean> box = this.register(new Setting("Box", true, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Integer> boxAlpha = this.register(new Setting("BoxAlpha", 30, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue() && this.box.getValue()));
    Setting<Boolean> outline = this.register(new Setting("Outline", true, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Float> lineWidth = this.register(new Setting("LineWidth", 0.1f, 0.1f, 5.0f, v -> this.setting.getValue() == Settings.Render && this.render.getValue() && this.outline.getValue()));
    Setting<Boolean> text = this.register(new Setting("DamageText", true, v -> this.setting.getValue() == Settings.Render && this.render.getValue()));
    Setting<Boolean> customOutline = this.register(new Setting("CustomLine", false, v -> this.setting.getValue() == Settings.Render && this.render.getValue() && this.outline.getValue()));
    Setting<Integer> cRed = this.register(new Setting("OL-Red", 255, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
    Setting<Integer> cGreen = this.register(new Setting("OL-Green", 255, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
    Setting<Integer> cBlue = this.register(new Setting("OL-Blue", 255, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));
    Setting<Integer> cAlpha = this.register(new Setting("OL-Alpha", 255, 0, 255, v -> this.setting.getValue() == Settings.Render && this.render.getValue() && this.customOutline.getValue() && this.outline.getValue()));

    EntityEnderCrystal crystal;
    private final Map<Integer, Integer> attackMap;
    private final List<BlockPos> placedList;
    private final Timer breakTimer;
    private final Timer placeTimer;
    private final Timer renderTimer;
    public static EntityPlayer currentTarget;
    private BlockPos renderPos;
    private double renderDamage;
    private boolean offhand;
    private boolean shouldEnable = false;

    public AutoCrystal() {
        super("AutoCrystal", "Based crystal aura.", Module.Category.COMBAT, true, false, false);
        this.attackMap = new HashMap<>();
        this.placedList = new ArrayList<>();
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
        this.renderTimer = new Timer();
        this.renderPos = null;
        this.renderDamage = 0.0D;
        INSTANCE = this;
    }

    public static AutoCrystal getInstance() {
        return INSTANCE;
    }

    public void onEnable() {
        this.placedList.clear();
        this.breakTimer.reset();
        this.placeTimer.reset();
        this.renderTimer.reset();
        this.currentTarget = null;
        this.attackMap.clear();
        this.renderPos = null;
        this.offhand = false;
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (fullNullCheck())
            return;
        if (this.renderTimer.passedMs(500L)) {
            this.placedList.clear();
            this.renderPos = null;
            this.renderTimer.reset();
        }
        this.offhand = ((mc.player.inventory.offHandInventory.get(0)).getItem() == Items.END_CRYSTAL);
        currentTarget = EntityUtil.getClosestPlayer(((Float)this.range.getValue()).floatValue());
        if (currentTarget == null)
            return;
        doCrystal();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        BlockPos pos = null;
        CPacketUseEntity packet;
        if (event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal)
            pos = packet.getEntityFromWorld(mc.world).getPosition();
        if (event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
            EntityEnderCrystal crystal = (EntityEnderCrystal)packet.getEntityFromWorld(mc.world);
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal && (this.cancelcrystal.getValue()).booleanValue()) {
            (Objects.<Entity>requireNonNull(packet.getEntityFromWorld(mc.world))).setDead();
            mc.world.removeEntityFromWorld(packet.entityId);
        }
    }

    private void doCrystal () {
        Entity maxCrystal = null;
        Entity crystal = null;
        double maxDamage = 0.5D;
        int size = mc.world.loadedEntityList.size();
        for (int i = 0; i < size; i++) {
            Entity entity = mc.world.loadedEntityList.get(i);
            if (entity instanceof EntityEnderCrystal &&
                    mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? this.breakRange.getValue() : this.breakWallRange.getValue()).floatValue()) {
                float targetDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, (EntityLivingBase) currentTarget);
                if (targetDamage > ((Float) this.minDamage.getValue()).floatValue() || targetDamage * (this.lethalMult.getValue()).floatValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || ItemUtil.isArmorUnderPercent(currentTarget, ((Float) this.armorScale.getValue()).floatValue())) {
                    float selfDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, (EntityLivingBase) mc.player);
                    if (selfDamage <= ((Float) this.maxSelf.getValue()).floatValue() && selfDamage + 2.0F <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage && maxDamage <= targetDamage) {
                        maxDamage = targetDamage;
                        crystal = entity;
                        maxCrystal = crystal;
                    }
                }
            }
        }
        if (crystal != null && this.breakTimer.passedMs(((Integer) this.breakDelay.getValue()).intValue())) {
            mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
            mc.player.swingArm((this.offhandS.getValue()).booleanValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            this.breakTimer.reset();
        }
        if (maxCrystal != null && this.breakTimer.hasReached(((Integer) this.breakDelay.getValue()).intValue())) {
            int lastSlot = -1;
            if ((this.antiWeakness.getValue()).booleanValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                boolean swtch = (!mc.player.isPotionActive(MobEffects.STRENGTH) || ((PotionEffect) Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH))).getAmplifier() != 2);
                int swordSlot = ItemUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                if (swtch && swordSlot != -1) {
                    lastSlot = mc.player.inventory.currentItem;
                    if ((this.antiWeaknessSilent.getValue()).booleanValue()) {
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(swordSlot));
                    } else {
                        mc.player.inventory.currentItem = swordSlot;
                    }
                }
            }
            mc.getConnection().sendPacket(new CPacketUseEntity(maxCrystal));
            this.attackMap.put(Integer.valueOf(maxCrystal.getEntityId()), Integer.valueOf(this.attackMap.containsKey(Integer.valueOf(maxCrystal.getEntityId())) ? (((Integer) this.attackMap.get(Integer.valueOf(maxCrystal.getEntityId()))).intValue() + 1) : 1));
            mc.player.swingArm(EnumHand.OFF_HAND);
            if (lastSlot != -1)
                if ((this.antiWeaknessSilent.getValue()).booleanValue()) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                } else {
                    mc.player.inventory.currentItem = lastSlot;
                }
            this.breakTimer.reset();
        }

        BlockPos placePos = null;
        List<BlockPos> sphere = BlockUtil.getSphereRealth((this.placeRange.getValue()).floatValue(), true);
        int size2 = sphere.size();
        for (int i = 0; i < size2; i++) {
            BlockPos pos = sphere.get(i);
            if (BlockUtil.canPlaceCrystal(pos)) {
                float targetDamage = EntityUtil.calculate(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, currentTarget);
                if (targetDamage > (this.minDamage.getValue()).floatValue() || targetDamage * (this.lethalMult.getValue()).floatValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || ItemUtil.isArmorUnderPercent(currentTarget, (this.armorScale.getValue()).floatValue())) {
                    float selfDamage = EntityUtil.calculate(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, mc.player);
                    if (selfDamage <= (this.maxSelf.getValue()).floatValue() && selfDamage + 2.0F <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage && maxDamage <= targetDamage) {
                        maxDamage = targetDamage;
                        placePos = pos;
                        this.renderPos = pos;
                        this.renderDamage = targetDamage;
                    }
                }
            }
        }
        int prevslot = mc.player.inventory.currentItem;
        int crystalslot = findStuffInHotbar();
        boolean flag = false;
        if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
            if (this.autoSwitch.getValue()) {
                mc.player.inventory.currentItem = crystalslot;
                return;
            }
        }

        if (this.silentSwitch.getValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(crystalslot));
        }

        if (placePos != null) {
            if (this.placeTimer.passedMs((this.placeDelay.getValue()).intValue())) {
                if (flag) {
                    int slot = ItemUtil.getItemFromHotbar(Items.END_CRYSTAL);
                    if (slot == -1)
                        return;
                    mc.player.inventory.currentItem = slot;
                }
                this.placedList.add(placePos);
                mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                this.placeTimer.reset();
            }
            this.renderPos = placePos;
        }
        for (BlockPos pos : BlockUtil.possiblePlacePositions((this.placeRange.getValue()).floatValue())) {
        }

        if (silentSwitch.getValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(prevslot));
        }

    }

    private int findStuffInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && predict.getValue().booleanValue()) {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51 && this.placedList.contains(new BlockPos(packet.getX(), packet.getY() - 1.0D, packet.getZ()))) {
                AccessorCPacketUseEntity use = (AccessorCPacketUseEntity)new CPacketUseEntity();
                use.setEntityId(packet.getEntityID());
                use.setAction(CPacketUseEntity.Action.ATTACK);
                mc.getConnection().sendPacket((Packet)use);
                mc.player.swingArm((this.offhandS.getValue()).booleanValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                this.breakTimer.reset();
                return;
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
                (new ArrayList(mc.world.loadedEntityList)).forEach(e -> {
                    if (e instanceof EntityEnderCrystal && ((EntityEnderCrystal) e).getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36.0D)
                        ((EntityEnderCrystal) e).setDead();
                });
        }

        if (autoDisable.getValue().booleanValue()) {
            if (EntityUtil.isDead(mc.player)) {
               this.disable();
            }
        }
    }

    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.renderPos != null && this.render.getValue() && (this.box.getValue() || this.text.getValue() || this.outline.getValue())) {
            RenderUtil.drawBoxESP(this.renderPos, (this.colorSync.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), (this.colorSync.getValue()) ? this.getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
            if (this.text.getValue()) {
                RenderUtil.drawText(this.renderPos, ((Math.floor(this.renderDamage) == this.renderDamage) ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
            }
        }
    }

    public Color getCurrentColor() {
        return new Color((this.red.getValue()).intValue(), (this.green.getValue()).intValue(), (this.blue.getValue()).intValue(), (this.alpha.getValue()).intValue());
    }

    @Override
    public void onLogout() {
        if (this.autoDisable.getValue().booleanValue()) {
            this.disable();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (currentTarget != null) {
            if (this.infomode.getValue() == InfoMode.Target) {
                return currentTarget.getName();
            }
            if (this.infomode.getValue() == InfoMode.Damage) {
                return ((Math.floor(this.renderDamage) == this.renderDamage) ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "";
            }
            if (this.infomode.getValue() == InfoMode.Both) {
                return currentTarget.getName() + ", " + ((Math.floor(this.renderDamage) == this.renderDamage) ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "";
            }
        }
        return null;
    }
}

