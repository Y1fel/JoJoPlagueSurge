package com.Y1fel.JoJoPlagueSurge.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SToggleStandPacket {
    public C2SToggleStandPacket() {
    }

    public static void encode(C2SToggleStandPacket packet, FriendlyByteBuf buf) {
    }

    public static C2SToggleStandPacket decode(FriendlyByteBuf buf) {
        return new C2SToggleStandPacket();
    }

    public static void handle(C2SToggleStandPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                StandSummonLogic.toggleDuWangStand(context.getSender());
            }
        });
        context.setPacketHandled(true);
    }
}
