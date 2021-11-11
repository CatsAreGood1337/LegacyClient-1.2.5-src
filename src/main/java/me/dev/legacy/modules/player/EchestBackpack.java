package me.dev.legacy.modules.player;

import me.dev.legacy.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;

public class EchestBackpack extends Module {
    private GuiScreen echestScreen = null;

    public EchestBackpack() {
        super("EchestBackpack", "Allows to open your echest later.", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onUpdate() {
        InventoryBasic basic;
        Container container;
        if (EchestBackpack.mc.currentScreen instanceof GuiContainer && (container = ((GuiContainer) EchestBackpack.mc.currentScreen).inventorySlots) instanceof ContainerChest && ((ContainerChest) container).getLowerChestInventory() instanceof InventoryBasic && (basic = (InventoryBasic) ((ContainerChest) container).getLowerChestInventory()).getName().equalsIgnoreCase("Ender Chest")) {
            this.echestScreen = EchestBackpack.mc.currentScreen;
            EchestBackpack.mc.currentScreen = null;
        }
    }

    @Override
    public void onDisable() {
        if (!EchestBackpack.fullNullCheck() && this.echestScreen != null) {
            mc.displayGuiScreen(this.echestScreen);
        }
        this.echestScreen = null;
    }
}
