package me.dev.legacy.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BurrowAlert extends Module {

    private final ConcurrentHashMap<EntityPlayer, Integer> players = new ConcurrentHashMap<>();
    List<EntityPlayer> anti_spam = new ArrayList<>();
    List<Entity> burrowedPlayers = new ArrayList<>();

    public BurrowAlert() {
        super("BurrowAlert", "Burrow Alert", Category.MISC, true, false, false);
    }


    @Override
    public void onEnable() {
        players.clear();
        anti_spam.clear();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (Entity entity : mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList())) {
            if (!(entity instanceof EntityPlayer)) {
                continue;
            }
            if (!burrowedPlayers.contains(entity) && isBurrowed(entity)) {
                burrowedPlayers.add(entity);
                Command.sendMessage(ChatFormatting.RED + entity.getName() + " has just burrowed!");
            } else if (burrowedPlayers.contains(entity) && !isBurrowed(entity)) {
                burrowedPlayers.remove(entity);
                Command.sendMessage(ChatFormatting.GREEN + entity.getName() + " is no longer burrowed!");
            }
        }
    }

    private boolean isBurrowed(Entity entity) {
        BlockPos entityPos = new BlockPos(roundValueToCenter(entity.posX), entity.posY + .2, roundValueToCenter(entity.posZ));

        if (mc.world.getBlockState(entityPos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(entityPos).getBlock() == Blocks.ENDER_CHEST) {
            return true;
        }

        return false;
    }

    private double roundValueToCenter(double inputVal) {
        double roundVal = Math.round(inputVal);

        if (roundVal > inputVal) {
            roundVal -= 0.5;
        } else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }

        return roundVal;
    }
}