package com.Y1fel.JoJoPlagueSurge.network.packet;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class CrescentMoonSkillLogic {
    private static final String OWNED = "jojoplaguesurge.crescent_moon.owned";
    private static final ResourceLocation HEAVY_ID =
            ResourceLocation.fromNamespaceAndPath("more_potion_effects", "heavy");
    private static final ResourceLocation BLEEDING_ID =
            ResourceLocation.fromNamespaceAndPath("more_potion_effects", "bleeding");
    private static final int HEAVY_DURATION_TICKS = 20 * 5;
    private static final int BLEEDING_DURATION_TICKS = 20;
    private static final int COOLDOWN_TICKS = 20 * 8;
    private static final double RADIUS = 5.0D;

    private CrescentMoonSkillLogic() {
    }

    public static boolean ownsSkill(ServerPlayer player) {
        return player.getPersistentData().getBoolean(OWNED);
    }

    public static void toggleOwnership(ServerPlayer player) {
        if (ownsSkill(player)) {
            player.getPersistentData().remove(OWNED);
            player.displayClientMessage(Component.literal("已失去新月技能使用权"), true);
            return;
        }

        player.getPersistentData().putBoolean(OWNED, true);
        player.displayClientMessage(Component.literal("已获得新月技能使用权"), true);
    }

    public static void useSkill(ServerPlayer player) {
        if (!ownsSkill(player)) {
            player.displayClientMessage(Component.literal("你还没有新月技能使用权"), true);
            return;
        }

        int cooldown = SkillCooldowns.getRemainingTicks(player, SkillCooldowns.CRESCENT_MOON_SKILL);
        if (cooldown > 0) {
            long remainSeconds = (cooldown + 19L) / 20L;
            player.displayClientMessage(Component.literal("新月技能冷却中，无法释放，还需 " + remainSeconds + " 秒"), true);
            return;
        }

        MobEffect heavy = ForgeRegistries.MOB_EFFECTS.getValue(HEAVY_ID);
        MobEffect bleeding = ForgeRegistries.MOB_EFFECTS.getValue(BLEEDING_ID);
        List<ServerPlayer> targets = player.serverLevel().getEntitiesOfClass(
                ServerPlayer.class,
                player.getBoundingBox().inflate(RADIUS),
                candidate -> candidate != player
                        && !candidate.isSpectator()
                        && candidate.distanceToSqr(player) <= RADIUS * RADIUS
        );

        for (ServerPlayer target : targets) {
            if (heavy != null) {
                target.addEffect(new MobEffectInstance(heavy, HEAVY_DURATION_TICKS, 0, false, true, true));
            }
            if (bleeding != null) {
                target.addEffect(new MobEffectInstance(bleeding, BLEEDING_DURATION_TICKS, 0, false, true, true));
            }
        }

        SkillCooldowns.startCooldown(player, SkillCooldowns.CRESCENT_MOON_SKILL, COOLDOWN_TICKS);
    }
}
