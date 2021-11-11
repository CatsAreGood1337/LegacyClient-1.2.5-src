package me.dev.legacy.modules.movement;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.move.MoveEvent;
import me.dev.legacy.api.event.events.move.UpdateWalkingPlayerEvent;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.player.Freecam;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.Timer;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;
import java.math.*;
import net.minecraftforge.fml.common.eventhandler.*;

import java.util.Objects;


public class Strafe extends Module
{
    private static Strafe INSTANCE;
    private final Setting<Integer> acceleration;
    private final Setting<Float> speedLimit;
    private int potionSpeed;
    private int stage;
    private double moveSpeed;
    private double lastDist;
    private int cooldownHops;
    private boolean waitForGround;
    private final Timer timer;
    private int hops;

    public Strafe() {
        super("Strafe", "AirControl etc.", Category.MOVEMENT, true, false, false);
        this.acceleration = (Setting<Integer>)this.register(new Setting("Speed", 1600, 1000, 2500));
        this.speedLimit = (Setting<Float>)this.register(new Setting("MaxSpeed", 60.0f, 20.0f, 60.0f));
        this.potionSpeed = 1500;
        this.stage = 1;
        this.cooldownHops = 0;
        this.waitForGround = false;
        this.timer = new Timer();
        this.hops = 0;
        Strafe.INSTANCE = this;
    }

    public static Strafe getInstance() {
        if (Strafe.INSTANCE == null) {
            Strafe.INSTANCE = new Strafe();
        }
        return Strafe.INSTANCE;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.272;
        if (Strafe.mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(Strafe.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * amplifier;
        }
        return baseSpeed;
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        final BigDecimal bigDecimal = new BigDecimal(value).setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    @Override
    public void onEnable() {
        if (!Strafe.mc.player.onGround) {
            this.waitForGround = true;
        }
        this.hops = 0;
        this.timer.reset();
        this.moveSpeed = getBaseMoveSpeed();
    }

    @Override
    public void onDisable() {
        this.hops = 0;
        this.moveSpeed = 0.0;
        this.stage = 0;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            this.lastDist = Math.sqrt((Strafe.mc.player.posX- Strafe.mc.player.prevPosX) * (Strafe.mc.player.posX - Strafe.mc.player.prevPosX) + (Strafe.mc.player.posZ - Strafe.mc.player.prevPosZ) * (Strafe.mc.player.posZ - Strafe.mc.player.prevPosZ));
        }
    }

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (event.getStage() != 0 || this.shouldReturn()) {
            return;
        }
        if (!Strafe.mc.player.onGround) {
            this.waitForGround = false;
        }
        float moveForward = Strafe.mc.player.movementInput.moveForward;
        float moveStrafe = Strafe.mc.player.movementInput.moveStrafe;
        float rotationYaw = Strafe.mc.player.rotationYaw;
        if (Strafe.mc.player.onGround && Legacy.speedManager.getSpeedKpH() < this.speedLimit.getValue()) {
            this.stage = 2;
        }
        if (round(Strafe.mc.player.posY - (int)Strafe.mc.player.posY, 3) == round(1.0, 3) && EntityUtil.isEntityMoving((Entity)Strafe.mc.player)) {
            final EntityPlayerSP player = Strafe.mc.player;
            player.motionY -= 0.0;
            event.setY(event.getY() - 0.2);
        }
        if (this.stage == 1 && EntityUtil.isMoving()) {
            this.stage = 2;
            this.moveSpeed = this.getMultiplier() * getBaseMoveSpeed() - 0.01;
        }
        else if (this.stage == 2 && EntityUtil.isMoving()) {
            this.stage = 3;
            event.setY(Strafe.mc.player.motionY = 0.4);
            if (this.cooldownHops > 0) {
                --this.cooldownHops;
            }
            ++this.hops;
            final double accel = (this.acceleration.getValue() == 2149) ? 2.149802 : (this.acceleration.getValue() / 1000.0);
            this.moveSpeed *= accel;
        }
        else if (this.stage == 3) {
            this.stage = 4;
            final double difference = 0.66 * (this.lastDist - getBaseMoveSpeed());
            this.moveSpeed = this.lastDist - difference;
        }
        else {
            if (Strafe.mc.world.getCollisionBoxes((Entity)Strafe.mc.player, Strafe.mc.player.getEntityBoundingBox().offset(0.0, Strafe.mc.player.motionY, 0.0)).size() > 0 || (Strafe.mc.player.collidedVertically && this.stage > 0)) {
                this.stage = ((Strafe.mc.player.moveForward != 0.0f || Strafe.mc.player.moveStrafing != 0.0f) ? 1 : 0);
            }
            this.moveSpeed = this.lastDist - this.lastDist / 200.0;
        }
        this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            event.setX(0.0);
            event.setZ(0.0);
            this.moveSpeed = 0.0;
        }
        else if (moveForward != 0.0f) {
            if (moveStrafe >= 1.0f) {
                rotationYaw += ((moveForward > 0.0f) ? -45.0f : 45.0f);
                moveStrafe = 0.0f;
            }
            else if (moveStrafe <= -1.0f) {
                rotationYaw += ((moveForward > 0.0f) ? 45.0f : -45.0f);
                moveStrafe = 0.0f;
            }
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double motionX = Math.cos(Math.toRadians(rotationYaw + 90.0f));
        final double motionZ = Math.sin(Math.toRadians(rotationYaw + 90.0f));
        if (this.cooldownHops == 0) {
            event.setX(moveForward * this.moveSpeed * motionX + moveStrafe * this.moveSpeed * motionZ);
            event.setZ(moveForward * this.moveSpeed * motionZ - moveStrafe * this.moveSpeed * motionX);
        }
        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            this.timer.reset();
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    private float getMultiplier() {
        float baseSpeed = 500.0f;
        if (Strafe.mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(Strafe.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1;
            baseSpeed = ((amplifier >= 2) ? ((float)this.potionSpeed) : ((float)this.potionSpeed));
        }
        return baseSpeed / 100.0f;
    }

    private boolean shouldReturn() {
        return Legacy.moduleManager.isModuleEnabled(Freecam.class) || Legacy.moduleManager.isModuleEnabled(PacketFly.class);
    }
}
