package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.client.ClientSkillCooldownState;
import com.Y1fel.JoJoPlagueSurge.entity.custom.bluehawaii.BlueHawaiiEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandManager;
import com.Y1fel.JoJoPlagueSurge.entity.custom.stand.StandEntity;
import com.Y1fel.JoJoPlagueSurge.network.packet.OzoneSkillLogic;
import com.Y1fel.JoJoPlagueSurge.network.packet.SkillCooldowns;
import com.Y1fel.JoJoPlagueSurge.skill.BlueHawaiiSkillCatalog;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import com.Y1fel.JoJoPlagueSurge.skill.OzoneSkillCatalog;
import com.mojang.blaze3d.systems.RenderSystem;
import net.arna.jcraft.client.gui.hud.JCraftAbilityHud;
import net.arna.jcraft.common.util.ColorUtils;
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
        StandEntity stand = getOwnedStand(player);
        GuiGraphics gui = event.getGuiGraphics();
        int baseX = JCraftAbilityHud.getHudX(event.getWindow().getGuiScaledWidth(), 32);
        float alpha = 1.0F;

        if (stand != null) {
            // Stand is invulnerable, so the separate health gauge stays disabled.
            // renderStandGauge(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight(), stand);

            if (stand instanceof BlueHawaiiEntity) {
                renderBlueHawaiiSkills(gui, baseX,
                        ClientSkillCooldownState.getRemainRatio(SkillCooldowns.BLUE_HAWAII_RELEASE), alpha);
                return;
            }

            renderDuWangSkills(gui, baseX,
                    ClientSkillCooldownState.getRemainRatio(SkillCooldowns.DUWANG_SKILL_1),
                    ClientSkillCooldownState.getRemainRatio(SkillCooldowns.DUWANG_SKILL_2), alpha);
            return;
        }

        if (OzoneSkillLogic.shouldRenderHud(player)) {
            renderOzoneSkills(gui, baseX,
                    ClientSkillCooldownState.getRemainRatio(SkillCooldowns.OZONE_SKILL_1),
                    ClientSkillCooldownState.getRemainRatio(SkillCooldowns.OZONE_SKILL_2), alpha);
            return;
        }
    }

    private static void renderDuWangSkills(GuiGraphics gui, int baseX, double cd1Ratio, double cd2Ratio, float alpha) {
        renderSkill(gui, baseX, SPACING * 14,
                DuWangSkillCatalog.getSkillIconPath(DuWangSkillCatalog.TRACKING_TORNADO_ID),
                SkillCooldowns.DUWANG_SKILL_1, cd1Ratio, "special1",
                ModKeyMappings.DUWANG_SKILL_1.getTranslatedKeyMessage().getString(), alpha);
        renderSkill(gui, baseX, SPACING * 17,
                DuWangSkillCatalog.getSkillIconPath(DuWangSkillCatalog.HURRICANE_BARRIER_ID),
                SkillCooldowns.DUWANG_SKILL_2, cd2Ratio, "special2",
                ModKeyMappings.DUWANG_SKILL_2.getTranslatedKeyMessage().getString(), alpha);
    }

    private static void renderBlueHawaiiSkills(GuiGraphics gui, int baseX, double cd3Ratio, float alpha) {
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
                SkillCooldowns.BLUE_HAWAII_RELEASE, cd3Ratio, "release",
                ModKeyMappings.DUWANG_SKILL_3.getTranslatedKeyMessage().getString(), alpha);
    }

    private static void renderOzoneSkills(GuiGraphics gui, int baseX, double cd1Ratio, double cd2Ratio, float alpha) {
        renderSkill(gui, baseX, SPACING * 14,
                OzoneSkillCatalog.getSkillIconPath(OzoneSkillCatalog.MARK_TARGET_1_ID),
                SkillCooldowns.OZONE_SKILL_1, cd1Ratio, "ozone1",
                ModKeyMappings.DUWANG_SKILL_1.getTranslatedKeyMessage().getString(), alpha);
        renderSkill(gui, baseX, SPACING * 17,
                OzoneSkillCatalog.getSkillIconPath(OzoneSkillCatalog.MARK_TARGET_2_ID),
                SkillCooldowns.OZONE_SKILL_2, cd2Ratio, "ozone2",
                ModKeyMappings.DUWANG_SKILL_2.getTranslatedKeyMessage().getString(), alpha);
    }

    private static void renderSkill(
            GuiGraphics gui,
            int x,
            int y,
            @Nullable String iconPath,
            @Nullable String cooldownId,
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

        int remainTicks = cooldownId == null
                ? 0
                : ClientSkillCooldownState.getRemainingTicks(cooldownId);
        if (cooldownId != null && remainTicks > 0 && remainRatio > 0.0D) {
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
        BlueHawaiiEntity blueHawaii = StandManager.findNearestOwnedStand(player, BlueHawaiiEntity.class);
        if (blueHawaii != null) {
            return blueHawaii;
        }
        return StandManager.findNearestOwnedStand(player, DuWangEntity.class);
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
