package me.dev.legacy.api.event;

import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.impl.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClientEvent
        extends EventStage {
    private AbstractModule feature;
    private Setting setting;

    public ClientEvent(int stage, AbstractModule feature) {
        super(stage);
        this.feature = feature;
    }

    public ClientEvent(Setting setting) {
        super(2);
        this.setting = setting;
    }

    public AbstractModule getFeature() {
        return this.feature;
    }

    public Setting getSetting() {
        return this.setting;
    }

    public Setting getProperty() {
        return this.setting;
    }
}

