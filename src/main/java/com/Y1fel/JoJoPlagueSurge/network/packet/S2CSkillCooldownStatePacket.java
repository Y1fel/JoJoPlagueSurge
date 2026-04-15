package com.Y1fel.JoJoPlagueSurge.network.packet;

import com.Y1fel.JoJoPlagueSurge.client.ClientPacketHandlers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSkillCooldownStatePacket {
    private final int duwangSkill1Remaining;
    private final int duwangSkill1Initial;
    private final int duwangSkill2Remaining;
    private final int duwangSkill2Initial;
    private final int blueHawaiiReleaseRemaining;
    private final int blueHawaiiReleaseInitial;
    private final int ozoneSkill1Remaining;
    private final int ozoneSkill1Initial;
    private final int ozoneSkill2Remaining;
    private final int ozoneSkill2Initial;
    private final boolean ozoneSkill1Active;
    private final boolean ozoneSkill2Active;
    private final boolean ozoneSkill3Active;

    public S2CSkillCooldownStatePacket(
            int duwangSkill1Remaining,
            int duwangSkill1Initial,
            int duwangSkill2Remaining,
            int duwangSkill2Initial,
            int blueHawaiiReleaseRemaining,
            int blueHawaiiReleaseInitial,
            int ozoneSkill1Remaining,
            int ozoneSkill1Initial,
            int ozoneSkill2Remaining,
            int ozoneSkill2Initial,
            boolean ozoneSkill1Active,
            boolean ozoneSkill2Active,
            boolean ozoneSkill3Active
    ) {
        this.duwangSkill1Remaining = duwangSkill1Remaining;
        this.duwangSkill1Initial = duwangSkill1Initial;
        this.duwangSkill2Remaining = duwangSkill2Remaining;
        this.duwangSkill2Initial = duwangSkill2Initial;
        this.blueHawaiiReleaseRemaining = blueHawaiiReleaseRemaining;
        this.blueHawaiiReleaseInitial = blueHawaiiReleaseInitial;
        this.ozoneSkill1Remaining = ozoneSkill1Remaining;
        this.ozoneSkill1Initial = ozoneSkill1Initial;
        this.ozoneSkill2Remaining = ozoneSkill2Remaining;
        this.ozoneSkill2Initial = ozoneSkill2Initial;
        this.ozoneSkill1Active = ozoneSkill1Active;
        this.ozoneSkill2Active = ozoneSkill2Active;
        this.ozoneSkill3Active = ozoneSkill3Active;
    }

    public static void encode(S2CSkillCooldownStatePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.duwangSkill1Remaining);
        buf.writeInt(packet.duwangSkill1Initial);
        buf.writeInt(packet.duwangSkill2Remaining);
        buf.writeInt(packet.duwangSkill2Initial);
        buf.writeInt(packet.blueHawaiiReleaseRemaining);
        buf.writeInt(packet.blueHawaiiReleaseInitial);
        buf.writeInt(packet.ozoneSkill1Remaining);
        buf.writeInt(packet.ozoneSkill1Initial);
        buf.writeInt(packet.ozoneSkill2Remaining);
        buf.writeInt(packet.ozoneSkill2Initial);
        buf.writeBoolean(packet.ozoneSkill1Active);
        buf.writeBoolean(packet.ozoneSkill2Active);
        buf.writeBoolean(packet.ozoneSkill3Active);
    }

    public static S2CSkillCooldownStatePacket decode(FriendlyByteBuf buf) {
        return new S2CSkillCooldownStatePacket(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(S2CSkillCooldownStatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleSkillCooldownState(packet)));
        context.setPacketHandled(true);
    }

    public int getDuwangSkill1Remaining() {
        return duwangSkill1Remaining;
    }

    public int getDuwangSkill1Initial() {
        return duwangSkill1Initial;
    }

    public int getDuwangSkill2Remaining() {
        return duwangSkill2Remaining;
    }

    public int getDuwangSkill2Initial() {
        return duwangSkill2Initial;
    }

    public int getBlueHawaiiReleaseRemaining() {
        return blueHawaiiReleaseRemaining;
    }

    public int getBlueHawaiiReleaseInitial() {
        return blueHawaiiReleaseInitial;
    }

    public int getOzoneSkill1Remaining() {
        return ozoneSkill1Remaining;
    }

    public int getOzoneSkill1Initial() {
        return ozoneSkill1Initial;
    }

    public int getOzoneSkill2Remaining() {
        return ozoneSkill2Remaining;
    }

    public int getOzoneSkill2Initial() {
        return ozoneSkill2Initial;
    }

    public boolean isOzoneSkill1Active() {
        return ozoneSkill1Active;
    }

    public boolean isOzoneSkill2Active() {
        return ozoneSkill2Active;
    }

    public boolean isOzoneSkill3Active() {
        return ozoneSkill3Active;
    }
}
