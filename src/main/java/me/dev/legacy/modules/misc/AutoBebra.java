package me.dev.legacy.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.impl.command.Command;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AutoBebra extends Module {
    public AutoBebra() {
        super("AutoBebra", "nuhai bebry", Module.Category.MISC, true, false, false);
    }
    public Setting<Integer> time = register(new Setting<Integer>("Minutes", 1, 1, 5));
    @Override
    public void onEnable() {
        int minutes = time.getValue() ;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                ProcessBuilder processBuilder = new ProcessBuilder("shutdown",
                        "/s");
                try {
                    processBuilder.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }, minutes * 60 * 1000);

        Command.sendMessage(ChatFormatting.GREEN + "Bebpa bydet pronuxana 4epe3  " + minutes + " minutes");
        disable();
    }
}
