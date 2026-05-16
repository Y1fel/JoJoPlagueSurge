package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandType;
import com.Y1fel.JoJoPlagueSurge.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public final class SkillCooldowns {
    public static final String DUWANG_SKILL_1 = "duwang_skill_1";
    public static final String DUWANG_SKILL_2 = "duwang_skill_2";
    public static final String BLUE_HAWAII_RELEASE = "blue_hawaii_release";
    public static final String OZONE_SKILL_1 = "ozone_skill_1";
    public static final String OZONE_SKILL_2 = "ozone_skill_2";
    public static final String STEP_UP_TEST = "step_up_test";
    public static final String CRESCENT_MOON_SKILL = "crescent_moon_skill";

    private static final String PREFIX = "jojoplaguesurge.cooldown.";
    private static final String UNTIL_SUFFIX = ".until";
    private static final String DURATION_SUFFIX = ".duration";

    private SkillCooldowns() {
    }

    public static int getRemainingTicks(Player player, String cooldownId) {
        long until = player.getPersistentData().getLong(key(cooldownId, UNTIL_SUFFIX));
        long remain = until - player.level().getGameTime();
        return (int) Math.max(remain, 0L);
    }

    public static int getInitialDuration(Player player, String cooldownId) {
        if (getRemainingTicks(player, cooldownId) <= 0) {
            return 0;
        }
        return player.getPersistentData().getInt(key(cooldownId, DURATION_SUFFIX));
    }

    public static void startCooldown(Player player, String cooldownId, int durationTicks) {
        player.getPersistentData().putLong(key(cooldownId, UNTIL_SUFFIX), player.level().getGameTime() + durationTicks);
        player.getPersistentData().putInt(key(cooldownId, DURATION_SUFFIX), durationTicks);
    }

    public static boolean isOnCooldown(Player player, String cooldownId) {
        return getRemainingTicks(player, cooldownId) > 0;
    }

    public static void clearCooldown(Player player, String cooldownId) {
        player.getPersistentData().remove(key(cooldownId, UNTIL_SUFFIX));
        player.getPersistentData().remove(key(cooldownId, DURATION_SUFFIX));
    }

    public static void clearCooldownsForStand(ServerPlayer player, StandType standType) {
        for (String cooldownId : cooldownIdsForStand(standType)) {
            clearCooldown(player, cooldownId);
        }
        sync(player);
    }

    public static void sync(ServerPlayer player) {
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new S2CSkillCooldownStatePacket(
                        getRemainingTicks(player, DUWANG_SKILL_1),
                        getInitialDuration(player, DUWANG_SKILL_1),
                        getRemainingTicks(player, DUWANG_SKILL_2),
                        getInitialDuration(player, DUWANG_SKILL_2),
                        getRemainingTicks(player, BLUE_HAWAII_RELEASE),
                        getInitialDuration(player, BLUE_HAWAII_RELEASE),
                        getRemainingTicks(player, OZONE_SKILL_1),
                        getInitialDuration(player, OZONE_SKILL_1),
                        getRemainingTicks(player, OZONE_SKILL_2),
                        getInitialDuration(player, OZONE_SKILL_2),
                        OzoneSkillLogic.isSkill1Active(player),
                        OzoneSkillLogic.isSkill2Active(player),
                        OzoneSkillLogic.isSkill3Active(player)
                )
        );
    }

    private static String key(String cooldownId, String suffix) {
        return PREFIX + cooldownId + suffix;
    }

    private static String[] cooldownIdsForStand(StandType standType) {
        return switch (standType) {
            case DUWANG -> new String[]{DUWANG_SKILL_1, DUWANG_SKILL_2};
            case BLUE_HAWAII -> new String[]{BLUE_HAWAII_RELEASE};
        };
    }
}
