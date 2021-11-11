package me.dev.legacy.api.manager;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.impl.gui.font.CustomFont;
import me.dev.legacy.modules.client.Font;
import me.dev.legacy.api.util.MathUtil;
import me.dev.legacy.api.util.Timer;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class TextManager
        extends AbstractModule {
    private final Timer idleTimer = new Timer();
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    private CustomFont customFont = new CustomFont(new java.awt.Font("Verdana", java.awt.Font.PLAIN, 17), true, false);
    private boolean idling;

    public TextManager() {
        this.updateResolution();
    }


    public void init(boolean startup) {
        Font cFont = Legacy.moduleManager.getModuleByClass(Font.class);
        try {
            this.setFontRenderer(new java.awt.Font(cFont.fontName.getValue(), cFont.fontStyle.getValue(), cFont.fontSize.getValue()), cFont.antiAlias.getValue(), cFont.fractionalMetrics.getValue());
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public float drawString(String text, float x, float y, int color, boolean shadow) {
        if (Legacy.moduleManager.isModuleEnabled(Font.getInstance().getName())) {
            if (shadow) {
                this.customFont.drawStringWithShadow(text, x, y, color);
            } else {
                this.customFont.drawString(text, x, y, color);
            }
            return x;
        }
        TextManager.mc.fontRenderer.drawString(text, x, y, color, shadow);
        return x;
    }
    public void drawRainbowString(String text, float x, float y, int startColor, float factor, boolean shadow) {
        Color currentColor = new Color(startColor);
        float hueIncrement = 1.0f / factor;
        String[] rainbowStrings = text.split("\u00a7.");
        float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[0];
        float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[1];
        float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[2];
        int currentWidth = 0;
        boolean shouldRainbow = true;
        boolean shouldContinue = false;
        for (int i = 0; i < text.length(); ++i) {
            char currentChar = text.charAt(i);
            char nextChar = text.charAt(MathUtil.clamp(i + 1, 0, text.length() - 1));
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                shouldRainbow = false;
            } else if ((String.valueOf(currentChar) + nextChar).equals("\u00a7+")) {
                shouldRainbow = true;
            }
            if (shouldContinue) {
                shouldContinue = false;
                continue;
            }
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                String escapeString = text.substring(i);
                this.drawString(escapeString, x + (float) currentWidth, y, Color.WHITE.getRGB(), shadow);
                break;
            }
            this.drawString(String.valueOf(currentChar).equals("\u00a7") ? "" : String.valueOf(currentChar), x + (float) currentWidth, y, shouldRainbow ? currentColor.getRGB() : Color.WHITE.getRGB(), shadow);
            if (String.valueOf(currentChar).equals("\u00a7")) {
                shouldContinue = true;
            }
            currentWidth += this.getStringWidth(String.valueOf(currentChar));
            if (String.valueOf(currentChar).equals(" ")) continue;
            currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
            currentHue += hueIncrement;
        }
    }

    public int getStringWidth(String text) {
        if (Legacy.moduleManager.isModuleEnabled(Font.getInstance().getName())) {
            return this.customFont.getStringWidth(text);
        }
        return TextManager.mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        if (Legacy.moduleManager.isModuleEnabled(Font.getInstance().getName())) {
            String text = "A";
            return this.customFont.getStringHeight(text);
        }
        return TextManager.mc.fontRenderer.FONT_HEIGHT;
    }

    public void setFontRenderer(java.awt.Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public java.awt.Font getCurrentFont() {
        return this.customFont.getFont();
    }

    public void updateResolution() {
        this.scaledWidth = TextManager.mc.displayWidth;
        this.scaledHeight = TextManager.mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = TextManager.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = this.scaledWidth / this.scaleFactor;
        double scaledHeightD = this.scaledHeight / this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public String getIdleSign() {
        if (this.idleTimer.passedMs(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }
        if (this.idling) {
            return "_";
        }
        return "";
    }
}