package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.MinecraftInstance;
import me.dev.legacy.modules.render.Chams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(RenderLivingBase.class)
public class MixinRenderLivingBase <T extends EntityLivingBase> extends Render implements MinecraftInstance {

    @Shadow
    protected ModelBase mainModel;

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    public void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        if (entitylivingbaseIn instanceof EntityPlayer && entitylivingbaseIn != mc.player && Chams.INSTANCE.isEnabled() && Chams.INSTANCE.Pchams.getValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glClear(1024);
            GL11.glClearStencil(15);
            GL11.glStencilFunc(512, 1, 15);
            GL11.glStencilOp(7681, 7681, 7681);
            GL11.glPolygonMode(1028, 6913);
            GL11.glStencilFunc(512, 0, 15);
            GL11.glStencilOp(7681, 7681, 7681);
            GL11.glPolygonMode(1028, 6914);
            GL11.glStencilFunc(514, 1, 15);
            GL11.glStencilOp(7680, 7680, 7680);
            GL11.glPolygonMode(1028, 6913);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            GL11.glColor4f(1, 0, 0, 1);
            GL11.glColor4d(((float) Chams.INSTANCE.invisibleRed.getValue() / 255), ((float) Chams.INSTANCE.invisibleGreen.getValue() / 255), ((float) Chams.INSTANCE.invisibleBlue.getValue() / 255), ((float) Chams.INSTANCE.Palpha.getValue() / 255));
            mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glColor4d(((float) Chams.INSTANCE.visibleRed.getValue() / 255), ((float) Chams.INSTANCE.visibleGreen.getValue() / 255), ((float) Chams.INSTANCE.visibleBlue.getValue() / 255), ((float) Chams.INSTANCE.Palpha.getValue() / 255));
            mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            ci.cancel();
        }
    }

    @Inject(method = "renderLayers", at = @At("RETURN"))
    public void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {

        if (entitylivingbaseIn instanceof EntityPlayer && Chams.INSTANCE.isEnabled() && Chams.INSTANCE.Pwireframe.getValue()) {
            Chams.INSTANCE.onRenderModelPlayer(mainModel, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
        }
    }

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

}
