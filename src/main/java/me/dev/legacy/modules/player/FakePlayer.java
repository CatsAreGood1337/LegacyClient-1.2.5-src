package me.dev.legacy.modules.player;

import com.mojang.authlib.GameProfile;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakePlayer extends Module {
    public FakePlayer() {
        super("FakePlayer", "Spawns a literal fake player", Module.Category.PLAYER, false, false, false);
        this.setInstance();
    }

    public static FakePlayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakePlayer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public Setting<String> fakename = register(new Setting("Name", "FakePlayer"));

    public List<Integer> fakePlayerIdList = new ArrayList<Integer>();
    private static FakePlayer INSTANCE = new FakePlayer();
    private EntityOtherPlayerMP clonedPlayer;

    public void onEnable() {
        Command.sendMessage("FakePlayer by the name of " + fakename.getValueAsString() + " has been spawned!");
        if (mc.player == null || mc.player.isDead) {
            disable();
            return;
        }

        clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("48efc40f-56bf-42c3-aa24-28e0c053f325"), fakename.getValueAsString()));
        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.world.addEntityToWorld(-12345, clonedPlayer);
        clonedPlayer.onLivingUpdate();
    }

    public void onDisable() {
        if (mc.world != null) {
            mc.world.removeEntityFromWorld(-12345);
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (isEnabled()){
            disable();
        }
    }

}

