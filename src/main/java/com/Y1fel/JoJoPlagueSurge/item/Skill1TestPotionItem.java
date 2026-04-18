package com.Y1fel.JoJoPlagueSurge.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class Skill1TestPotionItem extends Item {
    private static final String ACTIVE_UNTIL = "jojoplaguesurge.skill1_test_potion.active_until";
    private static final String INJURY_OUTBURST_APPLIED = "jojoplaguesurge.skill1_test_potion.injury_outburst_applied";
    private static final String BLEEDING_APPLIED = "jojoplaguesurge.skill1_test_potion.bleeding_applied";

    private static final int DURATION_TICKS = 20 * 60;
    private static final int INJURY_OUTBURST_START_TICKS = 20 * 10;
    private static final int INJURY_OUTBURST_DURATION_TICKS = 20 * 50;
    private static final int BLEEDING_START_TICKS = 20 * 45;
    private static final int BLEEDING_DURATION_TICKS = DURATION_TICKS - BLEEDING_START_TICKS;

    public Skill1TestPotionItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            activate(serverPlayer);
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
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.jojoplaguesurge.skill_1_test_potion.tooltip")
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
    }

    public static void onServerPlayerTick(ServerPlayer player) {
        if (!player.getPersistentData().contains(ACTIVE_UNTIL)) {
            return;
        }

        long activeUntil = player.getPersistentData().getLong(ACTIVE_UNTIL);
        long now = player.level().getGameTime();
        if (now >= activeUntil) {
            clearState(player);
            return;
        }

        long elapsedTicks = Math.max(0L, DURATION_TICKS - Math.max(activeUntil - now, 0L));
        if (elapsedTicks >= INJURY_OUTBURST_START_TICKS
                && !player.getPersistentData().getBoolean(INJURY_OUTBURST_APPLIED)) {
            applyMorePotionEffect(player, "injury_outburst", INJURY_OUTBURST_DURATION_TICKS, 0);
            player.getPersistentData().putBoolean(INJURY_OUTBURST_APPLIED, true);
        }

        if (elapsedTicks >= BLEEDING_START_TICKS
                && !player.getPersistentData().getBoolean(BLEEDING_APPLIED)) {
            applyMorePotionEffect(player, "bleeding", BLEEDING_DURATION_TICKS, 0);
            player.getPersistentData().putBoolean(BLEEDING_APPLIED, true);
        }
    }

    private static void activate(ServerPlayer player) {
        player.getPersistentData().putLong(ACTIVE_UNTIL, player.level().getGameTime() + DURATION_TICKS);
        player.getPersistentData().remove(INJURY_OUTBURST_APPLIED);
        player.getPersistentData().remove(BLEEDING_APPLIED);
        applyMorePotionEffect(player, "heavy", DURATION_TICKS, 0);
    }

    private static void clearState(ServerPlayer player) {
        player.getPersistentData().remove(ACTIVE_UNTIL);
        player.getPersistentData().remove(INJURY_OUTBURST_APPLIED);
        player.getPersistentData().remove(BLEEDING_APPLIED);
    }

    private static void applyMorePotionEffect(LivingEntity entity, String effectId, int durationTicks, int amplifier) {
        if (durationTicks <= 0) {
            return;
        }

        MobEffect effect = findMorePotionEffect(effectId);
        if (effect != null) {
            entity.addEffect(new MobEffectInstance(effect, durationTicks, amplifier, false, false, false));
        }
    }

    @Nullable
    private static MobEffect findMorePotionEffect(String path) {
        return ForgeRegistries.MOB_EFFECTS.getValue(
                ResourceLocation.fromNamespaceAndPath("more_potion_effects", path)
        );
    }
}
