package me.dev.legacy.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import me.dev.legacy.api.event.events.move.MoveEvent;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.event.events.move.PushEvent;
import me.dev.legacy.api.event.events.move.UpdateWalkingPlayerEvent;
import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.modules.Module;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.Timer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.*;
import net.minecraft.entity.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;



import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PacketFly extends Module
{

    private final Set<CPacketPlayer> packets;
    private final Map<Integer, IDtime> teleportmap;
    private int flightCounter = 0;
    private int teleportID = 0;
    private static PacketFly instance;
    private boolean setMove = false;
    private boolean nocliperino = true;
    public PacketFly() {
        super("PacketFly", "WAN BLOCK BLYAT!", Category.MOVEMENT, true, false, false);
        this.packets = (Set<CPacketPlayer>)new ConcurrentSet();
        this.teleportmap = new ConcurrentHashMap<Integer, IDtime>();
        instance = this;
    }

    public static PacketFly getInstance() {
        if (PacketFly.instance == null) {
            PacketFly.instance = new PacketFly();
        }
        return PacketFly.instance;
    }

    @Override
    public void onToggle() {
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(final UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        PacketFly.mc.player.setVelocity(0.0, 0.0, 0.0);
        final boolean checkCollisionBoxes = this.checkHitBoxes();
        double speed = (PacketFly.mc.player.movementInput.jump && (checkCollisionBoxes || !EntityUtil.isMoving())) ? (checkCollisionBoxes ? 0.062 : (this.resetCounter(10) ? -0.032 : 0.062)) : (PacketFly.mc.player.movementInput.sneak ? -0.062 : (checkCollisionBoxes ? 0.0 : (this.resetCounter(4) ? -0.04 : 0.0)));
        if (checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0) {
            final double antiFactor = 2.5;
            speed /= antiFactor;
        }
        final boolean strafeFactor = true;
        final double[] strafing = this.getMotion((strafeFactor && checkCollisionBoxes) ? 0.031 : 0.26);
        for (int loops = 1, i = 1; i < loops + 1; ++i) {
            final double extraFactor = 1.0;
            PacketFly.mc.player.motionX = strafing[0] * i * extraFactor;
            PacketFly.mc.player.motionY = speed * i;
            PacketFly.mc.player.motionZ = strafing[1] * i * extraFactor;
            final boolean sendTeleport = true;
            this.sendPackets(PacketFly.mc.player.motionX, PacketFly.mc.player.motionY, PacketFly.mc.player.motionZ, sendTeleport);
        }
    }

    @SubscribeEvent
    public void onMove (MoveEvent event) {
        if (this.setMove && this.flightCounter != 0) {
            event.setX(PacketFly.mc.player.motionX);
            event.setY(PacketFly.mc.player.motionY);
            event.setZ(PacketFly.mc.player.motionZ);
            if (this.nocliperino && this.checkHitBoxes()) {
                PacketFly.mc.player.noClip = true;
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && !this.packets.remove(event.getPacket())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPushOutOfBlocks(final PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && !AbstractModule.fullNullCheck()) {
            final SPacketPlayerPosLook packet = event.getPacket();
            if (PacketFly.mc.player.isEntityAlive() && PacketFly.mc.world.isBlockLoaded(new BlockPos(PacketFly.mc.player.	posX, PacketFly.mc.player.posY, PacketFly.mc.player.posZ), false) && !(PacketFly.mc.currentScreen instanceof GuiDownloadTerrain)) {
                this.teleportmap.remove(packet.getTeleportId());
            }
            this.teleportID = packet.getTeleportId();
        }
    }

    private boolean checkHitBoxes() {
        return !PacketFly.mc.world.getCollisionBoxes((Entity) PacketFly.mc.player, PacketFly.mc.player.getEntityBoundingBox().expand(-0.0, -0.1, -0.0)).isEmpty();
    }

    private boolean resetCounter(final int counter) {
        if (++this.flightCounter >= counter) {
            this.flightCounter = 0;
            return true;
        }
        return false;
    }

    private double[] getMotion(final double speed) {
        float moveForward = PacketFly.mc.player.movementInput.moveForward;
        float moveStrafe = PacketFly.mc.player.movementInput.moveStrafe;
        float rotationYaw = PacketFly.mc.player.prevRotationYaw + (PacketFly.mc.player.rotationYaw - PacketFly.mc.player.prevRotationYaw) * PacketFly.mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        final double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[] { posX, posZ };
    }

    private void sendPackets(final double x, final double y, final double z, final boolean teleport) {
        final Vec3d vec = new Vec3d(x, y, z);
        final Vec3d position = PacketFly.mc.player.getPositionVector().add(vec);
        final Vec3d outOfBoundsVec = this.outOfBoundsVec(position);
        this.packetSender((CPacketPlayer)new CPacketPlayer.Position(position.x, position.y, position.z, PacketFly.mc.player.onGround));
        this.packetSender((CPacketPlayer)new CPacketPlayer.Position(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, PacketFly.mc.player.onGround));
        this.teleportPacket(position, teleport);
    }

    private void teleportPacket(final Vec3d pos, final boolean shouldTeleport) {
        if (shouldTeleport) {
            PacketFly.mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(++this.teleportID));
            this.teleportmap.put(this.teleportID, new IDtime(pos, new Timer()));
        }
    }

    private Vec3d outOfBoundsVec(final Vec3d position) {
        return position.add(0.0, 1337.0, 0.0);
    }

    private void packetSender(final CPacketPlayer packet) {
        this.packets.add(packet);
        PacketFly.mc.player.connection.sendPacket((Packet)packet);
    }

    public static class IDtime
    {
        private final Vec3d pos;
        private final Timer timer;

        public IDtime(final Vec3d pos, final Timer timer) {
            this.pos = pos;
            (this.timer = timer).reset();
        }

        public Vec3d getPos() {
            return this.pos;
        }

        public Timer getTimer() {
            return this.timer;
        }
    }
}