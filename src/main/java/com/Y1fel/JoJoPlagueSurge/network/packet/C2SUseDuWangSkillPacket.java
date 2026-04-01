package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SUseDuWangSkillPacket {
    private final int skillId;

    public C2SUseDuWangSkillPacket(int skillId) {
        this.skillId = skillId;
    }

    public static void encode(C2SUseDuWangSkillPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.skillId);
    }

    public static C2SUseDuWangSkillPacket decode(FriendlyByteBuf buf) {
        return new C2SUseDuWangSkillPacket(buf.readInt());
    }

    public static void handle(C2SUseDuWangSkillPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                if (StandManager.findNearestOwnedStand(context.getSender(), BlueHawaiiEntity.class) != null) {
                    BlueHawaiiSkillLogic.handleSkillUse(context.getSender(), packet.skillId);
                } else if (StandManager.findNearestOwnedStand(context.getSender(), DuWangEntity.class) != null) {
                    DuWangSkillLogic.handleSkillUse(context.getSender(), packet.skillId);
                } else if (OzoneSkillLogic.hasOzoneHouse(context.getSender())) {
                    OzoneSkillLogic.handleSkillUse(context.getSender(), packet.skillId);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
