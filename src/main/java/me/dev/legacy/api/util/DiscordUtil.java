package me.dev.legacy.api.util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.dev.legacy.modules.client.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordUtil {
    public static DiscordRichPresence presence;
    private static final DiscordRPC rpc;
    private static Thread thread;
    private static int index;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("847105722956513312", handlers, true, "");
        DiscordUtil.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordUtil.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "Main menu" : "Playing " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().currentServerData.serverIP + "." : "multiplayer") : "singleplayer");
        DiscordUtil.presence.state = "Donbass activity";
        DiscordUtil.presence.largeImageKey = "legacy-1";
        DiscordUtil.presence.largeImageText = "v1.2.5";
        DiscordUtil.presence.smallImageKey = "";
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                DiscordUtil.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "Main menu" : "Playing " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().currentServerData.serverIP + "." : "multiplayer") : "singleplayer");
                DiscordUtil.presence.state = "Eating kids";
                if(RPC.INSTANCE.users.getValue()) {
                    if (index == 6) {
                        index = 1;
                    }
                    DiscordUtil.presence.smallImageKey = "user" + index;
                    ++index;
                    if (index == 2) {
                        DiscordUtil.presence.smallImageText = "BlackBro4";
                    }
                    if (index == 3) {
                        DiscordUtil.presence.smallImageText = "rianix";
                    }
                    if (index == 4) {
                        DiscordUtil.presence.smallImageText = "Sudmarin";
                    }
                    if (index == 5) {
                        DiscordUtil.presence.smallImageText = "Ziasan";
                    }
                    if (index == 6) {
                        DiscordUtil.presence.smallImageText = "end41r";
                    }
                }
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }

    static {
        index = 1;
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }
}
