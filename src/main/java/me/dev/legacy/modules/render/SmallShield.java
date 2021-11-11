package me.dev.legacy.modules.render;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import net.minecraft.client.renderer.ItemRenderer;

public class SmallShield extends Module {
    public SmallShield() {
        super("SmallShield", "Low Hands.", Module.Category.RENDER, true, false, false);
    }

    public Setting<Float> offhand = this.register(new Setting<Float>("Height", 0.7F, 0.1F, 1F));

    ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

    @Override
    public void onUpdate(){
        itemRenderer.equippedProgressOffHand = offhand.getValue();
    }
}
