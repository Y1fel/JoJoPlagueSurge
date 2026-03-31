package com.Y1fel.JoJoPlagueSurge.skill;

public final class BlueHawaiiSkillCatalog {
    public static final int TOOTH_MARK_ID = 1;
    public static final int HUNT_ACTIVATE_ID = 2;
    public static final int HUNT_RELEASE_ID = 3;

    public static final String TOOTH_MARK_ICON = "jojoplaguesurge:textures/item/bloody_tooth.png";
    public static final String HUNT_ACTIVATE_ICON = "jojoplaguesurge:textures/item/bluehawaii_spawn_egg.png";
    public static final String HUNT_RELEASE_ICON = "jojoplaguesurge:textures/item/bluehawaii_stand_disc.png";

    private BlueHawaiiSkillCatalog() {
    }

    public static String getSkillIconPath(int skillId) {
        return switch (skillId) {
            case TOOTH_MARK_ID -> TOOTH_MARK_ICON;
            case HUNT_ACTIVATE_ID -> HUNT_ACTIVATE_ICON;
            case HUNT_RELEASE_ID -> HUNT_RELEASE_ICON;
            default -> TOOTH_MARK_ICON;
        };
    }
}
