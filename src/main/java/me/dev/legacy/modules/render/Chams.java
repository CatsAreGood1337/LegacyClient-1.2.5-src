package me.dev.legacy.modules.render;

import me.dev.legacy.api.event.events.other.PacketEvent;
import me.dev.legacy.api.event.events.render.RenderEntityModelEvent;
import me.dev.legacy.api.util.EntityUtil;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.modules.Module;
import me.dev.legacy.modules.client.ClickGui;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Chams extends Module {

    public enum Settings {
        Crystal,
        Player
    }

    public Setting<Settings> setting = this.register(new Setting("Mode", Settings.Crystal));
    //Crystal
    public Setting<Boolean> animateScale = this.register(new Setting("AnimateScale", false, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Boolean> chams = this.register(new Setting("Chams", false, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Boolean> throughWalls = this.register(new Setting("ThroughWalls", true, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Boolean> wireframeThroughWalls = this.register(new Setting("WireThroughWalls", true, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Boolean> glint = this.register(new Setting("Glint", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.Crystal && this.chams.getValue()));
    public Setting<Boolean> wireframe = this.register(new Setting("Wireframe", false, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Float> scale = this.register(new Setting("Scale", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Float> lineWidth = this.register(new Setting("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f), v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Boolean> colorSync = this.register(new Setting("Sync", false, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Integer> saturation = this.register(new Setting("Saturation", 50, 0, 100, v -> this.setting.getValue() == Settings.Crystal && this.rainbow.getValue()));
    public Setting<Integer> brightness = this.register(new Setting("Brightness", 100, 0, 100, v -> this.setting.getValue() == Settings.Crystal && this.rainbow.getValue()));
    public Setting<Integer> speed = this.register(new Setting("Speed", 40, 1, 100, v -> this.rainbow.getValue()));
    public Setting<Boolean> xqz = this.register(new Setting("XQZ", false, v ->this.setting.getValue() == Settings.Crystal && this.rainbow.getValue() == false && this.throughWalls.getValue() != false));
    public Setting<Integer> red = this.register(new Setting("Red", 0, 0, 255, v -> this.setting.getValue() == Settings.Crystal && this.rainbow.getValue() == false));
    public Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255, v -> this.setting.getValue() == Settings.Crystal && this.rainbow.getValue() == false));
    public Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255, v -> this.setting.getValue() == Settings.Crystal && this.rainbow.getValue() == false));
    public Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255, v -> this.setting.getValue() == Settings.Crystal));
    public Setting<Integer> hiddenRed = this.register(new Setting("HiddenRed", 255, 0, 255, v -> this.setting.getValue() == Settings.Crystal && this.xqz.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> hiddenGreen = this.register(new Setting("HiddenGreen", 0, 0, 255, v -> this.setting.getValue() == Settings.Crystal && this.xqz.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> hiddenBlue = this.register(new Setting("HiddenBlue", 255, 0, 255, v -> this.setting.getValue() == Settings.Crystal && this.xqz.getValue() != false && this.rainbow.getValue() == false));
    public Setting<Integer> hiddenAlpha = this.register(new Setting("HiddenAlpha", 255, 0, 255, v -> this.setting.getValue() == Settings.Crystal && this.xqz.getValue() != false && this.rainbow.getValue() == false));
    //Player
    public final Setting<Boolean> Pchams = register(new Setting<>("Chams", false, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Boolean> Pwireframe = register(new Setting<>("Wireframe", false, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Float> PlineWidth = register(new Setting<>("Linewidth", 1f, 0.1f, 3f, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Integer> Palpha = register(new Setting<>("Alpha", 100, 0, 255, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Integer> visibleRed = register(new Setting<>("Red", 255, 0, 255, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Integer> visibleGreen = register(new Setting<>("Green", 255, 0, 255, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Integer> visibleBlue = register(new Setting<>("Blue", 255, 0, 255, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Integer> invisibleRed = register(new Setting<>("WallRed", 255, 0, 255, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Integer> invisibleGreen = register(new Setting<>("WallGreen", 255, 0, 255, v -> this.setting.getValue() == Settings.Player));
    public final Setting<Integer> invisibleBlue = register(new Setting<>("WallBlue", 255, 0, 255, v -> this.setting.getValue() == Settings.Player));

    public Map<EntityEnderCrystal, Float> scaleMap = new ConcurrentHashMap<EntityEnderCrystal, Float>();
    public static Chams INSTANCE;

    public Chams () {
        super("Chams", "lol u know what is", Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        for (Entity crystal : Chams.mc.world.loadedEntityList) {
            if (!(crystal instanceof EntityEnderCrystal)) continue;
            if (!this.scaleMap.containsKey(crystal)) {
                this.scaleMap.put((EntityEnderCrystal) crystal, Float.valueOf(3.125E-4f));
            } else {
                this.scaleMap.put((EntityEnderCrystal) crystal, Float.valueOf(this.scaleMap.get(crystal).floatValue() + 3.125E-4f));
            }
            if (!(this.scaleMap.get(crystal).floatValue() >= 0.0625f * this.scale.getValue().floatValue())) continue;
            this.scaleMap.remove(crystal);
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = event.getPacket();
            for (int id : packet.getEntityIDs()) {
                Entity entity = Chams.mc.world.getEntityByID(id);
                if (!(entity instanceof EntityEnderCrystal)) continue;
                this.scaleMap.remove(entity);
            }
        }
    }

    public void onRenderModel(RenderEntityModelEvent event) {
        if (event.getStage() != 0 || !(event.entity instanceof EntityEnderCrystal) || !this.wireframe.getValue().booleanValue()) {
            return;
        }
        Color color = this.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : EntityUtil.getColor(event.entity, this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue(), false);
        boolean fancyGraphics = Chams.mc.gameSettings.fancyGraphics;
        Chams.mc.gameSettings.fancyGraphics = false;
        float gamma = Chams.mc.gameSettings.gammaSetting;
        Chams.mc.gameSettings.gammaSetting = 10000.0f;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glPolygonMode(1032, 6913);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        if (this.wireframeThroughWalls.getValue().booleanValue()) {
            GL11.glDisable(2929);
        }
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GlStateManager.glLineWidth(this.lineWidth.getValue().floatValue());
        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public final void onRenderModelPlayer(ModelBase base, Entity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float Pscale) {
        if (entity instanceof EntityPlayer) {
            return;
        }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(PlineWidth.getValue());
        GL11.glDepthMask(false);
        GL11.glColor4d(((float) Chams.INSTANCE.invisibleRed.getValue() / 255), ((float) Chams.INSTANCE.invisibleGreen.getValue() / 255), ((float) Chams.INSTANCE.invisibleBlue.getValue() / 255), ((float) Chams.INSTANCE.Palpha.getValue() / 255));
        base.render(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, Pscale);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glColor4d(((float) Chams.INSTANCE.visibleRed.getValue() / 255), ((float) Chams.INSTANCE.visibleGreen.getValue() / 255), ((float) Chams.INSTANCE.visibleBlue.getValue() / 255), ((float) Chams.INSTANCE.Palpha.getValue() / 255));
        base.render(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, Pscale);
        GL11.glEnable(3042);
        GL11.glPopAttrib();
    }
}
