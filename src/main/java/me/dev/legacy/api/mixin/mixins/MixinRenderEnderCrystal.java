package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.api.event.events.render.RenderEntityModelEvent;
import me.dev.legacy.modules.client.ClickGui;
import me.dev.legacy.modules.render.Chams;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.api.util.Render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(value = {RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    private static final ResourceLocation glint;

    @Redirect(method = {"doRender"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (Chams.INSTANCE.isEnabled()) {
            if (Chams.INSTANCE.animateScale.getValue().booleanValue() && Chams.INSTANCE.scaleMap.containsKey((EntityEnderCrystal) entity)) {
                GlStateManager.scale(Chams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity).floatValue(), Chams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity).floatValue(), Chams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity).floatValue());
            } else {
                GlStateManager.scale(Chams.INSTANCE.scale.getValue().floatValue(), Chams.INSTANCE.scale.getValue().floatValue(), Chams.INSTANCE.scale.getValue().floatValue());
            }
        }
        if (Chams.INSTANCE.isEnabled() && Chams.INSTANCE.wireframe.getValue().booleanValue()) {
            RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            Chams.INSTANCE.onRenderModel(event);
        }
        if (Chams.INSTANCE.isEnabled() && Chams.INSTANCE.chams.getValue().booleanValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            if (Chams.INSTANCE.rainbow.getValue().booleanValue()) {
                Color rainbowColor1 = Chams.INSTANCE.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(RenderUtil.getRainbow(Chams.INSTANCE.speed.getValue() * 100, 0, (float) Chams.INSTANCE.saturation.getValue().intValue() / 100.0f, (float) Chams.INSTANCE.brightness.getValue().intValue() / 100.0f));
                Color rainbowColor = EntityUtil.getColor(entity, rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue(), Chams.INSTANCE.alpha.getValue(), true);
                if (Chams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f((float) rainbowColor.getRed() / 255.0f, (float) rainbowColor.getGreen() / 255.0f, (float) rainbowColor.getBlue() / 255.0f, (float) Chams.INSTANCE.alpha.getValue().intValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (Chams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            } else if (Chams.INSTANCE.xqz.getValue().booleanValue() && Chams.INSTANCE.throughWalls.getValue().booleanValue()) {
                Color visibleColor;
                Color hiddenColor = Chams.INSTANCE.colorSync.getValue() != false ? EntityUtil.getColor(entity, Chams.INSTANCE.hiddenRed.getValue(), Chams.INSTANCE.hiddenGreen.getValue(), Chams.INSTANCE.hiddenBlue.getValue(), Chams.INSTANCE.hiddenAlpha.getValue(), true) : EntityUtil.getColor(entity, Chams.INSTANCE.hiddenRed.getValue(), Chams.INSTANCE.hiddenGreen.getValue(), Chams.INSTANCE.hiddenBlue.getValue(), Chams.INSTANCE.hiddenAlpha.getValue(), true);
                Color color = visibleColor = Chams.INSTANCE.colorSync.getValue() != false ? EntityUtil.getColor(entity, Chams.INSTANCE.red.getValue(), Chams.INSTANCE.green.getValue(), Chams.INSTANCE.blue.getValue(), Chams.INSTANCE.alpha.getValue(), true) : EntityUtil.getColor(entity, Chams.INSTANCE.red.getValue(), Chams.INSTANCE.green.getValue(), Chams.INSTANCE.blue.getValue(), Chams.INSTANCE.alpha.getValue(), true);
                if (Chams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f((float) hiddenColor.getRed() / 255.0f, (float) hiddenColor.getGreen() / 255.0f, (float) hiddenColor.getBlue() / 255.0f, (float) Chams.INSTANCE.alpha.getValue().intValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (Chams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
                GL11.glColor4f((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, (float) Chams.INSTANCE.alpha.getValue().intValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
                Color visibleColor;
                Color color = visibleColor = Chams.INSTANCE.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : EntityUtil.getColor(entity, Chams.INSTANCE.red.getValue(), Chams.INSTANCE.green.getValue(), Chams.INSTANCE.blue.getValue(), Chams.INSTANCE.alpha.getValue(), true);
                if (Chams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f((float) visibleColor.getRed() / 255.0f, (float) visibleColor.getGreen() / 255.0f, (float) visibleColor.getBlue() / 255.0f, (float) Chams.INSTANCE.alpha.getValue().intValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (Chams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            if (Chams.INSTANCE.glint.getValue().booleanValue()) {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0f, 0.0f, 0.0f, 0.13f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
            }
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (Chams.INSTANCE.isEnabled()) {
            if (Chams.INSTANCE.animateScale.getValue().booleanValue() && Chams.INSTANCE.scaleMap.containsKey((EntityEnderCrystal) entity)) {
                GlStateManager.scale(1.0f / Chams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity).floatValue(), 1.0f / Chams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity).floatValue(), 1.0f / Chams.INSTANCE.scaleMap.get((EntityEnderCrystal) entity).floatValue());
            } else {
                GlStateManager.scale(1.0f / Chams.INSTANCE.scale.getValue().floatValue(), 1.0f / Chams.INSTANCE.scale.getValue().floatValue(), 1.0f / Chams.INSTANCE.scale.getValue().floatValue());
            }
        }
    }

    static {
        glint = new ResourceLocation("textures/glint.png");
    }
}
