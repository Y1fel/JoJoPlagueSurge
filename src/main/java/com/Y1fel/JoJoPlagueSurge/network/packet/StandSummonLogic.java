package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StandSummonLogic {
    private StandSummonLogic() {
    }

    public static void toggleDuWangStand(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        List<DuWangEntity> owned = level.getEntitiesOfClass(
                DuWangEntity.class,
                player.getBoundingBox().inflate(64.0D),
                stand -> stand.isOwnedBy(player)
        );

        if (!owned.isEmpty()) {
            owned.forEach(DuWangEntity::discard);
            player.displayClientMessage(Component.literal("替身收回"), true);
            return;
        }

        DuWangEntity stand = ModEntities.DUWANG.get().create(level);
        if (stand == null) {
            return;
        }

        stand.setOwner(player);
        Vec3 spawnPos = player.position().add(player.getLookAngle().scale(0.85D)).add(0.0D, 1.0D, 0.0D);
        stand.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), 0.0F);
        level.addFreshEntity(stand);

        player.displayClientMessage(Component.literal("替身显现"), true);
    }
}
