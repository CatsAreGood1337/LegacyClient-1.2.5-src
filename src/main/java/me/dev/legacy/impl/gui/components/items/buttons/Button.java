package me.dev.legacy.impl.gui.components.items.buttons;

import me.dev.legacy.Legacy;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.gui.components.Component;
import me.dev.legacy.impl.gui.components.items.Item;
import me.dev.legacy.modules.client.ClickGui;
import me.dev.legacy.api.util.Render.ColorUtil;
import me.dev.legacy.api.util.Render.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button
        extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color2 = ColorUtil.toARGB(ClickGui.getInstance().fontcolorr.getValue(), ClickGui.getInstance().fontcolorb.getValue(), ClickGui.getInstance().fontcolorb.getValue(), 255);

        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 20, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Legacy.colorManager.getColorWithAlpha(Legacy.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : Legacy.colorManager.getColorWithAlpha(Legacy.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515));
        Legacy.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float) LegacyGui.getClickGui().getTextOffset(), this.getState() ? -2 : color2);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : LegacyGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}

