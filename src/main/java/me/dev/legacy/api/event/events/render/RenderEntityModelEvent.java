package me.dev.legacy.api.event.events.render;

import me.dev.legacy.api.event.EventStage;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class RenderEntityModelEvent extends EventStage {
    public ModelBase modelBase;
    public Entity entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float age;
    public float headYaw;
    public float headPitch;
    public float scale;

    public RenderEntityModelEvent(final int stage, final ModelBase modelBase, final Entity entity, final float limbSwing, final float limbSwingAmount, final float age, final float headYaw, final float headPitch, final float scale) {
        super(stage);
        this.modelBase = modelBase;
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.age = age;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }
}
