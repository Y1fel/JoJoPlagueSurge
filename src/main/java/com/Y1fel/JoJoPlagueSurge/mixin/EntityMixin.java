package com.Y1fel.JoJoPlagueSurge.mixin;

import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Redirect(
            method = "rideTick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;positionRider(Lnet/minecraft/world/entity/Entity;)V"
            )
    )
    private void jojoplaguesurge$redirectStandPassengerPosition(Entity vehicle, Entity passenger) {
        if (!(passenger instanceof StandEntity stand) || !(vehicle instanceof Player player)) {
            vehicle.positionRider(passenger);
            return;
        }

        stand.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        stand.setPos(stand.calculateBackStandPos(player));
    }
}
