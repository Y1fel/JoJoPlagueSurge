package com.Y1fel.JoJoPlagueSurge.mixin;

import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V",
            at = @At("HEAD"), cancellable = true)
    private void jojoplaguesurge$updateStandPassengerPosition(Entity passenger, Entity.MoveFunction moveFunction, CallbackInfo ci) {
        Entity vehicle = (Entity) (Object) this;
        if (!(passenger instanceof StandEntity stand) || !(vehicle instanceof Player player)) {
            return;
        }

        Vec3 desiredPos = stand.calculateBackStandPos(player);
        moveFunction.accept(passenger, desiredPos.x, desiredPos.y, desiredPos.z);
        ci.cancel();
    }
}
