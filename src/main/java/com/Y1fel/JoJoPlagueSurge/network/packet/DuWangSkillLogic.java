package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.trackingtornado.TrackingTornadoEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DuWangSkillLogic {
    private static final String SKILL_1_LAST_USE = "jojoplaguesurge.duwang_skill_1_last_use";
    private static final String SKILL_2_LAST_USE = "jojoplaguesurge.duwang_skill_2_last_use";

    private static final int SKILL_1_COOLDOWN_TICKS = 20 * 180;
    private static final int SKILL_2_COOLDOWN_TICKS = 20 * 60;
    private static final Logger log = LoggerFactory.getLogger(DuWangSkillLogic.class);

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
        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(SKILL_1_LAST_USE);
        long elapsed = now - last;

        if (elapsed < SKILL_1_COOLDOWN_TICKS) {
            long remainSeconds = (SKILL_1_COOLDOWN_TICKS - elapsed + 19) / 20;
            player.displayClientMessage(Component.literal("追踪飓风冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        Player target = findLookTargetPlayer(player);
        if (target == null) {
            player.displayClientMessage(Component.literal("请先把准星对准要攻击的玩家"), true);
            return;
        }

        player.getPersistentData().putLong(SKILL_1_LAST_USE, now);
        broadcastToOpTeam(player, "追踪飓风");

        ServerLevel level = player.serverLevel();

        TrackingTornadoEntity tornado = ModEntities.TRACKING_TORNADO.get().create(level);
        if (tornado == null) {
            return;
        }
        Vec3 spawnPos = player.getEyePosition().add(player.getLookAngle().scale(1.0D));
        tornado.moveTo(spawnPos.x, spawnPos.y - 0.3D, spawnPos.z, player.getYRot(), player.getXRot());
        tornado.setOwner(player);
        tornado.setTarget(target);

        Vec3 initialVelocity = player.getLookAngle().scale(0.3D);
        tornado.setDeltaMovement(initialVelocity);
        level.addFreshEntity(tornado);
        //level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,
        //        target.getX(), target.getY() + 1.0D, target.getZ(),
        //        80, 1.8D, 1.2D, 1.8D, 0.02D);
    }

    private static void useHurricaneBarrier(ServerPlayer player) {
        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(SKILL_2_LAST_USE);
        long elapsed = now - last;

        if (elapsed < SKILL_2_COOLDOWN_TICKS) {
            long remainSeconds = (SKILL_2_COOLDOWN_TICKS - elapsed + 19) / 20;
            player.displayClientMessage(Component.literal("飓风屏障冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        player.getPersistentData().putLong(SKILL_2_LAST_USE, now);

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 10, 1));
        player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 20 * 10, 0));

        ServerLevel level = player.serverLevel();
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,
                player.getX(), player.getY() + 1.0D, player.getZ(),
                120, 1.6D, 1.0D, 1.6D, 0.06D);
    }

    private static Player findLookTargetPlayer(ServerPlayer player) {
        double maxDistance = 256.0D;
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eyePos.add(look.scale(maxDistance));

        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(maxDistance)).inflate(2.0D);
        EntityHitResult hitResult = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                player,
                eyePos,
                end,
                searchBox,
                entity -> entity instanceof Player && entity != player,
                1.0F
        );

        if (hitResult == null) {
            return null;
        }

        Entity hitEntity = hitResult.getEntity();
        if (hitEntity instanceof Player targetPlayer) {
            return targetPlayer;
        }

        return null;
    }

    private static void spawnHurricaneTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double distance = delta.length();
        if (distance < 0.1D) {
            return;
        }

        Vec3 normal = delta.normalize();
        int points = Math.max(12, (int) (distance * 3));

        for (int i = 0; i <= points; i++) {
            double t = i / (double) points;
            Vec3 base = start.add(normal.scale(distance * t));
            double angle = t * Math.PI * 8;
            double radius = 0.45D;

            double swirlX = Math.cos(angle) * radius;
            double swirlZ = Math.sin(angle) * radius;

            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,
                    base.x + swirlX, base.y, base.z + swirlZ,
                    2, 0.02D, 0.02D, 0.02D, 0.001D);
        }
    }

    private static void broadcastToOpTeam(ServerPlayer user, String skillName) {
        String safeName = user.getName().getString().replace("\"", "'");
        String json = "[{\"text\":\"杜比华使用能力：\",\"color\":\"gold\"},"
                + "{\"text\":\"" + skillName + "\",\"color\":\"aqua\"},"
                + "{\"text\":\"（使用者: " + safeName + "）\",\"color\":\"yellow\"}]";

        CommandSourceStack source = user.server.createCommandSourceStack();
        user.server.getCommands().performPrefixedCommand(source, "tellraw @a[team=op] " + json);
    }
}
