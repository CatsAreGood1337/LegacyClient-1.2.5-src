package me.dev.legacy.modules.misc;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketChunkData;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Iterator;
import java.io.File;

public class StashLogger extends Module
{
    private final Setting<Boolean> chests = this.register(new Setting("Chests", true));
    private final Setting<Integer> chestsValue = this.register(new Setting("ChestsValue", 4, 1, 30, v -> this.chests.getValue()));
    private final Setting<Boolean> Shulkers = this.register(new Setting("Shulkers", true));
    private final Setting<Integer> shulkersValue = this.register(new Setting("ShulkersValue", 4, 1, 30, v -> this.Shulkers.getValue()));
    private final Setting<Boolean> writeToFile = this.register(new Setting("CoordsSaver", true));;
    File mainFolder;
    final Iterator<NBTTagCompound> iterator;

    public StashLogger() {
        super("StashLogger", "Logs stashes", Category.MISC, true, false, false);
        this.mainFolder = new File(Minecraft.getMinecraft().gameDir + File.separator + "legacy");
        this.iterator = null;
    }

    @SubscribeEvent
    public void onPacket(final PacketEvent event) {
        if (nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketChunkData) {
            final SPacketChunkData l_Packet = event.getPacket();
            int l_ChestsCount = 0;
            int shulkers = 0;
            for (final NBTTagCompound l_Tag : l_Packet.getTileEntityTags()) {
                final String l_Id = l_Tag.getString("id");
                if (l_Id.equals("minecraft:chest") && this.chests.getValue()) {
                    ++l_ChestsCount;
                }
                else {
                    if (!l_Id.equals("minecraft:shulker_box") || !this.Shulkers.getValue()) {
                        continue;
                    }
                    ++shulkers;
                }
            }
            if (l_ChestsCount >= this.chestsValue.getValue()) {
                this.SendMessage(String.format("%s chests located at X: %s, Z: %s", l_ChestsCount, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), true);
            }
            if (shulkers >= this.shulkersValue.getValue()) {
                this.SendMessage(String.format("%s shulker boxes at X: %s, Z: %s", shulkers, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), true);
            }
        }
    }

    private void SendMessage(final String message, final boolean save) {
        final String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toUpperCase() : StashLogger.mc.getCurrentServerData().serverIP;
        if (this.writeToFile.getValue() && save) {
            try {
                final FileWriter writer = new FileWriter(this.mainFolder + "/stashes.txt", true);
                writer.write("[" + server + "]: " + message + "\n");
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        StashLogger.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
        Command.sendMessage(ChatFormatting.GREEN + message);
    }
}
