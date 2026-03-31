package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.Config;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duvillager.DuVillagerEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import com.Y1fel.JoJoPlagueSurge.item.ModItems;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class BlueHawaiiSkillLogic {
    private static final String HAWAII_TARGET_TAG = "hawaii_target";
    private static final String LOCKED_TARGET_UUID = "jojoplaguesurge.bluehawaii.locked_target";
    private static final String HUNT_ACTIVE = "jojoplaguesurge.bluehawaii.hunt_active";
    private static final String HAD_TOOTH_LAST_TICK = "jojoplaguesurge.bluehawaii.had_tooth_last_tick";
    private static final String TOOTH_RESTORE_AT = "jojoplaguesurge.bluehawaii.tooth_restore_at";
    private static final String ANCHOR_X = "jojoplaguesurge.bluehawaii.anchor_x";
    private static final String ANCHOR_Y = "jojoplaguesurge.bluehawaii.anchor_y";
    private static final String ANCHOR_Z = "jojoplaguesurge.bluehawaii.anchor_z";

    private static final int RELEASE_COOLDOWN_TICKS = 20 * 60 * 20;
    private static final double HUNT_RADIUS = 256.0D;
    private static final double CHASE_SPEED = 1.35D;
    private static final double TEST_LOCK_RANGE = 32.0D;

    private BlueHawaiiSkillLogic() {
    }

    public static void handleSkillUse(ServerPlayer player, int skillId) {
        if (player instanceof FakePlayer) {
            return;
        }

        if (skillId == 1) {
            useToothMark(player);
        } else if (skillId == 2) {
            activateHunt(player);
        } else if (skillId == 3) {
            releaseHunt(player, true);
        }
    }

    public static void onServerPlayerTick(ServerPlayer player) {
        if (!player.isAlive()) {
            return;
        }

        detectNewToothHolder(player);
        restoreToothIfReady(player);
        maintainActiveHunt(player);
    }

    public static boolean isHuntActive(Player player) {
        return player.getPersistentData().getBoolean(HUNT_ACTIVE);
    }

    private static void useToothMark(ServerPlayer player) {
        if (!Config.BLUE_HAWAII_SKILL_1_ALLOW_ANY_ENTITY_TARGET_FOR_TEST.get()) {
            player.displayClientMessage(Component.literal("请通过带血的牙齿进行标记"), true);
            return;
        }

        Entity target = findNearestTestTarget(player);
        if (target == null) {
            player.displayClientMessage(Component.literal("范围内没有可锁定实体"), true);
            return;
        }

        setLockedTarget(player, target);
        notifyLock(player.server.getPlayerList().getPlayers(), target, player, true);
    }

    private static void detectNewToothHolder(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        boolean hasTooth = hasBloodyTooth(player);
        boolean hadTooth = tag.getBoolean(HAD_TOOTH_LAST_TICK);

        if (hasTooth && !hadTooth && !hasBlueHawaiiStand(player) && !player.isSpectator()) {
            lockTargetForBlueHawaiiOwners(player);
        }

        tag.putBoolean(HAD_TOOTH_LAST_TICK, hasTooth);
    }

    private static void lockTargetForBlueHawaiiOwners(ServerPlayer target) {
        List<ServerPlayer> players = target.server.getPlayerList().getPlayers();
        for (ServerPlayer candidate : players) {
            if (!hasBlueHawaiiStand(candidate) || candidate == target) {
                continue;
            }

            setLockedTarget(candidate, target);
        }

        notifyLock(players, target, target, false);
    }

    private static void activateHunt(ServerPlayer player) {
        if (!hasBlueHawaiiStand(player)) {
            return;
        }

        if (isHuntActive(player)) {
            player.displayClientMessage(Component.literal("蓝色夏威夷能力已经在维持中"), true);
            return;
        }

        int cooldown = JComponentPlatformUtils.getCooldowns(player).getCooldown(CooldownType.STAND_SP2);
        if (cooldown > 0) {
            long remainSeconds = (cooldown + 19L) / 20L;
            player.displayClientMessage(Component.literal("蓝色夏威夷能力冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        Entity target = getLockedTarget(player);
        if (target == null || !target.isAlive() || target.level() != player.level()) {
            player.displayClientMessage(Component.literal("没有可追击的锁定目标"), true);
            return;
        }

        CompoundTag tag = player.getPersistentData();
        tag.putBoolean(HUNT_ACTIVE, true);
        tag.putDouble(ANCHOR_X, player.getX());
        tag.putDouble(ANCHOR_Y, player.getY());
        tag.putDouble(ANCHOR_Z, player.getZ());
        player.displayClientMessage(Component.literal("蓝色夏威夷已开始追猎"), true);
    }

    private static void maintainActiveHunt(ServerPlayer player) {
        if (!isHuntActive(player)) {
            return;
        }

        if (!hasBlueHawaiiStand(player)) {
            releaseHunt(player, false);
            return;
        }

        Entity target = getLockedTarget(player);
        if (target == null || !target.isAlive() || target.level() != player.level()) {
            releaseHunt(player, true);
            return;
        }

        Vec3 anchor = getAnchor(player);
        player.setDeltaMovement(Vec3.ZERO);
        if (player.position().distanceToSqr(anchor) > 0.0025D) {
            player.teleportTo(anchor.x, anchor.y, anchor.z);
        }
        player.hurtMarked = true;
        player.fallDistance = 0.0F;

        applyOwnerEffects(player);
        directNearbyDuVillagers(player, target);
    }

    private static void releaseHunt(ServerPlayer player, boolean startCooldown) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.getBoolean(HUNT_ACTIVE) && !startCooldown) {
            clearAnchor(tag);
            return;
        }

        if (!tag.getBoolean(HUNT_ACTIVE) && startCooldown) {
            player.displayClientMessage(Component.literal("蓝色夏威夷能力尚未启动"), true);
            return;
        }

        tag.putBoolean(HUNT_ACTIVE, false);
        clearAnchor(tag);

        player.removeEffect(MobEffects.GLOWING);
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        player.removeEffect(MobEffects.JUMP);
        MobEffect imprison = findImprisonEffect();
        if (imprison != null) {
            player.removeEffect(imprison);
        }

        if (startCooldown) {
            JComponentPlatformUtils.getCooldowns(player).setCooldown(CooldownType.STAND_SP2, RELEASE_COOLDOWN_TICKS);
            tag.putLong(TOOTH_RESTORE_AT, player.level().getGameTime() + RELEASE_COOLDOWN_TICKS);
            player.displayClientMessage(Component.literal("蓝色夏威夷能力已解除"), true);
        }
    }

    private static void restoreToothIfReady(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains(TOOTH_RESTORE_AT)) {
            return;
        }

        long restoreAt = tag.getLong(TOOTH_RESTORE_AT);
        if (player.level().getGameTime() < restoreAt) {
            return;
        }

        ItemStack tooth = new ItemStack(ModItems.BLOODY_TOOTH.get());
        if (!player.getInventory().add(tooth)) {
            player.drop(tooth, false);
        }

        tag.remove(TOOTH_RESTORE_AT);
        player.displayClientMessage(Component.literal("你获得了一颗新的带血的牙齿"), true);
    }

    private static void applyOwnerEffects(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 220, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 220, 255, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 220, 128, false, false, false));

        MobEffect imprison = findImprisonEffect();
        if (imprison != null) {
            player.addEffect(new MobEffectInstance(imprison, 220, 0, false, false, false));
        }
    }

    private static void directNearbyDuVillagers(ServerPlayer owner, Entity target) {
        List<DuVillagerEntity> villagers = owner.serverLevel().getEntitiesOfClass(
                DuVillagerEntity.class,
                owner.getBoundingBox().inflate(HUNT_RADIUS),
                entity -> entity.isAlive()
                        && entity != target
        );

        for (DuVillagerEntity villager : villagers) {
            if (target instanceof LivingEntity livingTarget) {
                villager.setTarget(livingTarget);
                villager.getNavigation().moveTo(livingTarget, CHASE_SPEED);
                villager.getLookControl().setLookAt(livingTarget, 30.0F, 30.0F);

                double attackReach = villager.getBbWidth() + livingTarget.getBbWidth() + 1.5D;
                if (villager.distanceToSqr(livingTarget) <= attackReach * attackReach) {
                    villager.doHurtTarget(livingTarget);
                }
            } else {
                villager.setTarget(null);
                villager.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), CHASE_SPEED);
                villager.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
        }
    }

    private static boolean hasBloodyTooth(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.BLOODY_TOOTH.get())) {
                return true;
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModItems.BLOODY_TOOTH.get())) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasBlueHawaiiStand(ServerPlayer player) {
        return StandSummonLogic.findNearestOwnedStand(player, BlueHawaiiEntity.class) != null;
    }

    @Nullable
    private static Entity getLockedTarget(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.hasUUID(LOCKED_TARGET_UUID)) {
            return null;
        }

        return findEntityByUuid(player.serverLevel(), tag.getUUID(LOCKED_TARGET_UUID));
    }

    private static void setLockedTarget(ServerPlayer player, Entity target) {
        Entity previousTarget = getLockedTarget(player);
        if (previousTarget != null && previousTarget != target) {
            previousTarget.removeTag(HAWAII_TARGET_TAG);
        }

        player.getPersistentData().putUUID(LOCKED_TARGET_UUID, target.getUUID());
        target.addTag(HAWAII_TARGET_TAG);
    }

    @Nullable
    private static Entity findNearestTestTarget(ServerPlayer player) {
        AABB area = player.getBoundingBox().inflate(TEST_LOCK_RANGE);
        List<Entity> candidates = player.serverLevel().getEntities(
                player,
                area,
                entity -> entity.isAlive()
                        && entity != player
                        && !(entity instanceof StandEntity)
                        && !(entity instanceof BlueHawaiiEntity)
        );

        return candidates.stream()
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                .orElse(null);
    }

    @Nullable
    private static Entity findEntityByUuid(ServerLevel level, UUID uuid) {
        Entity sameLevel = level.getEntity(uuid);
        if (sameLevel != null) {
            return sameLevel;
        }

        for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    private static void notifyLock(List<ServerPlayer> players, Entity target, ServerPlayer source, boolean echoToSource) {
        Component message = Component.literal(getTargetDisplayName(target) + " 已被锁定")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
        Set<UUID> notified = new HashSet<>();

        for (ServerPlayer candidate : players) {
            if (!hasBlueHawaiiStand(candidate)) {
                continue;
            }
            if (!echoToSource && candidate == source) {
                continue;
            }

            candidate.sendSystemMessage(message);
            notified.add(candidate.getUUID());
        }

        for (ServerPlayer candidate : players) {
            if (candidate.getTeam() != null
                    && "op".equals(candidate.getTeam().getName())
                    && !notified.contains(candidate.getUUID())) {
                candidate.sendSystemMessage(message);
            }
        }
    }

    private static String getTargetDisplayName(Entity target) {
        String displayName = target.getName().getString();
        if (!displayName.isBlank()) {
            return displayName;
        }

        return target.getStringUUID();
    }

    private static Vec3 getAnchor(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        return new Vec3(tag.getDouble(ANCHOR_X), tag.getDouble(ANCHOR_Y), tag.getDouble(ANCHOR_Z));
    }

    private static void clearAnchor(CompoundTag tag) {
        tag.remove(ANCHOR_X);
        tag.remove(ANCHOR_Y);
        tag.remove(ANCHOR_Z);
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
}
