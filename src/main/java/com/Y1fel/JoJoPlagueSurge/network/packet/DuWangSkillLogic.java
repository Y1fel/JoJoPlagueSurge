package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.Config;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandManager;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.trackingtornado.TrackingTornadoEntity;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class DuWangSkillLogic {
    private static final String SKILL_1_LAST_USE = "jojoplaguesurge.duwang_skill_1_last_use";
    private static final String SKILL_2_LAST_USE = "jojoplaguesurge.duwang_skill_2_last_use";
    private static final double TEST_TARGET_RANGE = 32.0D;

    private static final int SKILL_1_COOLDOWN_TICKS = DuWangSkillCatalog.TRACKING_TORNADO_COOLDOWN_TICKS;
    private static final int SKILL_2_COOLDOWN_TICKS = DuWangSkillCatalog.HURRICANE_BARRIER_COOLDOWN_TICKS;

    private DuWangSkillLogic() {
    }

    public static void handleSkillUse(ServerPlayer player, int skillId) {
        if (player instanceof FakePlayer) {
            return;
        }

        if (skillId == 1) {
            useTrackingHurricane(player);
        } else if (skillId == 2) {
            useHurricaneBarrier(player);
        }
    }

    private static void useTrackingHurricane(ServerPlayer player) {
        int cooldown = SkillCooldowns.getRemainingTicks(player, SkillCooldowns.DUWANG_SKILL_1);
        if (cooldown > 0) {
            long remainSeconds = (cooldown + 19L) / 20L;
            player.displayClientMessage(Component.literal("追踪飓风冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        LivingEntity target = Config.DUWANG_SKILL_1_ALLOW_ANY_LIVING_TARGET_FOR_TEST.get()
                ? findNearestAnyLivingTarget(player)
                : findTaggedTarget(player, "@e[tag=dbh_target,limit=1,sort=nearest]");
        if (target == null) {
            player.displayClientMessage(Component.literal("没有可用目标"), true);
            return;
        }

        player.getPersistentData().putLong(SKILL_1_LAST_USE, player.level().getGameTime());
        SkillCooldowns.startCooldown(player, SkillCooldowns.DUWANG_SKILL_1, SKILL_1_COOLDOWN_TICKS);
        broadcastToOpTeam(player, "追踪飓风");

        ServerLevel level = player.serverLevel();
        DuWangEntity casterStand = findOwnedStand(player);
        if (casterStand == null) {
            player.displayClientMessage(Component.literal("未找到已召唤的替身，无法释放追踪飓风"), true);
            return;
        }

        TrackingTornadoEntity tornado = ModEntities.TRACKING_TORNADO.get().create(level);
        if (tornado == null) {
            return;
        }

        Vec3 spawnPos = casterStand.position().add(0.0D, casterStand.getBbHeight() * 0.65D, 0.0D);
        Vec3 launchDirection = casterStand.getLookAngle();
        if (launchDirection.lengthSqr() < 1.0E-5D) {
            launchDirection = player.getLookAngle();
        }

        tornado.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, casterStand.getYRot(), casterStand.getXRot());
        tornado.setOwner(player);
        tornado.setTarget(target);
        tornado.setDeltaMovement(launchDirection.normalize().scale(0.3D));
        level.addFreshEntity(tornado);
    }

    private static void useHurricaneBarrier(ServerPlayer player) {
        int cooldown = SkillCooldowns.getRemainingTicks(player, SkillCooldowns.DUWANG_SKILL_2);
        if (cooldown > 0) {
            long remainSeconds = (cooldown + 19L) / 20L;
            player.displayClientMessage(Component.literal("飓风屏障冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        player.getPersistentData().putLong(SKILL_2_LAST_USE, player.level().getGameTime());
        SkillCooldowns.startCooldown(player, SkillCooldowns.DUWANG_SKILL_2, SKILL_2_COOLDOWN_TICKS);

        MobEffect solidShield = findMorePotionEffect("solid_shield");
        if (solidShield != null) {
            player.addEffect(new MobEffectInstance(solidShield, 20 * 15, 0, false, false, false));
        }
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 15, 1, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 5, 0, false, false, false));

        ServerLevel level = player.serverLevel();
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,
                player.getX(), player.getY() + 1.0D, player.getZ(),
                120, 1.6D, 1.0D, 1.6D, 0.06D);
    }

    @Nullable
    private static LivingEntity findNearestAnyLivingTarget(ServerPlayer player) {
        AABB area = player.getBoundingBox().inflate(TEST_TARGET_RANGE);
        return player.serverLevel().getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        entity -> entity.isAlive()
                                && entity != player
                                && !(entity instanceof StandEntity))
                .stream()
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                .orElse(null);
    }

    @Nullable
    private static LivingEntity findTaggedTarget(ServerPlayer player, String selector) {
        try {
            StringReader reader = new StringReader(selector);
            EntitySelectorParser parser = new EntitySelectorParser(reader, true);
            EntitySelector entitySelector = parser.parse();
            List<? extends Entity> entities = entitySelector.findEntities(player.createCommandSourceStack());

            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living && living.isAlive() && entity != player) {
                    return living;
                }
            }
        } catch (CommandSyntaxException e) {
            player.displayClientMessage(Component.literal("目标选择器无效: " + e.getMessage()), true);
        }

        return null;
    }

    private static void broadcastToOpTeam(ServerPlayer user, String skillName) {
        String safeName = user.getName().getString().replace("\"", "'");
        String json = "[{\"text\":\"杜比华使用能力：\",\"color\":\"gold\"},"
                + "{\"text\":\"" + skillName + "\",\"color\":\"aqua\"},"
                + "{\"text\":\"（使用者 " + safeName + "）\",\"color\":\"yellow\"}]";

        CommandSourceStack source = user.server.createCommandSourceStack();
        user.server.getCommands().performPrefixedCommand(source, "tellraw @a[team=op] " + json);
    }

    private static DuWangEntity findOwnedStand(ServerPlayer player) {
        return StandManager.findNearestOwnedStand(player, DuWangEntity.class);
    }

    @Nullable
    private static MobEffect findMorePotionEffect(String path) {
        return ForgeRegistries.MOB_EFFECTS.getValue(
                ResourceLocation.fromNamespaceAndPath("more_potion_effects", path)
        );
    }
}
