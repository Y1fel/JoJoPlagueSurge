package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.Config;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;


public class DuWangSkillLogic {
    private static final String SKILL_1_LAST_USE = "jojoplaguesurge.duwang_skill_1_last_use";
    private static final String SKILL_2_LAST_USE = "jojoplaguesurge.duwang_skill_2_last_use";

    private DuWangSkillLogic() {
    }

    public static void handleSkillUse(ServerPlayer player, int skillId) {
        if (player instanceof FakePlayer) {
            return;
        }

        if (skillId == DuWangSkillCatalog.STAND_ASSAULT_ID) {
            commandStandAttack(player);
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

        DuWangEntity stand = findOwnedStand(player);
        if (stand == null) {
            player.displayClientMessage(Component.literal("请先按 G 召唤替身"), true);
            return;
        }

        LivingEntity target = findCrosshairTarget(player, 18.0D);
        if (target == null) {
            String selector = "@e[tag=duwang_target,limit=1,sort=nearest]";
            target = findLookTarget(player, selector);
        }
        if (target == null) {
            player.displayClientMessage(Component.literal("没有找到可攻击目标（请用准星对准生物）"), true);
            return;
        }

        player.getPersistentData().putLong(SKILL_1_LAST_USE, now);
        stand.setTarget(target);
        player.setLastHurtMob(target);

        player.displayClientMessage(Component.literal("替身已锁定目标: " + target.getName().getString()), true);
        broadcastToOpTeam(player, DuWangSkillCatalog.displayNameZh(DuWangSkillCatalog.STAND_ASSAULT_ID));
    }

    private static DuWangEntity findOwnedStand(ServerPlayer player) {
        List<DuWangEntity> stands = player.serverLevel().getEntitiesOfClass(
                DuWangEntity.class,
                player.getBoundingBox().inflate(64.0D),
                stand -> stand.isOwnedBy(player) && stand.isAlive()
        );
        return stands.isEmpty() ? null : stands.get(0);
    }

    private static LivingEntity findCrosshairTarget(ServerPlayer player, double maxDistance) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 reachPos = eyePos.add(look.scale(maxDistance));
        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(maxDistance)).inflate(1.0D);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                player,
                eyePos,
                reachPos,
                searchBox,
                candidate -> candidate instanceof LivingEntity living && living.isAlive() && candidate != player,
                maxDistance * maxDistance
        );
        Entity hit = hitResult != null ? hitResult.getEntity() : null;

        return hit instanceof LivingEntity living ? living : null;
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
