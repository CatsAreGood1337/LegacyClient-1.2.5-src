package me.dev.legacy.api.mixin.mixins;

import me.dev.legacy.api.event.events.chat.ChatEvent;
import me.dev.legacy.api.event.events.move.MoveEvent;
import me.dev.legacy.api.event.events.move.PushEvent;
import me.dev.legacy.api.event.events.move.UpdateWalkingPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.fml.common.eventhandler.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = {EntityPlayerSP.class}, priority = 9998)
public abstract class MixinEntityPlayerSP
        extends AbstractClientPlayer {
    public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_) {
        super(p_i47378_2_, p_i47378_3_.getGameProfile());
    }

    @Inject(method = {"sendChatMessage"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void sendChatMessage(String message, CallbackInfo callback) {
        ChatEvent chatEvent = new ChatEvent(message);
        MinecraftForge.EVENT_BUS.post(chatEvent);
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "HEAD")})
    private void preMotion(CallbackInfo info) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(0);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At(value = "RETURN")})
    private void postMotion(CallbackInfo info) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(final EntityPlayerSP entityPlayerSP, final boolean sprinting) {
        entityPlayerSP.setSprinting(sprinting);
    }

    @Inject(method = { "pushOutOfBlocks" }, at = { @At("HEAD") }, cancellable = true)
    private void pushOutOfBlocksHook(final double x, final double y, final double z, final CallbackInfoReturnable<Boolean> info) {
        final PushEvent event = new PushEvent(1);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            info.setReturnValue(false);
        }
    }

    @Redirect(method = { "onUpdateWalkingPlayer" }, at = @At(value = "FIELD", target = "net/minecraft/util/math/AxisAlignedBB.minY:D"))
    private double minYHook(final AxisAlignedBB bb) {
        return bb.minY;
    }

    @Inject(method = { "move" }, at = { @At("HEAD") }, cancellable = true)
    public void move(final MoverType type, final double x, final double y, final double z, final CallbackInfo ci) {
        final MoveEvent event = new MoveEvent(0, type, x, y, z);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.getX() != x || event.getY() != y || event.getZ() != z) {
            super.move(type, event.getX(), event.getY(), event.getZ());
            ci.cancel();
        }
    }

}

