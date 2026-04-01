package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandManager;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public final class StandOwnershipLogic {
    private StandOwnershipLogic() {
    }

    @Nullable
    public static StandType getOwnedStandType(Player player) {
        return StandManager.getOwnedStandType(player);
    }

    public static boolean ownsStandType(Player player, StandType type) {
        return StandManager.ownsStandType(player, type);
    }

    public static void toggleOwnershipAndStand(ServerPlayer player, StandType type) {
        StandManager.toggleOwnershipAndStand(player, type);
    }

    public static void toggleOwnership(ServerPlayer player, StandType type) {
        StandManager.toggleOwnership(player, type);
    }

    public static void clearOwnership(ServerPlayer player) {
        StandManager.clearOwnership(player);
    }

    public static void dismissAllSummonedStands(ServerPlayer player) {
        StandManager.dismissAll(player);
    }
}
