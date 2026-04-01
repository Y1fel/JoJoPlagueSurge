package com.Y1fel.JoJoPlagueSurge.item;

import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandType;
import com.Y1fel.JoJoPlagueSurge.network.packet.StandOwnershipLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StandDiscItem extends Item {
    private final StandType standType;

    public StandDiscItem(Properties properties, StandType standType) {
        super(properties);
        this.standType = standType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            StandOwnershipLogic.toggleOwnershipAndStand(serverPlayer, standType);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
