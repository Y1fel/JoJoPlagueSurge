package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandManager;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class StandSummonLogic {
    private StandSummonLogic() {
    }

    public static void toggleDuWangStand(ServerPlayer player) {
        StandManager.toggleStand(player, StandType.DUWANG);
    }

    public static void toggleBlueHawaiiStand(ServerPlayer player) {
        StandManager.toggleStand(player, StandType.BLUE_HAWAII);
    }

    public static void toggleOwnedStand(ServerPlayer player) {
        StandManager.toggleOwnedStand(player);
    }

    public static void toggleStand(ServerPlayer player, StandType standType) {
        StandManager.toggleStand(player, standType);
    }

    public static <T extends StandEntity> List<T> findOwnedStands(Player player, Class<T> standClass) {
        return StandManager.findOwnedStands(player, standClass);
    }

    public static <T extends StandEntity> T findNearestOwnedStand(Player player, Class<T> standClass) {
        return StandManager.findNearestOwnedStand(player, standClass);
    }
}
