package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.Config;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.trackingtornado.TrackingTornadoEntity;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

public class DuWangSkillLogic {
    private static final String SKILL_1_LAST_USE = "jojoplaguesurge.duwang_skill_1_last_use";
    private static final String SKILL_2_LAST_USE = "jojoplaguesurge.duwang_skill_2_last_use";

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
        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(SKILL_1_LAST_USE);
        long elapsed = now - last;
        Config.duWangSkill1AllowAnyLivingTargetForTest=true;

        if (elapsed < SKILL_1_COOLDOWN_TICKS) {
            long remainSeconds = (SKILL_1_COOLDOWN_TICKS - elapsed + 19) / 20;
            player.displayClientMessage(Component.literal("追踪飓风冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        String selector = "@e[tag=duwang_target,limit=1,sort=nearest]";
        LivingEntity target = findLookTarget(player, selector);
        if (target == null) {
            player.displayClientMessage(Component.literal("Invalid target"), false);
            return;
        }

        player.getPersistentData().putLong(SKILL_1_LAST_USE, now);
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

        Vec3 initialVelocity = launchDirection.normalize().scale(0.3D);
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

    private static LivingEntity findLookTarget(ServerPlayer player, String selector) {
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
                + "{\"text\":\"（使用者: " + safeName + "）\",\"color\":\"yellow\"}]";

        CommandSourceStack source = user.server.createCommandSourceStack();
        user.server.getCommands().performPrefixedCommand(source, "tellraw @a[team=op] " + json);
    }

    private static DuWangEntity findOwnedStand(ServerPlayer player) {
        List<DuWangEntity> stands = player.serverLevel().getEntitiesOfClass(
                DuWangEntity.class,
                player.getBoundingBox().inflate(64.0D),
                stand -> stand.isAlive() && stand.isOwnedBy(player)
        );
        if (stands.isEmpty()) {
            return null;
        }
        DuWangEntity nearest = stands.get(0);
        double nearestDist = nearest.distanceToSqr(player);
        for (int i = 1; i < stands.size(); i++) {
            DuWangEntity current = stands.get(i);
            double dist = current.distanceToSqr(player);
            if (dist < nearestDist) {
                nearest = current;
                nearestDist = dist;
            }
        }
        return nearest;
    }
}
