package com.Y1fel.JoJoPlagueSurge.entity.custom.stand;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

public final class StandManager {
    private static final String OWNED_STAND_TYPE = "jojoplaguesurge.owned_stand_type";

    private StandManager() {
    }

    @Nullable
    public static StandType getOwnedStandType(Player player) {
        return StandType.byId(player.getPersistentData().getString(OWNED_STAND_TYPE));
    }

    public static boolean ownsStandType(Player player, StandType type) {
        return type == getOwnedStandType(player);
    }

    public static void setOwnedStandType(ServerPlayer player, StandType type) {
        player.getPersistentData().putString(OWNED_STAND_TYPE, type.getId());
    }

    public static void clearOwnership(ServerPlayer player) {
        dismissAll(player);
        player.getPersistentData().remove(OWNED_STAND_TYPE);
    }

    public static void toggleOwnership(ServerPlayer player, StandType type) {
        if (ownsStandType(player, type)) {
            clearOwnership(player);
            player.displayClientMessage(Component.literal("已撤销 " + type.getId() + " 替身所有权"), true);
            return;
        }

        dismissAll(player);
        setOwnedStandType(player, type);
        player.displayClientMessage(Component.literal("已获得 " + type.getId() + " 替身所有权"), true);
    }

    public static void toggleOwnershipAndStand(ServerPlayer player, StandType type) {
        if (ownsStandType(player, type)) {
            clearOwnership(player);
            player.displayClientMessage(Component.literal("已撤销 " + type.getId() + " 替身所有权并收回替身"), true);
            return;
        }

        dismissAll(player);
        setOwnedStandType(player, type);
        toggleStand(player, type);
        player.displayClientMessage(Component.literal("已获得 " + type.getId() + " 替身所有权并召唤替身"), true);
    }

    public static void toggleOwnedStand(ServerPlayer player) {
        StandType ownedType = getOwnedStandType(player);
        if (ownedType == null) {
            player.displayClientMessage(Component.literal("你当前没有替身所有权"), true);
            return;
        }

        toggleStand(player, ownedType);
    }

    public static void toggleStand(ServerPlayer player, StandType type) {
        if (player.isSpectator()) {
            return;
        }

        List<StandEntity> owned = findOwnedStands(player, StandEntity.class);
        if (!owned.isEmpty()) {
            dismissAll(player);
            return;
        }

        if (!ownsStandType(player, type)) {
            player.displayClientMessage(Component.literal("你没有 " + type.getId() + " 替身所有权"), true);
            return;
        }

        StandEntity stand = type.getEntityType().create(player.serverLevel());
        if (stand == null) {
            return;
        }

        stand.setOwner(player);
        Vec3 spawnPos = stand.calculateBackStandPos(player);
        stand.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), player.getXRot());
        stand.startRiding(player, true);
        player.serverLevel().addFreshEntity(stand);
    }

    public static void dismissAll(ServerPlayer player) {
        findOwnedStands(player, StandEntity.class).forEach(StandEntity::dismiss);
    }

    public static <T extends StandEntity> List<T> findOwnedStands(Player player, Class<T> standClass) {
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            return StreamSupport.stream(serverLevel.getAllEntities().spliterator(), false)
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

    @Nullable
    public static <T extends StandEntity> T findNearestOwnedStand(Player player, Class<T> standClass) {
        return findOwnedStands(player, standClass).stream()
                .min(Comparator.comparingDouble(stand -> stand.distanceToSqr(player)))
                .orElse(null);
    }

    @Nullable
    public static StandEntity findActiveStand(Player player) {
        return findOwnedStands(player, StandEntity.class).stream()
                .min(Comparator.comparingDouble(stand -> stand.distanceToSqr(player)))
                .orElse(null);
    }
}
