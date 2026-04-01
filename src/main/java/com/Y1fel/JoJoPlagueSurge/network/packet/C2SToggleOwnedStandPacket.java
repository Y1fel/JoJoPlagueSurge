package com.Y1fel.JoJoPlagueSurge.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SToggleOwnedStandPacket {
    public static void encode(C2SToggleOwnedStandPacket packet, FriendlyByteBuf buf) {
    }

    public static C2SToggleOwnedStandPacket decode(FriendlyByteBuf buf) {
        return new C2SToggleOwnedStandPacket();
    }

    public static void handle(C2SToggleOwnedStandPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                StandSummonLogic.toggleOwnedStand(context.getSender());
            }
        });
        context.setPacketHandled(true);
    }
}
