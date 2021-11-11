package me.dev.legacy.modules.movement;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class Phase extends Module {
    public Phase() {
        super("Phase", "Block phase", Module.Category.MOVEMENT, false, false, false);
    }

    public enum Mode {
        Rel,
        Abs,
    }

    //Boolean
    Setting<Boolean> debug = this.register(new Setting("Debug", false));
    Setting<Boolean> twodelay = this.register(new Setting("2Delay", true));
    Setting<Boolean> advd = this.register(new Setting("AVD", false));
    Setting<Boolean> EnhancedRots = this.register(new Setting("EnchancedControl", false));
    Setting<Boolean> invert = this.register(new Setting("InvertedYaw", false));
    Setting<Boolean> SendRotPackets = this.register(new Setting("SendRotPackets", true));
    Setting<Boolean> twobeepvp = this.register(new Setting("2b2tpvp", true));
    Setting<Boolean> PUP = this.register(new Setting("PUP", true));
    //Integer
    Setting<Integer> tickDelay = this.register(new Setting("TickDelay", 2, 0, 40));
    Setting<Integer> EnhancedRotsAmount = this.register(new Setting("EnhancedCtrlSpeed", 2, 0, 20));
    //Double
    Setting<Double> speed = this.register(new Setting("Speed", 6.25, 0.0, 6.25));
    //Mode
    Setting<Mode> cmode = this.register(new Setting("ControlMode", Mode.Rel));

    @EventHandler
    private Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook pak = (SPacketPlayerPosLook) event.getPacket();
            pak.yaw = mc.player.rotationYaw;
            pak.pitch = mc.player.rotationPitch;
        }
        if(event.getPacket() instanceof SPacketPlayerPosLook){
            SPacketPlayerPosLook pak = (SPacketPlayerPosLook) event.getPacket();

            double dx = Math.abs(pak.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X) ? pak.x : mc.player.posX-pak.x);
            double dy = Math.abs(pak.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y) ? pak.y : mc.player.posY-pak.y);
            double dz = Math.abs(pak.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z) ? pak.z : mc.player.posZ-pak.z);

            if (dx<1E-3) dx=0;
            if (dz<1E-3) dz=0;

            if (!(dx==0 && dy==0 && dz==0) && debug.getValue()) mc.player.sendMessage(new TextComponentString("position pak, dx="+dx+" dy="+dy+" dz="+dz));

            if (pak.yaw!=mc.player.rotationYaw || pak.pitch!=mc.player.rotationPitch) {
                if (SendRotPackets.getValue()) mc.getConnection().sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw,mc.player.rotationPitch,mc.player.onGround));
                pak.yaw = mc.player.rotationYaw;
                pak.pitch = mc.player.rotationPitch;
            }
        }
    });

    KeyBinding left,right,down,up;
    long last = 0;

    @Override
    public void onUpdate() {
        try {
            mc.player.noClip = true;
            if (tickDelay.getValue() > 0)
                if (mc.player.ticksExisted % tickDelay.getValue() != 0 && twodelay.getValue()) return;

            int eca = EnhancedRotsAmount.getValue();

            if (EnhancedRots.getValue() && up.isKeyDown()) mc.player.rotationPitch -= eca;
            if (EnhancedRots.getValue() && down.isKeyDown()) mc.player.rotationPitch += eca;

            if (EnhancedRots.getValue() && left.isKeyDown()) mc.player.rotationYaw -= eca;
            if (EnhancedRots.getValue() && right.isKeyDown()) mc.player.rotationYaw += eca;

            double yaw = (mc.player.rotationYaw + 90) * (invert.getValue() ? -1 : 1);

            double xDir, zDir;

            if (cmode.getValue().equals("Rel")) {
                double dO_numer = 0;
                double dO_denom = 0;

                if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                    dO_numer -= 90;
                    dO_denom++;
                }
                if (mc.gameSettings.keyBindRight.isKeyDown()) {
                    dO_numer += 90;
                    dO_denom++;
                }
                if (mc.gameSettings.keyBindBack.isKeyDown()) {
                    dO_numer += 180;
                    dO_denom++;
                }
                if (mc.gameSettings.keyBindForward.isKeyDown()) {
                    dO_denom++;
                }

                if (dO_denom > 0) yaw += (dO_numer / dO_denom) % 361; // calculate average yaw
                // lets not divide by zero thats bad :)

                if (yaw < 0) yaw = 360 - yaw;
                if (yaw > 360) yaw = yaw % 361;

                xDir = Math.cos(Math.toRadians(yaw));
                zDir = Math.sin(Math.toRadians(yaw));
            } else {
                // for absolute control/hell mode :)
                // W = +x
                // S = -x
                // A = -z
                // D = +z

                xDir = 0;
                zDir = 0;

                xDir += mc.gameSettings.keyBindForward.isKeyDown() ? 1 : 0;
                xDir -= mc.gameSettings.keyBindBack.isKeyDown() ? 1 : 0;

                zDir += mc.gameSettings.keyBindLeft.isKeyDown() ? 1 : 0;
                zDir -= mc.gameSettings.keyBindRight.isKeyDown() ? 1 : 0;
            }

            if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
                mc.player.motionX = xDir * (speed.getValue() / 100);
                mc.player.motionZ = zDir * (speed.getValue() / 100);
            }
            mc.player.motionY = 0;

            boolean yes = false;
            if (advd.getValue()) {
                if (last + 50 >= System.currentTimeMillis()) {
                    yes = false;
                } else {
                    last = System.currentTimeMillis();
                    yes = true;
                }
            }

            mc.player.noClip = true;
            if (yes)
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, mc.player.posY + (mc.player.posY < (twobeepvp.getValue() ? 1.1 : -0.98) ? (speed.getValue() / 100) : 0) + (mc.gameSettings.keyBindJump.isKeyDown() ? (speed.getValue() / 100) : 0) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (speed.getValue() / 100) : 0), mc.player.posZ + mc.player.motionZ, false)); // mc.player.rotationYaw, mc.player.rotationPitch, false));

            if (PUP.getValue()) {
                mc.player.noClip = true;
                mc.player.setLocationAndAngles(mc.player.posX + mc.player.motionX, mc.player.posY, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch);
            }

            mc.player.noClip = true;
            if (yes)
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, mc.player.posY - 42069, mc.player.posZ + mc.player.motionZ, true));   //, mc.player.rotationYaw , mc.player.rotationPitch, true));

            double dx=0,dy=0,dz=0;
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                dy=-0.0625D;
            }
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                dy= 0.0625D;
            }

            mc.player.setLocationAndAngles(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
            mc.getConnection().sendPacket(new CPacketPlayer.Position(
                    mc.player.posX, mc.player.posY, mc.player.posZ, false));
        } catch (Exception e) {
            disable();
        }
        return;
    }

    @Override
    public void onEnable(){
        Legacy.getEventManager().subscribe(this);
    }

    @Override
    public void onDisable() {
        if (mc.player!=null) mc.player.noClip=false;
        Legacy.getEventManager().unsubscribe(this);
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
}
