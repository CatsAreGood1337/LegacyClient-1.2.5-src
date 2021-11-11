package me.dev.legacy.modules.render;

import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;

public class CameraClip extends Module {

    private static CameraClip INSTANCE = new CameraClip();
    public Setting<Boolean> extend = this.register(new Setting("Extend", false));
    public Setting<Double> distance = this.register(new Setting("Distance", 10.0, 0.0, 50.0, v -> this.extend.getValue()));

    public CameraClip () {
        super("ViewClip", "b", Category.RENDER, true, false, false);
    }

    public static CameraClip getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CameraClip();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
