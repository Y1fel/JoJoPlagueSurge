package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.network.ModNetwork;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SUseDuWangSkillPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DuWangSkillKeyHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        while (ModKeyMappings.DUWANG_SKILL_1.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new C2SUseDuWangSkillPacket(1));
        }

        while (ModKeyMappings.DUWANG_SKILL_2.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(new C2SUseDuWangSkillPacket(2));
        }
    }
}
