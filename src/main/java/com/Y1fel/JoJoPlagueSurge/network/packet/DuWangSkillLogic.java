package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.Config;
import com.Y1fel.JoJoPlagueSurge.compat.JCraftCompat;
import com.Y1fel.JoJoPlagueSurge.entity.ModEntities;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;


public class DuWangSkillLogic {
    private static final String SKILL_1_LAST_USE = "jojoplaguesurge.duwang_skill_1_last_use";
    private static final String SKILL_2_LAST_USE = "jojoplaguesurge.duwang_skill_2_last_use";
    private static final int SKILL_1_COOLDOWN_TICKS = 20 * 5;
    private static final int SKILL_2_COOLDOWN_TICKS = 20 * 60;

    private DuWangSkillLogic() {
    }

    public static void handleSkillUse(ServerPlayer player, int skillId) {
        if (player instanceof FakePlayer) {
            return;
        }

        // 优先尝试调用 JCraft 的技能实现（如果模组与 API 可用）。
        if (JCraftCompat.tryUseDuWangSkill(player, skillId)) {
            return;
        }

        if (skillId == DuWangSkillCatalog.STAND_ASSAULT_ID) {
            useTrackingHurricane(player);
        } else if (skillId == DuWangSkillCatalog.HURRICANE_BARRIER_ID) {
            useHurricaneBarrier(player);
        }
    }

    private static void commandStandAttack(ServerPlayer player) {
        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(SKILL_1_LAST_USE);
        long elapsed = now - last;
        Config.duWangSkill1AllowAnyLivingTargetForTest = true;

        if (elapsed < DuWangSkillCatalog.STAND_ASSAULT_COOLDOWN_TICKS) {
            long remainSeconds = (DuWangSkillCatalog.STAND_ASSAULT_COOLDOWN_TICKS - elapsed + 19) / 20;
            player.displayClientMessage(Component.literal(DuWangSkillCatalog.displayNameZh(DuWangSkillCatalog.STAND_ASSAULT_ID)
                    + "冷却中，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        String selector = "@e[tag=duwang_target,limit=1,sort=nearest]";
        LivingEntity target = findLookTarget(player, selector);
        if (target == null) {
            player.displayClientMessage(Component.literal("No target found!"), true);
            return;
        }

        player.getPersistentData().putLong(SKILL_1_LAST_USE, now);
        broadcastToOpTeam(player, DuWangSkillCatalog.displayNameZh(DuWangSkillCatalog.STAND_ASSAULT_ID));

        player.displayClientMessage(Component.literal("替身已锁定目标: " + target.getName().getString()), true);
        broadcastToOpTeam(player, DuWangSkillCatalog.displayNameZh(DuWangSkillCatalog.STAND_ASSAULT_ID));
    }

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
    }

    private static void useHurricaneBarrier(ServerPlayer player) {
        long now = player.level().getGameTime();
        long last = player.getPersistentData().getLong(SKILL_2_LAST_USE);
        long elapsed = now - last;

        if (elapsed < DuWangSkillCatalog.HURRICANE_BARRIER_COOLDOWN_TICKS) {
            long remainSeconds = (DuWangSkillCatalog.HURRICANE_BARRIER_COOLDOWN_TICKS - elapsed + 19) / 20;
            player.displayClientMessage(Component.literal(DuWangSkillCatalog.displayNameZh(DuWangSkillCatalog.HURRICANE_BARRIER_ID)
                    + "冷却中，还需 " + remainSeconds + " 秒"), true);
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
        try{
            StringReader reader = new StringReader(selector);
            EntitySelectorParser parser = new EntitySelectorParser(reader,true);
            EntitySelector entitySelector = parser.parse();

            List<? extends Entity> entities = entitySelector.findEntities(player.createCommandSourceStack());
            for(Entity entity : entities){
                if(entity instanceof LivingEntity living && living.isAlive() && entity!=player ){
                    return living;
                }
            }

        }catch (CommandSyntaxException e){
            player.displayClientMessage(Component.literal("Invalid Selector"), true);
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
}
