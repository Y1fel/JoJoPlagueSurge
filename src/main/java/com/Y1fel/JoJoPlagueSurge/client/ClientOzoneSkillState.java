package com.Y1fel.JoJoPlagueSurge.client;

public final class ClientOzoneSkillState {
    private static boolean skill1Active;
    private static boolean skill2Active;
    private static boolean skill3Active;

    private ClientOzoneSkillState() {
    }

    public static void update(boolean newSkill1Active, boolean newSkill2Active, boolean newSkill3Active) {
        skill1Active = newSkill1Active;
        skill2Active = newSkill2Active;
        skill3Active = newSkill3Active;
    }

    public static boolean isSkillActive(int skillId) {
        return switch (skillId) {
            case 1 -> skill1Active;
            case 2 -> skill2Active;
            case 3 -> skill3Active;
            default -> false;
        };
    }
}
