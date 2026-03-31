package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.block.ModBlocks;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class OzoneSkillLogic {
    public static final String TAG_SKILL_1 = "ozone_target_1";
    public static final String TAG_SKILL_2 = "ozone_target_2";
    public static final String TAG_SKILL_3 = "ozone_target_3";

    private static final String SKILL_1_ACTIVE_UNTIL = "jojoplaguesurge.ozone.skill_1_active_until";
    private static final String SKILL_2_ACTIVE_UNTIL = "jojoplaguesurge.ozone.skill_2_active_until";
    private static final String HOUSE_RECALL_COOLDOWN_UNTIL = "jojoplaguesurge.ozone.house_recall_cooldown_until";

    private static final int SKILL_1_DURATION_TICKS = 20 * 60;
    private static final int SKILL_1_COOLDOWN_TICKS = 20 * 60;
    private static final int SKILL_2_DURATION_TICKS = 20 * 2;
    private static final int SKILL_2_COOLDOWN_TICKS = 20 * 30;
    private static final int HOUSE_RECALL_COOLDOWN_TICKS = 20 * 60 * 60 * 2;
    private static final int IMPRISON_DURATION_TICKS = 20 * 2;

    private static final double SKILL_PLAYER_RADIUS = 16.0D;
    private static final double HOUSE_RADIUS = 32.0D;

    private static final Map<UUID, Set<UUID>> SKILL_1_TARGETS = new HashMap<>();
    private static final Map<UUID, Set<UUID>> SKILL_2_TARGETS = new HashMap<>();
    private static final Map<UUID, ActiveHouseZone> ACTIVE_HOUSES = new HashMap<>();
    private static final Map<Long, UUID> HOUSE_OWNERS = new HashMap<>();

    private OzoneSkillLogic() {
    }

    public static boolean hasOzoneHouse(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModBlocks.OZONE.get().asItem())) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModBlocks.OZONE.get().asItem())) {
                return true;
            }
        }

        return false;
    }

    public static boolean shouldRenderHud(Player player) {
        return hasOzoneHouse(player);
    }

    public static void handleSkillUse(ServerPlayer player, int skillId) {
        if (!hasOzoneHouse(player)) {
            player.displayClientMessage(Component.literal("背包里没有 OZONE"), true);
            return;
        }

        if (skillId == 1) {
            useSkill1(player);
        } else if (skillId == 2) {
            useSkill2(player);
        }
    }

    public static void onServerPlayerTick(ServerPlayer player) {
        updateTimedSkill(player, SKILL_1_ACTIVE_UNTIL, SKILL_1_TARGETS, TAG_SKILL_1, CooldownType.STAND_SP1, SKILL_1_COOLDOWN_TICKS);
        updateTimedSkill(player, SKILL_2_ACTIVE_UNTIL, SKILL_2_TARGETS, TAG_SKILL_2, CooldownType.STAND_SP2, SKILL_2_COOLDOWN_TICKS);
        updateActiveHouseZone(player);
    }

    public static void onHousePlaced(Level level, BlockPos pos, @Nullable Entity placer) {
        if (!(level instanceof ServerLevel) || !(placer instanceof ServerPlayer player)) {
            return;
        }

        HOUSE_OWNERS.put(pos.asLong(), player.getUUID());
    }

    public static InteractionResult onHouseUsed(ServerPlayer player, BlockPos pos) {
        UUID owner = HOUSE_OWNERS.get(pos.asLong());
        if (owner == null) {
            owner = player.getUUID();
            HOUSE_OWNERS.put(pos.asLong(), owner);
        }

        if (!owner.equals(player.getUUID())) {
            player.displayClientMessage(Component.literal("这不是你的 OZONE 房子"), true);
            return InteractionResult.SUCCESS;
        }

        ActiveHouseZone activeZone = ACTIVE_HOUSES.get(player.getUUID());
        if (player.isShiftKeyDown() || (activeZone != null && activeZone.pos().equals(pos))) {
            recallHouse(player, pos);
            return InteractionResult.SUCCESS;
        }

        long recallCooldownUntil = player.getPersistentData().getLong(HOUSE_RECALL_COOLDOWN_UNTIL);
        if (player.level().getGameTime() < recallCooldownUntil) {
            long remainSeconds = (recallCooldownUntil - player.level().getGameTime() + 19L) / 20L;
            player.displayClientMessage(Component.literal("OZONE 房子效果冷却中，还需 " + remainSeconds + " 秒"), true);
            return InteractionResult.SUCCESS;
        }

        ActiveHouseZone previous = ACTIVE_HOUSES.put(player.getUUID(), new ActiveHouseZone(pos.immutable(), new HashSet<>()));
        if (previous != null) {
            clearEntityTags(player.server, previous.taggedEntities(), TAG_SKILL_3);
        }

        player.displayClientMessage(Component.literal("OZONE 房子已启动"), true);
        return InteractionResult.SUCCESS;
    }

    public static void onHouseRemoved(Level level, BlockPos pos) {
        UUID owner = HOUSE_OWNERS.remove(pos.asLong());
        if (!(level instanceof ServerLevel serverLevel) || owner == null) {
            return;
        }

        ActiveHouseZone zone = ACTIVE_HOUSES.remove(owner);
        if (zone != null && zone.pos().equals(pos)) {
            clearEntityTags(serverLevel.getServer(), zone.taggedEntities(), TAG_SKILL_3);
            ServerPlayer ownerPlayer = serverLevel.getServer().getPlayerList().getPlayer(owner);
            if (ownerPlayer != null) {
                startHouseRecallCooldown(ownerPlayer);
            }
        }
    }

    private static void useSkill1(ServerPlayer player) {
        if (player.getPersistentData().contains(SKILL_1_ACTIVE_UNTIL)) {
            player.displayClientMessage(Component.literal("OZONE 技能1正在持续中"), true);
            return;
        }

        int cooldown = JComponentPlatformUtils.getCooldowns(player).getCooldown(CooldownType.STAND_SP1);
        if (cooldown > 0) {
            long remainSeconds = (cooldown + 19L) / 20L;
            player.displayClientMessage(Component.literal("OZONE 技能1冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        Set<UUID> targets = tagNearbyPlayers(player, SKILL_PLAYER_RADIUS, TAG_SKILL_1);
        if (targets.isEmpty()) {
            player.displayClientMessage(Component.literal("范围内没有可标记玩家"), true);
            return;
        }

        SKILL_1_TARGETS.put(player.getUUID(), targets);
        player.getPersistentData().putLong(SKILL_1_ACTIVE_UNTIL, player.level().getGameTime() + SKILL_1_DURATION_TICKS);
        player.displayClientMessage(Component.literal("附近玩家已获得 ozone_target_1"), true);
    }

    private static void useSkill2(ServerPlayer player) {
        if (player.getPersistentData().contains(SKILL_2_ACTIVE_UNTIL)) {
            player.displayClientMessage(Component.literal("OZONE 技能2正在持续中"), true);
            return;
        }

        int cooldown = JComponentPlatformUtils.getCooldowns(player).getCooldown(CooldownType.STAND_SP2);
        if (cooldown > 0) {
            long remainSeconds = (cooldown + 19L) / 20L;
            player.displayClientMessage(Component.literal("OZONE 技能2冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        Set<UUID> targets = tagNearbyPlayers(player, SKILL_PLAYER_RADIUS, TAG_SKILL_2);
        if (targets.isEmpty()) {
            player.displayClientMessage(Component.literal("范围内没有可标记玩家"), true);
            return;
        }

        SKILL_2_TARGETS.put(player.getUUID(), targets);
        player.getPersistentData().putLong(SKILL_2_ACTIVE_UNTIL, player.level().getGameTime() + SKILL_2_DURATION_TICKS);
        applyImprisonToTaggedTargets(player.server, targets);
        player.displayClientMessage(Component.literal("附近玩家已获得 ozone_target_2"), true);
    }

    private static void recallHouse(ServerPlayer player, BlockPos pos) {
        ActiveHouseZone zone = ACTIVE_HOUSES.remove(player.getUUID());
        if (zone != null) {
            clearEntityTags(player.server, zone.taggedEntities(), TAG_SKILL_3);
        }

        HOUSE_OWNERS.remove(pos.asLong());
        if (!player.level().removeBlock(pos, false)) {
            return;
        }

        ItemStack stack = new ItemStack(ModBlocks.OZONE.get().asItem());
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }

        startHouseRecallCooldown(player);
        player.displayClientMessage(Component.literal("OZONE 房子已回收，房子效果进入 2 小时冷却"), true);
    }

    private static void startHouseRecallCooldown(ServerPlayer player) {
        player.getPersistentData().putLong(HOUSE_RECALL_COOLDOWN_UNTIL, player.level().getGameTime() + HOUSE_RECALL_COOLDOWN_TICKS);
    }

    private static void updateTimedSkill(
            ServerPlayer player,
            String activeUntilKey,
            Map<UUID, Set<UUID>> targetStore,
            String tagName,
            CooldownType cooldownType,
            int cooldownTicks
    ) {
        if (!player.getPersistentData().contains(activeUntilKey)) {
            return;
        }

        long activeUntil = player.getPersistentData().getLong(activeUntilKey);
        if (player.level().getGameTime() < activeUntil) {
            return;
        }

        clearEntityTags(player.server, targetStore.remove(player.getUUID()), tagName);
        player.getPersistentData().remove(activeUntilKey);
        JComponentPlatformUtils.getCooldowns(player).setCooldown(cooldownType, cooldownTicks);
    }

    private static void updateActiveHouseZone(ServerPlayer player) {
        ActiveHouseZone zone = ACTIVE_HOUSES.get(player.getUUID());
        if (zone == null) {
            return;
        }

        BlockPos pos = zone.pos();
        if (!player.serverLevel().getBlockState(pos).is(ModBlocks.OZONE.get())) {
            clearEntityTags(player.server, zone.taggedEntities(), TAG_SKILL_3);
            ACTIVE_HOUSES.remove(player.getUUID());
            HOUSE_OWNERS.remove(pos.asLong());
            return;
        }

        AABB area = new AABB(pos).inflate(HOUSE_RADIUS, HOUSE_RADIUS, HOUSE_RADIUS);
        List<LivingEntity> currentTargets = player.serverLevel().getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> entity.isAlive()
                        && entity != player
                        && !(entity instanceof StandEntity)
        );

        Set<UUID> currentIds = new HashSet<>();
        for (LivingEntity entity : currentTargets) {
            entity.addTag(TAG_SKILL_3);
            currentIds.add(entity.getUUID());
        }

        Set<UUID> previous = zone.taggedEntities();
        for (UUID uuid : new HashSet<>(previous)) {
            if (!currentIds.contains(uuid)) {
                Entity entity = findEntityByUuid(player.server, uuid);
                if (entity != null) {
                    entity.removeTag(TAG_SKILL_3);
                }
                previous.remove(uuid);
            }
        }

        previous.addAll(currentIds);
    }

    private static Set<UUID> tagNearbyPlayers(ServerPlayer source, double radius, String tagName) {
        List<ServerPlayer> players = source.serverLevel().getEntitiesOfClass(
                ServerPlayer.class,
                source.getBoundingBox().inflate(radius),
                player -> player != source && !player.isSpectator()
        );

        Set<UUID> ids = new HashSet<>();
        for (ServerPlayer player : players) {
            player.addTag(tagName);
            ids.add(player.getUUID());
        }
        return ids;
    }

    private static void applyImprisonToTaggedTargets(MinecraftServer server, Set<UUID> targetIds) {
        MobEffect imprison = findImprisonEffect();
        for (UUID uuid : targetIds) {
            Entity entity = findEntityByUuid(server, uuid);
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }

            if (imprison != null) {
                living.addEffect(new MobEffectInstance(imprison, IMPRISON_DURATION_TICKS, 0, false, false, false));
            } else {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, IMPRISON_DURATION_TICKS, 255, false, false, false));
                living.addEffect(new MobEffectInstance(MobEffects.JUMP, IMPRISON_DURATION_TICKS, 128, false, false, false));
            }
        }
    }

    private static void clearEntityTags(MinecraftServer server, @Nullable Set<UUID> entityIds, String tagName) {
        if (entityIds == null || entityIds.isEmpty()) {
            return;
        }

        for (UUID uuid : entityIds) {
            Entity entity = findEntityByUuid(server, uuid);
            if (entity != null) {
                entity.removeTag(tagName);
            }
        }
    }

    @Nullable
    private static Entity findEntityByUuid(MinecraftServer server, UUID uuid) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    @Nullable
    private static MobEffect findImprisonEffect() {
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(
                ResourceLocation.fromNamespaceAndPath("more_potion_effects", "imprision")
        );
        if (effect != null) {
            return effect;
        }

        return ForgeRegistries.MOB_EFFECTS.getValue(
                ResourceLocation.fromNamespaceAndPath("more_potion_effects", "imprison")
        );
    }

    private record ActiveHouseZone(BlockPos pos, Set<UUID> taggedEntities) {
    }
}
