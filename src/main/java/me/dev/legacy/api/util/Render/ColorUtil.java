package me.dev.legacy.api.util.Render;

import me.dev.legacy.modules.client.ClickGui;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class ColorUtil
{


    public ColorUtil(final int i, final int i1, final int i2, final int i3) {
    }
    public static void color(int color) {
        GL11.glColor4f((float)((float)(color >> 16 & 0xFF) / 255.0f), (float)((float)(color >> 8 & 0xFF) / 255.0f), (float)((float)(color & 0xFF) / 255.0f), (float)((float)(color >> 24 & 0xFF) / 255.0f));
    }
    public static int shadeColour(int color, int precent) {
        int r = (((color & 0xFF0000) >> 16) * (100 + precent) / 100);
        int g = (((color & 0xFF00) >> 8) * (100 + precent) / 100);
        int b = ((color & 0xFF) * (100 + precent) / 100);
        return new Color(r,g,b).hashCode();
    }

    public static int getRainbow(int speed, float s) {
        float hue = (System.currentTimeMillis()) % speed;
        return (Color.getHSBColor(hue / speed, s, 1f).getRGB());
    }

    public static int getRainbow(int speed, int offset, float s) {
        float hue = (System.currentTimeMillis()) % speed + (offset * 15L);
        return (Color.getHSBColor(hue / speed, s, 1f).getRGB());
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorUtil.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return ColorUtil.toRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }

    public static Color rainbow(final int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), (float) ClickGui.getInstance().rainbowSaturation.getValue() / 255.0f, (float)ClickGui.getInstance().rainbowBrightness.getValue() / 255.0f);
    }

    public static int toRGBA(final float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int toRGBA(final double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
    }

    public static class Colors {
        public static final int WHITE = ColorUtil.toRGBA(255, 255, 255, 255);
        public static final int BLACK = ColorUtil.toRGBA(0, 0, 0, 255);
        public static final int RED = ColorUtil.toRGBA(255, 0, 0, 255);
        public static final int GREEN = ColorUtil.toRGBA(0, 255, 0, 255);
        public static final int BLUE = ColorUtil.toRGBA(0, 0, 255, 255);
        public static final int ORANGE = ColorUtil.toRGBA(255, 128, 0, 255);
        public static final int PURPLE = ColorUtil.toRGBA(163, 73, 163, 255);
        public static final int GRAY = ColorUtil.toRGBA(127, 127, 127, 255);
        public static final int DARK_RED = ColorUtil.toRGBA(64, 0, 0, 255);
        public static final int YELLOW = ColorUtil.toRGBA(255, 255, 0, 255);
        public static final int RAINBOW = Integer.MIN_VALUE;
    }

    public static int toRGBA(final Color color) {
        return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static class ColorName {
        public int r;
        public int g;
        public int b;
        public String name;

        public ColorName(String name, int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.name = name;
        }

        public int computeMSE(int pixR, int pixG, int pixB) {
            return ((pixR - this.r) * (pixR - this.r) + (pixG - this.g) * (pixG - this.g) + (pixB - this.b) * (pixB - this.b)) / 3;
        }

        public int getR() {
            return this.r;
        }

        public int getG() {
            return this.g;
        }

        public int getB() {
            return this.b;
        }

        public String getName() {
            return this.name;
        }
    }
}

