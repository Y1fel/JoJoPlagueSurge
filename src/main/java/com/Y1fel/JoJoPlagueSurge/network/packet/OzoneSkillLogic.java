package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.block.ModBlocks;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
    private static final String SKILL_1_INJURY_OUTBURST_APPLIED = "jojoplaguesurge.ozone.skill_1_injury_outburst_applied";
    private static final String SKILL_1_BLEEDING_APPLIED = "jojoplaguesurge.ozone.skill_1_bleeding_applied";
    private static final String HOUSE_RECALL_COOLDOWN_UNTIL = "jojoplaguesurge.ozone.house_recall_cooldown_until";

    private static final int SKILL_1_DURATION_TICKS = 20 * 60;
    private static final int SKILL_1_COOLDOWN_TICKS = 20 * 60;
    private static final int SKILL_2_DURATION_TICKS = 20 * 2;
    private static final int SKILL_2_COOLDOWN_TICKS = 20 * 30;
    private static final int SKILL_EFFECT_REFRESH_TICKS = 40;
    private static final int SKILL_1_INJURY_OUTBURST_START_TICKS = 20 * 10;
    private static final int SKILL_1_INJURY_OUTBURST_DURATION_TICKS = 20 * 50;
    private static final int SKILL_1_BLEEDING_START_TICKS = 20 * 45;
    private static final int SKILL_1_HEAVY_DURATION_TICKS = SKILL_1_DURATION_TICKS;
    private static final int SKILL_1_BLEEDING_DURATION_TICKS = SKILL_1_DURATION_TICKS - SKILL_1_BLEEDING_START_TICKS;
    private static final int HOUSE_RECALL_COOLDOWN_TICKS = 20 * 60 * 60 * 2;
    private static final int IMPRISON_DURATION_TICKS = 20 * 2;
    private static final int HOUSE_EFFECT_REFRESH_TICKS = 40;
    private static final int HOUSE_HEAVY_1_START_TICKS = 0;
    private static final int HOUSE_HEAVY_2_START_TICKS = 20 * 10;
    private static final int HOUSE_INJURY_OUTBURST_START_TICKS = 20 * 15;
    private static final int HOUSE_INJURY_OUTBURST_DURATION_TICKS = 20 * 30;
    private static final int HOUSE_BLEEDING_START_TICKS = 20 * 25;
    private static final int HOUSE_IMPRISON_START_TICKS = 20 * 150;

    private static final double SKILL_PLAYER_RADIUS = 16.0D;
    private static final double HOUSE_RADIUS = 24.0D;
    private static final double HOUSE_SEARCH_RADIUS_XZ = 40.0D;
    private static final double HOUSE_SEARCH_RADIUS_Y = 40.0D;

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

    public static boolean isSkill1Active(Player player) {
        return player.getPersistentData().contains(SKILL_1_ACTIVE_UNTIL);
    }

    public static boolean isSkill2Active(Player player) {
        return player.getPersistentData().contains(SKILL_2_ACTIVE_UNTIL);
    }

    public static boolean isSkill3Active(Player player) {
        return ACTIVE_HOUSES.containsKey(player.getUUID());
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
        updateSkill1Effects(player);
        updateTimedSkill(player, SKILL_1_ACTIVE_UNTIL, SKILL_1_TARGETS, TAG_SKILL_1, SkillCooldowns.OZONE_SKILL_1, SKILL_1_COOLDOWN_TICKS);
        updateTimedSkill(player, SKILL_2_ACTIVE_UNTIL, SKILL_2_TARGETS, TAG_SKILL_2, SkillCooldowns.OZONE_SKILL_2, SKILL_2_COOLDOWN_TICKS);
        updateActiveHouseZone(player);
        updateActiveSkillHint(player);
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

        ActiveHouseZone previous = ACTIVE_HOUSES.put(
                player.getUUID(),
                new ActiveHouseZone(pos.immutable(), new HashMap<>(), player.level().getGameTime())
        );
        if (previous != null) {
            clearEntityTags(player.server, previous.trackedEntities().keySet(), TAG_SKILL_3);
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
            clearEntityTags(serverLevel.getServer(), zone.trackedEntities().keySet(), TAG_SKILL_3);
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

        int cooldown = SkillCooldowns.getRemainingTicks(player, SkillCooldowns.OZONE_SKILL_1);
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
        player.getPersistentData().remove(SKILL_1_INJURY_OUTBURST_APPLIED);
        player.getPersistentData().remove(SKILL_1_BLEEDING_APPLIED);
        applySkill1InitialEffects(player.server, targets);
        player.displayClientMessage(Component.literal("附近玩家已获得 ozone_target_1"), true);
    }

    private static void useSkill2(ServerPlayer player) {
        if (player.getPersistentData().contains(SKILL_2_ACTIVE_UNTIL)) {
            player.displayClientMessage(Component.literal("OZONE 技能2正在持续中"), true);
            return;
        }

        int cooldown = SkillCooldowns.getRemainingTicks(player, SkillCooldowns.OZONE_SKILL_2);
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
            clearEntityTags(player.server, zone.trackedEntities().keySet(), TAG_SKILL_3);
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
            String cooldownId,
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
        if (SKILL_1_ACTIVE_UNTIL.equals(activeUntilKey)) {
            player.getPersistentData().remove(SKILL_1_INJURY_OUTBURST_APPLIED);
            player.getPersistentData().remove(SKILL_1_BLEEDING_APPLIED);
        }
        SkillCooldowns.startCooldown(player, cooldownId, cooldownTicks);
    }

    private static void updateSkill1Effects(ServerPlayer player) {
        if (!player.getPersistentData().contains(SKILL_1_ACTIVE_UNTIL)) {
            return;
        }

        Set<UUID> targetIds = SKILL_1_TARGETS.get(player.getUUID());
        if (targetIds == null || targetIds.isEmpty()) {
            return;
        }

        long activeUntil = player.getPersistentData().getLong(SKILL_1_ACTIVE_UNTIL);
        long elapsedTicks = Math.max(0L, SKILL_1_DURATION_TICKS - Math.max(activeUntil - player.level().getGameTime(), 0L));

        if (elapsedTicks >= SKILL_1_INJURY_OUTBURST_START_TICKS
                && !player.getPersistentData().getBoolean(SKILL_1_INJURY_OUTBURST_APPLIED)) {
            applySkill1DelayedEffect(player.server, targetIds, "injury_outburst", SKILL_1_INJURY_OUTBURST_DURATION_TICKS);
            player.getPersistentData().putBoolean(SKILL_1_INJURY_OUTBURST_APPLIED, true);
        }

        if (elapsedTicks >= SKILL_1_BLEEDING_START_TICKS
                && !player.getPersistentData().getBoolean(SKILL_1_BLEEDING_APPLIED)) {
            applySkill1DelayedEffect(player.server, targetIds, "bleeding", SKILL_1_BLEEDING_DURATION_TICKS);
            player.getPersistentData().putBoolean(SKILL_1_BLEEDING_APPLIED, true);
        }
    }

    private static void updateActiveHouseZone(ServerPlayer player) {
        ActiveHouseZone zone = ACTIVE_HOUSES.get(player.getUUID());
        if (zone == null) {
            return;
        }

        BlockPos pos = zone.pos();
        if (!player.serverLevel().getBlockState(pos).is(ModBlocks.OZONE.get())) {
            clearEntityTags(player.server, zone.trackedEntities().keySet(), TAG_SKILL_3);
            ACTIVE_HOUSES.remove(player.getUUID());
            HOUSE_OWNERS.remove(pos.asLong());
            return;
        }

        AABB area = new AABB(pos).inflate(HOUSE_SEARCH_RADIUS_XZ, HOUSE_SEARCH_RADIUS_Y, HOUSE_SEARCH_RADIUS_XZ);
        long now = player.level().getGameTime();
        List<LivingEntity> currentTargets = player.serverLevel().getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> entity.isAlive()
                        && entity != player
                        && !(entity instanceof StandEntity)
                        && entity.distanceToSqr(
                                pos.getX() + 0.5D,
                                pos.getY() + 0.5D,
                                pos.getZ() + 0.5D
                        ) <= HOUSE_RADIUS * HOUSE_RADIUS
        );

        Set<UUID> currentIds = new HashSet<>();
        Map<UUID, Long> trackedEntities = zone.trackedEntities();
        for (LivingEntity entity : currentTargets) {
            entity.addTag(TAG_SKILL_3);
            UUID entityId = entity.getUUID();
            long enteredAt = trackedEntities.computeIfAbsent(entityId, unused -> now);
            applyHouseEffects(entity, Math.max(0L, now - enteredAt));
            currentIds.add(entityId);
        }

        for (UUID uuid : new HashSet<>(trackedEntities.keySet())) {
            if (!currentIds.contains(uuid)) {
                Entity entity = findEntityByUuid(player.server, uuid);
                if (entity != null) {
                    entity.removeTag(TAG_SKILL_3);
                }
                trackedEntities.remove(uuid);
            }
        }
    }

    private static void updateActiveSkillHint(ServerPlayer player) {
        List<String> activeHints = new ArrayList<>(3);
        if (isSkill1Active(player)) {
            activeHints.add("技能1持续中");
        }
        if (isSkill2Active(player)) {
            activeHints.add("技能2持续中");
        }
        if (isSkill3Active(player)) {
            activeHints.add("技能3持续中");
        }

        if (!activeHints.isEmpty()) {
            player.displayClientMessage(
                    Component.literal(String.join("  |  ", activeHints))
                            .withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                    true
            );
        }
    }

    private static Set<UUID> tagNearbyPlayers(ServerPlayer source, double radius, String tagName) {
        Vec3 center = source.position().add(0.0D, source.getBbHeight() * 0.5D, 0.0D);
        List<ServerPlayer> players = source.serverLevel().getEntitiesOfClass(
                ServerPlayer.class,
                new AABB(center, center).inflate(radius),
                player -> player != source
                        && !player.isSpectator()
                        && player.position().add(0.0D, player.getBbHeight() * 0.5D, 0.0D).distanceToSqr(center) <= radius * radius
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
        MobEffect effect = findMorePotionEffect("imprision");
        if (effect != null) {
            return effect;
        }

        return findMorePotionEffect("imprison");
    }

    @Nullable
    private static MobEffect findMorePotionEffect(String path) {
        return ForgeRegistries.MOB_EFFECTS.getValue(
                ResourceLocation.fromNamespaceAndPath("more_potion_effects", path)
        );
    }

    private static void applyHouseEffects(LivingEntity entity, long elapsedTicks) {
        if (elapsedTicks >= HOUSE_HEAVY_2_START_TICKS) {
            applyMorePotionEffect(entity, "heavy", HOUSE_EFFECT_REFRESH_TICKS, 1);
        } else if (elapsedTicks >= HOUSE_HEAVY_1_START_TICKS) {
            applyMorePotionEffect(entity, "heavy", HOUSE_EFFECT_REFRESH_TICKS, 0);
        }

        if (elapsedTicks >= HOUSE_INJURY_OUTBURST_START_TICKS) {
            applyMorePotionEffect(entity, "injury_outburst", HOUSE_INJURY_OUTBURST_DURATION_TICKS, 0);
        }

        if (elapsedTicks >= HOUSE_BLEEDING_START_TICKS) {
            applyMorePotionEffect(entity, "bleeding", HOUSE_EFFECT_REFRESH_TICKS, 0);
        }

        if (elapsedTicks >= HOUSE_IMPRISON_START_TICKS) {
            MobEffect imprison = findImprisonEffect();
            if (imprison != null) {
                entity.addEffect(new MobEffectInstance(imprison, HOUSE_EFFECT_REFRESH_TICKS, 0, false, false, false));
            }
        }
    }

    private static void applySkill1InitialEffects(MinecraftServer server, Set<UUID> targetIds) {
        for (UUID uuid : targetIds) {
            Entity entity = findEntityByUuid(server, uuid);
            if (entity instanceof LivingEntity living) {
                applyMorePotionEffect(living, "heavy", SKILL_1_HEAVY_DURATION_TICKS, 0);
            }
        }
    }

    private static void applySkill1DelayedEffect(MinecraftServer server, Set<UUID> targetIds, String effectId, int durationTicks) {
        for (UUID uuid : targetIds) {
            Entity entity = findEntityByUuid(server, uuid);
            if (entity instanceof LivingEntity living) {
                applyMorePotionEffect(living, effectId, durationTicks, 0);
            }
        }
    }

    private static void applyMorePotionEffect(LivingEntity entity, String effectId, int durationTicks, int amplifier) {
        if (durationTicks <= 0) {
            return;
        }

        MobEffect effect = findMorePotionEffect(effectId);
        if (effect != null) {
            entity.addEffect(new MobEffectInstance(effect, durationTicks, amplifier, false, false, false));
        }
    }

    private record ActiveHouseZone(BlockPos pos, Map<UUID, Long> trackedEntities, long activatedAt) {
    }
}
