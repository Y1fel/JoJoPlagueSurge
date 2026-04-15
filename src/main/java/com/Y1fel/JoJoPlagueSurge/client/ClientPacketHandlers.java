package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.network.packet.S2CSkillCooldownStatePacket;
import com.Y1fel.JoJoPlagueSurge.network.packet.SkillCooldowns;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {
    }

    public static void handleSkillCooldownState(S2CSkillCooldownStatePacket packet) {
        ClientSkillCooldownState.update(SkillCooldowns.DUWANG_SKILL_1, packet.getDuwangSkill1Remaining(), packet.getDuwangSkill1Initial());
        ClientSkillCooldownState.update(SkillCooldowns.DUWANG_SKILL_2, packet.getDuwangSkill2Remaining(), packet.getDuwangSkill2Initial());
        ClientSkillCooldownState.update(SkillCooldowns.BLUE_HAWAII_RELEASE, packet.getBlueHawaiiReleaseRemaining(), packet.getBlueHawaiiReleaseInitial());
        ClientSkillCooldownState.update(SkillCooldowns.OZONE_SKILL_1, packet.getOzoneSkill1Remaining(), packet.getOzoneSkill1Initial());
        ClientSkillCooldownState.update(SkillCooldowns.OZONE_SKILL_2, packet.getOzoneSkill2Remaining(), packet.getOzoneSkill2Initial());
        ClientOzoneSkillState.update(packet.isOzoneSkill1Active(), packet.isOzoneSkill2Active(), packet.isOzoneSkill3Active());
    }
}
