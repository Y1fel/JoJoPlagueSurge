package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import com.Y1fel.JoJoPlagueSurge.item.ModItems;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class BlueHawaiiSkillLogic {
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

    private BlueHawaiiSkillLogic() {
    }

    public static void handleSkillUse(ServerPlayer player, int skillId) {
        if (player instanceof FakePlayer) {
            return;
        }

        if (skillId == 1) {
            activateHunt(player);
        } else if (skillId == 2) {
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
        Component message = Component.literal(target.getScoreboardName() + " 已被锁定")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
        Set<UUID> notified = new HashSet<>();

        for (ServerPlayer candidate : players) {
            if (!hasBlueHawaiiStand(candidate) || candidate == target) {
                continue;
            }

            candidate.getPersistentData().putUUID(LOCKED_TARGET_UUID, target.getUUID());
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

        ServerPlayer target = getLockedTarget(player);
        if (target == null || !target.isAlive() || target.serverLevel() != player.serverLevel()) {
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

        ServerPlayer target = getLockedTarget(player);
        if (target == null || !target.isAlive() || target.serverLevel() != player.serverLevel()) {
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
        directNearbyCreatures(player, target);
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

    private static void directNearbyCreatures(ServerPlayer owner, ServerPlayer target) {
        List<LivingEntity> entities = owner.serverLevel().getEntitiesOfClass(
                LivingEntity.class,
                owner.getBoundingBox().inflate(HUNT_RADIUS),
                entity -> entity.isAlive()
                        && entity != owner
                        && entity != target
                        && !(entity instanceof Player)
                        && !(entity instanceof StandEntity)
        );

        for (LivingEntity entity : entities) {
            if (entity instanceof Mob mob) {
                mob.setTarget(target);
                mob.getNavigation().moveTo(target, CHASE_SPEED);
                mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            } else {
                Vec3 direction = target.position().subtract(entity.position());
                if (direction.lengthSqr() > 1.0E-4D) {
                    entity.setDeltaMovement(entity.getDeltaMovement().scale(0.4D).add(direction.normalize().scale(0.25D)));
                }
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
    private static ServerPlayer getLockedTarget(ServerPlayer player) {
        CompoundTag tag = player.getPersistentData();
        if (!tag.hasUUID(LOCKED_TARGET_UUID)) {
            return null;
        }

        UUID uuid = tag.getUUID(LOCKED_TARGET_UUID);
        return player.server.getPlayerList().getPlayer(uuid);
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
