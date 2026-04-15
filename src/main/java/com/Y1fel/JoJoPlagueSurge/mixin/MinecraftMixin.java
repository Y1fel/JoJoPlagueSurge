package com.Y1fel.JoJoPlagueSurge.mixin;

import com.Y1fel.JoJoPlagueSurge.effect.ModEffects;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager.CriminalDuVillagerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    private static final double SPIRIT_VISION_RADIUS = 256.0D;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void jojoplaguesurge$highlightCriminalsForSpiritVision(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof CriminalDuVillagerEntity criminal)) {
            return;
        }

        LocalPlayer localPlayer = this.player;
        if (localPlayer == null || !localPlayer.hasEffect(ModEffects.SPIRIT_VISION.get())) {
            return;
        }

        if (criminal.isAlive() && criminal.distanceToSqr(localPlayer) <= SPIRIT_VISION_RADIUS * SPIRIT_VISION_RADIUS) {
            cir.setReturnValue(true);
        }
    }
}
