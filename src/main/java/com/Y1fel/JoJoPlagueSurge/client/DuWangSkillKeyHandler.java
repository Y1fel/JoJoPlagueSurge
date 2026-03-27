package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.network.ModNetwork;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SUseDuWangSkillPacket;
import net.arna.jcraft.client.JCraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DuWangSkillKeyHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (JCraftClient.special1Key != null && JCraftClient.special1Key.isPressedThisTick()) {
            ModNetwork.CHANNEL.sendToServer(new C2SUseDuWangSkillPacket(1));
            JCraftDuWangSkillOverlay.markSkillTriggered(1);
        }

        if (JCraftClient.special2Key != null && JCraftClient.special2Key.isPressedThisTick()) {
            ModNetwork.CHANNEL.sendToServer(new C2SUseDuWangSkillPacket(2));
            JCraftDuWangSkillOverlay.markSkillTriggered(2);
        }

    }
}
