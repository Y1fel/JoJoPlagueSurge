package com.Y1fel.JoJoPlagueSurge.client;

import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public final class ClientSkillCooldownState {
    private static final Map<String, CooldownEntry> COOLDOWNS = new HashMap<>();

    private ClientSkillCooldownState() {
    }

    public static void update(String id, int remainingTicks, int initialDurationTicks) {
        COOLDOWNS.put(id, new CooldownEntry(remainingTicks, initialDurationTicks));
    }

    public static int getRemainingTicks(String id) {
        return COOLDOWNS.getOrDefault(id, CooldownEntry.EMPTY).remainingTicks();
    }

    public static double getRemainRatio(String id) {
        CooldownEntry entry = COOLDOWNS.getOrDefault(id, CooldownEntry.EMPTY);
        if (entry.remainingTicks() <= 0 || entry.initialDurationTicks() <= 0) {
            return 0.0D;
        }
        return Mth.clamp(entry.remainingTicks() / (double) entry.initialDurationTicks(), 0.0D, 1.0D);
    }

    private record CooldownEntry(int remainingTicks, int initialDurationTicks) {
        private static final CooldownEntry EMPTY = new CooldownEntry(0, 0);
    }
}
