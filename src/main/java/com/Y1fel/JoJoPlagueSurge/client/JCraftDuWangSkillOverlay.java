package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.network.packet.StandSummonLogic;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import com.mojang.blaze3d.systems.RenderSystem;
import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.client.gui.hud.JCraftAbilityHud;
import net.arna.jcraft.common.util.ColorUtils;
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


/**
 * 杜王技能 HUD（按 JCraftAbilityHud 的显隐与冷却来源逻辑对齐）。
 */
@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JCraftDuWangSkillOverlay {
    private static final int SLOT_SIZE = 22;
    private static final int SPACING = 8;
    private static final ResourceLocation EMPTY_GAUGE = ResourceLocation.tryParse("jcraft:textures/gui/empty_gauge.png");
    private static final ResourceLocation FULL_GAUGE = ResourceLocation.tryParse("jcraft:textures/gui/full_gauge.png");
    private static final int GAUGE_WIDTH = 42;
    private static final int GAUGE_HEIGHT = 5;
    private static final int GAUGE_Y_OFFSET = -65;

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
        DuWangEntity stand = getOwnedStand(player);
        if (stand == null) {
            return;
        }
        renderStandGauge(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight(), stand);

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
        int y1 = SPACING * 14;
        int y2 = SPACING * 17;

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
        }

        String keyText = JCraftAbilityHud.cooldownTypeToKeybind(cooldownType, true);
        gui.drawString(
                Minecraft.getInstance().font,
                keyText,
                x,
                y,
                ColorUtils.HSBAtoRGBA(0.3f - (float) remainRatio * 10f / 720f, remainRatio < 1.6 ? 0.0f : 1.0f, 1.0f, alpha * 2.0f)
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static DuWangEntity getOwnedStand(LocalPlayer player) {
        return StandSummonLogic.findNearestOwnedStand(player, DuWangEntity.class);
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

    private static void renderStandGauge(GuiGraphics gui, int screenWidth, int screenHeight, DuWangEntity stand) {
        if (EMPTY_GAUGE == null || FULL_GAUGE == null) {
            return;
        }

        int x = screenWidth / 2 - GAUGE_WIDTH / 2;
        int y = screenHeight + GAUGE_Y_OFFSET;
        float healthRatio = Mth.clamp(stand.getHealth() / stand.getMaxHealth(), 0.0F, 1.0F);
        int fullWidth = Mth.floor(healthRatio * GAUGE_WIDTH);

        RenderSystem.setShaderColor(0.5F, 0.5F, 1.0F, 1.0F);
        gui.blit(EMPTY_GAUGE, x, y, 0, 0, GAUGE_WIDTH, GAUGE_HEIGHT, GAUGE_WIDTH, GAUGE_HEIGHT);
        gui.blit(FULL_GAUGE, x, y, 0, 0, fullWidth, GAUGE_HEIGHT, GAUGE_WIDTH, GAUGE_HEIGHT);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
