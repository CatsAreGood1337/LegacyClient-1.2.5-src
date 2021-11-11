package me.dev.legacy.modules.client;

import me.dev.legacy.api.util.DiscordUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class RPC
        extends Module {
    public static RPC INSTANCE;
    public Setting<Boolean> showIP = this.register(new Setting("IP", false));
    public Setting<Boolean> users = this.register(new Setting("Users", false));

    public RPC() {
        super("RPC", "Discord rich presence", Category.CLIENT, false, false, false);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        DiscordUtil.start();
    }

    @Override
    public void onDisable() {
        DiscordUtil.stop();
    }
}

