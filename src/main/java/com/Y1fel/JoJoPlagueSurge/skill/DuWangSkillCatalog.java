package com.Y1fel.JoJoPlagueSurge.skill;

/**
 * DuWang 技能配置：
 * - 在一个地方统一维护技能 id、显示名、冷却时间，方便后续继续扩展技能栏。
 * - 服务端逻辑与客户端 HUD 共用这份定义，避免“名字/冷却写死在多处”。
 */
public final class DuWangSkillCatalog {
    // 以下图标路径作为 JCraft UI 接口占位保留；资源可后续替换为独立技能图标。
    public static final String TRACKING_TRONADO_ICON = "jojoplaguesurge:textures/skill/tracking_tornado.png";
    public static final String HURRICANE_BARRIER_ICON = "jojoplaguesurge:textures/skill/dbh_sheild.png";
    public static final int TRACKING_TORNADO_ID = 1;
    public static final int HURRICANE_BARRIER_ID = 2;

    public static final int TRACKING_TORNADO_COOLDOWN_TICKS = 20 * 10;
    public static final int HURRICANE_BARRIER_COOLDOWN_TICKS = 20 * 60;

    private DuWangSkillCatalog() {
    }



    public static String getSkillIconPath(int skillId) {
        return switch (skillId) {
            case TRACKING_TORNADO_ID -> TRACKING_TRONADO_ICON;
            case HURRICANE_BARRIER_ID -> HURRICANE_BARRIER_ICON;
            default -> TRACKING_TRONADO_ICON;
        };
    }


    public static String displayNameZh(int skillId) {
        return switch (skillId) {
            case TRACKING_TORNADO_ID -> "追踪飓风";
            case HURRICANE_BARRIER_ID -> "飓风屏障";
            default -> "未知技能";
        };
    }
}
