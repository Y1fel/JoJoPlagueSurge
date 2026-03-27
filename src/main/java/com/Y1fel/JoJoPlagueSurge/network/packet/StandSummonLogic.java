package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public class StandSummonLogic {
    private StandSummonLogic() {
    }

    public static void toggleDuWangStand(ServerPlayer player) {
        toggleStand(player, ModEntities.DUWANG.get());
    }

    /**
     * 对齐 JCraft summon/recall 主流程：
     * 1) 若已有该玩家拥有的替身则回收
     * 2) 否则在玩家背后生成并绑定 owner
     */
    public static <T extends StandEntity> void toggleStand(ServerPlayer player, EntityType<T> standType) {
        if (player.isSpectator()) {
            return;
        }

        List<StandEntity> owned = findOwnedStands(player, StandEntity.class);
        if (!owned.isEmpty()) {
            owned.forEach(Entity::stopRiding);
            player.displayClientMessage(Component.literal("替身收回"), true);
            return;
        }

        T stand = standType.create(player.serverLevel());
        if (stand == null) {
            return;
        }

        stand.setOwner(player);
        Vec3 spawnPos = player.position().subtract(player.getLookAngle());
        stand.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), player.getXRot());
        stand.startRiding(player, true);
        player.serverLevel().addFreshEntity(stand);

        player.displayClientMessage(Component.literal("替身显现"), true);
    }

    /**
     * 统一的 owner-stand 查询入口（服务端尽量全量、客户端按已加载实体）。
     */
    public static <T extends StandEntity> List<T> findOwnedStands(Player player, Class<T> standClass) {
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            return StreamSupport.stream(serverLevel.getAllEntities().spliterator(),false)
                    .filter(standClass::isInstance)
                    .map(standClass::cast)
                    .filter(stand -> stand.isAlive() && stand.isOwnedBy(player))
                    .toList();
        }

        return level.getEntitiesOfClass(
                standClass,
                player.getBoundingBox().inflate(128.0D),
                stand -> stand.isAlive() && stand.isOwnedBy(player)
        );
    }

    public static <T extends StandEntity> T findNearestOwnedStand(Player player, Class<T> standClass) {
        return findOwnedStands(player, standClass).stream()
                .min(Comparator.comparingDouble(stand -> stand.distanceToSqr(player)))
                .orElse(null);
    }
}
