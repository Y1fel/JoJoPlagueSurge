package com.Y1fel.JoJoPlagueSurge.item;

import com.Y1fel.JoJoPlagueSurge.effect.ModEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class SpiritVisionPotionItem extends Item {
    private static final String SPIRIT_VISION_POTION_TAG = "SpiritVisionPotion";
    private static final int SPIRIT_VISION_DURATION_TICKS = 20 * 60 * 3;

    public SpiritVisionPotionItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ItemStack getDefaultInstance() {
        return ensureSpiritVisionTag(PotionUtils.setPotion(super.getDefaultInstance(), net.minecraft.world.item.alchemy.Potions.WATER));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ensureSpiritVisionTag(stack);
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) {
            entity.addEffect(new MobEffectInstance(ModEffects.SPIRIT_VISION.get(), SPIRIT_VISION_DURATION_TICKS, 0));
        }

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (stack.isEmpty()) {
                return bottle;
            }

            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ensureSpiritVisionTag(stack);
        return net.minecraft.world.item.ItemUtils.startUsingInstantly(level, player, hand);
    }

    private static ItemStack ensureSpiritVisionTag(ItemStack stack) {
        stack.getOrCreateTag().putBoolean(SPIRIT_VISION_POTION_TAG, true);
        return stack;
    }
}
