package me.dev.legacy.modules.movement;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBoat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

public class BoatFly extends Module {
    public Setting<Double> speed = register(new Setting<Double>("Speed", 3.0, 1.0, 10.0));
    public Setting<Double> verticalSpeed = register(new Setting<Double>("VerticalSpeed", 3.0, 1.0, 10.0));
    public Setting<Boolean> placeBypass = register(new Setting<Boolean>("PlaceBypass", true));
    public Setting<Boolean> bypass = register(new Setting<Boolean>("Bypass", true));
    public Setting<Boolean> noKick = register(new Setting<Boolean>("No-Kick", true));
    public Setting<Boolean> packet = register(new Setting<Boolean>("Packet", true));
    public Setting<Integer> packets = register(new Setting<Object>("Packets", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(5), v -> packet.getValue()));
    public Setting<Integer> interact = register(new Setting<Integer>("Delay", 2, 1, 20));
    public static BoatFly INSTANCE;
    private EntityBoat target;
    private int teleportID;
    private int packetCounter = 0;

    public BoatFly() {
        super("BoatFly", "/fly but boat", Module.Category.MOVEMENT, true, false, false);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (BoatFly.mc.player == null) {
            return;
        }
        if (BoatFly.mc.world == null || BoatFly.mc.player.getRidingEntity() == null) {
            return;
        }
        if (BoatFly.mc.player.getRidingEntity() instanceof EntityBoat) {
            target = (EntityBoat)BoatFly.mc.player.ridingEntity;
        }
        BoatFly.mc.player.getRidingEntity().setNoGravity(true);
        BoatFly.mc.player.getRidingEntity().motionY = 0.0;
        if (BoatFly.mc.gameSettings.keyBindJump.isKeyDown()) {
            BoatFly.mc.player.getRidingEntity().onGround = false;
            BoatFly.mc.player.getRidingEntity().motionY = verticalSpeed.getValue() / 10.0;
        }
        if (BoatFly.mc.gameSettings.keyBindSprint.isKeyDown()) {
            BoatFly.mc.player.getRidingEntity().onGround = false;
            BoatFly.mc.player.getRidingEntity().motionY = -(verticalSpeed.getValue() / 10.0);
        }
        double[] normalDir = directionSpeed(speed.getValue() / 2.0);
        if (BoatFly.mc.player.movementInput.moveStrafe != 0.0f || BoatFly.mc.player.movementInput.moveForward != 0.0f) {
            BoatFly.mc.player.getRidingEntity().motionX = normalDir[0];
            BoatFly.mc.player.getRidingEntity().motionZ = normalDir[1];
        } else {
            BoatFly.mc.player.getRidingEntity().motionX = 0.0;
            BoatFly.mc.player.getRidingEntity().motionZ = 0.0;
        }
        if (noKick.getValue().booleanValue()) {
            if (BoatFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                if (BoatFly.mc.player.ticksExisted % 8 < 2) {
                    BoatFly.mc.player.getRidingEntity().motionY = -0.04f;
                }
            } else if (BoatFly.mc.player.ticksExisted % 8 < 4) {
                BoatFly.mc.player.getRidingEntity().motionY = -0.08f;
            }
        }
        handlePackets(BoatFly.mc.player.getRidingEntity().motionX, BoatFly.mc.player.getRidingEntity().motionY, BoatFly.mc.player.getRidingEntity().motionZ);
    }

    public void handlePackets(double x, double y, double z) {
        if (packet.getValue().booleanValue()) {
            Vec3d vec = new Vec3d(x, y, z);
            if (BoatFly.mc.player.getRidingEntity() == null) {
                return;
            }
            Vec3d position = BoatFly.mc.player.getRidingEntity().getPositionVector().add(vec);
            BoatFly.mc.player.getRidingEntity().setPosition(position.x, position.y, position.z);
            BoatFly.mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(BoatFly.mc.player.getRidingEntity()));
            for (int i = 0; i < packets.getValue(); ++i) {
                BoatFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(teleportID++));
            }
        }
    }

    private void NCPPacketTrick() {
        packetCounter = 0;
        mc.player.getRidingEntity().dismountRidingEntity();
        Entity l_Entity = mc.world.loadedEntityList.stream().filter(p_Entity -> p_Entity instanceof EntityBoat).min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity))).orElse(null);
        if (l_Entity != null) {
            mc.playerController.interactWithEntity(mc.player, l_Entity, EnumHand.MAIN_HAND);
        }
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {

        if (mc.world != null && mc.player != null) {
            if (mc.player.getRidingEntity() instanceof EntityBoat) {
                if (bypass.getValue() && event.getPacket() instanceof CPacketInput && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.player.getRidingEntity().onGround) {
                    ++packetCounter;
                    if (packetCounter == 3) {
                        NCPPacketTrick();
                    }
                }
                if (bypass.getValue() && event.getPacket() instanceof SPacketPlayerPosLook || event.getPacket() instanceof SPacketMoveVehicle) {
                    event.setCanceled(true);
                }
            }
        }

        if (event.getPacket() instanceof CPacketVehicleMove && BoatFly.mc.player.isRiding() && BoatFly.mc.player.ticksExisted % interact.getValue() == 0) {
            BoatFly.mc.playerController.interactWithEntity((EntityPlayer)BoatFly.mc.player, BoatFly.mc.player.ridingEntity, EnumHand.OFF_HAND);
        }
        if ((event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketInput) && BoatFly.mc.player.isRiding()) {
            event.setCanceled(true);
        }
        if (placeBypass.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock
                && mc.player.getHeldItemMainhand().getItem() instanceof ItemBoat || mc.player.getHeldItemOffhand().getItem() instanceof ItemBoat) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketMoveVehicle && BoatFly.mc.player.isRiding()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            teleportID = ((SPacketPlayerPosLook)event.getPacket()).teleportId;
        }
    }

    private double[] directionSpeed(double speed) {
        float forward = BoatFly.mc.player.movementInput.moveForward;
        float side = BoatFly.mc.player.movementInput.moveStrafe;
        float yaw = BoatFly.mc.player.prevRotationYaw + (BoatFly.mc.player.rotationYaw - BoatFly.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }
}
