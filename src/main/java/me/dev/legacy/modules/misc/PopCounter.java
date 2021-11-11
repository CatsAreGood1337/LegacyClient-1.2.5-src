package me.dev.legacy.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class PopCounter extends Module {
    public static HashMap<String, Integer> TotemPopContainer = new HashMap();
    public static PopCounter INSTANCE = new PopCounter();

    public PopCounter() {
        super("PopCounter", "Counts other players totem pops.", Module.Category.MISC, true, false, false);
        setInstance();
    }

    public static PopCounter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopCounter();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        TotemPopContainer.clear();
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopContainer.containsKey(player.getName())) {
            int l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.remove(player.getName());
            if (l_Count == 1) {
                Command.sendMessage(ChatFormatting.WHITE + player.getName() + ChatFormatting.LIGHT_PURPLE + " died after popping " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totem " + ChatFormatting.BOLD);
            } else {
                Command.sendMessage(ChatFormatting.WHITE + player.getName() + ChatFormatting.LIGHT_PURPLE + " died after popping " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totems " + ChatFormatting.BOLD);
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (PopCounter.fullNullCheck()) {
            return;
        }
        if (PopCounter.mc.player.equals(player)) {
            return;
        }
        int l_Count = 1;
        if (TotemPopContainer.containsKey(player.getName())) {
            l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.put(player.getName(), ++l_Count);
        } else {
            TotemPopContainer.put(player.getName(), l_Count);
        }
        if (l_Count == 1) {
            Command.sendMessage(ChatFormatting.WHITE + player.getName() + ChatFormatting.LIGHT_PURPLE + " just popped " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totem " + ChatFormatting.BOLD);
        } else {
            Command.sendMessage(ChatFormatting.WHITE + player.getName() + ChatFormatting.LIGHT_PURPLE + " just popped " + ChatFormatting.WHITE + l_Count + ChatFormatting.LIGHT_PURPLE + " totems " + ChatFormatting.BOLD);
        }
    }
}