package com.Y1fel.JoJoPlagueSurge.network;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SApplyStepUpPacket;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SUseCrescentMoonSkillPacket;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SToggleOwnedStandPacket;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SUseDuWangSkillPacket;
import com.Y1fel.JoJoPlagueSurge.network.packet.S2CSkillCooldownStatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(ModEntrance.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++,
                C2SUseDuWangSkillPacket.class,
                C2SUseDuWangSkillPacket::encode,
                C2SUseDuWangSkillPacket::decode,
                C2SUseDuWangSkillPacket::handle);
        CHANNEL.registerMessage(id++,
                C2SToggleOwnedStandPacket.class,
                C2SToggleOwnedStandPacket::encode,
                C2SToggleOwnedStandPacket::decode,
                C2SToggleOwnedStandPacket::handle);
        CHANNEL.registerMessage(id++,
                C2SApplyStepUpPacket.class,
                C2SApplyStepUpPacket::encode,
                C2SApplyStepUpPacket::decode,
                C2SApplyStepUpPacket::handle);
        CHANNEL.registerMessage(id++,
                C2SUseCrescentMoonSkillPacket.class,
                C2SUseCrescentMoonSkillPacket::encode,
                C2SUseCrescentMoonSkillPacket::decode,
                C2SUseCrescentMoonSkillPacket::handle);
        CHANNEL.messageBuilder(S2CSkillCooldownStatePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSkillCooldownStatePacket::encode)
                .decoder(S2CSkillCooldownStatePacket::decode)
                .consumerMainThread(S2CSkillCooldownStatePacket::handle)
                .add();

    }
}
