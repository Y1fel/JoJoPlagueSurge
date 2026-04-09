package com.Y1fel.JoJoPlagueSurge.skill;

public final class OzoneSkillCatalog {
    public static final int MARK_TARGET_1_ID = 1;
    public static final int MARK_TARGET_2_ID = 2;

    public static final String MARK_TARGET_1_ICON = "jojoplaguesurge:textures/skill/ozone_skill1.png";
    public static final String MARK_TARGET_2_ICON = "jojoplaguesurge:textures/skill/ozone_skill2.png";

    private OzoneSkillCatalog() {
    }

    public static String getSkillIconPath(int skillId) {
        return switch (skillId) {
            case MARK_TARGET_1_ID -> MARK_TARGET_1_ICON;
            case MARK_TARGET_2_ID -> MARK_TARGET_2_ICON;
            default -> MARK_TARGET_1_ICON;
        };
    }
}
