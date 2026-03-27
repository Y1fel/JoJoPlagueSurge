package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StandSummonLogic {
    private StandSummonLogic() {
    }

    public static void toggleDuWangStand(ServerPlayer player) {
        toggleStand(player, ModEntities.DUWANG.get());
    }

    /**
     * JCraft 风格的可复用替身显现/收回逻辑：
     * 1) 若已有该玩家拥有的替身则回收
     * 2) 否则在玩家身后生成并绑定 owner
     */
    public static <T extends StandEntity> void toggleStand(ServerPlayer player, EntityType<T> standType) {
        if (player.isSpectator()) {
            return;
        }

        ServerLevel level = player.serverLevel();

        List<StandEntity> owned = level.getAllEntities().stream()
                .filter(entity -> entity instanceof StandEntity stand && stand.isOwnedBy(player))
                .map(entity -> (StandEntity) entity)
                .toList();

        if (!owned.isEmpty()) {
            owned.forEach(StandEntity::discard);
            player.displayClientMessage(Component.literal("替身收回"), true);
            return;
        }

        T stand = standType.create(level);
        if (stand == null) {
            return;
        }

        stand.setOwner(player);
        Vec3 spawnPos = player.position().subtract(player.getLookAngle());
        stand.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), player.getXRot());
        stand.startRiding(player, true);
        level.addFreshEntity(stand);

        player.displayClientMessage(Component.literal("替身显现"), true);
    }
}
