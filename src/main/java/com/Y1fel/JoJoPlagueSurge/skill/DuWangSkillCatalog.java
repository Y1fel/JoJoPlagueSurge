package com.Y1fel.JoJoPlagueSurge.skill;

/**
 * DuWang 技能配置（JCraft 风格）：
 * - 在一个地方统一维护技能 id、显示名、冷却时间，方便后续继续扩展技能栏。
 * - 服务端逻辑与客户端 HUD 共用这份定义，避免“名字/冷却写死在多处”。
 */
public final class DuWangSkillCatalog {
    public static final int STAND_ASSAULT_ID = 1;
    public static final int HURRICANE_BARRIER_ID = 2;

    public static final int STAND_ASSAULT_COOLDOWN_TICKS = 20 * 5;
    public static final int HURRICANE_BARRIER_COOLDOWN_TICKS = 20 * 60;

    private DuWangSkillCatalog() {
    }

    public static String displayNameZh(int skillId) {
        return switch (skillId) {
            case STAND_ASSAULT_ID -> "替身突击";
            case HURRICANE_BARRIER_ID -> "飓风屏障";
            default -> "未知技能";
        };
    }
}
