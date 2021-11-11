package me.dev.legacy.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class Wrapper {

    private static FontRenderer fontRenderer;

    public static Minecraft mc = Minecraft.getMinecraft();

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static Minecraft GetMC()
    {
        return mc;
    }

    public static EntityPlayerSP GetPlayer()
    {
        return mc.player;
    }

    public static EntityPlayerSP getPlayer() {
        return getMinecraft().player;
    }

    public static Entity getRenderEntity() {
        return Wrapper.mc.getRenderViewEntity();
    }

    public static World getWorld() {
        return getMinecraft().world;
    }

    public static int getKey(String keyname) {
        return Keyboard.getKeyIndex(keyname.toUpperCase());
    }

    public static FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
