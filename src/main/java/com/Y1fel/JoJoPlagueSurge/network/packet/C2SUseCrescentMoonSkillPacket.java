package com.Y1fel.JoJoPlagueSurge.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SUseCrescentMoonSkillPacket {
    public static void encode(C2SUseCrescentMoonSkillPacket packet, FriendlyByteBuf buf) {
    }

    public static C2SUseCrescentMoonSkillPacket decode(FriendlyByteBuf buf) {
        return new C2SUseCrescentMoonSkillPacket();
    }

    public static void handle(C2SUseCrescentMoonSkillPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CrescentMoonSkillLogic.useSkill(player);
            }
        });
        context.setPacketHandled(true);
    }
}
