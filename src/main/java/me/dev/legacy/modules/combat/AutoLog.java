package me.dev.legacy.modules.combat;

import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.text.TextComponentString;

public class AutoLog extends Module {
    public AutoLog() {
        super("AutoLog", "Automatically logs on combat.", Module.Category.COMBAT, true, false, false);
    }

    public final Setting<Boolean> packetKick = this.register(new Setting<Boolean>("Packet Kick", false));
    public final Setting<Boolean> fakeKick = this.register(new Setting<Boolean>("Fake Kick", false));
    public final Setting<Integer> Health = this.register(new Setting<Integer>("Health",6, 1, 20));

    public void onTick() {
        if (mc.player == null || mc.world == null || mc.player.capabilities.isCreativeMode) {
            return;
        }

        float health = mc.player.getHealth();

        if (health <= Health.getValue() && health != 0f && !mc.player.isDead) {
            this.doLog();
            this.toggle();
        }
    }

    public void doLog() {
        if (packetKick.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 50, mc.player.posZ, false));
        }

        if (fakeKick.getValue()) {
            mc.player.connection.getNetworkManager().closeChannel(new TextComponentString("Internal Exception: java.lang.NullPointerException"));
            return;
        }

        mc.player.connection.getNetworkManager().closeChannel(new TextComponentString("Auto Log!"));
    }
}
