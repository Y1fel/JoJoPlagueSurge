package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import com.Y1fel.JoJoPlagueSurge.network.packet.OzoneSkillLogic;
import com.Y1fel.JoJoPlagueSurge.network.packet.StandSummonLogic;
import com.Y1fel.JoJoPlagueSurge.skill.BlueHawaiiSkillCatalog;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import com.Y1fel.JoJoPlagueSurge.skill.OzoneSkillCatalog;
import com.mojang.blaze3d.systems.RenderSystem;
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

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JCraftDuWangSkillOverlay {
    private static final int SPACING = 8;
    private static final ResourceLocation EMPTY_GAUGE = ResourceLocation.tryParse("jcraft:textures/gui/empty_gauge.png");
    private static final ResourceLocation FULL_GAUGE = ResourceLocation.tryParse("jcraft:textures/gui/full_gauge.png");
    private static final int GAUGE_WIDTH = 42;
    private static final int GAUGE_HEIGHT = 5;
    private static final int GAUGE_Y_OFFSET = -65;

    private JCraftDuWangSkillOverlay() {
    }

    public static void markSkillTriggered(int skillId) {
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.level == null) {
            return;
        }

        LocalPlayer player = mc.player;
        if (OzoneSkillLogic.shouldRenderHud(player)) {
            GuiGraphics gui = event.getGuiGraphics();
            int baseX = JCraftAbilityHud.getHudX(event.getWindow().getGuiScaledWidth(), 32);
            double cd1Ratio = getCooldownRemainRatio(CooldownType.STAND_SP1);
            double cd2Ratio = getCooldownRemainRatio(CooldownType.STAND_SP2);
            renderOzoneSkills(gui, baseX, cd1Ratio, cd2Ratio, 1.0F);
            return;
        }

        StandEntity stand = getOwnedStand(player);
        if (stand == null) {
            return;
        }

        renderStandGauge(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight(), stand);

        double cd1Ratio = getCooldownRemainRatio(CooldownType.STAND_SP1);
        double cd2Ratio = getCooldownRemainRatio(CooldownType.STAND_SP2);
        float alpha = 1.0F;

        GuiGraphics gui = event.getGuiGraphics();
        int baseX = JCraftAbilityHud.getHudX(event.getWindow().getGuiScaledWidth(), 32);

        if (stand instanceof BlueHawaiiEntity) {
            renderBlueHawaiiSkills(gui, baseX, cd1Ratio, cd2Ratio, alpha);
            return;
        }

        renderDuWangSkills(gui, baseX, cd1Ratio, cd2Ratio, alpha);
    }

    private static void renderDuWangSkills(GuiGraphics gui, int baseX, double cd1Ratio, double cd2Ratio, float alpha) {
        renderSkill(gui, baseX, SPACING * 14,
                DuWangSkillCatalog.getSkillIconPath(DuWangSkillCatalog.TRACKING_TORNADO_ID),
                CooldownType.STAND_SP1, cd1Ratio, "special1",
                ModKeyMappings.DUWANG_SKILL_1.getTranslatedKeyMessage().getString(), alpha);
        renderSkill(gui, baseX, SPACING * 17,
                DuWangSkillCatalog.getSkillIconPath(DuWangSkillCatalog.HURRICANE_BARRIER_ID),
                CooldownType.STAND_SP2, cd2Ratio, "special2",
                ModKeyMappings.DUWANG_SKILL_2.getTranslatedKeyMessage().getString(), alpha);
    }

    private static void renderBlueHawaiiSkills(GuiGraphics gui, int baseX, double cd1Ratio, double cd2Ratio, float alpha) {
        renderSkill(gui, baseX, SPACING * 11,
                BlueHawaiiSkillCatalog.getSkillIconPath(BlueHawaiiSkillCatalog.TOOTH_MARK_ID),
                null, 0.0D, "tooth",
                ModKeyMappings.DUWANG_SKILL_1.getTranslatedKeyMessage().getString(), alpha);
        renderSkill(gui, baseX, SPACING * 14,
                BlueHawaiiSkillCatalog.getSkillIconPath(BlueHawaiiSkillCatalog.HUNT_ACTIVATE_ID),
                null, 0.0D, "hunt",
                ModKeyMappings.DUWANG_SKILL_2.getTranslatedKeyMessage().getString(), alpha);
        renderSkill(gui, baseX, SPACING * 17,
                BlueHawaiiSkillCatalog.getSkillIconPath(BlueHawaiiSkillCatalog.HUNT_RELEASE_ID),
                CooldownType.STAND_SP2, cd2Ratio, "release",
                ModKeyMappings.DUWANG_SKILL_3.getTranslatedKeyMessage().getString(), alpha);
    }

    private static void renderOzoneSkills(GuiGraphics gui, int baseX, double cd1Ratio, double cd2Ratio, float alpha) {
        renderSkill(gui, baseX, SPACING * 14,
                OzoneSkillCatalog.getSkillIconPath(OzoneSkillCatalog.MARK_TARGET_1_ID),
                CooldownType.STAND_SP1, cd1Ratio, "ozone1",
                ModKeyMappings.DUWANG_SKILL_1.getTranslatedKeyMessage().getString(), alpha);
        renderSkill(gui, baseX, SPACING * 17,
                OzoneSkillCatalog.getSkillIconPath(OzoneSkillCatalog.MARK_TARGET_2_ID),
                CooldownType.STAND_SP2, cd2Ratio, "ozone2",
                ModKeyMappings.DUWANG_SKILL_2.getTranslatedKeyMessage().getString(), alpha);
    }

    private static void renderSkill(
            GuiGraphics gui,
            int x,
            int y,
            @Nullable String iconPath,
            @Nullable CooldownType cooldownType,
            double remainRatio,
            String fallback,
            String keyText,
            float alpha
    ) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        JCraftAbilityHud.renderBorder(gui, x, y);

        if (iconPath != null) {
            ResourceLocation icon = ResourceLocation.tryParse(iconPath);
            if (icon != null) {
                JCraftAbilityHud.renderAbsIcon(gui, x, y, icon, fallback);
            }
        }

        int remainTicks = cooldownType == null
                ? 0
                : JComponentPlatformUtils.getCooldowns(Minecraft.getInstance().player).getCooldown(cooldownType);
        if (cooldownType != null && remainTicks > 0 && remainRatio > 0.0D) {
            JCraftAbilityHud.renderCooldown(gui, remainRatio, x, y);
        }

        gui.drawString(
                Minecraft.getInstance().font,
                keyText,
                x,
                y,
                ColorUtils.HSBAtoRGBA(0.3f - (float) remainRatio * 10f / 720f, remainRatio < 1.6 ? 0.0f : 1.0f, 1.0f, alpha * 2.0f)
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static StandEntity getOwnedStand(LocalPlayer player) {
        BlueHawaiiEntity blueHawaii = StandSummonLogic.findNearestOwnedStand(player, BlueHawaiiEntity.class);
        if (blueHawaii != null) {
            return blueHawaii;
        }
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

    private static void renderStandGauge(GuiGraphics gui, int screenWidth, int screenHeight, StandEntity stand) {
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
