package me.dev.legacy.impl.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.modules.client.ClickGui;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.impl.setting.Bind;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.Render.ColorUtil;
import me.dev.legacy.api.util.Render.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton
        extends Button {
    private final Setting setting;
    public boolean isListening;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int color2 = ColorUtil.toARGB(ClickGui.getInstance().fontcolorr.getValue(), ClickGui.getInstance().fontcolorb.getValue(), ClickGui.getInstance().fontcolorb.getValue(), 255);

        int color = ColorUtil.toARGB(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue(), 255);
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f + 20, this.y + (float) this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515) : (!this.isHovering(mouseX, mouseY) ? Legacy.colorManager.getColorWithAlpha(Legacy.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) : Legacy.colorManager.getColorWithAlpha(Legacy.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue())));
        if (this.isListening) {
            Legacy.textManager.drawStringWithShadow("Press a Key...", this.x + 2.3f, this.y - 1.7f - (float) LegacyGui.getClickGui().getTextOffset(), color2 );
        } else {
            Legacy.textManager.drawStringWithShadow(this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase(), this.x + 2.3f, this.y - 1.7f - (float) LegacyGui.getClickGui().getTextOffset(), this.getState() ? -1 : color2);
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }
}

