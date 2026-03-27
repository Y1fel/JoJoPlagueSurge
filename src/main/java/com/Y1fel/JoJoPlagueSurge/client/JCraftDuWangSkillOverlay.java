package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import com.mojang.blaze3d.systems.RenderSystem;
import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.client.gui.hud.JCraftAbilityHud;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 杜王技能 HUD（按 JCraftAbilityHud 的显隐与冷却来源逻辑对齐）。
 */
@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JCraftDuWangSkillOverlay {
    private static final int SLOT_SIZE = 22;
    private static final int SLOT_GAP = 6;
    private static final int TOP = 18;

    private static int timeSinceNoCooldowns = 100;

    private JCraftDuWangSkillOverlay() {
    }

    public static void markSkillTriggered(int skillId) {
        // 改为由 JCraft cooldown component 驱动，保留兼容入口。
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.level == null) {
            return;
        }

        LocalPlayer player = mc.player;
        if (!hasOwnedStand(player)) {
            return;
        }

        timeSinceNoCooldowns++;

        double cd1Ratio = getCooldownRemainRatio(CooldownType.STAND_SP1);
        double cd2Ratio = getCooldownRemainRatio(CooldownType.STAND_SP2);
        boolean coolingDown = cd1Ratio > 0.0D || cd2Ratio > 0.0D;

        final boolean peekAllMoves = JClientConfig.getInstance().isIconHudPeekAllMoves();
        float alpha = peekAllMoves ? 0.1F : 0.0F;
        if (coolingDown) {
            timeSinceNoCooldowns = 0;
            alpha = 1.0F;
        }

        if (timeSinceNoCooldowns >= 100 || alpha <= 0.0F) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();
        int baseX = JCraftAbilityHud.getHudX(event.getWindow().getGuiScaledWidth(), 32);
        int y1 = TOP;
        int y2 = TOP + SLOT_SIZE + SLOT_GAP;

        renderSkill(gui, baseX, y1, DuWangSkillCatalog.TRACKING_TORNADO_ID,
                CooldownType.STAND_SP1, cd1Ratio, "special1", alpha);
        renderSkill(gui, baseX, y2, DuWangSkillCatalog.HURRICANE_BARRIER_ID,
                CooldownType.STAND_SP2, cd2Ratio, "special2", alpha);
    }

    private static void renderSkill(
            GuiGraphics gui,
            int x,
            int y,
            int skillId,
            CooldownType cooldownType,
            double remainRatio,
            String fallback,
            float alpha
    ) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        JCraftAbilityHud.renderBorder(gui, x, y);

        ResourceLocation icon = ResourceLocation.tryParse(DuWangSkillCatalog.getSkillIconPath(skillId));
        if (icon != null) {
            JCraftAbilityHud.renderAbsIcon(gui, x, y, icon, fallback);
        }

        int remainTicks = JComponentPlatformUtils.getCooldowns(Minecraft.getInstance().player).getCooldown(cooldownType);
        if (remainTicks > 0 && remainRatio > 0.0D) {
            JCraftAbilityHud.renderCooldown(gui, remainRatio, x, y);
            String remainText = Integer.toString(Mth.ceil(remainTicks / 20.0F));
            int textX = x + (SLOT_SIZE - Minecraft.getInstance().font.width(remainText)) / 2;
            gui.drawString(Minecraft.getInstance().font, remainText, textX, y + 7, withAlpha(0xFFE7E7E7, alpha), true);
        }

        String keyText = JCraftAbilityHud.cooldownTypeToKeybind(cooldownType, true);
        gui.drawString(Minecraft.getInstance().font, keyText, x + SLOT_SIZE + 4, y + 7, withAlpha(0xFFDEE6EF, alpha), false);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static int withAlpha(int argb, float alpha) {
        int a = Mth.clamp((int) (alpha * 255.0F), 0, 255);
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    private static boolean hasOwnedStand(LocalPlayer player) {
        List<DuWangEntity> stands = player.level().getEntitiesOfClass(
                DuWangEntity.class,
                player.getBoundingBox().inflate(64.0D),
                stand -> stand.isOwnedBy(player) && stand.isAlive()
        );
        return !stands.isEmpty();
    }

    private static double getCooldownRemainRatio(CooldownType cooldownType) {
        var cooldowns = JComponentPlatformUtils.getCooldowns(Minecraft.getInstance().player);
        int remain = cooldowns.getCooldown(cooldownType);
        int initial = cooldowns.getInitialDuration(cooldownType);
        if (remain <= 0 || initial <= 0) {
            return 0.0D;
        }
        return Mth.clamp(remain / (double) initial, 0.0D, 1.0D);
    }
}
