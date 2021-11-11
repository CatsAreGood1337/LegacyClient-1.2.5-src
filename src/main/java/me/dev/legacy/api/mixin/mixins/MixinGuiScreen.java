package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.modules.misc.ShulkerViewer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiScreen.class})
public class MixinGuiScreen
        extends Gui {
    @Inject(method = {"renderToolTip"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (ShulkerViewer.getInstance().isOn() && stack.getItem() instanceof ItemShulkerBox) {
            ShulkerViewer.getInstance().renderShulkerToolTip(stack, x, y, null);
            info.cancel();
        }
    }
}

