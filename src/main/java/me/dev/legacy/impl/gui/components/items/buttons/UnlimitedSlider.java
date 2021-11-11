package me.dev.legacy.impl.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.modules.client.ClickGui;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.Render.ColorUtil;
import me.dev.legacy.api.util.Render.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class UnlimitedSlider
        extends Button {
    public Setting setting;

    public UnlimitedSlider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color2 = ColorUtil.toARGB(ClickGui.getInstance().fontcolorr.getValue(), ClickGui.getInstance().fontcolorb.getValue(), ClickGui.getInstance().fontcolorb.getValue(), 255);

        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f + 20, this.y + (float) this.height - 0.5f, !this.isHovering(mouseX, mouseY) ? Legacy.colorManager.getColorWithAlpha(Legacy.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : Legacy.colorManager.getColorWithAlpha(Legacy.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()));
        Legacy.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue() + ChatFormatting.WHITE + " +", this.x + 2.3f, this.y - 1.7f - (float) LegacyGui.getClickGui().getTextOffset(), this.getState() ? -2 : color2);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.isRight(mouseX)) {
                if (this.setting.getValue() instanceof Double) {
                    this.setting.setValue((Double) this.setting.getValue() + 1.0);
                } else if (this.setting.getValue() instanceof Float) {
                    this.setting.setValue(Float.valueOf(((Float) this.setting.getValue()).floatValue() + 1.0f));
                } else if (this.setting.getValue() instanceof Integer) {
                    this.setting.setValue((Integer) this.setting.getValue() + 1);
                }
            } else if (this.setting.getValue() instanceof Double) {
                this.setting.setValue((Double) this.setting.getValue() - 1.0);
            } else if (this.setting.getValue() instanceof Float) {
                this.setting.setValue(Float.valueOf(((Float) this.setting.getValue()).floatValue() - 1.0f));
            } else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue((Integer) this.setting.getValue() - 1);
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
    }

    @Override
    public boolean getState() {
        return true;
    }

    public boolean isRight(int x) {
        return (float) x > this.x + ((float) this.width + 7.4f) / 2.0f;
    }
}

