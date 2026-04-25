package com.Y1fel.JoJoPlagueSurge.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class C2SApplyStepUpPacket {
    private static final ResourceLocation STEP_UP_ID =
            ResourceLocation.fromNamespaceAndPath("more_potion_effects", "step_up");
    private static final int DURATION_TICKS = 40;
    private static final int AMPLIFIER = 2;
    private static final int COOLDOWN_TICKS = 600;

    public static void encode(C2SApplyStepUpPacket packet, FriendlyByteBuf buf) {
    }

    public static C2SApplyStepUpPacket decode(FriendlyByteBuf buf) {
        return new C2SApplyStepUpPacket();
    }

    public static void handle(C2SApplyStepUpPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            int cooldown = SkillCooldowns.getRemainingTicks(player, SkillCooldowns.STEP_UP_TEST);
            if (cooldown > 0) {
                long remainSeconds = (cooldown + 19L) / 20L;
                player.displayClientMessage(Component.literal("高踏冷却中，还需 " + remainSeconds + " 秒"), true);
                return;
            }

            MobEffect stepUp = ForgeRegistries.MOB_EFFECTS.getValue(STEP_UP_ID);
            if (stepUp != null) {
                player.addEffect(new MobEffectInstance(stepUp, DURATION_TICKS, AMPLIFIER, false, true, true));
                SkillCooldowns.startCooldown(player, SkillCooldowns.STEP_UP_TEST, COOLDOWN_TICKS);
            }
        });
        context.setPacketHandled(true);
    }
}
