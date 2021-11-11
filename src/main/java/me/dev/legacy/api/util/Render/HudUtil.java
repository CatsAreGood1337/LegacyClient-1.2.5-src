package me.dev.legacy.api.util.Render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;

import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.api.util.TimeUtil;
import me.dev.legacy.api.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HudUtil implements Util {

    public static void drawHudString(String string, int x, int y, int colour) {
        mc.fontRenderer.drawStringWithShadow(string, x, y, colour);
    }

    public static int getHudStringWidth(String string) {

        return mc.fontRenderer.getStringWidth(string);
    }


    public static int getHudStringHeight(String string) {

        return mc.fontRenderer.FONT_HEIGHT;
    }


    public static String getPingLine() {
        String line = "";
        int ping = Legacy.serverManager.getPing();
        if (ping > 150) {
            line += ChatFormatting.RED;
        } else if (ping > 100) {
            line += ChatFormatting.YELLOW;
        } else {
            line += ChatFormatting.GREEN;
        }
        return line + " " + ping;
    }

    public static String getTpsLine() {
        String line = "";
        double tps = MathUtil.round(Legacy.serverManager.getTPS(), 1);
        if (tps > 16) {
            line += ChatFormatting.GREEN;
        } else if (tps > 10) {
            line += ChatFormatting.YELLOW;
        } else {
            line += ChatFormatting.RED;
        }
        return line + " " + tps;
    }

    public static String getFpsLine() {
        String line = "";
        int fps = Minecraft.getDebugFPS();
        if (fps > 120) {
            line += ChatFormatting.GREEN;
        } else if (fps > 60) {
            line += ChatFormatting.YELLOW;
        } else {
            line += ChatFormatting.RED;
        }
        return line + " " + fps;
    }

    public static String getAnaTimeLine() {
        String line = "";
        line += TimeUtil.get_hour() < 10 ? "0" + TimeUtil.get_hour() : TimeUtil.get_hour();
        line += ":";
        line += TimeUtil.get_minuite() < 10 ? "0" + TimeUtil.get_minuite() : TimeUtil.get_minuite();
        line += ":";
        line += TimeUtil.get_second() < 10 ? "0" + TimeUtil.get_second() : TimeUtil.get_second();
        return line;
    }
}