package com.Y1fel.JoJoPlagueSurge.network;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SToggleStandPacket;
import com.Y1fel.JoJoPlagueSurge.network.packet.C2SUseDuWangSkillPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
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
                C2SToggleStandPacket.class,
                C2SToggleStandPacket::encode,
                C2SToggleStandPacket::decode,
                C2SToggleStandPacket::handle);

    }
}
